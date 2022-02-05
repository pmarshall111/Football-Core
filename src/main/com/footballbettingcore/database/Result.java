package com.footballbettingcore.database;

public enum Result {
    HOME_WIN(0),
    DRAW(1),
    AWAY_WIN(2);

    private final int sqlIntCode;

    Result(int sqlIntCode) {
        this.sqlIntCode = sqlIntCode;
    }

    public int getSqlIntCode() {
        return sqlIntCode;
    }

    public static Result getResultFromInt(int sqlIntCode) {
        if (sqlIntCode == 0) return HOME_WIN;
        if (sqlIntCode == 1) return DRAW;
        if (sqlIntCode == 2) return AWAY_WIN;
        return null;
    }
}
