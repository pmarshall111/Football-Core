package com.petermarshall.machineLearning;

import org.datavec.api.records.reader.RecordReader;
import org.datavec.api.records.reader.impl.csv.CSVRecordReader;
import org.datavec.api.split.FileSplit;
import org.deeplearning4j.datasets.datavec.RecordReaderDataSetIterator;
import org.deeplearning4j.nn.conf.MultiLayerConfiguration;
import org.deeplearning4j.nn.conf.NeuralNetConfiguration;
import org.deeplearning4j.nn.conf.layers.DenseLayer;
import org.deeplearning4j.nn.conf.layers.OutputLayer;
import org.deeplearning4j.nn.multilayer.MultiLayerNetwork;
import org.deeplearning4j.nn.weights.WeightInit;
import org.deeplearning4j.optimize.listeners.ScoreIterationListener;
import org.nd4j.evaluation.classification.Evaluation;
import org.nd4j.linalg.activations.Activation;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.dataset.DataSet;
import org.nd4j.linalg.dataset.api.iterator.DataSetIterator;
import org.nd4j.linalg.factory.Nd4j;
import org.nd4j.linalg.learning.config.Nesterovs;
import org.nd4j.linalg.lossfunctions.LossFunctions;
import org.nd4j.linalg.lossfunctions.LossFunctions.LossFunction;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;

public class TrainModel {
    //result will need to be at index 0
    private static String trainCsv = "C:\\Users\\Peter\\Documents\\JavaProjects\\Football\\train.csv";
    private static String evalCsv = "C:\\Users\\Peter\\Documents\\JavaProjects\\Football\\eval.csv";
    private static String oddsEvalCsv = "C:\\Users\\Peter\\Documents\\JavaProjects\\Football\\oddseval.csv";
    private static String saveModelTo = "C:\\Users\\Peter\\Documents\\JavaProjects\\Football\\trained_model.zip";

    public static void main(String[] args) throws Exception {
        int seed = 123;
        double learningRate = 0.01;
        int batchSize = 50;
        int nEpochs = 30;

        int numInputs = 69;
        int numOutputs = 3;
        int numHiddenNodes = 20;

        int csvLinesToSkip = 0;
        //Load the training data:
        RecordReader rr = new CSVRecordReader(csvLinesToSkip);
        rr.initialize(new FileSplit(new File(trainCsv)));
        DataSetIterator trainIter = new RecordReaderDataSetIterator(rr,batchSize,0,3);

        //Load the test/evaluation data:
        RecordReader rrTest = new CSVRecordReader(csvLinesToSkip);
        rrTest.initialize(new FileSplit(new File(evalCsv)));
        DataSetIterator testIter = new RecordReaderDataSetIterator(rrTest,batchSize,0,3);

        MultiLayerConfiguration conf = new NeuralNetConfiguration.Builder()
                .seed(seed)
                .weightInit(WeightInit.XAVIER)
                .updater(new Nesterovs(learningRate, 0.9))
                .list()
                .layer(new DenseLayer.Builder().nIn(numInputs).nOut(numHiddenNodes)
                        .activation(Activation.RELU)
                        .build())
                .layer(new OutputLayer.Builder(LossFunctions.LossFunction.NEGATIVELOGLIKELIHOOD)
                        .activation(Activation.SOFTMAX)
                        .nIn(numHiddenNodes).nOut(numOutputs).build())
                .build();

        MultiLayerNetwork model = new MultiLayerNetwork(conf);
        model.init();
        model.setListeners(new ScoreIterationListener(10));  //Print score every 10 parameter updates

        model.fit(trainIter, nEpochs);
        model.save(new File(saveModelTo));

//        for (int i = 0; i<nEpochs; i++) {
//            model.fit(allTrain);
//        }

        RecordReader rrTestOdds = new CSVRecordReader(csvLinesToSkip);
        rrTestOdds.initialize(new FileSplit(new File(oddsEvalCsv)));
        DataSetIterator testOddsIter = new RecordReaderDataSetIterator(rrTestOdds,batchSize);

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
            for (int i = 0; i<batchSize; i++) {
                INDArray oddsRow = odds.getRow(i);
                INDArray predictionRow = predicted.getRow(i);
                INDArray labelsRow = labels.getRow(i);
                decideToBet(oddsRow, predictionRow, labelsRow, mrHigherThanBookies, mrHigherThanSecondHighest);
            }

            eval.eval(labels, predicted);
        }

        //Print the evaluation statistics
        System.out.println(eval.stats());
        mrHigherThanBookies.printResults();
        mrHigherThanSecondHighest.printResults();
    }

    private static void decideToBet(INDArray oddsRow, INDArray predictionRow, INDArray labelsRow, MoneyResults higherThanBookies, MoneyResults higherThanSecondHighest) {
        double btb = 0.15; //better than bookies
        double htsh = 0.15; //higher than second highest
        double bookieHomePred = calcProbabilityFromOdds(oddsRow.getDouble(0));
        double bookieDrawPred = calcProbabilityFromOdds(oddsRow.getDouble(1));
        double bookieAwayPred = calcProbabilityFromOdds(oddsRow.getDouble(2));

        double homePrediction = predictionRow.getDouble(0);
        double drawPrediction = predictionRow.getDouble(1);
        double awayPrediction = predictionRow.getDouble(2);

        if (homePrediction > bookieHomePred-btb) {
            higherThanBookies.addBet(5, oddsRow.getDouble(0), labelsRow.getDouble(0) == 0);
            if (homePrediction > drawPrediction-htsh && homePrediction > awayPrediction-htsh) {
                higherThanSecondHighest.addBet(5, oddsRow.getDouble(0), labelsRow.getDouble(0) == 0);
            }
        }

        if (awayPrediction > bookieAwayPred-btb) {
            higherThanBookies.addBet(5, oddsRow.getDouble(2), labelsRow.getDouble(0) == 2);
            if (awayPrediction > drawPrediction-htsh && awayPrediction > homePrediction-htsh) {
                higherThanSecondHighest.addBet(5, oddsRow.getDouble(2), labelsRow.getDouble(0) == 2);
            }
        }
    }

    private static double calcProbabilityFromOdds(double odds) {
        return 1/odds;
    }
}
