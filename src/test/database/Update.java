package database;

import com.footballbettingcore.utils.DateHelper;
import com.footballbettingcore.database.FirstScorer;
import com.footballbettingcore.database.datasource.DS_Get;
import com.footballbettingcore.database.datasource.DS_Insert;
import com.footballbettingcore.database.datasource.DS_Main;
import com.footballbettingcore.database.datasource.DS_Update;
import com.footballbettingcore.database.datasource.dbTables.MatchTable;
import com.footballbettingcore.database.datasource.dbTables.PlayerRatingTable;
import com.footballbettingcore.database.datasource.dbTables.PredictionTable;
import com.footballbettingcore.database.datasource.dbTables.TeamTable;
import com.footballbettingcore.machineLearning.createData.classes.MatchToPredict;
import com.footballbettingcore.scrape.classes.League;
import com.footballbettingcore.scrape.classes.Match;
import com.footballbettingcore.scrape.classes.OddsCheckerBookies;
import com.footballbettingcore.scrape.classes.Season;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.LinkedHashMap;

import static com.footballbettingcore.database.datasource.DS_Main.connection;
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
            ResultSet rs = s.executeQuery("SELECT " + MatchTable.getColHomeScore() + ", " + MatchTable.getColAwayScore() + ", " + MatchTable.getColHomeXg() + ", " + MatchTable.getColAwayXg() + ", " +
                    MatchTable.getColHomeWinOdds() + ", " + MatchTable.getColDrawOdds() + ", " + MatchTable.getColAwayWinOdds() + ", " +
                    MatchTable.getColFirstScorer() + ", " + MatchTable.getColSofascoreId() +
                    " FROM " + PlayerRatingTable.getTableName() +
                    " INNER JOIN " + MatchTable.getTableName() + " ON " + PlayerRatingTable.getColMatchId() + " = " + MatchTable.getTableName() + "._id" +
                    " WHERE " + PlayerRatingTable.getTableName() + "." + PlayerRatingTable.getColPlayerName() + " = '" + playerInMatchWithScores + "'" +
                    " AND " + PlayerRatingTable.getTableName() + "." + PlayerRatingTable.getColPlayerName() + " = '" + playerInMatchNoScores + "'");
            while (rs.next()) {
                int homeScore = rs.getInt(1);
                int awayScore = rs.getInt(2);
                double homeXg = rs.getDouble(3);
                double awayXg = rs.getDouble(4);
                double homeOdds = rs.getDouble(5);
                double drawOdds = rs.getDouble(6);
                double awayOdds = rs.getDouble(7);
                int firstScorer = rs.getInt(8);
                int sofascoreId = rs.getInt(9);
                Assert.assertEquals(-1, homeScore);
                Assert.assertEquals(-1, awayScore);
                Assert.assertEquals(-1, homeXg, 0.0001);
                Assert.assertEquals(-1, awayXg, 0.0001);
                Assert.assertEquals(-1, homeOdds, 0.0001);
                Assert.assertEquals(-1, drawOdds, 0.0001);
                Assert.assertEquals(-1, awayOdds, 0.0001);
                Assert.assertEquals(-1, firstScorer);
                Assert.assertEquals(-1, sofascoreId);
            }
            //adding data to matches
            Date newKickoff = DateHelper.addXDaysToDate(new Date(), 1);
            int newSofascoreId = 1914;
            matchToHaveScores.setKickoffTime(newKickoff);
            matchToHaveScores.setSofaScoreGameId(newSofascoreId);
            matchToHaveScores.setHomeScore(HOMESCORE);
            matchToHaveScores.setAwayScore(AWAYSCORE);
            matchToHaveScores.setHomeXGF(HOMEXG);
            matchToHaveScores.setAwayXGF(AWAYXG);
            ArrayList<Double> homeDrawAwayOdds = new ArrayList<>();
            homeDrawAwayOdds.add(HOMEODDS);
            homeDrawAwayOdds.add(DRAWODDS);
            homeDrawAwayOdds.add(AWAYODDS);
            matchToHaveScores.setHomeDrawAwayOdds(homeDrawAwayOdds);
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
                Assert.assertEquals(HOMESCORE, homeScore);
                Assert.assertEquals(AWAYSCORE, awayScore);
                Assert.assertEquals(HOMEXG, homeXg, 0.0001);
                Assert.assertEquals(AWAYXG, awayXg, 0.0001);
                Assert.assertEquals(DateHelper.getSqlDate(newKickoff), date);
                Assert.assertEquals(HOMEODDS, homeOdds, 0.0001);
                Assert.assertEquals(DRAWODDS, drawOdds, 0.0001);
                Assert.assertEquals(AWAYODDS, awayOdds, 0.0001);
                Assert.assertEquals(FIRSTSCORER, firstScorer);
                Assert.assertEquals(newSofascoreId, sofascoreId);
            }
            //checking without scores match after update
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
                Assert.assertEquals(-1, homeScore);
                Assert.assertEquals(-1, awayScore);
                Assert.assertEquals(-1, homeXg, 0.0001);
                Assert.assertEquals(-1, awayXg, 0.0001);
                Assert.assertEquals(DateHelper.getSqlDate(newKickoffNoScores), date);
                Assert.assertEquals(-1, homeOdds, 0.0001);
                Assert.assertEquals(-1, drawOdds, 0.0001);
                Assert.assertEquals(-1, awayOdds, 0.0001);
                Assert.assertEquals(-1, firstScorer);
                Assert.assertEquals(newSofascoreIdNoScores, sofascoreId);
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

    @Test
    public void canUpdatePredictionToIncludeOdds() {
        GenerateData data = addBulkData(false);
        League league = data.getLeagues().get(0);
        Season season = getSeasonWithGames(league);
        Match startsWithOdds = season.getAllMatches().get(0);
        Match noOdds = season.getAllMatches().get(1);
        Match neverHasOdds = season.getAllMatches().get(2);

        //need the db ids
        try (Statement s = connection.createStatement()) {
            String home = "home", away = "away";
            ResultSet rs = s.executeQuery("SELECT " + home + "." + TeamTable.getColTeamName() + ", " +
                        away + "." + TeamTable.getColTeamName() +  ", " + MatchTable.getTableName() + "._id FROM " + MatchTable.getTableName() +
                    " INNER JOIN " + TeamTable.getTableName() + " AS " + home + " ON " + MatchTable.getColHometeamId() + " = " + home + "._id" +
                    " INNER JOIN " + TeamTable.getTableName() + " AS " + away + " ON " + MatchTable.getColAwayteamId() + " = " + away + "._id" +
                    " WHERE (" + home + "." + TeamTable.getColTeamName() + " = '" + startsWithOdds.getHomeTeam().getTeamName() + "'" +
                    " AND " + away + "." + TeamTable.getColTeamName() + " = '" + startsWithOdds.getAwayTeam().getTeamName() + "')" +
                    " OR (" + home + "." + TeamTable.getColTeamName() + " = '" + noOdds.getHomeTeam().getTeamName() + "'" +
                    " AND " + away + "." + TeamTable.getColTeamName() + " = '" + noOdds.getAwayTeam().getTeamName() + "')" +
                    " OR (" + home + "." + TeamTable.getColTeamName() + " = '" + neverHasOdds.getHomeTeam().getTeamName() + "'" +
                    " AND " + away + "." + TeamTable.getColTeamName() + " = '" + neverHasOdds.getAwayTeam().getTeamName() + "')");

            int dbIdOdds = -1, dbIdNoOdds = -1, dbIdNeverHaveOdds = -1;
            while (rs.next()) {
                String homeTeam = rs.getString(1);
                String awayTeam = rs.getString(2);
                if (homeTeam.equals(startsWithOdds.getHomeTeam().getTeamName()) && awayTeam.equals(startsWithOdds.getAwayTeam().getTeamName())) {
                    dbIdOdds = rs.getInt(3);
                } else if (homeTeam.equals(noOdds.getHomeTeam().getTeamName()) && awayTeam.equals(noOdds.getAwayTeam().getTeamName())) {
                    dbIdNoOdds = rs.getInt(3);
                } else {
                    dbIdNeverHaveOdds = rs.getInt(3);
                }
            }

            MatchToPredict withOdds = new MatchToPredict(startsWithOdds.getHomeTeam().getTeamName(), startsWithOdds.getAwayTeam().getTeamName(),
                    Season.getSeasonKeyFromYearStart(19), "EPL", DateHelper.getSqlDate(new Date()), dbIdOdds, 2);
            MatchToPredict noOddsYet = new MatchToPredict(noOdds.getHomeTeam().getTeamName(), noOdds.getAwayTeam().getTeamName(),
                    Season.getSeasonKeyFromYearStart(19), "EPL", DateHelper.getSqlDate(new Date()), dbIdNoOdds, 2);
            MatchToPredict neverHaveOdds = new MatchToPredict(neverHasOdds.getHomeTeam().getTeamName(), neverHasOdds.getAwayTeam().getTeamName(),
                    Season.getSeasonKeyFromYearStart(19), "EPL", DateHelper.getSqlDate(new Date()), dbIdNeverHaveOdds, 2);
            LinkedHashMap<String, double[]> bookiesOdds = new LinkedHashMap<>();
            double[] oddsAlreadyInDb = new double[]{1,3,5};
            bookiesOdds.put(OddsCheckerBookies.BET365.getName(), oddsAlreadyInDb);
            withOdds.setBookiesOdds(bookiesOdds);
            double[] predictions = new double[]{0.2,0.3,0.5};
            withOdds.setOurPredictions(predictions, false);
            noOddsYet.setOurPredictions(predictions, false);
            neverHaveOdds.setOurPredictions(predictions, false);

            ArrayList<MatchToPredict> matches = new ArrayList<>(Arrays.asList(withOdds, noOddsYet, neverHaveOdds));
            DS_Insert.addPredictionsToDb(matches);
            //ensuring adding as expected.
            rs = s.executeQuery("SELECT COUNT(*) FROM " + PredictionTable.getTableName());
            while (rs.next()) {
                Assert.assertEquals(matches.size(), rs.getInt(1));
            }
            rs = s.executeQuery("SELECT " + PredictionTable.getColMatchId() + ", " + PredictionTable.getColHOdds() + ", " +
                    PredictionTable.getColDOdds() + ", " + PredictionTable.getColAOdds() + " FROM " + PredictionTable.getTableName());
            while (rs.next()) {
                int matchId = rs.getInt(1);
                double hOdds = rs.getDouble(2);
                double dOdds = rs.getDouble(3);
                double aOdds = rs.getDouble(4);
                if (matchId == withOdds.getDatabase_id()) {
                    Assert.assertEquals(oddsAlreadyInDb[0], hOdds, 0.0001);
                    Assert.assertEquals(oddsAlreadyInDb[1], dOdds, 0.0001);
                    Assert.assertEquals(oddsAlreadyInDb[2], aOdds, 0.0001);
                } else {
                    Assert.assertEquals(-1, hOdds, 0.0001);
                    Assert.assertEquals(-1, hOdds, 0.0001);
                    Assert.assertEquals(-1, hOdds, 0.0001);
                }
            }

            //now try to update
            double[] newOdds = new double[]{6.2,9.1,2.2};
            LinkedHashMap<String, double[]> newBookiesOdds = new LinkedHashMap<>();
            newBookiesOdds.put(OddsCheckerBookies.BET365.getName(), newOdds);
            noOddsYet.setBookiesOdds(newBookiesOdds);

            DS_Update.updatePredictionToIncludeOdds(matches);

            //check final db status.
            rs = s.executeQuery("SELECT COUNT(*) FROM " + PredictionTable.getTableName());
            while (rs.next()) {
                Assert.assertEquals(matches.size(), rs.getInt(1));
            }
            rs = s.executeQuery("SELECT " + PredictionTable.getColMatchId() + ", " + PredictionTable.getColHOdds() + ", " +
                    PredictionTable.getColDOdds() + ", " + PredictionTable.getColAOdds() + " FROM " + PredictionTable.getTableName());
            while (rs.next()) {
                int matchId = rs.getInt(1);
                double hOdds = rs.getDouble(2);
                double dOdds = rs.getDouble(3);
                double aOdds = rs.getDouble(4);
                if (matchId == withOdds.getDatabase_id()) {
                    Assert.assertEquals(oddsAlreadyInDb[0], hOdds, 0.0001);
                    Assert.assertEquals(oddsAlreadyInDb[1], dOdds, 0.0001);
                    Assert.assertEquals(oddsAlreadyInDb[2], aOdds, 0.0001);
                } else if (matchId == noOddsYet.getDatabase_id()) {
                    Assert.assertEquals(newOdds[0], hOdds, 0.0001);
                    Assert.assertEquals(newOdds[1], dOdds, 0.0001);
                    Assert.assertEquals(newOdds[2], aOdds, 0.0001);
                } else {
                    Assert.assertEquals(-1, hOdds, 0.0001);
                    Assert.assertEquals(-1, hOdds, 0.0001);
                    Assert.assertEquals(-1, hOdds, 0.0001);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            fail();
        }
    }
}
