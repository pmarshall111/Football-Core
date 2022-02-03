package com.footballbettingcore.scrape;

import com.footballbettingcore.machineLearning.createData.classes.TrainingMatch;
import com.footballbettingcore.scrape.classes.LeagueIdsAndData;
import com.footballbettingcore.scrape.classes.Match;
import com.footballbettingcore.scrape.classes.Team;
import com.footballbettingcore.utils.DateHelper;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Array;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.*;

public class FiveThirtyEight {
    private static final Logger logger = LogManager.getLogger(FiveThirtyEight.class);

    private static final String ALL_GAMES_CSV = "https://projects.fivethirtyeight.com/soccer-api/club/spi_matches.csv";

    public static void addPredictionsToTrainingMatches(ArrayList<TrainingMatch> matches) {
        HashMap<String, HashMap<String, HashMap<Integer, Match>>> fiveThirtyEightMatches = getFiveThirtyEightMatches();
        matches.forEach(match -> {
            if (match.getSeasonYearStart() >= 17) {
                try {
                    String homeTeam = match.getHomeTeamName();
                    String awayTeam = match.getAwayTeamName();
                    HashMap<Integer, Match> matchesBetweenTeams = fiveThirtyEightMatches.get(homeTeam).get(awayTeam);
                    int seasonYearEnd = DateHelper.getEndingYearForSeason(match.getKickoffTime());
                    Match m538 = matchesBetweenTeams.get(seasonYearEnd);
                    match.setFiveThirtyEightProbabilities(m538.getFiveThirtyEightPredictions());
                } catch (Exception e) {
                    boolean hasHomeTeam = fiveThirtyEightMatches.containsKey(match.getHomeTeamName());
                    Boolean hasAwayTeam = null;
                    if (hasHomeTeam) {
                        hasAwayTeam = fiveThirtyEightMatches.get(match.getHomeTeamName()).containsKey(match.getAwayTeamName());
                        if (hasAwayTeam) {
                            HashMap<Integer, Match> matchMap = fiveThirtyEightMatches.get(match.getHomeTeamName()).get(match.getAwayTeamName());
                            System.out.println(matchMap.size());
                        }
                    }
                    System.out.println("Has home team: " + hasHomeTeam);
                    System.out.println("Has away team: " + hasAwayTeam);
                    logger.error(match.getMatchString());
                    logger.error(e.getStackTrace());
                }
            }
        });
    }

    public static HashMap<String, HashMap<String, HashMap<Integer, Match>>> getFiveThirtyEightMatches() {
        ArrayList<Match> allMatches = readCsvFromUrl(ALL_GAMES_CSV);
        HashMap<String, HashMap<String, HashMap<Integer, Match>>> homeTeamAwayTeamMatches = new HashMap<>();
        allMatches.forEach(match -> {
            String homeTeam = match.getHomeTeamName();
            String awayTeam = match.getAwayTeamName();
            homeTeamAwayTeamMatches.putIfAbsent(homeTeam, new HashMap<>());
            HashMap<String, HashMap<Integer, Match>> matchesAgainstAwayTeams = homeTeamAwayTeamMatches.get(homeTeam);
            matchesAgainstAwayTeams.putIfAbsent(awayTeam, new HashMap<>());
            HashMap<Integer, Match> matchesBySeasonYearStart = matchesAgainstAwayTeams.get(awayTeam);
            matchesBySeasonYearStart.put(DateHelper.getEndingYearForSeason(match.getKickoffTime()), match);
        });
        return homeTeamAwayTeamMatches;
    }

    public static ArrayList<Match> readCsvFromUrl(String url) {
        ArrayList<Match> records = new ArrayList<>();
        CSVFormat csvFormat = CSVFormat.DEFAULT.withFirstRecordAsHeader().withIgnoreHeaderCase();

        try(CSVParser csvParser = CSVParser.parse(new File("/home/peter/Downloads/spi_matches.csv"), StandardCharsets.UTF_8, csvFormat)) {
            for(CSVRecord csvRecord : csvParser) {
                try {
                    int leagueId = Integer.parseInt(csvRecord.get("league_id"));
                    if (LeagueIdsAndData.isRelevantFiveThirtyEightId(leagueId)) {
                        Team homeTeam = new Team(Team.match538TeamNamesToUnderstat(csvRecord.get("team1")));
                        Team awayTeam = new Team(Team.match538TeamNamesToUnderstat(csvRecord.get("team2")));
                        int[] dateString = Arrays.stream(csvRecord.get("date").split("-")).mapToInt(Integer::parseInt).toArray();
                        Date date = new GregorianCalendar(dateString[0], dateString[1]-1, dateString[2]).getTime();
                        Match match = new Match(homeTeam, awayTeam, date, Integer.parseInt(csvRecord.get("score1")), Integer.parseInt(csvRecord.get("score2")));
                        match.setFiveThirtyEightPredictions(new double[]{Double.parseDouble(csvRecord.get("prob1")), Double.parseDouble(csvRecord.get("probtie")), Double.parseDouble(csvRecord.get("prob2"))});
                        records.add(match);
                    }
                } catch(NumberFormatException e) {
                    // do nothing. Means that a match didn't have a score or XG
                }
            }
        } catch (IOException e) {
            logger.error(e.getStackTrace());
        }
        return records;
    }

    public static void main(String[] args) {
        ArrayList<Match> matches = readCsvFromUrl("has");
        System.out.println(matches.size());
    }
}
