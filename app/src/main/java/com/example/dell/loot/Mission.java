package com.example.dell.loot;

/**
 * Created by DELL on 2/12/2018.
 */

public class Mission {

    String missionId;
    String mName;
    String story;
    String description;
    double lat;
    double lng;
    String answer;

    public Mission()
    {

    }

    public Mission(String missionId,String mName,String story,String description,String answer,double lat,double lng)
    {
        this.missionId=missionId;
        this.mName=mName;
        this.story=story;
        this.description=description;
        this.lat=lat;
        this.lng=lng;
        this.answer=answer;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setStory(String story) {
        this.story = story;
    }

    public void setAnswer(String answer) {
        this.answer = answer;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public void setLng(double lng) {
        this.lng = lng;
    }

    public void setMissionId(String missionId) {
        this.missionId = missionId;
    }

    public void setmName(String mName) {
        this.mName = mName;
    }

    public String getDescription() {
        return description;
    }

    public double getLat() {
        return lat;
    }

    public double getLng() {
        return lng;
    }

    public String getAnswer() {
        return answer;
    }

    public String getMissionId() {
        return missionId;
    }

    public String getmName() {
        return mName;
    }

    public String getStory() {
        return story;
    }

}
