package database;

import com.petermarshall.DateHelper;
import com.petermarshall.database.datasource.DS_Main;
import com.petermarshall.database.datasource.DS_Update;
import com.petermarshall.database.tables.BetTable;
import com.petermarshall.database.tables.LeagueTable;
import com.petermarshall.database.tables.MatchTable;
import com.petermarshall.database.tables.PlayerRatingTable;
import com.petermarshall.scrape.classes.League;
import com.petermarshall.scrape.classes.Match;
import com.petermarshall.scrape.classes.Season;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Date;

import static com.petermarshall.database.datasource.DS_Main.TEST_CONNECTION_NAME;
import static com.petermarshall.database.datasource.DS_Main.connection;
import static database.GenerateData.*;
import static org.junit.Assert.fail;

public class Update {
    @Before
    public static void setup() {
        DS_Main.openTestConnection();
        DS_Main.initDB();
    }

    @Test
    public static void addsStatsCorrectly() {
        GenerateData data = addBulkData(false);
        League league = data.getLeagues().get(0);
        Season season = league.getAllSeasons().get(0);
        Match match = season.getAllMatches().get(0);
        String playerInMatch = match.getHomePlayerRatings().values().iterator().next().getName();
        try {
            Statement s = connection.createStatement();
            ResultSet rs = s.executeQuery("SELECT " + MatchTable.getColHomeScore() + ", " + MatchTable.getColAwayScore() + ", " + MatchTable.getColHomeXg() + ", " + MatchTable.getColAwayXg() + ", " +
                    MatchTable.getColHomeWinOdds() + ", " + MatchTable.getColDrawOdds() + ", " + MatchTable.getColAwayWinOdds() + ", " + MatchTable.getColFirstScorer() +
                    " FROM " + PlayerRatingTable.getTableName() +
                    " INNER JOIN " + MatchTable.getTableName() + " ON " + PlayerRatingTable.getColMatchId() + " = " + MatchTable.getTableName() + "._id" +
                    " WHERE " + PlayerRatingTable.getTableName() + "." + PlayerRatingTable.getColPlayerName() + " = '" + playerInMatch + "'");
            int numbRecords = rs.getFetchSize();
            Assert.assertEquals(1, numbRecords);
            while (rs.next()) {
                //ensuring there is no data already
                int homeScore = rs.getInt(1);
                int awayScore = rs.getInt(2);
                double homeXg = rs.getDouble(3);
                double awayXg = rs.getDouble(4);
                double homeOdds = rs.getDouble(5);
                double drawOdds = rs.getDouble(6);
                double awayOdds = rs.getDouble(7);
                int firstScorer = rs.getInt(8);
                Assert.assertEquals(null, homeScore);
                Assert.assertEquals(null, awayScore);
                Assert.assertEquals(null, homeXg);
                Assert.assertEquals(null, homeXg);
                Assert.assertEquals(null, homeXg);
                Assert.assertEquals(null, homeXg);
                Assert.assertEquals(null, homeXg);
                Assert.assertEquals(-1, firstScorer);
            }
            match.setHomeScore(HOMESCORE);
            match.setAwayScore(AWAYSCORE);
            match.setHomeXGF(HOMEXG);
            match.setAwayXGF(AWAYXG);
            ArrayList<Double> homeDrawAwayOdds = new ArrayList<>();
            homeDrawAwayOdds.add(HOMEODDS);
            homeDrawAwayOdds.add(DRAWODDS);
            homeDrawAwayOdds.add(AWAYODDS);
            match.setHomeDrawAwayOdds(homeDrawAwayOdds);
            match.setFirstScorer(FIRSTSCORER);
            DS_Update.updateGamesInDB(league, season);
            //checking after update
            ResultSet rsAfterUpdate = s.executeQuery("SELECT " + MatchTable.getColHomeScore() + ", " + MatchTable.getColAwayScore() + ", " + MatchTable.getColHomeXg() + ", " + MatchTable.getColAwayXg() + ", " +
                    MatchTable.getColHomeWinOdds() + ", " + MatchTable.getColDrawOdds() + ", " + MatchTable.getColAwayWinOdds() + ", " + MatchTable.getColFirstScorer() +
                    " FROM " + PlayerRatingTable.getTableName() +
                    " INNER JOIN " + MatchTable.getTableName() + " ON " + PlayerRatingTable.getColMatchId() + " = " + MatchTable.getTableName() + "._id" +
                    " WHERE " + PlayerRatingTable.getTableName() + "." + PlayerRatingTable.getColPlayerName() + " = '" + playerInMatch + "'");
            int numbRecordsAfterUpdate = rs.getFetchSize();
            Assert.assertEquals(1, numbRecordsAfterUpdate);
            while (rsAfterUpdate.next()) {
                int homeScore = rs.getInt(1);
                int awayScore = rs.getInt(2);
                double homeXg = rs.getDouble(3);
                double awayXg = rs.getDouble(4);
                double homeOdds = rs.getDouble(5);
                double drawOdds = rs.getDouble(6);
                double awayOdds = rs.getDouble(7);
                int firstScorer = rs.getInt(8);
                Assert.assertEquals(HOMESCORE, homeScore);
                Assert.assertEquals(AWAYSCORE, awayScore);
                Assert.assertEquals(HOMEXG, homeXg, 0.0001);
                Assert.assertEquals(AWAYXG, awayXg, 0.0001);
                Assert.assertEquals(HOMEODDS, homeOdds, 0.0001);
                Assert.assertEquals(DRAWODDS, drawOdds, 0.0001);
                Assert.assertEquals(AWAYODDS, awayOdds, 0.0001);
                Assert.assertEquals(FIRSTSCORER, firstScorer);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            fail();
        }
    }

    @Test
    public static void canUpdateTheKickoffTime() {
        GenerateData data = addBulkData(false);
        League league = data.getLeagues().get(0);
        Season season = league.getAllSeasons().get(0);
        int seasonYearStart = season.getSeasonYearStart();
        Match match = season.getAllMatches().get(2);
        String homeTeam = match.getHomeTeam().getTeamName();
        String awayTeam = match.getAwayTeam().getTeamName();
        String playerInMatch = match.getHomePlayerRatings().values().iterator().next().getName();
        try {
            Statement s = connection.createStatement();
            ResultSet rs = s.executeQuery("SELECT _id FROM " + LeagueTable.getTableName() +
                    " WHERE " + LeagueTable.getColName() + " = '" + league.getName() + "'");
            int numbRecords = rs.getFetchSize();
            Assert.assertEquals(1, numbRecords);
            int leagueId = -1;
            while (rs.next()) {
                leagueId = rs.getInt(1);
            }
            String newStartDate = DateHelper.getSqlDate(new Date(0));
            DS_Update.updateKickoffTime(seasonYearStart, homeTeam, awayTeam, newStartDate, leagueId);
            ResultSet rsAfterUpdate = s.executeQuery("SELECT " + MatchTable.getColDate() + " FROM " + PlayerRatingTable.getTableName() +
                    " INNER JOIN " + MatchTable.getTableName() + " ON " + PlayerRatingTable.getColMatchId() + " = " + MatchTable.getTableName() + "._id" +
                    " WHERE " + PlayerRatingTable.getTableName() + "." + PlayerRatingTable.getColPlayerName() + " = '" + playerInMatch + "'");
            int numbRecordsAfterUpdate = rs.getFetchSize();
            Assert.assertEquals(1, numbRecords);
            while (rs.next()) {
                String dateFromDb = rs.getString(1);
                Assert.assertEquals(newStartDate, dateFromDb);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            fail();
        }
    }

    @After
    public static void tearDown() {
        try {
            if (connection.getMetaData().getURL().equals(TEST_CONNECTION_NAME)) {
                Statement s = connection.createStatement();
                s.addBatch("DROP TABLE " + LeagueTable.getTableName());
                s.addBatch("DROP TABLE " + MatchTable.getTableName());
                s.addBatch("DROP TABLE " + PlayerRatingTable.getTableName());
                s.addBatch("DROP TABLE " + BetTable.getTableName());
                s.executeBatch();
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }
}
