package com.petermarshall.machineLearning.createData.classes;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;

/*
 * Class will get created from DataSource.getBaseMatchesToPredict().
 * Lineups, features, bookiePredictions and our predictions will be added through setters.
 */
public class MatchToPredict {
    private final String sqlDateString; //will be stored as an SQL type datestring e.g. 2018-12-21 20:00:00

    private final String homeTeamName;
    private final String awayTeamName;

    private final String seasonKey;
    private final String leagueSeasonIdName;
    private final int sofaScoreId;

    private ArrayList<String> homeTeamPlayers;
    private ArrayList<String> awayTeamPlayers;

    private ArrayList<Double> features; //will be set with bias parameter included as first entry

    private double[] ourPredictions;
    private LinkedHashMap<String, double[]> bookiesOdds;

    private final int database_id;

    public MatchToPredict(String homeTeamName, String awayTeamName, String seasonKey, String leagueName, int sofaScoreId, String sqlDateString, int database_id) {
        this.homeTeamName = homeTeamName;
        this.awayTeamName = awayTeamName;
        this.seasonKey = seasonKey;
        this.leagueSeasonIdName = leagueName;
        this.sofaScoreId = sofaScoreId;
        this.sqlDateString = sqlDateString;
        this.database_id = database_id;
    }

    public void setHomeTeamPlayers(ArrayList<String> homeTeamPlayers) {
        this.homeTeamPlayers = homeTeamPlayers;
    }

    public void setAwayTeamPlayers(ArrayList<String> awayTeamPlayers) {
        this.awayTeamPlayers = awayTeamPlayers;
    }

    public String getLeagueSeasonIdName() {
        return leagueSeasonIdName;
    }

    public String getHomeTeamName() {
        return homeTeamName;
    }

    public String getAwayTeamName() {
        return awayTeamName;
    }

    public String getSeasonKey() {
        return seasonKey;
    }

    public int getSeasonYearStart() {
        return Integer.parseInt(seasonKey.substring(0,2));
    }

    public int getSofaScoreId() {
        return sofaScoreId;
    }

    public ArrayList<String> getHomeTeamPlayers() {
        return homeTeamPlayers;
    }

    public ArrayList<String> getAwayTeamPlayers() {
        return awayTeamPlayers;
    }

    public double[] getOurPredictions() {
        return ourPredictions;
    }

    public void setOurPredictions(double[] ourPredictions) {
        this.ourPredictions = ourPredictions;
    }

    public HashMap<String, double[]> getBookiesOdds() {
        return bookiesOdds;
    }

    public void setBookiesOdds(LinkedHashMap<String, double[]> bookiesOdds) {
        this.bookiesOdds = bookiesOdds;
    }

    public String getSqlDateString() {
        return sqlDateString;
    }

    public ArrayList<Double> getFeatures() {
        return features;
    }

    public void setFeatures(ArrayList<Double> features) {
        this.features = features;
    }

    public int getDatabase_id() {
        return database_id;
    }

}
