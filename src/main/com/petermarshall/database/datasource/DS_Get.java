package com.petermarshall.database.datasource;

import com.petermarshall.database.tables.LeagueTable;
import com.petermarshall.database.tables.MatchTable;
import com.petermarshall.database.tables.TeamTable;
import com.petermarshall.scrape.classes.League;
import com.petermarshall.scrape.classes.LeagueSeasonIds;
import com.petermarshall.scrape.classes.Match;
import com.petermarshall.scrape.classes.Team;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;

public class DS_Get {
    static int getMatchId(int homeTeamId, int awayTeamId, int seasonYearStart) {
        try (Statement statement = DS_Main.connection.createStatement()) {

            ResultSet rs = statement.executeQuery("SELECT _id FROM '" + MatchTable.getTableName() +
                    "' WHERE '" + MatchTable.getColHometeamId() + "' = " + homeTeamId +
                    " AND '" + MatchTable.getColAwayteamId() + "' = " + awayTeamId +
                    " AND '" + MatchTable.getColSeasonYearStart() + "' = " + seasonYearStart);

            return rs.getInt(1);
        } catch (SQLException e) {
            System.out.println(e);
            return -9999;
        }
    }

    public static int getLeagueId(String name) {
        try (Statement statement = DS_Main.connection.createStatement()) {

            ResultSet rs = statement.executeQuery("SELECT _id FROM '" + LeagueTable.getTableName() +
                    "' WHERE '" + LeagueTable.getColName() + "' = '" + name + "'");

            return rs.getInt(1);
        } catch (SQLException e) {
            System.out.println(e);
            return -9999;
        }
    }
    public static int getLeagueId(League league) {
        return getLeagueId(league.getName());
    }

    static int getTeamId(String teamName, int leagueId) {
        try (Statement statement = DS_Main.connection.createStatement();
             ResultSet teamQuery = statement.executeQuery("SELECT _id FROM '" + TeamTable.getTableName() +
                     "WHERE " + TeamTable.getColTeamName() + " = '" + teamName + "') " +
                     "AND " + LeagueTable.getTableName() + "._id = " + leagueId)) {
            if (teamQuery.next()) {
                return teamQuery.getInt(1);
            } else {
                return -9999;
            }
        } catch (SQLException e) {
            System.out.println(e);
            return -9999;
        }
    }

    static HashMap<String, Integer> getTeamIds(HashMap<String, Team> teams, int leagueId) {
        HashMap<String, Integer> ids = new HashMap<>();
        teams.keySet().forEach(key -> {
            //getting team id
            int id = getTeamId(key, leagueId);
            if (id < 0) {
                //then we could not find a team of that name in db
                id = DS_Insert.writeTeamToDb(teams.get(key));
            }
            ids.put(key, id);
        });
        return ids;
    }

    public static String getMostRecentMatchInLeague(League league) {
        try (Statement statement = DS_Main.connection.createStatement()) {
            ResultSet dateStringRS = statement.executeQuery("SELECT " + MatchTable.getTableName() + "." + MatchTable.getColDate() + " FROM " + MatchTable.getTableName() +
                    " INNER JOIN " + TeamTable.getTableName() + " ON " + MatchTable.getTableName() + "." + MatchTable.getColHometeamId() + " = " + TeamTable.getTableName() + "._id" +
                    " INNER JOIN " + LeagueTable.getTableName() + " ON " + TeamTable.getTableName() + "." + TeamTable.getColLeagueId() + " = " + LeagueTable.getTableName() + "._id" +
                    " WHERE " + LeagueTable.getTableName() + "." + LeagueTable.getColName() + " = '" + league.getName() + "'" +
                    " AND " + MatchTable.getTableName() + "." + MatchTable.getColHomeScore() + " > " + "-1" +
                    " ORDER BY " + MatchTable.getTableName() + "." + MatchTable.getColDate() + " DESC" +
                    " LIMIT " + "1");

            String dateString = dateStringRS.getString(1);
            return dateString;
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            e.printStackTrace();
            return null;
        }
    }


    /*
     * Used to decide where we need to start scraping in new games from.
     */
    public static String getMostRecentMatchInDb(LeagueSeasonIds leagueSeasonIds) {
        try (Statement statement = DS_Main.connection.createStatement()) {
//            SELECT match.date FROM match
//            INNER JOIN team ON match.hometeam_id = team._id
//            INNER JOIN season ON team.season_id = season._id
//            INNER JOIN league ON season.league_id = league._id
//            WHERE league.name = 'EPL'
//              AND match.home_score > -1
//            ORDER BY date DESC
//            LIMIT 1;

            ResultSet dateStringRS = statement.executeQuery("SELECT " + MatchTable.getTableName() + "." + MatchTable.getColDate() + " FROM " + MatchTable.getTableName() +
                    " INNER JOIN " + TeamTable.getTableName() + " ON " + MatchTable.getTableName() + "." + MatchTable.getColHometeamId() + " = " + TeamTable.getTableName() + "._id" +
                    " INNER JOIN " + SeasonTable.getTableName() + " ON " + TeamTable.getTableName() + "." + TeamTable.getColSeasonId() + " = " + SeasonTable.getTableName() + "._id" +
                    " INNER JOIN " + LeagueTable.getTableName() + " ON " + SeasonTable.getTableName() + "." + SeasonTable.getColLeagueId() + " = " + LeagueTable.getTableName() + "._id" +
                    " WHERE " + LeagueTable.getTableName() + "." + LeagueTable.getColName() + " = '" + leagueSeasonIds.name() + "'" +
                    " AND " + MatchTable.getTableName() + "." + MatchTable.getColHomeScore() + " > " + "-1" +
                    " ORDER BY " + MatchTable.getTableName() + "." + MatchTable.getColDate() + " DESC" +
                    " LIMIT " + "1");

            String dateString = dateStringRS.getString(1);


            return dateString;

        } catch (SQLException e) {
            System.out.println(e.getMessage());
            e.printStackTrace();
            return null;
        }
    }

}
