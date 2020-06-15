package com.petermarshall.database;

import com.petermarshall.Winner;

public enum Result {
    NOT_BET_ON(-1),
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

    public static Result convertFromWinnerToRbOn(Winner w) {
        if (w.equals(Winner.HOME)) {
            return Result.HOME_WIN;
        } else if (w.equals(Winner.DRAW)) {
            return Result.DRAW;
        } else if (w.equals(Winner.AWAY)) {
            return Result.AWAY_WIN;
        }
        return Result.NOT_BET_ON;
    }
}
