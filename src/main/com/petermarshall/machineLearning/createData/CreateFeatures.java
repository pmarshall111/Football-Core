package com.petermarshall.machineLearning.createData;

import com.petermarshall.machineLearning.createData.classes.GamesSelector;
import com.petermarshall.machineLearning.createData.classes.TrainingTeam;
import com.petermarshall.machineLearning.createData.classes.TrainingTeamsSeason;

import java.util.ArrayList;

import static com.petermarshall.machineLearning.createData.CalculatePastStats.COMPARE_LAST_N_GAMES;
import static com.petermarshall.machineLearning.createData.CalculatePastStats.NUMB_SEASONS_HISTORY;
import static com.petermarshall.machineLearning.createData.classes.GamesSelector.*;

public class CreateFeatures {
    public static ArrayList<Double> getFeatures(TrainingTeam homeTeam, TrainingTeamsSeason homeSeason,
                                                TrainingTeam awayTeam, TrainingTeamsSeason awaySeason,
                                                ArrayList<String> homePlayersNames, ArrayList<String> awayPlayersNames,
                                                int seasonYearStart, int result) {
        ArrayList<Double> features = new ArrayList<>();
        features.add((double) result);

        //adding bias parameter of 1
        features.add(1d);

        //homeStats
        features.add(homeSeason.getAvgGoalsFor(ALL_GAMES));
        features.add(homeSeason.getAvgGoalsAgainst(ALL_GAMES));
        features.add(homeSeason.getAvgXGF(ALL_GAMES));
        features.add(homeSeason.getAvgXGA(ALL_GAMES));
        features.add(homeSeason.getWeightedAvgXGF(ALL_GAMES));
        features.add(homeSeason.getWeightedAvgXGA(ALL_GAMES));
        features.add(homeSeason.getFormGoalsFor(ALL_GAMES));
        features.add(homeSeason.getFormGoalsAgainst(ALL_GAMES));
        features.add(homeSeason.getFormXGF(ALL_GAMES));
        features.add(homeSeason.getFormXGA(ALL_GAMES));
        features.add(homeSeason.getFormWeightedXGF(ALL_GAMES));
        features.add(homeSeason.getFormWeightedXGA(ALL_GAMES));
        features.add(homeSeason.getAvgFormGoalsFor(ALL_GAMES, 0));
        features.add(homeSeason.getAvgFormGoalsAgainst(ALL_GAMES, 0));
        features.add(homeSeason.getAvgFormXGF(ALL_GAMES, 0));
        features.add(homeSeason.getAvgFormXGA(ALL_GAMES, 0));
        features.add(homeSeason.getAvgFormWeightedXGF(ALL_GAMES, 0));
        features.add(homeSeason.getAvgFormWeightedXGA(ALL_GAMES, 0));
        features.add(homeSeason.getFormXGFOverLastNGames(ALL_GAMES, COMPARE_LAST_N_GAMES));
        features.add(homeSeason.getFormXGAOverLastNGames(ALL_GAMES, COMPARE_LAST_N_GAMES));
        features.add(homeSeason.getAvgPoints(ALL_GAMES));
        features.add(homeSeason.getAvgPointsOverLastXGames(ALL_GAMES, COMPARE_LAST_N_GAMES));
        features.add(homeSeason.getAvgPointsWhenScoredFirst(ALL_GAMES));
        features.add(homeSeason.getAvgPointsWhenConceededFirst(ALL_GAMES));
        features.add(homeTeam.getPointsOfLastMatchups(awayTeam.getTeamName(), ALL_GAMES, seasonYearStart - NUMB_SEASONS_HISTORY));
        features.add(homeSeason.getMinsWeightedLineupRating(ALL_GAMES, homePlayersNames));
        features.add(homeSeason.getGamesWeightedLineupRating(ALL_GAMES, homePlayersNames));
        features.add(homeSeason.getLineupStrength(ALL_GAMES, homePlayersNames));
        features.add(homeSeason.getAvgPointsOfAllOpponentsGamesWholeSeason(ALL_GAMES));
        features.add(homeSeason.getAvgPointsOfLastXOpponentsGamesWholeSeason(ALL_GAMES, COMPARE_LAST_N_GAMES));
        features.add(homeSeason.getAvgPointsOfAllOpponentsLast5Games(ALL_GAMES));
        features.add(homeSeason.getAvgPointsOfLastXOpponentsLast5Games(ALL_GAMES, COMPARE_LAST_N_GAMES));
        features.add(homeSeason.getAvgNumberOfCleanSheets(ALL_GAMES));
        features.add(homeSeason.getAvgNumberOfCleanSheetsLastXGames(ALL_GAMES, COMPARE_LAST_N_GAMES, true));

        //awayStats
        features.add(awaySeason.getAvgGoalsFor(ALL_GAMES));
        features.add(awaySeason.getAvgGoalsAgainst(ALL_GAMES));
        features.add(awaySeason.getAvgXGF(ALL_GAMES));
        features.add(awaySeason.getAvgXGA(ALL_GAMES));
        features.add(awaySeason.getWeightedAvgXGF(ALL_GAMES));
        features.add(awaySeason.getWeightedAvgXGA(ALL_GAMES));
        features.add(awaySeason.getFormGoalsFor(ALL_GAMES));
        features.add(awaySeason.getFormGoalsAgainst(ALL_GAMES));
        features.add(awaySeason.getFormXGF(ALL_GAMES));
        features.add(awaySeason.getFormXGA(ALL_GAMES));
        features.add(awaySeason.getFormWeightedXGF(ALL_GAMES));
        features.add(awaySeason.getFormWeightedXGA(ALL_GAMES));
        features.add(awaySeason.getAvgFormGoalsFor(ALL_GAMES, 0));
        features.add(awaySeason.getAvgFormGoalsAgainst(ALL_GAMES, 0));
        features.add(awaySeason.getAvgFormXGF(ALL_GAMES, 0));
        features.add(awaySeason.getAvgFormXGA(ALL_GAMES, 0));
        features.add(awaySeason.getAvgFormWeightedXGF(ALL_GAMES, 0));
        features.add(awaySeason.getAvgFormWeightedXGA(ALL_GAMES, 0));
        features.add(awaySeason.getFormXGFOverLastNGames(ALL_GAMES, COMPARE_LAST_N_GAMES));
        features.add(awaySeason.getFormXGAOverLastNGames(ALL_GAMES, COMPARE_LAST_N_GAMES));
        features.add(awaySeason.getAvgPoints(ALL_GAMES));
        features.add(awaySeason.getAvgPointsOverLastXGames(ALL_GAMES, COMPARE_LAST_N_GAMES));
        features.add(awaySeason.getAvgPointsWhenScoredFirst(ALL_GAMES));
        features.add(awaySeason.getAvgPointsWhenConceededFirst(ALL_GAMES));
        features.add(awayTeam.getPointsOfLastMatchups(homeTeam.getTeamName(), ALL_GAMES, seasonYearStart - NUMB_SEASONS_HISTORY));
        features.add(awaySeason.getMinsWeightedLineupRating(ALL_GAMES, awayPlayersNames));
        features.add(awaySeason.getGamesWeightedLineupRating(ALL_GAMES, awayPlayersNames));
        features.add(awaySeason.getLineupStrength(ALL_GAMES, awayPlayersNames));
        features.add(awaySeason.getAvgPointsOfAllOpponentsGamesWholeSeason(ALL_GAMES));
        features.add(awaySeason.getAvgPointsOfLastXOpponentsGamesWholeSeason(ALL_GAMES, COMPARE_LAST_N_GAMES));
        features.add(awaySeason.getAvgPointsOfAllOpponentsLast5Games(ALL_GAMES));
        features.add(awaySeason.getAvgPointsOfLastXOpponentsLast5Games(ALL_GAMES, COMPARE_LAST_N_GAMES));
        features.add(awaySeason.getAvgNumberOfCleanSheets(ALL_GAMES));
        features.add(awaySeason.getAvgNumberOfCleanSheetsLastXGames(ALL_GAMES, COMPARE_LAST_N_GAMES, true));

        return features;
    }

