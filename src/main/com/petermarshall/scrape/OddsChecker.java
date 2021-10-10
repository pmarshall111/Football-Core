package com.petermarshall.scrape;

import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.*;
import com.petermarshall.ConvertOdds;
import com.petermarshall.DateHelper;
import com.petermarshall.machineLearning.createData.classes.MatchToPredict;
import com.petermarshall.mail.SendEmail;
import com.petermarshall.scrape.classes.Team;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.util.*;
import java.util.function.Predicate;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.petermarshall.machineLearning.logisticRegression.Predict.DAYS_IN_FUTURE_TO_PREDICT;
import static com.petermarshall.scrape.ScrapeTimeout.getRandomTimeoutMs;
import static org.openqa.selenium.support.ui.ExpectedConditions.presenceOfElementLocated;

public class OddsChecker implements Runnable {
    public static final String BASE_URL = "https://www.oddschecker.com";
    public static final String PL_URL = "https://www.oddschecker.com/football/english/premier-league";
    public static final String BUNDESLIGA_URL = "https://www.oddschecker.com/football/germany/bundesliga";
    public static final String LIGUE_1_URL = "https://www.oddschecker.com/football/france/ligue-1";
    public static final String SERIE_A_URL = "https://www.oddschecker.com/football/italy/serie-a";
    public static final String LA_LIGA_URL = "https://www.oddschecker.com/football/spain/la-liga-primera";
    public static final String RUSSIA_URL = "https://www.oddschecker.com/football/russia/premier-league";

    private ArrayList<MatchToPredict> matches;

    public OddsChecker(ArrayList<MatchToPredict> matches) {
        this.matches = matches;
    }

    @Override
    public void run() {
        OddsChecker.addBookiesOddsForGames(matches);
    }

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
        System.setProperty("webdriver.chrome.driver", "/home/peter/Documents/personalProjects/footballBettingCore/target/chromedriver");
        WebDriver driver = new ChromeDriver();
        WebDriverWait wait = new WebDriverWait(driver, 20);
        try {
            driver.get(url);
            wait.until(presenceOfElementLocated(By.cssSelector(".match-on")));

            ArrayList<MatchInfo> matchInfos = new ArrayList<>();
            List<WebElement> items = driver.findElements(By.cssSelector(".match-on"));
            items.forEach(item -> {
                List<WebElement> teamNames = item.findElements(By.cssSelector(".fixtures-bet-name"));
                double[] odds = item.findElements(By.cssSelector(".add-to-bet-basket")).stream().mapToDouble(OddsChecker::getOddsFromHtmlElement).toArray();
                matchInfos.add(new MatchInfo(teamNames.get(0).getText(), teamNames.get(1).getText(), odds));
            });

            for (MatchToPredict match: matches) {
                MatchInfo correctMatch = findCorrectMatch(matchInfos, match);
                matchInfos.remove(correctMatch);
                if (correctMatch != null && correctMatch.odds != null) {
                    LinkedHashMap<String, double[]> bookiesOdds = new LinkedHashMap<>() {{
                        put("Best", correctMatch.odds);
                    }};
                    match.setBookiesOdds(bookiesOdds);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            driver.close();
        }
    }

    private static class MatchInfo{
        String homeTeam;
        String awayTeam;
        double[] odds;

        public MatchInfo(String homeTeam, String awayTeam, double[] odds) {
            this.homeTeam = homeTeam;
            this.awayTeam = awayTeam;
            this.odds = odds;
        }
    }

    private static double getOddsFromHtmlElement(WebElement elem) {
        return ConvertOdds.fromFractionToDecimal(elem.getText());
    }

    /*
     * Will first go through looking for either 1 of the 2 teams to be correct.
     *
     * If both are not perfect matches, will go through and pick the one where we have the most consecutive characters correct. If we then have 2 equal there, we will
     * pick the match with highest ratio of correct characters.
     */
    private static MatchInfo findCorrectMatch (ArrayList<MatchInfo> potentialMatches, MatchToPredict match) {
        for (MatchInfo potentialMatch: potentialMatches) {
            String homeTeamName = Team.matchTeamNamesUnderstatToOddsChecker(match.getHomeTeamName());
            String awayTeamName = Team.matchTeamNamesUnderstatToOddsChecker(match.getAwayTeamName());
            if (homeTeamName.equals(potentialMatch.homeTeam) || awayTeamName.equals(potentialMatch.awayTeam)) {
                return potentialMatch;
            }
        }
        System.out.println("Could not find Oddschecker match " + match.getHomeTeamName() + " vs " + match.getAwayTeamName());
        return null;
    }

    public static void main(String[] args) {
        ArrayList<MatchToPredict> mtps = new ArrayList<>();
        mtps.add(new MatchToPredict("Chelsea", "Manchester City", "19-20", "EPL", "17/06/20",-1,-1));
        mtps.add(new MatchToPredict("Aston Villa", "Wolverhampton Wanderers", "19-20", "EPL", "17/06/20",-1,-1));
        mtps.add(new MatchToPredict("Sheffield United", "Tottenham", "19-20", "EPL", "21/06/20",-1,-1));

        addOddsForLeague(mtps, "https://www.oddschecker.com/football/english/premier-league");
    }
}
