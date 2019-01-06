package com.petermarshall.machineLearning.createData.classes;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;

/*
 * Class will get created from DataSource.getBaseMatchesToPredict().
 * Lineups, features, bookiePredictions and our predictions will be added through setters.
 */
public class MatchToPredict {
    private String dateString; //will be stored as an SQL type datestring e.g. 2018-12-21 20:00:00

    private String homeTeamName;
    private String awayTeamName;

    private String seasonKey;
    private String leagueSeasonName;
    private int sofaScoreId;

    private ArrayList<String> homeTeamPlayers;
    private ArrayList<String> awayTeamPlayers;

    private ArrayList<Double> features; //will be set with bias parameter included as first entry

    private double[] ourPredictions;
    private LinkedHashMap<String, double[]> bookiesPredictions;

    public MatchToPredict(String homeTeamName, String awayTeamName, String seasonKey, String leagueSeasonName, int sofaScoreId, String dateString) {
        this.homeTeamName = homeTeamName;
        this.awayTeamName = awayTeamName;
        this.seasonKey = seasonKey;
        this.leagueSeasonName = leagueSeasonName;
        this.sofaScoreId = sofaScoreId;
        this.dateString = dateString;
    }

    public void setHomeTeamPlayers(ArrayList<String> homeTeamPlayers) {
        this.homeTeamPlayers = homeTeamPlayers;
    }

    public void setAwayTeamPlayers(ArrayList<String> awayTeamPlayers) {
        this.awayTeamPlayers = awayTeamPlayers;
    }

    public String getLeagueSeasonName() {
        return leagueSeasonName;
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

    public HashMap<String, double[]> getBookiesPredictions() {
        return bookiesPredictions;
    }

    public void setBookiesPredictions(LinkedHashMap<String, double[]> bookiesPredictions) {
        this.bookiesPredictions = bookiesPredictions;
    }

    public String getDateString() {
        return dateString;
    }

    public ArrayList<Double> getFeatures() {
        return features;
    }

    public void setFeatures(ArrayList<Double> features) {
        this.features = features;
    }
}
