package com.footballbettingcore.machineLearning.createData.classes;

public class LeagueStatsForSeason {
    private int games;
    private int leagueHomeGoals;
    private int leagueAwayGoals;
    private double leagueHomeXG;
    private double leagueAwayXG;

    public LeagueStatsForSeason() {
        games = 0;
        leagueHomeGoals = 0;
        leagueAwayGoals = 0;
        leagueHomeXG = 0;
        leagueAwayXG = 0;
    }

    public void addGame(int homeGoals, int awayGoals, double homeXG, double awayXG) {
        games++;
        leagueHomeGoals += homeGoals;
        leagueAwayGoals += awayGoals;
        leagueHomeXG += homeXG;
        leagueAwayXG += awayXG;
    }

    public double getAvgHomeGoalsPerGame() {
        return ((double) leagueHomeGoals) / games;
    }

    public double getAvgAwayGoalsPerGame() {
        return ((double) leagueAwayGoals) / games;
    }

    public double getAvgHomeXgPerGame() {
        return ((double) leagueHomeXG) / games;
    }

    public double getAvgAwayXgPerGame() {
        return ((double) leagueAwayXG) / games;
    }
}
