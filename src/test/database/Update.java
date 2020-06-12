package database;

import com.petermarshall.DateHelper;
import com.petermarshall.database.datasource.DS_Get;
import com.petermarshall.database.datasource.DS_Main;
import com.petermarshall.database.datasource.DS_Update;
import com.petermarshall.database.tables.*;
import com.petermarshall.scrape.classes.League;
import com.petermarshall.scrape.classes.Match;
import com.petermarshall.scrape.classes.Season;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Date;

import static com.petermarshall.database.datasource.DS_Main.connection;
import static database.GenerateData.*;
import static org.junit.Assert.fail;

public class Update {
    @Before
    public void setup() {
        DbTestHelper.setupNewTestDb();
    }

    @After
    public void tearDown() {
        DS_Main.closeConnection();
    }

    @Test
    public void addsStatsCorrectly() {
        GenerateData data = addBulkData(false);
        League league = data.getLeagues().get(0);
        Season season = getSeasonWithGames(league);
        Match match = season.getAllMatches().get(0);
        String playerInMatch = match.getHomePlayerRatings().values().iterator().next().getName();
        try (Statement s = connection.createStatement()) {
            ResultSet rs = s.executeQuery("SELECT " + MatchTable.getColHomeScore() + ", " + MatchTable.getColAwayScore() + ", " + MatchTable.getColHomeXg() + ", " + MatchTable.getColAwayXg() + ", " +
                    MatchTable.getColHomeWinOdds() + ", " + MatchTable.getColDrawOdds() + ", " + MatchTable.getColAwayWinOdds() + ", " + MatchTable.getColFirstScorer() +
                    " FROM " + PlayerRatingTable.getTableName() +
                    " INNER JOIN " + MatchTable.getTableName() + " ON " + PlayerRatingTable.getColMatchId() + " = " + MatchTable.getTableName() + "._id" +
                    " WHERE " + PlayerRatingTable.getTableName() + "." + PlayerRatingTable.getColPlayerName() + " = '" + playerInMatch + "'");
            while (rs.next()) {
                //ensuring there is no data already
                int homeScore = rs.getInt(1);
                int awayScore = rs.getInt(2);
                double homeXg = rs.getDouble(3);
                double awayXg = rs.getDouble(4);
                double homeOdds = rs.getDouble(5);
                double drawOdds = rs.getDouble(6);
                double awayOdds = rs.getDouble(7);
                int firstScorer = rs.getInt(8);
                Assert.assertEquals(-1, homeScore);
                Assert.assertEquals(-1, awayScore);
                Assert.assertEquals(-1, homeXg, 0.0001);
                Assert.assertEquals(-1, awayXg, 0.0001);
                Assert.assertEquals(-1, homeOdds, 0.0001);
                Assert.assertEquals(-1, drawOdds, 0.0001);
                Assert.assertEquals(-1, awayOdds, 0.0001);
                Assert.assertEquals(-1, firstScorer);
            }
            match.setHomeScore(HOMESCORE);
            match.setAwayScore(AWAYSCORE);
            match.setHomeXGF(HOMEXG);
            match.setAwayXGF(AWAYXG);
            ArrayList<Double> homeDrawAwayOdds = new ArrayList<>();
            homeDrawAwayOdds.add(HOMEODDS);
            homeDrawAwayOdds.add(DRAWODDS);
            homeDrawAwayOdds.add(AWAYODDS);
            match.setHomeDrawAwayOdds(homeDrawAwayOdds);
            match.setFirstScorer(FIRSTSCORER);
            DS_Update.updateGamesInDB(league, season);
            //checking after update
            ResultSet rsAfterUpdate = s.executeQuery("SELECT " + MatchTable.getColHomeScore() + ", " + MatchTable.getColAwayScore() + ", " + MatchTable.getColHomeXg() + ", " + MatchTable.getColAwayXg() + ", " +
                    MatchTable.getColHomeWinOdds() + ", " + MatchTable.getColDrawOdds() + ", " + MatchTable.getColAwayWinOdds() + ", " + MatchTable.getColFirstScorer() +
                    " FROM " + PlayerRatingTable.getTableName() +
                    " INNER JOIN " + MatchTable.getTableName() + " ON " + PlayerRatingTable.getColMatchId() + " = " + MatchTable.getTableName() + "._id" +
                    " WHERE " + PlayerRatingTable.getTableName() + "." + PlayerRatingTable.getColPlayerName() + " = '" + playerInMatch + "'");
            while (rsAfterUpdate.next()) {
                int homeScore = rsAfterUpdate.getInt(1);
                int awayScore = rsAfterUpdate.getInt(2);
                double homeXg = rsAfterUpdate.getDouble(3);
                double awayXg = rsAfterUpdate.getDouble(4);
                double homeOdds = rsAfterUpdate.getDouble(5);
                double drawOdds = rsAfterUpdate.getDouble(6);
                double awayOdds = rsAfterUpdate.getDouble(7);
                int firstScorer = rsAfterUpdate.getInt(8);
                Assert.assertEquals(HOMESCORE, homeScore);
                Assert.assertEquals(AWAYSCORE, awayScore);
                Assert.assertEquals(HOMEXG, homeXg, 0.0001);
                Assert.assertEquals(AWAYXG, awayXg, 0.0001);
                Assert.assertEquals(HOMEODDS, homeOdds, 0.0001);
                Assert.assertEquals(DRAWODDS, drawOdds, 0.0001);
                Assert.assertEquals(AWAYODDS, awayOdds, 0.0001);
                Assert.assertEquals(FIRSTSCORER, firstScorer);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            fail();
        }
    }

    @Test
    public void canUpdateTheKickoffTime() {
        GenerateData data = addBulkData(false);
        League league = data.getLeagues().get(0);
        Season season = getSeasonWithGames(league);
        int seasonYearStart = season.getSeasonYearStart();
        Match match = season.getAllMatches().get(2);
        String homeTeam = match.getHomeTeam().getTeamName();
        String awayTeam = match.getAwayTeam().getTeamName();
        String playerInMatch = match.getHomePlayerRatings().values().iterator().next().getName();
        try (Statement s = connection.createStatement()) {
            int leagueId = DS_Get.getLeagueId(league);
            String newStartDate = DateHelper.getSqlDate(new Date(0));
            DS_Update.updateKickoffTime(seasonYearStart, homeTeam, awayTeam, newStartDate, leagueId);
            ResultSet rsAfterUpdate = s.executeQuery("SELECT " + MatchTable.getColDate() + " FROM " + PlayerRatingTable.getTableName() +
                    " INNER JOIN " + MatchTable.getTableName() + " ON " + PlayerRatingTable.getColMatchId() + " = " + MatchTable.getTableName() + "._id" +
                    " WHERE " + PlayerRatingTable.getTableName() + "." + PlayerRatingTable.getColPlayerName() + " = '" + playerInMatch + "'");
            while (rsAfterUpdate.next()) {
                String dateFromDb = rsAfterUpdate.getString(1);
                Assert.assertEquals(newStartDate, dateFromDb);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            fail();
        }
    }

    private Season getSeasonWithGames(League league) {
        ArrayList<Season> seasons = league.getAllSeasons();
        for (Season s: seasons) {
            if (s.hasMatches()) {
                return s;
            }
        }
        return null;
    }
}
