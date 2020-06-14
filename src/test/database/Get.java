package database;

import com.petermarshall.DateHelper;
import com.petermarshall.database.datasource.DS_Get;
import com.petermarshall.database.datasource.DS_Insert;
import com.petermarshall.database.datasource.DS_Main;
import com.petermarshall.database.tables.MatchTable;
import com.petermarshall.database.tables.TeamTable;
import com.petermarshall.machineLearning.createData.classes.MatchToPredict;
import com.petermarshall.machineLearning.createData.HistoricMatchDbData;
import com.petermarshall.machineLearning.createData.PlayerMatchDbData;
import com.petermarshall.scrape.classes.*;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;

import static com.petermarshall.database.datasource.DS_Main.connection;
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
        ArrayList<HistoricMatchDbData> pastMatches = DS_Get.getMatchesBetweenTeams(l.getName(), matches,5);
        Assert.assertEquals(1, pastMatches.size());
        //add new match with the home team now playing away (vice versa). Should now have 2 games total, but only 1 venue specific
        s.addNewMatch(new Match(away, home, new Date()));
        DS_Insert.writeLeagueToDb(l);
        pastMatches = DS_Get.getMatchesBetweenTeams(l.getName(), matches,5);
        Assert.assertEquals(2, pastMatches.size());
        pastMatches = DS_Get.getMatchesBetweenTeams(l.getName(), matches,5);
        Assert.assertEquals(1, pastMatches.size());
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
    public void canGetOutNewMatchesToPredict() {
        //testing that only games in the future that are the first game for both teams are got out
        League l = new League(LeagueIdsAndData.EPL);
        Season s = l.getSeason(19);
        Team t1 = s.addNewTeam(new Team("team1")), t2 = s.addNewTeam(new Team("team2")),
                t3 = s.addNewTeam(new Team("team3")), t4 = s.addNewTeam(new Team("team4")),
                t5 = s.addNewTeam(new Team("team5")), t6 = s.addNewTeam(new Team("team6")),
                t7 = s.addNewTeam(new Team("team7"));

        Match inPast = s.addNewMatch(new Match(t2,t1, DateHelper.subtractXDaysFromDate(new Date(), 10)));
        Match pastWithPrediction = s.addNewMatch(new Match(t6, t7, DateHelper.subtractXDaysFromDate(new Date(), 5)));
        //future games
        Match fAlreadyPredicted = s.addNewMatch(new Match(t1,t2, DateHelper.addDaysToDate(new Date(),2)));
        Match fNotPredicted = s.addNewMatch(new Match(t3,t4, DateHelper.addDaysToDate(new Date(), 3)));             //THIS ONE TO BE RETURNED
        Match fNotPredReverseFixture = s.addNewMatch(new Match(t4,t3, DateHelper.addDaysToDate(new Date(),4)));
        Match fOneTeamWithGamesToPlay = s.addNewMatch(new Match(t5,t1, DateHelper.addDaysToDate(new Date(),5)));
        Match fTwoTeamsWithGamesToPlay = s.addNewMatch(new Match(t2,t5, DateHelper.addDaysToDate(new Date(),6)));

        //add demo data to DB
        DS_Insert.writeLeagueToDb(l);

        //add a prediction for match already predicted
        try (Statement stmt = connection.createStatement()) {
            String home = "home", away = "away";
            ResultSet rs = stmt.executeQuery("SELECT " + MatchTable.getTableName() + "._id, " + MatchTable.getColDate() + ", " +
                    home + "." + TeamTable.getColTeamName() + " FROM " + MatchTable.getTableName() +
                    " INNER JOIN " + TeamTable.getTableName() + " AS " + home + " ON " + MatchTable.getColHometeamId() + " = " + home + "._id" +
                    " INNER JOIN " + TeamTable.getTableName() + " AS " + away + " ON " + MatchTable.getColAwayteamId() + " = " + away + "._id" +
                    " WHERE (" + home + "." + TeamTable.getColTeamName() + " = '" + fAlreadyPredicted.getHomeTeam().getTeamName() + "'" +
                    " AND " + away + "." + TeamTable.getColTeamName() + " = '" + fAlreadyPredicted.getAwayTeam().getTeamName() + "')" +
                    " OR (" + home + "." + TeamTable.getColTeamName() + " = '" + pastWithPrediction.getHomeTeam().getTeamName() + "'" +
                    " AND " + away + "." + TeamTable.getColTeamName() + " = '" + pastWithPrediction.getAwayTeam().getTeamName() + "')");

            int dbIdPast = -1;
            int dbIdFuture = -1;
            String sqlMatchDatePast = "null";
            String sqlMatchDateFuture = "null";
            while (rs.next()) {
                String homeTeamName = rs.getString(3);
                if (homeTeamName.equals(pastWithPrediction.getHomeTeam().getTeamName())) {
                    dbIdPast = rs.getInt(1);
                    sqlMatchDatePast = rs.getString(2);
                } else {
                    dbIdFuture = rs.getInt(1);
                    sqlMatchDateFuture = rs.getString(2);
                }
            }
            Assert.assertNotEquals(-1, dbIdFuture);
            Assert.assertNotEquals(-1, dbIdPast);

            MatchToPredict mtpFuture = new MatchToPredict(fAlreadyPredicted.getHomeTeam().getTeamName(), fAlreadyPredicted.getAwayTeam().getTeamName(),
                    s.getSeasonKey(), l.getName(), sqlMatchDateFuture, dbIdFuture, -1);
            MatchToPredict mtpPast = new MatchToPredict(pastWithPrediction.getHomeTeam().getTeamName(), pastWithPrediction.getAwayTeam().getTeamName(),
                    s.getSeasonKey(), l.getName(), sqlMatchDatePast, dbIdPast, -1);
            mtpFuture.setOurPredictions(new double[]{1337, 117, 23456}, false);
            mtpPast.setOurPredictions(new double[]{1.1, 3.8, 21.3}, false);
            ArrayList<MatchToPredict> mtps = new ArrayList<>(Arrays.asList(mtpPast, mtpFuture));
            DS_Insert.addPredictionsToDb(mtps);

            //test that only 1 game given back
            ArrayList<MatchToPredict> matches = DS_Get.getMatchesToPredict();
            Assert.assertEquals(1, matches.size());
            MatchToPredict mtpNotPredicted = matches.get(0);
            Assert.assertEquals(fNotPredicted.getHomeTeam().getTeamName(), mtpNotPredicted.getHomeTeamName());
            Assert.assertEquals(fNotPredicted.getAwayTeam().getTeamName(), mtpNotPredicted.getAwayTeamName());

        } catch (SQLException e) {
            e.printStackTrace();
            fail();
        }
    }
}
