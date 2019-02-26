package com.petermarshall.taskScheduling;

import com.petermarshall.DateHelper;
import com.petermarshall.logging.LastPredicted;
import com.petermarshall.machineLearning.createData.GetMatchesFromDb;
import com.petermarshall.machineLearning.createData.classes.MatchToPredict;
import com.petermarshall.machineLearning.logisticRegression.Predict;
import com.petermarshall.mail.SendEmail;
import com.petermarshall.database.DataSource;
import com.petermarshall.scrape.OddsChecker;
import com.petermarshall.scrape.SofaScore;
import com.petermarshall.scrape.classes.OddsCheckerBookies;

import java.util.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.function.Predicate;

public class PredictTodaysGames {
    //TODO: need to update played games before we do anything. check in db to see if games were played yesterday. if they were, then we need to scrape in played games.

    private static int minsAfterLineupsAnnouned = 15;
    private static int minsBeforeKickoff = 5;
    static final String trainedThetasPath = "C:\\Users\\Peter\\Documents\\JavaProjects\\Football\\testThetas.csv";

    /*
     * Method will update todays times in the database and also store the sofascore ID in the database. Then it gets the times of those games and sets times where we should
     * scrape lineups and predict.
     */
    public static void main(String[] args) {
        System.out.println("Scraping in todays kickoff times and schedulling times to predict these games...\n");

        decideWhetherToScrapeInPlayedGames();


        //db dateString format is yyyy-mm-dd hh:mm:ss
        ArrayList<Date> kickOffTimes = SofaScore.updateTodaysKickoffTimes();

        ArrayList<Date> scrapingTimes = getTimesToScrape(kickOffTimes, minsAfterLineupsAnnouned, minsBeforeKickoff);
        Timer timer = new Timer();

        HashSet<String> bookiesWeveSignedUpFor = new HashSet<>();
        bookiesWeveSignedUpFor.add(OddsCheckerBookies.BET365.getBookie());
        bookiesWeveSignedUpFor.add(OddsCheckerBookies.SKYBET.getBookie());
        bookiesWeveSignedUpFor.add(OddsCheckerBookies.BETVICTOR.getBookie());
        bookiesWeveSignedUpFor.add(OddsCheckerBookies.LADBROKES.getBookie());


        for (Date scrapeTime: scrapingTimes) {
//            TimerTask timerTask = new TimerTask() {
//                @Override
//                public void run() {
//                    runPredictor(scrapeTime, bookiesWeveSignedUpFor);
//                }
//            };
//
//            timer.schedule(timerTask, scrapeTime);


            TimeUnit timeInMins = TimeUnit.MINUTES;
            int minsTillRuntime = DateHelper.findMinutesBetweenDates(new Date(), scrapeTime);
            ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

            scheduler.schedule(new Runnable() {
                @Override
                public void run() {
                    runPredictor(scrapeTime, bookiesWeveSignedUpFor);
                }
            }, minsTillRuntime, timeInMins);
        }
    }