    public static ArrayList<Double> getFeaturesNoLineups(TrainingTeam homeTeam, TrainingTeamsSeason homeSeason,
                                                TrainingTeam awayTeam, TrainingTeamsSeason awaySeason,
                                                int seasonYearStart, int result) {
        ArrayList<Double> features = new ArrayList<>();
        features.add((double) result);

        //adding bias parameter of 1
        features.add(1d);

        //homeStats
        features.add(homeSeason.getAvgGoalsFor(ALL_GAMES));
        features.add(homeSeason.getAvgGoalsAgainst(ALL_GAMES));
        features.add(homeSeason.getAvgXGF(ALL_GAMES));
        features.add(homeSeason.getAvgXGA(ALL_GAMES));
        features.add(homeSeason.getWeightedAvgXGF(ALL_GAMES));
        features.add(homeSeason.getWeightedAvgXGA(ALL_GAMES));
        features.add(homeSeason.getFormGoalsFor(ALL_GAMES));
        features.add(homeSeason.getFormGoalsAgainst(ALL_GAMES));
        features.add(homeSeason.getFormXGF(ALL_GAMES));
        features.add(homeSeason.getFormXGA(ALL_GAMES));
        features.add(homeSeason.getFormWeightedXGF(ALL_GAMES));
        features.add(homeSeason.getFormWeightedXGA(ALL_GAMES));
        features.add(homeSeason.getAvgFormGoalsFor(ALL_GAMES, 0));
        features.add(homeSeason.getAvgFormGoalsAgainst(ALL_GAMES, 0));
        features.add(homeSeason.getAvgFormXGF(ALL_GAMES, 0));
        features.add(homeSeason.getAvgFormXGA(ALL_GAMES, 0));
        features.add(homeSeason.getAvgFormWeightedXGF(ALL_GAMES, 0));
        features.add(homeSeason.getAvgFormWeightedXGA(ALL_GAMES, 0));
        features.add(homeSeason.getFormXGFOverLastNGames(ALL_GAMES, COMPARE_LAST_N_GAMES));
        features.add(homeSeason.getFormXGAOverLastNGames(ALL_GAMES, COMPARE_LAST_N_GAMES));
        features.add(homeSeason.getAvgPoints(ALL_GAMES));
        features.add(homeSeason.getAvgPointsOverLastXGames(ALL_GAMES, COMPARE_LAST_N_GAMES));
        features.add(homeSeason.getAvgPointsWhenScoredFirst(ALL_GAMES));
        features.add(homeSeason.getAvgPointsWhenConceededFirst(ALL_GAMES));
        features.add(homeTeam.getPointsOfLastMatchups(awayTeam.getTeamName(), ALL_GAMES, seasonYearStart - NUMB_SEASONS_HISTORY));
        features.add(homeSeason.getAvgPointsOfAllOpponentsGamesWholeSeason(ALL_GAMES));
        features.add(homeSeason.getAvgPointsOfLastXOpponentsGamesWholeSeason(ALL_GAMES, COMPARE_LAST_N_GAMES));
        features.add(homeSeason.getAvgPointsOfAllOpponentsLast5Games(ALL_GAMES));
        features.add(homeSeason.getAvgPointsOfLastXOpponentsLast5Games(ALL_GAMES, COMPARE_LAST_N_GAMES));
        features.add(homeSeason.getAvgNumberOfCleanSheets(ALL_GAMES));
        features.add(homeSeason.getAvgNumberOfCleanSheetsLastXGames(ALL_GAMES, COMPARE_LAST_N_GAMES, true));

        //awayStats
        features.add(awaySeason.getAvgGoalsFor(ALL_GAMES));
        features.add(awaySeason.getAvgGoalsAgainst(ALL_GAMES));
        features.add(awaySeason.getAvgXGF(ALL_GAMES));
        features.add(awaySeason.getAvgXGA(ALL_GAMES));
        features.add(awaySeason.getWeightedAvgXGF(ALL_GAMES));
        features.add(awaySeason.getWeightedAvgXGA(ALL_GAMES));
        features.add(awaySeason.getFormGoalsFor(ALL_GAMES));
        features.add(awaySeason.getFormGoalsAgainst(ALL_GAMES));
        features.add(awaySeason.getFormXGF(ALL_GAMES));
        features.add(awaySeason.getFormXGA(ALL_GAMES));
        features.add(awaySeason.getFormWeightedXGF(ALL_GAMES));
        features.add(awaySeason.getFormWeightedXGA(ALL_GAMES));
        features.add(awaySeason.getAvgFormGoalsFor(ALL_GAMES, 0));
        features.add(awaySeason.getAvgFormGoalsAgainst(ALL_GAMES, 0));
        features.add(awaySeason.getAvgFormXGF(ALL_GAMES, 0));
        features.add(awaySeason.getAvgFormXGA(ALL_GAMES, 0));
        features.add(awaySeason.getAvgFormWeightedXGF(ALL_GAMES, 0));
        features.add(awaySeason.getAvgFormWeightedXGA(ALL_GAMES, 0));
        features.add(awaySeason.getFormXGFOverLastNGames(ALL_GAMES, COMPARE_LAST_N_GAMES));
        features.add(awaySeason.getFormXGAOverLastNGames(ALL_GAMES, COMPARE_LAST_N_GAMES));
        features.add(awaySeason.getAvgPoints(ALL_GAMES));
        features.add(awaySeason.getAvgPointsOverLastXGames(ALL_GAMES, COMPARE_LAST_N_GAMES));
        features.add(awaySeason.getAvgPointsWhenScoredFirst(ALL_GAMES));
        features.add(awaySeason.getAvgPointsWhenConceededFirst(ALL_GAMES));
        features.add(awayTeam.getPointsOfLastMatchups(homeTeam.getTeamName(), ALL_GAMES, seasonYearStart - NUMB_SEASONS_HISTORY));
        features.add(awaySeason.getAvgPointsOfAllOpponentsGamesWholeSeason(ALL_GAMES));
        features.add(awaySeason.getAvgPointsOfLastXOpponentsGamesWholeSeason(ALL_GAMES, COMPARE_LAST_N_GAMES));
        features.add(awaySeason.getAvgPointsOfAllOpponentsLast5Games(ALL_GAMES));
        features.add(awaySeason.getAvgPointsOfLastXOpponentsLast5Games(ALL_GAMES, COMPARE_LAST_N_GAMES));
        features.add(awaySeason.getAvgNumberOfCleanSheets(ALL_GAMES));
        features.add(awaySeason.getAvgNumberOfCleanSheetsLastXGames(ALL_GAMES, COMPARE_LAST_N_GAMES, true));

        return features;
    }
    
