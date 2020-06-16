package trainingData;

import com.petermarshall.machineLearning.createData.classes.GamesSelector;
import com.petermarshall.machineLearning.createData.classes.Player;
import com.petermarshall.machineLearning.createData.classes.TrainingTeamsSeason;
import com.petermarshall.machineLearning.createData.CalculatePastStats;
import com.petermarshall.machineLearning.createData.PlayerMatchDbData;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

import static com.petermarshall.machineLearning.createData.classes.TrainingTeamsSeason.AVG_GOALS_PER_GAME;

//Right now testing is only for overall stats and not for separated home and away
public class TrainingTeamsSeasonTest {
    private static TrainingTeamsSeason team1;
    private static TrainingTeamsSeason team2;
    private static int[] HOMESCORE = new int[]{4,1,0,7,0,1};
    private static int[] AWAYSCORE = new int[]{0,3,2,2,0,1};
    private static double[] HOMEXG = new double[]{4.37,2.71,1.72,5.22,0.45,0.82};
    private static double[] AWAYXG = new double[]{1.44,3.12,1.54,1.19,1.04,1.33};
    private static int[] FIRSTSCORER = new int[]{1,1,2,2,0,1};
    private static HashMap<String,Player>[] HOMERATINGS = new HashMap[6];
    private static HashMap<String,Player>[] AWAYRATINGS = new HashMap[6];
    private static String[] PLAYER_NAMES = new String[]{"Fabinho", "Gomez", "Alexander-Arnold", "Wijnaldum", "Salah", "Mane", "Firmino", "Keita", "Alisson", "Van Dijk", "Robertson"};
    
    @Before
    public void setup() {
        team1 = new TrainingTeamsSeason(19);
        team2 = new TrainingTeamsSeason(19);
        PlayerMatchDbData game1 = PlayerMatchDbData.getTestPlayerMatchDbData(HOMESCORE[0],AWAYSCORE[0],HOMEXG[0],AWAYXG[0],FIRSTSCORER[0]);
        PlayerMatchDbData game2 = PlayerMatchDbData.getTestPlayerMatchDbData(HOMESCORE[1],AWAYSCORE[1],HOMEXG[1],AWAYXG[1],FIRSTSCORER[1]);
        PlayerMatchDbData game3 = PlayerMatchDbData.getTestPlayerMatchDbData(HOMESCORE[2],AWAYSCORE[2],HOMEXG[2],AWAYXG[2],FIRSTSCORER[2]);
        PlayerMatchDbData game4 = PlayerMatchDbData.getTestPlayerMatchDbData(HOMESCORE[3],AWAYSCORE[3],HOMEXG[3],AWAYXG[3],FIRSTSCORER[3]);
        PlayerMatchDbData game5 = PlayerMatchDbData.getTestPlayerMatchDbData(HOMESCORE[4],AWAYSCORE[4],HOMEXG[4],AWAYXG[4],FIRSTSCORER[4]);
        PlayerMatchDbData game6 = PlayerMatchDbData.getTestPlayerMatchDbData(HOMESCORE[5],AWAYSCORE[5],HOMEXG[5],AWAYXG[5],FIRSTSCORER[5]);
        createPlayerRatings(true);
        createPlayerRatings(false);
        //game1 team1 vs team2
        CalculatePastStats.addStatsToTeamsSeasons(game1, team1, team2, HOMERATINGS[0], AWAYRATINGS[0]);
        //game2 team2 vs team1
        CalculatePastStats.addStatsToTeamsSeasons(game2, team2, team1, HOMERATINGS[1], AWAYRATINGS[1]);
        //game3 team1 vs team2
        CalculatePastStats.addStatsToTeamsSeasons(game3, team1, team2, HOMERATINGS[2], AWAYRATINGS[2]);
        //game4 team2 vs team1
        CalculatePastStats.addStatsToTeamsSeasons(game4, team2, team1, HOMERATINGS[3], AWAYRATINGS[3]);
        //game5 team1 vs team2
        CalculatePastStats.addStatsToTeamsSeasons(game5, team1, team2, HOMERATINGS[4], AWAYRATINGS[4]);
        //game6 team2 vs team1
        CalculatePastStats.addStatsToTeamsSeasons(game6, team2, team1, HOMERATINGS[5], AWAYRATINGS[5]);
    }

