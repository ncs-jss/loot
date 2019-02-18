package com.example.dell.loot;

import android.app.Notification;
import android.app.PendingIntent;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

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
                intent.putExtra("stake", score);
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

        //The message which i send will have keys named [message, image, AnotherActivity] and corresponding values.
        //You can change as per the requirement.

//        //message will contain the Push Message
//        String message = remoteMessage.getData().get("message");
//
//        String notiTitle=remoteMessage.getNotification().getTitle();
//        String notiMessgae=remoteMessage.getNotification().getBody();
//        //To get a Bitmap image from the URL received


    }
}
