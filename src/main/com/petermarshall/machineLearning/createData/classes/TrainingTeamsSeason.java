package com.petermarshall.machineLearning.createData.classes;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

//contains metrics that can be used to create the features for a match to predict.
//huge range of fields to allow for choice when creating features & training model
public class TrainingTeamsSeason {
    private int seasonYearStart;
    private int homeGamesPlayed = 0;
    private int awayGamesPlayed = 0;

    //Using arrays here so if we want to we can calculate fields for last 5 games etc.
    //Not done the same for weighted xG because that's already been taken into account (bc it's weighted)
    private ArrayList<HomeAwayWrapper> goalsFor;
    private ArrayList<HomeAwayWrapper> goalsAgainst;
    private ArrayList<HomeAwayWrapper> xGF;
    private ArrayList<HomeAwayWrapper> xGA;
    private ArrayList<HomeAwayWrapper> points;
    private ArrayList<HomeAwayWrapper> pointsScoredFirst;
    private ArrayList<HomeAwayWrapper> pointsConceededFirst;
    private ArrayList<HomeAwayWrapper> totalPointsPerGameOfOpponentsWholeSeason; //can be used to calculate the difficulty of opponents a team has faced over the season. contains opponents total PPG
    private ArrayList<HomeAwayWrapper> homeAwayPointsPerGameOfOpponentsWholeSeason; //contains opponents Home/Away PPG.
    private ArrayList<HomeAwayWrapper> totalPointsPerGameOfOpponentsLast5;
    private ArrayList<HomeAwayWrapper> homeAwayPointsPerGameOfOpponentsLast5;

    //default values. calculated over 7718 games in db at end of 18/19 season.
    public static final double AVG_GOALS_PER_GAME = 1.34;
    public static final double AVG_XG_PER_GAME = 1.19;
    public static final double AVG_PPG = 1.25;
    public static double AVG_CLEAN_SHEETS_PER_GAME = 0.29;
    public static final double AVG_RATING_PER_GAME = 6.87;
    public static final double AVG_MINS_RATING_PER_GAME = 6.93;
    private static final int ADD_DEFAULT_VALUE_UNTIL_N_LENGTH = 3;

    //Exponential weighting placing more weight on recent results.
    private double totalWeightedXGF = AVG_XG_PER_GAME;
    private double homeWeightedXGF = AVG_XG_PER_GAME;
    private double awayWeightedXGF = AVG_XG_PER_GAME;
    private double totalWeightedXGA = AVG_XG_PER_GAME;
    private double homeWeightedXGA = AVG_XG_PER_GAME;
    private double awayWeightedXGA = AVG_XG_PER_GAME;

    //compares how many more xG were achieved against the oppositions xGA
    //will be calculated by taking a teams actual xG and comparing against the oppositions average xGA to see how much they overperformed.
    private double totalFormXGF = 0;
    private double homeFormXGF = 0;
    private double awayFormXGF = 0;
    private double totalFormXGA = 0;
    private double homeFormXGA = 0;
    private double awayFormXGA = 0;
    private ArrayList<HomeAwayWrapper> totalFormXGFHistory = new ArrayList<>();
    private ArrayList<HomeAwayWrapper> totalFormXGAHistory = new ArrayList<>();

    //Form weighted says how many more xG the team got against what the opponents recent weighted xG suggested they should.
    //will be calculated by using a teams actual xGF and seeing how they performed against the oppositions weighted xGA.
    //initialised to be that each team scored/conceeded the exact amount of goals that the other team's stats expected them to conceede. (0 goals more than expected)
    private double totalFormWeightedXGF = 0;
    private double homeFormWeightedXGF = 0;
    private double awayFormWeightedXGF = 0;
    private double totalFormWeightedXGA = 0;
    private double homeFormWeightedXGA = 0;
    private double awayFormWeightedXGA = 0;
    private ArrayList<HomeAwayWrapper> totalFormWeightedXGFHistory = new ArrayList<>(); //arrays so we can look at the last X amount of games as an average if needed.
    private ArrayList<HomeAwayWrapper> totalFormWeightedXGAHistory = new ArrayList<>();

    //Exponential weighting placing more weight on recent results.
    private double totalWeightedGoalsFor = AVG_GOALS_PER_GAME;
    private double homeWeightedGoalsFor = AVG_GOALS_PER_GAME;
    private double awayWeightedGoalsFor = AVG_GOALS_PER_GAME;
    private double totalWeightedGoalsAgainst = AVG_GOALS_PER_GAME;
    private double homeWeightedGoalsAgainst = AVG_GOALS_PER_GAME;
    private double awayWeightedGoalsAgainst = AVG_GOALS_PER_GAME;

    //compares how many more goals were scored against the oppositions average goals against
    //will be calculated by taking a teams actual goals and comparing against the oppositions average goals conceeded to see how much they overperformed.
    private double totalFormGoalsFor = 0;
    private double homeFormGoalsFor = 0;
    private double awayFormGoalsFor = 0;
    private double totalFormGoalsAgainst = 0;
    private double homeFormGoalsAgainst = 0;
    private double awayFormGoalsAgainst = 0;
    private ArrayList<HomeAwayWrapper> totalFormGoalsForHistory = new ArrayList<>();
    private ArrayList<HomeAwayWrapper> totalFormGoalsAgainstHistory = new ArrayList<>();

    //Form weighted says how many more xG the team got against what the opponents recent weighted xG suggested they should.
    //will be calculated by using a teams actual xGF and seeing how they performed against the oppositions weighted xGA.
    //initialised to be that each team scored/conceeded the exact amount of goals that the other team's stats expected them to conceede. (0 goals more than expected)
    private double totalFormWeightedGoalsFor = 0;
    private double homeFormWeightedGoalsFor = 0;
    private double awayFormWeightedGoalsFor = 0;
    private double totalFormWeightedGoalsAgainst = 0;
    private double homeFormWeightedGoalsAgainst = 0;
    private double awayFormWeightedGoalsAgainst = 0;
    private ArrayList<HomeAwayWrapper> totalFormWeightedGoalsForHistory = new ArrayList<>(); //arrays so we can look at the last X amount of games as an average if needed.
    private ArrayList<HomeAwayWrapper> totalFormWeightedGoalsAgainstHistory = new ArrayList<>();


