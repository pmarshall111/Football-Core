package com.petermarshall.taskScheduling;

import com.petermarshall.database.datasource.DS_Main;
import com.petermarshall.machineLearning.ModelPerformance;
import com.petermarshall.machineLearning.ModelTrain;
import com.petermarshall.machineLearning.createData.CalculatePastStats;
import com.petermarshall.machineLearning.createData.WriteTrainingData;
import com.petermarshall.machineLearning.createData.classes.TrainingMatch;

import java.util.ArrayList;

public class RetrainModels {
    public static void trainModels() {
        try {
            System.out.println("Getting matches from db and writing to files\n\n");
            DS_Main.openProductionConnection();
            ArrayList<TrainingMatch> trainingMatches = CalculatePastStats.getAllTrainingMatches();
            WriteTrainingData.writeDataOutToCsvFiles(trainingMatches, "train.csv", "eval.csv");
            DS_Main.closeConnection();

            System.out.println("Training models...\nNo Lineups first\n\n");
            ModelTrain.trainNoLineups();
            System.out.println("\n\nTraining With lineups...\n\n");
            ModelTrain.trainWithLineups();
            System.out.println("\n\nCalculating performance with NO lineups...\n\n");
            ModelPerformance.performanceNoLineups();
            System.out.println("\n\nCalculating performance WITH lineups...\n\n");
            ModelPerformance.performanceWithLineups();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        trainModels();
    }
}
