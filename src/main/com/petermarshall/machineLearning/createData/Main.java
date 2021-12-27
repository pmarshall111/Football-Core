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

    public static void createOneBigFileWithGameIdAtEndOfRow(Date removeBefore, Date removeAfter, String filename) {
        DS_Main.openProductionConnection();
        ArrayList<TrainingMatch> trainingMatches = CalcPastStats.getAllTrainingMatches();
        ArrayList<TrainingMatch> matchesSubset = removeTrainingMatches(removeBefore, removeAfter, trainingMatches);
        WriteTrainingData.writeAllDataOutToOneCsvFileWithGameIdAtEndOfRow(matchesSubset, filename);
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
//        Date removeBefore = null;
//        Date removeAfter = DateHelper.createDateyyyyMMdd("2021", "07", "20");
//        createOneBigFile(null, removeAfter, "allDataCarryOverLastSeason.csv");

//        Date removeBefore = DateHelper.createDateyyyyMMdd("2020", "08", "01");
//        Date removeAfter = null;
//        createOneBigFile(null, removeBefore, "toTrainOn_extended.csv");
//        createOneBigFile(removeBefore, null, "lastSeason_extended.csv");

//        //just getting the results for the 2019-20 season.
        Date removeBefore = DateHelper.createDateyyyyMMdd("2019", "08", "01");
        Date removeAfter = DateHelper.createDateyyyyMMdd("2021", "08", "01");
        createOneBigFile(null, removeBefore, "toTrainOn_extended.csv");
        createOneBigFile(removeBefore, removeAfter, "lastSeason_extended.csv");

        //just getting the results for the 2020-21 season.
//        Date removeBefore = DateHelper.createDateyyyyMMdd("2020", "08", "01");
//        Date removeAfter = DateHelper.createDateyyyyMMdd("2021", "08", "01");
//        createOneBigFile(null, removeBefore, "toTrainOn_extended.csv");
//        createOneBigFile(removeBefore, removeAfter, "lastSeason_extended.csv");

//        createOneBigFile(null, null, "allDataSPSS.csv");
    }

}
