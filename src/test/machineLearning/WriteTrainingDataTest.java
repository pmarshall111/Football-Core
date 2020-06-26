package machineLearning;

import com.petermarshall.DateHelper;
import com.petermarshall.machineLearning.createData.Main;
import com.petermarshall.machineLearning.createData.classes.MatchToPredict;
import com.petermarshall.machineLearning.createData.classes.TrainingMatch;
import com.petermarshall.machineLearning.createData.classes.TrainingTeam;
import com.petermarshall.scrape.classes.*;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;

public class WriteTrainingDataTest {
    @Test
    public void canRemoveMatchesAfterADate() {
        Date removeAfter = DateHelper.createDateyyyyMMdd("2020", "08", "26");

        TrainingTeam t1 = new TrainingTeam("team1"), t2 = new TrainingTeam("team2");
        TrainingMatch beforeTime = new TrainingMatch(t1, t2, 1.1,2.2,3.3,3,2,
                DateHelper.getSqlDate(DateHelper.subtractXminsFromDate(removeAfter, 1)), 20);
        TrainingMatch sameTime = new TrainingMatch(t1, t2, 1.1,2.2,3.3,3,2,
                DateHelper.getSqlDate(DateHelper.addXMinsToDate(removeAfter, 0)), 20);
        TrainingMatch afterTime = new TrainingMatch(t1, t2, 1.1,2.2,3.3,3,2,
                DateHelper.getSqlDate(DateHelper.addXMinsToDate(removeAfter, 1)), 20);

        ArrayList<TrainingMatch> tms = new ArrayList<>(Arrays.asList(beforeTime, sameTime, afterTime));
        ArrayList<TrainingMatch> filteredTms = Main.removeTrainingMatches(null, removeAfter, tms);
        Assert.assertEquals(2, filteredTms.size());
        Assert.assertTrue(filteredTms.contains(beforeTime));
        Assert.assertTrue(filteredTms.contains(sameTime));
        Assert.assertFalse(filteredTms.contains(afterTime));
    }

    @Test
    public void canRemoveMatchesBeforeADate() {
        Date removeBefore = DateHelper.createDateyyyyMMdd("2020", "08", "26");

        TrainingTeam t1 = new TrainingTeam("team1"), t2 = new TrainingTeam("team2");

        TrainingMatch beforeTime = new TrainingMatch(t1, t2, 1.1,2.2,3.3,3,2,
                DateHelper.getSqlDate(DateHelper.subtractXminsFromDate(removeBefore, 1)), 20);
        TrainingMatch sameTime = new TrainingMatch(t1, t2, 1.1,2.2,3.3,3,2,
                DateHelper.getSqlDate(DateHelper.addXMinsToDate(removeBefore, 0)), 20);
        TrainingMatch afterTime = new TrainingMatch(t1, t2, 1.1,2.2,3.3,3,2,
                DateHelper.getSqlDate(DateHelper.addXMinsToDate(removeBefore, 1)), 20);

        ArrayList<TrainingMatch> tms = new ArrayList<>(Arrays.asList(beforeTime, sameTime, afterTime));
        ArrayList<TrainingMatch> filteredTms = Main.removeTrainingMatches(removeBefore, null, tms);
        Assert.assertEquals(2, filteredTms.size());
        Assert.assertFalse(filteredTms.contains(beforeTime));
        Assert.assertTrue(filteredTms.contains(sameTime));
        Assert.assertTrue(filteredTms.contains(afterTime));
    }

    @Test
    public void canRemoveMatchesOutsideOfRange() {
        Date removeBefore = DateHelper.createDateyyyyMMdd("2020", "08", "26");
        Date removeAfter = DateHelper.createDateyyyyMMdd("2020", "08", "27");

        TrainingTeam t1 = new TrainingTeam("team1"), t2 = new TrainingTeam("team2");

        TrainingMatch beforeTime = new TrainingMatch(t1, t2, 1.1,2.2,3.3,3,2,
                DateHelper.getSqlDate(DateHelper.subtractXminsFromDate(removeBefore, 1)), 20);
        TrainingMatch sameAsBeforeTime = new TrainingMatch(t1, t2, 1.1,2.2,3.3,3,2,
                DateHelper.getSqlDate(DateHelper.addXMinsToDate(removeBefore, 0)), 20);
        TrainingMatch inRange = new TrainingMatch(t1, t2, 1.1,2.2,3.3,3,2,
                DateHelper.getSqlDate(DateHelper.addXMinsToDate(removeBefore, 1)), 20);
        TrainingMatch sameAsAfterTime = new TrainingMatch(t1, t2, 1.1,2.2,3.3,3,2,
                DateHelper.getSqlDate(DateHelper.addXMinsToDate(removeAfter, 0)), 20);
        TrainingMatch afterTime = new TrainingMatch(t1, t2, 1.1,2.2,3.3,3,2,
                DateHelper.getSqlDate(DateHelper.addXMinsToDate(removeAfter, 1)), 20);

        ArrayList<TrainingMatch> tms = new ArrayList<>(Arrays.asList(beforeTime, sameAsBeforeTime, inRange, sameAsAfterTime, afterTime));
        ArrayList<TrainingMatch> filteredTms = Main.removeTrainingMatches(removeBefore, removeAfter, tms);
        Assert.assertEquals(3, filteredTms.size());
        Assert.assertFalse(filteredTms.contains(beforeTime));
        Assert.assertTrue(filteredTms.contains(sameAsBeforeTime));
        Assert.assertTrue(filteredTms.contains(inRange));
        Assert.assertTrue(filteredTms.contains(sameAsAfterTime));
        Assert.assertFalse(filteredTms.contains(afterTime));
    }
}
