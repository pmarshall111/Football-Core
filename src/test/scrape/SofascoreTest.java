package scrape;

import com.petermarshall.DateHelper;
import com.petermarshall.scrape.SofaScore;
import com.petermarshall.scrape.Understat;
import com.petermarshall.scrape.classes.League;
import com.petermarshall.scrape.classes.Match;
import com.petermarshall.scrape.classes.Season;
import static com.petermarshall.scrape.classes.LeagueIdsAndData.EPL;

import com.petermarshall.scrape.classes.Team;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Date;
import java.util.Set;


public class SofascoreTest {
    private static League epl;

    @BeforeClass
    public static void scrapeGames() {
        //thinking we may not need to test with real data.
//        epl = new League(LeagueIdsAndData.EPL);
//        Understat.addLeaguesGames(epl);
    }

    //possibly will need a test to look at what it does with postponed matches.
    //also should move the scrape into the beforeClass method.
    @Test
    public void canGetIdsForLeagueSeason() {
        String seasonYear = "19-20";
        int seasonId = EPL.getLeaguesSeasonId(seasonYear);
        int leagueId = EPL.getLeagueId();
        String leagueName = EPL.getSofaScoreLeagueName();
        League epl = new League(EPL);
        Season season = epl.getSeason(seasonYear);

        //add a known game from the season
        Team manUnited = new Team("Manchester United");
        Team liverpool = new Team("Liverpool");
        Date kickOff = DateHelper.createDateyyyyMMdd("2020", "01", "19");
        Match lfcHome = new Match(liverpool, manUnited, kickOff);
        season.addNewMatch(lfcHome);

        //scrape from sofascore
        Set<Integer> ids = SofaScore.getGamesOfLeaguesSeason(leagueName, leagueId, seasonId, null, null, season);
        Assert.assertNotNull(ids);
        Assert.assertEquals(380, ids.size()); //expect an id for every game of the season

        //check id is added to known game
        Assert.assertTrue(lfcHome.getSofaScoreGameId() > 0);
        Assert.assertEquals(8243623, lfcHome.getSofaScoreGameId());
        Assert.assertTrue(ids.contains(lfcHome.getSofaScoreGameId()));
    }
}
