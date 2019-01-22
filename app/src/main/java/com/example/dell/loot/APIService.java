package com.example.dell.loot;
import org.json.JSONObject;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

public interface APIService {

    @POST("/api/fcm/send/")
    Call<JSONObject> sendFCM(@Body FCMData data);
}