package com.petermarshall.machineLearning.createData;

import com.petermarshall.machineLearning.createData.classes.TrainingMatch;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.stream.Collectors;

public class WriteTrainingData {
    //INFO: (written 2019) current status is that AllMatchData function is the best. AllMatchDataPlusCleanSheets also performs quite well
    //INFO: (written 06/2020) writeFeaturesToCsv performs well
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

    public static void writeNoLineupsFeaturesToCsv(ArrayList<TrainingMatch> trainingData, String fileName) {
        try (FileWriter featuresWriter = new FileWriter(fileName);
             FileWriter oddsWriter = new FileWriter("odds"+fileName)) {
            for (int i = 0; i < trainingData.size(); i++) {
                TrainingMatch match = trainingData.get(i);
                ArrayList<Double> features = match.getFeaturesNoLineups();
                String csv = features.stream().map(x -> x+"").collect(Collectors.joining(","));
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
    }



    //legacy below




    /*
     * Method will create csv file from training data. Possible to only include data between certain dates into csv file.
     *
     * NOTE: IF CHANGING ANY ORDERING IN THIS METHOD, DATA WILL ALSO HAVE TO BE REORDERED IN GetMatchesFromDb.addLegacyFeaturesToMatchesToPredict().
     * OTHERWISE WE WILL CREATE A PREDICTION WITH WEIGHTS THAT HAVE BEEN TRAINED ON DIFFERENT FEATURES.
     */
    private static void writeDataToCSVFile(String fileName, Date earliestDate, Date latestDate, ArrayList<TrainingMatch> trainingData,
                                           String delimiter, boolean includeTitles) {

        try (FileWriter writer = new FileWriter(fileName)) {
            if (includeTitles) {

                ArrayList<Double> features = trainingData.get(0).getFeatures();
                //ONLY IN HERE TO MAKE WEKA HAPPY THAT THE FIRST ROW IS THE TITLES
                for (int i = 1; i <= features.size(); i++) {
                    writer.append(i + delimiter);
                }
                writer.append("\n");
            }

            for (int i = 0; i<trainingData.size(); i++) {
                TrainingMatch match = trainingData.get(i);
                Date kickoffTime = match.getKickoffTime();
                if (earliestDate != null && kickoffTime.before(earliestDate)) continue;
                else if (latestDate != null && kickoffTime.after(latestDate)) continue;

                ArrayList<Double> features = match.getFeatures();
                for (double f : features) {
                    writer.append(f + delimiter);
                }
            }

        } catch (IOException e) {}
    }

    private static void writeSubtractedDataToCSVFile(String fileName, ArrayList<TrainingMatch> trainingData, String delimiter) {
        try (FileWriter writer = new FileWriter(fileName)) {

            for (int i = 0; i<trainingData.size(); i++) {
                TrainingMatch match = trainingData.get(i);

                for (int pow = 1; pow<=2; pow++) {
                    //home total
//                    writer.append(Math.pow(match.getHomeTeamAvgGoalsFor(), pow) + delimiter);
//                    writer.append(Math.pow(match.getHomeTeamAvgXGF(), pow) + delimiter);
//                    writer.append(Math.pow(match.getHomeTeamWeightedAvgXGF(), pow) + delimiter);
                    writer.append(Math.pow(match.getHomeTeamFormGoalsFor(), pow) + delimiter);
                    writer.append(Math.pow(match.getHomeTeamFormXGF(), pow) + delimiter);
                    writer.append(Math.pow(match.getHomeTeamFormWeightedXGF(), pow) + delimiter);

                    //away total
//                    writer.append(Math.pow(match.getAwayTeamAvgGoalsFor(), pow) + delimiter);
//                    writer.append(Math.pow(match.getAwayTeamAvgXGF(), pow) + delimiter);
//                    writer.append(Math.pow(match.getAwayTeamWeightedAvgXGF(), pow) + delimiter);
                    writer.append(Math.pow(match.getAwayTeamFormGoalsFor(), pow) + delimiter);
                    writer.append(Math.pow(match.getAwayTeamFormXGF(), pow) + delimiter);
                    writer.append(Math.pow(match.getAwayTeamFormWeightedXGF(), pow) + delimiter);

                    //home @ home
//                    writer.append(Math.pow(match.getHomeTeamAvgHomeGoalsFor(), pow) + delimiter);
//                    writer.append(Math.pow(match.getHomeTeamAvgHomeXGF(), pow) + delimiter);
//                    writer.append(Math.pow(match.getHomeTeamWeightedAvgHomeXGF(), pow) + delimiter);
//                    writer.append(Math.pow(match.getHomeTeamHomeFormGoalsFor(), pow) + delimiter);
//                    writer.append(Math.pow(match.getHomeTeamHomeFormXGF(), pow) + delimiter);
//                    writer.append(Math.pow(match.getHomeTeamHomeFormWeightedXGF(), pow) + delimiter);

                    //away @ away
//                    writer.append(Math.pow(match.getAwayTeamAvgAwayGoalsFor(), pow) + delimiter);
//                    writer.append(Math.pow(match.getAwayTeamAvgAwayXGF(), pow) + delimiter);
//                    writer.append(Math.pow(match.getAwayTeamWeightedAvgAwayXGF(), pow) + delimiter);
//                    writer.append(Math.pow(match.getAwayTeamAwayFormGoalsFor(), pow) + delimiter);
//                    writer.append(Math.pow(match.getAwayTeamAwayFormXGF(), pow) + delimiter);
//                    writer.append(Math.pow(match.getAwayTeamAwayFormWeightedXGF(), pow) + delimiter);
                    
                    
                    
                    //prediction stats home total
                    //TODO: could potentially create another abstraction here by combining the home with away to get a predicted score for each metric. i.e. create a GD prediction
                    
                    //INFO: seemed to perform better when we just included powers for goals. Perhaps we should only include powers for continuous data? For points it's possibly the wrong approach.
                    //TODO: try powers just for continuous data (i.e. remove points)
                    //TODO: try combining powers... which ones??????


//                    writer.append(Math.pow(match.getHomeTeamAvgGoalsFor() - match.getAwayTeamAvgGoalsAgainst(), pow) + delimiter); //avg g - away avg gA
//                    writer.append(Math.pow(match.getHomeTeamAvgXGF() - match.getAwayTeamAvgXGA(), pow) + delimiter);
//                    writer.append(Math.pow(match.getHomeTeamWeightedAvgXGF() - match.getAwayTeamWeightedAvgXGA(), pow) + delimiter); //expected home team goals
                    writer.append(Math.pow(match.getHomeTeamFormGoalsFor() - match.getAwayTeamFormGoalsAgainst(), pow) + delimiter);
                    writer.append(Math.pow(match.getHomeTeamFormXGF() - match.getAwayTeamFormXGA(), pow) + delimiter);
                    writer.append(Math.pow(match.getHomeTeamFormWeightedXGF() - match.getAwayTeamFormWeightedXGA(), pow) + delimiter);

                    //prediction stats away total
//                    writer.append(Math.pow(match.getAwayTeamAvgGoalsFor() - match.getHomeTeamAvgGoalsAgainst(), pow) + delimiter); //avg g - away avg gA
//                    writer.append(Math.pow(match.getAwayTeamAvgXGF() - match.getHomeTeamAvgXGA(), pow) + delimiter);
//                    writer.append(Math.pow(match.getAwayTeamWeightedAvgXGF() - match.getHomeTeamWeightedAvgXGA(), pow) + delimiter); //expected home team goals
                    writer.append(Math.pow(match.getAwayTeamFormGoalsFor() - match.getHomeTeamFormGoalsAgainst(), pow) + delimiter);
                    writer.append(Math.pow(match.getAwayTeamFormXGF() - match.getHomeTeamFormXGA(), pow) + delimiter);
                    writer.append(Math.pow(match.getAwayTeamFormWeightedXGF() - match.getHomeTeamFormWeightedXGA(), pow) + delimiter);

                    //prediction stats home @ home vs away

//                    writer.append(Math.pow(match.getHomeTeamAvgHomeGoalsFor() - match.getAwayTeamAvgAwayGoalsAgainst(), pow) + delimiter);
//                    writer.append(Math.pow(match.getHomeTeamAvgHomeXGF() - match.getAwayTeamAvgAwayXGA(), pow) + delimiter);
//                    writer.append(Math.pow(match.getHomeTeamWeightedAvgHomeXGF() - match.getAwayTeamWeightedAvgAwayXGA(), pow) + delimiter);
//                    writer.append(Math.pow(match.getHomeTeamHomeFormGoalsFor() - match.getAwayTeamAwayFormGoalsAgainst(), pow) + delimiter);
//                    writer.append(Math.pow(match.getHomeTeamHomeFormXGF() - match.getAwayTeamAwayFormXGA(), pow) + delimiter);
//                    writer.append(Math.pow(match.getHomeTeamHomeFormWeightedXGF() - match.getAwayTeamAwayFormWeightedXGA(), pow) + delimiter);

                    //prediction stats away @ away vs home

//                    writer.append(Math.pow(match.getAwayTeamAvgAwayGoalsFor() - match.getHomeTeamAvgHomeGoalsAgainst(), pow) + delimiter);
//                    writer.append(Math.pow(match.getAwayTeamAvgAwayXGF() - match.getHomeTeamAvgHomeXGA(), pow) + delimiter);
//                    writer.append(Math.pow(match.getAwayTeamWeightedAvgAwayXGF() - match.getHomeTeamWeightedAvgHomeXGA(), pow) + delimiter);
//                    writer.append(Math.pow(match.getAwayTeamAwayFormGoalsFor() - match.getHomeTeamHomeFormGoalsAgainst(), pow) + delimiter);
//                    writer.append(Math.pow(match.getAwayTeamAwayFormXGF() - match.getHomeTeamHomeFormXGA(), pow) + delimiter);
//                    writer.append(Math.pow(match.getAwayTeamAwayFormWeightedXGF() - match.getHomeTeamHomeFormWeightedXGA(), pow) + delimiter); //24

                    //game gd stats total
                    writer.append(Math.pow(match.getHomeTeamFormWeightedXGF() - match.getAwayTeamFormWeightedXGA(), pow) - Math.pow(match.getAwayTeamFormWeightedXGF() - match.getHomeTeamFormWeightedXGA(), pow) + delimiter);
//                    writer.append(Math.pow(match.getHomeTeamHomeFormWeightedXGF() - match.getAwayTeamAwayFormWeightedXGA(), pow) - Math.pow(match.getAwayTeamAwayFormWeightedXGF() - match.getHomeTeamHomeFormWeightedXGA(), pow) + delimiter);
                    
                    
                    //points
//                    writer.append(Math.pow(match.getAvgHomeTeamPoints(),pow) + delimiter);
//                    writer.append(Math.pow(match.getAvgAwayTeamPoints(),pow) + delimiter);
//                    writer.append(Math.pow(match.getAvgHomeTeamHomePoints(),pow) + delimiter);
//                    writer.append(Math.pow(match.getAvgAwayTeamAwayPoints(),pow) + delimiter);
//                    writer.append(Math.pow(match.getLast5HomeTeamPoints(),pow) + delimiter);
//                    writer.append(Math.pow(match.getLast5AwayTeamPoints(),pow) + delimiter);
//                    writer.append(Math.pow(match.getLast5HomeTeamHomePoints(),pow) + delimiter);
//                    writer.append(Math.pow(match.getLast5AwayTeamAwayPoints(),pow) + delimiter); //32
//
//                    //scored & conceeded first
//                    writer.append(Math.pow(match.getIfScoredFirstHomeTeamPoints(),pow) + delimiter);
//                    writer.append(Math.pow(match.getIfScoredFirstAwayTeamPoints(),pow) + delimiter);
//                    writer.append(Math.pow(match.getIfScoredFirstAtHomeHomeTeamPoints(),pow) + delimiter);
//                    writer.append(Math.pow(match.getIfScoredFirstAtAwayAwayTeamPoints(),pow) + delimiter);
//                    writer.append(Math.pow(match.getIfConceededFirstHomeTeamPoints(),pow) + delimiter);
//                    writer.append(Math.pow(match.getIfConceededFirstAwayTeamPoints(),pow) + delimiter);
//                    writer.append(Math.pow(match.getIfConceededFirstAtHomeHomeTeamPoints(),pow) + delimiter);
//                    writer.append(Math.pow(match.getIfConceededFirstAtAwayAwayTeamPoints(),pow) + delimiter); //40

                    //team strength
                    writer.append(Math.pow(match.getHomeTeamStrength(),pow) + delimiter);
                    writer.append(Math.pow(match.getAwayTeamStrength(),pow) + delimiter);
//                    writer.append(Math.pow(match.getHomeTeamHomeStrength(),pow) + delimiter);
//                    writer.append(Math.pow(match.getAwayTeamAwayStrength(),pow) + delimiter); //44

                    //points vs opposition
//                    writer.append(Math.pow(match.getHomeTeamPointsAgainstOpposition(),pow) + delimiter);
//                    writer.append(Math.pow(match.getHomeTeamPointsAtHomeAgainstOpposition(),pow) + delimiter); //46

                    //lineup rating comparison
                    //NOTE: beneath 4 tend to cause underfitting.
//                    writer.append(Math.pow(match.getHomeTeamMinsWeightedLineupRating(),pow) + delimiter);
//                    writer.append(Math.pow(match.getAwayTeamMinsWeightedLineupRating(),pow) + delimiter);
//                    writer.append(Math.pow(match.getHomeTeamAtHomeMinsWeightedLineupRating(),pow) + delimiter);
//                    writer.append(Math.pow(match.getAwayTeamAtAwayMinsWeightedLineupRating(),pow) + delimiter);
                    writer.append(Math.pow(match.getHomeTeamMinsWeightedLineupRating() - match.getAwayTeamMinsWeightedLineupRating(),pow) + delimiter);
//                    writer.append(Math.pow(match.getHomeTeamAtHomeMinsWeightedLineupRating() - match.getAwayTeamAtAwayMinsWeightedLineupRating(),pow) + delimiter); //48
                }


//                double lineupRatingComp = match.getHomeTeamMinsWeightedLineupRating() - match.getAwayTeamMinsWeightedLineupRating();
//                double awayTeamStrength = match.getAwayTeamAwayStrength();
//                double awayAwayFWGD = match.getAwayTeamAwayFormWeightedXGF() - match.getHomeTeamHomeFormWeightedXGA();
//                double awayXGD = match.getAwayTeamAvgXGF() - match.getHomeTeamAvgXGA();
//                double homeHomeFWGD = match.getHomeTeamHomeFormWeightedXGF() - match.getAwayTeamAwayFormWeightedXGA();
//                double homeHomePts = match.getLast5HomeTeamHomePoints();
//                double avgHomePts = match.getAvgHomeTeamPoints();
//                double homeFormGD = match.getHomeTeamFormXGF() - match.getAwayTeamFormXGA();
//                double homeAvgXGD = match.getHomeTeamAvgXGF() - match.getAwayTeamAvgXGA();
//                double homeHomeFXGD = match.getHomeTeamHomeFormXGF() - match.getAwayTeamAwayFormXGA();
//                double last5AwayPts = match.getLast5AwayTeamPoints();
//                double awayTeamAvgPts = match.getAvgAwayTeamPoints();
//                double awayAwayXGD = match.getAwayTeamAvgAwayXGF() - match.getHomeTeamAvgHomeXGA();
//
//                double[] toCombine = new double[]{
//                        lineupRatingComp,  awayTeamStrength, homeHomePts, avgHomePts,
//                        last5AwayPts, awayTeamAvgPts
//                };
//
//                ArrayList<Double> newFeatures = combineFeatures(toCombine);
//                for (double feature: newFeatures) writer.append(feature + delimiter);
                
                //form weighted XGD
                double[] totalFormWeightXG = createCircularVals(match.getHomeTeamFormWeightedXGF() - match.getAwayTeamFormWeightedXGA(), match.getAwayTeamFormWeightedXGF() - match.getHomeTeamFormWeightedXGA());
//                double[] homeAwayFormWeightXG = createCircularVals(match.getHomeTeamHomeFormWeightedXGF() - match.getAwayTeamAwayFormWeightedXGA(), match.getAwayTeamAwayFormWeightedXGF() - match.getHomeTeamHomeFormWeightedXGA());
                
                //form xGF and points?
                double[] homeXGFAndPoints = createCircularVals(match.getHomeTeamFormXGF(), match.getAvgHomeTeamPoints());
                double[] awayXGFAndPoints = createCircularVals(match.getAwayTeamFormXGF(), match.getAvgAwayTeamPoints());
                double[] homeXGAAndPoints = createCircularVals(match.getHomeTeamFormXGA(), match.getAvgHomeTeamPoints());
                double[] awayXGAAndPoints = createCircularVals(match.getAwayTeamFormXGA(), match.getAvgAwayTeamPoints());
                
//                double[] homeHomeXGFAndPoints = createCircularVals(match.getHomeTeamHomeFormXGF(), match.getAvgHomeTeamHomePoints());
//                double[] awayAwayXGFAndPoints = createCircularVals(match.getAwayTeamAwayFormXGF(), match.getAvgAwayTeamAwayPoints());
//                double[] homeHomeXGAAndPoints = createCircularVals(match.getHomeTeamHomeFormXGA(), match.getAvgHomeTeamHomePoints());
//                double[] awayAwayXGAAndPoints = createCircularVals(match.getAwayTeamAwayFormXGA(), match.getAvgAwayTeamAwayPoints());
                
                //form weighted xGF and last5 pts
                double[] homeFormXGFAndLast5Pts = createCircularVals(match.getHomeTeamFormWeightedXGF(), match.getLast5HomeTeamPoints());
                double[] awayFormXGFAndLast5Pts = createCircularVals(match.getAwayTeamFormWeightedXGF(), match.getLast5AwayTeamPoints());
                double[] homeFormXGAAndLast5Pts = createCircularVals(match.getHomeTeamFormWeightedXGA(), match.getLast5HomeTeamPoints());
                double[] awayFormXGAAndLast5Pts = createCircularVals(match.getAwayTeamFormWeightedXGA(), match.getLast5AwayTeamPoints());
                
//                double[] homeHomeFormXGFAndLast5Pts = createCircularVals(match.getHomeTeamHomeFormWeightedXGF(), match.getLast5HomeTeamHomePoints());
//                double[] awayAwayFormXGFAndLast5Pts = createCircularVals(match.getAwayTeamAwayFormWeightedXGF(), match.getLast5AwayTeamAwayPoints());
//                double[] homeHomeFormXGAAndLast5Pts = createCircularVals(match.getHomeTeamHomeFormWeightedXGA(), match.getLast5HomeTeamHomePoints());
//                double[] awayAwayFormXGAAndLast5Pts = createCircularVals(match.getAwayTeamAwayFormWeightedXGF(), match.getLast5AwayTeamAwayPoints());


                //goals and XG
//                double[] homeXGFAndGoals = createCircularVals(match.getHomeTeamAvgXGF(), match.getHomeTeamAvgGoalsFor());
//                double[] awayXGFAndGoals = createCircularVals(match.getAwayTeamAvgXGF(), match.getAwayTeamAvgGoalsFor());
//                double[] homeXGAAndGoals = createCircularVals(match.getHomeTeamAvgXGA(), match.getHomeTeamAvgGoalsAgainst());
//                double[] awayXGAAndGoals = createCircularVals(match.getAwayTeamAvgXGA(), match.getAwayTeamAvgGoalsAgainst());
//
//                double[] homeHomeXGFAndGoals = createCircularVals(match.getHomeTeamAvgHomeXGF(), match.getHomeTeamAvgHomeGoalsFor());
//                double[] awayAwayXGFAndGoals = createCircularVals(match.getAwayTeamAvgAwayXGF(), match.getAwayTeamAvgAwayGoalsFor());
//                double[] homeHomeXGAAndGoals = createCircularVals(match.getHomeTeamAvgHomeXGA(), match.getHomeTeamAvgHomeGoalsAgainst());
//                double[] awayAwayXGAAndGoals = createCircularVals(match.getAwayTeamAvgAwayXGA(), match.getAwayTeamAvgAwayGoalsAgainst());

                double[] homeXGFAndGoals = createCircularVals(match.getHomeTeamFormXGF(), match.getHomeTeamFormGoalsFor());
                double[] awayXGFAndGoals = createCircularVals(match.getAwayTeamFormXGF(), match.getAwayTeamFormGoalsFor());
                double[] homeXGAAndGoals = createCircularVals(match.getHomeTeamFormXGA(), match.getHomeTeamFormGoalsAgainst());
                double[] awayXGAAndGoals = createCircularVals(match.getAwayTeamFormXGA(), match.getAwayTeamFormGoalsAgainst());

//                double[] homeHomeXGFAndGoals = createCircularVals(match.getHomeTeamHomeFormXGF(), match.getHomeTeamHomeFormGoalsFor());
//                double[] awayAwayXGFAndGoals = createCircularVals(match.getAwayTeamAwayFormXGF(), match.getAwayTeamAwayFormGoalsFor());
//                double[] homeHomeXGAAndGoals = createCircularVals(match.getHomeTeamHomeFormXGA(), match.getHomeTeamHomeFormGoalsAgainst());
//                double[] awayAwayXGAAndGoals = createCircularVals(match.getAwayTeamAwayFormXGA(), match.getAwayTeamAwayFormGoalsAgainst());

                double[][] addToFeatures = new double[][]{
                        totalFormWeightXG, //homeAwayFormWeightXG,
                        homeXGFAndPoints, awayXGFAndPoints, homeXGAAndPoints, awayXGAAndPoints,
                        //homeHomeXGFAndPoints, awayAwayXGFAndPoints, homeHomeXGAAndPoints, awayAwayXGAAndPoints,
                        homeFormXGFAndLast5Pts, awayFormXGFAndLast5Pts, homeFormXGAAndLast5Pts, awayFormXGAAndLast5Pts,
                        //homeHomeFormXGFAndLast5Pts, awayAwayFormXGFAndLast5Pts, homeHomeFormXGAAndLast5Pts, awayAwayFormXGAAndLast5Pts,
                        homeXGFAndGoals, awayXGFAndGoals, homeXGAAndGoals, awayXGAAndGoals//,
                        //homeHomeXGFAndGoals, awayAwayXGFAndGoals, homeHomeXGAAndGoals, awayAwayXGAAndGoals
                };

                for (int outer = 0; outer<addToFeatures.length; outer++) {
                    for (int inner = 0; inner<addToFeatures[outer].length; inner++) {
                        writer.append(addToFeatures[outer][inner] + delimiter);
                    }
                }
                

                writer.append(match.getAvgHomeTeamPoints() + delimiter);
                writer.append(match.getAvgAwayTeamPoints() + delimiter);
                writer.append(match.getAvgHomeTeamHomePoints() + delimiter);
                writer.append(match.getAvgAwayTeamAwayPoints() + delimiter);
                writer.append(match.getLast5HomeTeamPoints() + delimiter);
                writer.append(match.getLast5AwayTeamPoints() + delimiter);
                writer.append(match.getLast5HomeTeamHomePoints() + delimiter);
                writer.append(match.getLast5AwayTeamAwayPoints() + delimiter);

                writer.append(match.getIfScoredFirstHomeTeamPoints() + delimiter);
                writer.append(match.getIfScoredFirstAwayTeamPoints() + delimiter);
                writer.append(match.getIfScoredFirstAtHomeHomeTeamPoints() + delimiter);
                writer.append(match.getIfScoredFirstAtAwayAwayTeamPoints() + delimiter);
                writer.append(match.getIfConceededFirstHomeTeamPoints() + delimiter);
                writer.append(match.getIfConceededFirstAwayTeamPoints() + delimiter);
                writer.append(match.getIfConceededFirstAtHomeHomeTeamPoints() + delimiter);
                writer.append(match.getIfConceededFirstAtAwayAwayTeamPoints() + delimiter);

                writer.append(match.getHomeTeamPointsAgainstOpposition() + delimiter);
                writer.append(match.getHomeTeamPointsAtHomeAgainstOpposition() + delimiter);


                //odds
                writer.append(match.getHomeTeamProbability() + delimiter);
                writer.append(match.getDrawProbability() + delimiter);
                writer.append(match.getAwayTeamProbability() + delimiter);

                //result
                writer.append(match.getResult() + "");
                writer.append("\n");
            }

        } catch (IOException e) {}
    }
    
    private static double[] createCircularVals(double f1, double f2) {
        return new double[]{
                f1*f2, Math.pow(f1,2)*f2, f1*Math.pow(f2,2), Math.pow(f1,2)*Math.pow(f2,2)
        };
    }

    //added 4 new features to regular method. Form Weighted XGF for home + away teams for both total and home/away stats.
    private static void writeExtendedDataToCSVFile(String fileName, Date earliestDate, Date latestDate, ArrayList<TrainingMatch> trainingData,
                                                    String delimiter, boolean includeTitles) {

        try (FileWriter writer = new FileWriter(fileName)) {

            if (includeTitles) {
                //ONLY IN HERE TO MAKE WEKA HAPPY THAT THE FIRST ROW IS THE TITLES
                for (int i = 1; i <= 53; i++) {
                    writer.append(i + delimiter);
                }
                writer.append("\n");
            }

            for (int i = 0; i<trainingData.size(); i++) {
                TrainingMatch match = trainingData.get(i);

                Date kickoffTime = match.getKickoffTime();

                if (earliestDate != null && kickoffTime.before(earliestDate)) continue;
                else if (latestDate != null && kickoffTime.after(latestDate)) continue;


                //home team total stats
                writer.append(match.getHomeTeamAvgGoalsFor() + delimiter); //2
                writer.append(match.getHomeTeamAvgGoalsAgainst() + delimiter);
                writer.append(match.getHomeTeamAvgXGF() + delimiter);
                writer.append(match.getHomeTeamAvgXGA() + delimiter);
                writer.append(match.getHomeTeamWeightedAvgXGF() + delimiter);
                writer.append(match.getHomeTeamWeightedAvgXGA() + delimiter);
                writer.append(match.getAvgHomeTeamPoints() + delimiter);
                writer.append(match.getLast5HomeTeamPoints() + delimiter);
                writer.append(match.getIfScoredFirstHomeTeamPoints() + delimiter);
                writer.append(match.getIfConceededFirstHomeTeamPoints() + delimiter);
                writer.append(match.getHomeTeamPointsAgainstOpposition() + delimiter);
                writer.append(match.getHomeTeamMinsWeightedLineupRating() + delimiter);
                writer.append(match.getHomeTeamStrength() + delimiter); //14

                //home team home stats
                writer.append(match.getHomeTeamAvgHomeGoalsFor() + delimiter); //15
                writer.append(match.getHomeTeamAvgHomeGoalsAgainst() + delimiter);
                writer.append(match.getHomeTeamAvgHomeXGF() + delimiter);
                writer.append(match.getHomeTeamAvgHomeXGA() + delimiter);
                writer.append(match.getHomeTeamWeightedAvgHomeXGF() + delimiter);
                writer.append(match.getHomeTeamWeightedAvgHomeXGA() + delimiter);
                writer.append(match.getAvgHomeTeamHomePoints() + delimiter);
                writer.append(match.getLast5HomeTeamHomePoints() + delimiter);
                writer.append(match.getIfScoredFirstAtHomeHomeTeamPoints() + delimiter);
                writer.append(match.getIfConceededFirstAtHomeHomeTeamPoints() + delimiter);
                writer.append(match.getHomeTeamPointsAtHomeAgainstOpposition() + delimiter);
                writer.append(match.getHomeTeamAtHomeMinsWeightedLineupRating() + delimiter);
                writer.append(match.getHomeTeamHomeStrength() + delimiter); //27

                //awawy team total stats
                writer.append(match.getAwayTeamAvgGoalsFor() + delimiter); //28
                writer.append(match.getAwayTeamAvgGoalsAgainst() + delimiter);
                writer.append(match.getAwayTeamAvgXGF() + delimiter);
                writer.append(match.getAwayTeamAvgXGA() + delimiter);
                writer.append(match.getAwayTeamWeightedAvgXGF() + delimiter);
                writer.append(match.getAwayTeamWeightedAvgXGA() + delimiter);
                writer.append(match.getAvgAwayTeamPoints() + delimiter);
                writer.append(match.getLast5AwayTeamPoints() + delimiter);
                writer.append(match.getIfScoredFirstAwayTeamPoints() + delimiter);
                writer.append(match.getIfConceededFirstAwayTeamPoints() + delimiter);
                writer.append(match.getAwayTeamPointsAgainstOpposition() + delimiter);
                writer.append(match.getAwayTeamMinsWeightedLineupRating() + delimiter);
                writer.append(match.getAwayTeamStrength() + delimiter); //40

                //away team away stats
                writer.append(match.getAwayTeamAvgAwayGoalsFor() + delimiter); //41
                writer.append(match.getAwayTeamAvgAwayGoalsAgainst() + delimiter);
                writer.append(match.getAwayTeamAvgAwayXGF() + delimiter);
                writer.append(match.getAwayTeamAvgAwayXGA() + delimiter);
                writer.append(match.getAwayTeamWeightedAvgAwayXGF() + delimiter);
                writer.append(match.getAwayTeamWeightedAvgAwayXGA() + delimiter);
                writer.append(match.getAvgAwayTeamAwayPoints() + delimiter);
                writer.append(match.getLast5AwayTeamAwayPoints() + delimiter);
                writer.append(match.getIfScoredFirstAtAwayAwayTeamPoints() + delimiter);
                writer.append(match.getIfConceededFirstAtAwayAwayTeamPoints() + delimiter);
                writer.append(match.getAwayTeamPointsAtAwayAgainstOpposition() + delimiter);
                writer.append(match.getAwayTeamAtAwayMinsWeightedLineupRating() + delimiter);
                writer.append(match.getAwayTeamAwayStrength() + delimiter); //53

                //prediction stats
                writer.append(match.getHomeTeamWeightedAvgXGF() - match.getAwayTeamWeightedAvgXGA() + delimiter); //expected home team goals
                writer.append(match.getAwayTeamWeightedAvgXGF() - match.getHomeTeamWeightedAvgXGA() + delimiter); // expected away team goals
                writer.append(match.getHomeTeamWeightedAvgHomeXGF() - match.getAwayTeamWeightedAvgAwayXGA() + delimiter); //expected home team home goals
                writer.append(match.getAwayTeamWeightedAvgAwayXGF() - match.getHomeTeamWeightedAvgHomeXGA() + delimiter); // expected away team away goals
                
//                writer.append(Math.pow(match.getHomeTeamWeightedAvgXGF() - match.getAwayTeamWeightedAvgXGA(),2) + delimiter); //expected home team goals
//                writer.append(Math.pow(match.getAwayTeamWeightedAvgXGF() - match.getHomeTeamWeightedAvgXGA(),2) + delimiter); // expected away team goals
//                writer.append(Math.pow(match.getHomeTeamWeightedAvgHomeXGF() - match.getAwayTeamWeightedAvgAwayXGA(),2) + delimiter); //expected home team home goals
//                writer.append(Math.pow(match.getAwayTeamWeightedAvgAwayXGF() - match.getHomeTeamWeightedAvgHomeXGA(),2) + delimiter); // expected away team away goals



                writer.append(match.getHomeTeamFormWeightedXGF() - match.getAwayTeamFormWeightedXGA() + delimiter); //new value added. Basically the number of goals we expect home team to score based on all recent form.
//                writer.append(Math.pow(match.getHomeTeamFormWeightedXGF() - match.getAwayTeamFormWeightedXGA(),2) + delimiter);
                writer.append(match.getHomeTeamHomeFormWeightedXGF() - match.getAwayTeamAwayFormWeightedXGA() + delimiter);
//                writer.append(Math.pow(match.getHomeTeamHomeFormWeightedXGF() - match.getAwayTeamAwayFormWeightedXGA(),2) + delimiter);
                writer.append(match.getAwayTeamFormWeightedXGF() - match.getHomeTeamFormWeightedXGA() + delimiter);
//                writer.append(Math.pow(match.getAwayTeamFormWeightedXGF() - match.getHomeTeamFormWeightedXGA(),2) + delimiter);
                writer.append(match.getAwayTeamAwayFormWeightedXGF() - match.getHomeTeamHomeFormWeightedXGA() + delimiter);
//                writer.append(Math.pow(match.getAwayTeamAwayFormWeightedXGF() - match.getHomeTeamHomeFormWeightedXGA(),2) + delimiter);


                //powered vals
                double homeTeamTotalGF = match.getHomeTeamAvgGoalsFor();
                double awayTeamTotalLineupRating = match.getAwayTeamMinsWeightedLineupRating();
                double awayTeamTotalXGF = match.getAwayTeamAvgXGF();
                double awayTeamAwayPts = match.getAvgAwayTeamAwayPoints();
                double homeTeamTotalXGF = match.getHomeTeamAvgXGF();
                double homeTeamHomeGF = match.getHomeTeamAvgHomeGoalsFor();
                double homeTeamTotalLineupRating = match.getHomeTeamMinsWeightedLineupRating();
                double awayTeamTotalXGA = match.getAwayTeamAvgXGA();
                double homeTeamHomePts = match.getAvgHomeTeamHomePoints();
                double homeTeamWeightedXGA = match.getHomeTeamWeightedAvgXGA();
                //10
                double awayTeamAwayWeightedXGF = match.getAwayTeamWeightedAvgAwayXGF();
                double awayTeamAvfGF = match.getAwayTeamAvgGoalsFor();
                double homeTeamHomeXGF = match.getHomeTeamAvgHomeXGF();

                double[] toCombine = new double[]{homeTeamTotalGF, awayTeamTotalLineupRating, awayTeamTotalXGF, awayTeamAwayPts, homeTeamTotalXGF, homeTeamHomeGF,
                        homeTeamTotalLineupRating, awayTeamTotalXGA, homeTeamHomePts, homeTeamWeightedXGA, awayTeamAwayWeightedXGF, awayTeamAvfGF, homeTeamHomeXGF};


                ArrayList<Double> newFeatures = combineFeatures(toCombine);
                for (double feature: newFeatures) writer.append(feature + delimiter);


//                //odds
                writer.append(match.getHomeTeamProbability() + delimiter);
                writer.append(match.getDrawProbability() + delimiter);
                writer.append(match.getAwayTeamProbability() + delimiter);

                //result
                writer.append(match.getResult() + "");
                writer.append("\n");
            }

        } catch (IOException e) {}
    }


    /*
     * Going to try these selected features to find the most important, then add powered values to them.
     */
    private static void writeNewTryDataToCSVFile(String fileName, ArrayList<TrainingMatch> trainingData, String delimiter) {

        try (FileWriter writer = new FileWriter(fileName)) {

            for (int i = 0; i < trainingData.size(); i++) {
                TrainingMatch match = trainingData.get(i);

//                writer.append(match.getHomeTeamFormWeightedXGF() + delimiter);
//                writer.append(match.getHomeTeamFormWeightedXGA() + delimiter);
//                writer.append(match.getAwayTeamFormWeightedXGF() + delimiter);
//                writer.append(match.getAwayTeamFormWeightedXGF() + delimiter);
                
                writer.append(match.getAvgHomeTeamHomePoints() + delimiter);
                writer.append(match.getAvgAwayTeamAwayPoints() + delimiter);
                
                writer.append(match.getHomeTeamAvgHomeGoalsAgainst() + delimiter);
                writer.append(match.getAwayTeamAvgAwayGoalsFor() + delimiter);
                
                writer.append(match.getLast5HomeTeamPoints() + delimiter);
                writer.append(match.getLast5AwayTeamPoints()+ delimiter);
                
                writer.append(match.getHomeTeamAvgHomeXGF() + delimiter);
                writer.append(match.getHomeTeamAvgHomeXGA() + delimiter);
                writer.append(match.getAwayTeamAvgAwayXGF() + delimiter);
                writer.append(match.getAwayTeamAvgAwayXGA() + delimiter);

                writer.append(match.getHomeTeamStrength() + delimiter);
                writer.append(match.getAwayTeamStrength() + delimiter);
                writer.append(match.getHomeTeamMinsWeightedLineupRating() + delimiter);
                writer.append(match.getAwayTeamMinsWeightedLineupRating() + delimiter);

                writer.append(match.getHomeTeamPointsAgainstOpposition() + delimiter);

                writer.append(match.getIfConceededFirstHomeTeamPoints() + delimiter);
                writer.append(match.getIfConceededFirstAwayTeamPoints() + delimiter);
                
                writer.append(match.getHomeTeamWeightedAvgXGF() + delimiter);
                writer.append(match.getHomeTeamWeightedAvgXGA() + delimiter);
                writer.append(match.getAwayTeamWeightedAvgXGF() + delimiter);
                writer.append(match.getAwayTeamWeightedAvgXGA() + delimiter);


                //odds
                writer.append(match.getHomeTeamProbability() + delimiter);
                writer.append(match.getDrawProbability() + delimiter);
                writer.append(match.getAwayTeamProbability() + delimiter);

                //result
                writer.append(match.getResult() + "");
                writer.append("\n");
            }

        } catch (IOException e) {}


    }

    /*
     * Working quite well right now without any powers or combinations. just the base 85 features.
     *
     * TODO: add in features for how many clean sheets each team has, and also the average points per game of all opponents and average points per game of last 5 (at the time of the game).
     */
    private static void writeAllMatchDataToCSVFile(String fileName, ArrayList<TrainingMatch> trainingData, String delimiter) {

        try (FileWriter writer = new FileWriter(fileName)) {

            for (int i = 0; i < trainingData.size(); i++) {
                TrainingMatch match = trainingData.get(i);

                for (int pow = 1; pow<=1; pow++) {
                    //home team total stats
                    writer.append(Math.pow(match.getHomeTeamAvgGoalsFor(), pow) + delimiter); //2
                    writer.append(Math.pow(match.getHomeTeamAvgGoalsAgainst(), pow) + delimiter);
                    writer.append(Math.pow(match.getHomeTeamAvgXGF(), pow) + delimiter);
                    writer.append(Math.pow(match.getHomeTeamAvgXGA(), pow) + delimiter);
                    writer.append(Math.pow(match.getHomeTeamWeightedAvgXGF(), pow) + delimiter);
                    writer.append(Math.pow(match.getHomeTeamWeightedAvgXGA(), pow) + delimiter);
                    writer.append(Math.pow(match.getAvgHomeTeamPoints(), pow) + delimiter);
                    writer.append(Math.pow(match.getLast5HomeTeamPoints(), pow) + delimiter);
                    writer.append(Math.pow(match.getIfScoredFirstHomeTeamPoints(), pow) + delimiter);
                    writer.append(Math.pow(match.getIfConceededFirstHomeTeamPoints(), pow) + delimiter);
                    writer.append(Math.pow(match.getHomeTeamPointsAgainstOpposition(), pow) + delimiter);
                    writer.append(Math.pow(match.getHomeTeamMinsWeightedLineupRating(), pow) + delimiter);
                    writer.append(Math.pow(match.getHomeTeamStrength(), pow) + delimiter); //14

                    //home team home stats
                    writer.append(Math.pow(match.getHomeTeamAvgHomeGoalsFor(), pow) + delimiter); //15
                    writer.append(Math.pow(match.getHomeTeamAvgHomeGoalsAgainst(), pow) + delimiter);
                    writer.append(Math.pow(match.getHomeTeamAvgHomeXGF(), pow) + delimiter);
                    writer.append(Math.pow(match.getHomeTeamAvgHomeXGA(), pow) + delimiter);
                    writer.append(Math.pow(match.getHomeTeamWeightedAvgHomeXGF(), pow) + delimiter);
                    writer.append(Math.pow(match.getHomeTeamWeightedAvgHomeXGA(), pow) + delimiter);
                    writer.append(Math.pow(match.getAvgHomeTeamHomePoints(), pow) + delimiter);
                    writer.append(Math.pow(match.getLast5HomeTeamHomePoints(), pow) + delimiter);
                    writer.append(Math.pow(match.getIfScoredFirstAtHomeHomeTeamPoints(), pow) + delimiter);
                    writer.append(Math.pow(match.getIfConceededFirstAtHomeHomeTeamPoints(), pow) + delimiter);
                    writer.append(Math.pow(match.getHomeTeamPointsAtHomeAgainstOpposition(), pow) + delimiter);
                    writer.append(Math.pow(match.getHomeTeamAtHomeMinsWeightedLineupRating(), pow) + delimiter);
                    writer.append(Math.pow(match.getHomeTeamHomeStrength(), pow) + delimiter); //27

                    //awawy team total stats
                    writer.append(Math.pow(match.getAwayTeamAvgGoalsFor(), pow) + delimiter); //28
                    writer.append(Math.pow(match.getAwayTeamAvgGoalsAgainst(), pow) + delimiter);
                    writer.append(Math.pow(match.getAwayTeamAvgXGF(), pow) + delimiter);
                    writer.append(Math.pow(match.getAwayTeamAvgXGA(), pow) + delimiter);
                    writer.append(Math.pow(match.getAwayTeamWeightedAvgXGF(), pow) + delimiter);
                    writer.append(Math.pow(match.getAwayTeamWeightedAvgXGA(), pow) + delimiter);
                    writer.append(Math.pow(match.getAvgAwayTeamPoints(), pow) + delimiter);
                    writer.append(Math.pow(match.getLast5AwayTeamPoints(), pow) + delimiter);
                    writer.append(Math.pow(match.getIfScoredFirstAwayTeamPoints(), pow) + delimiter);
                    writer.append(Math.pow(match.getIfConceededFirstAwayTeamPoints(), pow) + delimiter);
                    writer.append(Math.pow(match.getAwayTeamPointsAgainstOpposition(), pow) + delimiter);
                    writer.append(Math.pow(match.getAwayTeamMinsWeightedLineupRating(), pow) + delimiter);
                    writer.append(Math.pow(match.getAwayTeamStrength(), pow) + delimiter); //40

                    //away team away stats
                    writer.append(Math.pow(match.getAwayTeamAvgAwayGoalsFor(), pow) + delimiter); //41
                    writer.append(Math.pow(match.getAwayTeamAvgAwayGoalsAgainst(), pow) + delimiter);
                    writer.append(Math.pow(match.getAwayTeamAvgAwayXGF(), pow) + delimiter);
                    writer.append(Math.pow(match.getAwayTeamAvgAwayXGA(), pow) + delimiter);
                    writer.append(Math.pow(match.getAwayTeamWeightedAvgAwayXGF(), pow) + delimiter);
                    writer.append(Math.pow(match.getAwayTeamWeightedAvgAwayXGA(), pow) + delimiter);
                    writer.append(Math.pow(match.getAvgAwayTeamAwayPoints(), pow) + delimiter);
                    writer.append(Math.pow(match.getLast5AwayTeamAwayPoints(), pow) + delimiter);
                    writer.append(Math.pow(match.getIfScoredFirstAtAwayAwayTeamPoints(), pow) + delimiter);
                    writer.append(Math.pow(match.getIfConceededFirstAtAwayAwayTeamPoints(), pow) + delimiter);
                    writer.append(Math.pow(match.getAwayTeamPointsAtAwayAgainstOpposition(), pow) + delimiter);
                    writer.append(Math.pow(match.getAwayTeamAtAwayMinsWeightedLineupRating(), pow) + delimiter);
                    writer.append(Math.pow(match.getAwayTeamAwayStrength(), pow) + delimiter); //53


                    //extra home team stats
                    writer.append(Math.pow(match.getHomeTeamAvgFormGoalsFor(), pow) + delimiter);
                    writer.append(Math.pow(match.getHomeTeamAvgFormGoalsAgainst(), pow) + delimiter);
                    writer.append(Math.pow(match.getHomeTeamAvgFormXGF(), pow) + delimiter);
                    writer.append(Math.pow(match.getHomeTeamAvgFormXGA(), pow) + delimiter);
                    writer.append(Math.pow(match.getHomeTeamAvgFormWeightedXGF(), pow) + delimiter);
                    writer.append(Math.pow(match.getHomeTeamAvgFormWeightedXGA(), pow) + delimiter);
                    writer.append(Math.pow(match.getHomeTeamAvgFormXGFLast5Games(), pow) + delimiter);
                    writer.append(Math.pow(match.getHomeTeamAvgFormXGALast5Games(), pow) + delimiter);

                    //extra home home stats
                    writer.append(Math.pow(match.getHomeTeamHomeAvgFormGoalsFor(), pow) + delimiter);
                    writer.append(Math.pow(match.getHomeTeamHomeAvgFormGoalsAgainst(), pow) + delimiter);
                    writer.append(Math.pow(match.getHomeTeamHomeAvgFormXGF(), pow) + delimiter);
                    writer.append(Math.pow(match.getHomeTeamHomeAvgFormXGA(), pow) + delimiter);
                    writer.append(Math.pow(match.getHomeTeamHomeAvgFormWeightedXGF(), pow) + delimiter);
                    writer.append(Math.pow(match.getHomeTeamHomeAvgFormWeightedXGA(), pow) + delimiter);
                    writer.append(Math.pow(match.getHomeTeamHomeAvgFormXGFLast5Games(), pow) + delimiter);
                    writer.append(Math.pow(match.getHomeTeamHomeAvgFormXGALast5Games(), pow) + delimiter);

                    //extra away stats
                    writer.append(Math.pow(match.getAwayTeamAvgFormGoalsFor(), pow) + delimiter);
                    writer.append(Math.pow(match.getAwayTeamAvgFormGoalsAgainst(), pow) + delimiter);
                    writer.append(Math.pow(match.getAwayTeamAvgFormXGF(), pow) + delimiter);
                    writer.append(Math.pow(match.getAwayTeamAvgFormXGA(), pow) + delimiter);
                    writer.append(Math.pow(match.getAwayTeamAvgFormWeightedXGF(), pow) + delimiter);
                    writer.append(Math.pow(match.getAwayTeamAvgFormWeightedXGA(), pow) + delimiter);
                    writer.append(Math.pow(match.getAwayTeamAvgFormXGFLast5Games(), pow) + delimiter);
                    writer.append(Math.pow(match.getAwayTeamAvgFormXGALast5Games(), pow) + delimiter);

                    //extra away away stats
                    writer.append(Math.pow(match.getAwayTeamAwayAvgFormGoalsFor(), pow) + delimiter);
                    writer.append(Math.pow(match.getAwayTeamAwayAvgFormGoalsAgainst(), pow) + delimiter);
                    writer.append(Math.pow(match.getAwayTeamAwayAvgFormXGF(), pow) + delimiter);
                    writer.append(Math.pow(match.getAwayTeamAwayAvgFormXGA(), pow) + delimiter);
                    writer.append(Math.pow(match.getAwayTeamAwayAvgFormWeightedXGF(), pow) + delimiter);
                    writer.append(Math.pow(match.getAwayTeamAwayAvgFormWeightedXGA(), pow) + delimiter);
                    writer.append(Math.pow(match.getAwayTeamAwayAvgFormXGFLast5Games(), pow) + delimiter);
                    writer.append(Math.pow(match.getAwayTeamAwayAvgFormXGALast5Games(), pow) + delimiter); //81
                }


//                double homePts = match.getAvgHomeTeamPoints();
//                double homeGF = match.getHomeTeamAvgGoalsFor();
//                double awayGF = match.getAwayTeamAvgGoalsFor();
//                double homeFormWeightXGF = match.getHomeTeamAvgFormWeightedXGF();
//                double awayLineupRating = match.getAwayTeamAtAwayMinsWeightedLineupRating();
//                double awayFormWeightXGA = match.getAwayTeamAvgFormWeightedXGA();
//                double awayXGA = match.getAwayTeamWeightedAvgAwayXGA();
//                double homeHomeGA = match.getHomeTeamAvgHomeGoalsAgainst();
//                double awayAvgXGA = match.getAwayTeamAvgXGA();
//                double awayWeightXGF = match.getAwayTeamWeightedAvgXGF();
//                double awayAwayXGF = match.getAwayTeamAvgAwayXGF();
//                double conceededFirstPts = match.getIfConceededFirstHomeTeamPoints();
//                double homeFormGA = match.getHomeTeamAvgFormGoalsAgainst();
//
//                double[] toCombine = new double[]{
//                        homePts, homeGF, awayGF, homeFormWeightXGF, awayLineupRating, awayFormWeightXGA, awayXGA,
//                        homeHomeGA, awayAvgXGA, awayWeightXGF, awayAwayXGF, conceededFirstPts, homeFormGA
//                };
//
//                ArrayList<Double> newFeatures = combineFeatures(toCombine);
//                for (double feature: newFeatures) writer.append(feature + delimiter);




                //odds
                writer.append(match.getHomeTeamProbability() + delimiter);
                writer.append(match.getDrawProbability() + delimiter);
                writer.append(match.getAwayTeamProbability() + delimiter);

                //result
                writer.append(match.getResult() + "");
                writer.append("\n");
            }

        } catch (IOException e) {}


    }

    private static void writeAllMatchDataPlusCleanSheetsToCSVFile(String fileName, ArrayList<TrainingMatch> trainingData, String delimiter) {

        try (FileWriter writer = new FileWriter(fileName)) {

            for (int i = 0; i < trainingData.size(); i++) {
                TrainingMatch match = trainingData.get(i);

                for (int pow = 1; pow<=1; pow++) {
                    //home team total stats
                    writer.append(Math.pow(match.getHomeTeamAvgGoalsFor(), pow) + delimiter); //2
                    writer.append(Math.pow(match.getHomeTeamAvgGoalsAgainst(), pow) + delimiter);
                    writer.append(Math.pow(match.getHomeTeamAvgXGF(), pow) + delimiter);
                    writer.append(Math.pow(match.getHomeTeamAvgXGA(), pow) + delimiter);
                    writer.append(Math.pow(match.getHomeTeamWeightedAvgXGF(), pow) + delimiter);
                    writer.append(Math.pow(match.getHomeTeamWeightedAvgXGA(), pow) + delimiter);
                    writer.append(Math.pow(match.getAvgHomeTeamPoints(), pow) + delimiter);
                    writer.append(Math.pow(match.getLast5HomeTeamPoints(), pow) + delimiter);
                    writer.append(Math.pow(match.getIfScoredFirstHomeTeamPoints(), pow) + delimiter);
                    writer.append(Math.pow(match.getIfConceededFirstHomeTeamPoints(), pow) + delimiter);
                    writer.append(Math.pow(match.getHomeTeamPointsAgainstOpposition(), pow) + delimiter);
                    writer.append(Math.pow(match.getHomeTeamMinsWeightedLineupRating(), pow) + delimiter);
                    writer.append(Math.pow(match.getHomeTeamStrength(), pow) + delimiter); //14

                    //home team home stats
                    writer.append(Math.pow(match.getHomeTeamAvgHomeGoalsFor(), pow) + delimiter); //15
                    writer.append(Math.pow(match.getHomeTeamAvgHomeGoalsAgainst(), pow) + delimiter);
                    writer.append(Math.pow(match.getHomeTeamAvgHomeXGF(), pow) + delimiter);
                    writer.append(Math.pow(match.getHomeTeamAvgHomeXGA(), pow) + delimiter);
                    writer.append(Math.pow(match.getHomeTeamWeightedAvgHomeXGF(), pow) + delimiter);
                    writer.append(Math.pow(match.getHomeTeamWeightedAvgHomeXGA(), pow) + delimiter);
                    writer.append(Math.pow(match.getAvgHomeTeamHomePoints(), pow) + delimiter);
                    writer.append(Math.pow(match.getLast5HomeTeamHomePoints(), pow) + delimiter);
                    writer.append(Math.pow(match.getIfScoredFirstAtHomeHomeTeamPoints(), pow) + delimiter);
                    writer.append(Math.pow(match.getIfConceededFirstAtHomeHomeTeamPoints(), pow) + delimiter);
                    writer.append(Math.pow(match.getHomeTeamPointsAtHomeAgainstOpposition(), pow) + delimiter);
                    writer.append(Math.pow(match.getHomeTeamAtHomeMinsWeightedLineupRating(), pow) + delimiter);
                    writer.append(Math.pow(match.getHomeTeamHomeStrength(), pow) + delimiter); //27

                    //awawy team total stats
                    writer.append(Math.pow(match.getAwayTeamAvgGoalsFor(), pow) + delimiter); //28
                    writer.append(Math.pow(match.getAwayTeamAvgGoalsAgainst(), pow) + delimiter);
                    writer.append(Math.pow(match.getAwayTeamAvgXGF(), pow) + delimiter);
                    writer.append(Math.pow(match.getAwayTeamAvgXGA(), pow) + delimiter);
                    writer.append(Math.pow(match.getAwayTeamWeightedAvgXGF(), pow) + delimiter);
                    writer.append(Math.pow(match.getAwayTeamWeightedAvgXGA(), pow) + delimiter);
                    writer.append(Math.pow(match.getAvgAwayTeamPoints(), pow) + delimiter);
                    writer.append(Math.pow(match.getLast5AwayTeamPoints(), pow) + delimiter);
                    writer.append(Math.pow(match.getIfScoredFirstAwayTeamPoints(), pow) + delimiter);
                    writer.append(Math.pow(match.getIfConceededFirstAwayTeamPoints(), pow) + delimiter);
                    writer.append(Math.pow(match.getAwayTeamPointsAgainstOpposition(), pow) + delimiter);
                    writer.append(Math.pow(match.getAwayTeamMinsWeightedLineupRating(), pow) + delimiter);
                    writer.append(Math.pow(match.getAwayTeamStrength(), pow) + delimiter); //40

                    //away team away stats
                    writer.append(Math.pow(match.getAwayTeamAvgAwayGoalsFor(), pow) + delimiter); //41
                    writer.append(Math.pow(match.getAwayTeamAvgAwayGoalsAgainst(), pow) + delimiter);
                    writer.append(Math.pow(match.getAwayTeamAvgAwayXGF(), pow) + delimiter);
                    writer.append(Math.pow(match.getAwayTeamAvgAwayXGA(), pow) + delimiter);
                    writer.append(Math.pow(match.getAwayTeamWeightedAvgAwayXGF(), pow) + delimiter);
                    writer.append(Math.pow(match.getAwayTeamWeightedAvgAwayXGA(), pow) + delimiter);
                    writer.append(Math.pow(match.getAvgAwayTeamAwayPoints(), pow) + delimiter);
                    writer.append(Math.pow(match.getLast5AwayTeamAwayPoints(), pow) + delimiter);
                    writer.append(Math.pow(match.getIfScoredFirstAtAwayAwayTeamPoints(), pow) + delimiter);
                    writer.append(Math.pow(match.getIfConceededFirstAtAwayAwayTeamPoints(), pow) + delimiter);
                    writer.append(Math.pow(match.getAwayTeamPointsAtAwayAgainstOpposition(), pow) + delimiter);
                    writer.append(Math.pow(match.getAwayTeamAtAwayMinsWeightedLineupRating(), pow) + delimiter);
                    writer.append(Math.pow(match.getAwayTeamAwayStrength(), pow) + delimiter); //53


                    //extra home team stats
                    writer.append(Math.pow(match.getHomeTeamAvgFormGoalsFor(), pow) + delimiter);
                    writer.append(Math.pow(match.getHomeTeamAvgFormGoalsAgainst(), pow) + delimiter);
                    writer.append(Math.pow(match.getHomeTeamAvgFormXGF(), pow) + delimiter);
                    writer.append(Math.pow(match.getHomeTeamAvgFormXGA(), pow) + delimiter);
                    writer.append(Math.pow(match.getHomeTeamAvgFormWeightedXGF(), pow) + delimiter);
                    writer.append(Math.pow(match.getHomeTeamAvgFormWeightedXGA(), pow) + delimiter);
                    writer.append(Math.pow(match.getHomeTeamAvgFormXGFLast5Games(), pow) + delimiter);
                    writer.append(Math.pow(match.getHomeTeamAvgFormXGALast5Games(), pow) + delimiter);

                    //extra home home stats
                    writer.append(Math.pow(match.getHomeTeamHomeAvgFormGoalsFor(), pow) + delimiter);
                    writer.append(Math.pow(match.getHomeTeamHomeAvgFormGoalsAgainst(), pow) + delimiter);
                    writer.append(Math.pow(match.getHomeTeamHomeAvgFormXGF(), pow) + delimiter);
                    writer.append(Math.pow(match.getHomeTeamHomeAvgFormXGA(), pow) + delimiter);
                    writer.append(Math.pow(match.getHomeTeamHomeAvgFormWeightedXGF(), pow) + delimiter);
                    writer.append(Math.pow(match.getHomeTeamHomeAvgFormWeightedXGA(), pow) + delimiter);
                    writer.append(Math.pow(match.getHomeTeamHomeAvgFormXGFLast5Games(), pow) + delimiter);
                    writer.append(Math.pow(match.getHomeTeamHomeAvgFormXGALast5Games(), pow) + delimiter);

                    //extra away stats
                    writer.append(Math.pow(match.getAwayTeamAvgFormGoalsFor(), pow) + delimiter);
                    writer.append(Math.pow(match.getAwayTeamAvgFormGoalsAgainst(), pow) + delimiter);
                    writer.append(Math.pow(match.getAwayTeamAvgFormXGF(), pow) + delimiter);
                    writer.append(Math.pow(match.getAwayTeamAvgFormXGA(), pow) + delimiter);
                    writer.append(Math.pow(match.getAwayTeamAvgFormWeightedXGF(), pow) + delimiter);
                    writer.append(Math.pow(match.getAwayTeamAvgFormWeightedXGA(), pow) + delimiter);
                    writer.append(Math.pow(match.getAwayTeamAvgFormXGFLast5Games(), pow) + delimiter);
                    writer.append(Math.pow(match.getAwayTeamAvgFormXGALast5Games(), pow) + delimiter);

                    //extra away away stats
                    writer.append(Math.pow(match.getAwayTeamAwayAvgFormGoalsFor(), pow) + delimiter);
                    writer.append(Math.pow(match.getAwayTeamAwayAvgFormGoalsAgainst(), pow) + delimiter);
                    writer.append(Math.pow(match.getAwayTeamAwayAvgFormXGF(), pow) + delimiter);
                    writer.append(Math.pow(match.getAwayTeamAwayAvgFormXGA(), pow) + delimiter);
                    writer.append(Math.pow(match.getAwayTeamAwayAvgFormWeightedXGF(), pow) + delimiter);
                    writer.append(Math.pow(match.getAwayTeamAwayAvgFormWeightedXGA(), pow) + delimiter);
                    writer.append(Math.pow(match.getAwayTeamAwayAvgFormXGFLast5Games(), pow) + delimiter);
                    writer.append(Math.pow(match.getAwayTeamAwayAvgFormXGALast5Games(), pow) + delimiter); //81

                    //clean sheet stats
                    writer.append(Math.pow(match.getHomeTeamsAvgNumbCleanSheets(), pow) + delimiter);
                    writer.append(Math.pow(match.getHomeTeamsHomeAvgNumbCleanSheets(), pow) + delimiter);
                    writer.append(Math.pow(match.getAwayTeamsAvgNumbCleanSheets(), pow) + delimiter);
                    writer.append(Math.pow(match.getAwayTeamsAwayAvgNumbCleanSheets(), pow) + delimiter);
                }


//                double homePts = match.getAvgHomeTeamPoints();
//                double homeGF = match.getHomeTeamAvgGoalsFor();
//                double awayGF = match.getAwayTeamAvgGoalsFor();
//                double homeFormWeightXGF = match.getHomeTeamAvgFormWeightedXGF();
//                double awayLineupRating = match.getAwayTeamAtAwayMinsWeightedLineupRating();
//                double awayFormWeightXGA = match.getAwayTeamAvgFormWeightedXGA();
//                double awayXGA = match.getAwayTeamWeightedAvgAwayXGA();
//                double homeHomeGA = match.getHomeTeamAvgHomeGoalsAgainst();
//                double awayAvgXGA = match.getAwayTeamAvgXGA();
//                double awayWeightXGF = match.getAwayTeamWeightedAvgXGF();
//                double awayAwayXGF = match.getAwayTeamAvgAwayXGF();
//                double conceededFirstPts = match.getIfConceededFirstHomeTeamPoints();
//                double homeFormGA = match.getHomeTeamAvgFormGoalsAgainst();
//
//                double[] toCombine = new double[]{
//                        homePts, homeGF, awayGF, homeFormWeightXGF, awayLineupRating, awayFormWeightXGA, awayXGA,
//                        homeHomeGA, awayAvgXGA, awayWeightXGF, awayAwayXGF, conceededFirstPts, homeFormGA
//                };
//
//                ArrayList<Double> newFeatures = combineFeatures(toCombine);
//                for (double feature: newFeatures) writer.append(feature + delimiter);




                //odds
                writer.append(match.getHomeTeamProbability() + delimiter);
                writer.append(match.getDrawProbability() + delimiter);
                writer.append(match.getAwayTeamProbability() + delimiter);

                //result
                writer.append(match.getResult() + "");
                writer.append("\n");
            }

        } catch (IOException e) {}


    }



    /*
     * Used by writing methods to create powered features. Also used when creating matches to predict to create the powered features.
     * Important to do this within 1 method so we get the same variations every time.
     */
    public static ArrayList<Double> combineFeatures (double[] toCombine) {
        ArrayList<Double> newFeatures = new ArrayList<>();

        for (int j = 0; j<toCombine.length; j++) {
            //we want to go through the other numbs and
            double x = toCombine[j];
            double x2 = Math.pow(toCombine[j],2);
            double x3 = Math.pow(x,3);
            newFeatures.add(Math.pow(x,4));
            newFeatures.add(x3);
            newFeatures.add(x2);
            for (int k = j+1; k<toCombine.length; k++) {
                double xk = x * toCombine[k];
                double x2k = x2 * toCombine[k];
                double xk2 = x * Math.pow(toCombine[k],2);
                newFeatures.add(xk);
                newFeatures.add(x2k);
                newFeatures.add(xk2);
                newFeatures.add(x3 * toCombine[k]);
                newFeatures.add(x2 * Math.pow(toCombine[k],2));
                for (int l = k+1; l<toCombine.length; l++) {
                    double xkl = xk * toCombine[l];
                    newFeatures.add(x2k * toCombine[l]);
                    newFeatures.add(xk2 * toCombine[l]);
                    newFeatures.add(xkl);
                    newFeatures.add(xk * Math.pow(toCombine[l],2));
                    for (int m = l+1; m<toCombine.length; m++) {
                        newFeatures.add(xkl * toCombine[m]);
                    }
                }
            }
        }

        return newFeatures;
    }






    //LEGACY

    /*
     * Used to calculate potential profits in Java. No longer needed, as we just extract the odds out of a Matrix.
     */
    private static void writeBettersProbabilitiesToCSVFile(String fileName, Date earliestDate, Date latestDate, ArrayList<TrainingMatch> trainingData, String delimiter) {
        try (FileWriter writer = new FileWriter(fileName)) {

            for (int i = 0; i < trainingData.size(); i++) {
                TrainingMatch match = trainingData.get(i);

                Date kickoffTime = match.getKickoffTime();

                if (earliestDate != null && kickoffTime.before(earliestDate)) continue;
                else if (latestDate != null && kickoffTime.after(latestDate)) continue;

                writer.append(match.getHomeTeamProbability() + delimiter);
                writer.append(match.getDrawProbability() + delimiter);
                writer.append(match.getAwayTeamProbability() + delimiter);
                writer.append(match.getResult() + delimiter);
                writer.append("\n");
            }

        } catch (IOException e) {}
    }

    /*
     * Used for WEKA. No longer needed.
     * NOTE: If used again, need to make sure that the ordering of features is the same as in the data we trained our thetas on.
     */
    private static void writeDataToArffFile(String fileName, Date earliestDate, Date latestDate, ArrayList<TrainingMatch> trainingData) {
//        String[] poss = new String[]{"", "win", "draw", "loss"};
//
//        for (int k = 1; k<poss.length;k++) {

        try (FileWriter writer = new FileWriter(fileName)) {

            //ONLY IN HERE TO MAKE WEKA HAPPY THAT THE FIRST ROW IS THE TITLES
            String[] attributes = new String[]{"homeTotalGF", "homeTotalGA", "homeTotalXGF", "homeTotalXGA", "homeTotalWeightXGF", "homeTotalWeightXGA",
                    "homeTotalAvgPoints", "homeTotalLast5Points", "homeTotalIfScoredFirstPoints", "homeTotalIfConceededFirstPoints", "homeTotalPtsVsOppo","homeTotalLineupRating",
                    "homeTotalStrength", "homeHomeGF", "homeHomeGA", "homeHomeXGF", "homeHomeXGA", "homeHomeWeightXGF", "homeHomeWeightXGA",
                    "homeHomeAvgPoints", "homeHomeLast5Points", "homeHomeIfScoredFirstPoints", "homeHomeIfConceededFirstPoints", "homeHomePtsVsOppo", "homeHomeLineupRating",
                    "homeHomeStrength", "awayTotalGF", "awayTotalGA", "awayTotalXGF", "awayTotalXGA", "awayTotalWeightXGF", "awayTotalWeightXGA",
                    "awayTotalAvgPoints", "awayTotalLast5Points", "awayTotalIfScoredFirstPoints", "awayTotalIfConceededFirstPoints", "awayTotalPtsVsOppo", "awayTotalLineupRating",
                    "awayTotalStrength", "awayAwayGF", "awayAwayGA", "awayAwayXGF", "awayAwayXGA", "awayAwayWeightXGF", "awayAwayWeightXGA",
                    "awayAwayAvgPoints", "awayAwayLast5Points", "awayAwayIfScoredFirstPoints", "awayAwayIfConceededFirstPoints", "awayAwayPtsVsOppo", "awayAwayLineupRating",
                    "awayAwayStrength", "homeTotalFG", "awayTotalFG", "homeHomeFG", "awayAwayFG"};
//                String[] attributes = new String[]{"homeTotalFG", "awayTotalFG", "homeHomeFG", "awayAwayFG"};
            writer.append("@relation 'plswork'" + "\n\n");
            for (int i = 0; i < attributes.length; i++) {
                writer.append("@attribute '" + attributes[i] + "' real\n");
            }
            for (int y = 0; y<2288; y++) {
                writer.append("@attribute 'powered" + y + "' real\n");
            }

            writer.append("@attribute 'class' {1, 2, 3}\n\n");

            writer.append("@data");
            for (int i = 0; i < trainingData.size(); i++) {
                TrainingMatch match = trainingData.get(i);

                Date kickoffTime = match.getKickoffTime();

                if (earliestDate != null && kickoffTime.before(earliestDate)) continue;
                else if (latestDate != null && kickoffTime.after(latestDate)) continue;

                writer.append("\n");
                //home team total stats
                writer.append(match.getHomeTeamAvgGoalsFor() + ","); //2
                writer.append(match.getHomeTeamAvgGoalsAgainst() + ",");
                writer.append(match.getHomeTeamAvgXGF() + ",");
                writer.append(match.getHomeTeamAvgXGA() + ",");
                writer.append(match.getHomeTeamWeightedAvgXGF() + ",");
                writer.append(match.getHomeTeamWeightedAvgXGA() + ",");
                writer.append(match.getAvgHomeTeamPoints() + ",");
                writer.append(match.getLast5HomeTeamPoints() + ",");
                writer.append(match.getIfScoredFirstHomeTeamPoints() + ",");
                writer.append(match.getIfConceededFirstHomeTeamPoints() + ",");
                writer.append(match.getHomeTeamPointsAgainstOpposition() + ",");
                writer.append(match.getHomeTeamMinsWeightedLineupRating() + ",");
                writer.append(match.getHomeTeamStrength() + ","); //14

                //home team home stats
                writer.append(match.getHomeTeamAvgHomeGoalsFor() + ","); //15
                writer.append(match.getHomeTeamAvgHomeGoalsAgainst() + ",");
                writer.append(match.getHomeTeamAvgHomeXGF() + ",");
                writer.append(match.getHomeTeamAvgHomeXGA() + ",");
                writer.append(match.getHomeTeamWeightedAvgHomeXGF() + ",");
                writer.append(match.getHomeTeamWeightedAvgHomeXGA() + ",");
                writer.append(match.getAvgHomeTeamHomePoints() + ",");
                writer.append(match.getLast5HomeTeamHomePoints() + ",");
                writer.append(match.getIfScoredFirstAtHomeHomeTeamPoints() + ",");
                writer.append(match.getIfConceededFirstAtHomeHomeTeamPoints() + ",");
                writer.append(match.getHomeTeamPointsAtHomeAgainstOpposition() + ",");
                writer.append(match.getHomeTeamAtHomeMinsWeightedLineupRating() + ",");
                writer.append(match.getHomeTeamHomeStrength() + ","); //27

                //awawy team total stats
                writer.append(match.getAwayTeamAvgGoalsFor() + ","); //28
                writer.append(match.getAwayTeamAvgGoalsAgainst() + ",");
                writer.append(match.getAwayTeamAvgXGF() + ",");
                writer.append(match.getAwayTeamAvgXGA() + ",");
                writer.append(match.getAwayTeamWeightedAvgXGF() + ",");
                writer.append(match.getAwayTeamWeightedAvgXGA() + ",");
                writer.append(match.getAvgAwayTeamPoints() + ",");
                writer.append(match.getLast5AwayTeamPoints() + ",");
                writer.append(match.getIfScoredFirstAwayTeamPoints() + ",");
                writer.append(match.getIfConceededFirstAwayTeamPoints() + ",");
                writer.append(match.getAwayTeamPointsAgainstOpposition() + ",");
                writer.append(match.getAwayTeamMinsWeightedLineupRating() + ",");
                writer.append(match.getAwayTeamStrength() + ","); //40

                //away team away stats
                writer.append(match.getAwayTeamAvgAwayGoalsFor() + ","); //41
                writer.append(match.getAwayTeamAvgAwayGoalsAgainst() + ",");
                writer.append(match.getAwayTeamAvgAwayXGF() + ",");
                writer.append(match.getAwayTeamAvgAwayXGA() + ",");
                writer.append(match.getAwayTeamWeightedAvgAwayXGF() + ",");
                writer.append(match.getAwayTeamWeightedAvgAwayXGA() + ",");
                writer.append(match.getAvgAwayTeamAwayPoints() + ",");
                writer.append(match.getLast5AwayTeamAwayPoints() + ",");
                writer.append(match.getIfScoredFirstAtAwayAwayTeamPoints() + ",");
                writer.append(match.getIfConceededFirstAtAwayAwayTeamPoints() + ",");
                writer.append(match.getAwayTeamPointsAtAwayAgainstOpposition() + ",");
                writer.append(match.getAwayTeamAtAwayMinsWeightedLineupRating() + ",");
                writer.append(match.getAwayTeamAwayStrength() + ","); //53

                //prediction stats
                writer.append(match.getHomeTeamWeightedAvgXGF() - match.getAwayTeamWeightedAvgXGA() + ","); //expected home team goals
                writer.append(match.getAwayTeamWeightedAvgXGF() - match.getHomeTeamWeightedAvgXGA() + ","); // expected away team goals
                writer.append(match.getHomeTeamWeightedAvgHomeXGF() - match.getAwayTeamWeightedAvgAwayXGA() + ","); //expected home team home goals
                writer.append(match.getAwayTeamWeightedAvgAwayXGF() - match.getHomeTeamWeightedAvgHomeXGA() + ","); // expected away team away goals //57



                //powered vals
//                    double homeTeamTotalGF = match.getHomeTeamAvgGoalsFor();
//                    double awayTeamTotalLineupRating = match.getAwayTeamMinsWeightedLineupRating();
//                    double awayTeamTotalXGF = match.getAwayTeamAvgXGF();
//                    double awayTeamAwayPts = match.getAvgAwayTeamAwayPoints();
//                    double homeTeamTotalXGF = match.getHomeTeamAvgXGF();
//                    double homeTeamHomeGF = match.getHomeTeamAvgHomeGoalsFor();
//                    double homeTeamTotalLineupRating = match.getHomeTeamMinsWeightedLineupRating();
//                    double awayTeamTotalXGA = match.getAwayTeamAvgXGA();
//                    double homeTeamHomePts = match.getAvgHomeTeamHomePoints();
//                    double homeTeamHomeXGF = match.getHomeTeamAvgHomeXGF();
//                    //10
//                    double homeTeamWeightedXGA = match.getHomeTeamWeightedAvgXGA();
//                    double awayTeamAwayWeightedXGF = match.getAwayTeamWeightedAvgAwayXGF();
//                    double awayTeamAvfGF = match.getAwayTeamAvgGoalsFor();

//                double[] toCombine = new double[]{homeTeamTotalGF, awayTeamTotalLineupRating, awayTeamTotalXGF, awayTeamAwayPts, homeTeamTotalXGF, homeTeamHomeGF,
//                        homeTeamTotalLineupRating, awayTeamTotalXGA, homeTeamHomePts, homeTeamWeightedXGA, awayTeamAwayWeightedXGF, awayTeamAvfGF, homeTeamHomeXGF};

                //powered vals
                double homeTeamTotalLineupRating = match.getHomeTeamMinsWeightedLineupRating();
                double awayTeamTotalLineupRating = match.getAwayTeamMinsWeightedLineupRating();
                double awayTeamTotalXGF = match.getAwayTeamAvgXGF();
                double awayTeamStrength = match.getAwayTeamStrength();
                double homeTeamTotalGF = match.getHomeTeamAvgGoalsFor();
                double homeTeamTotalXGF = match.getHomeTeamAvgXGF();
                double awayTeamTotalXGA = match.getAwayTeamAvgXGA();
                double homeTeamWeightedTotalXGA = match.getHomeTeamWeightedAvgXGA();
                double awayTeamWeightedAwayXGF = match.getAwayTeamWeightedAvgAwayXGF();
                double awayTeamAwayXGF = match.getAwayTeamAvgAwayXGF();
                double homeTeamHomeXGF = match.getHomeTeamAvgHomeXGF();
                double homeTeamXGA = match.getHomeTeamAvgXGA();
                double awayTeamAwayXGA = match.getAwayTeamWeightedAvgAwayXGA();

                double[] toCombine = new double[]{homeTeamTotalLineupRating, awayTeamTotalLineupRating, awayTeamTotalXGF,awayTeamStrength, homeTeamTotalGF,
                        homeTeamTotalXGF, awayTeamTotalXGA, homeTeamWeightedTotalXGA, awayTeamWeightedAwayXGF, awayTeamAwayXGF, homeTeamHomeXGF, homeTeamXGA, awayTeamAwayXGA};

                ArrayList<Double> newFeatures = new ArrayList<>();

                //max pow: 4

                for (int j = 0; j<toCombine.length; j++) {
                    //we want to go through the other numbs and
                    double x = toCombine[j];
                    double x2 = Math.pow(toCombine[j],2);
                    double x3 = Math.pow(x,3);
                    newFeatures.add(Math.pow(x,4));
                    newFeatures.add(x3);
                    newFeatures.add(x2);
                    for (int k = j+1; k<toCombine.length; k++) {
                        double xk = x * toCombine[k];
                        double x2k = x2 * toCombine[k];
                        double xk2 = x * Math.pow(toCombine[k],2);
                        newFeatures.add(xk);
                        newFeatures.add(x2k);
                        newFeatures.add(xk2);
                        newFeatures.add(x3 * toCombine[k]);
                        newFeatures.add(x2 * Math.pow(toCombine[k],2));
                        for (int l = k+1; l<toCombine.length; l++) {
                            double xkl = xk * toCombine[l];
                            newFeatures.add(x2k * toCombine[l]);
                            newFeatures.add(xk2 * toCombine[l]);
                            newFeatures.add(xkl);
                            newFeatures.add(xk * Math.pow(toCombine[l],2));
                            for (int m = l+1; m<toCombine.length; m++) {
                                newFeatures.add(xkl * toCombine[m]);
                            }
                        }
                    }
                }

                for (double feature: newFeatures) writer.append(feature + ",");
//                    System.out.println(newFeatures.size());

//                //odds
//                writer.append(match.getHomeTeamProbability() + ",");
//                writer.append(match.getDrawProbability() + ",");
//                writer.append(match.getAwayTeamProbability() + ",");

                //result
                int result = match.getResult();
                writer.append(result + "");
            }

        } catch (IOException e) {
        }
//        }
    }

    private static void writeMoreMatchDataToCSVFile(String fileName, ArrayList<TrainingMatch> trainingData, String delimiter) {

        try (FileWriter writer = new FileWriter(fileName)) {

            for (int i = 0; i < trainingData.size(); i++) {
                TrainingMatch match = trainingData.get(i);

                for (int pow = 1; pow<=1; pow++) {
                    //home team total stats
                    writer.append(Math.pow(match.getHomeTeamAvgGoalsFor(), pow) + delimiter); //2
                    writer.append(Math.pow(match.getHomeTeamAvgGoalsAgainst(), pow) + delimiter);
                    writer.append(Math.pow(match.getHomeTeamAvgXGF(), pow) + delimiter);
                    writer.append(Math.pow(match.getHomeTeamAvgXGA(), pow) + delimiter);
                    writer.append(Math.pow(match.getHomeTeamWeightedAvgXGF(), pow) + delimiter);
                    writer.append(Math.pow(match.getHomeTeamWeightedAvgXGA(), pow) + delimiter);
                    writer.append(Math.pow(match.getAvgHomeTeamPoints(), pow) + delimiter);
                    writer.append(Math.pow(match.getLast5HomeTeamPoints(), pow) + delimiter);
                    writer.append(Math.pow(match.getIfScoredFirstHomeTeamPoints(), pow) + delimiter);
                    writer.append(Math.pow(match.getIfConceededFirstHomeTeamPoints(), pow) + delimiter);
                    writer.append(Math.pow(match.getHomeTeamPointsAgainstOpposition(), pow) + delimiter);
                    writer.append(Math.pow(match.getHomeTeamMinsWeightedLineupRating(), pow) + delimiter);
                    writer.append(Math.pow(match.getHomeTeamStrength(), pow) + delimiter); //14

                    //home team home stats
                    writer.append(Math.pow(match.getHomeTeamAvgHomeGoalsFor(), pow) + delimiter); //15
                    writer.append(Math.pow(match.getHomeTeamAvgHomeGoalsAgainst(), pow) + delimiter);
                    writer.append(Math.pow(match.getHomeTeamAvgHomeXGF(), pow) + delimiter);
                    writer.append(Math.pow(match.getHomeTeamAvgHomeXGA(), pow) + delimiter);
                    writer.append(Math.pow(match.getHomeTeamWeightedAvgHomeXGF(), pow) + delimiter);
                    writer.append(Math.pow(match.getHomeTeamWeightedAvgHomeXGA(), pow) + delimiter);
                    writer.append(Math.pow(match.getAvgHomeTeamHomePoints(), pow) + delimiter);
                    writer.append(Math.pow(match.getLast5HomeTeamHomePoints(), pow) + delimiter);
                    writer.append(Math.pow(match.getIfScoredFirstAtHomeHomeTeamPoints(), pow) + delimiter);
                    writer.append(Math.pow(match.getIfConceededFirstAtHomeHomeTeamPoints(), pow) + delimiter);
                    writer.append(Math.pow(match.getHomeTeamPointsAtHomeAgainstOpposition(), pow) + delimiter);
                    writer.append(Math.pow(match.getHomeTeamAtHomeMinsWeightedLineupRating(), pow) + delimiter);
                    writer.append(Math.pow(match.getHomeTeamHomeStrength(), pow) + delimiter); //27

                    //awawy team total stats
                    writer.append(Math.pow(match.getAwayTeamAvgGoalsFor(), pow) + delimiter); //28
                    writer.append(Math.pow(match.getAwayTeamAvgGoalsAgainst(), pow) + delimiter);
                    writer.append(Math.pow(match.getAwayTeamAvgXGF(), pow) + delimiter);
                    writer.append(Math.pow(match.getAwayTeamAvgXGA(), pow) + delimiter);
                    writer.append(Math.pow(match.getAwayTeamWeightedAvgXGF(), pow) + delimiter);
                    writer.append(Math.pow(match.getAwayTeamWeightedAvgXGA(), pow) + delimiter);
                    writer.append(Math.pow(match.getAvgAwayTeamPoints(), pow) + delimiter);
                    writer.append(Math.pow(match.getLast5AwayTeamPoints(), pow) + delimiter);
                    writer.append(Math.pow(match.getIfScoredFirstAwayTeamPoints(), pow) + delimiter);
                    writer.append(Math.pow(match.getIfConceededFirstAwayTeamPoints(), pow) + delimiter);
                    writer.append(Math.pow(match.getAwayTeamPointsAgainstOpposition(), pow) + delimiter);
                    writer.append(Math.pow(match.getAwayTeamMinsWeightedLineupRating(), pow) + delimiter);
                    writer.append(Math.pow(match.getAwayTeamStrength(), pow) + delimiter); //40

                    //away team away stats
                    writer.append(Math.pow(match.getAwayTeamAvgAwayGoalsFor(), pow) + delimiter); //41
                    writer.append(Math.pow(match.getAwayTeamAvgAwayGoalsAgainst(), pow) + delimiter);
                    writer.append(Math.pow(match.getAwayTeamAvgAwayXGF(), pow) + delimiter);
                    writer.append(Math.pow(match.getAwayTeamAvgAwayXGA(), pow) + delimiter);
                    writer.append(Math.pow(match.getAwayTeamWeightedAvgAwayXGF(), pow) + delimiter);
                    writer.append(Math.pow(match.getAwayTeamWeightedAvgAwayXGA(), pow) + delimiter);
                    writer.append(Math.pow(match.getAvgAwayTeamAwayPoints(), pow) + delimiter);
                    writer.append(Math.pow(match.getLast5AwayTeamAwayPoints(), pow) + delimiter);
                    writer.append(Math.pow(match.getIfScoredFirstAtAwayAwayTeamPoints(), pow) + delimiter);
                    writer.append(Math.pow(match.getIfConceededFirstAtAwayAwayTeamPoints(), pow) + delimiter);
                    writer.append(Math.pow(match.getAwayTeamPointsAtAwayAgainstOpposition(), pow) + delimiter);
                    writer.append(Math.pow(match.getAwayTeamAtAwayMinsWeightedLineupRating(), pow) + delimiter);
                    writer.append(Math.pow(match.getAwayTeamAwayStrength(), pow) + delimiter); //53


                    //extra home team stats
                    writer.append(Math.pow(match.getHomeTeamAvgFormGoalsFor(), pow) + delimiter);
                    writer.append(Math.pow(match.getHomeTeamAvgFormGoalsAgainst(), pow) + delimiter);
                    writer.append(Math.pow(match.getHomeTeamAvgFormXGF(), pow) + delimiter);
                    writer.append(Math.pow(match.getHomeTeamAvgFormXGA(), pow) + delimiter);
                    writer.append(Math.pow(match.getHomeTeamAvgFormWeightedXGF(), pow) + delimiter);
                    writer.append(Math.pow(match.getHomeTeamAvgFormWeightedXGA(), pow) + delimiter);
                    writer.append(Math.pow(match.getHomeTeamAvgFormXGFLast5Games(), pow) + delimiter);
                    writer.append(Math.pow(match.getHomeTeamAvgFormXGALast5Games(), pow) + delimiter);

                    //extra home home stats
                    writer.append(Math.pow(match.getHomeTeamHomeAvgFormGoalsFor(), pow) + delimiter);
                    writer.append(Math.pow(match.getHomeTeamHomeAvgFormGoalsAgainst(), pow) + delimiter);
                    writer.append(Math.pow(match.getHomeTeamHomeAvgFormXGF(), pow) + delimiter);
                    writer.append(Math.pow(match.getHomeTeamHomeAvgFormXGA(), pow) + delimiter);
                    writer.append(Math.pow(match.getHomeTeamHomeAvgFormWeightedXGF(), pow) + delimiter);
                    writer.append(Math.pow(match.getHomeTeamHomeAvgFormWeightedXGA(), pow) + delimiter);
                    writer.append(Math.pow(match.getHomeTeamHomeAvgFormXGFLast5Games(), pow) + delimiter);
                    writer.append(Math.pow(match.getHomeTeamHomeAvgFormXGALast5Games(), pow) + delimiter);

                    //extra away stats
                    writer.append(Math.pow(match.getAwayTeamAvgFormGoalsFor(), pow) + delimiter);
                    writer.append(Math.pow(match.getAwayTeamAvgFormGoalsAgainst(), pow) + delimiter);
                    writer.append(Math.pow(match.getAwayTeamAvgFormXGF(), pow) + delimiter);
                    writer.append(Math.pow(match.getAwayTeamAvgFormXGA(), pow) + delimiter);
                    writer.append(Math.pow(match.getAwayTeamAvgFormWeightedXGF(), pow) + delimiter);
                    writer.append(Math.pow(match.getAwayTeamAvgFormWeightedXGA(), pow) + delimiter);
                    writer.append(Math.pow(match.getAwayTeamAvgFormXGFLast5Games(), pow) + delimiter);
                    writer.append(Math.pow(match.getAwayTeamAvgFormXGALast5Games(), pow) + delimiter);

                    //extra away away stats
                    writer.append(Math.pow(match.getAwayTeamAwayAvgFormGoalsFor(), pow) + delimiter);
                    writer.append(Math.pow(match.getAwayTeamAwayAvgFormGoalsAgainst(), pow) + delimiter);
                    writer.append(Math.pow(match.getAwayTeamAwayAvgFormXGF(), pow) + delimiter);
                    writer.append(Math.pow(match.getAwayTeamAwayAvgFormXGA(), pow) + delimiter);
                    writer.append(Math.pow(match.getAwayTeamAwayAvgFormWeightedXGF(), pow) + delimiter);
                    writer.append(Math.pow(match.getAwayTeamAwayAvgFormWeightedXGA(), pow) + delimiter);
                    writer.append(Math.pow(match.getAwayTeamAwayAvgFormXGFLast5Games(), pow) + delimiter);
                    writer.append(Math.pow(match.getAwayTeamAwayAvgFormXGALast5Games(), pow) + delimiter); //81


                    //home ppg of opponents stats
//                    writer.append(Math.pow(match.getHomeTeamsOpponentsWholeSeasonPPG(), pow) + delimiter);
//                    writer.append(Math.pow(match.getHomeTeamsLast5OpponentsWholeSeasonPPG(), pow) + delimiter);
//                    writer.append(Math.pow(match.getHomeTeamsOpponentsLast5PPG(), pow) + delimiter);
//                    writer.append(Math.pow(match.getHomeTeamLast5OpponentsLast5PPG(), pow) + delimiter);
//
//                    //home home ppg of opponents stats
//                    writer.append(Math.pow(match.getHomeTeamsHomeOpponentsWholeSeasonPPG(), pow) + delimiter);
//                    writer.append(Math.pow(match.getHomeTeamsLast5HomeOpponentsWholeSeasonPPG(), pow) + delimiter);
//                    writer.append(Math.pow(match.getHomeTeamsHomeOpponentsLast5PPG(), pow) + delimiter);
//                    writer.append(Math.pow(match.getHomeTeamLast5HomeOpponentsLast5PPG(), pow) + delimiter);
//
//                    //away opponents ppg stats
//                    writer.append(Math.pow(match.getAwayTeamsOpponentsWholeSeasonPPG(), pow) + delimiter);
//                    writer.append(Math.pow(match.getAwayTeamsLast5OpponentsWholeSeasonPPG(), pow) + delimiter);
//                    writer.append(Math.pow(match.getAwayTeamsOpponentsLast5PPG(), pow) + delimiter);
//                    writer.append(Math.pow(match.getAwayTeamLast5OpponentsLast5PPG(), pow) + delimiter);
//
//                    //away away opponents ppg stats
//                    writer.append(Math.pow(match.getAwayTeamsAwayOpponentsWholeSeasonPPG(), pow) + delimiter);
//                    writer.append(Math.pow(match.getAwayTeamsLast5AwayOpponentsWholeSeasonPPG(), pow) + delimiter);
//                    writer.append(Math.pow(match.getAwayTeamsAwayOpponentsLast5PPG(), pow) + delimiter);
//                    writer.append(Math.pow(match.getAwayTeamLast5AwayOpponentsLast5PPG(), pow) + delimiter); // 97

                    //clean sheet stats
                    writer.append(Math.pow(match.getHomeTeamsAvgNumbCleanSheets(), pow) + delimiter);
                    writer.append(Math.pow(match.getHomeTeamsLast5AvgNumbCleanSheets(), pow) + delimiter);
                    writer.append(Math.pow(match.getHomeTeamsHomeAvgNumbCleanSheets(), pow) + delimiter);
                    writer.append(Math.pow(match.getHomeTeamsLast5HomeAvgNumbCleanSheets(), pow) + delimiter);
                    writer.append(Math.pow(match.getAwayTeamsAvgNumbCleanSheets(), pow) + delimiter);
                    writer.append(Math.pow(match.getAwayTeamsLast5AvgNumbCleanSheets(), pow) + delimiter);
                    writer.append(Math.pow(match.getAwayTeamsAwayAvgNumbCleanSheets(), pow) + delimiter);
                    writer.append(Math.pow(match.getAwayTeamsLast5AwayAvgNumbCleanSheets(), pow) + delimiter); //105
                }


//                double homePts = match.getAvgHomeTeamPoints();
//                double homeGF = match.getHomeTeamAvgGoalsFor();
//                double awayGF = match.getAwayTeamAvgGoalsFor();
//                double homeFormWeightXGF = match.getHomeTeamAvgFormWeightedXGF();
//                double awayLineupRating = match.getAwayTeamAtAwayMinsWeightedLineupRating();
//                double awayFormWeightXGA = match.getAwayTeamAvgFormWeightedXGA();
//                double awayXGA = match.getAwayTeamWeightedAvgAwayXGA();
//                double homeHomeGA = match.getHomeTeamAvgHomeGoalsAgainst();
//                double awayAvgXGA = match.getAwayTeamAvgXGA();
//                double awayWeightXGF = match.getAwayTeamWeightedAvgXGF();
//                double awayAwayXGF = match.getAwayTeamAvgAwayXGF();
//                double conceededFirstPts = match.getIfConceededFirstHomeTeamPoints();
//                double homeFormGA = match.getHomeTeamAvgFormGoalsAgainst();
//
//                double[] toCombine = new double[]{
//                        homePts, homeGF, awayGF, homeFormWeightXGF, awayLineupRating, awayFormWeightXGA, awayXGA,
//                        homeHomeGA, awayAvgXGA, awayWeightXGF, awayAwayXGF, conceededFirstPts, homeFormGA
//                };
//
//                ArrayList<Double> newFeatures = combineFeatures(toCombine);
//                for (double feature: newFeatures) writer.append(feature + delimiter);




                //odds
                writer.append(match.getHomeTeamProbability() + delimiter);
                writer.append(match.getDrawProbability() + delimiter);
                writer.append(match.getAwayTeamProbability() + delimiter);

                //result
                writer.append(match.getResult() + "");
                writer.append("\n");
            }

        } catch (IOException e) {}


    }


    private static void writeEvenMoreMatchDataToCSVFile(String fileName, ArrayList<TrainingMatch> trainingData, String delimiter) {

        try (FileWriter writer = new FileWriter(fileName)) {

            for (int i = 0; i < trainingData.size(); i++) {
                TrainingMatch match = trainingData.get(i);

                for (int pow = 1; pow<=1; pow++) {
                    //home team total stats
                    writer.append(Math.pow(match.getHomeTeamAvgGoalsFor(), pow) + delimiter); //2
                    writer.append(Math.pow(match.getHomeTeamAvgGoalsAgainst(), pow) + delimiter);
                    writer.append(Math.pow(match.getHomeTeamAvgXGF(), pow) + delimiter);
                    writer.append(Math.pow(match.getHomeTeamAvgXGA(), pow) + delimiter);
                    writer.append(Math.pow(match.getHomeTeamWeightedAvgXGF(), pow) + delimiter);
                    writer.append(Math.pow(match.getHomeTeamWeightedAvgXGA(), pow) + delimiter);
                    writer.append(Math.pow(match.getAvgHomeTeamPoints(), pow) + delimiter);
                    writer.append(Math.pow(match.getLast5HomeTeamPoints(), pow) + delimiter);
                    writer.append(Math.pow(match.getIfScoredFirstHomeTeamPoints(), pow) + delimiter);
                    writer.append(Math.pow(match.getIfConceededFirstHomeTeamPoints(), pow) + delimiter);
                    writer.append(Math.pow(match.getHomeTeamPointsAgainstOpposition(), pow) + delimiter);
                    writer.append(Math.pow(match.getHomeTeamMinsWeightedLineupRating(), pow) + delimiter);
                    writer.append(Math.pow(match.getHomeTeamStrength(), pow) + delimiter); //14

                    //home team home stats
                    writer.append(Math.pow(match.getHomeTeamAvgHomeGoalsFor(), pow) + delimiter); //15
                    writer.append(Math.pow(match.getHomeTeamAvgHomeGoalsAgainst(), pow) + delimiter);
                    writer.append(Math.pow(match.getHomeTeamAvgHomeXGF(), pow) + delimiter);
                    writer.append(Math.pow(match.getHomeTeamAvgHomeXGA(), pow) + delimiter);
                    writer.append(Math.pow(match.getHomeTeamWeightedAvgHomeXGF(), pow) + delimiter);
                    writer.append(Math.pow(match.getHomeTeamWeightedAvgHomeXGA(), pow) + delimiter);
                    writer.append(Math.pow(match.getAvgHomeTeamHomePoints(), pow) + delimiter);
                    writer.append(Math.pow(match.getLast5HomeTeamHomePoints(), pow) + delimiter);
                    writer.append(Math.pow(match.getIfScoredFirstAtHomeHomeTeamPoints(), pow) + delimiter);
                    writer.append(Math.pow(match.getIfConceededFirstAtHomeHomeTeamPoints(), pow) + delimiter);
                    writer.append(Math.pow(match.getHomeTeamPointsAtHomeAgainstOpposition(), pow) + delimiter);
                    writer.append(Math.pow(match.getHomeTeamAtHomeMinsWeightedLineupRating(), pow) + delimiter);
                    writer.append(Math.pow(match.getHomeTeamHomeStrength(), pow) + delimiter); //27

                    //awawy team total stats
                    writer.append(Math.pow(match.getAwayTeamAvgGoalsFor(), pow) + delimiter); //28
                    writer.append(Math.pow(match.getAwayTeamAvgGoalsAgainst(), pow) + delimiter);
                    writer.append(Math.pow(match.getAwayTeamAvgXGF(), pow) + delimiter);
                    writer.append(Math.pow(match.getAwayTeamAvgXGA(), pow) + delimiter);
                    writer.append(Math.pow(match.getAwayTeamWeightedAvgXGF(), pow) + delimiter);
                    writer.append(Math.pow(match.getAwayTeamWeightedAvgXGA(), pow) + delimiter);
                    writer.append(Math.pow(match.getAvgAwayTeamPoints(), pow) + delimiter);
                    writer.append(Math.pow(match.getLast5AwayTeamPoints(), pow) + delimiter);
                    writer.append(Math.pow(match.getIfScoredFirstAwayTeamPoints(), pow) + delimiter);
                    writer.append(Math.pow(match.getIfConceededFirstAwayTeamPoints(), pow) + delimiter);
                    writer.append(Math.pow(match.getAwayTeamPointsAgainstOpposition(), pow) + delimiter);
                    writer.append(Math.pow(match.getAwayTeamMinsWeightedLineupRating(), pow) + delimiter);
                    writer.append(Math.pow(match.getAwayTeamStrength(), pow) + delimiter); //40

                    //away team away stats
                    writer.append(Math.pow(match.getAwayTeamAvgAwayGoalsFor(), pow) + delimiter); //41
                    writer.append(Math.pow(match.getAwayTeamAvgAwayGoalsAgainst(), pow) + delimiter);
                    writer.append(Math.pow(match.getAwayTeamAvgAwayXGF(), pow) + delimiter);
                    writer.append(Math.pow(match.getAwayTeamAvgAwayXGA(), pow) + delimiter);
                    writer.append(Math.pow(match.getAwayTeamWeightedAvgAwayXGF(), pow) + delimiter);
                    writer.append(Math.pow(match.getAwayTeamWeightedAvgAwayXGA(), pow) + delimiter);
                    writer.append(Math.pow(match.getAvgAwayTeamAwayPoints(), pow) + delimiter);
                    writer.append(Math.pow(match.getLast5AwayTeamAwayPoints(), pow) + delimiter);
                    writer.append(Math.pow(match.getIfScoredFirstAtAwayAwayTeamPoints(), pow) + delimiter);
                    writer.append(Math.pow(match.getIfConceededFirstAtAwayAwayTeamPoints(), pow) + delimiter);
                    writer.append(Math.pow(match.getAwayTeamPointsAtAwayAgainstOpposition(), pow) + delimiter);
                    writer.append(Math.pow(match.getAwayTeamAtAwayMinsWeightedLineupRating(), pow) + delimiter);
                    writer.append(Math.pow(match.getAwayTeamAwayStrength(), pow) + delimiter); //53


                    //extra home team stats
                    writer.append(Math.pow(match.getHomeTeamAvgFormGoalsFor(), pow) + delimiter);
                    writer.append(Math.pow(match.getHomeTeamAvgFormGoalsAgainst(), pow) + delimiter);
                    writer.append(Math.pow(match.getHomeTeamAvgFormXGF(), pow) + delimiter);
                    writer.append(Math.pow(match.getHomeTeamAvgFormXGA(), pow) + delimiter);
                    writer.append(Math.pow(match.getHomeTeamAvgFormWeightedXGF(), pow) + delimiter);
                    writer.append(Math.pow(match.getHomeTeamAvgFormWeightedXGA(), pow) + delimiter);
                    writer.append(Math.pow(match.getHomeTeamAvgFormXGFLast5Games(), pow) + delimiter);
                    writer.append(Math.pow(match.getHomeTeamAvgFormXGALast5Games(), pow) + delimiter);

                    //extra home home stats
                    writer.append(Math.pow(match.getHomeTeamHomeAvgFormGoalsFor(), pow) + delimiter);
                    writer.append(Math.pow(match.getHomeTeamHomeAvgFormGoalsAgainst(), pow) + delimiter);
                    writer.append(Math.pow(match.getHomeTeamHomeAvgFormXGF(), pow) + delimiter);
                    writer.append(Math.pow(match.getHomeTeamHomeAvgFormXGA(), pow) + delimiter);
                    writer.append(Math.pow(match.getHomeTeamHomeAvgFormWeightedXGF(), pow) + delimiter);
                    writer.append(Math.pow(match.getHomeTeamHomeAvgFormWeightedXGA(), pow) + delimiter);
                    writer.append(Math.pow(match.getHomeTeamHomeAvgFormXGFLast5Games(), pow) + delimiter);
                    writer.append(Math.pow(match.getHomeTeamHomeAvgFormXGALast5Games(), pow) + delimiter);

                    //extra away stats
                    writer.append(Math.pow(match.getAwayTeamAvgFormGoalsFor(), pow) + delimiter);
                    writer.append(Math.pow(match.getAwayTeamAvgFormGoalsAgainst(), pow) + delimiter);
                    writer.append(Math.pow(match.getAwayTeamAvgFormXGF(), pow) + delimiter);
                    writer.append(Math.pow(match.getAwayTeamAvgFormXGA(), pow) + delimiter);
                    writer.append(Math.pow(match.getAwayTeamAvgFormWeightedXGF(), pow) + delimiter);
                    writer.append(Math.pow(match.getAwayTeamAvgFormWeightedXGA(), pow) + delimiter);
                    writer.append(Math.pow(match.getAwayTeamAvgFormXGFLast5Games(), pow) + delimiter);
                    writer.append(Math.pow(match.getAwayTeamAvgFormXGALast5Games(), pow) + delimiter);

                    //extra away away stats
                    writer.append(Math.pow(match.getAwayTeamAwayAvgFormGoalsFor(), pow) + delimiter);
                    writer.append(Math.pow(match.getAwayTeamAwayAvgFormGoalsAgainst(), pow) + delimiter);
                    writer.append(Math.pow(match.getAwayTeamAwayAvgFormXGF(), pow) + delimiter);
                    writer.append(Math.pow(match.getAwayTeamAwayAvgFormXGA(), pow) + delimiter);
                    writer.append(Math.pow(match.getAwayTeamAwayAvgFormWeightedXGF(), pow) + delimiter);
                    writer.append(Math.pow(match.getAwayTeamAwayAvgFormWeightedXGA(), pow) + delimiter);
                    writer.append(Math.pow(match.getAwayTeamAwayAvgFormXGFLast5Games(), pow) + delimiter);
                    writer.append(Math.pow(match.getAwayTeamAwayAvgFormXGALast5Games(), pow) + delimiter); //81


                    //home ppg of opponents stats
                    double homeAllOpponentsWholeSeasonPPG = match.getHomeTeamsOpponentsWholeSeasonPPG();
                    double homeLast5OpponentsWholeSeasonPPG = match.getHomeTeamsLast5OpponentsWholeSeasonPPG();
                    double homeAllOpponentsLast5PPG = match.getHomeTeamsOpponentsLast5PPG();
                    double homeLast5OpponentsLast5PPG = match.getHomeTeamLast5OpponentsLast5PPG();

                    //home home ppg of opponents stats
                    double homeAllHomeOpponentsWholeSeasonPPG = match.getHomeTeamsHomeOpponentsWholeSeasonPPG();
                    double homeLast5HomeOpponentsWholeSeasonPPG = match.getHomeTeamsLast5HomeOpponentsWholeSeasonPPG();
                    double homeAllHomeOpponentsLast5PPG = match.getHomeTeamsHomeOpponentsLast5PPG();
                    double homeLast5HomeOpponentsLast5PPG = match.getHomeTeamLast5HomeOpponentsLast5PPG();

                    //away opponents ppg stats
                    double awayAllOpponentsWholeSeasonPPG = match.getAwayTeamsOpponentsWholeSeasonPPG();
                    double awayLast5OpponentsWholeSeasonPPG = match.getAwayTeamsLast5OpponentsWholeSeasonPPG();
                    double awayAllOpponentsLast5PPG = match.getAwayTeamsOpponentsLast5PPG();
                    double awayLast5OpponentsLast5PPG = match.getAwayTeamLast5OpponentsLast5PPG();

                    //away away opponents ppg stats
                    double awayAllAwayOpponentsWholeSeasonPPG = match.getAwayTeamsAwayOpponentsWholeSeasonPPG();
                    double awayLast5AwayOpponentsWholeSeasonPPG = match.getAwayTeamsLast5AwayOpponentsWholeSeasonPPG();
                    double awayAllAwayOpponentsLast5PPG = match.getAwayTeamsAwayOpponentsLast5PPG();
                    double awayLast5AwayOpponentsLast5PPG = match.getAwayTeamLast5AwayOpponentsLast5PPG();

                    writer.append(Math.pow(match.getHomeTeamAvgGoalsFor() * homeAllOpponentsWholeSeasonPPG, pow) + delimiter); //2
                    writer.append(Math.pow(match.getHomeTeamAvgGoalsAgainst() * homeAllOpponentsWholeSeasonPPG, pow) + delimiter);
                    writer.append(Math.pow(match.getHomeTeamAvgXGF() * homeAllOpponentsWholeSeasonPPG, pow) + delimiter);
                    writer.append(Math.pow(match.getHomeTeamAvgXGA() * homeAllOpponentsWholeSeasonPPG, pow) + delimiter);
                    writer.append(Math.pow(match.getAvgHomeTeamPoints() * homeAllOpponentsWholeSeasonPPG, pow) + delimiter);
                    writer.append(Math.pow(match.getLast5HomeTeamPoints() * homeLast5OpponentsWholeSeasonPPG, pow) + delimiter);

                    //home team home stats
                    writer.append(Math.pow(match.getHomeTeamAvgHomeGoalsFor() * homeAllHomeOpponentsWholeSeasonPPG, pow) + delimiter); //15
                    writer.append(Math.pow(match.getHomeTeamAvgHomeGoalsAgainst() * homeAllHomeOpponentsWholeSeasonPPG, pow) + delimiter);
                    writer.append(Math.pow(match.getHomeTeamAvgHomeXGF() * homeAllHomeOpponentsWholeSeasonPPG, pow) + delimiter);
                    writer.append(Math.pow(match.getHomeTeamAvgHomeXGA() * homeAllHomeOpponentsWholeSeasonPPG, pow) + delimiter);
                    writer.append(Math.pow(match.getAvgHomeTeamHomePoints() * homeAllHomeOpponentsWholeSeasonPPG, pow) + delimiter);
                    writer.append(Math.pow(match.getLast5HomeTeamHomePoints() * homeLast5HomeOpponentsWholeSeasonPPG, pow) + delimiter);

                    //awawy team total stats
                    writer.append(Math.pow(match.getAwayTeamAvgGoalsFor() * awayAllOpponentsWholeSeasonPPG, pow) + delimiter); //28
                    writer.append(Math.pow(match.getAwayTeamAvgGoalsAgainst() * awayAllOpponentsWholeSeasonPPG, pow) + delimiter);
                    writer.append(Math.pow(match.getAwayTeamAvgXGF() * awayAllOpponentsWholeSeasonPPG, pow) + delimiter);
                    writer.append(Math.pow(match.getAwayTeamAvgXGA() * awayAllOpponentsWholeSeasonPPG, pow) + delimiter);
                    writer.append(Math.pow(match.getAvgAwayTeamPoints() * awayAllOpponentsWholeSeasonPPG, pow) + delimiter);
                    writer.append(Math.pow(match.getLast5AwayTeamPoints() * awayLast5OpponentsWholeSeasonPPG, pow) + delimiter);

                    //away team away stats
                    writer.append(Math.pow(match.getAwayTeamAvgAwayGoalsFor() * awayAllAwayOpponentsWholeSeasonPPG, pow) + delimiter); //41
                    writer.append(Math.pow(match.getAwayTeamAvgAwayGoalsAgainst() * awayAllAwayOpponentsWholeSeasonPPG, pow) + delimiter);
                    writer.append(Math.pow(match.getAwayTeamAvgAwayXGF() * awayAllAwayOpponentsWholeSeasonPPG, pow) + delimiter);
                    writer.append(Math.pow(match.getAwayTeamAvgAwayXGA() * awayAllAwayOpponentsWholeSeasonPPG, pow) + delimiter);
                    writer.append(Math.pow(match.getAvgAwayTeamAwayPoints() * awayAllAwayOpponentsWholeSeasonPPG, pow) + delimiter);
                    writer.append(Math.pow(match.getLast5AwayTeamAwayPoints() * awayLast5AwayOpponentsWholeSeasonPPG, pow) + delimiter);


                    //clean sheet stats
                    writer.append(Math.pow(match.getHomeTeamsAvgNumbCleanSheets() * homeAllOpponentsWholeSeasonPPG, pow) + delimiter);
                    writer.append(Math.pow(match.getHomeTeamsLast5AvgNumbCleanSheets() * homeLast5OpponentsWholeSeasonPPG, pow) + delimiter);
                    writer.append(Math.pow(match.getHomeTeamsHomeAvgNumbCleanSheets() * homeAllHomeOpponentsWholeSeasonPPG, pow) + delimiter);
                    writer.append(Math.pow(match.getHomeTeamsLast5HomeAvgNumbCleanSheets() * homeLast5HomeOpponentsWholeSeasonPPG, pow) + delimiter);
                    writer.append(Math.pow(match.getAwayTeamsAvgNumbCleanSheets() * awayAllOpponentsWholeSeasonPPG, pow) + delimiter);
                    writer.append(Math.pow(match.getAwayTeamsLast5AvgNumbCleanSheets() * awayLast5OpponentsWholeSeasonPPG, pow) + delimiter);
                    writer.append(Math.pow(match.getAwayTeamsAwayAvgNumbCleanSheets() * awayAllAwayOpponentsWholeSeasonPPG, pow) + delimiter);
                    writer.append(Math.pow(match.getAwayTeamsLast5AwayAvgNumbCleanSheets() * awayLast5AwayOpponentsWholeSeasonPPG, pow) + delimiter); //105
                }


//                double homePts = match.getAvgHomeTeamPoints();
//                double homeGF = match.getHomeTeamAvgGoalsFor();
//                double awayGF = match.getAwayTeamAvgGoalsFor();
//                double homeFormWeightXGF = match.getHomeTeamAvgFormWeightedXGF();
//                double awayLineupRating = match.getAwayTeamAtAwayMinsWeightedLineupRating();
//                double awayFormWeightXGA = match.getAwayTeamAvgFormWeightedXGA();
//                double awayXGA = match.getAwayTeamWeightedAvgAwayXGA();
//                double homeHomeGA = match.getHomeTeamAvgHomeGoalsAgainst();
//                double awayAvgXGA = match.getAwayTeamAvgXGA();
//                double awayWeightXGF = match.getAwayTeamWeightedAvgXGF();
//                double awayAwayXGF = match.getAwayTeamAvgAwayXGF();
//                double conceededFirstPts = match.getIfConceededFirstHomeTeamPoints();
//                double homeFormGA = match.getHomeTeamAvgFormGoalsAgainst();
//
//                double[] toCombine = new double[]{
//                        homePts, homeGF, awayGF, homeFormWeightXGF, awayLineupRating, awayFormWeightXGA, awayXGA,
//                        homeHomeGA, awayAvgXGA, awayWeightXGF, awayAwayXGF, conceededFirstPts, homeFormGA
//                };
//
//                ArrayList<Double> newFeatures = combineFeatures(toCombine);
//                for (double feature: newFeatures) writer.append(feature + delimiter);




                //odds
                writer.append(match.getHomeTeamProbability() + delimiter);
                writer.append(match.getDrawProbability() + delimiter);
                writer.append(match.getAwayTeamProbability() + delimiter);

                //result
                writer.append(match.getResult() + "");
                writer.append("\n");
            }

        } catch (IOException e) {}


    }

    //adds more fields with current fields multiplied by each teams previous opponents previous 5 matches. (to gauge how impressive their recent stats are.)
    private static void writeEvenMoreFormMatchDataToCSVFile(String fileName, ArrayList<TrainingMatch> trainingData, String delimiter) {

        try (FileWriter writer = new FileWriter(fileName)) {

            for (int i = 0; i < trainingData.size(); i++) {
                TrainingMatch match = trainingData.get(i);

                for (int pow = 1; pow<=1; pow++) {
                    //home team total stats
                    writer.append(Math.pow(match.getHomeTeamAvgGoalsFor(), pow) + delimiter); //2
                    writer.append(Math.pow(match.getHomeTeamAvgGoalsAgainst(), pow) + delimiter);
                    writer.append(Math.pow(match.getHomeTeamAvgXGF(), pow) + delimiter);
                    writer.append(Math.pow(match.getHomeTeamAvgXGA(), pow) + delimiter);
                    writer.append(Math.pow(match.getHomeTeamWeightedAvgXGF(), pow) + delimiter);
                    writer.append(Math.pow(match.getHomeTeamWeightedAvgXGA(), pow) + delimiter);
                    writer.append(Math.pow(match.getAvgHomeTeamPoints(), pow) + delimiter);
                    writer.append(Math.pow(match.getLast5HomeTeamPoints(), pow) + delimiter);
                    writer.append(Math.pow(match.getIfScoredFirstHomeTeamPoints(), pow) + delimiter);
                    writer.append(Math.pow(match.getIfConceededFirstHomeTeamPoints(), pow) + delimiter);
                    writer.append(Math.pow(match.getHomeTeamPointsAgainstOpposition(), pow) + delimiter);
                    writer.append(Math.pow(match.getHomeTeamMinsWeightedLineupRating(), pow) + delimiter);
                    writer.append(Math.pow(match.getHomeTeamStrength(), pow) + delimiter); //14

                    //home team home stats
                    writer.append(Math.pow(match.getHomeTeamAvgHomeGoalsFor(), pow) + delimiter); //15
                    writer.append(Math.pow(match.getHomeTeamAvgHomeGoalsAgainst(), pow) + delimiter);
                    writer.append(Math.pow(match.getHomeTeamAvgHomeXGF(), pow) + delimiter);
                    writer.append(Math.pow(match.getHomeTeamAvgHomeXGA(), pow) + delimiter);
                    writer.append(Math.pow(match.getHomeTeamWeightedAvgHomeXGF(), pow) + delimiter);
                    writer.append(Math.pow(match.getHomeTeamWeightedAvgHomeXGA(), pow) + delimiter);
                    writer.append(Math.pow(match.getAvgHomeTeamHomePoints(), pow) + delimiter);
                    writer.append(Math.pow(match.getLast5HomeTeamHomePoints(), pow) + delimiter);
                    writer.append(Math.pow(match.getIfScoredFirstAtHomeHomeTeamPoints(), pow) + delimiter);
                    writer.append(Math.pow(match.getIfConceededFirstAtHomeHomeTeamPoints(), pow) + delimiter);
                    writer.append(Math.pow(match.getHomeTeamPointsAtHomeAgainstOpposition(), pow) + delimiter);
                    writer.append(Math.pow(match.getHomeTeamAtHomeMinsWeightedLineupRating(), pow) + delimiter);
                    writer.append(Math.pow(match.getHomeTeamHomeStrength(), pow) + delimiter); //27

                    //awawy team total stats
                    writer.append(Math.pow(match.getAwayTeamAvgGoalsFor(), pow) + delimiter); //28
                    writer.append(Math.pow(match.getAwayTeamAvgGoalsAgainst(), pow) + delimiter);
                    writer.append(Math.pow(match.getAwayTeamAvgXGF(), pow) + delimiter);
                    writer.append(Math.pow(match.getAwayTeamAvgXGA(), pow) + delimiter);
                    writer.append(Math.pow(match.getAwayTeamWeightedAvgXGF(), pow) + delimiter);
                    writer.append(Math.pow(match.getAwayTeamWeightedAvgXGA(), pow) + delimiter);
                    writer.append(Math.pow(match.getAvgAwayTeamPoints(), pow) + delimiter);
                    writer.append(Math.pow(match.getLast5AwayTeamPoints(), pow) + delimiter);
                    writer.append(Math.pow(match.getIfScoredFirstAwayTeamPoints(), pow) + delimiter);
                    writer.append(Math.pow(match.getIfConceededFirstAwayTeamPoints(), pow) + delimiter);
                    writer.append(Math.pow(match.getAwayTeamPointsAgainstOpposition(), pow) + delimiter);
                    writer.append(Math.pow(match.getAwayTeamMinsWeightedLineupRating(), pow) + delimiter);
                    writer.append(Math.pow(match.getAwayTeamStrength(), pow) + delimiter); //40

                    //away team away stats
                    writer.append(Math.pow(match.getAwayTeamAvgAwayGoalsFor(), pow) + delimiter); //41
                    writer.append(Math.pow(match.getAwayTeamAvgAwayGoalsAgainst(), pow) + delimiter);
                    writer.append(Math.pow(match.getAwayTeamAvgAwayXGF(), pow) + delimiter);
                    writer.append(Math.pow(match.getAwayTeamAvgAwayXGA(), pow) + delimiter);
                    writer.append(Math.pow(match.getAwayTeamWeightedAvgAwayXGF(), pow) + delimiter);
                    writer.append(Math.pow(match.getAwayTeamWeightedAvgAwayXGA(), pow) + delimiter);
                    writer.append(Math.pow(match.getAvgAwayTeamAwayPoints(), pow) + delimiter);
                    writer.append(Math.pow(match.getLast5AwayTeamAwayPoints(), pow) + delimiter);
                    writer.append(Math.pow(match.getIfScoredFirstAtAwayAwayTeamPoints(), pow) + delimiter);
                    writer.append(Math.pow(match.getIfConceededFirstAtAwayAwayTeamPoints(), pow) + delimiter);
                    writer.append(Math.pow(match.getAwayTeamPointsAtAwayAgainstOpposition(), pow) + delimiter);
                    writer.append(Math.pow(match.getAwayTeamAtAwayMinsWeightedLineupRating(), pow) + delimiter);
                    writer.append(Math.pow(match.getAwayTeamAwayStrength(), pow) + delimiter); //53


                    //extra home team stats
                    writer.append(Math.pow(match.getHomeTeamAvgFormGoalsFor(), pow) + delimiter);
                    writer.append(Math.pow(match.getHomeTeamAvgFormGoalsAgainst(), pow) + delimiter);
                    writer.append(Math.pow(match.getHomeTeamAvgFormXGF(), pow) + delimiter);
                    writer.append(Math.pow(match.getHomeTeamAvgFormXGA(), pow) + delimiter);
                    writer.append(Math.pow(match.getHomeTeamAvgFormWeightedXGF(), pow) + delimiter);
                    writer.append(Math.pow(match.getHomeTeamAvgFormWeightedXGA(), pow) + delimiter);
                    writer.append(Math.pow(match.getHomeTeamAvgFormXGFLast5Games(), pow) + delimiter);
                    writer.append(Math.pow(match.getHomeTeamAvgFormXGALast5Games(), pow) + delimiter);

                    //extra home home stats
                    writer.append(Math.pow(match.getHomeTeamHomeAvgFormGoalsFor(), pow) + delimiter);
                    writer.append(Math.pow(match.getHomeTeamHomeAvgFormGoalsAgainst(), pow) + delimiter);
                    writer.append(Math.pow(match.getHomeTeamHomeAvgFormXGF(), pow) + delimiter);
                    writer.append(Math.pow(match.getHomeTeamHomeAvgFormXGA(), pow) + delimiter);
                    writer.append(Math.pow(match.getHomeTeamHomeAvgFormWeightedXGF(), pow) + delimiter);
                    writer.append(Math.pow(match.getHomeTeamHomeAvgFormWeightedXGA(), pow) + delimiter);
                    writer.append(Math.pow(match.getHomeTeamHomeAvgFormXGFLast5Games(), pow) + delimiter);
                    writer.append(Math.pow(match.getHomeTeamHomeAvgFormXGALast5Games(), pow) + delimiter);

                    //extra away stats
                    writer.append(Math.pow(match.getAwayTeamAvgFormGoalsFor(), pow) + delimiter);
                    writer.append(Math.pow(match.getAwayTeamAvgFormGoalsAgainst(), pow) + delimiter);
                    writer.append(Math.pow(match.getAwayTeamAvgFormXGF(), pow) + delimiter);
                    writer.append(Math.pow(match.getAwayTeamAvgFormXGA(), pow) + delimiter);
                    writer.append(Math.pow(match.getAwayTeamAvgFormWeightedXGF(), pow) + delimiter);
                    writer.append(Math.pow(match.getAwayTeamAvgFormWeightedXGA(), pow) + delimiter);
                    writer.append(Math.pow(match.getAwayTeamAvgFormXGFLast5Games(), pow) + delimiter);
                    writer.append(Math.pow(match.getAwayTeamAvgFormXGALast5Games(), pow) + delimiter);

                    //extra away away stats
                    writer.append(Math.pow(match.getAwayTeamAwayAvgFormGoalsFor(), pow) + delimiter);
                    writer.append(Math.pow(match.getAwayTeamAwayAvgFormGoalsAgainst(), pow) + delimiter);
                    writer.append(Math.pow(match.getAwayTeamAwayAvgFormXGF(), pow) + delimiter);
                    writer.append(Math.pow(match.getAwayTeamAwayAvgFormXGA(), pow) + delimiter);
                    writer.append(Math.pow(match.getAwayTeamAwayAvgFormWeightedXGF(), pow) + delimiter);
                    writer.append(Math.pow(match.getAwayTeamAwayAvgFormWeightedXGA(), pow) + delimiter);
                    writer.append(Math.pow(match.getAwayTeamAwayAvgFormXGFLast5Games(), pow) + delimiter);
                    writer.append(Math.pow(match.getAwayTeamAwayAvgFormXGALast5Games(), pow) + delimiter); //81


                    //home ppg of opponents stats
                    double homeAllOpponentsWholeSeasonPPG = match.getHomeTeamsOpponentsWholeSeasonPPG();
                    double homeLast5OpponentsWholeSeasonPPG = match.getHomeTeamsLast5OpponentsWholeSeasonPPG();
                    double homeAllOpponentsLast5PPG = match.getHomeTeamsOpponentsLast5PPG();
                    double homeLast5OpponentsLast5PPG = match.getHomeTeamLast5OpponentsLast5PPG();

                    //home home ppg of opponents stats
                    double homeAllHomeOpponentsWholeSeasonPPG = match.getHomeTeamsHomeOpponentsWholeSeasonPPG();
                    double homeLast5HomeOpponentsWholeSeasonPPG = match.getHomeTeamsLast5HomeOpponentsWholeSeasonPPG();
                    double homeAllHomeOpponentsLast5PPG = match.getHomeTeamsHomeOpponentsLast5PPG();
                    double homeLast5HomeOpponentsLast5PPG = match.getHomeTeamLast5HomeOpponentsLast5PPG();

                    //away opponents ppg stats
                    double awayAllOpponentsWholeSeasonPPG = match.getAwayTeamsOpponentsWholeSeasonPPG();
                    double awayLast5OpponentsWholeSeasonPPG = match.getAwayTeamsLast5OpponentsWholeSeasonPPG();
                    double awayAllOpponentsLast5PPG = match.getAwayTeamsOpponentsLast5PPG();
                    double awayLast5OpponentsLast5PPG = match.getAwayTeamLast5OpponentsLast5PPG();

                    //away away opponents ppg stats
                    double awayAllAwayOpponentsWholeSeasonPPG = match.getAwayTeamsAwayOpponentsWholeSeasonPPG();
                    double awayLast5AwayOpponentsWholeSeasonPPG = match.getAwayTeamsLast5AwayOpponentsWholeSeasonPPG();
                    double awayAllAwayOpponentsLast5PPG = match.getAwayTeamsAwayOpponentsLast5PPG();
                    double awayLast5AwayOpponentsLast5PPG = match.getAwayTeamLast5AwayOpponentsLast5PPG();

                    writer.append(Math.pow(match.getHomeTeamAvgGoalsFor() * homeAllOpponentsLast5PPG, pow) + delimiter); //2
                    writer.append(Math.pow(match.getHomeTeamAvgGoalsAgainst() * homeAllOpponentsLast5PPG, pow) + delimiter);
                    writer.append(Math.pow(match.getHomeTeamAvgXGF() * homeAllOpponentsLast5PPG, pow) + delimiter);
                    writer.append(Math.pow(match.getHomeTeamAvgXGA() * homeAllOpponentsLast5PPG, pow) + delimiter);
                    writer.append(Math.pow(match.getAvgHomeTeamPoints() * homeAllOpponentsLast5PPG, pow) + delimiter);
                    writer.append(Math.pow(match.getLast5HomeTeamPoints() * homeLast5OpponentsLast5PPG, pow) + delimiter);

                    //home team home stats
                    writer.append(Math.pow(match.getHomeTeamAvgHomeGoalsFor() * homeAllHomeOpponentsLast5PPG, pow) + delimiter); //15
                    writer.append(Math.pow(match.getHomeTeamAvgHomeGoalsAgainst() * homeAllHomeOpponentsLast5PPG, pow) + delimiter);
                    writer.append(Math.pow(match.getHomeTeamAvgHomeXGF() * homeAllHomeOpponentsLast5PPG, pow) + delimiter);
                    writer.append(Math.pow(match.getHomeTeamAvgHomeXGA() * homeAllHomeOpponentsLast5PPG, pow) + delimiter);
                    writer.append(Math.pow(match.getAvgHomeTeamHomePoints() * homeAllHomeOpponentsLast5PPG, pow) + delimiter);
                    writer.append(Math.pow(match.getLast5HomeTeamHomePoints() * homeLast5HomeOpponentsLast5PPG, pow) + delimiter);

                    //awawy team total stats
                    writer.append(Math.pow(match.getAwayTeamAvgGoalsFor() * awayAllOpponentsLast5PPG, pow) + delimiter); //28
                    writer.append(Math.pow(match.getAwayTeamAvgGoalsAgainst() * awayAllOpponentsLast5PPG, pow) + delimiter);
                    writer.append(Math.pow(match.getAwayTeamAvgXGF() * awayAllOpponentsLast5PPG, pow) + delimiter);
                    writer.append(Math.pow(match.getAwayTeamAvgXGA() * awayAllOpponentsLast5PPG, pow) + delimiter);
                    writer.append(Math.pow(match.getAvgAwayTeamPoints() * awayAllOpponentsLast5PPG, pow) + delimiter);
                    writer.append(Math.pow(match.getLast5AwayTeamPoints() * awayLast5OpponentsLast5PPG, pow) + delimiter);

                    //away team away stats
                    writer.append(Math.pow(match.getAwayTeamAvgAwayGoalsFor() * awayAllAwayOpponentsLast5PPG, pow) + delimiter); //41
                    writer.append(Math.pow(match.getAwayTeamAvgAwayGoalsAgainst() * awayAllAwayOpponentsLast5PPG, pow) + delimiter);
                    writer.append(Math.pow(match.getAwayTeamAvgAwayXGF() * awayAllAwayOpponentsLast5PPG, pow) + delimiter);
                    writer.append(Math.pow(match.getAwayTeamAvgAwayXGA() * awayAllAwayOpponentsLast5PPG, pow) + delimiter);
                    writer.append(Math.pow(match.getAvgAwayTeamAwayPoints() * awayAllAwayOpponentsLast5PPG, pow) + delimiter);
                    writer.append(Math.pow(match.getLast5AwayTeamAwayPoints() * awayLast5AwayOpponentsLast5PPG, pow) + delimiter);


                    //clean sheet stats
                    writer.append(Math.pow(match.getHomeTeamsAvgNumbCleanSheets() * homeAllOpponentsLast5PPG, pow) + delimiter);
                    writer.append(Math.pow(match.getHomeTeamsLast5AvgNumbCleanSheets() * homeLast5OpponentsLast5PPG, pow) + delimiter);
                    writer.append(Math.pow(match.getHomeTeamsHomeAvgNumbCleanSheets() * homeAllHomeOpponentsLast5PPG, pow) + delimiter);
                    writer.append(Math.pow(match.getHomeTeamsLast5HomeAvgNumbCleanSheets() * homeLast5HomeOpponentsLast5PPG, pow) + delimiter);
                    writer.append(Math.pow(match.getAwayTeamsAvgNumbCleanSheets() * awayAllOpponentsLast5PPG, pow) + delimiter);
                    writer.append(Math.pow(match.getAwayTeamsLast5AvgNumbCleanSheets() * awayLast5OpponentsLast5PPG, pow) + delimiter);
                    writer.append(Math.pow(match.getAwayTeamsAwayAvgNumbCleanSheets() * awayAllAwayOpponentsLast5PPG, pow) + delimiter);
                    writer.append(Math.pow(match.getAwayTeamsLast5AwayAvgNumbCleanSheets() * awayLast5AwayOpponentsLast5PPG, pow) + delimiter); //105
                }


//                double homePts = match.getAvgHomeTeamPoints();
//                double homeGF = match.getHomeTeamAvgGoalsFor();
//                double awayGF = match.getAwayTeamAvgGoalsFor();
//                double homeFormWeightXGF = match.getHomeTeamAvgFormWeightedXGF();
//                double awayLineupRating = match.getAwayTeamAtAwayMinsWeightedLineupRating();
//                double awayFormWeightXGA = match.getAwayTeamAvgFormWeightedXGA();
//                double awayXGA = match.getAwayTeamWeightedAvgAwayXGA();
//                double homeHomeGA = match.getHomeTeamAvgHomeGoalsAgainst();
//                double awayAvgXGA = match.getAwayTeamAvgXGA();
//                double awayWeightXGF = match.getAwayTeamWeightedAvgXGF();
//                double awayAwayXGF = match.getAwayTeamAvgAwayXGF();
//                double conceededFirstPts = match.getIfConceededFirstHomeTeamPoints();
//                double homeFormGA = match.getHomeTeamAvgFormGoalsAgainst();
//
//                double[] toCombine = new double[]{
//                        homePts, homeGF, awayGF, homeFormWeightXGF, awayLineupRating, awayFormWeightXGA, awayXGA,
//                        homeHomeGA, awayAvgXGA, awayWeightXGF, awayAwayXGF, conceededFirstPts, homeFormGA
//                };
//
//                ArrayList<Double> newFeatures = combineFeatures(toCombine);
//                for (double feature: newFeatures) writer.append(feature + delimiter);




                //odds
                writer.append(match.getHomeTeamProbability() + delimiter);
                writer.append(match.getDrawProbability() + delimiter);
                writer.append(match.getAwayTeamProbability() + delimiter);

                //result
                writer.append(match.getResult() + "");
                writer.append("\n");
            }

        } catch (IOException e) {}


    }

}
