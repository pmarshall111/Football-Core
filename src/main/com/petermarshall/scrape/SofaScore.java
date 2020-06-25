package com.petermarshall.scrape;

import com.petermarshall.*;
import com.petermarshall.database.FirstScorer;
import com.petermarshall.database.datasource.DS_Get;
import com.petermarshall.database.datasource.DS_Main;
import com.petermarshall.database.datasource.DS_Update;
import com.petermarshall.machineLearning.createData.classes.MatchToPredict;
import com.petermarshall.scrape.classes.*;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.util.*;


/*
 * To be called after scraping from Understat
 */
public class SofaScore {
    public static final String BASE_URL = "https://www.sofascore.com";
    public static final String TOURNAMENT_ADD_ON = "/u-tournament/";
    public static final String SEASON_ADD_ON = "/season/";
    public static final String EVENTS_ADD_ON = "/events/";
    public static final String EVENT_ADD_ON = "/event/";
    public static final String LINEUPS_ADD_ON = "/lineups/";
    public static final String JSON_ENDING = "json";
    static final String ALTERNATE_BASE_URL = "https://api.sofascore.com/api/v1";
    static final String ALTERNATE_ODDS_ADD_ON = "/odds/1/all";

    /*
     * Creating scraping URLs
     */
    private static String getSeasonStatsUrl(int LeagueId, int SeasonId) {
        return BASE_URL + TOURNAMENT_ADD_ON + LeagueId + SEASON_ADD_ON + SeasonId + EVENTS_ADD_ON + JSON_ENDING;
    }
    private static String getGameInfoUrl(int gameId) {
        return BASE_URL + EVENT_ADD_ON + gameId + "/" + JSON_ENDING;
    }
    private static String getPlayerRatingsUrl(int gameId) {
        return BASE_URL + EVENT_ADD_ON + gameId + LINEUPS_ADD_ON + JSON_ENDING;
    }
    private static String getAlternateBetsUrl(int gameId) {
        return ALTERNATE_BASE_URL + EVENT_ADD_ON + gameId + ALTERNATE_ODDS_ADD_ON;
    }


