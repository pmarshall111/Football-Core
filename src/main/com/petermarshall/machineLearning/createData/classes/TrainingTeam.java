package com.petermarshall.machineLearning.createData.classes;

import java.util.ArrayList;
import java.util.HashMap;

import static com.petermarshall.machineLearning.createData.classes.TrainingTeamsSeason.AVG_PPG;

/*
 * Class to be used as a wrapper for a teams league seasons. Created in this way so we can collect a teams history against each team in a single hashmap instead
 * of having to loop through all the seasons and then all the games in those seasons to find previous results.
 */
public class TrainingTeam {
    private final String teamName;
    private final HashMap<Integer, TrainingTeamsSeason> seasons;
    private final HashMap<String, ArrayList<TrainingMatch>> historyAgainstOtherTeams;

    public TrainingTeam(String teamName) {
        this.teamName = teamName;
        this.seasons = new HashMap<>();
        this.historyAgainstOtherTeams = new HashMap<>();
    }

    /*
     * DEFAULT: If requested season not present, it creates one and adds it to a hashmap.
     */
    public TrainingTeamsSeason getTeamsSeason(int seasonYearStart) {
        TrainingTeamsSeason reqSeason = this.seasons.getOrDefault(seasonYearStart, null);
        if (reqSeason == null) {
            reqSeason = new TrainingTeamsSeason(seasonYearStart);
            this.seasons.put(seasonYearStart, reqSeason);
        }
        return reqSeason;
    }

    /*
     * Adds a match to the result history with an opponent team.
     */
    public void addMatchWithTeam(String oppTeamName, TrainingMatch match) {
        ArrayList<TrainingMatch> matchesVsATeam = historyAgainstOtherTeams.getOrDefault(oppTeamName, null);
        if (matchesVsATeam == null) {
            matchesVsATeam = new ArrayList<>();
            historyAgainstOtherTeams.put(oppTeamName, matchesVsATeam);
        }
        matchesVsATeam.add(match);
    }

    /*
     * Filters through matches between teams over previous seasons. Can specify for just home & away, or both (GamesSelector). Also
     * can specify how many seasons back you want to go.
     *
     * If we have no previous matches, the method will return average ppg over all matches in database
     */
    public double getPointsOfLastMatchups(String oppTeamName, GamesSelector gamesSelector, int seasonYearStart) {
        ArrayList<TrainingMatch> previousMatches = getPreviousMatchesWithTeam(oppTeamName);

        if (previousMatches.size() == 0) return AVG_PPG;
        else {
            int points = 0, matches = 0;

            for (TrainingMatch match: previousMatches) {
                if (match.isInOrAfterSeasonYearStart(seasonYearStart)) {
                    if (!gamesSelector.equals(GamesSelector.ONLY_AWAY_GAMES) && match.isHomeTeam(this.teamName)) {
                        points += match.getPoints(this.teamName);
                        matches++;
                    }
                    else if (!gamesSelector.equals(GamesSelector.ONLY_HOME_GAMES) && !match.isHomeTeam(this.teamName)) {
                        points += match.getPoints(this.teamName);
                        matches++;
                    }
                }
            }

            return matches > 0 ? ( ((double) points)/matches ) : AVG_PPG;
        }
    }
    private ArrayList<TrainingMatch> getPreviousMatchesWithTeam(String oppTeamName) {
        return historyAgainstOtherTeams.getOrDefault(oppTeamName, new ArrayList<>());
    }

    public String getTeamName() {
        return teamName;
    }
}
