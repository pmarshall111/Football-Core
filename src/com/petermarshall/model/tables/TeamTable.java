package com.petermarshall.model.tables;

public class TeamTable {
    private static final String TABLE_NAME = "team";
    private static final String COL_TEAM_NAME = "team_name";
    private static final String COL_SEASON_ID = "season_id";

    public static String getTableName() {
        return TABLE_NAME;
    }

    public static String getColTeamName() {
        return COL_TEAM_NAME;
    }

    public static String getColSeasonId() {
        return COL_SEASON_ID;
    }
}
