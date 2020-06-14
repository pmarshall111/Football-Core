package com.petermarshall.taskScheduling;

import com.petermarshall.scrape.classes.League;
import com.petermarshall.scrape.classes.Season;

import java.util.HashSet;

/*
 * Sometimes shortly after the games the sites do not have data from the game. This results in the data not being added to the database
 * This method will get the games from the database without data in and try to scrape in the data again and store it in the database if successful.
 *
 *
 */
public class ScrapeGamesMissingData {

    public static void addDataToMissedGames() {
//        HashSet<League> allLeagues = new HashSet<League>() {{
//            add(new League(LeagueSeasonIds.EPL));
//            add(new League(LeagueSeasonIds.BUNDESLIGA));
//            add(new League(LeagueSeasonIds.LA_LIGA));
//            add(new League(LeagueSeasonIds.LIGUE_1));
//            add(new League(LeagueSeasonIds.SERIE_A));
//            add(new League(LeagueSeasonIds.RUSSIA));
//        }};
//
//        DataSource.openConnection();
//        DataSource.addMissedMatches(allLeagues);
//
//        filterOutLeaguesWithNoGames(allLeagues);
//        allLeagues.forEach(league -> {
//            Understat.addDataToGamesFromLeague(league);
//
//            LeagueSeasonIds ids = league.getSeasonIds();
//            ArrayList<Season> allSeasons = league.getAllSeasons();
//            allSeasons.forEach(season -> {
//                if (season.getAllMatches().size() > 0) {
//                    SofaScore.getGamesOfLeaguesSeason(ids.getSofaScoreLeagueName(), ids.getLeagueId(), ids.getLeaguesSeasonId(season.getSeasonKey()),
//                            null, null, season);
//
//                    season.getAllMatches().forEach(match -> {
//                        if (match.getSofaScoreGameId() > 0) SofaScore.addInfoToGame(season, match.getSofaScoreGameId());
//                    });
//
//                    DataSource.addPlayedGamesToDB(season);
//                }
//            });
//        });
//        DataSource.closeConnection();
    }

    private static void filterOutLeaguesWithNoGames(HashSet<League> leagues) {
        leagues.removeIf(league -> {
            for (Season s: league.getAllSeasons()) {
                if (s.getAllMatches().size() > 0) {
                    return false;
                }
            }
            return true;
        });
    }

    public static void main(String[] args) {
        addDataToMissedGames();
    }

}
