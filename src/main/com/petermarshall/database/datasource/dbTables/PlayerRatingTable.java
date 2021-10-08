package com.petermarshall.database.datasource.dbTables;

public class PlayerRatingTable {
    private PlayerRatingTable() {}

    private static final String TABLE_NAME = "playerrating";
    private static final String COL_MINS = "minutes";
    private static final String COL_RATING = "rating";
    private static final String COL_PLAYER_NAME = "playerName";
    private static final String COL_MATCH_ID = "match_id";
    private static final String COL_TEAM_ID = "team_id";

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