    private HashMap<String, Player> playerStats; //kept as a hashmap to enable faster player updates - which will be most used operation.

    public TrainingTeamsSeason(int seasonYearStart) {
        this.seasonYearStart = seasonYearStart;
        this.goalsFor = new ArrayList<>();
        this.goalsAgainst = new ArrayList<>();
        this.xGF = new ArrayList<>();
        this.xGA = new ArrayList<>();
        this.points = new ArrayList<>();
        this.pointsScoredFirst = new ArrayList<>();
        this.pointsConceededFirst = new ArrayList<>();
        this.playerStats = new HashMap<>();
        this.totalPointsPerGameOfOpponentsWholeSeason = new ArrayList<>();
        this.homeAwayPointsPerGameOfOpponentsWholeSeason = new ArrayList<>();
        this.totalPointsPerGameOfOpponentsLast5 = new ArrayList<>();
        this.homeAwayPointsPerGameOfOpponentsLast5 = new ArrayList<>();
    }

    /*
     * UPDATING METHODS
     */

    /*
     * Method has checks to only add data if the parameters we've been given != -1. Filters out incomplete data.
     *
     * IMPORTANT: Needs to be called after the TrainingMatch has been created and values set. Otherwise when we create the TrainingMatch,
     * it will include the stats from the match that we're trying to predict. Whereas the training match needs to be
     * a predictor, so must include data from previous games only.
     *
     * Therefore we cannot just pass in the opponents TrainingTeamsSeason or we will update one and then use the updated version to update the other.
     * Temp variables are needed, hence the long argument list.
     */
    public void addGameStats(int goalsFor, int goalsAgainst, double xGF, double xGA, boolean scoredFirst, boolean hasScoredFirstData, boolean isHomeTeam, double oppositionAvgTotalGF,
                             double oppositionAvgTotalGA, double oppositionAvgHomeAwayGF, double oppositionAvgHomeAwayGA, double oppositionAvgTotalXGF, double oppositionAvgTotalXGA,
                             double oppositionAvgHomeAwayXGF, double oppositionAvgHomeAwayXGA, double oppositionWeightedTotalXGF, double oppositionWeightedTotalXGA,
                             double oppositionWeightedHomeAwayXGF, double oppositionWeightedHomeAwayXGA, double opponentTotalWholeSeasonPPG, double opponentHomeAwayWholeSeasonPPG,
                             double opponentTotalLast5PPG, double opponentHomeAwayLast5PPG, double oppositionWeightedTotalGF,
                             double oppositionWeightedTotalGA, double oppositionWeightedHomeAwayGoalsFor, double oppositionWeightedHomeAwayGoalsAgainst) {
        if (isHomeTeam) this.homeGamesPlayed++;
        else this.awayGamesPlayed++;

        if (goalsFor != -1 && goalsAgainst != -1) {
            //goals
            this.goalsFor.add(new HomeAwayWrapper(isHomeTeam, goalsFor));
            this.goalsAgainst.add(new HomeAwayWrapper(isHomeTeam, goalsAgainst));
            this.totalWeightedGoalsFor = calcExponWeightedAvg(this.totalWeightedGoalsFor, goalsFor);
            this.totalWeightedGoalsAgainst = calcExponWeightedAvg(this.totalWeightedGoalsAgainst, goalsAgainst);
            if (isHomeTeam) {
                this.homeWeightedGoalsFor = calcExponWeightedAvg(this.homeWeightedGoalsFor, goalsFor);
                this.homeWeightedGoalsAgainst = calcExponWeightedAvg(this.homeWeightedGoalsAgainst, goalsAgainst);
            } else {
                this.awayWeightedGoalsFor = calcExponWeightedAvg(this.awayWeightedGoalsFor, goalsFor);
                this.awayWeightedGoalsAgainst = calcExponWeightedAvg(this.awayWeightedGoalsAgainst, goalsAgainst);
            }

            this.totalFormGoalsForHistory.add(new HomeAwayWrapper(isHomeTeam, goalsFor - oppositionAvgTotalGA));
            this.totalFormGoalsAgainstHistory.add(new HomeAwayWrapper(isHomeTeam, goalsAgainst - oppositionAvgTotalGF));
            this.totalFormWeightedGoalsForHistory.add(new HomeAwayWrapper(isHomeTeam, goalsFor - oppositionWeightedTotalGA));
            this.totalFormWeightedGoalsAgainstHistory.add(new HomeAwayWrapper(isHomeTeam, goalsAgainst - oppositionWeightedTotalGF));

            this.totalFormGoalsFor = calcExponWeightedAvg(this.totalFormGoalsFor, goalsFor - oppositionAvgTotalGA);
            this.totalFormGoalsAgainst = calcExponWeightedAvg(this.totalFormGoalsAgainst, goalsAgainst - oppositionAvgTotalGF);
            this.totalFormWeightedGoalsFor = calcExponWeightedAvg(this.totalFormWeightedGoalsFor, goalsFor - oppositionWeightedTotalGA);
            this.totalFormWeightedGoalsAgainst = calcExponWeightedAvg(this.totalFormWeightedGoalsAgainst, goalsAgainst - oppositionWeightedTotalGF);
            if (isHomeTeam) {
                this.homeFormGoalsFor = calcExponWeightedAvg(this.homeFormGoalsFor, goalsFor - oppositionAvgHomeAwayGA);
                this.homeFormGoalsAgainst = calcExponWeightedAvg(this.homeFormGoalsAgainst, goalsAgainst - oppositionAvgHomeAwayGF);
                this.homeFormWeightedGoalsFor = calcExponWeightedAvg(this.homeFormWeightedGoalsFor, goalsFor - oppositionWeightedHomeAwayGoalsAgainst);
                this.homeFormWeightedGoalsAgainst = calcExponWeightedAvg(this.homeFormWeightedGoalsAgainst, goalsAgainst - oppositionWeightedHomeAwayGoalsFor);
            } else {
                this.awayFormGoalsFor = calcExponWeightedAvg(this.awayFormGoalsFor, goalsFor - oppositionAvgHomeAwayGA);
                this.awayFormGoalsAgainst = calcExponWeightedAvg(this.awayFormGoalsAgainst, goalsAgainst - oppositionAvgHomeAwayGF);
                this.awayFormWeightedGoalsFor = calcExponWeightedAvg(this.awayFormWeightedGoalsFor, goalsFor - oppositionWeightedHomeAwayGoalsAgainst);
                this.awayFormWeightedGoalsAgainst = calcExponWeightedAvg(this.awayFormWeightedGoalsAgainst, goalsAgainst - oppositionWeightedHomeAwayGoalsFor);
            }

            //points
            HomeAwayWrapper points = new HomeAwayWrapper(isHomeTeam, goalsFor > goalsAgainst ? 3 : goalsFor == goalsAgainst ? 1 : 0);
            this.points.add(points);
            if (hasScoredFirstData && (goalsFor > 0 || goalsAgainst > 0)) {
                if (scoredFirst) this.pointsScoredFirst.add(points);
                else this.pointsConceededFirst.add(points);
            }
            this.totalPointsPerGameOfOpponentsWholeSeason.add(new HomeAwayWrapper(isHomeTeam, opponentTotalWholeSeasonPPG));
            this.homeAwayPointsPerGameOfOpponentsWholeSeason.add(new HomeAwayWrapper(isHomeTeam, opponentHomeAwayWholeSeasonPPG));
            this.totalPointsPerGameOfOpponentsLast5.add(new HomeAwayWrapper(isHomeTeam, opponentTotalLast5PPG));
            this.homeAwayPointsPerGameOfOpponentsLast5.add(new HomeAwayWrapper(isHomeTeam, opponentHomeAwayLast5PPG));
        }

        //xG
        if (xGF != -1 && xGA != -1) {
            this.xGF.add(new HomeAwayWrapper(isHomeTeam, xGF));
            this.xGA.add(new HomeAwayWrapper(isHomeTeam, xGA));
            this.totalWeightedXGF = calcExponWeightedAvg(this.totalWeightedXGF, xGF);
            this.totalWeightedXGA = calcExponWeightedAvg(this.totalWeightedXGA, xGA);
            if (isHomeTeam) {
                this.homeWeightedXGF = calcExponWeightedAvg(this.homeWeightedXGF, xGF);
                this.homeWeightedXGA = calcExponWeightedAvg(this.homeWeightedXGA, xGA);
            } else {
                this.awayWeightedXGF = calcExponWeightedAvg(this.awayWeightedXGF, xGF);
                this.awayWeightedXGA = calcExponWeightedAvg(this.awayWeightedXGA, xGA);
            }
            //how many more xG we had compared to how many the oppositions form dictated we should have had. A measure of how good team is compared to other teams opposition has faced.
            //all calculated with exponentially weighted averages to include overperformance over the whole season, but place more weight ont he most recent games.
            this.totalFormXGFHistory.add(new HomeAwayWrapper(isHomeTeam, xGF - oppositionAvgTotalXGA));
            this.totalFormXGAHistory.add(new HomeAwayWrapper(isHomeTeam, xGA - oppositionAvgTotalXGF));
            this.totalFormWeightedXGFHistory.add(new HomeAwayWrapper(isHomeTeam, xGF - oppositionWeightedTotalXGA));
            this.totalFormWeightedXGAHistory.add(new HomeAwayWrapper(isHomeTeam, xGA - oppositionWeightedTotalXGF));

            this.totalFormXGF = calcExponWeightedAvg(this.totalFormXGF, xGF - oppositionAvgTotalXGA);
            this.totalFormXGA = calcExponWeightedAvg(this.totalFormXGA, xGA - oppositionAvgTotalXGF);
            this.totalFormWeightedXGF = calcExponWeightedAvg(this.totalFormWeightedXGF, xGF - oppositionWeightedTotalXGA);
            this.totalFormWeightedXGA = calcExponWeightedAvg(this.totalFormWeightedXGA, xGA - oppositionWeightedTotalXGF);

            if (isHomeTeam) {
                this.homeFormXGF = calcExponWeightedAvg(this.homeFormXGF, xGF - oppositionAvgHomeAwayXGA);
                this.homeFormXGA = calcExponWeightedAvg(this.homeFormXGA, xGA - oppositionAvgHomeAwayXGF);
                this.homeFormWeightedXGF = calcExponWeightedAvg(this.homeFormWeightedXGF, xGF - oppositionWeightedHomeAwayXGA);
                this.homeFormWeightedXGA = calcExponWeightedAvg(this.homeFormWeightedXGA, xGA - oppositionWeightedHomeAwayXGF);
            } else {
                this.awayFormXGF = calcExponWeightedAvg(this.awayFormXGF, xGF - oppositionAvgHomeAwayXGA);
                this.awayFormXGA = calcExponWeightedAvg(this.awayFormXGA, xGA - oppositionAvgHomeAwayXGF);
                this.awayFormWeightedXGF = calcExponWeightedAvg(this.awayFormWeightedXGF, xGF - oppositionWeightedHomeAwayXGA);
                this.awayFormWeightedXGA = calcExponWeightedAvg(this.awayFormWeightedXGA, xGA - oppositionWeightedHomeAwayXGF);
            }
        }
    }

