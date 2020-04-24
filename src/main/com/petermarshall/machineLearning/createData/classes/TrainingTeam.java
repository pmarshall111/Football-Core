package com.petermarshall.machineLearning.createData.classes;

import java.util.ArrayList;
import java.util.HashMap;


/*
 * Class to be used as a wrapper for a teams league seasons. Created in this way so we can collect a teams history against each team in a single hashmap instead
 * of having to loop through all the seasons and then all the games in those seasons to find previous results.
 */
public class TrainingTeam {
    private String teamName;

    private HashMap<Integer, TrainingTeamsSeason> seasons;
    private HashMap<String, ArrayList<TrainingMatch>> historyAgainstOtherTeams;

    public TrainingTeam(String teamName) {
        this.teamName = teamName;
        this.seasons = new HashMap<>();
        this.historyAgainstOtherTeams = new HashMap<>();
    }

    /*
     * Gets the requested season.
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
     * If we have no previous matches, the method will return 1.5. Reasoning for this is that if we returned 0, the system would think
     * that the team had lost all it's previous games. Instead we will average between won (3) and lost(0).
     */
    public double getPointsOfLastMatchups(String oppTeamName, GamesSelector gamesSelector, int seasonYearStart) {
        //need methods within the training match class
        ArrayList<TrainingMatch> previousMatches = getPreviousMatchesWithTeam(oppTeamName);

        if (previousMatches.size() == 0) return 1.5;
        else {
            int points = 0, matches = 0;

            for (TrainingMatch match: previousMatches) {
                if (match.isInOrAfterSeasonYearStart(seasonYearStart)) {
                    if (gamesSelector.getSetting() != 2) {
                        points += match.getPoints(this.teamName);
                        matches++;
                    }
                    else if (gamesSelector.getSetting() != 1) {
                        points += match.getPoints(this.teamName);
                        matches++;
                    }
                }
            }

            return matches > 0 ? (points/matches) : 1.5;
        }
    }
    private ArrayList<TrainingMatch> getPreviousMatchesWithTeam(String oppTeamName) {
        return historyAgainstOtherTeams.getOrDefault(oppTeamName, new ArrayList<>());
    }

    public String getTeamName() {
        return teamName;
    }
}
