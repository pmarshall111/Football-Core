package com.petermarshall.database.datasource;

import com.petermarshall.database.tables.*;
import com.petermarshall.machineLearning.createData.classes.Player;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class DS_Main {
    //used for calls to db to ensure consistent naming
    static final String HOMETEAM = "hometeam";
    static final String AWAYTEAM = "awayteam";

    private static final String CONNECTION_NAME = "jdbc:sqlite:C:\\Databases\\footballMatchesREFACTOR.db";
    static Connection connection;

    public static boolean isOpen() {
        try {
            return connection == null || !connection.isClosed();
        } catch(SQLException e) {
            return false;
        }
    }

    /*
     * To be called before every use of this class, followed by a call to closeConnection when all db
     * work has been completed.
     */
    public static boolean openConnection() {
        try {
            connection = DriverManager.getConnection(CONNECTION_NAME);
            return true;
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            return false;
        }
    }
    public static boolean closeConnection() {
        try {
            connection.close();
            return true;
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            return false;
        }
    }


    /*
     * To be called when first creating database and also before any inserts to the database. Creates all tables for database and
     * gets the next index of each table that can be added.
     * Returns true if database initialising was successful. Otherwise will return false.
     */
    public static void initDB() {
        try (Statement statement = connection.createStatement()) {

            statement.execute("CREATE TABLE IF NOT EXISTS '" + LeagueTable.getTableName() +
                    "' ('" + LeagueTable.getColName() + "' TEXT NOT NULL UNIQUE," + " '_id' INTEGER NOT NULL UNIQUE, PRIMARY KEY('_id') )");

            statement.execute("CREATE TABLE IF NOT EXISTS '" + TeamTable.getTableName() +
                    "' ('" + TeamTable.getColTeamName() + "' TEXT NOT NULL, '" + TeamTable.getColLeagueId() + "' INTEGER NOT NULL, "
                    + "'_id' INTEGER NOT NULL UNIQUE, PRIMARY KEY('_id'), " +
                    "FOREIGN KEY('" + TeamTable.getColLeagueId() + "') REFERENCES '" + LeagueTable.getTableName() + "'('_id'))");

            statement.execute("CREATE TABLE IF NOT EXISTS '" + MatchTable.getTableName() +
                    "' ('" + MatchTable.getColHomeScore() + "' INTEGER, '" + MatchTable.getColAwayScore() + "' INTEGER, '" +
                    MatchTable.getColHomeXg() + "' REAL, '" + MatchTable.getColAwayXg() + "' REAL, '" +
                    MatchTable.getColDate() + "' TEXT NOT NULL, '" + MatchTable.getColHomeWinOdds() + "' REAL, '" +
                    MatchTable.getColDrawOdds() + "' REAL, '" + MatchTable.getColAwayWinOdds() + "' REAL, '" +
                    MatchTable.getColFirstScorer() + "' INTEGER DEFAULT -1, '" + MatchTable.getColIsPostponed() + "' INTEGER DEFAULT 0, '" +
                    MatchTable.getColHometeamId() + "' INTEGER NOT NULL, '" + MatchTable.getColAwayteamId() + "' INTEGER NOT NULL, '" +
                    MatchTable.getColSeasonYearStart() + "' INTEGER NOT NULL, '_id' INTEGER NOT NULL UNIQUE,'" +
                    MatchTable.getColPredictedLive() + "' INTEGER, 'CHECK('" + MatchTable.getColPredictedLive() + "' == 0 OR '" + MatchTable.getColPredictedLive() + "' == 1), '" +
                    MatchTable.getColSofascoreId() + "' INTEGER, " +
                    "FOREIGN KEY('" + MatchTable.getColHometeamId() + "') REFERENCES '" + TeamTable.getTableName() + "'('_id'), " +
                    "FOREIGN KEY('" + MatchTable.getColAwayteamId() + "') REFERENCES '" + TeamTable.getTableName() + "'('_id'), " +
                    "PRIMARY KEY('_id'))");

            statement.execute("CREATE TABLE IF NOT EXISTS '" + PlayerRatingTable.getTableName() +
                    "' ('" + PlayerRatingTable.getColMins() + "' INTEGER NOT NULL, " + "CHECK ('" + PlayerRatingTable.getColMins() + "' <= 90 " +
                    " AND '" + PlayerRatingTable.getColMins() + "' > 0), '" +
                    PlayerRatingTable.getColRating() + "' REAL NOT NULL, " + "CHECK ('" + PlayerRatingTable.getColRating() + "' <= 10 " +
                    "AND '" + PlayerRatingTable.getColRating() + "' > 0), '" +
                    PlayerRatingTable.getColMatchId() + "' INTEGER NOT NULL, '" + PlayerRatingTable.getColTeamId() + "' INTEGER NOT NULL, '" +
                    PlayerRatingTable.getColPlayerName() + "' TEXT NOT NULL, " +
                    "FOREIGN KEY('" + PlayerRatingTable.getColTeamId() + "') REFERENCES '" + TeamTable.getTableName() + "'('_id'), " +
                    "FOREIGN KEY('" + PlayerRatingTable.getColMatchId() + "') REFERENCES '" + MatchTable.getTableName() + "'('_id'), " +
                    "UNIQUE('" + PlayerRatingTable.getColPlayerName() + "','" + PlayerRatingTable.getColMatchId() + "'))");

            statement.execute("CREATE TABLE IF NOT EXISTS '" + BetTable.getTableName() +
                    "' ('" + BetTable.getColResultBetOn() + "' INTEGER NOT NULL " +
                    "CHECK('" + BetTable.getColResultBetOn() + "' >= 1 AND '" + BetTable.getColResultBetOn() + "' <= 3), '" +
                    BetTable.getColOdds() + "' REAL NOT NULL, '" + BetTable.getColStake() + "' REAL NOT NULL, '" +
                    BetTable.getColMatchId() + "' INTEGER NOT NULL, '_id' INTEGER NOT NULL UNIQUE," +
                    "FOREIGN KEY('" + BetTable.getColMatchId() + "') REFERENCES '" + MatchTable.getTableName() + "'('_id'))");

            statement.execute("CREATE TABLE IF NOT EXISTS '" + LogTable.getTableName() +
                    "'( '" + LogTable.getColDatetime() + "' TEXT, '" + LogTable.getColInfo() + "' TEXT)");

        } catch (SQLException e) {
            System.out.println(e.getMessage());
            e.printStackTrace();
        }
    }
}
