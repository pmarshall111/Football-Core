package database;

import com.petermarshall.database.datasource.DS_Insert;
import com.petermarshall.database.datasource.DS_Main;
import com.petermarshall.database.dbTables.*;

import java.sql.SQLException;
import java.sql.Statement;

import static com.petermarshall.database.datasource.DS_Main.TEST_CONNECTION_NAME;
import static com.petermarshall.database.datasource.DS_Main.connection;

public abstract class DbTestHelper {
    static void setupNewTestDb() {
        DS_Main.openTestConnection();
        dropDatabaseTables();
        DS_Main.initDB();
        DS_Insert.getNextIds();
    }

    //NOTE: order of deletion is important here so that we do not delete records that are references to other tables.
    private static void dropDatabaseTables() {
        dropTable(BetTable.getTableName());
        dropTable(PlayerRatingTable.getTableName());
        dropTable(PredictionTable.getTableName());
        dropTable(MatchTable.getTableName());
        dropTable(TeamTable.getTableName());
        dropTable(LeagueTable.getTableName());
    }

    private static void dropTable(String tableName) {
        try (Statement s = connection.createStatement()) {
            if (connection.getMetaData().getURL().equals(TEST_CONNECTION_NAME)) {
                s.addBatch("DELETE FROM " + tableName);
                s.addBatch("DROP TABLE " + tableName);
                s.executeBatch();
            }
        } catch (SQLException | NullPointerException throwables) {
            throwables.printStackTrace();
        }
    }
}
