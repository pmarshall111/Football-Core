package com.petermarshall.scrape.classes;

import com.petermarshall.DateHelper;

import java.util.Date;
import java.util.HashMap;


/*
 * Contains the Id's needed to scrape from SofaScore. Id's will be equal to -1 if during that season SofaScore did
 * not have the ratings available for that league season. The earliest season is 2014-15, and the first int[] Id
 * corresponds to that season. Later season id's follow.
 * NOTE: once added new season Ids here, the seasons must be scraped to the database by creating a call to Scrape.scrapeOneSeason();
 */
public enum LeagueSeasonIds {
    EPL("EPL", "Premier League", "England", 17, new int[]{-1, 10356, 11733, 13380, 17359, 23776}),
    LA_LIGA( "La_liga", "LaLiga", "Spain", 8, new int[]{-1, 10495, 11906, 13662, 18020, 24127}),
    BUNDESLIGA("Bundesliga", "Bundesliga", "Germany", 35, new int[]{-1, -1, 11818, 13477, 17597, 23538}),
    SERIE_A("Serie_A", "Serie A", "Italy", 23, new int[]{-1, 10596, 11966, 13768, 17932, 24644}),
    LIGUE_1("Ligue_1", "Ligue 1", "France", 34, new int[]{-1, 10373, 11648, 13384, 17279, 23872}),
    RUSSIA("RFPL", "Premier Liga", "Russia", 203, new int[]{-1, -1, 11868, 13387, 17753, 23682});

    private static final int MOST_RECENT_SEASON_END;

    static {
        Date currentDate = new Date();
        MOST_RECENT_SEASON_END = DateHelper.getEndingYearForCurrentSeason(currentDate);
    }

    private final int leagueId;
    private final HashMap<String, Integer> seasonIds;
    private final String understatUrl;
    private final String sofaScoreLeagueName;
    private final String sofaScoreCountryName;

    LeagueSeasonIds(String understatUrl, String sofaScoreLeagueName, String sofaScoreCountryName, int LeagueId, int[] ids) {
        this.understatUrl = understatUrl;
        this.sofaScoreLeagueName = sofaScoreLeagueName;
        this.leagueId = LeagueId;
        this.sofaScoreCountryName = sofaScoreCountryName;

        this.seasonIds = new HashMap<>();
        for (int i = 0; i<ids.length; i++) {
            if (ids[i] == -1) continue;
            else this.seasonIds.put(14+i + "-" + (15+i), ids[i]);
        }
    }



    //GETTERS

    public String getUnderstatUrl() {
        return understatUrl;
    }

    public String getSofaScoreLeagueName() {
        return sofaScoreLeagueName;
    }

    public String getSofaScoreCountryName() {
        return sofaScoreCountryName;
    }

    public int getLeagueId() {
        return leagueId;
    }

    /*
     * Takes a string in the form of 14-15, or 15-16 corresponding to 2014 to 2015 season, or 2015 to 2016 season.
     * Earliest season is 14-15. Default behaviour if key not found is to return -1.
     */
    public int getLeaguesSeasonId(String season) {
        return seasonIds.getOrDefault(season, -1);
    }

    /*
     * Returns the 2 digit year of the start of the earliest season. For example, Bundesliga ratings started in 16-17,
     * so the method would return 16.
     */
    public int getFirstSeasonAvailable() {
        return MOST_RECENT_SEASON_END - this.seasonIds.size();
    }

    public HashMap<String, Integer> getSeasonIds() {
        return seasonIds;
    }
}