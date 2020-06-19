package com.petermarshall.machineLearning.createData.classes;

import com.petermarshall.database.Result;
import com.petermarshall.scrape.classes.OddsCheckerBookies;

public class BetDecision {
    private OddsCheckerBookies[] bookiePriority;
    private Result winner;
    private double minOdds;

    public BetDecision(OddsCheckerBookies[] bookiePriority, Result winner, double minOdds) {
        this.bookiePriority = bookiePriority;
        this.winner = winner;
        this.minOdds = minOdds;
    }

    public OddsCheckerBookies[] getBookiePriority() {
        return bookiePriority;
    }

    public Result getWinner() {
        return winner;
    }

    public double getMinOdds() {
        return minOdds;
    }
}
