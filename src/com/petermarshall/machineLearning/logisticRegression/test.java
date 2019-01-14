package com.petermarshall.machineLearning.logisticRegression;

import org.ejml.data.DMatrixRMaj;
import org.ejml.ops.MatrixIO;
import org.ejml.simple.SimpleMatrix;
import weka.classifiers.Classifier;
import weka.classifiers.Evaluation;
import weka.classifiers.meta.MultiClassClassifier;
import weka.classifiers.trees.RandomTree;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.SerializationHelper;
import weka.core.converters.ArffLoader;
import weka.core.converters.CSVLoader;
import weka.core.converters.ConverterUtils.DataSource;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;

public class test {

    /**
     * This method is to load the data set.
     * @param fileName
     * @return
     * @throws IOException
     */


    //Right now is hardcoded to get all data at once.
    public static Instances getDataSet(String fileName) throws Exception {
        /**
         * we can set the file i.e., loader.setFile("finename") to load the data
         */
//        int classIdx = 1;
//        /** the arffloader to load the arff file */
//        ArffLoader loader = new ArffLoader();
//        //loader.setFile(new File(fileName));
//
//        File file = new File(fileName);
//        /** load the traing data */
//        loader.setSource(file);
//        /**
//         * we can also set the file like loader3.setFile(new
//         * File("test-confused.arff"));
//         */
//        Instances dataSet = loader.getDataSet();
//        /** set the index based on the data given in the arff files */
//        dataSet.setClassIndex(classIdx);
//        return dataSet;
            DataSource source = new DataSource(fileName);
            Instances data = source.getDataSet();

            if (data.classIndex() == -1)
                data.setClassIndex(data.numAttributes() - 1);

            return data;
    }

    /**
     * This method is used to process the input and return the statistics.
     *
     * @throws Exception
     */

    /*
     * We predict on our testing set using our testing data and trained weka database, and then compare our predictions to theirs via the CSV file
     */
    public static double process(double higherThan, double betterThanBetters) throws Exception {

        Instances testingDataSet = getDataSet("C:\\Users\\Peter\\Documents\\JavaProjects\\Football\\wekaTest.arff");
        DMatrixRMaj dMatrix = MatrixIO.loadCSV("C:\\Users\\Peter\\Documents\\JavaProjects\\Football\\wekaTest.csv", 1565, 4);
        SimpleMatrix simpleMatrix = SimpleMatrix.wrap(dMatrix);

        MultiClassClassifier multiClassClassifier = (MultiClassClassifier) SerializationHelper.read(new FileInputStream("mCM.database"));

        Iterator iterator = testingDataSet.iterator();

        ArrayList<Double> amountsHigherThanBookie = new ArrayList<>();
        double moneyOut = 0;
        double moneyIn = 0;

        double ratioMoneyOut = 0;
        double ratioMoneyIn = 0;

        int csvRow = 0;
        while (iterator.hasNext()) {

            Instance instance = (Instance) iterator.next();

            double[] possibilities = multiClassClassifier.distributionForInstance(instance);

            int highestIndex = -1;
            double highestVal = -1;
            int secondHighestIndex = -1;
            double secondHighestVal = -1;


            for (int i = 0; i<possibilities.length; i++) {
                if (possibilities[i] > highestVal) {
                    secondHighestIndex = highestIndex;
                    secondHighestVal = highestVal;
                    highestIndex = i;
                    highestVal = possibilities[i];
                } else if (possibilities[i] > secondHighestVal) {
                    secondHighestIndex = i;
                    secondHighestVal = possibilities[i];
                }
            }

            if (highestVal - secondHighestVal > higherThan) {
                double bettersVal = simpleMatrix.get(csvRow, highestIndex);

                if (highestVal - bettersVal > betterThanBetters) {
                    moneyOut += 5;
                    int result = (int) simpleMatrix.get(csvRow, 3);
                    if (result == highestIndex) moneyIn += 5/bettersVal;
                }
            }
            
            double winRatio = possibilities[0]/(possibilities[0] + possibilities[2]);
            double lossRatio = possibilities[2]/(possibilities[0] + possibilities[2]);

            double bettersWinRatio = simpleMatrix.get(csvRow, 0)/(simpleMatrix.get(csvRow, 0) + simpleMatrix.get(csvRow, 2));
            double bettersLossRatio = simpleMatrix.get(csvRow, 2)/(simpleMatrix.get(csvRow, 0) + simpleMatrix.get(csvRow, 2));

            double bettersWinOdds = 1/simpleMatrix.get(csvRow, 0);
            double bettersLoseOdds = 1/simpleMatrix.get(csvRow, 2);

            if (winRatio - bettersWinRatio > betterThanBetters) {
                amountsHigherThanBookie.add(winRatio - bettersWinRatio);

                double amountBetter = winRatio - bettersWinRatio;
                double extraBets = amountBetter*15/0.74; //adds up to an extra 15 pounds to the bet if our odds are way better than bookies.
                ratioMoneyOut += 5;//20- extraBets;
                int result = (int) simpleMatrix.get(csvRow, 3);
                if (result == 1) ratioMoneyIn += (5)*bettersWinOdds;
            }
            if (lossRatio - bettersLossRatio > betterThanBetters) {
                amountsHigherThanBookie.add(lossRatio - bettersLossRatio);

                double amountBetter = lossRatio - bettersLossRatio;
                double extraBets = amountBetter*15/0.74; //adds up to an extra 15 pounds to the bet if our odds are way better than bookies.
                ratioMoneyOut += 5;//20- extraBets;
                int result = (int) simpleMatrix.get(csvRow, 3);
                if (result == 3) ratioMoneyIn += (5)*bettersLoseOdds;
            }

            csvRow++;
        }

//        Collections.sort(amountsHigherThanBookie);
//        System.out.println("min: " + amountsHigherThanBookie.get(0) + ", max: " + amountsHigherThanBookie.get(amountsHigherThanBookie.size()-1));

        System.out.println("money out: " + ratioMoneyOut + " money in: " + ratioMoneyIn + " profit: " + (ratioMoneyIn - ratioMoneyOut) + " percentage: " + ((ratioMoneyIn - ratioMoneyOut)*100/ratioMoneyOut) + "%");
        return ratioMoneyIn-ratioMoneyOut;

    }

