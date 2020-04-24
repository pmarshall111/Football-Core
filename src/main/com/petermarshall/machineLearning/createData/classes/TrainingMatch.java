package com.petermarshall.machineLearning.createData.classes;

import java.util.ArrayList;
import java.util.Date;

public class TrainingMatch {

    //HOME TOTAL DATA
    private String homeTeamName;
    private double homeTeamAvgGoalsFor;
    private double homeTeamAvgGoalsAgainst;
    private double homeTeamAvgXGF;
    private double homeTeamAvgXGA;
    private double homeTeamWeightedAvgXGF;
    private double homeTeamWeightedAvgXGA;
    
    //NEW FORM CALCULATED FIELDS (USING WEIGHTED AVG)
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
    
    private double avgHomeTeamPoints;
    private double last5HomeTeamPoints;
    private double ifScoredFirstHomeTeamPoints;
    private double ifConceededFirstHomeTeamPoints;
    private double homeTeamPointsAgainstOpposition;
    private double homeTeamMinsWeightedLineupRating;
    private double homeTeamGamesWeightedLineupRating;
    //calc'd by looking at how many minutes are on the pitch vs the minutes of highest 11 players (justification: highest 11 players minutes will be the players the manager thinks
    // works best in his team.)
    private double homeTeamStrength;
    
    private double homeTeamsOpponentsWholeSeasonPPG;
    private double homeTeamsLast5OpponentsWholeSeasonPPG;
    private double homeTeamsOpponentsLast5PPG;
    private double homeTeamLast5OpponentsLast5PPG;
    //clean sheet stats
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
    private int homeTeamHomeGamesPlayed;
    private int awayTeamAwayGamesPlayed;
    private int seasonYearStart;

    public void setMiscStats(double homeTeamProbability, double awayTeamProbability, double drawProbability, int homeScore, int awayScore, Date kickoffTime,
                             int homeTeamHomeGamesPlayed, int awayTeamAwayGamesPlayed, String seasonYears) {

        this.homeTeamProbability = homeTeamProbability;
        this.awayTeamProbability = awayTeamProbability;
        this.drawProbability = drawProbability;
        this.homeScore = homeScore;
        this.awayScore = awayScore;
        this.kickoffTime = kickoffTime;
        this.homeTeamHomeGamesPlayed = homeTeamHomeGamesPlayed;
        this.awayTeamAwayGamesPlayed = awayTeamAwayGamesPlayed;
        this.seasonYearStart = Integer.parseInt(seasonYears.substring(0, 2));
    }

