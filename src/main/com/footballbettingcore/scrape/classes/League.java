package com.footballbettingcore.scrape.classes;

import com.footballbettingcore.scrape.SofaScore;
import com.footballbettingcore.scrape.Understat;

import java.util.*;

public class League {
    private final String name;
    private final HashMap<String, Season> seasons;
    private final LeagueIdsAndData idsAndData;

    //constructor will create a league and populate with a bunch of empty seasons from the leagueSeasonIds.
    public League(LeagueIdsAndData leagueIdsAndData) {
        this.name = leagueIdsAndData.name();
        this.seasons = new HashMap<>();
        this.idsAndData = leagueIdsAndData;
        for (String seasonYear : leagueIdsAndData.getSeasonIds().keySet()) {
            this.seasons.put(seasonYear, new Season(seasonYear));
        }
    }

    /*
     * GETTERS
     */
    public Season getSeason(String seasonYear) {
        return this.seasons.getOrDefault(seasonYear, null);
    }

    public Season getSeason(int startYear) {
        if (startYear > 99 || startYear < 10) throw new RuntimeException("league's getSeason method can only take an int of 2 digits");
        else return this.seasons.getOrDefault(Season.getSeasonKeyFromYearStart(startYear), null);
    }

    public String getName() {
        return name;
    }

    public ArrayList<Season> getAllSeasons() {
        return new ArrayList<>(seasons.values());
    }

    public LeagueIdsAndData getIdsAndData() {
        return idsAndData;
    }

    /*
     * Returns the 2 digit year of the start of the earliest season. For example, Bundesliga ratings started in 16-17,
     * so the method would return 16.
     */
    public int getStartYearFirstSeasonAvailable() {
        return idsAndData.getFirstSeasonAvailable();
    }

    public int getCurrentSeasonStartYear() {
        int highestNumb = -1;
        for (String key: this.seasons.keySet()) {
            int seasonStart = Integer.parseInt(key.substring(0,2));
            if (seasonStart > highestNumb) highestNumb = seasonStart;
        }
        return highestNumb;
    }


    //SCRAPERS
    /*
     * To be called when we go over to a new season to add all the seasons games to the database.
     */
    public void scrapeOneSeason(int seasonStart) {
        Understat.addSeasonsGames(this, seasonStart, null, null);
        scrapeSeason(this.getSeason(seasonStart));
    }

    private void scrapeSeason(Season currSeason) {
        Set<Integer> allGameIds = SofaScore.getSofascoreIdsAndAddBaseDataToMatches(this.idsAndData.getSofaScoreLeagueName(), this.idsAndData.getLeagueId(),
                this.idsAndData.getLeaguesSeasonId(currSeason.getSeasonKey()), null, null, currSeason);
        System.out.println("For " + currSeason.getSeasonKey() + ", we have " + allGameIds.size() + "ids");
        ArrayList<Integer> shuffledIds = new ArrayList<>(allGameIds);
        Collections.shuffle(shuffledIds); //to look less like a scraper that goes through all dates sequentially
        int added = 0;
        for (Integer id: shuffledIds) {
            SofaScore.addInfoToGame(currSeason, id);
            System.out.println(added++);
        }
    }

    public static ArrayList<League> getAllLeagues() {
        return new ArrayList<>(Arrays.asList(new League(LeagueIdsAndData.EPL), new League(LeagueIdsAndData.LA_LIGA), new League(LeagueIdsAndData.BUNDESLIGA),
                new League(LeagueIdsAndData.SERIE_A), new League(LeagueIdsAndData.LIGUE_1), new League(LeagueIdsAndData.RUSSIA)));
    }

    //Testing purposes only
    public static Season addASeason(League league, String year) {
        Season nSeason = new Season(year);
        league.seasons.put(year, nSeason);
        return nSeason;
    }
}