    public void addPlayerStats(String playerName, int minsPlayed, double rating, boolean homeTeam) {
        Player player = this.playerStats.getOrDefault(playerName, null);
        if (player == null) {
            this.playerStats.put(playerName, new Player(playerName, minsPlayed, rating, homeTeam));
        }
        else {
            player.addMatchMinsRating(minsPlayed, rating, homeTeam);
        }
    }

    // Method will add the last 5 records from the arrays of the last season and also update any values with the values from last season.
    public void copyStatsFromPreviousSeason(TrainingTeamsSeason lastSeason) {
        if (lastSeason == null) return;

        final int NUMB_GAMES_TO_COPY = 5;

        this.goalsFor.addAll(lastSeason.goalsFor.subList(lastSeason.goalsFor.size() - NUMB_GAMES_TO_COPY, lastSeason.goalsFor.size()));
        this.goalsAgainst.addAll(lastSeason.goalsAgainst.subList(lastSeason.goalsAgainst.size() - NUMB_GAMES_TO_COPY, lastSeason.goalsAgainst.size()));
        this.xGF.addAll(lastSeason.xGF.subList(lastSeason.xGF.size() - NUMB_GAMES_TO_COPY, lastSeason.xGF.size()));
        this.xGA.addAll(lastSeason.xGA.subList(lastSeason.xGA.size() - NUMB_GAMES_TO_COPY, lastSeason.xGA.size()));
        this.points.addAll(lastSeason.points.subList(lastSeason.points.size() - NUMB_GAMES_TO_COPY, lastSeason.points.size()));
        this.pointsConceededFirst.addAll(lastSeason.pointsConceededFirst.subList(lastSeason.pointsConceededFirst.size() - NUMB_GAMES_TO_COPY, lastSeason.pointsConceededFirst.size()));
        this.pointsScoredFirst.addAll(lastSeason.pointsScoredFirst.subList(lastSeason.pointsScoredFirst.size() - NUMB_GAMES_TO_COPY, lastSeason.pointsScoredFirst.size()));
        this.totalPointsPerGameOfOpponentsWholeSeason.addAll(lastSeason.totalPointsPerGameOfOpponentsWholeSeason.subList(lastSeason.totalPointsPerGameOfOpponentsWholeSeason.size() - NUMB_GAMES_TO_COPY, lastSeason.totalPointsPerGameOfOpponentsWholeSeason.size()));
        this.homeAwayPointsPerGameOfOpponentsWholeSeason.addAll(lastSeason.homeAwayPointsPerGameOfOpponentsWholeSeason.subList(lastSeason.homeAwayPointsPerGameOfOpponentsWholeSeason.size() - NUMB_GAMES_TO_COPY, lastSeason.homeAwayPointsPerGameOfOpponentsWholeSeason.size()));
        this.totalPointsPerGameOfOpponentsLast5.addAll(lastSeason.totalPointsPerGameOfOpponentsLast5.subList(lastSeason.totalPointsPerGameOfOpponentsLast5.size() - NUMB_GAMES_TO_COPY, lastSeason.totalPointsPerGameOfOpponentsLast5.size()));
        this.homeAwayPointsPerGameOfOpponentsLast5.addAll(lastSeason.homeAwayPointsPerGameOfOpponentsLast5.subList(lastSeason.homeAwayPointsPerGameOfOpponentsLast5.size() - NUMB_GAMES_TO_COPY, lastSeason.homeAwayPointsPerGameOfOpponentsLast5.size()));

        this.totalWeightedXGF = lastSeason.totalWeightedXGF;
        this.homeWeightedXGF = lastSeason.homeWeightedXGF;
        this.awayWeightedXGF = lastSeason.awayWeightedXGF;
        this.totalWeightedXGA = lastSeason.totalWeightedXGA;
        this.homeWeightedXGA = lastSeason.homeWeightedXGA;
        this.awayWeightedXGA = lastSeason.awayWeightedXGA;

        this.totalFormXGF = lastSeason.totalFormXGF;
        this.homeFormXGF = lastSeason.homeFormXGF;
        this.awayFormXGF = lastSeason.awayFormXGF;
        this.totalFormXGA = lastSeason.totalFormXGA;
        this.homeFormXGA = lastSeason.homeFormXGA;
        this.awayFormXGA = lastSeason.awayFormXGA;
        this.totalFormXGFHistory.addAll(lastSeason.totalFormXGFHistory.subList(lastSeason.totalFormXGFHistory.size() - NUMB_GAMES_TO_COPY, lastSeason.totalFormXGFHistory.size()));
        this.totalFormXGAHistory.addAll(lastSeason.totalFormXGAHistory.subList(lastSeason.totalFormXGAHistory.size() - NUMB_GAMES_TO_COPY, lastSeason.totalFormXGAHistory.size()));

        this.totalFormWeightedXGF = lastSeason.totalFormWeightedXGF;
        this.homeFormWeightedXGF = lastSeason.homeFormWeightedXGF;
        this.awayFormWeightedXGF = lastSeason.awayFormWeightedXGF;
        this.totalFormWeightedXGA = lastSeason.totalFormWeightedXGA;
        this.homeFormWeightedXGA = lastSeason.homeFormWeightedXGA;
        this.awayFormWeightedXGA = lastSeason.awayFormWeightedXGA;
        this.totalFormWeightedXGFHistory.addAll(lastSeason.totalFormWeightedXGFHistory.subList(lastSeason.totalFormWeightedXGFHistory.size() - NUMB_GAMES_TO_COPY, lastSeason.totalFormWeightedXGFHistory.size()));
        this.totalFormWeightedXGAHistory.addAll(lastSeason.totalFormWeightedXGAHistory.subList(lastSeason.totalFormWeightedXGAHistory.size() - NUMB_GAMES_TO_COPY, lastSeason.totalFormWeightedXGAHistory.size()));

        this.totalWeightedGoalsFor = lastSeason.totalWeightedGoalsFor;
        this.homeWeightedGoalsFor = lastSeason.homeWeightedGoalsFor;
        this.awayWeightedGoalsFor = lastSeason.awayWeightedGoalsFor;
        this.totalWeightedGoalsAgainst = lastSeason.totalWeightedGoalsAgainst;
        this.homeWeightedGoalsAgainst = lastSeason.homeWeightedGoalsAgainst;
        this.awayWeightedGoalsAgainst = lastSeason.awayWeightedGoalsAgainst;

        this.totalFormGoalsFor = lastSeason.totalFormGoalsFor;
        this.homeFormGoalsFor = lastSeason.homeFormGoalsFor;
        this.awayFormGoalsFor = lastSeason.awayFormGoalsFor;
        this.totalFormGoalsAgainst = lastSeason.totalFormGoalsAgainst;
        this.homeFormGoalsAgainst = lastSeason.homeFormGoalsAgainst;
        this.awayFormGoalsAgainst = lastSeason.awayFormGoalsAgainst;
        this.totalFormGoalsForHistory.addAll(lastSeason.totalFormGoalsForHistory.subList(lastSeason.totalFormGoalsForHistory.size() - NUMB_GAMES_TO_COPY, lastSeason.totalFormGoalsForHistory.size()));
        this.totalFormGoalsAgainstHistory.addAll(lastSeason.totalFormGoalsAgainstHistory.subList(lastSeason.totalFormGoalsAgainstHistory.size() - NUMB_GAMES_TO_COPY, lastSeason.totalFormGoalsAgainstHistory.size()));

        this.totalFormWeightedGoalsFor = lastSeason.totalFormWeightedGoalsFor;
        this.homeFormWeightedGoalsFor = lastSeason.homeFormWeightedGoalsFor;
        this.awayFormWeightedGoalsFor = lastSeason.awayFormWeightedGoalsFor;
        this.totalFormWeightedGoalsAgainst = lastSeason.totalFormWeightedGoalsAgainst;
        this.homeFormWeightedGoalsAgainst = lastSeason.homeFormWeightedGoalsAgainst;
        this.awayFormWeightedGoalsAgainst = lastSeason.awayFormWeightedGoalsAgainst;
        this.totalFormWeightedGoalsForHistory.addAll(lastSeason.totalFormWeightedGoalsForHistory.subList(lastSeason.totalFormWeightedGoalsForHistory.size() - NUMB_GAMES_TO_COPY, lastSeason.totalFormWeightedGoalsForHistory.size()));
        this.totalFormWeightedGoalsAgainstHistory.addAll(lastSeason.totalFormWeightedGoalsAgainstHistory.subList(lastSeason.totalFormWeightedGoalsAgainstHistory.size() - NUMB_GAMES_TO_COPY, lastSeason.totalFormWeightedGoalsAgainstHistory.size()));
    }

