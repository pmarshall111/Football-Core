package com.petermarshall.scrape.classes;

import com.petermarshall.DateHelper;

import java.util.Date;
import java.util.HashMap;

public class Team {
    private String teamName;
    private HashMap<Date, Match> matchMap;

    public Team(String teamName) {
        this.teamName = teamName;
        this.matchMap = new HashMap<>();
    }

    public String getTeamName() {
        return teamName;
    }

    public HashMap<Date, Match> getAllMatches() {
        return matchMap;
    }

    public boolean addMatch(Match match) {
        Date date = match.getKickoffTime();
        Date dateKey = DateHelper.removeTimeFromDate(date);
        if (matchMap.containsKey(dateKey)) return false;
        else {
            matchMap.put(dateKey, match);
            return true;
        }
    }

    public Match getMatchFromAwayTeamName(String teamName) {
        teamName = Team.matchTeamNamesSofaScoreToUnderstat(teamName);
        for (Match m: matchMap.values()) {
            if (m.isAwayTeam(teamName)) return m;
        }
        return null;
    }

    /*
     * Method called from SofaScore & Understat scraping methods. Initially tries to get a match with the day SofaScore has in their
     * database. However, Understat has some matches where the days played are not the same as those according to SofaScore.
     * To allow for this, if the exact day match fails we look for the match 3 days either side of the date this method is
     * called with. If method still cannot find the match, we will return null.
     * Required instead of just finding the match from the opponents team name as Understat data does not store opposition team names,
     * but instead just gives the match dates and then the data.
     */
    public Match getMatchFromDate(Date date) {
        Date dateKey = DateHelper.removeTimeFromDate(date);
        Match match = matchMap.getOrDefault(dateKey, null);
        if (match == null) {
            Date[] dates = getPotentialDates(dateKey);
            for (Date d: dates) {
                match = matchMap.getOrDefault(d, null);
                if (match != null) {
                    break;
                }
            }
        }
        return match;
    }

    private Date[] getPotentialDates(Date dateKey) {
        //order is important as we want to have dates closest to target date first.
        return new Date[]{
                DateHelper.addXDaysToDate(dateKey, 1),
                DateHelper.addXDaysToDate(dateKey, -1),
                DateHelper.addXDaysToDate(dateKey, 2),
                DateHelper.addXDaysToDate(dateKey, -2),
                DateHelper.addXDaysToDate(dateKey, 3),
                DateHelper.addXDaysToDate(dateKey, -3),
        };
    }

