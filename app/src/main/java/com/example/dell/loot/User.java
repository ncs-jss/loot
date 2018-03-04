package com.example.dell.loot;

import java.util.ArrayList;

public class User {

    int avatarId, score;
    long contact;
    boolean online;
    String userId, username, zealID, name, email, password, active;
    ArrayList<String> found, completed, dropped;

    public User() {

    }

    public User(String userId, String username, String name, String email, String password,
                String zealID, long contact, int score, int avatarId, String active,
                ArrayList<String> found, ArrayList<String> completed, ArrayList<String> dropped,
                boolean online) {
        this.active=active;
        this.avatarId = avatarId;
        this.completed=completed;
        this.contact=contact;
        this.dropped=dropped;
        this.email=email;
        this.name=name;
        this.username=username;
        this.password=password;
        this.score=score;
        this.found=found;
        this.userId=userId;
        this.zealID=zealID;
        this.online=online;
     }

    public void setOnline(boolean online) {
        this.online = online;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setActive(String active) {
        this.active = active;
    }

    public void setZealID(String zealID) {
        this.zealID = zealID;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setAvatarId(int avatarId) {
        this.avatarId = avatarId;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public void setDropped(ArrayList<String> dropped) {
        this.dropped = dropped;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setCompleted(ArrayList<String> completed) {
        this.completed = completed;
    }

    public void setContact(long contact) {
        this.contact = contact;
    }

    public void setFound(ArrayList<String> found) {
        this.found = found;
    }

    public String getEmail() {
        return email;
    }

    public int getAvatarId() {
        return avatarId;
    }

    public String getName() {
        return name;
    }

    public ArrayList<String> getCompleted() {
        return completed;
    }

    public int getScore() {
        return score;
    }

    public ArrayList<String> getFound() {
        return found;
    }

    public long getContact() {
        return contact;
    }

    public String getUserId() {
        return userId;
    }

    public ArrayList<String> getDropped() {
        return dropped;
    }

    public String getActive() {
        return active;
    }

    public String getPassword() {
        return password;
    }

    public String getUsername() {
        return username;
    }

    public String getZealID() {
        return zealID;
    }

    public boolean isOnline() {
        return online;
    }

}
