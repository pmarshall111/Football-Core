package database;

import com.footballbettingcore.database.BetLog;
import com.footballbettingcore.database.Result;
import com.footballbettingcore.database.datasource.DS_Insert;
import com.footballbettingcore.database.datasource.DS_Main;
import com.footballbettingcore.database.datasource.dbTables.*;
import com.footballbettingcore.machineLearning.createData.classes.MatchToPredict;
import com.footballbettingcore.scrape.classes.*;
import static org.junit.jupiter.api.Assertions.*;

import com.footballbettingcore.utils.DateHelper;
import org.junit.jupiter.api.*;
import org.testcontainers.containers.MariaDBContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.*;

import static com.footballbettingcore.database.datasource.DS_Main.connection;
import static database.GenerateData.*;

@Testcontainers
public class InsertIT {
    @Container
    public static final MariaDBContainer mariaDb = new MariaDBContainer<>(DockerImageName.parse("mariadb:10.5.5"))
            .withReuse(true);

    public static GenerateData data;

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
        data = addBulkData(true);
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
    public void canAddRecordsWithIncreasingIds() {
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
                assertNotNull(prevId);
                assertEquals(prevId+1, id);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            fail();
        }
    }

    @Test
    public void insertsCorrectNumbOfMatches() {
        try (Statement s = connection.createStatement()) {
            ResultSet rs = s.executeQuery("SELECT COUNT(*) FROM " + MatchTable.getTableName());
            while (rs.next()) {
                int count = rs.getInt(1);
                assertEquals(data.getMatches().size(), count);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            fail();
        }
    }

    @Test
    public void insertsAPlayerWithCorrectTeam() {
        Match m = data.getMatches().get(10);
        String homeTeamName = m.getHomeTeam().getTeamName();
        String awayTeamName = m.getAwayTeam().getTeamName();
        String firstHomePlayer = m.getHomePlayerRatings().values().iterator().next().getName();
        String firstAwayPlayer = m.getAwayPlayerRatings().values().iterator().next().getName();
        try (Statement s = connection.createStatement()) {
            ResultSet rs = s.executeQuery(
                    "SELECT COUNT(*) FROM " + PlayerRatingTable.getTableName() +
                        " INNER JOIN " + TeamTable.getTableName() + " ON " + PlayerRatingTable.getColTeamId() + " = " + TeamTable.getTableName() + "._id" +
                        " WHERE " + PlayerRatingTable.getTableName() + "." + PlayerRatingTable.getColPlayerName() + " = '" + firstHomePlayer + "'" +
                        " AND " + TeamTable.getTableName() + "." + TeamTable.getColTeamName() + " = '" + homeTeamName + "'");
            while (rs.next()) {
                int count = rs.getInt(1);
                assertEquals(1, count);
            }
            //away
            rs = s.executeQuery(
                    "SELECT COUNT(*) FROM " + PlayerRatingTable.getTableName() +
                            " INNER JOIN " + TeamTable.getTableName() + " ON " + PlayerRatingTable.getColTeamId() + " = " + TeamTable.getTableName() + "._id" +
                            " WHERE " + PlayerRatingTable.getTableName() + "." + PlayerRatingTable.getColPlayerName() + " = '" + firstAwayPlayer + "'" +
                            " AND " + TeamTable.getTableName() + "." + TeamTable.getColTeamName() + " = '" + awayTeamName + "'");
            while (rs.next()) {
                int count = rs.getInt(1);
                assertEquals(1, count);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            fail();
        }
    }

    @Test
    public void addsTheCorrectDataToMatch() {
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
                assertEquals(HOMESCORE, homeScore);
                assertEquals(AWAYSCORE, awayScore);
                assertEquals(HOMEXG, homeXg, 0.0001);
                assertEquals(AWAYXG, awayXg, 0.0001);
                assertEquals(HOMEODDS, homeOdds, 0.0001);
                assertEquals(DRAWODDS, drawOdds, 0.0001);
                assertEquals(AWAYODDS, awayOdds, 0.0001);
                assertEquals(FIRSTSCORER, firstScorer);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            fail();
        }
    }

    @Test
    public void addsTheCorrectDataToPlayer() {
        PlayerRating pr = data.getPlayerRatings().get(100);
        String playerName = pr.getName();
        try (Statement s = connection.createStatement()) {
            ResultSet rs = s.executeQuery(
                    "SELECT " + PlayerRatingTable.getColMins() + ", " + PlayerRatingTable.getColRating() + ", " + PlayerRatingTable.getColPosition() +
                            " FROM " + PlayerRatingTable.getTableName() +
                            " WHERE " + PlayerRatingTable.getTableName() + "." + PlayerRatingTable.getColPlayerName() + " = '" + playerName + "'");
            while (rs.next()) {
                int mins = rs.getInt(1);
                double rating = rs.getDouble(2);
                String position = rs.getString(3);
                assertEquals(MINUTES, mins);
                assertEquals(RATING, rating, 0.0001);
                assertEquals(POSITION, position);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            fail();
        }
    }

    @Test
    public void doesntAddRepeatedPlayer() {
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
                assertEquals(1, count);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            fail();
        }
    }

    @Test
    public void addsOtherPlayersEvenWithRepeatedPlayer() {
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
                assertEquals(1, count);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            fail();
        }
    }

    @Test
    public void givesPromotedTeamsCorrectLeagueId() {
        League l = data.getLeagues().get(0);
        Season s = l.getSeason(21); // add new season
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
                assertEquals(leagueId, afterAddleagueId);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            fail();
        }
    }

    @Test
    public void canLogBetPlaced() {
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
                    l.getName(), sqlMatchDate, dbId, m.getSofaScoreGameId());

            Result rbOn = Result.HOME_WIN;
            String bookieUsed = "BettingIsForFools";
            double odds = 1.22;
            int stake = 5;
            DS_Insert.logBetPlaced(new BetLog(mtp, rbOn, bookieUsed, odds, stake, false));

            ResultSet rsBet = stmt.executeQuery("SELECT COUNT(*) FROM " + BetTable.getTableName());
            while (rsBet.next()) {
                assertEquals(1, rsBet.getInt(1));
            }

            rsBet = stmt.executeQuery("SELECT " +
                    BetTable.getColStake() + ", " +
                    BetTable.getColOdds() + ", " +
                    BetTable.getColResultBetOn() + ", " +
                    BetTable.getColBetPlacedWith() + ", " +
                    BetTable.getColIsLayBet() +
                    " FROM " + BetTable.getTableName() +
                    " WHERE " + BetTable.getColMatchId() + " = " + dbId);

            while (rsBet.next()) {
                assertEquals(stake, rsBet.getInt(1));
                assertEquals(odds, rsBet.getDouble(2), 0.01);
                assertEquals(rbOn.getSqlIntCode(), rsBet.getInt(3));
                assertEquals(bookieUsed, rsBet.getString(4));
                assertFalse(rsBet.getBoolean(5));
            }

        } catch (SQLException e) {
            e.printStackTrace();
            fail();
        }
    }

    @Test
    public void canAddPredictionToDb() {
        League l = data.getLeagues().get(0);
        Season s = l.getSeason(20);
        Match m = s.getAllMatches().get(0);

        try (Statement stmt = connection.createStatement()) {
            String home = "home", away = "away";
            ResultSet rs = stmt.executeQuery("SELECT " + MatchTable.getTableName() + "._id " +
                    "FROM " + MatchTable.getTableName() +
                    " INNER JOIN " + TeamTable.getTableName() + " AS " + home + " ON " + MatchTable.getColHometeamId() + " = " + home + "._id" +
                    " INNER JOIN " + TeamTable.getTableName() + " AS " + away + " ON " + MatchTable.getColAwayteamId() + " = " + away + "._id" +
                    " WHERE " + home + "." + TeamTable.getColTeamName() + " = '" + m.getHomeTeam().getTeamName() + "'" +
                    " AND " + away + "." + TeamTable.getColTeamName() + " = '" + m.getAwayTeam().getTeamName() + "'");
            int dbId = -1;
            while (rs.next()) {
                dbId = rs.getInt(1);
            }

            MatchToPredict prediction = new MatchToPredict(m.getHomeTeam().getTeamName(), m.getAwayTeam().getTeamName(), s.getSeasonKey(),
                    l.getName(), DateHelper.getSqlDate(m.getKickoffTime()), dbId, -1);
            prediction.setOurPredictions(new double[]{1.5,2.5,3.5}, false);
            LinkedHashMap<String, double[]> bookiesOdds = new LinkedHashMap<>();
            String expectedBookie = "BettingIsForFools";
            double[] bookieOddsArr = new double[]{0.1,0.2,0.3};
            bookiesOdds.put(expectedBookie, bookieOddsArr);
            prediction.setBookiesOdds(bookiesOdds);
            ArrayList<MatchToPredict> mtps = new ArrayList<>(List.of(prediction));
            DS_Insert.addPredictionsToDb(mtps);

            rs = stmt.executeQuery("SELECT " + PredictionTable.getColDate() + ", " +
                    PredictionTable.getColHomePred() + ", " + PredictionTable.getColDrawPred() + ", " + PredictionTable.getColAwayPred() + ", " +
                    PredictionTable.getColBookieName() + ", " + PredictionTable.getColHOdds() + ", " + PredictionTable.getColDOdds() + ", " +
                    PredictionTable.getColAOdds() + ", " + PredictionTable.getColMatchId() + " FROM " + PredictionTable.getTableName());

            int count = 0;
            while (rs.next()) {
                String predDate = rs.getString(1);
                double homePred = rs.getDouble(2);
                double drawPred = rs.getDouble(3);
                double awayPred = rs.getDouble(4);
                String bookie = rs.getString(5);
                double homeOdds = rs.getDouble(6);
                double drawOdds = rs.getDouble(7);
                double awayOdds = rs.getDouble(8);
                int matchId = rs.getInt(9);

                assertNotNull(predDate);
                assertEquals(homePred, prediction.getOurPredictions(false)[0], 0.01);
                assertEquals(drawPred, prediction.getOurPredictions(false)[1], 0.01);
                assertEquals(awayPred, prediction.getOurPredictions(false)[2], 0.01);
                assertEquals(expectedBookie, bookie);
                assertEquals(bookieOddsArr[0], homeOdds, 0.01);
                assertEquals(bookieOddsArr[1], drawOdds, 0.01);
                assertEquals(bookieOddsArr[2], awayOdds, 0.01);
                assertEquals(dbId, matchId);

                count++;
            }
            assertEquals(1, count);
        } catch (SQLException e) {
            e.printStackTrace();
            fail();
        }
    }
}
