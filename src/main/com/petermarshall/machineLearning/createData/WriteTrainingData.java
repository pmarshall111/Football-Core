package com.petermarshall.machineLearning.createData;

import com.petermarshall.machineLearning.createData.classes.TrainingMatch;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.stream.Collectors;

public class WriteTrainingData {
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
        try (FileWriter featuresWriter = new FileWriter(fileName);
             FileWriter oddsWriter = new FileWriter("odds"+fileName)) {
            for (int i = 0; i < trainingData.size(); i++) {
                TrainingMatch match = trainingData.get(i);
                ArrayList<Double> features = match.getFeatures();
                String csv = features.stream().map(x -> x+"").collect(Collectors.joining(","));
                csv = match.getHomeScore() + "," + match.getAwayScore() + "," + match.getProbability() + "," + match.getGameId() + "," + csv; // adding the score to the front of the csv for the cost function
                featuresWriter.append(csv);
                String odds = Arrays.stream(match.getOdds()).mapToObj(x -> x+"").collect(Collectors.joining(","));
//                featuresWriter.append(","+odds+","+(match.getOdds()[0] - match.getOdds()[2]));
//                featuresWriter.append(","+ Arrays.stream(match.getOdds()).mapToObj(x -> (1/x)+"").collect(Collectors.joining(",")));
                oddsWriter.append(odds);
                if (i != trainingData.size()-1) {
                    featuresWriter.append("\n");
                    oddsWriter.append("\n");
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void writeNoLineupsFeaturesToCsv(ArrayList<TrainingMatch> trainingData, String fileName) {
        try (FileWriter featuresWriter = new FileWriter(fileName);
             FileWriter oddsWriter = new FileWriter("odds"+fileName)) {
            for (int i = 0; i < trainingData.size(); i++) {
                TrainingMatch match = trainingData.get(i);
                ArrayList<Double> features = match.getFeaturesNoLineups();
                String csv = features.stream().map(x -> x+"").collect(Collectors.joining(","));
                csv = match.getHomeScore() + "," + match.getAwayScore() + "," + match.getProbability() + "," + match.getGameId() + "," + csv; // adding the score to the front of the csv for the cost function
                featuresWriter.append(csv);
                String odds = Arrays.stream(match.getOdds()).mapToObj(x -> x+"").collect(Collectors.joining(","));
                oddsWriter.append(odds);
                if (i != trainingData.size()-1) {
                    featuresWriter.append("\n");
                    oddsWriter.append("\n");
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void writeFeaturesToCsvWithGameIdAtEndOfRow(ArrayList<TrainingMatch> trainingData, String fileName) {
        try (FileWriter featuresWriter = new FileWriter(fileName);
             FileWriter oddsWriter = new FileWriter("odds"+fileName)) {
            for (int i = 0; i < trainingData.size(); i++) {
                TrainingMatch match = trainingData.get(i);
                ArrayList<Double> features = match.getFeaturesNoLineups();
                String csv = features.stream().map(x -> x+"").collect(Collectors.joining(","));
                csv += ","+match.getGameId();
                featuresWriter.append(csv);
                String odds = Arrays.stream(match.getOdds()).mapToObj(x -> x+"").collect(Collectors.joining(","));
                oddsWriter.append(odds);
                if (i != trainingData.size()-1) {
                    featuresWriter.append("\n");
                    oddsWriter.append("\n");
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void writeNoLineupsFeaturesToCsvWithGameIdAtEndOfRow(ArrayList<TrainingMatch> trainingData, String fileName) {
        try (FileWriter featuresWriter = new FileWriter(fileName);
             FileWriter oddsWriter = new FileWriter("odds"+fileName)) {
            for (int i = 0; i < trainingData.size(); i++) {
                TrainingMatch match = trainingData.get(i);
                ArrayList<Double> features = match.getFeaturesNoLineups();
                String csv = features.stream().map(x -> x+"").collect(Collectors.joining(","));
                csv += ","+match.getGameId();
                featuresWriter.append(csv);
                String odds = Arrays.stream(match.getOdds()).mapToObj(x -> x+"").collect(Collectors.joining(","));
                oddsWriter.append(odds);
                if (i != trainingData.size()-1) {
                    featuresWriter.append("\n");
                    oddsWriter.append("\n");
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

    public static void writeAllDataOutToOneCsvFileWithGameIdAtEndOfRow(ArrayList<TrainingMatch> trainingData, String fileName) {
        writeFeaturesToCsvWithGameIdAtEndOfRow(trainingData, fileName);
        writeNoLineupsFeaturesToCsvWithGameIdAtEndOfRow(trainingData, "nolineups_"+fileName);
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
}
