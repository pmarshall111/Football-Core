package machineLearning;

import com.petermarshall.machineLearning.BetDecision;
import com.petermarshall.machineLearning.BookieBetInfo;
import com.petermarshall.scrape.classes.OddsCheckerBookies;
import org.junit.Assert;
import org.junit.Test;

import java.util.TreeSet;

import static com.petermarshall.database.Result.HOME_WIN;

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
}
