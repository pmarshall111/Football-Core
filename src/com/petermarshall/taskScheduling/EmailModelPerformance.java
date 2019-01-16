package com.petermarshall.taskScheduling;

import com.petermarshall.DateHelper;
import com.petermarshall.database.BetResult;
import com.petermarshall.mail.SendEmail;
import com.petermarshall.database.BetResultsTotalled;
import com.petermarshall.database.DataSource;
import com.petermarshall.mail.UptimeData;

import java.util.Date;

public class EmailModelPerformance {

    private static final int MODEL_CHANGED_EVERY_X_MONTHS = 2;

    /*
     *Method will be schedulled on the first day of each month and will send a performance review along with a reminder to update the database if needed.
     * Performance review will only be for games the model actively predicted... i.e. the computer has to be on and the program has to be running for a
     * prediction to be made.
     */
    public static void main(String[] args) {
        System.out.println("Sending email of models performance...\n");
        Date lastChangeOfModel = calculateWhenModelWasLastChanged();


        DataSource.openConnection();

        BetResultsTotalled betResults = DataSource.getResultsOfPredictions(null, null, null);
        BetResultsTotalled resultsSinceLastModelChange = DataSource.getResultsOfPredictions(lastChangeOfModel, null, null);
        UptimeData uptimeData = DataSource.getModelOnlinePercentage();

        DataSource.closeConnection();


        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("Hello Peter,\nhere is a summary of the performance of your betting app: ");

        addTotalledDataToBuilder(stringBuilder, betResults);
        addRecentModelDataToBuilder(stringBuilder, lastChangeOfModel, resultsSinceLastModelChange);
        addUptimeDataToBuilder(stringBuilder, uptimeData);
        if (isModelDueToBeChanged()) {
            addReminderToChangeModel(stringBuilder);
        }

//        System.out.println(stringBuilder.toString());

        SendEmail.sendOutEmail("Betting app performance review", stringBuilder.toString());
    }

    private static void addTotalledDataToBuilder(StringBuilder stringBuilder, BetResultsTotalled totalledData) {

        stringBuilder.append("\n\nTotal Model Performance:\n");
        stringBuilder.append("In total, as of ");
        addBaseDataToBuilder(stringBuilder, totalledData, new Date());

    }

    private static void addReminderToChangeModel(StringBuilder stringBuilder) {
        stringBuilder.append("\n\n_______________________________\n\nAlert: It has now been " + MODEL_CHANGED_EVERY_X_MONTHS + " months since the database was last changed. " +
                "Please call taskSchedulling.RetrainPredictor, and transfer over the training data to Octave and bring back the retrained Thetas to Java. Thanks.\n\n" +
                "_______________________________");
    }

    private static void addRecentModelDataToBuilder(StringBuilder stringBuilder, Date lastChangeOfModel, BetResultsTotalled recentData) {
        stringBuilder.append("\n\nRecent Model Performance:\n");
        stringBuilder.append("Since the database was last changed on ");
        addBaseDataToBuilder(stringBuilder, recentData, lastChangeOfModel);
    }
    
    private static void addBaseDataToBuilder(StringBuilder builder, BetResultsTotalled betResultsTotalled, Date date) {

        builder.append(DateHelper.turnDateToddMMyyyyString(date));
        builder.append(", the database has spent £");
        builder.append(String.format("%.2f", betResultsTotalled.getTotalMoneyOut()));
        builder.append(" on ");
        builder.append(betResultsTotalled.getNumbBetsPlaced());
        builder.append(" bets, making £");
        builder.append(String.format("%.2f", betResultsTotalled.getRealProfit()));
        builder.append(" profit. This equates to a percentage profit of about ");
        builder.append(String.format("%.0f", betResultsTotalled.getPercentageProfit()));
        builder.append("%.");
        
    }

    private static void addUptimeDataToBuilder(StringBuilder stringBuilder, UptimeData uptime) {
        //TODO: we want to give data on how many games we were able to predict in real time compared to those predicted later.

        stringBuilder.append("\n\nUptime:\n");
        stringBuilder.append("Since ");
        stringBuilder.append(uptime.getWhenStartedRecordingPredictions());
        stringBuilder.append(" (when the ability to record predictions as well as placed bets was added), the program has been able to make a live prediction for ");
        stringBuilder.append(uptime.getBetsPredictedInRealTime());
        stringBuilder.append(" games, out of a total of ");
        stringBuilder.append(uptime.getTotalNumbBets());
        stringBuilder.append(" games.");
        stringBuilder.append("\nThis means that our app has been running for ");
        stringBuilder.append(String.format("%.2f", uptime.getPercUptime()));
        stringBuilder.append("% of games.");
    }


    //TODO: test working correctly, even when changing how often database is changed.
    private static Date calculateWhenModelWasLastChanged() {
        //will be changed on the 1st every 3 months starting in january.
        //month%3 should be 1.
        Date currentDate = DateHelper.setTimeOfDate(new Date(),0,0,0);

        int month = DateHelper.getMonthOfDate(currentDate);

        int modulo = month % MODEL_CHANGED_EVERY_X_MONTHS;
        if (modulo == 0) modulo = MODEL_CHANGED_EVERY_X_MONTHS;

        int numbMonthsToGoBack = modulo - 1;

        return DateHelper.setMonthAndDay(currentDate, month-numbMonthsToGoBack, 1);
    }

    private static boolean isModelDueToBeChanged() {
        Date currentDate = new Date();

        int month = DateHelper.getMonthOfDate(currentDate);

        int modulo = month % MODEL_CHANGED_EVERY_X_MONTHS;
        if (modulo == 0) modulo = MODEL_CHANGED_EVERY_X_MONTHS;

        return modulo - 1 == 0;
    }

}
