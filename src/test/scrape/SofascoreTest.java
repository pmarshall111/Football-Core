package scrape;

import com.footballbettingcore.scrape.Understat;
import com.footballbettingcore.scrape.classes.*;
import com.footballbettingcore.database.FirstScorer;
import com.footballbettingcore.scrape.SofaScore;

import static org.junit.jupiter.api.Assertions.*;

import org.apache.commons.io.IOUtils;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.*;


public class SofascoreTest {
    private static Season s;

    @BeforeEach
    public void setup() throws IOException {
        // Sofascore scraper requires games already to be set up in the season. Understat does this.
        FileInputStream fis = new FileInputStream("src/test/scrape/Understat.xml");
        String xml = IOUtils.toString(fis, StandardCharsets.UTF_8);
        UnderstatData understatData = Understat.getSeasonsData(xml, 21);
        League l = new League(LeagueIdsAndData.EPL);
        s = l.getSeason(21);
        Understat.addMatchesToSeason(s, understatData.getDatesData());
        Understat.addXgToMatches(s, understatData.getTeamsData());
    }

    @Test
    public void canAddDataToMatches() throws Exception {
        // given
        FileInputStream fis = new FileInputStream("src/test/scrape/SofascoreEvents.json");
        String sofascoreEvents = IOUtils.toString(fis, StandardCharsets.UTF_8);
        JSONParser parser = new JSONParser();
        JSONObject json = (JSONObject) parser.parse(sofascoreEvents);
        HashSet<Integer> gameIds = new HashSet<>();

        // when
        Date earliestDate = new GregorianCalendar(2022, Calendar.JANUARY, 16, 14,0).getTime(); // southampton brentford
        Date latestDate = new GregorianCalendar(2022, Calendar.JANUARY, 16, 15,0).getTime(); //lfc brentford
        SofaScore.addDataToMatches(json, s, earliestDate, latestDate, gameIds, true);
        Match postponedMatch = s.getTeam("Tottenham").getMatchFromAwayTeamName("Arsenal");
        Match normalMatch = s.getTeam("Liverpool").getMatchFromAwayTeamName("Brentford");

        // then
        assertTrue(postponedMatch.isPostponed());
        assertEquals(9576450, postponedMatch.getSofaScoreGameId());
        assertEquals(new GregorianCalendar(2022, Calendar.JANUARY, 16, 16, 30).getTime(), postponedMatch.getKickoffTime());
        assertFalse(normalMatch.isPostponed());
        assertEquals(9576342, normalMatch.getSofaScoreGameId());
        assertEquals(new GregorianCalendar(2022, Calendar.JANUARY, 16, 14, 0).getTime(), normalMatch.getKickoffTime());
        assertEquals(2, gameIds.size()); // only matches between the earliest & latest dates should be added to gameIds
    }

    @Test
    public void canGetOddsForMatch() throws Exception {
        FileInputStream fis = new FileInputStream("src/test/scrape/SofascoreOdds.json");
        String sofascoreEvents = IOUtils.toString(fis, StandardCharsets.UTF_8);
        ArrayList<Double> odds = SofaScore.getOdds(sofascoreEvents);

        assertEquals(3, odds.size());
        assertEquals(1.4, odds.get(0));
        assertEquals(4.75, odds.get(1));
        assertEquals(8, odds.get(2));
    }

    @Test
    public void canAddPlayerRatingsToMatch() throws Exception {
        // given
        FileInputStream fis = new FileInputStream("src/test/scrape/SofascoreRatings.json");
        String sofascoreRatings = IOUtils.toString(fis, StandardCharsets.UTF_8);
        Match m = s.getTeam("Manchester City").getMatchFromAwayTeamName("Tottenham");
        assertEquals(0, m.getHomePlayerRatings().size());
        assertEquals(0, m.getAwayPlayerRatings().size());

        // when
        SofaScore.addPlayerRatingsToGame(m, sofascoreRatings);
        PlayerRating deBruyne = m.getHomePlayerRatings().get("Kevin De Bruyne");
        PlayerRating kane = m.getAwayPlayerRatings().get("Harry Kane");

        // then
        assertEquals(12, m.getHomePlayerRatings().size());
        assertEquals(90, deBruyne.getMinutesPlayed());
        assertEquals("M", deBruyne.getPosition());
        assertEquals(7, deBruyne.getRating());

        assertEquals(13, m.getAwayPlayerRatings().size());
        assertEquals(90, kane.getMinutesPlayed());
        assertEquals("F", kane.getPosition());
        assertEquals(8.5, kane.getRating());
    }

    @Test
    public void canAddFirstGoalScorerToGame() throws Exception {
        // given
        FileInputStream fis = new FileInputStream("src/test/scrape/SofascoreIncidents.json");
        String sofascoreIncidents = IOUtils.toString(fis, StandardCharsets.UTF_8);
        Match m = s.getTeam("Manchester City").getMatchFromAwayTeamName("Tottenham");

        // when
        SofaScore.addFirstGoalScorer(m, sofascoreIncidents);

        // then
        assertEquals(FirstScorer.AWAY_FIRST, m.getFirstScorer());
    }

    @Test
    public void canAddMatchStatistics() throws Exception {
        FileInputStream fis = new FileInputStream("src/test/scrape/SofascoreStatistics.json");
        String sofascoreStatistics = IOUtils.toString(fis, StandardCharsets.UTF_8);
        Match m = s.getTeam("Manchester City").getMatchFromAwayTeamName("Tottenham");

        SofaScore.addMatchStatistics(m, sofascoreStatistics);

        assertEquals(71, m.getHomePossession());
        assertEquals(29, m.getAwayPossession());
        assertEquals(21, m.getHomeShots());
        assertEquals(4, m.getHomeShotsOnTarget());
        assertEquals(6, m.getAwayShots());
        assertEquals(5, m.getAwayShotsOnTarget());
    }
}