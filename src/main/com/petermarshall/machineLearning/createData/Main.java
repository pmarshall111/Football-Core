package com.petermarshall.machineLearning.createData;
import com.petermarshall.DateHelper;
import com.petermarshall.database.datasource.DS_Main;
import com.petermarshall.machineLearning.createData.classes.TrainingMatch;
import com.petermarshall.machineLearning.createData.refactor.PastStatsCalculator;

import java.util.ArrayList;
import java.util.Date;

//This file will be the main interface into the CreateData package.
//Things we want to do with MachineLearning package:
//1. Create files that we can train on in Octave and also Java.
//2. Add features to Matches to Predict.
public class Main {

    public static void createFilesToTrainAndTestOn(String octaveTrainingDataFileName, String octaveTestDataFileName, String javaTestDataFileName) {
        DS_Main.openProductionConnection();
        ArrayList<TrainingMatch> trainingMatches = PastStatsCalculator.getAllTrainingMatches();
//        WriteTrainingData.writeDataOutToCsvFiles(trainingMatches, octaveTrainingDataFileName, octaveTestDataFileName, javaTestDataFileName);
        DS_Main.closeConnection();
    }

    public static void createFileJustToTrainOn(String octaveTrainingDataFileName) {
        DS_Main.openProductionConnection();
        ArrayList<TrainingMatch> trainingMatches = PastStatsCalculator.getAllTrainingMatches();
//        WriteTrainingData.writeAllDataOutToOneCsvFile(trainingMatches, octaveTrainingDataFileName);
        DS_Main.closeConnection();
    }

    public static void createFilesForDl4j() {
        DS_Main.openProductionConnection();
        ArrayList<TrainingMatch> trainingMatches = PastStatsCalculator.getAllTrainingMatches();
        WriteTrainingData.writeDataOutToCsvFiles(trainingMatches);
        DS_Main.closeConnection();
    }

    /*
     * Used to move data over to octave to see if our predict file in Java did the same things as the one in Octave.
     */
//    public static void createFileOfMatchesFromCertainDate(String matchDataFileName, Date addMatchesAfter) {
//        DataSource.openConnection();
//
//        GetMatchesFromDb.loadInDataFromDb(addMatchesAfter);
//        ArrayList<TrainingMatch> trainingMatches = GetMatchesFromDb.getTrainingData();
//        WriteTrainingData.writeAllDataOutToOneCsvFile(trainingMatches, matchDataFileName);
//
//        DataSource.closeConnection();
//    }


    public static void main(String[] args) {
//        createFilesToTrainAndTestOn("octaveEvenMoreTraining.csv", "octaveEvenMoreTest.csv", "javaEvenMoreTest.csv");
//        Date addOnlyAfter = DateHelper.createDateyyyyMMdd("2019","01", "05");
//        createFileOfMatchesFromCertainDate("gamesAfterModelWasMade.csv", addOnlyAfter);
        createFilesForDl4j();
    }

}
