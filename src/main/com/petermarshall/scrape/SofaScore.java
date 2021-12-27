package com.petermarshall.scrape;

import com.petermarshall.*;
import com.petermarshall.database.FirstScorer;
import com.petermarshall.database.datasource.DS_Get;
import com.petermarshall.database.datasource.DS_Main;
import com.petermarshall.database.datasource.DS_Update;
import com.petermarshall.machineLearning.createData.classes.MatchToPredict;
import com.petermarshall.scrape.classes.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.util.*;


/*
 * To be called after scraping from Understat
 */
public class SofaScore {

    private static final String API_URL = "https://api.sofascore.com/api/v1";
    private static final String TOURNAMENT_ADD_ON = "/unique-tournament/";
    private static final String SEASON_ADD_ON = "/season/";
    private static final String EVENTS_ADD_ON = "/events/last/";
    private static final String EVENT_ADD_ON = "/event/";
    private static final String LINEUPS_ADD_ON = "/lineups";
    private static final String ODDS_ADD_ON = "/odds/1/all";
    private static final String INCIDENTS_ADD_ON = "/incidents";
    private static final String STATISTICS_ADD_ON = "/statistics";

    /*
     * Creating scraping URLs
     */
    private static String getSeasonStatsUrl(int LeagueId, int SeasonId, int pageNumb) {
        return API_URL + TOURNAMENT_ADD_ON + LeagueId + SEASON_ADD_ON + SeasonId + EVENTS_ADD_ON + pageNumb;
    }
    private static String getGameInfoUrl(int gameId) {
        return API_URL + EVENT_ADD_ON + gameId;
    }
    private static String getPlayerRatingsUrl(int gameId) {
        return API_URL + EVENT_ADD_ON + gameId + LINEUPS_ADD_ON;
    }
    private static String getBetsUrl(int gameId) {
        return API_URL + EVENT_ADD_ON + gameId + ODDS_ADD_ON;
    }
    private static String getIncidentsUrl(int gameId) {
        return API_URL + EVENT_ADD_ON + gameId + INCIDENTS_ADD_ON;
    }
    private static String getTodaysGamesUrl(String yyyymmdd) { //yyyymmdd must have a - between parts of the date e.g. 2021-08-26
        return API_URL + "/sport/football/scheduled-events/" + yyyymmdd;
    }
    private static String getStatisticsUrl(int gameId) { return API_URL + EVENT_ADD_ON + gameId + STATISTICS_ADD_ON; }


