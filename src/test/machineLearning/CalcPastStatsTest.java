package machineLearning;

import com.petermarshall.machineLearning.createData.CalcPastStats;
import com.petermarshall.machineLearning.createData.HistoricMatchDbData;
import com.petermarshall.machineLearning.createData.classes.GamesSelector;
import com.petermarshall.machineLearning.createData.classes.TrainingTeam;
import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

public class CalcPastStatsTest {
    @Test
    public void canSplitHistoricMatchesWithMultipleTeams() {
        String team1 = "team1", team2 = "team2", team3 = "team3";
        //t1vst2. t1 wins 3 draws 1 - avg 2.5 t1, avg 0.25 t2
        HistoricMatchDbData t1vst2In2016 = new HistoricMatchDbData(team1, team2, 4,2,16);
        HistoricMatchDbData t2vst1In2016 = new HistoricMatchDbData(team2, team1, 0,1,16);
        HistoricMatchDbData t1vst2In2017 = new HistoricMatchDbData(team1, team2, 3,2,17);
        HistoricMatchDbData t2vst1In2017 = new HistoricMatchDbData(team2, team1, 1,1,17);
        //t2vst3. t3 wins 1 draws 1 loses 2 - avg 1 t3, avg 1.75 t2
        HistoricMatchDbData t2vst3In2016 = new HistoricMatchDbData(team2, team3, 2,2,16);
        HistoricMatchDbData t3vst2In2016 = new HistoricMatchDbData(team3, team2, 3,2,16);
        HistoricMatchDbData t2vst3In2017 = new HistoricMatchDbData(team2, team3, 2,1,17);
        HistoricMatchDbData t3vst2In2017 = new HistoricMatchDbData(team3, team2, 0,2,17);
        //t1vst3. t1 wins 1, draws 3 - avg 1.5 t1, avg 0.75 t3
        HistoricMatchDbData t3vst1In2016 = new HistoricMatchDbData(team3, team1, 3,4,16);
        HistoricMatchDbData t1vst3In2016 = new HistoricMatchDbData(team1, team3, 3,3,16);
        HistoricMatchDbData t3vst1In2017 = new HistoricMatchDbData(team3, team1, 1,1,17);
        HistoricMatchDbData t1vst3In2017 = new HistoricMatchDbData(team1, team3, 3,3,17);

        ArrayList<HistoricMatchDbData> hmdbData = new ArrayList<>(Arrays.asList(t1vst2In2016, t3vst2In2016, t3vst1In2016, t2vst3In2016, t1vst3In2016, t2vst1In2016,
                t3vst1In2017, t1vst3In2017, t2vst3In2017, t2vst1In2017, t1vst2In2017, t3vst2In2017));

        HashMap<String, TrainingTeam> teams = CalcPastStats.createHistoricMatchups(hmdbData);
        TrainingTeam t1 = teams.get(team1);
        TrainingTeam t2 = teams.get(team2);
        TrainingTeam t3 = teams.get(team3);
        Assert.assertNotNull(t1);
        Assert.assertNotNull(t2);
        Assert.assertNotNull(t3);

        //t1vst2
        Assert.assertEquals(2.5, t1.getPointsOfLastMatchups(team2, GamesSelector.ALL_GAMES, 16), 0.0001);
        Assert.assertEquals(0.25, t2.getPointsOfLastMatchups(team1, GamesSelector.ALL_GAMES, 16), 0.0001);

        //t2vst3
        Assert.assertEquals(1, t3.getPointsOfLastMatchups(team2, GamesSelector.ALL_GAMES, 16), 0.0001);
        Assert.assertEquals(1.75, t2.getPointsOfLastMatchups(team3, GamesSelector.ALL_GAMES, 16), 0.0001);

        //t1vst3
        Assert.assertEquals(1.5, t1.getPointsOfLastMatchups(team3, GamesSelector.ALL_GAMES, 16), 0.0001);
        Assert.assertEquals(0.75, t3.getPointsOfLastMatchups(team1, GamesSelector.ALL_GAMES, 16), 0.0001);

        //test for limiting seasons
        Assert.assertEquals(2, t1.getPointsOfLastMatchups(team2, GamesSelector.ALL_GAMES, 17), 0.0001);
        Assert.assertEquals(0.5, t2.getPointsOfLastMatchups(team1, GamesSelector.ALL_GAMES, 17), 0.0001);
    }
}
