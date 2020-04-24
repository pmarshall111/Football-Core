package com.petermarshall.machineLearning.logisticRegression;

import com.petermarshall.ConvertOdds;
import com.petermarshall.database.ResultBetOn;
import com.petermarshall.database.WhenGameWasPredicted;
import com.petermarshall.logging.MatchLog;
import com.petermarshall.machineLearning.createData.classes.MatchToPredict;
import com.petermarshall.database.datasource.DataSource;
import com.petermarshall.scrape.classes.OddsCheckerBookies;
import org.ejml.data.DMatrixRMaj;
import org.ejml.equation.Equation;
import org.ejml.ops.MatrixIO;
import org.ejml.simple.SimpleMatrix;

import java.io.IOException;
import java.util.*;

/*
 * Aim of this class is to Predict the results of unseen games.
 * SimpleMatrix is not like Octave in having a 1 index. The first row + col in EJML is 0.
 */
public class Predict {
    //will have a method for calculating prediction %,
    //convertLogitsToProbability
    //calcMoneyMade with different vals

    private static SimpleMatrix logitPredictions = null;
    private static SimpleMatrix resultPredictions = null;

    private static final double delta = 0.15;
    private static final double gamma = 0.15;
    private static final double sigma = 4;

    /*
     * Creates 2 simple matrices. One with the logit predictions of the tests (which will be X*allTheta), and the other just takes the
     * column with the highest value and takes that to be our main prediction for the result of the game.
     * TODO: check that the first few rows are the same in both octave and java. (not massively important)
     */
    public static void calcPredictions(SimpleMatrix allTheta, SimpleMatrix X) {
        int m = X.numRows();
        int numLabels = allTheta.numRows();

        logitPredictions = X.mult(allTheta.transpose());
        resultPredictions = new SimpleMatrix(m, 1);

        for (int row = 0; row<m; row++) {
            double currMax = -99999999;
            int maxCol = -1;
            for (int col = 0; col<numLabels; col++) {

                if (logitPredictions.get(row,col) > currMax) {
                    currMax = logitPredictions.get(row, col);
                    maxCol = col;
                }

            }

            resultPredictions.set(row, 0, maxCol+1); //+1 here as we have a win as 1, draw as 2 and loss as 3.
        }
    }


    /*
     * Looks at the resultPredictions (which just takes the column with the highest value) and compares to the actual result.
     * Returns accuracy as a percentage.
     *
     * Result predictions and y are the same size with just 1 column. Both contain just the result number of the match (1 = win, 2 = draw, 3 = loss)
     */
    public static double getAccuracy(SimpleMatrix y) {
        if (resultPredictions == null) throw new RuntimeException("Cannot calculate the accuracy when we haven't got any predictions yet. Call calcPredictions() first.");

        double numbCorrect = 0;

        for (int row = 0; row<y.numRows(); row++) {
            if (resultPredictions.get(row,0) == y.get(row,0)) numbCorrect++;
        }

        return numbCorrect * 100 /y.numRows();
    }

    /*
     * Changes our logistic regression values so that the probabilities all add up to 1.
     *
     * TODO: check that our equation is correct. The formula to calc the sums in octave is sum(m,2).
     */
    static SimpleMatrix convertLogitsToProbability(SimpleMatrix A) {
        SimpleMatrix exp = A.elementExp();
        SimpleMatrix matrix = exp.elementDiv(exp.plus(1));

        //getting the sum for each row. Equations allow you to write Octave/MATLAB code in Java.
        Equation eq = new Equation();
        eq.alias(matrix, "m");
        eq.process("sums = sum(m, 0)");
        SimpleMatrix sums = eq.lookupSimple("sums");

        //making a 3 column matrix so we can elementDivide.
        SimpleMatrix threeSums = sums.combine(0,1,sums).combine(0,2,sums);
        return matrix.elementDiv(threeSums);
    }



