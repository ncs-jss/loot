package com.hackncs.zealicon.loot;

/**
 * Created by siddhartha on 14/3/18.
 */

public class Data_Message {

    String request_type,reference_token,user,stake;
    String id;

    public String getReference_token() {
        return reference_token;
    }

    public String getRequest_type() {
        return request_type;
    }

    public String getId() {
        return id;
    }

    public String getStake() {
        return stake;
    }

    public String getUser() {
        return user;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setReference_token(String reference_token) {
        this.reference_token = reference_token;
    }

    public void setRequest_type(String request_type) {
        this.request_type = request_type;
    }

    public void setStake(String stake) {
        this.stake = stake;
    }

    public void setUser(String user) {
        this.user = user;
    }
}
