package com.footballbettingcore.database.datasource;

import com.footballbettingcore.utils.DateHelper;
import com.footballbettingcore.database.datasource.dbTables.MatchTable;
import com.footballbettingcore.scrape.classes.*;

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
