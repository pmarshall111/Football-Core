package com.petermarshall.taskScheduling;

import com.petermarshall.BetPlaced;
import com.petermarshall.BetPlacedUniBet;
import com.petermarshall.DateHelper;
import com.petermarshall.database.datasource.DS_Get;
import com.petermarshall.database.datasource.DS_Insert;
import com.petermarshall.database.datasource.DS_Main;
import com.petermarshall.database.BetLog;
import com.petermarshall.database.datasource.DS_Update;
import com.petermarshall.machineLearning.BetPlacedTemp;
import com.petermarshall.machineLearning.DecideBet;
import com.petermarshall.machineLearning.createData.CalcPastStats;
import com.petermarshall.machineLearning.BetDecision;
import com.petermarshall.machineLearning.BookieBetInfo;
import com.petermarshall.machineLearning.createData.classes.MatchToPredict;
import com.petermarshall.machineLearning.logisticRegression.Predict;
import com.petermarshall.mail.SendEmail;
import com.petermarshall.scrape.OddsChecker;
import com.petermarshall.scrape.SofaScore;
import com.petermarshall.scrape.classes.LeagueIdsAndData;
import com.petermarshall.scrape.classes.OddsCheckerBookies;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;


public class PredictPipeline {

    private static final double MIN_BALANCE_WARNING = 20.0;

    public static void main(String[] args) {
        finishPredictedGames();
        predictGames();
    }

    //Method will create non-lineup predictions for all teams next games in the database, but only the next 1 game.
    //Then will place a bet for us if good odds found, first will try Bet365, then if not possible tries UniBet
    public static void predictGames() {
        //first check that all games are updated.
        UpdatePipeline.updateGames(false);
        ArrayList<MatchToPredict> mtps = DS_Get.getMatchesToPredict();
        if (mtps.size() > 0) {
            CalcPastStats.addFeaturesToPredict(mtps, false);
            Predict.addOurProbabilitiesToGames(mtps);
            OddsChecker.addBookiesOddsForGames(mtps);
            DS_Insert.addPredictionsToDb(mtps);
            DecideBet.addDecisionRealMatches(mtps);
            mtps.removeIf(mtp -> mtp.getGoodBets().size() == 0);
            if (mtps.size() > 0) {
                betOnMatches(mtps);
            }
        }
    }

    //Method to be called when predictions have been made for games, but at the time no odds were available.
    //Goes through and tries to get odds and then potentially places a bet.
    public static void finishPredictedGames() {
        DS_Main.openProductionConnection();
        ArrayList<MatchToPredict> matches = DS_Get.getMatchesWithPredictionsButNoOdds();
        if (matches.size() > 0) {
            OddsChecker.addBookiesOddsForGames(matches);
            matches.removeIf(mtp -> mtp.getBookiesOdds() == null || mtp.getBookiesOdds().size() == 0);
            DS_Update.updatePredictionToIncludeOdds(matches);
            DecideBet.addDecisionRealMatches(matches);
            matches.removeIf(mtp -> mtp.getGoodBets().size() == 0);
            if (matches.size() > 0) {
                betOnMatches(matches);
            }
        }
    }

    public static void predictGamesWithLineups() {
        //first check that all games are updated.
        UpdatePipeline.updateGames(false);
        Date in5Mins = DateHelper.addXMinsToDate(new Date(), 5);
        Date in55Mins = DateHelper.addXMinsToDate(new Date(), 55);
        ArrayList<MatchToPredict> mtps = DS_Get.getMatchesToPredictByDates(in5Mins, in55Mins);
        if (mtps.size() > 0) {
            //add lineups & odds and remove game if couldn't get lineups or odds
            addLineupsAndOddsConcurrently(mtps);
            mtps.removeIf(mtp -> mtp.getHomeTeamPlayers().size() != 11 || mtp.getAwayTeamPlayers().size() != 11 ||
                    mtp.getBookiesOdds() == null || mtp.getBookiesOdds().size() == 0);
            if (mtps.size() > 0) {
                CalcPastStats.addFeaturesToPredict(mtps, false);
                Predict.addOurProbabilitiesToGames(mtps);
                DS_Insert.addPredictionsToDb(mtps);
                DecideBet.addDecisionRealMatches(mtps);
                mtps.removeIf(mtp -> mtp.getGoodBets().size() == 0);
                if (mtps.size() > 0) {
                    betOnMatches(mtps);
                }
            }
        }
    }

