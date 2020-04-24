package com.petermarshall.machineLearning.createData.classes;

import com.petermarshall.DateHelper;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

import static com.petermarshall.machineLearning.createData.refactor.PastStatsCalculator.COMPARE_LAST_N_GAMES;
import static com.petermarshall.machineLearning.createData.refactor.PastStatsCalculator.NUMB_SEASONS_HISTORY;

//Purpose of training match is to hold all data that we have for a match, to make it easier to change which features we
//use for our ML model.
public class TrainingMatch {
    private ArrayList<Double> features;

    //HOME OVR DATA
    private String homeTeamName;
    private double homeTeamAvgGoalsFor;
    private double homeTeamAvgGoalsAgainst;
    private double homeTeamAvgXGF;
    private double homeTeamAvgXGA;
    private double homeTeamWeightedAvgXGF;
    private double homeTeamWeightedAvgXGA;
    //WEIGHTED AVG FORM CALCULATED FIELDS
    private double homeTeamFormGoalsFor;
    private double homeTeamFormGoalsAgainst;
    private double homeTeamFormXGF;
    private double homeTeamFormXGA;
    private double homeTeamFormWeightedXGF;
    private double homeTeamFormWeightedXGA;
    //NORMAL AVG FORM CALCULATED FIELDS
    private double homeTeamAvgFormGoalsFor;
    private double homeTeamAvgFormGoalsAgainst;
    private double homeTeamAvgFormXGF;
    private double homeTeamAvgFormXGA;
    private double homeTeamAvgFormWeightedXGF;
    private double homeTeamAvgFormWeightedXGA;
    private double homeTeamAvgFormXGFLast5Games;
    private double homeTeamAvgFormXGALast5Games;
    //EXTRA HOME
    private double avgHomeTeamPoints;
    private double last5HomeTeamPoints;
    private double ifScoredFirstHomeTeamPoints;
    private double ifConceededFirstHomeTeamPoints;
    private double homeTeamPointsAgainstOpposition;
    private double homeTeamMinsWeightedLineupRating;
    private double homeTeamGamesWeightedLineupRating;
    private double homeTeamStrength; //calc'd by looking at how many minutes are on the pitch vs the minutes of highest 11 players (justification: highest 11 players minutes will be the players the manager thinks works best in his team.)
    private double homeTeamsOpponentsWholeSeasonPPG;
    private double homeTeamsLast5OpponentsWholeSeasonPPG;
    private double homeTeamsOpponentsLast5PPG;
    private double homeTeamLast5OpponentsLast5PPG;
    private double homeTeamsAvgNumbCleanSheets;
    private double homeTeamsLast5AvgNumbCleanSheets;
    
    //HOME HOME DATA
    private double homeTeamAvgHomeGoalsFor;
    private double homeTeamAvgHomeGoalsAgainst;
    private double homeTeamAvgHomeXGF;
    private double homeTeamAvgHomeXGA;
    private double homeTeamWeightedAvgHomeXGF;
    private double homeTeamWeightedAvgHomeXGA;
    private double homeTeamHomeFormGoalsFor;
    private double homeTeamHomeFormGoalsAgainst;
    private double homeTeamHomeFormXGF;
    private double homeTeamHomeFormXGA;
    private double homeTeamHomeFormWeightedXGF;
    private double homeTeamHomeFormWeightedXGA;
    private double homeTeamHomeAvgFormGoalsFor;
    private double homeTeamHomeAvgFormGoalsAgainst;
    private double homeTeamHomeAvgFormXGF;
    private double homeTeamHomeAvgFormXGA;
    private double homeTeamHomeAvgFormWeightedXGF;
    private double homeTeamHomeAvgFormWeightedXGA;
    private double homeTeamHomeAvgFormXGFLast5Games;
    private double homeTeamHomeAvgFormXGALast5Games;
    private double avgHomeTeamHomePoints;
    private double last5HomeTeamHomePoints;
    private double ifScoredFirstAtHomeHomeTeamPoints;
    private double ifConceededFirstAtHomeHomeTeamPoints;
    private double homeTeamPointsAtHomeAgainstOpposition;
    private double homeTeamAtHomeMinsWeightedLineupRating;
    private double homeTeamAtHomeGamesWeightedLineupRating;
    private double homeTeamHomeStrength;
    private double homeTeamsHomeOpponentsWholeSeasonPPG;
    private double homeTeamsLast5HomeOpponentsWholeSeasonPPG;
    private double homeTeamsHomeOpponentsLast5PPG;
    private double homeTeamLast5HomeOpponentsLast5PPG;
    private double homeTeamsHomeAvgNumbCleanSheets;
    private double homeTeamsLast5HomeAvgNumbCleanSheets;
    
    
    //AWAY DATA
    private String awayTeamName;
    private double awayTeamAvgGoalsFor;
    private double awayTeamAvgGoalsAgainst;
    private double awayTeamAvgXGF;
    private double awayTeamAvgXGA;
    private double awayTeamWeightedAvgXGF;
    private double awayTeamWeightedAvgXGA;
    private double awayTeamFormGoalsFor;
    private double awayTeamFormGoalsAgainst;
    private double awayTeamFormXGF;
    private double awayTeamFormXGA;
    private double awayTeamFormWeightedXGF;
    private double awayTeamFormWeightedXGA;
    private double awayTeamAvgFormGoalsFor;
    private double awayTeamAvgFormGoalsAgainst;
    private double awayTeamAvgFormXGF;
    private double awayTeamAvgFormXGA;
    private double awayTeamAvgFormWeightedXGF;
    private double awayTeamAvgFormWeightedXGA;
    private double awayTeamAvgFormXGFLast5Games;
    private double awayTeamAvgFormXGALast5Games;
    private double avgAwayTeamPoints;
    private double last5AwayTeamPoints;
    private double ifScoredFirstAwayTeamPoints;
    private double ifConceededFirstAwayTeamPoints;
    private double awayTeamPointsAgainstOpposition;
    private double awayTeamMinsWeightedLineupRating;
    private double awayTeamGamesWeightedLineupRating;
    private double awayTeamStrength;
    private double awayTeamsOpponentsWholeSeasonPPG;
    private double awayTeamsLast5OpponentsWholeSeasonPPG;
    private double awayTeamsOpponentsLast5PPG;
    private double awayTeamLast5OpponentsLast5PPG;
    private double awayTeamsAvgNumbCleanSheets;
    private double awayTeamsLast5AvgNumbCleanSheets;

