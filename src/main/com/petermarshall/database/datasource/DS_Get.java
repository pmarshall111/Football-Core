package com.petermarshall.database.datasource;

import com.petermarshall.DateHelper;
import com.petermarshall.database.BetReflection;
import com.petermarshall.database.BetReflectionsTotalled;
import com.petermarshall.database.FirstScorer;
import com.petermarshall.database.Result;
import com.petermarshall.database.dbTables.*;
import com.petermarshall.machineLearning.createData.classes.MatchToPredict;
import com.petermarshall.machineLearning.createData.HistoricMatchDbData;
import com.petermarshall.machineLearning.createData.PlayerMatchDbData;
import com.petermarshall.scrape.classes.League;
import com.petermarshall.scrape.classes.LeagueIdsAndData;
import com.petermarshall.scrape.classes.Season;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.*;

import static com.petermarshall.database.datasource.DS_Main.*;

public class DS_Get {
    public static int getLeagueId(String name) {
        try (Statement statement = DS_Main.connection.createStatement()) {
            ResultSet rs = statement.executeQuery("SELECT _id FROM " + LeagueTable.getTableName() +
                    " WHERE " + LeagueTable.getColName() + " = '" + name + "'");
            while (rs.next()) {
                return rs.getInt(1);
            }
            return -9999;
        } catch (SQLException e) {
            System.out.println(e);
            return -9999;
        }
    }

    public static int getLeagueId(League league) {
        return getLeagueId(league.getName());
    }

    static int getMatchId(int homeTeamId, int awayTeamId, int seasonYearStart) {
        try (Statement statement = DS_Main.connection.createStatement()) {
            ResultSet rs = statement.executeQuery("SELECT _id FROM " + MatchTable.getTableName() +
                    " WHERE " + MatchTable.getColHometeamId() + " = " + homeTeamId +
                    " AND " + MatchTable.getColAwayteamId() + " = " + awayTeamId +
                    " AND " + MatchTable.getColSeasonYearStart() + " = " + seasonYearStart);
            while (rs.next()) {
                return rs.getInt(1);
            }
            return -9999;
        } catch (SQLException e) {
            System.out.println(e);
            return -9999;
        }
    }

