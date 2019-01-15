package com.petermarshall.logging;

import com.petermarshall.machineLearning.createData.classes.MatchToPredict;
import com.petermarshall.machineLearning.logisticRegression.Predict;

import java.util.ArrayList;

public class GamesWithMissedPredictions {

    public static void compareAndLogPredictions(ArrayList<MatchToPredict> matches) {

        //TODO: add a method in DataSource to save our predictions to the database.


        //first need to loop through matches and see if it's worthy of betting on.
        //This will require refactoring Predict class's calcBetsForCurrentGamesAndAddToBuilder so we can just do a single call to check whether it's a good bet or not rather than having
        //to add a string to email body.
        Predict.predictAndLogMissedGames(matches);


        //while we're looping, we need to build up a string that we can use to get the Id's out of the database for each game. ALTERNATIVE... we add these in when we first
        //get our matchesToPredict from the database. This is probably better.

        //Then we do 1 big bulk update with all the match Id's and what our decision was. This should be a method in DataSource.

    }

}