    public static ArrayList<Double> getFeaturesAllData(TrainingTeam homeTeam, TrainingTeamsSeason homeSeason,
                                                       TrainingTeam awayTeam, TrainingTeamsSeason awaySeason,
                                                       int seasonYearStart, int result) {

        ArrayList<Double> features = new ArrayList<>();
        features.add((double) result);

        //adding bias parameter of 1
        features.add(1d);
        
        //home
        features.add(homeSeason.getAvgGoalsFor(ALL_GAMES));
        features.add(homeSeason.getAvgGoalsAgainst(ALL_GAMES));
        features.add(homeSeason.getAvgXGF(ALL_GAMES));
        features.add(homeSeason.getAvgXGA(ALL_GAMES));
        features.add(homeSeason.getWeightedAvgXGF(ALL_GAMES));
        features.add(homeSeason.getWeightedAvgXGA(ALL_GAMES));
        features.add(homeSeason.getAvgPoints(ALL_GAMES));
        features.add(homeSeason.getAvgPointsOverLastXGames(ALL_GAMES, COMPARE_LAST_N_GAMES));
        features.add(homeSeason.getAvgPointsWhenScoredFirst(ALL_GAMES));
        features.add(homeSeason.getAvgPointsWhenConceededFirst(ALL_GAMES));
        features.add(homeTeam.getPointsOfLastMatchups(awayTeam.getTeamName(), ALL_GAMES, seasonYearStart - NUMB_SEASONS_HISTORY));
//home@home
        features.add(homeSeason.getAvgGoalsFor(ONLY_HOME_GAMES));
        features.add(homeSeason.getAvgGoalsAgainst(ONLY_HOME_GAMES));
        features.add(homeSeason.getAvgXGF(ONLY_HOME_GAMES));
        features.add(homeSeason.getAvgXGA(ONLY_HOME_GAMES));
        features.add(homeSeason.getWeightedAvgXGF(ONLY_HOME_GAMES));
        features.add(homeSeason.getWeightedAvgXGA(ONLY_HOME_GAMES));
        features.add(homeSeason.getAvgPoints(ONLY_HOME_GAMES));
        features.add(homeSeason.getAvgPointsOverLastXGames(ONLY_HOME_GAMES, COMPARE_LAST_N_GAMES));
        features.add(homeSeason.getAvgPointsWhenScoredFirst(ONLY_HOME_GAMES));
        features.add(homeSeason.getAvgPointsWhenConceededFirst(ONLY_HOME_GAMES));
        features.add(homeTeam.getPointsOfLastMatchups(awayTeam.getTeamName(), ONLY_HOME_GAMES, seasonYearStart - NUMB_SEASONS_HISTORY));
//away
        features.add(awaySeason.getAvgGoalsFor(ALL_GAMES));
        features.add(awaySeason.getAvgGoalsAgainst(ALL_GAMES));
        features.add(awaySeason.getAvgXGF(ALL_GAMES));
        features.add(awaySeason.getAvgXGA(ALL_GAMES));
        features.add(awaySeason.getWeightedAvgXGF(ALL_GAMES));
        features.add(awaySeason.getWeightedAvgXGA(ALL_GAMES));
        features.add(awaySeason.getAvgPoints(ALL_GAMES));
        features.add(awaySeason.getAvgPointsOverLastXGames(ALL_GAMES, COMPARE_LAST_N_GAMES));
        features.add(awaySeason.getAvgPointsWhenScoredFirst(ALL_GAMES));
        features.add(awaySeason.getAvgPointsWhenConceededFirst(ALL_GAMES));
        features.add(awayTeam.getPointsOfLastMatchups(homeTeam.getTeamName(), ALL_GAMES, seasonYearStart - NUMB_SEASONS_HISTORY));
//away@away
        features.add(awaySeason.getAvgGoalsFor(ONLY_AWAY_GAMES));
        features.add(awaySeason.getAvgGoalsAgainst(ONLY_AWAY_GAMES));
        features.add(awaySeason.getAvgXGF(ONLY_AWAY_GAMES));
        features.add(awaySeason.getAvgXGA(ONLY_AWAY_GAMES));
        features.add(awaySeason.getWeightedAvgXGF(ONLY_AWAY_GAMES));
        features.add(awaySeason.getWeightedAvgXGA(ONLY_AWAY_GAMES));
        features.add(awaySeason.getAvgPoints(ONLY_AWAY_GAMES));
        features.add(awaySeason.getAvgPointsOverLastXGames(ONLY_AWAY_GAMES, COMPARE_LAST_N_GAMES));
        features.add(awaySeason.getAvgPointsWhenScoredFirst(ONLY_AWAY_GAMES));
        features.add(awaySeason.getAvgPointsWhenConceededFirst(ONLY_AWAY_GAMES));
        features.add(awayTeam.getPointsOfLastMatchups(homeTeam.getTeamName(), ONLY_AWAY_GAMES, seasonYearStart - NUMB_SEASONS_HISTORY));

        //extra home
        features.add(homeSeason.getAvgFormGoalsFor(ALL_GAMES, 0));
        features.add(homeSeason.getAvgFormGoalsAgainst(ALL_GAMES, 0));
        features.add(homeSeason.getAvgFormXGF(ALL_GAMES, 0));
        features.add(homeSeason.getAvgFormXGA(ALL_GAMES, 0));
        features.add(homeSeason.getAvgFormWeightedXGF(ALL_GAMES, 0));
        features.add(homeSeason.getAvgFormWeightedXGA(ALL_GAMES, 0));
        features.add(homeSeason.getFormXGFOverLastNGames(ALL_GAMES, COMPARE_LAST_N_GAMES));
        features.add(homeSeason.getFormXGAOverLastNGames(ALL_GAMES, COMPARE_LAST_N_GAMES));
        features.add(homeSeason.getAvgNumberOfCleanSheets(ALL_GAMES));

        //extra home@home
        features.add(homeSeason.getAvgFormGoalsFor(ONLY_HOME_GAMES, 0));
        features.add(homeSeason.getAvgFormGoalsAgainst(ONLY_HOME_GAMES, 0));
        features.add(homeSeason.getAvgFormXGF(ONLY_HOME_GAMES, 0));
        features.add(homeSeason.getAvgFormXGA(ONLY_HOME_GAMES, 0));
        features.add(homeSeason.getAvgFormWeightedXGF(ONLY_HOME_GAMES, 0));
        features.add(homeSeason.getAvgFormWeightedXGA(ONLY_HOME_GAMES, 0));
        features.add(homeSeason.getFormXGFOverLastNGames(ONLY_HOME_GAMES, COMPARE_LAST_N_GAMES));
        features.add(homeSeason.getFormXGAOverLastNGames(ONLY_HOME_GAMES, COMPARE_LAST_N_GAMES));

        //extra away
        features.add(awaySeason.getAvgFormGoalsFor(ALL_GAMES, 0));
        features.add(awaySeason.getAvgFormGoalsAgainst(ALL_GAMES, 0));
        features.add(awaySeason.getAvgFormXGF(ALL_GAMES, 0));
        features.add(awaySeason.getAvgFormXGA(ALL_GAMES, 0));
        features.add(awaySeason.getAvgFormWeightedXGF(ALL_GAMES, 0));
        features.add(awaySeason.getAvgFormWeightedXGA(ALL_GAMES, 0));
        features.add(awaySeason.getFormXGFOverLastNGames(ALL_GAMES, COMPARE_LAST_N_GAMES));
        features.add(awaySeason.getFormXGAOverLastNGames(ALL_GAMES, COMPARE_LAST_N_GAMES));
        features.add(awaySeason.getAvgNumberOfCleanSheets(ALL_GAMES));

        //extra away@away
        features.add(awaySeason.getAvgFormGoalsFor(ONLY_AWAY_GAMES, 0));
        features.add(awaySeason.getAvgFormGoalsAgainst(ONLY_AWAY_GAMES, 0));
        features.add(awaySeason.getAvgFormXGF(ONLY_AWAY_GAMES, 0));
        features.add(awaySeason.getAvgFormXGA(ONLY_AWAY_GAMES, 0));
        features.add(awaySeason.getAvgFormWeightedXGF(ONLY_AWAY_GAMES, 0));
        features.add(awaySeason.getAvgFormWeightedXGA(ONLY_AWAY_GAMES, 0));
        features.add(awaySeason.getFormXGFOverLastNGames(ONLY_AWAY_GAMES, COMPARE_LAST_N_GAMES));
        features.add(awaySeason.getFormXGAOverLastNGames(ONLY_AWAY_GAMES, COMPARE_LAST_N_GAMES));

        return features;
    }

