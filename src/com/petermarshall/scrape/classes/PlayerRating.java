package com.petermarshall.scrape.classes;

//player data for a single game
public class PlayerRating {
    private int minutesPlayed;
    private final double rating;
    private final String name;

    public PlayerRating(int minutesPlayed, double rating, String name) {
        this.minutesPlayed = minutesPlayed;
        this.rating = rating;
        this.name = name;
    }

    public int getMinutesPlayed() {
        return minutesPlayed;
    }
    public void setMinutesPlayed(int minutesPlayed) {
        this.minutesPlayed = minutesPlayed;
    }

    public double getRating() {
        return rating;
    }

    public String getName() {
        return name;
    }
}
