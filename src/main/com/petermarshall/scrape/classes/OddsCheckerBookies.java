package com.petermarshall.scrape.classes;

import java.util.Arrays;
import java.util.HashSet;

/*
 * Enum used in taskScheduling/PredictTodaysGames to specify which bookies we've signed up for.
 * TODO: needs a test from within OddsChecker scraper to see if any of the bookies have been removed or had their name changed.
 */
public enum OddsCheckerBookies {
    BET365("Bet365"),
    SKYBET("Skybet"),
    LADBROKES("Ladbrokes"),
    WILLIAM_HILL("William Hill"),
    MARATHON_BET("Marathon Bet"),
    BETFAIR_SPORTSBOOK("Betfair Sportsbook"),
    BETVICTOR("Bet Victor"), 
    PADDY_POWER("Paddy Power"),
    UNIBET("Unibet"),
    CORAL("Coral"), 
    BETFRED("Betfred"),
    BOYLE_SPORTS("Boyle Sports"), 
    BLACK_TYPE("Black Type"), 
    REDZONE("Redzone"), 
    BETWAY("Betway"), 
    BETBRIGHT("BetBright"), 
    TEN_BET("10Bet"), 
    SPORTINGBET("Sportingbet"), 
    ONE_EIGHT_EIGHT_BET("188Bet"), 
    EIGHT_EIGHT_EIGHT_SPORT("888sport"), 
    SPORTPESA("SportPesa"), 
    SPREADEX("Spreadex"), 
    ROYAL_PANDA("Royal Panda"), 
    SPORT_NATION("Sport Nation"),
    BETFAIR("Betfair"), 
    BETDAQ("Betdaq"), 
    MATCHBOOK("Matchbook"), 
    SMARKETS("Smarkets");
    
    private static HashSet<String> allBookies = new HashSet<>();
    static {
        allBookies.add(BET365.getBookie());
        allBookies.add(SKYBET.getBookie());
        allBookies.add(LADBROKES.getBookie());
        allBookies.add(WILLIAM_HILL.getBookie());
        allBookies.add(MARATHON_BET.getBookie());
        allBookies.add(BETFAIR_SPORTSBOOK.getBookie());
        allBookies.add(BETVICTOR.getBookie());
        allBookies.add(PADDY_POWER.getBookie());
        allBookies.add(UNIBET.getBookie());
        allBookies.add(CORAL.getBookie());
        allBookies.add(BETFRED.getBookie());
        allBookies.add(BOYLE_SPORTS.getBookie());
        allBookies.add(BLACK_TYPE.getBookie());
        allBookies.add(REDZONE.getBookie());
        allBookies.add(BETWAY.getBookie());
        allBookies.add(BETBRIGHT.getBookie());
        allBookies.add(TEN_BET.getBookie());
        allBookies.add(SPORTINGBET.getBookie());
        allBookies.add(ONE_EIGHT_EIGHT_BET.getBookie());
        allBookies.add(EIGHT_EIGHT_EIGHT_SPORT.getBookie());
        allBookies.add(SPORTPESA.getBookie());
        allBookies.add(SPREADEX.getBookie());
        allBookies.add(ROYAL_PANDA.getBookie());
        allBookies.add(SPORT_NATION.getBookie());
        allBookies.add(BETFAIR.getBookie());
        allBookies.add(BETDAQ.getBookie());
        allBookies.add(MATCHBOOK.getBookie());
        allBookies.add(SMARKETS.getBookie());
    }
    
    private final String bookie;

    OddsCheckerBookies(String bookie) {
        this.bookie = bookie;
    }

    public String getBookie() {
        return bookie;
    }

    public static HashSet<String> getAllBookies() {
        return allBookies;
    }
}
