package com.petermarshall.machineLearning.logisticRegression;

import com.petermarshall.machineLearning.createData.classes.MatchToPredict;
import org.ejml.data.DMatrixRMaj;
import org.ejml.equation.Equation;
import org.ejml.ops.MatrixIO;
import org.ejml.simple.SimpleMatrix;

import java.io.IOException;
import java.util.ArrayList;

import static com.petermarshall.machineLearning.logisticRegression.Main.THETAS_LINEUPS_PATH;
import static com.petermarshall.machineLearning.logisticRegression.Main.THETAS_NO_LINEUPS_PATH;

public class Predict {
    public static final int DAYS_IN_FUTURE_TO_PREDICT = 8;
    private static SimpleMatrix logitPredictions = null;
    private static SimpleMatrix resultPredictions = null;

    /*
     * Creates 2 SimpleMatrix's as class fields. One with the logit predictions of the tests (which will be X*allTheta), and the other just takes the
     * column with the highest value and takes that to be our main prediction for the result of the game.
     */
    public static void calcPredictions(SimpleMatrix allTheta, SimpleMatrix X) {
        int m = X.numRows();
        int numLabels = allTheta.numRows();

        logitPredictions = X.mult(allTheta.transpose());
        resultPredictions = new SimpleMatrix(m, 1);

        for (int row = 0; row<m; row++) {
            double currMax = -99999999;
            int maxCol = -1;
            for (int col = 0; col<numLabels; col++) {
                if (logitPredictions.get(row,col) > currMax) {
                    currMax = logitPredictions.get(row, col);
                    maxCol = col;
                }
            }
            resultPredictions.set(row, 0, maxCol+1); //+1 here as we have a win as 1, draw as 2 and loss as 3.
        }
    }


    /*
     * Looks at the resultPredictions (which just takes the column with the highest value) and compares to the actual result.
     * Returns accuracy as a percentage.
     *
     * Result predictions and y are the same size with just 1 column. Both contain just the result number of the match (1 = win, 2 = draw, 3 = loss)
     */
    public static double getAccuracy(SimpleMatrix y) {
        if (resultPredictions == null) throw new RuntimeException("Cannot calculate the accuracy when we haven't got any predictions yet. Call calcPredictions() first.");

        double numbCorrect = 0;
        for (int row = 0; row<y.numRows(); row++) {
            if (resultPredictions.get(row,0) == y.get(row,0)+1) {
                numbCorrect++;
            }
        }
        return numbCorrect * 100 /y.numRows();
    }

    /*
     * Changes our logistic regression values so that the probabilities all add up to 1.
     *
     * TODO: check that our equation is correct. The formula to calc the sums in octave is sum(m,2).
     */
    static SimpleMatrix convertLogitsToProbability(SimpleMatrix A) {
        SimpleMatrix exp = A.elementExp();
        SimpleMatrix matrix = exp.elementDiv(exp.plus(1));

        //getting the sum for each row. Equations allow you to write Octave/MATLAB code in Java.
        Equation eq = new Equation();
        eq.alias(matrix, "m");
        eq.process("sums = sum(m, 0)");
        SimpleMatrix sums = eq.lookupSimple("sums");

        //making a 3 column matrix so we can elementDivide.
        SimpleMatrix threeSums = sums.combine(0,1,sums).combine(0,2,sums);
        return matrix.elementDiv(threeSums);
    }

    /*
     * For this function to work, we need a list of all the features for each match so that we can multiply them out with our thetas to get predictions.
     * Method will fail when changing number of model featuers, as we've hardcoded in the number of rows and columns in our thetas.
     *
     * Bias parameter is already added to match features so no need to do that here.
     */
    public static void addOurProbabilitiesToGames(ArrayList<MatchToPredict> matches) {
        try {
            ArrayList<MatchToPredict> matchesWithLineups = new ArrayList<>();
            ArrayList<MatchToPredict> matchesNoLineups = new ArrayList<>();
            for (MatchToPredict mtp: matches) {
                if (mtp.hasPredictionsWithLineups()) {
                    matchesWithLineups.add(mtp);
                } else {
                    matchesNoLineups.add(mtp);
                }
            }

            if (matchesWithLineups.size() > 0) {
                DMatrixRMaj dMatrixWithLineups = MatrixIO.loadCSV(THETAS_LINEUPS_PATH, 3, 53);
                SimpleMatrix thetasWithLineups = SimpleMatrix.wrap(dMatrixWithLineups);
                addOurProbabilitiesToGames(matchesWithLineups, thetasWithLineups, true);
            }
            if (matchesNoLineups.size() > 0) {
                DMatrixRMaj dMatrixNoLineups = MatrixIO.loadCSV(THETAS_NO_LINEUPS_PATH, 3, 49);
                SimpleMatrix thetasNoLineups = SimpleMatrix.wrap(dMatrixNoLineups);
                addOurProbabilitiesToGames(matchesNoLineups, thetasNoLineups, false);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void addOurProbabilitiesToGames(ArrayList<MatchToPredict> matches, SimpleMatrix thetas, boolean withLineups) {
        for (MatchToPredict match: matches) {
            double[] features = match.getFeaturesWithoutResult(withLineups);
            SimpleMatrix matchFeatures = new SimpleMatrix(new double[][]{features});
            double[] ourPredictions = predictForMatch(thetas, matchFeatures); //should be length 3 with percentage for win/draw/loss
            if (ourPredictions.length != 3) {
                throw new RuntimeException("Not all predictions have been added to our double array");
            }
            else {
                match.setOurPredictions(ourPredictions, withLineups);
            }
        }
    }

    /*
     * Calculates logit probabilities and then regualrises them so that the total probability that comes out is 1.
     */
    public static double[] predictForMatch (SimpleMatrix thetas, SimpleMatrix matchFeatures) {
//        System.out.println("thetas size: " + thetas.numRows() + " x " + thetas.numCols());
//        System.out.println("features size: " + matchFeatures.numRows() + " x " + matchFeatures.numCols());

        SimpleMatrix logits = matchFeatures.mult(thetas.transpose());
        SimpleMatrix results = convertLogitsToProbability(logits);

        return new double[]{results.get(0,0), results.get(0,1), results.get(0,2)};
    }

    public static SimpleMatrix getOurPredictions() {
        if (logitPredictions == null) throw new RuntimeException("We haven't predicted yet. Call calcPredictions first.");
        return convertLogitsToProbability(logitPredictions);
    }

}