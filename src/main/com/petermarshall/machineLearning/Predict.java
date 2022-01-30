package com.petermarshall.machineLearning;

import com.petermarshall.CommandLineExecutor;
import com.petermarshall.CsvReader;
import com.petermarshall.machineLearning.createData.WriteTrainingData;
import com.petermarshall.machineLearning.createData.classes.MatchToPredict;

import java.util.ArrayList;

public class Predict {
    private Predict() {}

    public final static String OCTAVE_PREDICT_FILE = System.getenv("OCTAVE_PREDICT_FILE");
    public final static String OCTAVE_THETA_PATH = System.getenv("OCTAVE_THETA_PATH");

    public static void addPredictionsToGames(ArrayList<MatchToPredict> matches) {
        String featuresCsvPath = "/tmp/matches_to_predict.csv";
        String predictionsCsvPath = "/tmp/predictions.csv";
        WriteTrainingData.writeMatchesToPredictOutToCsvFile(matches, featuresCsvPath);
        // call the Octave function
        String command = "octave " + OCTAVE_PREDICT_FILE + " " + OCTAVE_THETA_PATH + " " + featuresCsvPath + " " + predictionsCsvPath;
        CommandLineExecutor.runCommand(command);
        // read the resulting file
        ArrayList<ArrayList<String>> predictions = CsvReader.readCsv(predictionsCsvPath);
        // match the file to the matches
        if (predictions != null) {
            OctavePredictionMapper predictionMapper = new OctavePredictionMapper();
            predictionMapper.addPredictionsToMatches(predictions, matches);
        }
    }
}
