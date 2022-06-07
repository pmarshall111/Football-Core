package com.footballbettingcore.taskScheduling;

import com.footballbettingcore.database.datasource.DS_Main;
import com.footballbettingcore.database.datasource.dbTables.LeagueTable;
import com.footballbettingcore.database.datasource.dbTables.MatchTable;
import com.footballbettingcore.database.datasource.dbTables.PlayerRatingTable;
import com.footballbettingcore.database.datasource.dbTables.TeamTable;
import com.footballbettingcore.mail.SendEmail;
import com.footballbettingcore.scrape.classes.LeagueIdsAndData;
import com.footballbettingcore.utils.DateHelper;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Date;

public class DbIntegrityCheck {
    private static final Logger logger = LogManager.getLogger(DbIntegrityCheck.class);

    private static int ERROR_COUNT = 0;

    public static void main(String[] args) {
        DS_Main.openProductionConnection();
        noMatchHasMoreThan14PlayersOnOneTeam();
        noMatchHasLessThan11PlayersOnOneTeam();
        noDuplicatePlayers();
        noMatchesWithNoRatingsMoreThan3DaysAgo();
        noGameMoreThan3DaysAgoWithoutStats();
        noPartiallyCompletedGames();
        playersOnlyPlayFor3ClubsInASeason();
        teamsAreGivenTheRightLeagueId();
        if (ERROR_COUNT > 0) {
            SendEmail.sendOutEmail("Database Integrity Checks Failed", ERROR_COUNT + " failures.", SendEmail.ADMIN_EMAIL);
        }
    }

    private static void noMatchHasMoreThan14PlayersOnOneTeam() {
        try (Statement s = DS_Main.connection.createStatement()) {
            String totalPlayers = "totalplayers";
            //date conditions added to excuse coronavirus times, where 5 substitutes were allowed.
            String query = "SELECT COUNT(*) AS " + totalPlayers + " FROM " + PlayerRatingTable.getTableName() +
                    " INNER JOIN " + MatchTable.getTableName() + " ON " + PlayerRatingTable.getColMatchId() + " = " + MatchTable.getTableName() + "._id" +
                    " WHERE " + MatchTable.getColDate() + " > '" + DateHelper.getSqlDate(DateHelper.createDateyyyyMMdd("2020", "08", "03")) +
                    "' AND " + MatchTable.getColDate() + " < '" + DateHelper.getSqlDate(DateHelper.createDateyyyyMMdd("2020", "05", "16")) +
                    "' GROUP BY " + PlayerRatingTable.getColMatchId() + ", " + PlayerRatingTable.getColTeamId() +
                    " HAVING " + totalPlayers + " > 14";
            logger.info(query);
            ResultSet rs = s.executeQuery(query);

            while (rs.next()) {
                int count = rs.getInt(1);
                if (count == 0) logger.info("noMatchHasMoreThan14PlayersOnOneTeam PASS");
                else {
                    logger.error("noMatchHasMoreThan14PlayersOnOneTeam FAIL: " + count);
                    ERROR_COUNT++;
                }
            }
        } catch (SQLException e) {
            sqlErr(e);
        }
    }

    private static void noMatchHasLessThan11PlayersOnOneTeam() {
        try (Statement s = DS_Main.connection.createStatement()) {
            String totalPlayers = "totalplayers";
            String query = "SELECT COUNT(*) AS " + totalPlayers + " FROM " + PlayerRatingTable.getTableName() +
                    " GROUP BY " + PlayerRatingTable.getColMatchId() + ", " + PlayerRatingTable.getColTeamId() +
                    " HAVING " + totalPlayers + " < 11";
            logger.info(query);
            ResultSet rs = s.executeQuery(query);
            while (rs.next()) {
                int count = rs.getInt(1);
                if (count == 0) logger.info("noMatchHasLessThan11PlayersOnOneTeam PASS");
                else {
                    logger.error("noMatchHasLessThan11PlayersOnOneTeam FAIL: " + count);
                    ERROR_COUNT++;
                }
            }
        } catch (SQLException e) {
            sqlErr(e);
        }
    }

