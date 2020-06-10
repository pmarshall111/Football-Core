package database;

import com.petermarshall.DateHelper;
import com.petermarshall.database.datasource.DS_Get;
import com.petermarshall.database.datasource.DS_Insert;
import com.petermarshall.database.datasource.DS_Main;
import com.petermarshall.database.tables.MatchTable;
import com.petermarshall.database.tables.PlayerRatingTable;
import com.petermarshall.machineLearning.createData.classes.MatchToPredict;
import com.petermarshall.machineLearning.createData.refactor.HistoricMatchDbData;
import com.petermarshall.machineLearning.createData.refactor.PlayerMatchDbData;
import com.petermarshall.scrape.classes.*;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import static com.petermarshall.database.datasource.DS_Main.connection;
import static database.GenerateData.*;
import static org.junit.Assert.fail;

public class Get {
    @Before
    public void setup() {
        DbTestHelper.setupNewTestDb();
    }

    @After
    public void tearDown() {
        DS_Main.closeConnection();
    }

    @Test
    public void canGetTheLastCompletedMatch() {
        GenerateData data = addBulkData(true);
        League l = data.getLeagues().get(0);
        Season s = l.getSeason(20);
        Match lastMatch = s.getAllMatches().get(s.getAllMatches().size()-1);
        String expectedDate = DateHelper.getSqlDate(lastMatch.getKickoffTime());
        String actualDate = DS_Get.getLastCompletedMatchInLeague(l);
        Assert.assertEquals(expectedDate, actualDate);
    }

    @Test
    public void canGetPreviousMatchesBetweenTeams() {
        GenerateData data = addBulkData(true);
        League l = data.getLeagues().get(0);
        String seasonKey = "19";
        Season s = l.getSeason(Integer.parseInt(seasonKey));
        Match m = s.getAllMatches().get(0);
        Team home = m.getHomeTeam();
        Team away = m.getAwayTeam();
        MatchToPredict mtp = new MatchToPredict(home.getTeamName(), away.getTeamName(), seasonKey, l.getName(), DateHelper.getSqlDate(new Date()), 1, 1);
        ArrayList<MatchToPredict> matches = new ArrayList<>(Arrays.asList(mtp));
        //default matches from GenerateData should be 1 per pair of teams
        ArrayList<HistoricMatchDbData> pastMatches = DS_Get.getMatchesBetweenTeams(l.getName(), matches,5);
        Assert.assertEquals(1, pastMatches.size());
        //add new match with the home team now playing away (vice versa). Should now have 2 games total, but only 1 venue specific
        s.addNewMatch(new Match(away, home, new Date()));
        DS_Insert.writeLeagueToDb(l);
        pastMatches = DS_Get.getMatchesBetweenTeams(l.getName(), matches,5);
        Assert.assertEquals(2, pastMatches.size());
        pastMatches = DS_Get.getMatchesBetweenTeams(l.getName(), matches,5);
        Assert.assertEquals(1, pastMatches.size());
    }

    @Test
    public void canGetOutAllDataNeededToPredict() {
        GenerateData data = addBulkData(true);
        League l = data.getLeagues().get(0);
        Season s = l.getSeason(19);
        ArrayList<PlayerMatchDbData> pmdbDataArr = DS_Get.getLeagueData(l.getName(), 19);
        if (pmdbDataArr == null) {
            fail();
        }
        for (PlayerMatchDbData pmdbData: pmdbDataArr) {
            //match stats
            Assert.assertEquals(HOMESCORE, pmdbData.getHomeScore());
            Assert.assertEquals(AWAYSCORE, pmdbData.getAwayScore());
            Assert.assertEquals(HOMEXG, pmdbData.getHomeXGF(), 0.001);
            Assert.assertEquals(AWAYXG, pmdbData.getAwayXGF(), 0.001);
            Assert.assertEquals(HOMEODDS, pmdbData.getHomeOdds(), 0.001);
            Assert.assertEquals(DRAWODDS, pmdbData.getDrawOdds(), 0.001);
            Assert.assertEquals(AWAYODDS, pmdbData.getAwayOdds(), 0.001);
            Assert.assertEquals(FIRSTSCORER, pmdbData.getFirstScorer());
            //player stats
            Assert.assertEquals(MINUTES, pmdbData.getMins());
            Assert.assertEquals(RATING, pmdbData.getRating(), 0.001);
            Assert.assertNotNull(pmdbData.getName());
        }
        Assert.assertEquals(s.getAllMatches().size()*NUMB_PLAYERS_PER_MATCH*2, pmdbDataArr.size());
    }
}
