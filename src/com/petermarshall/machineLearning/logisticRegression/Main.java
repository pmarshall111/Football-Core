package com.petermarshall.machineLearning.logisticRegression;

import com.petermarshall.machineLearning.logisticRegression.*;

import org.ejml.data.DMatrixRMaj;
import org.ejml.ops.MatrixIO;
import org.ejml.simple.SimpleMatrix;

import java.io.IOException;
import java.util.ArrayList;


//will only worry about calculating money made in this file and we can let the prediction for unseen games deal directly with Predict file.
public class Main {

    /*
     * Method loads in csv files for both the Test data and also the theta values for all 3 logistic regressions, trained outside of this app in octave.
     * Then calculates overall accuracy, which we can compare to what Octave gets with the same data to ensure all is working correctly.
     *
     * NOTE: values will be slightly different to those in Octave due to rounding differences. (Octave only takes 5dp.)
     */
    private static void predictMoneyFromOctaveThetasAndTestData() {
        try {
            DMatrixRMaj dMatrix = MatrixIO.loadCSV("C:\\Users\\Peter\\Documents\\JavaProjects\\Football\\javaWeightedTest.csv", 1546, 88);
            SimpleMatrix testDataFull = SimpleMatrix.wrap(dMatrix);

            DMatrixRMaj dMatrixRMaj = MatrixIO.loadCSV("C:\\Users\\Peter\\Documents\\JavaProjects\\Football\\testThetas.csv", 3, 85);
            SimpleMatrix thetas = SimpleMatrix.wrap(dMatrixRMaj);


            SimpleMatrix testData = testDataFull.cols(0,testDataFull.numCols()-4); //4 to include the 3 odds and also the result
            SimpleMatrix bookieProbabilities = testDataFull.cols(testDataFull.numCols()-4, testDataFull.numCols()-1);
            SimpleMatrix resultData = testDataFull.cols(testDataFull.numCols()-1, testDataFull.numCols());
            //need to add a bias feature to testData.
            SimpleMatrix ones = new SimpleMatrix(testData.numRows(), 1);
            ones.fill(1);
            testData = ones.combine(0,1, testData);


            Predict.calcPredictions(thetas, testData);
            SimpleMatrix ourProbabilities = Predict.getOurPredictions();
            double accuracyPercentage = Predict.getAccuracy(resultData);
            System.out.println("Accuracy for these predictions were " + accuracyPercentage + "%.");

            ArrayList<CalcPotentialMoneyMade.Profits> predictions = CalcPotentialMoneyMade.calcMoneyMade(bookieProbabilities, ourProbabilities, resultData, 0.15, 0.15, 4, 5);

            for (CalcPotentialMoneyMade.Profits profits: predictions) {
                System.out.println(profits.getOutput());
            }


        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public static void main(String[] args) {
        predictMoneyFromOctaveThetasAndTestData();
    }

}