    //AWAY AWAY DATA
    private double awayTeamAvgAwayGoalsFor;
    private double awayTeamAvgAwayGoalsAgainst;
    private double awayTeamAvgAwayXGF;
    private double awayTeamAvgAwayXGA;
    private double awayTeamWeightedAvgAwayXGF;
    private double awayTeamWeightedAvgAwayXGA;
    private double awayTeamAwayFormGoalsFor;
    private double awayTeamAwayFormGoalsAgainst;
    private double awayTeamAwayFormXGF;
    private double awayTeamAwayFormXGA;
    private double awayTeamAwayFormWeightedXGF;
    private double awayTeamAwayFormWeightedXGA;
    private double awayTeamAwayAvgFormGoalsFor;
    private double awayTeamAwayAvgFormGoalsAgainst;
    private double awayTeamAwayAvgFormXGF;
    private double awayTeamAwayAvgFormXGA;
    private double awayTeamAwayAvgFormWeightedXGF;
    private double awayTeamAwayAvgFormWeightedXGA;
    private double awayTeamAwayAvgFormXGFLast5Games;
    private double awayTeamAwayAvgFormXGALast5Games;
    private double avgAwayTeamAwayPoints;
    private double last5AwayTeamAwayPoints;
    private double ifScoredFirstAtAwayAwayTeamPoints;
    private double ifConceededFirstAtAwayAwayTeamPoints;
    private double awayTeamPointsAtAwayAgainstOpposition;
    private double awayTeamAtAwayMinsWeightedLineupRating;
    private double awayTeamAtAwayGamesWeightedLineupRating;
    private double awayTeamAwayStrength;
    private double awayTeamsAwayOpponentsWholeSeasonPPG;
    private double awayTeamsLast5AwayOpponentsWholeSeasonPPG;
    private double awayTeamsAwayOpponentsLast5PPG;
    private double awayTeamLast5AwayOpponentsLast5PPG;
    private double awayTeamsAwayAvgNumbCleanSheets;
    private double awayTeamsLast5AwayAvgNumbCleanSheets;

    //MISC DATA
    //probabilities calculated from betting odds
    private double homeTeamProbability;
    private double awayTeamProbability;
    private double drawProbability;
    private int homeScore;
    private int awayScore;
    private Date kickoffTime;
    private int homeTeamGamesPlayed;
    private int awayTeamGamesPlayed;
    private int seasonYearStart;

    public ArrayList<Double> getFeatures() {
        return features;
    }

    public void setFeatures(ArrayList<Double> features) {
        this.features = features;
    }

    public TrainingMatch(TrainingTeam homeTeam, TrainingTeamsSeason homeSeason, TrainingTeam awayTeam, TrainingTeamsSeason awaySeason,
                         HashMap<String, Player> homeLineup, HashMap<String, Player> awayLineup, double homeOdds, double drawOdds, double awayOdds,
                         int homeScore, int awayScore, String kickoff, int seasonYearStart) {

        setHomeTeamStats(homeTeam, homeSeason, homeLineup, awayTeam, GamesSelector.ALL_GAMES);
        setHomeTeamAtHomeStats(homeTeam, homeSeason, homeLineup, awayTeam, GamesSelector.ONLY_HOME_GAMES);
        setAwayTeamStats(awayTeam, awaySeason, awayLineup, homeTeam, GamesSelector.ALL_GAMES);
        setAwayTeamAtAwayStats(awayTeam, awaySeason, awayLineup, homeTeam, GamesSelector.ONLY_AWAY_GAMES);
        setMiscStats(homeOdds, drawOdds, awayOdds, homeScore, awayScore, kickoff, homeSeason.getNumbGamesPlayed(), awaySeason.getNumbGamesPlayed(), seasonYearStart);
    }
    //needed to create a training match for historic games of previous seasons so that future TrainingMatches can be made.
    //used when we predict games and do not go through the entire history of the games, just the current season. Limited info is retrieved from the old games.
    public TrainingMatch(TrainingTeam homeTeam, TrainingTeam awayTeam, int homeScore, int awayScore, int seasonYearStart) {
        this.homeTeamName = homeTeam.getTeamName();
        this.awayTeamName = awayTeam.getTeamName();
        this.homeScore = homeScore;
        this.awayScore = awayScore;
        this.seasonYearStart = seasonYearStart;
    }

    private double calcProbabilityFromOdds(double odds) {
        return 1/odds;
    }

    private void setMiscStats(double homeOdds, double drawOdds, double awayOdds, int homeScore, int awayScore, String kickoff, int homeTeamGamesPlayed, int awayTeamGamesPlayed, int seasonYearStart) {
        this.homeTeamProbability = calcProbabilityFromOdds(homeOdds);
        this.drawProbability = calcProbabilityFromOdds(drawOdds);
        this.awayTeamProbability = calcProbabilityFromOdds(awayOdds);
        this.homeScore = homeScore;
        this.awayScore = awayScore;
        this.kickoffTime = DateHelper.createDateFromSQL(kickoff);
        this.homeTeamGamesPlayed = homeTeamGamesPlayed;
        this.awayTeamGamesPlayed = awayTeamGamesPlayed;
        this.seasonYearStart = seasonYearStart;
    }
    
