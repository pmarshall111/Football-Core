package database;

import com.petermarshall.DateHelper;
import com.petermarshall.database.Result;
import com.petermarshall.database.datasource.DS_Insert;
import com.petermarshall.database.datasource.DS_Main;
import com.petermarshall.database.BetLog;
import com.petermarshall.database.datasource.dbTables.*;
import com.petermarshall.machineLearning.createData.classes.MatchToPredict;
import com.petermarshall.scrape.classes.*;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.*;

import static com.petermarshall.database.datasource.DS_Main.connection;
import static database.GenerateData.*;
import static org.junit.Assert.fail;

public class Write {
    @Before
    public void setup() {
        DbTestHelper.setupNewTestDb();
    }

    @Test
    public void canAddRecordsWithIncreasingIds() {
        //did not use autoincrement as this hurts performance.
        GenerateData data = addBulkData(true);
        try (Statement s = connection.createStatement()) {
            ResultSet rs = s.executeQuery("SELECT max(_id) FROM " + MatchTable.getTableName());
            Integer prevId = null;
            while (rs.next()) {
                prevId = rs.getInt(1);
            }
            League league = data.getLeagues().get(0);
            Season untouchedSeason = league.getSeason(16);
            Team team1 = untouchedSeason.addNewTeam(new Team("increasingId_team1"));
            Team team2 = untouchedSeason.addNewTeam(new Team("increasingId_team2"));
            untouchedSeason.addNewMatch(new Match(team1, team2, new Date(), 3, 1));
            DS_Insert.writeLeagueToDb(league);
            ResultSet rsAfterInsert = s.executeQuery("SELECT max(_id) FROM " + MatchTable.getTableName());
            while (rsAfterInsert.next()) {
                int id = rsAfterInsert.getInt(1);
                Assert.assertNotNull(prevId);
                Assert.assertEquals(prevId+1, id);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            fail();
        }
    }

    @Test
    public void insertsCorrectNumbOfMatches() {
        GenerateData data = addBulkData(true);
        addBulkData(true);
        try (Statement s = connection.createStatement()) {
            ResultSet rs = s.executeQuery("SELECT COUNT(*) FROM " + MatchTable.getTableName());
            while (rs.next()) {
                int count = rs.getInt(1);
                Assert.assertEquals(data.getMatches().size(), count);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            fail();
        }
    }

    @Test
    public void insertsAPlayerWithCorrectTeam() {
        GenerateData data = addBulkData(true);
        Match m = data.getMatches().get(10);
        String homeTeamName = m.getHomeTeam().getTeamName();
        String awayTeamName = m.getAwayTeam().getTeamName();
        String firstHomePlayer = m.getHomePlayerRatings().values().iterator().next().getName();
        String firstAwayPlayer = m.getAwayPlayerRatings().values().iterator().next().getName();
        try (Statement s = connection.createStatement()) {
            //home
            System.out.println("SELECT COUNT(*) FROM " + PlayerRatingTable.getTableName() +
                    " INNER JOIN " + TeamTable.getTableName() + " ON " + PlayerRatingTable.getColTeamId() + " = " + TeamTable.getTableName() + "._id" +
                    " WHERE " + PlayerRatingTable.getTableName() + "." + PlayerRatingTable.getColPlayerName() + " = '" + firstHomePlayer + "'" +
                    " AND " + TeamTable.getTableName() + "." + TeamTable.getColTeamName() + " = '" + homeTeamName + "'");
            ResultSet rs = s.executeQuery(
                    "SELECT COUNT(*) FROM " + PlayerRatingTable.getTableName() +
                        " INNER JOIN " + TeamTable.getTableName() + " ON " + PlayerRatingTable.getColTeamId() + " = " + TeamTable.getTableName() + "._id" +
                        " WHERE " + PlayerRatingTable.getTableName() + "." + PlayerRatingTable.getColPlayerName() + " = '" + firstHomePlayer + "'" +
                        " AND " + TeamTable.getTableName() + "." + TeamTable.getColTeamName() + " = '" + homeTeamName + "'");
            while (rs.next()) {
                int count = rs.getInt(1);
                Assert.assertEquals(1, count);
            }
            //away
            rs = s.executeQuery(
                    "SELECT COUNT(*) FROM " + PlayerRatingTable.getTableName() +
                            " INNER JOIN " + TeamTable.getTableName() + " ON " + PlayerRatingTable.getColTeamId() + " = " + TeamTable.getTableName() + "._id" +
                            " WHERE " + PlayerRatingTable.getTableName() + "." + PlayerRatingTable.getColPlayerName() + " = '" + firstAwayPlayer + "'" +
                            " AND " + TeamTable.getTableName() + "." + TeamTable.getColTeamName() + " = '" + awayTeamName + "'");
            while (rs.next()) {
                int count = rs.getInt(1);
                Assert.assertEquals(1, count);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            fail();
        }
    }

    @Test
    public void addsTheCorrectDataToMatch() {
        GenerateData data = addBulkData(true);
        Match m = data.getMatches().get(10);
        String playerInMatch = m.getHomePlayerRatings().values().iterator().next().getName();
        try (Statement s = connection.createStatement()) {
            ResultSet rs = s.executeQuery(
                    "SELECT " + MatchTable.getColHomeScore() + ", " + MatchTable.getColAwayScore() + ", " + MatchTable.getColHomeXg() + ", " + MatchTable.getColAwayXg() + ", " +
                            MatchTable.getColHomeWinOdds() + ", " + MatchTable.getColDrawOdds() + ", " + MatchTable.getColAwayWinOdds() + ", " + MatchTable.getColFirstScorer() +
                            " FROM " + PlayerRatingTable.getTableName() +
                            " INNER JOIN " + MatchTable.getTableName() + " ON " + PlayerRatingTable.getColMatchId() + " = " + MatchTable.getTableName() + "._id" +
                            " WHERE " + PlayerRatingTable.getTableName() + "." + PlayerRatingTable.getColPlayerName() + " = '" + playerInMatch + "'");
            while (rs.next()) {
                int homeScore = rs.getInt(1);
                int awayScore = rs.getInt(2);
                double homeXg = rs.getDouble(3);
                double awayXg = rs.getDouble(4);
                double homeOdds = rs.getDouble(5);
                double drawOdds = rs.getDouble(6);
                double awayOdds = rs.getDouble(7);
                int firstScorer = rs.getInt(8);
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
    public void addsTheCorrectDataToPlayer() {
        GenerateData data = addBulkData(true);
        PlayerRating pr = data.getPlayerRatings().get(100);
        String playerName = pr.getName();
        try (Statement s = connection.createStatement()) {
            ResultSet rs = s.executeQuery(
                    "SELECT " + PlayerRatingTable.getColMins() + ", " + PlayerRatingTable.getColRating() +
                            " FROM " + PlayerRatingTable.getTableName() +
                            " WHERE " + PlayerRatingTable.getTableName() + "." + PlayerRatingTable.getColPlayerName() + " = '" + playerName + "'");
            while (rs.next()) {
                int mins = rs.getInt(1);
                double rating = rs.getDouble(2);
                Assert.assertEquals(MINUTES, mins);
                Assert.assertEquals(RATING, rating, 0.0001);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            fail();
        }
    }

    @Test
    public void doesntAddRepeatedPlayer() {
        GenerateData data = addBulkData(true);
        PlayerRating pr = data.getPlayerRatings().get(100);
        String playerName = pr.getName();
        try (Statement s = connection.createStatement()) {
            //first get out matchid and teamid of player
            ResultSet rs = s.executeQuery(
                    "SELECT " + PlayerRatingTable.getColMatchId() + ", " + PlayerRatingTable.getColTeamId() +
                            " FROM " + PlayerRatingTable.getTableName() +
                            " WHERE " + PlayerRatingTable.getColPlayerName() + " = '" + playerName + "'");
            int matchId = -1, teamId = -1;
            while (rs.next()) {
                matchId = rs.getInt(1);
                teamId = rs.getInt(2);
            }
            HashMap<String, PlayerRating> pRatings = new HashMap<>();
            pRatings.put(playerName, pr);
            //try insert repeated player
            DS_Insert.addPlayerRatingsToBatch(s, pRatings, matchId, teamId);
            s.executeBatch();
            //check if in db
            rs = s.executeQuery("SELECT COUNT(*) FROM " + PlayerRatingTable.getTableName() +
                    " WHERE " + PlayerRatingTable.getColPlayerName() + " = '" + playerName + "'");
            while (rs.next()) {
                int count = rs.getInt(1);
                Assert.assertEquals(1, count);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            fail();
        }
    }

    @Test
    public void addsOtherPlayersEvenWithRepeatedPlayer() {
        GenerateData data = addBulkData(true);
        PlayerRating pr = data.getPlayerRatings().get(100);
        String playerName = pr.getName();
        try (Statement s = connection.createStatement();) {
            //first get out matchid and teamid of player
            ResultSet rs = s.executeQuery(
                    "SELECT " + PlayerRatingTable.getColMatchId() + ", " + PlayerRatingTable.getColTeamId() +
                            " FROM " + PlayerRatingTable.getTableName() +
                            " WHERE " + PlayerRatingTable.getTableName() + "." + PlayerRatingTable.getColPlayerName() + " = '" + playerName + "'");
            int matchId = -1, teamId = -1;
            while (rs.next()) {
                matchId = rs.getInt(1);
                teamId = rs.getInt(2);
            }
            String newPlayerName = "Peter Marshall";
            HashMap<String, PlayerRating> pRatings = new HashMap<>();
            pRatings.put(playerName, pr);
            pRatings.put(newPlayerName, new PlayerRating(90, 10, newPlayerName, "D"));
            //try insert repeated & extra player
            DS_Insert.addPlayerRatingsToBatch(s, pRatings, matchId, teamId);
            s.executeBatch();
            //check if in db
            ResultSet withNewPlayerRs = s.executeQuery("SELECT COUNT(*) FROM " + PlayerRatingTable.getTableName() +
                    " WHERE " + PlayerRatingTable.getTableName() + "." + PlayerRatingTable.getColPlayerName() + " = '" + newPlayerName + "'");
            while (withNewPlayerRs.next()) {
                int count = withNewPlayerRs.getInt(1);
                Assert.assertEquals(1, count);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            fail();
        }
    }

    @Test
    public void givesPromotedTeamsCorrectLeagueId() {
        GenerateData data = addBulkData(true);
        League l = data.getLeagues().get(0);
        Season s = l.getAllSeasons().stream().reduce((emptySeason, season) -> season.hasMatches() ? emptySeason : season).get();
        String t1Name = "new_team1";
        String t2Name = "new_team2";
        Team t1 = s.addNewTeam(new Team(t1Name));
        Team t2 = s.addNewTeam(new Team(t2Name));
        s.addNewMatch(new Match(t1, t2, new Date(), 100, 0));
        try (Statement stmt = connection.createStatement()) {
            //first get out leagueid of league we added match to
            ResultSet rs = stmt.executeQuery(
                    "SELECT _id FROM " + LeagueTable.getTableName() +
                        " WHERE " + LeagueTable.getColName() + " = '" + l.getName() + "'");
            int leagueId = -1;
            while (rs.next()) {
                leagueId = rs.getInt(1);
            }
            //now insert league to DB again
            DS_Insert.writeLeagueToDb(l);
            //get out the ids of the teams we just added
            ResultSet afterAddRs = stmt.executeQuery("SELECT " + TeamTable.getColLeagueId() +
                    " FROM " + TeamTable.getTableName() +
                    " WHERE " + TeamTable.getColTeamName() + " = '" + t1Name + "'" +
                    " OR " + TeamTable.getColTeamName() + " = '" + t2Name + "'");
            while (afterAddRs.next()) {
                int afterAddleagueId = afterAddRs.getInt(1);
                Assert.assertEquals(leagueId, afterAddleagueId);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            fail();
        }
    }

    @Test
    public void canLogBetPlaced() {
        GenerateData data = addBulkData(true);
        League l = data.getLeagues().get(0);
        Season s = l.getSeason(20);
        Match m = s.getAllMatches().get(0);

        try (Statement stmt = connection.createStatement()) {
            String home = "home", away = "away";
            ResultSet rs = stmt.executeQuery("SELECT " + MatchTable.getTableName() + "._id, " + MatchTable.getColDate() + " FROM " + MatchTable.getTableName() +
                                            " INNER JOIN " + TeamTable.getTableName() + " AS " + home + " ON " + MatchTable.getColHometeamId() + " = " + home + "._id" +
                                            " INNER JOIN " + TeamTable.getTableName() + " AS " + away + " ON " + MatchTable.getColAwayteamId() + " = " + away + "._id" +
                                            " WHERE " + home + "." + TeamTable.getColTeamName() + " = '" + m.getHomeTeam().getTeamName() + "'" +
                                            " AND " + away + "." + TeamTable.getColTeamName() + " = '" + m.getAwayTeam().getTeamName() + "'");
            int dbId = -1;
            String sqlMatchDate = "null";
            while (rs.next()) {
                dbId = rs.getInt(1);
                sqlMatchDate = rs.getString(2);
            }

            MatchToPredict mtp = new MatchToPredict(m.getHomeTeam().getTeamName(), m.getAwayTeam().getTeamName(), s.getSeasonKey(),
                    l.getName(), sqlMatchDate, dbId,m.getSofaScoreGameId());

            Result rbOn = Result.HOME_WIN;
            String bookieUsed = "BettingIsForFools";
            double odds = 1.22;
            int stake = 5;
            DS_Insert.logBetPlaced(new BetLog(mtp, rbOn, bookieUsed, odds, stake));

            ResultSet rsBet = stmt.executeQuery("SELECT COUNT(*) FROM " + BetTable.getTableName());
            while (rsBet.next()) {
                Assert.assertEquals(1, rsBet.getInt(1));
            }

            rsBet = stmt.executeQuery("SELECT " + BetTable.getColStake() + ", " + BetTable.getColOdds() + ", " +
                    BetTable.getColResultBetOn() + ", " + BetTable.getColBetPlacedWith() + " FROM " + BetTable.getTableName() +
                    " WHERE " + BetTable.getColMatchId() + " = " + dbId);

            while (rsBet.next()) {
                Assert.assertEquals(stake, rsBet.getInt(1));
                Assert.assertEquals(odds, rsBet.getDouble(2), 0.01);
                Assert.assertEquals(rbOn.getSqlIntCode(), rsBet.getInt(3));
                Assert.assertEquals(bookieUsed, rsBet.getString(4));
            }

        } catch (SQLException e) {
            e.printStackTrace();
            fail();
        }
    }

    @Test
    public void canAddPredictionToDb() {
        //needs a single game in db and then create a prediction and see if prediction is there and added correctly.
        League l = new League(LeagueIdsAndData.EPL);
        Season s = l.getSeason(19);
        Date date = DateHelper.subtractXDaysFromDate(new Date(),5);
        Match m1 = s.addNewMatch(new Match(new Team("home1"), new Team("away1"), date));
        Match m2 = s.addNewMatch(new Match(new Team("home2"), new Team("away2"), date));
        DS_Insert.writeLeagueToDb(l);

        MatchToPredict withLineupPredictions = new MatchToPredict(m1.getHomeTeam().getTeamName(), m1.getAwayTeam().getTeamName(), s.getSeasonKey(),
                l.getName(), DateHelper.getSqlDate(date), 1, -1);
        MatchToPredict noLineupPredictions = new MatchToPredict(m2.getHomeTeam().getTeamName(), m2.getAwayTeam().getTeamName(), s.getSeasonKey(),
                l.getName(), DateHelper.getSqlDate(date), 2, -1);
        withLineupPredictions.setOurPredictions(new double[]{1.1,2.2,3.3}, true);
        noLineupPredictions.setOurPredictions(new double[]{1.5,2.5,3.5}, false);
        LinkedHashMap<String, double[]> bookiesOdds = new LinkedHashMap<>();
        String expectedBookie = "BettingIsForFools";
        double[] bookieOddsArr = new double[]{0.1,0.2,0.3};
        bookiesOdds.put(expectedBookie, bookieOddsArr);
        withLineupPredictions.setBookiesOdds(bookiesOdds);
        noLineupPredictions.setBookiesOdds(bookiesOdds);
        ArrayList<MatchToPredict> mtps = new ArrayList<>(Arrays.asList(withLineupPredictions, noLineupPredictions));
        DS_Insert.addPredictionsToDb(mtps);

        try (Statement stmt = connection.createStatement()) {
            ResultSet rs = stmt.executeQuery("SELECT " + PredictionTable.getColDate() + ", " + PredictionTable.getColWithLineups() + ", " +
                    PredictionTable.getColHomePred() + ", " + PredictionTable.getColDrawPred() + ", " + PredictionTable.getColAwayPred() + ", " +
                    PredictionTable.getColBookieName() + ", " + PredictionTable.getColHOdds() + ", " + PredictionTable.getColDOdds() + ", " +
                    PredictionTable.getColAOdds() + ", " + PredictionTable.getColMatchId() + " FROM " + PredictionTable.getTableName());

            int count = 0;
            while (rs.next()) {
                String predDate = rs.getString(1);
                boolean withLineups = rs.getBoolean(2);
                double homePred = rs.getDouble(3);
                double drawPred = rs.getDouble(4);
                double awayPred = rs.getDouble(5);
                String bookie = rs.getString(6);
                double homeOdds = rs.getDouble(7);
                double drawOdds = rs.getDouble(8);
                double awayOdds = rs.getDouble(9);
                int matchId = rs.getInt(10);

                if (count == 0) {
                    //we'll test for withLineups
                    Assert.assertNotEquals(DateHelper.getSqlDate(date), predDate);
                    Assert.assertTrue(withLineups);
                    Assert.assertEquals(homePred, withLineupPredictions.getOurPredictions(true)[0], 0.01);
                    Assert.assertEquals(drawPred, withLineupPredictions.getOurPredictions(true)[1], 0.01);
                    Assert.assertEquals(awayPred, withLineupPredictions.getOurPredictions(true)[2], 0.01);
                    Assert.assertEquals(expectedBookie, bookie);
                    Assert.assertEquals(bookieOddsArr[0], homeOdds, 0.01);
                    Assert.assertEquals(bookieOddsArr[1], drawOdds, 0.01);
                    Assert.assertEquals(bookieOddsArr[2], awayOdds, 0.01);
                    Assert.assertEquals(1, matchId);
                } else {
                    //looking at without lineups
                    Assert.assertNotEquals(DateHelper.getSqlDate(date), predDate);
                    Assert.assertFalse(withLineups);
                    Assert.assertEquals(homePred, noLineupPredictions.getOurPredictions(false)[0], 0.01);
                    Assert.assertEquals(drawPred, noLineupPredictions.getOurPredictions(false)[1], 0.01);
                    Assert.assertEquals(awayPred, noLineupPredictions.getOurPredictions(false)[2], 0.01);
                    Assert.assertEquals(expectedBookie, bookie);
                    Assert.assertEquals(bookieOddsArr[0], homeOdds, 0.01);
                    Assert.assertEquals(bookieOddsArr[1], drawOdds, 0.01);
                    Assert.assertEquals(bookieOddsArr[2], awayOdds, 0.01);
                    Assert.assertEquals(2, matchId);
                }
                count++;
            }
            Assert.assertNotEquals(0, count);
        } catch (SQLException e) {
            e.printStackTrace();
            fail();
        }
    }

    @After
    public void tearDown() {
        DS_Main.closeConnection();
    }

}
