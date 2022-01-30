package com.petermarshall.machineLearning.createData;

import com.petermarshall.machineLearning.createData.classes.MatchToPredict;
import com.petermarshall.machineLearning.createData.classes.TrainingMatch;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.stream.Collectors;

public class WriteTrainingData {
    private static final Logger logger = LogManager.getLogger(WriteTrainingData.class);

    public static void writeDataOutToCsvFiles(ArrayList<TrainingMatch> trainingData, String trainFileName, String testFileName) {
        Collections.shuffle(trainingData);
        ArrayList<TrainingMatch> trainingDataSet = new ArrayList<>(trainingData.subList(0, (int) (trainingData.size()*0.7)));
        ArrayList<TrainingMatch> testingDataSet = new ArrayList<>(trainingData.subList((int) (trainingData.size()*0.7), trainingData.size()));

        writeFeaturesToCsv(trainingDataSet, trainFileName);
        writeFeaturesToCsv(testingDataSet, testFileName);
        writeNoLineupsFeaturesToCsv(trainingDataSet, "noLineups_"+trainFileName);
        writeNoLineupsFeaturesToCsv(testingDataSet, "noLineups_"+testFileName);
    }

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

    private static String getCsvLine(int homeScore, int awayScore, double resultProbability, int gameId, ArrayList<Double> features) {
        return homeScore + "," + awayScore + "," + resultProbability + "," + gameId + "," + features.stream().map(x -> x+"").collect(Collectors.joining(","));
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
}