    public void setHomeTeamStats(TrainingTeam homeTeam, TrainingTeamsSeason homeSeason, HashMap<String, Player> homeLineup, TrainingTeam awayTeam, GamesSelector venueSelector) {
        ArrayList<String> playersNames =  new ArrayList<>(homeLineup.keySet());
        this.homeTeamName = homeTeam.getTeamName();
        this.homeTeamAvgGoalsFor = homeSeason.getAvgGoalsFor(venueSelector);
        this.homeTeamAvgGoalsAgainst = homeSeason.getAvgGoalsAgainst(venueSelector);
        this.homeTeamAvgXGF = homeSeason.getAvgXGF(venueSelector);
        this.homeTeamAvgXGA = homeSeason.getAvgXGA(venueSelector);
        this.homeTeamWeightedAvgXGF = homeSeason.getWeightedAvgXGF(venueSelector);
        this.homeTeamWeightedAvgXGA = homeSeason.getWeightedAvgXGA(venueSelector);
        this.homeTeamFormGoalsFor = homeSeason.getFormGoalsFor(venueSelector);
        this.homeTeamFormGoalsAgainst = homeSeason.getFormGoalsAgainst(venueSelector);
        this.homeTeamFormXGF = homeSeason.getFormXGF(venueSelector);
        this.homeTeamFormXGA = homeSeason.getFormXGA(venueSelector);
        this.homeTeamFormWeightedXGF = homeSeason.getFormWeightedXGF(venueSelector);
        this.homeTeamFormWeightedXGA = homeSeason.getFormWeightedXGA(venueSelector);
        this.homeTeamAvgFormGoalsFor = homeSeason.getAvgFormGoalsFor(venueSelector, 0);
        this.homeTeamAvgFormGoalsAgainst = homeSeason.getAvgFormGoalsAgainst(venueSelector, 0);
        this.homeTeamAvgFormXGF = homeSeason.getAvgFormXGF(venueSelector, 0);
        this.homeTeamAvgFormXGA = homeSeason.getAvgFormXGA(venueSelector, 0);
        this.homeTeamAvgFormWeightedXGF = homeSeason.getAvgFormWeightedXGF(venueSelector, 0);
        this.homeTeamAvgFormWeightedXGA = homeSeason.getAvgFormWeightedXGA(venueSelector, 0);
        this.homeTeamAvgFormXGFLast5Games = homeSeason.getFormXGFOverLastNGames(venueSelector, COMPARE_LAST_N_GAMES);
        this.homeTeamAvgFormXGALast5Games = homeSeason.getFormXGAOverLastNGames(venueSelector, COMPARE_LAST_N_GAMES);
        this.avgHomeTeamPoints = homeSeason.getAvgPoints(venueSelector);
        this.last5HomeTeamPoints = homeSeason.getAvgPointsOverLastXGames(venueSelector, COMPARE_LAST_N_GAMES);
        this.ifScoredFirstHomeTeamPoints = homeSeason.getAvgPointsWhenScoredFirst(venueSelector);
        this.ifConceededFirstHomeTeamPoints = homeSeason.getAvgPointsWhenConceededFirst(venueSelector);
        this.homeTeamPointsAgainstOpposition = homeTeam.getPointsOfLastMatchups(awayTeam.getTeamName(), venueSelector, seasonYearStart - NUMB_SEASONS_HISTORY);
        this.homeTeamMinsWeightedLineupRating = homeSeason.getMinsWeightedLineupRating(venueSelector, playersNames);
        this.homeTeamGamesWeightedLineupRating = homeSeason.getGamesWeightedLineupRating(venueSelector, playersNames);
        this.homeTeamStrength = homeSeason.getLineupStrength(venueSelector, playersNames);
        this.homeTeamsOpponentsWholeSeasonPPG = homeSeason.getAvgPointsOfAllOpponentsGamesWholeSeason(venueSelector);
        this.homeTeamsLast5OpponentsWholeSeasonPPG = homeSeason.getAvgPointsOfLastXOpponentsGamesWholeSeason(venueSelector, COMPARE_LAST_N_GAMES);
        this.homeTeamsOpponentsLast5PPG = homeSeason.getAvgPointsOfAllOpponentsLast5Games(venueSelector);
        this.homeTeamLast5OpponentsLast5PPG = homeSeason.getAvgPointsOfLastXOpponentsLast5Games(venueSelector, COMPARE_LAST_N_GAMES);
        this.homeTeamsAvgNumbCleanSheets = homeSeason.getAvgNumberOfCleanSheets(venueSelector);
        this.homeTeamsLast5AvgNumbCleanSheets = homeSeason.getAvgNumberOfCleanSheetsLastXGames(venueSelector, COMPARE_LAST_N_GAMES, true);
    }

