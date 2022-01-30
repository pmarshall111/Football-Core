package com.footballbettingcore.scrape;

import com.footballbettingcore.database.datasource.DS_Insert;
import com.footballbettingcore.database.datasource.DS_Main;
import com.footballbettingcore.mail.SendEmail;
import com.footballbettingcore.scrape.classes.League;
import com.footballbettingcore.scrape.classes.LeagueIdsAndData;

/*
 * This class is used to add a new season to the database.
 * First the IDs for the new seasons must be retrieved from the Sofascore website, then the addNewSeasons method can be called for
 * each new season.
 */
public class AddNewSeason {
    //to be scheduled yearly in august.
    public static void addIdsReminder() {
        SendEmail.sendOutEmail("Pls add season ids", "\n___________________________________________________\n\n" +
                "It's now the summer and time to add in new season Ids for sofascore." +
                "You may also have to add some new team name transitions in the Team.makeTeamNamesCompatible()." +
                "\n\nThis does not need to be done now, but be aware that some errors may be thrown by new teams having different names in SofaScore and Understat." +
                "\n\n___________________________________________________\n");
    }

    public static void main(String[] args) {
        League l = new League(LeagueIdsAndData.RUSSIA);
        l.scrapeOneSeason(21);
        DS_Main.openProductionConnection();
        DS_Main.initDB();
        System.out.println("Scraped everything for " + l.getName() + ". Commencing write to database...");
        DS_Insert.writeLeagueToDb(l);
        DS_Main.closeConnection();
    }
}
