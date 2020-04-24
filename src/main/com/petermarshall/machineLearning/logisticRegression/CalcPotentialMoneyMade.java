package com.petermarshall.machineLearning.logisticRegression;

import org.ejml.simple.SimpleMatrix;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class CalcPotentialMoneyMade {

    /*
     * First we create a new matrix with the odds the betting companies are offering based on their given probabilities.
     * Then method will loop through both odds matrices, and create a matrix with 1s where we place a bet and 0s otherwise.
     * We then elemMultiply our matrix where we place a bet by the odds to get a matrix with all our stakes. (Times by 5 to put £5 on)
     * Then we sum this matrix to get our total bets
     * We also go through matrix y, summing the cols with the correct result
     * Then calc profits.
     *
     * Note: We do not bet on draws.
     *
     * Delta is the change from our probabilities to the betters probabilities.
     * Gamma is how much higher our probabilities should be than the second highest prob.
     *
     * NOTE: HIGHESTWINNINGPROBBETTERTHANBETODDS IS WHAT WE USE WHEN WE PREDICT GAMES.
     */
    public static ArrayList<Profits> calcMoneyMade(SimpleMatrix betProbs, SimpleMatrix ourProbs, SimpleMatrix y, double delta, double gamma, double sigma, double stake) {
        ArrayList<Profits> results = new ArrayList<>();

        SimpleMatrix betOdds = turnProbabilityToOdds(betProbs);

        SimpleMatrix goodBetsHighestProb = new SimpleMatrix(betProbs.numRows(), betProbs.numCols());
        SimpleMatrix goodBetsHighestWinningProb = new SimpleMatrix(betProbs.numRows(), betProbs.numCols());
        SimpleMatrix goodBetsBetterThanBetOdds = new SimpleMatrix(betProbs.numRows(), betProbs.numCols());
        SimpleMatrix goodBetsHighestProbBetterThanBetOdds = new SimpleMatrix(betProbs.numRows(), betProbs.numCols());
        SimpleMatrix goodBetsHighestWinningProbBetterThanBetOdds = new SimpleMatrix(betProbs.numRows(), betProbs.numCols()); //What we use in our prediction database.
        SimpleMatrix goodBetsRatios = new SimpleMatrix(betProbs.numRows(), betProbs.numCols());
        SimpleMatrix goodBetsRatiosDivByBoth = new SimpleMatrix(betProbs.numRows(), betProbs.numCols());

        goodBetsHighestProb.fill(0);
        goodBetsHighestWinningProb.fill(0);
        goodBetsBetterThanBetOdds.fill(0);
        goodBetsHighestProbBetterThanBetOdds.fill(0);
        goodBetsHighestWinningProbBetterThanBetOdds.fill(0);
        goodBetsRatios.fill(0);
        goodBetsRatiosDivByBoth.fill(0);

        int highestProbMoneyOut = 0;
        int highestWinningProbMoneyOut = 0;
        int betterThanBetOddsMoneyOut = 0;
        int highestProbBetterThanBetOddsMoneyOut = 0;
        int highestWinningProbBetterThanBetOddsMoneyOut = 0;
        int betsRatiosMoneyOut = 0;
        int betsRatiosDivByBothMoneyOut = 0;

        for (int row = 0; row<betProbs.numRows(); row++) {

            //calculating the most likely columns
            int bestCol = -1;
            double highestProb = -9999;
            int secondBestCol = -1;
            double secondHighestProb = -9999;
            double thirdHighestProb = -9999;

            for (int col = 0; col<betProbs.numCols(); col++) {
                double ourProb = ourProbs.get(row,col);
                double bettersProb = betProbs.get(row,col);

                if (ourProb - bettersProb >= delta) {
                    goodBetsBetterThanBetOdds.set(row,col,stake);
                    betterThanBetOddsMoneyOut += stake;
                }

                if (ourProb > highestProb) {
                    //making sure we move the previous highest down to second highest.
                    thirdHighestProb = secondHighestProb;
                    secondBestCol = bestCol;
                    secondHighestProb = highestProb;
                    bestCol = col;
                    highestProb = ourProb;
                } else if (ourProb > secondHighestProb) {
                    thirdHighestProb = secondHighestProb;
                    secondBestCol = col;
                    secondHighestProb = ourProb;
                } else if (ourProb > thirdHighestProb) {
                    thirdHighestProb = ourProb;
                }
            }


            if (highestProb - betProbs.get(row, bestCol) > delta) {
                goodBetsBetterThanBetOdds.set(row, bestCol, stake);
                betterThanBetOddsMoneyOut += stake;
            }
            if (highestProb - secondHighestProb > gamma) {
                goodBetsHighestProb.set(row, bestCol, stake);
                highestProbMoneyOut += stake;

                if (bestCol != 1) {
                    goodBetsHighestWinningProb.set(row, bestCol, stake);
                    highestWinningProbMoneyOut += stake;
                }

                if (highestProb - betProbs.get(row, bestCol) > delta) {
                    goodBetsHighestProbBetterThanBetOdds.set(row, bestCol, stake);
                    highestProbBetterThanBetOddsMoneyOut += stake;

                    if (bestCol != 1) {
                        goodBetsHighestWinningProbBetterThanBetOdds.set(row, bestCol, stake);
                        highestWinningProbBetterThanBetOddsMoneyOut += stake;
                    }
                }
            }


            //simple ratio w/l
            double ourWinRatio = ourProbs.get(row,0)/ourProbs.get(row,2);
            double ourLossRatio = ourProbs.get(row,2)/ourProbs.get(row,0);
            double bookieWinRatio = betProbs.get(row,0)/betProbs.get(row,2);
            double bookieLossRatio = betProbs.get(row,2)/betProbs.get(row,0);

            //TODO: if needed, here is where we can alter the stake based on how much higher the win ratio is than the bookies.
            if (ourWinRatio - bookieWinRatio >= sigma) {
                goodBetsRatios.set(row,0, stake);
                betsRatiosMoneyOut += stake;
            }
            if (ourLossRatio - bookieLossRatio >= sigma) {
                goodBetsRatios.set(row,2, stake);
                betsRatiosMoneyOut += stake;
            }

            //ratio w/l+w
            double ourWinRatioDivByBoth = ourProbs.get(row,0)/(ourProbs.get(row,0) + ourProbs.get(row,2));
            double ourLossRatioDivByBoth = ourProbs.get(row,2)/(ourProbs.get(row,0) + ourProbs.get(row,2));
            double bookieWinRatioDivByBoth = betProbs.get(row,0)/(betProbs.get(row,0) + betProbs.get(row,2));
            double bookieLossRatioDivByBoth = betProbs.get(row,2)/(betProbs.get(row,0) + betProbs.get(row,2));

            if (ourWinRatioDivByBoth - bookieWinRatioDivByBoth >= sigma) {
                goodBetsRatiosDivByBoth.set(row,0, stake);
                betsRatiosDivByBothMoneyOut += stake;
            }
            if (ourLossRatioDivByBoth - bookieLossRatioDivByBoth >= sigma) {
                goodBetsRatiosDivByBoth.set(row,2, stake);
                betsRatiosDivByBothMoneyOut += stake;
            }

        }



        double betterThanBetOddsMoneyIn = 0;
        double highestProbMoneyIn = 0;
        double highestWinningProbMoneyIn = 0;
        double highestProbBetterThanBetOddsMoneyIn = 0;
        double highestWinningProbBetterThanBetOddsMoneyIn = 0;
        double betsRatiosMoneyIn = 0;
        double betsRatiosDivByBothMoneyIn = 0;

        for (int row = 0; row<goodBetsHighestProb.numRows(); row++) {

            int correctResultCol = (int) y.get(row, 0) - 1;

            betterThanBetOddsMoneyIn += goodBetsBetterThanBetOdds.get(row, correctResultCol) * betOdds.get(row, correctResultCol);
            highestProbMoneyIn += goodBetsHighestProb.get(row,correctResultCol) * betOdds.get(row, correctResultCol);
            highestWinningProbMoneyIn += goodBetsHighestWinningProb.get(row, correctResultCol) * betOdds.get(row, correctResultCol);
            highestProbBetterThanBetOddsMoneyIn += goodBetsHighestProbBetterThanBetOdds.get(row, correctResultCol) * betOdds.get(row, correctResultCol);
            highestWinningProbBetterThanBetOddsMoneyIn += goodBetsHighestWinningProbBetterThanBetOdds.get(row, correctResultCol) * betOdds.get(row, correctResultCol);
            betsRatiosMoneyIn += goodBetsRatios.get(row, correctResultCol) * betOdds.get(row, correctResultCol);
            betsRatiosDivByBothMoneyIn += goodBetsRatiosDivByBoth.get(row, correctResultCol) * betOdds.get(row, correctResultCol);
        }



        double betterThanBetOddsProfit = betterThanBetOddsMoneyIn - betterThanBetOddsMoneyOut;
        double highestProbProfit = highestProbMoneyIn - highestProbMoneyOut;
        double highestWinningProbProfit = highestWinningProbMoneyIn - highestWinningProbMoneyOut;
        double highestProbBetterThanBetOddsProfit = highestProbBetterThanBetOddsMoneyIn - highestProbBetterThanBetOddsMoneyOut;
        double highestWinningProbBetterThanBetOddsProfit = highestWinningProbBetterThanBetOddsMoneyIn - highestWinningProbBetterThanBetOddsMoneyOut;
        double goodRatiosProfit = betsRatiosMoneyIn - betsRatiosMoneyOut;
        double goodRatiosDivByBothProfit = betsRatiosDivByBothMoneyIn - betsRatiosDivByBothMoneyOut;

        results.add(new Profits((betterThanBetOddsProfit*100/betterThanBetOddsMoneyOut),betterThanBetOddsProfit, "Better than bet odds by " + delta + "%: £" + betterThanBetOddsProfit + ", " + (betterThanBetOddsProfit*100/betterThanBetOddsMoneyOut) + "%"));
        results.add(new Profits((highestProbProfit*100/highestProbMoneyOut),highestProbProfit, "Highest prob by " + gamma + ": £" + highestProbProfit + ", " + (highestProbProfit*100/highestProbMoneyOut) + "%"));
        results.add(new Profits((highestWinningProbProfit*100/highestWinningProbMoneyOut), highestWinningProbProfit,"Highest winning prob by " + gamma + ": £" + highestWinningProbProfit + ", " + (highestWinningProbProfit*100/highestWinningProbMoneyOut) + "%"));
        results.add(new Profits((highestProbBetterThanBetOddsProfit*100/highestProbBetterThanBetOddsMoneyOut), highestProbBetterThanBetOddsProfit, "Highest prob by " + gamma + " and also better than bet odds by " + delta + "%: £" + highestProbBetterThanBetOddsProfit + ", " + (highestProbBetterThanBetOddsProfit*100/highestProbBetterThanBetOddsMoneyOut) + "%"));
        results.add(new Profits((highestWinningProbBetterThanBetOddsProfit*100/highestWinningProbBetterThanBetOddsMoneyOut), highestWinningProbBetterThanBetOddsProfit,"Highest winning prob by " + gamma + " and also better than bet odds by " + delta + ": £" + highestWinningProbBetterThanBetOddsProfit + ", " + (highestWinningProbBetterThanBetOddsProfit*100/highestWinningProbBetterThanBetOddsMoneyOut) + "%"));
        results.add(new Profits((goodRatiosProfit*100/betsRatiosMoneyOut), goodRatiosProfit, "Better win/loss ratio or loss/win ratio by " + sigma + ": £" + goodRatiosProfit + ", " + (goodRatiosProfit*100/betsRatiosMoneyOut) + "%"));
        results.add(new Profits((goodRatiosDivByBothProfit*100/betsRatiosDivByBothMoneyOut), goodRatiosDivByBothProfit, "Better win/loss ratio or loss/win ratio by " + sigma + ": £" + goodRatiosDivByBothProfit + ", " + (goodRatiosDivByBothProfit*100/betsRatiosDivByBothMoneyOut) + "%"));

        Collections.sort(results, new Comparator<Profits>() {
            @Override
            public int compare(Profits o1, Profits o2) {
                double comp = o2.profitPerc - o1.profitPerc ;
                return comp > 0 ? 1 : comp < 0 ? -1 : 0;
            }
        });

        return results;
    }

    /*
     * Changes a probability to decimal odds by doing 1/probability.
     */
    private static SimpleMatrix turnProbabilityToOdds(SimpleMatrix bettersProbs) {
        SimpleMatrix ones = bettersProbs.copy();
        ones.fill(1);

        return ones.elementDiv(bettersProbs);
    }

    /*
     * Used to decide which method we should use to determine what is a good bet.
     */
    static class Profits {
        private double profitPerc = -1;
        private double profitReal = -1;
        private String output;

        public Profits(double profitPerc, double profitReal, String output) {
            this.profitPerc = profitPerc;
            this.profitReal = profitReal;
            this.output = output;
        }

        public double getProfitPerc() {
            return profitPerc;
        }

        public double getProfitReal() {
            return profitReal;
        }

        public String getOutput() {
            return output;
        }
    }
}
