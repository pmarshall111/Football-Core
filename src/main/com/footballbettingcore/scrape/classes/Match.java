package com.footballbettingcore.scrape.classes;

import com.footballbettingcore.database.FirstScorer;

import java.util.*;

public class Match {
    private final Team homeTeam;
    private final Team awayTeam;
    private double homeXGF;
    private double awayXGF;
    private int homeScore;
    private int awayScore;
    private FirstScorer firstScorer;
    private Date kickoffTime;
    //player ratings data will come from sofascore scraper
    private HashMap<String, PlayerRating> homePlayerRatings;
    private HashMap<String, PlayerRating> awayPlayerRatings;
    private ArrayList<Double> homeDrawAwayOdds;
    private int sofaScoreGameId;
    private boolean isPostponed = false;
    private double homePossession;
    private double awayPossession;
    private int homeShots;
    private int awayShots;
    private int homeShotsOnTarget;
    private int awayShotsOnTarget;
    private double[] fiveThirtyEightPredictions = new double[]{-1,-1,-1};


    /*
     * Constructor will be initialised by Understat scraper, which will first look at the dates source so we can get the team names + kickoff times.
     * Then the understat scraper can set the xG info later with setter functions.
     * Info from understat comes from 2 different sources which is why setting info is partly in constructor and partly using setters.
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
        this.firstScorer = FirstScorer.NO_FIRST_SCORER;
        this.homeXGF = -1;
        this.awayXGF = -1;
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

    public FirstScorer getFirstScorer() {
        return firstScorer;
    }
    public void setFirstScorer(FirstScorer firstScorer) {
        if (this.homeScore == -1 || this.awayScore == -1) throw new RuntimeException("Error trying to setFirstScorer for match " +
                this.homeTeam.getTeamName() + " vs " + this.awayTeam.getTeamName() + ". Cannot set firstScorer because scores have not been set.");
        else if (this.homeScore == 0 && this.awayScore == 0 && !firstScorer.equals(FirstScorer.NO_FIRST_SCORER)) throw new RuntimeException("Error trying to setFirstScorer for match " +
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
    public boolean isPostponed() {
        return isPostponed;
    }

    public void setPostponed(boolean postponed) {
        isPostponed = postponed;
    }

    public double getHomePossession() {
        return homePossession;
    }

    public void setHomePossession(String homePossession) {
        try {
            this.homePossession = Double.parseDouble(homePossession);
        } catch (NumberFormatException e) {
            e.printStackTrace();
            System.out.println(this.homeTeam.getTeamName() + " vs " + this.awayTeam.getTeamName());
        }
    }

    public double getAwayPossession() {
        return awayPossession;
    }

    public void setAwayPossession(String awayPossession) {
        try {
            this.awayPossession = Double.parseDouble(awayPossession);
        } catch (NumberFormatException e) {
            e.printStackTrace();
            System.out.println(this.homeTeam.getTeamName() + " vs " + this.awayTeam.getTeamName());
        }
    }

    public int getHomeShots() {
        return homeShots;
    }

    public void setHomeShots(String homeShots) {
        try {
            this.homeShots = Integer.parseInt(homeShots);
        } catch (NumberFormatException e) {
            e.printStackTrace();
            System.out.println(this.homeTeam.getTeamName() + " vs " + this.awayTeam.getTeamName());
        }
    }

    public int getAwayShots() {
        return awayShots;
    }

    public void setAwayShots(String awayShots) {
        try {
            this.awayShots = Integer.parseInt(awayShots);
        } catch (NumberFormatException e) {
            e.printStackTrace();
            System.out.println(this.homeTeam.getTeamName() + " vs " + this.awayTeam.getTeamName());
        }
    }

    public int getHomeShotsOnTarget() {
        return homeShotsOnTarget;
    }

    public void setHomeShotsOnTarget(String homeShotsOnTarget) {
        try {
            this.homeShotsOnTarget = Integer.parseInt(homeShotsOnTarget);
        } catch (NumberFormatException e) {
            e.printStackTrace();
            System.out.println(this.homeTeam.getTeamName() + " vs " + this.awayTeam.getTeamName());
        }
    }

    public int getAwayShotsOnTarget() {
        return awayShotsOnTarget;
    }

    public void setAwayShotsOnTarget(String awayShotsOnTarget) {
        try {
            this.awayShotsOnTarget = Integer.parseInt(awayShotsOnTarget);
        } catch (NumberFormatException e) {
            e.printStackTrace();
            System.out.println(this.homeTeam.getTeamName() + " vs " + this.awayTeam.getTeamName());
        }
    }

    public double[] getFiveThirtyEightPredictions() {
        return fiveThirtyEightPredictions;
    }

    public void setFiveThirtyEightPredictions(double[] fiveThirtyEightPredictions) {
        this.fiveThirtyEightPredictions = fiveThirtyEightPredictions;
    }

    public String getHomeTeamName() {
        return this.homeTeam.getTeamName();
    }

    public String getAwayTeamName() {
        return this.awayTeam.getTeamName();
    }
}