    /*
     * GETTERS
     */

    public int getNumbGamesPlayed() {
        return this.homeGamesPlayed + this.awayGamesPlayed;
    }
    public int getNumbGamesPlayed(GamesSelector gamesSelector) {
        if (gamesSelector.getSetting() == 1) return this.homeGamesPlayed;
        else if (gamesSelector.getSetting() == 2) return this.awayGamesPlayed;
        else return this.getNumbGamesPlayed();
    }

    public double getAvgGoalsFor(GamesSelector gamesSelector) {
        return calcHomeAwayAvg(this.goalsFor, gamesSelector.getSetting(), AVG_GOALS_PER_GAME);
    }

    public double getAvgGoalsForLastXGames(GamesSelector gamesSelector, int lastXGames) {
        ArrayList<HomeAwayWrapper> lastXGoalsFor = getLastNRecords(gamesSelector, this.goalsFor, lastXGames);
        return calcHomeAwayAvg(lastXGoalsFor, gamesSelector.getSetting(), AVG_GOALS_PER_GAME, lastXGames);
    }

    public double getAvgGoalsAgainst(GamesSelector gamesSelector) {
        return calcHomeAwayAvg(this.goalsAgainst, gamesSelector.getSetting(), AVG_GOALS_PER_GAME);
    }

