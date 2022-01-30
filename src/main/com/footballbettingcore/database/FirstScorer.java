package com.footballbettingcore.database;

public enum FirstScorer {
    NO_FIRST_SCORER(-1),
    HOME_FIRST(1),
    AWAY_FIRST(2);

    private final int sqlIntCode;

    FirstScorer(int sqlIntCode) {
        this.sqlIntCode = sqlIntCode;
    }

    public int getSqlIntCode() {
        return sqlIntCode;
    }

    public static FirstScorer getFirstScoreFromSql(int sqlIntCode) {
        switch (sqlIntCode) {
            case 1:
                return HOME_FIRST;
            case 2:
                return AWAY_FIRST;
            default:
                return NO_FIRST_SCORER;
        }
    }
}
