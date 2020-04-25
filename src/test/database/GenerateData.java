package database;

import com.petermarshall.database.datasource.DS_Insert;
import com.petermarshall.scrape.classes.*;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

//Class will generate 2 unique leagues, having 2 seasons, having 10 unique teams and 5 unique matches, with 22 unique players for each match
//2 leagues, 40 teams, 20 matches, 440 players
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

    static GenerateData addBulkData(boolean addStatsToMatch) {
        GenerateData data = new GenerateData(addStatsToMatch);
        ArrayList<League> leagues = data.getLeagues();
        leagues.forEach(league -> {
            DS_Insert.writeLeagueToDb(league);
        });
        return data;
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
                Season newSeason = League.addASeason(league, year+"");
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
                    t1.addMatch(m);
                    t2.addMatch(m);
                }
            }
        });
    }

    private void addDataToMatch(Match m) {
        m.setAwayXGF(AWAYXG);
        m.setFirstScorer(1);
        m.setHomeXGF(HOMEXG);
        m.setHomeScore(HOMESCORE);
        m.setAwayScore(AWAYSCORE);
        m.setSofaScoreGameId(matches.size());
        ArrayList<Double> odds = new ArrayList<>();
        m.setHomeDrawAwayOdds(odds);
    }

    private void addPlayerRatingsToMatch(Match m, int seasonYear, int matchNumb, String leagueName) {
        HashMap<String, PlayerRating> homeRatings = new HashMap<>();
        HashMap<String, PlayerRating> awayRatings = new HashMap<>();
        for (int j = 0; j<11; j++) {
            String playerName = StringUtils.rightPad("home"+seasonYear+matchNumb+leagueName, j, "_");
            PlayerRating pr = new PlayerRating(MINUTES, RATING, playerName);
            this.playerRatings.add(pr);
            homeRatings.put(playerName, pr);
        }
        for (int j = 0; j<11; j++) {
            String playerName = StringUtils.rightPad("away"+seasonYear+matchNumb+leagueName, j, "_");
            PlayerRating pr = new PlayerRating(MINUTES, RATING, playerName);
            this.playerRatings.add(pr);
            awayRatings.put(playerName, pr);
        }
        m.setHomePlayerRatings(homeRatings);
        m.setAwayPlayerRatings(awayRatings);
    }

    private void generateLeagues() {
        League epl = new League(LeagueSeasonIds.EPL);
        League la_liga = new League(LeagueSeasonIds.LA_LIGA);
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
