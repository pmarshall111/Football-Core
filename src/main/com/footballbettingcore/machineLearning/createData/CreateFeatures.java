package com.footballbettingcore.machineLearning.createData;

import java.util.ArrayList;
import com.footballbettingcore.machineLearning.createData.classes.TrainingTeam;
import com.footballbettingcore.machineLearning.createData.classes.TrainingTeamsSeason;
import static com.footballbettingcore.machineLearning.createData.classes.GamesSelector.*;

public class CreateFeatures {
    public static ArrayList<Double> getFeaturesSmallMakesSense(TrainingTeam homeTeam, TrainingTeamsSeason homeSeason,
                                                                  TrainingTeam awayTeam, TrainingTeamsSeason awaySeason) {

        ArrayList<Double> features = new ArrayList<>();

        features.add(homeSeason.getAvgPoints(ALL_GAMES));
        features.add(homeSeason.getAvgPointsOverLastXGames(ALL_GAMES, 5));
        features.add(awaySeason.getAvgPoints(ALL_GAMES));
        features.add(awaySeason.getAvgPointsOverLastXGames(ALL_GAMES, 5));
        features.add(awayTeam.getPointsOfLastMatchups(homeTeam.getTeamName(), ALL_GAMES, awaySeason.getSeasonYearStart()-2));
        features.add(homeSeason.getAvgGoalsFor(ALL_GAMES));
        features.add(awaySeason.getAvgGoalsFor(ALL_GAMES));
        features.add(homeSeason.getAvgXGF(ALL_GAMES));
        features.add(awaySeason.getAvgXGF(ALL_GAMES));
        features.add(homeSeason.getAvgXGA(ALL_GAMES));
        features.add(awaySeason.getAvgXGA(ALL_GAMES));
        features.add(homeSeason.getFormXGF(ALL_GAMES));
        features.add(awaySeason.getFormXGF(ALL_GAMES));

        return features;
    }

    public static ArrayList<Double> getFeaturesFromRCoefficient(TrainingTeam homeTeam, TrainingTeamsSeason homeSeason,
                                                                         TrainingTeam awayTeam, TrainingTeamsSeason awaySeason,
                                                                         double[] odds) {

        ArrayList<Double> features = new ArrayList<>();

        features.add(homeSeason.getOvrPlayerRating() - awaySeason.getOvrPlayerRating());
        features.add((homeSeason.getAvgPoints(ALL_GAMES) / homeSeason.getAvgPointsOfAllOpponentsGamesWholeSeason(ALL_GAMES))
                - (awaySeason.getAvgPoints(ALL_GAMES) / awaySeason.getAvgPointsOfAllOpponentsGamesWholeSeason(ALL_GAMES)));

        SimulateMatches.ResultProbabilities avgXgProbabilities = SimulateMatches.createResultProbabilities(homeSeason.getAvgXGF(ALL_GAMES), awaySeason.getAvgXGF(ALL_GAMES));
        SimulateMatches.ResultProbabilities avgXgaProbabilities = SimulateMatches.createResultProbabilities(homeSeason.getAvgXGA(ALL_GAMES), awaySeason.getAvgXGA(ALL_GAMES));
        features.add((avgXgProbabilities.getHomeWinProbability() - avgXgaProbabilities.getHomeWinProbability()) - (avgXgProbabilities.getAwayWinProbability() - avgXgaProbabilities.getAwayWinProbability()));

        SimulateMatches.ResultProbabilities avgGoalProbabilities = SimulateMatches.createResultProbabilities(homeSeason.getAvgGoalsFor(ALL_GAMES), awaySeason.getAvgGoalsFor(ALL_GAMES));
        SimulateMatches.ResultProbabilities avgGoalsAgainstProbabilities = SimulateMatches.createResultProbabilities(homeSeason.getAvgGoalsAgainst(ALL_GAMES), awaySeason.getAvgGoalsAgainst(ALL_GAMES));
        features.add((avgGoalProbabilities.getHomeWinProbability() - avgGoalsAgainstProbabilities.getHomeWinProbability()) - (avgGoalProbabilities.getAwayWinProbability() - avgGoalsAgainstProbabilities.getAwayWinProbability()));

        features.add(homeSeason.getFormXGF(ALL_GAMES)-awaySeason.getFormXGF(ALL_GAMES));
        features.add(homeSeason.getAvgPoints(ALL_GAMES) - awaySeason.getAvgPoints(ALL_GAMES));
        features.add(homeSeason.getAvgPointsOverLastXGames(ALL_GAMES, 5) - awaySeason.getAvgPointsOverLastXGames(ALL_GAMES, 5));

        features.add(homeSeason.getAvgPoints(ALL_GAMES));
        features.add(homeSeason.getAvgPointsOverLastXGames(ALL_GAMES, 5));
        features.add(awaySeason.getAvgPoints(ALL_GAMES));
        features.add(awaySeason.getAvgPointsOverLastXGames(ALL_GAMES, 5));
        features.add(awayTeam.getPointsOfLastMatchups(homeTeam.getTeamName(), ALL_GAMES, awaySeason.getSeasonYearStart()-2));
        features.add(homeSeason.getAvgGoalsFor(ALL_GAMES));
        features.add(awaySeason.getAvgGoalsFor(ALL_GAMES));
        features.add(homeSeason.getAvgXGF(ALL_GAMES));
        features.add(awaySeason.getAvgXGF(ALL_GAMES));
        features.add(homeSeason.getAvgXGA(ALL_GAMES));
        features.add(awaySeason.getAvgXGA(ALL_GAMES));
        features.add(homeSeason.getFormXGF(ALL_GAMES));
        features.add(awaySeason.getFormXGF(ALL_GAMES));

        return features;
    }

}
