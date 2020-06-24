package com.petermarshall.machineLearning;

import com.petermarshall.machineLearning.createData.classes.MatchToPredict;
import org.deeplearning4j.nn.multilayer.MultiLayerNetwork;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.factory.Nd4j;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public class ModelPredict {
    private static String modelLocation = "C:\\Users\\Peter\\Documents\\JavaProjects\\Football\\trained_model.zip";

    public static void addLineupPredictions(ArrayList<MatchToPredict> mtps) {
//        try {
//            MultiLayerNetwork model = MultiLayerNetwork.load(new File(modelLocation), false);
//            model.getLabels();
//            for (MatchToPredict mtp: mtps) {
//                INDArray predicted = model.output(Nd4j.create(mtp.getFeaturesWithoutResult()), false);
//                double homePred = predicted.getRow(0).getDouble(0);
//                double drawPred = predicted.getRow(0).getDouble(1);
//                double awayPred = predicted.getRow(0).getDouble(2);
//                mtp.setOurPredictions(new double[]{homePred, drawPred, awayPred}, true);
//            }
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
    }

    public static void addBasePredictions(ArrayList<MatchToPredict> mtps) {
//        try {
//            MultiLayerNetwork model = MultiLayerNetwork.load(new File(modelLocation), false);
//            model.getLabels();
//            for (MatchToPredict mtp: mtps) {
//                INDArray predicted = model.output(Nd4j.create(mtp.getFeaturesNoLineupsWithoutResult()), false);
//                double homePred = predicted.getRow(0).getDouble(0);
//                double drawPred = predicted.getRow(0).getDouble(1);
//                double awayPred = predicted.getRow(0).getDouble(2);
//                mtp.setOurPredictions(new double[]{homePred, drawPred, awayPred}, false);
//            }
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
    }
}
