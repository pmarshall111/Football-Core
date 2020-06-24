package machineLearning;

import com.petermarshall.database.Result;
import com.petermarshall.machineLearning.MoneyResults;
import org.junit.Assert;
import org.junit.Test;

public class MoneyResultsTest {

    @Test
    public void canAddANewBet() {
        MoneyResults mr = new MoneyResults();
        int stake = 5;
        double odds = 1.22;
        mr.addBet(stake, odds, true, Result.HOME_WIN);
        Assert.assertEquals(1, mr.getBetsMade());
        Assert.assertEquals(stake*odds, mr.getMoneyGotBack(), 0.01);
        Assert.assertEquals(stake, mr.getMoneySpent(), 0.01);
        Assert.assertEquals(stake*odds - stake, mr.getRawProfit(), 0.01);
        Assert.assertEquals(100*(stake*odds - stake)/stake, mr.getPcProfit(), 0.01);
    }

    @Test
    public void canCalcForMultipleBets() {
        MoneyResults mr = new MoneyResults();
        int[] stake = new int[]{5,5,5,5};
        double[] odds = new double[]{1.22,11,4.21,2.62};
        boolean[] betWon = new boolean[]{true,false,true,false};

        int totalSpent = 0;
        double moneyWonBack = 0;

        for (int i = 0; i<stake.length; i++) {
            mr.addBet(stake[i],odds[i],betWon[i], Result.HOME_WIN);
            totalSpent += stake[i];
            moneyWonBack += betWon[i] ? stake[i]*odds[i] : 0;
        }

        Assert.assertEquals(stake.length, mr.getBetsMade());
        Assert.assertEquals(moneyWonBack, mr.getMoneyGotBack(), 0.01);
        Assert.assertEquals(totalSpent, mr.getMoneySpent(), 0.01);
        Assert.assertEquals(moneyWonBack-totalSpent, mr.getRawProfit(), 0.01);
        Assert.assertEquals(100*(moneyWonBack-totalSpent) / totalSpent, mr.getPcProfit(), 0.01);
    }
}