    public double getAvgGoalsAgainstLastXGames(GamesSelector gamesSelector, int lastXGames) {
        ArrayList<HomeAwayWrapper> lastXGoalsAgainst = getLastNRecords(gamesSelector, this.goalsAgainst, lastXGames);
        return calcHomeAwayAvg(lastXGoalsAgainst, gamesSelector.getSetting(), AVG_GOALS_PER_GAME, lastXGames);
    }

    public double getWeightedAvgGoalsFor(GamesSelector gamesSelector) {
        if (gamesSelector.getSetting() == 3) return this.totalWeightedGoalsFor;
        else if (gamesSelector.getSetting() == 1) return this.homeWeightedGoalsFor;
        else return this.awayWeightedGoalsFor;
    }

    public double getWeightedAvgGoalsAgainst(GamesSelector gamesSelector) {
        if (gamesSelector.getSetting() == 3) return this.totalWeightedGoalsAgainst;
        else if (gamesSelector.getSetting() == 1) return this.homeWeightedGoalsAgainst;
        else return this.awayWeightedGoalsAgainst;
    }

    public double getAvgXGF(GamesSelector gamesSelector) {
        return calcHomeAwayAvg(this.xGF, gamesSelector.getSetting(), AVG_XG_PER_GAME);
    }

    public double getAvgXGFOverLastXGames(GamesSelector gamesSelector, int lastXGames) {
        ArrayList<HomeAwayWrapper> lastNRecords = getLastNRecords(gamesSelector, this.xGF, lastXGames);
        return calcHomeAwayAvg(lastNRecords, gamesSelector.getSetting(), AVG_GOALS_PER_GAME, lastXGames);
    }

    public double getAvgXGA(GamesSelector gamesSelector) {
        return calcHomeAwayAvg(this.xGA, gamesSelector.getSetting(), AVG_XG_PER_GAME);
    }

    public double getAvgXGAOverLastXGames(GamesSelector gamesSelector, int lastXGames) {
        ArrayList<HomeAwayWrapper> lastNRecords = getLastNRecords(gamesSelector, this.xGA, lastXGames);
        return calcHomeAwayAvg(lastNRecords, gamesSelector.getSetting(), AVG_GOALS_PER_GAME, lastXGames);
    }

    public double getWeightedAvgXGF (GamesSelector gamesSelector) {
        if (gamesSelector.getSetting() == 3) return this.totalWeightedXGF;
        else if (gamesSelector.getSetting() == 1) return this.homeWeightedXGF;
        else return this.awayWeightedXGF;
    }

    public double getWeightedAvgXGA (GamesSelector gamesSelector) {
        if (gamesSelector.getSetting() == 3) return this.totalWeightedXGA;
        else if (gamesSelector.getSetting() == 1) return this.homeWeightedXGA;
        else return this.awayWeightedXGA;
    }