    public void setHomeTeamAtHomeStats(TrainingTeam homeTeam, TrainingTeamsSeason homeSeason, HashMap<String, Player> homeLineup, TrainingTeam awayTeam, GamesSelector venueSelector) {
        ArrayList<String> playersNames =  new ArrayList<>(homeLineup.keySet());
        this.homeTeamAvgHomeGoalsFor = homeSeason.getAvgGoalsFor(venueSelector);
        this.homeTeamAvgHomeGoalsAgainst = homeSeason.getAvgGoalsAgainst(venueSelector);
        this.homeTeamAvgHomeXGF = homeSeason.getAvgXGF(venueSelector);
        this.homeTeamAvgHomeXGA = homeSeason.getAvgXGA(venueSelector);
        this.homeTeamWeightedAvgHomeXGF = homeSeason.getWeightedAvgXGF(venueSelector);
        this.homeTeamWeightedAvgHomeXGA = homeSeason.getWeightedAvgXGA(venueSelector);
        this.homeTeamHomeFormGoalsFor = homeSeason.getFormGoalsFor(venueSelector);
        this.homeTeamHomeFormGoalsAgainst = homeSeason.getFormGoalsAgainst(venueSelector);
        this.homeTeamHomeFormXGF = homeSeason.getFormXGF(venueSelector);
        this.homeTeamHomeFormXGA = homeSeason.getFormXGA(venueSelector);
        this.homeTeamHomeFormWeightedXGF = homeSeason.getFormWeightedXGF(venueSelector);
        this.homeTeamHomeFormWeightedXGA = homeSeason.getFormWeightedXGA(venueSelector);
        this.homeTeamHomeAvgFormGoalsFor = homeSeason.getAvgFormGoalsFor(venueSelector, 0);
        this.homeTeamHomeAvgFormGoalsAgainst = homeSeason.getAvgFormGoalsAgainst(venueSelector, 0);
        this.homeTeamHomeAvgFormXGF = homeSeason.getAvgFormXGF(venueSelector, 0);
        this.homeTeamHomeAvgFormXGA = homeSeason.getAvgFormXGA(venueSelector, 0);
        this.homeTeamHomeAvgFormWeightedXGF = homeSeason.getAvgFormWeightedXGF(venueSelector, 0);
        this.homeTeamHomeAvgFormWeightedXGA = homeSeason.getAvgFormWeightedXGA(venueSelector, 0);
        this.homeTeamHomeAvgFormXGFLast5Games = homeSeason.getFormXGFOverLastNGames(venueSelector, COMPARE_LAST_N_GAMES);
        this.homeTeamHomeAvgFormXGALast5Games = homeSeason.getFormXGAOverLastNGames(venueSelector, COMPARE_LAST_N_GAMES);
        this.avgHomeTeamHomePoints = homeSeason.getAvgPoints(venueSelector);
        this.last5HomeTeamHomePoints = homeSeason.getAvgPointsOverLastXGames(venueSelector, COMPARE_LAST_N_GAMES);
        this.ifScoredFirstAtHomeHomeTeamPoints = homeSeason.getAvgPointsWhenScoredFirst(venueSelector);
        this.ifConceededFirstAtHomeHomeTeamPoints = homeSeason.getAvgPointsWhenConceededFirst(venueSelector);
        this.homeTeamPointsAtHomeAgainstOpposition = homeTeam.getPointsOfLastMatchups(awayTeam.getTeamName(), venueSelector, seasonYearStart - NUMB_SEASONS_HISTORY);
        this.homeTeamAtHomeMinsWeightedLineupRating = homeSeason.getMinsWeightedLineupRating(venueSelector, playersNames);
        this.homeTeamAtHomeGamesWeightedLineupRating = homeSeason.getGamesWeightedLineupRating(venueSelector, playersNames);
        this.homeTeamHomeStrength = homeSeason.getLineupStrength(venueSelector, playersNames);
        this.homeTeamsHomeOpponentsWholeSeasonPPG = homeSeason.getAvgPointsOfAllOpponentsGamesWholeSeason(venueSelector);
        this.homeTeamsLast5HomeOpponentsWholeSeasonPPG = homeSeason.getAvgPointsOfLastXOpponentsGamesWholeSeason(venueSelector, COMPARE_LAST_N_GAMES);
        this.homeTeamsHomeOpponentsLast5PPG = homeSeason.getAvgPointsOfAllOpponentsLast5Games(venueSelector);
        this.homeTeamLast5HomeOpponentsLast5PPG = homeSeason.getAvgPointsOfLastXOpponentsLast5Games(venueSelector, COMPARE_LAST_N_GAMES);
        this.homeTeamsHomeAvgNumbCleanSheets = homeSeason.getAvgNumberOfCleanSheets(venueSelector);
        this.homeTeamsLast5HomeAvgNumbCleanSheets = homeSeason.getAvgNumberOfCleanSheetsLastXGames(venueSelector, COMPARE_LAST_N_GAMES, true);
    }
    
    public void setAwayTeamStats(TrainingTeam awayTeam, TrainingTeamsSeason awaySeason, HashMap<String, Player> awayLineup, TrainingTeam homeTeam, GamesSelector venueSelector) {
        ArrayList<String> playersNames =  new ArrayList<>(awayLineup.keySet());
        this.awayTeamName = awayTeam.getTeamName();
        this.awayTeamAvgGoalsFor = awaySeason.getAvgGoalsFor(venueSelector);
        this.awayTeamAvgGoalsAgainst = awaySeason.getAvgGoalsAgainst(venueSelector);
        this.awayTeamAvgXGF = awaySeason.getAvgXGF(venueSelector);
        this.awayTeamAvgXGA = awaySeason.getAvgXGA(venueSelector);
        this.awayTeamWeightedAvgXGF = awaySeason.getWeightedAvgXGF(venueSelector);
        this.awayTeamWeightedAvgXGA = awaySeason.getWeightedAvgXGA(venueSelector);
        this.awayTeamFormGoalsFor = awaySeason.getFormGoalsFor(venueSelector);
        this.awayTeamFormGoalsAgainst = awaySeason.getFormGoalsAgainst(venueSelector);
        this.awayTeamFormXGF = awaySeason.getFormXGF(venueSelector);
        this.awayTeamFormXGA = awaySeason.getFormXGA(venueSelector);
        this.awayTeamFormWeightedXGF = awaySeason.getFormWeightedXGF(venueSelector);
        this.awayTeamFormWeightedXGA = awaySeason.getFormWeightedXGA(venueSelector);
        this.awayTeamAvgFormGoalsFor = awaySeason.getAvgFormGoalsFor(venueSelector, 0);
        this.awayTeamAvgFormGoalsAgainst = awaySeason.getAvgFormGoalsAgainst(venueSelector, 0);
        this.awayTeamAvgFormXGF = awaySeason.getAvgFormXGF(venueSelector, 0);
        this.awayTeamAvgFormXGA = awaySeason.getAvgFormXGA(venueSelector, 0);
        this.awayTeamAvgFormWeightedXGF = awaySeason.getAvgFormWeightedXGF(venueSelector, 0);
        this.awayTeamAvgFormWeightedXGA = awaySeason.getAvgFormWeightedXGA(venueSelector, 0);
        this.awayTeamAvgFormXGFLast5Games = awaySeason.getFormXGFOverLastNGames(venueSelector, COMPARE_LAST_N_GAMES);
        this.awayTeamAvgFormXGALast5Games = awaySeason.getFormXGAOverLastNGames(venueSelector, COMPARE_LAST_N_GAMES);
        this.avgAwayTeamPoints = awaySeason.getAvgPoints(venueSelector);
        this.last5AwayTeamPoints = awaySeason.getAvgPointsOverLastXGames(venueSelector, COMPARE_LAST_N_GAMES);
        this.ifScoredFirstAwayTeamPoints = awaySeason.getAvgPointsWhenScoredFirst(venueSelector);
        this.ifConceededFirstAwayTeamPoints = awaySeason.getAvgPointsWhenConceededFirst(venueSelector);
        this.awayTeamPointsAgainstOpposition = awayTeam.getPointsOfLastMatchups(homeTeam.getTeamName(), venueSelector, seasonYearStart - NUMB_SEASONS_HISTORY);
        this.awayTeamMinsWeightedLineupRating = awaySeason.getMinsWeightedLineupRating(venueSelector, playersNames);
        this.awayTeamGamesWeightedLineupRating = awaySeason.getGamesWeightedLineupRating(venueSelector, playersNames);
        this.awayTeamStrength = awaySeason.getLineupStrength(venueSelector, playersNames);
        this.awayTeamsOpponentsWholeSeasonPPG = awaySeason.getAvgPointsOfAllOpponentsGamesWholeSeason(venueSelector);
        this.awayTeamsLast5OpponentsWholeSeasonPPG = awaySeason.getAvgPointsOfLastXOpponentsGamesWholeSeason(venueSelector, COMPARE_LAST_N_GAMES);
        this.awayTeamsOpponentsLast5PPG = awaySeason.getAvgPointsOfAllOpponentsLast5Games(venueSelector);
        this.awayTeamLast5OpponentsLast5PPG = awaySeason.getAvgPointsOfLastXOpponentsLast5Games(venueSelector, COMPARE_LAST_N_GAMES);
        this.awayTeamsAvgNumbCleanSheets = awaySeason.getAvgNumberOfCleanSheets(venueSelector);
        this.awayTeamsLast5AvgNumbCleanSheets = awaySeason.getAvgNumberOfCleanSheetsLastXGames(venueSelector, COMPARE_LAST_N_GAMES, true);
    }


