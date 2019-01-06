package com.petermarshall.model.tables;

public class PlayerRatingsTable {
    private static final String TABLE_NAME = "player_ratings";

    private static final String COL_PLAYER_NAME = "player_name";
    private static final String COL_MINS = "mins_played";
    private static final String COL_RATING = "rating";
    private static final String COL_TEAM_ID = "team";
    private static final String COL_MATCH_ID = "match";

    public static String getTableName() {
        return TABLE_NAME;
    }

    public static String getColPlayerName() {
        return COL_PLAYER_NAME;
    }

    public static String getColMins() {
        return COL_MINS;
    }

    public static String getColRating() {
        return COL_RATING;
    }

    public static String getColTeamId() {
        return COL_TEAM_ID;
    }

    public static String getColMatchId() {
        return COL_MATCH_ID;
    }
}
