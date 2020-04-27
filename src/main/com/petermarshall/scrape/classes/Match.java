package com.petermarshall.scrape.classes;

import java.util.*;

public class Match {
    private final Team homeTeam;
    private final Team awayTeam;
    private double homeXGF = -1;
    private double awayXGF = -1;
    private int homeScore;
    private int awayScore;
    private int firstScorer; //1 means hometeam, 2 means awayteam
    private Date kickoffTime;
    //player ratings data will come from sofascore scraper
    private HashMap<String, PlayerRating> homePlayerRatings;
    private HashMap<String, PlayerRating> awayPlayerRatings;
    private ArrayList<Double> homeDrawAwayOdds;
    private int sofaScoreGameId;

    /*
     * Constructor will be initialised by Understat scraper, which will first look at the dates source so we can get the team names + kickoff times.
     * Then the understat scraper can set the xG info later with setter functions.
     * Info from understat comes from 2 different sources which is why setting info is partly in constructor and partly using setters.
     *
     * Also can be created from DataSource if we have games in database without complete data.
     */
    public Match(Team homeTeam, Team awayTeam, Date kickoffTime) {
        this(homeTeam, awayTeam, kickoffTime, -1, -1);
    }
    public Match(Team homeTeam, Team awayTeam, Date kickoffTime, int homeScore, int awayScore) {
        this.homeTeam = homeTeam;
        this.awayTeam = awayTeam;
        this.kickoffTime = kickoffTime;
        this.homeScore = homeScore;
        this.awayScore = awayScore;
        this.firstScorer = -1;
        this.homePlayerRatings = new HashMap<>();
        this.awayPlayerRatings = new HashMap<>();
        this.homeDrawAwayOdds = new ArrayList<>();
        //initialising so we have something to put into our database.
        this.homeDrawAwayOdds.add(-1d);
        this.homeDrawAwayOdds.add(-1d);
        this.homeDrawAwayOdds.add(-1d);
    }

    public void setHomeXGF(double homeXGF) {
        this.homeXGF = homeXGF;
    }
    public void setAwayXGF(double awayXGF) {
        this.awayXGF = awayXGF;
    }

    public Team getHomeTeam() {
        return homeTeam;
    }
    public Team getAwayTeam() {
        return awayTeam;
    }

    public boolean isHomeTeam(String homeTeamName) {return homeTeam.getTeamName().equals(homeTeamName);}
    public boolean isAwayTeam(String awayTeamName) {return awayTeam.getTeamName().equals(awayTeamName);}

    public double getHomeXGF() {
        return homeXGF;
    }
    public double getAwayXGF() {
        return awayXGF;
    }

    public Date getKickoffTime() {
        return kickoffTime;
    }

    //changes kickoff time for the match and also changes the date in each teams' matchmap so the match can be found via the new date
    public void setKickoffTime(Date kickoffTime) {
        HashMap<Date, Match> homeTeamHashMap = this.homeTeam.getAllMatches();
        HashMap<Date, Match> awayTeamHashMap = this.awayTeam.getAllMatches();
        homeTeamHashMap.put(kickoffTime, this);
        homeTeamHashMap.remove(this.kickoffTime);
        awayTeamHashMap.put(kickoffTime, this);
        awayTeamHashMap.remove(this.kickoffTime);
        this.kickoffTime = kickoffTime;
    }

    public int getHomeScore() {
        return homeScore;
    }
    public int getAwayScore() {
        return awayScore;
    }
    public void setHomeScore(int homeScore) {
        this.homeScore = homeScore;
    }
    public void setAwayScore(int awayScore) {
        this.awayScore = awayScore;
    }

    public int getFirstScorer() {
        return firstScorer;
    }
    public void setFirstScorer(int firstScorer) {
        if (this.homeScore == -1 || this.awayScore == -1) throw new RuntimeException("Error trying to setFirstScorer for match " +
                this.homeTeam.getTeamName() + " vs " + this.awayTeam.getTeamName() + ". Cannot set firstScorer because scores have not been set.");
        else if (this.homeScore == 0 && this.awayScore == 0 && firstScorer != 0) throw new RuntimeException("Error trying to setFirstScorer for match " +
                this.homeTeam.getTeamName() + " vs " + this.awayTeam.getTeamName() + ". Cannot set firstScorer to value other than 0 (no first scorer) when scores are 0-0.");

        this.firstScorer = firstScorer;
    }

    public HashMap<String, PlayerRating> getHomePlayerRatings() {
        return homePlayerRatings;
    }
    public void setHomePlayerRatings(HashMap<String, PlayerRating> homePlayerRatings) {
        this.homePlayerRatings = homePlayerRatings;
    }

    public HashMap<String, PlayerRating> getAwayPlayerRatings() {
        return awayPlayerRatings;
    }
    public void setAwayPlayerRatings(HashMap<String, PlayerRating> awayPlayerRatings) {
        this.awayPlayerRatings = awayPlayerRatings;
    }

    public ArrayList<Double> getHomeDrawAwayOdds() {
        return homeDrawAwayOdds;
    }
    public double getHomeOdds() {
        return this.homeDrawAwayOdds.get(0);
    }
    public double getDrawOdds() {
        return this.homeDrawAwayOdds.get(1);
    }
    public double getAwayOdds() {
        return this.homeDrawAwayOdds.get(2);
    }
    public void setHomeDrawAwayOdds(ArrayList<Double> homeDrawAwayOdds) {
        this.homeDrawAwayOdds = homeDrawAwayOdds;
    }

    public int getSofaScoreGameId() {
        return sofaScoreGameId;
    }
    public void setSofaScoreGameId(int sofaScoreGameId) {
        this.sofaScoreGameId = sofaScoreGameId;
    }
}
