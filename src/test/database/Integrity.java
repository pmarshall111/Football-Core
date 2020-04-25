package database;

import com.petermarshall.DateHelper;
import com.petermarshall.database.datasource.DS_Main;
import com.petermarshall.database.tables.MatchTable;
import com.petermarshall.database.tables.PlayerRatingTable;
import com.petermarshall.database.tables.TeamTable;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Date;

import static org.junit.Assert.fail;

public class Integrity {
    @Before
    public static void openConnection() {
        DS_Main.openConnection();
    }

    @Test
    public static void noMatchHasMoreThan14PlayersOnOneTeam() {
        try {
            Statement s = DS_Main.connection.createStatement();
            String totalPlayers = "totalplayers";
            ResultSet rs = s.executeQuery("SELECT COUNT(*) AS " + totalPlayers + " FROM " + PlayerRatingTable.getTableName() +
                                                " GROUP BY " + PlayerRatingTable.getColMatchId() + ", " + PlayerRatingTable.getColTeamId() +
                                                " HAVING " + totalPlayers + " > 14");
            int numbResults = rs.getFetchSize();
            Assert.assertEquals(0, numbResults);
        } catch (SQLException throwables) {
            throwables.printStackTrace();
            fail();
        }
    }

    @Test
    public static void noMatchHasLessThan11PlayersOnOneTeam() {
        try {
            Statement s = DS_Main.connection.createStatement();
            String totalPlayers = "totalplayers";
            ResultSet rs = s.executeQuery("SELECT COUNT(*) AS " + totalPlayers + " FROM " + PlayerRatingTable.getTableName() +
                    " GROUP BY " + PlayerRatingTable.getColMatchId() + ", " + PlayerRatingTable.getColTeamId() +
                    " HAVING " + totalPlayers + " < 11");
            int numbResults = rs.getFetchSize();
            Assert.assertEquals(0, numbResults);
        } catch (SQLException throwables) {
            throwables.printStackTrace();
            fail();
        }
    }

    @Test
    public static void noDuplicatePlayers() {
        try {
            Statement s = DS_Main.connection.createStatement();
            String timesAddedToGame = "timesAddedToGame";
            ResultSet rs = s.executeQuery("SELECT COUNT(*) AS " + timesAddedToGame + " FROM " + PlayerRatingTable.getTableName() +
                    " GROUP BY " + PlayerRatingTable.getColMatchId() + ", " + PlayerRatingTable.getColTeamId() + ", " + PlayerRatingTable.getColPlayerName() +
                    " HAVING " + timesAddedToGame + " > 1");
            int numbResults = rs.getFetchSize();
            Assert.assertEquals(0, numbResults);
        } catch (SQLException throwables) {
            throwables.printStackTrace();
            fail();
        }
    }

    @Test
    public static void matchesWithNoRatings() {
        try {
            Statement s = DS_Main.connection.createStatement();
            //need to include the date in query as the database will also have future games that have not yet been played.
            String currDate = DateHelper.getSqlDate(new Date());
            ResultSet rs = s.executeQuery("SELECT * FROM " + MatchTable.getTableName() +
                                                " WHERE _id NOT IN " +
                                                "( SELECT " + PlayerRatingTable.getColMatchId() + " FROM " + PlayerRatingTable.getTableName() + ")" +
                                                " AND " + MatchTable.getColDate() + " < " + currDate);
            int numbResults = rs.getFetchSize();
            Assert.assertEquals(0, numbResults);
        } catch (SQLException throwables) {
            throwables.printStackTrace();
            fail();
        }
    }

    @Test
    public static void noMissedGames() {
        try {
            Statement s = DS_Main.connection.createStatement();
            String currDate = DateHelper.getSqlDate(new Date());
            ResultSet rs = s.executeQuery("SELECT * FROM " + MatchTable.getTableName() +
                                            " WHERE (" + MatchTable.getColDate() + " < " + currDate +
                                            " AND " + MatchTable.getColHomeScore() + " = -1");
            int numbResults = rs.getFetchSize();
            Assert.assertEquals(0, numbResults);
        } catch (SQLException throwables) {
            throwables.printStackTrace();
            fail();
        }
    }

    @Test
    public static void noPartiallyCompletedGames() {
        try {
            Statement s = DS_Main.connection.createStatement();
            ResultSet rs = s.executeQuery("SELECT * FROM " + MatchTable.getTableName() +
                                            " WHERE (" + MatchTable.getColAwayScore() + " = -1" +
                                                " OR " + MatchTable.getColHomeScore() + " = -1" +
                                                " OR " + MatchTable.getColAwayXg() + " = -1" +
                                                " OR " + MatchTable.getColHomeXg() + " = -1)" +
                                                " AND (" + MatchTable.getColAwayScore() + " > -1" +
                                                " OR " + MatchTable.getColHomeScore() + " > -1" +
                                                " OR " + MatchTable.getColAwayXg() + " > -1" +
                                                " OR " + MatchTable.getColHomeXg() + " > -1)");
            int numbResults = rs.getFetchSize();
            Assert.assertEquals(0, numbResults);
        } catch (SQLException throwables) {
            throwables.printStackTrace();
            fail();
        }
    }

    @Test
    public static void playersOnlyPlayFor2ClubsInASeason() {
        try {
            Statement s = DS_Main.connection.createStatement();
            String CLUBS_IN_SEASON = "clubsPlayedForInSeason";
            ResultSet rs = s.executeQuery("SELECT COUNT(*) AS " + CLUBS_IN_SEASON + " FROM " +
                                    "(" +
                                    " SELECT * FROM " + PlayerRatingTable.getTableName() +
                                    " INNER JOIN " + TeamTable.getTableName() + " ON " + PlayerRatingTable.getColTeamId() + " = " + TeamTable.getTableName() + "._id" +
                                    " INNER JOIN " + MatchTable.getTableName() + " ON " + PlayerRatingTable.getColMatchId() + " = " + MatchTable.getTableName() + "._id" +
                                    " GROUP BY " + MatchTable.getColSeasonYearStart() + ", " + PlayerRatingTable.getColPlayerName() + ", " + PlayerRatingTable.getColTeamId() +
                                    " ORDER BY " + PlayerRatingTable.getColPlayerName() +
                                    ")" +
                                " GROUP BY " + PlayerRatingTable.getColPlayerName() + ", " + MatchTable.getColSeasonYearStart() +
                                " HAVING " + CLUBS_IN_SEASON + " > 2");

            int numbResults = rs.getFetchSize();
            Assert.assertEquals(0, numbResults);
        } catch (SQLException throwables) {
            throwables.printStackTrace();
            fail();
        }
    }

    @After
    public static void tearDown() {
        DS_Main.closeConnection();
    }
}
