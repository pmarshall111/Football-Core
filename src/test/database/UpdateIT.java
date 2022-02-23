package database;

import com.footballbettingcore.utils.DateHelper;
import com.footballbettingcore.database.FirstScorer;
import com.footballbettingcore.database.datasource.DS_Main;
import com.footballbettingcore.database.datasource.DS_Update;
import com.footballbettingcore.database.datasource.dbTables.MatchTable;
import com.footballbettingcore.database.datasource.dbTables.PlayerRatingTable;
import com.footballbettingcore.scrape.classes.League;
import com.footballbettingcore.scrape.classes.Match;
import com.footballbettingcore.scrape.classes.Season;
import org.junit.jupiter.api.*;
import org.testcontainers.containers.MariaDBContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.utility.DockerImageName;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.*;

import static com.footballbettingcore.database.datasource.DS_Main.connection;
import static database.GenerateData.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

public class UpdateIT {
    @Container
    public static final MariaDBContainer mariaDb = new MariaDBContainer<>(DockerImageName.parse("mariadb:10.5.5"))
            .withReuse(true);

    @BeforeAll
    public static void setup() {
        mariaDb.start();
        DS_Main.openTestConnection(
                mariaDb.getJdbcUrl(),
                mariaDb.getUsername(),
                mariaDb.getPassword()
        );
    }

    @BeforeEach
    public void addData() {
        DS_Main.initDB();
    }

    @AfterEach
    public void removeDbTables() {
        DbTestHelper.dropDatabaseTables();
    }

    @AfterAll
    public static void tearDown() {
        mariaDb.stop();
    }

