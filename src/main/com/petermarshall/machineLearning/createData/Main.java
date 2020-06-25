package com.petermarshall.machineLearning.createData;
import com.petermarshall.DateHelper;
import com.petermarshall.database.datasource.DS_Main;
import com.petermarshall.machineLearning.createData.classes.TrainingMatch;

import java.util.ArrayList;
import java.util.Date;

public class Main {
    public static void createTrainEval(Date onlyBefore, Date onlyAfter) {
        DS_Main.openProductionConnection();
        ArrayList<TrainingMatch> trainingMatches = CalculatePastStats.getAllTrainingMatches();
        if (onlyBefore != null || onlyAfter != null) {
            trainingMatches.removeIf(tm -> {
                if (onlyBefore != null && onlyAfter != null) {
                    return tm.getKickoffTime().before(onlyAfter) || tm.getKickoffTime().after(onlyBefore);
                } else if (onlyBefore != null) {
                    return tm.getKickoffTime().after(onlyBefore);
                } else {
                    return tm.getKickoffTime().before(onlyAfter);
                }
            });
        }
        WriteTrainingData.writeDataOutToCsvFiles(trainingMatches, "train.csv", "eval.csv");
        DS_Main.closeConnection();
    }

    public static void createOneBigFile(Date onlyBefore, Date onlyAfter, String filename) {
        DS_Main.openProductionConnection();
        ArrayList<TrainingMatch> trainingMatches = CalculatePastStats.getAllTrainingMatches();
        if (onlyBefore != null || onlyAfter != null) {
            trainingMatches.removeIf(tm -> {
                if (onlyBefore != null && onlyAfter != null) {
                    return tm.getKickoffTime().before(onlyAfter) || tm.getKickoffTime().after(onlyBefore);
                } else if (onlyBefore != null) {
                    return tm.getKickoffTime().after(onlyBefore);
                } else {
                    return tm.getKickoffTime().before(onlyAfter);
                }
            });
        }
        WriteTrainingData.writeAllDataOutToOneCsvFile(trainingMatches, filename);
        DS_Main.closeConnection();
    }

    public static void main(String[] args) {
        Date onlyBefore = DateHelper.createDateyyyyMMdd("2019", "07", "05");
        Date onlyAfter = null;
//        createTrainEval(onlyBefore, null);
//        createOneBigFile(onlyBefore, null, "trainPre2019.csv");
//        createOneBigFile(null, onlyBefore, "eval2019.csv");
        createOneBigFile(null, null, "allData.csv");
    }

}
