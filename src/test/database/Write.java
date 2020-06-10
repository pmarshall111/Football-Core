package database;

import com.petermarshall.database.datasource.DS_Insert;
import com.petermarshall.database.datasource.DS_Main;
import com.petermarshall.database.tables.*;
import com.petermarshall.scrape.classes.*;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.theories.suppliers.TestedOn;

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
        GenerateData data = addBulkData(true);
        try {
            Statement s = connection.createStatement();
            ResultSet rs = s.executeQuery("SELECT max(_id) FROM " + MatchTable.getTableName());
            Integer prevId = null;
            while (rs.next()) {
                prevId = rs.getInt(1);
            }
            League league = data.getLeagues().get(0);
            Season untouchedSeason = league.getSeason(16);
            Team team1 = untouchedSeason.addNewTeam(new Team("increasingId_team1"));
            Team team2 = untouchedSeason.addNewTeam(new Team("increasingId_team2"));
            untouchedSeason.addNewMatch(new Match(team1, team2, new Date(), 3, 1));
            DS_Insert.writeLeagueToDb(league);
            ResultSet rsAfterInsert = s.executeQuery("SELECT max(_id) FROM " + MatchTable.getTableName());
            while (rsAfterInsert.next()) {
                int id = rsAfterInsert.getInt(1);
                Assert.assertNotNull(prevId);
                Assert.assertEquals(prevId+1, id);
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
            System.out.println("SELECT COUNT(*) FROM " + PlayerRatingTable.getTableName() +
                    " INNER JOIN " + TeamTable.getTableName() + " ON " + PlayerRatingTable.getColTeamId() + " = " + TeamTable.getTableName() + "._id" +
                    " WHERE " + PlayerRatingTable.getTableName() + "." + PlayerRatingTable.getColPlayerName() + " = '" + firstHomePlayer + "'" +
                    " AND " + TeamTable.getTableName() + "." + TeamTable.getColTeamName() + " = '" + homeTeamName + "'");
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
            rs = s.executeQuery("SELECT COUNT(*) FROM " + PlayerRatingTable.getTableName() +
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

    @Test
    public void givesPromotedTeamsCorrectLeagueId() {
        GenerateData data = addBulkData(true);
        League l = data.getLeagues().get(0);
        Season s = l.getAllSeasons().stream().reduce((emptySeason, season) -> season.hasMatches() ? emptySeason : season).get();
        String t1Name = "new_team1";
        String t2Name = "new_team2";
        Team t1 = s.addNewTeam(new Team(t1Name));
        Team t2 = s.addNewTeam(new Team(t2Name));
        Match m = s.addNewMatch(new Match(t1, t2, new Date(), 100, 0));
        try {
            //first get out leagueid of league we added match to
            Statement stmt = connection.createStatement();
            ResultSet rs = stmt.executeQuery(
                    "SELECT _id FROM " + LeagueTable.getTableName() +
                        " WHERE " + LeagueTable.getColName() + " = '" + l.getName() + "'");
            int leagueId = -1;
            while (rs.next()) {
                leagueId = rs.getInt(1);
            }
            //now insert league to DB again
            DS_Insert.writeLeagueToDb(l);
            //get out the ids of the teams we just added
            ResultSet afterAddRs = stmt.executeQuery("SELECT " + TeamTable.getColLeagueId() +
                    " FROM " + TeamTable.getTableName() +
                    " WHERE " + TeamTable.getColTeamName() + " = '" + t1Name + "'" +
                    " OR " + TeamTable.getColTeamName() + " = '" + t2Name + "'");
            while (afterAddRs.next()) {
                int afterAddleagueId = afterAddRs.getInt(1);
                Assert.assertEquals(leagueId, afterAddleagueId);
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
