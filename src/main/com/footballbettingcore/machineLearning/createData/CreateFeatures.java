package com.footballbettingcore.machineLearning.createData;

import java.util.ArrayList;
import com.footballbettingcore.machineLearning.createData.classes.TrainingTeam;
import com.footballbettingcore.machineLearning.createData.classes.TrainingTeamsSeason;
import static com.footballbettingcore.machineLearning.createData.classes.GamesSelector.*;

public class CreateFeatures {
    // NN Ac: 51.3%, prof -4%. Log Reg: Ac: 52.2%, prof: 59%
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

    public static ArrayList<Double> getFeaturesSmallFromRCoeffMakesSense(TrainingTeam homeTeam, TrainingTeamsSeason homeSeason,
                                                                         TrainingTeam awayTeam, TrainingTeamsSeason awaySeason,
                                                                         double[] odds) {

        ArrayList<Double> features = new ArrayList<>();

        features.add(1/odds[0]);
        features.add(homeSeason.getOvrPlayerRating() - awaySeason.getOvrPlayerRating());
        features.add((homeSeason.getAvgPoints(ALL_GAMES) / homeSeason.getAvgPointsOfAllOpponentsGamesWholeSeason(ALL_GAMES))
                - (awaySeason.getAvgPoints(ALL_GAMES) / awaySeason.getAvgPointsOfAllOpponentsGamesWholeSeason(ALL_GAMES)));
        features.add(homeSeason.getAvgXGF(ALL_GAMES));
        features.add(awaySeason.getAvgXGF(ALL_GAMES));
        features.add(homeSeason.getFormXGF(ALL_GAMES));
        features.add(awaySeason.getFormXGF(ALL_GAMES));
        features.add(homeSeason.getAvgGoalsFor(ALL_GAMES));
        features.add(awaySeason.getAvgGoalsFor(ALL_GAMES));
        features.add(homeSeason.getAvgPoints(ALL_GAMES));
        features.add(awaySeason.getAvgPoints(ALL_GAMES));
        features.add(homeSeason.getAvgPointsOverLastXGames(ALL_GAMES, 5));
        features.add(awaySeason.getAvgPointsOverLastXGames(ALL_GAMES, 5));
        features.add(homeSeason.getAvgXGA(ALL_GAMES));
        features.add(awaySeason.getAvgXGA(ALL_GAMES));

        return features;
    }
}
