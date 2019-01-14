package com.petermarshall.database.tables;

public class LeagueTable {
    private static final String TABLE_NAME = "league";
    private static final String COL_NAME = "name";

    public static String getTableName() {
        return TABLE_NAME;
    }

    public static String getColName() {
        return COL_NAME;
    }
}
