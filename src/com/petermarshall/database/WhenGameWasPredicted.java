package com.petermarshall.database;

public enum GamePredictedOn {

    NOT_PREDICTED_ON(-1),
    PREDICTED_ON_IN_REAL_TIME(0),
    PREDICTED_ON_LATER(1);

    private final int sqlIntCode;

    GamePredictedOn(int sqlIntCode) {
        this.sqlIntCode = sqlIntCode;
    }

    public int getSqlIntCode() {
        return sqlIntCode;
    }

    public static GamePredictedOn getMeaningOfSqlCode(int code) {
        switch(code) {
            case -1:
                return GamePredictedOn.NOT_PREDICTED_ON;
            case 0:
                return GamePredictedOn.PREDICTED_ON_IN_REAL_TIME;
            case 1:
                return GamePredictedOn.PREDICTED_ON_LATER;
        }

        return null;
    }
}