    public static ArrayList<Double> getFeaturesNoLineupsExtended(TrainingTeam homeTeam, TrainingTeamsSeason homeSeason,
                                                         TrainingTeam awayTeam, TrainingTeamsSeason awaySeason,
                                                         int seasonYearStart, int result) {
        ArrayList<Double> features = new ArrayList<>();
        features.add((double) result);

        //adding bias parameter of 1
        features.add(1d);

        //homeStats
        features.add(homeSeason.getAvgGoalsFor(ALL_GAMES));
        features.add(homeSeason.getAvgGoalsAgainst(ALL_GAMES));
        features.add(homeSeason.getAvgXGF(ALL_GAMES));
        features.add(homeSeason.getAvgXGA(ALL_GAMES));
        features.add(homeSeason.getWeightedAvgXGF(ALL_GAMES));
        features.add(homeSeason.getWeightedAvgXGA(ALL_GAMES));
        features.add(homeSeason.getFormGoalsFor(ALL_GAMES));
        features.add(homeSeason.getFormGoalsAgainst(ALL_GAMES));
        features.add(homeSeason.getFormXGF(ALL_GAMES));
        features.add(homeSeason.getFormXGA(ALL_GAMES));
        features.add(homeSeason.getFormWeightedXGF(ALL_GAMES));
        features.add(homeSeason.getFormWeightedXGA(ALL_GAMES));
        features.add(homeSeason.getAvgFormGoalsFor(ALL_GAMES, 0));
        features.add(homeSeason.getAvgFormGoalsAgainst(ALL_GAMES, 0));
        features.add(homeSeason.getAvgFormXGF(ALL_GAMES, 0));
        features.add(homeSeason.getAvgFormXGA(ALL_GAMES, 0));
        features.add(homeSeason.getAvgFormWeightedXGF(ALL_GAMES, 0));
        features.add(homeSeason.getAvgFormWeightedXGA(ALL_GAMES, 0));
        features.add(homeSeason.getFormXGFOverLastNGames(ALL_GAMES, COMPARE_LAST_N_GAMES));
        features.add(homeSeason.getFormXGAOverLastNGames(ALL_GAMES, COMPARE_LAST_N_GAMES));
        features.add(homeSeason.getAvgPoints(ALL_GAMES));
        features.add(homeSeason.getAvgPointsOverLastXGames(ALL_GAMES, COMPARE_LAST_N_GAMES));
        features.add(homeSeason.getAvgPointsWhenScoredFirst(ALL_GAMES));
        features.add(homeSeason.getAvgPointsWhenConceededFirst(ALL_GAMES));
        features.add(homeTeam.getPointsOfLastMatchups(awayTeam.getTeamName(), ALL_GAMES, seasonYearStart - NUMB_SEASONS_HISTORY));
        features.add(homeSeason.getAvgPointsOfAllOpponentsGamesWholeSeason(ALL_GAMES));
        features.add(homeSeason.getAvgPointsOfLastXOpponentsGamesWholeSeason(ALL_GAMES, COMPARE_LAST_N_GAMES));
        features.add(homeSeason.getAvgPointsOfAllOpponentsLast5Games(ALL_GAMES));
        features.add(homeSeason.getAvgPointsOfLastXOpponentsLast5Games(ALL_GAMES, COMPARE_LAST_N_GAMES));
        features.add(homeSeason.getAvgNumberOfCleanSheets(ALL_GAMES));
        features.add(homeSeason.getAvgNumberOfCleanSheetsLastXGames(ALL_GAMES, COMPARE_LAST_N_GAMES, true));

        //home@home
        features.add(homeSeason.getAvgXGF(ONLY_HOME_GAMES));
        features.add(homeSeason.getAvgXGA(ONLY_HOME_GAMES));

        //awayStats
        features.add(awaySeason.getAvgGoalsFor(ALL_GAMES));
        features.add(awaySeason.getAvgGoalsAgainst(ALL_GAMES));
        features.add(awaySeason.getAvgXGF(ALL_GAMES));
        features.add(awaySeason.getAvgXGA(ALL_GAMES));
        features.add(awaySeason.getWeightedAvgXGF(ALL_GAMES));
        features.add(awaySeason.getWeightedAvgXGA(ALL_GAMES));
        features.add(awaySeason.getFormGoalsFor(ALL_GAMES));
        features.add(awaySeason.getFormGoalsAgainst(ALL_GAMES));
        features.add(awaySeason.getFormXGF(ALL_GAMES));
        features.add(awaySeason.getFormXGA(ALL_GAMES));
        features.add(awaySeason.getFormWeightedXGF(ALL_GAMES));
        features.add(awaySeason.getFormWeightedXGA(ALL_GAMES));
        features.add(awaySeason.getAvgFormGoalsFor(ALL_GAMES, 0));
        features.add(awaySeason.getAvgFormGoalsAgainst(ALL_GAMES, 0));
        features.add(awaySeason.getAvgFormXGF(ALL_GAMES, 0));
        features.add(awaySeason.getAvgFormXGA(ALL_GAMES, 0));
        features.add(awaySeason.getAvgFormWeightedXGF(ALL_GAMES, 0));
        features.add(awaySeason.getAvgFormWeightedXGA(ALL_GAMES, 0));
        features.add(awaySeason.getFormXGFOverLastNGames(ALL_GAMES, COMPARE_LAST_N_GAMES));
        features.add(awaySeason.getFormXGAOverLastNGames(ALL_GAMES, COMPARE_LAST_N_GAMES));
        features.add(awaySeason.getAvgPoints(ALL_GAMES));
        features.add(awaySeason.getAvgPointsOverLastXGames(ALL_GAMES, COMPARE_LAST_N_GAMES));
        features.add(awaySeason.getAvgPointsWhenScoredFirst(ALL_GAMES));
        features.add(awaySeason.getAvgPointsWhenConceededFirst(ALL_GAMES));
        features.add(awayTeam.getPointsOfLastMatchups(homeTeam.getTeamName(), ALL_GAMES, seasonYearStart - NUMB_SEASONS_HISTORY));
        features.add(awaySeason.getAvgPointsOfAllOpponentsGamesWholeSeason(ALL_GAMES));
        features.add(awaySeason.getAvgPointsOfLastXOpponentsGamesWholeSeason(ALL_GAMES, COMPARE_LAST_N_GAMES));
        features.add(awaySeason.getAvgPointsOfAllOpponentsLast5Games(ALL_GAMES));
        features.add(awaySeason.getAvgPointsOfLastXOpponentsLast5Games(ALL_GAMES, COMPARE_LAST_N_GAMES));
        features.add(awaySeason.getAvgNumberOfCleanSheets(ALL_GAMES));
        features.add(awaySeason.getAvgNumberOfCleanSheetsLastXGames(ALL_GAMES, COMPARE_LAST_N_GAMES, true));

        //away@away
        features.add(awaySeason.getAvgXGF(ONLY_AWAY_GAMES));
        features.add(awaySeason.getAvgXGA(ONLY_AWAY_GAMES));

        return features;
    }

    //did not work too well. -ve profit
    public static ArrayList<Double> getNewFeaturesNoLineups(TrainingTeam homeTeam, TrainingTeamsSeason homeSeason,
                                                            TrainingTeam awayTeam, TrainingTeamsSeason awaySeason,
                                                            int seasonYearStart, int result) {
        int LAST_5 = 5;
        ArrayList<Double> features = new ArrayList<>();
        features.add((double) result);

        //adding bias parameter of 1
        features.add(1d);

        //home stats
        features.add(homeSeason.getAvgGoalsForLastXGames(ALL_GAMES, LAST_5));
        features.add(homeSeason.getWeightedAvgGoalsFor(ALL_GAMES));
        features.add(homeSeason.getFormGoalsFor(ALL_GAMES));
        features.add(homeSeason.getFormWeightedGoalsFor(ALL_GAMES));
        features.add(homeSeason.getAvgGoalsAgainstLastXGames(ALL_GAMES, LAST_5));
        features.add(homeSeason.getWeightedAvgGoalsAgainst(ALL_GAMES));
        features.add(homeSeason.getFormGoalsAgainst(ALL_GAMES));
        features.add(homeSeason.getFormWeightedGoalsAgainst(ALL_GAMES));
        features.add(homeSeason.getAvgXGF(ALL_GAMES));
        features.add(homeSeason.getWeightedAvgXGF(ALL_GAMES));
        features.add(homeSeason.getFormXGF(ALL_GAMES));
        features.add(homeSeason.getFormWeightedXGF(ALL_GAMES));
        features.add(homeSeason.getAvgXGA(ALL_GAMES));
        features.add(homeSeason.getWeightedAvgXGA(ALL_GAMES));
        features.add(homeSeason.getFormXGA(ALL_GAMES));
        features.add(homeSeason.getFormWeightedXGA(ALL_GAMES));
        features.add(homeSeason.getAvgPoints(ALL_GAMES));
        features.add(homeSeason.getAvgPoints(ONLY_HOME_GAMES));
        features.add(homeTeam.getPointsOfLastMatchups(awayTeam.getTeamName(), ALL_GAMES, seasonYearStart - NUMB_SEASONS_HISTORY));
        features.add(homeSeason.getAvgPointsOfAllOpponentsGamesWholeSeason(ALL_GAMES));
        features.add(homeSeason.getAvgPointsOfLastXOpponentsGamesWholeSeason(ALL_GAMES, COMPARE_LAST_N_GAMES));
        features.add(homeSeason.getAvgPointsOfAllOpponentsLast5Games(ALL_GAMES));
        features.add(homeSeason.getAvgPointsOfLastXOpponentsLast5Games(ALL_GAMES, COMPARE_LAST_N_GAMES));
        features.add(homeSeason.getAvgNumberOfCleanSheets(ALL_GAMES));
        
        //away stats
        features.add(awaySeason.getAvgGoalsForLastXGames(ALL_GAMES, LAST_5));
        features.add(awaySeason.getWeightedAvgGoalsFor(ALL_GAMES));
        features.add(awaySeason.getFormGoalsFor(ALL_GAMES));
        features.add(awaySeason.getFormWeightedGoalsFor(ALL_GAMES));
        features.add(awaySeason.getAvgGoalsAgainstLastXGames(ALL_GAMES, LAST_5));
        features.add(awaySeason.getWeightedAvgGoalsAgainst(ALL_GAMES));
        features.add(awaySeason.getFormGoalsAgainst(ALL_GAMES));
        features.add(awaySeason.getFormWeightedGoalsAgainst(ALL_GAMES));
        features.add(awaySeason.getAvgXGF(ALL_GAMES));
        features.add(awaySeason.getWeightedAvgXGF(ALL_GAMES));
        features.add(awaySeason.getFormXGF(ALL_GAMES));
        features.add(awaySeason.getFormWeightedXGF(ALL_GAMES));
        features.add(awaySeason.getAvgXGA(ALL_GAMES));
        features.add(awaySeason.getWeightedAvgXGA(ALL_GAMES));
        features.add(awaySeason.getFormXGA(ALL_GAMES));
        features.add(awaySeason.getFormWeightedXGA(ALL_GAMES));
        features.add(awaySeason.getAvgPoints(ALL_GAMES));
        features.add(awaySeason.getAvgPoints(ONLY_AWAY_GAMES));
        features.add(awayTeam.getPointsOfLastMatchups(homeTeam.getTeamName(), ALL_GAMES, seasonYearStart - NUMB_SEASONS_HISTORY));
        features.add(awaySeason.getAvgPointsOfAllOpponentsGamesWholeSeason(ALL_GAMES));
        features.add(awaySeason.getAvgPointsOfLastXOpponentsGamesWholeSeason(ALL_GAMES, COMPARE_LAST_N_GAMES));
        features.add(awaySeason.getAvgPointsOfAllOpponentsLast5Games(ALL_GAMES));
        features.add(awaySeason.getAvgPointsOfLastXOpponentsLast5Games(ALL_GAMES, COMPARE_LAST_N_GAMES));
        features.add(awaySeason.getAvgNumberOfCleanSheets(ALL_GAMES));

        return features;
    }

