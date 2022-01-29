package com.petermarshall.machineLearning.createData;
import com.petermarshall.DateHelper;
import com.petermarshall.database.datasource.DS_Get;
import com.petermarshall.database.datasource.DS_Main;
import com.petermarshall.machineLearning.createData.classes.TrainingMatch;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;

import static com.petermarshall.machineLearning.createData.WriteTrainingData.writeArrayOfStringsToCsv;

public class Main {
    public static void createTrainEval(Date removeBefore, Date removeAfter) {
        DS_Main.openProductionConnection();
        ArrayList<TrainingMatch> trainingMatches = CalcPastStats.getAllTrainingMatches();
        ArrayList<TrainingMatch> matchesSubset = removeTrainingMatches(removeBefore, removeAfter, trainingMatches);
        WriteTrainingData.writeDataOutToCsvFiles(matchesSubset, "train.csv", "eval.csv");
        DS_Main.closeConnection();
    }

    public static void createOneBigFile(Date removeBefore, Date removeAfter, String filename) {
        DS_Main.openProductionConnection();
        ArrayList<TrainingMatch> trainingMatches = CalcPastStats.getAllTrainingMatches();
        ArrayList<TrainingMatch> matchesSubset = removeTrainingMatches(removeBefore, removeAfter, trainingMatches);
        WriteTrainingData.writeAllDataOutToOneCsvFile(matchesSubset, filename);
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

    public static void createOneBigFileForPoissonRegression() {
        DS_Main.openProductionConnection();
        var data = DS_Get.getRawGameData();
        writeArrayOfStringsToCsv(data, "poisson_regression_data.csv");
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

    public static void main(String[] args) {
//        Date removeBefore = null;
//        Date removeAfter = DateHelper.createDateyyyyMMdd("2021", "07", "20");
//        createOneBigFile(null, removeAfter, "allDataCarryOverLastSeason.csv");

//        Date removeBefore = DateHelper.createDateyyyyMMdd("2020", "08", "01");
//        Date removeAfter = null;
//        createOneBigFile(null, removeBefore, "toTrainOn_extended.csv");
//        createOneBigFile(removeBefore, null, "lastSeason_extended.csv");

//        //just getting the results for the 2019-20 season.
//        Date removeBefore = DateHelper.createDateyyyyMMdd("2019", "08", "01");
//        Date removeAfter = DateHelper.createDateyyyyMMdd("2021", "08", "01");
//        createOneBigFile(null, removeBefore, "toTrainOn_extended.csv");
//        createOneBigFile(removeBefore, null, "lastSeason_extended.csv");

        //just getting the results for the 2020-21 season.
//        Date removeBefore = DateHelper.createDateyyyyMMdd("2020", "08", "01");
//        Date removeAfter = DateHelper.createDateyyyyMMdd("2021", "08", "01");
//        createOneBigFile(null, removeBefore, "toTrainOn_extended.csv");
//        createOneBigFile(removeBefore, removeAfter, "lastSeason_extended.csv");

//        createOneBigFile(null, null, "allDataSpssWithNewFeatures.csv");

//        createOneBigFileForPoissonRegression();

//        Date removeBefore = DateHelper.createDateyyyyMMdd("2019", "08", "01");
//        Date removeAfter = DateHelper.createDateyyyyMMdd("2021", "08", "01");
//        createFileWithSimulatedMatchesByScore(null, removeBefore, "toTrainOn_extended_with_simulated_score.csv");
//        createOneBigFile(removeBefore, null, "lastSeason_extended.csv");

//        Date removeBefore = DateHelper.createDateyyyyMMdd("2019", "08", "01");
//        Date removeAfter = DateHelper.createDateyyyyMMdd("2021", "08", "01");
//        createFileWithSimulatedMatchesByResult(null, removeBefore, "toTrainOn_extended_with_simulated.csv");
//        createOneBigFile(removeBefore, null, "lastSeason_extended.csv");

        createFileWithSimulatedMatchesByScore(null, null, "train_final_model_score.csv");
    }

}
