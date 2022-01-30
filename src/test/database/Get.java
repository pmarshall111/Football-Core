package database;

import com.footballbettingcore.utils.DateHelper;
import com.footballbettingcore.database.FirstScorer;
import com.footballbettingcore.database.datasource.DS_Get;
import com.footballbettingcore.database.datasource.DS_Insert;
import com.footballbettingcore.database.datasource.DS_Main;
import com.footballbettingcore.database.datasource.dbTables.MatchTable;
import com.footballbettingcore.database.datasource.dbTables.TeamTable;
import com.footballbettingcore.machineLearning.createData.classes.MatchToPredict;
import com.footballbettingcore.machineLearning.createData.HistoricMatchDbData;
import com.footballbettingcore.machineLearning.createData.PlayerMatchDbData;
import com.footballbettingcore.scrape.classes.*;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.*;

import static com.footballbettingcore.database.datasource.DS_Main.connection;
import static com.footballbettingcore.machineLearning.createData.CalcPastStats.NUMB_MATCHES_BEFORE_VALID_TRAINING_DATA;
import static database.GenerateData.*;
import static org.junit.Assert.fail;

public class Get {
    @Before
    public void setup() {
        DbTestHelper.setupNewTestDb();
    }

    @After
    public void tearDown() {
        DS_Main.closeConnection();
    }

    @Test
    public void canGetTheLastCompletedMatch() {
        GenerateData data = addBulkData(true);
        League l = data.getLeagues().get(0);
        Season s = l.getSeason(20);
        Match lastMatch = s.getAllMatches().get(s.getAllMatches().size()-1);
        String expectedDate = DateHelper.getSqlDate(lastMatch.getKickoffTime());
        String actualDate = DS_Get.getLastCompletedMatchInLeague(l);
        Assert.assertEquals(expectedDate, actualDate);
    }

    @Test
    public void canGetPreviousMatchesBetweenTeams() {
        GenerateData data = addBulkData(true);
        League l = data.getLeagues().get(0);
        String seasonKey = "19";
        Season s = l.getSeason(Integer.parseInt(seasonKey));
        Match m = s.getAllMatches().get(0);
        Team home = m.getHomeTeam();
        Team away = m.getAwayTeam();
        MatchToPredict mtp = new MatchToPredict(home.getTeamName(), away.getTeamName(), seasonKey, l.getName(), DateHelper.getSqlDate(new Date()), 1, 1);
        ArrayList<MatchToPredict> matches = new ArrayList<>(Arrays.asList(mtp));
        //default matches from GenerateData should be 1 per pair of teams
        ArrayList<HistoricMatchDbData> pastMatches = DS_Get.getMatchesBetweenTeams(l.getName(), matches);
        Assert.assertEquals(1, pastMatches.size());
        //add new match with the home team now playing away (vice versa). Should now have 2 games total, but only 1 venue specific
        s.addNewMatch(new Match(away, home, new Date()));
        DS_Insert.writeLeagueToDb(l);
        pastMatches = DS_Get.getMatchesBetweenTeams(l.getName(), matches);
        Assert.assertEquals(2, pastMatches.size());
    }

    @Test
    public void canGetOutAllDataNeededToPredict() {
        GenerateData data = addBulkData(true);
        League l = data.getLeagues().get(0);
        Season s = l.getSeason(19);
        ArrayList<PlayerMatchDbData> pmdbDataArr = DS_Get.getLeagueData(l.getName(), 19);
        if (pmdbDataArr == null) {
            fail();
        }
        for (PlayerMatchDbData pmdbData: pmdbDataArr) {
            //match stats
            Assert.assertEquals(HOMESCORE, pmdbData.getHomeScore());
            Assert.assertEquals(AWAYSCORE, pmdbData.getAwayScore());
            Assert.assertEquals(HOMEXG, pmdbData.getHomeXGF(), 0.001);
            Assert.assertEquals(AWAYXG, pmdbData.getAwayXGF(), 0.001);
            Assert.assertEquals(HOMEODDS, pmdbData.getHomeOdds(), 0.001);
            Assert.assertEquals(DRAWODDS, pmdbData.getDrawOdds(), 0.001);
            Assert.assertEquals(AWAYODDS, pmdbData.getAwayOdds(), 0.001);
            Assert.assertEquals(FIRSTSCORER, pmdbData.getFirstScorer());
            //player stats
            Assert.assertEquals(MINUTES, pmdbData.getMins());
            Assert.assertEquals(RATING, pmdbData.getRating(), 0.001);
            Assert.assertNotNull(pmdbData.getName());
        }
        Assert.assertEquals(s.getAllMatches().size()*NUMB_PLAYERS_PER_MATCH*2, pmdbDataArr.size());
    }

