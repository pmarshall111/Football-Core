package com.petermarshall.machineLearning.createData;
import com.petermarshall.database.datasource.DS_Main;
import com.petermarshall.machineLearning.createData.classes.TrainingMatch;

import java.util.ArrayList;
import java.util.Date;

public class Main {
    public static void createFilesForDl4j() {
        //last written out to train.csv with data up to end of 2018-19 - 11/06/20
        DS_Main.openProductionConnection();
        ArrayList<TrainingMatch> trainingMatches = CalculatePastStats.getAllTrainingMatches();
        WriteTrainingData.writeDataOutToCsvFiles(trainingMatches, "train.csv", "eval.csv");
        DS_Main.closeConnection();
    }

    public static void createFileOfMatchesFromCertainDate(Date addMatchesAfter) {
        DS_Main.openProductionConnection();
        ArrayList<TrainingMatch> trainingMatches = CalculatePastStats.getAllTrainingMatches();
        trainingMatches.removeIf(tm -> tm.getKickoffTime().before(addMatchesAfter));
        WriteTrainingData.writeAllDataOutToOneCsvFile(trainingMatches, "");
        DS_Main.closeConnection();
    }


    public static void main(String[] args) {
//        Date addOnlyAfter = DateHelper.createDateyyyyMMdd("2019","01", "05");
//        createFileOfMatchesFromCertainDate("gamesAfterModelWasMade.csv", addOnlyAfter);
        createFilesForDl4j();
    }

}
