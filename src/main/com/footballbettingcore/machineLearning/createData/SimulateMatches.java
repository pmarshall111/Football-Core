package com.footballbettingcore.machineLearning.createData;

import com.footballbettingcore.machineLearning.createData.classes.TrainingMatch;
import org.apache.commons.math3.distribution.PoissonDistribution;

import java.util.ArrayList;

/*
 * Class looks at the XG of a game, and plugs that into a Poisson Distribution to get a probability distribution that Y number of
 * goals were scored given an XG value.
 */
public class SimulateMatches {
    private SimulateMatches() {}

    public static ArrayList<TrainingMatch> createSimulatedMatchesWithProbabilityOfResult(ArrayList<TrainingMatch> realMatches) {
        ArrayList<TrainingMatch> simulated = new ArrayList<>();
        for (TrainingMatch match: realMatches) {
            ResultProbabilities probs = createResultProbabilities(match.getHomeXG(), match.getAwayXG());
            TrainingMatch homeWinSimulation = match.clone();
            homeWinSimulation.setProbability(probs.getHomeWinProbability());
            homeWinSimulation.setHomeScore(1);
            homeWinSimulation.setAwayScore(0);
            TrainingMatch drawSimulation = match.clone();
            drawSimulation.setProbability(probs.getDrawProbability());
            drawSimulation.setHomeScore(0);
            drawSimulation.setAwayScore(0);
            TrainingMatch awayWinSimulation = match.clone();
            awayWinSimulation.setProbability(probs.getAwayWinProbability());
            awayWinSimulation.setHomeScore(0);
            awayWinSimulation.setAwayScore(1);

            simulated.add(homeWinSimulation);
            simulated.add(drawSimulation);
            simulated.add(awayWinSimulation);

            match.setXgSimulatedProbabilties(new double[]{probs.getHomeWinProbability(), probs.getDrawProbability(), probs.getAwayWinProbability()});
        }

        return simulated;
    }

    public static ArrayList<TrainingMatch> createSimulatedMatchesWithProbabilityOfScores(ArrayList<TrainingMatch> realMatches) {
        ArrayList<TrainingMatch> simulated = new ArrayList<>();
        for (TrainingMatch match: realMatches) {
            ScoreProbabilities scoreProbabilities = simulateMatch(match.getHomeXG(), match.getAwayXG());
            for (int i = 0; i<10; i++) {
                for (int j = 0; j<10; j++) {
                    double scoreProb = scoreProbabilities.getProbability(i, j);
                    if (scoreProb >= 0.01) { // not including really low probabilities so training set stays reasonably small
                        TrainingMatch matchSimulation = match.clone();
                        matchSimulation.setProbability(scoreProb);
                        matchSimulation.setHomeScore(i);
                        matchSimulation.setAwayScore(j);
                        simulated.add(matchSimulation);
                    }
                }
            }
        }

        return simulated;
    }

    public static ResultProbabilities createResultProbabilities(double homeXg, double awayXg) {
        ScoreProbabilities scoreProbabilities = simulateMatch(homeXg, awayXg);
        double homeWinProb = 0, drawWinProb = 0, awayWinProb = 0;
        for (int i = 0; i<12; i++) {
            for (int j = 0; j < 12; j++) {
                double scoreProb = scoreProbabilities.getProbability(i, j);
                if (i > j) {
                    homeWinProb += scoreProb;
                } else if (i == j) {
                    drawWinProb += scoreProb;
                } else {
                    awayWinProb += scoreProb;
                }
            }
        }
        return new ResultProbabilities(homeWinProb, drawWinProb, awayWinProb);
    }

    private static ScoreProbabilities simulateMatch(double homeXg, double awayXg) {
        var homePoissonDistribution = new PoissonDistribution(homeXg+0.0001); // addition to prevent mean of 0 error
        var awayPoissonDistribution = new PoissonDistribution(awayXg+0.0001);

        ScoreProbabilities scoreProbs = new ScoreProbabilities();
        for (int i = 0; i<12; i++) {
            for (int j = 0; j<12; j++) {
                double homeScoreProb = homePoissonDistribution.probability(i);
                double awayScoreProb = awayPoissonDistribution.probability(j);
                double scoreProb = homeScoreProb * awayScoreProb;
                scoreProbs.addScore(i, j, scoreProb);
            }
        }
        return scoreProbs;
    }

    private static class ScoreProbabilities {
        private ArrayList<ArrayList<Double>> scoreMatrix;

        public ScoreProbabilities() {
            this.scoreMatrix = new ArrayList<>();
        }

        public void addScore(int homeGoals, int awayGoals, double prob) {
            while (scoreMatrix.size() <= homeGoals) {
                this.scoreMatrix.add(new ArrayList<>());
            }
            this.scoreMatrix.get(homeGoals).add(awayGoals, prob);
        }

        public double getProbability(int homeScore, int awayScore) {
            return scoreMatrix.get(homeScore).get(awayScore);
        }
    }

    static class ResultProbabilities {
        private double homeWin;
        private double draw;
        private double awayWin;

        public ResultProbabilities(double homeWin, double draw, double awayWin) {
            this.homeWin = homeWin;
            this.draw = draw;
            this.awayWin = awayWin;
        }

        public double getHomeWinProbability() {
            return homeWin;
        }

        public double getDrawProbability() {
            return draw;
        }

        public double getAwayWinProbability() {
            return awayWin;
        }
    }
}
