package com.footballbettingcore.database.datasource.dbTables;

public class PredictionTable {
    private PredictionTable() {}

    public static final String TABLE_NAME = "prediction";
    public static final String COL_DATE = "date";
    public static final String COL_WITH_LINEUPS = "withLineups";
    public static final String COL_HOME_PRED = "homePred";
    public static final String COL_DRAW_PRED = "drawPred";
    public static final String COL_AWAY_PRED = "awayPred";
    public static final String COL_H_ODDS = "homeOdds";
    public static final String COL_D_ODDS = "drawOdds";
    public static final String COL_A_ODDS = "awayOdds";
    public static final String COL_BOOKIE_NAME = "bookie";
    public static final String COL_MATCH_ID = "match_id";

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
