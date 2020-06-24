package com.petermarshall.machineLearning.logisticRegression;

import org.ejml.data.DMatrixRMaj;
import org.ejml.ops.MatrixIO;
import org.ejml.simple.SimpleMatrix;

import java.io.IOException;

public class Main {
    public static final String THETAS_LINEUPS_PATH = "C:\\Users\\Peter\\Documents\\JavaProjects\\Football\\allThetas_spaceDelim.csv";
    public static final String THETAS_NO_LINEUPS_PATH = "C:\\Users\\Peter\\Documents\\JavaProjects\\Football\\allThetas_spaceDelim.csv";

    /*
     * Method loads in csv files for both the Test data and also the theta values for all 3 logistic regressions, trained outside of this app in octave.
     * Then calculates overall accuracy, which we can compare to what Octave gets with the same data to ensure all is working correctly.
     *
     * NOTE: values will be slightly different to those in Octave due to rounding differences. (Octave only takes 5dp.)
     */
    private static void predictMoneyFromOctaveThetasAndTestData() {
        try {
            DMatrixRMaj dMatrixData = MatrixIO.loadCSV("C:\\Users\\Peter\\Documents\\JavaProjects\\Football\\noLineups_eval_spaceDelim.csv", 1871, 80);
            SimpleMatrix testDataFull = SimpleMatrix.wrap(dMatrixData);
            DMatrixRMaj dMatrixThetas = MatrixIO.loadCSV(THETAS_LINEUPS_PATH, 3, 79);
            SimpleMatrix thetas = SimpleMatrix.wrap(dMatrixThetas);
            DMatrixRMaj dMatrixOdds = MatrixIO.loadCSV("C:\\Users\\Peter\\Documents\\JavaProjects\\Football\\oddsnoLineups_eval_spaceDelim.csv", 1871, 3);

            SimpleMatrix testDataOdds = SimpleMatrix.wrap(dMatrixOdds);
            SimpleMatrix Y = testDataFull.cols(0, 1);
            SimpleMatrix testData = testDataFull.cols(1,testDataFull.numCols()); //4 to include the 3 odds and also the result

            //need to add a bias feature to testData.
            SimpleMatrix ones = new SimpleMatrix(testData.numRows(), 1);
            ones.fill(1);
            testData = ones.combine(0,0, testData);


            Predict.calcPredictions(thetas, testData);
            SimpleMatrix ourProbabilities = Predict.getOurPredictions();
            double accuracyPercentage = Predict.getAccuracy(Y);
            System.out.println("Accuracy for these predictions were " + accuracyPercentage + "%.");
            calcProfits(testDataOdds, ourProbabilities, Y, 0.15, false);
            calcProfits(testDataOdds, ourProbabilities, Y, 0.15, true);

//            ArrayList<CalcPotentialMoneyMade.Profits> predictions = CalcPotentialMoneyMade.calcMoneyMade(bookieProbabilities, ourProbabilities, resultData, 0.15, 0.15, 4, 5);
//
//            for (CalcPotentialMoneyMade.Profits profits: predictions) {
//                System.out.println(profits.getOutput());
//            }


        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //Varies the stake such that games which our model predicts a result to be way more likely gets a higher stake, and those which are
    //closer to the odds get a lower stake.
    private static void calcProfits(SimpleMatrix bookieOdds, SimpleMatrix ourProbabilities, SimpleMatrix Y, double betterThanBookiesBy, boolean roundTo50p) {
        SimpleMatrix goodBets = new SimpleMatrix(ourProbabilities.numRows(), ourProbabilities.numCols());
        goodBets.fill(0);

        int initalStake = 5;
        SimpleMatrix bookieProbabilities = turnProbabilityToOdds(bookieOdds);

        int numBets = 0;
        int moneyOut = 0;
        double moneyIn = 0;

        for (int row = 0; row<ourProbabilities.numRows(); row++) {
            int bestCol = -1;
            double highestProb = -9999;
            //figuring out the highest and second highest probability of the row.
            for (int col = 0; col<ourProbabilities.numCols(); col++) {
                double ourProb = ourProbabilities.get(row, col);
                if (ourProb > highestProb) {
                    highestProb = ourProb;
                    bestCol = col;
                }
            }

            double betterBy = highestProb - bookieProbabilities.get(row,bestCol) - betterThanBookiesBy;
            if (bestCol != 1 && betterBy > 0) {
                double varStake = initalStake * (25*betterBy);
                if (roundTo50p) {
                    varStake = roundToNearest50p(varStake);
                }
                moneyOut+=varStake;
                if (Y.get(row, 0) == bestCol) {
                    moneyIn += varStake*bookieOdds.get(row, bestCol);
                }
            }
        }

        double profit = moneyIn - moneyOut;
        double percProfit = 100*profit/moneyOut;

        System.out.println("Total bets: " + numBets);
        System.out.println("Money spent: " + moneyOut);
        System.out.println("Money got back: " + moneyIn);
        System.out.println("Profit: " + profit);
        System.out.println("Percentage profit: " + percProfit);
    }

    private static double roundToNearest50p(double stake) {
        return (double)(Math.round(stake*2))/2;
    }

    private static SimpleMatrix turnProbabilityToOdds(SimpleMatrix bettersProbs) {
        SimpleMatrix ones = bettersProbs.copy();
        ones.fill(1);

        return ones.elementDiv(bettersProbs);
    }

    public static void main(String[] args) {
        predictMoneyFromOctaveThetasAndTestData();
    }
}
