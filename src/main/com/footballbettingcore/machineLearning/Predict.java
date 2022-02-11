package com.footballbettingcore.machineLearning;

import com.footballbettingcore.utils.CommandLineExecutor;
import com.footballbettingcore.utils.CsvReader;
import com.footballbettingcore.machineLearning.createData.WriteTrainingData;
import com.footballbettingcore.machineLearning.createData.classes.MatchToPredict;

import java.util.ArrayList;

public class Predict {
    private Predict() {}

    public final static String OCTAVE_PREDICT_FILE = System.getenv("OCTAVE_PREDICT_FILE");
    public final static String OCTAVE_THETA_PATH = System.getenv("OCTAVE_THETA_PATH");

    public static void addPredictionsToGames(ArrayList<MatchToPredict> matches) {
        if (matches.size() > 0) {
            String featuresCsvPath = "/tmp/matches_to_predict.csv";
            String noLineupsFeaturesCsvPath = "/tmp/matches_to_predict_no_lineups.csv";
            String predictionsCsvPath = "/tmp/predictions.csv";
            WriteTrainingData.writeMatchesToPredictOutToCsvFile(matches, featuresCsvPath, noLineupsFeaturesCsvPath);
            // call the Octave function
            String command = "octave " + OCTAVE_PREDICT_FILE + " " + OCTAVE_THETA_PATH + " " + noLineupsFeaturesCsvPath + " " + predictionsCsvPath;
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
}
