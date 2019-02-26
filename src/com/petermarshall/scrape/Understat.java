package com.petermarshall.scrape;

import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.*;
import com.petermarshall.scrape.classes.*;
import org.json.simple.JSONArray;
import org.json.simple.JSONAware;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.petermarshall.Main.MOST_RECENT_SEASON_END;

public class Understat {

    private static final String UNDERSTAT_SITE = "https://understat.com/";

    public static void addLeaguesGames(League league) {
        int currentSeason = league.getStartYearFirstSeasonAvailable();

        while (currentSeason < MOST_RECENT_SEASON_END) {
            addSeasonsGames(league, currentSeason, null, null);
            currentSeason++;
        }
    }

    /*
     * Method will get teams expected goals for and against for each matchday.
     * Should it also calculate a rating as to how well each team has done over the last 5 games?
     *
     * Has parameters to only add games between 2 dates to scrape the most recent games.
     */
    public static void addSeasonsGames (League league, int seasonStartYear, Date earliestDate, Date latestDate) {
        UnderstatData data = getSeasonsData(league, seasonStartYear);

        JSONArray datesData = data.getDatesData();
        JSONObject teamsData = data.getTeamsData();

            Season season = league.getSeason(seasonStartYear);


            Iterator datesIterator = datesData.iterator();
            while (datesIterator.hasNext()) {
                JSONObject nextMatch = (JSONObject) datesIterator.next();

                String homeTeamName = (String) ((JSONObject) nextMatch.get("h")).get("title");
                String awayTeamName = (String) ((JSONObject) nextMatch.get("a")).get("title");


                Team homeTeam = season.getTeam(homeTeamName);
                if (homeTeam == null) homeTeam = season.addNewTeam(new Team(homeTeamName));
                Team awayTeam = season.getTeam(awayTeamName);
                if (awayTeam == null) awayTeam = season.addNewTeam(new Team(awayTeamName));


                String dateString = (String) nextMatch.get("datetime");
                //Correcting incorrect date in Understat data
                if (homeTeamName.equals("Caen") && awayTeamName.equals("Toulouse") && dateString.equals("2018-04-14 18:00:00")) {
                    dateString = "2018-04-25 17:45:00";
                }
                Date date;
                try {
                    date = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(dateString);
                } catch (java.text.ParseException e) {
                    throw new RuntimeException("Bad dateTime from team " + homeTeamName + " vs " + awayTeamName + ". The date given is " + dateString);
                }

                //test to only add in games that are only new games, or games given between 2 dates.
                if (earliestDate != null && latestDate != null &&
                        (!date.before(latestDate) || !date.after(earliestDate))) continue;


                JSONObject goals = (JSONObject) nextMatch.get("goals");

                String homeGoals = (String) goals.get("h");
                String awayGoals = (String) goals.get("a");

                Match match;
                if (homeGoals != null && awayGoals != null)
                    match = new Match(homeTeam, awayTeam, date, Integer.parseInt(homeGoals), Integer.parseInt(awayGoals));
                else match = new Match(homeTeam, awayTeam, date);


                season.addNewMatch(match);

                //adding match to our hashmap in team so we can quickly find matches as well as having a collection of all of them.
                homeTeam.addMatch(match);
                awayTeam.addMatch(match);
            }

            Iterator teamsIterator = teamsData.values().iterator();
            while (teamsIterator.hasNext()) {
                JSONObject teamObj = (JSONObject) teamsIterator.next();

                String teamName = (String) teamObj.get("title");
                Team currTeam = season.getTeam(teamName);
                if (currTeam == null)
                    throw new RuntimeException("could not find " + teamName + " in season " + season.getSeasonKey());


                JSONArray games = (JSONArray) teamObj.get("history");

                Iterator gamesIterator = games.iterator();
                while (gamesIterator.hasNext()) {
                    JSONObject gameObj = (JSONObject) gamesIterator.next();

                    String dateTime = (String) gameObj.get("date");
                    //correcting incorrect Understat date
                    if ((teamName.equals("Caen") || teamName.equals("Toulouse")) && dateTime.equals("2018-04-14 18:00:00")) {
                        dateTime = "2018-04-25 17:45:00";
                    }
                    Date date;
                    try {
                        date = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(dateTime);
                    } catch (java.text.ParseException e) {
                        throw new RuntimeException("Bad dateTime from team " + teamName + ". The date given is " + dateTime);
                    }


                    //test to only add in games that are only new games, or games given between 2 dates.
                    if (earliestDate != null && latestDate != null &&
                            (!date.before(latestDate) || !date.after(earliestDate))) continue;


                    double xGF = Double.parseDouble(gameObj.get("npxG").toString());
                    double xGA = Double.parseDouble(gameObj.get("npxGA").toString());



                    Match currMatch = currTeam.getMatchFromDate(date);
                    if (currMatch == null)
                        throw new RuntimeException("Could not find match on " + date + " for team " + teamName);

                    boolean isHomeTeam = currMatch.getHomeTeam().getTeamName().equals(teamName);

                    currMatch.setHomeXGF(isHomeTeam ? xGF : xGA);
                    currMatch.setAwayXGF(isHomeTeam ? xGA : xGF);
                }
            }

    }


