package com.petermarshall.machineLearning.createData.classes;

import java.util.ArrayList;
import java.util.HashMap;

//contains metrics that can be used to create the features for a match to predict.
//huge range of fields to allow for choice when creating features & training model
public class TrainingTeamsSeason {
    private int seasonYearStart;
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
    public void addGameStats(int goalsFor, int goalsAgainst, double xGF, double xGA, boolean scoredFirst, boolean hasScoredFirstData, boolean homeTeam, double oppositionAvgTotalGF,
                             double oppositionAvgTotalGA, double oppositionAvgHomeAwayGF, double oppositionAvgHomeAwayGA, double oppositionAvgTotalXGF, double oppositionAvgTotalXGA,
                             double oppositionAvgHomeAwayXGF, double oppositionAvgHomeAwayXGA, double oppositionWeightedTotalXGF, double oppositionWeightedTotalXGA,
                             double oppositionWeightedHomeAwayXGF, double oppositionWeightedHomeAwayXGA, double opponentTotalWholeSeasonPPG, double opponentHomeAwayWholeSeasonPPG,
                             double opponentTotalLast5PPG, double opponentHomeAwayLast5PPG) {

        if (goalsFor != -1 && goalsAgainst != -1) {
            //goals
            this.goalsFor.add(new HomeAwayWrapper(homeTeam, goalsFor));
            this.goalsAgainst.add(new HomeAwayWrapper(homeTeam, goalsAgainst));
            this.totalFormGoalsForHistory.add(new HomeAwayWrapper(homeTeam, goalsFor - oppositionAvgTotalGA));
            this.totalFormGoalsAgainstHistory.add(new HomeAwayWrapper(homeTeam, goalsAgainst - oppositionAvgTotalGF));
            this.totalFormGoalsFor = calcExponWeightedAvg(this.totalFormGoalsFor, goalsFor - oppositionAvgTotalGA);
            this.totalFormGoalsAgainst = calcExponWeightedAvg(this.totalFormGoalsAgainst, goalsAgainst - oppositionAvgTotalGF);
            if (homeTeam) {
                this.homeFormGoalsFor = calcExponWeightedAvg(this.homeFormGoalsFor, goalsFor - oppositionAvgHomeAwayGA);
                this.homeFormGoalsAgainst = calcExponWeightedAvg(this.homeFormGoalsAgainst, goalsAgainst - oppositionAvgHomeAwayGF);
            } else {
                this.awayFormGoalsFor = calcExponWeightedAvg(this.awayFormGoalsFor, goalsFor - oppositionAvgHomeAwayGA);
                this.awayFormGoalsAgainst = calcExponWeightedAvg(this.awayFormGoalsAgainst, goalsAgainst - oppositionAvgHomeAwayGF);
            }

            //points
            HomeAwayWrapper points = new HomeAwayWrapper(homeTeam, goalsFor > goalsAgainst ? 3 : goalsFor == goalsAgainst ? 1 : 0);
            this.points.add(points);
            if (hasScoredFirstData && (goalsFor > 0 || goalsAgainst > 0)) {
                if (scoredFirst) this.pointsScoredFirst.add(points);
                else this.pointsConceededFirst.add(points);
            }
            this.totalPointsPerGameOfOpponentsWholeSeason.add(new HomeAwayWrapper(homeTeam, opponentTotalWholeSeasonPPG));
            this.homeAwayPointsPerGameOfOpponentsWholeSeason.add(new HomeAwayWrapper(homeTeam, opponentHomeAwayWholeSeasonPPG));
            this.totalPointsPerGameOfOpponentsLast5.add(new HomeAwayWrapper(homeTeam, opponentTotalLast5PPG));
            this.homeAwayPointsPerGameOfOpponentsLast5.add(new HomeAwayWrapper(homeTeam, opponentHomeAwayLast5PPG));
        }

        //xG
        if (xGF != -1 && xGA != -1) {
            this.xGF.add(new HomeAwayWrapper(homeTeam, xGF));
            this.xGA.add(new HomeAwayWrapper(homeTeam, xGA));
            this.totalWeightedXGF = calcExponWeightedAvg(this.totalWeightedXGF, xGF);
            this.totalWeightedXGA = calcExponWeightedAvg(this.totalWeightedXGA, xGA);
            if (homeTeam) {
                this.homeWeightedXGF = calcExponWeightedAvg(this.homeWeightedXGF, xGF);
                this.homeWeightedXGA = calcExponWeightedAvg(this.homeWeightedXGA, xGA);
            } else {
                this.awayWeightedXGF = calcExponWeightedAvg(this.awayWeightedXGF, xGF);
                this.awayWeightedXGA = calcExponWeightedAvg(this.awayWeightedXGA, xGA);
            }
            //how many more xG we had compared to how many the oppositions form dictated we should have had. A measure of how good team is compared to other teams opposition has faced.
            //all calculated with exponentially weighted averages to include overperformance over the whole season, but place more weight ont he most recent games.
            this.totalFormXGFHistory.add(new HomeAwayWrapper(homeTeam, xGF - oppositionAvgTotalXGA));
            this.totalFormXGAHistory.add(new HomeAwayWrapper(homeTeam, xGA - oppositionAvgTotalXGF));
            this.totalFormWeightedXGFHistory.add(new HomeAwayWrapper(homeTeam, xGF - oppositionWeightedTotalXGA));
            this.totalFormWeightedXGAHistory.add(new HomeAwayWrapper(homeTeam, xGA - oppositionWeightedTotalXGF));

            this.totalFormXGF = calcExponWeightedAvg(this.totalFormXGF, xGF - oppositionAvgTotalXGA);
            this.totalFormXGA = calcExponWeightedAvg(this.totalFormXGA, xGA - oppositionAvgTotalXGF);
            this.totalFormWeightedXGF = calcExponWeightedAvg(this.totalFormWeightedXGF, xGF - oppositionWeightedTotalXGA);
            this.totalFormWeightedXGA = calcExponWeightedAvg(this.totalFormWeightedXGA, xGA - oppositionWeightedTotalXGF);

            if (homeTeam) {
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

    /*
     * GETTERS
     */

    public int getNumbGamesPlayed() {
        return this.points.size();
    }
    public int getNumbGamesPlayed(GamesSelector gamesSelector) {
        if (gamesSelector.getSetting() == 3) return this.points.size();
        else {
            ArrayList<HomeAwayWrapper> filteredList =  new ArrayList<>(this.points);
            filteredList.removeIf(record -> gamesSelector.getSetting() == 1 ? record.isHome() : !record.isHome());

            return filteredList.size();
        }
    }

    public double getAvgGoalsFor(GamesSelector gamesSelector) {
        return calcHomeAway(this.goalsFor, gamesSelector.getSetting(), AVG_GOALS_PER_GAME);
    }

    public double getAvgGoalsAgainst(GamesSelector gamesSelector) {
        return calcHomeAway(this.goalsAgainst, gamesSelector.getSetting(), AVG_GOALS_PER_GAME);
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

    public double getAvgXGF(GamesSelector gamesSelector) {
        return calcHomeAway(this.xGF, gamesSelector.getSetting(), AVG_XG_PER_GAME);
    }

    public double getAvgXGA(GamesSelector gamesSelector) {
        return calcHomeAway(this.xGA, gamesSelector.getSetting(), AVG_XG_PER_GAME);
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


    public double getAvgFormGoalsFor(GamesSelector gamesSelector, int removeFirstNGames) {
        return calcHomeAway(removeFirstNRecordsOfGroup(this.totalFormGoalsForHistory, removeFirstNGames), gamesSelector.getSetting(), 0);
        //default value for how many more xGF they got than expected... 0.
    }

    public double getAvgFormGoalsAgainst(GamesSelector gamesSelector, int removeFirstNGames) {
        return calcHomeAway(removeFirstNRecordsOfGroup(this.totalFormGoalsAgainstHistory, removeFirstNGames), gamesSelector.getSetting(), 0);
    }

    public double getAvgFormXGF(GamesSelector gamesSelector, int removeFirstNGames) {
        return calcHomeAway(removeFirstNRecordsOfGroup(this.totalFormXGFHistory, removeFirstNGames), gamesSelector.getSetting(), 0);
    }

    public double getAvgFormXGA(GamesSelector gamesSelector, int removeFirstNGames) {
        return calcHomeAway(removeFirstNRecordsOfGroup(this.totalFormXGAHistory, removeFirstNGames), gamesSelector.getSetting(), 0);
    }

    public double getAvgFormWeightedXGF (GamesSelector gamesSelector, int removeFirstNGames) {
        return calcHomeAway(removeFirstNRecordsOfGroup(this.totalFormWeightedXGFHistory, removeFirstNGames), gamesSelector.getSetting(), 0);
    }

    public double getAvgFormWeightedXGA (GamesSelector gamesSelector, int removeFirstNGames) {
        return calcHomeAway(removeFirstNRecordsOfGroup(this.totalFormWeightedXGAHistory, removeFirstNGames), gamesSelector.getSetting(), 0);
    }

    public double getFormXGFOverLastNGames(GamesSelector gamesSelector, int numbPreviousGames) {
        ArrayList<HomeAwayWrapper> lastNGamesXGF = this.getLastNRecords(gamesSelector, this.totalFormXGFHistory, numbPreviousGames);
        return calcHomeAway(lastNGamesXGF, gamesSelector.getSetting(), 0);
    }

    public double getFormXGAOverLastNGames(GamesSelector gamesSelector, int numbPreviousGames) {
        ArrayList<HomeAwayWrapper> lastNGamesXGA = this.getLastNRecords(gamesSelector, this.totalFormXGAHistory, numbPreviousGames);
        return calcHomeAway(lastNGamesXGA, gamesSelector.getSetting(), 0);
    }

    private ArrayList<HomeAwayWrapper> getLastNRecords(GamesSelector gamesSelector, ArrayList<HomeAwayWrapper> source, int numbPreviousGames) {
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
        return lastNRecords;
    }


    private ArrayList<HomeAwayWrapper> removeFirstNRecordsOfGroup (ArrayList<HomeAwayWrapper> source, int startGamesIgnored) {
        return new ArrayList<>(source.subList(Math.min(startGamesIgnored, source.size()), source.size()));
    }

    public double getAvgPoints(GamesSelector gamesSelector) {
        return calcHomeAway(this.points, gamesSelector.getSetting(), AVG_PPG);
    }

    public double getAvgPointsOverLastXGames(GamesSelector gamesSelector, int numbPreviousGames) {
        ArrayList<HomeAwayWrapper> lastXPoints = getLastNRecords(gamesSelector, this.points, numbPreviousGames);
        return calcHomeAway(lastXPoints,gamesSelector.getSetting(), AVG_PPG);
    }

    public double getAvgPointsWhenScoredFirst(GamesSelector gamesSelector) {
        return calcHomeAway(this.pointsScoredFirst, gamesSelector.getSetting(), AVG_PPG);
    }

    public double getAvgPointsWhenConceededFirst(GamesSelector gamesSelector) {
        return calcHomeAway(this.pointsConceededFirst, gamesSelector.getSetting(), AVG_PPG);
    }

    public double getAvgPointsOfAllOpponentsGamesWholeSeason (GamesSelector gamesSelector) {
        return calcHomeAway(this.totalPointsPerGameOfOpponentsWholeSeason, gamesSelector.getSetting(), AVG_PPG, ADD_DEFAULT_VALUE_UNTIL_N_LENGTH);
    }
    public double getAvgPointsOfLastXOpponentsGamesWholeSeason (GamesSelector gamesSelector, int lastNRecords) {
        ArrayList<HomeAwayWrapper> ppgOfOpponentsLast5Games = getLastNRecords(gamesSelector, this.totalPointsPerGameOfOpponentsWholeSeason, lastNRecords);
        return calcHomeAway(ppgOfOpponentsLast5Games, gamesSelector.getSetting(), AVG_PPG, ADD_DEFAULT_VALUE_UNTIL_N_LENGTH);
    }

    public double getAvgPointsOfAllOpponentsHomeAwayGamesWholeSeason (GamesSelector gamesSelector) {
        return calcHomeAway(this.homeAwayPointsPerGameOfOpponentsWholeSeason, gamesSelector.getSetting(), AVG_PPG, ADD_DEFAULT_VALUE_UNTIL_N_LENGTH);
    }
    public double getAvgPointsOfLastXOpponentsHomeAwayGamesWholeSeason (GamesSelector gamesSelector, int lastNRecords) {
        ArrayList<HomeAwayWrapper> ppgOfOpponentsLast5Games = getLastNRecords(gamesSelector, this.homeAwayPointsPerGameOfOpponentsWholeSeason, lastNRecords);
        return calcHomeAway(ppgOfOpponentsLast5Games, gamesSelector.getSetting(), AVG_PPG, ADD_DEFAULT_VALUE_UNTIL_N_LENGTH);
    }

    public double getAvgPointsOfAllOpponentsLast5Games (GamesSelector gamesSelector) {
        return calcHomeAway(this.totalPointsPerGameOfOpponentsLast5, gamesSelector.getSetting(), AVG_PPG, ADD_DEFAULT_VALUE_UNTIL_N_LENGTH);
    }
    public double getAvgPointsOfLastXOpponentsLast5Games (GamesSelector gamesSelector, int lastNRecords) {
        ArrayList<HomeAwayWrapper> ppgOfOpponentsLast5Games = getLastNRecords(gamesSelector, this.totalPointsPerGameOfOpponentsLast5 , lastNRecords);
        return calcHomeAway(ppgOfOpponentsLast5Games, gamesSelector.getSetting(), AVG_PPG, ADD_DEFAULT_VALUE_UNTIL_N_LENGTH);
    }

    public double getAvgPointsOfAllOpponentsHomeAwayLast5Games (GamesSelector gamesSelector) {
        return calcHomeAway(this.homeAwayPointsPerGameOfOpponentsLast5, gamesSelector.getSetting(), AVG_PPG, ADD_DEFAULT_VALUE_UNTIL_N_LENGTH);
    }
    public double getAvgPointsOfLastXOpponentsHomeAwayLast5Games (GamesSelector gamesSelector, int lastNRecords) {
        ArrayList<HomeAwayWrapper> ppgOfOpponentsLast5Games = getLastNRecords(gamesSelector, this.homeAwayPointsPerGameOfOpponentsLast5 , lastNRecords);
        return calcHomeAway(ppgOfOpponentsLast5Games, gamesSelector.getSetting(), AVG_PPG, ADD_DEFAULT_VALUE_UNTIL_N_LENGTH);
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
    private double calcHomeAway (ArrayList<HomeAwayWrapper> doubleArray, int homeAwaySetting, double defaultValue, int makeLengthUpTo) {
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

    private double calcHomeAway (ArrayList<HomeAwayWrapper> doubleArray, int homeAwaySetting, double defaultValue) {
        return calcHomeAway(doubleArray, homeAwaySetting, defaultValue, -1);
    }

    public static double calcExponWeightedAvg(double currAvg, double newEntry) {
        double ALPHA = 0.8;
        return ALPHA * currAvg + (1-ALPHA)*newEntry;
    }
}