    public double getFormGoalsFor(GamesSelector gamesSelector) {
        if (gamesSelector.getSetting() == 3) return this.totalFormGoalsFor;
        else if (gamesSelector.getSetting() == 1) return this.homeFormGoalsFor;
        else return this.awayFormGoalsFor;
    }

    public double getFormGoalsAgainst(GamesSelector gamesSelector) {
        if (gamesSelector.getSetting() == 3) return this.totalFormGoalsAgainst;
        else if (gamesSelector.getSetting() == 1) return this.homeFormGoalsAgainst;
        else return this.awayFormGoalsAgainst;
    }

    public double getFormXGF(GamesSelector gamesSelector) {
        if (gamesSelector.getSetting() == 3) return this.totalFormXGF;
        else if (gamesSelector.getSetting() == 1) return this.homeFormXGF;
        else return this.awayFormXGF;
    }

    public double getFormXGA(GamesSelector gamesSelector) {
        if (gamesSelector.getSetting() == 3) return this.totalFormXGA;
        else if (gamesSelector.getSetting() == 1) return this.homeFormXGA;
        else return this.awayFormXGA;
    }

    public double getFormWeightedXGF(GamesSelector gamesSelector) {
        if (gamesSelector.getSetting() == 3) return this.totalFormWeightedXGF;
        else if (gamesSelector.getSetting() == 1) return this.homeFormWeightedXGF;
        else return this.awayFormWeightedXGF;
    }

    public double getFormWeightedXGA(GamesSelector gamesSelector) {
        if (gamesSelector.getSetting() == 3) return this.totalFormWeightedXGA;
        else if (gamesSelector.getSetting() == 1) return this.homeFormWeightedXGA;
        else return this.awayFormWeightedXGA;
    }

    public double getFormWeightedGoalsFor(GamesSelector gamesSelector) {
        if (gamesSelector.getSetting() == 3) return this.totalFormWeightedGoalsFor;
        else if (gamesSelector.getSetting() == 1) return this.homeFormWeightedGoalsFor;
        else return this.awayFormWeightedGoalsFor;
    }

    public double getFormWeightedGoalsAgainst(GamesSelector gamesSelector) {
        if (gamesSelector.getSetting() == 3) return this.totalFormWeightedGoalsAgainst;
        else if (gamesSelector.getSetting() == 1) return this.homeFormWeightedGoalsAgainst;
        else return this.awayFormWeightedGoalsAgainst;
    }

    public double getAvgFormGoalsFor(GamesSelector gamesSelector, int removeFirstNGames) {
        return calcHomeAwayAvg(removeFirstNRecordsOfGroup(this.totalFormGoalsForHistory, removeFirstNGames), gamesSelector.getSetting(), 0);
        //default value for how many more xGF they got than expected... 0.
    }

    public double getAvgFormGoalsAgainst(GamesSelector gamesSelector, int removeFirstNGames) {
        return calcHomeAwayAvg(removeFirstNRecordsOfGroup(this.totalFormGoalsAgainstHistory, removeFirstNGames), gamesSelector.getSetting(), 0);
    }

    public double getAvgFormXGF(GamesSelector gamesSelector, int removeFirstNGames) {
        return calcHomeAwayAvg(removeFirstNRecordsOfGroup(this.totalFormXGFHistory, removeFirstNGames), gamesSelector.getSetting(), 0);
    }

    public double getAvgFormXGA(GamesSelector gamesSelector, int removeFirstNGames) {
        return calcHomeAwayAvg(removeFirstNRecordsOfGroup(this.totalFormXGAHistory, removeFirstNGames), gamesSelector.getSetting(), 0);
    }

    public double getAvgFormWeightedXGF (GamesSelector gamesSelector, int removeFirstNGames) {
        return calcHomeAwayAvg(removeFirstNRecordsOfGroup(this.totalFormWeightedXGFHistory, removeFirstNGames), gamesSelector.getSetting(), 0);
    }

    public double getAvgFormWeightedXGA (GamesSelector gamesSelector, int removeFirstNGames) {
        return calcHomeAwayAvg(removeFirstNRecordsOfGroup(this.totalFormWeightedXGAHistory, removeFirstNGames), gamesSelector.getSetting(), 0);
    }

    public double getFormXGFOverLastNGames(GamesSelector gamesSelector, int numbPreviousGames) {
        ArrayList<HomeAwayWrapper> lastNGamesXGF = this.getLastNRecords(gamesSelector, this.totalFormXGFHistory, numbPreviousGames);
        return calcHomeAwayAvg(lastNGamesXGF, gamesSelector.getSetting(), 0);
    }

    public double getFormXGAOverLastNGames(GamesSelector gamesSelector, int numbPreviousGames) {
        ArrayList<HomeAwayWrapper> lastNGamesXGA = this.getLastNRecords(gamesSelector, this.totalFormXGAHistory, numbPreviousGames);
        return calcHomeAwayAvg(lastNGamesXGA, gamesSelector.getSetting(), 0);
    }

    public ArrayList<HomeAwayWrapper> getLastNRecords(GamesSelector gamesSelector, ArrayList<HomeAwayWrapper> source, int numbPreviousGames) {
        ArrayList<HomeAwayWrapper> lastNRecords = new ArrayList<>();
        for (int i = source.size() - 1, count = 0; i >= 0 && count < numbPreviousGames; i--) {
            HomeAwayWrapper currRecord = source.get(i);
            if (gamesSelector.getSetting() == 3 ||
                    (gamesSelector.getSetting() == 1 && currRecord.isHome()) ||
                    (gamesSelector.getSetting() == 2 && !currRecord.isHome())) {
                lastNRecords.add(currRecord);
                count++;
            }
        }
        Collections.reverse(lastNRecords);
        return lastNRecords;
    }


    public ArrayList<HomeAwayWrapper> removeFirstNRecordsOfGroup (ArrayList<HomeAwayWrapper> source, int startGamesIgnored) {
        return new ArrayList<>(source.subList(Math.min(startGamesIgnored, source.size()), source.size()));
    }

    public double getAvgNumberOfCleanSheets(GamesSelector gamesSelector) {
        return calcCleanSheetsAvg(this.goalsAgainst, gamesSelector, ADD_DEFAULT_VALUE_UNTIL_N_LENGTH);
    }

