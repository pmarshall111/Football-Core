package com.petermarshall.taskScheduling;

import static com.petermarshall.machineLearning.createData.Main.createFileJustToTrainOn;


public class RetrainPredictor {


    public static void retrainPredictor() {
        //need to make sure we first have all played games in database
        //then we createData and send it over to Octave to calc new parameters.
        //then bring it back over and update the thetas file in this project.

        UpdatePlayedGames.main(new String[]{}); //placeholder argument to allow main method to be called.

        //First method is to train and test the model. Second is to train the model with all data once we've decided on a suitable model.
//        createFilesToTrainAndTestOn("octaveWeighted.csv", "octaveWeightedTest.csv", "javaWeightedTest.csv");
        createFileJustToTrainOn("allDataTraining.csv");

        //NOW WILL NEED TO MANUALLY GO OVER TO OCTAVE AND RETRAIN, SAVE TRAINED THETA VALUES (Using writeThetasToJavaCompatible)AND PASTE BACK OVER TO JAVA TO TEST AND PUT INTO ACTION.
    }

    public static void main(String[] args) {
        retrainPredictor();
    }

}
