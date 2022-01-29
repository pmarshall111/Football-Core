package com.petermarshall.taskScheduling;

import com.petermarshall.DateHelper;
import com.petermarshall.database.datasource.DS_Get;
import com.petermarshall.database.datasource.DS_Main;
import com.petermarshall.database.datasource.DS_Update;
import com.petermarshall.scrape.SofaScore;
import com.petermarshall.scrape.Understat;
import com.petermarshall.scrape.classes.League;
import com.petermarshall.scrape.classes.LeagueIdsAndData;
import com.petermarshall.scrape.classes.Match;
import com.petermarshall.scrape.classes.Season;

import java.lang.reflect.Array;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

public class UpdatePipeline {
    public static void main(String[] args) {
        updatePlayedGames(true);
    }

    /*
     * NOTE: Will only update games that happened at least 4 hours ago.
     */
    public static void updatePlayedGames(boolean closeConnection) {
        DS_Main.openProductionConnection();
        DS_Main.initDB();
        updatePostponedGames();
        HashMap<League, String> leaguesToUpdate = DS_Get.getLeaguesToUpdate();
        if (leaguesToUpdate.keySet().size() == 0) {
            System.out.println("No games to update! - " + new Date());
            return;
        }
        Iterator<League> iter = leaguesToUpdate.keySet().iterator();
        while (iter.hasNext()) {
            League l = iter.next();
            String sqlDateOfEarliestGameToUpdate = leaguesToUpdate.get(l);
            int currSeasonKey = l.getCurrentSeasonStartYear();
            Season currSeason = l.getSeason(currSeasonKey);
            Date earliestMatchDate = DateHelper.createDateFromSQL(sqlDateOfEarliestGameToUpdate);
            Date earliestMatchWithBuffer = DateHelper.subtractXDaysFromDate(earliestMatchDate,1); // buffer to account for TV rescheduling
            Date fourHoursAgo = DateHelper.subtractXminsFromDate(new Date(),240);
            Understat.addSeasonsGames(l, currSeasonKey, null, null);
            Set<Integer> allGameIds = SofaScore.getGamesOfLeaguesSeason(l.getIdsAndData().getSofaScoreLeagueName(), l.getIdsAndData().getLeagueId(),
                    l.getIdsAndData().getLeaguesSeasonId(currSeason.getSeasonKey()), earliestMatchWithBuffer, fourHoursAgo, currSeason);
            System.out.println("For " + currSeason.getSeasonKey() + " in " + l.getName() +", we have " + allGameIds.size() + "new ids");
            AtomicInteger gamesScraped = new AtomicInteger();
            allGameIds.forEach(gameId -> {
                SofaScore.addInfoToGame(currSeason, gameId);
                System.out.println("Scraped " + gamesScraped.incrementAndGet() + " games");
            });
            DS_Update.updateGamesInDB(l, currSeason, earliestMatchWithBuffer);
        }
        if (closeConnection) {
            DS_Main.closeConnection();
        }
    }

    private static void updatePostponedGames() {
        HashMap<String, ArrayList<Integer>> postponedIds = DS_Get.getPostponedGames();
        Iterator<String> iter = postponedIds.keySet().iterator();
        while (iter.hasNext()) {
            String leagueName = iter.next();
            League l = new League(LeagueIdsAndData.valueOf(leagueName));
            Understat.addSeasonsGames(l, l.getCurrentSeasonStartYear(), null, null);
            ArrayList<Integer> leaguePostponedIds = postponedIds.get(leagueName);
            Season currSeason = l.getSeason(l.getCurrentSeasonStartYear());
            ArrayList<Match> postponedMatches = new ArrayList<>();
            leaguePostponedIds.forEach(gameId -> {
                Match match = SofaScore.addInfoToGame(currSeason, gameId);
                postponedMatches.add(match);
            });
            DS_Update.updatePostponedMatches(l, currSeason, postponedMatches);
        }
    }
}
