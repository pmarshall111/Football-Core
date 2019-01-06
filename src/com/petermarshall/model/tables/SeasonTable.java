package com.petermarshall.model.tables;

public class SeasonTable {
    private static final String TABLE_NAME = "season";
    private static final String COL_YEAR = "season_years";
    private static final String COL_LEAGUE_ID = "league_id";

    public static String getTableName() {
        return TABLE_NAME;
    }

    public static String getColYear() {
        return COL_YEAR;
    }

    public static String getColLeagueId() {
        return COL_LEAGUE_ID;
    }
}