    public static ArrayList<Double> getFeaturesOld(TrainingTeam homeTeam, TrainingTeamsSeason homeSeason,
                                                   TrainingTeam awayTeam, TrainingTeamsSeason awaySeason,
                                                   int seasonYearStart, int result) {
        ArrayList<Double> features = new ArrayList<>();
        features.add((double) result);

        //adding bias parameter of 1
        features.add(1d);

        //homeStats
        features.add(homeSeason.getAvgGoalsFor(ALL_GAMES));
        features.add(homeSeason.getAvgGoalsAgainst(ALL_GAMES));
        features.add(homeSeason.getAvgXGF(ALL_GAMES));
        features.add(homeSeason.getAvgXGA(ALL_GAMES));
        features.add(homeSeason.getWeightedAvgXGF(ALL_GAMES));
        features.add(homeSeason.getWeightedAvgXGA(ALL_GAMES));
        features.add(homeSeason.getFormGoalsFor(ALL_GAMES));
        features.add(homeSeason.getFormGoalsAgainst(ALL_GAMES));
        features.add(homeSeason.getFormXGF(ALL_GAMES));
        features.add(homeSeason.getFormXGA(ALL_GAMES));
        features.add(homeSeason.getFormWeightedXGF(ALL_GAMES));
        features.add(homeSeason.getFormWeightedXGA(ALL_GAMES));
        features.add(homeSeason.getAvgFormGoalsFor(ALL_GAMES, 0));
        features.add(homeSeason.getAvgFormGoalsAgainst(ALL_GAMES, 0));
        features.add(homeSeason.getAvgFormXGF(ALL_GAMES, 0));
        features.add(homeSeason.getAvgFormXGA(ALL_GAMES, 0));
        features.add(homeSeason.getAvgFormWeightedXGF(ALL_GAMES, 0));
        features.add(homeSeason.getAvgFormWeightedXGA(ALL_GAMES, 0));
        features.add(homeSeason.getFormXGFOverLastNGames(ALL_GAMES, COMPARE_LAST_N_GAMES));
        features.add(homeSeason.getFormXGAOverLastNGames(ALL_GAMES, COMPARE_LAST_N_GAMES));
        features.add(homeSeason.getAvgPoints(ALL_GAMES));
        features.add(homeSeason.getAvgPointsOverLastXGames(ALL_GAMES, COMPARE_LAST_N_GAMES));
        features.add(homeSeason.getAvgPointsWhenScoredFirst(ALL_GAMES));
        features.add(homeSeason.getAvgPointsWhenConceededFirst(ALL_GAMES));
        features.add(homeTeam.getPointsOfLastMatchups(awayTeam.getTeamName(), ALL_GAMES, seasonYearStart - NUMB_SEASONS_HISTORY));
//        features.add(homeSeason.getMinsWeightedLineupRating(ALL_GAMES, homePlayersNames));
//        features.add(homeSeason.getGamesWeightedLineupRating(ALL_GAMES, homePlayersNames));
//        features.add(homeSeason.getLineupStrength(ALL_GAMES, homePlayersNames));
        features.add(homeSeason.getAvgPointsOfAllOpponentsGamesWholeSeason(ALL_GAMES));
        features.add(homeSeason.getAvgPointsOfLastXOpponentsGamesWholeSeason(ALL_GAMES, COMPARE_LAST_N_GAMES));
        features.add(homeSeason.getAvgPointsOfAllOpponentsLast5Games(ALL_GAMES));
        features.add(homeSeason.getAvgPointsOfLastXOpponentsLast5Games(ALL_GAMES, COMPARE_LAST_N_GAMES));
        features.add(homeSeason.getAvgNumberOfCleanSheets(ALL_GAMES));
        features.add(homeSeason.getAvgNumberOfCleanSheetsLastXGames(ALL_GAMES, COMPARE_LAST_N_GAMES, true));

        //awayStats
        features.add(awaySeason.getAvgGoalsFor(ALL_GAMES));
        features.add(awaySeason.getAvgGoalsAgainst(ALL_GAMES));
        features.add(awaySeason.getAvgXGF(ALL_GAMES));
        features.add(awaySeason.getAvgXGA(ALL_GAMES));
        features.add(awaySeason.getWeightedAvgXGF(ALL_GAMES));
        features.add(awaySeason.getWeightedAvgXGA(ALL_GAMES));
        features.add(awaySeason.getFormGoalsFor(ALL_GAMES));
        features.add(awaySeason.getFormGoalsAgainst(ALL_GAMES));
        features.add(awaySeason.getFormXGF(ALL_GAMES));
        features.add(awaySeason.getFormXGA(ALL_GAMES));
        features.add(awaySeason.getFormWeightedXGF(ALL_GAMES));
        features.add(awaySeason.getFormWeightedXGA(ALL_GAMES));
        features.add(awaySeason.getAvgFormGoalsFor(ALL_GAMES, 0));
        features.add(awaySeason.getAvgFormGoalsAgainst(ALL_GAMES, 0));
        features.add(awaySeason.getAvgFormXGF(ALL_GAMES, 0));
        features.add(awaySeason.getAvgFormXGA(ALL_GAMES, 0));
        features.add(awaySeason.getAvgFormWeightedXGF(ALL_GAMES, 0));
        features.add(awaySeason.getAvgFormWeightedXGA(ALL_GAMES, 0));
        features.add(awaySeason.getFormXGFOverLastNGames(ALL_GAMES, COMPARE_LAST_N_GAMES));
        features.add(awaySeason.getFormXGAOverLastNGames(ALL_GAMES, COMPARE_LAST_N_GAMES));
        features.add(awaySeason.getAvgPoints(ALL_GAMES));
        features.add(awaySeason.getAvgPointsOverLastXGames(ALL_GAMES, COMPARE_LAST_N_GAMES));
        features.add(awaySeason.getAvgPointsWhenScoredFirst(ALL_GAMES));
        features.add(awaySeason.getAvgPointsWhenConceededFirst(ALL_GAMES));
        features.add(awayTeam.getPointsOfLastMatchups(homeTeam.getTeamName(), ALL_GAMES, seasonYearStart - NUMB_SEASONS_HISTORY));
//        features.add(awaySeason.getMinsWeightedLineupRating(ALL_GAMES, awayPlayersNames));
//        features.add(awaySeason.getGamesWeightedLineupRating(ALL_GAMES, awayPlayersNames));
//        features.add(awaySeason.getLineupStrength(ALL_GAMES, awayPlayersNames));
        features.add(awaySeason.getAvgPointsOfAllOpponentsGamesWholeSeason(ALL_GAMES));
        features.add(awaySeason.getAvgPointsOfLastXOpponentsGamesWholeSeason(ALL_GAMES, COMPARE_LAST_N_GAMES));
        features.add(awaySeason.getAvgPointsOfAllOpponentsLast5Games(ALL_GAMES));
        features.add(awaySeason.getAvgPointsOfLastXOpponentsLast5Games(ALL_GAMES, COMPARE_LAST_N_GAMES));
        features.add(awaySeason.getAvgNumberOfCleanSheets(ALL_GAMES));
        features.add(awaySeason.getAvgNumberOfCleanSheetsLastXGames(ALL_GAMES, COMPARE_LAST_N_GAMES, true));

//        if (homeOdds != -1d) {
//            //odds
//            features.add(homeOdds);
//            features.add(drawOdds);
//            features.add(awayOdds);
//            //result
//            features.add((double) result);
//        }

        return features;
    }
    
