package machineLearning;

import com.petermarshall.DateHelper;
import com.petermarshall.machineLearning.BetDecision;
import com.petermarshall.machineLearning.DecideBet;
import com.petermarshall.machineLearning.createData.classes.MatchToPredict;
import com.petermarshall.scrape.classes.OddsCheckerBookies;
import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.LinkedHashMap;

import static com.petermarshall.database.Result.AWAY_WIN;
import static com.petermarshall.database.Result.HOME_WIN;
import static com.petermarshall.machineLearning.DecideBet.*;

public class DecideBetTest {
    @Test
    public void onlyBetsOnMostLikelyOutcome() {
        MatchToPredict mtp = new MatchToPredict("Home", "Away", "18-19", "EPL",
                DateHelper.getSqlDate(new Date()), -1, -1);
        //bookie probability of home win is 0.2 less than our predictions. But it is not the highest probability so should not be bet on.
        //probability of away win is highest probability and also 0.2 less so this should be bet on.
        mtp.setOurPredictions(new double[]{0.3, 0.3, 0.4}, false);
        LinkedHashMap<String, double[]> bookiesOdds = new LinkedHashMap<>();
        bookiesOdds.put(OddsCheckerBookies.BET365.getName(), new double[]{1/0.1, 1/0.3, 1/0.2});
        bookiesOdds.put(OddsCheckerBookies.UNIBET.getName(), new double[]{1/0.5, 1/0.5, 1/0.5});
        mtp.setBookiesOdds(bookiesOdds);
        ArrayList<MatchToPredict> mtps = new ArrayList<>(Arrays.asList(mtp));
        DecideBet.addDecisionRealMatches(mtps);
        Assert.assertEquals(1, mtp.getGoodBets().size());
        BetDecision bd = mtp.getGoodBets().get(0);
        Assert.assertEquals(AWAY_WIN, bd.getWinner());
    }

    @Test
    public void canRoundToNearest50p() {
        double same = 1.5;
        Assert.assertEquals(same, DecideBet.roundToNearest50p(same), 0.0001);
        double slightlyLess = 2.49;
        Assert.assertEquals(2.5, DecideBet.roundToNearest50p(slightlyLess), 0.0001);
        double slightlyMore = 3.51;
        Assert.assertEquals(3.5, DecideBet.roundToNearest50p(slightlyMore), 0.0001);
        double closerTo0 = 4.24;
        Assert.assertEquals(4, DecideBet.roundToNearest50p(closerTo0), 0.0001);
        double borderLineGoesUp = 5.75;
        Assert.assertEquals(6, DecideBet.roundToNearest50p(borderLineGoesUp), 0.0001);
    }

    @Test
    public void canLimitStake() {
        double aboveStake = MAX_STAKE + 0.2;
        Assert.assertEquals(MAX_STAKE, DecideBet.roundToLimits(aboveStake), 0.0001);
        double belowStake = MIN_STAKE - 0.2;
        Assert.assertEquals(MIN_STAKE, DecideBet.roundToLimits(belowStake), 0.0001);
        double withinStake = MAX_STAKE - 0.5;
        Assert.assertEquals(withinStake, DecideBet.roundToLimits(withinStake), 0.0001);
    }

    @Test
    public void doesNotMakeBetsWhereOddsCouldntBeScraped() {
        MatchToPredict mtp = new MatchToPredict("Home", "Away", "18-19", "EPL",
                DateHelper.getSqlDate(new Date()), -1, -1);
        mtp.setOurPredictions(new double[]{0.3, 0.3, 0.4}, false);
        LinkedHashMap<String, double[]> bookiesOdds = new LinkedHashMap<>();
        bookiesOdds.put(OddsCheckerBookies.BET365.getName(), new double[]{-1, -1, -1});
        bookiesOdds.put(OddsCheckerBookies.UNIBET.getName(), new double[]{-1,-1,-1});
        mtp.setBookiesOdds(bookiesOdds);
        ArrayList<MatchToPredict> mtps = new ArrayList<>(Arrays.asList(mtp));
        DecideBet.addDecisionRealMatches(mtps);
        Assert.assertEquals(0, mtp.getGoodBets().size());
    }

