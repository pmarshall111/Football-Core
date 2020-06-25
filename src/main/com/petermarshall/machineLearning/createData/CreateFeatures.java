package com.petermarshall.machineLearning.createData;

import com.petermarshall.machineLearning.createData.classes.TrainingTeam;
import com.petermarshall.machineLearning.createData.classes.TrainingTeamsSeason;

import java.util.ArrayList;

import static com.petermarshall.machineLearning.createData.CalculatePastStats.COMPARE_LAST_N_GAMES;
import static com.petermarshall.machineLearning.createData.CalculatePastStats.NUMB_SEASONS_HISTORY;
import static com.petermarshall.machineLearning.createData.classes.GamesSelector.*;

public class CreateFeatures {
    public static ArrayList<Double> getFeaturesNoLineupsExtended(TrainingTeam homeTeam, TrainingTeamsSeason homeSeason,
                                                         TrainingTeam awayTeam, TrainingTeamsSeason awaySeason,
                                                         int seasonYearStart, int result) {
        ArrayList<Double> features = new ArrayList<>();
        features.add((double) result);

        //adding bias parameter of 1
        features.add(1d);

        //homeStats
        features.add(homeSeason.getAvgGoalsFor(ALL_GAMES));
        features.add(homeSeason.getAvgGoalsAgainst(ALL_GAMES));
        features.add(homeSeason.getAvgXGF(ALL_GAMES));
        features.add(homeSeason.getAvgXGA(ALL_GAMES));
        features.add(homeSeason.getWeightedAvgXGF(ALL_GAMES));
        features.add(homeSeason.getWeightedAvgXGA(ALL_GAMES));
        features.add(homeSeason.getFormGoalsFor(ALL_GAMES));
        features.add(homeSeason.getFormGoalsAgainst(ALL_GAMES));
        features.add(homeSeason.getFormXGF(ALL_GAMES));
        features.add(homeSeason.getFormXGA(ALL_GAMES));
        features.add(homeSeason.getFormWeightedXGF(ALL_GAMES));
        features.add(homeSeason.getFormWeightedXGA(ALL_GAMES));
        features.add(homeSeason.getAvgFormGoalsFor(ALL_GAMES, 0));
        features.add(homeSeason.getAvgFormGoalsAgainst(ALL_GAMES, 0));
        features.add(homeSeason.getAvgFormXGF(ALL_GAMES, 0));
        features.add(homeSeason.getAvgFormXGA(ALL_GAMES, 0));
        features.add(homeSeason.getAvgFormWeightedXGF(ALL_GAMES, 0));
        features.add(homeSeason.getAvgFormWeightedXGA(ALL_GAMES, 0));
        features.add(homeSeason.getFormXGFOverLastNGames(ALL_GAMES, COMPARE_LAST_N_GAMES));
        features.add(homeSeason.getFormXGAOverLastNGames(ALL_GAMES, COMPARE_LAST_N_GAMES));
        features.add(homeSeason.getAvgPoints(ALL_GAMES));
        features.add(homeSeason.getAvgPointsOverLastXGames(ALL_GAMES, COMPARE_LAST_N_GAMES));
        features.add(homeSeason.getAvgPointsWhenScoredFirst(ALL_GAMES));
        features.add(homeSeason.getAvgPointsWhenConceededFirst(ALL_GAMES));
        features.add(homeTeam.getPointsOfLastMatchups(awayTeam.getTeamName(), ALL_GAMES, seasonYearStart - NUMB_SEASONS_HISTORY));
        features.add(homeSeason.getAvgPointsOfAllOpponentsGamesWholeSeason(ALL_GAMES));
        features.add(homeSeason.getAvgPointsOfLastXOpponentsGamesWholeSeason(ALL_GAMES, COMPARE_LAST_N_GAMES));
        features.add(homeSeason.getAvgPointsOfAllOpponentsLast5Games(ALL_GAMES));
        features.add(homeSeason.getAvgPointsOfLastXOpponentsLast5Games(ALL_GAMES, COMPARE_LAST_N_GAMES));
        features.add(homeSeason.getAvgNumberOfCleanSheets(ALL_GAMES));
        features.add(homeSeason.getAvgNumberOfCleanSheetsLastXGames(ALL_GAMES, COMPARE_LAST_N_GAMES, true));

        //home@home
        features.add(homeSeason.getAvgXGF(ONLY_HOME_GAMES));
        features.add(homeSeason.getAvgXGA(ONLY_HOME_GAMES));

        //awayStats
        features.add(awaySeason.getAvgGoalsFor(ALL_GAMES));
        features.add(awaySeason.getAvgGoalsAgainst(ALL_GAMES));
        features.add(awaySeason.getAvgXGF(ALL_GAMES));
        features.add(awaySeason.getAvgXGA(ALL_GAMES));
        features.add(awaySeason.getWeightedAvgXGF(ALL_GAMES));
        features.add(awaySeason.getWeightedAvgXGA(ALL_GAMES));
        features.add(awaySeason.getFormGoalsFor(ALL_GAMES));
        features.add(awaySeason.getFormGoalsAgainst(ALL_GAMES));
        features.add(awaySeason.getFormXGF(ALL_GAMES));
        features.add(awaySeason.getFormXGA(ALL_GAMES));
        features.add(awaySeason.getFormWeightedXGF(ALL_GAMES));
        features.add(awaySeason.getFormWeightedXGA(ALL_GAMES));
        features.add(awaySeason.getAvgFormGoalsFor(ALL_GAMES, 0));
        features.add(awaySeason.getAvgFormGoalsAgainst(ALL_GAMES, 0));
        features.add(awaySeason.getAvgFormXGF(ALL_GAMES, 0));
        features.add(awaySeason.getAvgFormXGA(ALL_GAMES, 0));
        features.add(awaySeason.getAvgFormWeightedXGF(ALL_GAMES, 0));
        features.add(awaySeason.getAvgFormWeightedXGA(ALL_GAMES, 0));
        features.add(awaySeason.getFormXGFOverLastNGames(ALL_GAMES, COMPARE_LAST_N_GAMES));
        features.add(awaySeason.getFormXGAOverLastNGames(ALL_GAMES, COMPARE_LAST_N_GAMES));
        features.add(awaySeason.getAvgPoints(ALL_GAMES));
        features.add(awaySeason.getAvgPointsOverLastXGames(ALL_GAMES, COMPARE_LAST_N_GAMES));
        features.add(awaySeason.getAvgPointsWhenScoredFirst(ALL_GAMES));
        features.add(awaySeason.getAvgPointsWhenConceededFirst(ALL_GAMES));
        features.add(awayTeam.getPointsOfLastMatchups(homeTeam.getTeamName(), ALL_GAMES, seasonYearStart - NUMB_SEASONS_HISTORY));
        features.add(awaySeason.getAvgPointsOfAllOpponentsGamesWholeSeason(ALL_GAMES));
        features.add(awaySeason.getAvgPointsOfLastXOpponentsGamesWholeSeason(ALL_GAMES, COMPARE_LAST_N_GAMES));
        features.add(awaySeason.getAvgPointsOfAllOpponentsLast5Games(ALL_GAMES));
        features.add(awaySeason.getAvgPointsOfLastXOpponentsLast5Games(ALL_GAMES, COMPARE_LAST_N_GAMES));
        features.add(awaySeason.getAvgNumberOfCleanSheets(ALL_GAMES));
        features.add(awaySeason.getAvgNumberOfCleanSheetsLastXGames(ALL_GAMES, COMPARE_LAST_N_GAMES, true));

        //away@away
        features.add(awaySeason.getAvgXGF(ONLY_AWAY_GAMES));
        features.add(awaySeason.getAvgXGA(ONLY_AWAY_GAMES));

        return features;
    }

