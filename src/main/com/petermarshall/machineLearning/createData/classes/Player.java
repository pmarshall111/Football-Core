package com.petermarshall.machineLearning.createData.classes;

import java.util.HashMap;

import static com.petermarshall.machineLearning.createData.classes.TrainingTeamsSeason.AVG_RATING_PER_GAME;

//Decision taken in this class to have separate fields for home and away instead of having all the data in 1 ratings array and filtering
//every time we want home/away. Choice taken for speed as we will be sorting through a teams players for each match, so performance wise
//it's better to not have to calculate these things each time.
public class Player {
    private String playerName;
    private int ovrMins = 0;
    private int homeMins = 0;
    private int awayMins = 0;
    private int totalGames = 0;
    private int homeGames = 0;
    private int awayGames = 0;
    private double weightedOvrRating = 0;
    private double weightedHomeRating = 0;
    private double weightedAwayRating = 0;
    private double totalOvrRating = 0;
    private double totalHomeRating = 0;
    private double totalAwayRating = 0;
    private HashMap<String, Integer> playerPositionCount = new HashMap<>();

    public Player(String playerName, int mins, double rating, boolean homeTeam, String playerPosition) {
        this.playerName = playerName;
        addMatchMinsRating(mins, rating, homeTeam, playerPosition);
    }
    //only to be used when creating a Player from a match that has yet to be played and will be sent off to be predicted.
    public Player(String playerName) {
        this.playerName = playerName;
    }

    public void addMatchMinsRating(int mins, double rating, boolean homeTeam, String playerPosition) {
        this.ovrMins += mins;
        this.totalGames++;
        this.totalOvrRating += rating;
        this.weightedOvrRating += mins * rating;
        this.playerPositionCount.put(playerPosition, this.playerPositionCount.getOrDefault(playerPosition, 0) + 1);
        if (homeTeam) {
            this.homeMins += mins;
            this.homeGames++;
            this.totalHomeRating += rating;
            this.weightedHomeRating += rating * mins;
        } else {
            this.awayMins += mins;
            this.awayGames++;
            this.totalAwayRating += rating;
            this.weightedAwayRating += rating * mins;
        }
    }

    public String getPlayerName() {
        return playerName;
    }

    public int getOvrMins() {
        return ovrMins;
    }
    public int getHomeMins() {
        return homeMins;
    }
    public int getAwayMins() {
        return awayMins;
    }

    public double getSummedWeightedOvrRating() {
        return weightedOvrRating;
    }
    public double getSummedWeightedHomeRating() {
        return weightedHomeRating;
    }
    public double getSummedWeightedAwayRating() {
        return weightedAwayRating;
    }

    //if we have no data on the player yet, give the default rating of 6.
    public double getAvgOvrRating() {
        return this.totalGames == 0 ? AVG_RATING_PER_GAME : this.totalOvrRating/this.totalGames;
    }
    public double getAvgHomeRating() {
        return this.homeGames == 0 ? AVG_RATING_PER_GAME : this.totalHomeRating/this.homeGames;
    }
    public double getAvgAwayRating() {
        return this.awayGames == 0 ? AVG_RATING_PER_GAME : this.totalAwayRating/this.awayGames;
    }

    public String getPlayerPosition() {
        String modePosition = "_";
        int gamesAtPosition = 0;
        for (String position: this.playerPositionCount.keySet()) {
            if (this.playerPositionCount.get(position) > gamesAtPosition) {
                modePosition = position;
                gamesAtPosition = this.playerPositionCount.get(position);
            }
        }
        return modePosition;
    }
}
