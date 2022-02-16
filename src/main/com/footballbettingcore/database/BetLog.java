package com.footballbettingcore.database;

import com.footballbettingcore.machineLearning.createData.classes.MatchToPredict;

public class BetLog {
    private final MatchToPredict match;
    private final Result rbOn;
    private final String bookieUsed;
    private final double oddsBetOn;
    private final double stake;
    private final boolean isLayBet;

    public BetLog(MatchToPredict match, Result rbOn, String bookieUsed, double oddsBetOn, double stake, boolean isLayBet) {
        this.match = match;
        this.rbOn = rbOn;
        this.bookieUsed = bookieUsed;
        this.oddsBetOn = oddsBetOn;
        this.stake = stake;
        this.isLayBet = isLayBet;
    }

    public MatchToPredict getMatch() {
        return match;
    }

    public Result getResultBetOn() {
        return rbOn;
    }

    public double getOddsBetOn() {
        return oddsBetOn;
    }

    public double getStake() {
        return stake;
    }

    public String getBookieUsed() {
        return bookieUsed;
    }

    public boolean isLayBet() {
        return isLayBet;
    }
}
