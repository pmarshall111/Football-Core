package com.footballbettingcore.scrape.classes;

/*
 * Enum used in taskScheduling/PredictTodaysGames to specify which bookies we've signed up for.
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
    SMARKETS("Smarkets"),
    OPTIMAL_ODDS("Best");
    
    private final String bookie;

    OddsCheckerBookies(String bookie) {
        this.bookie = bookie;
    }

    public String getName() {
        return bookie;
    }
}
