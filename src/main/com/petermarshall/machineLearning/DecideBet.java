package com.petermarshall.machineLearning;

import com.petermarshall.machineLearning.createData.classes.MatchToPredict;
import com.petermarshall.scrape.classes.OddsCheckerBookies;

import java.util.ArrayList;

import static com.petermarshall.ConvertOdds.fromOddsToProbability;
import static com.petermarshall.ConvertOdds.fromProbabilityToOdds;
import static com.petermarshall.database.Result.*;

public class DecideBet {
    public static final int INITIAL_BET = 5;

    //IMPORTANT: if changing decision logic, ensure to also change it for model performance (also in this class).
    public static void addDecisionRealMatches(ArrayList<MatchToPredict> mtps) {
        double btb = 0.15; //better than betters
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

            //DECISION LOGIC - bet only on the highest prediction and adjust stake based on how much above their probability we are.
            if (homePrediction > awayPrediction && homePrediction > drawPrediction) {
                double bet365BetterBy = homePrediction - bet365HomePred - btb;
                double unibetBetterBy = homePrediction - unibetHomePred - btb;
                BetDecision bd = new BetDecision(HOME_WIN);
                if (bet365BetterBy > 0) {
                    double varStake = INITIAL_BET * (25*bet365BetterBy);
                    varStake = roundToNearest50p(varStake);
                    bd.addBookie(OddsCheckerBookies.BET365, varStake, fromProbabilityToOdds(bet365HomePred));
                }
                if (unibetBetterBy > 0) {
                    double varStake = INITIAL_BET * (25*unibetBetterBy);
                    varStake = roundToNearest50p(varStake);
                    bd.addBookie(OddsCheckerBookies.UNIBET, varStake, fromProbabilityToOdds(unibetHomePred));
                }
                if (bd.getBookiePriority().size() > 0) {
                    mtp.addGoodBet(bd);
                }
            } else if (awayPrediction > homePrediction && awayPrediction > drawPrediction) {
                double bet365BetterBy = awayPrediction - bet365AwayPred - btb;
                double unibetBetterBy = awayPrediction - unibetAwayPred - btb;
                BetDecision bd = new BetDecision(AWAY_WIN);
                if (bet365BetterBy > 0) {
                    double varStake = INITIAL_BET * (25*bet365BetterBy);
                    varStake = roundToNearest50p(varStake);
                    bd.addBookie(OddsCheckerBookies.BET365, varStake, fromProbabilityToOdds(bet365HomePred));
                }
                if (unibetBetterBy > 0) {
                    double varStake = INITIAL_BET * (25*unibetBetterBy);
                    varStake = roundToNearest50p(varStake);
                    bd.addBookie(OddsCheckerBookies.UNIBET, varStake, fromProbabilityToOdds(unibetHomePred));
                }
                if (bd.getBookiePriority().size() > 0) {
                    mtp.addGoodBet(bd);
                }
            }
        });
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

    private static double roundToNearest50p(double stake) {
        return (double)(Math.round(stake*2))/2;
    }
}
