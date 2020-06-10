
import com.petermarshall.DateHelper;
import com.petermarshall.database.datasource.DS_Get;
import com.petermarshall.database.datasource.DS_Main;
import com.petermarshall.machineLearning.createData.classes.MatchToPredict;
import com.petermarshall.machineLearning.createData.refactor.PastStatsCalculator;
import com.petermarshall.machineLearning.logisticRegression.Predict;
import com.petermarshall.mail.SendEmail;
import com.petermarshall.scrape.OddsChecker;
import com.petermarshall.scrape.SofaScore;
import com.petermarshall.scrape.classes.OddsCheckerBookies;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;

public class testPredictions {

    public static void main(String[] args) {

//        SofaScore.updateTodaysKickoffTimes();


        Date earliestGame = DateHelper.setTimeOfDate(new Date(), 19,00,0);
        Date latestGame = DateHelper.setTimeOfDate(new Date(), 20,20,0);

        DS_Main.openProductionConnection();
//        ArrayList<MatchToPredict> matches = DataSource.getBaseMatchesToPredict(earliestGame, latestGame);
        ArrayList<MatchToPredict> matches = DS_Get.getMatchesToPredict(earliestGame, latestGame);
        DS_Main.closeConnection();

        SofaScore.addLineupsToGamesAboutToStart(matches);
        PastStatsCalculator.addFeaturesToPredict(matches);
        OddsChecker.addBookiesOddsForGames(matches);
        Predict.addOurProbabilitiesToGames(matches, "C:\\Users\\Peter\\Documents\\JavaProjects\\Football\\testThetas.csv");

        StringBuilder emailBody = new StringBuilder();
        emailBody.append("Dear app user,\n\n We currently suggest placing the following bets: \n\n");
//
//
//
        HashSet<String> bookiesWeveSignedUpFor = new HashSet<>();
        bookiesWeveSignedUpFor.add(OddsCheckerBookies.BET365.getBookie());
        bookiesWeveSignedUpFor.add(OddsCheckerBookies.SKYBET.getBookie());
        bookiesWeveSignedUpFor.add(OddsCheckerBookies.BETVICTOR.getBookie());
        bookiesWeveSignedUpFor.add(OddsCheckerBookies.LADBROKES.getBookie());

        //method can be called without last argument, to assume that we've signed up for all bookies.
        boolean gamesToEmail = Predict.calcBetsForCurrentGamesAndAddToBuilder(matches, emailBody, bookiesWeveSignedUpFor);
        if (gamesToEmail) {
            SendEmail.sendOutEmail("New bet", emailBody.toString());
            System.out.println("We found a good bet!");
        } else {
            System.out.println("No current good bets.");
        }
    }
}
