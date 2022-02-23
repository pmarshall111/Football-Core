package database;

import com.footballbettingcore.database.datasource.DS_Insert;
import com.footballbettingcore.database.datasource.DS_Main;
import com.footballbettingcore.database.datasource.dbTables.*;

import java.sql.SQLException;
import java.sql.Statement;

import static com.footballbettingcore.database.datasource.DS_Main.connection;

public abstract class DbTestHelper {
    static void dropDatabaseTables() {
        dropTable(BetTable.getTableName());
        dropTable(PlayerRatingTable.getTableName());
        dropTable(PredictionTable.getTableName());
        dropTable(MatchTable.getTableName());
        dropTable(TeamTable.getTableName());
        dropTable(LeagueTable.getTableName());
    }

    private static void dropTable(String tableName) {
        try (Statement s = connection.createStatement()) {
            s.addBatch("DELETE FROM " + tableName);
            s.addBatch("DROP TABLE " + tableName);
            s.executeBatch();
        } catch (SQLException | NullPointerException throwables) {
            throwables.printStackTrace();
        }
    }
}
