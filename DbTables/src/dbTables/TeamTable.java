package dbTables;

public class TeamTable {
    private TeamTable() {}

    private static final String TABLE_NAME = "team";
    private static final String COL_TEAM_NAME = "name";
    private static final String COL_LEAGUE_ID = "league_id";

    public static String getTableName() {
        return TABLE_NAME;
    }

    public static String getColTeamName() {
        return COL_TEAM_NAME;
    }

    public static String getColLeagueId() {
        return COL_LEAGUE_ID;
    }
}