    public static ArrayList<Double> getNewFeatures(TrainingTeam homeTeam, TrainingTeamsSeason homeSeason,
                                                            TrainingTeam awayTeam, TrainingTeamsSeason awaySeason,
                                                   ArrayList<String> homePlayersNames, ArrayList<String> awayPlayersNames,
                                                            int seasonYearStart, int result) {
        int LAST_5 = 5;
        ArrayList<Double> features = new ArrayList<>();
        features.add((double) result);

        //adding bias parameter of 1
        features.add(1d);

        //home stats
        features.add(homeSeason.getAvgGoalsForLastXGames(ALL_GAMES, LAST_5));
        features.add(homeSeason.getWeightedAvgGoalsFor(ALL_GAMES));
        features.add(homeSeason.getFormGoalsFor(ALL_GAMES));
        features.add(homeSeason.getFormWeightedGoalsFor(ALL_GAMES));
        features.add(homeSeason.getAvgGoalsAgainstLastXGames(ALL_GAMES, LAST_5));
        features.add(homeSeason.getWeightedAvgGoalsAgainst(ALL_GAMES));
        features.add(homeSeason.getFormGoalsAgainst(ALL_GAMES));
        features.add(homeSeason.getFormWeightedGoalsAgainst(ALL_GAMES));
        features.add(homeSeason.getAvgXGF(ALL_GAMES));
        features.add(homeSeason.getWeightedAvgXGF(ALL_GAMES));
        features.add(homeSeason.getFormXGF(ALL_GAMES));
        features.add(homeSeason.getFormWeightedXGF(ALL_GAMES));
        features.add(homeSeason.getAvgXGA(ALL_GAMES));
        features.add(homeSeason.getWeightedAvgXGA(ALL_GAMES));
        features.add(homeSeason.getFormXGA(ALL_GAMES));
        features.add(homeSeason.getFormWeightedXGA(ALL_GAMES));
        features.add(homeSeason.getAvgPoints(ALL_GAMES));
        features.add(homeSeason.getAvgPoints(ONLY_HOME_GAMES));
        features.add(homeTeam.getPointsOfLastMatchups(awayTeam.getTeamName(), ALL_GAMES, seasonYearStart - NUMB_SEASONS_HISTORY));
        features.add(homeSeason.getAvgPointsOfAllOpponentsGamesWholeSeason(ALL_GAMES));
        features.add(homeSeason.getAvgPointsOfLastXOpponentsGamesWholeSeason(ALL_GAMES, COMPARE_LAST_N_GAMES));
        features.add(homeSeason.getAvgPointsOfAllOpponentsLast5Games(ALL_GAMES));
        features.add(homeSeason.getAvgPointsOfLastXOpponentsLast5Games(ALL_GAMES, COMPARE_LAST_N_GAMES));
        features.add(homeSeason.getAvgNumberOfCleanSheets(ALL_GAMES));
        features.add(homeSeason.getGamesWeightedLineupRating(ALL_GAMES, homePlayersNames));
        features.add(homeSeason.getLineupStrength(ALL_GAMES, homePlayersNames));

        //away stats
        features.add(awaySeason.getAvgGoalsForLastXGames(ALL_GAMES, LAST_5));
        features.add(awaySeason.getWeightedAvgGoalsFor(ALL_GAMES));
        features.add(awaySeason.getFormGoalsFor(ALL_GAMES));
        features.add(awaySeason.getFormWeightedGoalsFor(ALL_GAMES));
        features.add(awaySeason.getAvgGoalsAgainstLastXGames(ALL_GAMES, LAST_5));
        features.add(awaySeason.getWeightedAvgGoalsAgainst(ALL_GAMES));
        features.add(awaySeason.getFormGoalsAgainst(ALL_GAMES));
        features.add(awaySeason.getFormWeightedGoalsAgainst(ALL_GAMES));
        features.add(awaySeason.getAvgXGF(ALL_GAMES));
        features.add(awaySeason.getWeightedAvgXGF(ALL_GAMES));
        features.add(awaySeason.getFormXGF(ALL_GAMES));
        features.add(awaySeason.getFormWeightedXGF(ALL_GAMES));
        features.add(awaySeason.getAvgXGA(ALL_GAMES));
        features.add(awaySeason.getWeightedAvgXGA(ALL_GAMES));
        features.add(awaySeason.getFormXGA(ALL_GAMES));
        features.add(awaySeason.getFormWeightedXGA(ALL_GAMES));
        features.add(awaySeason.getAvgPoints(ALL_GAMES));
        features.add(awaySeason.getAvgPoints(ONLY_AWAY_GAMES));
        features.add(awayTeam.getPointsOfLastMatchups(homeTeam.getTeamName(), ALL_GAMES, seasonYearStart - NUMB_SEASONS_HISTORY));
        features.add(awaySeason.getAvgPointsOfAllOpponentsGamesWholeSeason(ALL_GAMES));
        features.add(awaySeason.getAvgPointsOfLastXOpponentsGamesWholeSeason(ALL_GAMES, COMPARE_LAST_N_GAMES));
        features.add(awaySeason.getAvgPointsOfAllOpponentsLast5Games(ALL_GAMES));
        features.add(awaySeason.getAvgPointsOfLastXOpponentsLast5Games(ALL_GAMES, COMPARE_LAST_N_GAMES));
        features.add(awaySeason.getAvgNumberOfCleanSheets(ALL_GAMES));
        features.add(awaySeason.getGamesWeightedLineupRating(ALL_GAMES, awayPlayersNames));
        features.add(awaySeason.getLineupStrength(ALL_GAMES, awayPlayersNames));

        return features;
    }

