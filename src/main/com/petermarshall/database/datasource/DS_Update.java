package com.petermarshall.database.datasource;

import com.petermarshall.DateHelper;
import com.petermarshall.database.datasource.dbTables.MatchTable;
import com.petermarshall.database.datasource.dbTables.PredictionTable;
import com.petermarshall.machineLearning.createData.classes.MatchToPredict;
import com.petermarshall.scrape.classes.*;

import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.stream.Collectors;

public class DS_Update {
    /*
     * Updates all stats of game to that in league. Scores, dates, sofascore id, player ratings.
     *
     * Note, if a game has scores but say does not have xG, and this method is called, any XG data in the db will be overwritten with -1.
     */
    public static void updateGamesInDB(League league, Season season, Date onlyUpdateGamesAfter) {
        try (Statement batchStatement = DS_Main.connection.createStatement()) {
            ArrayList<Match> matchesToUpdate = season.getAllMatches().stream()
                    .filter(m -> m.getKickoffTime().after(onlyUpdateGamesAfter) || m.getKickoffTime().equals(onlyUpdateGamesAfter))
                    .collect(Collectors.toCollection(ArrayList::new));
            addMatchesToBatch(batchStatement, league, season, matchesToUpdate);

            HashMap<String, Integer> teamIds = DS_Insert.getTeamIds(season.getAllTeams(), DS_Get.getLeagueId(league));
            season.getAllMatches().forEach(match ->
                    addUpdateKickoffTimeToBatch(batchStatement, teamIds, match, season.getSeasonYearStart())
            );

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
                            PredictionTable.getColAOdds() + " = " + bookieOdds[2] + ", " +
                            PredictionTable.getColBookieName() + " = '" + bookie + "' " +
                            " WHERE " + PredictionTable.getColMatchId() + " = " + mtp.getDatabase_id());
                }
            }
            batchStatement.executeBatch();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void updatePostponedMatches(League league, Season season, ArrayList<Match> postponedMatches) {
        try (Statement batchStatement = DS_Main.connection.createStatement()) {
            addMatchesToBatch(batchStatement, league, season, postponedMatches);
            batchStatement.executeBatch();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            e.printStackTrace();
        }
    }

    private static void addMatchesToBatch(Statement batchStatement, League league, Season season, ArrayList<Match> matches) {
        int leagueId = DS_Get.getLeagueId(league);
        int seasonYearStart = season.getSeasonYearStart();
        HashMap<String, Integer> teamIds = DS_Insert.getTeamIds(season.getAllTeams(), leagueId);
        matches.forEach(match -> {
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
                            MatchTable.getColSofascoreId() + " = " + match.getSofaScoreGameId() + ", " +
                            MatchTable.getColIsPostponed() + " = " + match.isPostponed() + ", " +
                            MatchTable.COL_HOME_POSESSION + " = " + match.getHomePossession() + ", " + MatchTable.COL_AWAY_POSESSION + " = " + match.getAwayPossession() + ", " +
                            MatchTable.COL_HOME_TOTAL_SHOTS + " = " + match.getHomeShots() + ", " + MatchTable.COL_AWAY_TOTAL_SHOTS + " = " + match.getAwayShots() + ", " +
                            MatchTable.COL_HOME_SHOTS_ON_TARGET + " = " + match.getHomeShotsOnTarget() + ", " + MatchTable.COL_AWAY_SHOTS_ON_TARGET + " = " + match.getAwayShotsOnTarget() +
                            " WHERE _id = " + matchId);
                    DS_Insert.addPlayerRatingsToBatch(batchStatement, match.getHomePlayerRatings(), matchId, homeTeamId);
                    DS_Insert.addPlayerRatingsToBatch(batchStatement, match.getAwayPlayerRatings(), matchId, awayTeamId);
                } else {
                    addUpdateKickoffTimeToBatch(batchStatement, teamIds, match, seasonYearStart);
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        });
    }

    private static void addUpdateKickoffTimeToBatch(Statement batchStatement, HashMap<String, Integer> teamIds,
                                                    Match match, int seasonYearStart) {
        int homeTeamId = teamIds.get(match.getHomeTeam().getTeamName());
        int awayTeamId = teamIds.get(match.getAwayTeam().getTeamName());
        try {
                    //just updating the kickoff time and postponed flag
                    batchStatement.addBatch("UPDATE " + MatchTable.getTableName() +
                            " SET " + MatchTable.getColDate() + " = '" + DateHelper.getSqlDate(match.getKickoffTime()) + "', " +
                            MatchTable.getColSofascoreId() + " = " + match.getSofaScoreGameId() + ", " +
                            MatchTable.getColIsPostponed() + " = " + match.isPostponed() +
                            " WHERE " + MatchTable.getColHometeamId() + " = " + homeTeamId +
                            " AND " + MatchTable.getColAwayteamId() + " = " + awayTeamId +
                            " AND " + MatchTable.getColSeasonYearStart() + " = " + seasonYearStart);
            } catch (SQLException e) {
                e.printStackTrace();
            }
    }
}
