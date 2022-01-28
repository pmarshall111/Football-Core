package com.petermarshall.machineLearning.createData;

import com.petermarshall.machineLearning.createData.classes.LeagueStatsForSeason;
import com.petermarshall.machineLearning.createData.classes.TrainingMatch;
import com.petermarshall.machineLearning.createData.classes.TrainingTeam;
import com.petermarshall.machineLearning.createData.classes.TrainingTeamsSeason;
import org.apache.logging.log4j.core.appender.AbstractWriterAppender;

import java.lang.reflect.Array;
import java.util.ArrayList;

import static com.petermarshall.machineLearning.createData.CalcPastStats.COMPARE_LAST_N_GAMES;
import static com.petermarshall.machineLearning.createData.CalcPastStats.NUMB_SEASONS_HISTORY;
import static com.petermarshall.machineLearning.createData.classes.GamesSelector.*;

public class CreateFeatures {
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

    public static ArrayList<Double> getNewFeatures(TrainingTeam homeTeam, TrainingTeamsSeason homeSeason,
                                                            TrainingTeam awayTeam, TrainingTeamsSeason awaySeason,
                                                   ArrayList<String> homePlayersNames, ArrayList<String> awayPlayersNames,
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
        features.add(homeSeason.getGamesWeightedLineupRating(ALL_GAMES, homePlayersNames));
        features.add(homeSeason.getLineupStrength(ALL_GAMES, homePlayersNames));

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
        features.add(awaySeason.getGamesWeightedLineupRating(ALL_GAMES, awayPlayersNames));
        features.add(awaySeason.getLineupStrength(ALL_GAMES, awayPlayersNames));

        return features;
    }

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

    public static ArrayList<Double> getFeaturesOldOld(TrainingTeam homeTeam, TrainingTeamsSeason homeSeason,
                                                   TrainingTeam awayTeam, TrainingTeamsSeason awaySeason, int result) {
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
        return features;
    }

