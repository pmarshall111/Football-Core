package com.footballbettingcore.scrape;

import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.footballbettingcore.scrape.classes.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.simple.JSONArray;
import org.json.simple.JSONAware;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Understat {
    private static final Logger logger = LogManager.getLogger(Understat.class);

    private static final String UNDERSTAT_SITE = "https://understat.com/";

    public static void addLeaguesGames(League league) {
        int currentSeason = league.getStartYearFirstSeasonAvailable();
        int numbSeasons = league.getAllSeasons().size();

        for (int i = 0; i<numbSeasons; i++) {
            int seasonToGet = currentSeason+i;
            addSeasonsGames(league, seasonToGet);
        }
    }

    public static void main(String[] args) {
        addSeasonsGames(new League(LeagueIdsAndData.EPL), 21);
    }

    /*
     * Method will get teams expected goals for and against for each matchday.
     * Has parameters to only add games between 2 dates to scrape the most recent games.
     */
    public static void addSeasonsGames (League league, int seasonStartYear) {
        Season season = league.getSeason(seasonStartYear);
        String xml = getUnderstatXmlString(league, seasonStartYear);
        UnderstatData data = getSeasonsData(xml, seasonStartYear);
        JSONArray datesData = data.getDatesData();
        JSONObject teamsData = data.getTeamsData();
        addMatchesToSeason(season, datesData);
        addXgToMatches(season, teamsData);
    }

    public static void addMatchesToSeason(Season season, JSONArray datesData) {
        Iterator datesIterator = datesData.iterator();
        while (datesIterator.hasNext()) {
            JSONObject nextMatch = (JSONObject) datesIterator.next();
            String homeTeamName = (String) ((JSONObject) nextMatch.get("h")).get("title");
            String awayTeamName = (String) ((JSONObject) nextMatch.get("a")).get("title");
            Team homeTeam = season.getTeam(homeTeamName);
            Team awayTeam = season.getTeam(awayTeamName);
            if (homeTeam == null) {
                homeTeam = season.addNewTeam(new Team(homeTeamName));
            }
            if (awayTeam == null) {
                awayTeam = season.addNewTeam(new Team(awayTeamName));
            }

            String dateString = (String) nextMatch.get("datetime");
            //Correcting incorrect date from Understat data
            if (homeTeamName.equals("Caen") && awayTeamName.equals("Toulouse") && dateString.equals("2018-04-14 18:00:00")) {
                dateString = "2018-04-25 17:45:00";
            }
            Date date;
            try {
                date = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(dateString);
            } catch (java.text.ParseException e) {
                throw new RuntimeException("Bad dateTime from team " + homeTeamName + " vs " + awayTeamName + ". The date given is " + dateString);
            }

            JSONObject goals = (JSONObject) nextMatch.get("goals");
            String homeGoals = (String) goals.get("h");
            String awayGoals = (String) goals.get("a");
            Match match;
            if (homeGoals != null && awayGoals != null) {
                match = new Match(homeTeam, awayTeam, date, Integer.parseInt(homeGoals), Integer.parseInt(awayGoals));
            }
            else {
                match = new Match(homeTeam, awayTeam, date);
            }
            season.addNewMatch(match); //method will also add match to both teams' hashmap for faster lookups.
        }
    }

    public static void addXgToMatches(Season season, JSONObject teamsData) {
        Iterator teamsIterator = teamsData.values().iterator();
        while (teamsIterator.hasNext()) {
            JSONObject teamObj = (JSONObject) teamsIterator.next();
            String teamName = (String) teamObj.get("title");
            Team currTeam = season.getTeam(teamName);
            if (currTeam == null) {
                throw new RuntimeException("could not find " + teamName + " in season " + season.getSeasonKey());
            }

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

                Match currMatch = currTeam.getMatchFromDate(date);
                if (currMatch == null) {
                    throw new RuntimeException("Could not find match on " + date + " for team " + teamName);
                }
                boolean isHomeTeam = currMatch.getHomeTeam().getTeamName().equals(teamName);
                double xGF = Double.parseDouble(gameObj.get("npxG").toString());
                double xGA = Double.parseDouble(gameObj.get("npxGA").toString());
                currMatch.setHomeXGF(isHomeTeam ? xGF : xGA);
                currMatch.setAwayXGF(isHomeTeam ? xGA : xGF);
            }
        }
    }

    private static String getUnderstatXmlString(League league, int seasonStartYear) {
        try (final WebClient webClient = new WebClient()) {
            webClient.getOptions().setCssEnabled(false);
            webClient.getOptions().setJavaScriptEnabled(false);

            final HtmlPage page = webClient.getPage(UNDERSTAT_SITE + "league/" + league.getIdsAndData().getUnderstatUrl() + "/20" + seasonStartYear);
            return page.asXml();
        } catch (Exception e) {
            logger.error("Couldn't get Understat page", e);
            return null;
        }
    }

    public static UnderstatData getSeasonsData(String understatXml, int seasonStartYear) {
        //dates will give us all basic information about the game.
        //teams will give us an array of 38 matches for each team, with the non-penalty expected goals. no info about which team they played though.
        Matcher dates = Pattern.compile("var datesData\\s+= JSON.parse\\('([^)]+)'\\)").matcher(understatXml);
        Matcher teams = Pattern.compile("var teamsData\\s+= JSON.parse\\('([^)]+)'\\)").matcher(understatXml);

        JSONArray datesData;
        JSONObject teamsData;
        if (dates.find() && teams.find()) {
            datesData = (JSONArray) decodeAscii(dates.group(1));
            teamsData = (JSONObject) decodeAscii(teams.group(1));
            return new UnderstatData(datesData, teamsData);
        }

        throw new RuntimeException("could not find data from Underscored page in season " + seasonStartYear);
    }

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