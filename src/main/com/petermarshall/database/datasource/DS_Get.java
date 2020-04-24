package com.petermarshall.database.datasource;

import com.petermarshall.DateHelper;
import com.petermarshall.database.BetResult;
import com.petermarshall.database.BetResultsTotalled;
import com.petermarshall.database.WhenGameWasPredicted;
import com.petermarshall.database.tables.*;
import com.petermarshall.machineLearning.createData.classes.MatchToPredict;
import com.petermarshall.scrape.classes.League;
import com.petermarshall.scrape.classes.LeagueSeasonIds;
import com.petermarshall.scrape.classes.Match;
import com.petermarshall.scrape.classes.Team;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;

import static com.petermarshall.database.datasource.DS_Main.AWAYTEAM;
import static com.petermarshall.database.datasource.DS_Main.HOMETEAM;

public class DS_Get {
    static int getMatchId(int homeTeamId, int awayTeamId, int seasonYearStart) {
        try (Statement statement = DS_Main.connection.createStatement()) {

            ResultSet rs = statement.executeQuery("SELECT _id FROM '" + MatchTable.getTableName() +
                    "' WHERE '" + MatchTable.getColHometeamId() + "' = " + homeTeamId +
                    " AND '" + MatchTable.getColAwayteamId() + "' = " + awayTeamId +
                    " AND '" + MatchTable.getColSeasonYearStart() + "' = " + seasonYearStart);

            return rs.getInt(1);
        } catch (SQLException e) {
            System.out.println(e);
            return -9999;
        }
    }

    public static int getLeagueId(String name) {
        try (Statement statement = DS_Main.connection.createStatement()) {

            ResultSet rs = statement.executeQuery("SELECT _id FROM '" + LeagueTable.getTableName() +
                    "' WHERE '" + LeagueTable.getColName() + "' = '" + name + "'");

            return rs.getInt(1);
        } catch (SQLException e) {
            System.out.println(e);
            return -9999;
        }
    }
    public static int getLeagueId(League league) {
        return getLeagueId(league.getName());
    }

    static int getTeamId(String teamName, int leagueId) {
        try (Statement statement = DS_Main.connection.createStatement();
             ResultSet teamQuery = statement.executeQuery("SELECT _id FROM '" + TeamTable.getTableName() +
                     "WHERE " + TeamTable.getColTeamName() + " = '" + teamName + "') " +
                     "AND " + LeagueTable.getTableName() + "._id = " + leagueId)) {
            if (teamQuery.next()) {
                return teamQuery.getInt(1);
            } else {
                return -9999;
            }
        } catch (SQLException e) {
            System.out.println(e);
            return -9999;
        }
    }

    static HashMap<String, Integer> getTeamIds(HashMap<String, Team> teams, int leagueId) {
        HashMap<String, Integer> ids = new HashMap<>();
        teams.keySet().forEach(key -> {
            //getting team id
            int id = getTeamId(key, leagueId);
            if (id < 0) {
                //then we could not find a team of that name in db
                id = DS_Insert.writeTeamToDb(teams.get(key));
            }
            ids.put(key, id);
        });
        return ids;
    }

    public static String getMostRecentMatchInLeague(League league) {
        try (Statement statement = DS_Main.connection.createStatement()) {
            ResultSet dateStringRS = statement.executeQuery("SELECT " + MatchTable.getTableName() + "." + MatchTable.getColDate() + " FROM " + MatchTable.getTableName() +
                    " INNER JOIN " + TeamTable.getTableName() + " ON " + MatchTable.getTableName() + "." + MatchTable.getColHometeamId() + " = " + TeamTable.getTableName() + "._id" +
                    " INNER JOIN " + LeagueTable.getTableName() + " ON " + TeamTable.getTableName() + "." + TeamTable.getColLeagueId() + " = " + LeagueTable.getTableName() + "._id" +
                    " WHERE " + LeagueTable.getTableName() + "." + LeagueTable.getColName() + " = '" + league.getName() + "'" +
                    " AND " + MatchTable.getTableName() + "." + MatchTable.getColHomeScore() + " > " + "-1" +
                    " ORDER BY " + MatchTable.getTableName() + "." + MatchTable.getColDate() + " DESC" +
                    " LIMIT " + "1");

            String dateString = dateStringRS.getString(1);
            return dateString;
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            e.printStackTrace();
            return null;
        }
    }

