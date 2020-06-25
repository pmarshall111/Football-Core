package scrape;

import com.petermarshall.scrape.Understat;
import com.petermarshall.scrape.classes.*;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

public class UnderstatTest {
    private static League epl;

    @BeforeClass
    public static void scrapeGames() {
        UnderstatTest.epl = new League(LeagueIdsAndData.EPL);
        Understat.addLeaguesGames(epl);
    }

    @Test
    public void canCreateMatchesWithinSeasons() {
        ArrayList<Season> seasons = epl.getAllSeasons();
        seasons.forEach(season -> {
            Assert.assertTrue(season.hasMatches());
        });
    }

    @Test
    public void canFindAllMatchesInSeasons() {
        ArrayList<Season> seasons = epl.getAllSeasons();
        seasons.forEach(season -> {
            int teams = season.getAllTeams().size();
            int actualMatches = season.getAllMatches().size();
            int expectedMatches = teams*(teams-1);
            Assert.assertEquals(expectedMatches, actualMatches);
        });
    }

    @Test
    public void matchHasTeamNames() {
        ArrayList<Season> seasons = epl.getAllSeasons();
        seasons.forEach(season -> {
            HashMap<String, Team> allTeams = season.getAllTeams();
            ArrayList<Match> matches = season.getAllMatches();
            matches.forEach(match -> {
                String homeTeam = match.getHomeTeam().getTeamName();
                String awayTeam = match.getAwayTeam().getTeamName();
                Assert.assertNotEquals(homeTeam, awayTeam);
                Assert.assertTrue(allTeams.containsKey(homeTeam));
                Assert.assertTrue(allTeams.containsKey(awayTeam));
            });
        });
    }

    @Test
    public void matchInPastHasXGAndScore() {
        //currently fails due to the coronavirus with matches not being rearranged.
        ArrayList<Season> seasons = epl.getAllSeasons();
        for (Season s: seasons) {
            ArrayList<Match> matches = s.getAllMatches();
            for (Match match : matches) {
                if (match.getKickoffTime().after(new Date())) {
                    break;
                }
                Assert.assertTrue(match.getHomeScore() >= 0);
                Assert.assertTrue(match.getAwayScore() >= 0);
                Assert.assertTrue(match.getHomeXGF() >= 0);
                Assert.assertTrue(match.getAwayXGF() >= 0);
            }
        }
    }

    @Test
    public void matchInFutureNoXGOrScore() {
        ArrayList<Season> seasons = epl.getAllSeasons();
        seasons.forEach(season -> {
            HashMap<String, Team> allTeams = season.getAllTeams();
            ArrayList<Match> matches = season.getAllMatches();
            for (int i = matches.size()-1; i >= 0; i--) {
                Match m = matches.get(i);
                if (m.getKickoffTime().before(new Date())) {
                    break;
                }
                Assert.assertEquals(-1, m.getHomeScore());
                Assert.assertEquals(-1, m.getAwayScore());
                Assert.assertEquals(-1, m.getHomeXGF(), 0.9);
                Assert.assertEquals(-1, m.getAwayXGF(), 0.9);
            }
        });
    }
}
