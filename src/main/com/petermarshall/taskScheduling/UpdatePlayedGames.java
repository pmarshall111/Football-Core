package com.petermarshall.taskScheduling;

import com.petermarshall.scrape.Scrape;
import com.petermarshall.scrape.classes.League;
import com.petermarshall.scrape.classes.LeagueIdsAndData;

public class UpdatePlayedGames {
    public static void main(String[] args) {
        System.out.println("Storing results and ratings of played matches into the database...\n");
        //happy that this is working as I'd like.
        Scrape.scrapeRecentlyPlayedMatches();
//        scrapeEPL();
    }

    public static void scrapeEPL() {
        League EPL = new League(LeagueIdsAndData.EPL);
        EPL.scrapeAndSavePlayedGames();
    }
}
