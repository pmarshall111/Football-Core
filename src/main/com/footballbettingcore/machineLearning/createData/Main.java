package com.footballbettingcore.machineLearning.createData;
import com.footballbettingcore.database.datasource.DS_Get;
import com.footballbettingcore.database.datasource.DS_Main;
import com.footballbettingcore.machineLearning.createData.classes.TrainingMatch;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;

import static com.footballbettingcore.machineLearning.createData.WriteTrainingData.writeArrayOfStringsToCsv;

public class Main {
    public static void main(String[] args) {
        // Generate files for a test and training set.
//        Date removeBefore = DateHelper.createDateyyyyMMdd("2020", "08", "01");
//        Date removeAfter = DateHelper.createDateyyyyMMdd("2021", "08", "01");
//        createFileWithSimulatedMatchesByResult(null, removeBefore, "train.csv");
//        createOneBigFile(removeBefore, removeAfter, "test.csv");

        // Generate file with all data for feature analysis or final model training
//        createOneBigFile(null, null, "allDataSpssWithNewFeatures.csv");
//        createFileWithSimulatedMatchesByScore(null, null, "train_final_model_score.csv");

        // Generate file for the Poisson Regression python code
//        createOneBigFileForPoissonRegression();
    }

    public static void createOneBigFile(Date removeBefore, Date removeAfter, String filename) {
        DS_Main.openProductionConnection();
        ArrayList<TrainingMatch> trainingMatches = CalcPastStats.getAllTrainingMatches();
        ArrayList<TrainingMatch> matchesSubset = removeTrainingMatches(removeBefore, removeAfter, trainingMatches);
        WriteTrainingData.writeAllDataOutToOneCsvFile(matchesSubset, filename);
        DS_Main.closeConnection();
    }

    // Matches are simulated using XG data, and a probability is added to assign a weight to the result.
    public static void createFileWithSimulatedMatchesByResult(Date removeBefore, Date removeAfter, String filename) {
        DS_Main.openProductionConnection();
        ArrayList<TrainingMatch> trainingMatches = CalcPastStats.getAllTrainingMatches();
        ArrayList<TrainingMatch> matchesSubset = removeTrainingMatches(removeBefore, removeAfter, trainingMatches);
        matchesSubset.addAll(SimulateMatches.createSimulatedMatchesWithProbabilityOfResult(matchesSubset));
        matchesSubset.sort(Comparator.comparing(TrainingMatch::getKickoffTime));
        WriteTrainingData.writeAllDataOutToOneCsvFile(matchesSubset, filename);
        DS_Main.closeConnection();
    }

    // Matches are simulated using XG data, and a probability is added to assign a weight to the score.
    public static void createFileWithSimulatedMatchesByScore(Date removeBefore, Date removeAfter, String filename) {
        DS_Main.openProductionConnection();
        ArrayList<TrainingMatch> trainingMatches = CalcPastStats.getAllTrainingMatches();
        ArrayList<TrainingMatch> matchesSubset = removeTrainingMatches(removeBefore, removeAfter, trainingMatches);
        ArrayList<TrainingMatch> simGames = SimulateMatches.createSimulatedMatchesWithProbabilityOfScores(matchesSubset);
        matchesSubset.addAll(simGames);
        matchesSubset.sort(Comparator.comparing(TrainingMatch::getKickoffTime));
        WriteTrainingData.writeAllDataOutToOneCsvFile(matchesSubset, filename);
        DS_Main.closeConnection();
    }

    public static void createOneBigFileForPoissonRegression() {
        DS_Main.openProductionConnection();
        var data = DS_Get.getRawGameData();
        writeArrayOfStringsToCsv(data, "poisson_regression_data.csv");
        DS_Main.closeConnection();
    }

    public static ArrayList<TrainingMatch> removeTrainingMatches(Date removeBefore, Date removeAfter, ArrayList<TrainingMatch> origList) {
        ArrayList<TrainingMatch> matchesSubset = new ArrayList<>();
        for (TrainingMatch match: origList) {
            Date kickOff = match.getKickoffTime();
            if ((removeBefore == null || kickOff.after(removeBefore) || kickOff.equals(removeBefore)) &&
                    (removeAfter == null || kickOff.before(removeAfter) || kickOff.equals(removeAfter))) {
                matchesSubset.add(match);
            }
        }
        return matchesSubset;
    }
}
