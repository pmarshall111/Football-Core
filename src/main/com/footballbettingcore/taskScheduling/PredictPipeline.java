package com.footballbettingcore.taskScheduling;

import com.footballbettingcore.database.BetLog;
import com.footballbettingcore.machineLearning.BookieBetInfo;
import com.footballbettingcore.machineLearning.Predict;
import com.footballbettingcore.database.datasource.DS_Get;
import com.footballbettingcore.database.datasource.DS_Insert;
import com.footballbettingcore.database.datasource.DS_Main;
import com.footballbettingcore.machineLearning.createData.CalcPastStats;
import com.footballbettingcore.machineLearning.createData.classes.MatchToPredict;
import com.footballbettingcore.scrape.OddsChecker;
import com.footballbettingcore.scrape.placeBet.PlaceBet;

import java.util.ArrayList;

public class PredictPipeline {

    public static void main(String[] args) {
        predictGames();
    }

    //Method will create non-lineup predictions for all teams next games in the database, but only the next 1 game.
    //Then will place a bet for us if good odds found, first will try Bet365, then if not possible tries UniBet
    public static void predictGames() {
        DS_Main.openProductionConnection();
        UpdatePipeline.updatePlayedGames(false);
        ArrayList<MatchToPredict> mtps = DS_Get.getMatchesToPredict();
        if (mtps.size() > 0) {
            OddsChecker.addBookiesOddsForGames(mtps);
            mtps.removeIf(mtp -> mtp.getBookiesOdds() == null || mtp.getBookiesOdds().size() == 0);
            CalcPastStats.addFeaturesToPredict(mtps, false);
            Predict.addPredictionsToGames(mtps);
            mtps.removeIf(mtp -> mtp.getOurPredictions(false) == null || mtp.getOurPredictions(false).length == 0);
            DS_Insert.addPredictionsToDb(mtps);
            mtps.removeIf(mtp -> mtp.getGoodBets() == null || mtp.getGoodBets().size() == 0);
            PlaceBet.betOnMatches(mtps);
        }
    }

}
