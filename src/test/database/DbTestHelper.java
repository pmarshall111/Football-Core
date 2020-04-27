package database;

import com.petermarshall.database.datasource.DS_Insert;
import com.petermarshall.database.datasource.DS_Main;
import com.petermarshall.database.tables.*;

import java.sql.SQLException;
import java.sql.Statement;

import static com.petermarshall.database.datasource.DS_Main.TEST_CONNECTION_NAME;
import static com.petermarshall.database.datasource.DS_Main.connection;

public abstract class DbTestHelper {
    static void setupNewTestDb() {
        DS_Main.openTestConnection();
        dropDatabase();
        DS_Main.initDB();
        DS_Insert.getNextIds();
    }

    private static void dropDatabase() {
        try {
            if (connection.getMetaData().getURL().equals(TEST_CONNECTION_NAME)) {
                Statement s = connection.createStatement();
                s.addBatch("DROP TABLE " + LeagueTable.getTableName());
                s.addBatch("DROP TABLE " + MatchTable.getTableName());
                s.addBatch("DROP TABLE " + TeamTable.getTableName());
                s.addBatch("DROP TABLE " + PlayerRatingTable.getTableName());
                s.addBatch("DROP TABLE " + BetTable.getTableName());
                s.executeBatch();
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }
}