    /*
     * Takes a leagueId and seasonId (unique to each league) from the LeagueSeasonIds ENUM. Scrapes SofaScore
     * and returns a set of all the Id's of the games in that season.
     *
     * Also updates matches in league to have the latest kickoff date and sofascore id.
     *
     * Called from League class which loops through all it's seasons and calls this method.
     * When it finds an ID that correlates to match in the season argument, it will add the ID to that match.
     */
    public static Set<Integer> getGamesOfLeaguesSeason(String sofaScoreLeagueName, int leagueId, int seasonId, Date earliestDate, Date latestDate, Season season ) {
        Set<Integer> gameIds = new HashSet<>();
        //need to loop through the events until we get a 404 err as Sofascore added pagination to their API
        int pageNumb = 0;
        boolean hasMorePages = true;
        while (hasMorePages) {
            String url = getSeasonStatsUrl(leagueId, seasonId, pageNumb);
            try {
                String jsonString = GetJsonHelper.jsonGetRequest(url);
                JSONParser parser = new JSONParser();
                JSONObject json = (JSONObject) parser.parse(jsonString);

                JSONArray events = (JSONArray) json.get("events");

                for (Object aJsonObject : events) {
                    JSONObject game = (JSONObject) aJsonObject;
                    JSONObject tournament = (JSONObject) game.get("tournament");
                    String tournamentName = (String) tournament.get("name");
                    if (tournamentName.toLowerCase().contains("relegation")) {
                        continue; //not interested in matches that are for relegation/promotion playoffs as we have no data about the team from the lower league.
                    }

                    JSONObject homeTeam = (JSONObject) game.get("homeTeam");
                    String homeTeamName = (String) homeTeam.get("name");
                    JSONObject awayTeam = (JSONObject) game.get("awayTeam");
                    String awayTeamName = (String) awayTeam.get("name");

                    JSONObject statusObj = (JSONObject) game.get("status");
                    String status = (String) statusObj.get("description");

                    String startTime = game.get("startTimestamp").toString(); //returns number of seconds since epoch
                    Date kickoff = DateHelper.getDateFromSofascoreTimestamp(Long.parseLong(startTime));

                    if (!status.equals("Postponed") && !status.equals("Canceled")) {
                        int id = Integer.parseInt(game.get("id").toString());
                        if (season != null) {
                            Team hTeam = season.getTeam(homeTeamName);
                            if (hTeam != null) {
                                Match thisMatch = hTeam.getMatchFromAwayTeamName(awayTeamName);
                                if (thisMatch != null) {
                                    thisMatch.setSofaScoreGameId(id);
                                    thisMatch.setKickoffTime(kickoff);
                                }
                            }
                        }
                        if (earliestDate == null && latestDate == null) {
                            gameIds.add(id);
                        } else if (kickoff.before(latestDate) && (kickoff.after(earliestDate) || kickoff.equals(earliestDate))) {
                            gameIds.add(id);
                        }
                    } else {
                        if (season != null) {
                            Team hTeam = season.getTeam(homeTeamName);
                            if (hTeam != null) {
                                Match thisMatch = hTeam.getMatchFromAwayTeamName(awayTeamName);
                                if (thisMatch != null) {
                                    thisMatch.setPostponed(true);
                                }
                            }
                        }
                    }
                }
            } catch (ParseException e) {
                System.out.println(e.getMessage());
                e.printStackTrace();
                return null;
            } catch (Exception e) { //JSONParser throws FileNotFoundException if 404.
                hasMorePages = false;
            }
            pageNumb++;
        }
        return gameIds;
    }

    /*
     * Will be called after the call to Understat scraper. Method does not create Matches, but instead finds them and
     * adds data to it. Method will throw error if cannot find correct game.
     *
     * Main body of function finds the correct match instance already created, then adds the
     * betting odds to it. Then if it is full time, we call addPlayerRatingsToGame, and also addFirstScorer.
     */
    public static void addInfoToGame(Season season, int gameId) {
        //SofaScore does not have data for this game (Ligue 1. Bastia vs Lyon 16-17 - abandoned early due to crowd trouble). Return early to avoid error.
        if (gameId == 7080222) return;

        String url = getGameInfoUrl(gameId);
        try  {
            String jsonString = GetJsonHelper.jsonGetRequest(url);
            JSONParser parser = new JSONParser();
            JSONObject json = (JSONObject) parser.parse(jsonString);
            JSONObject event = (JSONObject) json.get("event");

            long timeStamp = (long) event.get("startTimestamp");
            Date kickOff = DateHelper.getDateFromSofascoreTimestamp(timeStamp);
            Date now = new Date();
            int minsBetweenDates = DateHelper.findMinutesToAddToDate1ToGetDate2(now, kickOff);
            boolean kickOffIsInPast =  now.after(kickOff);

            boolean shouldHaveLineups = kickOffIsInPast || minsBetweenDates < 60; //lineups are announced 1hr before kickoff.
            if (!shouldHaveLineups) {
                //game is not within 1 hour to the future. no point getting in betting info, or player ratings/full-time score as algorithm needs confirmed lineups to work.
                return;
            }

            JSONObject homeTeam = (JSONObject) event.get("homeTeam");
            JSONObject awayTeam = (JSONObject) event.get("awayTeam");
            String homeTeamName = (String) homeTeam.get("name");
            String awayTeamName = (String) awayTeam.get("name");
            Team hTeam = season.getTeam(homeTeamName);
            if (hTeam == null) {
                Logger logger = LogManager.getLogger(SofaScore.class);
                logger.error("could not find team with string " + homeTeamName + ".\n the altered name for this team is " + Team.matchTeamNamesSofaScoreToUnderstat(homeTeamName) + ". The sofascore ID is " + gameId);
                throw new RuntimeException("could not find team with string " + homeTeamName + ".\n the altered name for this team is " + Team.matchTeamNamesSofaScoreToUnderstat(homeTeamName) + ". The sofascore ID is " + gameId);
            }
            Match match = hTeam.getMatchFromAwayTeamName(awayTeamName);
            if (match == null) {
                String date = DateHelper.turnDateToddMMyyyyString(kickOff);
                if (homeTeamName.equals("Genoa") && awayTeamName.equals("Fiorentina") && date.equals("11-09-2016")) {
                    //we have encountered a bug in SofaScore's data where they haven't changed a match's status to postponed. Instead there are 2 records of this game
                    //with the same score, but one is on the wrong date.
                    return;
                }
                else {
                    Logger logger = LogManager.getLogger(SofaScore.class);
                    logger.error("could not find match with date " + kickOff + " from team " + homeTeamName + " against " + awayTeamName + ". Sofascore id " + gameId);
                    throw new RuntimeException("could not find match with date " + kickOff + " from team " + homeTeamName + " against " + awayTeamName + ". Sofascore id " + gameId);
                }
            }
            match.setSofaScoreGameId(gameId);
            match.setHomeDrawAwayOdds(getOdds(gameId));
            JSONObject status = (JSONObject) event.get("status");
            boolean isFullTime = ((String) status.get("description")).equals("Ended");
            if (isFullTime) {
                addPlayerRatingsToGame(match, gameId);
                addFirstGoalScorer(match, gameId);
                addMatchStatistics(match, gameId);
            }
        } catch(ParseException e) {
            System.out.println(e.getMessage());
            e.printStackTrace();
        }
    }

