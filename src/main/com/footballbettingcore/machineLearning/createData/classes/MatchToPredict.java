package com.footballbettingcore.machineLearning.createData.classes;

import com.footballbettingcore.machineLearning.BookieBetInfo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;

public class MatchToPredict {
    private final String sqlDateString; //will be stored as an SQL type datestring e.g. 2018-12-21 20:00:00
    private final String homeTeamName;
    private final String awayTeamName;
    private final String seasonKey;
    private final String leagueName;
    private ArrayList<String> homeTeamPlayers;
    private ArrayList<String> awayTeamPlayers;
    private ArrayList<Double> features;
    private ArrayList<Double> featuresNoLineups;
    private double[] ourPredictions;
    private double[] ourPredictionsNoLineups;
    private LinkedHashMap<String, double[]> bookiesOdds;
    private final int database_id;
    private final int sofascore_id;
    private final ArrayList<BookieBetInfo> goodBets;

    public MatchToPredict(String homeTeamName, String awayTeamName, String seasonKey, String leagueName, String sqlDateString, int database_id, int sofascore_id) {
        this.homeTeamName = homeTeamName;
        this.awayTeamName = awayTeamName;
        this.seasonKey = seasonKey;
        this.leagueName = leagueName;
        this.sqlDateString = sqlDateString;
        this.database_id = database_id;
        this.sofascore_id = sofascore_id;
        this.goodBets = new ArrayList<>();
        this.homeTeamPlayers = new ArrayList<>();
        this.awayTeamPlayers = new ArrayList<>();
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

    public double[] getOurPredictions(boolean withLineups) {
        return withLineups ? ourPredictions : ourPredictionsNoLineups;
    }

    public void setOurPredictions(double[] ourPredictions, boolean withLineups) {
        if (withLineups) {
            this.ourPredictions = ourPredictions;
        } else {
            this.ourPredictionsNoLineups = ourPredictions;
        }
    }

    public boolean hasLineups() {
        return this.features != null && this.features.size() > 0;
    }

    public boolean hasPredictionsWithLineups() {
        return ourPredictions != null && ourPredictions.length == 3;
    }

    public boolean hasAnyPredictions() {
        return hasPredictionsWithLineups() || (ourPredictionsNoLineups != null && ourPredictionsNoLineups.length == 3);
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

    public void setFeatures(ArrayList<Double> features) {
            this.features = features;
    }

    public void setFeaturesNoLineups(ArrayList<Double> features) {
        this.featuresNoLineups = features;
    }

    public int getDatabase_id() {
        return database_id;
    }

    public ArrayList<BookieBetInfo> getGoodBets() {
        return goodBets;
    }

    public void addGoodBet(BookieBetInfo goodBet) {
        goodBets.add(goodBet);
    }

    public ArrayList<Double> getFeatures() {
        return features;
    }

    public ArrayList<Double> getFeaturesNoLineups() {
        return featuresNoLineups;
    }

    public String getMatchString() {
        return this.homeTeamName + " vs " + this.awayTeamName + " on " + this.getSqlDateString();
    }
}
