package com.petermarshall.scrape.classes;

import com.petermarshall.DateHelper;
import com.petermarshall.database.datasource.DS_Get;
import com.petermarshall.database.datasource.DS_Main;
import com.petermarshall.database.datasource.DS_Update;
import com.petermarshall.scrape.SofaScore;
import com.petermarshall.scrape.Understat;
import com.petermarshall.database.datasource.DataSource;

import java.util.*;


/*
 * Class will contain seasons, which will contain all the info about the league in that season.
 */
public class League {
    private final String name;
    private HashMap<String, Season> seasons;
    private LeagueSeasonIds seasonIds;

    //constructor will create a league with a bunch of blank seasons from the leagueSeasonIds.
    public League(LeagueSeasonIds leagueSeasonIds) {
        this.name = leagueSeasonIds.name();
        this.seasons = new HashMap<>();
        this.seasonIds = leagueSeasonIds;

        Iterator seasonKeys = leagueSeasonIds.getSeasonIds().keySet().iterator();
        while (seasonKeys.hasNext()) {
            String seasonYear = (String) seasonKeys.next();
            this.seasons.put(seasonYear, new Season(seasonYear));
        }
    }

    //SCRAPERS

    /*
     * Goes through each season to add in base game stats from understat.
     * Then goes through each season and gets more detailed stats from Sofascore for each game.
     */
    public void scrapeEverything() {
        Understat.addLeaguesGames(this); //iterates through the seasons in method

        Iterator<Season> seasonIterator = this.seasons.values().iterator();
        while (seasonIterator.hasNext()) {
            scrapeSeason(seasonIterator.next());
        }
    }

    /*
     * To be called when we go over to a new season to add all the seasons games to the database.
     */
    public void scrapeOneSeason(int seasonStart) {
        Understat.addSeasonsGames(this, seasonStart, null, null);

        scrapeSeason(this.getSeason(seasonStart));
    }

    private void scrapeSeason(Season currSeason) {
        Set<Integer> allGameIds = SofaScore.getGamesOfLeaguesSeason(this.seasonIds.getSofaScoreLeagueName(), this.seasonIds.getLeagueId(),
                                                                    this.seasonIds.getLeaguesSeasonId(currSeason.getSeasonKey()), null, null, currSeason);

        System.out.println("For " + currSeason.getSeasonKey() + ", we have " + allGameIds.size() + "ids");
        allGameIds.forEach(gameId -> {
            SofaScore.addInfoToGame(currSeason, gameId);
        });
    }


    /*
     * Method will scrape results from every game AFTER the last game played (not equals to).
     *
     * NOTE:
     * Will only scrape in results from yesterdays games. SofaScore scraped dates only give the day of the game, so if we tried to scrape in played games today,
     * the app would take all games to be played today and have no way of knowing if they have been played or not from sofascore.
     * For this reason, we only scrape yesterdays games as we know they will all have been played.
     *
     * TODO RECENT:
     * Get out all previous games of the season that do not have final results before scrape time and look for those instead of just
     * looking at games after a certain time.
     * Would need to have an extra field called postponed.
     */
    public void scrapePlayedGames() {
        int currSeasonKey = getCurrentSeasonStartYear();
        Season currSeason = this.getSeason(currSeasonKey);

        DS_Main.openConnection();
        DS_Main.initDB();

        String lastMatchPlayed = DS_Get.getMostRecentMatchInLeague(this);
        Date lastMatchDate = DateHelper.createDateFromSQL(lastMatchPlayed);
        Date beginningOfLastMatchDate = DateHelper.setTimeOfDate(lastMatchDate, 0, 0, 0);

        Date yesterday = DateHelper.subtract1DayFromDate(new Date());
        Date lastMinuteOfYesterday = DateHelper.setTimeOfDate(yesterday, 23, 59, 59);

        System.out.println("lastMatchDate: " + lastMatchDate);

        Understat.addSeasonsGames(this, currSeasonKey, beginningOfLastMatchDate, lastMinuteOfYesterday);

        Set<Integer> allGameIds = SofaScore.getGamesOfLeaguesSeason(this.seasonIds.getSofaScoreLeagueName(), this.seasonIds.getLeagueId(),
                                                                    this.seasonIds.getLeaguesSeasonId(currSeason.getSeasonKey()), beginningOfLastMatchDate, lastMinuteOfYesterday, currSeason);

        System.out.println("For " + currSeason.getSeasonKey() + " in " + name +", we have " + allGameIds.size() + "new ids");
        allGameIds.forEach(gameId -> {
            SofaScore.addInfoToGame(currSeason, gameId);
        });

        DS_Update.updateGamesInDB(this, currSeason);
        DS_Main.closeConnection();
    }




    //GETTERS

    public Season getSeason(String seasonYear) {
        return this.seasons.getOrDefault(seasonYear, null);
    }

    public Season getSeason(int startYear) {
        if (startYear > 99 || startYear < 10) throw new RuntimeException("league's getSeason method can only take an int of 2 digits");
        else return this.seasons.getOrDefault(startYear + "-" + (startYear+1), null);
    }

    public String getName() {
        return name;
    }

    public ArrayList<Season> getAllSeasons() {
        return new ArrayList<>(seasons.values());
    }

    public LeagueSeasonIds getSeasonIds() {
        return seasonIds;
    }

    /*
     * Returns the 2 digit year of the start of the earliest season. For example, Bundesliga ratings started in 16-17,
     * so the method would return 16.
     */
    public int getStartYearFirstSeasonAvailable() {
        return seasonIds.getFirstSeasonAvailable();
    }


    /*
     * Returns the start year of the curent season
     */
    public int getCurrentSeasonStartYear() {
        int highestNumb = -1;
        for (String key: this.seasons.keySet()) {
            int seasonStart = Integer.parseInt(key.substring(0,2));
            if (seasonStart > highestNumb) highestNumb = seasonStart;
        }

        return highestNumb;
    }

}














