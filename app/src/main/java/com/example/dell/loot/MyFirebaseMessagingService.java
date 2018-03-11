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
        if (remoteMessage.getData().size() > 0) {
            Log.d(TAG, "Message data payload: " + remoteMessage.getData());
            String request_type=remoteMessage.getData().get("request_type");
            Intent intent=new Intent(this,Duel_Alert_Transparent_Activity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.putExtra("request_type",request_type);
            if(request_type.equals("duel_request")) {
                String from_user = remoteMessage.getData().get("user");
                String stake = remoteMessage.getData().get("stake");
                intent.putExtra("user",from_user);
                intent.putExtra("stake",stake);

            }
            else if(request_type.equals("accept_request")) {
                String from_user = remoteMessage.getData().get("user");
                String stake = remoteMessage.getData().get("stake");
                intent.putExtra("user",from_user);
                intent.putExtra("stake",stake);
            }
            else if(request_type.equals("reject_request"))
            {
                String from_user = remoteMessage.getData().get("user");
                intent.putExtra("user",from_user);

            }
            else if(request_type.equals("won_message"))
            {
                String from_user = remoteMessage.getData().get("user");
                intent.putExtra("user",from_user);

            }
            else if(request_type.equals("lost_message"))
            {
                String from_user = remoteMessage.getData().get("user");
                intent.putExtra("user",from_user);

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


    /**
     * Create and show a simple notification containing the received FCM message.
     */

    private void showDialog(String user,String stake)
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(getApplicationContext());
        LayoutInflater layoutInflater = LayoutInflater.from(getApplicationContext());
        View view=layoutInflater.inflate(R.layout.duel_alert,null);
        TextView from_user=view.findViewById(R.id.from_user);
        TextView user_stake=view.findViewById(R.id.user_stake);
        from_user.setText(user);
        user_stake.setText(stake);
        builder.setView(view)
                // Add action buttons
                .setPositiveButton("Accept", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        // sign in the user ...
                    }
                })
                .setNegativeButton("Reject", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {

                    }
                });

        AlertDialog dialog = builder.create();
        dialog.getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
        dialog.show();
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    private void sendNotification(String user, String stake) {
//        Intent intent = new Intent(this, MainActivity.class);
//        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//
//        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0 /* Request code */, intent,
//                PendingIntent.FLAG_ONE_SHOT);


//        RemoteViews notificationLayout = new RemoteViews(getPackageName(), R.layout.duel_alert);
//        RemoteViews notificationLayoutExpanded = new RemoteViews(getPackageName(), R.layout.duel_alert);
//
//// Apply the layouts to the notification
//        Notification customNotification = new NotificationCompat.Builder(this)
//                .setSmallIcon(R.drawable.avatar1)
//                .setStyle(new NotificationCompat.DecoratedCustomViewStyle())
//                .setCustomContentView(notificationLayout)
//                .setCustomBigContentView(notificationLayoutExpanded)
//                .build();

//        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
//        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this)
//                .setLargeIcon(image)/*Notification icon image*/
//                .setSmallIcon(R.drawable.firebase_icon)
//                .setContentTitle(messageBody)
//                .setStyle(new NotificationCompat.BigPictureStyle()
//                        .bigPicture(image))/*Notification with Image*/
//                .setAutoCancel(true)
//                .setSound(defaultSoundUri)
//                .setContentIntent(pendingIntent);
//
//        NotificationManager notificationManager =
//                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
//
//        notificationManager.notify(0 /* ID of notification*/, customNotification);
////        startForeground(0,customNotification);


        Intent showTaskIntent = new Intent(getApplicationContext(), Duel_Alert_Transparent_Activity.class);
        showTaskIntent.setAction(Intent.ACTION_MAIN);
        showTaskIntent.addCategory(Intent.CATEGORY_LAUNCHER);
        showTaskIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        PendingIntent contentIntent = PendingIntent.getActivity(
                getApplicationContext(),
                0,
                showTaskIntent,
                PendingIntent.FLAG_UPDATE_CURRENT);

        Notification notification = new Notification.Builder(getApplicationContext())
                .setContentTitle(getString(R.string.app_name))
                .setContentText("Hello")
                .setSmallIcon(R.drawable.ic_audiotrack_dark)
                .setWhen(System.currentTimeMillis())
                .setContentIntent(contentIntent)
                .build();
        startForeground(0, notification);
    }

//    /*
//    *To get a Bitmap image from the URL received
//    * */
//    public Bitmap getBitmapfromUrl(String imageUrl) {
//        try {
//            URL url = new URL(imageUrl);
//            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
//            connection.setDoInput(true);
//            connection.connect();
//            InputStream input = connection.getInputStream();
//            Bitmap bitmap = BitmapFactory.decodeStream(input);
//            return bitmap;
//
//        } catch (Exception e) {
//            // TODO Auto-generated catch block
//            e.printStackTrace();
//            return null;
//
//        }
//    }
}