    //To be called when we want to predict on games that will have lineups. So we only want to get out the games that are 1 hour before kickoff as those will have lineups.
    public static ArrayList<MatchToPredict> getMatchesToPredict(java.util.Date earliestKickoff, java.util.Date latestKickoff) {
        ArrayList<MatchToPredict> matches = new ArrayList<>();

        try (Statement statement = DS_Main.connection.createStatement()) {
            String earliestDate = DateHelper.getSqlDate(earliestKickoff);
            String latestDate = DateHelper.getSqlDate(latestKickoff);

            ResultSet resultSet = statement.executeQuery("SELECT " + HOMETEAM + "." + TeamTable.getColTeamName() + ", " + AWAYTEAM + "." + TeamTable.getColTeamName() +
                    ", " + MatchTable.getTableName() + "." + MatchTable.getColSeasonYearStart() + ", " + LeagueTable.getTableName() + "." + LeagueTable.getColName() + ", " +
                    MatchTable.getTableName() + "." + MatchTable.getColDate() + ", " + MatchTable.getTableName() + "._id" + MatchTable.getTableName() + "." + MatchTable.getColSofascoreId() +
                    " FROM " + MatchTable.getTableName() +
                    " INNER JOIN " + TeamTable.getTableName() + " AS " + HOMETEAM + " ON " + MatchTable.getTableName() + "." + MatchTable.getColHometeamId() + " = " + HOMETEAM + "._id" +
                    " INNER JOIN " + TeamTable.getTableName() + " AS " + AWAYTEAM + " ON " + MatchTable.getTableName() + "." + MatchTable.getColAwayteamId() + " = " + AWAYTEAM + "._id" +
                    " INNER JOIN " + LeagueTable.getTableName() + " ON " + TeamTable.getTableName() + "." + TeamTable.getColLeagueId() + " = " + LeagueTable.getTableName() + "._id" +
                    " WHERE " + MatchTable.getTableName() + "." + MatchTable.getColDate() + " >= '" + earliestDate + "'" +
                    " AND " + MatchTable.getTableName() + "." + MatchTable.getColDate() + " <= '" + latestDate + "'");

            while (resultSet.next()) {
                String homeTeam = resultSet.getString(1);
                String awayTeam = resultSet.getString(2);
                String seasonYearStart = resultSet.getString(3);
                String leagueName = resultSet.getString(4);
                String kickOffTime = resultSet.getString(5);
                int database_id = resultSet.getInt(6);
                int sofascore_id = resultSet.getInt(7);

                MatchToPredict match = new MatchToPredict(homeTeam, awayTeam, seasonYearStart, leagueName, kickOffTime, database_id, sofascore_id);
                matches.add(match);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return matches;
    }

    /*
     * Gets the matches that have not been predicted on where there is not another completed match with either team after it.
     * To be called when we come to input our results from the games and find that we didn't predict on some.
     */
    public static ArrayList<MatchToPredict> getMatchesToPredict() {
//        SELECT h.team_name, a.team_name, season.season_years, league.name, match.date AS matchdate, _id FROM match
//        INNER JOIN team AS h ON match.hometeam_id = h._id
//        INNER JOIN team AS a ON match.awayteam_id = a._id
//        INNER JOIN league ON team.league_id = league._id
//        WHERE match.seasonYearStart == currentSeason;
//        AND (
//                SELECT COUNT(*) FROM Matches
//                WHERE date > outerMatch.date
//                AND (homeTeamId == awayteam.id
//                OR homeTeamId == hometeam.id)
//                OR  (awayTeamId == hometeam.id
//                OR awayTeamId == awayteam.id)
//                        AND homeScore > -1
//                ) == 0

        ArrayList<MatchToPredict> matches = new ArrayList<>();

        try (Statement statement = DS_Main.connection.createStatement()) {
            String MATCHDATE = "matchdate";
            String HOME_ID = "homeid";
            String AWAY_ID = "awayid";
            int currentSeasonYearStart = DateHelper.getStartYearForCurrentSeason();

            ResultSet resultSet = statement.executeQuery("SELECT " + HOMETEAM + "." + TeamTable.getColTeamName() + ", " + AWAYTEAM + "." + TeamTable.getColTeamName() +
                    ", " + MatchTable.getTableName() + "." + MatchTable.getColSeasonYearStart() + ", " + LeagueTable.getTableName() + "." + LeagueTable.getColName() + ", " +
                    MatchTable.getTableName() + "." + MatchTable.getColDate() + " AS '" + MATCHDATE + "', " + MatchTable.getTableName() + "._id" + MatchTable.getTableName() + "." + MatchTable.getColSofascoreId() + ", " +
                    MatchTable.getTableName() + "." + MatchTable.getColHometeamId() + " AS " + HOME_ID + ", " + MatchTable.getTableName() + "." + MatchTable.getColAwayteamId() + " AS "  + AWAY_ID +
                    " FROM " + MatchTable.getTableName() +
                    " INNER JOIN " + TeamTable.getTableName() + " AS " + HOMETEAM + " ON " + MatchTable.getTableName() + "." + MatchTable.getColHometeamId() + " = " + HOMETEAM + "._id" +
                    " INNER JOIN " + TeamTable.getTableName() + " AS " + AWAYTEAM + " ON " + MatchTable.getTableName() + "." + MatchTable.getColAwayteamId() + " = " + AWAYTEAM + "._id" +
                    " INNER JOIN " + LeagueTable.getTableName() + " ON " + HOMETEAM + "." + TeamTable.getColLeagueId() + " = " + LeagueTable.getTableName() + "._id" +
                    " WHERE " + MatchTable.getTableName() + "." + MatchTable.getColSeasonYearStart() + " = " + currentSeasonYearStart +
                    " AND (" +
                    " SELECT COUNT(*) FROM " + MatchTable.getTableName() +
                    " WHERE " + MatchTable.getColDate() + " > " + MATCHDATE +
                    " AND " + MatchTable.getColHomeScore() + " > -1 " +
                    " AND ((" + MatchTable.getColHometeamId() + " == " + HOME_ID + " OR " + MatchTable.getColAwayteamId() + " == " + HOME_ID + ")" +
                    " OR (" + MatchTable.getColAwayteamId() + " == " + AWAY_ID + " OR " + MatchTable.getColAwayteamId() + " == " + AWAY_ID + "))" +
                    ") == 0");


            while (resultSet.next()) {
                String homeTeam = resultSet.getString(1);
                String awayTeam = resultSet.getString(2);
                String seasonYearStart = resultSet.getString(3);
                String leagueName = resultSet.getString(4);
                String kickOffTime = resultSet.getString(5);
                int database_id = resultSet.getInt(6);
                int sofascore_id = resultSet.getInt(7);

                MatchToPredict match = new MatchToPredict(homeTeam, awayTeam, seasonYearStart, leagueName, kickOffTime, database_id, sofascore_id);
                matches.add(match);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return matches;
    }

    /*
     * Method will return a set of games our database decided to bet on.
     * It will also total these bets up as it adds these and this result can be obtained by looking at the BetResultsTotalled class.
     */
    public static BetResultsTotalled getResultsOfPredictions(Date lowerLimit, Date upperLimit,
                                                             WhenGameWasPredicted whenGameWasPredicted) {
        HashSet<BetResult> betResults = new HashSet<>();
        BetResultsTotalled totalledResults = new BetResultsTotalled();

        String EARLIEST = DateHelper.getSqlDate( lowerLimit == null ? new Date(0) : lowerLimit);
        String LATEST = DateHelper.getSqlDate( upperLimit == null ? new Date() : lowerLimit);

        try (Statement statement = DS_Main.connection.createStatement()) {
            ResultSet gamesBetOn = statement.executeQuery("SELECT " + HOMETEAM + "." + TeamTable.getColTeamName() + ", " + AWAYTEAM + "." + TeamTable.getColTeamName() + ", " +
                    MatchTable.getTableName() + "." + MatchTable.getColDate() + ", " + BetTable.getTableName() + "." + BetTable.getColStake() + ", " +
                    BetTable.getTableName() + "." + BetTable.getColOdds() + ", " + BetTable.getTableName() + "." + BetTable.getColResultBetOn() + ", " +
                    MatchTable.getTableName() + "." + MatchTable.getColHomeScore() + ", " + MatchTable.getTableName() + "." + MatchTable.getColAwayScore() +
                    " FROM " + BetTable.getTableName() +
                    " INNER JOIN " + MatchTable.getTableName() + " ON " + BetTable.getTableName() + "." + BetTable.getColMatchId() + " = " + MatchTable.getTableName() + "._id" +
                    " INNER JOIN " + TeamTable.getTableName() + " AS " + HOMETEAM + " ON " + MatchTable.getTableName() + "." + MatchTable.getColHometeamId() + " = " + HOMETEAM + "._id" +
                    " INNER JOIN " + TeamTable.getTableName() + " AS " + AWAYTEAM + " ON " + MatchTable.getTableName() + "." + MatchTable.getColAwayteamId() + " = " + AWAYTEAM + "._id" +
                    " WHERE " + MatchTable.getTableName() + "." + MatchTable.getColPredictedLive() + " = " + whenGameWasPredicted.getSqlIntCode() +
                    " AND " + MatchTable.getTableName() + "." + MatchTable.getColDate() + " >= '" + EARLIEST + "'" +
                    " AND " + MatchTable.getTableName() + "." + MatchTable.getColDate() + " <= '" + LATEST + "'"
                    );

            while (gamesBetOn.next()) {
                String homeTeamName = gamesBetOn.getString(1);
                String awayTeamName = gamesBetOn.getString(2);
                String dateString = gamesBetOn.getString(3);
                double stakeOnBet = gamesBetOn.getDouble(4);
                double oddsWhenBetPlaced = gamesBetOn.getDouble(5);
                int resultBetOn = gamesBetOn.getInt(6);
                int homeScore = gamesBetOn.getInt(7);
                int awayScore = gamesBetOn.getInt(8);
                int result = calculateResult(homeScore, awayScore);

                Date date = DateHelper.createDateFromSQL(dateString);

                BetResult betResult = new BetResult(date, homeTeamName, awayTeamName, stakeOnBet, oddsWhenBetPlaced, resultBetOn, result);
                betResults.add(betResult);

                //double moneyOut, double odds, int resultBetOn, int result
                totalledResults.addBet(stakeOnBet, oddsWhenBetPlaced, resultBetOn, result);
            }


        } catch (Exception e) {
            e.printStackTrace();
        }

        return totalledResults;
    }

    private static int calculateResult (int homeScore, int awayScore) {
        if (homeScore > awayScore) return 0;
        else if (homeScore == awayScore) return 1;
        else return 2;
    }

    /*
     * Method to be called by machineLearning.GetMatchesFromDb and will return a ResultSet of all Player Ratings populated with
     * Teams, Matches, leagues.
     * From here we can calculate our training data and save into it's own database or file so we can recall whenever we want.
     */
    public static ArrayList getLeagueData(LeagueSeasonIds leagueSeasonIds) {
        return getLeagueData(leagueSeasonIds.name(), -1);
    }
    public static ArrayList getLeagueData(String leagueName, int seasonYearStart) {
        try {
            //no try with resources here as we're going to be passing resultset outside of this method
            Statement statement = DS_Main.connection.createStatement();
            String PLAYERS_TEAM = "players_team";

            //used to allow method to get out just 1 season
            int earliestSeason = -1;
            int latestSeason = 999;
            if (seasonYearStart != -1) {
                earliestSeason = seasonYearStart;
                latestSeason = seasonYearStart;
            }

            //gets all data we need to plug back into our classes for every player rating, sorted firstly by date, and then by the match id, then the team that the player
            //played for and then finally by how many minutes the player played. This gives us grouped player ratings by match and team, ordered by minutes played.
            ResultSet playerRatingsRows = statement.executeQuery("SELECT " + PlayerRatingTable.getTableName() + "." + PlayerRatingTable.getColPlayerName() + ", " +
                    PlayerRatingTable.getTableName() + "." + PlayerRatingTable.getColMins() + ", " +
                    PlayerRatingTable.getTableName() + "." + PlayerRatingTable.getColRating() + ", " +
                    PLAYERS_TEAM + "." + TeamTable.getColTeamName() + " AS '" + PLAYERS_TEAM + "', " +
                    MatchTable.getTableName() + "." + MatchTable.getColDate() + ", " +
                    HOMETEAM + "." + TeamTable.getColTeamName() + " AS '" + HOMETEAM + "', " +
                    MatchTable.getTableName() + "." + MatchTable.getColHomeScore() + ", " +
                    MatchTable.getTableName() + "." + MatchTable.getColHomeXg() + ", " +
                    AWAYTEAM + "." + TeamTable.getColTeamName() + " AS '" + AWAYTEAM + "', " +
                    MatchTable.getTableName() + "." + MatchTable.getColAwayScore() + ", " +
                    MatchTable.getTableName() + "." + MatchTable.getColAwayXg() + ", " +
                    MatchTable.getTableName() + "." + MatchTable.getColHomeWinOdds() + ", " +
                    MatchTable.getTableName() + "." + MatchTable.getColDrawOdds() + ", " +
                    MatchTable.getTableName() + "." + MatchTable.getColAwayWinOdds() + ", " +
                    MatchTable.getTableName() + "." + MatchTable.getColFirstScorer() + ", " +
                    MatchTable.getTableName() + "._id, " +
                    MatchTable.getTableName() + "." + MatchTable.getColSeasonYearStart() + ", " +
                    LeagueTable.getTableName() + "." + LeagueTable.getColName() +
                    " FROM " + PlayerRatingTable.getTableName() +
                    " INNER JOIN " + MatchTable.getTableName() + " ON " + PlayerRatingTable.getTableName() + "." + PlayerRatingTable.getColMatchId() + " = " + MatchTable.getTableName() + "._id" +
                    " INNER JOIN " + TeamTable.getTableName() + " AS " + PLAYERS_TEAM + " ON " + PlayerRatingTable.getTableName() + "." + PlayerRatingTable.getColTeamId() + " = " + PLAYERS_TEAM + "._id" +
                    " INNER JOIN " + TeamTable.getTableName() + " AS " + HOMETEAM + " ON " + MatchTable.getTableName() + "." + MatchTable.getColHometeamId() + " = " + HOMETEAM + "._id" +
                    " INNER JOIN " + TeamTable.getTableName() + " AS " + AWAYTEAM + " ON " + MatchTable.getTableName() + "." + MatchTable.getColAwayteamId() + " = " + AWAYTEAM + "._id" +
                    " INNER JOIN " + LeagueTable.getTableName() + " ON " + HOMETEAM + "." + TeamTable.getColLeagueId() + " = " + LeagueTable.getTableName() + "._id" +
                    " WHERE " + LeagueTable.getTableName() + "." + LeagueTable.getColName() + " = '" + leagueName + "'" +
                    " AND " + MatchTable.getTableName() + "." + MatchTable.getColSeasonYearStart() + " >= " + earliestSeason +
                    " AND " + MatchTable.getTableName() + "." + MatchTable.getColSeasonYearStart() + " <= " + latestSeason +
                    " ORDER BY " + MatchTable.getTableName() + "." + MatchTable.getColDate() + ", " + MatchTable.getTableName() + "._id, " + PLAYERS_TEAM + ", " + PlayerRatingTable.getTableName() + "." + PlayerRatingTable.getColMins() + " DESC");

            ArrayList statementAndResults = new ArrayList(); //no type arraylist as we are passing both the statement and resultset to another function.
            statementAndResults.add(statement);
            statementAndResults.add(playerRatingsRows);
            return statementAndResults;
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            e.printStackTrace();
            return null;
        }
    }

    public static ArrayList getMatchesBetweenTeams(String leagueName, ArrayList<MatchToPredict> matches, int numbSeasonsPrior) {
        int currSeasonStart = DateHelper.getStartYearForCurrentSeason();
        int earliestSeason = currSeasonStart-numbSeasonsPrior;

        try {
            Statement statement = DS_Main.connection.createStatement();

            ResultSet rs = statement.executeQuery("SELECT " + HOMETEAM + "." + TeamTable.getColTeamName() + ", " + AWAYTEAM + "." + TeamTable.getColTeamName() + ", " +
                                        MatchTable.getTableName() + "." + MatchTable.getColHomeScore() + ", " + MatchTable.getTableName() + "."+ MatchTable.getColAwayScore() +
                                        MatchTable.getTableName() + "." + MatchTable.getColSeasonYearStart() + ", " +
                                        " INNER JOIN " + TeamTable.getTableName() + " AS " + HOMETEAM + " ON " + MatchTable.getTableName() + "." + MatchTable.getColHometeamId() + " = " + HOMETEAM + "._id" +
                                        " INNER JOIN " + TeamTable.getTableName() + " AS " + AWAYTEAM + " ON " + MatchTable.getTableName() + "." + MatchTable.getColAwayteamId() + " = " + AWAYTEAM + "._id" +
                                        " INNER JOIN " + LeagueTable.getTableName() + " ON " + HOMETEAM + "." + TeamTable.getColLeagueId() + " = " + LeagueTable.getTableName() + "._id" +
                                        getConditions(matches) +
                                        " AND " + LeagueTable.getTableName() + "." + LeagueTable.getColName() + " = '" + leagueName + "' " +
                                        " ORDER BY " + MatchTable.getTableName() + "." + MatchTable.getColDate());

            ArrayList statementAndResults = new ArrayList(); //no type arraylist as we are passing both the statement and resultset to another function.
            statementAndResults.add(statement);
            statementAndResults.add(rs);
            return statementAndResults;
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            e.printStackTrace();
            return null;
        }
    }

    private static String getConditions(ArrayList<MatchToPredict> matches) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i<matches.size(); i++) {
            MatchToPredict currMatch = matches.get(i);
            sb.append( i == 0 ? " WHERE (" : ") OR (");
            sb.append(HOMETEAM + "." + TeamTable.getColTeamName() + " = '" + currMatch.getHomeTeamName() + "' ");
            sb.append(" AND " + AWAYTEAM + "." + TeamTable.getColTeamName() + " = '" + currMatch.getAwayTeamName() + "' ");
        }
        sb.append(")");
        return sb.toString();
    }


}
