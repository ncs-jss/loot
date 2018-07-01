package com.example.dell.loot;

public class Mission {

    int missionID;
    String missionName, story, description, answer;
    double lat, lng;

    public Mission() {
    }

    public Mission(int missionID, String missionName, String story, String description, String answer,
                   double lat, double lng) {
        this.missionID = missionID;
        this.missionName = missionName;
        this.story = story;
        this.description = description;
        this.answer = answer;
        this.lat = lat;
        this.lng = lng;
    }

    public int getMissionID() {
        return missionID;
    }

    public void setMissionID(int missionID) {
        this.missionID = missionID;
    }

    public String getMissionName() {
        return missionName;
    }

    public void setMissionName(String missionName) {
        this.missionName = missionName;
    }

    public String getStory() {
        return story;
    }

    public void setStory(String story) {
        this.story = story;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getAnswer() {
        return answer;
    }

    public void setAnswer(String answer) {
        this.answer = answer;
    }

    public double getLat() {
        return lat;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public double getLng() {
        return lng;
    }

    public void setLng(double lng) {
        this.lng = lng;
    }
}