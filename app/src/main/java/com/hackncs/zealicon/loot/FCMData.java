package com.hackncs.zealicon.loot;

/**
 * Created by siddhartha on 14/3/18.
 */

public class FCMData {

    String registration_id;
    String message_body;
    String message_title;
    Data_Message data_message;

    public Data_Message getData_message() {
        return data_message;
    }

    public String getMessage_body() {
        return message_body;
    }

    public String getMessage_title() {
        return message_title;
    }

    public String getRegistration_id() {
        return registration_id;
    }

    public void setData_message(Data_Message data_message) {
        this.data_message = data_message;
    }

    public void setMessage_body(String message_body) {
        this.message_body = message_body;
    }

    public void setMessage_title(String message_title) {
        this.message_title = message_title;
    }

    public void setRegistration_id(String registration_id) {
        this.registration_id = registration_id;
    }
}
