package com.petermarshall.taskScheduling;

import com.petermarshall.DateHelper;
import com.petermarshall.database.datasource.DS_Get;
import com.petermarshall.database.datasource.DS_Main;
import com.petermarshall.database.datasource.DS_Update;
import com.petermarshall.scrape.SofaScore;
import com.petermarshall.scrape.Understat;
import com.petermarshall.scrape.classes.League;
import com.petermarshall.scrape.classes.Season;

import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;

public class UpdatePipeline {
    public static void updateGames(HashMap<League, String> leaguesToUpdate) {
        DS_Main.openProductionConnection();
        DS_Main.initDB();
        Iterator<League> iter = leaguesToUpdate.keySet().iterator();
        while (iter.hasNext()) {
            League l = iter.next();
            String sqlDateOfEarliestGameToUpdate = leaguesToUpdate.get(l);
            int currSeasonKey = l.getCurrentSeasonStartYear();
            Season currSeason = l.getSeason(currSeasonKey);
            Date earliestMatchDate = DateHelper.createDateFromSQL(sqlDateOfEarliestGameToUpdate);
            Date earliestMatchWithBuffer = DateHelper.subtractXDaysFromDate(earliestMatchDate,1);
            Date lastMinuteOfYesterday = DateHelper.setTimeOfDate(DateHelper.subtract1DayFromDate(new Date()), 23, 59, 59);
            Understat.addSeasonsGames(l, currSeasonKey, null, null);
            Set<Integer> allGameIds = SofaScore.getGamesOfLeaguesSeason(l.getSeasonIds().getSofaScoreLeagueName(), l.getSeasonIds().getLeagueId(),
                    l.getSeasonIds().getLeaguesSeasonId(currSeason.getSeasonKey()), earliestMatchWithBuffer, lastMinuteOfYesterday, currSeason);
            System.out.println("For " + currSeason.getSeasonKey() + " in " + l.getName() +", we have " + allGameIds.size() + "new ids");
            allGameIds.forEach(gameId -> {
                SofaScore.addInfoToGame(currSeason, gameId);
            });
            DS_Update.updateGamesInDB(l, currSeason, earliestMatchWithBuffer);
        }
        DS_Main.closeConnection();
    }
}
