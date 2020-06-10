package com.petermarshall.machineLearning.createData.classes;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;

import static com.petermarshall.machineLearning.createData.classes.GamesSelector.*;
import static com.petermarshall.machineLearning.createData.refactor.PastStatsCalculator.COMPARE_LAST_N_GAMES;

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
    //initialising the xGF as average goals per game in PL 2017/18.
    public static final double AVG_GOALS_PER_GAME = 1.34;
    //places more weight on more recent results.
    private double totalWeightedXGF = AVG_GOALS_PER_GAME;
    private double homeWeightedXGF = AVG_GOALS_PER_GAME;
    private double awayWeightedXGF = AVG_GOALS_PER_GAME;
    private double totalWeightedXGA = AVG_GOALS_PER_GAME;
    private double homeWeightedXGA = AVG_GOALS_PER_GAME;
    private double awayWeightedXGA = AVG_GOALS_PER_GAME;
    //will be calculated by using a teams actual xGF and seeing how they performed against the oppositions weighted xGA.
    //initialised to be that each team scored/conceeded the exact amount of goals that the other team's stats expected them to conceede. (0 goals more than expected)
    //FormWeighted says how many more xG the team got against what the opponents recent weighted xG suggested they should.
    private double totalFormWeightedXGF = 0;
    private double homeFormWeightedXGF = 0;
    private double awayFormWeightedXGF = 0;
    private double totalFormWeightedXGA = 0;
    private double homeFormWeightedXGA = 0;
    private double awayFormWeightedXGA = 0;
    //will have arrays so we can look at the last X amount of games as an average instead of using the weightedAvg thing.
    private ArrayList<HomeAwayWrapper> totalFormWeightedXGFHistory = new ArrayList<>();
    private ArrayList<HomeAwayWrapper> totalFormWeightedXGAHistory = new ArrayList<>();
    //compares how many more goals were scored against the oppositions average goals against
    //will be calculated by taking a teams actual goals and comparing against the oppositions average goals conceeded to see how much they overperformed.
    private double totalFormGoalsFor = 0;
    private double homeFormGoalsFor = 0;
    private double awayFormGoalsFor = 0;
    private double totalFormGoalsAgainst = 0;
    private double homeFormGoalsAgainst = 0;
    private double awayFormGoalsAgainst = 0;
    //arrays
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
    //arrays
    private ArrayList<HomeAwayWrapper> totalFormXGFHistory = new ArrayList<>();
    private ArrayList<HomeAwayWrapper> totalFormXGAHistory = new ArrayList<>();
    //kept as a hashmap to enable super fast player updates - which will be most used operation.
    private HashMap<String, Player> playerStats;

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
        this.totalPointsPerGameOfOpponentsWholeSeason = new ArrayList<>(); //can be used to calculate the difficulty of opponents a team has faced over the season. contains opponents total PPG
        this.homeAwayPointsPerGameOfOpponentsWholeSeason = new ArrayList<>(); //contains opponents Home/Away PPG.
        this.totalPointsPerGameOfOpponentsLast5 = new ArrayList<>();
        this.homeAwayPointsPerGameOfOpponentsLast5 = new ArrayList<>();
    }

    /*
     * Method adds the game stats for a game to a team. IMPORTANT: Needs to be called after the TrainingMatch has been created. Otherwise when we create the TrainingMatch,
     * it will include the stats from the TrainingMatch that we're trying to predict. Whereas we want the training match to be
     * a predictor, so must include data from previous games only.
     *
     * Method has checks to only add data if the parameters we've been given != -1. Filters out incomplete data.
     *
     * NOTE: we cannot just pass in the opponents TrainingTeamsSeason or we will update one and then use the updated version to update the other. We need to create temp variables and pass them
     * into each teams addGameStats method.
     */
    public void addGameStats(int goalsFor, int goalsAgainst, double xGF, double xGA, boolean scoredFirst, boolean hasScoredFirstData, boolean homeTeam, double oppositionAvgTotalGF,
                             double oppositionAvgTotalGA, double oppositionAvgHomeAwayGF, double oppositionAvgHomeAwayGA, double oppositionAvgTotalXGF, double oppositionAvgTotalXGA,
                             double oppositionAvgHomeAwayXGF, double oppositionAvgHomeAwayXGA, double oppositionWeightedTotalXGF, double oppositionWeightedTotalXGA,
                             double oppositionWeightedHomeAwayXGF, double oppositionWeightedHomeAwayXGA, double opponentTotalWholeSeasonPPG, double opponentHomeAwayWholeSeasonPPG,
                             double opponentTotalLast5PPG, double opponentHomeAwayLast5PPG) {

        if (goalsFor != -1 && goalsAgainst != -1) {
            this.goalsFor.add(new HomeAwayWrapper(homeTeam, goalsFor));
            this.goalsAgainst.add(new HomeAwayWrapper(homeTeam, goalsAgainst));

            HomeAwayWrapper points = new HomeAwayWrapper(homeTeam, goalsFor > goalsAgainst ? 3 : goalsFor == goalsAgainst ? 1 : 0);
            this.points.add(points);
            if (hasScoredFirstData && (goalsFor > 0 || goalsAgainst > 0)) {
                if (scoredFirst) this.pointsScoredFirst.add(points);
                else this.pointsConceededFirst.add(points);
            }

            //new fields
            this.totalPointsPerGameOfOpponentsWholeSeason.add(new HomeAwayWrapper(homeTeam, opponentTotalWholeSeasonPPG));
            this.homeAwayPointsPerGameOfOpponentsWholeSeason.add(new HomeAwayWrapper(homeTeam, opponentHomeAwayWholeSeasonPPG));

            this.totalPointsPerGameOfOpponentsLast5.add(new HomeAwayWrapper(homeTeam, opponentTotalLast5PPG));
            this.homeAwayPointsPerGameOfOpponentsLast5.add(new HomeAwayWrapper(homeTeam, opponentHomeAwayLast5PPG));
            //end of new fields

            this.totalFormGoalsForHistory.add(new HomeAwayWrapper(homeTeam, goalsFor - oppositionAvgTotalGA)); //TODO: will need to check that these parameters are not these exponential avg vals
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
        }


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



    /*
     * Adds stats to player unless player has not played a game yet. In that case, default action is to create a new player obj and add it to player map.
     */
    public void addPlayerStats(String playerName, int minsPlayed, double rating, boolean homeTeam) {
        Player player = this.playerStats.getOrDefault(playerName, null);

        if (player == null) this.playerStats.put(playerName, new Player(playerName, minsPlayed, rating, homeTeam));
        else player.addMatchMinsRating(minsPlayed, rating, homeTeam);
    }

    public int getNumbGamesPlayed(GamesSelector gamesSelector) {
        if (gamesSelector.getSetting() == 3) return this.points.size();
        else {
            ArrayList<HomeAwayWrapper> filteredList =  new ArrayList<>(this.points);
            filteredList.removeIf(record -> gamesSelector.getSetting() == 1 ? record.isHome() : !record.isHome());

            return filteredList.size();
        }
    }
    public int getNumbGamesPlayed() {
        return this.points.size();
    }

    /*
     * Default for each category justification:
     *
     * Goals + xG: Avg goals per game is 1.34;
     * Points in the middle would be 1.5;
     */
    public double getAvgGoalsFor(GamesSelector gamesSelector) {
        return calcHomeAway(this.goalsFor, gamesSelector.getSetting(), 1.34);
    }

    public double getAvgGoalsAgainst(GamesSelector gamesSelector) {
        return calcHomeAway(this.goalsAgainst, gamesSelector.getSetting(), 1.34);
    }

    //default value of clean sheets is on average 0.29 per game. - averaging to about 11 clean sheets over a whole season.
    private static double DEFAULT_CLEAN_SHEETS_PER_GAME = 0.29;
    public double getAvgNumberOfCleanSheets(GamesSelector gamesSelector) {
        return calcCleanSheetsAvg(this.goalsAgainst, gamesSelector, 0);
    }
    public double getAvgNumberOfCleanSheetsLastXGames(GamesSelector gamesSelector, int lastXGames, boolean lengthenResultToSizeOfLastXGames) {
        ArrayList<HomeAwayWrapper> lastXGoalsAgainst = getLastNRecords(gamesSelector, this.goalsAgainst, lastXGames);
        return calcCleanSheetsAvg(lastXGoalsAgainst, gamesSelector, lengthenResultToSizeOfLastXGames ? lastXGames : 0);
    }
    private double calcCleanSheetsAvg(ArrayList<HomeAwayWrapper> goalsAgainst, GamesSelector gamesSelector,  int minLength) {
        double numbGames = 0, cleanSheetCount = 0;
        for (HomeAwayWrapper goalsAgainstRecord: goalsAgainst) {
            if (gamesSelector.getSetting() == 3 ||
                    gamesSelector.getSetting() == 1 && goalsAgainstRecord.isHome() ||
                    gamesSelector.getSetting() == 2 && !goalsAgainstRecord.isHome()) {
                numbGames++;
                if (goalsAgainstRecord.getNumb() == 0) cleanSheetCount++;
            }
        };

        while (numbGames < minLength) {
            cleanSheetCount += DEFAULT_CLEAN_SHEETS_PER_GAME;
            numbGames++;
        }

        if (numbGames == 0) return 0;
        else return cleanSheetCount / numbGames;
    }

    public double getAvgXGF(GamesSelector gamesSelector) {
        return calcHomeAway(this.xGF, gamesSelector.getSetting(), 1.34);
    }

    public double getAvgXGA(GamesSelector gamesSelector) {
        return calcHomeAway(this.xGA, gamesSelector.getSetting(), 1.34);
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

    //GETTERS FOR WEIGHTED FORM FIELDS
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

    //Getters for average Form fields
    public double getAvgFormGoalsFor(GamesSelector gamesSelector, int removeFirstNGames) {
        return calcHomeAway(removeFirstNGames == 0 ? this.totalFormGoalsForHistory : removeFirstNRecordsOfGroup(this.totalFormGoalsForHistory, removeFirstNGames),
                gamesSelector.getSetting(),
                0);
    }

    public double getAvgFormGoalsAgainst(GamesSelector gamesSelector, int removeFirstNGames) {
        return calcHomeAway(removeFirstNGames == 0 ? this.totalFormGoalsAgainstHistory : removeFirstNRecordsOfGroup(this.totalFormGoalsAgainstHistory, removeFirstNGames),
                gamesSelector.getSetting(),
                0);
    }

    public double getAvgFormXGF(GamesSelector gamesSelector, int removeFirstNGames) {
        return calcHomeAway(removeFirstNGames == 0 ? this.totalFormXGFHistory : removeFirstNRecordsOfGroup(this.totalFormXGFHistory, removeFirstNGames),
                gamesSelector.getSetting(),
                0);
    }

    public double getAvgFormXGA(GamesSelector gamesSelector, int removeFirstNGames) {
        return calcHomeAway(removeFirstNGames == 0 ? this.totalFormXGAHistory : removeFirstNRecordsOfGroup(this.totalFormXGAHistory, removeFirstNGames),
                gamesSelector.getSetting(),
                0);
    }

    public double getAvgFormWeightedXGF (GamesSelector gamesSelector, int removeFirstNGames) {
        return calcHomeAway(removeFirstNGames == 0 ? this.totalFormWeightedXGFHistory : removeFirstNRecordsOfGroup(this.totalFormWeightedXGFHistory, removeFirstNGames),
                gamesSelector.getSetting(),
                0);
    }

    public double getAvgFormWeightedXGA (GamesSelector gamesSelector, int removeFirstNGames) {
        return calcHomeAway(removeFirstNGames == 0 ? this.totalFormWeightedXGAHistory : removeFirstNRecordsOfGroup(this.totalFormWeightedXGAHistory, removeFirstNGames),
                gamesSelector.getSetting(),
                0);
    }


    public double getFormXGFOverLastNGames(GamesSelector gamesSelector, int numbPreviousGames) {
        ArrayList<HomeAwayWrapper> lastNGamesXGF = this.getLastNRecords(gamesSelector, this.totalFormXGFHistory, numbPreviousGames);
        return calcHomeAway(lastNGamesXGF, gamesSelector.getSetting(), 1.5);
    }

    public double getFormXGAOverLastNGames(GamesSelector gamesSelector, int numbPreviousGames) {
        ArrayList<HomeAwayWrapper> lastNGamesXGA = this.getLastNRecords(gamesSelector, this.totalFormXGAHistory, numbPreviousGames);
        return calcHomeAway(lastNGamesXGA, gamesSelector.getSetting(), 1.5);
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
        return calcHomeAway(this.points, gamesSelector.getSetting(), 1.5);
    }

    /*
     * Method starts at end and adds home records if gameSelector is 1 or 3, and adds away records if gameSelector is 2 or 3.
     * Goes until we get to the number of games we want to look at.
     */
    public double getAvgPointsOverLastXGames(GamesSelector gamesSelector, int numbPreviousGames) {
        ArrayList<HomeAwayWrapper> lastXPoints = getLastNRecords(gamesSelector, this.points, numbPreviousGames);

        return calcHomeAway(lastXPoints,gamesSelector.getSetting(), 1.5);
    }

    public double getAvgPointsWhenScoredFirst(GamesSelector gamesSelector) {
        return calcHomeAway(this.pointsScoredFirst, gamesSelector.getSetting(), 1.5);
    }

    public double getAvgPointsWhenConceededFirst(GamesSelector gamesSelector) {
        return calcHomeAway(this.pointsConceededFirst, gamesSelector.getSetting(), 1.5);
    }


    //
    ///NEW FEATURES ABOUT THE QUALITY OF OPPONENTS A TEAM HAS FACED SO WE CAN FIGURE OUT HOW IMPRESSIVE THEIR STATS ARE.
    //

    //TODO: if these arraylists have less than 3 records in it, we need to lengthen them with default vaules to avoid extreme vals.
    private int ADD_DEFAULT_VALUE_UNTIL_N_LENGTH = 3;

    public double getAvgPointsOfAllOpponentsGamesWholeSeason (GamesSelector gamesSelector) {
        return calcHomeAway(this.totalPointsPerGameOfOpponentsWholeSeason, gamesSelector.getSetting(), 1.5, ADD_DEFAULT_VALUE_UNTIL_N_LENGTH);
    }
    public double getAvgPointsOfLastXOpponentsGamesWholeSeason (GamesSelector gamesSelector, int lastNRecords) {
        ArrayList<HomeAwayWrapper> ppgOfOpponentsLast5Games = getLastNRecords(gamesSelector, this.totalPointsPerGameOfOpponentsWholeSeason , lastNRecords);
        return calcHomeAway(ppgOfOpponentsLast5Games, gamesSelector.getSetting(), 1.5, ADD_DEFAULT_VALUE_UNTIL_N_LENGTH);
    }


    public double getAvgPointsOfAllOpponentsHomeAwayGamesWholeSeason (GamesSelector gamesSelector) {
        return calcHomeAway(this.homeAwayPointsPerGameOfOpponentsWholeSeason, gamesSelector.getSetting(), 1.5, ADD_DEFAULT_VALUE_UNTIL_N_LENGTH);
    }
    public double getAvgPointsOfLastXOpponentsHomeAwayGamesWholeSeason (GamesSelector gamesSelector, int lastNRecords) {
        ArrayList<HomeAwayWrapper> ppgOfOpponentsLast5Games = getLastNRecords(gamesSelector, this.homeAwayPointsPerGameOfOpponentsWholeSeason , lastNRecords);
        return calcHomeAway(ppgOfOpponentsLast5Games, gamesSelector.getSetting(), 1.5, ADD_DEFAULT_VALUE_UNTIL_N_LENGTH);
    }

    public double getAvgPointsOfAllOpponentsLast5Games (GamesSelector gamesSelector) {
        return calcHomeAway(this.totalPointsPerGameOfOpponentsLast5, gamesSelector.getSetting(), 1.5, ADD_DEFAULT_VALUE_UNTIL_N_LENGTH);
    }
    public double getAvgPointsOfLastXOpponentsLast5Games (GamesSelector gamesSelector, int lastNRecords) {
        ArrayList<HomeAwayWrapper> ppgOfOpponentsLast5Games = getLastNRecords(gamesSelector, this.totalPointsPerGameOfOpponentsLast5 , lastNRecords);
        return calcHomeAway(ppgOfOpponentsLast5Games, gamesSelector.getSetting(), 1.5, ADD_DEFAULT_VALUE_UNTIL_N_LENGTH);
    }


    public double getAvgPointsOfAllOpponentsHomeAwayLast5Games (GamesSelector gamesSelector) {
        return calcHomeAway(this.homeAwayPointsPerGameOfOpponentsLast5, gamesSelector.getSetting(), 1.5, ADD_DEFAULT_VALUE_UNTIL_N_LENGTH);
    }
    public double getAvgPointsOfLastXOpponentsHomeAwayLast5Games (GamesSelector gamesSelector, int lastNRecords) {
        ArrayList<HomeAwayWrapper> ppgOfOpponentsLast5Games = getLastNRecords(gamesSelector, this.homeAwayPointsPerGameOfOpponentsLast5 , lastNRecords);
        return calcHomeAway(ppgOfOpponentsLast5Games, gamesSelector.getSetting(), 1.5, ADD_DEFAULT_VALUE_UNTIL_N_LENGTH);
    }

    //
    //END OF NEW FEATURES
    //



    /*
     * Mins weighted means the weight of the rating is determined by the number of mins he played in the game. If he played 2 games, one he
     * played 90mins at a 3/10, the other 45mins at a 7/10, mins weighted would give an answer closer to 3, whereas games rated would treat
     * both ratings equally and return a 5.
     *
     * minsWeightedRating is basically your average rating from a minute in a match
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
                        weightedRating +=player.getWeightedOvrRating();
                        teamMinsPlayed += player.getOvrMins();
                    }
                    else if (gamesSelector.getSetting() == 1) {
                        weightedRating += player.getWeightedHomeRating();
                        teamMinsPlayed += player.getHomeMins();
                    }
                    else {
                        weightedRating += player.getWeightedAwayRating();
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
        return ((11-numbTeammatesNotYetPlayed)*weightedRating + numbTeammatesNotYetPlayed*6) / 11;
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
                weightedRating += 6;
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
     * Called to calculate values from the array lists where we can specify if we just want the home records, away records, or both
     *
     * Setting options are 1: get avg goals at home. 2: get avg goals away. Anything else: get avg goals both home and away.
     * If 0 matches counted, we just return 0 to avoid dividing by 0.
     *
     * Can be called either with an arrayList of ints, or doubles. IntArray will always take priority unless it is set to null.
     * If both are null, we will throw exception.
     *
     * MakeLengthUpTo parameter is used to help with matches at the start of the season. We don't want to be training with games where
     * there's only 2 valid games for an average, so we add in the default value of 1.5 until we have reached the numb in makeLengthUpTo.
     * Currently only used with opponentsPPG, where we only want extreme values when a team has only played either really good or really bad teams.
     * Values at the start where a team has lost 2 games and comes out with 0 does not compare to a team losing the last 5 games and coming out with 0.
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
