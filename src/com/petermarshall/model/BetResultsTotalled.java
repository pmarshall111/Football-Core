package com.petermarshall.model;

public class BetResultsTotalled {
    private static double totalMoneyOut;
    private static double totalMoneyIn;

    public BetResultsTotalled() { //singleton
    }

    void addBet(double moneyOut, double odds, int teamBetOn, int result) {
        totalMoneyOut += moneyOut;

        if (teamBetOn == result) {
            totalMoneyIn += moneyOut * odds;
        }
    }

    public double getTotalMoneyOut() {
        return totalMoneyOut;
    }

    public double getTotalMoneyIn() {
        return totalMoneyIn;
    }

    public double getRealProfit() {
        return totalMoneyIn - totalMoneyOut;
    }

    public double getPercentageProfit() {
        return getRealProfit()*100 / totalMoneyOut;
    }
}
