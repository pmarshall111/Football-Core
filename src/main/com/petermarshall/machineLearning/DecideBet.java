package com.petermarshall.machineLearning;

import com.petermarshall.machineLearning.createData.classes.MatchToPredict;
import com.petermarshall.scrape.classes.OddsCheckerBookies;

import java.util.ArrayList;

import static com.petermarshall.ConvertOdds.fromOddsToProbability;
import static com.petermarshall.ConvertOdds.fromProbabilityToOdds;
import static com.petermarshall.database.Result.*;

public class DecideBet {
    public static final int INITIAL_BET = 5;
    public static final int MAX_STAKE = 35;
    public static final int MIN_STAKE = 1;
    public static final double btb = 0.15; //Note: if changing btb, will need to change MULT_TO_GET_AVG_OF_1 as this was chosen to give avg bet of initial stake.
    public static final double MULT_TO_GET_AVG_OF_1 = 25;

    public static void addDecisionRealMatches(ArrayList<MatchToPredict> mtps) {
        mtps.forEach(mtp -> {
            double bet365HomePred = 999, bet365DrawPred = 999, bet365AwayPred = 999;
            boolean bet365ScrapeSuccess = false;
            try {
                double[] bet365Odds = mtp.getBookiesOdds().get(OddsCheckerBookies.BET365.getName());
                bet365HomePred = fromOddsToProbability(bet365Odds[0]);
                bet365DrawPred = fromOddsToProbability(bet365Odds[1]);
                bet365AwayPred = fromOddsToProbability(bet365Odds[2]);
                if (bet365HomePred != -1 && bet365DrawPred != -1 && bet365AwayPred != -1) {
                    bet365ScrapeSuccess = true;
                }
            } catch (NullPointerException e) {
                System.out.println("Couldn't get Bet365 odds for " + mtp.getHomeTeamName() + " vs " + mtp.getAwayTeamName());
                e.printStackTrace();
                bet365ScrapeSuccess = false;
            }

            double unibetHomePred = 999, unibetDrawPred = 999, unibetAwayPred = 999;
            boolean unibetScrapeSuccess = false;
            try {
                double[] unibetOdds = mtp.getBookiesOdds().get(OddsCheckerBookies.UNIBET.getName());
                unibetHomePred = fromOddsToProbability(unibetOdds[0]);
                unibetDrawPred = fromOddsToProbability(unibetOdds[1]);
                unibetAwayPred = fromOddsToProbability(unibetOdds[2]);
                if (unibetHomePred != -1 && unibetDrawPred != -1 && unibetAwayPred != -1) {
                    unibetScrapeSuccess = true;
                }
            } catch (NullPointerException e) {
                System.out.println("Couldn't get Unibet odds for " + mtp.getHomeTeamName() + " vs " + mtp.getAwayTeamName());
                e.printStackTrace();
                unibetScrapeSuccess = false;
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
                if (bet365ScrapeSuccess && bet365BetterBy > 0) {
                    double varStake = roundToLimits(getVariableStake(bet365BetterBy));
                    bd.addBookie(OddsCheckerBookies.BET365, varStake, fromProbabilityToOdds(bet365HomePred));
                }
                if (unibetScrapeSuccess && unibetBetterBy > 0) {
                    double varStake = roundToLimits(getVariableStake(unibetBetterBy));
                    bd.addBookie(OddsCheckerBookies.UNIBET, varStake, fromProbabilityToOdds(unibetHomePred));
                }
                if (bd.getBookiePriority().size() > 0) {
                    mtp.addGoodBet(bd);
                }
            } else if (awayPrediction > homePrediction && awayPrediction > drawPrediction) {
                double bet365BetterBy = awayPrediction - bet365AwayPred - btb;
                double unibetBetterBy = awayPrediction - unibetAwayPred - btb;
                BetDecision bd = new BetDecision(AWAY_WIN);
                if (bet365ScrapeSuccess && bet365BetterBy > 0) {
                    double varStake = roundToLimits(getVariableStake(bet365BetterBy));
                    bd.addBookie(OddsCheckerBookies.BET365, varStake, fromProbabilityToOdds(bet365AwayPred));
                }
                if (unibetScrapeSuccess && unibetBetterBy > 0) {
                    double varStake = roundToLimits(getVariableStake(unibetBetterBy));
                    bd.addBookie(OddsCheckerBookies.UNIBET, varStake, fromProbabilityToOdds(unibetAwayPred));
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
