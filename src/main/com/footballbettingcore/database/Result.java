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
}