    private void createPlayerRatings(boolean isHome) {
        for (int i = 0; i<6; i++) {
            HashMap<String, Player> pRatings = new HashMap<>();
            int mins = Math.min(90, 45 + 10 * i);
            double rating = Math.min(10, 5 + 10 * i);
            for (int p = 0; p < 11; p++) {
                String pName = PLAYER_NAMES[p];
                Player player = new Player(pName, mins, rating, isHome);
                pRatings.put(pName, player);
            }
            pRatings.put("Sub", new Player("Sub", 20, 6, isHome));
            if (isHome) {
                HOMERATINGS[i] = pRatings;
            } else {
                AWAYRATINGS[i] = pRatings;
            }
        }
    }

    @Test
    public void canCalcAvgGoals() {
        //goals for
        double avgGoalsT1 = team1.getAvgGoalsFor(GamesSelector.ALL_GAMES);
        double avgGoalsT2 = team2.getAvgGoalsFor(GamesSelector.ALL_GAMES);
        double actualAvgGoalsT1 = (HOMESCORE[0] + HOMESCORE[2]+HOMESCORE[4] + AWAYSCORE[1] + AWAYSCORE[3] + AWAYSCORE[5])/6d;
        double actualAvgGoalsT2 = (HOMESCORE[1] + HOMESCORE[3]+HOMESCORE[5] + AWAYSCORE[0] + AWAYSCORE[2] + AWAYSCORE[4])/6d;
        
        Assert.assertEquals(actualAvgGoalsT1, avgGoalsT1, 0.1);
        Assert.assertEquals(actualAvgGoalsT2, avgGoalsT2, 0.1);

        //goals against
        Assert.assertEquals(actualAvgGoalsT1, team2.getAvgGoalsAgainst(GamesSelector.ALL_GAMES), 0.1);
        Assert.assertEquals(actualAvgGoalsT2, team1.getAvgGoalsAgainst(GamesSelector.ALL_GAMES), 0.1);
    }
    

    @Test
    public void canCalcAvgXg() {
        //goals for
        double avgXGFT1 = team1.getAvgXGF(GamesSelector.ALL_GAMES);
        double avgXGFT2 = team2.getAvgXGF(GamesSelector.ALL_GAMES);
        double actualAvgXgfT1 = (HOMEXG[0] + HOMEXG[2]+HOMEXG[4] + AWAYXG[1] + AWAYXG[3] + AWAYXG[5])/6d;
        double actualAvgXgfT2 = (HOMEXG[1] + HOMEXG[3]+HOMEXG[5] + AWAYXG[0] + AWAYXG[2] + AWAYXG[4])/6d;
        
        Assert.assertEquals(actualAvgXgfT1, avgXGFT1, 0.1);
        Assert.assertEquals(actualAvgXgfT2, avgXGFT2, 0.1);

        //goals against
        Assert.assertEquals(actualAvgXgfT1, team2.getAvgXGA(GamesSelector.ALL_GAMES), 0.1);
        Assert.assertEquals(actualAvgXgfT2, team1.getAvgXGA(GamesSelector.ALL_GAMES), 0.1);
    }

