package com.petermarshall.database.datasource.dbTables;

public class MatchTable {
    private MatchTable() {}

    public static final String TABLE_NAME = "game";
    public static final String COL_HOME_SCORE = "homeScore";
    public static final String COL_AWAY_SCORE = "awayScore";
    public static final String COL_HOME_XG = "homeXG";
    public static final String COL_AWAY_XG = "awayXG";
    public static final String COL_DATE = "date";
    public static final String COL_HOME_WIN_ODDS = "homeOdds";
    public static final String COL_DRAW_ODDS = "drawOdds";
    public static final String COL_AWAY_WIN_ODDS = "awayOdds";
    public static final String COL_FIRST_SCORER = "firstScorer";
    public static final String COL_IS_POSTPONED = "isPostponed";
    public static final String COL_HOMETEAM_ID = "homeTeam_id";
    public static final String COL_AWAYTEAM_ID = "awayTeam_id";
    public static final String COL_SEASON_YEAR_START = "season_year_start";
    public static final String COL_PREDICTED_LIVE = "predictedLive";
    public static final String COL_SOFASCORE_ID = "sofascore_id";

    public static String getColSofascoreId() {
        return COL_SOFASCORE_ID;
    }

    public static String getTableName() {
        return TABLE_NAME;
    }

    public static String getColHomeScore() {
        return COL_HOME_SCORE;
    }

    public static String getColAwayScore() {
        return COL_AWAY_SCORE;
    }

    public static String getColHomeXg() {
        return COL_HOME_XG;
    }

    public static String getColAwayXg() {
        return COL_AWAY_XG;
    }

    public static String getColDate() {
        return COL_DATE;
    }

    public static String getColHomeWinOdds() {
        return COL_HOME_WIN_ODDS;
    }

    public static String getColDrawOdds() {
        return COL_DRAW_ODDS;
    }

    public static String getColAwayWinOdds() {
        return COL_AWAY_WIN_ODDS;
    }

    public static String getColFirstScorer() {
        return COL_FIRST_SCORER;
    }

    public static String getColIsPostponed() {
        return COL_IS_POSTPONED;
    }

    public static String getColHometeamId() {
        return COL_HOMETEAM_ID;
    }

    public static String getColAwayteamId() {
        return COL_AWAYTEAM_ID;
    }

    public static String getColSeasonYearStart() {
        return COL_SEASON_YEAR_START;
    }

    public static String getColPredictedLive() {
        return COL_PREDICTED_LIVE;
    }
}