    public void setAwayTeamAtAwayStats(TrainingTeam awayTeam, TrainingTeamsSeason awaySeason, HashMap<String, Player> awayLineup, TrainingTeam homeTeam, GamesSelector venueSelector) {
        ArrayList<String> playersNames =  new ArrayList<>(awayLineup.keySet());
        this.awayTeamAvgAwayGoalsFor = awaySeason.getAvgGoalsFor(venueSelector);
        this.awayTeamAvgAwayGoalsAgainst = awaySeason.getAvgGoalsAgainst(venueSelector);
        this.awayTeamAvgAwayXGF = awaySeason.getAvgXGF(venueSelector);
        this.awayTeamAvgAwayXGA = awaySeason.getAvgXGA(venueSelector);
        this.awayTeamWeightedAvgAwayXGF = awaySeason.getWeightedAvgXGF(venueSelector);
        this.awayTeamWeightedAvgAwayXGA = awaySeason.getWeightedAvgXGA(venueSelector);
        this.awayTeamAwayFormGoalsFor = awaySeason.getFormGoalsFor(venueSelector);
        this.awayTeamAwayFormGoalsAgainst = awaySeason.getFormGoalsAgainst(venueSelector);
        this.awayTeamAwayFormXGF = awaySeason.getFormXGF(venueSelector);
        this.awayTeamAwayFormXGA = awaySeason.getFormXGA(venueSelector);
        this.awayTeamAwayFormWeightedXGF = awaySeason.getFormWeightedXGF(venueSelector);
        this.awayTeamAwayFormWeightedXGA = awaySeason.getFormWeightedXGA(venueSelector);
        this.awayTeamAwayAvgFormGoalsFor = awaySeason.getAvgFormGoalsFor(venueSelector, 0);
        this.awayTeamAwayAvgFormGoalsAgainst = awaySeason.getAvgFormGoalsAgainst(venueSelector, 0);
        this.awayTeamAwayAvgFormXGF = awaySeason.getAvgFormXGF(venueSelector, 0);
        this.awayTeamAwayAvgFormXGA = awaySeason.getAvgFormXGA(venueSelector, 0);
        this.awayTeamAwayAvgFormWeightedXGF = awaySeason.getAvgFormWeightedXGF(venueSelector, 0);
        this.awayTeamAwayAvgFormWeightedXGA = awaySeason.getAvgFormWeightedXGA(venueSelector, 0);
        this.awayTeamAwayAvgFormXGFLast5Games = awaySeason.getFormXGFOverLastNGames(venueSelector, COMPARE_LAST_N_GAMES);
        this.awayTeamAwayAvgFormXGALast5Games = awaySeason.getFormXGAOverLastNGames(venueSelector, COMPARE_LAST_N_GAMES);
        this.avgAwayTeamAwayPoints = awaySeason.getAvgPoints(venueSelector);
        this.last5AwayTeamAwayPoints = awaySeason.getAvgPointsOverLastXGames(venueSelector, COMPARE_LAST_N_GAMES);
        this.ifScoredFirstAtAwayAwayTeamPoints = awaySeason.getAvgPointsWhenScoredFirst(venueSelector);
        this.ifConceededFirstAtAwayAwayTeamPoints = awaySeason.getAvgPointsWhenConceededFirst(venueSelector);
        this.awayTeamPointsAtAwayAgainstOpposition = awayTeam.getPointsOfLastMatchups(homeTeam.getTeamName(), venueSelector, seasonYearStart - NUMB_SEASONS_HISTORY);
        this.awayTeamAtAwayMinsWeightedLineupRating = awaySeason.getMinsWeightedLineupRating(venueSelector, playersNames);
        this.awayTeamAtAwayGamesWeightedLineupRating = awaySeason.getGamesWeightedLineupRating(venueSelector, playersNames);
        this.awayTeamAwayStrength = awaySeason.getLineupStrength(venueSelector, playersNames);
        this.awayTeamsAwayOpponentsWholeSeasonPPG = awaySeason.getAvgPointsOfAllOpponentsGamesWholeSeason(venueSelector);
        this.awayTeamsLast5AwayOpponentsWholeSeasonPPG = awaySeason.getAvgPointsOfLastXOpponentsGamesWholeSeason(venueSelector, COMPARE_LAST_N_GAMES);
        this.awayTeamsAwayOpponentsLast5PPG = awaySeason.getAvgPointsOfAllOpponentsLast5Games(venueSelector);
        this.awayTeamLast5AwayOpponentsLast5PPG = awaySeason.getAvgPointsOfLastXOpponentsLast5Games(venueSelector, COMPARE_LAST_N_GAMES);
        this.awayTeamsAwayAvgNumbCleanSheets = awaySeason.getAvgNumberOfCleanSheets(venueSelector);
        this.awayTeamsLast5AwayAvgNumbCleanSheets = awaySeason.getAvgNumberOfCleanSheetsLastXGames(venueSelector, COMPARE_LAST_N_GAMES, true);
    }

