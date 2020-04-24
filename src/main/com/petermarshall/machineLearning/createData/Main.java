package com.petermarshall.machineLearning.createData;
import com.petermarshall.DateHelper;
import com.petermarshall.machineLearning.createData.classes.MatchToPredict;
import com.petermarshall.machineLearning.createData.classes.TrainingMatch;
import com.petermarshall.database.datasource.DataSource;

import java.util.ArrayList;
import java.util.Date;

//This file will be the main interface into the CreateData package.
//Things we want to do with MachineLearning package:
//1. Create files that we can train on in Octave and also Java.
//2. Add features to Matches to Predict.
public class Main {

    public static void createFilesToTrainAndTestOn(String octaveTrainingDataFileName, String octaveTestDataFileName, String javaTestDataFileName) {
        DataSource.openConnection();

        GetMatchesFromDb.loadInDataFromDb();
        ArrayList<TrainingMatch> trainingMatches = GetMatchesFromDb.getTrainingData();
        WriteTrainingData.writeDataOutToCsvFiles(trainingMatches, octaveTrainingDataFileName, octaveTestDataFileName, javaTestDataFileName);

        DataSource.closeConnection();
    }

    public static void createFileJustToTrainOn(String octaveTrainingDataFileName) {
        DataSource.openConnection();

        GetMatchesFromDb.loadInDataFromDb();
        ArrayList<TrainingMatch> trainingMatches = GetMatchesFromDb.getTrainingData();
        WriteTrainingData.writeAllDataOutToOneCsvFile(trainingMatches, octaveTrainingDataFileName);

        DataSource.closeConnection();
    }

    /*
     * Used to move data over to octave to see if our predict file in Java did the same things as the one in Octave.
     */
    public static void createFileOfMatchesFromCertainDate(String matchDataFileName, Date addMatchesAfter) {
        DataSource.openConnection();

        GetMatchesFromDb.loadInDataFromDb(addMatchesAfter);
        ArrayList<TrainingMatch> trainingMatches = GetMatchesFromDb.getTrainingData();
        WriteTrainingData.writeAllDataOutToOneCsvFile(trainingMatches, matchDataFileName);

        DataSource.closeConnection();
    }

    public static void addFeaturesToMatchesToPredict(ArrayList<MatchToPredict> matches) {
        GetMatchesFromDb.addFeaturesToMatchesToPredict(matches);
    }

    public static void main(String[] args) {
//        createFilesToTrainAndTestOn("octaveEvenMoreTraining.csv", "octaveEvenMoreTest.csv", "javaEvenMoreTest.csv");
        Date addOnlyAfter = DateHelper.createDateyyyyMMdd("2019","01", "05");
        createFileOfMatchesFromCertainDate("gamesAfterModelWasMade.csv", addOnlyAfter);
    }

}
