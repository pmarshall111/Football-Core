package com.petermarshall.database.tables;

public class MatchTable {

    private static final String TABLE_NAME = "match";
    private static final String COL_DATE = "date";
    private static final String COL_HOMETEAM_ID = "hometeam_id";
    private static final String COL_AWAYTEAM_ID = "awayteam_id";
    private static final String COL_HOME_XG = "home_xG";
    private static final String COL_AWAY_XG = "away_xG";
    private static final String COL_HOME_WIN_ODDS = "home_win_odds";
    private static final String COL_DRAW_ODDS = "draw_odds";
    private static final String COL_AWAY_WIN_ODDS = "away_win_odds";
    private static final String COL_HOME_SCORE = "home_score";
    private static final String COL_AWAY_SCORE = "away_score";
    private static final String COL_FIRST_SCORER = "first_scorer";
    private static final String COL_SOFASCORE_ID = "sofascore_id";
    private static final String COL_RESULT_BET_ON = "result_bet_on";
    private static final String COL_ODDS_WHEN_BET_PLACED = "odds_when_bet_placed";
    private static final String COL_STAKE_ON_BET = "stake_on_bet";


    public static String getTableName() {
        return TABLE_NAME;
    }

    public static String getColDate() {
        return COL_DATE;
    }

    public static String getColHometeamId() {
        return COL_HOMETEAM_ID;
    }

    public static String getColAwayteamId() {
        return COL_AWAYTEAM_ID;
    }

    public static String getColHomeXg() {
        return COL_HOME_XG;
    }

    public static String getColAwayXg() {
        return COL_AWAY_XG;
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

    public static String getColHomeScore() {
        return COL_HOME_SCORE;
    }

    public static String getColAwayScore() {
        return COL_AWAY_SCORE;
    }

    public static String getColFirstScorer() {
        return COL_FIRST_SCORER;
    }

    public static String getColSofascoreId() {
        return COL_SOFASCORE_ID;
    }

    public static String getColResultBetOn() {
        return COL_RESULT_BET_ON;
    }

    public static String getColOddsWhenBetPlaced() {
        return COL_ODDS_WHEN_BET_PLACED;
    }

    public static String getColStakeOnBet() {
        return COL_STAKE_ON_BET;
    }
}
