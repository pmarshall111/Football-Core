package dbTables;

public class BetTable {
    private BetTable() {}

    private static final String TABLE_NAME = "bet";
    private static final String COL_RESULT_BET_ON = "resultBetOn";
    private static final String COL_ODDS = "odds";
    private static final String COL_STAKE = "stake";
    private static final String COL_MATCH_ID = "match_id";
    private static final String COL_BET_PLACED_WITH = "betPlacedWith";

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