    public static ArrayList<Double> getFeaturesExtendedOldNoLineups(TrainingTeam homeTeam, TrainingTeamsSeason homeSeason,
                                                           TrainingTeam awayTeam, TrainingTeamsSeason awaySeason,
                                                           int seasonYearStart, int result) {

        int LAST_5 = 3;
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
        features.add(homeSeason.getAvgPointsOverLastXGames(ALL_GAMES, LAST_5));
        features.add(homeSeason.getAvgPointsWhenScoredFirst(ALL_GAMES));
        features.add(homeSeason.getAvgPointsWhenConceededFirst(ALL_GAMES));
        features.add(homeTeam.getPointsOfLastMatchups(awayTeam.getTeamName(), ALL_GAMES, homeSeason.getSeasonYearStart()-2));//12

        //home@home
        features.add(homeSeason.getAvgGoalsFor(ONLY_HOME_GAMES));
        features.add(homeSeason.getAvgGoalsAgainst(ONLY_HOME_GAMES));
        features.add(homeSeason.getAvgXGF(ONLY_HOME_GAMES));
        features.add(homeSeason.getAvgXGA(ONLY_HOME_GAMES));
        features.add(homeSeason.getWeightedAvgXGF(ONLY_HOME_GAMES));
        features.add(homeSeason.getWeightedAvgXGA(ONLY_HOME_GAMES));
        features.add(homeSeason.getAvgPoints(ONLY_HOME_GAMES));
        features.add(homeSeason.getAvgPointsOverLastXGames(ONLY_HOME_GAMES, LAST_5));
        features.add(homeSeason.getAvgPointsWhenScoredFirst(ONLY_HOME_GAMES));
        features.add(homeSeason.getAvgPointsWhenConceededFirst(ONLY_HOME_GAMES));
        features.add(homeTeam.getPointsOfLastMatchups(awayTeam.getTeamName(), ONLY_HOME_GAMES, homeSeason.getSeasonYearStart()-2)); //23

        //away
        features.add(awaySeason.getAvgGoalsFor(ALL_GAMES));
        features.add(awaySeason.getAvgGoalsAgainst(ALL_GAMES));
        features.add(awaySeason.getAvgXGF(ALL_GAMES));
        features.add(awaySeason.getAvgXGA(ALL_GAMES));
        features.add(awaySeason.getWeightedAvgXGF(ALL_GAMES));
        features.add(awaySeason.getWeightedAvgXGA(ALL_GAMES));
        features.add(awaySeason.getAvgPoints(ALL_GAMES));
        features.add(awaySeason.getAvgPointsOverLastXGames(ALL_GAMES, 5));
        features.add(awaySeason.getAvgPointsWhenScoredFirst(ALL_GAMES));
        features.add(awaySeason.getAvgPointsWhenConceededFirst(ALL_GAMES));
        features.add(awayTeam.getPointsOfLastMatchups(homeTeam.getTeamName(), ALL_GAMES, awaySeason.getSeasonYearStart()-2)); //34

        //away@away
        features.add(awaySeason.getAvgGoalsFor(ONLY_AWAY_GAMES));
        features.add(awaySeason.getAvgGoalsAgainst(ONLY_AWAY_GAMES));
        features.add(awaySeason.getAvgXGF(ONLY_AWAY_GAMES));
        features.add(awaySeason.getAvgXGA(ONLY_AWAY_GAMES));
        features.add(awaySeason.getWeightedAvgXGF(ONLY_AWAY_GAMES));
        features.add(awaySeason.getWeightedAvgXGA(ONLY_AWAY_GAMES));
        features.add(awaySeason.getAvgPoints(ONLY_AWAY_GAMES));
        features.add(awaySeason.getAvgPointsOverLastXGames(ONLY_AWAY_GAMES, 5));
        features.add(awaySeason.getAvgPointsWhenScoredFirst(ONLY_AWAY_GAMES));
        features.add(awaySeason.getAvgPointsWhenConceededFirst(ONLY_AWAY_GAMES));
        features.add(awayTeam.getPointsOfLastMatchups(homeTeam.getTeamName(), ONLY_AWAY_GAMES, awaySeason.getSeasonYearStart()-2)); //45

        //extra home team stats
        features.add(homeSeason.getAvgFormGoalsFor(ALL_GAMES, 5));
        features.add(homeSeason.getAvgFormGoalsAgainst(ALL_GAMES, 5));
        features.add(homeSeason.getFormXGF(ALL_GAMES));
        features.add(homeSeason.getFormXGA(ALL_GAMES));
        features.add(homeSeason.getFormWeightedGoalsFor(ALL_GAMES));
        features.add(homeSeason.getFormWeightedGoalsAgainst(ALL_GAMES));
        features.add(homeSeason.getFormXGFOverLastNGames(ALL_GAMES, LAST_5));
        features.add(homeSeason.getFormXGAOverLastNGames(ALL_GAMES, LAST_5)); //53

        //extra home team at home
        features.add(homeSeason.getAvgFormGoalsFor(ONLY_HOME_GAMES, 5));
        features.add(homeSeason.getAvgFormGoalsAgainst(ONLY_HOME_GAMES, 5));
        features.add(homeSeason.getFormXGF(ONLY_HOME_GAMES));
        features.add(homeSeason.getFormXGA(ONLY_HOME_GAMES));
        features.add(homeSeason.getFormWeightedGoalsFor(ONLY_HOME_GAMES));
        features.add(homeSeason.getFormWeightedGoalsAgainst(ONLY_HOME_GAMES));
        features.add(homeSeason.getFormXGFOverLastNGames(ONLY_HOME_GAMES, LAST_5));
        features.add(homeSeason.getFormXGAOverLastNGames(ONLY_HOME_GAMES, LAST_5)); //61

        //extra away team stats
        features.add(awaySeason.getAvgFormGoalsFor(ALL_GAMES, 5));
        features.add(awaySeason.getAvgFormGoalsAgainst(ALL_GAMES, 5));
        features.add(awaySeason.getFormXGF(ALL_GAMES));
        features.add(awaySeason.getFormXGA(ALL_GAMES));
        features.add(awaySeason.getFormWeightedGoalsFor(ALL_GAMES));
        features.add(awaySeason.getFormWeightedGoalsAgainst(ALL_GAMES));
        features.add(awaySeason.getFormXGFOverLastNGames(ALL_GAMES, LAST_5));
        features.add(awaySeason.getFormXGAOverLastNGames(ALL_GAMES, LAST_5)); //69

        //extra away team away stats
        features.add(awaySeason.getAvgFormGoalsFor(ONLY_AWAY_GAMES, 5));
        features.add(awaySeason.getAvgFormGoalsAgainst(ONLY_AWAY_GAMES, 5));
        features.add(awaySeason.getFormXGF(ONLY_AWAY_GAMES));
        features.add(awaySeason.getFormXGA(ONLY_AWAY_GAMES));
        features.add(awaySeason.getFormWeightedGoalsFor(ONLY_AWAY_GAMES));
        features.add(awaySeason.getFormWeightedGoalsAgainst(ONLY_AWAY_GAMES));
        features.add(awaySeason.getFormXGFOverLastNGames(ONLY_AWAY_GAMES, LAST_5));
        features.add(awaySeason.getFormXGAOverLastNGames(ONLY_AWAY_GAMES, LAST_5)); //77

        //clean sheet stats
//        features.add(homeSeason.getAvgNumberOfCleanSheets(ALL_GAMES));
//        features.add(homeSeason.getAvgNumberOfCleanSheets(ONLY_HOME_GAMES));
//        features.add(awaySeason.getAvgNumberOfCleanSheets(ALL_GAMES));
//        features.add(awaySeason.getAvgNumberOfCleanSheets(ONLY_AWAY_GAMES));

        // additional features based on both the home and away team
        // a predictor of the number of XG in the game
        features.add((homeSeason.getAvgXGF(ONLY_HOME_GAMES) - awaySeason.getAvgXGA(ONLY_AWAY_GAMES)) - (awaySeason.getAvgXGF(ONLY_AWAY_GAMES) - homeSeason.getAvgXGA(ONLY_HOME_GAMES)));
        features.add((homeSeason.getAvgXGF(ALL_GAMES) - awaySeason.getAvgXGA(ALL_GAMES)) - (awaySeason.getAvgXGF(ALL_GAMES) - homeSeason.getAvgXGA(ALL_GAMES)));
        // same but for goals
        features.add((homeSeason.getAvgGoalsFor(ONLY_HOME_GAMES) - awaySeason.getAvgGoalsAgainst(ONLY_AWAY_GAMES)) - (awaySeason.getAvgGoalsFor(ONLY_AWAY_GAMES) - homeSeason.getAvgGoalsAgainst(ONLY_HOME_GAMES)));
        features.add((homeSeason.getAvgGoalsFor(ALL_GAMES) - awaySeason.getAvgGoalsAgainst(ALL_GAMES)) - (awaySeason.getAvgGoalsFor(ALL_GAMES) - homeSeason.getAvgGoalsAgainst(ALL_GAMES)));

        // combined features using the form metrics
        features.add((homeSeason.getFormGoalsFor(ALL_GAMES) - awaySeason.getFormGoalsAgainst(ALL_GAMES))
                - (awaySeason.getFormGoalsFor(ALL_GAMES) - homeSeason.getFormGoalsAgainst(ALL_GAMES)));
        features.add((homeSeason.getFormGoalsFor(ONLY_HOME_GAMES) - awaySeason.getFormGoalsAgainst(ONLY_AWAY_GAMES))
                - (awaySeason.getFormGoalsFor(ONLY_AWAY_GAMES) - homeSeason.getFormGoalsAgainst(ONLY_HOME_GAMES)));
        features.add((homeSeason.getFormXGF(ALL_GAMES) - awaySeason.getFormXGA(ALL_GAMES))
                - (awaySeason.getFormXGF(ALL_GAMES) - homeSeason.getFormXGA(ALL_GAMES)));
        features.add((homeSeason.getFormXGF(ONLY_HOME_GAMES) - awaySeason.getFormXGA(ONLY_AWAY_GAMES))
                - (awaySeason.getFormXGF(ONLY_AWAY_GAMES) - homeSeason.getFormXGA(ONLY_HOME_GAMES)));
        // combined features using the weighted metrics
        features.add((homeSeason.getWeightedAvgGoalsFor(ALL_GAMES) - awaySeason.getWeightedAvgGoalsAgainst(ALL_GAMES))
                - (awaySeason.getWeightedAvgGoalsFor(ALL_GAMES) - homeSeason.getWeightedAvgGoalsAgainst(ALL_GAMES)));
        features.add((homeSeason.getWeightedAvgGoalsFor(ONLY_HOME_GAMES) - awaySeason.getWeightedAvgGoalsAgainst(ONLY_AWAY_GAMES))
                - (awaySeason.getWeightedAvgGoalsFor(ONLY_AWAY_GAMES) - homeSeason.getWeightedAvgGoalsAgainst(ONLY_HOME_GAMES)));
        features.add((homeSeason.getWeightedAvgXGF(ALL_GAMES) - awaySeason.getWeightedAvgXGA(ALL_GAMES))
                - (awaySeason.getWeightedAvgXGF(ALL_GAMES) - homeSeason.getWeightedAvgXGA(ALL_GAMES)));
        features.add((homeSeason.getWeightedAvgXGF(ONLY_HOME_GAMES) - awaySeason.getWeightedAvgXGA(ONLY_AWAY_GAMES))
                - (awaySeason.getWeightedAvgXGF(ONLY_AWAY_GAMES) - homeSeason.getWeightedAvgXGA(ONLY_HOME_GAMES)));


        // points compared to points of opposition faced
        features.add(homeSeason.getAvgPoints(ALL_GAMES) / homeSeason.getAvgPointsOfAllOpponentsGamesWholeSeason(ALL_GAMES));
        features.add(awaySeason.getAvgPoints(ALL_GAMES) / awaySeason.getAvgPointsOfAllOpponentsGamesWholeSeason(ALL_GAMES));
        features.add((homeSeason.getAvgPoints(ALL_GAMES) / homeSeason.getAvgPointsOfAllOpponentsGamesWholeSeason(ALL_GAMES))
                - (awaySeason.getAvgPoints(ALL_GAMES) / awaySeason.getAvgPointsOfAllOpponentsGamesWholeSeason(ALL_GAMES)));

        // features to do with possession
        features.add(homeSeason.getAvgPossession(ALL_GAMES));
        features.add(awaySeason.getAvgPossession(ALL_GAMES));
        // xg per possession
        features.add(homeSeason.getAvgXGF(ALL_GAMES) / homeSeason.getAvgPossession(ALL_GAMES));
        features.add(awaySeason.getAvgXGF(ALL_GAMES) / awaySeason.getAvgPossession(ALL_GAMES));


        // features to do with player ratings
        // avg rating of defense
        features.add(homeSeason.getDefensePlayerRating());
        features.add(awaySeason.getDefensePlayerRating());
        // avg rating of mid
        features.add(homeSeason.getMidfieldPlayerRating());
        features.add(awaySeason.getMidfieldPlayerRating());
        // avg rating of attack
        features.add(homeSeason.getAttackPlayerRating());
        features.add(awaySeason.getAttackPlayerRating());
        // rating combinations
        features.add(homeSeason.getDefensePlayerRating() - awaySeason.getAttackPlayerRating());
        features.add(awaySeason.getDefensePlayerRating() - homeSeason.getAttackPlayerRating());
        features.add(homeSeason.getMidfieldPlayerRating() - awaySeason.getMidfieldPlayerRating());

        // features to do with shots on target
        features.add(homeSeason.getAvgShots(ALL_GAMES));
        features.add(awaySeason.getAvgShots(ALL_GAMES));
        features.add(homeSeason.getAvgShotsOnTarget(ALL_GAMES));
        features.add(awaySeason.getAvgShotsOnTarget(ALL_GAMES));
        // avg xg of chances created.
        features.add(homeSeason.getAvgXGF(ALL_GAMES) / homeSeason.getAvgShots(ALL_GAMES));
        features.add(awaySeason.getAvgXGF(ALL_GAMES) / awaySeason.getAvgShots(ALL_GAMES));

        // xg compared to goals
        features.add(homeSeason.getAvgGoalsFor(ALL_GAMES) - homeSeason.getAvgXGF(ALL_GAMES));
        features.add(homeSeason.getAvgGoalsAgainst(ALL_GAMES) - homeSeason.getAvgXGA(ALL_GAMES));
        features.add(awaySeason.getAvgGoalsFor(ALL_GAMES) - awaySeason.getAvgXGF(ALL_GAMES));
        features.add(awaySeason.getAvgGoalsAgainst(ALL_GAMES) - awaySeason.getAvgXGA(ALL_GAMES));

        // team strength features based on comparison to the rest of the league.
        features.add(homeSeason.getHomeAttackStrength());
        features.add(homeSeason.getHomeDefenseStrength());
        features.add(awaySeason.getAwayAttackStrength());
        features.add(awaySeason.getAwayDefenseStrength());
        features.add((homeSeason.getHomeAttackStrength() - awaySeason.getAwayDefenseStrength()) - (awaySeason.getAwayAttackStrength() - homeSeason.getHomeDefenseStrength()));

        // team strength using XG
        features.add(homeSeason.getHomeXgAttackStrength());
        features.add(homeSeason.getHomeXgDefenseStrength());
        features.add(awaySeason.getAwayXgAttackStrength());
        features.add(awaySeason.getAwayXgDefenseStrength());
        features.add((homeSeason.getHomeXgAttackStrength() - awaySeason.getAwayXgDefenseStrength()) - (awaySeason.getAwayXgAttackStrength() - homeSeason.getHomeXgDefenseStrength()));

        // last 5 goals compared to opponent strength
        double last5HomeGoalsComparedToOpponentStrength = homeSeason.getAvgGoalsForLastXGames(ALL_GAMES, LAST_5) / homeSeason.getAvgPointsOfAllOpponentsLast5Games(ALL_GAMES);
        double last5AwayGoalsComparedToOpponentStrength = awaySeason.getAvgGoalsForLastXGames(ALL_GAMES, LAST_5) / awaySeason.getAvgPointsOfAllOpponentsLast5Games(ALL_GAMES);
        double last5HomeGoalsAgainstComparedToOpponentStrength = homeSeason.getAvgGoalsAgainstLastXGames(ALL_GAMES, LAST_5) / homeSeason.getAvgPointsOfAllOpponentsLast5Games(ALL_GAMES);
        double last5AwayGoalsAgainstComparedToOpponentStrength = awaySeason.getAvgGoalsAgainstLastXGames(ALL_GAMES, LAST_5) / awaySeason.getAvgPointsOfAllOpponentsLast5Games(ALL_GAMES);
        features.add(last5HomeGoalsComparedToOpponentStrength);
        features.add(last5AwayGoalsComparedToOpponentStrength);
        features.add((last5HomeGoalsComparedToOpponentStrength - last5AwayGoalsAgainstComparedToOpponentStrength) - (last5AwayGoalsComparedToOpponentStrength - last5HomeGoalsAgainstComparedToOpponentStrength));

        // goals compared to opponent strength
        double homeGoalsComparedToOpponentStrength = homeSeason.getAvgGoalsFor(ALL_GAMES) / homeSeason.getAvgPointsOfAllOpponentsGamesWholeSeason(ALL_GAMES);
        double awayGoalsComparedToOpponentStrength = awaySeason.getAvgGoalsFor(ALL_GAMES) / awaySeason.getAvgPointsOfAllOpponentsGamesWholeSeason(ALL_GAMES);
        double homeGoalsAgainstComparedToOpponentStrength = homeSeason.getAvgGoalsAgainst(ALL_GAMES) / homeSeason.getAvgPointsOfAllOpponentsGamesWholeSeason(ALL_GAMES);
        double awayGoalsAgainstComparedToOpponentStrength = awaySeason.getAvgGoalsAgainst(ALL_GAMES) / awaySeason.getAvgPointsOfAllOpponentsGamesWholeSeason(ALL_GAMES);
        features.add(homeGoalsComparedToOpponentStrength);
        features.add(awayGoalsComparedToOpponentStrength);
        features.add((homeGoalsComparedToOpponentStrength - awayGoalsAgainstComparedToOpponentStrength) - (awayGoalsComparedToOpponentStrength - homeGoalsAgainstComparedToOpponentStrength));

        // last 5 goals compared to opponent strength
        double last5HomeXgfComparedToOpponentStrength = homeSeason.getAvgXGFOverLastXGames(ALL_GAMES, LAST_5) / homeSeason.getAvgPointsOfAllOpponentsLast5Games(ALL_GAMES);
        double last5AwayXgfComparedToOpponentStrength = awaySeason.getAvgXGFOverLastXGames(ALL_GAMES, LAST_5) / awaySeason.getAvgPointsOfAllOpponentsLast5Games(ALL_GAMES);
        double last5HomeXgaComparedToOpponentStrength = homeSeason.getAvgXGAOverLastXGames(ALL_GAMES, LAST_5) / homeSeason.getAvgPointsOfAllOpponentsLast5Games(ALL_GAMES);
        double last5AwayXgaComparedToOpponentStrength = awaySeason.getAvgXGAOverLastXGames(ALL_GAMES, LAST_5) / awaySeason.getAvgPointsOfAllOpponentsLast5Games(ALL_GAMES);
        features.add(last5HomeXgfComparedToOpponentStrength);
        features.add(last5AwayXgfComparedToOpponentStrength);
        features.add((last5HomeXgfComparedToOpponentStrength - last5AwayXgaComparedToOpponentStrength) - (last5AwayXgfComparedToOpponentStrength - last5HomeXgaComparedToOpponentStrength));

        // goals compared to opponent strength
        double homeXgfComparedToOpponentStrength = homeSeason.getAvgXGF(ALL_GAMES) / homeSeason.getAvgPointsOfAllOpponentsGamesWholeSeason(ALL_GAMES);
        double awayXgfComparedToOpponentStrength = awaySeason.getAvgXGF(ALL_GAMES) / awaySeason.getAvgPointsOfAllOpponentsGamesWholeSeason(ALL_GAMES);
        double homeXgaComparedToOpponentStrength = homeSeason.getAvgXGA(ALL_GAMES) / homeSeason.getAvgPointsOfAllOpponentsGamesWholeSeason(ALL_GAMES);
        double awayXgaComparedToOpponentStrength = awaySeason.getAvgXGA(ALL_GAMES) / awaySeason.getAvgPointsOfAllOpponentsGamesWholeSeason(ALL_GAMES);
        features.add(homeXgfComparedToOpponentStrength);
        features.add(awayXgfComparedToOpponentStrength);
        features.add((homeXgfComparedToOpponentStrength - awayXgaComparedToOpponentStrength) - (awayXgfComparedToOpponentStrength - homeXgaComparedToOpponentStrength));

        return features;
    }