    private static void noDuplicatePlayers() {
        try (Statement s = DS_Main.connection.createStatement()) {
            String timesAddedToGame = "timesAddedToGame";
            String query = "SELECT COUNT(*) AS " + timesAddedToGame + " FROM " + PlayerRatingTable.getTableName() +
                    " GROUP BY " + PlayerRatingTable.getColMatchId() + ", " + PlayerRatingTable.getColTeamId() + ", " + PlayerRatingTable.getColPlayerName() +
                    " HAVING " + timesAddedToGame + " > 1";
            logger.info(query);
            ResultSet rs = s.executeQuery(query);
            while (rs.next()) {
                int count = rs.getInt(1);
                if (count == 0) logger.info("noDuplicatePlayers PASS");
                else {
                    logger.error("noDuplicatePlayers FAIL: " + count);
                    ERROR_COUNT++;
                }
            }
        } catch (SQLException e) {
            sqlErr(e);
        }
    }

    private static void noMatchesWithNoRatingsMoreThan3DaysAgo() {
        try (Statement s = DS_Main.connection.createStatement()) {
            //need to include the date in query as the database will also have future games that have not yet been played.
            String threeDaysAgo = DateHelper.getSqlDate(DateHelper.subtractXDaysFromDate(new Date(), 3));
            String home = "HOME", away = "AWAY";
            String query = "SELECT COUNT(*) FROM " + MatchTable.getTableName() +
                    " INNER JOIN " + TeamTable.getTableName() + " AS " + home + " ON " + MatchTable.getTableName() + "." + MatchTable.getColHometeamId() + " = " + home + "._id" +
                    " INNER JOIN " + TeamTable.getTableName() + " AS " + away + " ON " + MatchTable.getTableName() + "." + MatchTable.getColAwayteamId() + " = " + away + "._id" +
                    " WHERE " + MatchTable.getTableName() + "._id NOT IN " +
                    "( SELECT " + PlayerRatingTable.getColMatchId() + " FROM " + PlayerRatingTable.getTableName() + ")" +
                    " AND " + MatchTable.getColDate() + " < '" + threeDaysAgo + "'" +
                    " AND " + MatchTable.getColSeasonYearStart() + " != 19" +
                    " AND " + MatchTable.getColIsPostponed() + " != 1";

            logger.info(query);
            ResultSet rs = s.executeQuery(query);
            while (rs.next()) {
                int count = rs.getInt(1);
                if (count == 0) logger.info("noMatchesWithNoRatingsMoreThan3DaysAgo PASS");
                else {
                    logger.error("noMatchesWithNoRatingsMoreThan3DaysAgo FAIL: " + count);
                    ERROR_COUNT++;
                }
            }
        } catch (SQLException e) {
            sqlErr(e);
        }
    }

    private static void noPartiallyCompletedGames() {
        try (Statement s = DS_Main.connection.createStatement()) {
            String query = "SELECT COUNT(*) FROM " + MatchTable.getTableName() +
                    " WHERE (" + MatchTable.getColAwayScore() + " = -1" +
                    " OR " + MatchTable.getColHomeScore() + " = -1" +
                    " OR " + MatchTable.getColAwayXg() + " = -1" +
                    " OR " + MatchTable.getColHomeXg() + " = -1)" +
                    " AND (" + MatchTable.getColAwayScore() + " > -1" +
                    " OR " + MatchTable.getColHomeScore() + " > -1" +
                    " OR " + MatchTable.getColAwayXg() + " > -1" +
                    " OR " + MatchTable.getColHomeXg() + " > -1)";
            logger.info(query);
            ResultSet rs = s.executeQuery(query);
            while (rs.next()) {
                int count = rs.getInt(1);
                if (count == 0) logger.info("noPartiallyCompletedGames PASS");
                else {
                    logger.error("noPartiallyCompletedGames FAIL: " + count);
                    ERROR_COUNT++;
                }
            }
        } catch (SQLException e) {
            sqlErr(e);
        }
    }

    //Problem with this test is that some players have the same name - Juanfran, Raul Garcia, Naldo, Danilo, Éder, Adama Traoré, Rafael. These have been checked
    //to make sure there are multiple players in the 6 leagues with the same name.
    //possible to play for 3 clubs if you get loaned out for the first half of the season, play some games in January and are then loaned out for the end of the season
    private static void playersOnlyPlayFor3ClubsInASeason() {
        try {
            Statement s = DS_Main.connection.createStatement();
            String CLUBS_IN_SEASON = "clubsPlayedForInSeason";
            String query = "SELECT " + PlayerRatingTable.getColPlayerName() + ", COUNT(*) AS " + CLUBS_IN_SEASON + " FROM " +
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
                    " HAVING " + CLUBS_IN_SEASON + " > 3";
            logger.info(query);
            ResultSet rs = s.executeQuery(query);
            boolean hasErrored = false;
            while (rs.next()) {
                String playerName = rs.getString(1);
                int count = rs.getInt(2);
                if (count != 0) {
                    logger.error("playersOnlyPlayFor3ClubsInASeason FAIL: " + playerName);
                    hasErrored = true;
                }
            }
            if (!hasErrored) logger.info("playersOnlyPlayFor3ClubsInASeason PASS");
            else ERROR_COUNT++;
        } catch (SQLException e) {
            sqlErr(e);
        }
    }