    public double getAvgNumberOfCleanSheetsLastXGames(GamesSelector gamesSelector, int lastXGames, boolean lengthenResultToSizeOfLastXGames) {
        ArrayList<HomeAwayWrapper> lastXGoalsAgainst = getLastNRecords(gamesSelector, this.goalsAgainst, lastXGames);
        return calcCleanSheetsAvg(lastXGoalsAgainst, gamesSelector, lengthenResultToSizeOfLastXGames ? lastXGames : ADD_DEFAULT_VALUE_UNTIL_N_LENGTH);
    }

    private double calcCleanSheetsAvg(ArrayList<HomeAwayWrapper> goalsAgainst, GamesSelector gamesSelector, int minLength) {
        double numbGames = 0, cleanSheetCount = 0;
        for (HomeAwayWrapper goalsAgainstRecord: goalsAgainst) {
            if (gamesSelector.getSetting() == 3 ||
                    gamesSelector.getSetting() == 1 && goalsAgainstRecord.isHome() ||
                    gamesSelector.getSetting() == 2 && !goalsAgainstRecord.isHome()) {
                numbGames++;
                if (goalsAgainstRecord.getNumb() == 0) cleanSheetCount++;
            }
        }
        while (numbGames < minLength) {
            cleanSheetCount += AVG_CLEAN_SHEETS_PER_GAME;
            numbGames++;
        }
        if (numbGames == 0) return AVG_CLEAN_SHEETS_PER_GAME;
        else return cleanSheetCount / numbGames;
    }

    public double getAvgPoints(GamesSelector gamesSelector) {
        return calcHomeAwayAvg(this.points, gamesSelector.getSetting(), AVG_PPG);
    }

    public double getAvgPointsOverLastXGames(GamesSelector gamesSelector, int numbPreviousGames) {
        ArrayList<HomeAwayWrapper> lastXPoints = getLastNRecords(gamesSelector, this.points, numbPreviousGames);
        return calcHomeAwayAvg(lastXPoints,gamesSelector.getSetting(), AVG_PPG);
    }

    public double getAvgPointsWhenScoredFirst(GamesSelector gamesSelector) {
        return calcHomeAwayAvg(this.pointsScoredFirst, gamesSelector.getSetting(), AVG_PPG);
    }

    public double getAvgPointsWhenConceededFirst(GamesSelector gamesSelector) {
        return calcHomeAwayAvg(this.pointsConceededFirst, gamesSelector.getSetting(), AVG_PPG);
    }

    public double getAvgPointsOfAllOpponentsGamesWholeSeason (GamesSelector gamesSelector) {
        return calcHomeAwayAvg(this.totalPointsPerGameOfOpponentsWholeSeason, gamesSelector.getSetting(), AVG_PPG, ADD_DEFAULT_VALUE_UNTIL_N_LENGTH);
    }
    public double getAvgPointsOfLastXOpponentsGamesWholeSeason (GamesSelector gamesSelector, int lastNRecords) {
        ArrayList<HomeAwayWrapper> ppgOfOpponentsLast5Games = getLastNRecords(gamesSelector, this.totalPointsPerGameOfOpponentsWholeSeason, lastNRecords);
        return calcHomeAwayAvg(ppgOfOpponentsLast5Games, gamesSelector.getSetting(), AVG_PPG, ADD_DEFAULT_VALUE_UNTIL_N_LENGTH);
    }

    public double getAvgPointsOfAllOpponentsHomeAwayGamesWholeSeason (GamesSelector gamesSelector) {
        return calcHomeAwayAvg(this.homeAwayPointsPerGameOfOpponentsWholeSeason, gamesSelector.getSetting(), AVG_PPG, ADD_DEFAULT_VALUE_UNTIL_N_LENGTH);
    }
    public double getAvgPointsOfLastXOpponentsHomeAwayGamesWholeSeason (GamesSelector gamesSelector, int lastNRecords) {
        ArrayList<HomeAwayWrapper> ppgOfOpponentsLast5Games = getLastNRecords(gamesSelector, this.homeAwayPointsPerGameOfOpponentsWholeSeason, lastNRecords);
        return calcHomeAwayAvg(ppgOfOpponentsLast5Games, gamesSelector.getSetting(), AVG_PPG, ADD_DEFAULT_VALUE_UNTIL_N_LENGTH);
    }

    public double getAvgPointsOfAllOpponentsLast5Games (GamesSelector gamesSelector) {
        return calcHomeAwayAvg(this.totalPointsPerGameOfOpponentsLast5, gamesSelector.getSetting(), AVG_PPG, ADD_DEFAULT_VALUE_UNTIL_N_LENGTH);
    }
    public double getAvgPointsOfLastXOpponentsLast5Games (GamesSelector gamesSelector, int lastNRecords) {
        ArrayList<HomeAwayWrapper> ppgOfOpponentsLast5Games = getLastNRecords(gamesSelector, this.totalPointsPerGameOfOpponentsLast5 , lastNRecords);
        return calcHomeAwayAvg(ppgOfOpponentsLast5Games, gamesSelector.getSetting(), AVG_PPG, ADD_DEFAULT_VALUE_UNTIL_N_LENGTH);
    }

    public double getAvgPointsOfAllOpponentsHomeAwayLast5Games (GamesSelector gamesSelector) {
        return calcHomeAwayAvg(this.homeAwayPointsPerGameOfOpponentsLast5, gamesSelector.getSetting(), AVG_PPG, ADD_DEFAULT_VALUE_UNTIL_N_LENGTH);
    }
    public double getAvgPointsOfLastXOpponentsHomeAwayLast5Games (GamesSelector gamesSelector, int lastNRecords) {
        ArrayList<HomeAwayWrapper> ppgOfOpponentsLast5Games = getLastNRecords(gamesSelector, this.homeAwayPointsPerGameOfOpponentsLast5 , lastNRecords);
        return calcHomeAwayAvg(ppgOfOpponentsLast5Games, gamesSelector.getSetting(), AVG_PPG, ADD_DEFAULT_VALUE_UNTIL_N_LENGTH);
    }


