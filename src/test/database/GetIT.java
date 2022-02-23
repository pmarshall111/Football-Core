package database;

import com.footballbettingcore.database.FirstScorer;
import com.footballbettingcore.database.datasource.DS_Get;
import com.footballbettingcore.database.datasource.DS_Insert;
import com.footballbettingcore.database.datasource.DS_Main;
import com.footballbettingcore.database.datasource.dbTables.MatchTable;
import com.footballbettingcore.database.datasource.dbTables.TeamTable;
import com.footballbettingcore.machineLearning.createData.HistoricMatchDbData;
import com.footballbettingcore.machineLearning.createData.PlayerMatchDbData;
import com.footballbettingcore.machineLearning.createData.classes.MatchToPredict;
import com.footballbettingcore.scrape.classes.*;
import com.footballbettingcore.utils.DateHelper;
import org.junit.jupiter.api.*;
import org.testcontainers.containers.MariaDBContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.utility.DockerImageName;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;

import static com.footballbettingcore.database.datasource.DS_Main.connection;
import static com.footballbettingcore.machineLearning.createData.CalcPastStats.NUMB_MATCHES_BEFORE_VALID_TRAINING_DATA;
import static database.GenerateData.*;
import static org.junit.jupiter.api.Assertions.*;


public class GetIT {
    @Container
    public static final MariaDBContainer mariaDb = new MariaDBContainer<>(DockerImageName.parse("mariadb:10.5.5"))
            .withReuse(true);

    @BeforeAll
    public static void setup() {
        mariaDb.start();
        DS_Main.openTestConnection(
                mariaDb.getJdbcUrl(),
                mariaDb.getUsername(),
                mariaDb.getPassword()
        );
    }

    @BeforeEach
    public void addData() {
        DS_Main.initDB();
    }

    @AfterEach
    public void removeDbTables() {
        DbTestHelper.dropDatabaseTables();
    }

    @AfterAll
    public static void tearDown() {
        mariaDb.stop();
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
        assertEquals(1, pastMatches.size());
        //add new match with the home team now playing away (vice versa). Should now have 2 games total, but only 1 venue specific
        s.addNewMatch(new Match(away, home, new Date()));
        DS_Insert.writeLeagueToDb(l);
        pastMatches = DS_Get.getMatchesBetweenTeams(l.getName(), matches);
        assertEquals(2, pastMatches.size());
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
            assertEquals(HOMESCORE, pmdbData.getHomeScore());
            assertEquals(AWAYSCORE, pmdbData.getAwayScore());
            assertEquals(HOMEXG, pmdbData.getHomeXGF(), 0.001);
            assertEquals(AWAYXG, pmdbData.getAwayXGF(), 0.001);
            assertEquals(HOMEODDS, pmdbData.getHomeOdds(), 0.001);
            assertEquals(DRAWODDS, pmdbData.getDrawOdds(), 0.001);
            assertEquals(AWAYODDS, pmdbData.getAwayOdds(), 0.001);
            assertEquals(FIRSTSCORER, pmdbData.getFirstScorer());
            //player stats
            assertEquals(MINUTES, pmdbData.getMins());
            assertEquals(RATING, pmdbData.getRating(), 0.001);
            assertEquals(POSITION, pmdbData.getPlayerPosition());
            assertNotNull(pmdbData.getName());
        }
        assertEquals(s.getAllMatches().size()*NUMB_PLAYERS_PER_MATCH*2, pmdbDataArr.size());
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
        noXg.setHomeDrawAwayOdds(new ArrayList<>(Arrays.asList(2.99,3.11,4.66)));
        noXg.setFirstScorer(FirstScorer.HOME_FIRST);
        dataHelper.addPlayerRatingsToMatch(noXg, season, 2, "epl");

