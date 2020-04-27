package database;

import com.petermarshall.database.datasource.DS_Insert;
import com.petermarshall.database.datasource.DS_Main;
import com.petermarshall.database.tables.*;
import com.petermarshall.scrape.classes.*;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.*;

import static com.petermarshall.database.datasource.DS_Main.connection;
import static database.GenerateData.*;
import static org.junit.Assert.fail;

public class Write {
    @Before
    public void setup() {
        DbTestHelper.setupNewTestDb();
    }

    @Test
    public void canAddRecordsWithIncreasingIds() {
        //did not use autoincrement as this hurts performance.
        addBulkData(true);
        try {
            Statement s = connection.createStatement();
            ResultSet rs = s.executeQuery("SELECT _id FROM " + MatchTable.getTableName());
            int prevId = 0;
            while (rs.next()) {
                int id = rs.getInt(1);
                Assert.assertEquals(prevId + 1, id);
                prevId = id;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            fail();
        }
    }

    @Test
    public void insertsCorrectNumbOfMatches() {
        GenerateData data = addBulkData(true);
        addBulkData(true);
        try {
            Statement s = connection.createStatement();
            ResultSet rs = s.executeQuery("SELECT COUNT(*) FROM " + MatchTable.getTableName());
            while (rs.next()) {
                int count = rs.getInt(1);
                Assert.assertEquals(data.getMatches().size(), count);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            fail();
        }
    }

    @Test
    public void insertsAPlayerWithCorrectTeam() {
        GenerateData data = addBulkData(true);
        Match m = data.getMatches().get(10);
        String homeTeamName = m.getHomeTeam().getTeamName();
        String awayTeamName = m.getAwayTeam().getTeamName();
        String firstHomePlayer = m.getHomePlayerRatings().values().iterator().next().getName();
        String firstAwayPlayer = m.getAwayPlayerRatings().values().iterator().next().getName();
        try {
            Statement s = connection.createStatement();
            //home
            ResultSet rs = s.executeQuery(
                    "SELECT COUNT(*) FROM " + PlayerRatingTable.getTableName() +
                        " INNER JOIN " + TeamTable.getTableName() + " ON " + PlayerRatingTable.getColTeamId() + " = " + TeamTable.getTableName() + "._id" +
                        " WHERE " + PlayerRatingTable.getTableName() + "." + PlayerRatingTable.getColPlayerName() + " = '" + firstHomePlayer + "'" +
                        " AND " + TeamTable.getTableName() + "." + TeamTable.getColTeamName() + " = '" + homeTeamName + "'");
            while (rs.next()) {
                int count = rs.getInt(1);
                Assert.assertEquals(1, count);
            }
            //away
            rs = s.executeQuery(
                    "SELECT COUNT(*) FROM " + PlayerRatingTable.getTableName() +
                            " INNER JOIN " + TeamTable.getTableName() + " ON " + PlayerRatingTable.getColTeamId() + " = " + TeamTable.getTableName() + "._id" +
                            " WHERE " + PlayerRatingTable.getTableName() + "." + PlayerRatingTable.getColPlayerName() + " = '" + firstAwayPlayer + "'" +
                            " AND " + TeamTable.getTableName() + "." + TeamTable.getColTeamName() + " = '" + awayTeamName + "'");
            while (rs.next()) {
                int count = rs.getInt(1);
                Assert.assertEquals(1, count);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            fail();
        }
    }

    @Test
    public void addsTheCorrectDataToMatch() {
        GenerateData data = addBulkData(true);
        Match m = data.getMatches().get(10);
        String playerInMatch = m.getHomePlayerRatings().values().iterator().next().getName();
        try {
            Statement s = connection.createStatement();
            ResultSet rs = s.executeQuery(
                    "SELECT " + MatchTable.getColHomeScore() + ", " + MatchTable.getColAwayScore() + ", " + MatchTable.getColHomeXg() + ", " + MatchTable.getColAwayXg() + ", " +
                            MatchTable.getColHomeWinOdds() + ", " + MatchTable.getColDrawOdds() + ", " + MatchTable.getColAwayWinOdds() + ", " + MatchTable.getColFirstScorer() +
                            " FROM " + PlayerRatingTable.getTableName() +
                            " INNER JOIN " + MatchTable.getTableName() + " ON " + PlayerRatingTable.getColMatchId() + " = " + MatchTable.getTableName() + "._id" +
                            " WHERE " + PlayerRatingTable.getTableName() + "." + PlayerRatingTable.getColPlayerName() + " = '" + playerInMatch + "'");
            while (rs.next()) {
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
    public void addsTheCorrectDataToPlayer() {
        GenerateData data = addBulkData(true);
        PlayerRating pr = data.getPlayerRatings().get(100);
        String playerName = pr.getName();
        try {
            Statement s = connection.createStatement();
            ResultSet rs = s.executeQuery(
                    "SELECT " + PlayerRatingTable.getColMins() + ", " + PlayerRatingTable.getColRating() +
                            " FROM " + PlayerRatingTable.getTableName() +
                            " WHERE " + PlayerRatingTable.getTableName() + "." + PlayerRatingTable.getColPlayerName() + " = '" + playerName + "'");
            while (rs.next()) {
                int mins = rs.getInt(1);
                double rating = rs.getDouble(2);
                Assert.assertEquals(MINUTES, mins);
                Assert.assertEquals(RATING, rating, 0.0001);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            fail();
        }
    }

    @Test
    public void doesntAddRepeatedPlayer() {
        GenerateData data = addBulkData(true);
        PlayerRating pr = data.getPlayerRatings().get(100);
        String playerName = pr.getName();
        try {
            Statement s = connection.createStatement();
            //first get out matchid and teamid of player
            ResultSet rs = s.executeQuery(
                    "SELECT " + PlayerRatingTable.getColMatchId() + ", " + PlayerRatingTable.getColTeamId() +
                            " FROM " + PlayerRatingTable.getTableName() +
                            " WHERE " + PlayerRatingTable.getColPlayerName() + " = '" + playerName + "'");
            int matchId = -1, teamId = -1;
            while (rs.next()) {
                matchId = rs.getInt(1);
                teamId = rs.getInt(2);
            }
            HashMap<String, PlayerRating> pRatings = new HashMap<>();
            pRatings.put(playerName, pr);
            //try insert repeated player
            DS_Insert.addPlayerRatingsToBatch(s, pRatings, matchId, teamId);
            s.executeBatch();
            //check if in db
            s.executeQuery("SELECT COUNT(*) FROM " + PlayerRatingTable.getTableName() +
                    " WHERE " + PlayerRatingTable.getColPlayerName() + " = '" + playerName + "'");
            while (rs.next()) {
                int count = rs.getInt(1);
                Assert.assertEquals(1, count);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            fail();
        }
    }

    @Test
    public void addsOtherPlayersEvenWithRepeatedPlayer() {
        GenerateData data = addBulkData(true);
        PlayerRating pr = data.getPlayerRatings().get(100);
        String playerName = pr.getName();
        try {
            Statement s = connection.createStatement();
            //first get out matchid and teamid of player
            ResultSet rs = s.executeQuery(
                    "SELECT " + PlayerRatingTable.getColMatchId() + ", " + PlayerRatingTable.getColTeamId() +
                            " FROM " + PlayerRatingTable.getTableName() +
                            " WHERE " + PlayerRatingTable.getTableName() + "." + PlayerRatingTable.getColPlayerName() + " = '" + playerName + "'");
            int matchId = -1, teamId = -1;
            while (rs.next()) {
                matchId = rs.getInt(1);
                teamId = rs.getInt(2);
            }
            String newPlayerName = "Peter Marshall";
            HashMap<String, PlayerRating> pRatings = new HashMap<>();
            pRatings.put(playerName, pr);
            pRatings.put(newPlayerName, new PlayerRating(90, 10, newPlayerName));
            //try insert repeated & extra player
            DS_Insert.addPlayerRatingsToBatch(s, pRatings, matchId, teamId);
            s.executeBatch();
            //check if in db
            ResultSet withNewPlayerRs = s.executeQuery("SELECT COUNT(*) FROM " + PlayerRatingTable.getTableName() +
                    " WHERE " + PlayerRatingTable.getTableName() + "." + PlayerRatingTable.getColPlayerName() + " = '" + newPlayerName + "'");
            while (withNewPlayerRs.next()) {
                int count = withNewPlayerRs.getInt(1);
                Assert.assertEquals(1, count);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            fail();
        }
    }

    @After
    public void tearDown() {
        DS_Main.closeConnection();
    }

}
