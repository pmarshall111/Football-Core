package database;

import com.petermarshall.DateHelper;
import com.petermarshall.database.datasource.DS_Main;
import com.petermarshall.database.datasource.dbTables.LeagueTable;
import com.petermarshall.database.datasource.dbTables.MatchTable;
import com.petermarshall.database.datasource.dbTables.PlayerRatingTable;
import com.petermarshall.database.datasource.dbTables.TeamTable;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Date;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.fail;

//NOTE: Class must have NO calls to DS_Insert or DS_Update as this file tests production db.
public class Integrity {
    @Before
    public void openConnection() {
        DS_Main.openProductionConnection();
    }

    @Test
    public void noMatchHasMoreThan14PlayersOnOneTeam() {
        try (Statement s = DS_Main.connection.createStatement()) {
            String totalPlayers = "totalplayers";
            //date conditions added to excuse coronavirus times, where 5 substitutes were allowed.
            ResultSet rs = s.executeQuery("SELECT COUNT(*) AS " + totalPlayers + " FROM " + PlayerRatingTable.getTableName() +
                        " INNER JOIN " + MatchTable.getTableName() + " ON " + PlayerRatingTable.getColMatchId() + " = " + MatchTable.getTableName() + "._id" +
                        " WHERE " + MatchTable.getColDate() + " > '" + DateHelper.getSqlDate(DateHelper.createDateyyyyMMdd("2020", "08", "03")) +
                        "' AND " + MatchTable.getColDate() + " < '" + DateHelper.getSqlDate(DateHelper.createDateyyyyMMdd("2020", "05", "16")) +
                        "' GROUP BY " + PlayerRatingTable.getColMatchId() + ", " + PlayerRatingTable.getColTeamId() +
                        " HAVING " + totalPlayers + " > 14");
            while (rs.next()) {
                int count = rs.getInt(1);
                Assert.assertEquals(0, count);
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
            fail();
        }
    }

    @Test
    public void noMatchHasLessThan11PlayersOnOneTeam() {
        try (Statement s = DS_Main.connection.createStatement()) {
            String totalPlayers = "totalplayers";
            ResultSet rs = s.executeQuery("SELECT COUNT(*) AS " + totalPlayers + " FROM " + PlayerRatingTable.getTableName() +
                                            " GROUP BY " + PlayerRatingTable.getColMatchId() + ", " + PlayerRatingTable.getColTeamId() +
                                            " HAVING " + totalPlayers + " < 11");
            while (rs.next()) {
                int count = rs.getInt(1);
                Assert.assertEquals(0, count);
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
            fail();
        }
    }

    @Test
    public void noDuplicatePlayers() {
        try (Statement s = DS_Main.connection.createStatement()) {
            String timesAddedToGame = "timesAddedToGame";
            ResultSet rs = s.executeQuery("SELECT COUNT(*) AS " + timesAddedToGame + " FROM " + PlayerRatingTable.getTableName() +
                    " GROUP BY " + PlayerRatingTable.getColMatchId() + ", " + PlayerRatingTable.getColTeamId() + ", " + PlayerRatingTable.getColPlayerName() +
                    " HAVING " + timesAddedToGame + " > 1");
            while (rs.next()) {
                int count = rs.getInt(1);
                Assert.assertEquals(0, count);
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
            fail();
        }
    }

    //TEST WILL FAIL IF NO DATA IN DB
    @Test
    public void matchesWithNoRatingsMoreThan3DaysAgo() {
        try (Statement s = DS_Main.connection.createStatement()) {
            //need to include the date in query as the database will also have future games that have not yet been played.
            String threeDaysAgo = DateHelper.getSqlDate(DateHelper.subtractXDaysFromDate(new Date(), 3));
            ResultSet rs = s.executeQuery("SELECT COUNT(*) FROM " + MatchTable.getTableName() +
                                                " WHERE _id NOT IN " +
                                                "( SELECT " + PlayerRatingTable.getColMatchId() + " FROM " + PlayerRatingTable.getTableName() + ")" +
                                                " AND " + MatchTable.getColDate() + " < '" + threeDaysAgo + "'");
            while (rs.next()) {
                int count = rs.getInt(1);
                Assert.assertEquals(0, count);
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
            fail();
        }
    }

    @Test
    public void noPartiallyCompletedGames() {
        try (Statement s = DS_Main.connection.createStatement()) {
            ResultSet rs = s.executeQuery("SELECT COUNT(*) FROM " + MatchTable.getTableName() +
                                            " WHERE (" + MatchTable.getColAwayScore() + " = -1" +
                                                " OR " + MatchTable.getColHomeScore() + " = -1" +
                                                " OR " + MatchTable.getColAwayXg() + " = -1" +
                                                " OR " + MatchTable.getColHomeXg() + " = -1)" +
                                                " AND (" + MatchTable.getColAwayScore() + " > -1" +
                                                " OR " + MatchTable.getColHomeScore() + " > -1" +
                                                " OR " + MatchTable.getColAwayXg() + " > -1" +
                                                " OR " + MatchTable.getColHomeXg() + " > -1)");
            while (rs.next()) {
                int count = rs.getInt(1);
                Assert.assertEquals(0, count);
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
            fail();
        }
    }

    //Problem with this test is that some players have the same name - Juanfran, Raul Garcia, Naldo, Danilo, Éder, Adama Traoré, Rafael. These have been checked
    //to make sure there are multiple players in the 6 leagues with the same name.
    //possible to play for 3 clubs if you get loaned out for the first half of the season, play some games in January and are then loaned out for the end of the season
    @Test
    public void playersOnlyPlayFor3ClubsInASeason() {
        try {
            Statement s = DS_Main.connection.createStatement();
            String CLUBS_IN_SEASON = "clubsPlayedForInSeason";
            ResultSet rs = s.executeQuery("SELECT " + PlayerRatingTable.getColPlayerName() + ", COUNT(*) AS " + CLUBS_IN_SEASON + " FROM " +
                                    "(" +
                                    " SELECT " + PlayerRatingTable.getColPlayerName() + ", " + MatchTable.getColSeasonYearStart() + " FROM " + PlayerRatingTable.getTableName() +
                                    " INNER JOIN " + TeamTable.getTableName() + " ON " + PlayerRatingTable.getColTeamId() + " = " + TeamTable.getTableName() + "._id" +
                                    " INNER JOIN " + MatchTable.getTableName() + " ON " + PlayerRatingTable.getColMatchId() + " = " + MatchTable.getTableName() + "._id" +
                                    " WHERE " + PlayerRatingTable.getColPlayerName() + " != 'Juanfran'" +
                                    " AND " + PlayerRatingTable.getColPlayerName() + " != 'Danilo'" +
                                    " AND " + PlayerRatingTable.getColPlayerName() + " != 'Éder'" +
                                    " AND " + PlayerRatingTable.getColPlayerName() + " != 'Raúl García'" +
                                    " AND " + PlayerRatingTable.getColPlayerName() + " != 'Adama Traoré'" +
                                    " AND " + PlayerRatingTable.getColPlayerName() + " != 'Rafael'" +
                                    " AND " + PlayerRatingTable.getColPlayerName() + " != 'Naldo'" +
                                    " GROUP BY " + MatchTable.getColSeasonYearStart() + ", " + PlayerRatingTable.getColPlayerName() + ", " + PlayerRatingTable.getColTeamId() +
                                    " ORDER BY " + PlayerRatingTable.getColPlayerName() +
                                    ") AS PLAYERS_FOR_EACH_TEAM" +
                                " GROUP BY " + PlayerRatingTable.getColPlayerName() + ", " + MatchTable.getColSeasonYearStart() +
                                " HAVING " + CLUBS_IN_SEASON + " > 3");
            while (rs.next()) {
                String playerName = rs.getString(1);
                int count = rs.getInt(2);
                Assert.assertEquals("Failed for: " + playerName, 0, count);
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
            fail();
        }
    }

    @Test
    public void teamsAreGivenTheRightLeagueId() {
        try (Statement s = DS_Main.connection.createStatement()) {
            String home = "home", away = "away";
            ResultSet rs = s.executeQuery("SELECT COUNT(*) FROM " + MatchTable.getTableName() +
                    " INNER JOIN " + TeamTable.getTableName() + " AS " + home + " ON " + MatchTable.getTableName() + "." + MatchTable.getColHometeamId() + " = " + home + "._id" +
                    " INNER JOIN " + TeamTable.getTableName() + " AS " + away + " ON " + MatchTable.getTableName() + "." + MatchTable.getColAwayteamId() + " = " + away + "._id" +
                    " WHERE " + home + "." + TeamTable.getColLeagueId() + " != " + away + "." + TeamTable.getColLeagueId());
            while (rs.next()) {
                int count = rs.getInt(1);
                Assert.assertEquals(0, count);
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
            fail();
        }
    }

    //TEST WILL FAIL IF NO DATA IN DB.
    //delete any matches that are abandoned and awarded 3-0 victories without playing. this data doesn't help us to predict results.
    //or add any data that the scraper has missed (sofascore may not have the data)
    @Test
    public void noGameMoreThan3DaysAgoWithoutStats() {
        try (Statement s = DS_Main.connection.createStatement()) {

            System.out.println("SELECT COUNT(*) FROM " + MatchTable.getTableName() +
                    " WHERE " + MatchTable.getColDate() + " < '" + DateHelper.getSqlDate(DateHelper.subtractXDaysFromDate(new Date(), 3)) + "'" +
                    " AND (" + MatchTable.getColHomeScore() + " = -1" +
                    " OR " + MatchTable.getColAwayScore() + " = -1" +
                    " OR " + MatchTable.getColHomeXg() + " = -1" +
                    " OR " + MatchTable.getColAwayXg() + " = -1" +
                    " OR " + MatchTable.getColHomeWinOdds() + " = -1" +
                    " OR " + MatchTable.getColDrawOdds() + " = -1" +
                    " OR " + MatchTable.getColAwayWinOdds() + " = -1" +
                    " OR (" + MatchTable.getColFirstScorer() + " = -1 AND (" + MatchTable.getColHomeScore() + " >0 OR " + MatchTable.getColAwayScore() + " >0)))");

            ResultSet rs = s.executeQuery("SELECT COUNT(*) FROM " + MatchTable.getTableName() +
                    " WHERE " + MatchTable.getColDate() + " < '" + DateHelper.getSqlDate(DateHelper.subtractXDaysFromDate(new Date(), 3)) + "'" +
                    " AND (" + MatchTable.getColHomeScore() + " = -1" +
                        " OR " + MatchTable.getColAwayScore() + " = -1" +
                        " OR " + MatchTable.getColHomeXg() + " = -1" +
                        " OR " + MatchTable.getColAwayXg() + " = -1" +
                        " OR " + MatchTable.getColHomeWinOdds() + " = -1" +
                        " OR " + MatchTable.getColDrawOdds() + " = -1" +
                        " OR " + MatchTable.getColAwayWinOdds() + " = -1" +
                        " OR (" + MatchTable.getColFirstScorer() + " = -1 AND (" + MatchTable.getColHomeScore() + " >0 OR " + MatchTable.getColAwayScore() + " >0)))");

            while (rs.next()) {
                int count = rs.getInt(1);
                Assert.assertEquals(0, count);
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
            fail();
        }
    }

    @After
    public void tearDown() {
        DS_Main.closeConnection();
    }
}
