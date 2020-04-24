package com.petermarshall.machineLearning.createData.classes;

/*
 * Used within TrainingTeamSeason so we can quickly filter between xGF or xGA that were home or away.
 */
public class HomeAwayDouble {
    private boolean home;
    private double numb;

    public HomeAwayDouble(boolean home, double numb) {
        this.home = home;
        this.numb = numb;
    }

    public boolean isHome() {
        return home;
    }

    public double getNumb() {
        return numb;
    }
}
