package com.petermarshall.scrape.classes;

import java.util.ArrayList;
import java.util.HashMap;

//Season class goes within the League class.
public class Season {
    private final String seasonKey; //format 14-15 or 19-20
    private HashMap<String, Team> teams;
    private ArrayList<Match> matches;

    public Season(String seasonKey) {
        this.seasonKey = seasonKey;
        this.teams = new HashMap<>();
        this.matches = new ArrayList<>();
    }

    public Match addNewMatch(Match match) {
        this.matches.add(match);
        return match;
    }

    public Team addNewTeam(Team team) {
        this.teams.put(team.getTeamName(), team);
        return team;
    }

    public String getSeasonKey() {
        return seasonKey;
    }

    public int getSeasonYearStart() {
        return Integer.parseInt(seasonKey.split("-")[0]);
    }

    public Team getTeam(String teamName) {
        //compatible name needed to make the team names the same from sofascore to understat. teams in db use understat names.
        String compatibleTeamName = Team.makeTeamNamesCompatible(teamName);
        return this.teams.getOrDefault(compatibleTeamName, null);
    }

    public HashMap<String, Team> getAllTeams() {
        return teams;
    }

    public ArrayList<Match> getAllMatches() {
        return matches;
    }

    public boolean hasMatches() {return matches.size() > 0;}

    public static String getSeasonKeyFromYearStart(int yearStart) {
        return yearStart + "-" + (yearStart+1);
    }
}
