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
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

public class UpdatePipeline {
    private static final Logger logger = LogManager.getLogger(UpdatePipeline.class);

    public static void main(String[] args) {
        updatePlayedGames(true);
    }

    /*
     * NOTE: Will only update games that happened at least 4 hours ago.
     */
    public static void updatePlayedGames(boolean closeConnection) {
        DS_Main.openProductionConnection();
        DS_Main.initDB();
        HashMap<League, String> leaguesToUpdate = DS_Get.getLeaguesToUpdate();
        if (leaguesToUpdate.keySet().size() == 0) {
            logger.info("No games to update");
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
            Set<Integer> allGameIds = SofaScore.getSofascoreIdsAndAddBaseDataToMatches(l.getIdsAndData().getSofaScoreLeagueName(), l.getIdsAndData().getLeagueId(),
                    l.getIdsAndData().getLeaguesSeasonId(currSeason.getSeasonKey()), earliestMatchWithBuffer, fourHoursAgo, currSeason);
            logger.info("For " + currSeason.getSeasonKey() + " in " + l.getName() +", we have " + allGameIds.size() + "new ids");
            AtomicInteger gamesScraped = new AtomicInteger();
            allGameIds.forEach(gameId -> {
                SofaScore.addInfoToGame(currSeason, gameId);
                logger.info("Scraped " + gamesScraped.incrementAndGet() + " games");
            });
            DS_Update.updateGamesInDB(l, currSeason, earliestMatchWithBuffer);
        }
        if (closeConnection) {
            DS_Main.closeConnection();
        }
    }
}