    private static ArrayList<Double> getOdds(int gameId) {
        String url = getBetsUrl(gameId);
        String jsonString = GetJsonHelper.jsonGetRequest(url);
        try {
            JSONParser parser = new JSONParser();
            JSONObject json = (JSONObject) parser.parse(jsonString);
            JSONArray markets = (JSONArray) json.get("markets");

            for (Object marketObj: markets) {
                JSONObject market = (JSONObject) marketObj;
                String marketName = market.get("marketName").toString();

                if (marketName.equals("Full time")) {
                    JSONArray choices = (JSONArray) market.get("choices");
                    double homeOdds = -1d, drawOdds = -1d, awayOdds = -1d;
                    for (Object choice: choices) {
                        JSONObject betDetails = (JSONObject) choice;
                        String fractionalBet = betDetails.get("fractionalValue").toString();
                        double decimalOdds = ConvertOdds.fromFractionToDecimal(fractionalBet);

                        String team = betDetails.get("name").toString();
                        switch (team) {
                            case "1":
                                homeOdds = decimalOdds;
                                break;
                            case "X":
                                drawOdds = decimalOdds;
                                break;
                            case "2":
                                awayOdds = decimalOdds;
                                break;
                            default:
                                throw new RuntimeException("odds were not home, draw or away!");
                        }
                    }
                    ArrayList<Double> homeDrawAway = new ArrayList<>();
                    homeDrawAway.add(homeOdds);
                    homeDrawAway.add(drawOdds);
                    homeDrawAway.add(awayOdds);
                    return homeDrawAway;
                }
            }
        } catch (Exception e) {}
        return new ArrayList<Double>(){{add(-1d); add(-1d); add(-1d);}};
    }

    /*
     * Method will be called by SofaScore.addInfoToGame. Will add the player name, his rating and minutes played
     * for the game.
     */
    public static void addPlayerRatingsToGame(Match match, int gameId) {
        String url = getPlayerRatingsUrl(gameId);
        try {
            String jsonString = GetJsonHelper.jsonGetRequest(url);
            JSONParser parser = new JSONParser();
            JSONObject json = (JSONObject) parser.parse(jsonString);
            boolean isConfirmed = (boolean) json.get("confirmed");
            if (!isConfirmed) {
                System.out.println("Match doesn't have confirmed lineups yet. Not adding lineups/ratings. " + match.getHomeTeam().getTeamName() + " vs " + match.getAwayTeam().getTeamName() + " on " + match.getKickoffTime());
                return;
            }

            JSONObject homeTeam =(JSONObject) json.get("home");
            JSONObject awayTeam =(JSONObject) json.get("away");
            HashMap<String, PlayerRating> homeRatings = getRatingsFromJson(homeTeam);
            HashMap<String, PlayerRating> awayRatings = getRatingsFromJson(awayTeam);
            if (homeRatings != null && awayRatings != null) {
                match.setHomePlayerRatings(homeRatings);
                match.setAwayPlayerRatings(awayRatings);
            }
        } catch(ParseException e) {
            e.printStackTrace();
            System.out.println("Parse exception for " + url);
        } catch (NullPointerException e) {
            e.printStackTrace();
            System.out.println("Null pointer exception for " + url);
        }
    }

