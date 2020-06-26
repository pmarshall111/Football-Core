package com.petermarshall.machineLearning.createData;

import com.petermarshall.database.datasource.DS_Get;
import com.petermarshall.machineLearning.createData.classes.*;
import com.petermarshall.scrape.classes.LeagueIdsAndData;

import java.util.ArrayList;
import java.util.HashMap;

//class to be used to create csv files to learn from and update cache in db for past seasons.
public class CalcPastStats {
    //constants to use when calculating a teams form
    public static final int NUMB_SEASONS_HISTORY = 2;
    public static final int NUMB_MATCHES_BEFORE_VALID_TRAINING_DATA = 7; //NOTE: if changed to > 7, Get.canGetOutNewMatchesToPredict test needs more games added
    public static final int COMPARE_LAST_N_GAMES = 5;

    //will do it for all leagues in db.
    public static ArrayList<TrainingMatch> getAllTrainingMatches() {
        LeagueIdsAndData[] leagues = LeagueIdsAndData.values();
        ArrayList<TrainingMatch> allMatches = new ArrayList<>();
        for (LeagueIdsAndData league : leagues) {
            ArrayList<PlayerMatchDbData> pmdbData = DS_Get.getLeagueData(league);
            allMatches.addAll(createLeaguesMatches(pmdbData, new HashMap<>(), true));
        }
        return allMatches;
    }

    /*
     * Adds features in a slightly different way to those added to TrainingMatches to reduce computation time.
     * For the TrainingMatches, all the data from the league including historic seasons is used from the database, but
     * for predicting games, only the current season is useful along with a record of historic results between the 2 teams.
     *
     * If run in test suite, it doesn't save the results of the last match to the team stats, so a prediction can be made
     * with the team stats the way they were before the match happened.
     */
    public static void addFeaturesToPredict(ArrayList<MatchToPredict> matches, boolean isRunInTestSuite) {
        //first need to know which leagues we need data for
        HashMap<String, ArrayList<MatchToPredict>> leagueMatches = new HashMap<>();
        for (MatchToPredict m : matches) {
            leagueMatches.putIfAbsent(m.getLeagueName(), new ArrayList<>());
            leagueMatches.get(m.getLeagueName()).add(m);
        }

        //gets the current seasons matches to create stats for each teams season thus far
        //also gets previous matches in past seasons from the database
        leagueMatches.forEach((league, toPredicts) -> {
            ArrayList<PlayerMatchDbData> playerRatings = DS_Get.getLeagueData(league, matches.get(0).getSeasonYearStart());
            ArrayList<HistoricMatchDbData> pastMatches = DS_Get.getMatchesBetweenTeams(league, matches);
            HashMap<String, TrainingTeam> teamsInLeague = createHistoricMatchups(pastMatches);
            boolean saveLastMatch = isRunInTestSuite ? false : true;
            createLeaguesMatches(playerRatings, teamsInLeague, saveLastMatch);
            toPredicts.forEach(mtp -> {
                int currSeason = mtp.getSeasonYearStart();
                TrainingTeam homeTeam = teamsInLeague.get(mtp.getHomeTeamName());
                TrainingTeam awayTeam = teamsInLeague.get(mtp.getAwayTeamName());
                TrainingTeamsSeason homeSeason = homeTeam.getTeamsSeason(currSeason);
                TrainingTeamsSeason awaySeason = awayTeam.getTeamsSeason(currSeason);
                if (mtp.getHomeTeamPlayers() != null && mtp.getHomeTeamPlayers().size() == 11 &&
                        mtp.getAwayTeamPlayers() != null && mtp.getAwayTeamPlayers().size() == 11) {
                    ArrayList<Double> features = CreateFeatures.getNewFeatures(homeTeam, homeSeason, awayTeam, awaySeason,
                            mtp.getHomeTeamPlayers(), mtp.getAwayTeamPlayers(),
                            currSeason, -1);
                    mtp.setFeatures(features, true);
                }
                ArrayList<Double> featuresNoLineups = CreateFeatures.getNewFeaturesNoLineups(homeTeam, homeSeason, awayTeam, awaySeason,
                                                                                        currSeason,-1);
                mtp.setFeatures(featuresNoLineups, false);
            });
        });
    }

