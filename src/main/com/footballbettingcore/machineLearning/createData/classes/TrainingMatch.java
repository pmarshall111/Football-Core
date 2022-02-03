package com.footballbettingcore.machineLearning.createData.classes;

import com.footballbettingcore.utils.DateHelper;

import java.util.ArrayList;
import java.util.Date;

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
    private double homeXG;
    private double awayXG;
    private Date kickoffTime;
    private int seasonYearStart;
    private int gameId;
    private double probability = 1;
    private double[] xgSimulatedProbabilties = new double[]{-1,-1,-1};
    private double[] fiveThirtyEightProbabilities = new double[]{-1,-1,-1};

    //general usage
    public TrainingMatch(String homeTeamName, String awayTeamName, double homeOdds, double drawOdds, double awayOdds,
                         int homeScore, int awayScore, String sqlKickoffStr, int seasonYearStart, int gameId,
                         double homeXG, double awayXG) {
        this.homeTeamName = homeTeamName;
        this.awayTeamName = awayTeamName;
        this.gameId = gameId;
        setMiscStats(homeOdds, drawOdds, awayOdds, homeScore, awayScore, sqlKickoffStr, seasonYearStart, homeXG, awayXG);
    }

    //needed to create a training match for historic games of previous seasons so that future TrainingMatches can be made.
    //used when we predict games and do not go through the entire history of the games, just the current season.
    //Only limited info is needed from the old games.
    public TrainingMatch(String homeTeamName, String awayTeamName, int homeScore, int awayScore, int seasonYearStart,
                         double homeXG, double awayXG) {
        this.homeTeamName = homeTeamName;
        this.awayTeamName = awayTeamName;
        this.homeScore = homeScore;
        this.awayScore = awayScore;
        this.seasonYearStart = seasonYearStart;
        this.homeXG = homeXG;
        this.awayXG = awayXG;
    }

    private void setMiscStats(double homeOdds, double drawOdds, double awayOdds, int homeScore, int awayScore, String kickoff,
                              int seasonYearStart, double homeXG, double awayXG) {
        this.odds = new double[]{homeOdds,drawOdds,awayOdds};
        this.homeScore = homeScore;
        this.awayScore = awayScore;
        this.kickoffTime = DateHelper.createDateFromSQL(kickoff);
        this.seasonYearStart = seasonYearStart;
        this.homeXG = homeXG;
        this.awayXG = awayXG;
    }

    public TrainingMatch clone() {
        TrainingMatch clone = new TrainingMatch(this.homeTeamName, this.awayTeamName, this.odds[0], this.odds[1], this.odds[2],
                this.homeScore, this.awayScore, DateHelper.getSqlDate(this.kickoffTime), this.seasonYearStart, this.gameId,
                this.homeXG, this.awayXG);
        clone.setFeatures(this.features);
        clone.setFeaturesNoLineups(this.featuresNoLineups);
        clone.setXgSimulatedProbabilties(this.xgSimulatedProbabilties);
        clone.setFiveThirtyEightProbabilities(this.fiveThirtyEightProbabilities);
        return clone;
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

    public int getGameId() {
        return gameId;
    }

    public int getHomeScore() {
        return homeScore;
    }

    public int getAwayScore() {
        return awayScore;
    }

    public void setProbability(double probability) {
        this.probability = probability;
    }

    public double getProbability() {
        return probability;
    }

    public double getHomeXG() {
        return homeXG;
    }

    public double getAwayXG() {
        return awayXG;
    }

    public void setHomeScore(int homeScore) {
        this.homeScore = homeScore;
    }

    public void setAwayScore(int awayScore) {
        this.awayScore = awayScore;
    }

    public double[] getXgSimulatedProbabilties() {
        return xgSimulatedProbabilties;
    }

    public void setXgSimulatedProbabilties(double[] xgSimulatedProbabilties) {
        this.xgSimulatedProbabilties = xgSimulatedProbabilties;
    }

    public double[] getFiveThirtyEightProbabilities() {
        return fiveThirtyEightProbabilities;
    }

    public void setFiveThirtyEightProbabilities(double[] fiveThirtyEightProbabilities) {
        this.fiveThirtyEightProbabilities = fiveThirtyEightProbabilities;
    }

    public String getMatchString() {
        return getHomeTeamName() + " vs " + getAwayTeamName() + " on " + kickoffTime;
    }
}