    public static ArrayList<Double> getFeaturesExtendedOld(TrainingTeam homeTeam, TrainingTeamsSeason homeSeason,
                                                           TrainingTeam awayTeam, TrainingTeamsSeason awaySeason,
                                                           ArrayList<String> homePlayersNames, ArrayList<String> awayPlayersNames,
                                                           int seasonYearStart, int result) {

        int LAST_5 = 3;
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
        features.add(homeSeason.getAvgPointsOverLastXGames(ALL_GAMES, LAST_5));
        features.add(homeSeason.getAvgPointsWhenScoredFirst(ALL_GAMES));
        features.add(homeSeason.getAvgPointsWhenConceededFirst(ALL_GAMES));
        features.add(homeTeam.getPointsOfLastMatchups(awayTeam.getTeamName(), ALL_GAMES, homeSeason.getSeasonYearStart()-2));
        features.add(homeSeason.getMinsWeightedLineupRating(ALL_GAMES, homePlayersNames));
        features.add(homeSeason.getLineupStrength(ALL_GAMES, homePlayersNames)); //14

        //home@home
        features.add(homeSeason.getAvgGoalsFor(ONLY_HOME_GAMES));
        features.add(homeSeason.getAvgGoalsAgainst(ONLY_HOME_GAMES));
        features.add(homeSeason.getAvgXGF(ONLY_HOME_GAMES));
        features.add(homeSeason.getAvgXGA(ONLY_HOME_GAMES));
        features.add(homeSeason.getWeightedAvgXGF(ONLY_HOME_GAMES));
        features.add(homeSeason.getWeightedAvgXGA(ONLY_HOME_GAMES));
        features.add(homeSeason.getAvgPoints(ONLY_HOME_GAMES));
        features.add(homeSeason.getAvgPointsOverLastXGames(ONLY_HOME_GAMES, LAST_5));
        features.add(homeSeason.getAvgPointsWhenScoredFirst(ONLY_HOME_GAMES));
        features.add(homeSeason.getAvgPointsWhenConceededFirst(ONLY_HOME_GAMES));
        features.add(homeTeam.getPointsOfLastMatchups(awayTeam.getTeamName(), ONLY_HOME_GAMES, homeSeason.getSeasonYearStart()-2));
        features.add(homeSeason.getMinsWeightedLineupRating(ONLY_HOME_GAMES, homePlayersNames));
        features.add(homeSeason.getLineupStrength(ONLY_HOME_GAMES, homePlayersNames)); //27

        //away
        features.add(awaySeason.getAvgGoalsFor(ALL_GAMES));
        features.add(awaySeason.getAvgGoalsAgainst(ALL_GAMES));
        features.add(awaySeason.getAvgXGF(ALL_GAMES));
        features.add(awaySeason.getAvgXGA(ALL_GAMES));
        features.add(awaySeason.getWeightedAvgXGF(ALL_GAMES));
        features.add(awaySeason.getWeightedAvgXGA(ALL_GAMES));
        features.add(awaySeason.getAvgPoints(ALL_GAMES));
        features.add(awaySeason.getAvgPointsOverLastXGames(ALL_GAMES, 5));
        features.add(awaySeason.getAvgPointsWhenScoredFirst(ALL_GAMES));
        features.add(awaySeason.getAvgPointsWhenConceededFirst(ALL_GAMES));
//        features.add(awayTeam.getPointsOfLastMatchups(homeTeam.getTeamName(), ALL_GAMES, awaySeason.getSeasonYearStart()-2));
        features.add(awaySeason.getMinsWeightedLineupRating(ALL_GAMES, awayPlayersNames));
        features.add(awaySeason.getLineupStrength(ALL_GAMES, awayPlayersNames)); //40

        //away@away
        features.add(awaySeason.getAvgGoalsFor(ONLY_AWAY_GAMES));
        features.add(awaySeason.getAvgGoalsAgainst(ONLY_AWAY_GAMES));
        features.add(awaySeason.getAvgXGF(ONLY_AWAY_GAMES));
        features.add(awaySeason.getAvgXGA(ONLY_AWAY_GAMES));
        features.add(awaySeason.getWeightedAvgXGF(ONLY_AWAY_GAMES));
        features.add(awaySeason.getWeightedAvgXGA(ONLY_AWAY_GAMES));
        features.add(awaySeason.getAvgPoints(ONLY_AWAY_GAMES));
        features.add(awaySeason.getAvgPointsOverLastXGames(ONLY_AWAY_GAMES, 5));
        features.add(awaySeason.getAvgPointsWhenScoredFirst(ONLY_AWAY_GAMES));
        features.add(awaySeason.getAvgPointsWhenConceededFirst(ONLY_AWAY_GAMES));
//        features.add(awayTeam.getPointsOfLastMatchups(homeTeam.getTeamName(), ONLY_AWAY_GAMES, awaySeason.getSeasonYearStart()-2));
        features.add(awaySeason.getMinsWeightedLineupRating(ONLY_AWAY_GAMES, awayPlayersNames));
        features.add(awaySeason.getLineupStrength(ONLY_AWAY_GAMES, awayPlayersNames)); //53

        //extra home team stats
        features.add(homeSeason.getAvgFormGoalsFor(ALL_GAMES, 5));
        features.add(homeSeason.getAvgFormGoalsAgainst(ALL_GAMES, 5));
        features.add(homeSeason.getFormXGF(ALL_GAMES));
        features.add(homeSeason.getFormXGA(ALL_GAMES));
        features.add(homeSeason.getFormWeightedGoalsFor(ALL_GAMES));
        features.add(homeSeason.getFormWeightedGoalsAgainst(ALL_GAMES));
        features.add(homeSeason.getFormXGFOverLastNGames(ALL_GAMES, LAST_5));
        features.add(homeSeason.getFormXGAOverLastNGames(ALL_GAMES, LAST_5)); //61

        //extra home team at home
        features.add(homeSeason.getAvgFormGoalsFor(ONLY_HOME_GAMES, 5));
        features.add(homeSeason.getAvgFormGoalsAgainst(ONLY_HOME_GAMES, 5));
        features.add(homeSeason.getFormXGF(ONLY_HOME_GAMES));
        features.add(homeSeason.getFormXGA(ONLY_HOME_GAMES));
        features.add(homeSeason.getFormWeightedGoalsFor(ONLY_HOME_GAMES));
        features.add(homeSeason.getFormWeightedGoalsAgainst(ONLY_HOME_GAMES));
        features.add(homeSeason.getFormXGFOverLastNGames(ONLY_HOME_GAMES, LAST_5));
        features.add(homeSeason.getFormXGAOverLastNGames(ONLY_HOME_GAMES, LAST_5)); //69

        //extra away team stats
        features.add(awaySeason.getAvgFormGoalsFor(ALL_GAMES, 5));
        features.add(awaySeason.getAvgFormGoalsAgainst(ALL_GAMES, 5));
        features.add(awaySeason.getFormXGF(ALL_GAMES));
        features.add(awaySeason.getFormXGA(ALL_GAMES));
        features.add(awaySeason.getFormWeightedGoalsFor(ALL_GAMES));
        features.add(awaySeason.getFormWeightedGoalsAgainst(ALL_GAMES));
        features.add(awaySeason.getFormXGFOverLastNGames(ALL_GAMES, LAST_5));
        features.add(awaySeason.getFormXGAOverLastNGames(ALL_GAMES, LAST_5)); //77

        //extra away team away stats
        features.add(awaySeason.getAvgFormGoalsFor(ONLY_AWAY_GAMES, 5));
        features.add(awaySeason.getAvgFormGoalsAgainst(ONLY_AWAY_GAMES, 5));
        features.add(awaySeason.getFormXGF(ONLY_AWAY_GAMES));
        features.add(awaySeason.getFormXGA(ONLY_AWAY_GAMES));
        features.add(awaySeason.getFormWeightedGoalsFor(ONLY_AWAY_GAMES));
        features.add(awaySeason.getFormWeightedGoalsAgainst(ONLY_AWAY_GAMES));
        features.add(awaySeason.getFormXGFOverLastNGames(ONLY_AWAY_GAMES, LAST_5));
        features.add(awaySeason.getFormXGAOverLastNGames(ONLY_AWAY_GAMES, LAST_5)); //85

        // additional features based on both the home and away team
        // a predictor of the number of XG in the game
        features.add((homeSeason.getAvgXGF(ONLY_HOME_GAMES) - awaySeason.getAvgXGA(ONLY_AWAY_GAMES)) - (awaySeason.getAvgXGF(ONLY_AWAY_GAMES) - homeSeason.getAvgXGA(ONLY_HOME_GAMES)));
        features.add((homeSeason.getAvgXGF(ALL_GAMES) - awaySeason.getAvgXGA(ALL_GAMES)) - (awaySeason.getAvgXGF(ALL_GAMES) - homeSeason.getAvgXGA(ALL_GAMES)));
        // same but for goals
        features.add((homeSeason.getAvgGoalsFor(ONLY_HOME_GAMES) - awaySeason.getAvgGoalsAgainst(ONLY_AWAY_GAMES)) - (awaySeason.getAvgGoalsFor(ONLY_AWAY_GAMES) - homeSeason.getAvgGoalsAgainst(ONLY_HOME_GAMES)));
        features.add((homeSeason.getAvgGoalsFor(ALL_GAMES) - awaySeason.getAvgGoalsAgainst(ALL_GAMES)) - (awaySeason.getAvgGoalsFor(ALL_GAMES) - homeSeason.getAvgGoalsAgainst(ALL_GAMES)));

        // combined features using the form metrics
        features.add((homeSeason.getFormGoalsFor(ALL_GAMES) - awaySeason.getFormGoalsAgainst(ALL_GAMES))
                - (awaySeason.getFormGoalsFor(ALL_GAMES) - homeSeason.getFormGoalsAgainst(ALL_GAMES)));
        features.add((homeSeason.getFormGoalsFor(ONLY_HOME_GAMES) - awaySeason.getFormGoalsAgainst(ONLY_AWAY_GAMES))
                - (awaySeason.getFormGoalsFor(ONLY_AWAY_GAMES) - homeSeason.getFormGoalsAgainst(ONLY_HOME_GAMES)));
        features.add((homeSeason.getFormXGF(ALL_GAMES) - awaySeason.getFormXGA(ALL_GAMES))
                - (awaySeason.getFormXGF(ALL_GAMES) - homeSeason.getFormXGA(ALL_GAMES)));
        features.add((homeSeason.getFormXGF(ONLY_HOME_GAMES) - awaySeason.getFormXGA(ONLY_AWAY_GAMES))
                - (awaySeason.getFormXGF(ONLY_AWAY_GAMES) - homeSeason.getFormXGA(ONLY_HOME_GAMES)));
        // combined features using the weighted metrics
        features.add((homeSeason.getWeightedAvgGoalsFor(ALL_GAMES) - awaySeason.getWeightedAvgGoalsAgainst(ALL_GAMES))
                - (awaySeason.getWeightedAvgGoalsFor(ALL_GAMES) - homeSeason.getWeightedAvgGoalsAgainst(ALL_GAMES)));
        features.add((homeSeason.getWeightedAvgGoalsFor(ONLY_HOME_GAMES) - awaySeason.getWeightedAvgGoalsAgainst(ONLY_AWAY_GAMES))
                - (awaySeason.getWeightedAvgGoalsFor(ONLY_AWAY_GAMES) - homeSeason.getWeightedAvgGoalsAgainst(ONLY_HOME_GAMES)));
        features.add((homeSeason.getWeightedAvgXGF(ALL_GAMES) - awaySeason.getWeightedAvgXGA(ALL_GAMES))
                - (awaySeason.getWeightedAvgXGF(ALL_GAMES) - homeSeason.getWeightedAvgXGA(ALL_GAMES)));
        features.add((homeSeason.getWeightedAvgXGF(ONLY_HOME_GAMES) - awaySeason.getWeightedAvgXGA(ONLY_AWAY_GAMES))
                - (awaySeason.getWeightedAvgXGF(ONLY_AWAY_GAMES) - homeSeason.getWeightedAvgXGA(ONLY_HOME_GAMES)));


        // points compared to points of opposition faced
        features.add(homeSeason.getAvgPoints(ALL_GAMES) / homeSeason.getAvgPointsOfAllOpponentsGamesWholeSeason(ALL_GAMES));
        features.add(awaySeason.getAvgPoints(ALL_GAMES) / awaySeason.getAvgPointsOfAllOpponentsGamesWholeSeason(ALL_GAMES));
        features.add((homeSeason.getAvgPoints(ALL_GAMES) / homeSeason.getAvgPointsOfAllOpponentsGamesWholeSeason(ALL_GAMES))
                - (awaySeason.getAvgPoints(ALL_GAMES) / awaySeason.getAvgPointsOfAllOpponentsGamesWholeSeason(ALL_GAMES)));

        // features to do with possession
        features.add(homeSeason.getAvgPossession(ALL_GAMES));
        features.add(awaySeason.getAvgPossession(ALL_GAMES));
        // xg per possession
        features.add(homeSeason.getAvgXGF(ALL_GAMES) / homeSeason.getAvgPossession(ALL_GAMES));
        features.add(awaySeason.getAvgXGF(ALL_GAMES) / awaySeason.getAvgPossession(ALL_GAMES));


        // features to do with player ratings
        // avg rating of defense
        features.add(homeSeason.getDefensePlayerRating());
        features.add(awaySeason.getDefensePlayerRating());
        // avg rating of mid
        features.add(homeSeason.getMidfieldPlayerRating());
        features.add(awaySeason.getMidfieldPlayerRating());
        // avg rating of attack
        features.add(homeSeason.getAttackPlayerRating());
        features.add(awaySeason.getAttackPlayerRating());
        // avg ovr rating
        features.add(homeSeason.getOvrPlayerRating());
        features.add(awaySeason.getOvrPlayerRating());
        // rating combinations
        features.add(homeSeason.getDefensePlayerRating() - awaySeason.getAttackPlayerRating());
        features.add(awaySeason.getDefensePlayerRating() - homeSeason.getAttackPlayerRating());
        features.add(homeSeason.getMidfieldPlayerRating() - awaySeason.getMidfieldPlayerRating());
        features.add(homeSeason.getOvrPlayerRating() - awaySeason.getOvrPlayerRating());

        // features to do with shots on target
        features.add(homeSeason.getAvgShots(ALL_GAMES));
        features.add(awaySeason.getAvgShots(ALL_GAMES));
        features.add(homeSeason.getAvgShotsOnTarget(ALL_GAMES));
        features.add(awaySeason.getAvgShotsOnTarget(ALL_GAMES));
        // avg xg of chances created.
        features.add(homeSeason.getAvgXGF(ALL_GAMES) / homeSeason.getAvgShots(ALL_GAMES));
        features.add(awaySeason.getAvgXGF(ALL_GAMES) / awaySeason.getAvgShots(ALL_GAMES));

        // xg compared to goals
        features.add(homeSeason.getAvgGoalsFor(ALL_GAMES) - homeSeason.getAvgXGF(ALL_GAMES));
        features.add(homeSeason.getAvgGoalsAgainst(ALL_GAMES) - homeSeason.getAvgXGA(ALL_GAMES));
        features.add(awaySeason.getAvgGoalsFor(ALL_GAMES) - awaySeason.getAvgXGF(ALL_GAMES));
        features.add(awaySeason.getAvgGoalsAgainst(ALL_GAMES) - awaySeason.getAvgXGA(ALL_GAMES));

        // team strength features based on comparison to the rest of the league.
        features.add(homeSeason.getHomeAttackStrength());
        features.add(homeSeason.getHomeDefenseStrength());
        features.add(awaySeason.getAwayAttackStrength());
        features.add(awaySeason.getAwayDefenseStrength());
        features.add((homeSeason.getHomeAttackStrength() - awaySeason.getAwayDefenseStrength()) - (awaySeason.getAwayAttackStrength() - homeSeason.getHomeDefenseStrength()));

        // team strength using XG
        features.add(homeSeason.getHomeXgAttackStrength());
        features.add(homeSeason.getHomeXgDefenseStrength());
        features.add(awaySeason.getAwayXgAttackStrength());
        features.add(awaySeason.getAwayXgDefenseStrength());
        features.add((homeSeason.getHomeXgAttackStrength() - awaySeason.getAwayXgDefenseStrength()) - (awaySeason.getAwayXgAttackStrength() - homeSeason.getHomeXgDefenseStrength()));

        // last 5 goals compared to opponent strength
        double last5HomeGoalsComparedToOpponentStrength = homeSeason.getAvgGoalsForLastXGames(ALL_GAMES, LAST_5) / homeSeason.getAvgPointsOfAllOpponentsLast5Games(ALL_GAMES);
        double last5AwayGoalsComparedToOpponentStrength = awaySeason.getAvgGoalsForLastXGames(ALL_GAMES, LAST_5) / awaySeason.getAvgPointsOfAllOpponentsLast5Games(ALL_GAMES);
        double last5HomeGoalsAgainstComparedToOpponentStrength = homeSeason.getAvgGoalsAgainstLastXGames(ALL_GAMES, LAST_5) / homeSeason.getAvgPointsOfAllOpponentsLast5Games(ALL_GAMES);
        double last5AwayGoalsAgainstComparedToOpponentStrength = awaySeason.getAvgGoalsAgainstLastXGames(ALL_GAMES, LAST_5) / awaySeason.getAvgPointsOfAllOpponentsLast5Games(ALL_GAMES);
        features.add(last5HomeGoalsComparedToOpponentStrength);
        features.add(last5AwayGoalsComparedToOpponentStrength);
        features.add((last5HomeGoalsComparedToOpponentStrength - last5AwayGoalsAgainstComparedToOpponentStrength) - (last5AwayGoalsComparedToOpponentStrength - last5HomeGoalsAgainstComparedToOpponentStrength));

        // goals compared to opponent strength
        double homeGoalsComparedToOpponentStrength = homeSeason.getAvgGoalsFor(ALL_GAMES) / homeSeason.getAvgPointsOfAllOpponentsGamesWholeSeason(ALL_GAMES);
        double awayGoalsComparedToOpponentStrength = awaySeason.getAvgGoalsFor(ALL_GAMES) / awaySeason.getAvgPointsOfAllOpponentsGamesWholeSeason(ALL_GAMES);
        double homeGoalsAgainstComparedToOpponentStrength = homeSeason.getAvgGoalsAgainst(ALL_GAMES) / homeSeason.getAvgPointsOfAllOpponentsGamesWholeSeason(ALL_GAMES);
        double awayGoalsAgainstComparedToOpponentStrength = awaySeason.getAvgGoalsAgainst(ALL_GAMES) / awaySeason.getAvgPointsOfAllOpponentsGamesWholeSeason(ALL_GAMES);
        features.add(homeGoalsComparedToOpponentStrength);
        features.add(awayGoalsComparedToOpponentStrength);
        features.add((homeGoalsComparedToOpponentStrength - awayGoalsAgainstComparedToOpponentStrength) - (awayGoalsComparedToOpponentStrength - homeGoalsAgainstComparedToOpponentStrength));

        // last 5 goals compared to opponent strength
        double last5HomeXgfComparedToOpponentStrength = homeSeason.getAvgXGFOverLastXGames(ALL_GAMES, LAST_5) / homeSeason.getAvgPointsOfAllOpponentsLast5Games(ALL_GAMES);
        double last5AwayXgfComparedToOpponentStrength = awaySeason.getAvgXGFOverLastXGames(ALL_GAMES, LAST_5) / awaySeason.getAvgPointsOfAllOpponentsLast5Games(ALL_GAMES);
        double last5HomeXgaComparedToOpponentStrength = homeSeason.getAvgXGAOverLastXGames(ALL_GAMES, LAST_5) / homeSeason.getAvgPointsOfAllOpponentsLast5Games(ALL_GAMES);
        double last5AwayXgaComparedToOpponentStrength = awaySeason.getAvgXGAOverLastXGames(ALL_GAMES, LAST_5) / awaySeason.getAvgPointsOfAllOpponentsLast5Games(ALL_GAMES);
        features.add(last5HomeXgfComparedToOpponentStrength);
        features.add(last5AwayXgfComparedToOpponentStrength);
        features.add((last5HomeXgfComparedToOpponentStrength - last5AwayXgaComparedToOpponentStrength) - (last5AwayXgfComparedToOpponentStrength - last5HomeXgaComparedToOpponentStrength));

        // goals compared to opponent strength
        double homeXgfComparedToOpponentStrength = homeSeason.getAvgXGF(ALL_GAMES) / homeSeason.getAvgPointsOfAllOpponentsGamesWholeSeason(ALL_GAMES);
        double awayXgfComparedToOpponentStrength = awaySeason.getAvgXGF(ALL_GAMES) / awaySeason.getAvgPointsOfAllOpponentsGamesWholeSeason(ALL_GAMES);
        double homeXgaComparedToOpponentStrength = homeSeason.getAvgXGA(ALL_GAMES) / homeSeason.getAvgPointsOfAllOpponentsGamesWholeSeason(ALL_GAMES);
        double awayXgaComparedToOpponentStrength = awaySeason.getAvgXGA(ALL_GAMES) / awaySeason.getAvgPointsOfAllOpponentsGamesWholeSeason(ALL_GAMES);
        features.add(homeXgfComparedToOpponentStrength);
        features.add(awayXgfComparedToOpponentStrength);
        features.add((homeXgfComparedToOpponentStrength - awayXgaComparedToOpponentStrength) - (awayXgfComparedToOpponentStrength - homeXgaComparedToOpponentStrength));


        return features;
    }


