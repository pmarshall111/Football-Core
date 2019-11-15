package com.petermarshall.database.tables;

public class PlayerTable {
    private static final String TABLE_NAME = "Player";

    private static final String COL_PLAYER_NAME = "player_name";

    public static String getTableName() {
        return TABLE_NAME;
    }

    public static String getColPlayerName() {
        return COL_PLAYER_NAME;
    }
}
