package com.petermarshall.mail;

public class UptimeData {
    private final int betsPredictedLater;
    private final int betsPredictedInRealTime;
    private final String whenStartedRecordingPredictions;

    public UptimeData(int betsPredictedLater, int betsPredictedInRealTime, String whenStartedRecordingPredictions) {
        this.betsPredictedLater = betsPredictedLater;
        this.betsPredictedInRealTime = betsPredictedInRealTime;
        this.whenStartedRecordingPredictions = whenStartedRecordingPredictions;
    }

    public String getWhenStartedRecordingPredictions() {
        return whenStartedRecordingPredictions;
    }

    public int getBetsPredictedLater() {
        return betsPredictedLater;
    }

    public int getBetsPredictedInRealTime() {
        return betsPredictedInRealTime;
    }

    public double getPercUptime() {
        return (double) betsPredictedInRealTime / ( (double) betsPredictedLater + (double) betsPredictedInRealTime);
    }

    public int getTotalNumbBets() {
        return betsPredictedInRealTime + betsPredictedLater;
    }
}
