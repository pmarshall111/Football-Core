package com.petermarshall.scrape;

import com.petermarshall.*;
import com.petermarshall.machineLearning.createData.classes.MatchToPredict;
import com.petermarshall.database.DataSource;
import com.petermarshall.scrape.classes.*;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.util.*;


/*
 * To be called after scraping from Understat, using Understat class.
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

    //TESTING PURPOSES ONLY
    public static HashMap<String, Integer> numbBettingAdded = new HashMap<>();
    public static HashMap<String, Integer> gotToStartOfOdds = new HashMap<>();
    public static HashMap<String, Integer> retrievedJsonData = new HashMap<>();
    public static HashMap<String, Integer> functionCalled = new HashMap<>();
    ///////

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
     * Called from League class which loops through all it's seasons and calls this method.
     *
     * When it finds an ID from that correlates to match in the season argument, it will add the ID to that match.
     */
    public static Set<Integer> getGamesOfLeaguesSeason(String sofaScoreLeagueName, int leagueId, int seasonId, Date earliestDate, Date latestDate, Season season ) {
        String url = getSeasonStatsUrl(leagueId, seasonId);
        Set<Integer> allGameIds = new HashSet<>();

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

                        if (!matchStatus.equals("Postponed") && !matchStatus.equals("Canceled")) {
                            int id = Integer.parseInt(game.get("id").toString());

                            if (season != null) {
                                Team hTeam = season.getTeam(homeTeamName);
                                if (hTeam != null) {
                                    Match thisMatch = hTeam.getMatchFromAwayTeamName(awayTeamName);
                                    if (thisMatch != null) {
                                        thisMatch.setSofaScoreGameId(id);
                                    }
                                }
                            }

                            if (earliestDate == null && latestDate == null) allGameIds.add(id);
                            else {
                                String formattedStartDate = game.get("formatedStartDate").toString();
                                String[] partsOfDate = formattedStartDate.split("\\.");

                                Date gameDate = DateHelper.createDateyyyyMMdd(partsOfDate[2], partsOfDate[1], partsOfDate[0]);


                                if (gameDate.before(latestDate) && gameDate.after(earliestDate)) allGameIds.add(id);
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

        return allGameIds;
    }

//    private static void addGamesToSet(Set set, JSONArray jsonArray) {
//        for (Object aJsonArray : jsonArray) {
//            JSONObject game = (JSONObject) aJsonArray;
//
//            set.add(Integer.parseInt(game.get("slug").toString()));
//        }
//    }


    /*
     * Will be called after the call to Understat scraper. Method does not create Matches, but instead finds them and
     * adds data to it. Method will throw error if cannot find correct game.
     *
     * Main body of function finds the correct match instance already created, then adds the
     * betting odds to it. Then if it is full time, we call addPlayerRatingsToGame, and also addFirstScorer.
     */
    public static void addInfoToGame(Season season, int gameId) {
        //debugging
        functionCalled.put(season.getSeasonKey(), functionCalled.getOrDefault(season.getSeasonKey(), 0) + 1);

        //SofaScore does not have data for this game (Ligue 1. Bastia vs Lyon 16-17). Return early to avoid error.
        if (gameId == 7080222) return;

        String url = getGameInfoUrl(gameId);


        try  {
            String jsonString = GetJsonHelper.jsonGetRequest(url);

            JSONParser parser = new JSONParser();
            JSONObject json = (JSONObject) parser.parse(jsonString);

            if (json.size() > 0) {
                retrievedJsonData.put(season.getSeasonKey(), retrievedJsonData.getOrDefault(season.getSeasonKey(), 0) + 1);
            }

            JSONObject event = (JSONObject) json.get("event");
            Boolean confirmedLineups = (Boolean) event.get("confirmedLineups");
            if (!confirmedLineups) {
                //game is not within 1 hour played. no point getting in betting info, or player ratings/full time score as our algorithm needs confirmed lineups to work.
                return;
            }


            JSONObject homeTeam = (JSONObject) event.get("homeTeam");
            String homeTeamName = (String) homeTeam.get("name");

            JSONObject awayTeam = (JSONObject) event.get("awayTeam");
            String awayTeamName = (String) awayTeam.get("name");

            String gameDate = (String) event.get("formatedStartDate"); //SofaScore devs spelt formatted wrong.
            String[] dateInfo = gameDate.split("\\.");
            Date dateKey = DateHelper.createDateyyyyMMdd(dateInfo[2], dateInfo[1], dateInfo[0]);

            Team hTeam = season.getTeam(homeTeamName);
            if (hTeam == null) throw new RuntimeException("could not find team with string " + homeTeamName + ".\n the altered name for this team is " + Team.makeTeamNamesCompatible(homeTeamName));
            Match match = hTeam.getMatchFromAwayTeamName(awayTeamName);
            if (match == null) {
                //debugging used for Team.makeTeamsCompatible
                System.out.println(hTeam.getAllMatches().size());
                hTeam.getAllMatches().keySet().forEach(x -> {
                    System.out.println(
                            x + " :" +
                            hTeam.getAllMatches().get(x).getHomeTeam().getTeamName()      + " vs " +
                            hTeam.getAllMatches().get(x).getAwayTeam().getTeamName()
                    );
                });


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
                    ArrayList<Double> bettingOdds = new ArrayList<>();

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

        } catch (ParseException e) {}

        return new ArrayList<Double>(){{add(-1d); add(-1d); add(-1d);}};
    }




    public static void main(String[] args) {
        Season season = new Season("18-19");
        Team homeTeam = new Team("Burnley");
        Team awayTeam = new Team("Newcastle United");
        homeTeam.addMatch(new Match(homeTeam, awayTeam, DateHelper.createDateFromSQL("2018-11-26 20:30:00"), 1, 2));

        season.addNewTeam(homeTeam);
        season.addNewTeam(awayTeam);


        addInfoToGame(season, 7827995);

        System.out.println("hello");
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

            //NOTE: passing in gameId just for debugging purposes.
            HashMap<String, PlayerRating> homeRatings = getRatingsFromJson(homeTeam, gameId);
            HashMap<String, PlayerRating> awayRatings = getRatingsFromJson(awayTeam, gameId);

            if (homeRatings != null && awayRatings != null) {
                match.setHomePlayerRatings(homeRatings);
                match.setAwayPlayerRatings(awayRatings);
            }

//            System.out.println("\n New match");
//            System.out.println(homeRatings.size() + " " + match.getHomePlayerRatings().size());
//
//            System.out.println(awayRatings.size() + " " + match.getAwayPlayerRatings().size());
            

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
        if (!hasConfirmedLineups) return null;


        HashMap<String, PlayerRating> players = new HashMap<>();
        JSONArray lineups = (JSONArray) jsonObject.get("lineupsSorted");

        Iterator playersIterator = lineups.iterator();
        while (playersIterator.hasNext()) {
            JSONObject playerObj = (JSONObject) playersIterator.next();

            String playerName = (String) ((JSONObject) playerObj.get("player")).get("name");
            playerName = playerName.replaceAll("'", "");

            String rating = playerObj.get("rating").toString();
            try {
                PlayerRating playerRating = new PlayerRating(90, Double.parseDouble(playerObj.get("rating").toString()), playerName);

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
                    //for player out, we want to set minutes to be current minutes - time till end of game to account for if they didn't start the game.
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

                if (homeScore.equals("1") && awayScore.equals("0")) match.setFirstScorer(1);
                else if (homeScore.equals("0") && awayScore.equals("1")) match.setFirstScorer(2);
            }
        }
    }

    /*
     * Method will get all info for when games are happening, and update game time and gameId in database.
     *
     * Returns an ArrayList of game times.
     *
     * We should also add in a sofascore ID so that we don't have to scrape for all the games. we can just do 1 request - for the lineups.
     */
    public static ArrayList<Date> updateTodaysKickoffTimes() {
        League[] leagues = new League[]{
                new League(LeagueSeasonIds.EPL),
                new League(LeagueSeasonIds.LA_LIGA),
                new League(LeagueSeasonIds.BUNDESLIGA),
                new League(LeagueSeasonIds.SERIE_A),
                new League(LeagueSeasonIds.LIGUE_1),
                new League(LeagueSeasonIds.RUSSIA)
        };


        String today = DateHelper.turnDateToyyyyMMddString(new Date());
        String url = "https://www.sofascore.com/football//" + today + "/json";

        HashMap<String, String> leagueNames = new HashMap<>();
        for (League league: leagues) {
            leagueNames.put(league.getSeasonIds().getSofaScoreLeagueName(), league.getSeasonIds().getSofaScoreCountryName());
        }

        ArrayList<Date> dates = new ArrayList<>();

        try {
            String jsonString = GetJsonHelper.jsonGetRequest(url);

            JSONParser parser = new JSONParser();
            JSONObject json = (JSONObject) parser.parse(jsonString);

            JSONObject football = (JSONObject) json.get("sportItem");
            JSONArray tournaments = (JSONArray) football.get("tournaments");
            Iterator tournamentIterator = tournaments.iterator();

            DataSource.openConnection();
            while (tournamentIterator.hasNext()) {
                JSONObject currTournament = (JSONObject) tournamentIterator.next();
                JSONObject tournamentInfo = (JSONObject) currTournament.get("tournament");
                JSONObject countryInfo = (JSONObject) currTournament.get("category");

                String tournamentName = tournamentInfo.get("name").toString();
                String countryName = countryInfo.get("name").toString();

                if (leagueNames.getOrDefault(tournamentName, "NOT A REAL COUNTRY").equals(countryName)) {
                    //then we want to update that game in the database.

                    JSONArray events = (JSONArray) currTournament.get("events");
                    Iterator eventsIterator = events.iterator();

                    while (eventsIterator.hasNext()) {
                        JSONObject event = (JSONObject) eventsIterator.next();

                        String homeTeam = ((JSONObject) event.get("homeTeam")).get("name").toString();
                        String awayTeam = ((JSONObject) event.get("awayTeam")).get("name").toString();

                        int matchId = Integer.parseInt(event.get("id").toString());

                        String startDate = event.get("formatedStartDate").toString();
                        String startTime = event.get("startTime").toString();

                        String[] startDateParts = startDate.split("\\.");


                        //db dateString format is yyyy-mm-dd hh:mm:ss
                        String dateString = startDateParts[2] + "-" + startDateParts[1] + "-" + startDateParts[0] + " " + startTime + ":00";
                        Date date = DateHelper.createDateFromSQL(dateString);

                        //now need to do an update in matches db
                        String seasonStart = leagues[0].getCurrentSeason() + "-" + (leagues[0].getCurrentSeason()+1);

                        DataSource.changeMatchStartTime(seasonStart, homeTeam, awayTeam, dateString, matchId);

                        dates.add(date);
                    }
                }
            }


        } catch (ParseException e) {}
        finally {DataSource.closeConnection();}

        return dates;
    }


    public static void addLineupsToGamesAboutToStart(ArrayList<MatchToPredict> matches) {
        for (MatchToPredict match: matches) {
            String url = getPlayerRatingsUrl(match.getSofaScoreId());

            try {
                String jsonString = GetJsonHelper.jsonGetRequest(url);

                JSONParser parser = new JSONParser();
                JSONObject json = (JSONObject) parser.parse(jsonString);

                JSONObject homeTeam = (JSONObject) json.get("homeTeam");
                JSONObject awayTeam = (JSONObject) json.get("awayTeam");

                boolean homeConfirmedLineups = Boolean.parseBoolean(homeTeam.get("confirmedLineups").toString());
                boolean awayConfirmedLineups = Boolean.parseBoolean(awayTeam.get("confirmedLineups").toString());

                if (!homeConfirmedLineups || !awayConfirmedLineups) continue;

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

                    if (i == 0) match.setHomeTeamPlayers(lineup);
                    else match.setAwayTeamPlayers(lineup);

                    lineup = new ArrayList<>();
                }

            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
    }

}





