    // NN Ac: 51.3%, prof -4%. Log Reg: Ac: 52.2%, prof: 59%
    public static ArrayList<Double> getFeaturesSmallMakesSense(TrainingTeam homeTeam, TrainingTeamsSeason homeSeason,
                                                                  TrainingTeam awayTeam, TrainingTeamsSeason awaySeason,
                                                               int result, double[] odds) {

        ArrayList<Double> features = new ArrayList<>();
        features.add((double) result);

        features.add(homeSeason.getAvgPoints(ALL_GAMES));
        features.add(homeSeason.getAvgPointsOverLastXGames(ALL_GAMES, 5));
        features.add(awaySeason.getAvgPoints(ALL_GAMES));
        features.add(awaySeason.getAvgPointsOverLastXGames(ALL_GAMES, 5));
        features.add(awayTeam.getPointsOfLastMatchups(homeTeam.getTeamName(), ALL_GAMES, awaySeason.getSeasonYearStart()-2));
        features.add(homeSeason.getAvgGoalsFor(ALL_GAMES));
        features.add(awaySeason.getAvgGoalsFor(ALL_GAMES));
        features.add(homeSeason.getAvgXGF(ALL_GAMES));
        features.add(awaySeason.getAvgXGF(ALL_GAMES));
        features.add(homeSeason.getAvgXGA(ALL_GAMES));
        features.add(awaySeason.getAvgXGA(ALL_GAMES));
        features.add(homeSeason.getFormXGF(ALL_GAMES));
        features.add(awaySeason.getFormXGF(ALL_GAMES));

        return features;
    }

