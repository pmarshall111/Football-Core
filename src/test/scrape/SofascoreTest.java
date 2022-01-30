package scrape;

import com.petermarshall.ConvertOdds;
import com.petermarshall.DateHelper;
import com.petermarshall.database.FirstScorer;
import com.petermarshall.machineLearning.createData.classes.MatchToPredict;
import com.petermarshall.scrape.SofaScore;
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
    private static int seasonId;
    private static int leagueId;
    private static String leagueName;
    private static League epl;
    private static Season season;
    private static Set<Integer> ids;
    private static Match lfcHome;
    private static final int LFC_BURNLEY_MATCH_ID = 8692257;

    @BeforeClass
    public static void scrapeGames() {
        String seasonYear = "19-20";
        seasonId = EPL.getLeaguesSeasonId(seasonYear);
        leagueId = EPL.getLeagueId();
        leagueName = EPL.getSofaScoreLeagueName();
        epl = new League(EPL);
        season = epl.getSeason(seasonYear);

        //add a known game from the season
        Team burnley = new Team("Burnley");
        Team liverpool = new Team("Liverpool");
        Date kickOff = DateHelper.createDateyyyyMMdd("2020", "07", "11");
        lfcHome = new Match(liverpool, burnley, kickOff, 1, 1);
        season.addNewMatch(lfcHome);

        //get the IDs for the games (required to run before other tests)
        ids = SofaScore.getSofascoreIdsAndAddBaseDataToMatches(leagueName, leagueId, seasonId, null, null, season);
    }

    //possibly will need a test to look at what it does with postponed matches.
    @Test
    public void canGetIdsForLeagueSeason() {
        //scrape from sofascore
        Assert.assertNotNull(ids);
        Assert.assertEquals(380, ids.size()); //expect an id for every game of the season

        //check id is added to known game
        Assert.assertTrue(lfcHome.getSofaScoreGameId() > 0);
        Assert.assertEquals(LFC_BURNLEY_MATCH_ID, lfcHome.getSofaScoreGameId());
        Assert.assertTrue(ids.contains(lfcHome.getSofaScoreGameId()));
    }

    //adds odds, players ratings, first goalscorer.
    //executing as 1 integration test to save time.
    @Test
    public void canAddInfoToGame() {
        int gameId = lfcHome.getSofaScoreGameId();
        Assert.assertTrue(gameId > 0);

        SofaScore.addInfoToGame(season, gameId);
        //odds
        Assert.assertEquals(ConvertOdds.fromFractionToDecimal("1/5"), lfcHome.getHomeOdds(), 0.0001);
        Assert.assertEquals(ConvertOdds.fromFractionToDecimal("6/1"), lfcHome.getDrawOdds(), 0.0001);
        Assert.assertEquals(ConvertOdds.fromFractionToDecimal("12/1"), lfcHome.getAwayOdds(), 0.0001);
        //home player ratings
        Assert.assertNotNull(lfcHome.getHomePlayerRatings());
        Assert.assertTrue(lfcHome.getHomePlayerRatings().size() > 0);
        Assert.assertEquals(7.0, lfcHome.getHomePlayerRatings().get("Mohamed Salah").getRating(), 0.0001);
        Assert.assertEquals(90, lfcHome.getHomePlayerRatings().get("Roberto Firmino").getMinutesPlayed(), 0.0001);
        //away player ratings
        Assert.assertNotNull(lfcHome.getAwayPlayerRatings());
        Assert.assertTrue(lfcHome.getAwayPlayerRatings().size() > 0);
        Assert.assertEquals(6.4, lfcHome.getAwayPlayerRatings().get("Chris Wood").getRating(), 0.0001);
        Assert.assertEquals(65, lfcHome.getAwayPlayerRatings().get("Chris Wood").getMinutesPlayed(), 0.0001);
        //first goalscorer
        Assert.assertEquals(FirstScorer.HOME_FIRST.getSqlIntCode(), lfcHome.getFirstScorer().getSqlIntCode());
    }

    @Test
    public void canGetUpdatedKickoffTimes() {
        Date dateBST = DateHelper.createDateyyyyMMddHHmmss("2021", "05", "23", "16", "00", "00");
        Date dateGMT = DateHelper.createDateyyyyMMddHHmmss("2021", "05", "23", "15", "00", "00");
        ArrayList<Date> kickOffTimes = SofaScore.updateKickoffTimes(dateBST, false);
        Assert.assertTrue(kickOffTimes.size() > 0);
        Assert.assertTrue(kickOffTimes.contains(dateBST) || kickOffTimes.contains(dateGMT));
    }

    @Test
    public void canGetLineupsForAMatchToPredict() {
        MatchToPredict mtp = new MatchToPredict(lfcHome.getHomeTeam().getTeamName(), lfcHome.getAwayTeam().getTeamName(), "19-20",
                EPL.getSofaScoreLeagueName(), DateHelper.getSqlDate(lfcHome.getKickoffTime()),-1, LFC_BURNLEY_MATCH_ID);
        ArrayList<MatchToPredict> matches = new ArrayList<>();
        matches.add(mtp);
        SofaScore.addLineupsToGamesAboutToStart(matches);
        Assert.assertNotNull(mtp.getHomeTeamPlayers());
        Assert.assertNotNull(mtp.getAwayTeamPlayers());
        Assert.assertEquals(11, mtp.getHomeTeamPlayers().size());
        Assert.assertEquals(11, mtp.getAwayTeamPlayers().size());
        Assert.assertTrue(mtp.getHomeTeamPlayers().contains("Mohamed Salah"));
        Assert.assertTrue(mtp.getAwayTeamPlayers().contains("Chris Wood"));
    }
}