    public void setHomeTeamStats (String homeTeamName, double homeTeamAvgGoalsFor, double homeTeamAvgGoalsAgainst, double homeTeamAvgXGF, double homeTeamAvgXGA,
                                  double homeTeamWeightedAvgXGF, double homeTeamWeightedAvgXGA, double homeTeamFormGoalsFor, double homeTeamFormGoalsAgainst, double homeTeamFormXGF,
                                  double homeTeamFormXGA, double homeTeamFormWeightedXGF, double homeTeamFormWeightedXGA, 
                                  double homeTeamAvgFormGoalsFor, double homeTeamAvgFormGoalsAgainst, double homeTeamAvgFormXGF, double homeTeamAvgFormXGA, 
                                  double homeTeamAvgFormWeightedXGF, double homeTeamAvgFormWeightedXGA, double homeTeamAvgFormXGFLast5Games, double homeTeamAvgFormXGALast5Games,
                                  double avgHomeTeamPoints, double last5HomeTeamPoints,
                                  double ifScoredFirstHomeTeamPoints, double ifConceededFirstHomeTeamPoints, double homeTeamPointsAgainstOpposition,
                                  double homeTeamMinsWeightedLineupRating, double homeTeamGamesWeighredLineupRating, double homeTeamStrength,
                                  double homeTeamsOpponentsWholeSeasonPPG, double homeTeamsLast5OpponentsWholeSeasonPPG,
                                  double homeTeamsOpponentsLast5PPG, double homeTeamLast5OpponentsLast5PPG,
                                  double homeTeamsAvgNumbCleanSheets, double homeTeamsLast5AvgNumbCleanSheets) {

        this.homeTeamName = homeTeamName;
        this.homeTeamAvgGoalsFor = homeTeamAvgGoalsFor;
        this.homeTeamAvgGoalsAgainst = homeTeamAvgGoalsAgainst;
        this.homeTeamAvgXGF = homeTeamAvgXGF;
        this.homeTeamAvgXGA = homeTeamAvgXGA;
        this.homeTeamWeightedAvgXGF = homeTeamWeightedAvgXGF;
        this.homeTeamWeightedAvgXGA = homeTeamWeightedAvgXGA;
        this.homeTeamFormGoalsFor = homeTeamFormGoalsFor;
        this.homeTeamFormGoalsAgainst = homeTeamFormGoalsAgainst;
        this.homeTeamFormXGF = homeTeamFormXGF;
        this.homeTeamFormXGA = homeTeamFormXGA;
        this.homeTeamFormWeightedXGF = homeTeamFormWeightedXGF;
        this.homeTeamFormWeightedXGA = homeTeamFormWeightedXGA;
        this.homeTeamAvgFormGoalsFor = homeTeamAvgFormGoalsFor;
        this.homeTeamAvgFormGoalsAgainst = homeTeamAvgFormGoalsAgainst;
        this.homeTeamAvgFormXGF = homeTeamAvgFormXGF;
        this.homeTeamAvgFormXGA = homeTeamAvgFormXGA;
        this.homeTeamAvgFormWeightedXGF = homeTeamAvgFormWeightedXGF;
        this.homeTeamAvgFormWeightedXGA = homeTeamAvgFormWeightedXGA;
        this.homeTeamAvgFormXGFLast5Games = homeTeamAvgFormXGFLast5Games;
        this.homeTeamAvgFormXGALast5Games = homeTeamAvgFormXGALast5Games;
        this.avgHomeTeamPoints = avgHomeTeamPoints;
        this.last5HomeTeamPoints = last5HomeTeamPoints;
        this.ifScoredFirstHomeTeamPoints = ifScoredFirstHomeTeamPoints;
        this.ifConceededFirstHomeTeamPoints = ifConceededFirstHomeTeamPoints;
        this.homeTeamPointsAgainstOpposition = homeTeamPointsAgainstOpposition;
        this.homeTeamMinsWeightedLineupRating = homeTeamMinsWeightedLineupRating;
        this.homeTeamGamesWeightedLineupRating = homeTeamGamesWeighredLineupRating;
        this.homeTeamStrength = homeTeamStrength;
        this.homeTeamsOpponentsWholeSeasonPPG = homeTeamsOpponentsWholeSeasonPPG;
        this.homeTeamsLast5OpponentsWholeSeasonPPG = homeTeamsLast5OpponentsWholeSeasonPPG;
        this.homeTeamsOpponentsLast5PPG = homeTeamsOpponentsLast5PPG;
        this.homeTeamLast5OpponentsLast5PPG = homeTeamLast5OpponentsLast5PPG;
    }

