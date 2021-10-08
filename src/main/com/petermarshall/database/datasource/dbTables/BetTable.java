package com.petermarshall.database.datasource.dbTables;

public class BetTable {
    private BetTable() {}

    public static final String TABLE_NAME = "bet";
    public static final String COL_RESULT_BET_ON = "resultBetOn";
    public static final String COL_ODDS = "odds";
    public static final String COL_STAKE = "stake";
    public static final String COL_MATCH_ID = "match_id";
    public static final String COL_BET_PLACED_WITH = "betPlacedWith";

    public static String getTableName() {
        return TABLE_NAME;
    }

    public static String getColResultBetOn() {
        return COL_RESULT_BET_ON;
    }

    public static String getColOdds() {
        return COL_ODDS;
    }

    public static String getColStake() {
        return COL_STAKE;
    }

    public static String getColMatchId() {
        return COL_MATCH_ID;
    }

    public static String getColBetPlacedWith() {
        return COL_BET_PLACED_WITH;
    }
}
