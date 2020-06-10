package com.petermarshall.scrape;

import com.petermarshall.database.datasource.DS_Insert;
import com.petermarshall.database.datasource.DS_Main;
import com.petermarshall.scrape.classes.League;
import com.petermarshall.scrape.classes.LeagueSeasonIds;

public class Scrape {
    /*
     * Method scrapes any games of the current season that do not have stats in the database. Only gets games from the day before and back.
     * Gets all stats and adds them to the database.
     */
    public static void scrapeRecentlyPlayedMatches() {
        for (LeagueSeasonIds leagueSeasonIds: LeagueSeasonIds.values()) {
            League league = new League(leagueSeasonIds);
            league.scrapeAndSavePlayedGames();
        }
    }

    public static void scrapeEverythingIntoDbFirstTime() {
        LeagueSeasonIds[] leagueIds = LeagueSeasonIds.values();
        for (int i = 0; i<leagueIds.length; i++) {
            League league = new League(leagueIds[i]);
            System.out.println("Starting to scrape league " + (i+1) + " out of " + leagueIds.length + ". Current league: " + league.getName());
            league.scrapeEverything();
            writeToDb(league);
        }
    }

    public static void scrapeInOneSeason(int seasonYearStart) {
        for (LeagueSeasonIds leagueSeasonIds: LeagueSeasonIds.values()) {
            League league = new League(leagueSeasonIds);
            league.scrapeOneSeason(seasonYearStart);
            writeToDb(league);
        }
    }

    public static void scrapeOneSeasonOfOneLeague(int seasonYearStart, LeagueSeasonIds leagueSeasonIds) {
        League league = new League(leagueSeasonIds);
        league.scrapeOneSeason(seasonYearStart);
        writeToDb(league);
    }

    private static void writeToDb(League league) {
        DS_Main.openProductionConnection();
        DS_Main.initDB();
        System.out.println("Scraped everything for " + league.getName() + ". Commencing write to database...");
        DS_Insert.writeLeagueToDb(league);
        DS_Main.closeConnection();
        System.out.println("Completed writing to databse!");
    }

    public static void main(String[] args) {
//        scrapeOneSeasonOfOneLeague(18, LeagueSeasonIds.EPL);
//        scrapeOneSeasonOfOneLeague(18, LeagueSeasonIds.LA_LIGA);
//        scrapeOneSeasonOfOneLeague(18, LeagueSeasonIds.SERIE_A);
//        scrapeOneSeasonOfOneLeague(18, LeagueSeasonIds.LIGUE_1);
//        scrapeOneSeasonOfOneLeague(18, LeagueSeasonIds.BUNDESLIGA);
        scrapeOneSeasonOfOneLeague(18, LeagueSeasonIds.RUSSIA);


        //NOTE: COULD NOT GET ANY DATA FOR RUSSIA!
        //Might need to port it over from the previous database somehow
        //Happened again for russia year 17...


        //scrapeRecentlyPlayedMatches();
//        scrapeEverythingIntoDbFirstTime();
//        System.out.println("no more errors");
//        League league = new League(LeagueSeasonIds.EPL);
//        league.scrapeEverything();
//        DS_Main.openProductionConnection();
//        DS_Main.initDB();
//        System.out.println("Scraped everything for " + league.getName() + ". Commencing write to database...");
//        DS_Insert.writeLeagueToDb(league);
//        DS_Main.closeConnection();
//        System.out.println("Completed writing to databse!");
    }
}
