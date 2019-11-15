package com.petermarshall.scrape.classes;

import java.util.ArrayList;
import java.util.HashMap;

//Season class goes within the League class.
public class Season {
    private final String seasonKey; //TODO: WTF IS A SEASON KEY?? NEEDS TO BE MORE DESCRIPTIVE. SEEMS TO BE THE YEARS IN OPERATION. DB HAS BEEN CHANGED TO USE JUST THE BEGINNING YEAR.
    private HashMap<String, Team> teams;
    private ArrayList<Match> matches;

    public Season(String seasonKey) {
        this.seasonKey = seasonKey;

        this.teams = new HashMap<>();
        this.matches = new ArrayList<>();
    }

    public void addNewMatch(Match match) {
        this.matches.add(match);
    }

    public Team addNewTeam(Team team) {
        this.teams.put(team.getTeamName(), team);
        return team;
    }




    public String getSeasonKey() {
        return seasonKey;
    }

    public Team getTeam(String teamName) {
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

}
