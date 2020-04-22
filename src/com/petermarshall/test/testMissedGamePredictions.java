package com.petermarshall.test;

import com.petermarshall.database.datasource.DataSource;
import com.petermarshall.logging.LastPredicted;
import com.petermarshall.machineLearning.createData.GetMatchesFromDb;
import com.petermarshall.machineLearning.createData.classes.MatchToPredict;
import com.petermarshall.machineLearning.logisticRegression.Predict;

import java.util.ArrayList;
import java.util.Date;

public class testMissedGamePredictions {

    private static final String trainedThetasPath = "C:\\Users\\Peter\\Documents\\JavaProjects\\Football\\testThetas.csv";

    public static void main(String[] args) {

        boolean needToPredictGamesWeMissed = LastPredicted.timeToPredictMissedGames(); //TODO: why is this coming back false???
        ArrayList<MatchToPredict> matchesWeDidntPredict = new ArrayList<>();

        DataSource.openConnection();

        Date checkGamesAfterDate = LastPredicted.getWhenMissedGamesWereLastPredicted();
        matchesWeDidntPredict = DataSource.getMatchesWithoutPredictions(checkGamesAfterDate); //TODO: why is this only giving us 10 records back. I THINK THE ANSWER TO BOTH OF THESE WAS THAT THE DATE GOT UPDATED WITHOUT PREDICTING THE GAMES.

        System.out.println("We're predicting " + matchesWeDidntPredict.size() + " missed games.");

        if (matchesWeDidntPredict.size() > 0) {

            GetMatchesFromDb.setGamesNeedPredictingAfterDate(checkGamesAfterDate);
            GetMatchesFromDb.setMissedGamesThatNeedPredicting(matchesWeDidntPredict);
            //NOTE: When we add features to the match to predict, we will also add features to these missed games we want to log through logic within GetMatchesFromDb.

        }

        DataSource.closeConnection();

        GetMatchesFromDb.loadInDataFromDb();

        Predict.addOurProbabilitiesToGames(matchesWeDidntPredict, trainedThetasPath);
        Predict.missedGamesBetDecisionAndLog(matchesWeDidntPredict);

//        LastPredicted.setAllMissedGamesPredictedUpTo2DaysAgo();

    }

}
