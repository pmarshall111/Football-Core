package machineLearning;

import com.petermarshall.database.datasource.DS_Main;
import com.petermarshall.database.dbTables.LeagueTable;
import com.petermarshall.database.dbTables.MatchTable;
import com.petermarshall.database.dbTables.TeamTable;
import com.petermarshall.machineLearning.createData.PastStatsCalculator;
import com.petermarshall.machineLearning.createData.classes.MatchToPredict;
import com.petermarshall.machineLearning.createData.classes.TrainingMatch;
import com.petermarshall.machineLearning.createData.classes.TrainingTeam;
import com.petermarshall.scrape.classes.League;
import com.petermarshall.scrape.classes.Match;
import com.petermarshall.scrape.classes.Season;
import org.junit.Assert;
import org.junit.Test;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;

import static com.petermarshall.database.datasource.DS_Main.connection;
import static org.junit.Assert.fail;

//Class will be to ensure that the methods we use to create features for TrainingMatches and MatchesToPredict are the same.
public class FeatureConsistency {
    @Test
    public void featuresAreTheSame() {
        DS_Main.openProductionConnection();
        ArrayList<TrainingMatch> trainingMatches = PastStatsCalculator.getAllTrainingMatches();

        //create a match to predict based on a random training match
        TrainingMatch tMatch = trainingMatches.get(trainingMatches.size()-100);
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
            ArrayList<MatchToPredict> mtps = new ArrayList<>(Arrays.asList(mtp));
            PastStatsCalculator.addFeaturesToPredict(mtps);

            ArrayList<Double> trainingFeaturesNL = tMatch.getFeaturesNoLineups();
            ArrayList<Double> trainingFeatures = tMatch.getFeatures();
            trainingFeaturesNL.remove(0);
            trainingFeatures.remove(0);

            //next todos: add in feature comparison
            double[] predictFeaturesNL = mtp.getFeaturesWithoutResult();
            double[] predictFeatures = mtp.getFeaturesNoLineupsWithoutResult();

            Assert.assertEquals(trainingFeaturesNL.size(), predictFeaturesNL.length);
            Assert.assertEquals(trainingFeatures.size(), predictFeatures.length);
            for (int i = 0; i<trainingFeaturesNL.size(); i++) {
                Assert.assertEquals(trainingFeaturesNL.get(i), predictFeaturesNL[i], 0.0001);
            }
            for (int i = 0; i<trainingFeatures.size(); i++) {
                Assert.assertEquals(trainingFeatures.get(i), predictFeatures[i], 0.0001);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            fail();
        }

    }
}
