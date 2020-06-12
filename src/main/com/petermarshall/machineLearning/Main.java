package com.petermarshall.machineLearning;

import java.io.IOException;

public class Main {
    public static void trainModels() {
        try {
            ModelTrain.trainNoLineups();
            ModelTrain.trainWithLineups();
            System.out.println("Now calculating performance with NO lineups...");
            ModelPerformance.performanceNoLineups();
            System.out.println("Now calculating performance WITH lineups...");
            ModelPerformance.performanceWithLineups();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        trainModels();
    }
}