    public static ArrayList<Double> getNewFeaturesNoLineups(TrainingTeam homeTeam, TrainingTeamsSeason homeSeason,
                                                            TrainingTeam awayTeam, TrainingTeamsSeason awaySeason,
                                                            int seasonYearStart, int result) {
        int LAST_5 = 5;
        ArrayList<Double> features = new ArrayList<>();
        features.add((double) result);

        //adding bias parameter of 1
        features.add(1d);

        //home stats
        features.add(homeSeason.getAvgGoalsForLastXGames(ALL_GAMES, LAST_5));
        features.add(homeSeason.getWeightedAvgGoalsFor(ALL_GAMES));
        features.add(homeSeason.getFormGoalsFor(ALL_GAMES));
        features.add(homeSeason.getFormWeightedGoalsFor(ALL_GAMES));
        features.add(homeSeason.getAvgGoalsAgainstLastXGames(ALL_GAMES, LAST_5));
        features.add(homeSeason.getWeightedAvgGoalsAgainst(ALL_GAMES));
        features.add(homeSeason.getFormGoalsAgainst(ALL_GAMES));
        features.add(homeSeason.getFormWeightedGoalsAgainst(ALL_GAMES));
        features.add(homeSeason.getAvgXGF(ALL_GAMES));
        features.add(homeSeason.getWeightedAvgXGF(ALL_GAMES));
        features.add(homeSeason.getFormXGF(ALL_GAMES));
        features.add(homeSeason.getFormWeightedXGF(ALL_GAMES));
        features.add(homeSeason.getAvgXGA(ALL_GAMES));
        features.add(homeSeason.getWeightedAvgXGA(ALL_GAMES));
        features.add(homeSeason.getFormXGA(ALL_GAMES));
        features.add(homeSeason.getFormWeightedXGA(ALL_GAMES));
        features.add(homeSeason.getAvgPoints(ALL_GAMES));
        features.add(homeSeason.getAvgPoints(ONLY_HOME_GAMES));
        features.add(homeTeam.getPointsOfLastMatchups(awayTeam.getTeamName(), ALL_GAMES, seasonYearStart - NUMB_SEASONS_HISTORY));
        features.add(homeSeason.getAvgPointsOfAllOpponentsGamesWholeSeason(ALL_GAMES));
        features.add(homeSeason.getAvgPointsOfLastXOpponentsGamesWholeSeason(ALL_GAMES, COMPARE_LAST_N_GAMES));
        features.add(homeSeason.getAvgPointsOfAllOpponentsLast5Games(ALL_GAMES));
        features.add(homeSeason.getAvgPointsOfLastXOpponentsLast5Games(ALL_GAMES, COMPARE_LAST_N_GAMES));
        features.add(homeSeason.getAvgNumberOfCleanSheets(ALL_GAMES));
        
        //away stats
        features.add(awaySeason.getAvgGoalsForLastXGames(ALL_GAMES, LAST_5));
        features.add(awaySeason.getWeightedAvgGoalsFor(ALL_GAMES));
        features.add(awaySeason.getFormGoalsFor(ALL_GAMES));
        features.add(awaySeason.getFormWeightedGoalsFor(ALL_GAMES));
        features.add(awaySeason.getAvgGoalsAgainstLastXGames(ALL_GAMES, LAST_5));
        features.add(awaySeason.getWeightedAvgGoalsAgainst(ALL_GAMES));
        features.add(awaySeason.getFormGoalsAgainst(ALL_GAMES));
        features.add(awaySeason.getFormWeightedGoalsAgainst(ALL_GAMES));
        features.add(awaySeason.getAvgXGF(ALL_GAMES));
        features.add(awaySeason.getWeightedAvgXGF(ALL_GAMES));
        features.add(awaySeason.getFormXGF(ALL_GAMES));
        features.add(awaySeason.getFormWeightedXGF(ALL_GAMES));
        features.add(awaySeason.getAvgXGA(ALL_GAMES));
        features.add(awaySeason.getWeightedAvgXGA(ALL_GAMES));
        features.add(awaySeason.getFormXGA(ALL_GAMES));
        features.add(awaySeason.getFormWeightedXGA(ALL_GAMES));
        features.add(awaySeason.getAvgPoints(ALL_GAMES));
        features.add(awaySeason.getAvgPoints(ONLY_AWAY_GAMES));
        features.add(awayTeam.getPointsOfLastMatchups(homeTeam.getTeamName(), ALL_GAMES, seasonYearStart - NUMB_SEASONS_HISTORY));
        features.add(awaySeason.getAvgPointsOfAllOpponentsGamesWholeSeason(ALL_GAMES));
        features.add(awaySeason.getAvgPointsOfLastXOpponentsGamesWholeSeason(ALL_GAMES, COMPARE_LAST_N_GAMES));
        features.add(awaySeason.getAvgPointsOfAllOpponentsLast5Games(ALL_GAMES));
        features.add(awaySeason.getAvgPointsOfLastXOpponentsLast5Games(ALL_GAMES, COMPARE_LAST_N_GAMES));
        features.add(awaySeason.getAvgNumberOfCleanSheets(ALL_GAMES));

        return features;
    }