    /*
     * Mins weighted means the weight of the rating is determined by the number of mins he played in the game. If he played 2 games, one he
     * played 90mins at a 3/10, the other 45mins at a 7/10, mins weighted would give an answer closer to 3, whereas games rated would treat
     * both ratings equally and return a 5.
     *
     * minsWeightedRating is basically average rating per minute in a match
     */
    public double getMinsWeightedLineupRating (GamesSelector gamesSelector, ArrayList<String> startingXI) {
        if (startingXI.size() != 11) {
            throw new RuntimeException();
        }
        double weightedRating = 0d;
        int teamMinsPlayed = 0;
        int numbTeammatesNotYetPlayed = 0;
        for (String p: startingXI) {
            Player player = this.playerStats.getOrDefault(p, null);
            if (player != null) {
                    if (gamesSelector.getSetting() == 3) {
                        weightedRating += player.getSummedWeightedOvrRating();
                        teamMinsPlayed += player.getOvrMins();
                    }
                    else if (gamesSelector.getSetting() == 1) {
                        weightedRating += player.getSummedWeightedHomeRating();
                        teamMinsPlayed += player.getHomeMins();
                    }
                    else {
                        weightedRating += player.getSummedWeightedAwayRating();
                        teamMinsPlayed += player.getAwayMins();
                    }
            }
            else {
                numbTeammatesNotYetPlayed++;
            }
        }

        if (teamMinsPlayed > 0) weightedRating /= teamMinsPlayed;

        //if we have players who haven't yet played, we multiply the average team rating per min by the number of players used to create it,
        //then we add an average rating of 6 per minute and divide by total players to get average
        return ((11-numbTeammatesNotYetPlayed)*weightedRating + numbTeammatesNotYetPlayed*AVG_MINS_RATING_PER_GAME) / 11;
    }

    /*
     * Gets the average rating of the lineup based on each players average rating per game.
     */
    public double getGamesWeightedLineupRating (GamesSelector gamesSelector, ArrayList<String> startingXI) {
        if (startingXI.size() != 11) {
            throw new RuntimeException();
        }
        double weightedRating = 0d;
        for (String p: startingXI) {
            Player player = this.playerStats.getOrDefault(p, null);
            if (player != null) {
                    if (gamesSelector.getSetting() == 3) weightedRating += player.getAvgOvrRating();
                    else if (gamesSelector.getSetting() == 1) weightedRating += player.getAvgHomeRating();
                    else weightedRating += player.getAvgAwayRating();
            }
            else {
                weightedRating += AVG_RATING_PER_GAME;
            }
        }

        return weightedRating / 11;
    }

    /*
     * Lineup strength works by counting the total minutes of each player in the starting lineup by the total minutes of the most played lineup to give a
     * % of the best team that is playing.
     */
    public double getLineupStrength (GamesSelector gamesSelector, ArrayList<String> startingXI) {
        int homeAwayTotalGamesSetting = gamesSelector.getSetting();
        double totalMinsInLineup = 0d, max11MinsInSquad = 0d;
        ArrayList<Player> players = new ArrayList<>(this.playerStats.values());
        players.sort((p1, p2) -> {
            if (homeAwayTotalGamesSetting == 3) return p2.getOvrMins() - p1.getOvrMins();
            else if (homeAwayTotalGamesSetting == 1) return p2.getHomeMins() - p1.getHomeMins();
            else return p2.getAwayMins() - p1.getAwayMins();
        });
        int numbPlayersAdded = 0;
        for (Player player: players) {
            if (startingXI.contains(player.getPlayerName())) {
                totalMinsInLineup += homeAwayTotalGamesSetting == 3 ? player.getOvrMins() :
                                        homeAwayTotalGamesSetting == 1 ? player.getHomeMins() : player.getAwayMins();
            }
            if (numbPlayersAdded < 11) {
                max11MinsInSquad += homeAwayTotalGamesSetting == 3 ? player.getOvrMins() :
                                        homeAwayTotalGamesSetting == 1 ? player.getHomeMins() : player.getAwayMins();
                numbPlayersAdded++;
            }
        }
        return max11MinsInSquad > 0 ? (totalMinsInLineup / max11MinsInSquad) : 0;
    }

    public int getSeasonYearStart() {
        return seasonYearStart;
    }

    /*
     * HELPERS
     */

    /*
     * Called to calculate values from the array lists where we can specify if we just want the home records, away records, or both
     *
     * Setting options are 1: get avg goals at home. 2: get avg goals away. Anything else: get avg goals both home and away.
     * If 0 matches counted, we just return 0 to avoid dividing by 0.
     *
     * MakeLengthUpTo parameter is used to help with matches at the start of the season. We don't want to be training with games where
     * there's only 2 valid games for an average, so we add in the default value until we have reached the numb in makeLengthUpTo.
     */
    public double calcHomeAwayAvg(ArrayList<HomeAwayWrapper> doubleArray, int homeAwaySetting, double defaultValue, int makeLengthUpTo) {
        if (doubleArray == null) throw new RuntimeException();

        int matchesCounted = 0;
        double dNumb = 0d;
        for (HomeAwayWrapper match: doubleArray) {
            if (homeAwaySetting != 1 && !match.isHome()) {
                dNumb += match.getNumb();
                matchesCounted++;
            }
            else if (homeAwaySetting != 2 && match.isHome()) {
                dNumb += match.getNumb();
                matchesCounted++;
            }
        }

        while (matchesCounted < makeLengthUpTo) {
            dNumb += defaultValue;
            matchesCounted++;
        }
        return matchesCounted == 0 ? defaultValue : dNumb/matchesCounted;
    }

    private double calcHomeAwayAvg(ArrayList<HomeAwayWrapper> doubleArray, int homeAwaySetting, double defaultValue) {
        return calcHomeAwayAvg(doubleArray, homeAwaySetting, defaultValue, -1);
    }

    public static double calcExponWeightedAvg(double currAvg, double newEntry) {
        double ALPHA = 0.75;
        return ALPHA * currAvg + (1-ALPHA)*newEntry;
    }
}
