package com.petermarshall.database;

public enum WhenGameWasPredicted {
    NOT_PREDICTED_ON(-1),
    PREDICTED_ON_IN_REAL_TIME(0),
    PREDICTED_LATER_ON(1);

    private final int sqlIntCode;

    WhenGameWasPredicted(int sqlIntCode) {
        this.sqlIntCode = sqlIntCode;
    }

    public int getSqlIntCode() {
        return sqlIntCode;
    }
}