    @Test
    public void doesNotGetOutGamesWithoutProperStats() {
        //do not want to train on games that do not have scores, xg, firstscorer data
        //create data
        GenerateData dataHelper = new GenerateData(false);
        League l = new League(LeagueIdsAndData.EPL);
        int season = 19;
        Season s = l.getSeason(season);
        Team t1 = s.addNewTeam(new Team("team1")), t2 = s.addNewTeam(new Team("team2")),
                t3 = s.addNewTeam(new Team("team3")), t4 = s.addNewTeam(new Team("team4"));
        Match normal = s.addNewMatch(new Match(t1,t2, DateHelper.subtractXDaysFromDate(new Date(), 5), 0, 0));
        double homeOdds = 2.3, drawOdds = 3.4, awayOdds = 4.5;
        normal.setHomeDrawAwayOdds(new ArrayList<>(Arrays.asList(homeOdds, drawOdds, awayOdds)));
        normal.setHomeXGF(2.2);
        normal.setAwayXGF(1.1);
        normal.setFirstScorer(FirstScorer.NO_FIRST_SCORER);
        dataHelper.addPlayerRatingsToMatch(normal, season, 1, "epl");

        Match noXg = s.addNewMatch(new Match(t3,t4, DateHelper.subtractXDaysFromDate(new Date(), 5), 4, 0));
        noXg.setHomeDrawAwayOdds(new ArrayList<>(Arrays.asList(2.9,3.1,4.6)));
        noXg.setFirstScorer(FirstScorer.HOME_FIRST);
        dataHelper.addPlayerRatingsToMatch(noXg, season, 2, "epl");

        Match noFirstScorer = s.addNewMatch(new Match(t2,t3, DateHelper.subtractXDaysFromDate(new Date(), 3), 1, 3));
        noFirstScorer.setHomeDrawAwayOdds(new ArrayList<>(Arrays.asList(2.3,3.8,4.5)));
        noFirstScorer.setHomeXGF(3.2);
        noFirstScorer.setAwayXGF(6.1);
        dataHelper.addPlayerRatingsToMatch(noFirstScorer, season, 4, "epl");
        //test
        DS_Insert.writeLeagueToDb(l);
        ArrayList<PlayerMatchDbData> pmdbDataArr = DS_Get.getLeagueData(l.getName(), season);
        if (pmdbDataArr == null) {
            fail();
        }
        Assert.assertEquals(normal.getHomePlayerRatings().size() + normal.getAwayPlayerRatings().size(), pmdbDataArr.size());
        for (PlayerMatchDbData pmdbData: pmdbDataArr) {
            //match stats
            Assert.assertEquals(normal.getHomeScore(), pmdbData.getHomeScore());
            Assert.assertEquals(normal.getAwayScore(), pmdbData.getAwayScore());
            Assert.assertEquals(normal.getHomeXGF(), pmdbData.getHomeXGF(), 0.001);
            Assert.assertEquals(normal.getAwayXGF(), pmdbData.getAwayXGF(), 0.001);
            Assert.assertEquals(homeOdds, pmdbData.getHomeOdds(), 0.001);
            Assert.assertEquals(drawOdds, pmdbData.getDrawOdds(), 0.001);
            Assert.assertEquals(awayOdds, pmdbData.getAwayOdds(), 0.001);
            Assert.assertEquals(normal.getFirstScorer().getSqlIntCode(), pmdbData.getFirstScorer());
            //player stats
            Assert.assertEquals(MINUTES, pmdbData.getMins());
            Assert.assertEquals(RATING, pmdbData.getRating(), 0.001);
            Assert.assertNotNull(pmdbData.getName());
        }
    }