    static int getTeamId(String teamName, int leagueId) {
        try (Statement statement = DS_Main.connection.createStatement();
             ResultSet teamQuery = statement.executeQuery("SELECT _id FROM " + TeamTable.getTableName() +
                     " WHERE " + TeamTable.getColTeamName() + " = '" + teamName + "'" +
                     " AND " + TeamTable.getColLeagueId() + " = " + leagueId)) {
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

    public static String getLastCompletedMatchInLeague(League league) {
        try (Statement statement = DS_Main.connection.createStatement()) {
            ResultSet dateStringRS = statement.executeQuery("SELECT " + MatchTable.getTableName() + "." + MatchTable.getColDate() + " FROM " + MatchTable.getTableName() +
                    " INNER JOIN " + TeamTable.getTableName() + " ON " + MatchTable.getTableName() + "." + MatchTable.getColHometeamId() + " = " + TeamTable.getTableName() + "._id" +
                    " INNER JOIN " + LeagueTable.getTableName() + " ON " + TeamTable.getTableName() + "." + TeamTable.getColLeagueId() + " = " + LeagueTable.getTableName() + "._id" +
                    " WHERE " + LeagueTable.getTableName() + "." + LeagueTable.getColName() + " = '" + league.getName() + "'" +
                    " AND " + MatchTable.getTableName() + "." + MatchTable.getColHomeScore() + " > " + "-1" +
                    " ORDER BY " + MatchTable.getTableName() + "." + MatchTable.getColDate() + " DESC" +
                    " LIMIT " + "1");

            while (dateStringRS.next()) {
                return dateStringRS.getString(1);
            }
            //otherwise there are no played games in the db.
            return DateHelper.getSqlDate(new Date(0));
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            e.printStackTrace();
            return null;
        }
    }

    public static ArrayList<MatchToPredict> getMatchesToPredictByDates(java.util.Date earliestKickoff, java.util.Date latestKickoff) {
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
     * Method will return a set of games our database decided to bet on.
     * It will also total these bets up as it adds these and this result can be obtained by looking at the BetResultsTotalled class.
     */
    public static BetReflectionsTotalled getResultsOfPredictions(Date lowerLimit, Date upperLimit) {
        HashSet<BetReflection> betResults = new HashSet<>();
        BetReflectionsTotalled totalledResults = new BetReflectionsTotalled();

        String EARLIEST = DateHelper.getSqlDate(lowerLimit == null ? new Date(0) : lowerLimit);
        String LATEST = DateHelper.getSqlDate(upperLimit == null ? new Date() : lowerLimit);

        try (Statement statement = DS_Main.connection.createStatement()) {
            ResultSet gamesBetOn = statement.executeQuery("SELECT " + HOMETEAM + "." + TeamTable.getColTeamName() + ", " + AWAYTEAM + "." + TeamTable.getColTeamName() + ", " +
                    MatchTable.getTableName() + "." + MatchTable.getColDate() + ", " + BetTable.getTableName() + "." + BetTable.getColStake() + ", " +
                    BetTable.getTableName() + "." + BetTable.getColOdds() + ", " + BetTable.getTableName() + "." + BetTable.getColResultBetOn() + ", " +
                    MatchTable.getTableName() + "." + MatchTable.getColHomeScore() + ", " + MatchTable.getTableName() + "." + MatchTable.getColAwayScore() +
                    " FROM " + BetTable.getTableName() +
                    " INNER JOIN " + MatchTable.getTableName() + " ON " + BetTable.getTableName() + "." + BetTable.getColMatchId() + " = " + MatchTable.getTableName() + "._id" +
                    " INNER JOIN " + TeamTable.getTableName() + " AS " + HOMETEAM + " ON " + MatchTable.getTableName() + "." + MatchTable.getColHometeamId() + " = " + HOMETEAM + "._id" +
                    " INNER JOIN " + TeamTable.getTableName() + " AS " + AWAYTEAM + " ON " + MatchTable.getTableName() + "." + MatchTable.getColAwayteamId() + " = " + AWAYTEAM + "._id" +
                    " WHERE " + MatchTable.getTableName() + "." + MatchTable.getColDate() + " >= '" + EARLIEST + "'" +
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

                BetReflection betResult = new BetReflection(date, homeTeamName, awayTeamName, stakeOnBet, oddsWhenBetPlaced, resultBetOn, result);
                betResults.add(betResult);

                //double moneyOut, double odds, int resultBetOn, int result
                totalledResults.addBet(stakeOnBet, oddsWhenBetPlaced, resultBetOn, result);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return totalledResults;
    }

    private static int calculateResult(int homeScore, int awayScore) {
        if (homeScore > awayScore) return Result.HOME_WIN.getSqlIntCode();
        else if (homeScore == awayScore) return Result.DRAW.getSqlIntCode();
        else return Result.AWAY_WIN.getSqlIntCode();
    }

    /*
     * Method to be called by machineLearning.GetMatchesFromDb and will return a ResultSet of all Player Ratings populated with
     * Teams, Matches, leagues.
     * From here we can calculate our training data and save into it's own database or file so we can recall whenever we want.
     */
    public static ArrayList<PlayerMatchDbData> getLeagueData(LeagueIdsAndData leagueIdsAndData) {
        return getLeagueData(leagueIdsAndData.name(), -1);
    }

    public static ArrayList<PlayerMatchDbData> getLeagueData(String leagueName, int seasonYearStart) {
        try (Statement statement = DS_Main.connection.createStatement()) {

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
                    " AND " + MatchTable.getColHomeScore() + " != -1" +
                    " AND " + MatchTable.getColAwayScore() + " != -1" +
                    " AND " + MatchTable.getColHomeXg() + " != -1" +
                    " AND " + MatchTable.getColAwayXg() + " != -1" +
                    " AND ( (" + MatchTable.getColFirstScorer() + " = -1" + " AND " + MatchTable.getColHomeScore() + " = 0 AND " + MatchTable.getColAwayScore() + " = 0)" +
                        " OR " + MatchTable.getColFirstScorer() + " != " + FirstScorer.NO_FIRST_SCORER.getSqlIntCode() + ")" +
                    " ORDER BY " + MatchTable.getTableName() + "." + MatchTable.getColDate() + ", " + MatchTable.getTableName() + "._id, " + PLAYERS_TEAM + ", " + PlayerRatingTable.getTableName() + "." + PlayerRatingTable.getColMins() + " DESC");

            ArrayList<PlayerMatchDbData> dbData = new ArrayList<>();
            while (playerRatingsRows.next()) {
                PlayerMatchDbData pmdbData = new PlayerMatchDbData(playerRatingsRows);
                dbData.add(pmdbData);
            }
            return dbData;
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            e.printStackTrace();
            return null;
        }
    }

    //Used when predicting matches just in 1 season. Here, the player ratings for previous seasons are not needed, but the results between teams are.
    //So this method is used to reduce the data we need to get from the DB
    public static ArrayList<HistoricMatchDbData> getMatchesBetweenTeams(String leagueName, ArrayList<MatchToPredict> matches) {
        try (Statement statement = DS_Main.connection.createStatement()) {
            ResultSet rs = statement.executeQuery("SELECT " + HOMETEAM + "." + TeamTable.getColTeamName() + ", " + AWAYTEAM + "." + TeamTable.getColTeamName() + ", " +
                    MatchTable.getTableName() + "." + MatchTable.getColHomeScore() + ", " + MatchTable.getTableName() + "." + MatchTable.getColAwayScore() + ", " +
                    MatchTable.getTableName() + "." + MatchTable.getColSeasonYearStart() + " FROM " + MatchTable.getTableName() +
                    " INNER JOIN " + TeamTable.getTableName() + " AS " + HOMETEAM + " ON " + MatchTable.getTableName() + "." + MatchTable.getColHometeamId() + " = " + HOMETEAM + "._id" +
                    " INNER JOIN " + TeamTable.getTableName() + " AS " + AWAYTEAM + " ON " + MatchTable.getTableName() + "." + MatchTable.getColAwayteamId() + " = " + AWAYTEAM + "._id" +
                    " INNER JOIN " + LeagueTable.getTableName() + " ON " + HOMETEAM + "." + TeamTable.getColLeagueId() + " = " + LeagueTable.getTableName() + "._id" +
                    getConditions(matches) +
                    " AND " + LeagueTable.getTableName() + "." + LeagueTable.getColName() + " = '" + leagueName + "' " +
                    " ORDER BY " + MatchTable.getTableName() + "." + MatchTable.getColDate());

            ArrayList<HistoricMatchDbData> previousMatches = new ArrayList<>();
            while (rs.next()) {
                HistoricMatchDbData histMatch = new HistoricMatchDbData(rs);
                previousMatches.add(histMatch);
            }
            return previousMatches;
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            e.printStackTrace();
            return null;
        }
    }

    private static String getConditions(ArrayList<MatchToPredict> matches) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < matches.size(); i++) {
            MatchToPredict currMatch = matches.get(i);
            sb.append(i == 0 ? " WHERE (" : " OR (")
                    .append(HOMETEAM + ".")
                    .append(TeamTable.getColTeamName()).append(" = '").append(currMatch.getHomeTeamName()).append("' ")
                    .append(" AND " + AWAYTEAM + "." + TeamTable.getColTeamName() + " = '" + currMatch.getAwayTeamName() + "' ")
                    .append(") OR (")
                    .append(HOMETEAM + "." + TeamTable.getColTeamName() + " = '" + currMatch.getAwayTeamName() + "' ")
                    .append(" AND " + AWAYTEAM + "." + TeamTable.getColTeamName() + " = '" + currMatch.getHomeTeamName() + "' ")
                    .append(")");
        }
        return sb.toString();
    }

    //method will get out matches from the database where it is both teams next match and has not already been predicted on
    public static ArrayList<MatchToPredict> getMatchesToPredict() {
        try (Statement statement = DS_Main.connection.createStatement()) {
            String ids = "idsWithFuturePredictions";
            String currDate = DateHelper.getSqlDate(new Date());
            //statement gets out the next home game of all teams that do not already have a prediction
            ResultSet rs = statement.executeQuery("WITH " + ids + " AS (" +
                        "SELECT " + MatchTable.getColHometeamId() + ", " + MatchTable.getColAwayteamId() + " FROM " + PredictionTable.getTableName() +
                        " INNER JOIN " + MatchTable.getTableName() + " ON " + PredictionTable.getColMatchId() + " = _id " +
                        " WHERE " + MatchTable.getTableName() + "." + MatchTable.getColDate() + " > '" + currDate + "') " +
                    " SELECT " + HOMETEAM + "." + TeamTable.getColTeamName() + ", " + AWAYTEAM + "." + TeamTable.getColTeamName() + ", " + MatchTable.getColSeasonYearStart() + ", " +
                    LeagueTable.getTableName() + "." + LeagueTable.getColName() + ", " + MatchTable.getTableName() + "." + MatchTable.getColDate() + ", " +
                    MatchTable.getTableName() + "._id, " + MatchTable.getColSofascoreId() + " FROM " + MatchTable.getTableName() +
                    " INNER JOIN " + TeamTable.getTableName() + " AS " + HOMETEAM + " ON " + MatchTable.getTableName() + "." + MatchTable.getColHometeamId() + " = " + HOMETEAM + "._id" +
                    " INNER JOIN " + TeamTable.getTableName() + " AS " + AWAYTEAM + " ON " + MatchTable.getTableName() + "." + MatchTable.getColAwayteamId() + " = " + AWAYTEAM + "._id" +
                    " INNER JOIN " + LeagueTable.getTableName() + " ON " + HOMETEAM + "." + TeamTable.getColLeagueId() + " = " + LeagueTable.getTableName() + "._id " +
                    " WHERE " + MatchTable.getColDate() + " > '" + currDate + "'" +
                    " AND " + MatchTable.getColHometeamId() + " NOT IN (SELECT " + MatchTable.getColHometeamId() + " FROM " + ids + ")" +
                    " AND " + MatchTable.getColHometeamId() + " NOT IN (SELECT " + MatchTable.getColAwayteamId() + " FROM " + ids + ")" +
                    " AND " + MatchTable.getColAwayteamId() + " NOT IN (SELECT " + MatchTable.getColHometeamId() + " FROM " + ids + ")" +
                    " AND " + MatchTable.getColAwayteamId() + " NOT IN (SELECT " + MatchTable.getColAwayteamId() + " FROM " + ids + ")" +
                    " GROUP BY " + MatchTable.getColHometeamId() +
                    " ORDER BY " + MatchTable.getColDate());

            //filtering out responses so that teams are only included once - we want to only predict a teams next match
            ArrayList<MatchToPredict> mtps = new ArrayList<>();
            HashSet<String> teamsWithNextMatchFound = new HashSet<>();
            while (rs.next()) {
                String homeTeamName = rs.getString(1);
                String awayTeamName = rs.getString(2);
                if (!teamsWithNextMatchFound.contains(homeTeamName) && !teamsWithNextMatchFound.contains(awayTeamName)) {
                    int seasonYearStart = rs.getInt(3);
                    String leagueName = rs.getString(4);
                    String gameDate = rs.getString(5);
                    int dbId = rs.getInt(6);
                    int sofascoreId = rs.getInt(7);
                    mtps.add(new MatchToPredict(homeTeamName, awayTeamName, Season.getSeasonKeyFromYearStart(seasonYearStart), leagueName,
                            gameDate, dbId, sofascoreId));
                    teamsWithNextMatchFound.addAll(Arrays.asList(homeTeamName, awayTeamName));
                }
            }
            return mtps;
        } catch (SQLException e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    public static HashMap<League, String> getLeaguesToUpdate() {
        try (Statement statement = DS_Main.connection.createStatement()) {

            ResultSet rs = statement.executeQuery("SELECT " + LeagueTable.getTableName() + "." + LeagueTable.getColName() + ", "  + 
                    MatchTable.getColDate() + " FROM " + MatchTable.getTableName() +
                    " INNER JOIN " + TeamTable.getTableName() + " ON " + MatchTable.getTableName() + "." + MatchTable.getColHometeamId() + " = " + TeamTable.getTableName() + "._id" +
                    " INNER JOIN " + LeagueTable.getTableName() + " ON " + TeamTable.getTableName() + "." + TeamTable.getColLeagueId() + " = " + LeagueTable.getTableName() + "._id " +
                    " WHERE " + MatchTable.getColHomeScore() + " = -1" +
                    " AND " + MatchTable.getColDate() + " < '" + DateHelper.getSqlDate(DateHelper.subtractXminsFromDate(new Date(), 200)) + "'" +
                    " AND " + MatchTable.getColSeasonYearStart() + " = " + DateHelper.getStartYearForCurrentSeason() +
                    " GROUP BY " + LeagueTable.getTableName() + "." + LeagueTable.getColName());

            HashMap<League, String> leaguesAndEarliestDate = new HashMap<>();
            while (rs.next()) {
                String leagueName = rs.getString(1);
                String earliestGameWithNoScore = rs.getString(2);
                if (leagueName.equals(LeagueIdsAndData.EPL.name())) {
                    leaguesAndEarliestDate.put(new League(LeagueIdsAndData.EPL), earliestGameWithNoScore);
                } else if (leagueName.equals(LeagueIdsAndData.LA_LIGA.name())) {
                    leaguesAndEarliestDate.put(new League(LeagueIdsAndData.LA_LIGA), earliestGameWithNoScore);
                } else if (leagueName.equals(LeagueIdsAndData.BUNDESLIGA.name())) {
                    leaguesAndEarliestDate.put(new League(LeagueIdsAndData.BUNDESLIGA), earliestGameWithNoScore);
                } else if (leagueName.equals(LeagueIdsAndData.SERIE_A.name())) {
                    leaguesAndEarliestDate.put(new League(LeagueIdsAndData.SERIE_A), earliestGameWithNoScore);
                } else if (leagueName.equals(LeagueIdsAndData.LIGUE_1.name())) {
                    leaguesAndEarliestDate.put(new League(LeagueIdsAndData.LIGUE_1), earliestGameWithNoScore);
                } else if (leagueName.equals(LeagueIdsAndData.RUSSIA.name())) {
                    leaguesAndEarliestDate.put(new League(LeagueIdsAndData.RUSSIA), earliestGameWithNoScore);
                }
            }
            return leaguesAndEarliestDate;
        } catch (SQLException e) {
            e.printStackTrace();
            return new HashMap<>();
        }
    }
}