    public int getPoints(String teamName) {
        boolean homeTeam = isHomeTeam(teamName);

        if (homeScore == awayScore) return 1;
        else if (homeScore > awayScore) return homeTeam ? 3 : 0;
        else return homeTeam ? 0 : 3;
    }

    /*
     * To be used when creating csv file.
     * 1 represents home win. 2 is draw, 3 is away win
     */
    public int getResult() {
        if (homeScore == awayScore) return 2;
        else if (homeScore > awayScore) return 1;
        else return 3;
    }

    public boolean isHomeTeam(String teamName) {
        if (homeTeamName.equals(teamName)) return true;
        else if (awayTeamName.equals(teamName)) return false;
        else throw new RuntimeException("We are trying to get the home/away of a team that did not play in this match. Team: " + teamName + ". Hometeam: " + homeTeamName + ". Awayteam: " + awayTeamName);
    }

    public boolean isInOrAfterSeasonYearStart(int seasonYearStart) {
        if (this.seasonYearStart >= seasonYearStart) return true;
        else return false;
    }

    public String getHomeTeamName() {
        return homeTeamName;
    }

    public double getHomeTeamAvgGoalsFor() {
        return homeTeamAvgGoalsFor;
    }

    public double getHomeTeamAvgGoalsAgainst() {
        return homeTeamAvgGoalsAgainst;
    }

    public double getHomeTeamAvgXGF() {
        return homeTeamAvgXGF;
    }

    public double getHomeTeamAvgXGA() {
        return homeTeamAvgXGA;
    }

    public double getHomeTeamWeightedAvgXGF() {
        return homeTeamWeightedAvgXGF;
    }

    public double getHomeTeamWeightedAvgXGA() {
        return homeTeamWeightedAvgXGA;
    }

    public double getAvgHomeTeamPoints() {
        return avgHomeTeamPoints;
    }

    public double getLast5HomeTeamPoints() {
        return last5HomeTeamPoints;
    }

    public double getIfScoredFirstHomeTeamPoints() {
        return ifScoredFirstHomeTeamPoints;
    }

    public double getIfConceededFirstHomeTeamPoints() {
        return ifConceededFirstHomeTeamPoints;
    }

    public double getHomeTeamPointsAgainstOpposition() {
        return homeTeamPointsAgainstOpposition;
    }

    public double getHomeTeamMinsWeightedLineupRating() {
        return homeTeamMinsWeightedLineupRating;
    }

    public double getHomeTeamGamesWeightedLineupRating() {
        return homeTeamGamesWeightedLineupRating;
    }

    public double getHomeTeamStrength() {
        return homeTeamStrength;
    }

    public String getAwayTeamName() {
        return awayTeamName;
    }

    public double getAwayTeamAvgGoalsFor() {
        return awayTeamAvgGoalsFor;
    }

    public double getAwayTeamAvgGoalsAgainst() {
        return awayTeamAvgGoalsAgainst;
    }

    public double getAwayTeamAvgXGF() {
        return awayTeamAvgXGF;
    }

    public double getAwayTeamAvgXGA() {
        return awayTeamAvgXGA;
    }

    public double getAwayTeamWeightedAvgXGF() {
        return awayTeamWeightedAvgXGF;
    }

    public double getAwayTeamWeightedAvgXGA() {
        return awayTeamWeightedAvgXGA;
    }

    public double getAvgAwayTeamPoints() {
        return avgAwayTeamPoints;
    }

    public double getLast5AwayTeamPoints() {
        return last5AwayTeamPoints;
    }

    public double getIfScoredFirstAwayTeamPoints() {
        return ifScoredFirstAwayTeamPoints;
    }

    public double getIfConceededFirstAwayTeamPoints() {
        return ifConceededFirstAwayTeamPoints;
    }

    public double getAwayTeamPointsAgainstOpposition() {
        return awayTeamPointsAgainstOpposition;
    }

    public double getAwayTeamMinsWeightedLineupRating() {
        return awayTeamMinsWeightedLineupRating;
    }

    public double getAwayTeamGamesWeightedLineupRating() {
        return awayTeamGamesWeightedLineupRating;
    }

    public double getAwayTeamStrength() {
        return awayTeamStrength;
    }

    public double getHomeTeamProbability() {
        return homeTeamProbability;
    }

    public double getAwayTeamProbability() {
        return awayTeamProbability;
    }

    public double getDrawProbability() {
        return drawProbability;
    }

    public int getHomeScore() {
        return homeScore;
    }

    public int getAwayScore() {
        return awayScore;
    }

    public Date getKickoffTime() {
        return kickoffTime;
    }

    public int getSeasonYearStart() {
        return seasonYearStart;
    }

    public int getHomeTeamGamesPlayed() {
        return homeTeamGamesPlayed;
    }

    public int getAwayTeamGamesPlayed() {
        return awayTeamGamesPlayed;
    }

    public double getHomeTeamAvgHomeGoalsFor() {
        return homeTeamAvgHomeGoalsFor;
    }

    public double getHomeTeamAvgHomeGoalsAgainst() {
        return homeTeamAvgHomeGoalsAgainst;
    }

    public double getHomeTeamAvgHomeXGF() {
        return homeTeamAvgHomeXGF;
    }

    public double getHomeTeamAvgHomeXGA() {
        return homeTeamAvgHomeXGA;
    }

    public double getHomeTeamWeightedAvgHomeXGF() {
        return homeTeamWeightedAvgHomeXGF;
    }

    public double getHomeTeamWeightedAvgHomeXGA() {
        return homeTeamWeightedAvgHomeXGA;
    }

    public double getAvgHomeTeamHomePoints() {
        return avgHomeTeamHomePoints;
    }

    public double getLast5HomeTeamHomePoints() {
        return last5HomeTeamHomePoints;
    }

    public double getIfScoredFirstAtHomeHomeTeamPoints() {
        return ifScoredFirstAtHomeHomeTeamPoints;
    }

    public double getIfConceededFirstAtHomeHomeTeamPoints() {
        return ifConceededFirstAtHomeHomeTeamPoints;
    }