    @Test
    public void canGetOutNewMatchesToPredict() {
        //testing that only games in the future that are the first game for both teams are got out
        //both teams must have also played more games than the training threshold. Currently 7...
        League l = new League(LeagueIdsAndData.EPL);
        Season s = l.getSeason(DateHelper.getStartYearForCurrentSeason());
        //1-4 are used as teams without enough games, 5-8 are teams who have played required games.
        Team t1 = s.addNewTeam(new Team("team1")), t2 = s.addNewTeam(new Team("team2")),
                t3 = s.addNewTeam(new Team("team3")), t4 = s.addNewTeam(new Team("team4")),
                t5 = s.addNewTeam(new Team("team5")), t6 = s.addNewTeam(new Team("team6")),
                t7 = s.addNewTeam(new Team("team7")), t8 = s.addNewTeam(new Team("team8"));

        //NOTE: all games with same are on different dates. Important for getting ids out of db for predictions.
        //IMPORTANT: t6vst5 (+rev), t7vst8 (+rev), t5vst8 (+rev), t7vst5 (+rev) MUST NOT BE USED HERE as they are needed for tests
        //cannot use for loop as db constraint ensures there are no repeat fixtures
        if (NUMB_MATCHES_BEFORE_VALID_TRAINING_DATA >= 1) {
            s.addNewMatch(new Match(t5,t7, DateHelper.subtractXDaysFromDate(new Date(), 4), 2, 1));
            s.addNewMatch(new Match(t6,t8, DateHelper.subtractXDaysFromDate(new Date(), 4), 4,2));
        }
        if (NUMB_MATCHES_BEFORE_VALID_TRAINING_DATA >= 2) {
            s.addNewMatch(new Match(t7,t5, DateHelper.subtractXDaysFromDate(new Date(), 5),3,1));
            s.addNewMatch(new Match(t8,t6, DateHelper.subtractXDaysFromDate(new Date(), 5),8,0));
        }
        if (NUMB_MATCHES_BEFORE_VALID_TRAINING_DATA >= 3) {
            s.addNewMatch(new Match(t2,t5, DateHelper.subtractXDaysFromDate(new Date(), 6),1,1));
            s.addNewMatch(new Match(t3,t6, DateHelper.subtractXDaysFromDate(new Date(), 6),0,0));
            s.addNewMatch(new Match(t4,t7, DateHelper.subtractXDaysFromDate(new Date(), 6),5,1));
            s.addNewMatch(new Match(t1,t8, DateHelper.subtractXDaysFromDate(new Date(), 6),1,4));
        }
        if (NUMB_MATCHES_BEFORE_VALID_TRAINING_DATA >= 4) {
            s.addNewMatch(new Match(t5,t2, DateHelper.subtractXDaysFromDate(new Date(), 7),2,3));
            s.addNewMatch(new Match(t6,t3, DateHelper.subtractXDaysFromDate(new Date(), 7),4,3));
            s.addNewMatch(new Match(t7,t4, DateHelper.subtractXDaysFromDate(new Date(), 7),0,3));
            s.addNewMatch(new Match(t8,t1, DateHelper.subtractXDaysFromDate(new Date(), 7),1,0));
        }
        if (NUMB_MATCHES_BEFORE_VALID_TRAINING_DATA >= 5) {
            s.addNewMatch(new Match(t5,t1, DateHelper.subtractXDaysFromDate(new Date(), 8),4,1));
            s.addNewMatch(new Match(t6,t2, DateHelper.subtractXDaysFromDate(new Date(), 8),2,0));
            s.addNewMatch(new Match(t7,t3, DateHelper.subtractXDaysFromDate(new Date(), 8),1,2));
            s.addNewMatch(new Match(t8,t4, DateHelper.subtractXDaysFromDate(new Date(), 8),3,3));
        }
        if (NUMB_MATCHES_BEFORE_VALID_TRAINING_DATA >= 6) {
            s.addNewMatch(new Match(t1,t5, DateHelper.subtractXDaysFromDate(new Date(), 9),0,0));
            s.addNewMatch(new Match(t2,t6, DateHelper.subtractXDaysFromDate(new Date(), 9),0,0));
            s.addNewMatch(new Match(t3,t7, DateHelper.subtractXDaysFromDate(new Date(), 9),0,0));
            s.addNewMatch(new Match(t4,t8, DateHelper.subtractXDaysFromDate(new Date(), 9),0,0));
        }
        if (NUMB_MATCHES_BEFORE_VALID_TRAINING_DATA >= 7) {
            s.addNewMatch(new Match(t5,t4, DateHelper.subtractXDaysFromDate(new Date(), 10),1,1));
            s.addNewMatch(new Match(t6,t1, DateHelper.subtractXDaysFromDate(new Date(), 10),1,1));
            s.addNewMatch(new Match(t7,t2, DateHelper.subtractXDaysFromDate(new Date(), 10),1,1));
            s.addNewMatch(new Match(t8,t3, DateHelper.subtractXDaysFromDate(new Date(), 10),1,1));
        }
        Match inPast = s.addNewMatch(new Match(t2,t1, DateHelper.subtractXDaysFromDate(new Date(), 3),0,0));
        Match pastWithPrediction = s.addNewMatch(new Match(t3, t4, DateHelper.subtractXDaysFromDate(new Date(), 2),3,2));
        Assert.assertTrue(t8.getAllMatches().size() >= NUMB_MATCHES_BEFORE_VALID_TRAINING_DATA);
        Assert.assertTrue(t7.getAllMatches().size() >= NUMB_MATCHES_BEFORE_VALID_TRAINING_DATA);
        Match pastWithPredictionAndReqGames = s.addNewMatch(new Match(t8, t7, DateHelper.subtractXDaysFromDate(new Date(), 1),3,2));
        //future games
        Assert.assertTrue(t1.getAllMatches().size() < NUMB_MATCHES_BEFORE_VALID_TRAINING_DATA);
        Assert.assertTrue(t2.getAllMatches().size() < NUMB_MATCHES_BEFORE_VALID_TRAINING_DATA);
        Match fAlreadyPredicted = s.addNewMatch(new Match(t1,t2, DateHelper.addXDaysToDate(new Date(),1)));
        Assert.assertTrue(t7.getAllMatches().size() >= NUMB_MATCHES_BEFORE_VALID_TRAINING_DATA);
        Assert.assertTrue(t8.getAllMatches().size() >= NUMB_MATCHES_BEFORE_VALID_TRAINING_DATA);
        Match fAlreadyPredictedAndReqGames = s.addNewMatch(new Match(t7,t8, DateHelper.addXDaysToDate(new Date(),2)));
        Assert.assertTrue(t3.getAllMatches().size() < NUMB_MATCHES_BEFORE_VALID_TRAINING_DATA);
        Assert.assertTrue(t4.getAllMatches().size() < NUMB_MATCHES_BEFORE_VALID_TRAINING_DATA);
        Match fNotPredicted = s.addNewMatch(new Match(t4,t3, DateHelper.addXDaysToDate(new Date(), 3)));
        Assert.assertTrue(t6.getAllMatches().size() >= NUMB_MATCHES_BEFORE_VALID_TRAINING_DATA);
        Assert.assertTrue(t5.getAllMatches().size() >= NUMB_MATCHES_BEFORE_VALID_TRAINING_DATA);
        Match fNotPredictedAndReqGames = s.addNewMatch(new Match(t6,t5, DateHelper.addXDaysToDate(new Date(), 4))); //SHOULD RETURN THIS ONE. Important for test never been t6@home vs t5
        Match fNotPredAndReqGamesNotFirstMatch = s.addNewMatch(new Match(t5,t6, DateHelper.addXDaysToDate(new Date(),5)));

        //add demo data to DB
        DS_Insert.writeLeagueToDb(l);

        String sqlMatchDatePast = DateHelper.getSqlDate(pastWithPrediction.getKickoffTime()),
                sqlMatchDatePastAndReqGames = DateHelper.getSqlDate(pastWithPredictionAndReqGames.getKickoffTime()),
                sqlMatchDateFuture = DateHelper.getSqlDate(fAlreadyPredicted.getKickoffTime()),
                sqlMatchDateFutureAndReqGames = DateHelper.getSqlDate(fAlreadyPredictedAndReqGames.getKickoffTime());

        //add a prediction for match already predicted. need to get out db ids
        try (Statement stmt = connection.createStatement()) {
            String home = "home", away = "away";
            ResultSet rs = stmt.executeQuery("SELECT " + MatchTable.getTableName() + "._id, " + MatchTable.getColDate() + ", " +
                    home + "." + TeamTable.getColTeamName() + " FROM " + MatchTable.getTableName() +
                    " INNER JOIN " + TeamTable.getTableName() + " AS " + home + " ON " + MatchTable.getColHometeamId() + " = " + home + "._id" +
                    " INNER JOIN " + TeamTable.getTableName() + " AS " + away + " ON " + MatchTable.getColAwayteamId() + " = " + away + "._id" +
                    " WHERE (" + home + "." + TeamTable.getColTeamName() + " = '" + fAlreadyPredicted.getHomeTeam().getTeamName() + "'" +
                        " AND " + away + "." + TeamTable.getColTeamName() + " = '" + fAlreadyPredicted.getAwayTeam().getTeamName() + "'" +
                        " AND " + MatchTable.getColDate() + " = '" + sqlMatchDateFuture + "')" +
                    " OR (" + home + "." + TeamTable.getColTeamName() + " = '" + pastWithPredictionAndReqGames.getHomeTeam().getTeamName() + "'" +
                        " AND " + away + "." + TeamTable.getColTeamName() + " = '" + pastWithPredictionAndReqGames.getAwayTeam().getTeamName() + "'" +
                        " AND " + MatchTable.getColDate() + " = '" + sqlMatchDatePastAndReqGames + "')" +
                    " OR (" + home + "." + TeamTable.getColTeamName() + " = '" + pastWithPrediction.getHomeTeam().getTeamName() + "'" +
                        " AND " + away + "." + TeamTable.getColTeamName() + " = '" + pastWithPrediction.getAwayTeam().getTeamName() + "'" +
                        " AND " + MatchTable.getColDate() + " = '" + sqlMatchDatePast + "')" +
                    " OR (" + home + "." + TeamTable.getColTeamName() + " = '" + fAlreadyPredictedAndReqGames.getHomeTeam().getTeamName() + "'" +
                        " AND " + away + "." + TeamTable.getColTeamName() + " = '" + fAlreadyPredictedAndReqGames.getAwayTeam().getTeamName() + "'" +
                        " AND " + MatchTable.getColDate() + " = '" + sqlMatchDateFutureAndReqGames + "')");

            int dbIdPastWithPred = -1, dbIdPastWithPredAndReqGames = -1;
            int dbIdFuture = -1, dbIdFutureAndReqGames = -1;
            while (rs.next()) {
                String date = rs.getString(2);
                String homeTeamName = rs.getString(3);
                if (homeTeamName.equals(pastWithPrediction.getHomeTeam().getTeamName()) && date.equals(sqlMatchDatePast)) {
                    dbIdPastWithPred = rs.getInt(1);
                } else if (homeTeamName.equals(pastWithPredictionAndReqGames.getHomeTeam().getTeamName()) && date.equals(sqlMatchDatePastAndReqGames)) {
                    dbIdPastWithPredAndReqGames = rs.getInt(1);
                } else if (homeTeamName.equals(fAlreadyPredicted.getHomeTeam().getTeamName()) && date.equals(sqlMatchDateFuture)) {
                    dbIdFuture = rs.getInt(1);
                } else {
                    dbIdFutureAndReqGames = rs.getInt(1);
                }
            }
            Assert.assertNotEquals(-1, dbIdPastWithPred);
            Assert.assertNotEquals(-1, dbIdPastWithPredAndReqGames);
            Assert.assertNotEquals(-1, dbIdFuture);
            Assert.assertNotEquals(-1, dbIdFutureAndReqGames);

            MatchToPredict mtpPast = new MatchToPredict(pastWithPrediction.getHomeTeam().getTeamName(), pastWithPrediction.getAwayTeam().getTeamName(),
                    s.getSeasonKey(), l.getName(), sqlMatchDatePast, dbIdPastWithPred, -1);
            MatchToPredict mtpPastWithReqGames = new MatchToPredict(pastWithPredictionAndReqGames.getHomeTeam().getTeamName(), pastWithPredictionAndReqGames.getAwayTeam().getTeamName(),
                    s.getSeasonKey(), l.getName(), sqlMatchDatePast, dbIdPastWithPredAndReqGames, -1);
            MatchToPredict mtpFuture = new MatchToPredict(fAlreadyPredicted.getHomeTeam().getTeamName(), fAlreadyPredicted.getAwayTeam().getTeamName(),
                    s.getSeasonKey(), l.getName(), sqlMatchDateFuture, dbIdFuture, -1);
            MatchToPredict mtpFutureWithReqGames = new MatchToPredict(fAlreadyPredictedAndReqGames.getHomeTeam().getTeamName(), fAlreadyPredictedAndReqGames.getAwayTeam().getTeamName(),
                    s.getSeasonKey(), l.getName(), sqlMatchDateFuture, dbIdFutureAndReqGames, -1);
            mtpPast.setOurPredictions(new double[]{1.1, 3.8, 21.3}, false);
            mtpPastWithReqGames.setOurPredictions(new double[]{1.9, 5.3, 1.3}, false);
            mtpFuture.setOurPredictions(new double[]{1337, 117, 23456}, false);
            mtpFutureWithReqGames.setOurPredictions(new double[]{1, 8, 3}, false);
            ArrayList<MatchToPredict> mtps = new ArrayList<>(Arrays.asList(mtpPast, mtpPastWithReqGames, mtpFuture, mtpFutureWithReqGames));
            DS_Insert.addPredictionsToDb(mtps);

            //test that only 1 game given back
            ArrayList<MatchToPredict> matches = DS_Get.getMatchesToPredict();
            if (NUMB_MATCHES_BEFORE_VALID_TRAINING_DATA > 0) {
                Assert.assertEquals(1, matches.size());
                MatchToPredict mtpNotPredicted = matches.get(0);
                Assert.assertEquals(fNotPredictedAndReqGames.getHomeTeam().getTeamName(), mtpNotPredicted.getHomeTeamName());
                Assert.assertEquals(fNotPredictedAndReqGames.getAwayTeam().getTeamName(), mtpNotPredicted.getAwayTeamName());
            } else {
                Assert.assertEquals(2, matches.size());
            }
        } catch (SQLException e) {
            e.printStackTrace();
            fail();
        }
    }

