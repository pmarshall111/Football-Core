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

    public boolean addMatch(Match match) {
        Date date = match.getKickoffTime();

        Date dateKey = DateHelper.removeTimeFromDate(date);

        if (matchMap.containsKey(dateKey)) return false;
        else {
            matchMap.put(dateKey, match);
            return true;
        }
    }

    /*
     * Method called from SofaScore & Understat scraping methods. Initially tries to get a match with the day SofaScore has in their
     * database. However, Understat has some matches where the days played are not the same as those according to SofaScore.
     * To allow for this, if the exact day match fails we look for the match 2 days either side of the date this method is
     * called with. If method still cannot find the match, we will return null.
     */
    //TODO: surely we can separate out getMatch to be one overload with the date and then another with the away team name? Slight problem is that we store all games in 1 array
    //todo: no matter if they are home or away. possibly have methods getMatchFromDate, getMatchFromAwayTeamName, getMatchFromHomeTeamName.

    //TODO: need to see where these funcs are used and how it will impact current project.
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

    public Match getMatchFromAwayTeamName(String teamName) {
        teamName = Team.makeTeamNamesCompatible(teamName);
        for (Match m: matchMap.values()) {
            if (m.isAwayTeam(teamName)) return m;
        }
        return null;
    }

    private Date[] getPotentialDates(Date dateKey) {
        Date yesterday = DateHelper.subtract1DayFromDate(dateKey);
        Date tomorrow = DateHelper.add1DayToDate(dateKey);

        //order is important as we want to have dates closest to target date first.
        return new Date[]{
                yesterday,
                tomorrow,
                DateHelper.add1DayToDate(tomorrow),
                DateHelper.subtract1DayFromDate(yesterday)
        };
    }



    public HashMap<Date, Match> getAllMatches() {
        return matchMap;
    }

    public String getTeamName() {
        return teamName;
    }
    public void setTeamName(String teamName) {
        this.teamName = teamName;
    }


    /*
     * Used to convert SofaScore team names to those used in Understat. This way round because teams are first created into
     * memory from Understat.
     */
    public static String makeTeamNamesCompatible(String teamName) {
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

            default:
                return teamName;
        }
    }
}