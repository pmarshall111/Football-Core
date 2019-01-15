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

    public static ResultBetOn getMeaningOfSqlCode(int code) {
        switch(code) {
            case -1:
                return ResultBetOn.NOT_BET_ON;
            case 0:
                return ResultBetOn.HOME_WIN;
            case 1:
                return ResultBetOn.DRAW;
            case 2:
                return ResultBetOn.AWAY_WIN;
        }

        return null;
    }
}