    public double getHomeTeamPointsAtHomeAgainstOpposition() {
        return homeTeamPointsAtHomeAgainstOpposition;
    }

    public double getHomeTeamAtHomeMinsWeightedLineupRating() {
        return homeTeamAtHomeMinsWeightedLineupRating;
    }

    public double getHomeTeamAtHomeGamesWeightedLineupRating() {
        return homeTeamAtHomeGamesWeightedLineupRating;
    }

    public double getHomeTeamHomeStrength() {
        return homeTeamHomeStrength;
    }

    public double getAwayTeamAvgAwayGoalsFor() {
        return awayTeamAvgAwayGoalsFor;
    }

    public double getAwayTeamAvgAwayGoalsAgainst() {
        return awayTeamAvgAwayGoalsAgainst;
    }

    public double getAwayTeamAvgAwayXGF() {
        return awayTeamAvgAwayXGF;
    }

    public double getAwayTeamAvgAwayXGA() {
        return awayTeamAvgAwayXGA;
    }

    public double getAwayTeamWeightedAvgAwayXGF() {
        return awayTeamWeightedAvgAwayXGF;
    }

    public double getAwayTeamWeightedAvgAwayXGA() {
        return awayTeamWeightedAvgAwayXGA;
    }

    public double getAvgAwayTeamAwayPoints() {
        return avgAwayTeamAwayPoints;
    }

    public double getLast5AwayTeamAwayPoints() {
        return last5AwayTeamAwayPoints;
    }

    public double getIfScoredFirstAtAwayAwayTeamPoints() {
        return ifScoredFirstAtAwayAwayTeamPoints;
    }

    public double getIfConceededFirstAtAwayAwayTeamPoints() {
        return ifConceededFirstAtAwayAwayTeamPoints;
    }

    public double getAwayTeamPointsAtAwayAgainstOpposition() {
        return awayTeamPointsAtAwayAgainstOpposition;
    }

    public double getAwayTeamAtAwayMinsWeightedLineupRating() {
        return awayTeamAtAwayMinsWeightedLineupRating;
    }

    public double getAwayTeamAtAwayGamesWeightedLineupRating() {
        return awayTeamAtAwayGamesWeightedLineupRating;
    }

    public double getAwayTeamAwayStrength() {
        return awayTeamAwayStrength;
    }

    public double getHomeTeamFormGoalsFor() {
        return homeTeamFormGoalsFor;
    }

    public double getHomeTeamFormGoalsAgainst() {
        return homeTeamFormGoalsAgainst;
    }

    public double getHomeTeamFormXGF() {
        return homeTeamFormXGF;
    }

    public double getHomeTeamFormXGA() {
        return homeTeamFormXGA;
    }

    public double getHomeTeamFormWeightedXGF() {
        return homeTeamFormWeightedXGF;
    }

    public double getHomeTeamFormWeightedXGA() {
        return homeTeamFormWeightedXGA;
    }

    public double getHomeTeamHomeFormGoalsFor() {
        return homeTeamHomeFormGoalsFor;
    }

    public double getHomeTeamHomeFormGoalsAgainst() {
        return homeTeamHomeFormGoalsAgainst;
    }

    public double getHomeTeamHomeFormXGF() {
        return homeTeamHomeFormXGF;
    }

    public double getHomeTeamHomeFormXGA() {
        return homeTeamHomeFormXGA;
    }

    public double getHomeTeamHomeFormWeightedXGF() {
        return homeTeamHomeFormWeightedXGF;
    }

    public double getHomeTeamHomeFormWeightedXGA() {
        return homeTeamHomeFormWeightedXGA;
    }

    public double getAwayTeamFormGoalsFor() {
        return awayTeamFormGoalsFor;
    }

    public double getAwayTeamFormGoalsAgainst() {
        return awayTeamFormGoalsAgainst;
    }

    public double getAwayTeamFormXGF() {
        return awayTeamFormXGF;
    }

    public double getAwayTeamFormXGA() {
        return awayTeamFormXGA;
    }

    public double getAwayTeamFormWeightedXGF() {
        return awayTeamFormWeightedXGF;
    }

    public double getAwayTeamFormWeightedXGA() {
        return awayTeamFormWeightedXGA;
    }

    public double getAwayTeamAwayFormGoalsFor() {
        return awayTeamAwayFormGoalsFor;
    }

    public double getAwayTeamAwayFormGoalsAgainst() {
        return awayTeamAwayFormGoalsAgainst;
    }

    public double getAwayTeamAwayFormXGF() {
        return awayTeamAwayFormXGF;
    }

    public double getAwayTeamAwayFormXGA() {
        return awayTeamAwayFormXGA;
    }

    public double getAwayTeamAwayFormWeightedXGF() {
        return awayTeamAwayFormWeightedXGF;
    }

    public double getAwayTeamAwayFormWeightedXGA() {
        return awayTeamAwayFormWeightedXGA;
    }

    public double getHomeTeamAvgFormGoalsFor() {
        return homeTeamAvgFormGoalsFor;
    }

    public double getHomeTeamAvgFormGoalsAgainst() {
        return homeTeamAvgFormGoalsAgainst;
    }

    public double getHomeTeamAvgFormXGF() {
        return homeTeamAvgFormXGF;
    }

    public double getHomeTeamAvgFormXGA() {
        return homeTeamAvgFormXGA;
    }

    public double getHomeTeamAvgFormWeightedXGF() {
        return homeTeamAvgFormWeightedXGF;
    }

    public double getHomeTeamAvgFormWeightedXGA() {
        return homeTeamAvgFormWeightedXGA;
    }

    public double getHomeTeamAvgFormXGFLast5Games() {
        return homeTeamAvgFormXGFLast5Games;
    }

    public double getHomeTeamAvgFormXGALast5Games() {
        return homeTeamAvgFormXGALast5Games;
    }

    public double getHomeTeamHomeAvgFormGoalsFor() {
        return homeTeamHomeAvgFormGoalsFor;
    }

    public double getHomeTeamHomeAvgFormGoalsAgainst() {
        return homeTeamHomeAvgFormGoalsAgainst;
    }

    public double getHomeTeamHomeAvgFormXGF() {
        return homeTeamHomeAvgFormXGF;
    }