    public static ArrayList<Double> getFeaturesOldExtended(TrainingTeam homeTeam, TrainingTeamsSeason homeSeason,
                                                           TrainingTeam awayTeam, TrainingTeamsSeason awaySeason,
                                                           int seasonYearStart, int result) {
        ArrayList<Double> features = new ArrayList<>();
        features.add((double) result);

        //adding bias parameter of 1
        features.add(1d);

        //homeStats
        features.add(homeSeason.getAvgGoalsFor(ALL_GAMES));
        features.add(homeSeason.getAvgGoalsAgainst(ALL_GAMES));
        features.add(homeSeason.getAvgXGF(ALL_GAMES));
        features.add(homeSeason.getAvgXGA(ALL_GAMES));
        features.add(homeSeason.getWeightedAvgXGF(ALL_GAMES));
        features.add(homeSeason.getWeightedAvgXGA(ALL_GAMES));
        features.add(homeSeason.getFormGoalsFor(ALL_GAMES));
        features.add(homeSeason.getFormGoalsAgainst(ALL_GAMES));
        features.add(homeSeason.getFormXGF(ALL_GAMES));
        features.add(homeSeason.getFormXGA(ALL_GAMES));
        features.add(homeSeason.getFormWeightedXGF(ALL_GAMES));
        features.add(homeSeason.getFormWeightedXGA(ALL_GAMES));
        features.add(homeSeason.getAvgFormGoalsFor(ALL_GAMES, 0));
        features.add(homeSeason.getAvgFormGoalsAgainst(ALL_GAMES, 0));
        features.add(homeSeason.getAvgFormXGF(ALL_GAMES, 0));
        features.add(homeSeason.getAvgFormXGA(ALL_GAMES, 0));
        features.add(homeSeason.getAvgFormWeightedXGF(ALL_GAMES, 0));
        features.add(homeSeason.getAvgFormWeightedXGA(ALL_GAMES, 0));
        features.add(homeSeason.getFormXGFOverLastNGames(ALL_GAMES, COMPARE_LAST_N_GAMES));
        features.add(homeSeason.getFormXGAOverLastNGames(ALL_GAMES, COMPARE_LAST_N_GAMES));
        features.add(homeSeason.getAvgPoints(ALL_GAMES));
        features.add(homeSeason.getAvgPointsOverLastXGames(ALL_GAMES, COMPARE_LAST_N_GAMES));
        features.add(homeSeason.getAvgPointsWhenScoredFirst(ALL_GAMES));
        features.add(homeSeason.getAvgPointsWhenConceededFirst(ALL_GAMES));
        features.add(homeTeam.getPointsOfLastMatchups(awayTeam.getTeamName(), ALL_GAMES, seasonYearStart - NUMB_SEASONS_HISTORY));
        features.add(homeSeason.getAvgPointsOfAllOpponentsGamesWholeSeason(ALL_GAMES));
        features.add(homeSeason.getAvgPointsOfLastXOpponentsGamesWholeSeason(ALL_GAMES, COMPARE_LAST_N_GAMES));
        features.add(homeSeason.getAvgPointsOfAllOpponentsLast5Games(ALL_GAMES));
        features.add(homeSeason.getAvgPointsOfLastXOpponentsLast5Games(ALL_GAMES, COMPARE_LAST_N_GAMES));
        features.add(homeSeason.getAvgNumberOfCleanSheets(ALL_GAMES));
        features.add(homeSeason.getAvgNumberOfCleanSheetsLastXGames(ALL_GAMES, COMPARE_LAST_N_GAMES, true));
        
        //home@home stats
        features.add(homeSeason.getAvgGoalsFor(ONLY_HOME_GAMES));
        features.add(homeSeason.getAvgGoalsAgainst(ONLY_HOME_GAMES));
        features.add(homeSeason.getAvgXGF(ONLY_HOME_GAMES));
        features.add(homeSeason.getAvgXGA(ONLY_HOME_GAMES));
        features.add(homeSeason.getWeightedAvgXGF(ONLY_HOME_GAMES));
        features.add(homeSeason.getWeightedAvgXGA(ONLY_HOME_GAMES));
        features.add(homeSeason.getFormGoalsFor(ONLY_HOME_GAMES));
        features.add(homeSeason.getFormGoalsAgainst(ONLY_HOME_GAMES));
        features.add(homeSeason.getFormXGF(ONLY_HOME_GAMES));
        features.add(homeSeason.getFormXGA(ONLY_HOME_GAMES));
        features.add(homeSeason.getFormWeightedXGF(ONLY_HOME_GAMES));
        features.add(homeSeason.getFormWeightedXGA(ONLY_HOME_GAMES));
        features.add(homeSeason.getAvgFormGoalsFor(ONLY_HOME_GAMES, 0));
        features.add(homeSeason.getAvgFormGoalsAgainst(ONLY_HOME_GAMES, 0));
        features.add(homeSeason.getAvgFormXGF(ONLY_HOME_GAMES, 0));
        features.add(homeSeason.getAvgFormXGA(ONLY_HOME_GAMES, 0));
        features.add(homeSeason.getAvgFormWeightedXGF(ONLY_HOME_GAMES, 0));
        features.add(homeSeason.getAvgFormWeightedXGA(ONLY_HOME_GAMES, 0));
        features.add(homeSeason.getFormXGFOverLastNGames(ONLY_HOME_GAMES, COMPARE_LAST_N_GAMES));
        features.add(homeSeason.getFormXGAOverLastNGames(ONLY_HOME_GAMES, COMPARE_LAST_N_GAMES));
        features.add(homeSeason.getAvgPoints(ONLY_HOME_GAMES));
        features.add(homeSeason.getAvgPointsOverLastXGames(ONLY_HOME_GAMES, COMPARE_LAST_N_GAMES));
        features.add(homeSeason.getAvgPointsOfAllOpponentsGamesWholeSeason(ONLY_HOME_GAMES));
        features.add(homeSeason.getAvgPointsOfLastXOpponentsGamesWholeSeason(ONLY_HOME_GAMES, COMPARE_LAST_N_GAMES));
        features.add(homeSeason.getAvgPointsOfAllOpponentsLast5Games(ONLY_HOME_GAMES));
        features.add(homeSeason.getAvgPointsOfLastXOpponentsLast5Games(ONLY_HOME_GAMES, COMPARE_LAST_N_GAMES));

        //awayStats
        features.add(awaySeason.getAvgGoalsFor(ALL_GAMES));
        features.add(awaySeason.getAvgGoalsAgainst(ALL_GAMES));
        features.add(awaySeason.getAvgXGF(ALL_GAMES));
        features.add(awaySeason.getAvgXGA(ALL_GAMES));
        features.add(awaySeason.getWeightedAvgXGF(ALL_GAMES));
        features.add(awaySeason.getWeightedAvgXGA(ALL_GAMES));
        features.add(awaySeason.getFormGoalsFor(ALL_GAMES));
        features.add(awaySeason.getFormGoalsAgainst(ALL_GAMES));
        features.add(awaySeason.getFormXGF(ALL_GAMES));
        features.add(awaySeason.getFormXGA(ALL_GAMES));
        features.add(awaySeason.getFormWeightedXGF(ALL_GAMES));
        features.add(awaySeason.getFormWeightedXGA(ALL_GAMES));
        features.add(awaySeason.getAvgFormGoalsFor(ALL_GAMES, 0));
        features.add(awaySeason.getAvgFormGoalsAgainst(ALL_GAMES, 0));
        features.add(awaySeason.getAvgFormXGF(ALL_GAMES, 0));
        features.add(awaySeason.getAvgFormXGA(ALL_GAMES, 0));
        features.add(awaySeason.getAvgFormWeightedXGF(ALL_GAMES, 0));
        features.add(awaySeason.getAvgFormWeightedXGA(ALL_GAMES, 0));
        features.add(awaySeason.getFormXGFOverLastNGames(ALL_GAMES, COMPARE_LAST_N_GAMES));
        features.add(awaySeason.getFormXGAOverLastNGames(ALL_GAMES, COMPARE_LAST_N_GAMES));
        features.add(awaySeason.getAvgPoints(ALL_GAMES));
        features.add(awaySeason.getAvgPointsOverLastXGames(ALL_GAMES, COMPARE_LAST_N_GAMES));
        features.add(awaySeason.getAvgPointsWhenScoredFirst(ALL_GAMES));
        features.add(awaySeason.getAvgPointsWhenConceededFirst(ALL_GAMES));
        features.add(awayTeam.getPointsOfLastMatchups(homeTeam.getTeamName(), ALL_GAMES, seasonYearStart - NUMB_SEASONS_HISTORY));
        features.add(awaySeason.getAvgPointsOfAllOpponentsGamesWholeSeason(ALL_GAMES));
        features.add(awaySeason.getAvgPointsOfLastXOpponentsGamesWholeSeason(ALL_GAMES, COMPARE_LAST_N_GAMES));
        features.add(awaySeason.getAvgPointsOfAllOpponentsLast5Games(ALL_GAMES));
        features.add(awaySeason.getAvgPointsOfLastXOpponentsLast5Games(ALL_GAMES, COMPARE_LAST_N_GAMES));
        features.add(awaySeason.getAvgNumberOfCleanSheets(ALL_GAMES));
        features.add(awaySeason.getAvgNumberOfCleanSheetsLastXGames(ALL_GAMES, COMPARE_LAST_N_GAMES, true));

        //away@away stats
        features.add(awaySeason.getAvgGoalsFor(ONLY_AWAY_GAMES));
        features.add(awaySeason.getAvgGoalsAgainst(ONLY_AWAY_GAMES));
        features.add(awaySeason.getAvgXGF(ONLY_AWAY_GAMES));
        features.add(awaySeason.getAvgXGA(ONLY_AWAY_GAMES));
        features.add(awaySeason.getWeightedAvgXGF(ONLY_AWAY_GAMES));
        features.add(awaySeason.getWeightedAvgXGA(ONLY_AWAY_GAMES));
        features.add(awaySeason.getFormGoalsFor(ONLY_AWAY_GAMES));
        features.add(awaySeason.getFormGoalsAgainst(ONLY_AWAY_GAMES));
        features.add(awaySeason.getFormXGF(ONLY_AWAY_GAMES));
        features.add(awaySeason.getFormXGA(ONLY_AWAY_GAMES));
        features.add(awaySeason.getFormWeightedXGF(ONLY_AWAY_GAMES));
        features.add(awaySeason.getFormWeightedXGA(ONLY_AWAY_GAMES));
        features.add(awaySeason.getAvgFormGoalsFor(ONLY_AWAY_GAMES, 0));
        features.add(awaySeason.getAvgFormGoalsAgainst(ONLY_AWAY_GAMES, 0));
        features.add(awaySeason.getAvgFormXGF(ONLY_AWAY_GAMES, 0));
        features.add(awaySeason.getAvgFormXGA(ONLY_AWAY_GAMES, 0));
        features.add(awaySeason.getAvgFormWeightedXGF(ONLY_AWAY_GAMES, 0));
        features.add(awaySeason.getAvgFormWeightedXGA(ONLY_AWAY_GAMES, 0));
        features.add(awaySeason.getFormXGFOverLastNGames(ONLY_AWAY_GAMES, COMPARE_LAST_N_GAMES));
        features.add(awaySeason.getFormXGAOverLastNGames(ONLY_AWAY_GAMES, COMPARE_LAST_N_GAMES));
        features.add(awaySeason.getAvgPoints(ONLY_AWAY_GAMES));
        features.add(awaySeason.getAvgPointsOverLastXGames(ONLY_AWAY_GAMES, COMPARE_LAST_N_GAMES));
        features.add(awaySeason.getAvgPointsOfAllOpponentsGamesWholeSeason(ONLY_AWAY_GAMES));
        features.add(awaySeason.getAvgPointsOfLastXOpponentsGamesWholeSeason(ONLY_AWAY_GAMES, COMPARE_LAST_N_GAMES));
        features.add(awaySeason.getAvgPointsOfAllOpponentsLast5Games(ONLY_AWAY_GAMES));
        features.add(awaySeason.getAvgPointsOfLastXOpponentsLast5Games(ONLY_AWAY_GAMES, COMPARE_LAST_N_GAMES));

//        if (homeOdds != -1d) {
//            //odds
//            features.add(homeOdds);
//            features.add(drawOdds);
//            features.add(awayOdds);
//            //result
//            features.add((double) result);
//        }

        return features;
    }

