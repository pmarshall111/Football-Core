package com.petermarshall.logging;

import com.petermarshall.Winner;
import com.petermarshall.database.WhenGameWasPredicted;
import com.petermarshall.database.ResultBetOn;
import com.petermarshall.machineLearning.createData.classes.MatchToPredict;

public class MatchLog {
    private final MatchToPredict match;
    private final Winner winner;
    private final String bookieUsed;
    private final double oddsBetOn;
    private final double stake;

    public MatchLog(MatchToPredict match, Winner winner, String bookieUsed, double oddsBetOn, double stake) {
        this.match = match;
        this.winner = winner;
        this.bookieUsed = bookieUsed;
        this.oddsBetOn = oddsBetOn;
        this.stake = stake;
    }

    public MatchToPredict getMatch() {
        return match;
    }

    public Winner getWinner() {
        return winner;
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
