package com.hackncs.zealicon.loot;


import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.util.HashMap;
import java.util.Map;

public class MyFirebaseMessagingService extends FirebaseMessagingService {

    private static final String TAG = "FirebaseMessageService";

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        // There are two types of messages data messages and notification messages. Data messages are handled
        // here in onMessageReceived whether the app is in the foreground or background. Data messages are the type
        // traditionally used with GCM. Notification messages are only received here in onMessageReceived when the app
        // is in the foreground. When the app is in the background an automatically generated notification is displayed.
        // When the user taps on the notification they are returned to the app. Messages containing both notification
        // and data payloads are treated as notification messages. The Firebase console always sends notification
        // messages. For more see: https://firebase.google.com/docs/cloud-messaging/concept-options
        //
        Log.d(TAG, "From: " + remoteMessage.getFrom());

        // Check if message contains a data payload.
        if (remoteMessage.getData() != null) {
            Log.d(TAG, "Message data payload: " + remoteMessage.getData());
            String request_type = remoteMessage.getData().get("request_type");
            Intent intent = new Intent(this, Duel_Alert_Transparent_Activity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.putExtra("request_type", request_type);
            if (request_type.equals("duel_request")) {
                String from_user = remoteMessage.getData().get("user");
                String stake = remoteMessage.getData().get("stake");
                String reference_token = remoteMessage.getData().get("reference_token");
                intent.putExtra("user", from_user);
                intent.putExtra("stake", stake);
                intent.putExtra("reference_token", reference_token);
            } else if (request_type.equals("accept_request")) {
                String from_user = remoteMessage.getData().get("user");
                String stake = remoteMessage.getData().get("stake");
                String duel_id = remoteMessage.getData().get("id");
                intent.putExtra("duel_id", duel_id);
                intent.putExtra("user", from_user);
                intent.putExtra("stake", stake);
            } else if (request_type.equals("reject_request")) {
                String from_user = remoteMessage.getData().get("user");
                intent.putExtra("user", from_user);

            } else if (request_type.equals("won_message")) {
                String from_user = remoteMessage.getData().get("user");
                String score = remoteMessage.getData().get("score");
                intent.putExtra("user", from_user);
                intent.putExtra("score", score);
            } else if (request_type.equals("lost_message")) {
                String from_user = remoteMessage.getData().get("user");
                String score = remoteMessage.getData().get("score");
                intent.putExtra("user", from_user);
                intent.putExtra("score", score);
            } else if (request_type.equals("tie_message")) {
                String from_user = remoteMessage.getData().get("user");
//                String stake = remoteMessage.getData().get("stake");
                intent.putExtra("user", from_user);
//                intent.putExtra("stake", stake);
            }
//            sendNotification(from_user,stake);

            startActivity(intent);
        }

        // Check if message contains a notification payload.
        if (remoteMessage.getNotification() != null) {
            Log.d(TAG, "Message Notification Body: " + remoteMessage.getNotification().getBody());
        }

    }
    @Override
    public void onNewToken(String token) {
        Log.d(TAG, "Refreshed token: " + token);

        // If you want to send messages to this application instance or
        // manage this apps subscriptions on the server side, send the
        // Instance ID token to your app server.
        sendRegistrationToServer(token);
    }
    private void sendRegistrationToServer(final String token) {
        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        if (firebaseUser != null) {
            String userID = firebaseUser.getUid();
            StringRequest updateFCMToken = new StringRequest(Request.Method.POST,
                    Endpoints.updateUser + userID + "/edit/",
                    null,
                    null) {
                @Override
                protected Map<String, String> getParams() throws AuthFailureError {
                    Map map = new HashMap();
                    map.put("fcm_token", token);
                    return map;
                }
                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    Map<String, String> params = new HashMap<>();
                    params.put("x-auth",Endpoints.apikey);
                    return params;
                }
            };
            RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
            requestQueue.add(updateFCMToken);
        }
        SharedPreferences sharedPreferences = getApplication().getSharedPreferences("LootPrefs", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        Log.i("FCMTOKEN",token);
        editor.putString("com.hackncs.FCMToken", token);
        editor.commit();
    }
}
