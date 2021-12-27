package database;

import com.petermarshall.database.FirstScorer;
import com.petermarshall.database.datasource.DS_Insert;
import com.petermarshall.scrape.classes.*;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

import static com.petermarshall.database.datasource.DS_Main.TEST_CONNECTION_NAME;
import static com.petermarshall.database.datasource.DS_Main.connection;

//for 1 league: 2 seasons, 8 teams per season, 4 matches per season,  22 players per match
//totals: 2 leagues, 4 seasons, 32 teams, 16 matches, 352 players
//tests rely on there being 1 match between each team
public class GenerateData {
    private ArrayList<League> leagues;
    private ArrayList<Team> teams;
    private ArrayList<Match> matches;
    private ArrayList<PlayerRating> playerRatings;
    //match vals
    static final int HOMESCORE = 1;
    static final int AWAYSCORE = 2;
    static final double HOMEXG = 2.5;
    static final double AWAYXG = 3.5;
    static final double HOMEODDS = 1.2;
    static final double DRAWODDS = 2.4;
    static final double AWAYODDS = 3.6;
    static final int FIRSTSCORER = 1;
    //player vals
    static final int MINUTES = 90;
    static final double RATING = 8.7;

    static final int NUMB_PLAYERS_PER_MATCH = 11;

    public static GenerateData addBulkData(boolean addStatsToMatch) {
        GenerateData data = new GenerateData(addStatsToMatch);
        ArrayList<League> leagues = data.getLeagues();
        writeData(leagues);
        return data;
    }

    private static void writeData(ArrayList<League> leagues) {
        try {
            if (connection.getMetaData().getURL().equals(TEST_CONNECTION_NAME)) {
                leagues.forEach(DS_Insert::writeLeagueToDb);
            } else {
                throw new RuntimeException("Data not added!");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    public GenerateData(boolean addStatsToMatch) {
        this.leagues = new ArrayList<>();
        this.teams = new ArrayList<>();
        this.matches = new ArrayList<>();
        this.playerRatings = new ArrayList<>();

        generateLeagues();
        addDataToLeagues(addStatsToMatch);
    }

    private void addDataToLeagues(boolean addStatsToMatch) {
        leagues.forEach(league -> {
            for (int year = 19; year <= 20; year++) {
                Season newSeason = League.addASeason(league, year+"-"+(year+1));
                for (int i = 1; i < 5; i++) {
                    String homeTeam = "Home" + "_game" + i +league.getName() + year;
                    String awayTeam = "Away" + "_game" + i +league.getName() + year;
                    Team t1 = new Team(homeTeam);
                    Team t2 = new Team(awayTeam);
                    newSeason.addNewTeam(t1);
                    newSeason.addNewTeam(t2);
                    Match m = new Match(t1, t2, new Date());
                    if (addStatsToMatch) {
                        addDataToMatch(m);
                    }
                    addPlayerRatingsToMatch(m, year, i, league.getName());
                    this.matches.add(m);
                    newSeason.addNewMatch(m);
                }
            }
        });
    }

    private void addDataToMatch(Match m) {
        m.setHomeScore(HOMESCORE);
        m.setAwayScore(AWAYSCORE);
        m.setHomeXGF(HOMEXG);
        m.setAwayXGF(AWAYXG);
        m.setFirstScorer(FirstScorer.HOME_FIRST);
        m.setSofaScoreGameId(matches.size());
        ArrayList<Double> odds = new ArrayList<>();
        odds.add(HOMEODDS);
        odds.add(DRAWODDS);
        odds.add(AWAYODDS);
        m.setHomeDrawAwayOdds(odds);
    }

    void addPlayerRatingsToMatch(Match m, int seasonYear, int matchNumb, String leagueName) {
        HashMap<String, PlayerRating> homeRatings = new HashMap<>();
        HashMap<String, PlayerRating> awayRatings = new HashMap<>();
        for (int j = 0; j<NUMB_PLAYERS_PER_MATCH; j++) {
            String playerName = "home"+j+"match"+matchNumb+leagueName+seasonYear;
            PlayerRating pr = new PlayerRating(MINUTES, RATING, playerName, "D");
            this.playerRatings.add(pr);
            homeRatings.put(playerName, pr);
        }
        for (int j = 0; j<NUMB_PLAYERS_PER_MATCH; j++) {
            String playerName = "away"+j+"match"+matchNumb+leagueName+seasonYear;
            PlayerRating pr = new PlayerRating(MINUTES, RATING, playerName, "D");
            this.playerRatings.add(pr);
            awayRatings.put(playerName, pr);
        }
        m.setHomePlayerRatings(homeRatings);
        m.setAwayPlayerRatings(awayRatings);
    }

    private void generateLeagues() {
        League epl = new League(LeagueIdsAndData.EPL);
        League la_liga = new League(LeagueIdsAndData.LA_LIGA);
        leagues.add(epl);
        leagues.add(la_liga);
    }

    public ArrayList<League> getLeagues() {
        return leagues;
    }

    public ArrayList<Match> getMatches() {
        return matches;
    }

    public ArrayList<PlayerRating> getPlayerRatings() {
        return playerRatings;
    }

    public ArrayList<Team> getTeams() {
        return teams;
    }
}