    @Test
    public void canCalcAvgCleanSheets() {
        double numbCleanSheetsT1 = ((HOMESCORE[1] == 0 ? 1 : 0) + (HOMESCORE[3] == 0 ? 1 : 0) + (HOMESCORE[5] == 0 ? 1 : 0) +
                                    (AWAYSCORE[0] == 0 ? 1 : 0) + (AWAYSCORE[2] == 0 ? 1 : 0) + (AWAYSCORE[4] == 0 ? 1 : 0))/6d;
        double numbCleanSheetsT2 = ((HOMESCORE[0] == 0 ? 1 : 0) + (HOMESCORE[2] == 0 ? 1 : 0) + (HOMESCORE[4] == 0 ? 1 : 0) +
                                    (AWAYSCORE[1] == 0 ? 1 : 0) + (AWAYSCORE[3] == 0 ? 1 : 0) + (AWAYSCORE[5] == 0 ? 1 : 0))/6d;
        Assert.assertEquals(numbCleanSheetsT1, team1.getAvgNumberOfCleanSheets(GamesSelector.ALL_GAMES),0.1);
        Assert.assertEquals(numbCleanSheetsT2, team2.getAvgNumberOfCleanSheets(GamesSelector.ALL_GAMES),0.1);
    }

    @Test
    public void canGetWeightedAvgXGF() {
        double[] team1XG = new double[]{HOMEXG[0],AWAYXG[1],HOMEXG[2],AWAYXG[3],HOMEXG[4],AWAYXG[5]};
        double[] team2XG = new double[]{AWAYXG[0],HOMEXG[1],AWAYXG[2],HOMEXG[3],AWAYXG[4],HOMEXG[5]};
        double actualWeightedXgT1 = calcExponWeightedAvg(team1XG,AVG_GOALS_PER_GAME);
        double actualWeightedXgT2 = calcExponWeightedAvg(team2XG,AVG_GOALS_PER_GAME);
        //xgf
        Assert.assertEquals(actualWeightedXgT1, team1.getWeightedAvgXGF(GamesSelector.ALL_GAMES), 0.1);
        Assert.assertEquals(actualWeightedXgT2, team2.getWeightedAvgXGF(GamesSelector.ALL_GAMES), 0.1);
        //xga
        Assert.assertEquals(actualWeightedXgT2, team1.getWeightedAvgXGA(GamesSelector.ALL_GAMES), 0.1);
        Assert.assertEquals(actualWeightedXgT1, team2.getWeightedAvgXGA(GamesSelector.ALL_GAMES), 0.1);
    }
    
    @Test
    public void canGetFormGoalsFor() {
        //basically looking at how the team scores vs what the opposition normally conceeds.
        //THOUGHT: should maybe be calculated using the last 5 games conceeded? RN it's calculated using totalAvgGoalsAgainst...
        //also potentially is too unstable to be using an exponential weighted average on it. maybe better to do a moving avg
        double[] team1Goals = new double[]{HOMESCORE[0],AWAYSCORE[1],HOMESCORE[2],AWAYSCORE[3],HOMESCORE[4],AWAYSCORE[5]};
        double[] team2Goals = new double[]{AWAYSCORE[0],HOMESCORE[1],AWAYSCORE[2],HOMESCORE[3],AWAYSCORE[4],HOMESCORE[5]};
        double[] team1FormGoalsFor = new double[6];
        double[] team2FormGoalsFor = new double[6];
        team1FormGoalsFor[0] = team1Goals[0]-AVG_GOALS_PER_GAME;
        team2FormGoalsFor[0] = team2Goals[0]-AVG_GOALS_PER_GAME;
        double team1AvgGF = team1Goals[0];
        double team2AvgGF = team2Goals[0];
        for (int i = 1; i<team1Goals.length; i++) {
            team1FormGoalsFor[i] = team1Goals[i]-team2AvgGF;
            team2FormGoalsFor[i] = team2Goals[i]-team1AvgGF;
            team1AvgGF = (team1AvgGF*i + team1Goals[i])/(i+1);
            team2AvgGF = (team2AvgGF*i + team2Goals[i])/(i+1);
        }
        //WILL FAIL
        //going to calc this as simply a last 5 average, then can compare with the expon weighted avg given by the TrainingTeamsSeason.
        double last5FormGoalsForT1 = calcLast5Avg(team1FormGoalsFor);
        double last5FormGoalsForT2 = calcLast5Avg(team2FormGoalsFor);
        Assert.assertEquals(last5FormGoalsForT1, team1.getFormGoalsFor(GamesSelector.ALL_GAMES), 0.1);
        Assert.assertEquals(last5FormGoalsForT2, team2.getFormGoalsFor(GamesSelector.ALL_GAMES), 0.1);
    }

