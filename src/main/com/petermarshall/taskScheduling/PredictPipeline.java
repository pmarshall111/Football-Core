package com.petermarshall.taskScheduling;

import com.petermarshall.BetPlaced;
import com.petermarshall.database.Result;
import com.petermarshall.database.datasource.DS_Get;
import com.petermarshall.database.datasource.DS_Insert;
import com.petermarshall.database.datasource.DS_Main;
import com.petermarshall.logging.MatchLog;
import com.petermarshall.machineLearning.DecideBet;
import com.petermarshall.machineLearning.ModelPredict;
import com.petermarshall.machineLearning.createData.CalculatePastStats;
import com.petermarshall.machineLearning.createData.classes.BetDecision;
import com.petermarshall.machineLearning.createData.classes.MatchToPredict;
import com.petermarshall.mail.SendEmail;
import com.petermarshall.scrape.OddsChecker;
import com.petermarshall.scrape.classes.League;
import com.petermarshall.scrape.classes.LeagueIdsAndData;
import com.petermarshall.scrape.classes.OddsCheckerBookies;

import java.util.ArrayList;
import java.util.HashMap;

import static com.petermarshall.AutomateBet.placeBet;

public class PredictPipeline {
    //Method will create non-lineup predictions for all teams next games in the database, but only the next 1 game.
    public static void predictGames() {
        //maybe first do a check that all games are updated.
        DS_Main.openProductionConnection();
        HashMap<League, String> leaguesToUpdate = DS_Get.getLeaguesToUpdate();
        if (leaguesToUpdate.keySet().size() > 0) {
            UpdatePipeline.updateGames(leaguesToUpdate);
        }

        ArrayList<MatchToPredict> mtps = DS_Get.getMatchesToPredict();
        if (mtps.size() > 0) {
            //matches need lineups for this func!
            //need to edit such that if there are no players involved we only create a non-lineup prediction
            CalculatePastStats.addFeaturesToPredict(mtps, false);
            ModelPredict.addBasePredictions(mtps);
            OddsChecker.addBookiesOddsForGames(mtps);
            DS_Insert.addPredictionsToDb(mtps);
            DecideBet.addDecision(mtps);
            mtps.removeIf(mtp -> mtp.getGoodBets().size() == 0);
            StringBuilder sb = new StringBuilder();
            int betsPlaced = 0;
            if (mtps.size() > 0) {
                //not a problem to go through individually as not expecting to have many bets at the same time.
                for (MatchToPredict mtp: mtps) {
                    String leagueName = translateLeagueName(mtp.getLeagueName());
                    String homeTeam = mtp.getHomeTeamName();
                    String awayTeam = mtp.getAwayTeamName();
                    for (BetDecision bd: mtp.getGoodBets()) {
                        if (bd.getBookie().equals(OddsCheckerBookies.BET365)) {
                            BetPlaced bet = placeBet(leagueName, homeTeam, awayTeam, bd.getWinner().getSetting(), 5, bd.getMinOdds());
                            if (bet.isBetSuccessful()) {
                                Result res = Result.convertFromWinnerToRes(bd.getWinner());
                                DS_Insert.logBetPlaced(new MatchLog(mtp, res, bd.getBookie().getName(), bet.getOddsOffered(), bet.getStake()));
                                sb.append(homeTeam + " vs " + awayTeam + ": Bet £5 on " + res.name() + " at odds of " +
                                        bet.getOddsOffered() + ". Potential return: " + (5*bet.getOddsOffered()));
                                betsPlaced++;
                            }
                        }
                    }
                }
            }
            if (betsPlaced > 0) {
                //add in email capabilities only for when we go ahead and actually bet on something.
                sb.insert(0, "Hello,\nWe have placed " + betsPlaced + " automated bets for you:\n\n");
                SendEmail.sendOutEmail("New bets placed!", sb.toString());
            }
        }
    }

    private static String translateLeagueName(String leagueName) {
        if (leagueName.equals(LeagueIdsAndData.EPL.name())) {
            return "England Premier League";
        } else if (leagueName.equals(LeagueIdsAndData.LA_LIGA.name())) {
            return "Spain Primera Liga";
        } else if (leagueName.equals(LeagueIdsAndData.BUNDESLIGA.name())) {
            return "Germany Bundesliga I";
        } else if (leagueName.equals(LeagueIdsAndData.SERIE_A.name())) {
            return "Italy Serie A";
        } else if (leagueName.equals(LeagueIdsAndData.LIGUE_1.name())) {
            return "France Ligue 1"; //TODO: since league has been cancelled, not showing on site. NEEDS CHECKING
        } else if (leagueName.equals(LeagueIdsAndData.RUSSIA.name())) {
            return "Russia Premier League";
        }
        return null;
    }
}
