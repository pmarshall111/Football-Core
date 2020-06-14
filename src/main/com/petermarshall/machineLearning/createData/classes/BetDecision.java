package com.petermarshall.machineLearning.createData.classes;

import com.petermarshall.Winner;
import com.petermarshall.database.ResultBetOn;
import com.petermarshall.scrape.classes.OddsCheckerBookies;

public class BetDecision {
    private OddsCheckerBookies bookie;
    private Winner winner;
    private double minOdds;

    public BetDecision(OddsCheckerBookies bookie, Winner winner, double minOdds) {
        this.bookie = bookie;
        this.winner = winner;
        this.minOdds = minOdds;
    }

    public OddsCheckerBookies getBookie() {
        return bookie;
    }

    public Winner getWinner() {
        return winner;
    }

    public double getMinOdds() {
        return minOdds;
    }
}