    public static HashMap<String, TrainingTeam> createHistoricMatchups(ArrayList<HistoricMatchDbData> matches) {
        HashMap<String, TrainingTeam> teamsInLeague = new HashMap<>();
        if (matches == null) {
            return teamsInLeague;
        }
        for (HistoricMatchDbData data: matches) {
            teamsInLeague.putIfAbsent(data.getHomeTeam(), new TrainingTeam(data.getHomeTeam()));
            teamsInLeague.putIfAbsent(data.getAwayTeam(), new TrainingTeam(data.getAwayTeam()));
            TrainingTeam homeTeam = teamsInLeague.get(data.getHomeTeam());
            TrainingTeam awayTeam = teamsInLeague.get(data.getAwayTeam());
            TrainingMatch match = new TrainingMatch(homeTeam, awayTeam, data.getHomeScore(), data.getAwayScore(), data.getSeasonYearStart());
            homeTeam.addMatchWithTeam(data.getAwayTeam(), match);
            awayTeam.addMatchWithTeam(data.getHomeTeam(), match);
        }
        return teamsInLeague;
    }

    /*
     * Method uses all historic matches in the database for a league and updates each teams stats as it comes across new games.
     * Uses all player rating records, which also contain match data, then when the match data changes to a new match it saves the
     * current stats to the old teams.
     *
     * saveLastMatch added in for testing purposes so that Team stats can be updated to the point right before the final match takes
     * place - to allow us to create features for a prediction and check they are the same as those used for training.
     */
    private static ArrayList<TrainingMatch> createLeaguesMatches(ArrayList<PlayerMatchDbData> playerRatings, HashMap<String, TrainingTeam> teamsInLeague, boolean saveLastMatch) {
        int lastMatchId = -1; //to be used to see if we come across a new match
        TrainingTeam homeTeam = null;
        TrainingTeamsSeason homeSeason = null;
        TrainingTeam awayTeam = null;
        TrainingTeamsSeason awaySeason = null;
        HashMap<String, Player> homeLineup = new HashMap<>();
        HashMap<String, Player> awayLineup = new HashMap<>();
        ArrayList<TrainingMatch> matches = new ArrayList<>();
        PlayerMatchDbData lastRecordData = null; //we only save a game once we get to data from a new match so need access to the last record
        PlayerMatchDbData data = null;

        //need to go through, and collect data for the current team
        for (int i = 0; i<playerRatings.size(); i++) {
            data = playerRatings.get(i);
            if (lastMatchId != data.getMatchId()) {
                //we have a new match. first need to save old stats
                if (lastMatchId != -1) {
                    saveData(homeTeam, homeSeason, awayTeam, awaySeason, homeLineup, awayLineup, lastRecordData, matches);
                }

                //then need to update fields for next iter
                homeLineup.clear();
                awayLineup.clear();
                teamsInLeague.putIfAbsent(data.getHomeTeam(), new TrainingTeam(data.getHomeTeam()));
                teamsInLeague.putIfAbsent(data.getAwayTeam(), new TrainingTeam(data.getAwayTeam()));
                homeTeam = teamsInLeague.get(data.getHomeTeam());
                awayTeam = teamsInLeague.get(data.getAwayTeam());
                homeSeason = homeTeam.getTeamsSeason(data.getSeasonYearStart());
                awaySeason = awayTeam.getTeamsSeason(data.getSeasonYearStart());
            }

            if (data.playsForHomeTeam()) {
                homeLineup.put(data.getName(), new Player(data.getName(), data.getMins(), data.getRating(), true));
            } else {
                awayLineup.put(data.getName(), new Player(data.getName(), data.getMins(), data.getRating(), false));
            }
            lastMatchId = data.getMatchId();
            lastRecordData = data;
        }
        //saving 1 last time to save the final record.
        if (data != null && saveLastMatch) {
            saveData(homeTeam, homeSeason, awayTeam, awaySeason, homeLineup, awayLineup, data, matches);
        }
        return matches;
    }

