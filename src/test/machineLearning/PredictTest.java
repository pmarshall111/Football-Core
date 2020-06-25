package machineLearning;

import com.petermarshall.DateHelper;
import com.petermarshall.machineLearning.createData.classes.MatchToPredict;
import com.petermarshall.machineLearning.logisticRegression.Predict;
import com.petermarshall.scrape.classes.Season;
import org.ejml.simple.SimpleMatrix;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.stream.Collectors;
import java.util.stream.DoubleStream;

public class PredictTest {
    SimpleMatrix allTheta = new SimpleMatrix(new double[][] {
            {1,0.5,0.25,0.6,1.2,0.21,0.5},
            {1,0.15,0.2,0.3, 0.2,0.1,2},
            {1,0.45,0.5,0.77,1.6,0.5,0.3},
    });
    double[][] ourFeatures = new double[][] {
            {1,0.5,0.45,0.3,1.2,0.21,0.5},
            {1,0.2,0.55,0.1,1.4,0.11,0.9},
            {1,0.9,0.45,0.7,0.72,0.41,0.3},
            {1,1.5,0.65,0.9,0.2,0.31,0.5},
            {1,7,0.75,0.6,1.2,0.01,4},
    };

    @Test
    public void predictTheSameForMatchToPredictVsMatrix() {
        SimpleMatrix X = new SimpleMatrix(ourFeatures);
        Predict.calcPredictions(allTheta, X);
        SimpleMatrix predictions = Predict.getOurPredictions();
        //create matches to predict using ourFeatures
        ArrayList<MatchToPredict> allMtps = new ArrayList<>();
        boolean withLineups = false;
        for (double[] matchFeatures : ourFeatures) {
            MatchToPredict ithMatch = new MatchToPredict("yoyoyo", "whadupwhadupwhadup", Season.getSeasonKeyFromYearStart(19),
                    "EPL", DateHelper.getSqlDate(new Date()), -1, -1);
            ArrayList<Double> featureList = DoubleStream.of(matchFeatures).boxed().collect(Collectors.toCollection(ArrayList::new));
            featureList.add(0, 3d); //adding the result at the start
            ithMatch.setFeatures(featureList, withLineups);
            allMtps.add(ithMatch);
        }
        //add probabilities & test
        Predict.addOurProbabilitiesToGames(allMtps, allTheta, withLineups);
        for (int row = 0; row<allMtps.size(); row++) {
            double[] matchPreds = allMtps.get(row).getOurPredictions(withLineups);
            for (int col = 0; col<matchPreds.length; col++) {
                Assert.assertEquals(predictions.get(row, col), matchPreds[col], 0.0001);
            }
        }
    }

    @Test
    public void predictionsTotalTo100Pc() {
        SimpleMatrix X = new SimpleMatrix(ourFeatures);
        Predict.calcPredictions(allTheta, X);
        SimpleMatrix predictions = Predict.getOurPredictions();
        for (int row = 0; row<predictions.numRows(); row++) {
            double homeProb = predictions.get(row, 0);
            double drawProb = predictions.get(row, 1);
            double awayProb = predictions.get(row, 2);
            Assert.assertEquals(1, homeProb+drawProb+awayProb, 0.001);
        }
    }
}
