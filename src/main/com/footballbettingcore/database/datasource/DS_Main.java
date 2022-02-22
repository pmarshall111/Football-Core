package com.footballbettingcore.database.datasource;

import com.footballbettingcore.database.Result;
import com.footballbettingcore.database.datasource.dbTables.*;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class DS_Main {
    //used for calls to db to ensure consistent naming
    static final String HOMETEAM = "hometeam";
    static final String AWAYTEAM = "awayteam";
    static final String PLAYERS_TEAM = "playersteam";

    private static final String CONNECTION_NAME = "jdbc:mariadb://" + Secrets.DB_URL + ":3306/" + Secrets.DB_NAME + "?serverTimezone=UTC";
    public static Connection connection;

    public static boolean isOpen() {
        try {
            return connection != null && !connection.isClosed();
        } catch (SQLException e) {
            return false;
        }
    }

    /*
     * To be called before every use of this class, followed by a call to closeConnection when all db
     * work has been completed.
     */
    public static boolean openProductionConnection() {
        if (isOpen()) {
            return true;
        }
        try {
            connection = DriverManager.getConnection(CONNECTION_NAME, Secrets.DB_USER, Secrets.DB_PASS);
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static boolean openTestConnection(String jdbcUrl, String username, String password) {
        try {
            connection = DriverManager.getConnection(jdbcUrl, username, password);
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

            statement.execute("CREATE TABLE IF NOT EXISTS " + LeagueTable.getTableName() +
                    " (" + LeagueTable.getColName() + " text NOT NULL," + " _id int NOT NULL, PRIMARY KEY(_id), " +
                    "UNIQUE KEY unique_id (_id)," +
                    "UNIQUE KEY unique_league (" + LeagueTable.getColName() + "(50)))");

            statement.execute("CREATE TABLE IF NOT EXISTS " + TeamTable.getTableName() + "" +
                    " (" + TeamTable.getColTeamName() + " text NOT NULL, " + TeamTable.getColLeagueId() + " int NOT NULL, "
                    + "_id int NOT NULL, " +
                    " PRIMARY KEY(_id), " +
                    " UNIQUE KEY unique_team (" + TeamTable.getColTeamName() + "(50), " + TeamTable.getColLeagueId() + ")," +
                    " KEY league_id_idx (" + TeamTable.getColLeagueId() + "), " +
                    " CONSTRAINT league_id FOREIGN KEY (" + TeamTable.getColLeagueId() + ") REFERENCES " + LeagueTable.getTableName() + " (_id))");


            statement.execute("CREATE TABLE IF NOT EXISTS " + MatchTable.getTableName() + " (" +
                    MatchTable.getColHomeScore() + " int DEFAULT NULL, " + MatchTable.getColAwayScore() + " int DEFAULT NULL, " +
                    MatchTable.getColHomeXg() + " double DEFAULT NULL, " + MatchTable.getColAwayXg() + " double DEFAULT NULL, " +
                    MatchTable.getColDate() + " text NOT NULL, " + MatchTable.getColHomeWinOdds() + " double DEFAULT NULL, " +
                    MatchTable.getColDrawOdds() + " double DEFAULT NULL, " + MatchTable.getColAwayWinOdds() + " double DEFAULT NULL, " +
                    MatchTable.getColFirstScorer() + " int DEFAULT -1, " + MatchTable.getColIsPostponed() + " int DEFAULT 0, " +
                    MatchTable.getColHometeamId() + " int NOT NULL, " + MatchTable.getColAwayteamId() + " int NOT NULL, " +
                    MatchTable.getColSeasonYearStart() + " int NOT NULL, _id int NOT NULL, " +
                    MatchTable.getColPredictedLive() + " int DEFAULT NULL, " +
                    MatchTable.getColSofascoreId() + " int DEFAULT NULL, " +
                    MatchTable.COL_HOME_POSESSION + " double DEFAULT NULL, " +
                    MatchTable.COL_AWAY_POSESSION + " double DEFAULT NULL, " +
                    MatchTable.COL_HOME_TOTAL_SHOTS + " int DEFAULT NULL, " +
                    MatchTable.COL_AWAY_TOTAL_SHOTS + " int DEFAULT NULL, " +
                    MatchTable.COL_HOME_SHOTS_ON_TARGET + " int DEFAULT NULL, " +
                    MatchTable.COL_AWAY_SHOTS_ON_TARGET + " int DEFAULT NULL, " +
                    "PRIMARY KEY(_id), " +
                    "UNIQUE (" + MatchTable.getColHometeamId() + "," + MatchTable.getColAwayteamId() + "," + MatchTable.getColSeasonYearStart() + "), " +
                    "FOREIGN KEY (" + MatchTable.getColAwayteamId() + ") REFERENCES " + TeamTable.getTableName() + "(_id), " +
                    "FOREIGN KEY (" + MatchTable.getColHometeamId() + ") REFERENCES " + TeamTable.getTableName() + "(_id))"
            );

            statement.execute("CREATE TABLE IF NOT EXISTS " + PlayerRatingTable.getTableName() + " (" +
                    PlayerRatingTable.getColMins() + " int NOT NULL, " +
                    PlayerRatingTable.getColRating() + " double NOT NULL, " +
                    PlayerRatingTable.getColMatchId() + " int NOT NULL, " +
                    PlayerRatingTable.getColTeamId() + " int NOT NULL, " +
                    PlayerRatingTable.getColPlayerName() + " text NOT NULL, " +
                    PlayerRatingTable.getColPosition() + " text, " +
                    "UNIQUE KEY unique_player_rating (" + PlayerRatingTable.getColMatchId() + "," + PlayerRatingTable.getColPlayerName() + "(50)," + PlayerRatingTable.getColTeamId() + ")," +
                    "KEY match_id_idx (" + PlayerRatingTable.getColMatchId() + "), " +
                    "KEY team_id_idx (" + PlayerRatingTable.getColTeamId() + "), " +
                    "CONSTRAINT match_id_played_in FOREIGN KEY (" + PlayerRatingTable.getColMatchId() + ") REFERENCES " + MatchTable.getTableName() + " (_id), " +
                    "CONSTRAINT team_id FOREIGN KEY (" + PlayerRatingTable.getColTeamId() + ") REFERENCES " + TeamTable.getTableName() + " (_id), " +
                    "CONSTRAINT mins_in_range CHECK (((" + PlayerRatingTable.getColMins() + " <= 90) and (" + PlayerRatingTable.getColMins() + " >= 0))), " +
                    "CONSTRAINT rating_in_range CHECK (((" + PlayerRatingTable.getColRating() + " <= 10) and (" + PlayerRatingTable.getColRating() + " >= 0))))");


            statement.execute("CREATE TABLE IF NOT EXISTS " + BetTable.getTableName() + " (" +
                    BetTable.getColResultBetOn() + " int NOT NULL, " +
                    BetTable.getColOdds() + " double NOT NULL, " + BetTable.getColStake() + " double NOT NULL, " +
                    BetTable.getColMatchId() + " int NOT NULL UNIQUE, " + BetTable.getColBetPlacedWith() + " text, " +
                    BetTable.getColIsLayBet() + " boolean DEFAULT false, " +
                    " UNIQUE KEY one_type_of_bet_for_each_game (" + BetTable.getColMatchId() + "," + BetTable.getColIsLayBet() + "), " +
                    " CONSTRAINT match_id FOREIGN KEY (" + BetTable.getColMatchId() + ") REFERENCES " + MatchTable.getTableName() + "(_id), " +
                    "CONSTRAINT result_in_range CHECK (((" + BetTable.getColResultBetOn() + " >= " + Result.HOME_WIN.getSqlIntCode() +
                        ") and (" + BetTable.getColResultBetOn() + " <= " + Result.AWAY_WIN.getSqlIntCode() + ")))" +
                    ")");

            statement.execute("CREATE TABLE IF NOT EXISTS " + PredictionTable.getTableName() + " (" +
                    PredictionTable.getColDate() + " text NOT NULL, " + PredictionTable.getColWithLineups() + " tinyint(1) NOT NULL, " +
                    PredictionTable.getColHomePred() + " double NOT NULL, " + PredictionTable.getColDrawPred() + " double NOT NULL, " +
                    PredictionTable.getColAwayPred() + " double NOT NULL, " + PredictionTable.getColBookieName() + " text, " +
                    PredictionTable.getColHOdds() + " double DEFAULT -1, " + PredictionTable.getColDOdds() + " double DEFAULT -1, " +
                    PredictionTable.getColAOdds() + " double DEFAULT -1, " + PredictionTable.getColMatchId() + " int NOT NULL, " +
                    " KEY match_id_idx (" + PredictionTable.getColMatchId() + "), " +
                    " CONSTRAINT predict_with_without_lineups_once UNIQUE (" + PredictionTable.getColWithLineups()  +"," + PredictionTable.getColMatchId() + "), " +
                    " CONSTRAINT match_id_f_key FOREIGN KEY (" + PredictionTable.getColMatchId() + ") REFERENCES " + MatchTable.getTableName() + "(_id))");

        } catch (SQLException e) {
            System.out.println(e.getMessage());
            e.printStackTrace();
        }
    }
}