    /*
     * Method loads in csv files for both the Test data and also the theta values for each logistic regression, trained outside of this app in octave.
     * Then calculates overall accuracy, which we can compare to what Octave gets with the same data to ensure all is working correctly.
     *
     * NOTE: values will be slightly different to those in Octave due to rounding differences. (Octave only takes 5dp.)
     */
//    public static double octaveProcess () {
//
//        try {
//            DMatrixRMaj dMatrix = MatrixIO.loadCSV("C:\\Users\\Peter\\Documents\\JavaProjects\\Football\\octaveJavaPoweredTest.csv", 1565, 2348);
//            SimpleMatrix testDataFull = SimpleMatrix.wrap(dMatrix);
//
//            DMatrixRMaj dMatrixRMaj = MatrixIO.loadCSV("C:\\Users\\Peter\\Documents\\JavaProjects\\Football\\src\\testThetas.csv", 3, 2345);
//            SimpleMatrix thetas = SimpleMatrix.wrap(dMatrixRMaj);
//
//
//            SimpleMatrix testData = testDataFull.cols(0,testDataFull.numCols()-4); //4 to include the 3 odds and also the result
//            SimpleMatrix probsData = testDataFull.cols(testDataFull.numCols()-4, testDataFull.numCols()-1);
//            SimpleMatrix resultData = testDataFull.cols(testDataFull.numCols()-1, testDataFull.numCols());
//            //need to add a bias feature to testData.
//            SimpleMatrix ones = new SimpleMatrix(testData.numRows(), 1);
//            ones.fill(1);
//            testData = ones.combine(0,1, testData);
//
//
//            Predict.calcPredictions(thetas, testData);
//            double accuracyPercentage = Predict.getAccuracy(resultData);
//            System.out.println("Accuracy for these predictions were " + accuracyPercentage + "%.");
//
//            ArrayList<Predict.Profits> predictions = Predict.calcMoneyMade(probsData,resultData, 0.15, 0.15, 4, 5);
//
//            for (Predict.Profits profits: predictions) {
//                System.out.println(profits.getOutput());
//            }
//
//
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//
//        return -1d;
//    }

    public static void main(String[] args) {
//        octaveProcess();

        try {
//            int highestHigherThan = -1;
//            int highestBetterThanBetters = -1;
//            double highestProfit = -1000;
//
//            for (int i = 0; i<=0.1; i+= 0.05) {
//                for (int y = 0; y<=0.1; y+=0.05) {
//                    double profit = process(i,y);
//
//                    if (profit>highestProfit) {
//                        highestHigherThan = i;
//                        highestBetterThanBetters = y;
//                        highestProfit = profit;
//                    }
//                }
//            }

//            System.out.println(highestProfit + " made on " + highestHigherThan + ", " + highestBetterThanBetters);

//            System.out.println(process(0,0));
//            process(0.05,0);
//            process(0.05,0.05);
//            process(0.05,0.1);
//            process(0.1,0);
//            process(0.1,0.05);
//            process(0.1,0.1);
//            process(0.1,0.15);
//            process(0.15,0.2);
//            process(0.15,0.25);
//            process(0.15,0.3);

            SimpleMatrix logits = new SimpleMatrix(new double[][]{
                    {-0.32},
                    {-0.78},
                    {-0.98}
            });

            SimpleMatrix results = Predict.convertLogitsToProbability(logits);
            results.print();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