    public double getHomeTeamHomeAvgFormXGA() {
        return homeTeamHomeAvgFormXGA;
    }

    public double getHomeTeamHomeAvgFormWeightedXGF() {
        return homeTeamHomeAvgFormWeightedXGF;
    }

    public double getHomeTeamHomeAvgFormWeightedXGA() {
        return homeTeamHomeAvgFormWeightedXGA;
    }

    public double getHomeTeamHomeAvgFormXGFLast5Games() {
        return homeTeamHomeAvgFormXGFLast5Games;
    }

    public double getHomeTeamHomeAvgFormXGALast5Games() {
        return homeTeamHomeAvgFormXGALast5Games;
    }

    public double getAwayTeamAvgFormGoalsFor() {
        return awayTeamAvgFormGoalsFor;
    }

    public double getAwayTeamAvgFormGoalsAgainst() {
        return awayTeamAvgFormGoalsAgainst;
    }

    public double getAwayTeamAvgFormXGF() {
        return awayTeamAvgFormXGF;
    }

    public double getAwayTeamAvgFormXGA() {
        return awayTeamAvgFormXGA;
    }

    public double getAwayTeamAvgFormWeightedXGF() {
        return awayTeamAvgFormWeightedXGF;
    }

    public double getAwayTeamAvgFormWeightedXGA() {
        return awayTeamAvgFormWeightedXGA;
    }

    public double getAwayTeamAvgFormXGFLast5Games() {
        return awayTeamAvgFormXGFLast5Games;
    }

    public double getAwayTeamAvgFormXGALast5Games() {
        return awayTeamAvgFormXGALast5Games;
    }

    public double getAwayTeamAwayAvgFormGoalsFor() {
        return awayTeamAwayAvgFormGoalsFor;
    }

    public double getAwayTeamAwayAvgFormGoalsAgainst() {
        return awayTeamAwayAvgFormGoalsAgainst;
    }

    public double getAwayTeamAwayAvgFormXGF() {
        return awayTeamAwayAvgFormXGF;
    }

    public double getAwayTeamAwayAvgFormXGA() {
        return awayTeamAwayAvgFormXGA;
    }

    public double getAwayTeamAwayAvgFormWeightedXGF() {
        return awayTeamAwayAvgFormWeightedXGF;
    }

    public double getAwayTeamAwayAvgFormWeightedXGA() {
        return awayTeamAwayAvgFormWeightedXGA;
    }

    public double getAwayTeamAwayAvgFormXGFLast5Games() {
        return awayTeamAwayAvgFormXGFLast5Games;
    }

    public double getAwayTeamAwayAvgFormXGALast5Games() {
        return awayTeamAwayAvgFormXGALast5Games;
    }

    public double getHomeTeamsOpponentsWholeSeasonPPG() {
        return homeTeamsOpponentsWholeSeasonPPG;
    }

    public double getHomeTeamsLast5OpponentsWholeSeasonPPG() {
        return homeTeamsLast5OpponentsWholeSeasonPPG;
    }

    public double getHomeTeamsHomeOpponentsWholeSeasonPPG() {
        return homeTeamsHomeOpponentsWholeSeasonPPG;
    }

    public double getHomeTeamsLast5HomeOpponentsWholeSeasonPPG() {
        return homeTeamsLast5HomeOpponentsWholeSeasonPPG;
    }

    public double getAwayTeamsOpponentsWholeSeasonPPG() {
        return awayTeamsOpponentsWholeSeasonPPG;
    }

    public double getAwayTeamsLast5OpponentsWholeSeasonPPG() {
        return awayTeamsLast5OpponentsWholeSeasonPPG;
    }

    public double getAwayTeamsAwayOpponentsWholeSeasonPPG() {
        return awayTeamsAwayOpponentsWholeSeasonPPG;
    }

    public double getAwayTeamsLast5AwayOpponentsWholeSeasonPPG() {
        return awayTeamsLast5AwayOpponentsWholeSeasonPPG;
    }

    public double getHomeTeamsOpponentsLast5PPG() {
        return homeTeamsOpponentsLast5PPG;
    }

    public double getHomeTeamLast5OpponentsLast5PPG() {
        return homeTeamLast5OpponentsLast5PPG;
    }

    public double getHomeTeamsHomeOpponentsLast5PPG() {
        return homeTeamsHomeOpponentsLast5PPG;
    }

    public double getHomeTeamLast5HomeOpponentsLast5PPG() {
        return homeTeamLast5HomeOpponentsLast5PPG;
    }

    public double getAwayTeamsOpponentsLast5PPG() {
        return awayTeamsOpponentsLast5PPG;
    }

    public double getAwayTeamLast5OpponentsLast5PPG() {
        return awayTeamLast5OpponentsLast5PPG;
    }

    public double getAwayTeamsAwayOpponentsLast5PPG() {
        return awayTeamsAwayOpponentsLast5PPG;
    }

    public double getAwayTeamLast5AwayOpponentsLast5PPG() {
        return awayTeamLast5AwayOpponentsLast5PPG;
    }

    public double getHomeTeamsAvgNumbCleanSheets() {
        return homeTeamsAvgNumbCleanSheets;
    }

    public double getHomeTeamsLast5AvgNumbCleanSheets() {
        return homeTeamsLast5AvgNumbCleanSheets;
    }

    public double getHomeTeamsHomeAvgNumbCleanSheets() {
        return homeTeamsHomeAvgNumbCleanSheets;
    }

    public double getHomeTeamsLast5HomeAvgNumbCleanSheets() {
        return homeTeamsLast5HomeAvgNumbCleanSheets;
    }

    public double getAwayTeamsAvgNumbCleanSheets() {
        return awayTeamsAvgNumbCleanSheets;
    }

    public double getAwayTeamsLast5AvgNumbCleanSheets() {
        return awayTeamsLast5AvgNumbCleanSheets;
    }

    public double getAwayTeamsAwayAvgNumbCleanSheets() {
        return awayTeamsAwayAvgNumbCleanSheets;
    }

    public double getAwayTeamsLast5AwayAvgNumbCleanSheets() {
        return awayTeamsLast5AwayAvgNumbCleanSheets;
    }
}
