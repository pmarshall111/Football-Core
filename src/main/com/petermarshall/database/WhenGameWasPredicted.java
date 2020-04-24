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

    public static WhenGameWasPredicted getMeaningOfSqlCode(int code) {
        switch(code) {
            case -1:
                return WhenGameWasPredicted.NOT_PREDICTED_ON;
            case 0:
                return WhenGameWasPredicted.PREDICTED_ON_IN_REAL_TIME;
            case 1:
                return WhenGameWasPredicted.PREDICTED_LATER_ON;
        }

        return null;
    }
}
