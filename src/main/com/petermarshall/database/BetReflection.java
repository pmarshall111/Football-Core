package com.petermarshall.database;

import java.util.Date;

public class BetReflection {
    private Date kickoffDate;
    private String homeTeamName;
    private String awayTeamName;

    private double stake;
    private double oddsAtTimeOfBet;

    private int teamPlacedBetOn;
    private int result;

    public BetReflection(Date kickoffDate, String homeTeamName, String awayTeamName, double stake, double oddsAtTimeOfBet, int teamPlacedBetOn, int result) {
        this.kickoffDate = kickoffDate;
        this.homeTeamName = homeTeamName;
        this.awayTeamName = awayTeamName;
        this.stake = stake;
        this.oddsAtTimeOfBet = oddsAtTimeOfBet;
        this.teamPlacedBetOn = teamPlacedBetOn;
        this.result = result;
    }

    public Date getKickoffDate() {
        return kickoffDate;
    }

    public String getHomeTeamName() {
        return homeTeamName;
    }

    public String getAwayTeamName() {
        return awayTeamName;
    }

    public int getResult() {
        return result;
    }
}
