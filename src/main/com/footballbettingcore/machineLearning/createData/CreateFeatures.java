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

        features.add(homeSeason.getFormXGF(ALL_GAMES) - awaySeason.getFormXGF(ALL_GAMES));
        features.add(homeSeason.getAvgPoints(ALL_GAMES) - awaySeason.getAvgPoints(ALL_GAMES));
        features.add(homeSeason.getAvgPointsOverLastXGames(ALL_GAMES, 5) - awaySeason.getAvgPointsOverLastXGames(ALL_GAMES, 5));

        features.add(homeSeason.getAvgPoints(ALL_GAMES));
        features.add(homeSeason.getAvgPointsOverLastXGames(ALL_GAMES, 5));
        features.add(awaySeason.getAvgPoints(ALL_GAMES));
        features.add(awaySeason.getAvgPointsOverLastXGames(ALL_GAMES, 5));
        features.add(homeSeason.getAvgGoalsFor(ALL_GAMES));
        features.add(awaySeason.getAvgGoalsFor(ALL_GAMES));
        features.add(homeSeason.getAvgXGF(ALL_GAMES));
        features.add(awaySeason.getAvgXGF(ALL_GAMES));features.add(homeSeason.getFormXGF(ALL_GAMES));
        features.add(awaySeason.getFormXGF(ALL_GAMES));

        return features;
    }

    public static ArrayList<Double> getAllFeatures(TrainingTeam homeTeam, TrainingTeamsSeason homeSeason,
                                                                TrainingTeam awayTeam, TrainingTeamsSeason awaySeason,
                                                                double[] odds) {

        // names of features in order: PlayerRatingDiff	PointsImportanceDiff	AvgXgProbabilityDiff	AvgGoalsProbabilityDiff	XGF diff	Points Diff	PointsLast5Diff	Home Points	Home Points last 5	Away Points	Away Points last 5	PointsOfLastMatchups	HomeGoalsFor	AwayGoalsFor	HomeXGF	AwayXGF	HomeXGA	AwayXGA	HomeFormXGF	AwayFormXGF	WeightedXgDiff	FormWeightedGoalsDiff	FormWeightedXgDiff	PointsWhenConceededFirstDiff	WeightedXgProbabilityDiff	WeightedGoalsProbabilityDiff	ShotsOnTargetDiff	FormXgDiff	FormGoalsDiff	XgfDivPossDiff	XgaDivPossDiff

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

        features.add(homeSeason.getFormXGF(ALL_GAMES) - awaySeason.getFormXGF(ALL_GAMES));
        features.add(homeSeason.getAvgPoints(ALL_GAMES) - awaySeason.getAvgPoints(ALL_GAMES));
        features.add(homeSeason.getAvgPointsOverLastXGames(ALL_GAMES, 5) - awaySeason.getAvgPointsOverLastXGames(ALL_GAMES, 5));

        features.add(homeSeason.getAvgPoints(ALL_GAMES));
        features.add(homeSeason.getAvgPointsOverLastXGames(ALL_GAMES, 5));
        features.add(awaySeason.getAvgPoints(ALL_GAMES)); // 10
        features.add(awaySeason.getAvgPointsOverLastXGames(ALL_GAMES, 5));
        features.add(awayTeam.getPointsOfLastMatchups(homeTeam.getTeamName(), ALL_GAMES, awaySeason.getSeasonYearStart()-2));
        features.add(homeSeason.getAvgGoalsFor(ALL_GAMES));
        features.add(awaySeason.getAvgGoalsFor(ALL_GAMES));
        features.add(homeSeason.getAvgXGF(ALL_GAMES));
        features.add(awaySeason.getAvgXGF(ALL_GAMES));
        features.add(homeSeason.getAvgXGA(ALL_GAMES));
        features.add(awaySeason.getAvgXGA(ALL_GAMES));
        features.add(homeSeason.getFormXGF(ALL_GAMES));
        features.add(awaySeason.getFormXGF(ALL_GAMES)); //20

        features.add((homeSeason.getWeightedAvgXGF(ALL_GAMES) - awaySeason.getWeightedAvgXGA(ALL_GAMES)) -
                (awaySeason.getWeightedAvgXGF(ALL_GAMES) - homeSeason.getWeightedAvgXGA(ALL_GAMES)));

        features.add((homeSeason.getFormWeightedGoalsFor(ALL_GAMES) - awaySeason.getFormGoalsAgainst(ALL_GAMES)) -
                (awaySeason.getFormWeightedGoalsFor(ALL_GAMES) - homeSeason.getFormWeightedGoalsAgainst(ALL_GAMES)));

        features.add((homeSeason.getAvgFormWeightedXGF(ALL_GAMES, 5) - awaySeason.getAvgFormWeightedXGA(ALL_GAMES, 5)) -
                (awaySeason.getAvgFormWeightedXGF(ALL_GAMES, 5) - homeSeason.getAvgFormWeightedXGA(ALL_GAMES, 5)));

        features.add(homeSeason.getAvgPointsWhenConceededFirst(ALL_GAMES) - awaySeason.getAvgPointsWhenConceededFirst(ALL_GAMES));
        SimulateMatches.ResultProbabilities avgWeightedXgProbabilities = SimulateMatches.createResultProbabilities(homeSeason.getWeightedAvgXGF(ALL_GAMES), awaySeason.getWeightedAvgXGF(ALL_GAMES));
        SimulateMatches.ResultProbabilities avgWeightedXgaProbabilities = SimulateMatches.createResultProbabilities(homeSeason.getWeightedAvgXGA(ALL_GAMES), awaySeason.getWeightedAvgXGA(ALL_GAMES));
        features.add((avgWeightedXgProbabilities.getHomeWinProbability() - avgWeightedXgaProbabilities.getHomeWinProbability()) - (avgWeightedXgProbabilities.getAwayWinProbability() - avgWeightedXgaProbabilities.getAwayWinProbability()));

        SimulateMatches.ResultProbabilities avgWeightedGoalProbabilities = SimulateMatches.createResultProbabilities(homeSeason.getWeightedAvgGoalsFor(ALL_GAMES), awaySeason.getWeightedAvgGoalsFor(ALL_GAMES));
        SimulateMatches.ResultProbabilities avgWeightedGoalsAgainstProbabilities = SimulateMatches.createResultProbabilities(homeSeason.getWeightedAvgGoalsAgainst(ALL_GAMES), awaySeason.getWeightedAvgGoalsAgainst(ALL_GAMES));
        features.add((avgWeightedGoalProbabilities.getHomeWinProbability() - avgWeightedGoalsAgainstProbabilities.getHomeWinProbability()) - (avgWeightedGoalProbabilities.getAwayWinProbability() - avgWeightedGoalsAgainstProbabilities.getAwayWinProbability()));

        features.add(homeSeason.getAvgShotsOnTarget(ALL_GAMES) - awaySeason.getAvgShotsOnTarget(ALL_GAMES));


        features.add((homeSeason.getFormXGF(ALL_GAMES) - homeSeason.getFormXGA(ALL_GAMES)) - (awaySeason.getFormXGF(ALL_GAMES) - awaySeason.getFormXGA(ALL_GAMES)));

        features.add((homeSeason.getFormGoalsFor(ALL_GAMES) - homeSeason.getFormGoalsAgainst(ALL_GAMES)) - (awaySeason.getFormGoalsFor(ALL_GAMES) - awaySeason.getFormGoalsAgainst(ALL_GAMES)));

        features.add((homeSeason.getAvgXGF(ALL_GAMES)/homeSeason.getAvgPossession(ALL_GAMES)) - (awaySeason.getAvgXGF(ALL_GAMES)/awaySeason.getAvgPossession(ALL_GAMES))); //30

        features.add((homeSeason.getAvgXGA(ALL_GAMES)/(1-homeSeason.getAvgPossession(ALL_GAMES))) - (awaySeason.getAvgXGA(ALL_GAMES)/(1-awaySeason.getAvgPossession(ALL_GAMES))));

        features.add(homeSeason.getAvgGoalsFor(ALL_GAMES)/homeSeason.getAvgPointsOfAllOpponentsGamesWholeSeason(ALL_GAMES) -
        awaySeason.getAvgGoalsFor(ALL_GAMES)/awaySeason.getAvgPointsOfAllOpponentsGamesWholeSeason(ALL_GAMES));

        features.add(homeSeason.getAvgGoalsAgainst(ALL_GAMES)/homeSeason.getAvgPointsOfAllOpponentsGamesWholeSeason(ALL_GAMES) -
                awaySeason.getAvgGoalsAgainst(ALL_GAMES)/awaySeason.getAvgPointsOfAllOpponentsGamesWholeSeason(ALL_GAMES));

        features.add((homeSeason.getAvgGoalsFor(ALL_GAMES)/homeSeason.getAvgPointsOfAllOpponentsGamesWholeSeason(ALL_GAMES) - awaySeason.getAvgGoalsAgainst(ALL_GAMES)/awaySeason.getAvgPointsOfAllOpponentsGamesWholeSeason(ALL_GAMES)) -
                awaySeason.getAvgGoalsFor(ALL_GAMES)/awaySeason.getAvgPointsOfAllOpponentsGamesWholeSeason(ALL_GAMES) - homeSeason.getAvgGoalsAgainst(ALL_GAMES)/homeSeason.getAvgPointsOfAllOpponentsGamesWholeSeason(ALL_GAMES));

        features.add(homeSeason.getAvgXGF(ALL_GAMES)/homeSeason.getAvgPointsOfAllOpponentsGamesWholeSeason(ALL_GAMES) -
                awaySeason.getAvgXGF(ALL_GAMES)/awaySeason.getAvgPointsOfAllOpponentsGamesWholeSeason(ALL_GAMES));

        features.add(homeSeason.getAvgXGA(ALL_GAMES)/homeSeason.getAvgPointsOfAllOpponentsGamesWholeSeason(ALL_GAMES) -
                awaySeason.getAvgXGA(ALL_GAMES)/awaySeason.getAvgPointsOfAllOpponentsGamesWholeSeason(ALL_GAMES));

        features.add((homeSeason.getAvgXGF(ALL_GAMES)/homeSeason.getAvgPointsOfAllOpponentsGamesWholeSeason(ALL_GAMES) - awaySeason.getAvgXGA(ALL_GAMES)/awaySeason.getAvgPointsOfAllOpponentsGamesWholeSeason(ALL_GAMES)) -
                awaySeason.getAvgXGF(ALL_GAMES)/awaySeason.getAvgPointsOfAllOpponentsGamesWholeSeason(ALL_GAMES) - homeSeason.getAvgXGA(ALL_GAMES)/homeSeason.getAvgPointsOfAllOpponentsGamesWholeSeason(ALL_GAMES));

        // not expecting high r coefficient here, but could be useful for training the draw log reg
        features.add((avgXgProbabilities.getDrawProbability()));
        features.add((avgXgaProbabilities.getDrawProbability()));
        features.add((avgXgProbabilities.getDrawProbability() + avgXgaProbabilities.getDrawProbability())); //40

        // points when scored first
        features.add(homeSeason.getAvgPointsWhenScoredFirst(ALL_GAMES));
        features.add(awaySeason.getAvgPointsWhenScoredFirst(ALL_GAMES));
        features.add(homeSeason.getAvgPointsWhenScoredFirst(ALL_GAMES) - awaySeason.getAvgPointsWhenScoredFirst(ALL_GAMES));

        features.add(homeSeason.getAvgFormWeightedXGF(ALL_GAMES, 5));
        features.add(awaySeason.getAvgFormWeightedXGF(ALL_GAMES, 5));

        features.add(homeSeason.getWeightedAvgXGF(ALL_GAMES));
        features.add(awaySeason.getWeightedAvgXGF(ALL_GAMES));

        features.add(homeSeason.getAvgNumberOfCleanSheets(ALL_GAMES));
        features.add(awaySeason.getAvgNumberOfCleanSheets(ALL_GAMES));
        features.add(homeSeason.getAvgNumberOfCleanSheets(ALL_GAMES) - awaySeason.getAvgNumberOfCleanSheets(ALL_GAMES));

        features.add(homeSeason.getHomeXgAttackStrength());
        features.add(awaySeason.getAwayXgAttackStrength());
        features.add(homeSeason.getHomeXgAttackStrength() - awaySeason.getAwayXgAttackStrength());
        features.add(homeSeason.getHomeXgAttackStrength()*homeSeason.getHomeXgDefenseStrength()*1.3 -
                awaySeason.getAwayXgAttackStrength()*awaySeason.getAwayXgDefenseStrength());
        features.add(homeSeason.getHomeXgAttackStrength()*homeSeason.getHomeXgDefenseStrength() -
                awaySeason.getAwayXgAttackStrength()*awaySeason.getAwayXgDefenseStrength());

        features.add(homeSeason.getAvgPossession(ALL_GAMES));
        features.add(awaySeason.getAvgPossession(ALL_GAMES));
        features.add(homeSeason.getAvgPossession(ALL_GAMES) - awaySeason.getAvgPossession(ALL_GAMES));

        features.add(homeSeason.getAvgPossession(ALL_GAMES)/homeSeason.getAvgPointsOfAllOpponentsGamesWholeSeason(ALL_GAMES));
        features.add(awaySeason.getAvgPossession(ALL_GAMES)/awaySeason.getAvgPointsOfAllOpponentsGamesWholeSeason(ALL_GAMES));
        features.add(homeSeason.getAvgPossession(ALL_GAMES)/homeSeason.getAvgPointsOfAllOpponentsGamesWholeSeason(ALL_GAMES) -
                awaySeason.getAvgPossession(ALL_GAMES)/awaySeason.getAvgPointsOfAllOpponentsGamesWholeSeason(ALL_GAMES));

        features.add(homeSeason.getAvgXGF(ALL_GAMES)/homeSeason.getAvgShots(ALL_GAMES));
        features.add(awaySeason.getAvgXGF(ALL_GAMES)/awaySeason.getAvgShots(ALL_GAMES));

        features.add(homeSeason.getAvgXGF(ALL_GAMES)/homeSeason.getAvgShots(ALL_GAMES) - awaySeason.getAvgXGF(ALL_GAMES)/awaySeason.getAvgShots(ALL_GAMES));

        features.add(homeSeason.getAvgXGF(ONLY_HOME_GAMES));
        features.add(awaySeason.getAvgXGF(ONLY_AWAY_GAMES));
        features.add((homeSeason.getAvgXGF(ONLY_HOME_GAMES) - awaySeason.getAvgXGA(ONLY_AWAY_GAMES)) -
                (awaySeason.getAvgXGF(ONLY_AWAY_GAMES) - homeSeason.getAvgXGA(ONLY_HOME_GAMES)));

        features.add((homeSeason.getAvgXGF(ALL_GAMES) - awaySeason.getAvgXGA(ALL_GAMES)) -
                (awaySeason.getAvgXGF(ALL_GAMES) - homeSeason.getAvgXGA(ALL_GAMES)));

        SimulateMatches.ResultProbabilities avgXgHomeAwayProbabilities = SimulateMatches.createResultProbabilities(homeSeason.getAvgXGF(ONLY_HOME_GAMES), awaySeason.getAvgXGF(ONLY_AWAY_GAMES));
        SimulateMatches.ResultProbabilities avgXgaHomeAwayProbabilities = SimulateMatches.createResultProbabilities(homeSeason.getAvgXGA(ONLY_HOME_GAMES), awaySeason.getAvgXGA(ONLY_AWAY_GAMES));
        features.add((avgXgHomeAwayProbabilities.getHomeWinProbability() - avgXgaHomeAwayProbabilities.getHomeWinProbability()) - (avgXgHomeAwayProbabilities.getAwayWinProbability() - avgXgaHomeAwayProbabilities.getAwayWinProbability()));

        features.add(homeSeason.getAvgPoints(ONLY_HOME_GAMES));
        features.add(awaySeason.getAvgPoints(ONLY_AWAY_GAMES));
        features.add(homeSeason.getAvgPoints(ONLY_HOME_GAMES) - awaySeason.getAvgShots(ONLY_AWAY_GAMES));

        features.add(homeSeason.getWeightedAvgPoints(ALL_GAMES));
        features.add(awaySeason.getWeightedAvgPoints(ALL_GAMES));
        features.add(homeSeason.getWeightedAvgPoints(ALL_GAMES) - awaySeason.getWeightedAvgPoints(ALL_GAMES));

        features.add(homeSeason.getWeightedAvgPoints(ONLY_HOME_GAMES));
        features.add(awaySeason.getWeightedAvgPoints(ONLY_AWAY_GAMES));
        features.add(homeSeason.getWeightedAvgPoints(ONLY_HOME_GAMES) - awaySeason.getWeightedAvgPoints(ONLY_AWAY_GAMES));

        features.add(homeSeason.getAvgShots(ALL_GAMES)/homeSeason.getAvgPossession(ALL_GAMES));
        features.add(awaySeason.getAvgShots(ALL_GAMES)/awaySeason.getAvgPossession(ALL_GAMES));

        features.add(homeSeason.getAvgShotsOnTarget(ALL_GAMES)/homeSeason.getAvgShots(ALL_GAMES));
        features.add(awaySeason.getAvgShotsOnTarget(ALL_GAMES)/awaySeason.getAvgShots(ALL_GAMES));

        features.add(homeSeason.getAvgXGF(ALL_GAMES)*homeSeason.getAvgShotsOnTarget(ALL_GAMES)/homeSeason.getAvgShots(ALL_GAMES));
        features.add(awaySeason.getAvgXGF(ALL_GAMES)*awaySeason.getAvgShotsOnTarget(ALL_GAMES)/awaySeason.getAvgShots(ALL_GAMES));


        SimulateMatches.ResultProbabilities shotsOnTargetAvgXgProbabilities = SimulateMatches.createResultProbabilities(
                homeSeason.getAvgXGF(ALL_GAMES)*homeSeason.getAvgShotsOnTarget(ALL_GAMES)/homeSeason.getAvgShots(ALL_GAMES)
                , awaySeason.getAvgXGF(ALL_GAMES)*awaySeason.getAvgShotsOnTarget(ALL_GAMES)/awaySeason.getAvgShots(ALL_GAMES));
        features.add(shotsOnTargetAvgXgProbabilities.getHomeWinProbability());

        features.add(homeSeason.getAvgXGF(ALL_GAMES) + awaySeason.getAvgXGF(ALL_GAMES) + homeSeason.getAvgXGA(ALL_GAMES) + awaySeason.getAvgXGA(ALL_GAMES));

        features.add(homeSeason.getXgVsOppDifficulty());
        features.add(awaySeason.getXgVsOppDifficulty());
        features.add(homeSeason.getXgVsOppDifficulty() - awaySeason.getXgVsOppDifficulty());

        features.add(homeSeason.getXgVsOppDifficultyWeighted());
        features.add(awaySeason.getXgVsOppDifficultyWeighted());
        features.add(homeSeason.getXgVsOppDifficultyWeighted() - awaySeason.getXgVsOppDifficultyWeighted());

        return features;
    }

    public static ArrayList<Double> getFeaturesFromDissertration(TrainingTeam homeTeam, TrainingTeamsSeason homeSeason,
                                                       TrainingTeam awayTeam, TrainingTeamsSeason awaySeason) {

        // Features are added in the order the RFE outputs. Most important features coming first
        ArrayList<Double> features = new ArrayList<>();


        SimulateMatches.ResultProbabilities avgXgProbabilities = SimulateMatches.createResultProbabilities(homeSeason.getAvgXGF(ALL_GAMES), awaySeason.getAvgXGF(ALL_GAMES));
        SimulateMatches.ResultProbabilities avgXgaProbabilities = SimulateMatches.createResultProbabilities(homeSeason.getAvgXGA(ALL_GAMES), awaySeason.getAvgXGA(ALL_GAMES));
        features.add((avgXgProbabilities.getHomeWinProbability() - avgXgaProbabilities.getHomeWinProbability()) - (avgXgProbabilities.getAwayWinProbability() - avgXgaProbabilities.getAwayWinProbability()));

        features.add(homeSeason.getAvgShotsOnTarget(ALL_GAMES) - awaySeason.getAvgShotsOnTarget(ALL_GAMES));

        features.add(homeSeason.getAvgGoalsFor(ALL_GAMES)/homeSeason.getAvgPointsOfAllOpponentsGamesWholeSeason(ALL_GAMES) -
                awaySeason.getAvgGoalsFor(ALL_GAMES)/awaySeason.getAvgPointsOfAllOpponentsGamesWholeSeason(ALL_GAMES));

        features.add(homeSeason.getAvgPossession(ALL_GAMES) - awaySeason.getAvgPossession(ALL_GAMES));

        SimulateMatches.ResultProbabilities shotsOnTargetAvgXgProbabilities = SimulateMatches.createResultProbabilities(
                homeSeason.getAvgXGF(ALL_GAMES)*homeSeason.getAvgShotsOnTarget(ALL_GAMES)/homeSeason.getAvgShots(ALL_GAMES)
                , awaySeason.getAvgXGF(ALL_GAMES)*awaySeason.getAvgShotsOnTarget(ALL_GAMES)/awaySeason.getAvgShots(ALL_GAMES));
        features.add(shotsOnTargetAvgXgProbabilities.getHomeWinProbability());


        features.add(homeSeason.getAvgXGA(ALL_GAMES)/homeSeason.getAvgPointsOfAllOpponentsGamesWholeSeason(ALL_GAMES) -
                awaySeason.getAvgXGA(ALL_GAMES)/awaySeason.getAvgPointsOfAllOpponentsGamesWholeSeason(ALL_GAMES));

        features.add((homeSeason.getFormGoalsFor(ALL_GAMES) - homeSeason.getFormGoalsAgainst(ALL_GAMES)) - (awaySeason.getFormGoalsFor(ALL_GAMES) - awaySeason.getFormGoalsAgainst(ALL_GAMES)));

        features.add((homeSeason.getAvgXGF(ALL_GAMES)/homeSeason.getAvgPossession(ALL_GAMES)) - (awaySeason.getAvgXGF(ALL_GAMES)/awaySeason.getAvgPossession(ALL_GAMES)));

        features.add(homeSeason.getAvgXGF(ALL_GAMES)/homeSeason.getAvgShots(ALL_GAMES) - awaySeason.getAvgXGF(ALL_GAMES)/awaySeason.getAvgShots(ALL_GAMES));

        features.add(homeSeason.getWeightedAvgPoints(ONLY_HOME_GAMES) - awaySeason.getWeightedAvgPoints(ONLY_AWAY_GAMES));

        features.add(homeSeason.getAvgPointsWhenConceededFirst(ALL_GAMES) - awaySeason.getAvgPointsWhenConceededFirst(ALL_GAMES));

        features.add(awaySeason.getAvgXGF(ALL_GAMES));

        features.add((avgXgProbabilities.getDrawProbability() + avgXgaProbabilities.getDrawProbability()));

//        features.add(homeSeason.getAvgNumberOfCleanSheets(ALL_GAMES) - awaySeason.getAvgNumberOfCleanSheets(ALL_GAMES));

        features.add(homeSeason.getAvgGoalsAgainst(ALL_GAMES)/homeSeason.getAvgPointsOfAllOpponentsGamesWholeSeason(ALL_GAMES) -
                awaySeason.getAvgGoalsAgainst(ALL_GAMES)/awaySeason.getAvgPointsOfAllOpponentsGamesWholeSeason(ALL_GAMES));
//        features.add((homeSeason.getAvgXGF(ALL_GAMES)/homeSeason.getAvgPossession(ALL_GAMES)) - (awaySeason.getAvgXGF(ALL_GAMES)/awaySeason.getAvgPossession(ALL_GAMES))); //30

//        features.add(homeSeason.getAvgNumberOfCleanSheets(ALL_GAMES) - awaySeason.getAvgNumberOfCleanSheets(ALL_GAMES));

        features.add(homeSeason.getHomeXgAttackStrength() - awaySeason.getAwayXgAttackStrength());

        return features;
    }

    public static ArrayList<Double> getFeaturesNewDissertation(TrainingTeam homeTeam, TrainingTeamsSeason homeSeason,
                                                                     TrainingTeam awayTeam, TrainingTeamsSeason awaySeason) {

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

        features.add(homeSeason.getFormXGF(ALL_GAMES) - awaySeason.getFormXGF(ALL_GAMES));
        features.add(homeSeason.getAvgPoints(ALL_GAMES) - awaySeason.getAvgPoints(ALL_GAMES));

        features.add(homeSeason.getAvgShotsOnTarget(ALL_GAMES) - awaySeason.getAvgShotsOnTarget(ALL_GAMES));
        features.add(homeSeason.getAvgGoalsFor(ALL_GAMES)/homeSeason.getAvgPointsOfAllOpponentsGamesWholeSeason(ALL_GAMES) -
                awaySeason.getAvgGoalsFor(ALL_GAMES)/awaySeason.getAvgPointsOfAllOpponentsGamesWholeSeason(ALL_GAMES));

        features.add(homeSeason.getAvgPossession(ALL_GAMES) - awaySeason.getAvgPossession(ALL_GAMES));

        features.add(homeSeason.getAvgXGA(ALL_GAMES)/homeSeason.getAvgPointsOfAllOpponentsGamesWholeSeason(ALL_GAMES) -
                awaySeason.getAvgXGA(ALL_GAMES)/awaySeason.getAvgPointsOfAllOpponentsGamesWholeSeason(ALL_GAMES));

        features.add(homeSeason.getHomeXgAttackStrength()*homeSeason.getHomeXgDefenseStrength() -
                awaySeason.getAwayXgAttackStrength()*awaySeason.getAwayXgDefenseStrength());

        features.add(homeSeason.getWeightedAvgPoints(ALL_GAMES) - awaySeason.getWeightedAvgPoints(ALL_GAMES));

        features.add(homeSeason.getAvgPointsWhenConceededFirst(ALL_GAMES) - awaySeason.getAvgPointsWhenConceededFirst(ALL_GAMES));

        features.add(homeSeason.getAvgPoints(ALL_GAMES));
        features.add(awaySeason.getAvgPoints(ALL_GAMES));
        features.add(homeSeason.getAvgXGF(ALL_GAMES));
        features.add(awaySeason.getAvgXGF(ALL_GAMES));

        features.add(homeSeason.getAvgNumberOfCleanSheets(ALL_GAMES) - awaySeason.getAvgNumberOfCleanSheets(ALL_GAMES));

        features.add(homeSeason.getAvgPointsWhenScoredFirst(ALL_GAMES) - awaySeason.getAvgPointsWhenScoredFirst(ALL_GAMES));

        features.add(homeSeason.getAvgXGA(ALL_GAMES));
        features.add(awaySeason.getAvgXGA(ALL_GAMES));
        features.add(homeSeason.getFormXGF(ALL_GAMES));
        features.add(awaySeason.getFormXGF(ALL_GAMES));

        features.add(homeSeason.getXgVsOppDifficultyWeighted() - awaySeason.getXgVsOppDifficultyWeighted());

        return features;

    }

    public static ArrayList<Double> getFeaturesFromCorrelationMatrix(TrainingTeam homeTeam, TrainingTeamsSeason homeSeason,
                                                       TrainingTeam awayTeam, TrainingTeamsSeason awaySeason) {

        ArrayList<Double> features = new ArrayList<>();

        features.add(homeSeason.getOvrPlayerRating() - awaySeason.getOvrPlayerRating());
//        features.add((homeSeason.getAvgPoints(ALL_GAMES) / homeSeason.getAvgPointsOfAllOpponentsGamesWholeSeason(ALL_GAMES))
//                - (awaySeason.getAvgPoints(ALL_GAMES) / awaySeason.getAvgPointsOfAllOpponentsGamesWholeSeason(ALL_GAMES)));

        SimulateMatches.ResultProbabilities avgXgProbabilities = SimulateMatches.createResultProbabilities(homeSeason.getAvgXGF(ALL_GAMES), awaySeason.getAvgXGF(ALL_GAMES));
        SimulateMatches.ResultProbabilities avgXgaProbabilities = SimulateMatches.createResultProbabilities(homeSeason.getAvgXGA(ALL_GAMES), awaySeason.getAvgXGA(ALL_GAMES));
        features.add((avgXgProbabilities.getHomeWinProbability() - avgXgaProbabilities.getHomeWinProbability()) - (avgXgProbabilities.getAwayWinProbability() - avgXgaProbabilities.getAwayWinProbability()));

//        SimulateMatches.ResultProbabilities avgGoalProbabilities = SimulateMatches.createResultProbabilities(homeSeason.getAvgGoalsFor(ALL_GAMES), awaySeason.getAvgGoalsFor(ALL_GAMES));
//        SimulateMatches.ResultProbabilities avgGoalsAgainstProbabilities = SimulateMatches.createResultProbabilities(homeSeason.getAvgGoalsAgainst(ALL_GAMES), awaySeason.getAvgGoalsAgainst(ALL_GAMES));
//        features.add((avgGoalProbabilities.getHomeWinProbability() - avgGoalsAgainstProbabilities.getHomeWinProbability()) - (avgGoalProbabilities.getAwayWinProbability() - avgGoalsAgainstProbabilities.getAwayWinProbability()));

//        features.add(homeSeason.getFormXGF(ALL_GAMES) - awaySeason.getFormXGF(ALL_GAMES));
        features.add(homeSeason.getAvgPoints(ALL_GAMES) - awaySeason.getAvgPoints(ALL_GAMES));
//        features.add(homeSeason.getAvgPointsOverLastXGames(ALL_GAMES, 5) - awaySeason.getAvgPointsOverLastXGames(ALL_GAMES, 5));

        features.add(homeSeason.getAvgPoints(ALL_GAMES));
//        features.add(homeSeason.getAvgPointsOverLastXGames(ALL_GAMES, 5));
        features.add(awaySeason.getAvgPoints(ALL_GAMES)); // 10
//        features.add(awaySeason.getAvgPointsOverLastXGames(ALL_GAMES, 5));
//        features.add(awayTeam.getPointsOfLastMatchups(homeTeam.getTeamName(), ALL_GAMES, awaySeason.getSeasonYearStart()-2));
//        features.add(homeSeason.getAvgGoalsFor(ALL_GAMES));
//        features.add(awaySeason.getAvgGoalsFor(ALL_GAMES));
        features.add(homeSeason.getAvgXGF(ALL_GAMES));
        features.add(awaySeason.getAvgXGF(ALL_GAMES));
//        features.add(homeSeason.getAvgXGA(ALL_GAMES));
//        features.add(awaySeason.getAvgXGA(ALL_GAMES));
        features.add(homeSeason.getFormXGF(ALL_GAMES));
        features.add(awaySeason.getFormXGF(ALL_GAMES)); //20

//        features.add((homeSeason.getWeightedAvgXGF(ALL_GAMES) - awaySeason.getWeightedAvgXGA(ALL_GAMES)) -
//                (awaySeason.getWeightedAvgXGF(ALL_GAMES) - homeSeason.getWeightedAvgXGA(ALL_GAMES)));
//
//        features.add((homeSeason.getFormWeightedGoalsFor(ALL_GAMES) - awaySeason.getFormGoalsAgainst(ALL_GAMES)) -
//                (awaySeason.getFormWeightedGoalsFor(ALL_GAMES) - homeSeason.getFormWeightedGoalsAgainst(ALL_GAMES)));
//
//        features.add((homeSeason.getAvgFormWeightedXGF(ALL_GAMES, 3) - awaySeason.getAvgFormWeightedXGA(ALL_GAMES, 3)) -
//                (awaySeason.getAvgFormWeightedXGF(ALL_GAMES, 3) - homeSeason.getAvgFormWeightedXGA(ALL_GAMES, 3)));

        features.add(homeSeason.getAvgPointsWhenConceededFirst(ALL_GAMES) - awaySeason.getAvgPointsWhenConceededFirst(ALL_GAMES));
        SimulateMatches.ResultProbabilities avgWeightedXgProbabilities = SimulateMatches.createResultProbabilities(homeSeason.getWeightedAvgXGF(ALL_GAMES), awaySeason.getWeightedAvgXGF(ALL_GAMES));
        SimulateMatches.ResultProbabilities avgWeightedXgaProbabilities = SimulateMatches.createResultProbabilities(homeSeason.getWeightedAvgXGA(ALL_GAMES), awaySeason.getWeightedAvgXGA(ALL_GAMES));
        features.add((avgWeightedXgProbabilities.getHomeWinProbability() - avgWeightedXgaProbabilities.getHomeWinProbability()) - (avgWeightedXgProbabilities.getAwayWinProbability() - avgWeightedXgaProbabilities.getAwayWinProbability()));

        SimulateMatches.ResultProbabilities avgWeightedGoalProbabilities = SimulateMatches.createResultProbabilities(homeSeason.getWeightedAvgGoalsFor(ALL_GAMES), awaySeason.getWeightedAvgGoalsFor(ALL_GAMES));
        SimulateMatches.ResultProbabilities avgWeightedGoalsAgainstProbabilities = SimulateMatches.createResultProbabilities(homeSeason.getWeightedAvgGoalsAgainst(ALL_GAMES), awaySeason.getWeightedAvgGoalsAgainst(ALL_GAMES));
        features.add((avgWeightedGoalProbabilities.getHomeWinProbability() - avgWeightedGoalsAgainstProbabilities.getHomeWinProbability()) - (avgWeightedGoalProbabilities.getAwayWinProbability() - avgWeightedGoalsAgainstProbabilities.getAwayWinProbability()));

        features.add(homeSeason.getAvgShotsOnTarget(ALL_GAMES) - awaySeason.getAvgShotsOnTarget(ALL_GAMES));


//        features.add((homeSeason.getFormXGF(ALL_GAMES) - homeSeason.getFormXGA(ALL_GAMES)) - (awaySeason.getFormXGF(ALL_GAMES) - awaySeason.getFormXGA(ALL_GAMES)));

//        features.add((homeSeason.getFormGoalsFor(ALL_GAMES) - homeSeason.getFormGoalsAgainst(ALL_GAMES)) - (awaySeason.getFormGoalsFor(ALL_GAMES) - awaySeason.getFormGoalsAgainst(ALL_GAMES)));

        features.add((homeSeason.getAvgXGF(ALL_GAMES)/homeSeason.getAvgPossession(ALL_GAMES)) - (awaySeason.getAvgXGF(ALL_GAMES)/awaySeason.getAvgPossession(ALL_GAMES)));

//        features.add((homeSeason.getAvgXGA(ALL_GAMES)/(1-homeSeason.getAvgPossession(ALL_GAMES))) - (awaySeason.getAvgXGA(ALL_GAMES)/(1-awaySeason.getAvgPossession(ALL_GAMES))));

//        features.add(homeSeason.getAvgGoalsFor(ALL_GAMES)/homeSeason.getAvgPointsOfAllOpponentsGamesWholeSeason(ALL_GAMES) -
//                awaySeason.getAvgGoalsFor(ALL_GAMES)/awaySeason.getAvgPointsOfAllOpponentsGamesWholeSeason(ALL_GAMES));

//        features.add(homeSeason.getAvgGoalsAgainst(ALL_GAMES)/homeSeason.getAvgPointsOfAllOpponentsGamesWholeSeason(ALL_GAMES) -
//                awaySeason.getAvgGoalsAgainst(ALL_GAMES)/awaySeason.getAvgPointsOfAllOpponentsGamesWholeSeason(ALL_GAMES));

//        features.add((homeSeason.getAvgGoalsFor(ALL_GAMES)/homeSeason.getAvgPointsOfAllOpponentsGamesWholeSeason(ALL_GAMES) - awaySeason.getAvgGoalsAgainst(ALL_GAMES)/awaySeason.getAvgPointsOfAllOpponentsGamesWholeSeason(ALL_GAMES)) -
//                awaySeason.getAvgGoalsFor(ALL_GAMES)/awaySeason.getAvgPointsOfAllOpponentsGamesWholeSeason(ALL_GAMES) - homeSeason.getAvgGoalsAgainst(ALL_GAMES)/homeSeason.getAvgPointsOfAllOpponentsGamesWholeSeason(ALL_GAMES));

//        features.add(homeSeason.getAvgXGF(ALL_GAMES)/homeSeason.getAvgPointsOfAllOpponentsGamesWholeSeason(ALL_GAMES) -
//                awaySeason.getAvgXGF(ALL_GAMES)/awaySeason.getAvgPointsOfAllOpponentsGamesWholeSeason(ALL_GAMES));
//
//        features.add(homeSeason.getAvgXGA(ALL_GAMES)/homeSeason.getAvgPointsOfAllOpponentsGamesWholeSeason(ALL_GAMES) -
//                awaySeason.getAvgXGA(ALL_GAMES)/awaySeason.getAvgPointsOfAllOpponentsGamesWholeSeason(ALL_GAMES));

        features.add((homeSeason.getAvgXGF(ALL_GAMES)/homeSeason.getAvgPointsOfAllOpponentsGamesWholeSeason(ALL_GAMES) - awaySeason.getAvgXGA(ALL_GAMES)/awaySeason.getAvgPointsOfAllOpponentsGamesWholeSeason(ALL_GAMES)) -
                awaySeason.getAvgXGF(ALL_GAMES)/awaySeason.getAvgPointsOfAllOpponentsGamesWholeSeason(ALL_GAMES) - homeSeason.getAvgXGA(ALL_GAMES)/homeSeason.getAvgPointsOfAllOpponentsGamesWholeSeason(ALL_GAMES));



        return features;
    }

    public static ArrayList<Double> getFeaturesBest15(TrainingTeam homeTeam, TrainingTeamsSeason homeSeason,
                                                                     TrainingTeam awayTeam, TrainingTeamsSeason awaySeason) {

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

        features.add(homeSeason.getFormXGF(ALL_GAMES) - awaySeason.getFormXGF(ALL_GAMES));
        features.add(homeSeason.getAvgPoints(ALL_GAMES) - awaySeason.getAvgPoints(ALL_GAMES));
//        features.add(homeSeason.getAvgPointsOverLastXGames(ALL_GAMES, 5) - awaySeason.getAvgPointsOverLastXGames(ALL_GAMES, 5));

//        features.add(homeSeason.getAvgPoints(ALL_GAMES));
//        features.add(homeSeason.getAvgPointsOverLastXGames(ALL_GAMES, 5));
//        features.add(awaySeason.getAvgPoints(ALL_GAMES)); // 10
//        features.add(awaySeason.getAvgPointsOverLastXGames(ALL_GAMES, 5));
//        features.add(awayTeam.getPointsOfLastMatchups(homeTeam.getTeamName(), ALL_GAMES, awaySeason.getSeasonYearStart()-2));
//        features.add(homeSeason.getAvgGoalsFor(ALL_GAMES));
//        features.add(awaySeason.getAvgGoalsFor(ALL_GAMES));
//        features.add(homeSeason.getAvgXGF(ALL_GAMES));
//        features.add(awaySeason.getAvgXGF(ALL_GAMES));
//        features.add(homeSeason.getAvgXGA(ALL_GAMES));
//        features.add(awaySeason.getAvgXGA(ALL_GAMES));
//        features.add(homeSeason.getFormXGF(ALL_GAMES));
//        features.add(awaySeason.getFormXGF(ALL_GAMES)); //20

//        features.add((homeSeason.getWeightedAvgXGF(ALL_GAMES) - awaySeason.getWeightedAvgXGA(ALL_GAMES)) -
//                (awaySeason.getWeightedAvgXGF(ALL_GAMES) - homeSeason.getWeightedAvgXGA(ALL_GAMES)));

//        features.add((homeSeason.getFormWeightedGoalsFor(ALL_GAMES) - awaySeason.getFormGoalsAgainst(ALL_GAMES)) -
//                (awaySeason.getFormWeightedGoalsFor(ALL_GAMES) - homeSeason.getFormWeightedGoalsAgainst(ALL_GAMES)));

//        features.add((homeSeason.getAvgFormWeightedXGF(ALL_GAMES, 3) - awaySeason.getAvgFormWeightedXGA(ALL_GAMES, 3)) -
//                (awaySeason.getAvgFormWeightedXGF(ALL_GAMES, 3) - homeSeason.getAvgFormWeightedXGA(ALL_GAMES, 3)));

//        features.add(homeSeason.getAvgPointsWhenConceededFirst(ALL_GAMES) - awaySeason.getAvgPointsWhenConceededFirst(ALL_GAMES));
        SimulateMatches.ResultProbabilities avgWeightedXgProbabilities = SimulateMatches.createResultProbabilities(homeSeason.getWeightedAvgXGF(ALL_GAMES), awaySeason.getWeightedAvgXGF(ALL_GAMES));
        SimulateMatches.ResultProbabilities avgWeightedXgaProbabilities = SimulateMatches.createResultProbabilities(homeSeason.getWeightedAvgXGA(ALL_GAMES), awaySeason.getWeightedAvgXGA(ALL_GAMES));
        features.add((avgWeightedXgProbabilities.getHomeWinProbability() - avgWeightedXgaProbabilities.getHomeWinProbability()) - (avgWeightedXgProbabilities.getAwayWinProbability() - avgWeightedXgaProbabilities.getAwayWinProbability()));

        SimulateMatches.ResultProbabilities avgWeightedGoalProbabilities = SimulateMatches.createResultProbabilities(homeSeason.getWeightedAvgGoalsFor(ALL_GAMES), awaySeason.getWeightedAvgGoalsFor(ALL_GAMES));
        SimulateMatches.ResultProbabilities avgWeightedGoalsAgainstProbabilities = SimulateMatches.createResultProbabilities(homeSeason.getWeightedAvgGoalsAgainst(ALL_GAMES), awaySeason.getWeightedAvgGoalsAgainst(ALL_GAMES));
        features.add((avgWeightedGoalProbabilities.getHomeWinProbability() - avgWeightedGoalsAgainstProbabilities.getHomeWinProbability()) - (avgWeightedGoalProbabilities.getAwayWinProbability() - avgWeightedGoalsAgainstProbabilities.getAwayWinProbability()));

        features.add(homeSeason.getAvgShotsOnTarget(ALL_GAMES) - awaySeason.getAvgShotsOnTarget(ALL_GAMES));


        features.add((homeSeason.getFormXGF(ALL_GAMES) - homeSeason.getFormXGA(ALL_GAMES)) - (awaySeason.getFormXGF(ALL_GAMES) - awaySeason.getFormXGA(ALL_GAMES)));

        features.add((homeSeason.getFormGoalsFor(ALL_GAMES) - homeSeason.getFormGoalsAgainst(ALL_GAMES)) - (awaySeason.getFormGoalsFor(ALL_GAMES) - awaySeason.getFormGoalsAgainst(ALL_GAMES)));

//        features.add((homeSeason.getAvgXGF(ALL_GAMES)/homeSeason.getAvgPossession(ALL_GAMES)) - (awaySeason.getAvgXGF(ALL_GAMES)/awaySeason.getAvgPossession(ALL_GAMES)));

        features.add((homeSeason.getAvgXGA(ALL_GAMES)/(1-homeSeason.getAvgPossession(ALL_GAMES))) - (awaySeason.getAvgXGA(ALL_GAMES)/(1-awaySeason.getAvgPossession(ALL_GAMES))));

        features.add(homeSeason.getAvgGoalsFor(ALL_GAMES)/homeSeason.getAvgPointsOfAllOpponentsGamesWholeSeason(ALL_GAMES) -
                awaySeason.getAvgGoalsFor(ALL_GAMES)/awaySeason.getAvgPointsOfAllOpponentsGamesWholeSeason(ALL_GAMES));
//
//        features.add(homeSeason.getAvgGoalsAgainst(ALL_GAMES)/homeSeason.getAvgPointsOfAllOpponentsGamesWholeSeason(ALL_GAMES) -
//                awaySeason.getAvgGoalsAgainst(ALL_GAMES)/awaySeason.getAvgPointsOfAllOpponentsGamesWholeSeason(ALL_GAMES));
//
//        features.add((homeSeason.getAvgGoalsFor(ALL_GAMES)/homeSeason.getAvgPointsOfAllOpponentsGamesWholeSeason(ALL_GAMES) - awaySeason.getAvgGoalsAgainst(ALL_GAMES)/awaySeason.getAvgPointsOfAllOpponentsGamesWholeSeason(ALL_GAMES)) -
//                awaySeason.getAvgGoalsFor(ALL_GAMES)/awaySeason.getAvgPointsOfAllOpponentsGamesWholeSeason(ALL_GAMES) - homeSeason.getAvgGoalsAgainst(ALL_GAMES)/homeSeason.getAvgPointsOfAllOpponentsGamesWholeSeason(ALL_GAMES));
//
        features.add(homeSeason.getAvgXGF(ALL_GAMES)/homeSeason.getAvgPointsOfAllOpponentsGamesWholeSeason(ALL_GAMES) -
                awaySeason.getAvgXGF(ALL_GAMES)/awaySeason.getAvgPointsOfAllOpponentsGamesWholeSeason(ALL_GAMES));

        features.add(homeSeason.getAvgXGA(ALL_GAMES)/homeSeason.getAvgPointsOfAllOpponentsGamesWholeSeason(ALL_GAMES) -
                awaySeason.getAvgXGA(ALL_GAMES)/awaySeason.getAvgPointsOfAllOpponentsGamesWholeSeason(ALL_GAMES));

//        features.add((homeSeason.getAvgXGF(ALL_GAMES)/homeSeason.getAvgPointsOfAllOpponentsGamesWholeSeason(ALL_GAMES) - awaySeason.getAvgXGA(ALL_GAMES)/awaySeason.getAvgPointsOfAllOpponentsGamesWholeSeason(ALL_GAMES)) -
//                awaySeason.getAvgXGF(ALL_GAMES)/awaySeason.getAvgPointsOfAllOpponentsGamesWholeSeason(ALL_GAMES) - homeSeason.getAvgXGA(ALL_GAMES)/homeSeason.getAvgPointsOfAllOpponentsGamesWholeSeason(ALL_GAMES));



        return features;
    }

    public static ArrayList<Double> getFeaturesForDissertation(TrainingTeam homeTeam, TrainingTeamsSeason homeSeason,
                                                                     TrainingTeam awayTeam, TrainingTeamsSeason awaySeason) {

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

        features.add(homeSeason.getFormXGF(ALL_GAMES) - awaySeason.getFormXGF(ALL_GAMES));
        features.add(homeSeason.getAvgPoints(ALL_GAMES) - awaySeason.getAvgPoints(ALL_GAMES));
        features.add(homeSeason.getAvgPointsOverLastXGames(ALL_GAMES, 5) - awaySeason.getAvgPointsOverLastXGames(ALL_GAMES, 5));

        features.add(homeSeason.getAvgPoints(ALL_GAMES));
        features.add(homeSeason.getAvgPointsOverLastXGames(ALL_GAMES, 5));
        features.add(awaySeason.getAvgPoints(ALL_GAMES)); // 10
        features.add(awaySeason.getAvgPointsOverLastXGames(ALL_GAMES, 5));
        features.add(awayTeam.getPointsOfLastMatchups(homeTeam.getTeamName(), ALL_GAMES, awaySeason.getSeasonYearStart()-2));
        features.add(homeSeason.getAvgGoalsFor(ALL_GAMES));
        features.add(awaySeason.getAvgGoalsFor(ALL_GAMES));
        features.add(homeSeason.getAvgXGF(ALL_GAMES));
        features.add(awaySeason.getAvgXGF(ALL_GAMES));
        features.add(homeSeason.getAvgXGA(ALL_GAMES));
        features.add(awaySeason.getAvgXGA(ALL_GAMES));
        features.add(homeSeason.getFormXGF(ALL_GAMES));
        features.add(awaySeason.getFormXGF(ALL_GAMES)); //20

        features.add((homeSeason.getWeightedAvgXGF(ALL_GAMES) - awaySeason.getWeightedAvgXGA(ALL_GAMES)) -
                (awaySeason.getWeightedAvgXGF(ALL_GAMES) - homeSeason.getWeightedAvgXGA(ALL_GAMES)));

        features.add((homeSeason.getFormWeightedGoalsFor(ALL_GAMES) - awaySeason.getFormGoalsAgainst(ALL_GAMES)) -
                (awaySeason.getFormWeightedGoalsFor(ALL_GAMES) - homeSeason.getFormWeightedGoalsAgainst(ALL_GAMES)));

        features.add((homeSeason.getAvgFormWeightedXGF(ALL_GAMES, 3) - awaySeason.getAvgFormWeightedXGA(ALL_GAMES, 3)) -
                (awaySeason.getAvgFormWeightedXGF(ALL_GAMES, 3) - homeSeason.getAvgFormWeightedXGA(ALL_GAMES, 3)));

        features.add(homeSeason.getAvgPointsWhenConceededFirst(ALL_GAMES) - awaySeason.getAvgPointsWhenConceededFirst(ALL_GAMES));
        SimulateMatches.ResultProbabilities avgWeightedXgProbabilities = SimulateMatches.createResultProbabilities(homeSeason.getWeightedAvgXGF(ALL_GAMES), awaySeason.getWeightedAvgXGF(ALL_GAMES));
        SimulateMatches.ResultProbabilities avgWeightedXgaProbabilities = SimulateMatches.createResultProbabilities(homeSeason.getWeightedAvgXGA(ALL_GAMES), awaySeason.getWeightedAvgXGA(ALL_GAMES));
        features.add((avgWeightedXgProbabilities.getHomeWinProbability() - avgWeightedXgaProbabilities.getHomeWinProbability()) - (avgWeightedXgProbabilities.getAwayWinProbability() - avgWeightedXgaProbabilities.getAwayWinProbability()));

        SimulateMatches.ResultProbabilities avgWeightedGoalProbabilities = SimulateMatches.createResultProbabilities(homeSeason.getWeightedAvgGoalsFor(ALL_GAMES), awaySeason.getWeightedAvgGoalsFor(ALL_GAMES));
        SimulateMatches.ResultProbabilities avgWeightedGoalsAgainstProbabilities = SimulateMatches.createResultProbabilities(homeSeason.getWeightedAvgGoalsAgainst(ALL_GAMES), awaySeason.getWeightedAvgGoalsAgainst(ALL_GAMES));
        features.add((avgWeightedGoalProbabilities.getHomeWinProbability() - avgWeightedGoalsAgainstProbabilities.getHomeWinProbability()) - (avgWeightedGoalProbabilities.getAwayWinProbability() - avgWeightedGoalsAgainstProbabilities.getAwayWinProbability()));

        features.add(homeSeason.getAvgShotsOnTarget(ALL_GAMES) - awaySeason.getAvgShotsOnTarget(ALL_GAMES));


        features.add((homeSeason.getFormXGF(ALL_GAMES) - homeSeason.getFormXGA(ALL_GAMES)) - (awaySeason.getFormXGF(ALL_GAMES) - awaySeason.getFormXGA(ALL_GAMES)));

        features.add((homeSeason.getFormGoalsFor(ALL_GAMES) - homeSeason.getFormGoalsAgainst(ALL_GAMES)) - (awaySeason.getFormGoalsFor(ALL_GAMES) - awaySeason.getFormGoalsAgainst(ALL_GAMES)));

        features.add((homeSeason.getAvgXGF(ALL_GAMES)/homeSeason.getAvgPossession(ALL_GAMES)) - (awaySeason.getAvgXGF(ALL_GAMES)/awaySeason.getAvgPossession(ALL_GAMES)));

        features.add((homeSeason.getAvgXGA(ALL_GAMES)/(1-homeSeason.getAvgPossession(ALL_GAMES))) - (awaySeason.getAvgXGA(ALL_GAMES)/(1-awaySeason.getAvgPossession(ALL_GAMES))));

        features.add(homeSeason.getAvgGoalsFor(ALL_GAMES)/homeSeason.getAvgPointsOfAllOpponentsGamesWholeSeason(ALL_GAMES) -
                awaySeason.getAvgGoalsFor(ALL_GAMES)/awaySeason.getAvgPointsOfAllOpponentsGamesWholeSeason(ALL_GAMES));

        features.add(homeSeason.getAvgGoalsAgainst(ALL_GAMES)/homeSeason.getAvgPointsOfAllOpponentsGamesWholeSeason(ALL_GAMES) -
                awaySeason.getAvgGoalsAgainst(ALL_GAMES)/awaySeason.getAvgPointsOfAllOpponentsGamesWholeSeason(ALL_GAMES));

        features.add((homeSeason.getAvgGoalsFor(ALL_GAMES)/homeSeason.getAvgPointsOfAllOpponentsGamesWholeSeason(ALL_GAMES) - awaySeason.getAvgGoalsAgainst(ALL_GAMES)/awaySeason.getAvgPointsOfAllOpponentsGamesWholeSeason(ALL_GAMES)) -
                awaySeason.getAvgGoalsFor(ALL_GAMES)/awaySeason.getAvgPointsOfAllOpponentsGamesWholeSeason(ALL_GAMES) - homeSeason.getAvgGoalsAgainst(ALL_GAMES)/homeSeason.getAvgPointsOfAllOpponentsGamesWholeSeason(ALL_GAMES));

        features.add(homeSeason.getAvgXGF(ALL_GAMES)/homeSeason.getAvgPointsOfAllOpponentsGamesWholeSeason(ALL_GAMES) -
                awaySeason.getAvgXGF(ALL_GAMES)/awaySeason.getAvgPointsOfAllOpponentsGamesWholeSeason(ALL_GAMES));
//
        features.add(homeSeason.getAvgXGA(ALL_GAMES)/homeSeason.getAvgPointsOfAllOpponentsGamesWholeSeason(ALL_GAMES) -
                awaySeason.getAvgXGA(ALL_GAMES)/awaySeason.getAvgPointsOfAllOpponentsGamesWholeSeason(ALL_GAMES));
//
        features.add((homeSeason.getAvgXGF(ALL_GAMES)/homeSeason.getAvgPointsOfAllOpponentsGamesWholeSeason(ALL_GAMES) - awaySeason.getAvgXGA(ALL_GAMES)/awaySeason.getAvgPointsOfAllOpponentsGamesWholeSeason(ALL_GAMES)) -
                awaySeason.getAvgXGF(ALL_GAMES)/awaySeason.getAvgPointsOfAllOpponentsGamesWholeSeason(ALL_GAMES) - homeSeason.getAvgXGA(ALL_GAMES)/homeSeason.getAvgPointsOfAllOpponentsGamesWholeSeason(ALL_GAMES));



        return features;
    }
}
