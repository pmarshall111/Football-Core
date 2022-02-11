package com.footballbettingcore.betfair;

import com.footballbettingcore.machineLearning.createData.classes.MatchToPredict;
import com.jbetfairng.BetfairClient;
import com.jbetfairng.entities.*;
import com.jbetfairng.enums.*;
import com.jbetfairng.exceptions.LoginException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.*;

import static com.footballbettingcore.scrape.classes.Team.matchBetfairNamesToUnderstat;

public class OddsRetriever {
    public final static String BETFAIR_EXCHANGE = "Betfair Exchange";

    private final static String FOOTBALL_ID = "1";
    private final static String EPL_ID = "10932509";
    private final static String LA_LIGA_ID = "117";
    private final static String BUNDESLIGA_ID = "59";
    private final static String LIGUE_1_ID = "55";
    private final static String SERIE_A_ID = "81";

    private static final Logger logger = LogManager.getLogger(OddsRetriever.class);

    public static void addOddsToMatches(ArrayList<MatchToPredict> matches) throws LoginException {
        BetfairClient client = Auth.login();

        MarketFilter eventMarketFilter = new MarketFilter();
        eventMarketFilter.setEventTypeIds(new HashSet<>(){{add(FOOTBALL_ID);}});
        eventMarketFilter.setCompetitionIds(new HashSet<>(Arrays.asList(EPL_ID, LA_LIGA_ID, BUNDESLIGA_ID, LIGUE_1_ID, SERIE_A_ID)));
        List<EventResult> events = client.listEvents(eventMarketFilter).getResponse();

        // need to match events to the matches
        HashMap<String, HashMap<String, MatchToPredict>> teamToMatch = getMatchesForTeams(matches);
        HashMap<String, MatchToPredict> eventIdToMatch = new HashMap<>();
        for (EventResult event: events) {
            String eventName = event.getEvent().getName();
            String[] teams = eventName.split(" v ");
            if (teams.length == 2) {
                String homeTeam = matchBetfairNamesToUnderstat(teams[0]);
                String awayTeam = matchBetfairNamesToUnderstat(teams[1]);
                if (teamToMatch.containsKey(homeTeam)) {
                    HashMap<String, MatchToPredict> homeTeamMatches = teamToMatch.get(homeTeam);
                    if (homeTeamMatches.containsKey(awayTeam)) {
                        eventIdToMatch.put(event.getEvent().getId(), homeTeamMatches.get(awayTeam));
                    } else {
                        logger.warn("Could not find a Betfair Exchange match for the away team of " + eventName);
                    }
                } else {
                    logger.warn("Could not find a Betfair Exchange match for the home team of " + eventName);
                }
            }
        }
        if (eventIdToMatch.size() < matches.size()) {
            Set<MatchToPredict> matchesFoundInBetfair = new HashSet<>(eventIdToMatch.values());
            for (MatchToPredict match: matches) {
                if (!matchesFoundInBetfair.contains(match)) {
                    logger.warn("Betfair exchange match not found for " + match.getMatchString());
                }
            }
        }

        if (eventIdToMatch.size() == 0) {
            return;
        }

        // next get the categories we can bet on for those events, so we can retrieve the ID for the final odds for the game
        MarketFilter marketsToBetOnMarketFilter = new MarketFilter();
        marketsToBetOnMarketFilter.setEventIds(eventIdToMatch.keySet());
        marketsToBetOnMarketFilter.setMarketTypeCodes(Collections.singleton("MATCH_ODDS"));

        Set<MarketProjection> marketProjections = new HashSet<>();
        marketProjections.add(MarketProjection.RUNNER_METADATA);
        marketProjections.add(MarketProjection.EVENT);


        List<MarketCatalogue> marketCatalogueList = client.listMarketCatalogue(
                marketsToBetOnMarketFilter,
                marketProjections,
                MarketSort.FIRST_TO_START,
                100
        ).getResponse();

        ArrayList<String> marketIds = new ArrayList<>();
        HashMap<Long, String> selectionIdToTeamname = new HashMap<>();
        HashMap<String, MatchToPredict> marketIdToMatch = new HashMap<>();
        for (MarketCatalogue marketCatalogue : marketCatalogueList) {
            List<RunnerCatalog> runnerInfos = marketCatalogue.getRunners();
            for (RunnerCatalog runnerInfo: runnerInfos) {
                selectionIdToTeamname.put(runnerInfo.getSelectionId(), matchBetfairNamesToUnderstat(runnerInfo.getRunnerName()));
            }
            marketIdToMatch.put(marketCatalogue.getMarketId(), eventIdToMatch.get(marketCatalogue.getEvent().getId()));
            marketIds.add(marketCatalogue.getMarketId());
        }

        // get the odds for the games
        PriceProjection pp = new PriceProjection();
        pp.setPriceData(new HashSet<>(Arrays.asList(PriceData.EX_BEST_OFFERS)));

        List<MarketBook> marketBooks = client.listMarketBook(
                marketIds,
                pp,
                OrderProjection.EXECUTABLE,
                MatchProjection.ROLLED_UP_BY_AVG_PRICE
        ).getResponse();

        for (MarketBook marketBook: marketBooks) {
            List<Runner> oddsInfo = marketBook.getRunners();
            double[] odds = new double[]{-1,-1,-1};
            MatchToPredict match = marketIdToMatch.get(marketBook.getMarketId());
            for (Runner runner: oddsInfo) {
                String oddsForTeam = selectionIdToTeamname.get(runner.getSelectionId());
                if (oddsForTeam.equals(match.getHomeTeamName())) {
                    odds[0] = runner.getEx().getAvailableToBack().get(0).getPrice();
                } else if (oddsForTeam.equals(match.getAwayTeamName())) {
                    odds[2] = runner.getEx().getAvailableToBack().get(0).getPrice();
                } else if (oddsForTeam.contains("Draw")) {
                    odds[1] = runner.getEx().getAvailableToBack().get(0).getPrice();
                }
            }

            if (odds[0] != -1 && odds[1] != -1 && odds[2] != -1) {
                match.setBookiesOdds(new LinkedHashMap<>(){{
                    put(BETFAIR_EXCHANGE, odds);}
                });
            }
        }
    }

    public static HashMap<String, HashMap<String, MatchToPredict>> getMatchesForTeams(ArrayList<MatchToPredict> matches) {
        HashMap<String, HashMap<String, MatchToPredict>> teamToMatch = new HashMap<>();
        matches.forEach(match -> {
            teamToMatch.putIfAbsent(match.getHomeTeamName(), new HashMap<>());
            HashMap<String, MatchToPredict> homeTeamMatches = teamToMatch.get(match.getHomeTeamName());
            homeTeamMatches.put(match.getAwayTeamName(), match);
        });
        return teamToMatch;
    }

    public static void main(String[] args) throws LoginException {
        addOddsToMatches(new ArrayList<>());
    }
}
