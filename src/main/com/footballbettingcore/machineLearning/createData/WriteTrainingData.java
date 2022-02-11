package com.footballbettingcore.machineLearning.createData;

import com.footballbettingcore.database.Result;
import com.footballbettingcore.machineLearning.createData.classes.MatchToPredict;
import com.footballbettingcore.machineLearning.createData.classes.TrainingMatch;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.stream.Collectors;

public class WriteTrainingData {
    private static final Logger logger = LogManager.getLogger(WriteTrainingData.class);

    public static void writeFeaturesToCsv(ArrayList<TrainingMatch> trainingData, String fileName) {
        try (FileWriter featuresWriter = new FileWriter(fileName)) {
            for (int i = 0; i < trainingData.size(); i++) {
                TrainingMatch match = trainingData.get(i);
                featuresWriter.append(getOddsLine(match.getOdds()) + ",");
                featuresWriter.append(getMatchDataLine(match.getHomeScore(), match.getAwayScore(), match.getHomeXG(),
                        match.getAwayXG(), match.getXgSimulatedProbabilties(), match.getFiveThirtyEightProbabilities(), match.getProbability(), match.getGameId()) + ",");
                featuresWriter.append(getFeaturesLine(match.getFeatures()));
                if (i != trainingData.size()-1) {
                    featuresWriter.append("\n");
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void writeFeaturesToPredictToCsv(ArrayList<MatchToPredict> trainingData, String fileName) {
        try (FileWriter featuresWriter = new FileWriter(fileName)) {
            for (int i = 0; i < trainingData.size(); i++) {
                MatchToPredict match = trainingData.get(i);
                featuresWriter.append(getOddsLineForMatchToPredict(match) + ",");
                // TODO: Need to also add code to get the 538 predictions when we're predicting games if it turns out to be worth doing
                featuresWriter.append(getMatchDataLine(-1, -1, -1, -1, new double[]{-1,-1,-1}, new double[]{-1,-1,-1}, -1, match.getDatabase_id()) + ",");
                featuresWriter.append(getFeaturesLine(match.getFeatures()));
                if (i != trainingData.size()-1) {
                    featuresWriter.append("\n");
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void writeNoLineupsFeaturesToCsv(ArrayList<TrainingMatch> trainingData, String fileName) {
        try (FileWriter featuresWriter = new FileWriter(fileName)) {
            for (int i = 0; i < trainingData.size(); i++) {
                TrainingMatch match = trainingData.get(i);
                featuresWriter.append(getOddsLine(match.getOdds()) + ",");
                featuresWriter.append(getMatchDataLine(match.getHomeScore(), match.getAwayScore(), match.getHomeXG(),
                        match.getAwayXG(), match.getXgSimulatedProbabilties(), match.getFiveThirtyEightProbabilities(),
                        match.getProbability(), match.getGameId()) + ",");
                featuresWriter.append(getFeaturesLine(match.getFeaturesNoLineups()));
                if (i != trainingData.size()-1) {
                    featuresWriter.append("\n");
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void writeNoLineupsFeaturesToPredictToCsv(ArrayList<MatchToPredict> trainingData, String fileName) {
        try (FileWriter featuresWriter = new FileWriter(fileName)) {
            for (int i = 0; i < trainingData.size(); i++) {
                MatchToPredict match = trainingData.get(i);
                featuresWriter.append(getOddsLineForMatchToPredict(match) + ",");
                featuresWriter.append(getMatchDataLine(-1, -1, -1, -1, new double[]{-1,-1,-1},
                        new double[]{-1,-1,-1}, -1, match.getDatabase_id()) + ",");
                featuresWriter.append(getFeaturesLine(match.getFeaturesNoLineups()));
                if (i != trainingData.size()-1) {
                    featuresWriter.append("\n");
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void writeAllDataOutToOneCsvFile(ArrayList<TrainingMatch> trainingData, String fileName) {
        writeFeaturesToCsv(trainingData, fileName);
        writeNoLineupsFeaturesToCsv(trainingData, "nolineups_"+fileName);
    }

    public static void writeMatchesToPredictOutToCsvFile(ArrayList<MatchToPredict> trainingData,
                                                         String featuresFileName, String noLineupFeaturesFineName) {
        writeFeaturesToPredictToCsv(trainingData, featuresFileName);
        writeNoLineupsFeaturesToPredictToCsv(trainingData, noLineupFeaturesFineName);
    }

    public static void writeArrayOfStringsToCsv(ArrayList<ArrayList<String>> rows, String fileName) {
        try (FileWriter featuresWriter = new FileWriter(fileName);) {
            for (ArrayList<String> row : rows) {
                String csvRow = String.join(",", row) + "\n";
                featuresWriter.append(csvRow);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static String getOddsLineForMatchToPredict(MatchToPredict match) {
        HashMap<String, double[]> odds = match.getBookiesOdds();
        if (odds == null || odds.size() == 0) {
            logger.warn(match.getMatchString() + " has no odds");
            return "-1,-1,-1";
        }
        double[] firstOdds = odds.values().iterator().next();
        return getOddsLine(firstOdds);
    }

    private static String getOddsLine(double [] odds) {
        return Arrays.stream(odds).mapToObj(x -> x+"").collect(Collectors.joining(","));
    }

    private static String getMatchDataLine(int homeScore, int awayScore, double homeXg, double awayXg,
                                           double[] xgSimulatedResultProbabilities, double[] fiveThirtyEightProbabilities,
                                           double resultProbability, int gameId) {
        int result = homeScore > awayScore ? Result.HOME_WIN.getSqlIntCode() :
                     homeScore == awayScore ? Result.DRAW.getSqlIntCode() :
                     Result.AWAY_WIN.getSqlIntCode();
        return homeScore + "," + awayScore + "," + homeXg + "," + awayXg + "," +
                xgSimulatedResultProbabilities[0] + "," + xgSimulatedResultProbabilities[1] + "," + xgSimulatedResultProbabilities[2] + "," +
                fiveThirtyEightProbabilities[0] + "," + fiveThirtyEightProbabilities[1] + "," + fiveThirtyEightProbabilities[2] + "," +
                resultProbability + "," + gameId + "," + result;
    }

    private static String getFeaturesLine(ArrayList<Double> features) {
        return features.stream().map(x -> x+"").collect(Collectors.joining(","));
    }
}
