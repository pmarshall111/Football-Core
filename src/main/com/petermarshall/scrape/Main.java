package com.petermarshall.scrape;

import com.petermarshall.database.datasource.DataSource;
import com.petermarshall.scrape.classes.League;
import com.petermarshall.scrape.classes.LeagueSeasonIds;

public class Main {
    /*
     * Method scrapes any games of the current season that do not have stats in the database. Only gets games from the day before and back.
     *
     * Gets all stats and adds them to the database.
     */
    public static void scrapeRecentlyPlayedMatches() {
        for (LeagueSeasonIds leagueSeasonIds: LeagueSeasonIds.values()) {
            League league = new League(leagueSeasonIds);

            league.scrapePlayedGames();
        }
    }

    /*
     * Method will create the database and go through adding all the games etc to the database.
     *
     * TODO:
     * Process will absolutely take some time (maybe even a few days of computing time, as all my writes to the database right now are in series rather than doing bulk writes)
     */
    public static void scrapeEverythingIntoDbFirstTime() {
        LeagueSeasonIds[] leagueIds = LeagueSeasonIds.values();

        for (int i = 0; i<leagueIds.length; i++) {

            League league = new League(leagueIds[i]);
            System.out.println("Starting to scrape league " + (i+1) + " out of " + leagueIds.length + ". Current league: " + league.getName());
            league.scrapeEverything();

            DataSource.openConnection();
            DataSource.initDB();
            System.out.println("Scraped everything for " + league.getName() + ". Commencing write to database...");
            DataSource.writeLeagueToDb(league);
            DataSource.closeConnection();

            System.out.println("Completed writing to databse!");

        }
    }

    /*
     *TODO
     * Method will check to see if leagueSeasonIds has new info to create a new season. Can do this by checking the database if the most recent season we have in there corresponds to the most
     * recent in leagueSeasonIds.
      * If it does, then we can scrape in all the games.
     */
    public static void scrapeInNewSeasons() {

    }

    public static void main(String[] args) {
        //scrapeRecentlyPlayedMatches();
    }
}
