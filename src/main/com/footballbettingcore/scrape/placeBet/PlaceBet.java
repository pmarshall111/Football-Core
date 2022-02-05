package com.footballbettingcore.scrape.placeBet;

import com.footballbettingcore.database.BetLog;
import com.footballbettingcore.database.Result;
import com.footballbettingcore.database.datasource.DS_Insert;
import com.footballbettingcore.machineLearning.BookieBetInfo;
import com.footballbettingcore.machineLearning.createData.classes.MatchToPredict;
import com.footballbettingcore.mail.SendEmail;
import com.footballbettingcore.scrape.placeBet.bet365.BetPlaced;
import com.footballbettingcore.scrape.placeBet.unibet.AutomateBetUniBet;
import com.footballbettingcore.scrape.placeBet.unibet.BetPlacedUniBet;
import com.footballbettingcore.scrape.classes.LeagueIdsAndData;
import com.footballbettingcore.scrape.classes.OddsCheckerBookies;
import com.footballbettingcore.scrape.placeBet.bet365.AutomateBet;

import java.util.ArrayList;

public class PlaceBet {
    private static final double MIN_BALANCE_WARNING = 20.0;

    public static void main(String[] args) {
        MatchToPredict mtp = new MatchToPredict("Inter", "AC Milan", "21-22", "SERIE_A", "2022-02-05", -1, -1);
        mtp.addGoodBet(new BookieBetInfo(OddsCheckerBookies.BET365, Result.HOME_WIN, 0.25, 2));
        ArrayList<MatchToPredict> mtps = new ArrayList<>(){{add(mtp);}};
        betOnMatches(mtps);
    }

    public static void betOnMatches(ArrayList<MatchToPredict> matches) {
        StringBuilder sb = new StringBuilder();
        int totalBets = 0;
        double bet365Balance = -1, unibetBalance = -1;
        if (matches.size() > 0) {
            //not a problem to go through individually as not expecting to have many bets at the same time.
            for (MatchToPredict mtp: matches) {
                String leagueName = translateLeagueNameBet365(mtp.getLeagueName());
                String homeTeam = mtp.getHomeTeamName();
                String awayTeam = mtp.getAwayTeamName();
                for (BookieBetInfo betInfo: mtp.getGoodBets()) {
                    // TODO: Bet365 scraper not letting me log in at the moment
//                    BetPlaced bet365 = AutomateBet.placeBet(leagueName, homeTeam, awayTeam, betInfo.getBetOn().getSqlIntCode(), betInfo.getStake(), betInfo.getMinOdds());
////                            BetPlaced bet365 = new BetPlaced(betInfo.getMinOdds(),betInfo.getStake(), true, 100);
//                    if (bet365.getBalance() > -1) {
//                        bet365Balance = bet365.getBalance();
//                    }
//                    if (bet365.isBetSuccessful()) {
//                        DS_Insert.logBetPlaced(new BetLog(mtp, betInfo.getBetOn(), betInfo.getBookie().getName(), bet365.getOddsOffered(), bet365.getStake()));
//                        sb.append(homeTeam + " vs " + awayTeam + ": Bet £" + bet365.getStake() + " on " + betInfo.getBetOn().name() + " at odds of " +
//                                bet365.getOddsOffered() + ". Potential return: " + (5*bet365.getOddsOffered()));
//                        totalBets++;
//                    } else {
                    // TODO: Unibet scraper needs amending to place bets for more than just the teams in the preview.

//                        String country = getCountryFromLeagueName(mtp.getLeagueName());
//                        String leagueNameUb = translateLeagueNameUnibet(mtp.getLeagueName());
//                        BetPlacedUniBet unibet = AutomateBetUniBet.placeBet(country, leagueNameUb, homeTeam, awayTeam, betInfo.getBetOn().getSqlIntCode(),
//                                betInfo.getStake(), betInfo.getMinOdds());
////                        BetPlacedUniBet unibet = new BetPlacedUniBet(betInfo.getMinOdds(), betInfo.getStake(), true, 100);
//                        if (unibet.getBalance() > -1) {
//                            unibetBalance = unibet.getBalance();
//                        }
//                        if (unibet.isBetSuccessful()) {
//                            DS_Insert.logBetPlaced(new BetLog(mtp, betInfo.getBetOn(), betInfo.getBookie().getName(),
//                                    unibet.getOddsOffered(), unibet.getStake()));
//                            sb.append("- " + homeTeam + " vs " + awayTeam + ": Bet £" + unibet.getStake() + " on " + betInfo.getBetOn().name() + " at odds of " +
//                                    unibet.getOddsOffered() + ". Potential return: " + (5*unibet.getOddsOffered()) + "\n");
//                            totalBets++;
//                        } else {
                            // neither bookie had odds that were better than the Bet365 odds scraped from Oddschecker
                            DS_Insert.logBetPlaced(new BetLog(mtp, betInfo.getBetOn(), "NOT_YET_PLACED", betInfo.getMinOdds(), betInfo.getStake()));
                            sb.append("- (NOT YET PLACED) " + homeTeam + " vs " + awayTeam + ": Recommend to bet £" + betInfo.getStake() + " on " + betInfo.getBetOn() + " at odds of " +
                                    betInfo.getMinOdds() + ". Potential return: " + (5*betInfo.getStake()*betInfo.getMinOdds()) + "\n");
                            totalBets++;
//                        }
//                    }
                }
            }
        }
        //emailing results
        if (totalBets > 0) {
            sb.insert(0, "Hello,\nWe have " + totalBets + " automated bets for you:\n\n");
            addBalancesToBuilder(sb, bet365Balance, unibetBalance);
            SendEmail.sendOutEmail("New bets placed!", sb.toString(), SendEmail.ADMIN_EMAIL);
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
            return "France Ligue 1";
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
