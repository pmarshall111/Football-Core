package com.petermarshall.machineLearning;

import org.bytedeco.javacv.FrameFilter;
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

public class ModelTrain {
    private static final String trainCsv = "C:\\Users\\Peter\\Documents\\JavaProjects\\Football\\train.csv";
    private static final String evalCsv = "C:\\Users\\Peter\\Documents\\JavaProjects\\Football\\eval.csv";
    private static final String saveModelTo = "C:\\Users\\Peter\\Documents\\JavaProjects\\Football\\trained_model.zip";

    private static final String trainNoLineupsCsv = "C:\\Users\\Peter\\Documents\\JavaProjects\\Football\\noLineups_train.csv";
    private static final String evalNoLineupsCsv = "C:\\Users\\Peter\\Documents\\JavaProjects\\Football\\noLineups_eval.csv";
    private static final String saveNoLineupsModelTo = "C:\\Users\\Peter\\Documents\\JavaProjects\\Football\\trained_no_lineups_model.zip";

    private static final int seed = 123;
    private static final double learningRate = 0.01;
    private static final int batchSize = 50;
    private static final int nEpochs = 30;
    private static final int numInputsWithLineups = 69;
    private static final int numInputsBase = 63;
    private static final int numOutputs = 3;
    private static final int numHiddenNodes = 20;
    private static final int csvLinesToSkip = 0;

    public static void trainWithLineups() throws Exception {
        //Load the training data:
        RecordReader rr = new CSVRecordReader(csvLinesToSkip);
        rr.initialize(new FileSplit(new File(trainCsv)));
        DataSetIterator trainIter = new RecordReaderDataSetIterator(rr,batchSize,0,3);

//        //Load the test/evaluation data:
//        RecordReader rrTest = new CSVRecordReader(csvLinesToSkip);
//        rrTest.initialize(new FileSplit(new File(evalCsv)));
//        DataSetIterator testIter = new RecordReaderDataSetIterator(rrTest,batchSize,0,3);

        MultiLayerConfiguration conf = new NeuralNetConfiguration.Builder()
                .seed(seed)
                .weightInit(WeightInit.XAVIER)
                .updater(new Nesterovs(learningRate, 0.9))
                .list()
                .layer(new DenseLayer.Builder().nIn(numInputsWithLineups).nOut(numHiddenNodes)
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

//        System.out.println("Evaluate model....");
//        Evaluation eval = new Evaluation(numOutputs);
//        while(testIter.hasNext()){
//            DataSet t = testIter.next();
//            INDArray features = t.getFeatures();
//            INDArray labels = t.getLabels();
//            INDArray predicted = model.output(features,false);
//            eval.eval(labels, predicted);
//        }
//
//        //Print the evaluation statistics
//        System.out.println(eval.stats());
    }

    public static void trainNoLineups() throws Exception {
        //Load the training data:
        RecordReader rr = new CSVRecordReader(csvLinesToSkip);
        rr.initialize(new FileSplit(new File(trainNoLineupsCsv)));
        DataSetIterator trainIter = new RecordReaderDataSetIterator(rr,batchSize,0,3);

//        //Load the test/evaluation data:
//        RecordReader rrTest = new CSVRecordReader(csvLinesToSkip);
//        rrTest.initialize(new FileSplit(new File(evalNoLineupsCsv)));
//        DataSetIterator testIter = new RecordReaderDataSetIterator(rrTest,batchSize,0,3);

        MultiLayerConfiguration conf = new NeuralNetConfiguration.Builder()
                .seed(seed)
                .weightInit(WeightInit.XAVIER)
                .updater(new Nesterovs(learningRate, 0.9))
                .list()
                .layer(new DenseLayer.Builder().nIn(numInputsBase).nOut(numHiddenNodes)
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
        model.save(new File(saveNoLineupsModelTo));

//        System.out.println("Evaluate model....");
//        Evaluation eval = new Evaluation(numOutputs);
//        while(testIter.hasNext()){
//            DataSet t = testIter.next();
//            INDArray features = t.getFeatures();
//            INDArray labels = t.getLabels();
//            INDArray predicted = model.output(features,false);
//            eval.eval(labels, predicted);
//        }
//
//        //Print the evaluation statistics
//        System.out.println(eval.stats());
    }
}