    /*
     * Converting SofaScore team names to those used in Understat. This way round because teams are first created into
     * memory from Understat.
     */
    public static String matchTeamNamesSofaScoreToUnderstat(String teamName) {
        switch (teamName) {
            //EPL
            case "Wolverhampton":
                return "Wolverhampton Wanderers";
            case "Leicester City":
                return "Leicester";
            case "Brighton & Hove Albion":
                return "Brighton";
            case "West Ham United":
                return "West Ham";
            case "Huddersfield Town":
                return "Huddersfield";
            case "Cardiff City":
                return "Cardiff";
            case "Swansea City":
                return "Swansea";
            case "Stoke City":
                return "Stoke";
            case "Hull City":
                return "Hull";
            case "Norwich City":
                return "Norwich";
            case "Leeds United":
                return "Leeds";

            //LA LIGA
            case "Atlético Madrid":
                return "Atletico Madrid";
            case "Deportivo Alavés":
                return "Alaves";
            case "Athletic Bilbao":
                return "Athletic Club";
            case "Leganés":
                return "Leganes";
            case "Huesca":
                return "SD Huesca";
            case "Deportivo La Coruña":
                return "Deportivo La Coruna";
            case "Málaga":
                return "Malaga";
            case "Sporting Gijón":
                return "Sporting Gijon";
            case "RCD Mallorca":
                return "Mallorca";
            case "Cádiz":
                return "Cadiz";
            case "Elche CF":
                return "Elche";
            case "Real Betis Balompié":
                return "Real Betis";

            //BUNDESLIGA
            case "Borussia M'gladbach":
                return "Borussia M.Gladbach";
            case "Bayern München":
                return "Bayern Munich";
            case "RB Leipzig":
                return "RasenBallsport Leipzig";
            case "1899 Hoffenheim":
                return "Hoffenheim";
            case "Hertha BSC":
                return "Hertha Berlin";
            case "1. FSV Mainz 05":
                return "Mainz 05";
            case "Bayer 04 Leverkusen":
                return "Bayer Leverkusen";
            case "FC Schalke 04":
                return "Schalke 04";
            case "1. FC Nürnberg":
                return "Nuernberg";
            case "Fortuna Düsseldorf":
                return "Fortuna Duesseldorf";
            case "1. FC Köln":
                return "FC Cologne";
            case "Darmstadt 98":
                return "Darmstadt";
            case "SC Paderborn 07":
                return "Paderborn";
            case "1. FC Union Berlin":
                return "Union Berlin";
            case "FC Augsburg":
                return "Augsburg";
            case "FC Ingolstadt 04":
                return "Ingolstadt";
            case "VfL Wolfsburg":
                return "Wolfsburg";
            case "SC Freiburg":
                return "Freiburg";
            case "SpVgg Greuther Fürth":
                return "Greuther Fuerth";
            case "VfL Bochum":
                return "Bochum";


            //SERIE A
            case "Milan":
                return "AC Milan";
            case "Parma":
                return "Parma Calcio 1913";
            case "SPAL":
                return "SPAL 2013";
            case "ChievoVerona":
                return "Chievo";
            case "Hellas Verona":
                return "Verona";

            //LIGUE 1
            case "Paris Saint-Germain":
                return "Paris Saint Germain";
            case "Lille OSC":
                return "Lille";
            case "Olympique Lyonnais":
                return "Lyon";
            case "Saint-Étienne":
                return "Saint-Etienne";
            case "Olympique de Marseille":
                return "Marseille";
            case "Stade de Reims":
                return "Reims";
            case "OGC Nice":
                return "Nice";
            case "Stade Rennais":
                return "Rennes";
            case "Nîmes Olympique":
                return "Nimes";
            case "AS Monaco":
                return "Monaco";
            case "Bastia":
                return "SC Bastia";
            case "FC Nantes":
                return "Nantes";
            case "Stade Brestois 29":
                return "Brest";
            case "RC Lens":
                return "Lens";
            case "Clermont Foot 63":
                return "Clermont Foot";

            //RFPL (Russia)
            case "FK Krasnodar":
                return "FC Krasnodar";
            case "FK Rostov":
                return "FC Rostov";
            case "Orenburg":
                return "FC Orenburg";
            case "Akhmat Grozny":
                return "FK Akhmat";
            case "Ural Yekaterinburg":
                return "Ural";
            case "Dynamo Moscow":
                return "Dinamo Moscow";
            case "FK Ufa":
                return "FC Ufa";
            case "Yenisey Krasnoyarsk":
                return "FC Yenisey Krasnoyarsk";
            case "FK Tosno":
                return "Tosno";
            case "Amkar Perm":
                return "Amkar";
            case "FC Sochi":
                return "PFC Sochi";
            case "Tambov":
                return "FC Tambov";
            case "FK Khimki":
                return "Khimki";
            case "Rotor Volgograd":
                return "FC Rotor Volgograd";
            case "FK Nizhny Novgorod":
                return "Nizhny Novgorod";

            default:
                return teamName;
        }
    }

    /*
     * Converting Understat team names (those stored in the database) to those used in Oddschecker.
     *
     * NOTE: The France teamnames will have to be expanded as due to coronavirus times these were not available.
     */
    public static String matchTeamNamesUnderstatToOddsChecker(String teamName) {
        switch (teamName) {
            //EPL
            case "Manchester City":
                return "Man City";
            case "Wolverhampton Wanderers":
                return "Wolves";
            case "Manchester United":
                return "Man Utd";
            case "Newcastle United":
                return "Newcastle";
            case "Sheffield United":
                return "Sheffield Utd";
            case "West Bromwich Albion":
                return "West Brom";

            //BUNDESLIGA
            case "RassenBallsport Leipzig":
                return "RB Leipzig";
            case "Borussia M.Gladbach":
                return "Borussia Monchengladbach";
            case "FC Cologne":
                return "FC Koln";
            case "Hamburger SV":
                return "Hamburg";
            case "Hannover 96":
                return "Hannover";
            case "Nuernberg":
                return "FC Nurnberg";

            //LA LIGA
            case "Real Valladolid":
                return "Valladolid";
            case "Athletic Club":
                return "Athletic Bilbao";
            case "Sporting Gijon":
                return "Gijon";

            //SERIE A
            case "SPAL 2013":
                return "Spal";
            case "Parma Calcio 1913":
                return "Parma";
            case "Inter":
                return "Inter Milan";

            //LIGUE 1
            case "Paris Saint Germain":
                return "PSG";

            //RUSSIA
            case "FK Akhmat":
                return "Terek Grozny";
            case "Zenit St. Petersburg":
                return "Zenit St Petersburg";
            case "FC Krasnodar":
                return "FK Krasnodar";
            case "FC Tambov":
                return "FK Tambov";

            default:
                return teamName;
        }
    }
}