    public void setHomeTeamAtHomeStats (double homeTeamAvgHomeGoalsFor, double homeTeamAvgHomeGoalsAgainst, double homeTeamAvgHomeXGF, double homeTeamAvgHomeXGA,
                                  double homeTeamWeightedAvgHomeXGF, double homeTeamWeightedAvgHomeXGA, double homeTeamHomeFormGoalsFor, double homeTeamHomeFormGoalsAgainst, double homeTeamHomeFormXGF,
                                        double homeTeamHomeFormXGA, double homeTeamHomeFormWeightedXGF, double homeTeamHomeFormWeightedXGA,
                                        double homeTeamHomeAvgFormGoalsFor, double homeTeamHomeAvgFormGoalsAgainst, double homeTeamHomeAvgFormXGF, double homeTeamHomeAvgFormXGA,
                                        double homeTeamHomeAvgFormWeightedXGF, double homeTeamHomeAvgFormWeightedXGA, double homeTeamHomeAvgFormXGFLast5Games, double homeTeamHomeAvgFormXGALast5Games,
                                        double avgHomeTeamHomePoints, double last5HomeTeamHomePoints,
                                  double ifScoredFirstAtHomeHomeTeamPoints, double ifConceededFirstAtHomeHomeTeamPoints, double homeTeamPointsAtHomeAgainstOpposition,
                                  double homeTeamAtHomeMinsWeightedLineupRating, double homeTeamAtHomeGamesWeightedLineupRating, double homeTeamHomeStrength,
                                        double homeTeamsHomeOpponentsWholeSeasonPPG, double homeTeamsLast5HomeOpponentsWholeSeasonPPG,
                                        double homeTeamsHomeOpponentsLast5PPG, double homeTeamLast5HomeOpponentsLast5PPG,
                                        double homeTeamsHomeAvgNumbCleanSheets, double homeTeamsLast5HomeAvgNumbCleanSheets) {
        
        this.homeTeamAvgHomeGoalsFor = homeTeamAvgHomeGoalsFor;
        this.homeTeamAvgHomeGoalsAgainst = homeTeamAvgHomeGoalsAgainst;
        this.homeTeamAvgHomeXGF = homeTeamAvgHomeXGF;
        this.homeTeamAvgHomeXGA = homeTeamAvgHomeXGA;
        this.homeTeamWeightedAvgHomeXGF = homeTeamWeightedAvgHomeXGF;
        this.homeTeamWeightedAvgHomeXGA = homeTeamWeightedAvgHomeXGA;
        this.homeTeamHomeFormGoalsFor = homeTeamHomeFormGoalsFor;
        this.homeTeamHomeFormGoalsAgainst = homeTeamHomeFormGoalsAgainst;
        this.homeTeamHomeFormXGF = homeTeamHomeFormXGF;
        this.homeTeamHomeFormXGA = homeTeamHomeFormXGA;
        this.homeTeamHomeFormWeightedXGF = homeTeamHomeFormWeightedXGF;
        this.homeTeamHomeFormWeightedXGA = homeTeamHomeFormWeightedXGA;
        this.homeTeamHomeAvgFormGoalsFor = homeTeamHomeAvgFormGoalsFor;
        this.homeTeamHomeAvgFormGoalsAgainst = homeTeamHomeAvgFormGoalsAgainst;
        this.homeTeamHomeAvgFormXGF = homeTeamHomeAvgFormXGF;
        this.homeTeamHomeAvgFormXGA = homeTeamHomeAvgFormXGA;
        this.homeTeamHomeAvgFormWeightedXGF = homeTeamHomeAvgFormWeightedXGF;
        this.homeTeamHomeAvgFormWeightedXGA = homeTeamHomeAvgFormWeightedXGA;
        this.homeTeamHomeAvgFormXGFLast5Games = homeTeamHomeAvgFormXGFLast5Games;
        this.homeTeamHomeAvgFormXGALast5Games = homeTeamHomeAvgFormXGALast5Games;
        this.avgHomeTeamHomePoints = avgHomeTeamHomePoints;
        this.last5HomeTeamHomePoints = last5HomeTeamHomePoints;
        this.ifScoredFirstAtHomeHomeTeamPoints = ifScoredFirstAtHomeHomeTeamPoints;
        this.ifConceededFirstAtHomeHomeTeamPoints = ifConceededFirstAtHomeHomeTeamPoints;
        this.homeTeamPointsAtHomeAgainstOpposition = homeTeamPointsAtHomeAgainstOpposition;
        this.homeTeamAtHomeMinsWeightedLineupRating = homeTeamAtHomeMinsWeightedLineupRating;
        this.homeTeamAtHomeGamesWeightedLineupRating = homeTeamAtHomeGamesWeightedLineupRating;
        this.homeTeamHomeStrength = homeTeamHomeStrength;
        this.homeTeamsHomeOpponentsWholeSeasonPPG = homeTeamsHomeOpponentsWholeSeasonPPG;
        this.homeTeamsLast5HomeOpponentsWholeSeasonPPG = homeTeamsLast5HomeOpponentsWholeSeasonPPG;
        this.homeTeamsHomeOpponentsLast5PPG = homeTeamsHomeOpponentsLast5PPG;
        this.homeTeamLast5HomeOpponentsLast5PPG = homeTeamLast5HomeOpponentsLast5PPG;
        this.homeTeamsHomeAvgNumbCleanSheets = homeTeamsHomeAvgNumbCleanSheets;
        this.homeTeamsLast5HomeAvgNumbCleanSheets = homeTeamsLast5HomeAvgNumbCleanSheets;
    }

