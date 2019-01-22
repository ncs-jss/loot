package com.example.dell.loot;

import java.util.ArrayList;

public class User {

    int avatarID, score, stage, state, dropCount, duelWon, duelLost;
    long contactNumber;
    String userID, username, admissionNo, name, email;
    ArrayList<String> dropped;

    public User() {
    }

    public User(int avatarID, int score, int stage, int state, int dropCount, int duelWon,
                int duelLost, long contactNumber, String userID, String username, String admissionNo,
                String name, String email, ArrayList<String> dropped) {
        this.avatarID = avatarID;
        this.score = score;
        this.stage = stage;
        this.state = state;
        this.dropCount = dropCount;
        this.duelWon = duelWon;
        this.duelLost = duelLost;
        this.contactNumber = contactNumber;
        this.userID = userID;
        this.username = username;
        this.admissionNo = admissionNo;
        this.name = name;
        this.email = email;
        this.dropped = dropped;
    }

    public int getAvatarID() {
        return avatarID;
    }

    public void setAvatarID(int avatarID) {
        this.avatarID = avatarID;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public int getStage() {
        return stage;
    }

    public void setStage(int stage) {
        this.stage = stage;
    }

    public int getState() {
        return state;
    }

    public void setState(int state) {
        this.state = state;
    }

    public int getDropCount() {
        return dropCount;
    }

    public void setDropCount(int dropCount) {
        this.dropCount = dropCount;
    }

    public int getDuelWon() {
        return duelWon;
    }

    public void setDuelWon(int duelWon) {
        this.duelWon = duelWon;
    }

    public int getDuelLost() {
        return duelLost;
    }

    public void setDuelLost(int duelLost) {
        this.duelLost = duelLost;
    }

    public long getContactNumber() {
        return contactNumber;
    }

    public void setContactNumber(long contactNumber) {
        this.contactNumber = contactNumber;
    }

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getAdmissionNo() {
        return admissionNo;
    }

    public void setAdmissionNo(String admissionNo) {
        this.admissionNo = admissionNo;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public ArrayList<String> getDropped() {
        return dropped;
    }

    public void setDropped(ArrayList<String> dropped) {
        this.dropped = dropped;
    }

}