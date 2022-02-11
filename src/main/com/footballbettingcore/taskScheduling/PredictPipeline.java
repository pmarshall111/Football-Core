package com.footballbettingcore.taskScheduling;

import com.footballbettingcore.betfair.OddsRetriever;
import com.footballbettingcore.machineLearning.Predict;
import com.footballbettingcore.database.datasource.DS_Get;
import com.footballbettingcore.database.datasource.DS_Insert;
import com.footballbettingcore.database.datasource.DS_Main;
import com.footballbettingcore.machineLearning.createData.CalcPastStats;
import com.footballbettingcore.machineLearning.createData.classes.MatchToPredict;
import com.footballbettingcore.betfair.PlaceBet;
import com.jbetfairng.exceptions.LoginException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.stream.Collectors;

public class PredictPipeline {
    private static final Logger logger = LogManager.getLogger(PredictPipeline.class);

    public static void main(String[] args) {
        predictGames();
    }

    //Method will create non-lineup predictions for all teams next games in the database, but only the next 1 game.
    public static void predictGames() {
        DS_Main.openProductionConnection();
        UpdatePipeline.updatePlayedGames(false);
        ArrayList<MatchToPredict> mtps = DS_Get.getMatchesToPredict();
        if (mtps.size() > 0) {
            try {
                OddsRetriever.addOddsToMatches(mtps);
                ArrayList<MatchToPredict> matchesWithoutOdds = mtps.stream().filter(mtp ->
                        mtp.getBookiesOdds() == null || mtp.getBookiesOdds().size() == 0)
                        .collect(Collectors.toCollection(ArrayList::new));
                matchesWithoutOdds.forEach(match -> logger.warn("Couldn't find odds on Betfair for match: " + match.getMatchString()));
                mtps.removeAll(matchesWithoutOdds);

                CalcPastStats.addFeaturesToPredict(mtps, false);
                Predict.addPredictionsToGames(mtps);
                mtps.removeIf(mtp -> mtp.getOurPredictions(false) == null || mtp.getOurPredictions(false).length == 0);
                DS_Insert.addPredictionsToDb(mtps);
                mtps.removeIf(mtp -> mtp.getGoodBets() == null || mtp.getGoodBets().size() == 0);
                PlaceBet.betOnMatches(mtps);
            } catch (LoginException e) {
                logger.error("Unable to log in to Betfair API", e);
            }
        }
    }

}