    public static ArrayList<Double> getFeaturesOldOld(TrainingTeam homeTeam, TrainingTeamsSeason homeSeason,
                                                   TrainingTeam awayTeam, TrainingTeamsSeason awaySeason,
                                                   int seasonYearStart, int result) {
        int pow = 1;
        ArrayList<Double> features = new ArrayList<>();
        features.add((double) result);
        //adding bias parameter of 1
        features.add(1d);

        //home
        homeSeason.getAvgGoalsFor(ALL_GAMES);
        homeSeason.getAvgGoalsAgainst(ALL_GAMES);
        homeSeason.getAvgXGF(ALL_GAMES);
        homeSeason.getAvgXGA(ALL_GAMES);
        homeSeason.getWeightedAvgXGF(ALL_GAMES);
        homeSeason.getWeightedAvgXGA(ALL_GAMES);
        homeSeason.getAvgPoints(ALL_GAMES);
        homeSeason.getAvgPointsOverLastXGames(ALL_GAMES, 5);
        homeSeason.getAvgPointsWhenScoredFirst(ALL_GAMES);
        homeSeason.getAvgPointsWhenConceededFirst(ALL_GAMES);
        homeTeam.getPointsOfLastMatchups(awayTeam.getTeamName(), ALL_GAMES, homeSeason.getSeasonYearStart()-2);

        //home@home
        homeSeason.getAvgGoalsFor(ONLY_HOME_GAMES);
        homeSeason.getAvgGoalsAgainst(ONLY_HOME_GAMES);
        homeSeason.getAvgXGF(ONLY_HOME_GAMES);
        homeSeason.getAvgXGA(ONLY_HOME_GAMES);
        homeSeason.getWeightedAvgXGF(ONLY_HOME_GAMES);
        homeSeason.getWeightedAvgXGA(ONLY_HOME_GAMES);
        homeSeason.getAvgPoints(ONLY_HOME_GAMES);
        homeSeason.getAvgPointsOverLastXGames(ONLY_HOME_GAMES, 5);
        homeSeason.getAvgPointsWhenScoredFirst(ONLY_HOME_GAMES);
        homeSeason.getAvgPointsWhenConceededFirst(ONLY_HOME_GAMES);
        homeTeam.getPointsOfLastMatchups(awayTeam.getTeamName(), ONLY_HOME_GAMES, homeSeason.getSeasonYearStart()-2);

        //away
        awaySeason.getAvgGoalsFor(ALL_GAMES);
        awaySeason.getAvgGoalsAgainst(ALL_GAMES);
        awaySeason.getAvgXGF(ALL_GAMES);
        awaySeason.getAvgXGA(ALL_GAMES);
        awaySeason.getWeightedAvgXGF(ALL_GAMES);
        awaySeason.getWeightedAvgXGA(ALL_GAMES);
        awaySeason.getAvgPoints(ALL_GAMES);
        awaySeason.getAvgPointsOverLastXGames(ALL_GAMES, 5);
        awaySeason.getAvgPointsWhenScoredFirst(ALL_GAMES);
        awaySeason.getAvgPointsWhenConceededFirst(ALL_GAMES);
        awayTeam.getPointsOfLastMatchups(homeTeam.getTeamName(), ALL_GAMES, awaySeason.getSeasonYearStart()-2);

        //away@away
        awaySeason.getAvgGoalsFor(ONLY_AWAY_GAMES);
        awaySeason.getAvgGoalsAgainst(ONLY_AWAY_GAMES);
        awaySeason.getAvgXGF(ONLY_AWAY_GAMES);
        awaySeason.getAvgXGA(ONLY_AWAY_GAMES);
        awaySeason.getWeightedAvgXGF(ONLY_AWAY_GAMES);
        awaySeason.getWeightedAvgXGA(ONLY_AWAY_GAMES);
        awaySeason.getAvgPoints(ONLY_AWAY_GAMES);
        awaySeason.getAvgPointsOverLastXGames(ONLY_AWAY_GAMES, 5);
        awaySeason.getAvgPointsWhenScoredFirst(ONLY_AWAY_GAMES);
        awaySeason.getAvgPointsWhenConceededFirst(ONLY_AWAY_GAMES);
        awayTeam.getPointsOfLastMatchups(homeTeam.getTeamName(), ONLY_AWAY_GAMES, awaySeason.getSeasonYearStart()-2);


        //home stats
//        features.add(Math.pow(match.getHomeTeamAvgGoalsFor(), pow)); homeSeason.getAvgGoalsFor(ALL_GAMES);
//        features.add(Math.pow(match.getHomeTeamAvgGoalsAgainst(), pow)); homeSeason.getAvgGoalsAgainst(ALL_GAMES);
//        features.add(Math.pow(match.getHomeTeamAvgXGF(), pow)); homeSeason.getAvgXGF(ALL_GAMES);
//        features.add(Math.pow(match.getHomeTeamAvgXGA(), pow)); homeSeason.getAvgXGA(ALL_GAMES);
//        features.add(Math.pow(match.getHomeTeamWeightedAvgXGF(), pow));
//        features.add(Math.pow(match.getHomeTeamWeightedAvgXGA(), pow));
//        features.add(Math.pow(match.getAvgHomeTeamPoints(), pow));
//        features.add(Math.pow(match.getLast5HomeTeamPoints(), pow));
//        features.add(Math.pow(match.getIfScoredFirstHomeTeamPoints(), pow));
//        features.add(Math.pow(match.getIfConceededFirstHomeTeamPoints(), pow));
//        features.add(Math.pow(match.getHomeTeamPointsAgainstOpposition(), pow));
//        features.add(Math.pow(match.getHomeTeamMinsWeightedLineupRating(), pow));
//        features.add(Math.pow(match.getHomeTeamStrength(), pow)); //14
//
//        //home team home stats
//        features.add(Math.pow(match.getHomeTeamAvgHomeGoalsFor(), pow)); //15
//        features.add(Math.pow(match.getHomeTeamAvgHomeGoalsAgainst(), pow));
//        features.add(Math.pow(match.getHomeTeamAvgHomeXGF(), pow));
//        features.add(Math.pow(match.getHomeTeamAvgHomeXGA(), pow));
//        features.add(Math.pow(match.getHomeTeamWeightedAvgHomeXGF(), pow));
//        features.add(Math.pow(match.getHomeTeamWeightedAvgHomeXGA(), pow));
//        features.add(Math.pow(match.getAvgHomeTeamHomePoints(), pow));
//        features.add(Math.pow(match.getLast5HomeTeamHomePoints(), pow));
//        features.add(Math.pow(match.getIfScoredFirstAtHomeHomeTeamPoints(), pow));
//        features.add(Math.pow(match.getIfConceededFirstAtHomeHomeTeamPoints(), pow));
//        features.add(Math.pow(match.getHomeTeamPointsAtHomeAgainstOpposition(), pow));
//        features.add(Math.pow(match.getHomeTeamAtHomeMinsWeightedLineupRating(), pow));
//        features.add(Math.pow(match.getHomeTeamHomeStrength(), pow)); //27
//
//        //awawy team stats
//        features.add(Math.pow(match.getAwayTeamAvgGoalsFor(), pow)); //28
//        features.add(Math.pow(match.getAwayTeamAvgGoalsAgainst(), pow));
//        features.add(Math.pow(match.getAwayTeamAvgXGF(), pow));
//        features.add(Math.pow(match.getAwayTeamAvgXGA(), pow));
//        features.add(Math.pow(match.getAwayTeamWeightedAvgXGF(), pow));
//        features.add(Math.pow(match.getAwayTeamWeightedAvgXGA(), pow));
//        features.add(Math.pow(match.getAvgAwayTeamPoints(), pow));
//        features.add(Math.pow(match.getLast5AwayTeamPoints(), pow));
//        features.add(Math.pow(match.getIfScoredFirstAwayTeamPoints(), pow));
//        features.add(Math.pow(match.getIfConceededFirstAwayTeamPoints(), pow));
//        features.add(Math.pow(match.getAwayTeamPointsAgainstOpposition(), pow));
//        features.add(Math.pow(match.getAwayTeamMinsWeightedLineupRating(), pow));
//        features.add(Math.pow(match.getAwayTeamStrength(), pow)); //40
//
//        //away team away stats
//        features.add(Math.pow(match.getAwayTeamAvgAwayGoalsFor(), pow)); //41
//        features.add(Math.pow(match.getAwayTeamAvgAwayGoalsAgainst(), pow));
//        features.add(Math.pow(match.getAwayTeamAvgAwayXGF(), pow));
//        features.add(Math.pow(match.getAwayTeamAvgAwayXGA(), pow));
//        features.add(Math.pow(match.getAwayTeamWeightedAvgAwayXGF(), pow));
//        features.add(Math.pow(match.getAwayTeamWeightedAvgAwayXGA(), pow));
//        features.add(Math.pow(match.getAvgAwayTeamAwayPoints(), pow));
//        features.add(Math.pow(match.getLast5AwayTeamAwayPoints(), pow));
//        features.add(Math.pow(match.getIfScoredFirstAtAwayAwayTeamPoints(), pow));
//        features.add(Math.pow(match.getIfConceededFirstAtAwayAwayTeamPoints(), pow));
//        features.add(Math.pow(match.getAwayTeamPointsAtAwayAgainstOpposition(), pow));
//        features.add(Math.pow(match.getAwayTeamAtAwayMinsWeightedLineupRating(), pow));
//        features.add(Math.pow(match.getAwayTeamAwayStrength(), pow)); //53
//
//        //extra home team stats
//        features.add(Math.pow(match.getHomeTeamAvgFormGoalsFor(), pow));
//        features.add(Math.pow(match.getHomeTeamAvgFormGoalsAgainst(), pow));
//        features.add(Math.pow(match.getHomeTeamAvgFormXGF(), pow));
//        features.add(Math.pow(match.getHomeTeamAvgFormXGA(), pow));
//        features.add(Math.pow(match.getHomeTeamAvgFormWeightedXGF(), pow));
//        features.add(Math.pow(match.getHomeTeamAvgFormWeightedXGA(), pow));
//        features.add(Math.pow(match.getHomeTeamAvgFormXGFLast5Games(), pow));
//        features.add(Math.pow(match.getHomeTeamAvgFormXGALast5Games(), pow));
//
//        //extra home home stats
//        features.add(Math.pow(match.getHomeTeamHomeAvgFormGoalsFor(), pow));
//        features.add(Math.pow(match.getHomeTeamHomeAvgFormGoalsAgainst(), pow));
//        features.add(Math.pow(match.getHomeTeamHomeAvgFormXGF(), pow));
//        features.add(Math.pow(match.getHomeTeamHomeAvgFormXGA(), pow));
//        features.add(Math.pow(match.getHomeTeamHomeAvgFormWeightedXGF(), pow));
//        features.add(Math.pow(match.getHomeTeamHomeAvgFormWeightedXGA(), pow));
//        features.add(Math.pow(match.getHomeTeamHomeAvgFormXGFLast5Games(), pow));
//        features.add(Math.pow(match.getHomeTeamHomeAvgFormXGALast5Games(), pow));
//
//        //extra away stats
//        features.add(Math.pow(match.getAwayTeamAvgFormGoalsFor(), pow));
//        features.add(Math.pow(match.getAwayTeamAvgFormGoalsAgainst(), pow));
//        features.add(Math.pow(match.getAwayTeamAvgFormXGF(), pow));
//        features.add(Math.pow(match.getAwayTeamAvgFormXGA(), pow));
//        features.add(Math.pow(match.getAwayTeamAvgFormWeightedXGF(), pow));
//        features.add(Math.pow(match.getAwayTeamAvgFormWeightedXGA(), pow));
//        features.add(Math.pow(match.getAwayTeamAvgFormXGFLast5Games(), pow));
//        features.add(Math.pow(match.getAwayTeamAvgFormXGALast5Games(), pow));
//
//        //extra away away stats
//        features.add(Math.pow(match.getAwayTeamAwayAvgFormGoalsFor(), pow));
//        features.add(Math.pow(match.getAwayTeamAwayAvgFormGoalsAgainst(), pow));
//        features.add(Math.pow(match.getAwayTeamAwayAvgFormXGF(), pow));
//        features.add(Math.pow(match.getAwayTeamAwayAvgFormXGA(), pow));
//        features.add(Math.pow(match.getAwayTeamAwayAvgFormWeightedXGF(), pow));
//        features.add(Math.pow(match.getAwayTeamAwayAvgFormWeightedXGA(), pow));
//        features.add(Math.pow(match.getAwayTeamAwayAvgFormXGFLast5Games(), pow));
//        features.add(Math.pow(match.getAwayTeamAwayAvgFormXGALast5Games(), pow));
//
//        if (addResults) {
//            //odds
//            features.add(match.getHomeTeamProbability());
//            features.add(match.getDrawProbability());
//            features.add(match.getAwayTeamProbability());
//            //result
//            features.add((double) match.getResult());
//        }

        return features;
    }
}