    /*
     * Method called by SofaScore.addPlayerRatingsToGame. Will be called by both home and away JSON objects.
     * Adds the player name as a key, with another hashmap as the value. The inner HashMap contains keys
     * of "minutes" and "rating", denoting minutes played and the rating for this game.
     *
     * NOTE: Removes any apostrophe's in a players name as this causes issues for SQL.
     */
    private static HashMap<String, PlayerRating> getRatingsFromJson(JSONObject jsonObject) {
        HashMap<String, PlayerRating> players = new HashMap<>();
        JSONArray playerObjects = (JSONArray) jsonObject.get("players");
        Iterator playersIterator = playerObjects.iterator();
        while (playersIterator.hasNext()) {
            JSONObject playerObj = (JSONObject) playersIterator.next();
            String playerName = (String) ((JSONObject) playerObj.get("player")).get("name");
            playerName = playerName.replaceAll("'", "");

            JSONObject playerStats = (JSONObject) playerObj.get("statistics");
            String playerPosition = playerObj.getOrDefault("position", "null").toString();
            if (playerStats.containsKey("rating") && playerStats.containsKey("minutesPlayed")) {
                double rating = Double.parseDouble(playerStats.get("rating") + "");
                long minsPlayed = (long) playerStats.get("minutesPlayed");
                if (rating > 0 && minsPlayed >= 10) {
                    try {
                        PlayerRating playerRating = new PlayerRating(Integer.parseInt(minsPlayed + ""), rating, playerName, playerPosition);
                        players.put(playerName, playerRating);
                    } catch (NumberFormatException e) {
                        System.out.println(rating);
                        e.printStackTrace();
                    }
                }
            }
        }
        return players;
    }

    public static void addFirstGoalScorer(Match match, int gameId) {
        String url = getIncidentsUrl(gameId);

        try {
            String jsonString = GetJsonHelper.jsonGetRequest(url);
            JSONParser parser = new JSONParser();
            JSONObject json = (JSONObject) parser.parse(jsonString);

            JSONArray incidents = (JSONArray) json.get("incidents");
            Iterator incidentsIterator = incidents.iterator();

            long minOfFirstGoal = 999;
            FirstScorer firstScorer = FirstScorer.NO_FIRST_SCORER;

            while (incidentsIterator.hasNext()) {
                JSONObject incident = (JSONObject) incidentsIterator.next();
                String typeOfIncident = (String) incident.get("incidentType");
                boolean isGoal = typeOfIncident.equals("goal");
                boolean isGoalFromVar = false;
                String incidentClass = (String) incident.get("incidentClass");
                if (typeOfIncident.equals("varDecision") && incidentClass.equals("goalAwarded")) {
                    try {
                        boolean isConfirmed = (boolean) incident.get("confirmed");
                        if (isConfirmed) {
                            isGoalFromVar = true;
                        }
                    } catch (NullPointerException e) {
                        System.out.println("Could not get confirmed field on VAR decision for URL: " + url);
                        Logger logger = LogManager.getLogger(SofaScore.class);
                        logger.warn("Could not get confirmed field on VAR decision for URL: " + url);
                    }
                }
                if (isGoal || isGoalFromVar) {
                    boolean isHomeGoal = (boolean) incident.get("isHome");
                    long min = (long) incident.get("time"); //JSONParser will make any whole numbers long's
                    if (min < minOfFirstGoal) {
                        firstScorer = isHomeGoal ? FirstScorer.HOME_FIRST : FirstScorer.AWAY_FIRST;
                    }
                }
            }

            match.setFirstScorer(firstScorer);

        } catch (Exception e) {
            System.out.println("Exception for URL: " + url);
            e.printStackTrace();
        }
    }

