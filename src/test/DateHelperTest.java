import com.footballbettingcore.utils.DateHelper;
import org.junit.Assert;
import org.junit.Test;

import java.text.SimpleDateFormat;
import java.util.Date;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

public class DateHelperTest {
//
//    @Test
//    public void canCreateDateFromSofascoreEpoch() {
//        Date d = DateHelper.getDateFromSofascoreTimestamp(1617561000);
//        String isBeforeDStr = "04/04/2021";
//        String isAfterDStr = "05/04/2021"; //using a range to avoid anything messy with timezones/BST/GMT
//        try {
//            Date isBeforeD = new SimpleDateFormat("dd/MM/yyyy").parse(isBeforeDStr);
//            Date isAfterD = new SimpleDateFormat("dd/MM/yyyy").parse(isAfterDStr);
//            Assert.assertNotNull(d);
//            Assert.assertTrue(isBeforeD.before(d));
//            Assert.assertTrue(isAfterD.after(d));
//        } catch (Exception e) {
//            fail();
//        }
//    }
//
//    @Test
//    public void canFindMinutesBetweenDates() {
//        String d0 = "04/04/2021 19:05:00";
//        String d1 = "04/04/2021 20:00:00";
//        try {
//            Date firstDate = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss").parse(d0);
//            Date lastDate = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss").parse(d1);
//            int mins = DateHelper.findMinutesToAddToDate1ToGetDate2(firstDate, lastDate);
//            Assert.assertEquals(55, mins);
//
//            int minsWhenPutDatesInWrongOrder = DateHelper.findMinutesToAddToDate1ToGetDate2(lastDate, firstDate);
//            Assert.assertEquals(0, minsWhenPutDatesInWrongOrder);
//        } catch (Exception e) {
//            fail();
//        }
//    }
//
//    @Test
//    public void canConvertDateToDbFormat() {
//        //db dateString format is yyyy-mm-dd hh:mm:ss
//        Date createdDate = DateHelper.createDateyyyyMMddHHmmss("2020", "08", "26", "12", "34", "56");
//        String sqlString = DateHelper.getSqlDate(createdDate);
//        assertEquals("2020-08-26 12:34:56", sqlString);
//    }
}
