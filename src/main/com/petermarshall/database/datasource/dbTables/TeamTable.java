package com.petermarshall.database.datasource.dbTables;

public class TeamTable {
    private TeamTable() {}

    public static final String TABLE_NAME = "team";
    public static final String COL_TEAM_NAME = "name";
    public static final String COL_LEAGUE_ID = "league_id";

    public static String getTableName() {
        return TABLE_NAME;
    }

    public static String getColTeamName() {
        return COL_TEAM_NAME;
    }

    public static String getColLeagueId() {
        return COL_LEAGUE_ID;
    }
}
