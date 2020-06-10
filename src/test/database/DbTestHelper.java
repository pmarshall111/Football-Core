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
        dropDatabaseTables();
        DS_Main.initDB();
        DS_Insert.getNextIds();
    }

    private static void dropDatabase() {
        try {
            if (connection.getMetaData().getURL().equals(TEST_CONNECTION_NAME)) {
                Statement s = connection.createStatement();
//                s.addBatch("DROP TABLE " + LeagueTable.getTableName());
//                s.addBatch("DROP TABLE " + MatchTable.getTableName());
//                s.addBatch("DROP TABLE " + TeamTable.getTableName());
//                s.addBatch("DROP TABLE " + PlayerRatingTable.getTableName());
//                s.addBatch("DROP TABLE " + BetTable.getTableName());
//                s.executeBatch();
            }
        } catch (SQLException | NullPointerException throwables) {
            throwables.printStackTrace();
        }
    }

    //NOTE: order of deletion is important here so that we do not delete records that are references to other tables.
    private static void dropDatabaseTables() {
        //bet
        try (Statement s = connection.createStatement()) {
            if (connection.getMetaData().getURL().equals(TEST_CONNECTION_NAME)) {
                s.execute("DELETE FROM " + BetTable.getTableName());
            }
        } catch (SQLException | NullPointerException throwables) {
//                throwables.printStackTrace();
        }
        //playerrating
        try (Statement s = connection.createStatement()) {
            if (connection.getMetaData().getURL().equals(TEST_CONNECTION_NAME)) {
                s.execute("DELETE FROM " + PlayerRatingTable.getTableName());
            }
        } catch (SQLException | NullPointerException throwables) {
            throwables.printStackTrace();
        }
        //match
        try (Statement s = connection.createStatement()) {
            if (connection.getMetaData().getURL().equals(TEST_CONNECTION_NAME)) {
                s.execute("DELETE FROM " + MatchTable.getTableName());
            }
        } catch (SQLException | NullPointerException throwables) {
            throwables.printStackTrace();
        }
        //team
        try (Statement s = connection.createStatement()) {
            if (connection.getMetaData().getURL().equals(TEST_CONNECTION_NAME)) {
                s.execute("DELETE FROM " + TeamTable.getTableName());
            }
        } catch (SQLException | NullPointerException throwables) {
            throwables.printStackTrace();
        }
        //league
        try (Statement s = connection.createStatement()) {
            if (connection.getMetaData().getURL().equals(TEST_CONNECTION_NAME)) {
                s.execute("DELETE FROM " + LeagueTable.getTableName());
            }
        } catch (SQLException | NullPointerException throwables) {
            throwables.printStackTrace();
        }
    }
}
