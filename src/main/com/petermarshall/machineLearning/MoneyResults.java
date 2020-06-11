package com.petermarshall.machineLearning;

public class MoneyResults {
    private double moneyGotBack;
    private double moneySpent;
    private int betsMade;

    public MoneyResults() {
        moneyGotBack = 0;
        moneySpent = 0;
        betsMade = 0;
    }

    public void addBet(int stake, double odds, boolean wasCorrect) {
        betsMade++;
        moneySpent += stake;
        if (wasCorrect) {
            moneyGotBack += stake*odds;
        }
    }

    public void printResults() {
        System.out.println("Total bets: " + betsMade);
        System.out.println("Money spent: " + moneySpent);
        System.out.println("Money got back: " + moneyGotBack);
        System.out.println("Profit: " + (moneyGotBack - moneySpent));
        System.out.println("Percentage profit: " + (100*(moneyGotBack-moneySpent)/moneySpent));
    }

    public double getMoneyGotBack() {
        return moneyGotBack;
    }

    public double getMoneySpent() {
        return moneySpent;
    }

    public int getBetsMade() {
        return betsMade;
    }

    public double getRawProfit() {
        return moneyGotBack-moneySpent;
    }

    public double getPcProfit() {
        return 100*getRawProfit()/getMoneySpent();
    }
}