    public void setAwayTeamStats (String awayTeamName, double awayTeamAvgGoalsFor, double awayTeamAvgGoalsAgainst, double awayTeamAvgXGF, double awayTeamAvgXGA,
                         double awayTeamWeightedAvgXGF, double awayTeamWeightedAvgXGA, double awayTeamFormGoalsFor, double awayTeamFormGoalsAgainst, double awayTeamFormXGF,
                                  double awayTeamFormXGA, double awayTeamFormWeightedXGF, double awayTeamFormWeightedXGA,
                                  double awayTeamAvgFormGoalsFor, double awayTeamAvgFormGoalsAgainst, double awayTeamAvgFormXGF, double awayTeamAvgFormXGA,
                                  double awayTeamAvgFormWeightedXGF, double awayTeamAvgFormWeightedXGA, double awayTeamAvgFormXGFLast5Games, double awayTeamAvgFormXGALast5Games,
                                  double avgAwayTeamPoints, double last5AwayTeamPoints,
                         double ifScoredFirstAwayTeamPoints, double ifConceededFirstAwayTeamPoints, double awayTeamPointsAgainstOpposition,
                         double awayTeamMinsWeightedLineupRating, double awayTeamGamesWeighredLineupRating, double awayTeamStrength,
                                  double awayTeamsOpponentsWholeSeasonPPG, double awayTeamsLast5OpponentsWholeSeasonPPG,
                                  double awayTeamsOpponentsLast5PPG, double awayTeamLast5OpponentsLast5PPG,
                                  double awayTeamsAvgNumbCleanSheets, double awayTeamsLast5AvgNumbCleanSheets) {

        this.awayTeamName = awayTeamName;
        this.awayTeamAvgGoalsFor = awayTeamAvgGoalsFor;
        this.awayTeamAvgGoalsAgainst = awayTeamAvgGoalsAgainst;
        this.awayTeamAvgXGF = awayTeamAvgXGF;
        this.awayTeamAvgXGA = awayTeamAvgXGA;
        this.awayTeamWeightedAvgXGF = awayTeamWeightedAvgXGF;
        this.awayTeamWeightedAvgXGA = awayTeamWeightedAvgXGA;
        this.awayTeamFormGoalsFor = awayTeamFormGoalsFor;
        this.awayTeamFormGoalsAgainst = awayTeamFormGoalsAgainst;
        this.awayTeamFormXGF = awayTeamFormXGF;
        this.awayTeamFormXGA = awayTeamFormXGA;
        this.awayTeamFormWeightedXGF = awayTeamFormWeightedXGF;
        this.awayTeamFormWeightedXGA = awayTeamFormWeightedXGA;
        this.awayTeamAvgFormGoalsFor = awayTeamAvgFormGoalsFor;
        this.awayTeamAvgFormGoalsAgainst = awayTeamAvgFormGoalsAgainst;
        this.awayTeamAvgFormXGF = awayTeamAvgFormXGF;
        this.awayTeamAvgFormXGA = awayTeamAvgFormXGA;
        this.awayTeamAvgFormWeightedXGF = awayTeamAvgFormWeightedXGF;
        this.awayTeamAvgFormWeightedXGA = awayTeamAvgFormWeightedXGA;
        this.awayTeamAvgFormXGFLast5Games = awayTeamAvgFormXGFLast5Games;
        this.awayTeamAvgFormXGALast5Games = awayTeamAvgFormXGALast5Games;
        this.avgAwayTeamPoints = avgAwayTeamPoints;
        this.last5AwayTeamPoints = last5AwayTeamPoints;
        this.ifScoredFirstAwayTeamPoints = ifScoredFirstAwayTeamPoints;
        this.ifConceededFirstAwayTeamPoints = ifConceededFirstAwayTeamPoints;
        this.awayTeamPointsAgainstOpposition = awayTeamPointsAgainstOpposition;
        this.awayTeamMinsWeightedLineupRating = awayTeamMinsWeightedLineupRating;
        this.awayTeamGamesWeightedLineupRating = awayTeamGamesWeighredLineupRating;
        this.awayTeamStrength = awayTeamStrength;
        this.awayTeamsOpponentsWholeSeasonPPG = awayTeamsOpponentsWholeSeasonPPG;
        this.awayTeamsLast5OpponentsWholeSeasonPPG = awayTeamsLast5OpponentsWholeSeasonPPG;
        this.awayTeamsOpponentsLast5PPG = awayTeamsOpponentsLast5PPG;
        this.awayTeamLast5OpponentsLast5PPG = awayTeamLast5OpponentsLast5PPG;
        this.awayTeamsAvgNumbCleanSheets = awayTeamsAvgNumbCleanSheets;
        this.awayTeamsLast5AvgNumbCleanSheets = awayTeamsLast5AvgNumbCleanSheets;
    }


