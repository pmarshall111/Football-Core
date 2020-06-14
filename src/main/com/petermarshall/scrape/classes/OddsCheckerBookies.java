package com.petermarshall.scrape.classes;

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
        allBookies.add(BET365.getName());
        allBookies.add(SKYBET.getName());
        allBookies.add(LADBROKES.getName());
        allBookies.add(WILLIAM_HILL.getName());
        allBookies.add(MARATHON_BET.getName());
        allBookies.add(BETFAIR_SPORTSBOOK.getName());
        allBookies.add(BETVICTOR.getName());
        allBookies.add(PADDY_POWER.getName());
        allBookies.add(UNIBET.getName());
        allBookies.add(CORAL.getName());
        allBookies.add(BETFRED.getName());
        allBookies.add(BOYLE_SPORTS.getName());
        allBookies.add(BLACK_TYPE.getName());
        allBookies.add(REDZONE.getName());
        allBookies.add(BETWAY.getName());
        allBookies.add(BETBRIGHT.getName());
        allBookies.add(TEN_BET.getName());
        allBookies.add(SPORTINGBET.getName());
        allBookies.add(ONE_EIGHT_EIGHT_BET.getName());
        allBookies.add(EIGHT_EIGHT_EIGHT_SPORT.getName());
        allBookies.add(SPORTPESA.getName());
        allBookies.add(SPREADEX.getName());
        allBookies.add(ROYAL_PANDA.getName());
        allBookies.add(SPORT_NATION.getName());
        allBookies.add(BETFAIR.getName());
        allBookies.add(BETDAQ.getName());
        allBookies.add(MATCHBOOK.getName());
        allBookies.add(SMARKETS.getName());
    }
    
    private final String bookie;

    OddsCheckerBookies(String bookie) {
        this.bookie = bookie;
    }

    public String getName() {
        return bookie;
    }

    public static HashSet<String> getAllBookies() {
        return allBookies;
    }
}