    //TODO: same kind of formula as above, calc in form goals, form XG. Lots more tests for all the weighted and form weighted things

    private double calcLast5Avg(double[] arr) {
        int startIdx = arr.length - 5;
        double sum = 0;
        for (int i = startIdx; i<arr.length; i++) {
            sum+=arr[i];
        }
        return sum/5;
    }
    
    private double calcExponWeightedAvg(double[] arr, double startingVal) {
        double avg = startingVal;
        for (double d: arr) {
            avg = TrainingTeamsSeason.calcExponWeightedAvg(avg, d);
        }
        return avg;
    }

    @Test
    public void canGetAvgPoints() {
        double[] team1Goals = new double[]{HOMESCORE[0],AWAYSCORE[1],HOMESCORE[2],AWAYSCORE[3],HOMESCORE[4],AWAYSCORE[5]};
        double[] team2Goals = new double[]{AWAYSCORE[0],HOMESCORE[1],AWAYSCORE[2],HOMESCORE[3],AWAYSCORE[4],HOMESCORE[5]};
        double[] team1Points = new double[6];
        double[] team2Points = new double[6];

        for (int i = 0; i<team1Goals.length; i++) {
            if (team1Goals[i] > team2Goals[i]) {
                team1Points[i] = 3;
                team2Points[i] = 0;
            } else if (team1Goals[i] == team2Goals[i]) {
                team1Points[i] = 1;
                team2Points[i] = 1;
            } else {
                team1Points[i] = 0;
                team2Points[i] = 3;
            }
        }

        //avg points
        double team1Avg = Arrays.stream(team1Points).average().getAsDouble();
        double team2Avg = Arrays.stream(team2Points).average().getAsDouble();
        Assert.assertEquals(team1Avg, team1.getAvgPoints(GamesSelector.ALL_GAMES), 0.1);
        Assert.assertEquals(team2Avg, team2.getAvgPoints(GamesSelector.ALL_GAMES), 0.1);

        //avg points over last 5 games
        double team1AvgLast5 = calcLast5Avg(team1Points);
        double team2AvgLast5 = calcLast5Avg(team2Points);
        Assert.assertEquals(team1AvgLast5, team1.getAvgPointsOverLastXGames(GamesSelector.ALL_GAMES, 5),0.1);
        Assert.assertEquals(team2AvgLast5, team2.getAvgPointsOverLastXGames(GamesSelector.ALL_GAMES, 5),0.1);

        //avg points when team scored first & conceeded first
        ArrayList<Double> team1PointsWhenScoredFirst = new ArrayList<>();
        ArrayList<Double> team1PointsWhenConceededFirst = new ArrayList<>();
        ArrayList<Double> team2PointsWhenScoredFirst = new ArrayList<>();
        ArrayList<Double> team2PointsWhenConceededFirst = new ArrayList<>();
        for (int i = 0; i<team1Points.length; i++) {
            if ((FIRSTSCORER[i] == 1 && i%2==0) || (FIRSTSCORER[i] == 2 && i%2==1)) {
                team1PointsWhenScoredFirst.add(team1Points[i]);
                team2PointsWhenConceededFirst.add(team2Points[i]);
            } else if ((FIRSTSCORER[i] == 2 && i%2 ==0) || (FIRSTSCORER[i] == 1 && i%2==1)) {
                team1PointsWhenConceededFirst.add(team1Points[i]);
                team2PointsWhenScoredFirst.add(team2Points[i]);
            }
        }
        double team1AvgScoredFirst = team1PointsWhenScoredFirst.stream().mapToDouble(d -> (double) d).average().getAsDouble();
        double team1AvgConceededFirst = team1PointsWhenConceededFirst.stream().mapToDouble(d -> (double) d).average().getAsDouble();
        double team2AvgScoredFirst = team2PointsWhenScoredFirst.stream().mapToDouble(d -> (double) d).average().getAsDouble();
        double team2AvgConceededFirst = team2PointsWhenConceededFirst.stream().mapToDouble(d -> (double) d).average().getAsDouble();

        Assert.assertEquals(team1AvgScoredFirst, team1.getAvgPointsWhenScoredFirst(GamesSelector.ALL_GAMES), 0.1);
        Assert.assertEquals(team1AvgConceededFirst, team1.getAvgPointsWhenConceededFirst(GamesSelector.ALL_GAMES), 0.1);
        Assert.assertEquals(team2AvgScoredFirst, team2.getAvgPointsWhenScoredFirst(GamesSelector.ALL_GAMES), 0.1);
        Assert.assertEquals(team2AvgConceededFirst, team2.getAvgPointsWhenConceededFirst(GamesSelector.ALL_GAMES), 0.1);
    }