    //
    public static ArrayList<Double> getFeaturesSmallFromRCoeffMakesSense(TrainingTeam homeTeam, TrainingTeamsSeason homeSeason,
                                                                         TrainingTeam awayTeam, TrainingTeamsSeason awaySeason,
                                                                         int result, double[] odds) {

        ArrayList<Double> features = new ArrayList<>();
        features.add((double) result);

        features.add(1/odds[0]);
        features.add(homeSeason.getOvrPlayerRating() - awaySeason.getOvrPlayerRating());
        features.add((homeSeason.getAvgPoints(ALL_GAMES) / homeSeason.getAvgPointsOfAllOpponentsGamesWholeSeason(ALL_GAMES))
                - (awaySeason.getAvgPoints(ALL_GAMES) / awaySeason.getAvgPointsOfAllOpponentsGamesWholeSeason(ALL_GAMES)));
        features.add(homeSeason.getAvgXGF(ALL_GAMES));
        features.add(awaySeason.getAvgXGF(ALL_GAMES));
        features.add(homeSeason.getFormXGF(ALL_GAMES));
        features.add(awaySeason.getFormXGF(ALL_GAMES));
        features.add(homeSeason.getAvgGoalsFor(ALL_GAMES));
        features.add(awaySeason.getAvgGoalsFor(ALL_GAMES));
        features.add(homeSeason.getAvgPoints(ALL_GAMES));
        features.add(awaySeason.getAvgPoints(ALL_GAMES));
        features.add(homeSeason.getAvgPointsOverLastXGames(ALL_GAMES, 5));
        features.add(awaySeason.getAvgPointsOverLastXGames(ALL_GAMES, 5));
        features.add(homeSeason.getAvgXGA(ALL_GAMES));
        features.add(awaySeason.getAvgXGA(ALL_GAMES));

        return features;
    }

