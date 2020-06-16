package com.petermarshall.scrape;

import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.*;
import com.petermarshall.DateHelper;
import com.petermarshall.machineLearning.createData.classes.MatchToPredict;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.util.*;
import java.util.function.Predicate;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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

        if (PL.size() > 0) addBettersOddsForLeague(PL, PL_URL);
        if (LA_LIGA.size() > 0) addBettersOddsForLeague(LA_LIGA, LA_LIGA_URL);
        if (LIGUE_1.size() > 0) addBettersOddsForLeague(LIGUE_1, LIGUE_1_URL);
        if (BUNDESLIGA.size() > 0) addBettersOddsForLeague(BUNDESLIGA, BUNDESLIGA_URL);
        if (SERIE_A.size() > 0) addBettersOddsForLeague(SERIE_A, SERIE_A_URL);
        if (RUSSIA.size() > 0) addBettersOddsForLeague(RUSSIA, RUSSIA_URL);
    }


    /*
     * Method will search through the various CDATA tags from the Oddschecker website, and when it finds the one with the fixtures, will grab
     */
    private static void addBettersOddsForLeague(ArrayList<MatchToPredict> matches, String url) { //ArrayList<MatchToPredict> matches

        try (final WebClient webClient = new WebClient()) {
            webClient.getOptions().setCssEnabled(false);
            webClient.getOptions().setJavaScriptEnabled(false);

            final HtmlPage page = webClient.getPage(url);

            ArrayList<JSONObject> matchUrls = new ArrayList<>();

//            int LOOK_AT_GAMES_MAX_N_MINS_IN_FUTURE = 56; //means we will get an error if the kickoff times do not match up as we're only allowing to look at games 56 mins in the future. This is because there may be games tomorrow etc that we're not interested in.
//            Date maxDateOfMatches = DateHelper.addMinsToDate(new Date(), LOOK_AT_GAMES_MAX_N_MINS_IN_FUTURE);
            Date maxDateOfMatches = DateHelper.addDaysToDate(new Date(), 21);
            String oddsCheckerDate = DateHelper.changeDateToOddsChecker(maxDateOfMatches);

            System.out.println(page.asXml());
            Matcher dates = Pattern.compile("CDATA\\[([^]]+)").matcher(page.asXml());
            while (dates.find()) {
//                System.out.println(dates.group(1));
                JSONObject gameInfo = turnToJson(dates.group(1).substring(0,dates.group(1).length()-2)); //-2 here as the JSONString we're reading in ends in }}}, so we get rid of 2 closing curly braces.

                if (gameInfo == null || !gameInfo.get("@type").toString().equals("SportsEvent")) continue;
                else if (gameInfo.get("startDate").toString().compareTo(oddsCheckerDate) == 1) break; //do not include games that are further ahead in time than LOOK_AT_GAMES_MAX_N_MINS_IN_FUTURE

                String H2H = gameInfo.get("name").toString();
                System.out.println(H2H);
                matchUrls.add(gameInfo);
            }


            for (MatchToPredict match: matches) {
                String kickoffTime = DateHelper.changeSqlDateToOddschecker( match.getSqlDateString() );


                //need to also remove a match if it's already been picked.
                ArrayList<JSONObject> potentialMatches = new ArrayList<>(matchUrls);
                potentialMatches.removeIf(new Predicate<JSONObject>() {
                    @Override
                    public boolean test(JSONObject jsonObject) {
                        return jsonObject.containsKey("matched"); //we add this key in findCorrectMatch() if we've already connected a game from our app to oddschecker.
                    }
                });

                JSONObject correctMatch = findCorrectMatch(potentialMatches, match);

                String matchTitle = correctMatch.get("name").toString();
                String[] teamNames = getTeamNamesFromMatchup(matchTitle);
                String homeTeam = teamNames[0];
                String awayTeam = teamNames[1];

                addOdds(correctMatch.get("url").toString(), match, homeTeam, awayTeam);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /*
     * Note that OddsChecker does not sort it's odds in order of home draw away, they just decide to put the main team first. So we need a way of checking which team is at home.
     *
     * Plan is to get the match title at the top and compare the names used there to those in the row. Then we know which is which.
     */
    private static void addOdds (String matchUrl, MatchToPredict match, String oddsCheckerHomeTeamName, String oddsCheckerAwayTeamName) {
        try (final WebClient webClient = new WebClient()) {
            webClient.getOptions().setCssEnabled(false);
            webClient.getOptions().setJavaScriptEnabled(false);

            //currently assuming only PL games. will have to possibly sort the original matches into blocks of each league before doing this.
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
                String selectionName = oddsRows.get(i).getAttributes().getNamedItem("data-bname").getNodeValue();
                int arrayPosition = getHomeDrawAway(selectionName, oddsCheckerHomeTeamName, "Draw", oddsCheckerAwayTeamName);

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

    private static int getHomeDrawAway(String selectionName, String homeTeamName, String drawName, String awayTeamName) {
        Pattern homeTeam = Pattern.compile(homeTeamName);
        Pattern draw = Pattern.compile(drawName);
        Pattern awayTeam = Pattern.compile(awayTeamName);

        Matcher h = homeTeam.matcher(selectionName);
        Matcher d = draw.matcher(selectionName);
        Matcher a = awayTeam.matcher(selectionName);

        boolean isHome = h.find(), isDraw = d.find(), isAway = a.find();

        if ((isHome && (isDraw || isAway)) ||
                (isDraw && isAway)) {
            throw new RuntimeException("We've matched twice or more on getting the team from the selection. Selection is " + selectionName + ". Home is " + homeTeamName +
                    ". Draw is " + drawName + ". Away is " + awayTeamName);
        }
        else if (isHome) return 0;
        else if (isDraw) return 1;
        else if (isAway) return 2;
        else throw new RuntimeException("We could not find a match for the selection. Selection is " + selectionName + ". Home is " + homeTeamName +
                    ". Draw is " + drawName + ". Away is " + awayTeamName);
    }




    //TESTING
//    private static void addOdds (String matchUrl) {
//        try (final WebClient webClient = new WebClient()) {
//            webClient.getOptions().setCssEnabled(false);
//            webClient.getOptions().setJavaScriptEnabled(false);
//
//            //currently assuming only PL games. will have to possibly sort the original matches into blocks of each league before doing this.
//            final HtmlPage page = webClient.getPage(matchUrl);
//
////            System.out.println(page.asXml());
//
//            HtmlElement body = page.getBody();
//
//            ArrayList<DomNode> bookies = new ArrayList<>(body.querySelectorAll("a.b90med"));
//            LinkedHashMap<String, double[]> bookiesOdds = new LinkedHashMap<>();
//            for (DomNode elem: bookies) {
//                bookiesOdds.put(elem.getAttributes().getNamedItem("title").getNodeValue(), new double[]{-1,-1,-1});
//            }
//
//            ArrayList<DomNode> oddsRows = new ArrayList<>(body.querySelectorAll(".diff-row.evTabRow.bc"));
//            for (int i = 0; i<oddsRows.size(); i++) {
//
//                ArrayList<DomNode> odds = new ArrayList<>(oddsRows.get(i).querySelectorAll("td.bc.bs"));
//                ArrayList<double[]> oddsArrays = new ArrayList<>(bookiesOdds.values());
//                for (int j = 0; j<odds.size(); j++) {
//                    oddsArrays.get(j)[i] = Double.parseDouble(odds.get(j).getAttributes().getNamedItem("data-odig").getNodeValue());
//                }
//
//            }
//
//
//        }
//        catch (Exception e) {
//            e.printStackTrace();
//        }
//    }

    /*
     * Will first go through looking for either 1 of the 2 teams to be correct. This is ok because the method will only be called with games going on at
     * the same time, so there will only be 1 instance of each team.
     *
     * If both are not perfect matches, will go through and pick the one where we have the most consecutive characters correct. If we then have 2 equal there, we will
     * pick the match with highest ratio of correct characters.
     */
    private static JSONObject findCorrectMatch (ArrayList<JSONObject> potentialMatches, MatchToPredict match) {
        int maxConsecChars = -1; double similarCharsRatio = -1;
        JSONObject currMostLikely = null;

        for (JSONObject potentialMatch: potentialMatches) {

            String[] teamNames = getTeamNamesFromMatchup( potentialMatch.get("name").toString() );
            String homeTeamName = match.getHomeTeamName();
            String awayTeamName = match.getAwayTeamName();

            if (homeTeamName.equals(teamNames[0]) || awayTeamName.equals(teamNames[1])) {
                potentialMatch.put("matched", true);
                return potentialMatch;
            } else {
                int totalConsec = getLongestCharSequence(teamNames[0], homeTeamName) + getLongestCharSequence(teamNames[1], awayTeamName);
                double totalRatio = getSimilarCharsRatio(teamNames[0], homeTeamName) + getSimilarCharsRatio(teamNames[1], awayTeamName);
                if (totalConsec > maxConsecChars || (totalConsec == maxConsecChars && totalRatio > similarCharsRatio)) {
                    currMostLikely = potentialMatch;
                    maxConsecChars = totalConsec;
                    similarCharsRatio = totalRatio;
                }
            }
        }
        if (maxConsecChars < 6) throw new RuntimeException("Unsure if we have found the correct match for the game " + match.getHomeTeamName() + " vs " + match.getAwayTeamName() +
                ". We have matched this game up with " + currMostLikely.get("name").toString());

        currMostLikely.put("matched", true);
        return currMostLikely;
    }

    private static String[] getTeamNamesFromMatchup (String matchup) {
        return matchup.split(" v ");
    }

    /*
     * Look at each character and finds the longest string from there that goes into the oddsCheckerTeamName
     */
    private static int getLongestCharSequence (String oddsCheckerTeamName, String ourTeamName) {
        int currLongest = 0;
        OuterLoop:
        for (int i = 0; i<ourTeamName.length()-1; i++) {
            for (int j = ourTeamName.length()-1; j>i; j--) {

                String subString = ourTeamName.substring(i,j);
                if (oddsCheckerTeamName.contains(subString)) {
                    if (subString.length() > currLongest) currLongest = subString.length();
                    continue OuterLoop;
                }

            }
        }
        return currLongest;
    }


    private static double getSimilarCharsRatio (String oddsCheckerTeamName, String ourTeamName) {
        HashMap<Character, Integer> oddsChars = getCharCollection(oddsCheckerTeamName);
        HashMap<Character, Integer> ourChars = getCharCollection(ourTeamName);

        double totalOurChars = 0;
        double matches = 0;
        for (Character c: ourChars.keySet()) {
            totalOurChars += ourChars.get(c);

            if (oddsChars.getOrDefault(c, 0) >= ourChars.get(c)) matches += ourChars.get(c);
            else matches += oddsChars.getOrDefault(c, 0);
        }

        return matches/totalOurChars;
    }

    private static HashMap<Character, Integer> getCharCollection (String teamName) {
        HashMap<Character, Integer> chars = new HashMap<>();
        for (int i = 0; i<teamName.length(); i++) {
            char currChar = teamName.charAt(i);
            if (currChar != ' ') {
                chars.put(currChar, chars.getOrDefault(currChar, 0) + 1);
            }
        }
        return chars;
    }


    public static void main(String[] args) {

        String oddsTN = "Atlitico Madrid";
        String ourTeamName = "Atletico Madrid";
        String ourTeamName2 = "Atletic Bilbao";

        ArrayList<MatchToPredict> mtps = new ArrayList<>();
        mtps.add(new MatchToPredict("Manchester City", "Arsenal", "19-20", "EPL", "17/06/20",-1,-1));
        mtps.add(new MatchToPredict("Aston Villa", "Sheffield United", "19-20", "EPL", "17/06/20",-1,-1));
        mtps.add(new MatchToPredict("Everton", "Liverpool", "19-20", "EPL", "21/06/20",-1,-1));

        addBettersOddsForLeague(mtps, "https://www.oddschecker.com/football/english/premier-league");

        System.out.println("hi");

//        System.out.println(getHomeDrawAway("Levantewwdll", "Levante", "Draw", "Girona"));
//        addBookiesOddsForGames();
//        System.out.println(getLongestCharSequence("Man Utd", "Manchester United"));
//        System.out.println(getLongestCharSequence("Man Utd", "Manchester City"));

//        System.out.println(getSimilarCharsRatio("Man Utd", "Manchester United"));
//        System.out.println(getSimilarCharsRatio("Man Utd", "Manchester City"));

//        addOdds("https://www.oddschecker.com//football/english/premier-league/wolves-v-liverpool");
    }

    private static JSONObject turnToJson (String string) {
        if (string == null) throw new RuntimeException("turnToJson given null argument");

        JSONParser parser = new JSONParser();
        try {
            JSONObject jsonObject = (JSONObject) parser.parse(string);
            return jsonObject;
        } catch (ParseException e) {
//            e.printStackTrace();
            return null;
        }
    }
}
