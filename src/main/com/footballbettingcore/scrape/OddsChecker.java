package com.footballbettingcore.scrape;

import com.footballbettingcore.utils.ConvertOdds;
import com.footballbettingcore.machineLearning.createData.classes.MatchToPredict;
import com.footballbettingcore.scrape.classes.OddsCheckerBookies;
import com.footballbettingcore.scrape.classes.Team;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.*;

import static org.openqa.selenium.support.ui.ExpectedConditions.presenceOfElementLocated;

public class OddsChecker implements Runnable {
    private static final Logger logger = LogManager.getLogger(OddsChecker.class);

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
        WebDriver driver = WebDriverFactory.getFirefoxDriver();
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        try {
            driver.get(url);
            Dimension d = driver.manage().window().getSize();
            wait.until(presenceOfElementLocated(By.cssSelector(".match-on")));

            ArrayList<MatchInfo> matchInfos = new ArrayList<>();
            List<WebElement> items = driver.findElements(By.cssSelector(".match-on"));
            items.forEach(item -> {
                List<WebElement> teamNames = item.findElements(By.cssSelector(".fixtures-bet-name"));
                String allOddsUrl = item.findElement(By.cssSelector(".betting a")).getAttribute("href");
                matchInfos.add(new MatchInfo(teamNames.get(0).getText(), teamNames.get(1).getText(), allOddsUrl));
            });

            for (MatchToPredict match: matches) {
                MatchInfo correctMatch = findCorrectMatch(matchInfos, match);
                matchInfos.remove(correctMatch);
                if (correctMatch != null) {
                    logger.info("Scraping for " + match.getMatchString());
                    driver.manage().deleteAllCookies();
                    driver.get(correctMatch.oddsUrl);
                    List<WebElement> oddsRows = driver.findElements(By.cssSelector("div[class^='oddsAreaWrapper']"));
                    wait.until(presenceOfElementLocated(By.cssSelector("button[data-bk=B3]")));
                    boolean rowOrderIsHomeDrawAway = isOddOrderHomeDrawAway(driver, match);
                    double[] bet365Odds = getOddsForBookie("B3", oddsRows, rowOrderIsHomeDrawAway);
                    double[] unibetOdds = getOddsForBookie("UN", oddsRows, rowOrderIsHomeDrawAway);
                    LinkedHashMap<String, double[]> bookiesOdds = new LinkedHashMap<>() {{
                        if (bet365Odds != null) put(OddsCheckerBookies.BET365.getName(), bet365Odds);
                        if (unibetOdds != null) put(OddsCheckerBookies.UNIBET.getName(), unibetOdds);
                    }};
                    match.setBookiesOdds(bookiesOdds);
                }
            }
        } catch (Exception e) {
            logger.error(e);
        } finally {
            driver.close();
        }
    }

    private static class MatchInfo{
        String homeTeam;
        String awayTeam;
        String oddsUrl;

        public MatchInfo(String homeTeam, String awayTeam, String oddsUrl) {
            this.homeTeam = homeTeam;
            this.awayTeam = awayTeam;
            this.oddsUrl = oddsUrl;
        }
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

    // Oddschecker order their matches by the alphabetical order of the teams, rather than by the team being the home/away team.
    // Therefore, we need to determine which row of odds applies to which team.
    private static boolean isOddOrderHomeDrawAway(WebDriver driver, MatchToPredict match) {
        List<WebElement> orderOfRows = driver.findElements(By.cssSelector("div[class^='BetRowLeftBetContent']"));
        String homeTeam = Team.matchTeamNamesUnderstatToOddsChecker(match.getHomeTeamName());
        String awayTeam = Team.matchTeamNamesUnderstatToOddsChecker(match.getAwayTeamName());
        String firstRowTeam = orderOfRows.get(0).findElement(By.cssSelector("a")).getText().trim();
        String lastRowTeam = orderOfRows.get(2).findElement(By.cssSelector("a")).getText().trim();
        if (firstRowTeam.equals(homeTeam) || lastRowTeam.equals(awayTeam)) {
            return true;
        }
        if (lastRowTeam.equals(homeTeam) || firstRowTeam.equals(awayTeam)) {
            return false;
        }
        // if none of the teamnames match, determine order alphabetically
        logger.warn("No team names match on Oddschecker. Oddschecker teamnames = " + firstRowTeam + ", " + lastRowTeam);
        return homeTeam.compareTo(awayTeam) < 0;
    }

    private static double[] getOddsForBookie(String bookieShortName, List<WebElement> oddsRows, boolean rowOrderIsHomeDrawAway) {
        double[] bookieOdds = new double[3];
        try {
            for (int i = 0; i < 3; i++) {
                WebElement oddsRow = oddsRows.get(i);
                String fractionalOdds = oddsRow.findElement(By.cssSelector("button[data-bk=" + bookieShortName + "]")).getText().trim();
                logger.info("Odds for " + bookieShortName + ": " + fractionalOdds);
                if (rowOrderIsHomeDrawAway) {
                    bookieOdds[i] = ConvertOdds.fromFractionToDecimal(fractionalOdds);
                } else {
                    bookieOdds[2 - i] = ConvertOdds.fromFractionToDecimal(fractionalOdds);
                }
            }
            return bookieOdds;
        } catch (Exception e) {return null;}
    }

    public static void main(String[] args) {
        ArrayList<MatchToPredict> mtps = new ArrayList<>();
        mtps.add(new MatchToPredict("Burnley", "Watford", "21-22", "EPL", "2022-02-05",-1,-1));

        addOddsForLeague(mtps, "https://www.oddschecker.com/football/english/premier-league");
    }
}
