package com.petermarshall.database.datasource;

import com.petermarshall.DateHelper;
import com.petermarshall.database.tables.*;
import com.petermarshall.logging.MatchLog;
import com.petermarshall.scrape.classes.*;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
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
            System.out.println(e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    /*
     * To be called after first scrape. Puts all data from a league into db. Cannot be called again once league is in database
     * as league table in sqlite has a unique name constraint. To add more data, we must either update existing records or add
     * a new season.
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

        } catch (SQLException e) {
            //TODO: INTRODUCE LOGGING HERE
            System.out.println(e.getMessage());
            System.out.println("ERROR writing league " + league.getName() + " to db");

            System.out.println("Error. Primary key now: " + LEAGUE_ID);
            e.printStackTrace();
        }
    }


    static int writeTeamToDb(Team t) {
        if (TEAM_ID == -1) {
            getNextIds();
        }
        try (Statement statement = DS_Main.connection.createStatement()) {
            statement.execute("INSERT IGNORE INTO " + TeamTable.getTableName() + " (" + TeamTable.getColTeamName() + ", " + TeamTable.getColLeagueId() + ", _id) " +
                    "VALUES ( '" + t.getTeamName() + "', " + LEAGUE_ID + ", " + ++TEAM_ID + " )");
            return TEAM_ID;
        }  catch (SQLException e) {
            //TODO: add log
            System.out.println(e);
            e.printStackTrace();
            return -99999;
        }
    }

    private static void writeMatchesToDb(ArrayList<Match> matches, HashMap<String, Integer> teamIds, int seasonYearStart) {
        //need to add to the batch both the player ratings and also the matches. need to add matches first as the player ratings need
        //the matchId
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
                          match.getHomeDrawAwayOdds().get(1) + ", " + match.getFirstScorer() + ", " + seasonYearStart + ", " + match.getSofaScoreGameId() + ", " + ++MATCH_ID + ")");

                  addPlayerRatingsToBatch(statement, match.getHomePlayerRatings(), MATCH_ID, homeTeamId);
                  addPlayerRatingsToBatch(statement, match.getAwayPlayerRatings(), MATCH_ID, awayTeamId);
              } catch (SQLException e) {
                  e.printStackTrace();
              }

          });
          statement.executeBatch();
        } catch (SQLException e) {
            //TODO: add log
            System.out.println(e);
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

    public static void logBetPlaced(MatchLog matchLog) {
        try (Statement statement = DS_Main.connection.createStatement()) {
            statement.execute("INSERT INTO " + BetTable.getTableName() +
                    " (" + BetTable.getColResultBetOn() + ", " + BetTable.getColOdds() + ", " + BetTable.getColStake() + ", " + BetTable.getColMatchId() + ") " +
                    "VALUES (" + matchLog.getResultBetOn().getSqlIntCode() + ", " + matchLog.getOddsBetOn() + ", " +
                    matchLog.getStake() + ", " + matchLog.getMatch().getDatabase_id() + ")");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    static HashMap<String, Integer> getTeamIds(HashMap<String, Team> teams, int leagueId) {
        HashMap<String, Integer> ids = new HashMap<>();
        teams.keySet().forEach(key -> {
            //getting team id
            int id = getTeamId(key, leagueId);
            if (id < 0) {
                //then we could not find a team of that name in db
                id = writeTeamToDb(teams.get(key));
            }
            ids.put(key, id);
        });
        return ids;
    }

}
