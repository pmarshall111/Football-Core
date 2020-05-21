package com.petermarshall.machineLearning.createData.refactor;

import com.petermarshall.DateHelper;
import com.petermarshall.database.datasource.DS_Get;
import com.petermarshall.machineLearning.createData.classes.*;
import com.petermarshall.scrape.classes.LeagueSeasonIds;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

//class to be used to create csv files to learn from and update cache in db for past seasons.
public class PastStatsCalculator {
    //constants to use when calculating a teams form
    public static final int NUMB_SEASONS_HISTORY = 2;
    public static final int NUMB_MATCHES_BEFORE_VALID_TRAINING_DATA = 7;
    public static final int COMPARE_LAST_N_GAMES = 5;

    //will do it for all leagues in db.
    public static ArrayList<TrainingMatch> getAllTrainingMatches() {
        LeagueSeasonIds[] leagues = LeagueSeasonIds.values();
        ArrayList<TrainingMatch> allMatches = new ArrayList<>();
        for (LeagueSeasonIds league : leagues) {
            try {
                //statement has to be passed in or else it auto closes and resultset not accessible
                ArrayList stmtAndResults = DS_Get.getLeagueData(league);
                Statement statement = (Statement) stmtAndResults.get(0);
                ResultSet leaguePlayerData = (ResultSet) stmtAndResults.get(1);
                allMatches.addAll(createLeaguesMatches(leaguePlayerData));
                leaguePlayerData.close();
                statement.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return allMatches;
    }

    //func will be given the leagues for which the season must be calculated and also the historic results
    public static void addFeaturesToPredict(ArrayList<MatchToPredict> matches) {
        //first need to get an idea of what leagues we need
        HashMap<String, ArrayList<MatchToPredict>> leagueMatches = new HashMap<>();
        for (MatchToPredict m : matches) {
            leagueMatches.putIfAbsent(m.getLeagueName(), new ArrayList<MatchToPredict>());
            leagueMatches.get(m.getLeagueName()).add(m);
        }
        //now can go through leagueNames updating the matchesToPredict with features.
        //gets the current seasons matches to create stats for each teams season thus far
        //also gets previous matches in past seasons from the database
        leagueMatches.forEach((league, toPredicts) -> {
            ArrayList playerRatingsStmtAndResults = DS_Get.getLeagueData(league, DateHelper.getStartYearForCurrentSeason());
            Statement statement1 = (Statement) playerRatingsStmtAndResults.get(0);
            ResultSet leaguePlayerData = (ResultSet) playerRatingsStmtAndResults.get(1);
            ArrayList matchesStmtAndResults = DS_Get.getMatchesBetweenTeams(league, matches, NUMB_SEASONS_HISTORY);
            Statement statement2 = (Statement) matchesStmtAndResults.get(0);
            ResultSet matchesData = (ResultSet) matchesStmtAndResults.get(1);
            try {
                HashMap<String, TrainingTeam> teamsInLeague = createHistoricMatchups(matchesData);
                statement2.close();
                //don't care about return value here. the func will go through and update all teams seasons to the most up to date vals
                createLeaguesMatches(leaguePlayerData, teamsInLeague);
                toPredicts.forEach(mtp -> {
                    int currSeason = DateHelper.getStartYearForCurrentSeason();
                    TrainingTeam homeTeam = teamsInLeague.get(mtp.getHomeTeamName());
                    TrainingTeam awayTeam = teamsInLeague.get(mtp.getAwayTeamName());
                    TrainingTeamsSeason homeSeason = homeTeam.getTeamsSeason(currSeason);
                    TrainingTeamsSeason awaySeason = awayTeam.getTeamsSeason(currSeason);
                    ArrayList<Double> features = CreateFeatures.getFeatures(homeTeam, homeSeason, awayTeam, awaySeason,
                                                                            mtp.getHomeTeamPlayers(), mtp.getAwayTeamPlayers(),
                                                                            currSeason, -1, -1, -1, -1);
                    ArrayList<Double> featuresNoLineups = CreateFeatures.getFeaturesNoLineups(homeTeam, homeSeason, awayTeam, awaySeason,
                                                                                            currSeason, -1, -1, -1, -1);
                    mtp.setFeatures(features);
                    mtp.setFeaturesNoLineups(featuresNoLineups);
                });
                statement1.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        });
    }

    private static ArrayList<TrainingMatch> createLeaguesMatches(ResultSet playerRatings) throws SQLException {
        return createLeaguesMatches(playerRatings, new HashMap<>());
    }
    private static ArrayList<TrainingMatch> createLeaguesMatches(ResultSet playerRatings, HashMap<String, TrainingTeam> teamsInLeague) throws SQLException {
        int lastMatchId = -1; //to be used to see if we come across a new match
        TrainingTeam homeTeam = null;
        TrainingTeamsSeason homeSeason = null;
        TrainingTeam awayTeam = null;
        TrainingTeamsSeason awaySeason = null;
        HashMap<String, Player> homeLineup = new HashMap<>();
        HashMap<String, Player> awayLineup = new HashMap<>();
        ArrayList<TrainingMatch> matches = new ArrayList<>();
        PlayerMatchDbData data = null;

        //need to go through, and collect data for the current team
        while (playerRatings.next()) {
            data = new PlayerMatchDbData(playerRatings);
            if (lastMatchId != data.getMatchId()) {
                //we have a new match. first need to save old stats
                if (lastMatchId != -1) {
                    saveData(homeTeam, homeSeason, awayTeam, awaySeason, homeLineup, awayLineup, data, matches);
                }

                //then need to update fields for next iter
                homeLineup.clear();
                awayLineup.clear();
                teamsInLeague.putIfAbsent(data.getHomeTeam(), new TrainingTeam(data.getHomeTeam()));
                teamsInLeague.putIfAbsent(data.getAwayTeam(), new TrainingTeam(data.getAwayTeam()));
                homeTeam = teamsInLeague.get(data.getHomeTeam());
                awayTeam = teamsInLeague.get(data.getAwayTeam());
                homeSeason = homeTeam.getTeamsSeason(data.getSeasonYearStart());
                awaySeason = homeTeam.getTeamsSeason(data.getSeasonYearStart());
            }

            if (data.playsForHomeTeam()) {
                homeLineup.put(data.getName(), new Player(data.getName(), data.getMins(), data.getRating(), true));
            } else {
                awayLineup.put(data.getName(), new Player(data.getName(), data.getMins(), data.getRating(), false));
            }
        }
        //saving 1 last time to save the final record.
        if (data != null) {
            saveData(homeTeam, homeSeason, awayTeam, awaySeason, homeLineup, awayLineup, data, matches);
        }

        return matches;
    }

    private static HashMap<String, TrainingTeam> createHistoricMatchups(ResultSet matches) throws SQLException {
        HashMap<String, TrainingTeam> teamsInLeague = new HashMap<>();
        while (matches.next()) {
            HistoricMatchDbData data = new HistoricMatchDbData(matches);
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

//    private static void storeSeasonStats(Collection<TrainingTeam> teams) {
//        HashSet<TrainingTeamsSeason> recentSeasons = new HashSet<>();
//        int seasonYearStart = DateHelper.getStartYearForCurrentSeason();
//        teams.forEach(team -> {
//            TrainingTeamsSeason season = team.getTeamsSeason(seasonYearStart);
//            if (season != null) {
//                recentSeasons.add(season);
//            }
//        });
//
//        DS_Insert.storeSeasonStats(recentSeasons);
//    }

    //NOTE: need to create the training match first before saving the games stats. We want to train on the state of the team before the game.
    private static void saveData(TrainingTeam homeTeam, TrainingTeamsSeason homeSeason, TrainingTeam awayTeam, TrainingTeamsSeason awaySeason, HashMap<String, Player> homeLineup, HashMap<String, Player> awayLineup,
                                 PlayerMatchDbData data, ArrayList<TrainingMatch> matches) {
        //creating match which we can create features on later
        TrainingMatch match = new TrainingMatch(homeTeam, homeSeason, awayTeam, awaySeason, homeLineup, awayLineup,
                data.getHomeOdds(), data.getDrawOdds(), data.getAwayOdds(),
                data.getHomeScore(), data.getAwayScore(), data.getDate(),
                data.getSeasonYearStart());
        ArrayList<Double> features = CreateFeatures.getFeatures(homeTeam, homeSeason, awayTeam, awaySeason,
                                                                new ArrayList<>(homeLineup.keySet()), new ArrayList<>(awayLineup.keySet()),
                                                                data.getSeasonYearStart(),
                                                                data.getHomeOdds(), data.getDrawOdds(),
                                                                data.getAwayOdds(), data.getResult());
        match.setFeatures(features);
        if (homeSeason.getNumbGamesPlayed() >= NUMB_MATCHES_BEFORE_VALID_TRAINING_DATA &&
                awaySeason.getNumbGamesPlayed() >= NUMB_MATCHES_BEFORE_VALID_TRAINING_DATA) {
            matches.add(match);
        }
        //saving stats to season and team history
        homeSeason.addGameStats(data.getHomeScore(), data.getAwayScore(), data.getHomeXGF(), data.getAwayXGF(),
                data.getFirstScorer() == 1, data.getFirstScorer() != -1, true, awaySeason);
        awaySeason.addGameStats(data.getAwayScore(), data.getHomeScore(), data.getAwayXGF(), data.getHomeXGF(),
                data.getFirstScorer() == 2, data.getFirstScorer() != -1, false, homeSeason);
        if (data.getHomeScore() != -1 && data.getAwayScore() != -1) {
            homeTeam.addMatchWithTeam(data.getAwayTeam(), match);
            awayTeam.addMatchWithTeam(data.getHomeTeam(), match);
        }
    }

}
