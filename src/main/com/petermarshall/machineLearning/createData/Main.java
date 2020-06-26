package com.petermarshall.machineLearning.createData;
import com.petermarshall.DateHelper;
import com.petermarshall.database.datasource.DS_Main;
import com.petermarshall.machineLearning.createData.classes.TrainingMatch;

import java.util.ArrayList;
import java.util.Date;

public class Main {
    public static void createTrainEval(Date removeBefore, Date removeAfter) {
        DS_Main.openProductionConnection();
        ArrayList<TrainingMatch> trainingMatches = CalcPastStats.getAllTrainingMatches();
        ArrayList<TrainingMatch> matchesSubset = removeTrainingMatches(removeBefore, removeAfter, trainingMatches);
        WriteTrainingData.writeDataOutToCsvFiles(matchesSubset, "train.csv", "eval.csv");
        DS_Main.closeConnection();
    }

    public static void createOneBigFile(Date removeBefore, Date removeAfter, String filename) {
        DS_Main.openProductionConnection();
        ArrayList<TrainingMatch> trainingMatches = CalcPastStats.getAllTrainingMatches();
        ArrayList<TrainingMatch> matchesSubset = removeTrainingMatches(removeBefore, removeAfter, trainingMatches);
        WriteTrainingData.writeAllDataOutToOneCsvFile(matchesSubset, filename);
        DS_Main.closeConnection();
    }
    
    public static ArrayList<TrainingMatch> removeTrainingMatches(Date removeBefore, Date removeAfter, ArrayList<TrainingMatch> origList) {
        ArrayList<TrainingMatch> matchesSubset = new ArrayList<>();
        for (TrainingMatch match: origList) {
            Date kickOff = match.getKickoffTime();
            if ((removeBefore == null || kickOff.after(removeBefore) || kickOff.equals(removeBefore)) &&
                    (removeAfter == null || kickOff.before(removeAfter) || kickOff.equals(removeAfter))) {
                matchesSubset.add(match);
            }
        }
        return matchesSubset;
    }

    public static void main(String[] args) {
        Date removeBefore = null;
        Date removeAfter = DateHelper.createDateyyyyMMdd("2019", "07", "05");
        createOneBigFile(null, null, "allData.csv");
    }

}
