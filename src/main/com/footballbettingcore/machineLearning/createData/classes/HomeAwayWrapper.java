package com.footballbettingcore.machineLearning.createData.classes;

/*
 * Used within TrainingTeamSeason so we can quickly filter between xGF or xGA that were home or away.
 */
public class HomeAwayWrapper {
    private boolean home;
    private double numb;

    public HomeAwayWrapper(boolean home, double numb) {
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
