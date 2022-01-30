package machineLearning;

import com.footballbettingcore.machineLearning.BetDecision;
import com.footballbettingcore.machineLearning.BookieBetInfo;
import com.footballbettingcore.scrape.classes.OddsCheckerBookies;
import org.junit.Assert;
import org.junit.Test;

import static com.footballbettingcore.database.Result.HOME_WIN;
import static com.footballbettingcore.machineLearning.DecideBet.MAX_STAKE;
import static com.footballbettingcore.machineLearning.DecideBet.MIN_STAKE;

public class BetDecisionTest {
    @Test
    public void canGetBookiesInRightOrder() {
        //looking for bookies offering higher odds to be shown first.
        BetDecision bd = new BetDecision(HOME_WIN);
        bd.addBookie(OddsCheckerBookies.BET365, 10, 5.4);
        bd.addBookie(OddsCheckerBookies.UNIBET, 13, 7.1);
        bd.addBookie(OddsCheckerBookies.LADBROKES, 4, 3.9);

        boolean seenUnibet = false;
        boolean seenBet365 = false;
        for (BookieBetInfo bbi: bd.getBookiePriority()) {
            if (!seenUnibet) {
                Assert.assertEquals(OddsCheckerBookies.UNIBET, bbi.getBookie());
                seenUnibet = true;
            } else if (!seenBet365) {
                Assert.assertEquals(OddsCheckerBookies.BET365, bbi.getBookie());
                seenBet365 = true;
            } else {
                Assert.assertEquals(OddsCheckerBookies.LADBROKES, bbi.getBookie());
            }
        }
    }

    @Test
    public void doesNotAddBookiesOutsideLimits() {
        BetDecision bd = new BetDecision(HOME_WIN);
        bd.addBookie(OddsCheckerBookies.BET365, MAX_STAKE+1, 5.4);
        bd.addBookie(OddsCheckerBookies.UNIBET, MIN_STAKE-0.2, 7.1);
        bd.addBookie(OddsCheckerBookies.LADBROKES, MIN_STAKE+0.2, -1);
        bd.addBookie(OddsCheckerBookies.PADDY_POWER, MIN_STAKE+0.2, 5); //within limits, should add.

        Assert.assertEquals(1, bd.getBookiePriority().size());
        BookieBetInfo bbi = bd.getBookiePriority().first();
        Assert.assertEquals(OddsCheckerBookies.PADDY_POWER, bbi.getBookie());
    }

    @Test
    public void addsCorrectOddsAndStake() {
        BetDecision bd = new BetDecision(HOME_WIN);
        double stake = MAX_STAKE-0.2;
        double odds = 5.4;
        bd.addBookie(OddsCheckerBookies.BET365, stake, odds);
        BookieBetInfo bbi = bd.getBookiePriority().first();
        Assert.assertEquals(OddsCheckerBookies.BET365, bbi.getBookie());
        Assert.assertEquals(stake, bbi.getStake(), 0.0001);
        Assert.assertEquals(odds, bbi.getMinOdds(), 0.0001);
    }
}
