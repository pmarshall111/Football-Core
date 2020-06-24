package com.petermarshall.database;

import com.petermarshall.database.Result;
import com.petermarshall.machineLearning.createData.classes.MatchToPredict;

public class BetLog {
    private final MatchToPredict match;
    private final Result rbOn;
    private final String bookieUsed;
    private final double oddsBetOn;
    private final double stake;

    public BetLog(MatchToPredict match, Result rbOn, String bookieUsed, double oddsBetOn, double stake) {
        this.match = match;
        this.rbOn = rbOn;
        this.bookieUsed = bookieUsed;
        this.oddsBetOn = oddsBetOn;
        this.stake = stake;
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
}