    @Test
    public void betsMoreWhenOurPredictionIsMuchHigher() {
        //will be 0.3 greater
        MatchToPredict highPrediction = new MatchToPredict("Home", "Away", "18-19", "EPL",
                DateHelper.getSqlDate(new Date()), -1, -1);
        highPrediction.setOurPredictions(new double[]{0.7, 0.3, 0.3}, false);
        //will be 0.2 greater
        MatchToPredict lowerPrediction = new MatchToPredict("Home", "Away", "18-19", "EPL",
                DateHelper.getSqlDate(new Date()), -1, -1);
        lowerPrediction.setOurPredictions(new double[]{0.6, 0.3, 0.3}, false);
        LinkedHashMap<String, double[]> bookiesOdds = new LinkedHashMap<>();
        bookiesOdds.put(OddsCheckerBookies.BET365.getName(), new double[]{1/0.4, 1/0.3, 1/0.4});
        bookiesOdds.put(OddsCheckerBookies.UNIBET.getName(), new double[]{1/0.4, 1/0.3, 1/0.4});
        highPrediction.setBookiesOdds(bookiesOdds);
        lowerPrediction.setBookiesOdds(bookiesOdds);
        ArrayList<MatchToPredict> mtps = new ArrayList<>(Arrays.asList(highPrediction, lowerPrediction));
        DecideBet.addDecisionRealMatches(mtps);
        Assert.assertEquals(1, highPrediction.getGoodBets().size());
        Assert.assertEquals(1, lowerPrediction.getGoodBets().size());
        Assert.assertEquals(HOME_WIN, highPrediction.getGoodBets().get(0).getWinner());
        Assert.assertEquals(HOME_WIN, lowerPrediction.getGoodBets().get(0).getWinner());
        double stakeHighPred = highPrediction.getGoodBets().get(0).getBookiePriority().first().getStake();
        double stakeLowPred = lowerPrediction.getGoodBets().get(0).getBookiePriority().first().getStake();
        Assert.assertTrue(stakeHighPred > stakeLowPred);
    }

    @Test
    public void addsCorrectStakeAndOdds() {
        MatchToPredict mtp = new MatchToPredict("Home", "Away", "18-19", "EPL",
                DateHelper.getSqlDate(new Date()), -1, -1);
        mtp.setOurPredictions(new double[]{0.65, 0.3, 0.4}, false);
        LinkedHashMap<String, double[]> bookiesOdds = new LinkedHashMap<>();
        bookiesOdds.put(OddsCheckerBookies.BET365.getName(), new double[]{1/0.41, 1/0.3, 1/0.29});
        bookiesOdds.put(OddsCheckerBookies.UNIBET.getName(), new double[]{1/0.4, 1/0.3, 1/0.3}); //should return this one as the odds will be more favourable
        mtp.setBookiesOdds(bookiesOdds);
        ArrayList<MatchToPredict> mtps = new ArrayList<>(Arrays.asList(mtp));
        DecideBet.addDecisionRealMatches(mtps);
        Assert.assertEquals(1, mtp.getGoodBets().size());
        Assert.assertEquals(HOME_WIN, mtp.getGoodBets().get(0).getWinner());
        Assert.assertEquals(1/0.4, mtp.getGoodBets().get(0).getBookiePriority().first().getMinOdds(), 0.0001);
        double expBetterBy = 0.1;
        double expVarStake = roundToLimits(getVariableStake(expBetterBy));
        Assert.assertEquals(expVarStake, mtp.getGoodBets().get(0).getBookiePriority().first().getStake(), 0.0001);
    }
}
