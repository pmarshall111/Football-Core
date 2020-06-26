package com.petermarshall.database.datasource;

import com.petermarshall.DateHelper;
import com.petermarshall.database.dbTables.MatchTable;
import com.petermarshall.database.dbTables.PredictionTable;
import com.petermarshall.machineLearning.createData.classes.MatchToPredict;
import com.petermarshall.scrape.classes.*;

import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

public class DS_Update {
    /*
     * Updates all stats of game to that in league. Scores, dates, sofascore id, player ratings.
     *
     * Note, if a game has scores but say does not have xG, and this method is called, any XG data in the db will be overwritten with -1.
     */
    public static void updateGamesInDB(League league, Season season, Date onlyUpdateGamesAfter) {
        try (Statement batchStatement = DS_Main.connection.createStatement()) {
            int leagueId = DS_Get.getLeagueId(league);
            int seasonYearStart = season.getSeasonYearStart();
            HashMap<String, Integer> teamIds = DS_Insert.getTeamIds(season.getAllTeams(), leagueId);
            season.getAllMatches().forEach(match -> {
                if (match.getKickoffTime().after(onlyUpdateGamesAfter)) {
                    int homeTeamId = teamIds.get(match.getHomeTeam().getTeamName());
                    int awayTeamId = teamIds.get(match.getAwayTeam().getTeamName());
                    try {
                        if (match.getHomeScore() > -1) {
                            //updating games with full stats and player ratings
                            int matchId = DS_Get.getMatchId(homeTeamId, awayTeamId, seasonYearStart);
                            batchStatement.addBatch("UPDATE " + MatchTable.getTableName() +
                                    " SET " + MatchTable.getColHomeXg() + " = " + match.getHomeXGF() + ", " + MatchTable.getColAwayXg() + " = " + match.getAwayXGF() + ", " +
                                    MatchTable.getColHomeScore() + " = " + match.getHomeScore() + ", " + MatchTable.getColAwayScore() + " = " + match.getAwayScore() + ", " +
                                    MatchTable.getColHomeWinOdds() + " = " + match.getHomeOdds() + ", " + MatchTable.getColDrawOdds() + " = " + match.getDrawOdds() + ", " +
                                    MatchTable.getColAwayWinOdds() + " = " + match.getAwayOdds() + ", " + MatchTable.getColFirstScorer() + " = " + match.getFirstScorer().getSqlIntCode() + ", " +
                                    MatchTable.getColDate() + " = '" + DateHelper.getSqlDate(match.getKickoffTime()) + "', " +
                                    MatchTable.getColSofascoreId() + " = " + match.getSofaScoreGameId() +
                                    " WHERE _id = " + matchId);
                            DS_Insert.addPlayerRatingsToBatch(batchStatement, match.getHomePlayerRatings(), matchId, homeTeamId);
                            DS_Insert.addPlayerRatingsToBatch(batchStatement, match.getAwayPlayerRatings(), matchId, awayTeamId);
                        } else {
                            //just updating the kickoff time
                            batchStatement.addBatch("UPDATE " + MatchTable.getTableName() +
                                    " SET " + MatchTable.getColDate() + " = '" + DateHelper.getSqlDate(match.getKickoffTime()) + "', " +
                                    MatchTable.getColSofascoreId() + " = " + match.getSofaScoreGameId() +
                                    " WHERE " + MatchTable.getColHometeamId() + " = " + homeTeamId +
                                    " AND " + MatchTable.getColAwayteamId() + " = " + awayTeamId +
                                    " AND " + MatchTable.getColSeasonYearStart() + " = " + seasonYearStart);
                        }
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                }
            });
            batchStatement.executeBatch();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            e.printStackTrace();
        }
    }

    /*
     * Needed because understat (who we created our dates from) sometimes have incorrect start dates and times.
     * Will also be used to change postponed matches in the database.
     */
    public static void updateKickoffTime(int seasonYearStart, String homeTeamName, String awayTeamName, String startDate, int leagueId) {
        try (Statement statement = DS_Main.connection.createStatement()) {
            int homeTeamId = DS_Get.getTeamId(homeTeamName, leagueId);
            int awayTeamId = DS_Get.getTeamId(awayTeamName, leagueId);
            statement.execute("UPDATE " + MatchTable.getTableName() +
                    " SET " + MatchTable.getColDate() + " = '" + startDate +
                    "' WHERE " + MatchTable.getColHometeamId() + " = " + homeTeamId +
                    " AND " + MatchTable.getColAwayteamId() + " = " + awayTeamId +
                    " AND " + MatchTable.getColSeasonYearStart() + " = " + seasonYearStart);
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void updatePredictionToIncludeOdds(ArrayList<MatchToPredict> matches) {
        try (Statement batchStatement = DS_Main.connection.createStatement()) {
            for (MatchToPredict mtp : matches) {
                boolean hasBookieOdds = mtp.getBookiesOdds() != null && mtp.getBookiesOdds().keySet().size() > 0;
                if (hasBookieOdds) {
                    String bookie = mtp.getBookiesOdds().keySet().iterator().next();
                    double[] bookieOdds = mtp.getBookiesOdds().get(bookie);
                    batchStatement.addBatch("UPDATE " + PredictionTable.getTableName() +
                            " SET " + PredictionTable.getColHOdds() + " = " + bookieOdds[0] + ", " +
                            PredictionTable.getColDOdds() + " = " + bookieOdds[1] + ", " +
                            PredictionTable.getColAOdds() + " = " + bookieOdds[2] +
                            " WHERE " + PredictionTable.getColMatchId() + " = " + mtp.getDatabase_id());
                }
            }
            batchStatement.executeBatch();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