    public static void addMatchStatistics(Match match, int gameId) {
        String url = getStatisticsUrl(gameId);
        try {
            String jsonString = GetJsonHelper.jsonGetRequest(url);
            JSONParser parser = new JSONParser();
            JSONObject json = (JSONObject) parser.parse(jsonString);

            JSONArray statistics = (JSONArray) json.get("statistics");
            Iterator statsIterator = statistics.iterator();

            while (statsIterator.hasNext()) {
                JSONObject stats = (JSONObject) statsIterator.next();
                if (stats.get("period").equals("ALL")) {
                    JSONArray groups = (JSONArray) stats.get("groups");
                    Iterator groupsIterator = groups.iterator();

                    while (groupsIterator.hasNext()) {
                        JSONObject group = (JSONObject) groupsIterator.next();
                        String category = group.get("groupName").toString();
                        JSONArray items = (JSONArray) group.get("statisticsItems");
                        Iterator itemsIterator = items.iterator();
                        if (category.equals("Possession")) {
                            while (itemsIterator.hasNext()) {
                                JSONObject item = (JSONObject) itemsIterator.next();
                                String itemName = item.get("name").toString();
                                if (itemName.equals("Ball possession")) {
                                    String home = item.get("home").toString();
                                    home = home.substring(0, home.length()-1);
                                    String away = item.get("away").toString();
                                    away = away.substring(0, away.length()-1);
                                    match.setHomePossession(home);
                                    match.setAwayPossession(away);
                                }
                            }
                        } else if (category.equals("Shots")) {
                            while (itemsIterator.hasNext()) {
                                JSONObject item = (JSONObject) itemsIterator.next();
                                String itemName = item.get("name").toString();
                                if (itemName.equalsIgnoreCase("Total shots")) {
                                    String home = item.get("home").toString();
                                    String away = item.get("away").toString();
                                    match.setHomeShots(home);
                                    match.setAwayShots(away);
                                } else if (itemName.equalsIgnoreCase("Shots on target")) {
                                    String home = item.get("home").toString();
                                    String away = item.get("away").toString();
                                    match.setHomeShotsOnTarget(home);
                                    match.setAwayShotsOnTarget(away);
                                }
                            }
                        }
                    }
                }
            }

        } catch (Exception e) {
            System.out.println("Exception for URL: " + url);
            e.printStackTrace();
        }
    }