    @Test
    public void canGetPpgOfOpponents() {
        double[] team1Goals = new double[]{HOMESCORE[0],AWAYSCORE[1],HOMESCORE[2],AWAYSCORE[3],HOMESCORE[4],AWAYSCORE[5]};
        double[] team2Goals = new double[]{AWAYSCORE[0],HOMESCORE[1],AWAYSCORE[2],HOMESCORE[3],AWAYSCORE[4],HOMESCORE[5]};
        double team1Points = 0;
        double team2Points = 0;
        double[] team1Ppg = new double[6];
        double[] team2Ppg = new double[6];

        for (int i = 0; i<team1Goals.length; i++) {
            if (team1Goals[i] > team2Goals[i]) {
                team1Points += 3;
                team2Points += 0;
            } else if (team1Goals[i] == team2Goals[i]) {
                team1Points += 1;
                team2Points += 1;
            } else {
                team1Points += 0;
                team2Points += 3;
            }
            team1Ppg[i] = team1Points/(i+1);
            team2Ppg[i] = team2Points/(i+1);
        }

        //ovr
        double team1AvgOppositionPpg = Arrays.stream(team2Ppg).average().getAsDouble();
        double team2AvgOppositionPpg = Arrays.stream(team1Ppg).average().getAsDouble();
        Assert.assertEquals(team1AvgOppositionPpg, team1.getAvgPointsOfAllOpponentsGamesWholeSeason(GamesSelector.ALL_GAMES),0.1); //TODO: doesnt make sens to have gamesselector here
        Assert.assertEquals(team2AvgOppositionPpg, team2.getAvgPointsOfAllOpponentsGamesWholeSeason(GamesSelector.ALL_GAMES),0.1);

        //last 5 games
        //Do we want the form value... i.e. the ppg of the last 5 teams someone faced
        //or do we want the overall average points per game of each opponents last 5 games?
        //or maybe even the ppg of the last 5 opponents last 5 games
        //maybe all 3? it's a possibility.
//        double team1AvgOppPpgLast5 = calcLast5Avg(team2Ppg);
//        double team2AvgOppPpgLast5 = calcLast5Avg(team1Ppg);
//        Assert.assertEquals(team1AvgOppPpgLast5, team1.getAvgPointsOfAllOpponentsLast5Games(GamesSelector.ALL_GAMES, 5), 0.1);
//        Assert.assertEquals(team2AvgOppPpgLast5, team2.getAvgPointsOfAllOpponentsLast5Games(GamesSelector.ALL_GAMES, 5), 0.1);
    }

    //PLAYER STATS NOW

//    @Test
//    public void canCalcMinsWeightedLineupRating() {
//        for (int i = 0; i<HOMERATINGS.length; i++) {
//
//        }
//    }
//
//    @Test
//    public void canCalcGamesWeightedLineupRating() {
//
//    }
//
//    @Test
//    public void canCalcLineupStrength() {
//
//    }


    //first need to check that items are added to the correct arrays.
    //then need to do individual checks to make sure things are calculated properly

    //then need to deal with individual player tests

}
