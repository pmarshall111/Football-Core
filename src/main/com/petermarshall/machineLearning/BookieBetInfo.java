package com.petermarshall.machineLearning;

import com.petermarshall.scrape.classes.OddsCheckerBookies;

public class BookieBetInfo implements Comparable {
    private OddsCheckerBookies bookie;
    private double stake;
    private double minOdds;

    public BookieBetInfo(OddsCheckerBookies bookie, double stake, double minOdds) {
        this.bookie = bookie;
        this.stake = stake;
        this.minOdds = minOdds;
    }

    @Override
    public int compareTo(Object that) {
        if (!that.getClass().equals(this.getClass())) {
            throw new RuntimeException("BetInfo should only be compared with BetInfo");
        }
        return this.minOdds < ((BookieBetInfo) that).getMinOdds() ? 1 : -1;
    }

    public OddsCheckerBookies getBookie() {
        return bookie;
    }

    public double getStake() {
        return stake;
    }

    public double getMinOdds() {
        return minOdds;
    }
}
