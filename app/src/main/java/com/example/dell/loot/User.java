package com.example.dell.loot;

import java.util.ArrayList;

/**
 * Created by DELL on 2/9/2018.
 */

public class User {

    int avtarId;
    String userId;
    String username;
    String zealID;
    int score;
    String name;
    long contact;
    String email;
    String password;
    ArrayList<String> found;
    String active;
    ArrayList<String> completed;
    ArrayList<String> dropped;
    public User()
    {

    }
     public User(String userId, String username, String name,String email,String password,String zealID,long contact,int score,int avtarId,String active,ArrayList<String> found, ArrayList<String> completed, ArrayList<String> dropped)
     {

         this.active=active;
         this.avtarId=avtarId;
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

    public void setAvtarId(int avtarId) {
        this.avtarId = avtarId;
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

    public int getAvtarId() {
        return avtarId;
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

}