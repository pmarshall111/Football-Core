package com.petermarshall.taskScheduling;

import com.petermarshall.scrape.Main;

public class UpdatePlayedGames {
    public static void main(String[] args) {
        System.out.println("Storing results and ratings of played matches into the database...\n");
        //happy that this is working as I'd like.
        Main.scrapeRecentlyPlayedMatches();
    }
}