    @Test
    public void canGetMatchesWithPredictionsButNoOdds() {
        League l = new League(LeagueIdsAndData.EPL);
        Season s = l.getSeason(19);
        Team t1 = s.addNewTeam(new Team("team1")), t2 = s.addNewTeam(new Team("team2")),
                t3 = s.addNewTeam(new Team("team3")), t4 = s.addNewTeam(new Team("team4")),
                t5 = s.addNewTeam(new Team("team5")), t6 = s.addNewTeam(new Team("team6"));

        Match pastWithPrediction = s.addNewMatch(new Match(t1, t4, DateHelper.subtractXDaysFromDate(new Date(), 5)));
        Match futureWithPrediction = s.addNewMatch(new Match(t1, t2, DateHelper.addXDaysToDate(new Date(), 2))); //should get this one out
        Match futureWithPredictionAndOdds = s.addNewMatch(new Match(t3, t4, DateHelper.addXDaysToDate(new Date(), 3)));
        Match futureWithPredictionAndLineups = s.addNewMatch(new Match(t5, t6, DateHelper.addXDaysToDate(new Date(), 4)));
        DS_Insert.writeLeagueToDb(l);

        //add a prediction for match already predicted. need to get out db id
        try (Statement stmt = connection.createStatement()) {
            String home = "home", away = "away";
            ResultSet rs = stmt.executeQuery("SELECT " + MatchTable.getTableName() + "._id, " + MatchTable.getColDate() + ", " +
                    home + "." + TeamTable.getColTeamName() + ", " + away + "." + TeamTable.getColTeamName() +
                    " FROM " + MatchTable.getTableName() +
                    " INNER JOIN " + TeamTable.getTableName() + " AS " + home + " ON " + MatchTable.getColHometeamId() + " = " + home + "._id" +
                    " INNER JOIN " + TeamTable.getTableName() + " AS " + away + " ON " + MatchTable.getColAwayteamId() + " = " + away + "._id" +
                    " WHERE (" + home + "." + TeamTable.getColTeamName() + " = '" + pastWithPrediction.getHomeTeam().getTeamName() + "'" +
                    " AND " + away + "." + TeamTable.getColTeamName() + " = '" + pastWithPrediction.getAwayTeam().getTeamName() + "')" +
                    " OR (" + home + "." + TeamTable.getColTeamName() + " = '" + futureWithPrediction.getHomeTeam().getTeamName() + "'" +
                    " AND " + away + "." + TeamTable.getColTeamName() + " = '" + futureWithPrediction.getAwayTeam().getTeamName() + "')" +
                    " OR (" + home + "." + TeamTable.getColTeamName() + " = '" + futureWithPredictionAndOdds.getHomeTeam().getTeamName() + "'" +
                    " AND " + away + "." + TeamTable.getColTeamName() + " = '" + futureWithPredictionAndOdds.getAwayTeam().getTeamName() + "')" +
                    " OR (" + home + "." + TeamTable.getColTeamName() + " = '" + futureWithPredictionAndLineups.getHomeTeam().getTeamName() + "'" +
                    " AND " + away + "." + TeamTable.getColTeamName() + " = '" + futureWithPredictionAndLineups.getAwayTeam().getTeamName() + "')");

            int dbIdPast = -1;
            int dbIdFuture = -1;
            int dbIdFutureWithOdds = -1;
            int dbIdFutureWithLineups = -1;
            String sqlMatchDatePast = "null";
            String sqlMatchDateFuture = "null";
            String sqlMatchDateFutureWithOdds = "null";
            String sqlMatchDateFutureWithLineups = "null";
            while (rs.next()) {
                String homeTeamName = rs.getString(3);
                String awayTeamName = rs.getString(4);
                if (homeTeamName.equals(pastWithPrediction.getHomeTeam().getTeamName()) && awayTeamName.equals(pastWithPrediction.getAwayTeam().getTeamName())) {
                    dbIdPast = rs.getInt(1);
                    sqlMatchDatePast = rs.getString(2);
                } else if (homeTeamName.equals(futureWithPrediction.getHomeTeam().getTeamName()) && awayTeamName.equals(futureWithPrediction.getAwayTeam().getTeamName())) {
                    dbIdFuture = rs.getInt(1);
                    sqlMatchDateFuture = rs.getString(2);
                } else if (homeTeamName.equals(futureWithPredictionAndOdds.getHomeTeam().getTeamName()) &&
                        awayTeamName.equals(futureWithPredictionAndOdds.getAwayTeam().getTeamName())) {
                    dbIdFutureWithOdds = rs.getInt(1);
                    sqlMatchDateFutureWithOdds = rs.getString(2);
                } else {
                    dbIdFutureWithLineups = rs.getInt(1);
                    sqlMatchDateFutureWithLineups = rs.getString(2);
                }
            }
            Assert.assertNotEquals(-1, dbIdPast);
            Assert.assertNotEquals(-1, dbIdFuture);
            Assert.assertNotEquals(-1, dbIdFutureWithOdds);
            Assert.assertNotEquals(-1, dbIdFutureWithLineups);

            MatchToPredict mtpPast = new MatchToPredict(pastWithPrediction.getHomeTeam().getTeamName(), pastWithPrediction.getAwayTeam().getTeamName(),
                    s.getSeasonKey(), l.getName(), sqlMatchDatePast, dbIdPast, -1);
            mtpPast.setOurPredictions(new double[]{0.1, 0.8, 0.3}, false);
            MatchToPredict mtpFuture = new MatchToPredict(futureWithPrediction.getHomeTeam().getTeamName(), futureWithPrediction.getAwayTeam().getTeamName(),
                    s.getSeasonKey(), l.getName(), sqlMatchDateFuture, dbIdFuture, -1);
            mtpFuture.setOurPredictions(new double[]{1337, 117, 23456}, false);
            MatchToPredict mtpFutureWithOdds = new MatchToPredict(futureWithPredictionAndOdds.getHomeTeam().getTeamName(), futureWithPredictionAndOdds.getAwayTeam().getTeamName(),
                    s.getSeasonKey(), l.getName(), sqlMatchDateFutureWithOdds, dbIdFutureWithOdds, -1);
            mtpFutureWithOdds.setOurPredictions(new double[]{0.5,0.4,0.1}, false);
            LinkedHashMap<String, double[]> bookiesOdds = new LinkedHashMap<>();
            bookiesOdds.put(OddsCheckerBookies.BET365.getName(), new double[]{1.25,1.7,20});
            mtpFutureWithOdds.setBookiesOdds(bookiesOdds);
            MatchToPredict mtpFutureWithLineups = new MatchToPredict(futureWithPredictionAndLineups.getHomeTeam().getTeamName(), futureWithPredictionAndLineups.getAwayTeam().getTeamName(),
                    s.getSeasonKey(), l.getName(), sqlMatchDateFutureWithLineups, dbIdFutureWithLineups, -1);
            mtpFutureWithLineups.setOurPredictions(new double[]{0.2,0.3,0.5}, true);
            ArrayList<MatchToPredict> mtps = new ArrayList<>(Arrays.asList(mtpPast, mtpFuture));
            DS_Insert.addPredictionsToDb(mtps);

            //get out matches with predictions no odds & compare
            ArrayList<MatchToPredict> matches = DS_Get.getMatchesWithPredictionsButNoOdds();
            Assert.assertEquals(1, matches.size());
            Assert.assertEquals(futureWithPrediction.getHomeTeam().getTeamName(), matches.get(0).getHomeTeamName());
            Assert.assertEquals(futureWithPrediction.getAwayTeam().getTeamName(), matches.get(0).getAwayTeamName());
        } catch (SQLException e) {
            e.printStackTrace();
            fail();
        }
    }
    
