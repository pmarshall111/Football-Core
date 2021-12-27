package com.petermarshall.taskScheduling;

import com.petermarshall.DateHelper;
import com.petermarshall.database.datasource.DS_Get;
import com.petermarshall.database.datasource.DS_Main;
import com.petermarshall.machineLearning.createData.classes.MatchToPredict;
import com.petermarshall.machineLearning.createData.CalcPastStats;
//import com.petermarshall.machineLearning.logisticRegression.Predict;
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
        ArrayList<Date> kickOffTimes = SofaScore.updateKickoffTimes(new Date(), true);

        ArrayList<Date> scrapingTimes = getTimesToScrape(kickOffTimes, minsAfterLineupsAnnouned, minsBeforeKickoff);
        Timer timer = new Timer();

        HashSet<String> bookiesWeveSignedUpFor = new HashSet<>();
        bookiesWeveSignedUpFor.add(OddsCheckerBookies.BET365.getName());
        bookiesWeveSignedUpFor.add(OddsCheckerBookies.SKYBET.getName());
        bookiesWeveSignedUpFor.add(OddsCheckerBookies.BETVICTOR.getName());
        bookiesWeveSignedUpFor.add(OddsCheckerBookies.LADBROKES.getName());


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
            int minsTillRuntime = DateHelper.findMinutesToAddToDate1ToGetDate2(new Date(), scrapeTime);
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
        Date earliestGame = DateHelper.addXMinsToDate(scrapeTime, minsBeforeKickoff);
        Date latestGame = DateHelper.addXMinsToDate(scrapeTime, 60 - minsAfterLineupsAnnouned);


        DS_Main.openProductionConnection();
        ArrayList<MatchToPredict> matchesHappeningNow = DS_Get.getMatchesToPredictByDates(earliestGame, latestGame);
        DS_Main.closeConnection();



        SofaScore.addLineupsToGamesAboutToStart(matchesHappeningNow);
        CalcPastStats.addFeaturesToPredict(matchesHappeningNow, false);
        //for each team from all the games in the database.
        OddsChecker.addBookiesOddsForGames(matchesHappeningNow);
//        Predict.addOurProbabilitiesToGames(matchesHappeningNow, trainedThetasPath);



        StringBuilder emailBody = new StringBuilder();
        emailBody.append("Dear app user,\n\n We currently suggest placing the following bets: \n\n");


        //method can be called without last argument, to assume that we've signed up for all bookies.
//        boolean gamesToEmail = Predict.calcBetsForCurrentGamesAndAddToBuilder(matchesHappeningNow, emailBody, bookiesWeveSignedUpFor);
//        if (gamesToEmail) {
//            SendEmail.sendOutEmail("New bet", emailBody.toString());
//            System.out.println("We found a good bet!");
//        } else {
//            System.out.println("No good bets found this time.");
//        }
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
            Date earliestCanScrape = DateHelper.addXMinsToDate(kickOffTime, -60 + minsAfterLineups);
            Date latestCanScrape = DateHelper.addXMinsToDate(kickOffTime, -minsBeforeKickoff);

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
//        Calendar rightNow = Calendar.getInstance();
//        int dayOfMonth = rightNow.get(Calendar.DAY_OF_MONTH);
//        boolean evenDay = dayOfMonth % 2 == 0;
//
//        //no need to update played games every day as teams play at MOST once every 2 games (usually once every 3 is minimum).
//        if (evenDay || dayOfMonth == 1) { //added if dayOfMonth is 1 because some months will end in 31, with the next day being 1, so we wouldn't scrape for 3 days which might miss a teams last game.
//            UpdatePlayedGames.main(new String[]{});
//        }
    }
}
