package machineLearning;

import com.petermarshall.database.datasource.DS_Main;
import com.petermarshall.database.dbTables.LeagueTable;
import com.petermarshall.database.dbTables.MatchTable;
import com.petermarshall.database.dbTables.PlayerRatingTable;
import com.petermarshall.database.dbTables.TeamTable;
import com.petermarshall.machineLearning.createData.CalculatePastStats;
import com.petermarshall.machineLearning.createData.classes.MatchToPredict;
import com.petermarshall.machineLearning.createData.classes.Player;
import com.petermarshall.machineLearning.createData.classes.TrainingMatch;
import com.petermarshall.scrape.classes.Season;
import org.junit.Assert;
import org.junit.Test;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

import static com.petermarshall.database.datasource.DS_Main.*;
import static org.junit.Assert.fail;

//Class will be to ensure that the methods we use to create features for TrainingMatches and MatchesToPredict are the same.
public class FeatureConsistency {
    @Test
    public void nonLineupFeaturesAreTheSame() {
        DS_Main.openProductionConnection();
        ArrayList<TrainingMatch> trainingMatches = CalculatePastStats.getAllTrainingMatches();

        //has to be the last match as the predict function calculates team stats for all played games so far.
        //The last match is then not saved
        TrainingMatch tMatch = trainingMatches.get(trainingMatches.size()-1);
        String matchSeasonKey = Season.getSeasonKeyFromYearStart(tMatch.getSeasonYearStart());
        //need league, db id, sofascore id. Possible DB call needed.
        try (Statement stmt = connection.createStatement()) {
            String home = "home", away = "away";
            ResultSet rs = stmt.executeQuery("SELECT " + LeagueTable.getTableName() + "." + LeagueTable.getColName() + ", " + MatchTable.getTableName() + "._id" + ", " +
                    MatchTable.getColDate() + ", " + MatchTable.getColSofascoreId() + " FROM " + MatchTable.getTableName() +
                    " INNER JOIN " + TeamTable.getTableName() + " AS " + home + " ON " + MatchTable.getColHometeamId() + " = " + home + "._id" +
                    " INNER JOIN " + TeamTable.getTableName() + " AS " + away + " ON " + MatchTable.getColAwayteamId() + " = " + away + "._id" +
                    " INNER JOIN " + LeagueTable.getTableName() + " ON " + home + "." + TeamTable.getColLeagueId() + " = " + LeagueTable.getTableName() + "._id" +
                    " WHERE " + home + "." + TeamTable.getColTeamName() + " = '" + tMatch.getHomeTeamName() + "'" +
                    " AND " + away + "." + TeamTable.getColTeamName() + " = '" + tMatch.getAwayTeamName() + "'" +
                    " AND " + MatchTable.getColSeasonYearStart() + " = " + tMatch.getSeasonYearStart());
            String leagueName = null, sqlDate = null;
            int dbId = -1, sofascoreId = -1;
            while (rs.next()) {
                leagueName = rs.getString(1);
                dbId = rs.getInt(2);
                sqlDate = rs.getString(3);
                sofascoreId = rs.getInt(4);
            }
            Assert.assertNotNull(leagueName);

            MatchToPredict mtp = new MatchToPredict(tMatch.getHomeTeamName(), tMatch.getAwayTeamName(), matchSeasonKey, leagueName, sqlDate, dbId, sofascoreId);
            addLineupsToMatch(mtp, sqlDate, stmt);
            ArrayList<MatchToPredict> mtps = new ArrayList<>(Arrays.asList(mtp));
            CalculatePastStats.addFeaturesToPredict(mtps, true);

            ArrayList<Double> trainingFeaturesNL = tMatch.getFeaturesNoLineups();
            ArrayList<Double> trainingFeatures = tMatch.getFeatures();
            trainingFeaturesNL.remove(0);
            trainingFeatures.remove(0);

            double[] predictFeaturesNL = mtp.getFeaturesNoLineupsWithoutResult();
            double[] predictFeatures = mtp.getFeaturesWithoutResult();

            Assert.assertEquals(trainingFeaturesNL.size(), predictFeaturesNL.length);
            Assert.assertEquals(trainingFeatures.size(), predictFeatures.length);
            for (int i = 0; i<trainingFeaturesNL.size(); i++) {
                if (i != 25 && i != 56) {
                    //Avoiding these 2 indexes as these are calculated from historic games and testing on past games will include the result of game we're predicting
                    Assert.assertEquals("Index failed at: " + i, trainingFeaturesNL.get(i), predictFeaturesNL[i], 0.0001);
                }
            }
            for (int i = 0; i<trainingFeatures.size(); i++) {
                if (i != 25 && i != 59) {
                    //Avoiding these 2 indexes as these are calculated from historic games and testing on past games will include the result of game we're predicting
                    Assert.assertEquals("Index failed at: " + i, trainingFeatures.get(i), predictFeatures[i], 0.0001);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            fail();
        }
    }

    private void addLineupsToMatch(MatchToPredict mtp, String sqlDate, Statement stmt) throws SQLException {
        String playersTeam = "players_team", home = "home", away = "away";
        ResultSet rs = stmt.executeQuery("SELECT " + PlayerRatingTable.getTableName() + "." + PlayerRatingTable.getColPlayerName() + ", " +
                PlayerRatingTable.getColMins() + ", " + PlayerRatingTable.getColRating() + ", " +
                playersTeam + "." + TeamTable.getColTeamName() + " FROM " + PlayerRatingTable.getTableName() +
                " INNER JOIN " + MatchTable.getTableName() + " ON " + PlayerRatingTable.getTableName() + "." + PlayerRatingTable.getColMatchId() + " = " + MatchTable.getTableName() + "._id" +
                " INNER JOIN " + TeamTable.getTableName() + " AS " + playersTeam + " ON " + PlayerRatingTable.getTableName() + "." + PlayerRatingTable.getColTeamId() + " = " + playersTeam + "._id" +
                " INNER JOIN " + TeamTable.getTableName() + " AS " + home + " ON " + MatchTable.getTableName() + "." + MatchTable.getColHometeamId() + " = " + home + "._id" +
                " INNER JOIN " + TeamTable.getTableName() + " AS " + away + " ON " + MatchTable.getTableName() + "." + MatchTable.getColAwayteamId() + " = " + away + "._id" +
                " WHERE " + home + "." + TeamTable.getColTeamName() + " = '" + mtp.getHomeTeamName() + "'" +
                " AND " + away + "." + TeamTable.getColTeamName() + " = '" + mtp.getAwayTeamName() + "'" +
                " AND " + MatchTable.getTableName() + "." + MatchTable.getColDate() + " = '" + sqlDate + "'" +
                " ORDER BY " + PlayerRatingTable.getColTeamId() + ", " + PlayerRatingTable.getColMins() + " DESC");

        HashMap<String, Player> homePlayers = new HashMap<>();
        HashMap<String, Player> awayPlayers = new HashMap<>();
        while (rs.next()) {
            String playerName = rs.getString(1);
            int mins = rs.getInt(2);
            double rating = rs.getDouble(3);
            String team = rs.getString(4);
            boolean isHomeTeam = team.equals(mtp.getHomeTeamName());
            Player p = new Player(playerName, mins, rating, isHomeTeam);
            if (isHomeTeam) {
                homePlayers.put(playerName, p);
            } else {
                awayPlayers.put(playerName, p);
            }
        }
        Assert.assertNotEquals(0, homePlayers.size());
        Assert.assertNotEquals(0, awayPlayers.size());

        ArrayList<String> homeStartingXI = new ArrayList<>(CalculatePastStats.getStartingXI(homePlayers).keySet());
        ArrayList<String> awayStartingXI = new ArrayList<>(CalculatePastStats.getStartingXI(awayPlayers).keySet());
        mtp.setHomeTeamPlayers(homeStartingXI);
        mtp.setAwayTeamPlayers(awayStartingXI);
    }
}
