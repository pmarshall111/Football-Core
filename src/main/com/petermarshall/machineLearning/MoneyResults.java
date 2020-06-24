package com.petermarshall.machineLearning;

import com.petermarshall.database.Result;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;

public class MoneyResults {
    private double moneyGotBack;
    private double moneySpent;
    private int betsMade;
    private HashMap<String, ArrayList<Bet>> betInfo;

    public MoneyResults() {
        moneyGotBack = 0;
        moneySpent = 0;
        betsMade = 0;
        initBetInfo();
    }

    private void initBetInfo() {
        betInfo = new HashMap<>();
        betInfo.put("1-1.4", new ArrayList<>());
        betInfo.put("1.4-2", new ArrayList<>());
        betInfo.put("2-3", new ArrayList<>());
        betInfo.put("3-4", new ArrayList<>());
        betInfo.put("4-5", new ArrayList<>());
        betInfo.put("5-6", new ArrayList<>());
        betInfo.put("6-7", new ArrayList<>());
        betInfo.put("7-8", new ArrayList<>());
        betInfo.put("8-9", new ArrayList<>());
        betInfo.put("9-10", new ArrayList<>());
        betInfo.put("10+", new ArrayList<>());
    }

    public void addBet(double stake, double odds, boolean wasCorrect, Result r) {
        betsMade++;
        moneySpent += stake;
        if (wasCorrect) {
            moneyGotBack += stake*odds;
        }

        Bet b = new Bet(odds, stake, wasCorrect, r);
        if (odds <= 1.4) {
            betInfo.get("1-1.4").add(b);
        } else if (odds <= 2) {
            betInfo.get("1.4-2").add(b);
        } else if (odds <= 3) {
            betInfo.get("2-3").add(b);
        } else if (odds <= 4) {
            betInfo.get("3-4").add(b);
        } else if (odds <= 5) {
            betInfo.get("4-5").add(b);
        } else if (odds <= 6) {
            betInfo.get("5-6").add(b);
        } else if (odds <= 7) {
            betInfo.get("6-7").add(b);
        } else if (odds <= 8) {
            betInfo.get("7-8").add(b);
        } else if (odds <= 9) {
            betInfo.get("8-9").add(b);
        } else if (odds <= 10) {
            betInfo.get("9-10").add(b);
        } else {
            betInfo.get("10+").add(b);
        }
    }


    public void printResults() {
        System.out.println("Total bets: " + betsMade);
        System.out.println("Money spent: " + moneySpent);
        System.out.println("Money got back: " + moneyGotBack);
        System.out.println("Profit: " + (moneyGotBack - moneySpent));
        System.out.println("Percentage profit: " + (100*(moneyGotBack-moneySpent)/moneySpent));

        System.out.println("\n Bet Anaylysis...");
        System.out.println("1-1.4: " + getInfoFromArrList(betInfo.get("1-1.4"), 1.2));
        System.out.println("1.4-2: " + getInfoFromArrList(betInfo.get("1.4-2"), 1.7));
        System.out.println("2-3: " + getInfoFromArrList(betInfo.get("2-3"), 2.5));
        System.out.println("3-4: " + getInfoFromArrList(betInfo.get("3-4"), 3.5));
        System.out.println("4-5: " + getInfoFromArrList(betInfo.get("4-5"), 4.5));
        System.out.println("5-6: " + getInfoFromArrList(betInfo.get("5-6"), 5.5));
        System.out.println("6-7: " + getInfoFromArrList(betInfo.get("6-7"), 6.5));
        System.out.println("7-8: " + getInfoFromArrList(betInfo.get("7-8"), 7.5));
        System.out.println("8-9: " + getInfoFromArrList(betInfo.get("8-9"), 8.5));
        System.out.println("9-10: " + getInfoFromArrList(betInfo.get("9-10"), 9.5));
        System.out.println("10+: " + getInfoFromArrList(betInfo.get("10+"), 10.5));
    }

    private String getInfoFromArrList(ArrayList<Bet> bets, double midPoint) {
        int success = 0;
        int failed = 0;
        double spent = 0;
        double gotBack = 0;

        for (Bet b: bets) {
            spent+=b.getStake();
            if (b.isCorrect()) {
                success++;
                gotBack+=b.getStake()*b.getOdds();
            }
            else failed++;
        }

        double profitPerc = (gotBack-spent)/spent;
        double expectedSuccessPerc = 1/midPoint;
        double actualSuccessPerc = success/(success+failed+0d);
        DecimalFormat twoDp = new DecimalFormat("#.##");
        return "N: " + (success+failed) + ", S: " + success + " SuccessPerc: " + twoDp.format(actualSuccessPerc) + " Exp SuccessPerc: "
                + twoDp.format(expectedSuccessPerc) + " Prof: " + twoDp.format(profitPerc);
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

class Bet {
    private double odds;
    private double stake;
    private boolean wasCorrect;
    private Result res;

    public Bet(double odds, double stake, boolean wasCorrect, Result res) {
        this.odds = odds;
        this.stake = stake;
        this.wasCorrect = wasCorrect;
        this.res = res;
    }

    public double getOdds() {
        return odds;
    }

    public double getStake() {
        return stake;
    }

    public boolean isCorrect() {
        return wasCorrect;
    }

    public Result getRes() {
        return res;
    }
}
