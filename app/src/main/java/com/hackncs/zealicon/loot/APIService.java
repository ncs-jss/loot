package com.hackncs.zealicon.loot;
import org.json.JSONObject;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface APIService {


	@Headers("x-auth: eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJ1c2VybmFtZSI6ImFkbWluIiwicGFzc3dvcmQiOiJsb290MjAxOSIsImlhdCI6MTU0ODMxMzI2Mn0.VqN0AmH6URo8z_zPff68C81a8e5EUYPgOrwU18TvLMU")
    @POST("/api/fcm/send/")
    Call<JSONObject> sendFCM(@Body FCMData data);
}