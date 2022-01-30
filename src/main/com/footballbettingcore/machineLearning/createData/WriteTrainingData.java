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
                featuresWriter.append(getCsvLine(match.getHomeScore(), match.getAwayScore(), match.getProbability(), match.getGameId(), match.getFeatures()));
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
                featuresWriter.append(getCsvLine(-1, -1, -1, match.getDatabase_id(), match.getFeatures()));
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
                featuresWriter.append(getCsvLine(match.getHomeScore(), match.getAwayScore(), match.getProbability(), match.getGameId(), match.getFeaturesNoLineups()));
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
                featuresWriter.append(getCsvLine(-1, -1, -1, match.getDatabase_id(), match.getFeaturesNoLineups()));
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

    public static void writeMatchesToPredictOutToCsvFile(ArrayList<MatchToPredict> trainingData, String fileName) {
        writeFeaturesToPredictToCsv(trainingData, fileName);
        writeNoLineupsFeaturesToPredictToCsv(trainingData, fileName.replace(".csv", "noLineups.csv"));
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

    private static String getCsvLine(int homeScore, int awayScore, double resultProbability, int gameId, ArrayList<Double> features) {
        int result = homeScore > awayScore ? Result.HOME_WIN.getSqlIntCode() :
                    homeScore == awayScore ? Result.DRAW.getSqlIntCode() :
                    Result.AWAY_WIN.getSqlIntCode();
        return homeScore + "," + awayScore + "," + resultProbability + "," + gameId + "," + result + "," + features.stream().map(x -> x+"").collect(Collectors.joining(","));
    }
}
