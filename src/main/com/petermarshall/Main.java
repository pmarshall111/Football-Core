package com.petermarshall;

import com.petermarshall.scrape.classes.League;
import com.petermarshall.scrape.classes.LeagueSeasonIds;
import org.apache.commons.logging.LogFactory;

public class Main {

    public static void main(String[] args) {
        LogFactory.getFactory().setAttribute("org.apache.commons.logging.Log", "org.apache.commons.logging.impl.NoOpLog");
//        System.out.println("running main");
//
//        try (final WebClient webClient = new WebClient()) {
//            webClient.getOptions().setCssEnabled(false);
//            webClient.getOptions().setJavaScriptEnabled(false);
//
//            final HtmlPage page = webClient.getPage("https://understat.com/league/EPL/2017");
//
//            //dates will give us all basic information about the game.
//            //teams will give us an array of 38 matches for each team, with the non-penalty expected goals. no info about which team they played though.
//            Matcher dates = Pattern.compile("var datesData\\s+= JSON.parse\\('([^)]+)'\\)").matcher(page.asXml());
//            Matcher teams = Pattern.compile("var teamsData\\s+= JSON.parse\\('([^)]+)'\\)").matcher(page.asXml());
//
//            JSONArray datesData;
//            JSONObject teamsData;
//
//            if (dates.find() && teams.find()) {
//                datesData = (JSONArray) decodeAscii(dates.group(1));
//                teamsData = (JSONObject) decodeAscii(teams.group(1));
//            } else throw new RuntimeException("could not find data from Underscored page in season ");
//        } catch (Exception e) {}

//        Set<Integer> numb = SofaScore.getGamesOfLeaguesSeason(17, 13380);
//        System.out.println(numb.size());


//        SimpleDateFormat fmt = new SimpleDateFormat("MMM-dd-yyyy");
//
//
//        try {
//            Date date = fmt.parse("Aug" + "-" + 31 + "-" + 2018);
//            Date date2 = fmt.parse("Aug" + "-" + 1 + "-" + 2018);
//            System.out.println(DateHelper.add1DayToDate(date));
//            System.out.println(DateHelper.subtract1DayFromDate(date2));
//        } catch (ParseException e) {
//            System.out.println(e.getMessage());
//            e.printStackTrace();
//        }




//        League league = new League(LeagueSeasonIds.LIGUE_1);
//        league.scrapeEverything();
//
//        for (String key: SofaScore.functionCalled.keySet()) {
//            System.out.println(key + ":\n\t FunctionCalled " + SofaScore.functionCalled.get(key) + "\n\t RetrivedData " + SofaScore.retrievedJsonData.get(key) + "\n\t Start of betting odds: " + SofaScore.gotToStartOfOdds.get(key) +
//                    "\n\t AddedBetting odds: " + SofaScore.numbBettingAdded.get(key));
//        }
//
//        System.out.println("starting db process");
//
//        DataSource.openConnection();
//
//        DataSource.initDB();
//    DataSource.writeLeagueToDb(league);
//
//        DataSource.closeConnection();

//        int seasonStart = 17;
//
//        League league = new League(LeagueSeasonIds.LIGUE_1);
//        league.scrapeOneSeason(seasonStart);
//
//        for (String key: SofaScore.functionCalled.keySet()) {
//            System.out.println(key + ":\n\t FunctionCalled " + SofaScore.functionCalled.get(key) + "\n\t RetrivedData " + SofaScore.retrievedJsonData.get(key) + "\n\t Start of betting odds: " + SofaScore.gotToStartOfOdds.get(key) +
//                    "\n\t AddedBetting odds: " + SofaScore.numbBettingAdded.get(key));
//        }
//
//        Season season = league.getSeason(seasonStart);
//
//
//        System.out.println("starting db process");
//
//        DataSource.openConnection();
//
//        DataSource.initDB();
//        Season season = league.getSeason(seasonStart);
//        DataSource.addRemainingMatchesOfSeason(league, season, 2);
//
//        DataSource.closeConnection();


//
//        String text = GetJsonHelper.jsonGetRequest("http://www.sofascore.com/u-tournament/17/season/13380/standings/json");
//        System.out.println(text);

//        LeagueSeasonIds[] vals = LeagueSeasonIds.values();
//
//        for (LeagueSeasonIds v: vals) {
//            System.out.println(v.name());
//        }

        League league = new League(LeagueSeasonIds.EPL);
        league.scrapePlayedGames();

        System.out.println("pausing here");

    }

    //alpha value dictates how much of the new value is dictated by the old value.

    private static final double ALPHA_VALUE = 0.8;

    /*
     * Method will compute a normal average from the first 5 games, and then move onto calcultating the average using an exponentially
     * weighted formula. This is to avoid the first matchday being unrealisticly important in our calculations.
     */
    public static double calcExponentialWeightedAverage(int gameWeek, double currExpWeightedAvg, double newGameAdjustedXG) {
        if (gameWeek > 5) return ALPHA_VALUE*currExpWeightedAvg + (1-ALPHA_VALUE)*newGameAdjustedXG;
        else return ((currExpWeightedAvg*gameWeek) + newGameAdjustedXG)/(gameWeek+1);
    }
    public static double calcExponentialWeightedAverage(double newGameAdjustedXG) {
        return newGameAdjustedXG;
    }
}
