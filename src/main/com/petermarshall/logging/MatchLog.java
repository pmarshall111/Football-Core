package com.petermarshall.logging;

import com.petermarshall.database.WhenGameWasPredicted;
import com.petermarshall.database.ResultBetOn;
import com.petermarshall.machineLearning.createData.classes.MatchToPredict;

public class MatchLog {
    private final MatchToPredict match;
    private final WhenGameWasPredicted whenGameWasPredicted;
    private final ResultBetOn resultBetOn;
    private final String bookieUsed;
    private final double oddsBetOn;
    private final double stake;

    public MatchLog(MatchToPredict match, WhenGameWasPredicted whenGameWasPredicted, ResultBetOn resultBetOn, String bookieUsed, double oddsBetOn, double stake) {
        this.match = match;
        this.whenGameWasPredicted = whenGameWasPredicted;
        this.resultBetOn = resultBetOn;
        this.bookieUsed = bookieUsed;
        this.oddsBetOn = oddsBetOn;
        this.stake = stake;
    }

    public MatchToPredict getMatch() {
        return match;
    }

    public WhenGameWasPredicted getWhenGameWasPredicted() {
        return whenGameWasPredicted;
    }

    public ResultBetOn getResultBetOn() {
        return resultBetOn;
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