    //NOTE: need to create the training match first before saving the games stats. We want to train on the state of the team before the game.
    private static void saveData(TrainingTeam homeTeam, TrainingTeamsSeason homeSeason, TrainingTeam awayTeam, TrainingTeamsSeason awaySeason, HashMap<String, Player> homeLineup, HashMap<String, Player> awayLineup,
                                 PlayerMatchDbData data, ArrayList<TrainingMatch> matches) {
        //creating match which we can create features on later
        HashMap<String, Player> homeStartingXI = getStartingXI(homeLineup);
        HashMap<String, Player> awayStartingXI = getStartingXI(awayLineup);
        TrainingMatch match = new TrainingMatch(homeTeam, awayTeam, data.getHomeOdds(), data.getDrawOdds(), data.getAwayOdds(),
                                                data.getHomeScore(), data.getAwayScore(), data.getDate(), data.getSeasonYearStart());
        ArrayList<Double> features = CreateFeatures.getNewFeatures(homeTeam, homeSeason, awayTeam, awaySeason,
                                                                new ArrayList<>(homeStartingXI.keySet()), new ArrayList<>(awayStartingXI.keySet()),
                                                                data.getSeasonYearStart(), data.getResult());
        ArrayList<Double> featuresNoLineups = CreateFeatures.getNewFeaturesNoLineups(homeTeam, homeSeason, awayTeam, awaySeason, data.getSeasonYearStart(), data.getResult());
        match.setFeatures(features);
        match.setFeaturesNoLineups(featuresNoLineups);
        if (homeSeason.getNumbGamesPlayed() >= NUMB_MATCHES_BEFORE_VALID_TRAINING_DATA &&
                awaySeason.getNumbGamesPlayed() >= NUMB_MATCHES_BEFORE_VALID_TRAINING_DATA) {
            matches.add(match);
        }
        if (data.getHomeScore() != -1 && data.getAwayScore() != -1) {
            //saving stats to season and team history
            addStatsToTeamsSeasons(data, homeSeason, awaySeason, homeLineup, awayLineup);
            homeTeam.addMatchWithTeam(data.getAwayTeam(), match);
            awayTeam.addMatchWithTeam(data.getHomeTeam(), match);
        }
    }

    public static HashMap<String,Player> getStartingXI(HashMap<String,Player> allPlayers) {
        if (allPlayers.size() == 11) return allPlayers;
        HashMap<String,Player> startingXI = new HashMap<>();
        ArrayList<Player> players = new ArrayList<>(allPlayers.values());
        players.sort((p1,p2) -> p2.getOvrMins() - p1.getOvrMins());
        for (int i = 0; i<11; i++) {
            Player p = players.get(i);
            startingXI.put(p.getPlayerName(), p);
        }
        return startingXI;
    }

