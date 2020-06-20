package com.petermarshall.database.dbTables;

public class PredictionTable {
    private PredictionTable() {}

    private static final String TABLE_NAME = "prediction";
    private static final String COL_DATE = "date";
    private static final String COL_WITH_LINEUPS = "withLineups";
    private static final String COL_HOME_PRED = "homePred";
    private static final String COL_DRAW_PRED = "drawPred";
    private static final String COL_AWAY_PRED = "awayPred";
    private static final String COL_H_ODDS = "homeOdds";
    private static final String COL_D_ODDS = "drawOdds";
    private static final String COL_A_ODDS = "awayOdds";
    private static final String COL_BOOKIE_NAME = "bookie";
    private static final String COL_MATCH_ID = "match_id";

    public static String getTableName() {
        return TABLE_NAME;
    }

    public static String getColDate() {
        return COL_DATE;
    }

    public static String getColWithLineups() {
        return COL_WITH_LINEUPS;
    }

    public static String getColHomePred() {
        return COL_HOME_PRED;
    }

    public static String getColDrawPred() {
        return COL_DRAW_PRED;
    }

    public static String getColAwayPred() {
        return COL_AWAY_PRED;
    }

    public static String getColHOdds() {
        return COL_H_ODDS;
    }

    public static String getColDOdds() {
        return COL_D_ODDS;
    }

    public static String getColAOdds() {
        return COL_A_ODDS;
    }

    public static String getColBookieName() {
        return COL_BOOKIE_NAME;
    }

    public static String getColMatchId() {
        return COL_MATCH_ID;
    }
}
