package trainingData;

public class TrainingMatch {

//    private ArrayList<PlayerMatchDbData> epl2018Data;
//    private ArrayList<HistoricMatchDbData> pre2018EplMatchHistory;
//    private ArrayList<PlayerMatchDbData> allEplData;
//
//    @BeforeClass
//    public void setup() {
//        //going to need an overall test for actually translating getting data out the db to features
//        //but also smaller than that
//
//        //actually we will need a test DB because the way the thing calculates is to go through all available records.
//
//
//        //class will get the same data out in 2 different forms.
//        //will get out all data from the whole league (as that is what the model will be trained on), and also get the data from the seaason along with the historic data as that
//        //is how the data will be predicted on
//        DS_Main.openProductionConnection();
//        epl2018Data = DS_Get.getLeagueData(LeagueIdsAndData.EPL.name(), 18);
//        pre2018EplMatchHistory = DS_Get.getMatchesBetweenTeams(LeagueIdsAndData.EPL.name(), createMatchesToGetAllTeamsHistoryOut());
//        allEplData = DS_Get.getLeagueData(LeagueIdsAndData.EPL);
//        DS_Main.closeConnection();
//
//        ArrayList<MatchToPredict> matches = new ArrayList<>();
//        CalculatePastStats.addFeaturesToPredict(matches, true);
//    }
//
//    private ArrayList<MatchToPredict> createMatchesToGetAllTeamsHistoryOut() {
//        String[] teamsInEpl2018 = new String[]{"Liverpool", "Manchester City", "Manchester United", "Chelsea", "Arsenal", "Tottenham", "Bournemouth", "Southampton", "West Ham",
//                                            "Huddersfield", "Fulham", "Cardiff", "Brighton", "Burnley", "Newcastle United", "Crystal Palace", "Watford",
//                                            "Leicester", "Everton", "Wolverhampton Wanderers"};
//        ArrayList<MatchToPredict> matchesWithAllTeams = new ArrayList<>();
//        for (int i = 0; i<teamsInEpl2018.length; i+=2) {
//            String t1 = teamsInEpl2018[i];
//            String t2 = teamsInEpl2018[i+1];
//            MatchToPredict mtp = new MatchToPredict(t1,t2,"18-19", LeagueIdsAndData.EPL.name(),
//                    DateHelper.getSqlDate(DateHelper.createDateyyyyMMdd("2018", "06","01")),-1, -1);
//            matchesWithAllTeams.add(mtp);
//        }
//        return matchesWithAllTeams;
//    }
//
//    @Test
//    public void canCreateAvgGoalsFor() {
//
//    }
//
//    @Test
//    public void canCreateAvgGoalsAgainst() {
//
//    }
//
//    @Test
//    public void canCreateAvgXGF() {
//
//    }
//
//    @Test
//    public void canCreateAvgXGA() {
//
//    }
//
//    @Test
//    public void canCreateAvgPoints() {
//
//    }
//
//    @Test
//    public void canCreateAvgPointsAgainstOppositionInPast() {
//
//    }
//
//    @Test
//    public void canCreateAvgCleanSheets() {
//
//    }
//
//    @Test
//    public void canCreateLinuepStrength() {
//
//    }

}
