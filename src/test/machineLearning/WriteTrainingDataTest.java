package machineLearning;

import com.footballbettingcore.utils.DateHelper;
import com.footballbettingcore.machineLearning.createData.Main;
import com.footballbettingcore.machineLearning.createData.classes.TrainingMatch;
import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;

public class WriteTrainingDataTest {
//    @Test
//    public void canRemoveMatchesAfterADate() {
//        Date removeAfter = DateHelper.createDateyyyyMMdd("2020", "08", "26");
//
//        String t1 = "team1", t2 = "team2";
//        TrainingMatch beforeTime = new TrainingMatch(t1, t2, 1.1,2.2,3.3,3,2,
//                DateHelper.getSqlDate(DateHelper.subtractXminsFromDate(removeAfter, 1)), 20, 1,1.2,2.3);
//        TrainingMatch sameTime = new TrainingMatch(t1, t2, 1.1,2.2,3.3,3,2,
//                DateHelper.getSqlDate(DateHelper.addXMinsToDate(removeAfter, 0)), 20, 1,1.2,2.3);
//        TrainingMatch afterTime = new TrainingMatch(t1, t2, 1.1,2.2,3.3,3,2,
//                DateHelper.getSqlDate(DateHelper.addXMinsToDate(removeAfter, 1)), 20, 1,1.2,2.3);
//
//        ArrayList<TrainingMatch> tms = new ArrayList<>(Arrays.asList(beforeTime, sameTime, afterTime));
//        ArrayList<TrainingMatch> filteredTms = Main.removeTrainingMatches(null, removeAfter, tms);
//        Assert.assertEquals(2, filteredTms.size());
//        Assert.assertTrue(filteredTms.contains(beforeTime));
//        Assert.assertTrue(filteredTms.contains(sameTime));
//        Assert.assertFalse(filteredTms.contains(afterTime));
//    }
//
//    @Test
//    public void canRemoveMatchesBeforeADate() {
//        Date removeBefore = DateHelper.createDateyyyyMMdd("2020", "08", "26");
//
//        String t1 = "team1", t2 = "team2";
//
//        TrainingMatch beforeTime = new TrainingMatch(t1, t2, 1.1,2.2,3.3,3,2,
//                DateHelper.getSqlDate(DateHelper.subtractXminsFromDate(removeBefore, 1)), 20, 1,1.2,2.3);
//        TrainingMatch sameTime = new TrainingMatch(t1, t2, 1.1,2.2,3.3,3,2,
//                DateHelper.getSqlDate(DateHelper.addXMinsToDate(removeBefore, 0)), 20, 1,1.2,2.3);
//        TrainingMatch afterTime = new TrainingMatch(t1, t2, 1.1,2.2,3.3,3,2,
//                DateHelper.getSqlDate(DateHelper.addXMinsToDate(removeBefore, 1)), 20, 1,1.2,2.3);
//
//        ArrayList<TrainingMatch> tms = new ArrayList<>(Arrays.asList(beforeTime, sameTime, afterTime));
//        ArrayList<TrainingMatch> filteredTms = Main.removeTrainingMatches(removeBefore, null, tms);
//        Assert.assertEquals(2, filteredTms.size());
//        Assert.assertFalse(filteredTms.contains(beforeTime));
//        Assert.assertTrue(filteredTms.contains(sameTime));
//        Assert.assertTrue(filteredTms.contains(afterTime));
//    }
//
//    @Test
//    public void canRemoveMatchesOutsideOfRange() {
//        Date removeBefore = DateHelper.createDateyyyyMMdd("2020", "08", "26");
//        Date removeAfter = DateHelper.createDateyyyyMMdd("2020", "08", "27");
//
//        String t1 = "team1", t2 = "team2";
//
//        TrainingMatch beforeTime = new TrainingMatch(t1, t2, 1.1,2.2,3.3,3,2,
//                DateHelper.getSqlDate(DateHelper.subtractXminsFromDate(removeBefore, 1)), 20, 1,1.2,2.3);
//        TrainingMatch sameAsBeforeTime = new TrainingMatch(t1, t2, 1.1,2.2,3.3,3,2,
//                DateHelper.getSqlDate(DateHelper.addXMinsToDate(removeBefore, 0)), 20, 1,1.2,2.3);
//        TrainingMatch inRange = new TrainingMatch(t1, t2, 1.1,2.2,3.3,3,2,
//                DateHelper.getSqlDate(DateHelper.addXMinsToDate(removeBefore, 1)), 20, 1,1.2,2.3);
//        TrainingMatch sameAsAfterTime = new TrainingMatch(t1, t2, 1.1,2.2,3.3,3,2,
//                DateHelper.getSqlDate(DateHelper.addXMinsToDate(removeAfter, 0)), 20, 1,1.2,2.3);
//        TrainingMatch afterTime = new TrainingMatch(t1, t2, 1.1,2.2,3.3,3,2,
//                DateHelper.getSqlDate(DateHelper.addXMinsToDate(removeAfter, 1)), 20, 1,1.2,2.3);
//
//        ArrayList<TrainingMatch> tms = new ArrayList<>(Arrays.asList(beforeTime, sameAsBeforeTime, inRange, sameAsAfterTime, afterTime));
//        ArrayList<TrainingMatch> filteredTms = Main.removeTrainingMatches(removeBefore, removeAfter, tms);
//        Assert.assertEquals(3, filteredTms.size());
//        Assert.assertFalse(filteredTms.contains(beforeTime));
//        Assert.assertTrue(filteredTms.contains(sameAsBeforeTime));
//        Assert.assertTrue(filteredTms.contains(inRange));
//        Assert.assertTrue(filteredTms.contains(sameAsAfterTime));
//        Assert.assertFalse(filteredTms.contains(afterTime));
//    }
}
