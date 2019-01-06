package com.petermarshall.machineLearning.createData.classes;

/*
 * Used within TrainingTeamSeason so we can quickly filter between scores or points that were home or away.
 */
public class HomeAwayInt {
    private boolean home;
    private int numb;

    public HomeAwayInt(boolean home, int numb) {
        this.home = home;
        this.numb = numb;
    }

    public boolean isHome() {
        return home;
    }

    public int getNumb() {
        return numb;
    }
}
