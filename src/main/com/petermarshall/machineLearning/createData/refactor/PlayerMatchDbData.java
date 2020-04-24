package com.petermarshall.machineLearning.createData.refactor;

import java.sql.ResultSet;
import java.sql.SQLException;

public class PlayerMatchDbData {
    private String name;
    private int mins;
    private double rating;
    private String playersTeam;
    private String date;
    private String homeTeam;
    private int homeScore;
    private double homeXGF;
    private String awayTeam;
    private int awayScore;
    private double awayXGF;
    private double homeOdds;
    private double drawOdds;
    private double awayOdds;
    private int firstScorer; //1 = hometeam, 2=awayteam
    private int matchId;
    private int seasonYearStart;
    private String leagueName;

    public PlayerMatchDbData(ResultSet rs) throws SQLException {
        this.name = rs.getString(1);
        this.mins = rs.getInt(2);
        this.rating = rs.getDouble(3);
        this.playersTeam = rs.getString(4);
        this.date = rs.getString(5);
        this.homeTeam = rs.getString(6);
        this.homeScore = rs.getInt(7);
        this.homeXGF = rs.getDouble(8);
        this.awayTeam = rs.getString(9);
        this.awayScore = rs.getInt(10);
        this.awayXGF = rs.getDouble(11);
        this.homeOdds = rs.getDouble(12);
        this.drawOdds = rs.getDouble(13);
        this.awayOdds = rs.getDouble(14);
        this.firstScorer = rs.getInt(15);
        this.matchId = rs.getInt(16);
        this.seasonYearStart = rs.getInt(17);
        this.leagueName = rs.getString(18);
    }

    public boolean playsForHomeTeam() {
        return this.playersTeam.equals(this.homeTeam);
    }

    public String getName() {
        return name;
    }

    public int getMins() {
        return mins;
    }

    public double getRating() {
        return rating;
    }

    public String getPlayersTeam() {
        return playersTeam;
    }

    public String getDate() {
        return date;
    }

    public String getHomeTeam() {
        return homeTeam;
    }

    public int getHomeScore() {
        return homeScore;
    }

    public double getHomeXGF() {
        return homeXGF;
    }

    public String getAwayTeam() {
        return awayTeam;
    }

    public int getAwayScore() {
        return awayScore;
    }

    public double getAwayXGF() {
        return awayXGF;
    }

    public double getHomeOdds() {
        return homeOdds;
    }

    public double getDrawOdds() {
        return drawOdds;
    }

    public double getAwayOdds() {
        return awayOdds;
    }

    public int getFirstScorer() {
        return firstScorer;
    }

    public int getMatchId() {
        return matchId;
    }

    public int getSeasonYearStart() {
        return seasonYearStart;
    }

    public String getLeagueName() {
        return leagueName;
    }

    public int getResult() {
        if (homeScore == awayScore) return 2;
        else if (homeScore > awayScore) return 1;
        else return 3;
    }
}
