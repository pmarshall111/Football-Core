package com.petermarshall.database.datasource;

import com.petermarshall.DateHelper;
import com.petermarshall.database.dbTables.*;
import com.petermarshall.database.BetLog;
import com.petermarshall.machineLearning.createData.classes.MatchToPredict;
import com.petermarshall.scrape.classes.*;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

import static com.petermarshall.database.datasource.DS_Get.getTeamId;

public class DS_Insert {
    private static int LEAGUE_ID = -1;
    private static int TEAM_ID = -1;
    private static int MATCH_ID = -1;

    /*
     * Retrieves the highest current Id within the current table and sets the class variable to it. Called from the initDb method.
     *
     * Requires separate statements as once you execute a new query on the same statement, the old resultSet is also closed.
     */
    public static boolean getNextIds() {
        try (Statement statement1 = DS_Main.connection.createStatement();
             Statement statement2 = DS_Main.connection.createStatement();
             Statement statement3 = DS_Main.connection.createStatement();

             ResultSet LeagueSet = statement1.executeQuery("SELECT max(_id) FROM " + LeagueTable.getTableName());
             ResultSet TeamSet = statement2.executeQuery("SELECT max(_id) FROM " + TeamTable.getTableName());
             ResultSet MatchSet = statement3.executeQuery("SELECT max(_id) FROM " + MatchTable.getTableName());
        ) {
            while (LeagueSet.next()) {
                LEAGUE_ID = LeagueSet.getInt(1);
            }
            while (TeamSet.next()) {
                TEAM_ID = TeamSet.getInt(1);
            }
            while (MatchSet.next()) {
                MATCH_ID = MatchSet.getInt(1);
            }
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    /*
     * To be called after first scrape. Puts all data from a league into db. Can also be used when adding new seasons.
     */
    public static void writeLeagueToDb(League league) {
        if (DS_Main.connection == null) {
            DS_Main.openProductionConnection();
        }
        if (LEAGUE_ID == -1) {
            getNextIds();
        }
        try (Statement statement = DS_Main.connection.createStatement()) {
            //checking that league is not already in the database
            ResultSet rs = statement.executeQuery("SELECT _id FROM " + LeagueTable.getTableName() +
                                            " WHERE " + LeagueTable.getColName() + " = '" + league.getName() + "'");
            boolean leagueInDb = false;
            while (rs.next()) {
                LEAGUE_ID = rs.getInt(1);
                leagueInDb = true;
            }
            if (!leagueInDb) {
                statement.execute("INSERT IGNORE INTO " + LeagueTable.getTableName() + " (" + LeagueTable.getColName() + ", _id) " +
                        "VALUES ( '" + league.getName() + "', " + ++LEAGUE_ID + " )");
            }

            //need to get league id from DB if we're scraping in a new season and the league is already in the database. Otherwise will insert
            //with a leagueId that doesn't correspond to a league in the database. Insert operation will fail.
            int leagueId = DS_Get.getLeagueId(league);
            ArrayList<Season> allSeasons = league.getAllSeasons();
            allSeasons.forEach(season -> {
                HashMap<String, Integer> teamIds = getTeamIds(season.getAllTeams(), leagueId);
                writeMatchesToDb(season.getAllMatches(), teamIds, season.getSeasonYearStart());
            });

            //resetting LEAGUE_ID in case an existing league is used with this method, followed by a new league. new league needs fresh id
            getNextIds();
        } catch (SQLException e) {
            System.out.println("ERROR writing league " + league.getName() + " to db");
            System.out.println("Error. Primary key now: " + LEAGUE_ID);
            e.printStackTrace();
        }
    }

    /*
     * Located in DS_Insert rather than DS_Get because method will cause unknown teams to be inserted into db
     */
    static HashMap<String, Integer> getTeamIds(HashMap<String, Team> teams, int leagueId) {
        HashMap<String, Integer> ids = new HashMap<>();
        teams.keySet().forEach(key -> {
            int id = getTeamId(key, leagueId);
            if (id < 0) {
                //then we could not find a team of that name in db
                id = writeTeamToDb(teams.get(key));
            }
            ids.put(key, id);
        });
        return ids;
    }

    private static int writeTeamToDb(Team t) {
        if (TEAM_ID == -1) {
            getNextIds();
        }
        try (Statement statement = DS_Main.connection.createStatement()) {
            statement.execute("INSERT IGNORE INTO " + TeamTable.getTableName() + " (" + TeamTable.getColTeamName() + ", " + TeamTable.getColLeagueId() + ", _id) " +
                    "VALUES ( '" + t.getTeamName() + "', " + LEAGUE_ID + ", " + ++TEAM_ID + " )");
            return TEAM_ID;
        }  catch (SQLException e) {
            e.printStackTrace();
            return -99999;
        }
    }

    private static void writeMatchesToDb(ArrayList<Match> matches, HashMap<String, Integer> teamIds, int seasonYearStart) {
        if (MATCH_ID == -1) {
            getNextIds();
        }
        try (Statement statement = DS_Main.connection.createStatement()) {
          matches.forEach(match -> {
              int homeTeamId = teamIds.get(match.getHomeTeam().getTeamName());
              int awayTeamId = teamIds.get(match.getAwayTeam().getTeamName());
              try {
                  statement.addBatch("INSERT IGNORE INTO " + MatchTable.getTableName() + " (" + MatchTable.getColDate() + ", " +
                          MatchTable.getColHometeamId() + ", " + MatchTable.getColAwayteamId() + ", " + MatchTable.getColHomeXg() + ", " + MatchTable.getColAwayXg() + ", " +
                          MatchTable.getColHomeScore() + ", " + MatchTable.getColAwayScore() + ", " + MatchTable.getColHomeWinOdds() + ", " + MatchTable.getColAwayWinOdds() + ", " +
                          MatchTable.getColDrawOdds() + ", " + MatchTable.getColFirstScorer() + ", " + MatchTable.getColSeasonYearStart() + ", " +
                          MatchTable.getColSofascoreId() + ", _id) " +
                          "VALUES ( '" + DateHelper.getSqlDate(match.getKickoffTime()) + "', " + homeTeamId + ", " + awayTeamId + ", " + match.getHomeXGF() + ", " + match.getAwayXGF() + ", " +
                          match.getHomeScore() + ", " + match.getAwayScore() + ", " + match.getHomeDrawAwayOdds().get(0) + ", " + match.getHomeDrawAwayOdds().get(2) + ", " +
                          match.getHomeDrawAwayOdds().get(1) + ", " + match.getFirstScorer().getSqlIntCode() + ", " + seasonYearStart + ", " + match.getSofaScoreGameId() + ", " + ++MATCH_ID + ")");

                  addPlayerRatingsToBatch(statement, match.getHomePlayerRatings(), MATCH_ID, homeTeamId);
                  addPlayerRatingsToBatch(statement, match.getAwayPlayerRatings(), MATCH_ID, awayTeamId);
              } catch (SQLException e) {
                  e.printStackTrace();
              }

          });
          statement.executeBatch();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void addPlayerRatingsToBatch(Statement batchStmt, HashMap<String, PlayerRating> pRatings, int matchId, int teamId) {
        pRatings.forEach((name, rating) -> {
            try {
                batchStmt.addBatch("INSERT IGNORE INTO " + PlayerRatingTable.getTableName() +
                        " (" + PlayerRatingTable.getColPlayerName() + ", " + PlayerRatingTable.getColRating() + ", " + PlayerRatingTable.getColMins() + ", " +
                        PlayerRatingTable.getColMatchId() + ", " + PlayerRatingTable.getColTeamId() + ") " +
                        "VALUES ('" + rating.getName() + "', " + rating.getRating() + ", " + rating.getMinutesPlayed() + ", " +
                        matchId + ", " + teamId + ")");
            } catch (SQLException e) {
                e.printStackTrace();
            }
        });
    }

    public static void logBetPlaced(BetLog betLog) {
        try (Statement statement = DS_Main.connection.createStatement()) {
            statement.execute("INSERT INTO " + BetTable.getTableName() +
                    " (" + BetTable.getColResultBetOn() + ", " + BetTable.getColOdds() + ", " + BetTable.getColStake() + ", " +
                    BetTable.getColMatchId() + ", " + BetTable.getColBetPlacedWith() + ") " +
                    "VALUES (" + betLog.getResultBetOn().getSqlIntCode() + ", " + betLog.getOddsBetOn() + ", " +
                    betLog.getStake() + ", " + betLog.getMatch().getDatabase_id() + ", '" + betLog.getBookieUsed() + "')");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void addPredictionsToDb(ArrayList<MatchToPredict> mtps) {
        try (Statement statement = DS_Main.connection.createStatement()) {
            for (MatchToPredict mtp: mtps) {
                if (mtp.hasAnyPredictions()) {
                    boolean hasLineupPredictions = mtp.hasPredictionsWithLineups();
                    double[] predictions = mtp.getOurPredictions(hasLineupPredictions);
                    boolean hasBookieOdds = mtp.getBookiesOdds() != null && mtp.getBookiesOdds().keySet().size() > 0;
                    String bookieInsertionStr = "null, -1, -1, -1";
                    if (hasBookieOdds) {
                        String bookie = mtp.getBookiesOdds().keySet().iterator().next();
                        double[] bookieOdds = mtp.getBookiesOdds().get(bookie);
                        bookieInsertionStr = "'" + bookie + "', " + bookieOdds[0] + ", " + bookieOdds[1] + ", " + bookieOdds[2];
                    }
                    statement.addBatch("INSERT INTO " + PredictionTable.getTableName() +
                            " (" + PredictionTable.getColDate() + ", " + PredictionTable.getColWithLineups() + ", " +
                            PredictionTable.getColHomePred() + ", " + PredictionTable.getColDrawPred() + ", " + PredictionTable.getColAwayPred() + ", " +
                            PredictionTable.getColBookieName() + ", " + PredictionTable.getColHOdds() + ", " + PredictionTable.getColDOdds() + ", " +
                            PredictionTable.getColAOdds() + ", " + PredictionTable.getColMatchId() + ") " +
                            " VALUES ('" + DateHelper.getSqlDate(new Date()) + "', " + hasLineupPredictions + ", " + predictions[0] + ", " +
                            predictions[1] + ", " + predictions[2] + ", " + bookieInsertionStr + ", " + mtp.getDatabase_id() + ")");
                }
            }
            statement.executeBatch();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
