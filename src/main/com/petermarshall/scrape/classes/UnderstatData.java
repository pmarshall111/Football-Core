package com.petermarshall.scrape.classes;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

//used to pass data scraped from Understat around.
public class UnderstatData {
    private JSONArray datesData;
    private JSONObject teamsData;

    public UnderstatData(JSONArray datesData, JSONObject teamsData) {
        this.datesData = datesData;
        this.teamsData = teamsData;
    }

    public JSONArray getDatesData() {
        return datesData;
    }

    public JSONObject getTeamsData() {
        return teamsData;
    }
}