    private static void addLineupsAndOddsConcurrently(ArrayList<MatchToPredict> mtps) {
        try {
            Thread addOdds = new Thread(new OddsChecker(mtps));
            addOdds.start();
            SofaScore.addLineupsToGamesAboutToStart(mtps);
            addOdds.join(); //wait for completion of add odds thread
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    //Currently not betting on anything. Just stores matches in db and sends me and email.
    private static void betOnMatches(ArrayList<MatchToPredict> matches) {
        StringBuilder sb = new StringBuilder();
        int betsPlaced = 0;
        double bet365Balance = -1, unibetBalance = -1;
        if (matches.size() > 0) {
            //not a problem to go through individually as not expecting to have many bets at the same time.
            for (MatchToPredict mtp: matches) {
                String leagueName = translateLeagueNameBet365(mtp.getLeagueName());
                String homeTeam = mtp.getHomeTeamName();
                String awayTeam = mtp.getAwayTeamName();
                for (BetDecision bd: mtp.getGoodBets()) {
                    for (BookieBetInfo bookieBetInfo : bd.getBookiePriority()) {
                        if (bookieBetInfo.getBookie().equals(OddsCheckerBookies.BET365)) {
//                            BetPlaced bet365 = placeBet(leagueName, homeTeam, awayTeam, bd.getWinner().getSqlIntCode(), bookieBetInfo.getStake(), bookieBetInfo.getMinOdds());
                            BetPlaced bet365 = new BetPlaced(bookieBetInfo.getMinOdds(),bookieBetInfo.getStake(), true, 100);
                            if (bet365.getBalance() > -1) {
                                bet365Balance = bet365.getBalance();
                            }
                            if (bet365.isBetSuccessful()) {
                                DS_Insert.logBetPlaced(new BetLog(mtp, bd.getWinner(), bookieBetInfo.getBookie().getName(), bet365.getOddsOffered(), bet365.getStake()));
                                sb.append(homeTeam + " vs " + awayTeam + ": Bet £" + bet365.getStake() + " on " + bd.getWinner().name() + " at odds of " +
                                        bet365.getOddsOffered() + ". Potential return: " + (5*bet365.getOddsOffered()));
                                betsPlaced++;
                            } else if (bet365.getOddsOffered() > bookieBetInfo.getMinOdds()) {
                                //try again with different stake
                                HashMap<String, double[]> bookiesOdds = mtp.getBookiesOdds();
                                bookiesOdds.get(OddsCheckerBookies.BET365.getName())[bd.getWinner().getSqlIntCode()] = bet365.getOddsOffered();
                                DecideBet.addDecisionRealMatches(new ArrayList<>(Arrays.asList(mtp)));
                                matches.add(mtp);
                            }
                            break; //ensure we do not place bets on multiple sites for the same bet.
                        } else if (bookieBetInfo.getBookie().equals(OddsCheckerBookies.UNIBET)) {
                            String country = getCountryFromLeagueName(mtp.getLeagueName());
                            String leagueNameUb = translateLeagueNameUnibet(mtp.getLeagueName());
//                            BetPlacedUniBet unibet = AutomateBetUniBet.placeBet(country, leagueNameUb, homeTeam, awayTeam, bd.getWinner().getSqlIntCode(),
//                                    bookieBetInfo.getStake(), bookieBetInfo.getMinOdds());
                            BetPlacedUniBet unibet = new BetPlacedUniBet(bookieBetInfo.getMinOdds(), bookieBetInfo.getStake(), true, 100);
                            if (unibet.getBalance() > -1) {
                                unibetBalance = unibet.getBalance();
                            }
                            if (unibet.isBetSuccessful()) {
                                DS_Insert.logBetPlaced(new BetLog(mtp, bd.getWinner(), bookieBetInfo.getBookie().getName(),
                                        unibet.getOddsOffered(), unibet.getStake()));
                                sb.append("- " + homeTeam + " vs " + awayTeam + ": Bet £" + unibet.getStake() + " on " + bd.getWinner().name() + " at odds of " +
                                        unibet.getOddsOffered() + ". Potential return: " + (5*unibet.getOddsOffered()) + "\n");
                                betsPlaced++;
                            } else if (unibet.getOddsOffered() > bookieBetInfo.getMinOdds()) {
                                //try again with different stake
                                HashMap<String, double[]> bookiesOdds = mtp.getBookiesOdds();
                                bookiesOdds.get(OddsCheckerBookies.UNIBET.getName())[bd.getWinner().getSqlIntCode()] = unibet.getOddsOffered();
                                DecideBet.addDecisionRealMatches(new ArrayList<>(Arrays.asList(mtp)));
                                matches.add(mtp);
                            }
                            break; //ensure we do not place bets on multiple sites for the same bet.
                        }
                    }
                }
            }
        }
        //emailing results
        if (betsPlaced > 0) {
            sb.insert(0, "Hello,\nWe have placed " + betsPlaced + " automated bets for you:\n\n");
            addBalancesToBuilder(sb, bet365Balance, unibetBalance);
            SendEmail.sendOutEmail("New bets placed!", sb.toString());
        } else if (bet365Balance > -1 || unibetBalance > -1) {
            //odds not as good as advertised.
            sb.append("Hello, \nWe were unable to place bets this time. This could be because the odds weren't good enough, we were unable to find the odds on the" +
                    " website, or because your balance is too low to place a bet.");
        }
    }

    private static void addBalancesToBuilder(StringBuilder sb, double bet365Balance, double unibetBalance) {
        if (bet365Balance > -1) {
            sb.append("\nYour bet365 balance is now: £" + bet365Balance + (bet365Balance > -1 && bet365Balance < MIN_BALANCE_WARNING ? "\t***LOW BALANCE***" : ""));
        }
        if (unibetBalance > -1) {
            sb.append("\nYour unibet balance is now: £" + unibetBalance + (unibetBalance > -1 && unibetBalance < MIN_BALANCE_WARNING ? "\t***LOW BALANCE***" : ""));
        }
    }

    private static String translateLeagueNameBet365(String leagueName) {
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

    private static String translateLeagueNameUnibet(String leagueName) {
        if (leagueName.equals(LeagueIdsAndData.EPL.name()) || leagueName.equals(LeagueIdsAndData.RUSSIA.name())) {
            return "Premier League";
        } else if (leagueName.equals(LeagueIdsAndData.LA_LIGA.name())) {
            return "La Liga";
        } else if (leagueName.equals(LeagueIdsAndData.BUNDESLIGA.name())) {
            return "Bundesliga";
        } else if (leagueName.equals(LeagueIdsAndData.SERIE_A.name())) {
            return "Serie A";
        } else if (leagueName.equals(LeagueIdsAndData.LIGUE_1.name())) {
            return "Ligue 1";
        }
        return null;
    }


    private static String getCountryFromLeagueName(String leagueName) {
        if (leagueName.equals(LeagueIdsAndData.EPL.name())) {
            return "England";
        } else if (leagueName.equals(LeagueIdsAndData.LA_LIGA.name())) {
            return "Spain";
        } else if (leagueName.equals(LeagueIdsAndData.BUNDESLIGA.name())) {
            return "Germany";
        } else if (leagueName.equals(LeagueIdsAndData.SERIE_A.name())) {
            return "Italy";
        } else if (leagueName.equals(LeagueIdsAndData.LIGUE_1.name())) {
            return "France";
        } else if (leagueName.equals(LeagueIdsAndData.RUSSIA.name())) {
            return "Russia";
        }
        return null;
    }
}
