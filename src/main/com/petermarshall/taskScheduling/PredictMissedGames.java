package com.petermarshall.taskScheduling;

import com.petermarshall.DateHelper;
import com.petermarshall.database.datasource.DataSource;
import com.petermarshall.logging.LastPredicted;
//import com.petermarshall.machineLearning.createData.GetMatchesFromDb;
import com.petermarshall.machineLearning.createData.classes.MatchToPredict;
import com.petermarshall.machineLearning.logisticRegression.Predict;

import java.util.ArrayList;
import java.util.Date;

public class PredictMissedGames {

    public static void main(String[] args) {
        predictMissedGames();
    }

    static void predictMissedGames() {
//        DataSource.openConnection();
//
//        Date checkGamesAfterDate = LastPredicted.getWhenMissedGamesWereLastPredicted();
//        checkGamesAfterDate = DateHelper.removeTimeFromDate(checkGamesAfterDate);
//
//        ArrayList<MatchToPredict> matchesWeDidntPredict = DataSource.getMatchesWithoutPredictions(checkGamesAfterDate);
//
//        if (matchesWeDidntPredict.size() > 0) {
//            System.out.println("Predicting missed games. We missed " + matchesWeDidntPredict.size() + " games since " + checkGamesAfterDate);
//
//            //NOTE: When we add features to the match to predict, we will also add features to these missed games we want to log through logic within GetMatchesFromDb.
//            GetMatchesFromDb.setGamesNeedPredictingAfterDate(checkGamesAfterDate);
//            GetMatchesFromDb.setMissedGamesThatNeedPredicting(matchesWeDidntPredict);
//            GetMatchesFromDb.addFeaturesToMatchesToPredict(new ArrayList<>());
//
//            Predict.addOurProbabilitiesToGames(matchesWeDidntPredict, PredictTodaysGames.trainedThetasPath);
//            Predict.missedGamesBetDecisionAndLog(matchesWeDidntPredict);
//
//            LastPredicted.setAllMissedGamesPredictedUpTo2DaysAgo();
//        }
//        System.out.println("Done predicting games.");
//        DataSource.closeConnection();
    }
}
