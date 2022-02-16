package com.footballbettingcore.machineLearning;

import com.footballbettingcore.database.Result;

public class BookieBetInfo implements Comparable {
    private String bookie;
    private double stake;
    private double minOdds;
    private Result betOn;
    private boolean isLayBet;

    public BookieBetInfo(String bookie, Result betOn, double stake, double minOdds, boolean isLayBet) {
        this.bookie = bookie;
        this.betOn = betOn;
        this.stake = getStakeToNearest50p(stake);
        this.minOdds = minOdds;
        this.isLayBet = isLayBet;
    }

    @Override
    public int compareTo(Object that) {
        if (!that.getClass().equals(this.getClass())) {
            throw new RuntimeException("BetInfo should only be compared with BetInfo");
        }
        return this.minOdds < ((BookieBetInfo) that).getMinOdds() ? 1 : -1;
    }

    public String getBookie() {
        return bookie;
    }

    public double getStake() {
        return stake;
    }

    public double getMinOdds() {
        return minOdds;
    }

    public static double getStakeToNearest50p(double stake) {
        return (double)(Math.round(stake*2))/2;
    }

    public Result getBetOn() {
        return betOn;
    }

    public boolean isLayBet() {
        return isLayBet;
    }
}
