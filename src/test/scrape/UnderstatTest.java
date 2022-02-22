package scrape;

import com.footballbettingcore.scrape.Understat;
import com.footballbettingcore.scrape.classes.*;
import org.apache.commons.io.IOUtils;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Calendar;
import java.util.GregorianCalendar;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class UnderstatTest {
    private static String xml;

    @BeforeAll
    public static void setup() throws IOException {
        FileInputStream fis = new FileInputStream("src/test/scrape/Understat.xml");
        xml = IOUtils.toString(fis, StandardCharsets.UTF_8);
    }

    @Test
    public void canGetDataFromUnderstatXml() throws IOException {
        UnderstatData understatData = Understat.getSeasonsData(xml, 15);
        assertNotNull(understatData.getDatesData());
        assertNotNull(understatData.getTeamsData());
        assertEquals(20, understatData.getTeamsData().size());
        assertEquals(380, understatData.getDatesData().size());
    }

    @Test
    public void canAddMatchesToLeague() {
        //given
        UnderstatData understatData = Understat.getSeasonsData(xml, 15);
        JSONObject firstMatch = (JSONObject) understatData.getDatesData().get(0);
        JSONArray justOneMatch = new JSONArray();
        justOneMatch.add(firstMatch);
        League l = new League(LeagueIdsAndData.EPL);
        Season s = l.getSeason(21);

        //when
        Understat.addMatchesToSeason(s, justOneMatch);
        Match m = s.getAllMatches().get(0);

        //then
        assertNotNull(s.getAllMatches());
        assertEquals(1, s.getAllMatches().size());
        assertNotNull(s.getAllTeams());
        assertEquals(2, s.getAllTeams().size());
        assertEquals(m.getHomeTeam().getTeamName(), ((JSONObject)firstMatch.get("h")).get("title"));
        assertEquals(m.getAwayTeam().getTeamName(), ((JSONObject)firstMatch.get("a")).get("title"));
        assertEquals(m.getHomeScore(), Integer.parseInt((String) ((JSONObject)firstMatch.get("goals")).get("h")));
        assertEquals(m.getAwayScore(), Integer.parseInt((String) ((JSONObject)firstMatch.get("goals")).get("a")));
        assertEquals(-1, m.getHomeXGF());
        assertEquals(-1, m.getAwayXGF());
    }

    @Test
    public void canAddXgToMatches() {
        // given
        League l = new League(LeagueIdsAndData.EPL);
        Season s = l.getSeason(15);
        UnderstatData understatData = Understat.getSeasonsData(xml, 15);

        // when
        Understat.addMatchesToSeason(s, understatData.getDatesData());
        Understat.addXgToMatches(s, understatData.getTeamsData());
        Match m = s.getTeam("Manchester City").getAllMatches().get(
                new GregorianCalendar(2022, Calendar.FEBRUARY, 19).getTime()
        );

        // then
        assertNotNull(s.getAllMatches());
        assertEquals(380, s.getAllMatches().size());
        assertNotNull(s.getAllTeams());
        assertEquals(20, s.getAllTeams().size());
        assertEquals("Manchester City", m.getHomeTeam().getTeamName());
        assertEquals("Tottenham", m.getAwayTeam().getTeamName());
        assertEquals(2, m.getHomeScore());
        assertEquals(3, m.getAwayScore());
        assertEquals(1.55656, m.getHomeXGF());
        assertEquals(2.00235, m.getAwayXGF());
    }
}