    public static ArrayList<Double> getFeaturesOldOld(TrainingTeam homeTeam, TrainingTeamsSeason homeSeason,
                                                   TrainingTeam awayTeam, TrainingTeamsSeason awaySeason, int result) {
        ArrayList<Double> features = new ArrayList<>();
        features.add((double) result);
        //adding bias parameter of 1
        features.add(1d);

        //home
        homeSeason.getAvgGoalsFor(ALL_GAMES);
        homeSeason.getAvgGoalsAgainst(ALL_GAMES);
        homeSeason.getAvgXGF(ALL_GAMES);
        homeSeason.getAvgXGA(ALL_GAMES);
        homeSeason.getWeightedAvgXGF(ALL_GAMES);
        homeSeason.getWeightedAvgXGA(ALL_GAMES);
        homeSeason.getAvgPoints(ALL_GAMES);
        homeSeason.getAvgPointsOverLastXGames(ALL_GAMES, 5);
        homeSeason.getAvgPointsWhenScoredFirst(ALL_GAMES);
        homeSeason.getAvgPointsWhenConceededFirst(ALL_GAMES);
        homeTeam.getPointsOfLastMatchups(awayTeam.getTeamName(), ALL_GAMES, homeSeason.getSeasonYearStart()-2);

        //home@home
        homeSeason.getAvgGoalsFor(ONLY_HOME_GAMES);
        homeSeason.getAvgGoalsAgainst(ONLY_HOME_GAMES);
        homeSeason.getAvgXGF(ONLY_HOME_GAMES);
        homeSeason.getAvgXGA(ONLY_HOME_GAMES);
        homeSeason.getWeightedAvgXGF(ONLY_HOME_GAMES);
        homeSeason.getWeightedAvgXGA(ONLY_HOME_GAMES);
        homeSeason.getAvgPoints(ONLY_HOME_GAMES);
        homeSeason.getAvgPointsOverLastXGames(ONLY_HOME_GAMES, 5);
        homeSeason.getAvgPointsWhenScoredFirst(ONLY_HOME_GAMES);
        homeSeason.getAvgPointsWhenConceededFirst(ONLY_HOME_GAMES);
        homeTeam.getPointsOfLastMatchups(awayTeam.getTeamName(), ONLY_HOME_GAMES, homeSeason.getSeasonYearStart()-2);

        //away
        awaySeason.getAvgGoalsFor(ALL_GAMES);
        awaySeason.getAvgGoalsAgainst(ALL_GAMES);
        awaySeason.getAvgXGF(ALL_GAMES);
        awaySeason.getAvgXGA(ALL_GAMES);
        awaySeason.getWeightedAvgXGF(ALL_GAMES);
        awaySeason.getWeightedAvgXGA(ALL_GAMES);
        awaySeason.getAvgPoints(ALL_GAMES);
        awaySeason.getAvgPointsOverLastXGames(ALL_GAMES, 5);
        awaySeason.getAvgPointsWhenScoredFirst(ALL_GAMES);
        awaySeason.getAvgPointsWhenConceededFirst(ALL_GAMES);
        awayTeam.getPointsOfLastMatchups(homeTeam.getTeamName(), ALL_GAMES, awaySeason.getSeasonYearStart()-2);

        //away@away
        awaySeason.getAvgGoalsFor(ONLY_AWAY_GAMES);
        awaySeason.getAvgGoalsAgainst(ONLY_AWAY_GAMES);
        awaySeason.getAvgXGF(ONLY_AWAY_GAMES);
        awaySeason.getAvgXGA(ONLY_AWAY_GAMES);
        awaySeason.getWeightedAvgXGF(ONLY_AWAY_GAMES);
        awaySeason.getWeightedAvgXGA(ONLY_AWAY_GAMES);
        awaySeason.getAvgPoints(ONLY_AWAY_GAMES);
        awaySeason.getAvgPointsOverLastXGames(ONLY_AWAY_GAMES, 5);
        awaySeason.getAvgPointsWhenScoredFirst(ONLY_AWAY_GAMES);
        awaySeason.getAvgPointsWhenConceededFirst(ONLY_AWAY_GAMES);
        awayTeam.getPointsOfLastMatchups(homeTeam.getTeamName(), ONLY_AWAY_GAMES, awaySeason.getSeasonYearStart()-2);
        return features;
    }
}