    private static void runPredictor(Date scrapeTime, HashSet<String> bookiesWeveSignedUpFor) {
        System.out.println("Predicting games...");

        //earliest
        Date earliestGame = DateHelper.addMinsToDate(scrapeTime, minsBeforeKickoff);
        Date latestGame = DateHelper.addMinsToDate(scrapeTime, 60 - minsAfterLineupsAnnouned);

        boolean needToPredictGamesWeMissed = LastPredicted.timeToPredictMissedGames();
        ArrayList<MatchToPredict> matchesWeDidntPredict = new ArrayList<>();

        DataSource.openConnection();
        ArrayList<MatchToPredict> matchesHappeningNow = DataSource.getBaseMatchesToPredict(earliestGame, latestGame);

        if (needToPredictGamesWeMissed) {
            //get array of MatchesToPredict from database with odds, TeamNames, league, season
            //set Date field and also MatchesToPredict field within GetMatchesFromDB class so it knows when to start checking if it's looking at a match it needs to predict


            Date checkGamesAfterDate = LastPredicted.getWhenMissedGamesWereLastPredicted();
            matchesWeDidntPredict = DataSource.getMatchesWithoutPredictions(checkGamesAfterDate);

            if (matchesWeDidntPredict.size() > 0) {

                //NOTE: When we add features to the match to predict, we will also add features to these missed games we want to log through logic within GetMatchesFromDb.
                GetMatchesFromDb.setGamesNeedPredictingAfterDate(checkGamesAfterDate);
                GetMatchesFromDb.setMissedGamesThatNeedPredicting(matchesWeDidntPredict);

            }

            System.out.println("Predicting missed games. We missed " + matchesWeDidntPredict.size() + " games since " + checkGamesAfterDate);
        }

        DataSource.closeConnection();



        SofaScore.addLineupsToGamesAboutToStart(matchesHappeningNow);
        GetMatchesFromDb.addFeaturesToMatchesToPredict(matchesHappeningNow); //method will check to see whether it needs to add stats to missed prediction games while it creates current stats
        //for each team from all the games in the database.
        OddsChecker.addBookiesOddsForGames(matchesHappeningNow);
        Predict.addOurProbabilitiesToGames(matchesHappeningNow, trainedThetasPath);

        if (needToPredictGamesWeMissed) {
            Predict.addOurProbabilitiesToGames(matchesWeDidntPredict, trainedThetasPath);
            Predict.missedGamesBetDecisionAndLog(matchesWeDidntPredict);

            LastPredicted.setAllMissedGamesPredictedUpTo2DaysAgo();
        }


        StringBuilder emailBody = new StringBuilder();
        emailBody.append("Dear app user,\n\n We currently suggest placing the following bets: \n\n");


        //method can be called without last argument, to assume that we've signed up for all bookies.
        boolean gamesToEmail = Predict.calcBetsForCurrentGamesAndAddToBuilder(matchesHappeningNow, emailBody, bookiesWeveSignedUpFor);
        if (gamesToEmail) {
            SendEmail.sendOutEmail("New bet", emailBody.toString());
            System.out.println("We found a good bet!");
        } else {
            System.out.println("No good bets found this time.");
        }
    }

    /*
     * Method will get a list of dates that will be good times to scrape for matches. It will filter out any dates that are not taking place today.
     */
    private static ArrayList<Date> getTimesToScrape(ArrayList<Date> kickOffTimes, int minsAfterLineups, int minsBeforeKickoff) {
        ArrayList<Date> timesToScrape = new ArrayList<>();

        Date currentTime = new Date();
        Date endOfToday = DateHelper.setTimeOfDate(new Date(), 23,59,59);

        ArrayList<Date> todaysKickoffs = new ArrayList<>(kickOffTimes);
        todaysKickoffs.removeIf(new Predicate<Date>() {
            @Override
            public boolean test(Date date) {
                return date.before(currentTime) || date.after(endOfToday);
            }
        });

        Collections.sort(todaysKickoffs);

        //we want to scrape 15mins after lineups are announced and place bet 5mins before game starts. so we have 40min window for each game.
        for (Date kickOffTime: todaysKickoffs) {
            Date earliestCanScrape = DateHelper.addMinsToDate(kickOffTime, -60 + minsAfterLineups);
            Date latestCanScrape = DateHelper.addMinsToDate(kickOffTime, -minsBeforeKickoff);

            if (timesToScrape.size() > 0) {
                Date lastScrape = timesToScrape.get(timesToScrape.size()-1);

                if (!lastScrape.after(earliestCanScrape) || !lastScrape.before(latestCanScrape)) {
                    timesToScrape.add(latestCanScrape);
                }
            }
            else timesToScrape.add(latestCanScrape);
        }

        return timesToScrape;
    }

    private static void decideWhetherToScrapeInPlayedGames() {
        Calendar rightNow = Calendar.getInstance();
        int dayOfMonth = rightNow.get(Calendar.DAY_OF_MONTH);
        boolean evenDay = dayOfMonth % 2 == 0;

        //no need to update played games every day as teams play at MOST once every 2 games (usually once every 3 is minimum).
        if (evenDay || dayOfMonth == 1) { //added if dayOfMonth is 1 because some months will end in 31, with the next day being 1, so we wouldn't scrape for 3 days which might miss a teams last game.
            UpdatePlayedGames.main(new String[]{});
        }
    }
}
