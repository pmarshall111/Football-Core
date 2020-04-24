package com.petermarshall.machineLearning.createData.refactor;

import java.sql.ResultSet;
import java.sql.SQLException;

public class HistoricMatchDbData {
    private String homeTeam;
    private String awayTeam;
    private int homeScore;
    private int awayScore;
    private int seasonYearStart;

    public HistoricMatchDbData(ResultSet rs) throws SQLException {
        this.homeTeam = rs.getString(1);
        this.awayTeam = rs.getString(2);
        this.homeScore = rs.getInt(3);
        this.awayScore = rs.getInt(4);
        this.seasonYearStart = rs.getInt(5);
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
}
