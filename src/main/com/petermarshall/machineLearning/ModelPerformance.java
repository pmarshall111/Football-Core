package com.petermarshall.machineLearning;

import org.datavec.api.records.reader.RecordReader;
import org.datavec.api.records.reader.impl.csv.CSVRecordReader;
import org.datavec.api.split.FileSplit;
import org.deeplearning4j.datasets.datavec.RecordReaderDataSetIterator;
import org.deeplearning4j.nn.multilayer.MultiLayerNetwork;
import org.nd4j.evaluation.classification.Evaluation;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.dataset.DataSet;
import org.nd4j.linalg.dataset.api.iterator.DataSetIterator;

import java.io.File;

import static com.petermarshall.machineLearning.DecideBet.addDecisionModelPerf;

public class ModelPerformance {
    private static final String modelLocation = "C:\\Users\\Peter\\Documents\\JavaProjects\\Football\\trained_model.zip";
    private static final String evalCsv = "C:\\Users\\Peter\\Documents\\JavaProjects\\Football\\eval.csv";
    private static final String oddsEvalCsv = "C:\\Users\\Peter\\Documents\\JavaProjects\\Football\\oddseval.csv";
    private static final String noLineupsModelLocation = "C:\\Users\\Peter\\Documents\\JavaProjects\\Football\\trained_no_lineups_model.zip";
    private static final String evalNoLineupsCsv = "C:\\Users\\Peter\\Documents\\JavaProjects\\Football\\noLineups_eval.csv";
    private static final String oddsEvalNoLineupsCsv = "C:\\Users\\Peter\\Documents\\JavaProjects\\Football\\oddsNoLineups_eval.csv";

    private static final int batchSize = 50;
    private static final int numOutputs = 3;
    private static final int csvLinesToSkip = 0;

    public static void main(String[] args) throws Exception {
//        performanceWithLineups();
        performanceNoLineups();
    }

    public static void performanceWithLineups() throws Exception {
        RecordReader rrTest = new CSVRecordReader(csvLinesToSkip);
        rrTest.initialize(new FileSplit(new File(evalCsv)));
        DataSetIterator testIter = new RecordReaderDataSetIterator(rrTest,batchSize,0,3);

        RecordReader rrTestOdds = new CSVRecordReader(csvLinesToSkip);
        rrTestOdds.initialize(new FileSplit(new File(oddsEvalCsv)));
        DataSetIterator testOddsIter = new RecordReaderDataSetIterator(rrTestOdds,batchSize);

        MultiLayerNetwork model = MultiLayerNetwork.load(new File(modelLocation), false);
        model.getLabels();

        System.out.println("Evaluate model....");
        MoneyResults mrHigherThanBookies = new MoneyResults();
        MoneyResults mrHigherThanSecondHighest = new MoneyResults();
        Evaluation eval = new Evaluation(numOutputs);
        while(testIter.hasNext()){
            DataSet t = testIter.next();
            INDArray features = t.getFeatures();
            INDArray labels = t.getLabels();
            INDArray predicted = model.output(features,false);

            INDArray odds = testOddsIter.next().getFeatures();
            try {
                for (int i = 0; i < batchSize; i++) {
                    INDArray oddsRow = odds.getRow(i);
                    INDArray predictionRow = predicted.getRow(i);
                    INDArray labelsRow = labels.getRow(i);
                    addDecisionModelPerf(oddsRow, predictionRow, labelsRow, mrHigherThanBookies);
                }
            } catch (Exception e) {
                System.out.println("reached end of test items");
            }
            eval.eval(labels, predicted);
        }

        //Print the evaluation statistics
        System.out.println(eval.stats());
        mrHigherThanBookies.printResults();
//        mrHigherThanSecondHighest.printResults();
    }

    public static void performanceNoLineups() throws Exception {
        RecordReader rrTest = new CSVRecordReader(csvLinesToSkip);
        rrTest.initialize(new FileSplit(new File(evalNoLineupsCsv)));
        DataSetIterator testIter = new RecordReaderDataSetIterator(rrTest,batchSize,0,3);

        RecordReader rrTestOdds = new CSVRecordReader(csvLinesToSkip);
        rrTestOdds.initialize(new FileSplit(new File(oddsEvalNoLineupsCsv)));
        DataSetIterator testOddsIter = new RecordReaderDataSetIterator(rrTestOdds,batchSize);

        MultiLayerNetwork model = MultiLayerNetwork.load(new File(noLineupsModelLocation), false);
        model.getLabels();

        System.out.println("Evaluate model....");
        MoneyResults mrHigherThanBookies = new MoneyResults();
        MoneyResults mrHigherThanSecondHighest = new MoneyResults();
        Evaluation eval = new Evaluation(numOutputs);
        while(testIter.hasNext()){
            DataSet t = testIter.next();
            INDArray features = t.getFeatures();
            INDArray labels = t.getLabels();
            INDArray predicted = model.output(features,false);

            INDArray odds = testOddsIter.next().getFeatures();
            try {
                for (int i = 0; i < batchSize; i++) {
                    INDArray oddsRow = odds.getRow(i);
                    INDArray predictionRow = predicted.getRow(i);
                    INDArray labelsRow = labels.getRow(i);
                    addDecisionModelPerf(oddsRow, predictionRow, labelsRow, mrHigherThanBookies);
                }
            } catch (Exception e) {
                System.out.println("reached end of test items");
            }
            eval.eval(labels, predicted);
        }

        //Print the evaluation statistics
        System.out.println(eval.stats());
        mrHigherThanBookies.printResults();
//        mrHigherThanSecondHighest.printResults();
    }

}