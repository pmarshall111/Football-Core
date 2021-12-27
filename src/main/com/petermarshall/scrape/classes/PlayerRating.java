package com.petermarshall.scrape.classes;

//player data for a single game
public class PlayerRating {
    private int minutesPlayed;
    private final double rating;
    private final String name;
    private final String position;

    public PlayerRating(int minutesPlayed, double rating, String name, String position) {
        this.minutesPlayed = Math.min(90, minutesPlayed); //forcing a max limit of 90mins played
        this.rating = rating;
        this.name = name;
        this.position = position;
    }

    public int getMinutesPlayed() {
        return minutesPlayed;
    }
    public void setMinutesPlayed(int minutesPlayed) {
        this.minutesPlayed = Math.min(90, minutesPlayed);
    }

    public double getRating() {
        return rating;
    }

    public String getName() {
        return name;
    }

    public String getPosition() {
        return position;
    }
}