    @Test
    public void canGetLeaguesThatNeedUpdating() {
        League pastWithoutScore = new League(LeagueIdsAndData.EPL);
        League pastWithScore = new League(LeagueIdsAndData.LA_LIGA);
        
        Season s1 = pastWithoutScore.getSeason(DateHelper.getStartYearForCurrentSeason());
        Team s1t1 = s1.addNewTeam(new Team("s1t1"));
        Team s1t2 = s1.addNewTeam(new Team("s1t2"));
        Date dateOfEarliestGame = DateHelper.subtractXDaysFromDate(new Date(), 4);
        s1.addNewMatch(new Match(s1t1, s1t2, dateOfEarliestGame));
        s1.addNewMatch(new Match(s1t2, s1t1, DateHelper.subtractXDaysFromDate(new Date(), 2))); //2 games with no score, but should only return league one time
        
        Season s2 = pastWithScore.getSeason(DateHelper.getStartYearForCurrentSeason());
        Team s2t1 = s2.addNewTeam(new Team("s2t1"));
        Team s2t2 = s2.addNewTeam(new Team("s2t2"));
        s2.addNewMatch(new Match(s2t1, s2t2, DateHelper.subtractXDaysFromDate(new Date(), 2), 2,1));
        s2.addNewMatch(new Match(s2t2, s2t1, DateHelper.addXDaysToDate(new Date(), 3))); //game with score in past, game no score in future.

        DS_Insert.writeLeagueToDb(pastWithoutScore);
        DS_Insert.writeLeagueToDb(pastWithScore);

        HashMap<League, String> leaguesToUpdate = DS_Get.getLeaguesToUpdate();
        Assert.assertEquals(1, leaguesToUpdate.keySet().size());
        League firstLeague = leaguesToUpdate.keySet().iterator().next();
        Assert.assertEquals(pastWithoutScore.getName(), firstLeague.getName());
        Assert.assertEquals(DateHelper.getSqlDate(dateOfEarliestGame), leaguesToUpdate.get(firstLeague));
    }
}