    // testing on 19-21 games: NN: acc 51.5% prof: -16.2%. LogReg acc 51.3% prof: -1.2%
    public static ArrayList<Double> getFeaturesSmallFromChiScore(TrainingTeam homeTeam, TrainingTeamsSeason homeSeason,
                                                                  TrainingTeam awayTeam, TrainingTeamsSeason awaySeason,
                                                                  ArrayList<String> homePlayersNames, ArrayList<String> awayPlayersNames,
                                                                  int seasonYearStart, int result) {

        int NUMB_PREV_GAMES = 3;
        ArrayList<Double> features = new ArrayList<>();
        features.add((double) result);

        features.add(homeTeam.getPointsOfLastMatchups(awayTeam.getTeamName(), ALL_GAMES, homeSeason.getSeasonYearStart()-2));
        features.add(homeSeason.getAvgPointsWhenConceededFirst(ONLY_HOME_GAMES));
        features.add(awaySeason.getAvgPointsOverLastXGames(ONLY_AWAY_GAMES, NUMB_PREV_GAMES));
        features.add(awaySeason.getAvgPoints(ONLY_AWAY_GAMES));
        features.add(awaySeason.getAvgGoalsFor(ALL_GAMES));
        features.add(homeSeason.getAvgPointsWhenConceededFirst(ALL_GAMES));
        features.add(awaySeason.getAvgXGF(ALL_GAMES));
        features.add(awaySeason.getAvgPointsWhenConceededFirst(ALL_GAMES));
        features.add(awaySeason.getAvgPoints(ALL_GAMES));
        features.add(homeSeason.getAvgGoalsFor(ALL_GAMES)); //10

        return features;
    }

