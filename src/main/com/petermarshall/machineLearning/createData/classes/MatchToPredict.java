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
    private final String leagueName;
    private ArrayList<String> homeTeamPlayers;
    private ArrayList<String> awayTeamPlayers;
    private ArrayList<Double> features; //will be set with bias parameter included as first entry
    private ArrayList<Double> featuresNoLineups;
    private double[] ourPredictions;
    private LinkedHashMap<String, double[]> bookiesOdds;
    //ids here to make it quicker to update the db with the bet if we decide to make a bet, and sofascore to quickly get scraping data.
    private final int database_id;
    private final int sofascore_id;

    public MatchToPredict(String homeTeamName, String awayTeamName, String seasonKey, String leagueName, String sqlDateString, int database_id, int sofascore_id) {
        this.homeTeamName = homeTeamName;
        this.awayTeamName = awayTeamName;
        this.seasonKey = seasonKey;
        this.leagueName = leagueName;
        this.sqlDateString = sqlDateString;
        this.database_id = database_id;
        this.sofascore_id = sofascore_id;
    }

    public int getSofascore_id() {
        return sofascore_id;
    }

    public void setHomeTeamPlayers(ArrayList<String> homeTeamPlayers) {
        this.homeTeamPlayers = homeTeamPlayers;
    }

    public void setAwayTeamPlayers(ArrayList<String> awayTeamPlayers) {
        this.awayTeamPlayers = awayTeamPlayers;
    }

    public String getLeagueName() {
        return leagueName;
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

    public double getHomeOdds() {
        return bookiesOdds.get(0)[0];
    }
    public double getDrawOdds() {
        return bookiesOdds.get(0)[1];
    }
    public double getAwayOdds() {
        return bookiesOdds.get(0)[2];
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

    public void setFeaturesNoLineups(ArrayList<Double> featuresNoLineups) {
        this.featuresNoLineups = featuresNoLineups;
    }

    public int getDatabase_id() {
        return database_id;
    }

}
