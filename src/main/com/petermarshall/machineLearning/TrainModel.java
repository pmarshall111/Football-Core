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

//        for (int i = 0; i<nEpochs; i++) {
//            model.fit(allTrain);
//        }

        System.out.println("Evaluate model....");
        Evaluation eval = new Evaluation(numOutputs);
        while(testIter.hasNext()){
            DataSet t = testIter.next();
            INDArray features = t.getFeatures();
            INDArray lables = t.getLabels();
            INDArray predicted = model.output(features,false);

            eval.eval(lables, predicted);
        }

        //Print the evaluation statistics
        System.out.println(eval.stats());
    }
}
