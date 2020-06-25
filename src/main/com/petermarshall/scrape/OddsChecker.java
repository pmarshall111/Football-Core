package com.petermarshall.scrape;

import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.*;
import com.petermarshall.DateHelper;
import com.petermarshall.machineLearning.createData.classes.MatchToPredict;
import com.petermarshall.mail.SendEmail;
import com.petermarshall.scrape.classes.Team;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.util.*;
import java.util.function.Predicate;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.petermarshall.machineLearning.logisticRegression.Predict.DAYS_IN_FUTURE_TO_PREDICT;
import static com.petermarshall.scrape.ScrapeTimeout.getRandomTimeoutMs;

public class OddsChecker {
    public static final String BASE_URL = "https://www.oddschecker.com";
    public static final String PL_URL = "https://www.oddschecker.com/football/english/premier-league";
    public static final String BUNDESLIGA_URL = "https://www.oddschecker.com/football/germany/bundesliga";
    public static final String LIGUE_1_URL = "https://www.oddschecker.com/football/france/ligue-1";
    public static final String SERIE_A_URL = "https://www.oddschecker.com/football/italy/serie-a";
    public static final String LA_LIGA_URL = "https://www.oddschecker.com/football/spain/la-liga-primera";
    public static final String RUSSIA_URL = "https://www.oddschecker.com/football/russia/premier-league";

    /*
     * Method will separate matches to predict into each league and then call addBettersOddsForLeague(). We do this because
     * we have a separate URL for each leagues page on oddschecker, so we must also separate out the matches.
     */
    public static void addBookiesOddsForGames(ArrayList<MatchToPredict> matches) {
        ArrayList<MatchToPredict> PL = new ArrayList<>();
        ArrayList<MatchToPredict> BUNDESLIGA = new ArrayList<>();
        ArrayList<MatchToPredict> LIGUE_1 = new ArrayList<>();
        ArrayList<MatchToPredict> SERIE_A = new ArrayList<>();
        ArrayList<MatchToPredict> LA_LIGA = new ArrayList<>();
        ArrayList<MatchToPredict> RUSSIA = new ArrayList<>();

        for (MatchToPredict match: matches) {
            switch(match.getLeagueName()) {
                case "EPL":
                    PL.add(match);
                    break;
                case "LA_LIGA":
                    LA_LIGA.add(match);
                    break;
                case "BUNDESLIGA":
                    BUNDESLIGA.add(match);
                    break;
                case "SERIE_A":
                    SERIE_A.add(match);
                    break;
                case "LIGUE_1":
                    LIGUE_1.add(match);
                    break;
                case "RUSSIA":
                    RUSSIA.add(match);
                    break;
                default:
                        throw new RuntimeException("Could not fit match into suitable league");
            }
        }

        if (PL.size() > 0) addOddsForLeague(PL, PL_URL);
        if (LA_LIGA.size() > 0) addOddsForLeague(LA_LIGA, LA_LIGA_URL);
        if (LIGUE_1.size() > 0) addOddsForLeague(LIGUE_1, LIGUE_1_URL);
        if (BUNDESLIGA.size() > 0) addOddsForLeague(BUNDESLIGA, BUNDESLIGA_URL);
        if (SERIE_A.size() > 0) addOddsForLeague(SERIE_A, SERIE_A_URL);
        if (RUSSIA.size() > 0) addOddsForLeague(RUSSIA, RUSSIA_URL);
    }

