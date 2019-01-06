package com.petermarshall.machineLearning.createData.classes;

/*
 * Used within TrainingTeam and TrainingTeamsSeason to specify whether we want a teams stats for home ganes, away games or both.
 */
public enum GamesSelector {
    ONLY_HOME_GAMES(1),
    ONLY_AWAY_GAMES(2),
    ALL_GAMES(3);

    private final int setting;

    GamesSelector(int setting) {
        this.setting = setting;
    }

    public int getSetting() {
        return setting;
    }
}
