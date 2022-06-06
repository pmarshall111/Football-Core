package com.footballbettingcore.scrape;

import com.footballbettingcore.database.datasource.DS_Main;
import com.footballbettingcore.database.datasource.DS_Update;
import com.footballbettingcore.scrape.classes.*;
import com.footballbettingcore.utils.DateHelper;

import java.util.Date;

// Class to be used in the case that one of the data sources doesn't have data.
public class ManualScrape {
    public static void skipUnderstatScrape(LeagueIdsAndData leagueIdsAndData, String homeTeamName,
                                           String awayTeamName, int homeScore, int awayScore,
                                           double homeXg, double awayXg, Date kickoff, int sofascoreId) {
        League l = new League(leagueIdsAndData);
        Season currSeason = l.getSeason(l.getCurrentSeasonStartYear());
        Team homeTeam = new Team(homeTeamName);
        Team awayTeam = new Team(awayTeamName);
        Match m = new Match(homeTeam, awayTeam, kickoff, homeScore, awayScore);
        m.setHomeXGF(homeXg);
        m.setAwayXGF(awayXg);
        currSeason.addNewTeam(homeTeam);
        currSeason.addNewTeam(awayTeam);
        currSeason.addNewMatch(m);
        SofaScore.addInfoToGame(currSeason, sofascoreId);
        DS_Main.openProductionConnection();
        DS_Update.updateGamesInDB(l, currSeason, kickoff);
    }

    public static void main(String[] args) {
        skipUnderstatScrape(
                LeagueIdsAndData.BUNDESLIGA,
                "Bochum",
                "Borussia M.Gladbach",
                0,
                2,
                1.75,
                0.97,
                DateHelper.createDateyyyyMMddHHmmss("2022", "03", "18", "19","30","00"),
                9594319
        );
    }
}
