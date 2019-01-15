package com.petermarshall.logging;

import com.petermarshall.DateHelper;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Date;

public class LastPredicted {

    private static final int NUMB_DAYS_BEFORE_WE_PREDICT_AGAIN = 16; //will equate to scraping every 2 weeks as when we write when we last predicted, we subtract 2 days from current date.
    private static final String locationOfDateLastPredicted = "C:\\Users\\Peter\\Documents\\JavaProjects\\Football\\src\\com\\petermarshall\\logging\\dateWhenLastPredictedMissedGames.txt";

    public static boolean timeToPredictMissedGames() {
        Date dateWhenLastPredictedMissedGames = getWhenMissedGamesWereLastPredicted();

        Date currentDate = new Date();
        Date whenWeShouldPredictAgain = DateHelper.addDaysToDate(dateWhenLastPredictedMissedGames, NUMB_DAYS_BEFORE_WE_PREDICT_AGAIN);

        return currentDate.after(whenWeShouldPredictAgain);
    }

    public static Date getWhenMissedGamesWereLastPredicted() {

        try (BufferedReader br = new BufferedReader(new FileReader(locationOfDateLastPredicted))) {

            String dateString = br.readLine();
            return DateHelper.getDateFromStandardDateString(dateString);

        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }

    }

    /*
     * Stores the current date MINUS 2 DAYS in the file contained in locationOfDateLastPredicted variable.
     * Chosen to minus 2 days here as we do not scrape results in every day. So if we only looked at games from when we last scraped that have results, we'd end up not
     * making predictions on some games.
     *
     * Method to be called once we have made our predictions of previous games so the program will know it only needs to re-predict missed games in another 2 weeks time.
     */
    public static void setAllMissedGamesPredictedUpTo2DaysAgo() {
        try (FileWriter fileWriter = new FileWriter(locationOfDateLastPredicted)){ //no need for BufferedWriter here as we're only doing 1 write operation.

            Date currentDate = new Date();
            Date dateToStore = DateHelper.addDaysToDate(currentDate, -2);

            fileWriter.write(dateToStore.toString());

        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }


}
