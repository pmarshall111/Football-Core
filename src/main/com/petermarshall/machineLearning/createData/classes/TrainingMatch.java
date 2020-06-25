package com.petermarshall.machineLearning.createData.classes;

import com.petermarshall.DateHelper;
import com.petermarshall.database.Result;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

import static com.petermarshall.machineLearning.createData.CalculatePastStats.COMPARE_LAST_N_GAMES;
import static com.petermarshall.machineLearning.createData.CalculatePastStats.NUMB_SEASONS_HISTORY;

//Purpose of training match is to hold all data that we have for a match, to make it easier to change which features we
//use for our ML model.
public class TrainingMatch {
    private ArrayList<Double> features;
    private ArrayList<Double> featuresNoLineups;
    private final String homeTeamName;
    private final String awayTeamName;
    private double[] odds;
    private int homeScore;
    private int awayScore;
    private Date kickoffTime;
    private int seasonYearStart;

    //general usage
    public TrainingMatch(TrainingTeam homeTeam, TrainingTeam awayTeam, double homeOdds, double drawOdds, double awayOdds,
                         int homeScore, int awayScore, String sqlKickoffStr, int seasonYearStart) {
        this.homeTeamName = homeTeam.getTeamName();
        this.awayTeamName = awayTeam.getTeamName();
        setMiscStats(homeOdds, drawOdds, awayOdds, homeScore, awayScore, sqlKickoffStr, seasonYearStart);
    }

    //needed to create a training match for historic games of previous seasons so that future TrainingMatches can be made.
    //used when we predict games and do not go through the entire history of the games, just the current season.
    //Only limited info is needed from the old games.
    public TrainingMatch(TrainingTeam homeTeam, TrainingTeam awayTeam, int homeScore, int awayScore, int seasonYearStart) {
        this.homeTeamName = homeTeam.getTeamName();
        this.awayTeamName = awayTeam.getTeamName();
        this.homeScore = homeScore;
        this.awayScore = awayScore;
        this.seasonYearStart = seasonYearStart;
    }

    private void setMiscStats(double homeOdds, double drawOdds, double awayOdds, int homeScore, int awayScore, String kickoff, int seasonYearStart) {
        this.odds = new double[]{homeOdds,drawOdds,awayOdds};
        this.homeScore = homeScore;
        this.awayScore = awayScore;
        this.kickoffTime = DateHelper.createDateFromSQL(kickoff);
        this.seasonYearStart = seasonYearStart;
    }

    public int getPoints(String teamName) {
        boolean homeTeam = isHomeTeam(teamName);
        if (homeScore == awayScore) return 1;
        else if (homeScore > awayScore) return homeTeam ? 3 : 0;
        else return homeTeam ? 0 : 3;
    }

    public boolean isHomeTeam(String teamName) {
        if (homeTeamName.equals(teamName)) return true;
        else if (awayTeamName.equals(teamName)) return false;
        else throw new RuntimeException("We are trying to get the home/away of a team that did not play in this match. Team: " + teamName + ". Hometeam: " + homeTeamName + ". Awayteam: " + awayTeamName);
    }

    public boolean isInOrAfterSeasonYearStart(int seasonYearStart) {
        return this.seasonYearStart >= seasonYearStart;
    }

    public String getHomeTeamName() {
        return homeTeamName;
    }

    public String getAwayTeamName() {
        return awayTeamName;
    }

    public Date getKickoffTime() {
        return kickoffTime;
    }

    public int getSeasonYearStart() {
        return seasonYearStart;
    }

    public double[] getOdds() {
        return odds;
    }

    public ArrayList<Double> getFeatures() {
        return features;
    }

    public ArrayList<Double> getFeaturesNoLineups() {
        return featuresNoLineups;
    }

    public void setFeatures(ArrayList<Double> features) {
        this.features = features;
    }

    public void setFeaturesNoLineups(ArrayList<Double> featuresNoLineups) {
        this.featuresNoLineups = featuresNoLineups;
    }

}
