package com.petermarshall.database;

public enum ResultBetOn {
    NOT_BET_ON(-1),
    HOME_WIN(1),
    DRAW(2),
    AWAY_WIN(3);

    private final int sqlIntCode;

    ResultBetOn(int sqlIntCode) {
        this.sqlIntCode = sqlIntCode;
    }

    public int getSqlIntCode() {
        return sqlIntCode;
    }
}
