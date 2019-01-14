package com.petermarshall.machineLearning.createData;
import com.petermarshall.machineLearning.createData.classes.MatchToPredict;
import com.petermarshall.machineLearning.createData.classes.TrainingMatch;
import com.petermarshall.database.DataSource;

import java.util.ArrayList;

//This file will be the main interface into the CreateData package.
//Things we want to do with MachineLearning package:
//1. Create files that we can train on in Octave and also Java.
//2. Add features to Matches to Predict.
public class Main {

    public static void createFilesToTrainAndTestOn(String octaveTrainingDataFileName, String octaveTestDataFileName, String javaTestDataFileName) {
        DataSource.openConnection();

        GetMatchesFromDb.getDataFromDb();
        ArrayList<TrainingMatch> trainingMatches = GetMatchesFromDb.getTrainingData();
        WriteTrainingData.writeDataOutToCsvFiles(trainingMatches, octaveTrainingDataFileName, octaveTestDataFileName, javaTestDataFileName);

        DataSource.closeConnection();
    }

    public static void createFileJustToTrainOn(String octaveTrainingDataFileName) {
        DataSource.openConnection();

        GetMatchesFromDb.getDataFromDb();
        ArrayList<TrainingMatch> trainingMatches = GetMatchesFromDb.getTrainingData();
        WriteTrainingData.writeAllDataOutToOneCsvFile(trainingMatches, octaveTrainingDataFileName);

        DataSource.closeConnection();
    }

    public static void addFeaturesToMatchesToPredict(ArrayList<MatchToPredict> matches) {
        GetMatchesFromDb.addFeaturesToMatchesToPredict(matches);
    }

    public static void main(String[] args) {
        createFilesToTrainAndTestOn("octaveEvenMoreTraining.csv", "octaveEvenMoreTest.csv", "javaEvenMoreTest.csv");
    }

}