    public void setAwayTeamAtAwayStats (double awayTeamAvgAwayGoalsFor, double awayTeamAvgAwayGoalsAgainst, double awayTeamAvgAwayXGF, double awayTeamAvgAwayXGA,
                                        double awayTeamWeightedAvgAwayXGF, double awayTeamWeightedAvgAwayXGA, double awayTeamAwayFormGoalsFor, double awayTeamAwayFormGoalsAgainst, double awayTeamAwayFormXGF,
                                        double awayTeamAwayFormXGA, double awayTeamAwayFormWeightedXGF, double awayTeamAwayFormWeightedXGA,
                                        double awayTeamAwayAvgFormGoalsFor, double awayTeamAwayAvgFormGoalsAgainst, double awayTeamAwayAvgFormXGF, double awayTeamAwayAvgFormXGA,
                                        double awayTeamAwayAvgFormWeightedXGF, double awayTeamAwayAvgFormWeightedXGA, double awayTeamAwayAvgFormXGFLast5Games, double awayTeamAwayAvgFormXGALast5Games,
                                        double avgAwayTeamAwayPoints, double last5AwayTeamAwayPoints,
                                        double ifScoredFirstAtAwayAwayTeamPoints, double ifConceededFirstAtAwayAwayTeamPoints, double awayTeamPointsAtAwayAgainstOpposition,
                                        double awayTeamAtAwayMinsWeightedLineupRating, double awayTeamAtAwayGamesWeightedLineupRating, double awayTeamAwayStrength,
                                        double awayTeamsAwayOpponentsWholeSeasonPPG, double awayTeamsLast5AwayOpponentsWholeSeasonPPG,
                                        double awayTeamsAwayOpponentsLast5PPG, double awayTeamLast5AwayOpponentsLast5PPG,
                                        double awayTeamsAwayAvgNumbCleanSheets, double awayTeamsLast5AwayAvgNumbCleanSheets) {

        this.awayTeamAvgAwayGoalsFor = awayTeamAvgAwayGoalsFor;
        this.awayTeamAvgAwayGoalsAgainst = awayTeamAvgAwayGoalsAgainst;
        this.awayTeamAvgAwayXGF = awayTeamAvgAwayXGF;
        this.awayTeamAvgAwayXGA = awayTeamAvgAwayXGA;
        this.awayTeamWeightedAvgAwayXGF = awayTeamWeightedAvgAwayXGF;
        this.awayTeamWeightedAvgAwayXGA = awayTeamWeightedAvgAwayXGA;
        this.awayTeamAwayFormGoalsFor = awayTeamAwayFormGoalsFor;
        this.awayTeamAwayFormGoalsAgainst = awayTeamAwayFormGoalsAgainst;
        this.awayTeamAwayFormXGF = awayTeamAwayFormXGF;
        this.awayTeamAwayFormXGA = awayTeamAwayFormXGA;
        this.awayTeamAwayFormWeightedXGF = awayTeamAwayFormWeightedXGF;
        this.awayTeamAwayFormWeightedXGA = awayTeamAwayFormWeightedXGA;
        this.awayTeamAwayAvgFormGoalsFor = awayTeamAwayAvgFormGoalsFor;
        this.awayTeamAwayAvgFormGoalsAgainst = awayTeamAwayAvgFormGoalsAgainst;
        this.awayTeamAwayAvgFormXGF = awayTeamAwayAvgFormXGF;
        this.awayTeamAwayAvgFormXGA = awayTeamAwayAvgFormXGA;
        this.awayTeamAwayAvgFormWeightedXGF = awayTeamAwayAvgFormWeightedXGF;
        this.awayTeamAwayAvgFormWeightedXGA = awayTeamAwayAvgFormWeightedXGA;
        this.awayTeamAwayAvgFormXGFLast5Games = awayTeamAwayAvgFormXGFLast5Games;
        this.awayTeamAwayAvgFormXGALast5Games = awayTeamAwayAvgFormXGALast5Games;
        this.avgAwayTeamAwayPoints = avgAwayTeamAwayPoints;
        this.last5AwayTeamAwayPoints = last5AwayTeamAwayPoints;
        this.ifScoredFirstAtAwayAwayTeamPoints = ifScoredFirstAtAwayAwayTeamPoints;
        this.ifConceededFirstAtAwayAwayTeamPoints = ifConceededFirstAtAwayAwayTeamPoints;
        this.awayTeamPointsAtAwayAgainstOpposition = awayTeamPointsAtAwayAgainstOpposition;
        this.awayTeamAtAwayMinsWeightedLineupRating = awayTeamAtAwayMinsWeightedLineupRating;
        this.awayTeamAtAwayGamesWeightedLineupRating = awayTeamAtAwayGamesWeightedLineupRating;
        this.awayTeamAwayStrength = awayTeamAwayStrength;
        this.awayTeamsAwayOpponentsWholeSeasonPPG = awayTeamsAwayOpponentsWholeSeasonPPG;
        this.awayTeamsLast5AwayOpponentsWholeSeasonPPG = awayTeamsLast5AwayOpponentsWholeSeasonPPG;
        this.awayTeamsAwayOpponentsLast5PPG = awayTeamsAwayOpponentsLast5PPG;
        this.awayTeamLast5AwayOpponentsLast5PPG = awayTeamLast5AwayOpponentsLast5PPG;
        this.awayTeamsAwayAvgNumbCleanSheets = awayTeamsAwayAvgNumbCleanSheets;
        this.awayTeamsLast5AwayAvgNumbCleanSheets = awayTeamsLast5AwayAvgNumbCleanSheets;
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

    public int getHomeTeamHomeGamesPlayed() {
        return homeTeamHomeGamesPlayed;
    }

    public int getAwayTeamAwayGamesPlayed() {
        return awayTeamAwayGamesPlayed;
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