    /*
     * Method will search through the various CDATA tags from the Oddschecker website, and when it finds the one with the fixtures, will grab
     */
    private static void addOddsForLeague(ArrayList<MatchToPredict> matches, String url) {
        try (final WebClient webClient = new WebClient()) {
            webClient.getOptions().setCssEnabled(false);
            webClient.getOptions().setJavaScriptEnabled(false);
            final HtmlPage page = webClient.getPage(url);
            //getting out all matches in next X days
            ArrayList<JSONObject> matchUrls = new ArrayList<>();
            Date maxDateOfMatches = DateHelper.addXDaysToDate(new Date(), DAYS_IN_FUTURE_TO_PREDICT+1);
            String oddsCheckerDate = DateHelper.changeDateToOddsChecker(maxDateOfMatches);
            Matcher dates = Pattern.compile("CDATA\\[([^]]+)").matcher(page.asXml());
            while (dates.find()) {
                JSONObject gameInfo = turnToJson(dates.group(1).substring(0,dates.group(1).length()-2)); //-2 here as the JSONString we're reading in ends in }}}, so we get rid of 2 closing curly braces.
                if (gameInfo == null || !gameInfo.get("@type").toString().equals("SportsEvent")) continue;
                else if (gameInfo.get("startDate").toString().compareTo(oddsCheckerDate) > 0) break; //do not include games that are toop far ahead in time
                String H2H = gameInfo.get("name").toString();
                System.out.println(H2H);
                matchUrls.add(gameInfo);
            }
            //ensuring matches are sorted with soonest first
            matchUrls.sort((our, their) -> {
                String ourDate = our.get("startDate").toString();
                String theirDate = their.get("startDate").toString();
                return ourDate.compareTo(theirDate);
            });
            //finding matches & adding odds
            for (MatchToPredict match: matches) {
                JSONObject correctMatch = findCorrectMatch(matchUrls, match);
                matchUrls.remove(correctMatch);
                String matchTitle = correctMatch.get("name").toString();
                String[] teamNames = getTeamNamesFromMatchup(matchTitle);
                String homeNameOddsChecker = teamNames[0];
                String awayNameOddsChecker = teamNames[1];
                addOdds(correctMatch.get("url").toString(), match, homeNameOddsChecker, awayNameOddsChecker);
                //random sleep after scrape to look more like a real user
                Thread.sleep(getRandomTimeoutMs());
            }
            //once done whole league another sleep to add delay to call to scrape next league.
            Thread.sleep(getRandomTimeoutMs());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void addOdds (String matchUrl, MatchToPredict match, String oddsCheckerHomeTeamName, String oddsCheckerAwayTeamName) {
        try (final WebClient webClient = new WebClient()) {
            webClient.getOptions().setCssEnabled(false);
            webClient.getOptions().setJavaScriptEnabled(false);
            final HtmlPage page = webClient.getPage(matchUrl);
            HtmlElement body = page.getBody();

            ArrayList<DomNode> bookies = new ArrayList<>(body.querySelectorAll("a.bk-logo-click")); //gets the bookie names in order
            LinkedHashMap<String, double[]> bookiesOdds = new LinkedHashMap<>();
            for (DomNode elem: bookies) {
                bookiesOdds.put(elem.getAttributes().getNamedItem("title").getNodeValue(), new double[]{-1,-1,-1});
            }

            ArrayList<DomNode> oddsRows = new ArrayList<>(body.querySelectorAll(".diff-row.evTabRow.bc")); //gets the 3 rows of bookie odds. one for home win, draw and loss. NOT ORDERED.
            for (int i = 0; i<oddsRows.size(); i++) {
                //in here we will test the first and 3rd rows and then set the index in oddsArrays when setting the value to be home or away.
                String result = oddsRows.get(i).getAttributes().getNamedItem("data-bname").getNodeValue();
                int arrayPosition = getHomeDrawAway(result, oddsCheckerHomeTeamName, "Draw", oddsCheckerAwayTeamName);
                ArrayList<DomNode> odds = new ArrayList<>(oddsRows.get(i).querySelectorAll("td.bc.bs"));
                ArrayList<double[]> oddsArrays = new ArrayList<>(bookiesOdds.values());
                for (int j = 0; j<odds.size(); j++) {
                    oddsArrays.get(j)[arrayPosition] = Double.parseDouble(odds.get(j).getAttributes().getNamedItem("data-odig").getNodeValue());
                }
            }
            match.setBookiesOdds(bookiesOdds);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    //Method to be called with teamnames from oddschecker data so should be direct match
    private static int getHomeDrawAway(String betChoice, String homeTeamName, String drawName, String awayTeamName) {
        if (homeTeamName.equals(betChoice)) {
            return 0;
        } else if (drawName.equals(betChoice)) {
            return 1;
        } else if (awayTeamName.equals(betChoice)) {
            return 2;
        } else
            throw new RuntimeException("We could not find a match for the selection. Selection is " + betChoice + ". Home is " + homeTeamName +
                    ". Draw is " + drawName + ". Away is " + awayTeamName);
    }

    /*
     * Will first go through looking for either 1 of the 2 teams to be correct.
     *
     * If both are not perfect matches, will go through and pick the one where we have the most consecutive characters correct. If we then have 2 equal there, we will
     * pick the match with highest ratio of correct characters.
     */
    private static JSONObject findCorrectMatch (ArrayList<JSONObject> potentialMatches, MatchToPredict match) {
        for (JSONObject potentialMatch: potentialMatches) {
            String[] teamNames = getTeamNamesFromMatchup( potentialMatch.get("name").toString() );
            String homeTeamName = Team.matchTeamNamesUnderstatToOddsChecker(match.getHomeTeamName());
            String awayTeamName = Team.matchTeamNamesUnderstatToOddsChecker(match.getAwayTeamName());
            if (homeTeamName.equals(teamNames[0]) || awayTeamName.equals(teamNames[1])) {
                return potentialMatch;
            }
        }
        System.out.println("Could not find Oddschecker match " + match.getHomeTeamName() + " vs " + match.getAwayTeamName());
        return null;
    }

    private static String[] getTeamNamesFromMatchup (String matchup) {
        return matchup.split(" v ");
    }

    private static JSONObject turnToJson (String string) {
        if (string == null) throw new RuntimeException("turnToJson given null argument");
        JSONParser parser = new JSONParser();
        try {
            JSONObject jsonObject = (JSONObject) parser.parse(string);
            return jsonObject;
        } catch (ParseException e) {
            System.out.println("Exception when converting Oddschecker to JSON: " + e.getMessage());
            return null;
        }
    }

    public static void main(String[] args) {
        ArrayList<MatchToPredict> mtps = new ArrayList<>();
        mtps.add(new MatchToPredict("Chelsea", "Manchester City", "19-20", "EPL", "17/06/20",-1,-1));
        mtps.add(new MatchToPredict("Aston Villa", "Wolverhampton Wanderers", "19-20", "EPL", "17/06/20",-1,-1));
        mtps.add(new MatchToPredict("Sheffield United", "Tottenham", "19-20", "EPL", "21/06/20",-1,-1));

        addOddsForLeague(mtps, "https://www.oddschecker.com/football/english/premier-league");
    }
}
