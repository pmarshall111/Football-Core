package com.petermarshall.machineLearning.createData;

import com.petermarshall.*;
import com.petermarshall.machineLearning.createData.classes.*;
import com.petermarshall.database.datasource.DataSource;
import com.petermarshall.scrape.classes.LeagueSeasonIds;


import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

/*
 * Class will get out data from database as each player rating and will create TrainingMatches that contains a measure of how each team was performing at the time.
 * Also used to create the features for our database.
 */
public class GetMatchesFromDb {
//
//    public static void main(String[] args) {
//        DataSource.openConnection();
//
//        loadInDataFromDb(); //creates TrainingMatches
////        WriteTrainingData.writeDataOutToCsvFiles(trainingData, "octavePowered.csv", "octavePoweredTest.csv", "javaPoweredTest.csv");
//
//        System.out.println("hello");
//
//        DataSource.closeConnection();
//    }
//
//
//    /*
//     * Method will loop through each league and call createData method.
//     * Gets all the teams for each league and adds their latest stats to them.
//     *
//     * Will only get data for played games, as we get the data out of the database by Player Ratings. (if the game hasn't been played yet, there will be no player ratings for that game.)
//     */
//    public static void loadInDataFromDb(Date onlyAddTrainingMatchAfter) {
//        if (onlyAddTrainingMatchAfter != null) {
//            addTrainingMatchesOnlyAfter = onlyAddTrainingMatchAfter;
//        }
//        loadInDataFromDb();
//    }
//    public static void loadInDataFromDb() {
//        DataSource.openConnection();
//
//        try {
//            LeagueSeasonIds[] leagues = LeagueSeasonIds.values();
//            for (LeagueSeasonIds league : leagues) {
//                //have to get the statement as well so we can control when it gets closed. If closed, resultset is unavailable.
//                ArrayList statementAndResults = DataSource.getLeagueData(league);
//                Statement statement = (Statement) statementAndResults.get(0);
//                ResultSet playerRatingsDataForLeague = (ResultSet) statementAndResults.get(1);
//
//                //making sure we store our TrainingTeams so we can access them when we come to create new games to predict.
//                teamsInLeague = new HashMap<>();
//                createData(playerRatingsDataForLeague);
//                leaguesOfTeams.put(league.name(), teamsInLeague);
//                teamsInLeague = new HashMap<>();
//
//                playerRatingsDataForLeague.close();
//                statement.close();
//            }
//
//        } catch (SQLException | NullPointerException e) {
//            System.out.println(e.getMessage());
//            e.printStackTrace();
//        }
//
//        DataSource.closeConnection();
//    }
//
//
//
//    //fields used to create training data and prediction data.
//    private static final int NUMB_SEASONS_HISTORY = 2;
//    private static final boolean USE_HOME_AWAY_STATS_ONLY = false;
//    private static final int NUMB_MATCHES_BEFORE_VALID_TRAINING_DATA = 7;
//    private static final int LAST_N_GAMES_FORM = 5;
//    private static final int IGNORE_FIRST_N_GAMES = 0; //because we start the season with everyone at the same level. might want to ignore for a few games whilst the right teams get the right data.
//
//    private static Date gamesNeedPredictingAfterDate = null;
//    private static ArrayList<MatchToPredict> missedGamesThatNeedPredicting = new ArrayList<>();
//
//    private static HashMap<String, HashMap<String, TrainingTeam>> leaguesOfTeams = new HashMap<>();
//    private static HashMap<String, TrainingTeam> teamsInLeague = new HashMap<>();
//    private static ArrayList<TrainingMatch> trainingData = new ArrayList<>();
//    private static Date addTrainingMatchesOnlyAfter = null;
//
//    //NOTE: we store this data outside of loop as we're looping through the player ratings. We only know we're done with a game when
//    //we get to a player rating from a different match, or the end of the ResultSet. Sqlite3 doesn't support backwards iteration, so we cannot go back
//    //once we know we've reached the end of the match - hence why we store the data outside of the loop.
//    private static Date kickoffTime;
//    private static String homeTeamName;
//    private static int homeScore;
//    private static double homeXGF;
//    private static String awayTeamName;
//    private static int awayScore;
//    private static double awayXGF;
//    private static double homeWinOdds;
//    private static double drawOdds;
//    private static double awayWinOdds;
//    private static int firstScorer;
//
//    private static int lastRecordMatchId = -1;
//    private static String lastRecordSeasonYear = null;
//
//    private static TrainingTeam homeTeam = null;
//    private static TrainingTeamsSeason homeTeamThisSeason = null;
//    private static TrainingTeam awayTeam = null;
//    private static TrainingTeamsSeason awayTeamThisSeason = null;
//
//    //NOTE: needs to be an ArrayList, not a HashSet as we care about the order of our players. They come sorted from the database by number
//    //of minutes played, which we use to calculate the main lineup of the game.
//    private static HashMap<String, ArrayList<Player>> lineups;
//
//
//
//    /*
//     * Creates the training data and returns an arraylist of all the matches with the data about how the teams were doing before the match was played.
//     * As it's creating the training data it will check to see if there are previous matches in missedGamesThatNeedPredicting and when it finds a game in there
//     * that matches the current game it's looking through, it will add the stats for both teams before they played the game. This will allow us to make predictions
//     * on games in the past so we can see how our model is doing.
//     *
//     * Method will go through ResultSet of player ratings data, joined with the team, match, season, league. Because
//     * Sqlite3 ResultSet can only go forwards, we must store the team and match data outside of the loop while we go through the
//     * players collecting the lineups. Then when we get to a new match_id, we use this data outside of the loop to create a new TrainingMatch and add the data to each team.
//     *
//     * Because we're getting the player ratings, we don't need to worry about games that haven't yet been played, as we wouldn't
//     * yet have ratings data for those players in the database.
//     */
//    public static void createData(ResultSet playerRatingsRows) {
//        try {
//
//            while (playerRatingsRows.next()) {
//
//                if (lastRecordMatchId == -1) { //initialising.
//                    initialiseForNewMatch(playerRatingsRows);
//                }
//
//                int currMatchId = playerRatingsRows.getInt(16);
//                if (currMatchId != lastRecordMatchId) {
//                    //Comes across a new game.
//
//                    //functionality to add stats at the time of the match for a game that was not previously predicted on (maybe the model was not running at the time).
//                    if (missedGamesThatNeedPredicting.size() > 0 && kickoffTime.after(gamesNeedPredictingAfterDate)) {
//                        tryToAddStatsToOldGame();
//                    }
//
//                    storeDataFromPreviousMatch();
//                    initialiseForNewMatch(playerRatingsRows);
//                }
//
//                //Saving new player ratings data each iteration.
//                String playerName = playerRatingsRows.getString(1);
//                int minsPlayed = playerRatingsRows.getInt(2);
//                double rating = playerRatingsRows.getDouble(3);
//                String playersTeam = playerRatingsRows.getString(4);
//
//                boolean isOnHomeTeam = playersTeam.equals(homeTeamName);
//                if (!isOnHomeTeam && !playersTeam.equals(awayTeamName)) throw new RuntimeException("Player was not on either home team or away team." +
//                        "Player name: " + playerName + " plays for " + playersTeam + " but was found in a match for " + homeTeamName + " vs " + awayTeamName);
//
//                Player player = new Player(playerName, minsPlayed, rating, isOnHomeTeam);
//                if (isOnHomeTeam) {
//                    lineups.get("home").add(player);
//                } else lineups.get("away").add(player);
//
//            }
//
//            //creating 1 last match for the very last record as we'd normally only create a match when we come across the next game.
//            if (missedGamesThatNeedPredicting.size() > 0 && kickoffTime.after(gamesNeedPredictingAfterDate)) {
//                tryToAddStatsToOldGame();
//            }
//            storeDataFromPreviousMatch();
//
//        } catch (Exception e) {
//            e.printStackTrace();
//            System.out.println(e.getMessage());
//        }
//    }
//
//    private static void storeDataFromPreviousMatch() throws Exception {
//        TrainingMatch match = addDataToTrainingMatch(homeTeam, awayTeam, homeTeamThisSeason, awayTeamThisSeason, lineups, Integer.parseInt(lastRecordSeasonYear.substring(0,2)));
//        addMatchToTrainingSet(match);
//        addMatchToTeamsOverallHistory(match);
//        addDataToEachTeamsSeason();
//    }
//
//
//    /*
//     * Creates training match and adds it to both teams. Adds it to matches to train on if both teams have played more than the
//     * specified number of games so far in the current season.
//     */
//    private static void addMatchToTrainingSet(TrainingMatch match) {
//        //to train on the data we need all available data complete, however to predict future games we only need the scores of the previous matchup.
//        if (match.getHomeTeamHomeGamesPlayed() >= NUMB_MATCHES_BEFORE_VALID_TRAINING_DATA &&
//                match.getAwayTeamAwayGamesPlayed() >= NUMB_MATCHES_BEFORE_VALID_TRAINING_DATA &&
//                homeScore != -1 && awayScore != -1 &&
//                homeXGF != -1 && awayXGF != -1 && homeWinOdds != -1 && drawOdds != -1 && awayWinOdds != -1) {
//
//            if (addTrainingMatchesOnlyAfter == null || match.getKickoffTime().after(addTrainingMatchesOnlyAfter)) {
//                trainingData.add(match);
//            }
//
//        }
//    }
//
//    private static void addMatchToTeamsOverallHistory(TrainingMatch match) {
//
//        if (homeScore != -1 && awayScore != -1) {
//            homeTeam.addMatchWithTeam(awayTeamName, match);
//            awayTeam.addMatchWithTeam(homeTeamName, match);
//        }
//
//    }
//
//    private static void initialiseForNewMatch(ResultSet playerRatingsRows) throws SQLException {
//        initLineups();
//        loadCurrentTeams(playerRatingsRows);
//        saveMatchDataToFields(playerRatingsRows);
//    }
//
//    /*
//     * Creates empty lineups
//     */
//    private static void initLineups() {
//        //resetting lineups
//        lineups = new HashMap<>();
//        ArrayList<Player> homeRatings = new ArrayList<>();
//        ArrayList<Player> awayRatings = new ArrayList<>();
//        lineups.put("home", homeRatings);
//        lineups.put("away", awayRatings);
//    }
//
//    /*
//     * Updates current home team and away team, then set the current teams to these. Default action if team is not found
//     * is to create a new team.
//     */
//    private static void loadCurrentTeams(ResultSet playerRatingsRows) throws SQLException {
//        homeTeamName = playerRatingsRows.getString(6);
//        awayTeamName = playerRatingsRows.getString(9);
//
//        homeTeam = teamsInLeague.getOrDefault(homeTeamName, null);
//        awayTeam = teamsInLeague.getOrDefault(awayTeamName, null);
//        if (homeTeam == null) {
//            homeTeam = new TrainingTeam(homeTeamName);
//            teamsInLeague.put(homeTeamName, homeTeam);
//        }
//        if (awayTeam == null) {
//            awayTeam = new TrainingTeam(awayTeamName);
//            teamsInLeague.put(awayTeamName, awayTeam);
//        }
//        homeTeamThisSeason = homeTeam.getTeamsSeason(lastRecordSeasonYear); //default action is to create a new season if not there.
//        awayTeamThisSeason = awayTeam.getTeamsSeason(lastRecordSeasonYear);
//    }
//
//    /*
//     * Method stores data from the match into fields on the class.
//     */
//    private static void saveMatchDataToFields(ResultSet playerRatingsRows) throws SQLException {
//        String dateString = playerRatingsRows.getString(5);
//        kickoffTime = DateHelper.createDateFromSQL(dateString);
//        homeScore = playerRatingsRows.getInt(7);
//        homeXGF = playerRatingsRows.getDouble(8);
//        awayScore = playerRatingsRows.getInt(10);
//        awayXGF = playerRatingsRows.getDouble(11);
//        homeWinOdds = playerRatingsRows.getDouble(12);
//        drawOdds = playerRatingsRows.getDouble(13);
//        awayWinOdds = playerRatingsRows.getDouble(14);
//        firstScorer = playerRatingsRows.getInt(15);
//        lastRecordMatchId = playerRatingsRows.getInt(16);
//        lastRecordSeasonYear = playerRatingsRows.getString(17);
//    }
//
//
//    private static void tryToAddStatsToOldGame() {
//
//        //need to loop through arraylist of matches to predict and try to find the match we're looking for.
//        //once we find that match, we need to remove it from the arraylist to reduce work.
//
//        for (MatchToPredict match : missedGamesThatNeedPredicting) {
//
//            Date gamePredictKickoffTime = DateHelper.createDateFromSQL(match.getSqlDateString());
//
//            if (match.getHomeTeamName().equals(homeTeamName) && match.getAwayTeamName().equals(awayTeamName)
//                    && gamePredictKickoffTime.equals(kickoffTime)) {
//
//                ArrayList<Player> homePlayersWhoPlayed = lineups.get("home");
//                ArrayList<Player> awayPlayersWhoPlayed = lineups.get("away");
//
//                ArrayList<String> homeLineupNames = convertPlayerListToLineupOfNames(homePlayersWhoPlayed);
//                ArrayList<String> awayLineupNames = convertPlayerListToLineupOfNames(awayPlayersWhoPlayed);
//
//                match.setHomeTeamPlayers(homeLineupNames);
//                match.setAwayTeamPlayers(awayLineupNames);
//
//                //create features
//                ArrayList<Double> features = createFeatures(homeTeam, awayTeam, homeTeamThisSeason, awayTeamThisSeason, homeLineupNames, awayLineupNames, match.getSeasonYearStart());
//                match.setFeatures(features);
//
//                missedGamesThatNeedPredicting.remove(match);
//                break;
//            }
//        }
//    }
//
//
//
//    /*
//     * ArrayList<Player> will come in sorted from the database, so in this method we just take the first 11 players in the list as the
//     * guys who played most and make our prediction from there.
//     */
//    private static ArrayList<String> convertPlayerListToLineupOfNames (ArrayList<Player> playersWhoPlayed) {
//        ArrayList<String> lineup = new ArrayList<>();
//
//        for (Player p: playersWhoPlayed) {
//            lineup.add(p.getPlayerName());
//            if (lineup.size() == 11) break;
//        }
//
//        return lineup;
//    }
//
//
//
//    /*
//     * To be called once the match has been created with the stats from before the match took place. This then adds the data
//     * to each team as the match has finished, before we overwrite our fields with info from the next match.
//     *
//     * Adds both game stats such as XG, and also the player ratings data that we have saved.
//     * Before data can be added, we need info from each team about how many goals they were expected to conceede for this match.
//     *
//     * Calls to addGameStats will only add the data to the team if the field in question != -1.
//     * Shouldn't need check for PlayerRatings as they'd only be added to the database if we have data.
//     */
//    private static void addDataToEachTeamsSeason() {
//        double homeTotalAvgGoalsFor = homeTeamThisSeason.getAvgGoalsFor(GamesSelector.ALL_GAMES);
//        double homeTotalAvgGoalsAgainst = homeTeamThisSeason.getAvgGoalsAgainst(GamesSelector.ALL_GAMES);
//        double homeHomeAvgGoalsFor = homeTeamThisSeason.getAvgGoalsFor(GamesSelector.ONLY_HOME_GAMES);
//        double homeHomeAvgGoalsAgainst = homeTeamThisSeason.getAvgGoalsAgainst(GamesSelector.ONLY_HOME_GAMES);
//
//        double homeTotalAvgXGF = homeTeamThisSeason.getAvgXGF(GamesSelector.ALL_GAMES);
//        double homeTotalAvgXGA = homeTeamThisSeason.getAvgXGA(GamesSelector.ALL_GAMES);
//        double homeHomeAvgXGF = homeTeamThisSeason.getAvgXGF(GamesSelector.ONLY_HOME_GAMES);
//        double homeHomeAvgXGA = homeTeamThisSeason.getAvgXGA(GamesSelector.ONLY_HOME_GAMES);
//
//        double homeWeightedTotalXGF = homeTeamThisSeason.getWeightedAvgXGF(GamesSelector.ALL_GAMES);
//        double homeWeightedTotalXGA = homeTeamThisSeason.getWeightedAvgXGA(GamesSelector.ALL_GAMES);
//        double homeWeightedHomeXGF = homeTeamThisSeason.getWeightedAvgXGF(GamesSelector.ONLY_HOME_GAMES);
//        double homeWeightedHomeXGA = homeTeamThisSeason.getWeightedAvgXGA(GamesSelector.ONLY_HOME_GAMES);
//
//        double awayTotalAvgGoalsFor = awayTeamThisSeason.getAvgGoalsFor(GamesSelector.ALL_GAMES);
//        double awayTotalAvgGoalsAgainst = awayTeamThisSeason.getAvgGoalsAgainst(GamesSelector.ALL_GAMES);
//        double awayAwayAvgGoalsFor = awayTeamThisSeason.getAvgGoalsFor(GamesSelector.ONLY_AWAY_GAMES);
//        double awayAwayAvgGoalsAgainst = awayTeamThisSeason.getAvgGoalsAgainst(GamesSelector.ONLY_AWAY_GAMES);
//
//        double awayTotalAvgXGF = awayTeamThisSeason.getAvgXGF(GamesSelector.ALL_GAMES);
//        double awayTotalAvgXGA = awayTeamThisSeason.getAvgXGA(GamesSelector.ALL_GAMES);
//        double awayAwayAvgXGF = awayTeamThisSeason.getAvgXGF(GamesSelector.ONLY_AWAY_GAMES);
//        double awayAwayAvgXGA = awayTeamThisSeason.getAvgXGA(GamesSelector.ONLY_AWAY_GAMES);
//
//        double awayWeightedTotalXGF = awayTeamThisSeason.getWeightedAvgXGF(GamesSelector.ALL_GAMES);
//        double awayWeightedTotalXGA = awayTeamThisSeason.getWeightedAvgXGA(GamesSelector.ALL_GAMES);
//        double awayWeightedAwayXGF = awayTeamThisSeason.getWeightedAvgXGF(GamesSelector.ONLY_AWAY_GAMES);
//        double awayWeightedAwayXGA = awayTeamThisSeason.getWeightedAvgXGA(GamesSelector.ONLY_AWAY_GAMES);
//
//        //added in later
//        double homeTotalPPG = homeTeamThisSeason.getAvgPoints(GamesSelector.ALL_GAMES);
//        double homeHomePPG = homeTeamThisSeason.getAvgPoints(GamesSelector.ONLY_HOME_GAMES);
//        double homeLast5TotalPPG = homeTeamThisSeason.getAvgPointsOverLastXGames(GamesSelector.ALL_GAMES, LAST_N_GAMES_FORM);
//        double homeLast5HomePPG = homeTeamThisSeason.getAvgPointsOverLastXGames(GamesSelector.ONLY_HOME_GAMES, LAST_N_GAMES_FORM);
//
//        double awayTotalPPG = awayTeamThisSeason.getAvgPoints(GamesSelector.ALL_GAMES);
//        double awayAwayPPG = awayTeamThisSeason.getAvgPoints(GamesSelector.ONLY_AWAY_GAMES);
//        double awayLast5TotalPPG = awayTeamThisSeason.getAvgPointsOverLastXGames(GamesSelector.ALL_GAMES, LAST_N_GAMES_FORM);
//        double awayLast5AwayPPG = awayTeamThisSeason.getAvgPointsOverLastXGames(GamesSelector.ONLY_AWAY_GAMES, LAST_N_GAMES_FORM);
//
//
////        int goalsFor, int goalsAgainst, double xGF, double xGA, boolean scoredFirst, boolean hasScoredFirstData, boolean homeTeam, double oppositionAvgTotalGF,
////        double oppositionAvgTotalGA, double oppositionAvgHomeAwayGF, double oppositionAvgHomeAwayGA, double oppositionAvgTotalXGF, double oppositionAvgTotalXGA,
////        double oppositionAvgHomeAwayXGF, double oppositionAvgHomeAwayXGA, double oppositionWeightedTotalXGF, double oppositionWeightedTotalXGA,
////        double oppositionWeightedHomeAwayXGF, double oppositionWeightedHomeAwayXGA, double opponentTotalWholeSeasonPPG, double opponentHomeAwayWholeSeasonPPG,
////        double opponentTotalLast5PPG, double opponentHomeAwayLast5PPG
//        //method will only add stats if they are present.
//        homeTeamThisSeason.addGameStats(homeScore,
//                awayScore,
//                homeXGF,
//                awayXGF,
//                firstScorer == 1,
//                firstScorer != -1,
//                true,
//                awayTotalAvgGoalsFor,
//                awayTotalAvgGoalsAgainst,
//                awayAwayAvgGoalsFor,
//                awayAwayAvgGoalsAgainst,
//                awayTotalAvgXGF,
//                awayTotalAvgXGA,
//                awayAwayAvgXGF,
//                awayAwayAvgXGA,
//                awayWeightedTotalXGF,
//                awayWeightedTotalXGA,
//                awayWeightedAwayXGF,
//                awayWeightedAwayXGA,
//                awayTotalPPG,
//                awayAwayPPG,
//                awayLast5TotalPPG,
//                awayLast5AwayPPG);
//
//        ArrayList<Player> homePlayerRatings = lineups.get("home");
//        homePlayerRatings.forEach(player -> {
//            homeTeamThisSeason.addPlayerStats(player.getPlayerName(), player.getOvrMins(), player.getAvgOvrRating(), true);
//        });
//
//        //method will only add stats if they are present.
//        awayTeamThisSeason.addGameStats(awayScore,
//                homeScore,
//                awayXGF,
//                homeXGF,
//                firstScorer == 2,
//                firstScorer != -1,
//                false,
//                homeTotalAvgGoalsFor,
//                homeTotalAvgGoalsAgainst,
//                homeHomeAvgGoalsFor,
//                homeHomeAvgGoalsAgainst,
//                homeTotalAvgXGF,
//                homeTotalAvgXGA,
//                homeHomeAvgXGF,
//                homeHomeAvgXGA,
//                homeWeightedTotalXGF,
//                homeWeightedTotalXGA,
//                homeWeightedHomeXGF,
//                homeWeightedHomeXGA,
//                homeTotalPPG,
//                homeHomePPG,
//                homeLast5TotalPPG,
//                homeLast5HomePPG);
//
//        ArrayList<Player> awayPlayerRatings = lineups.get("away");
//        awayPlayerRatings.forEach(player -> {
//            awayTeamThisSeason.addPlayerStats(player.getPlayerName(), player.getOvrMins(), player.getAvgOvrRating(), false);
//        });
//    }
//
//
//    /*
//     * Method creates and returns a training match. Can be customised to create the training data with all games, or just the homeTeam's
//     * home stats and the awayTeams away stats. Also can customise how many years in the past you want to look at results between 2 teams.
//     *
//     * TODO: check ordering in setting stats is the same as in the method being called.
//     */
//    private static TrainingMatch addDataToTrainingMatch(TrainingTeam homeTeam, TrainingTeam awayTeam, TrainingTeamsSeason homeTeamThisSeason,
//                                                        TrainingTeamsSeason awayTeamThisSeason, HashMap<String, ArrayList<Player>> lineups,
//                                                        int seasonYearStart) throws SQLException {
//        TrainingMatch match = new TrainingMatch();
//
//        GamesSelector HOME_GAMES = GamesSelector.ONLY_HOME_GAMES;
//        GamesSelector ALL_GAMES = GamesSelector.ALL_GAMES;
//        GamesSelector AWAY_GAMES = GamesSelector.ONLY_AWAY_GAMES;
//
//        //players will already be sorted into whoever played the most minutes because of the SQL query ordering, so
//        //to get the 11 players who played most, we can just take the first 11 elements.
//        ArrayList<Player> homePlayers = lineups.get("home");
//        ArrayList<Player> awayPlayers = lineups.get("away");
//
//        ArrayList<String> homeLineup = new ArrayList<>();
//        ArrayList<String> awayLineup = new ArrayList<>();
//        for (int i = 0; i<11; i++) {
//            homeLineup.add(homePlayers.get(i).getPlayerName());
//            try {
//                awayLineup.add(awayPlayers.get(i).getPlayerName());
//            } catch (Exception e) {
//                System.out.println("we have an eerrr");
//            }
//        }
//
//        match.setHomeTeamStats(homeTeam.getTeamName(),
//                homeTeamThisSeason.getAvgGoalsFor(ALL_GAMES),
//                homeTeamThisSeason.getAvgGoalsAgainst(ALL_GAMES),
//                homeTeamThisSeason.getAvgXGF(ALL_GAMES),
//                homeTeamThisSeason.getAvgXGA(ALL_GAMES),
//                homeTeamThisSeason.getWeightedAvgXGF(ALL_GAMES),
//                homeTeamThisSeason.getWeightedAvgXGA(ALL_GAMES),
//                homeTeamThisSeason.getFormGoalsFor(ALL_GAMES),
//                homeTeamThisSeason.getFormGoalsAgainst(ALL_GAMES),
//                homeTeamThisSeason.getFormXGF(ALL_GAMES),
//                homeTeamThisSeason.getFormXGA(ALL_GAMES),
//                homeTeamThisSeason.getFormWeightedXGF(ALL_GAMES),
//                homeTeamThisSeason.getFormWeightedXGA(ALL_GAMES),
//                //8 records in here. Contain Form values, but using usual averages rather than weighted averages.
//                homeTeamThisSeason.getAvgFormGoalsFor(ALL_GAMES, IGNORE_FIRST_N_GAMES),
//                homeTeamThisSeason.getAvgFormGoalsAgainst(ALL_GAMES, IGNORE_FIRST_N_GAMES),
//                homeTeamThisSeason.getAvgFormXGF(ALL_GAMES, IGNORE_FIRST_N_GAMES),
//                homeTeamThisSeason.getAvgFormXGA(ALL_GAMES, IGNORE_FIRST_N_GAMES),
//                homeTeamThisSeason.getAvgFormWeightedXGF(ALL_GAMES, IGNORE_FIRST_N_GAMES),
//                homeTeamThisSeason.getAvgFormWeightedXGA(ALL_GAMES, IGNORE_FIRST_N_GAMES),
//                homeTeamThisSeason.getFormXGFOverLastNGames(ALL_GAMES, LAST_N_GAMES_FORM),
//                homeTeamThisSeason.getFormXGAOverLastNGames(ALL_GAMES, LAST_N_GAMES_FORM),
//
//                homeTeamThisSeason.getAvgPoints(ALL_GAMES),
//                homeTeamThisSeason.getAvgPointsOverLastXGames(ALL_GAMES, LAST_N_GAMES_FORM),
//                homeTeamThisSeason.getAvgPointsWhenScoredFirst(ALL_GAMES),
//                homeTeamThisSeason.getAvgPointsWhenConceededFirst(ALL_GAMES),
//                homeTeam.getPointsOfLastMatchups(awayTeam.getTeamName(), ALL_GAMES, seasonYearStart - NUMB_SEASONS_HISTORY),
//                homeTeamThisSeason.getMinsWeightedLineupRating(ALL_GAMES, homeLineup),
//                homeTeamThisSeason.getGamesWeightedLineupRating(ALL_GAMES, homeLineup),
//                homeTeamThisSeason.getLineupStrength(ALL_GAMES, homeLineup),
//
//                //double homeTeamsOpponentsWholeSeasonPPG, double homeTeamsLast5OpponentsWholeSeasonPPG
//                homeTeamThisSeason.getAvgPointsOfAllOpponentsGamesWholeSeason(ALL_GAMES),
//                homeTeamThisSeason.getAvgPointsOfLastXOpponentsGamesWholeSeason(ALL_GAMES, LAST_N_GAMES_FORM),
//
//                //double homeTeamsOpponentsLast5PPG, double homeTeamLast5OpponentsLast5PPG
//                homeTeamThisSeason.getAvgPointsOfAllOpponentsLast5Games(ALL_GAMES),
//        homeTeamThisSeason.getAvgPointsOfLastXOpponentsLast5Games(ALL_GAMES, LAST_N_GAMES_FORM),
//
//                //double avgCleanSheets, double avgCleanSheetsLast5;
//                homeTeamThisSeason.getAvgNumberOfCleanSheets(ALL_GAMES),
//                homeTeamThisSeason.getAvgNumberOfCleanSheetsLastXGames(ALL_GAMES, LAST_N_GAMES_FORM, true)
//        );
//
//        match.setHomeTeamAtHomeStats(homeTeamThisSeason.getAvgGoalsFor(HOME_GAMES),
//                homeTeamThisSeason.getAvgGoalsAgainst(HOME_GAMES),
//                homeTeamThisSeason.getAvgXGF(HOME_GAMES),
//                homeTeamThisSeason.getAvgXGA(HOME_GAMES),
//                homeTeamThisSeason.getWeightedAvgXGF(HOME_GAMES),
//                homeTeamThisSeason.getWeightedAvgXGA(HOME_GAMES),
//                homeTeamThisSeason.getFormGoalsFor(HOME_GAMES),
//                homeTeamThisSeason.getFormGoalsAgainst(HOME_GAMES),
//                homeTeamThisSeason.getFormXGF(HOME_GAMES),
//                homeTeamThisSeason.getFormXGA(HOME_GAMES),
//                homeTeamThisSeason.getFormWeightedXGF(HOME_GAMES),
//                homeTeamThisSeason.getFormWeightedXGA(HOME_GAMES),
//
//                homeTeamThisSeason.getAvgFormGoalsFor(HOME_GAMES, IGNORE_FIRST_N_GAMES),
//                homeTeamThisSeason.getAvgFormGoalsAgainst(HOME_GAMES, IGNORE_FIRST_N_GAMES),
//                homeTeamThisSeason.getAvgFormXGF(HOME_GAMES, IGNORE_FIRST_N_GAMES),
//                homeTeamThisSeason.getAvgFormXGA(HOME_GAMES, IGNORE_FIRST_N_GAMES),
//                homeTeamThisSeason.getAvgFormWeightedXGF(HOME_GAMES, IGNORE_FIRST_N_GAMES),
//                homeTeamThisSeason.getAvgFormWeightedXGA(HOME_GAMES, IGNORE_FIRST_N_GAMES),
//                homeTeamThisSeason.getFormXGFOverLastNGames(HOME_GAMES, LAST_N_GAMES_FORM),
//                homeTeamThisSeason.getFormXGAOverLastNGames(HOME_GAMES, LAST_N_GAMES_FORM),
//
//                homeTeamThisSeason.getAvgPoints(HOME_GAMES),
//                homeTeamThisSeason.getAvgPointsOverLastXGames(HOME_GAMES, LAST_N_GAMES_FORM),
//                homeTeamThisSeason.getAvgPointsWhenScoredFirst(HOME_GAMES),
//                homeTeamThisSeason.getAvgPointsWhenConceededFirst(HOME_GAMES),
//                homeTeam.getPointsOfLastMatchups(awayTeam.getTeamName(), HOME_GAMES, seasonYearStart - NUMB_SEASONS_HISTORY),
//                homeTeamThisSeason.getMinsWeightedLineupRating(HOME_GAMES, homeLineup),
//                homeTeamThisSeason.getGamesWeightedLineupRating(HOME_GAMES, homeLineup),
//                homeTeamThisSeason.getLineupStrength(HOME_GAMES, homeLineup),
//
//                homeTeamThisSeason.getAvgPointsOfAllOpponentsGamesWholeSeason(HOME_GAMES),
//                homeTeamThisSeason.getAvgPointsOfLastXOpponentsGamesWholeSeason(HOME_GAMES, LAST_N_GAMES_FORM),
//
//                homeTeamThisSeason.getAvgPointsOfAllOpponentsLast5Games(HOME_GAMES),
//                homeTeamThisSeason.getAvgPointsOfLastXOpponentsLast5Games(HOME_GAMES, LAST_N_GAMES_FORM),
//
//                homeTeamThisSeason.getAvgNumberOfCleanSheets(HOME_GAMES),
//                homeTeamThisSeason.getAvgNumberOfCleanSheetsLastXGames(HOME_GAMES, LAST_N_GAMES_FORM, true));
//
//        match.setAwayTeamStats(awayTeam.getTeamName(),
//                awayTeamThisSeason.getAvgGoalsFor(ALL_GAMES),
//                awayTeamThisSeason.getAvgGoalsAgainst(ALL_GAMES),
//                awayTeamThisSeason.getAvgXGF(ALL_GAMES),
//                awayTeamThisSeason.getAvgXGA(ALL_GAMES),
//                awayTeamThisSeason.getWeightedAvgXGF(ALL_GAMES),
//                awayTeamThisSeason.getWeightedAvgXGA(ALL_GAMES),
//                awayTeamThisSeason.getFormGoalsFor(ALL_GAMES),
//                awayTeamThisSeason.getFormGoalsAgainst(ALL_GAMES),
//                awayTeamThisSeason.getFormXGF(ALL_GAMES),
//                awayTeamThisSeason.getFormXGA(ALL_GAMES),
//                awayTeamThisSeason.getFormWeightedXGF(ALL_GAMES),
//                awayTeamThisSeason.getFormWeightedXGA(ALL_GAMES),
//
//                awayTeamThisSeason.getAvgFormGoalsFor(ALL_GAMES, IGNORE_FIRST_N_GAMES),
//                awayTeamThisSeason.getAvgFormGoalsAgainst(ALL_GAMES, IGNORE_FIRST_N_GAMES),
//                awayTeamThisSeason.getAvgFormXGF(ALL_GAMES, IGNORE_FIRST_N_GAMES),
//                awayTeamThisSeason.getAvgFormXGA(ALL_GAMES, IGNORE_FIRST_N_GAMES),
//                awayTeamThisSeason.getAvgFormWeightedXGF(ALL_GAMES, IGNORE_FIRST_N_GAMES),
//                awayTeamThisSeason.getAvgFormWeightedXGA(ALL_GAMES, IGNORE_FIRST_N_GAMES),
//                awayTeamThisSeason.getFormXGFOverLastNGames(ALL_GAMES, LAST_N_GAMES_FORM),
//                awayTeamThisSeason.getFormXGAOverLastNGames(ALL_GAMES, LAST_N_GAMES_FORM),
//
//                awayTeamThisSeason.getAvgPoints(ALL_GAMES),
//                awayTeamThisSeason.getAvgPointsOverLastXGames(ALL_GAMES, LAST_N_GAMES_FORM),
//                awayTeamThisSeason.getAvgPointsWhenScoredFirst(ALL_GAMES),
//                awayTeamThisSeason.getAvgPointsWhenConceededFirst(ALL_GAMES),
//                awayTeam.getPointsOfLastMatchups(homeTeam.getTeamName(), ALL_GAMES, seasonYearStart - NUMB_SEASONS_HISTORY),
//                awayTeamThisSeason.getMinsWeightedLineupRating(ALL_GAMES, awayLineup),
//                awayTeamThisSeason.getGamesWeightedLineupRating(ALL_GAMES, awayLineup),
//                awayTeamThisSeason.getLineupStrength(ALL_GAMES, awayLineup),
//
//                awayTeamThisSeason.getAvgPointsOfAllOpponentsGamesWholeSeason(ALL_GAMES),
//                awayTeamThisSeason.getAvgPointsOfLastXOpponentsGamesWholeSeason(ALL_GAMES, LAST_N_GAMES_FORM),
//
//                awayTeamThisSeason.getAvgPointsOfAllOpponentsLast5Games(ALL_GAMES),
//                awayTeamThisSeason.getAvgPointsOfLastXOpponentsLast5Games(ALL_GAMES, LAST_N_GAMES_FORM),
//
//                awayTeamThisSeason.getAvgNumberOfCleanSheets(ALL_GAMES),
//                awayTeamThisSeason.getAvgNumberOfCleanSheetsLastXGames(ALL_GAMES, LAST_N_GAMES_FORM, true));
//
//        match.setAwayTeamAtAwayStats(awayTeamThisSeason.getAvgGoalsFor(AWAY_GAMES),
//                awayTeamThisSeason.getAvgGoalsAgainst(AWAY_GAMES),
//                awayTeamThisSeason.getAvgXGF(AWAY_GAMES),
//                awayTeamThisSeason.getAvgXGA(AWAY_GAMES),
//                awayTeamThisSeason.getWeightedAvgXGF(AWAY_GAMES),
//                awayTeamThisSeason.getWeightedAvgXGA(AWAY_GAMES),
//                awayTeamThisSeason.getFormGoalsFor(AWAY_GAMES),
//                awayTeamThisSeason.getFormGoalsAgainst(AWAY_GAMES),
//                awayTeamThisSeason.getFormXGF(AWAY_GAMES),
//                awayTeamThisSeason.getFormXGA(AWAY_GAMES),
//                awayTeamThisSeason.getFormWeightedXGF(AWAY_GAMES),
//                awayTeamThisSeason.getFormWeightedXGA(AWAY_GAMES),
//
//                awayTeamThisSeason.getAvgFormGoalsFor(AWAY_GAMES, IGNORE_FIRST_N_GAMES),
//                awayTeamThisSeason.getAvgFormGoalsAgainst(AWAY_GAMES, IGNORE_FIRST_N_GAMES),
//                awayTeamThisSeason.getAvgFormXGF(AWAY_GAMES, IGNORE_FIRST_N_GAMES),
//                awayTeamThisSeason.getAvgFormXGA(AWAY_GAMES, IGNORE_FIRST_N_GAMES),
//                awayTeamThisSeason.getAvgFormWeightedXGF(AWAY_GAMES, IGNORE_FIRST_N_GAMES),
//                awayTeamThisSeason.getAvgFormWeightedXGA(AWAY_GAMES, IGNORE_FIRST_N_GAMES),
//                awayTeamThisSeason.getFormXGFOverLastNGames(AWAY_GAMES, LAST_N_GAMES_FORM),
//                awayTeamThisSeason.getFormXGAOverLastNGames(AWAY_GAMES, LAST_N_GAMES_FORM),
//
//                awayTeamThisSeason.getAvgPoints(AWAY_GAMES),
//                awayTeamThisSeason.getAvgPointsOverLastXGames(AWAY_GAMES, LAST_N_GAMES_FORM),
//                awayTeamThisSeason.getAvgPointsWhenScoredFirst(AWAY_GAMES),
//                awayTeamThisSeason.getAvgPointsWhenConceededFirst(AWAY_GAMES),
//                awayTeam.getPointsOfLastMatchups(homeTeam.getTeamName(), AWAY_GAMES, seasonYearStart - NUMB_SEASONS_HISTORY),
//                awayTeamThisSeason.getMinsWeightedLineupRating(AWAY_GAMES, awayLineup),
//                awayTeamThisSeason.getGamesWeightedLineupRating(AWAY_GAMES, awayLineup),
//                awayTeamThisSeason.getLineupStrength(AWAY_GAMES, awayLineup),
//
//                awayTeamThisSeason.getAvgPointsOfAllOpponentsGamesWholeSeason(AWAY_GAMES),
//                awayTeamThisSeason.getAvgPointsOfLastXOpponentsGamesWholeSeason(AWAY_GAMES, LAST_N_GAMES_FORM),
//
//                awayTeamThisSeason.getAvgPointsOfAllOpponentsLast5Games(AWAY_GAMES),
//                awayTeamThisSeason.getAvgPointsOfLastXOpponentsLast5Games(AWAY_GAMES, LAST_N_GAMES_FORM),
//
//                awayTeamThisSeason.getAvgNumberOfCleanSheets(AWAY_GAMES),
//                awayTeamThisSeason.getAvgNumberOfCleanSheetsLastXGames(AWAY_GAMES, LAST_N_GAMES_FORM, true));
//
//        match.setMiscStats(calcProbabilityFromOdds(homeWinOdds),
//                calcProbabilityFromOdds(awayWinOdds),
//                calcProbabilityFromOdds(drawOdds),
//                homeScore,
//                awayScore,
//                kickoffTime,
//                homeTeamThisSeason.getNumbGamesPlayed(ALL_GAMES),
//                awayTeamThisSeason.getNumbGamesPlayed(ALL_GAMES),
//                lastRecordSeasonYear);
//
//        return match;
//    }
//
//
//    public static void addFeaturesToMatchesToPredict(ArrayList<MatchToPredict> matches) {
//        if (leaguesOfTeams.size() == 0) loadInDataFromDb();
//
//        for (MatchToPredict match: matches) {
//
//            //get TrainingTeam and TrainingTeamsSeason for both teams
//            TrainingTeam homeTeam = getTeam(match.getLeagueName(), match.getHomeTeamName());
//            TrainingTeam awayTeam = getTeam(match.getLeagueName(), match.getAwayTeamName());
//
//            TrainingTeamsSeason homeSeason = getTeamsCurrentSeason(homeTeam, match.getSeasonKey());
//            TrainingTeamsSeason awaySeason = getTeamsCurrentSeason(awayTeam, match.getSeasonKey());
//
//
//            //format lineups
//            ArrayList<String> homeLineup = match.getHomeTeamPlayers();
//            ArrayList<String> awayLineup = match.getAwayTeamPlayers();
//            if (homeLineup == null || awayLineup == null) throw new RuntimeException("Lineups not set! " + match.getHomeTeamName() + " vs " + match.getAwayTeamName());
//            if (homeLineup.size() != 11 || awayLineup.size() != 11) throw new RuntimeException("Trying to predict on a match without proper lineup size. " +
//                    match.getHomeTeamName() + " vs " + match.getAwayTeamName());
//
//
//            //create features
//            ArrayList<Double> features = createFeatures(homeTeam, awayTeam, homeSeason, awaySeason, homeLineup, awayLineup, match.getSeasonYearStart());
//
//            match.setFeatures(features);
//        }
//
//    }
//
//
//    private static ArrayList<Double> createFeatures(TrainingTeam homeTeam, TrainingTeam awayTeam, TrainingTeamsSeason homeSeason, TrainingTeamsSeason awaySeason,
//                                       ArrayList<String> homeLineup, ArrayList<String> awayLineup, int seasonYearStart) {
//
//        GamesSelector HOME_GAMES = GamesSelector.ONLY_HOME_GAMES;
//        GamesSelector ALL_GAMES = GamesSelector.ALL_GAMES;
//        GamesSelector AWAY_GAMES = GamesSelector.ONLY_AWAY_GAMES;
//
//
//        if (homeLineup == null || awayLineup == null) throw new RuntimeException("Lineups not set!");
//        if (homeLineup.size() != 11 || awayLineup.size() != 11) throw new RuntimeException("Trying to predict on a played match without proper lineup size. " +
//                homeTeam.getTeamName() + " vs " + awayTeam.getTeamName());
//
//
//        //create features
//        ArrayList<Double> features = new ArrayList<>();
//
//        features.add(1d); //adding bias parameter of 1
//
//        //home total stats
//        features.add(homeSeason.getAvgGoalsFor(ALL_GAMES));
//        features.add(homeSeason.getAvgGoalsAgainst(ALL_GAMES));
//        features.add(homeSeason.getAvgXGF(ALL_GAMES));
//        features.add(homeSeason.getAvgXGA(ALL_GAMES));
//        features.add(homeSeason.getWeightedAvgXGF(ALL_GAMES));
//        features.add(homeSeason.getWeightedAvgXGA(ALL_GAMES));
//        features.add(homeSeason.getAvgPoints(ALL_GAMES));
//        features.add(homeSeason.getAvgPointsOverLastXGames(ALL_GAMES, LAST_N_GAMES_FORM));
//        features.add(homeSeason.getAvgPointsWhenScoredFirst(ALL_GAMES));
//        features.add(homeSeason.getAvgPointsWhenConceededFirst(ALL_GAMES));
//        features.add(homeTeam.getPointsOfLastMatchups(awayTeam.getTeamName(), ALL_GAMES, seasonYearStart - NUMB_SEASONS_HISTORY));
//        features.add(homeSeason.getMinsWeightedLineupRating(ALL_GAMES, homeLineup));
//        features.add(homeSeason.getLineupStrength(ALL_GAMES, homeLineup));
//
//        //home at home stats
//        features.add(homeSeason.getAvgGoalsFor(HOME_GAMES));
//        features.add(homeSeason.getAvgGoalsAgainst(HOME_GAMES));
//        features.add(homeSeason.getAvgXGF(HOME_GAMES));
//        features.add(homeSeason.getAvgXGA(HOME_GAMES));
//        features.add(homeSeason.getWeightedAvgXGF(HOME_GAMES));
//        features.add(homeSeason.getWeightedAvgXGA(HOME_GAMES));
//        features.add(homeSeason.getAvgPoints(HOME_GAMES));
//        features.add(homeSeason.getAvgPointsOverLastXGames(HOME_GAMES, LAST_N_GAMES_FORM));
//        features.add(homeSeason.getAvgPointsWhenScoredFirst(HOME_GAMES));
//        features.add(homeSeason.getAvgPointsWhenConceededFirst(HOME_GAMES));
//        features.add(homeTeam.getPointsOfLastMatchups(awayTeam.getTeamName(), HOME_GAMES, seasonYearStart - NUMB_SEASONS_HISTORY));
//        features.add(homeSeason.getMinsWeightedLineupRating(HOME_GAMES, homeLineup));
//        features.add(homeSeason.getLineupStrength(HOME_GAMES, homeLineup));
//
//        //away total stats
//        features.add(awaySeason.getAvgGoalsFor(ALL_GAMES));
//        features.add(awaySeason.getAvgGoalsAgainst(ALL_GAMES));
//        features.add(awaySeason.getAvgXGF(ALL_GAMES));
//        features.add(awaySeason.getAvgXGA(ALL_GAMES));
//        features.add(awaySeason.getWeightedAvgXGF(ALL_GAMES));
//        features.add(awaySeason.getWeightedAvgXGA(ALL_GAMES));
//        features.add(awaySeason.getAvgPoints(ALL_GAMES));
//        features.add(awaySeason.getAvgPointsOverLastXGames(ALL_GAMES, LAST_N_GAMES_FORM));
//        features.add(awaySeason.getAvgPointsWhenScoredFirst(ALL_GAMES));
//        features.add(awaySeason.getAvgPointsWhenConceededFirst(ALL_GAMES));
//        features.add(awayTeam.getPointsOfLastMatchups(awayTeam.getTeamName(), ALL_GAMES, seasonYearStart - NUMB_SEASONS_HISTORY));
//        features.add(awaySeason.getMinsWeightedLineupRating(ALL_GAMES, homeLineup));
//        features.add(awaySeason.getLineupStrength(ALL_GAMES, homeLineup));
//
//        //away at away stats
//        features.add(awaySeason.getAvgGoalsFor(AWAY_GAMES));
//        features.add(awaySeason.getAvgGoalsAgainst(AWAY_GAMES));
//        features.add(awaySeason.getAvgXGF(AWAY_GAMES));
//        features.add(awaySeason.getAvgXGA(AWAY_GAMES));
//        features.add(awaySeason.getWeightedAvgXGF(AWAY_GAMES));
//        features.add(awaySeason.getWeightedAvgXGA(AWAY_GAMES));
//        features.add(awaySeason.getAvgPoints(AWAY_GAMES));
//        features.add(awaySeason.getAvgPointsOverLastXGames(AWAY_GAMES, LAST_N_GAMES_FORM));
//        features.add(awaySeason.getAvgPointsWhenScoredFirst(AWAY_GAMES));
//        features.add(awaySeason.getAvgPointsWhenConceededFirst(AWAY_GAMES));
//        features.add(awayTeam.getPointsOfLastMatchups(awayTeam.getTeamName(), AWAY_GAMES, seasonYearStart - NUMB_SEASONS_HISTORY));
//        features.add(awaySeason.getMinsWeightedLineupRating(AWAY_GAMES, homeLineup));
//        features.add(awaySeason.getLineupStrength(AWAY_GAMES, homeLineup)); //52 normal stats
//
//
//        //extra home stats
//        features.add(homeSeason.getAvgFormGoalsFor(ALL_GAMES, IGNORE_FIRST_N_GAMES));
//        features.add(homeSeason.getAvgFormGoalsAgainst(ALL_GAMES, IGNORE_FIRST_N_GAMES));
//        features.add(homeSeason.getAvgFormXGF(ALL_GAMES, IGNORE_FIRST_N_GAMES));
//        features.add(homeSeason.getAvgFormXGA(ALL_GAMES, IGNORE_FIRST_N_GAMES));
//        features.add(homeSeason.getAvgFormWeightedXGF(ALL_GAMES, IGNORE_FIRST_N_GAMES));
//        features.add(homeSeason.getAvgFormWeightedXGA(ALL_GAMES, IGNORE_FIRST_N_GAMES));
//        features.add(homeSeason.getFormXGFOverLastNGames(ALL_GAMES, LAST_N_GAMES_FORM));
//        features.add(homeSeason.getFormXGAOverLastNGames(ALL_GAMES, LAST_N_GAMES_FORM));
//
//
//        //extra home at home stats
//        features.add(homeSeason.getAvgFormGoalsFor(HOME_GAMES, IGNORE_FIRST_N_GAMES));
//        features.add(homeSeason.getAvgFormGoalsAgainst(HOME_GAMES, IGNORE_FIRST_N_GAMES));
//        features.add(homeSeason.getAvgFormXGF(HOME_GAMES, IGNORE_FIRST_N_GAMES));
//        features.add(homeSeason.getAvgFormXGA(HOME_GAMES, IGNORE_FIRST_N_GAMES));
//        features.add(homeSeason.getAvgFormWeightedXGF(HOME_GAMES, IGNORE_FIRST_N_GAMES));
//        features.add(homeSeason.getAvgFormWeightedXGA(HOME_GAMES, IGNORE_FIRST_N_GAMES));
//        features.add(homeSeason.getFormXGFOverLastNGames(HOME_GAMES, LAST_N_GAMES_FORM));
//        features.add(homeSeason.getFormXGAOverLastNGames(HOME_GAMES, LAST_N_GAMES_FORM));
//
//
//        //extra away stats
//        features.add(awaySeason.getAvgFormGoalsFor(ALL_GAMES, IGNORE_FIRST_N_GAMES));
//        features.add(awaySeason.getAvgFormGoalsAgainst(ALL_GAMES, IGNORE_FIRST_N_GAMES));
//        features.add(awaySeason.getAvgFormXGF(ALL_GAMES, IGNORE_FIRST_N_GAMES));
//        features.add(awaySeason.getAvgFormXGA(ALL_GAMES, IGNORE_FIRST_N_GAMES));
//        features.add(awaySeason.getAvgFormWeightedXGF(ALL_GAMES, IGNORE_FIRST_N_GAMES));
//        features.add(awaySeason.getAvgFormWeightedXGA(ALL_GAMES, IGNORE_FIRST_N_GAMES));
//        features.add(awaySeason.getFormXGFOverLastNGames(ALL_GAMES, LAST_N_GAMES_FORM));
//        features.add(awaySeason.getFormXGAOverLastNGames(ALL_GAMES, LAST_N_GAMES_FORM));
//
//
//        //extra away stats
//        features.add(awaySeason.getAvgFormGoalsFor(AWAY_GAMES, IGNORE_FIRST_N_GAMES));
//        features.add(awaySeason.getAvgFormGoalsAgainst(AWAY_GAMES, IGNORE_FIRST_N_GAMES));
//        features.add(awaySeason.getAvgFormXGF(AWAY_GAMES, IGNORE_FIRST_N_GAMES));
//        features.add(awaySeason.getAvgFormXGA(AWAY_GAMES, IGNORE_FIRST_N_GAMES));
//        features.add(awaySeason.getAvgFormWeightedXGF(AWAY_GAMES, IGNORE_FIRST_N_GAMES));
//        features.add(awaySeason.getAvgFormWeightedXGA(AWAY_GAMES, IGNORE_FIRST_N_GAMES));
//        features.add(awaySeason.getFormXGFOverLastNGames(AWAY_GAMES, LAST_N_GAMES_FORM));
//        features.add(awaySeason.getFormXGAOverLastNGames(AWAY_GAMES, LAST_N_GAMES_FORM)); //32 extra stats. total 85 features (inc bias).
//
//
//        return features;
//    }
//
//    /*
//     * Best to keep this method within this class as it needs access to all inner fields.
//     *
//     * Method first gets the data out of the database and creates Training Matches for those games. (Have to create a whole history of TrainingData because we take history from previous
//     * seasons into account.)
//     * Then we get the current stats from those teams (created by creating the Training Matches, which adds stats to each team at the same time) and create the features.
//     */
//    public static void addLegacyFeaturesToMatchesToPredict(ArrayList<MatchToPredict> matches) {
//
//        if (leaguesOfTeams.size() == 0) loadInDataFromDb();
//
//        GamesSelector HOME_GAMES = GamesSelector.ONLY_HOME_GAMES;
//        GamesSelector ALL_GAMES = GamesSelector.ALL_GAMES;
//        GamesSelector AWAY_GAMES = GamesSelector.ONLY_AWAY_GAMES;
//
//        for (MatchToPredict match: matches) {
//
//            //get TrainingTeam and TrainingTeam this season for both teams
//            TrainingTeam homeTeam = getTeam(match.getLeagueName(), match.getHomeTeamName());
//            TrainingTeam awayTeam = getTeam(match.getLeagueName(), match.getAwayTeamName());
//
//            TrainingTeamsSeason homeSeason = getTeamsCurrentSeason(homeTeam, match.getSeasonKey());
//            TrainingTeamsSeason awaySeason = getTeamsCurrentSeason(awayTeam, match.getSeasonKey());
//
//
//            //format lineups
//            ArrayList<String> homeLineup = match.getHomeTeamPlayers();
//            ArrayList<String> awayLineup = match.getAwayTeamPlayers();
//            if (homeLineup == null || awayLineup == null) throw new RuntimeException("Lineups not set!");
//            if (homeLineup.size() != 11 || awayLineup.size() != 11) throw new RuntimeException("Trying to predict on a match without proper lineup size. " +
//                                                                                                match.getHomeTeamName() + " vs " + match.getAwayTeamName());
//
//
//            //create features
//            ArrayList<Double> features = new ArrayList<>();
//            int seasonYearStart = match.getSeasonYearStart();
//
//            features.add(1d); //adding bias parameter of 1
//
//            features.add(homeSeason.getAvgGoalsFor(ALL_GAMES));
//            features.add(homeSeason.getAvgGoalsAgainst(ALL_GAMES));
//            features.add(homeSeason.getAvgXGF(ALL_GAMES));
//            features.add(homeSeason.getAvgXGA(ALL_GAMES));
//            features.add(homeSeason.getWeightedAvgXGF(ALL_GAMES));
//            features.add(homeSeason.getWeightedAvgXGA(ALL_GAMES));
//            features.add(homeSeason.getAvgPoints(ALL_GAMES));
//            features.add(homeSeason.getAvgPointsOverLastXGames(ALL_GAMES, 5));
//            features.add(homeSeason.getAvgPointsWhenScoredFirst(ALL_GAMES));
//            features.add(homeSeason.getAvgPointsWhenConceededFirst(ALL_GAMES));
//            features.add(homeTeam.getPointsOfLastMatchups(awayTeam.getTeamName(), ALL_GAMES, seasonYearStart - NUMB_SEASONS_HISTORY));
//            features.add(homeSeason.getMinsWeightedLineupRating(ALL_GAMES, homeLineup));
//            features.add(homeSeason.getLineupStrength(ALL_GAMES, homeLineup));
//
//            features.add(homeSeason.getAvgGoalsFor(HOME_GAMES));
//            features.add(homeSeason.getAvgGoalsAgainst(HOME_GAMES));
//            features.add(homeSeason.getAvgXGF(HOME_GAMES));
//            features.add(homeSeason.getAvgXGA(HOME_GAMES));
//            features.add(homeSeason.getWeightedAvgXGF(HOME_GAMES));
//            features.add(homeSeason.getWeightedAvgXGA(HOME_GAMES));
//            features.add(homeSeason.getAvgPoints(HOME_GAMES));
//            features.add(homeSeason.getAvgPointsOverLastXGames(HOME_GAMES, 5));
//            features.add(homeSeason.getAvgPointsWhenScoredFirst(HOME_GAMES));
//            features.add(homeSeason.getAvgPointsWhenConceededFirst(HOME_GAMES));
//            features.add(homeTeam.getPointsOfLastMatchups(awayTeam.getTeamName(), HOME_GAMES, seasonYearStart - NUMB_SEASONS_HISTORY));
//            features.add(homeSeason.getMinsWeightedLineupRating(HOME_GAMES, homeLineup));
//            features.add(homeSeason.getLineupStrength(HOME_GAMES, homeLineup));
//
//            features.add(awaySeason.getAvgGoalsFor(ALL_GAMES));
//            features.add(awaySeason.getAvgGoalsAgainst(ALL_GAMES));
//            features.add(awaySeason.getAvgXGF(ALL_GAMES));
//            features.add(awaySeason.getAvgXGA(ALL_GAMES));
//            features.add(awaySeason.getWeightedAvgXGF(ALL_GAMES));
//            features.add(awaySeason.getWeightedAvgXGA(ALL_GAMES));
//            features.add(awaySeason.getAvgPoints(ALL_GAMES));
//            features.add(awaySeason.getAvgPointsOverLastXGames(ALL_GAMES, 5));
//            features.add(awaySeason.getAvgPointsWhenScoredFirst(ALL_GAMES));
//            features.add(awaySeason.getAvgPointsWhenConceededFirst(ALL_GAMES));
//            features.add(awayTeam.getPointsOfLastMatchups(awayTeam.getTeamName(), ALL_GAMES, seasonYearStart - NUMB_SEASONS_HISTORY));
//            features.add(awaySeason.getMinsWeightedLineupRating(ALL_GAMES, homeLineup));
//            features.add(awaySeason.getLineupStrength(ALL_GAMES, homeLineup));
//
//            features.add(awaySeason.getAvgGoalsFor(AWAY_GAMES));
//            features.add(awaySeason.getAvgGoalsAgainst(AWAY_GAMES));
//            features.add(awaySeason.getAvgXGF(AWAY_GAMES));
//            features.add(awaySeason.getAvgXGA(AWAY_GAMES));
//            features.add(awaySeason.getWeightedAvgXGF(AWAY_GAMES));
//            features.add(awaySeason.getWeightedAvgXGA(AWAY_GAMES));
//            features.add(awaySeason.getAvgPoints(AWAY_GAMES));
//            features.add(awaySeason.getAvgPointsOverLastXGames(AWAY_GAMES, 5));
//            features.add(awaySeason.getAvgPointsWhenScoredFirst(AWAY_GAMES));
//            features.add(awaySeason.getAvgPointsWhenConceededFirst(AWAY_GAMES));
//            features.add(awayTeam.getPointsOfLastMatchups(awayTeam.getTeamName(), AWAY_GAMES, seasonYearStart - NUMB_SEASONS_HISTORY));
//            features.add(awaySeason.getMinsWeightedLineupRating(AWAY_GAMES, homeLineup));
//            features.add(awaySeason.getLineupStrength(AWAY_GAMES, homeLineup));
//
//            features.add(homeSeason.getWeightedAvgXGF(ALL_GAMES) - awaySeason.getWeightedAvgXGA(ALL_GAMES));
//            features.add(awaySeason.getWeightedAvgXGF(ALL_GAMES) - homeSeason.getWeightedAvgXGA(ALL_GAMES));
//            features.add(homeSeason.getWeightedAvgXGF(HOME_GAMES) - awaySeason.getWeightedAvgXGA(AWAY_GAMES));
//            features.add(awaySeason.getWeightedAvgXGF(AWAY_GAMES) - homeSeason.getWeightedAvgXGA(HOME_GAMES));
//
//            double[] toCombine = new double[]{homeSeason.getAvgGoalsFor(ALL_GAMES), awaySeason.getMinsWeightedLineupRating(ALL_GAMES, awayLineup),
//                    awaySeason.getAvgXGF(ALL_GAMES), awaySeason.getAvgPoints(AWAY_GAMES), homeSeason.getAvgXGF(ALL_GAMES),
//            homeSeason.getAvgGoalsFor(HOME_GAMES), homeSeason.getMinsWeightedLineupRating(ALL_GAMES, homeLineup), awaySeason.getAvgXGA(ALL_GAMES),
//            homeSeason.getAvgPoints(HOME_GAMES), homeSeason.getWeightedAvgXGA(ALL_GAMES),
//            awaySeason.getWeightedAvgXGF(AWAY_GAMES), awaySeason.getAvgGoalsFor(ALL_GAMES), homeSeason.getAvgXGF(HOME_GAMES)};
//
//            ArrayList<Double> newFeatures = WriteTrainingData.combineFeatures(toCombine);
//            for (Double d: newFeatures) {
//                features.add(d); //kept in for loop as unsure whether bulk addAll call maintains order of elements.
//            }
//
//            match.setFeatures(features);
//        }
//
//    }
//
//    private static TrainingTeam getTeam(String league, String teamName) {
//        HashMap<String, TrainingTeam> teamsInLeague = leaguesOfTeams.get(league);
//        TrainingTeam targetTeam = teamsInLeague.getOrDefault(teamName, null);
//
//        if (targetTeam == null) throw new RuntimeException("Could not find the team " + teamName + " in league " + league);
//        return targetTeam;
//    }
//
//    private static TrainingTeamsSeason getTeamsCurrentSeason(TrainingTeam team, String seasonYear) {
//        TrainingTeamsSeason season = team.getTeamsSeason(seasonYear);
//        if (season.getNumbGamesPlayed() == 0) throw new RuntimeException("We're requesting a season that has not yet been played. Perhaps our seasonYear formats are different.");
//
//        return season;
//    }
//
//
//    public static void setGamesNeedPredictingAfterDate(Date gamesNeedPredictingAfterDate) {
//        GetMatchesFromDb.gamesNeedPredictingAfterDate = gamesNeedPredictingAfterDate;
//    }
//
//    /*
//     * Method creates a copy within the class as we will be removing matches once we've found them when we create our data.
//     * This will help to reduce the number of times we have to loop through the array (we will be looking through the array whenever we come across a new match after the date
//     * in gamesNeedPredictingAfterDate)
//     */
//    public static void setMissedGamesThatNeedPredicting(ArrayList<MatchToPredict> missedGamesThatNeedPredicting) {
//        GetMatchesFromDb.missedGamesThatNeedPredicting = new ArrayList<>(missedGamesThatNeedPredicting);
//    }
//
//    public static ArrayList<TrainingMatch> getTrainingData() {
//        return trainingData;
//    }
//
//    /*
//     * Takes in a decimal odd and returns a probability.
//     *
//     * Note that probabilities added together will come to more than 100% so the betting companies make their
//     * profit. It usually amounts to 5 or 6%.
//     */
//    private static double calcProbabilityFromOdds(double odds) {
//        return 1/odds;
//    }
//
//    private static final double ALPHA = 0.8;
//    public static double calcExponWeightedAvg(double currAvg, double newEntry) {
//        return ALPHA * currAvg + (1-ALPHA)*newEntry;
//    }
}