    public static void addDataToGamesFromLeague(League league) {
        int currentSeason = league.getStartYearFirstSeasonAvailable();

        while (currentSeason < MOST_RECENT_SEASON_END) {
            if (league.getSeason(currentSeason).hasMatches()) {
                addDataToGamesInSeason(league, currentSeason);
            }
            currentSeason++;
        }
    }


    /*
     * League should be fully populated before this method is called. Strictly just adds data to an already created match.
     * Adds GF&GA and xGF&xGA
     */
    private static void addDataToGamesInSeason(League league, int seasonStartYear) {
        UnderstatData data = getSeasonsData(league, seasonStartYear);

        JSONArray datesData = data.getDatesData();
        JSONObject teamsData = data.getTeamsData();

        Season season = league.getSeason(seasonStartYear);
        HashSet<Match> matchesToUpdate = new HashSet<>(season.getAllMatches());


        Iterator datesIterator = datesData.iterator();
        while (datesIterator.hasNext()) {
            JSONObject nextMatch = (JSONObject) datesIterator.next();

            String homeTeamName = (String) ((JSONObject) nextMatch.get("h")).get("title");
            String awayTeamName = (String) ((JSONObject) nextMatch.get("a")).get("title");

            Match thisMatch = findMatchBetweenTeams(matchesToUpdate, homeTeamName, awayTeamName);
            if (thisMatch != null) {
                // will already have the date info in the match created from data from database.
                JSONObject goals = (JSONObject) nextMatch.get("goals");

                try {
                    int homeGoals = Integer.parseInt(goals.get("h").toString());
                    int awayGoals = Integer.parseInt(goals.get("a").toString());
                    thisMatch.setHomeScore(homeGoals);
                    thisMatch.setAwayScore(awayGoals);
                } catch (NullPointerException e) {
                    //understat most likely doesn't have goal stats because the game was postponed.
                }
            }
        }


        Iterator teamsIterator = teamsData.values().iterator();
        while (teamsIterator.hasNext()) {
            JSONObject teamObj = (JSONObject) teamsIterator.next();

            String teamName = (String) teamObj.get("title");
            Team currTeam = season.getTeam(teamName);
            if (currTeam == null) continue;


            JSONArray games = (JSONArray) teamObj.get("history");
            Iterator gamesIterator = games.iterator();
            while (gamesIterator.hasNext()) {
                JSONObject gameObj = (JSONObject) gamesIterator.next();

                String dateTime = (String) gameObj.get("date");
                Date date;
                try {
                    date = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(dateTime);
                } catch (java.text.ParseException e) {
                    throw new RuntimeException("Bad dateTime from team " + teamName + ". The date given is " + dateTime);
                }

                Match currMatch = currTeam.getMatchFromDate(date);
                if (currMatch == null) continue;

                boolean isHomeTeam = currMatch.getHomeTeam().getTeamName().equals(teamName);
                double xGF = Double.parseDouble(gameObj.get("npxG").toString());
                double xGA = Double.parseDouble(gameObj.get("npxGA").toString());

                currMatch.setHomeXGF(isHomeTeam ? xGF : xGA);
                currMatch.setAwayXGF(isHomeTeam ? xGA : xGF);
            }
        }
    }



    private static Match findMatchBetweenTeams(HashSet<Match> matches, String homeTeamName, String awayTeamName) {
        return matches.stream()
                .filter(m -> m.getHomeTeam().getTeamName().equals(homeTeamName) && m.getAwayTeam().getTeamName().equals(awayTeamName))
                .findFirst()
                .orElse(null);
    }