    // testing on 19-21 games: NN: acc 51.1% prof: 10.5%. LogReg acc 52.5% prof: 0.08%
    public static ArrayList<Double> getFeaturesMediumFromChiScore(TrainingTeam homeTeam, TrainingTeamsSeason homeSeason,
                                                                  TrainingTeam awayTeam, TrainingTeamsSeason awaySeason,
                                                                  ArrayList<String> homePlayersNames, ArrayList<String> awayPlayersNames,
                                                                  int seasonYearStart, int result) {

        int NUMB_PREV_GAMES = 3;
        ArrayList<Double> features = new ArrayList<>();
        features.add((double) result);

        features.add(homeTeam.getPointsOfLastMatchups(awayTeam.getTeamName(), ALL_GAMES, homeSeason.getSeasonYearStart()-2));
        features.add(homeSeason.getAvgPointsWhenConceededFirst(ONLY_HOME_GAMES));
        features.add(awaySeason.getAvgPointsOverLastXGames(ONLY_AWAY_GAMES, NUMB_PREV_GAMES));
        features.add(awaySeason.getAvgPoints(ONLY_AWAY_GAMES));
        features.add(awaySeason.getAvgGoalsFor(ALL_GAMES));
        features.add(homeSeason.getAvgPointsWhenConceededFirst(ALL_GAMES));
        features.add(awaySeason.getAvgXGF(ALL_GAMES));
        features.add(awaySeason.getAvgPointsWhenConceededFirst(ALL_GAMES));
        features.add(awaySeason.getAvgPoints(ALL_GAMES));
        features.add(homeSeason.getAvgGoalsFor(ALL_GAMES)); //10

        features.add(awaySeason.getAvgXGF(ONLY_AWAY_GAMES));
        features.add(awayTeam.getPointsOfLastMatchups(homeTeam.getTeamName(), ONLY_AWAY_GAMES, awaySeason.getSeasonYearStart()-2));
        features.add(awaySeason.getAvgPointsOverLastXGames(ALL_GAMES, NUMB_PREV_GAMES));
        features.add(homeSeason.getAvgPoints(ALL_GAMES));
        features.add(homeSeason.getAvgPointsOverLastXGames(ALL_GAMES, NUMB_PREV_GAMES));
        features.add(awaySeason.getFormGoalsFor(ALL_GAMES));
        features.add(homeSeason.getAvgXGF(ALL_GAMES));
        features.add(awaySeason.getWeightedAvgXGF(ONLY_AWAY_GAMES));
        features.add(homeSeason.getAvgPointsOverLastXGames(ONLY_HOME_GAMES, NUMB_PREV_GAMES));
        features.add(awaySeason.getAvgXGA(ALL_GAMES)); //20

        return features;
    }


    public static ArrayList<Double> getFeaturesLargeFromChiScore(TrainingTeam homeTeam, TrainingTeamsSeason homeSeason,
                                                                  TrainingTeam awayTeam, TrainingTeamsSeason awaySeason,
                                                                  ArrayList<String> homePlayersNames, ArrayList<String> awayPlayersNames,
                                                                  int seasonYearStart, int result) {

        int NUMB_PREV_GAMES = 3;
        ArrayList<Double> features = new ArrayList<>();
        features.add((double) result);

        features.add(homeTeam.getPointsOfLastMatchups(awayTeam.getTeamName(), ALL_GAMES, homeSeason.getSeasonYearStart()-2));
        features.add(homeSeason.getAvgPointsWhenConceededFirst(ONLY_HOME_GAMES));
        features.add(awaySeason.getAvgPointsOverLastXGames(ONLY_AWAY_GAMES, NUMB_PREV_GAMES));
        features.add(awaySeason.getAvgPoints(ONLY_AWAY_GAMES));
        features.add(awaySeason.getAvgGoalsFor(ALL_GAMES));
        features.add(homeSeason.getAvgPointsWhenConceededFirst(ALL_GAMES));
        features.add(awaySeason.getAvgXGF(ALL_GAMES));
        features.add(awaySeason.getAvgPointsWhenConceededFirst(ALL_GAMES));
        features.add(awaySeason.getAvgPoints(ALL_GAMES));
        features.add(homeSeason.getAvgGoalsFor(ALL_GAMES)); //10

        features.add(awaySeason.getAvgXGF(ONLY_AWAY_GAMES));
        features.add(awayTeam.getPointsOfLastMatchups(homeTeam.getTeamName(), ONLY_AWAY_GAMES, awaySeason.getSeasonYearStart()-2));
        features.add(awaySeason.getAvgPointsOverLastXGames(ALL_GAMES, NUMB_PREV_GAMES));
        features.add(homeSeason.getAvgPoints(ALL_GAMES));
        features.add(homeSeason.getAvgPointsOverLastXGames(ALL_GAMES, NUMB_PREV_GAMES));
        features.add(awaySeason.getFormGoalsFor(ALL_GAMES));
        features.add(homeSeason.getAvgXGF(ALL_GAMES));
        features.add(awaySeason.getWeightedAvgXGF(ONLY_AWAY_GAMES));
        features.add(homeSeason.getAvgPointsOverLastXGames(ONLY_HOME_GAMES, NUMB_PREV_GAMES));
        features.add(awaySeason.getAvgXGA(ALL_GAMES)); //20

        //TODO: Make up to 30
        features.add(awaySeason.getWeightedAvgXGF(ALL_GAMES));


        return features;
    }

