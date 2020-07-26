package dbTables;

public class LogTable {
    private LogTable() {}

    private static final String TABLE_NAME = "Log";
    private static final String COL_DATETIME = "dateTime";
    private static final String COL_INFO = "info";

    public static String getTableName() {
        return TABLE_NAME;
    }

    public static String getColDatetime() {
        return COL_DATETIME;
    }

    public static String getColInfo() {
        return COL_INFO;
    }
}