    private static UnderstatData getSeasonsData(League league, int seasonStartYear) {
        try (final WebClient webClient = new WebClient()) {
            webClient.getOptions().setCssEnabled(false);
            webClient.getOptions().setJavaScriptEnabled(false);

            final HtmlPage page = webClient.getPage(UNDERSTAT_SITE + "league/" + league.getSeasonIds().getUnderstatUrl() + "/20" + seasonStartYear);

            //dates will give us all basic information about the game.
            //teams will give us an array of 38 matches for each team, with the non-penalty expected goals. no info about which team they played though.
            Matcher dates = Pattern.compile("var datesData\\s+= JSON.parse\\('([^)]+)'\\)").matcher(page.asXml());
            Matcher teams = Pattern.compile("var teamsData\\s+= JSON.parse\\('([^)]+)'\\)").matcher(page.asXml());

            JSONArray datesData;
            JSONObject teamsData;

            if (dates.find() && teams.find()) {
                datesData = (JSONArray) decodeAscii(dates.group(1));
                teamsData = (JSONObject) decodeAscii(teams.group(1));
                return new UnderstatData(datesData, teamsData);
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
            e.printStackTrace();
        }

        throw new RuntimeException("could not find data from Underscored page in season " + seasonStartYear);
    }



//    public static void getLastYearsXG(League league) {
//        try (final WebClient webClient = new WebClient()) {
//            JavaScriptErrorListener er = webClient.getJavaScriptErrorListener();
//
//            webClient.getOptions().setCssEnabled(false);
//            webClient.getOptions().setJavaScriptEnabled(false);
//
//            final HtmlPage page = webClient.getPage("https://understat.com/league/EPL/2017");
//
////            System.out.println(page.asXml());
//
//            Matcher teamsData = Pattern.compile("var teamsData\\s+= JSON.parse\\('([^)]+)'\\)").matcher(page.asXml());
//
//            JSONObject teamsHistory;
//            if (teamsData.find()) teamsHistory = (JSONObject) decodeAscii(teamsData.group(1));
//            else throw new RuntimeException("No last years teamsdata found");
//
//            //we go through each team and calc how many points they got and goals they scored. this will be used to determine bottom 3
//            // teams so we can replace their stats with the promoted 3 teams. we will also count npXGF and npXGA.
//
//            Iterator iterator = teamsHistory.keySet().iterator();
//
//            while (iterator.hasNext()) {
//                JSONObject teamObj = (JSONObject) teamsHistory.get(iterator.next());
//                String teamName = (String) teamObj.get("title");
//
//                JSONArray matches = (JSONArray) teamObj.get("history");
//
//                int points = 0, goalsFor = 0, goalsAg = 0;
//                double xGF = 0, xGA = 0;
//
//                ListIterator listIterator = matches.listIterator();
//                while (listIterator.hasNext()) {
//                    JSONObject match = (JSONObject) listIterator.next();
//
//                    points += (long)match.get("pts");
//                    goalsFor += (long) match.get("scored");
//                    goalsAg += (long) match.get("missed");
//                    try {
//                        xGF += (double) match.get("npxG");
//                    } catch (ClassCastException e) {
//                        xGF += (long) match.get("npxG");
//                    }
//                    try {
//                        xGA += (double) match.get("npxGA");
//                    } catch (ClassCastException e) {
//                        xGA += (long) match.get("npxGA");
//                    }
//                }
//
//
//                league.addTeam(new Team(teamName, xGF, xGA, points, goalsFor, goalsAg));
//            }
//
//        }
//        catch (Exception e) {
//        System.out.println(e);
//        e.printStackTrace();
//        }
//    }
    
    /*
     * Understat have their data coming in as hard coded values within the xml, instead of XHR.
     * Method turns a hex encoded string and returns a JSONAware interface (can be casted to a JSONObject or a JSONArray).
     */
    public static JSONAware decodeAscii (String input) {
        Matcher match = Pattern.compile("\\\\x([0-9A-F]{2})").matcher(input);
        StringBuffer result = new StringBuffer();

        while (match.find()) {
            match.appendReplacement(result, ((char) Integer.parseInt(match.group(1), 16)) + "");
        }
        match.appendTail(result);

        JSONParser parser = new JSONParser();
        JSONAware json = null;

//        System.out.println("result \n" + result.toString());
        try {
            json =  (JSONAware) parser.parse(result.toString());
        } catch (ParseException e) {
            System.out.println(e.getMessage());
            e.printStackTrace();
        }

        return json;
    }
}
