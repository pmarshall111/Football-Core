package com.footballbettingcore.machineLearning;

import com.footballbettingcore.machineLearning.createData.classes.MatchToPredict;
import com.footballbettingcore.scrape.classes.OddsCheckerBookies;

import java.util.ArrayList;

import static com.footballbettingcore.utils.ConvertOdds.fromOddsToProbability;
import static com.footballbettingcore.utils.ConvertOdds.fromProbabilityToOdds;
import static com.footballbettingcore.database.Result.*;

public class DecideBet {
    public static final int INITIAL_BET = 5;
    public static final int MAX_STAKE = 35;
    public static final int MIN_STAKE = 1;
    public static final double btb = 0.15; //Note: if changing btb, will need to change MULT_TO_GET_AVG_OF_1 as this was chosen to give avg bet of initial stake.
    public static final double MULT_TO_GET_AVG_OF_1 = 25;

    public static void addDecisionRealMatches(ArrayList<MatchToPredict> mtps) {
        mtps.forEach(mtp -> {
            double homePred = 999, drawPred = 999, awayPred = 999;
            boolean scrapeSuccess = false;
            try {
                double[] odds = mtp.getBookiesOdds().get(OddsCheckerBookies.OPTIMAL_ODDS.getName());
                homePred = fromOddsToProbability(odds[0]);
                drawPred = fromOddsToProbability(odds[1]);
                awayPred = fromOddsToProbability(odds[2]);
                if (homePred != -1 && drawPred != -1 && awayPred != -1) {
                    scrapeSuccess = true;
                }
            } catch (NullPointerException e) {
                System.out.println("Couldn't get odds for " + mtp.getHomeTeamName() + " vs " + mtp.getAwayTeamName());
                e.printStackTrace();
            }

            double[] ourPredictions = mtp.getOurPredictions(mtp.hasPredictionsWithLineups());
            double homePrediction = ourPredictions[0];
            double drawPrediction = ourPredictions[1];
            double awayPrediction = ourPredictions[2];

            //DECISION LOGIC - bet only on the highest prediction and adjust stake based on how much above their probability we are.
            if (homePrediction > awayPrediction && homePrediction > drawPrediction) {
                double betterBy = homePrediction - homePred - btb;
                BetDecision bd = new BetDecision(HOME_WIN);
                if (scrapeSuccess && betterBy > 0) {
                    double varStake = roundToLimits(getVariableStake(betterBy));
                    bd.addBookie(OddsCheckerBookies.BET365, varStake, fromProbabilityToOdds(homePred));
                    bd.addBookie(OddsCheckerBookies.UNIBET, varStake, fromProbabilityToOdds(homePred));
                }
                if (bd.getBookiePriority().size() > 0) {
                    mtp.addGoodBet(bd);
                }
            } else if (awayPrediction > homePrediction && awayPrediction > drawPrediction) {
                double bet365BetterBy = awayPrediction - awayPred - btb;
                BetDecision bd = new BetDecision(AWAY_WIN);
                if (scrapeSuccess && bet365BetterBy > 0) {
                    double varStake = roundToLimits(getVariableStake(bet365BetterBy));
                    bd.addBookie(OddsCheckerBookies.BET365, varStake, fromProbabilityToOdds(awayPred));
                    bd.addBookie(OddsCheckerBookies.UNIBET, varStake, fromProbabilityToOdds(awayPred));
                }
                if (bd.getBookiePriority().size() > 0) {
                    mtp.addGoodBet(bd);
                }
            }
        });
    }

    public static double getVariableStake(double betterBy) {
        return INITIAL_BET * (MULT_TO_GET_AVG_OF_1*betterBy); //chosen to get average stake of INITIAL_BET
    }

    public static double roundToLimits(double stake) {
        double roundedStake = roundToNearest50p(stake);
        if (roundedStake > MAX_STAKE) {
            return MAX_STAKE;
        } else if (roundedStake < MIN_STAKE) {
            return 0;
        } else {
            return roundedStake;
        }
    }

    public static double roundToNearest50p(double stake) {
        return (double)(Math.round(stake*2))/2;
    }
}
