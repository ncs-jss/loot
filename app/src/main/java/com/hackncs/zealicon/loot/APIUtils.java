package com.hackncs.zealicon.loot;

/**
 * Created by siddhartha on 14/3/18.
 */

public class APIUtils {

    public static final String BASE_URL = Endpoints.send;

    public static APIService getAPIService() {

        return RetrofitClient.getClient(BASE_URL).create(APIService.class);
    }
}
