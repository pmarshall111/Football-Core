package com.petermarshall.machineLearning;

import com.petermarshall.machineLearning.createData.classes.BetDecision;
import com.petermarshall.machineLearning.createData.classes.MatchToPredict;
import com.petermarshall.scrape.classes.OddsCheckerBookies;

import java.util.ArrayList;

import static com.petermarshall.Winner.AWAY;
import static com.petermarshall.Winner.HOME;
import static com.petermarshall.machineLearning.ModelPerformance.calcProbabilityFromOdds;

public class DecideBet {
    public static void addDecision(ArrayList<MatchToPredict> mtps) {
        double btb = 0.1; //better than betters
        mtps.forEach(mtp -> {
            double[] bet365Odds = mtp.getBookiesOdds().get(OddsCheckerBookies.BET365.getName());
            double bookieHomePred = calcProbabilityFromOdds(bet365Odds[0]);
            double bookieDrawPred = calcProbabilityFromOdds(bet365Odds[1]);
            double bookieAwayPred = calcProbabilityFromOdds(bet365Odds[2]);

            double[] ourPredictions = mtp.getOurPredictions(mtp.hasPredictionsWithLineups());
            double homePrediction = ourPredictions[0];
            double drawPrediction = ourPredictions[1];
            double awayPrediction = ourPredictions[2];

            if (homePrediction-btb > bookieHomePred) {
                mtp.addGoodBet(new BetDecision(OddsCheckerBookies.BET365, HOME, bet365Odds[0]));
            }

            if (awayPrediction-btb > bookieAwayPred) {
                mtp.addGoodBet(new BetDecision(OddsCheckerBookies.BET365, AWAY, bet365Odds[2]));
            }
        });
    }
}