        Match noFirstScorer = s.addNewMatch(new Match(t2,t3, DateHelper.subtractXDaysFromDate(new Date(), 3), 1, 3));
        noFirstScorer.setHomeDrawAwayOdds(new ArrayList<>(Arrays.asList(2.333,3.888,4.555)));
        noFirstScorer.setHomeXGF(3.2);
        noFirstScorer.setAwayXGF(6.1);
        dataHelper.addPlayerRatingsToMatch(noFirstScorer, season, 4, "epl");
        //test
        DS_Insert.writeLeagueToDb(l);
        ArrayList<PlayerMatchDbData> pmdbDataArr = DS_Get.getLeagueData(l.getName(), season);
        if (pmdbDataArr == null) {
            fail();
        }
        assertEquals(normal.getHomePlayerRatings().size() + normal.getAwayPlayerRatings().size(), pmdbDataArr.size());
        for (PlayerMatchDbData pmdbData: pmdbDataArr) {
            //match stats
            assertEquals(normal.getHomeScore(), pmdbData.getHomeScore());
            assertEquals(normal.getAwayScore(), pmdbData.getAwayScore());
            assertEquals(normal.getHomeXGF(), pmdbData.getHomeXGF(), 0.001);
            assertEquals(normal.getAwayXGF(), pmdbData.getAwayXGF(), 0.001);
            assertEquals(homeOdds, pmdbData.getHomeOdds(), 0.001);
            assertEquals(drawOdds, pmdbData.getDrawOdds(), 0.001);
            assertEquals(awayOdds, pmdbData.getAwayOdds(), 0.001);
            assertEquals(normal.getFirstScorer().getSqlIntCode(), pmdbData.getFirstScorer());
            //player stats
            assertEquals(MINUTES, pmdbData.getMins());
            assertEquals(RATING, pmdbData.getRating(), 0.001);
            assertEquals(POSITION, pmdbData.getPlayerPosition());
            assertNotNull(pmdbData.getName());
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
                t7 = s.addNewTeam(new Team("team7")), t8 = s.addNewTeam(new Team("team8")),
                t9 = s.addNewTeam(new Team("team9"));

        //NOTE: all games with same are on different dates. Important for getting ids out of db for predictions.
        //IMPORTANT: t6 vs t5 (+rev), t7 vs t8 (+rev), t5 vs t8 (+rev), t7 vs t5 (+rev) MUST NOT BE USED HERE as they are needed for tests
        //db constraint ensures there is only 1 match for home team vs away team. so need to manually add games to get teams to required number of games
        if (NUMB_MATCHES_BEFORE_VALID_TRAINING_DATA >= 1) {                                                                 // games played per team
            s.addNewMatch(new Match(t5,t7, DateHelper.subtractXDaysFromDate(new Date(), 4), 2, 1)); // t5: 1, t7: 1
            s.addNewMatch(new Match(t6,t8, DateHelper.subtractXDaysFromDate(new Date(), 4), 4,2)); // t5: 1, t6: 1, t7: 1, t8:1
        }
        if (NUMB_MATCHES_BEFORE_VALID_TRAINING_DATA >= 2) {
            s.addNewMatch(new Match(t7,t5, DateHelper.subtractXDaysFromDate(new Date(), 5),3,1)); // t5: 2, t6: 1, t7: 2, t8:1
            s.addNewMatch(new Match(t8,t6, DateHelper.subtractXDaysFromDate(new Date(), 5),8,0)); // t5: 2, t6: 2, t7: 2, t8:2
        }
        if (NUMB_MATCHES_BEFORE_VALID_TRAINING_DATA >= 3) {
            s.addNewMatch(new Match(t2,t5, DateHelper.subtractXDaysFromDate(new Date(), 6),1,1)); // t2: 1, t5: 3, t6: 2, t7: 2, t8:2
            s.addNewMatch(new Match(t3,t6, DateHelper.subtractXDaysFromDate(new Date(), 6),0,0)); // t2: 1, t3: 1, t5: 3, t6: 3, t7: 2, t8:2
            s.addNewMatch(new Match(t4,t7, DateHelper.subtractXDaysFromDate(new Date(), 6),5,1)); // t2: 1, t3: 1, t4: 1, t5: 3, t6: 3, t7: 3, t8:2
            s.addNewMatch(new Match(t1,t8, DateHelper.subtractXDaysFromDate(new Date(), 6),1,4)); // t1: 1, t2: 1, t3: 1, t4: 1, t5: 3, t6: 3, t7: 3, t8:3
        }
        if (NUMB_MATCHES_BEFORE_VALID_TRAINING_DATA >= 4) {
            s.addNewMatch(new Match(t5,t2, DateHelper.subtractXDaysFromDate(new Date(), 7),2,3));// t1: 1, t2: 2, t3: 1, t4: 1, t5: 4, t6: 3, t7: 3, t8:3
            s.addNewMatch(new Match(t6,t3, DateHelper.subtractXDaysFromDate(new Date(), 7),4,3));// t1: 1, t2: 2, t3: 2, t4: 1, t5: 4, t6: 4, t7: 3, t8:3
            s.addNewMatch(new Match(t7,t4, DateHelper.subtractXDaysFromDate(new Date(), 7),0,3));// t1: 1, t2: 2, t3: 2, t4: 2, t5: 4, t6: 4, t7: 4, t8:3
            s.addNewMatch(new Match(t8,t1, DateHelper.subtractXDaysFromDate(new Date(), 7),1,0));// t1: 2, t2: 2, t3: 2, t4: 2, t5: 4, t6: 4, t7: 4, t8:4
        }
        if (NUMB_MATCHES_BEFORE_VALID_TRAINING_DATA >= 5) {
            s.addNewMatch(new Match(t5,t1, DateHelper.subtractXDaysFromDate(new Date(), 8),4,1));// t1: 3, t2: 2, t3: 2, t4: 2, t5: 5, t6: 4, t7: 4, t8:4
            s.addNewMatch(new Match(t6,t2, DateHelper.subtractXDaysFromDate(new Date(), 8),2,0));// t1: 3, t2: 3, t3: 2, t4: 2, t5: 5, t6: 5, t7: 4, t8:4
            s.addNewMatch(new Match(t7,t3, DateHelper.subtractXDaysFromDate(new Date(), 8),1,2));// t1: 3, t2: 3, t3: 3, t4: 2, t5: 5, t6: 5, t7: 5, t8:4
            s.addNewMatch(new Match(t8,t4, DateHelper.subtractXDaysFromDate(new Date(), 8),3,3));// t1: 3, t2: 3, t3: 3, t4: 3, t5: 5, t6: 5, t7: 5, t8:5
        }
        if (NUMB_MATCHES_BEFORE_VALID_TRAINING_DATA >= 6) {
            s.addNewMatch(new Match(t1,t5, DateHelper.subtractXDaysFromDate(new Date(), 9),0,0));// t1: 4, t2: 3, t3: 3, t4: 3, t5: 6, t6: 5, t7: 5, t8:5
            s.addNewMatch(new Match(t2,t6, DateHelper.subtractXDaysFromDate(new Date(), 9),0,0));// t1: 4, t2: 4, t3: 3, t4: 3, t5: 6, t6: 6, t7: 5, t8:5
            s.addNewMatch(new Match(t3,t7, DateHelper.subtractXDaysFromDate(new Date(), 9),0,0));// t1: 4, t2: 4, t3: 4, t4: 3, t5: 6, t6: 6, t7: 6, t8:5
            s.addNewMatch(new Match(t4,t8, DateHelper.subtractXDaysFromDate(new Date(), 9),0,0));// t1: 4, t2: 4, t3: 4, t4: 4, t5: 6, t6: 6, t7: 6, t8:6
        }
        if (NUMB_MATCHES_BEFORE_VALID_TRAINING_DATA >= 7) {
            s.addNewMatch(new Match(t5,t4, DateHelper.subtractXDaysFromDate(new Date(), 10),1,1));// t1: 4, t2: 4, t3: 4, t4: 5, t5: 7, t6: 6, t7: 6, t8:6
            s.addNewMatch(new Match(t6,t1, DateHelper.subtractXDaysFromDate(new Date(), 10),1,1));// t1: 5, t2: 4, t3: 4, t4: 5, t5: 7, t6: 7, t7: 6, t8:6
            s.addNewMatch(new Match(t7,t2, DateHelper.subtractXDaysFromDate(new Date(), 10),1,1));// t1: 5, t2: 5, t3: 4, t4: 5, t5: 7, t6: 7, t7: 7, t8:6
            s.addNewMatch(new Match(t8,t3, DateHelper.subtractXDaysFromDate(new Date(), 10),1,1));// t1: 5, t2: 5, t3: 5, t4: 5, t5: 7, t6: 7, t7: 7, t8:7
        }

        // teams 1-4 should be lower than the number of matches required. teams 5-8 should be above.
        Match inPast = s.addNewMatch(new Match(t2,t1, DateHelper.subtractXDaysFromDate(new Date(), 3),0,0));
        Match pastWithPrediction = s.addNewMatch(new Match(t3, t4, DateHelper.subtractXDaysFromDate(new Date(), 2),3,2));
        assertTrue(t8.getAllMatches().size() >= NUMB_MATCHES_BEFORE_VALID_TRAINING_DATA);
        assertTrue(t7.getAllMatches().size() >= NUMB_MATCHES_BEFORE_VALID_TRAINING_DATA);
        Match pastWithPredictionAndReqGames = s.addNewMatch(new Match(t8, t7, DateHelper.subtractXDaysFromDate(new Date(), 1),3,2));
        //future games
        assertTrue(t1.getAllMatches().size() < NUMB_MATCHES_BEFORE_VALID_TRAINING_DATA);
        assertTrue(t2.getAllMatches().size() < NUMB_MATCHES_BEFORE_VALID_TRAINING_DATA);
        Match fAlreadyPredicted = s.addNewMatch(new Match(t1,t2, DateHelper.addXDaysToDate(new Date(),1)));
        assertTrue(t7.getAllMatches().size() >= NUMB_MATCHES_BEFORE_VALID_TRAINING_DATA);
        assertTrue(t8.getAllMatches().size() >= NUMB_MATCHES_BEFORE_VALID_TRAINING_DATA);
        Match fAlreadyPredictedAndReqGames = s.addNewMatch(new Match(t7,t8, DateHelper.addXDaysToDate(new Date(),2)));
        assertTrue(t3.getAllMatches().size() < NUMB_MATCHES_BEFORE_VALID_TRAINING_DATA);
        assertTrue(t4.getAllMatches().size() < NUMB_MATCHES_BEFORE_VALID_TRAINING_DATA);
        Match fNotPredicted = s.addNewMatch(new Match(t4,t9, DateHelper.addXDaysToDate(new Date(), 3)));
        assertTrue(t6.getAllMatches().size() >= NUMB_MATCHES_BEFORE_VALID_TRAINING_DATA);
        assertTrue(t5.getAllMatches().size() >= NUMB_MATCHES_BEFORE_VALID_TRAINING_DATA);
        Match fNotPredictedAndReqGames = s.addNewMatch(new Match(t6,t5, DateHelper.addXDaysToDate(new Date(), 4))); //SHOULD RETURN THIS ONE. Important for test never been t6@home vs t5
        Match fNotPredAndReqGamesNotNextMatch = s.addNewMatch(new Match(t5,t6, DateHelper.addXDaysToDate(new Date(),5))); // shouldn't predict this one as both teams have a game to play before this

        //add demo data to DB
        DS_Insert.writeLeagueToDb(l);

        String sqlMatchDatePast = DateHelper.getSqlDate(pastWithPrediction.getKickoffTime()),
                sqlMatchDatePastAndReqGames = DateHelper.getSqlDate(pastWithPredictionAndReqGames.getKickoffTime()),
                sqlMatchDateFuture = DateHelper.getSqlDate(fAlreadyPredicted.getKickoffTime()),
                sqlMatchDateFutureAndReqGames = DateHelper.getSqlDate(fAlreadyPredictedAndReqGames.getKickoffTime());

        //add a prediction for match we're saying are already predicted.
        try (Statement stmt = connection.createStatement()) {
            // get out db ids so we can add prediction
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
            assertNotEquals(-1, dbIdPastWithPred);
            assertNotEquals(-1, dbIdPastWithPredAndReqGames);
            assertNotEquals(-1, dbIdFuture);
            assertNotEquals(-1, dbIdFutureAndReqGames);

            // add predictions
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
                assertEquals(1, matches.size());
                MatchToPredict mtpNotPredicted = matches.get(0);
                assertEquals(fNotPredictedAndReqGames.getHomeTeam().getTeamName(), mtpNotPredicted.getHomeTeamName());
                assertEquals(fNotPredictedAndReqGames.getAwayTeam().getTeamName(), mtpNotPredicted.getAwayTeamName());
            } else {
                assertEquals(2, matches.size());
            }
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
        assertEquals(1, leaguesToUpdate.keySet().size());
        League firstLeague = leaguesToUpdate.keySet().iterator().next();
        assertEquals(pastWithoutScore.getName(), firstLeague.getName());
        assertEquals(DateHelper.getSqlDate(dateOfEarliestGame), leaguesToUpdate.get(firstLeague));
    }
}