    //method gets the data first out rather than passing in the oppositions season, so that we use the stats before each season is updated.
    public static void addStatsToTeamsSeasons(PlayerMatchDbData data, TrainingTeamsSeason homeSeason, TrainingTeamsSeason awaySeason,
                                               HashMap<String, Player> homeLineup, HashMap<String, Player> awayLineup) {
        double homeTotalAvgGoalsFor = homeSeason.getAvgGoalsFor(GamesSelector.ALL_GAMES);
        double homeTotalAvgGoalsAgainst = homeSeason.getAvgGoalsAgainst(GamesSelector.ALL_GAMES);
        double homeHomeAvgGoalsFor = homeSeason.getAvgGoalsFor(GamesSelector.ONLY_HOME_GAMES);
        double homeHomeAvgGoalsAgainst = homeSeason.getAvgGoalsAgainst(GamesSelector.ONLY_HOME_GAMES);
        double homeTotalAvgXGF = homeSeason.getAvgXGF(GamesSelector.ALL_GAMES);
        double homeTotalAvgXGA = homeSeason.getAvgXGA(GamesSelector.ALL_GAMES);
        double homeHomeAvgXGF = homeSeason.getAvgXGF(GamesSelector.ONLY_HOME_GAMES);
        double homeHomeAvgXGA = homeSeason.getAvgXGA(GamesSelector.ONLY_HOME_GAMES);
        double homeWeightedTotalXGF = homeSeason.getWeightedAvgXGF(GamesSelector.ALL_GAMES);
        double homeWeightedTotalXGA = homeSeason.getWeightedAvgXGA(GamesSelector.ALL_GAMES);
        double homeWeightedHomeXGF = homeSeason.getWeightedAvgXGF(GamesSelector.ONLY_HOME_GAMES);
        double homeWeightedHomeXGA = homeSeason.getWeightedAvgXGA(GamesSelector.ONLY_HOME_GAMES);
        double homeTotalPPG = homeSeason.getAvgPoints(GamesSelector.ALL_GAMES);
        double homeHomePPG = homeSeason.getAvgPoints(GamesSelector.ONLY_HOME_GAMES);
        double homeLast5TotalPPG = homeSeason.getAvgPointsOverLastXGames(GamesSelector.ALL_GAMES, COMPARE_LAST_N_GAMES);
        double homeLast5HomePPG = homeSeason.getAvgPointsOverLastXGames(GamesSelector.ONLY_HOME_GAMES, COMPARE_LAST_N_GAMES);
        double homeWeightedTotalGoalsFor = homeSeason.getWeightedAvgGoalsFor(GamesSelector.ALL_GAMES);
        double homeWeightedTotalGoalsAgainst = homeSeason.getWeightedAvgGoalsAgainst(GamesSelector.ALL_GAMES);
        double homeWeightedHomeGoalsFor = homeSeason.getWeightedAvgGoalsFor(GamesSelector.ONLY_HOME_GAMES);
        double homeWeightedHomeGoalsAgainst = homeSeason.getWeightedAvgGoalsAgainst(GamesSelector.ONLY_HOME_GAMES);

        double awayTotalAvgGoalsFor = awaySeason.getAvgGoalsFor(GamesSelector.ALL_GAMES);
        double awayTotalAvgGoalsAgainst = awaySeason.getAvgGoalsAgainst(GamesSelector.ALL_GAMES);
        double awayAwayAvgGoalsFor = awaySeason.getAvgGoalsFor(GamesSelector.ONLY_AWAY_GAMES);
        double awayAwayAvgGoalsAgainst = awaySeason.getAvgGoalsAgainst(GamesSelector.ONLY_AWAY_GAMES);
        double awayTotalAvgXGF = awaySeason.getAvgXGF(GamesSelector.ALL_GAMES);
        double awayTotalAvgXGA = awaySeason.getAvgXGA(GamesSelector.ALL_GAMES);
        double awayAwayAvgXGF = awaySeason.getAvgXGF(GamesSelector.ONLY_AWAY_GAMES);
        double awayAwayAvgXGA = awaySeason.getAvgXGA(GamesSelector.ONLY_AWAY_GAMES);
        double awayWeightedTotalXGF = awaySeason.getWeightedAvgXGF(GamesSelector.ALL_GAMES);
        double awayWeightedTotalXGA = awaySeason.getWeightedAvgXGA(GamesSelector.ALL_GAMES);
        double awayWeightedAwayXGF = awaySeason.getWeightedAvgXGF(GamesSelector.ONLY_AWAY_GAMES);
        double awayWeightedAwayXGA = awaySeason.getWeightedAvgXGA(GamesSelector.ONLY_AWAY_GAMES);
        double awayTotalPPG = awaySeason.getAvgPoints(GamesSelector.ALL_GAMES);
        double awayAwayPPG = awaySeason.getAvgPoints(GamesSelector.ONLY_AWAY_GAMES);
        double awayLast5TotalPPG = awaySeason.getAvgPointsOverLastXGames(GamesSelector.ALL_GAMES, COMPARE_LAST_N_GAMES);
        double awayLast5AwayPPG = awaySeason.getAvgPointsOverLastXGames(GamesSelector.ONLY_AWAY_GAMES, COMPARE_LAST_N_GAMES);
        double awayWeightedTotalGoalsFor = awaySeason.getWeightedAvgGoalsFor(GamesSelector.ALL_GAMES);
        double awayWeightedTotalGoalsAgainst = awaySeason.getWeightedAvgGoalsAgainst(GamesSelector.ALL_GAMES);
        double awayWeightedAwayGoalsFor = awaySeason.getWeightedAvgGoalsFor(GamesSelector.ONLY_AWAY_GAMES);
        double awayWeightedAwayGoalsAgainst = awaySeason.getWeightedAvgGoalsAgainst(GamesSelector.ONLY_AWAY_GAMES);

        homeSeason.addGameStats(data.getHomeScore(),
                data.getAwayScore(),
                data.getHomeXGF(),
                data.getAwayXGF(),
                data.getFirstScorer() == 1,
                data.getFirstScorer() != -1,
                true,
                awayTotalAvgGoalsFor,
                awayTotalAvgGoalsAgainst,
                awayAwayAvgGoalsFor,
                awayAwayAvgGoalsAgainst,
                awayTotalAvgXGF,
                awayTotalAvgXGA,
                awayAwayAvgXGF,
                awayAwayAvgXGA,
                awayWeightedTotalXGF,
                awayWeightedTotalXGA,
                awayWeightedAwayXGF,
                awayWeightedAwayXGA,
                awayTotalPPG,
                awayAwayPPG,
                awayLast5TotalPPG,
                awayLast5AwayPPG,
                awayWeightedTotalGoalsFor,
                awayWeightedTotalGoalsAgainst,
                awayWeightedAwayGoalsFor,
                awayWeightedAwayGoalsAgainst);

        ArrayList<Player> homePlayerRatings = new ArrayList<>(homeLineup.values());
        homePlayerRatings.forEach(player -> {
            homeSeason.addPlayerStats(player.getPlayerName(), player.getOvrMins(), player.getAvgOvrRating(), true);
        });

        awaySeason.addGameStats(data.getAwayScore(),
                data.getHomeScore(),
                data.getAwayXGF(),
                data.getHomeXGF(),
                data.getFirstScorer() == 2,
                data.getFirstScorer() != -1,
                false,
                homeTotalAvgGoalsFor,
                homeTotalAvgGoalsAgainst,
                homeHomeAvgGoalsFor,
                homeHomeAvgGoalsAgainst,
                homeTotalAvgXGF,
                homeTotalAvgXGA,
                homeHomeAvgXGF,
                homeHomeAvgXGA,
                homeWeightedTotalXGF,
                homeWeightedTotalXGA,
                homeWeightedHomeXGF,
                homeWeightedHomeXGA,
                homeTotalPPG,
                homeHomePPG,
                homeLast5TotalPPG,
                homeLast5HomePPG,
                homeWeightedTotalGoalsFor,
                homeWeightedTotalGoalsAgainst,
                homeWeightedHomeGoalsFor,
                homeWeightedHomeGoalsAgainst);

        ArrayList<Player> awayPlayerRatings = new ArrayList<>(awayLineup.values());
        awayPlayerRatings.forEach(player -> {
            awaySeason.addPlayerStats(player.getPlayerName(), player.getOvrMins(), player.getAvgOvrRating(), false);
        });
    }

}
