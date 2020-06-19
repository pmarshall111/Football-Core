package com.petermarshall.machineLearning;

import com.petermarshall.database.Result;
import com.petermarshall.machineLearning.createData.classes.BetDecision;
import com.petermarshall.machineLearning.createData.classes.MatchToPredict;
import com.petermarshall.scrape.classes.OddsCheckerBookies;
import org.nd4j.linalg.api.ndarray.INDArray;

import java.util.ArrayList;

import static com.petermarshall.ConvertOdds.fromOddsToProbability;
import static com.petermarshall.ConvertOdds.fromProbabilityToOdds;
import static com.petermarshall.database.Result.*;

public class DecideBet {
    //IMPORTANT: if changing decision logic, ensure to also change it for model performance (also in this class).
    public static void addDecisionRealMatches(ArrayList<MatchToPredict> mtps) {
        double btb = 0.1; //better than betters
        mtps.forEach(mtp -> {
            double bet365HomePred = 999, bet365DrawPred = 999, bet365AwayPred = 999;
            try {
                double[] bet365Odds = mtp.getBookiesOdds().get(OddsCheckerBookies.BET365.getName());
                bet365HomePred = fromOddsToProbability(bet365Odds[0]);
                bet365DrawPred = fromOddsToProbability(bet365Odds[1]);
                bet365AwayPred = fromOddsToProbability(bet365Odds[2]);
            } catch (NullPointerException e) {
                System.out.println("Couldn't get Bet365 odds");
                e.printStackTrace();
            }

            double unibetHomePred = 999, unibetDrawPred = 999, unibetAwayPred = 999;
            try {
                double[] unibetOdds = mtp.getBookiesOdds().get(OddsCheckerBookies.UNIBET.getName());
                unibetHomePred = fromOddsToProbability(unibetOdds[0]);
                unibetDrawPred = fromOddsToProbability(unibetOdds[1]);
                unibetAwayPred = fromOddsToProbability(unibetOdds[2]);
            } catch (NullPointerException e) {
                System.out.println("Couldn't get Unibet odds");
                e.printStackTrace();
            }

            double[] ourPredictions = mtp.getOurPredictions(mtp.hasPredictionsWithLineups());
            double homePrediction = ourPredictions[0];
            double drawPrediction = ourPredictions[1];
            double awayPrediction = ourPredictions[2];

            //DECISION LOGIC
            double homeMinBet = homePrediction-btb, awayMinBet = awayPrediction-btb;
            if (homeMinBet > bet365HomePred || homeMinBet > unibetHomePred) {
                mtp.addGoodBet(new BetDecision(createBookiePriority(homeMinBet, bet365HomePred, unibetHomePred), HOME_WIN, fromProbabilityToOdds(homeMinBet)));
            }
            if (awayMinBet > bet365AwayPred || awayMinBet > unibetAwayPred) {
                mtp.addGoodBet(new BetDecision(createBookiePriority(awayMinBet, bet365AwayPred, unibetAwayPred), AWAY_WIN, fromProbabilityToOdds(awayMinBet)));
            }
        });
    }

    //IMPORTANT: if changing decision logic, ensure to also change it for model performance (also in this class).
    static void addDecisionModelPerf(INDArray oddsRow, INDArray predictionRow, INDArray labelsRow, MoneyResults higherThanBookies) {
        double btb = 0.1; //better than bookies
        double bookieHomePred = fromOddsToProbability(oddsRow.getDouble(0));
        double bookieDrawPred = fromOddsToProbability(oddsRow.getDouble(1));
        double bookieAwayPred = fromOddsToProbability(oddsRow.getDouble(2));

        double homePrediction = predictionRow.getDouble(0);
        double drawPrediction = predictionRow.getDouble(1);
        double awayPrediction = predictionRow.getDouble(2);

        //DECISION LOGIC
        if (homePrediction-btb > bookieHomePred) {
            higherThanBookies.addBet(5, oddsRow.getDouble(0), labelsRow.getDouble(0) == 1);
        }
        if (awayPrediction-btb > bookieAwayPred) {
            higherThanBookies.addBet(5, oddsRow.getDouble(2), labelsRow.getDouble(2) == 1);
        }
    }

    private static OddsCheckerBookies[] createBookiePriority(double hasToBeGreaterThan, double bet365Prob, double unibetProb) {
        if (bet365Prob > hasToBeGreaterThan && unibetProb > hasToBeGreaterThan) {
            if (bet365Prob >= unibetProb) {
                return new OddsCheckerBookies[]{OddsCheckerBookies.BET365, OddsCheckerBookies.UNIBET};
            } else {
                return new OddsCheckerBookies[]{OddsCheckerBookies.UNIBET, OddsCheckerBookies.BET365};
            }
        } else if (bet365Prob > hasToBeGreaterThan) {
            return new OddsCheckerBookies[]{OddsCheckerBookies.BET365};
        } else {
            return new OddsCheckerBookies[]{OddsCheckerBookies.UNIBET};
        }
    }
}