    public static ArrayList<Double> getFeaturesExtendedFromOctave(TrainingTeam homeTeam, TrainingTeamsSeason homeSeason,
                                                TrainingTeam awayTeam, TrainingTeamsSeason awaySeason,
                                                ArrayList<String> homePlayersNames, ArrayList<String> awayPlayersNames,
                                                int seasonYearStart, int result) {
        int LAST_5 = 5;
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
        features.add(homeSeason.getAvgPointsOverLastXGames(ALL_GAMES, LAST_5));
        features.add(homeSeason.getAvgPointsWhenScoredFirst(ALL_GAMES));
        features.add(homeSeason.getAvgPointsWhenConceededFirst(ALL_GAMES));
        features.add(homeTeam.getPointsOfLastMatchups(awayTeam.getTeamName(), ALL_GAMES, homeSeason.getSeasonYearStart()-2));
        features.add(homeSeason.getMinsWeightedLineupRating(ALL_GAMES, homePlayersNames));
        features.add(homeSeason.getLineupStrength(ALL_GAMES, homePlayersNames));

        //home@home
        features.add(homeSeason.getAvgGoalsFor(ONLY_HOME_GAMES));
        features.add(homeSeason.getAvgGoalsAgainst(ONLY_HOME_GAMES));
        features.add(homeSeason.getAvgXGF(ONLY_HOME_GAMES));
        features.add(homeSeason.getAvgXGA(ONLY_HOME_GAMES));
        features.add(homeSeason.getWeightedAvgXGF(ONLY_HOME_GAMES));
        features.add(homeSeason.getWeightedAvgXGA(ONLY_HOME_GAMES));
        features.add(homeSeason.getAvgPoints(ONLY_HOME_GAMES));
        features.add(homeSeason.getAvgPointsOverLastXGames(ONLY_HOME_GAMES, LAST_5));
        features.add(homeSeason.getAvgPointsWhenScoredFirst(ONLY_HOME_GAMES));
        features.add(homeSeason.getAvgPointsWhenConceededFirst(ONLY_HOME_GAMES));
        features.add(homeTeam.getPointsOfLastMatchups(awayTeam.getTeamName(), ONLY_HOME_GAMES, homeSeason.getSeasonYearStart()-2));
        features.add(homeSeason.getMinsWeightedLineupRating(ONLY_HOME_GAMES, homePlayersNames));
        features.add(homeSeason.getLineupStrength(ONLY_HOME_GAMES, homePlayersNames));

        //away
        features.add(awaySeason.getAvgGoalsFor(ALL_GAMES));
        features.add(awaySeason.getAvgGoalsAgainst(ALL_GAMES));
        features.add(awaySeason.getAvgXGF(ALL_GAMES));
        features.add(awaySeason.getAvgXGA(ALL_GAMES));
        features.add(awaySeason.getWeightedAvgXGF(ALL_GAMES));
        features.add(awaySeason.getWeightedAvgXGA(ALL_GAMES));
        features.add(awaySeason.getAvgPoints(ALL_GAMES));
        features.add(awaySeason.getAvgPointsOverLastXGames(ALL_GAMES, 5));
        features.add(awaySeason.getAvgPointsWhenScoredFirst(ALL_GAMES));
        features.add(awaySeason.getAvgPointsWhenConceededFirst(ALL_GAMES));
        features.add(awayTeam.getPointsOfLastMatchups(homeTeam.getTeamName(), ALL_GAMES, awaySeason.getSeasonYearStart()-2));
        features.add(awaySeason.getMinsWeightedLineupRating(ALL_GAMES, awayPlayersNames));
        features.add(awaySeason.getLineupStrength(ALL_GAMES, awayPlayersNames));

        //away@away
        features.add(awaySeason.getAvgGoalsFor(ONLY_AWAY_GAMES));
        features.add(awaySeason.getAvgGoalsAgainst(ONLY_AWAY_GAMES));
        features.add(awaySeason.getAvgXGF(ONLY_AWAY_GAMES));
        features.add(awaySeason.getAvgXGA(ONLY_AWAY_GAMES));
        features.add(awaySeason.getWeightedAvgXGF(ONLY_AWAY_GAMES));
        features.add(awaySeason.getWeightedAvgXGA(ONLY_AWAY_GAMES));
        features.add(awaySeason.getAvgPoints(ONLY_AWAY_GAMES));
        features.add(awaySeason.getAvgPointsOverLastXGames(ONLY_AWAY_GAMES, 5));
        features.add(awaySeason.getAvgPointsWhenScoredFirst(ONLY_AWAY_GAMES));
        features.add(awaySeason.getAvgPointsWhenConceededFirst(ONLY_AWAY_GAMES));
        features.add(awayTeam.getPointsOfLastMatchups(homeTeam.getTeamName(), ONLY_AWAY_GAMES, awaySeason.getSeasonYearStart()-2));
        features.add(awaySeason.getMinsWeightedLineupRating(ONLY_AWAY_GAMES, awayPlayersNames));
        features.add(awaySeason.getLineupStrength(ONLY_AWAY_GAMES, awayPlayersNames));

        double homeTeamTotalGF = homeSeason.getAvgGoalsFor(ALL_GAMES);
        double awayTeamTotalLineupRating = awaySeason.getMinsWeightedLineupRating(ALL_GAMES, awayPlayersNames);
        double awayTeamTotalXGF = awaySeason.getAvgXGF(ALL_GAMES);
        double awayTeamAwayPts = awaySeason.getAvgPointsOverLastXGames(ALL_GAMES, 5);
        double homeTeamTotalXGF = homeSeason.getAvgXGF(ALL_GAMES);
        double homeTeamHomeGF = homeSeason.getAvgXGF(ONLY_HOME_GAMES);
        double homeTeamTotalLineupRating = homeSeason.getMinsWeightedLineupRating(ONLY_HOME_GAMES, homePlayersNames);
        double awayTeamTotalXGA = awaySeason.getAvgXGA(ALL_GAMES);
        double homeTeamHomePts = homeSeason.getAvgPoints(ONLY_HOME_GAMES);
        double homeTeamWeightedXGA = homeSeason.getWeightedAvgXGA(ALL_GAMES);
        //10
        double awayTeamAwayWeightedXGF = awaySeason.getWeightedAvgXGF(ALL_GAMES);
        double awayTeamAvgGF = awaySeason.getAvgGoalsFor(ALL_GAMES);
        double homeTeamHomeXGF = homeSeason.getAvgXGF(ALL_GAMES);

        double[] toCombine = new double[]{homeTeamTotalGF, awayTeamTotalLineupRating, awayTeamTotalXGF, awayTeamAwayPts, homeTeamTotalXGF, homeTeamHomeGF,
                homeTeamTotalLineupRating, awayTeamTotalXGA, homeTeamHomePts, homeTeamWeightedXGA, awayTeamAwayWeightedXGF, awayTeamAvgGF, homeTeamHomeXGF};
        ArrayList<Double> newFeatures = combineFeatures(toCombine);
        features.addAll(newFeatures);

        return features;
    }

    /*
     * Used by writing methods to create powered features. Also used when creating matches to predict to create the powered features.
     * Important to do this within 1 method so we get the same variations every time.
     */
    private static ArrayList<Double> combineFeatures (double[] toCombine) {
        ArrayList<Double> newFeatures = new ArrayList<>();

        for (int j = 0; j<toCombine.length; j++) {
            //we want to go through the other numbs and
            double x = toCombine[j];
            double x2 = Math.pow(toCombine[j],2);
            double x3 = Math.pow(x,3);
            newFeatures.add(Math.pow(x,4));
            newFeatures.add(x3);
            newFeatures.add(x2);
            for (int k = j+1; k<toCombine.length; k++) {
                double xk = x * toCombine[k];
                double x2k = x2 * toCombine[k];
                double xk2 = x * Math.pow(toCombine[k],2);
                newFeatures.add(xk);
                newFeatures.add(x2k);
                newFeatures.add(xk2);
                newFeatures.add(x3 * toCombine[k]);
                newFeatures.add(x2 * Math.pow(toCombine[k],2));
                for (int l = k+1; l<toCombine.length; l++) {
                    double xkl = xk * toCombine[l];
                    newFeatures.add(x2k * toCombine[l]);
                    newFeatures.add(xk2 * toCombine[l]);
                    newFeatures.add(xkl);
                    newFeatures.add(xk * Math.pow(toCombine[l],2));
                    for (int m = l+1; m<toCombine.length; m++) {
                        newFeatures.add(xkl * toCombine[m]);
                    }
                }
            }
        }

        return newFeatures;
    }
}