    @Test
    public void updatesStatsCorrectlyWithLineups() {
        GenerateData data = addBulkData(false);
        League league = data.getLeagues().get(0);
        Season season = getSeasonWithGames(league);
        //match with scores will have all details updated
        Match matchToHaveScores = season.getAllMatches().get(0);
        String playerInMatchWithScores = matchToHaveScores.getHomePlayerRatings().values().iterator().next().getName();
        //match without scores will have just date and sofascoreId updated
        Match matchNoScores = season.getAllMatches().get(1);
        String playerInMatchNoScores = matchNoScores.getHomePlayerRatings().values().iterator().next().getName();
        try (Statement s = connection.createStatement()) {
            //ensuring there is no data already in db
            ResultSet rs = s.executeQuery("SELECT COUNT(*)" +
                    " FROM " + MatchTable.getTableName() +
                    " WHERE " + MatchTable.getColHomeScore() + " != -1");
            while (rs.next()) {
                int count = rs.getInt(1);
                assertEquals(0, count);
            }
            //adding data to match
            Date newKickoff = DateHelper.addXDaysToDate(new Date(), 1);
            int newSofascoreId = 1914;
            matchToHaveScores.setKickoffTime(newKickoff);
            matchToHaveScores.setSofaScoreGameId(newSofascoreId);
            matchToHaveScores.setHomeScore(HOMESCORE);
            matchToHaveScores.setAwayScore(AWAYSCORE);
            matchToHaveScores.setHomeXGF(HOMEXG);
            matchToHaveScores.setAwayXGF(AWAYXG);
            matchToHaveScores.setHomeDrawAwayOdds(new ArrayList<>(List.of(HOMEODDS, DRAWODDS, AWAYODDS)));
            matchToHaveScores.setFirstScorer(FirstScorer.getFirstScoreFromSql(FIRSTSCORER));

            Date newKickoffNoScores = DateHelper.addXDaysToDate(new Date(), 3);
            int newSofascoreIdNoScores = 1918;
            matchNoScores.setKickoffTime(newKickoffNoScores);
            matchNoScores.setSofaScoreGameId(newSofascoreIdNoScores);
            DS_Update.updateGamesInDB(league, season, DateHelper.subtractXDaysFromDate(new Date(), 100)); //will only update games within 100 days of today
            //checking with scores match after update
            ResultSet rsAfterUpdate = s.executeQuery("SELECT " + MatchTable.getColHomeScore() + ", " + MatchTable.getColAwayScore() + ", " +
                    MatchTable.getColHomeXg() + ", " + MatchTable.getColAwayXg() + ", " + MatchTable.getColDate() + ", " +
                    MatchTable.getColHomeWinOdds() + ", " + MatchTable.getColDrawOdds() + ", " + MatchTable.getColAwayWinOdds() + ", " +
                    MatchTable.getColFirstScorer() + ", " + MatchTable.getColSofascoreId() +
                    " FROM " + PlayerRatingTable.getTableName() +
                    " INNER JOIN " + MatchTable.getTableName() + " ON " + PlayerRatingTable.getColMatchId() + " = " + MatchTable.getTableName() + "._id" +
                    " WHERE " + PlayerRatingTable.getTableName() + "." + PlayerRatingTable.getColPlayerName() + " = '" + playerInMatchWithScores + "'");
            while (rsAfterUpdate.next()) {
                int homeScore = rsAfterUpdate.getInt(1);
                int awayScore = rsAfterUpdate.getInt(2);
                double homeXg = rsAfterUpdate.getDouble(3);
                double awayXg = rsAfterUpdate.getDouble(4);
                String date = rsAfterUpdate.getString(5);
                double homeOdds = rsAfterUpdate.getDouble(6);
                double drawOdds = rsAfterUpdate.getDouble(7);
                double awayOdds = rsAfterUpdate.getDouble(8);
                int firstScorer = rsAfterUpdate.getInt(9);
                int sofascoreId = rsAfterUpdate.getInt(10);
                assertEquals(HOMESCORE, homeScore);
                assertEquals(AWAYSCORE, awayScore);
                assertEquals(HOMEXG, homeXg, 0.0001);
                assertEquals(AWAYXG, awayXg, 0.0001);
                assertEquals(DateHelper.getSqlDate(newKickoff), date);
                assertEquals(HOMEODDS, homeOdds, 0.0001);
                assertEquals(DRAWODDS, drawOdds, 0.0001);
                assertEquals(AWAYODDS, awayOdds, 0.0001);
                assertEquals(FIRSTSCORER, firstScorer);
                assertEquals(newSofascoreId, sofascoreId);
            }

            //checking without scores match after update. should update date & sofascore id
            rsAfterUpdate = s.executeQuery("SELECT " + MatchTable.getColHomeScore() + ", " + MatchTable.getColAwayScore() + ", " +
                    MatchTable.getColHomeXg() + ", " + MatchTable.getColAwayXg() + ", " + MatchTable.getColDate() + ", " +
                    MatchTable.getColHomeWinOdds() + ", " + MatchTable.getColDrawOdds() + ", " + MatchTable.getColAwayWinOdds() + ", " +
                    MatchTable.getColFirstScorer() + ", " + MatchTable.getColSofascoreId() +
                    " FROM " + PlayerRatingTable.getTableName() +
                    " INNER JOIN " + MatchTable.getTableName() + " ON " + PlayerRatingTable.getColMatchId() + " = " + MatchTable.getTableName() + "._id" +
                    " WHERE " + PlayerRatingTable.getTableName() + "." + PlayerRatingTable.getColPlayerName() + " = '" + playerInMatchNoScores + "'");
            while (rsAfterUpdate.next()) {
                int homeScore = rsAfterUpdate.getInt(1);
                int awayScore = rsAfterUpdate.getInt(2);
                double homeXg = rsAfterUpdate.getDouble(3);
                double awayXg = rsAfterUpdate.getDouble(4);
                String date = rsAfterUpdate.getString(5);
                double homeOdds = rsAfterUpdate.getDouble(6);
                double drawOdds = rsAfterUpdate.getDouble(7);
                double awayOdds = rsAfterUpdate.getDouble(8);
                int firstScorer = rsAfterUpdate.getInt(9);
                int sofascoreId = rsAfterUpdate.getInt(10);
                assertEquals(-1, homeScore);
                assertEquals(-1, awayScore);
                assertEquals(-1, homeXg, 0.0001);
                assertEquals(-1, awayXg, 0.0001);
                assertEquals(DateHelper.getSqlDate(newKickoffNoScores), date);
                assertEquals(-1, homeOdds, 0.0001);
                assertEquals(-1, drawOdds, 0.0001);
                assertEquals(-1, awayOdds, 0.0001);
                assertEquals(-1, firstScorer);
                assertEquals(newSofascoreIdNoScores, sofascoreId);
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
