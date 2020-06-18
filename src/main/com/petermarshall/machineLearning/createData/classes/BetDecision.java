package com.petermarshall.machineLearning.createData.classes;

import com.petermarshall.database.Result;
import com.petermarshall.scrape.classes.OddsCheckerBookies;

public class BetDecision {
    private OddsCheckerBookies bookie;
    private Result winner;
    private double minOdds;

    public BetDecision(OddsCheckerBookies bookie, Result winner, double minOdds) {
        this.bookie = bookie;
        this.winner = winner;
        this.minOdds = minOdds;
    }

    public OddsCheckerBookies getBookie() {
        return bookie;
    }

    public Result getWinner() {
        return winner;
    }

    public double getMinOdds() {
        return minOdds;
    }
}
