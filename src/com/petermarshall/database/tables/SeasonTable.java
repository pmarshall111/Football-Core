package com.petermarshall.database.tables;

public class SeasonTable {
    private static final String TABLE_NAME = "Season";

    private static final String COL_YEAR_BEGINNING = "yearBeginning";

    public static String getTableName() {
        return TABLE_NAME;
    }

    public static String getColYearBeginning() {
        return COL_YEAR_BEGINNING;
    }
}