    /*
     * Takes a leagueId and seasonId (unique to each league) from the LeagueSeasonIds ENUM. Scrapes SofaScore
     * and returns a set of all the Id's of the games in that season.
     *
     * Also updates matches in league to have the latest kickoff date and sofascore id.
     *
     * Called from League class which loops through all it's seasons and calls this method.
     * When it finds an ID from that correlates to match in the season argument, it will add the ID to that match.
     */
    public static Set<Integer> getGamesOfLeaguesSeason(String sofaScoreLeagueName, int leagueId, int seasonId, Date earliestDate, Date latestDate, Season season ) {
        String url = getSeasonStatsUrl(leagueId, seasonId);
        Set<Integer> gameIds = new HashSet<>();
        try {
            String jsonString = GetJsonHelper.jsonGetRequest(url);
            JSONParser parser = new JSONParser();
            JSONObject json = (JSONObject) parser.parse(jsonString);
            JSONArray tournaments = (JSONArray) json.get("tournaments");
            Iterator tournamentIterator = tournaments.iterator();

            while (tournamentIterator.hasNext()) {
                JSONObject tournament = (JSONObject) tournamentIterator.next();
                JSONObject tournamentInfo = (JSONObject) tournament.get("tournament");
                String tournamentName = tournamentInfo.get("name").toString();

                if (tournamentName.equals(sofaScoreLeagueName)) {
                    JSONArray events = (JSONArray) tournament.get("events");

                    for (Object aJsonObject : events) {
                        JSONObject game = (JSONObject) aJsonObject;
                        JSONObject homeTeam = (JSONObject) game.get("homeTeam");
                        String homeTeamName = (String) homeTeam.get("name");
                        JSONObject awayTeam = (JSONObject) game.get("awayTeam");
                        String awayTeamName = (String) awayTeam.get("name");
                        String matchStatus = game.get("statusDescription").toString();
                        String formattedStartDate = game.get("formatedStartDate").toString();
                        String[] partsOfDate = formattedStartDate.split("\\.");
                        String startTime = game.get("startTime").toString();
                        String[] partsOfTime = startTime.split(":");
                        Date gameDate = DateHelper.createDateyyyyMMdd(partsOfDate[2], partsOfDate[1], partsOfDate[0]);
                        gameDate = DateHelper.setTimeOfDate(gameDate, Integer.parseInt(partsOfTime[0]), Integer.parseInt(partsOfTime[1]), 0);

                        if (!matchStatus.equals("Postponed") && !matchStatus.equals("Canceled")) {
                            int id = Integer.parseInt(game.get("id").toString());
                            if (season != null) {
                                Team hTeam = season.getTeam(homeTeamName);
                                if (hTeam != null) {
                                    Match thisMatch = hTeam.getMatchFromAwayTeamName(awayTeamName);
                                    if (thisMatch != null) {
                                        thisMatch.setSofaScoreGameId(id);
                                        thisMatch.setKickoffTime(gameDate);
                                    }
                                }
                            }
                            if (earliestDate == null && latestDate == null) {
                                gameIds.add(id);
                            } else if (gameDate.before(latestDate) && (gameDate.after(earliestDate) || gameDate.equals(earliestDate))) {
                                    gameIds.add(id);
                            }
                        }
                    }
                }
            }
        } catch(ParseException e) {
            System.out.println(e.getMessage());
            e.printStackTrace();
            return null;
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
            Boolean confirmedLineups = (Boolean) event.get("confirmedLineups");
            if (!confirmedLineups) {
                //game is not within 1 hour to the future. no point getting in betting info, or player ratings/full time
                // score as our algorithm needs confirmed lineups to work.
                return;
            }

            JSONObject homeTeam = (JSONObject) event.get("homeTeam");
            JSONObject awayTeam = (JSONObject) event.get("awayTeam");
            String homeTeamName = (String) homeTeam.get("name");
            String awayTeamName = (String) awayTeam.get("name");
            String gameDate = (String) event.get("formatedStartDate"); //SofaScore devs spelt formatted wrong.
            String[] dateInfo = gameDate.split("\\.");
            Date dateKey = DateHelper.createDateyyyyMMdd(dateInfo[2], dateInfo[1], dateInfo[0]);
            Team hTeam = season.getTeam(homeTeamName);
            if (hTeam == null) {
                throw new RuntimeException("could not find team with string " + homeTeamName + ".\n the altered name for this team is " + Team.matchTeamNamesSofaScoreToUnderstat(homeTeamName));
            }
            Match match = hTeam.getMatchFromAwayTeamName(awayTeamName);
            if (match == null) {
                if (homeTeamName.equals("Genoa") && awayTeamName.equals("Fiorentina") && gameDate.equals("11.09.2016.")) {
                    //we have encountered a bug in SofaScore's data where they haven't changed a match's status to postponed. Instead there are 2 records of this game
                    //with the same score, but one is on the wrong date.
                    return;
                }
                else throw new RuntimeException("could not find match with date " + dateKey + " from team " + homeTeamName + " against " + awayTeamName);
            }
            match.setSofaScoreGameId(gameId);
            JSONArray oddsArray = (JSONArray) json.get("odds");
            ArrayList<Double> homeDrawAway = oddsArray != null ? getOdds(oddsArray) : getOddsAlternate(gameId);
            match.setHomeDrawAwayOdds(homeDrawAway);
            Boolean isFullTime = ((String) event.get("statusDescription")).equals("FT");
            if (isFullTime) {
                addPlayerRatingsToGame(match, gameId);
                addFirstGoalScorer(match, json);
            }
        } catch(ParseException e) {
            System.out.println(e.getMessage());
            e.printStackTrace();
        }
    }