    /*
     * Method will get all info for when games are happening, and update game time and gameId in database.
     * Returns an ArrayList of game times.
     * We should also add in a sofascore ID so that we don't have to scrape for all the games. we can just do 1 request - for the lineups.
     *
     * Needed because the times given at the beginning of the season are often moved around by the TV companies. Possibly not needed every day.
     */
    public static ArrayList<Date> updateKickoffTimes(Date lookForGamesOnThisDate, boolean isProdEnv) { //boolean use
        ArrayList<League> leagues = new ArrayList<>();
        for (LeagueIdsAndData ids : LeagueIdsAndData.values()) {
            leagues.add(new League(ids));
        }
        String dateStr = DateHelper.turnDateToyyyyMMddString(lookForGamesOnThisDate);
        String url = getTodaysGamesUrl(dateStr);
        HashSet<String> targetCountryAndLeagueNames = new HashSet<>();
        HashMap<String, String> dbNameTranslator = new HashMap<>(); //names stored in sofascore may be different to those stored in the database.
        for (League league: leagues) {
            String combinedCountryAndLeagueName = combineCountryAndLeagueName(league.getIdsAndData().getSofaScoreCountryName(),
                                                                                league.getIdsAndData().getSofaScoreLeagueName());
            targetCountryAndLeagueNames.add(combinedCountryAndLeagueName);
            dbNameTranslator.put(combinedCountryAndLeagueName, league.getIdsAndData().name());
        }

        ArrayList<Date> dates = new ArrayList<>();
        try {
            String jsonString = GetJsonHelper.jsonGetRequest(url);
            JSONParser parser = new JSONParser();
            JSONObject json = (JSONObject) parser.parse(jsonString);
            JSONArray events = (JSONArray) json.get("events");
            Iterator eventsIterator = events.iterator();
            DS_Main.openProductionConnection();
            while (eventsIterator.hasNext()) {
                JSONObject event = (JSONObject) eventsIterator.next();
                JSONObject tournamentInfo = (JSONObject) event.get("tournament");
                JSONObject countryInfo = (JSONObject) tournamentInfo.get("category");
                String tournamentName = tournamentInfo.get("name").toString();
                String countryName = countryInfo.get("name").toString();
                String combinedCountryAndLeagueName = combineCountryAndLeagueName(countryName, tournamentName);
                if (targetCountryAndLeagueNames.contains(combinedCountryAndLeagueName)) {
                    String leagueName = dbNameTranslator.get(combinedCountryAndLeagueName);
                    int leagueId = DS_Get.getLeagueId(leagueName);
                    String homeTeam = ((JSONObject) event.get("homeTeam")).get("name").toString();
                    String awayTeam = ((JSONObject) event.get("awayTeam")).get("name").toString();
                    //altering team names to match the name that's stored in db. i.e. Wolverhampton Wanderers might be stored as Wolves.
                    homeTeam = Team.matchTeamNamesSofaScoreToUnderstat(homeTeam);
                    awayTeam = Team.matchTeamNamesSofaScoreToUnderstat(awayTeam);
                    long timeStamp = (long) event.get("startTimestamp");
                    Date date = DateHelper.getDateFromSofascoreTimestamp(timeStamp);
                    String sqlString = DateHelper.getSqlDate(date);
                    int seasonYearStart = leagues.get(0).getCurrentSeasonStartYear();
                    if (isProdEnv) {
                        DS_Update.updateKickoffTime(seasonYearStart, homeTeam, awayTeam, sqlString, leagueId);
                    }
                    dates.add(date);
                }
            }
        } catch (ParseException e) {
            System.out.println(e.getMessage());
        }
        finally {
            DS_Main.closeConnection();
        }
        return dates;
    }

    private static String combineCountryAndLeagueName(String countryName, String leagueName) {
        return countryName + leagueName;
    }

    public static void addLineupsToGamesAboutToStart(ArrayList<MatchToPredict> matches) {
        for (MatchToPredict match: matches) {
            String url = getPlayerRatingsUrl(match.getSofascore_id());
            try {
                String jsonString = GetJsonHelper.jsonGetRequest(url);
                JSONParser parser = new JSONParser();
                JSONObject json = (JSONObject) parser.parse(jsonString);
                boolean isConfirmed = Boolean.parseBoolean(json.get("confirmed").toString());
                if (!isConfirmed) {
                    Logger logger = LogManager.getLogger(SofaScore.class);
                    logger.error("Match to predict (" + match.getHomeTeamName() + " vs " + match.getAwayTeamName() + ") didn't have confirmed lineups when called.");
                    continue;
                }

                JSONObject homeTeam = (JSONObject) json.get("home");
                JSONObject awayTeam = (JSONObject) json.get("away");
                JSONObject[] teams = new JSONObject[]{homeTeam, awayTeam};
                for (int i = 0; i< teams.length; i++) {
                    JSONArray lineups = (JSONArray) teams[i].get("players");
                    Iterator players = lineups.iterator();
                    ArrayList<String> startingLineup = new ArrayList<>();
                    while (players.hasNext()) {
                        JSONObject currPlayer = (JSONObject) players.next();
                        JSONObject playerInfo = (JSONObject) currPlayer.get("player");
                        String playerName = playerInfo.get("name").toString();
                        boolean startsOnTheBench = Boolean.parseBoolean(currPlayer.get("substitute").toString());
                        if (!startsOnTheBench) {
                            startingLineup.add(playerName);
                        }
                    }
                    if (i == 0) {
                        match.setHomeTeamPlayers(startingLineup);
                    }
                    else {
                        match.setAwayTeamPlayers(startingLineup);
                    }
                }
            } catch (ParseException e) {
                Logger logger = LogManager.getLogger(SofaScore.class);
                logger.error(e);
            }
        }
    }
}