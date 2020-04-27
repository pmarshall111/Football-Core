package com.petermarshall.database;

public enum ResultBetOn {
    NOT_BET_ON(-1),
    HOME_WIN(0),
    DRAW(1),
    AWAY_WIN(2);

    private final int sqlIntCode;

    ResultBetOn(int sqlIntCode) {
        this.sqlIntCode = sqlIntCode;
    }

    public int getSqlIntCode() {
        return sqlIntCode;
    }
}