    private static ArrayList<Double> getOdds(JSONArray oddsArray) {
        Iterator betsIterator = oddsArray.iterator();
        while (betsIterator.hasNext()) {
            JSONObject bet = (JSONObject) betsIterator.next();
            String betType = (String) bet.get("name");

            if (betType.equals("Full time")) {
                JSONObject oddsObj = (JSONObject) ((JSONArray) bet.get("regular")).iterator().next();
                JSONArray odds = (JSONArray) oddsObj.get("odds");
                Iterator oddsIterator = odds.iterator();
                double homeOdds = -1d, drawOdds = -1d, awayOdds = -1d;

                while (oddsIterator.hasNext()) {
                    JSONObject oddsObject = (JSONObject) oddsIterator.next();
                    String choice = oddsObject.get("choice").toString();
                    double decimalOdds = Double.parseDouble(oddsObject.get("decimalValue").toString());
                    switch (choice) {
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
                if (homeOdds == -1d || drawOdds == -1d || awayOdds == -1d)
                    throw new RuntimeException("not all odds were scraped!");

                ArrayList<Double> homeDrawAway = new ArrayList<>();
                homeDrawAway.add(homeOdds);
                homeDrawAway.add(drawOdds);
                homeDrawAway.add(awayOdds);
                return homeDrawAway;
            }
        }
        return new ArrayList<Double>(){{add(-1d); add(-1d); add(-1d);}};
    }

    private static ArrayList<Double> getOddsAlternate(int gameId) {
        String url = getAlternateBetsUrl(gameId);
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
    private static void addPlayerRatingsToGame(Match match, int gameId) {
        String url = getPlayerRatingsUrl(gameId);
        try {
            String jsonString = GetJsonHelper.jsonGetRequest(url);
            JSONParser parser = new JSONParser();
            JSONObject json = (JSONObject) parser.parse(jsonString);
            JSONObject homeTeam =(JSONObject) json.get("homeTeam");
            JSONObject awayTeam =(JSONObject) json.get("awayTeam");
            HashMap<String, PlayerRating> homeRatings = getRatingsFromJson(homeTeam, gameId);
            HashMap<String, PlayerRating> awayRatings = getRatingsFromJson(awayTeam, gameId);
            if (homeRatings != null && awayRatings != null) {
                match.setHomePlayerRatings(homeRatings);
                match.setAwayPlayerRatings(awayRatings);
            }
        } catch(ParseException e) {
            System.out.println(e.getMessage());
            e.printStackTrace();
        }
    }

    /*
     * Method called by SofaScore.addPlayerRatingsToGame. Will be called by both home and away JSON objects.
     * Adds the player name as a key, with another hashmap as the value. The inner HashMap contains keys
     * of "minutes" and "rating", denoting minutes played and the rating for this game.
     *
     * NOTE: Removes any apostrophe's in a players name as this causes issues for SQL.
     */
    private static HashMap<String, PlayerRating> getRatingsFromJson(JSONObject jsonObject, int gameId) {
        Boolean hasConfirmedLineups = (Boolean) jsonObject.get("confirmedLineups");
        if (!hasConfirmedLineups) {
            return null;
        }

        HashMap<String, PlayerRating> players = new HashMap<>();
        JSONArray lineups = (JSONArray) jsonObject.get("lineupsSorted");
        Iterator playersIterator = lineups.iterator();
        while (playersIterator.hasNext()) {
            JSONObject playerObj = (JSONObject) playersIterator.next();
            String playerName = (String) ((JSONObject) playerObj.get("player")).get("name");
            playerName = playerName.replaceAll("'", "");
            String rating = playerObj.get("rating").toString();
            try {
                PlayerRating playerRating = new PlayerRating(90, Double.parseDouble(rating), playerName);
                players.put(playerName, playerRating);
            } catch (NumberFormatException e) {
                //do nothing. player did not get a rating.
            }
        }

        JSONObject incidents = new JSONObject();
        try {
            incidents = (JSONObject) jsonObject.get("incidents");
        } catch (ClassCastException e) {
            System.out.println(jsonObject.toJSONString());
            System.out.println(e.getMessage());
            e.printStackTrace();
            System.out.println("Game id: " + gameId);
        }

        Iterator incidentsIterator = incidents.values().iterator();
        while (incidentsIterator.hasNext()) {
            JSONObject incident = (JSONObject) ((JSONArray) incidentsIterator.next()).iterator().next();
            if (((String) incident.get("incidentType")).equals("substitution")) {
                String subbedOff = (String) ((JSONObject) incident.get("playerOut")).get("name");
                String subbedOn = (String) ((JSONObject) incident.get("playerIn")).get("name");
                int subTime = Integer.parseInt(incident.get("time").toString());
                try {
                    //for player in, we just want to set minutes as 90-sub time.
                    players.get(subbedOn).setMinutesPlayed(90 - subTime);
                } catch (NullPointerException e) {
                    //do nothing. means the player was subbed on too late to get a rating so he isn't in players map.
                }

                try {
                    //for player out, we want to set minutes to be current minutes - time till end of game. this accounts for if they didn't start the game.
                    int currentMinutes = players.get(subbedOff).getMinutesPlayed();
                    players.get(subbedOff).setMinutesPlayed(currentMinutes - (90 - subTime));
                } catch (NullPointerException e) {
                    //do nothing. means the player coming off was subbed on too late to get a rating.
                }
            }
        }
        return players;
    }

    private static void addFirstGoalScorer(Match match, JSONObject json) {
        JSONArray incidents = (JSONArray) json.get("incidents");
        Iterator incidentsIterator = incidents.iterator();
        while (incidentsIterator.hasNext()) {
            JSONObject incident = (JSONObject) incidentsIterator.next();
            String typeOfIncident = (String) incident.get("incidentType");
            if (typeOfIncident.equals("goal")) {
                String homeScore = incident.get("homeScore").toString();
                String awayScore = incident.get("awayScore").toString();
                if (homeScore.equals("1") && awayScore.equals("0")) match.setFirstScorer(FirstScorer.HOME_FIRST);
                else if (homeScore.equals("0") && awayScore.equals("1")) match.setFirstScorer(FirstScorer.AWAY_FIRST);
            }
        }
    }

    /*
     * Method will get all info for when games are happening, and update game time and gameId in database.
     * Returns an ArrayList of game times.
     * We should also add in a sofascore ID so that we don't have to scrape for all the games. we can just do 1 request - for the lineups.
     *
     * Needed because the times given at the beginning of the season are often moved around by the TV companies. Possibly not needed every day.
     */
    public static ArrayList<Date> updateTodaysKickoffTimes() {
        ArrayList<League> leagues = new ArrayList<>();
        for (LeagueIdsAndData ids : LeagueIdsAndData.values()) {
            leagues.add(new League(ids));
        }
        String today = DateHelper.turnDateToyyyyMMddString(new Date());
        String url = "https://www.sofascore.com/football//" + today + "/json";
        HashMap<String, String> leagueNames = new HashMap<>();
        HashMap<String, String> sofaScoreNameToDbName = new HashMap<>();
        for (League league: leagues) {
            leagueNames.put(league.getIdsAndData().getSofaScoreLeagueName(), league.getIdsAndData().getSofaScoreCountryName());
            sofaScoreNameToDbName.put(league.getIdsAndData().getSofaScoreLeagueName(), league.getIdsAndData().name());
        }

        ArrayList<Date> dates = new ArrayList<>();
        try {
            String jsonString = GetJsonHelper.jsonGetRequest(url);
            JSONParser parser = new JSONParser();
            JSONObject json = (JSONObject) parser.parse(jsonString);
            JSONObject football = (JSONObject) json.get("sportItem");
            JSONArray tournaments = (JSONArray) football.get("tournaments");
            Iterator tournamentIterator = tournaments.iterator();
            DS_Main.openProductionConnection();
            while (tournamentIterator.hasNext()) {
                JSONObject currTournament = (JSONObject) tournamentIterator.next();
                JSONObject tournamentInfo = (JSONObject) currTournament.get("tournament");
                JSONObject countryInfo = (JSONObject) currTournament.get("category");
                String tournamentName = tournamentInfo.get("name").toString();
                String countryName = countryInfo.get("name").toString();
                //default value needed as we only want to get leagues that are in our db.
                //default value of "_" will not be in json countryName so we won't look at it.
                if (leagueNames.getOrDefault(tournamentName, "_").equals(countryName)) {
                    String leagueName = sofaScoreNameToDbName.get(tournamentName);
                    int leagueId = DS_Get.getLeagueId(leagueName);
                    JSONArray events = (JSONArray) currTournament.get("events");
                    Iterator eventsIterator = events.iterator();
                    while (eventsIterator.hasNext()) {
                        JSONObject event = (JSONObject) eventsIterator.next();
                        String homeTeam = ((JSONObject) event.get("homeTeam")).get("name").toString();
                        String awayTeam = ((JSONObject) event.get("awayTeam")).get("name").toString();
                        //altering team names to match those stored in db. i.e. Wolverhampton Wanderers might be stored as Wolves.
                        homeTeam = Team.matchTeamNamesSofaScoreToUnderstat(homeTeam);
                        awayTeam = Team.matchTeamNamesSofaScoreToUnderstat(awayTeam);
                        String startDate = event.get("formatedStartDate").toString();
                        String startTime = event.get("startTime").toString();
                        String[] startDateParts = startDate.split("\\.");
                        //db dateString format is yyyy-mm-dd hh:mm:ss
                        String dateString = startDateParts[2] + "-" + startDateParts[1] + "-" + startDateParts[0] + " " + startTime + ":00";
                        Date date = DateHelper.createDateFromSQL(dateString);
                        int seasonYearStart = leagues.get(0).getCurrentSeasonStartYear();
                        DS_Update.updateKickoffTime(seasonYearStart, homeTeam, awayTeam, dateString, leagueId);
                        dates.add(date);
                    }
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

    public static void addLineupsToGamesAboutToStart(ArrayList<MatchToPredict> matches) {
        for (MatchToPredict match: matches) {
            String url = getPlayerRatingsUrl(match.getSofascore_id());
            try {
                String jsonString = GetJsonHelper.jsonGetRequest(url);
                JSONParser parser = new JSONParser();
                JSONObject json = (JSONObject) parser.parse(jsonString);
                JSONObject homeTeam = (JSONObject) json.get("homeTeam");
                JSONObject awayTeam = (JSONObject) json.get("awayTeam");
                boolean homeConfirmedLineups = Boolean.parseBoolean(homeTeam.get("confirmedLineups").toString());
                boolean awayConfirmedLineups = Boolean.parseBoolean(awayTeam.get("confirmedLineups").toString());
                if (!homeConfirmedLineups || !awayConfirmedLineups) {
                    //nothing to look at. go on to next game
                    //TODO: add logging here
                    continue;
                }

                JSONObject[] teams = new JSONObject[]{homeTeam, awayTeam};
                for (int i = 0; i< teams.length; i++) {
                    JSONArray lineups = (JSONArray) teams[i].get("lineupsSorted");
                    Iterator lineupsIterator = lineups.iterator();
                    ArrayList<String> lineup = new ArrayList<>();
                    while (lineupsIterator.hasNext() && lineup.size() < 11) {
                        JSONObject currPlayer = (JSONObject) lineupsIterator.next();
                        JSONObject playerInfo = (JSONObject) currPlayer.get("player");
                        String playerName = playerInfo.get("name").toString();
                        lineup.add(playerName);
                    }
                    if (i == 0) {
                        match.setHomeTeamPlayers(lineup);
                    }
                    else {
                        match.setAwayTeamPlayers(lineup);
                    }
                }
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
    }
}