    private static void teamsAreGivenTheRightLeagueId() {
        try (Statement s = DS_Main.connection.createStatement()) {
            String home = "home", away = "away";
            String query = "SELECT COUNT(*) FROM " + MatchTable.getTableName() +
                    " INNER JOIN " + TeamTable.getTableName() + " AS " + home + " ON " + MatchTable.getTableName() + "." + MatchTable.getColHometeamId() + " = " + home + "._id" +
                    " INNER JOIN " + TeamTable.getTableName() + " AS " + away + " ON " + MatchTable.getTableName() + "." + MatchTable.getColAwayteamId() + " = " + away + "._id" +
                    " WHERE " + home + "." + TeamTable.getColLeagueId() + " != " + away + "." + TeamTable.getColLeagueId();
            logger.info(query);
            ResultSet rs = s.executeQuery(query);
            while (rs.next()) {
                int count = rs.getInt(1);
                if (count == 0) logger.info("teamsAreGivenTheRightLeagueId PASS");
                else {
                    logger.error("teamsAreGivenTheRightLeagueId FAIL: " + count);
                    ERROR_COUNT++;
                }
            }
        } catch (SQLException e) {
            sqlErr(e);
        }
    }

    //TEST WILL FAIL IF NO DATA IN DB.
    //delete any matches that are abandoned and awarded 3-0 victories without playing. this data doesn't help us to predict results.
    //or add any data that the scraper has missed (sofascore may not have the data)
    private static void noGameMoreThan3DaysAgoWithoutStats() {
        try (Statement s = DS_Main.connection.createStatement()) {
            String home = "HOME";
            String away = "AWAY";
            String query = "SELECT COUNT(*) FROM " + MatchTable.getTableName() +
                    " INNER JOIN " + TeamTable.getTableName() + " AS " + home + " ON " + MatchTable.getTableName() + "." + MatchTable.getColHometeamId() + " = " + home + "._id" +
                    " INNER JOIN " + TeamTable.getTableName() + " AS " + away + " ON " + MatchTable.getTableName() + "." + MatchTable.getColAwayteamId() + " = " + away + "._id" +
                    " INNER JOIN " + LeagueTable.getTableName() + " ON " + home + "." + TeamTable.getColLeagueId() + " = " + LeagueTable.getTableName() + "._id" +
                    " WHERE " + MatchTable.getColDate() + " < '" + DateHelper.getSqlDate(DateHelper.subtractXDaysFromDate(new Date(), 3)) + "'" +
                    " AND " + MatchTable.getColIsPostponed() + " = 0 " +
                    " AND " + MatchTable.getColSeasonYearStart() + " != 19 " + //2019 had lots of cancelled games
                    " AND (" + MatchTable.getColHomeScore() + " = -1" +
                    " OR " + MatchTable.getColAwayScore() + " = -1" +
                    " OR " + MatchTable.getColHomeXg() + " = -1" +
                    " OR " + MatchTable.getColAwayXg() + " = -1" +
                    " OR " + MatchTable.getColHomeWinOdds() + " = -1" +
                    " OR " + MatchTable.getColDrawOdds() + " = -1" +
                    " OR " + MatchTable.getColAwayWinOdds() + " = -1" +
                    " OR (" + MatchTable.getColFirstScorer() + " = -1 AND (" + MatchTable.getColHomeScore() + " > 0 OR " + MatchTable.getColAwayScore() + " >0)))" +
                    " AND " + LeagueTable.getTableName() + "." + LeagueTable.getColName() + " != '" + LeagueIdsAndData.RUSSIA.name() + "'";

            logger.info(query);
            ResultSet rs = s.executeQuery(query);

            while (rs.next()) {
                int count = rs.getInt(1);
                if (count == 0) logger.info("noGameMoreThan3DaysAgoWithoutStats PASS");
                else {
                    logger.error("noGameMoreThan3DaysAgoWithoutStats FAIL: " + count);
                    ERROR_COUNT++;
                }
            }
        } catch (SQLException e) {
            sqlErr(e);
        }
    }

    private static void sqlErr(Throwable e) {
        logger.error("DB Connection error", e);
        ERROR_COUNT++;
    }
}
