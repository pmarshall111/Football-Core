package com.footballbettingcore.machineLearning.createData;

import java.sql.ResultSet;
import java.sql.SQLException;

public class HistoricMatchDbData {
    private final String homeTeam;
    private final String awayTeam;
    private final int homeScore;
    private final int awayScore;
    private final int seasonYearStart;
    private final double homeXG;
    private final double awayXG;

    public HistoricMatchDbData(ResultSet rs) throws SQLException {
        this.homeTeam = rs.getString(1);
        this.awayTeam = rs.getString(2);
        this.homeScore = rs.getInt(3);
        this.awayScore = rs.getInt(4);
        this.seasonYearStart = rs.getInt(5);
        this.homeXG = rs.getDouble(6);
        this.awayXG = rs.getDouble(7);
    }

    //NOTE: used for testing purposes only
    public HistoricMatchDbData(String homeTeam, String awayTeam, int homeScore, int awayScore, int seasonYearStart,
                               double homeXG, double awayXG) {
        this.homeTeam = homeTeam;
        this.awayTeam = awayTeam;
        this.homeScore = homeScore;
        this.awayScore = awayScore;
        this.seasonYearStart = seasonYearStart;
        this.homeXG = homeXG;
        this.awayXG = awayXG;
    }

    public String getHomeTeam() {
        return homeTeam;
    }

    public String getAwayTeam() {
        return awayTeam;
    }

    public int getHomeScore() {
        return homeScore;
    }

    public int getSeasonYearStart() {
        return seasonYearStart;
    }

    public int getAwayScore() {
        return awayScore;
    }

    public double getHomeXG() {
        return homeXG;
    }

    public double getAwayXG() {
        return awayXG;
    }
}
