package com.footballbettingcore.machineLearning;

import com.footballbettingcore.database.Result;
import com.footballbettingcore.machineLearning.createData.classes.MatchToPredict;

import java.util.ArrayList;
import java.util.HashMap;

import static com.footballbettingcore.betfair.OddsRetriever.BETFAIR_EXCHANGE;

public class OctavePredictionMapper {
    private final HashMap<Integer, MatchToPredict> matchMap = new HashMap<>();

    public void addPredictionsToMatches(ArrayList<ArrayList<String>> predictions, ArrayList<MatchToPredict> matches) {
        addMatchesToMatchMap(matches);
        predictions.forEach(prediction -> {
            int gameId = Integer.parseInt(prediction.get(0));
            double homePred = Double.parseDouble(prediction.get(1));
            double drawPred = Double.parseDouble(prediction.get(2));
            double awayPred = Double.parseDouble(prediction.get(3));
            MatchToPredict match = matchMap.get(gameId);
            if (match != null) {
                match.setOurPredictions(new double[]{homePred, drawPred, awayPred}, false);
                int resultToBetOn = Integer.parseInt(prediction.get(4));
                double stake = Double.parseDouble(prediction.get(5));
                double odds = Double.parseDouble(prediction.get(6));
                if (resultToBetOn != -1) {
                    BookieBetInfo bet = new BookieBetInfo(BETFAIR_EXCHANGE, Result.getResultFromInt(resultToBetOn), stake, odds);
                    match.addGoodBet(bet);
                }
            }
        });
    }

    private void addMatchesToMatchMap(ArrayList<MatchToPredict> matches) {
        matches.forEach(match -> {
            matchMap.put(match.getDatabase_id(), match);
        });
    }
}