    /*
     * For this function to work, we need a list of all the features for each match so that we can multiply them out with our thetas to get predictions.
     * Method will fail when we start to change our database, as we've hardcoded in the number of rows and columns in our thetas.
     *
     * Bias parameter added to match features when the features are created in GetMatchesFromDb.addLegacyFeaturesToMatchesToPredict();
     */
    public static void addOurProbabilitiesToGames(ArrayList<MatchToPredict> matches, String thetasFullPath) {

        try {

            //"C:\\Users\\Peter\\Documents\\JavaProjects\\Football\\testThetas.csv"
            DMatrixRMaj dMatrixRMaj = MatrixIO.loadCSV(thetasFullPath, 3, 85);
            SimpleMatrix thetas = SimpleMatrix.wrap(dMatrixRMaj);

            for (MatchToPredict match: matches) {
                ArrayList<Double> features = match.getFeatures();

                double[] arr = features.stream().mapToDouble(Double::doubleValue).toArray();
                SimpleMatrix matchFeatures = new SimpleMatrix(new double[][]{arr});

                double[] ourPredictions = predictForMatch(thetas, matchFeatures); //should be length 3 with percentage for win/draw/loss
                if (ourPredictions.length != 3) throw new RuntimeException("Not all predictions have been added to our double array");
                else match.setOurPredictions(ourPredictions);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

    }



    /*
     * Calculates logit probabilities and then regualrises them so that the total probability that comes out is 1.
     */
    private static double[] predictForMatch (SimpleMatrix thetas, SimpleMatrix matchFeatures) {
//        System.out.println("thetas size: " + thetas.numRows() + " x " + thetas.numCols());
//        System.out.println("features size: " + matchFeatures.numRows() + " x " + matchFeatures.numCols());

        SimpleMatrix logits = matchFeatures.mult(thetas.transpose());
        SimpleMatrix results = convertLogitsToProbability(logits);

        return new double[]{results.get(0,0), results.get(0,1), results.get(0,2)};
    }


    /*
     * IMPORTANT: Only to be called when dealing
     * will add info to email body.
     * default behaviour is to allow all bookies if allowedBookies is null, or empty.
     *
     * CURRENTLY ONLY ADDING GOOD BETS FOR BETTER THAN BOOKIES
     */
    public static boolean calcBetsForCurrentGamesAndAddToBuilder(ArrayList<MatchToPredict> matches, StringBuilder emailBody, HashSet<String> allowedBookies) {

        boolean madeChanges = false;

        if (allowedBookies == null || allowedBookies.size() == 0) {
            allowedBookies = OddsCheckerBookies.getAllBookies();
        }

        for (MatchToPredict match: matches) {
            TreeSet<String> homeWinTreeSet = new TreeSet<>(); //IMPORTANT: TreeSet used to automatically sort our bookies odds. pls don't change.
            TreeSet<String> awayWinTreeSet = new TreeSet<>();

            //creating base string for match
            StringBuilder matchStringBuilder = new StringBuilder();
            matchStringBuilder.append("\n\n\n");
            matchStringBuilder.append(match.getSqlDateString());
            matchStringBuilder.append(" ");
            matchStringBuilder.append(match.getHomeTeamName());
            matchStringBuilder.append(" vs ");
            matchStringBuilder.append(match.getAwayTeamName());
            matchStringBuilder.append(":");


            calculateIfGoodBetAndAddToTreeSet(match, allowedBookies, homeWinTreeSet, awayWinTreeSet);

            //NOTE: we wouldn't have unrecorded bets here even though it looks like the away win bet would overwrite the home win bet here (due to duplicate calls
            //and only 1 spot for storage in the database). However this will not happen as the model is designed to only predict on the 1 most likely outcome.
            if (homeWinTreeSet.size()>0) {
                matchStringBuilder.append("\nHome win: ");
                logGoodBetsAndAddToBuilder(matchStringBuilder, homeWinTreeSet, match, ResultBetOn.HOME_WIN, WhenGameWasPredicted.PREDICTED_ON_IN_REAL_TIME);
            }
            if (awayWinTreeSet.size() > 0) {
                matchStringBuilder.append("\nAway win: ");
                logGoodBetsAndAddToBuilder(matchStringBuilder, awayWinTreeSet, match, ResultBetOn.AWAY_WIN, WhenGameWasPredicted.PREDICTED_ON_IN_REAL_TIME);
            }
            matchStringBuilder.append("\n\n");

            if (homeWinTreeSet.size() > 0 || awayWinTreeSet.size() > 0) {
                emailBody.append(matchStringBuilder.toString());
                madeChanges = true;
            } else {
                //then we found no good bets
                MatchLog noBetFound = new MatchLog(match, WhenGameWasPredicted.PREDICTED_ON_IN_REAL_TIME, ResultBetOn.NOT_BET_ON, -1, ZERO_STAKE);
                DataSource.openConnection();
                DataSource.logBetPlaced(noBetFound);
                DataSource.closeConnection();

            }
        }

        return madeChanges;
    }
    public static boolean calcBetsForCurrentGamesAndAddToBuilder(ArrayList<MatchToPredict> matches, StringBuilder emailBody) {
        return calcBetsForCurrentGamesAndAddToBuilder(matches, emailBody, null);
    }

    /*
     * Used to calculate whether we have a good bet for both current games and also missed games, so we only need to change this function for both.
     */
    private static void calculateIfGoodBetAndAddToTreeSet(MatchToPredict match, HashSet<String> allowedBookies, TreeSet<String> homeWin, TreeSet<String> awayWin) {
        double[] ourPredictions = match.getOurPredictions();
        if (ourPredictions.length != 3) throw new RuntimeException("We haven't calculated our predictions properly.");

        int biggestIndex = -1;
        double biggestProb = -1;
        double secondBiggestProb = -1;

        for (int i = 0; i<ourPredictions.length; i++) {
            if (ourPredictions[i] > biggestProb) {
                secondBiggestProb = biggestProb;
                biggestProb = ourPredictions[i];
                biggestIndex = i;
            } else if (ourPredictions[i] > secondBiggestProb) {
                secondBiggestProb = ourPredictions[i];
            }
        }
        if (biggestIndex == -1 || biggestProb == -1 || secondBiggestProb == -1) throw new RuntimeException("Problem with getting the bet orders out of our predictions.");

        boolean winMostLikely = biggestIndex != 1;
        boolean isBiggerThanSecondByDelta = (biggestProb - secondBiggestProb > delta) && winMostLikely;

//            double ourWinRatio = ourPredictions[0]/ourPredictions[2];
//            double ourLossRatio = ourPredictions[2]/ourPredictions[0];



        //comparing our values to bookies
        HashMap<String, double[]> bookieOdds = match.getBookiesOdds();
        if (bookieOdds == null || bookieOdds.size() == 0) throw new RuntimeException("We have no bookie odds to compare our probabilities to! The game is " + match.getHomeTeamName() + " vs " + match.getAwayTeamName());

        for (String bookie: bookieOdds.keySet()) {

            if (allowedBookies.contains(bookie)) {
                double[] bookiesOdds = bookieOdds.get(bookie);
                double[] bookiesProbabilities = ConvertOdds.convert3OddsToProbabilities(bookiesOdds);

//                    double bookieWinRatio = bookiesOdds[0]/bookiesOdds[2];
//                    double bookieLossRatio = bookiesOdds[2]/bookiesOdds[0];
//                    boolean betOnWinRatio = ourWinRatio - bookieWinRatio > sigma;
//                    boolean betOnLossRatio = ourLossRatio - bookieLossRatio > sigma;

                boolean betOnBetterThanBookies = (biggestProb - bookiesProbabilities[biggestIndex] > gamma) && isBiggerThanSecondByDelta;

                //IMPORTANT: MUST NOT CHANGE FORMAT OF STRINGS TO BE ADDED TO TREESET AS WE'RE SORTING BASED ON THE BOOKIES PREDICTION BEING THE FIRST VALUE.
//                    if (betOnWinRatio) {
//                        homeWin.add(bookiesOdds[0] + " " + match.getHomeTeamName() + " " + bookie);
//                    }
//                    if (betOnLossRatio) {
//                        awayWin.add(bookiesOdds[2] + " " + match.getAwayTeamName() + " " + bookie);
//                    }
                if (betOnBetterThanBookies) {
                    if (biggestIndex == 0) homeWin.add(bookiesOdds[0] + " " + match.getHomeTeamName() + " " + bookie);
                    else if (biggestIndex == 2) awayWin.add(bookiesOdds[2] + " " + match.getAwayTeamName() + " " + bookie);
                }

            }
            else if (!OddsCheckerBookies.getAllBookies().contains(bookie)) throw new RuntimeException("There is a new bookie that has been added to oddschecker. Name is " + bookie);

        }
    }


    private final static double BASE_STAKE = 5;
    private final static double ZERO_STAKE = 0;

    private static void logGoodBetsAndAddToBuilder(StringBuilder stringBuilder, TreeSet<String> goodBets, MatchToPredict match,
                                                   ResultBetOn resultBetOn, WhenGameWasPredicted whenPredicted) {
        //adding best results to StringBuilder. TreeSet is already sorted so we can just add the first X records.
        int NUMB_BOOKIES_WE_WANT_IN_EMAIL = 3;


        if (goodBets.size() > 0) {
            Iterator<String> it = goodBets.descendingIterator();
            int numbAdded = 0;

            while (it.hasNext() && numbAdded < NUMB_BOOKIES_WE_WANT_IN_EMAIL) {
                String oddsDescriptor = it.next();

                if (numbAdded == 0) {
                    String[] partsOfOdds = oddsDescriptor.split(" ");
                    double odds = Double.parseDouble(partsOfOdds[0]);

//                DataSource.legacyLogBetPlaced(match.getHomeTeamName(), match.getAwayTeamName(), match.getSeasonKey(), HOME_WIN, odds, BASE_STAKE); //TODO: refactor this function.

                    MatchLog matchLog = new MatchLog(match, whenPredicted, resultBetOn, odds, BASE_STAKE);
                    DataSource.openConnection();
                    DataSource.logBetPlaced(matchLog);
                    DataSource.closeConnection();
                }


                stringBuilder.append(oddsDescriptor);
                stringBuilder.append(", ");
                numbAdded++;
            }
            stringBuilder.delete(stringBuilder.length() - 2, stringBuilder.length()); //remove comma + space
            stringBuilder.append(".");
        }
    }


    public static void missedGamesBetDecisionAndLog(ArrayList<MatchToPredict> matches) {
        HashSet<String> allowedBookies = new HashSet<>();
        allowedBookies.add(OddsCheckerBookies.BET365.getBookie());

        DataSource.openConnection();

        for (MatchToPredict match: matches) {
            TreeSet<String> homeWinTreeSet = new TreeSet<>();
            TreeSet<String> awayWinTreeSet = new TreeSet<>();
            calculateIfGoodBetAndAddToTreeSet(match, allowedBookies, homeWinTreeSet, awayWinTreeSet);

            MatchLog matchLog = getMatchLogForMissedPredictionGame(match, homeWinTreeSet, awayWinTreeSet);
            DataSource.logBetPlaced(matchLog);
        }

        DataSource.closeConnection();

    }

    private static MatchLog getMatchLogForMissedPredictionGame(MatchToPredict match, TreeSet<String> homeWin, TreeSet<String> awayWin) {
        //note: we will never decide to bet on both a home win and away win because we only try to bet on the single most likely outcome.
        if (homeWin.size() > 0) {
            return createMatchLogWhenGoodBetFound(match, homeWin, ResultBetOn.HOME_WIN, WhenGameWasPredicted.PREDICTED_LATER_ON);
        } else if(awayWin.size() > 0) {
            return createMatchLogWhenGoodBetFound(match, awayWin, ResultBetOn.AWAY_WIN, WhenGameWasPredicted.PREDICTED_LATER_ON);
        } else {
            return new MatchLog(match, WhenGameWasPredicted.PREDICTED_LATER_ON, ResultBetOn.NOT_BET_ON, -1, ZERO_STAKE);
        }

    }

    private static MatchLog createMatchLogWhenGoodBetFound(MatchToPredict match, TreeSet<String> betSet, ResultBetOn resultBetOn, WhenGameWasPredicted whenGameWasPredicted) {
        if (betSet.size() == 0) throw new RuntimeException("Trying to create match log for bet found when we didn't have any good bets");

        String oddsDescriptor = betSet.last(); //using last here as TreeSet is by default in ascending order and we want to store the best possible odds in db
        String[] descriptorParts = oddsDescriptor.split(" ");
        double odds = Double.parseDouble(descriptorParts[0]);

        return new MatchLog(match, whenGameWasPredicted, resultBetOn, odds, BASE_STAKE);
    }


    public static SimpleMatrix getOurPredictions() {
        if (logitPredictions == null) throw new RuntimeException("We haven't predicted yet. Call calcPredictions first.");
        return convertLogitsToProbability(logitPredictions);
    }

    public static void main(String[] args) {
        SimpleMatrix allTheta = new SimpleMatrix(new double[][] {
                {1,0.5,0.25,0.6,1.2,0.21,0.5},
                {1,0.15,0.2,0.3, 0.2,0.1,2},
                {1,0.45,0.5,0.77,1.6,0.5,0.3},
        });

        SimpleMatrix X = new SimpleMatrix(new double[][] {
                {0.5,0.45,0.3,1.2,0.21,0.5},
                {0.2,0.55,0.1,1.4,0.11,0.9},
                {0.9,0.45,0.7,0.72,0.41,0.3},
                {1.5,0.65,0.9,0.2,0.31,0.5},
                {7,0.75,0.6,1.2,0.01,4},
        });

        SimpleMatrix ones = new SimpleMatrix(new double[][] {
                {1},
                {1},
                {1},
                {1},
                {1}
        });

        SimpleMatrix combination = ones.combine(0,1,X);
        combination.print();


//        TreeSet<String> odds = new TreeSet<>();
//        odds.add("1.52 Liverpool Bet365");
//        odds.add("1.64 Liverpool BetVictor");
//        odds.add("1.29 Liverpool SkyBet");
//        odds.add("1.45 Liverpool MarathonBet");
//        odds.add("1.58 Liverpool Betfair");
//
//        Iterator it = odds.descendingIterator();
//
//        while (it.hasNext()) {
//            System.out.println(it.next());
//        }
    }

}
