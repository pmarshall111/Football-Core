package com.petermarshall.taskScheduling;

import com.petermarshall.DateHelper;
import com.petermarshall.mail.SendEmail;
import com.petermarshall.model.BetResult;
import com.petermarshall.model.BetResultsTotalled;
import com.petermarshall.model.DataSource;

import java.util.Date;
import java.util.HashSet;

public class EmailModelPerformance {

    private static final int MODEL_CHANGED_EVERY_X_MONTHS = 2;

    /*
     *Method will be schedulled on the first day of each month and will send a performance review along with a reminder to update the model if needed.
     */
    public static void main(String[] args) {
        System.out.println("Sending email of models performance...\n");

        Date lastChangeOfModel = calculateWhenModelWasLastChanged();

        BetResultsTotalled betResults = DataSource.getResultsOfPredictions(null, null);
        BetResultsTotalled resultsSinceLastModelChange = DataSource.getResultsOfPredictions(lastChangeOfModel, null);

        StringBuilder stringBuilder = new StringBuilder();


        addTotalledDataToString(stringBuilder, betResults);
        if (isModelDueToBeChanged()) addReminderToChangeModel(stringBuilder);
        addRecentModelDataToString(stringBuilder, lastChangeOfModel, resultsSinceLastModelChange);

        SendEmail.sendOutEmail("Betting app performance review", stringBuilder.toString());

    }

    private static void addTotalledDataToString(StringBuilder stringBuilder, BetResultsTotalled totalledData) {
//        StringBuilder stringBuilders = new StringBuilder();
//
        stringBuilder.append("Hello Peter,\n\nIn total, as of ");
        stringBuilder.append(new Date());
        stringBuilder.append(" our model has spent ");
        stringBuilder.append(totalledData.getTotalMoneyOut());
        stringBuilder.append(", making £");
        stringBuilder.append(totalledData.getRealProfit());
        stringBuilder.append(". This equates to a percentage profit of ");
        stringBuilder.append(totalledData.getPercentageProfit());
        stringBuilder.append(".");

    }

    private static void addReminderToChangeModel(StringBuilder stringBuilder) {
        stringBuilder.append("\n\n_______________________________\n\nAlert: It has now been " + MODEL_CHANGED_EVERY_X_MONTHS + " months since the model was last changed. " +
                "Please call taskSchedulling.RetrainPredictor, and transfer over the training data to Octave and bring back the retrained Thetas to Java. Thanks.\n\n" +
                "_______________________________\n\n");
    }

    private static void addRecentModelDataToString(StringBuilder stringBuilder, Date lastChangeOfModel, BetResultsTotalled recentData) {
        stringBuilder.append("<b>Recent Model Performance</b>\n");
        stringBuilder.append("Since the model was last changed on ");
        stringBuilder.append(lastChangeOfModel);
        stringBuilder.append(" our model has spent ");
        stringBuilder.append(recentData.getTotalMoneyOut());
        stringBuilder.append(", making £");
        stringBuilder.append(recentData.getRealProfit());
        stringBuilder.append(". This equates to a percentage profit of ");
        stringBuilder.append(recentData.getPercentageProfit());
        stringBuilder.append(".");
    }


    //TODO: test working correctly, even when changing how often model is changed.
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
