package com.example.dell.loot;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class Duel_Alert_Transparent_Activity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main22);
        Intent intent=getIntent();
        String request_type=intent.getStringExtra("request_type");
        if(request_type.equals("duel_request")) {
            String from_user = intent.getStringExtra("user");
            String stake = intent.getStringExtra("stake");
            String reference_token = intent.getStringExtra("reference_token");
            showDialogRequest(from_user,stake,reference_token);

        }
        else if(request_type.equals("accept_request")) {
            String from_user = intent.getStringExtra("user");
            String stake = intent.getStringExtra("stake");
            String duel_id = intent.getStringExtra("duel_id");
            showDialogAccept(from_user,stake, duel_id);

        }
        else if(request_type.equals("reject_request"))
        {
            String from_user = intent.getStringExtra("user");
            showDialogReject(from_user);
        }
        else if(request_type.equals("won_message"))
        {
            String from_user = intent.getStringExtra("user");
            showDialogWon(from_user);
        }
        else if(request_type.equals("lost_message"))
        {
            String from_user = intent.getStringExtra("user");
            showDialogLost(from_user);
        }

    }

    private void showDialogLost(String from_user) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setTitle("You Lost")
                .setMessage(from_user+" won!")
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        finish();
                    }
                });

        AlertDialog dialog = builder.create();

        dialog.show();
    }

    private void showDialogWon(String from_user) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setTitle("You Won")
                .setMessage(from_user+" lost!")
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        finish();
                    }
                });

        AlertDialog dialog = builder.create();

        dialog.show();
    }

    private void showDialogRequest(String user, final String stake, final String reference_token)
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater layoutInflater = getLayoutInflater();
        View view=layoutInflater.inflate(R.layout.duel_alert,null);
        final RequestQueue requestQueue = Volley.newRequestQueue(this);
        TextView from_user=view.findViewById(R.id.from_user);
        final TextView user_stake=view.findViewById(R.id.user_stake);
        final EditText my_stake = view.findViewById(R.id.stake);
        SharedPreferences sharedPreferences = getSharedPreferences("LootPrefs", Context.MODE_PRIVATE);
        final String userID = sharedPreferences.getString("com.hackncs.userID", "");
        final String username = sharedPreferences.getString("com.hackncs.username", "");
        from_user.setText(user);
        user_stake.setText(stake);
        builder.setView(view)
                .setPositiveButton("Accept", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        final String finalStake;
                        final String[] duelID = new String[1];
                        final String[] fcm = new String[1];
                        if (Integer.valueOf(stake) < Integer.valueOf(my_stake.getText().toString())) {
                            finalStake = stake;
                        } else {
                            finalStake = my_stake.getText().toString();
                        }
                        final StringRequest send = new StringRequest(Request.Method.POST,
                                Endpoints.send,
                                new Response.Listener<String>() {
                                    @Override
                                    public void onResponse(String response) {
                                        Intent i = new Intent(Duel_Alert_Transparent_Activity.this, MiniGame.class);
                                        i.putExtra("duel_id", duelID);
                                        i.putExtra("player_type", "opponent");
                                        startActivity(i);
                                    }
                                },
                                new Response.ErrorListener() {
                                    @Override
                                    public void onErrorResponse(VolleyError error) {

                                    }
                                }) {
                            @Override
                            protected Map<String, String> getParams() throws AuthFailureError {
                                Map map = new HashMap();
                                map.put("registration_id", fcm);
                                map.put("message_title", "");
                                map.put("message_body", "");
                                JSONObject data = new JSONObject();
                                try {
                                    data.put("duel_id", duelID);
                                    data.put("request_type", "accept_request");
                                    data.put("stake", finalStake);
                                    data.put("user", username);
                                    map.put("data_message", data);
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                                return map;
                            }
                        };
                        final StringRequest getFCM = new StringRequest(Request.Method.GET,
                                Endpoints.syncRequest + reference_token,
                                new Response.Listener<String>() {
                                    @Override
                                    public void onResponse(String response) {
                                        try {
                                            JSONObject jsonObject = new JSONObject(response);
                                            fcm[0] = jsonObject.getString("fcm_token");
                                            requestQueue.add(send);
                                        } catch (JSONException e) {
                                            e.printStackTrace();
                                        }
                                    }
                                },
                                new Response.ErrorListener() {
                                    @Override
                                    public void onErrorResponse(VolleyError error) {

                                    }
                                });
                        StringRequest createDuel = new StringRequest(Request.Method.POST,
                                Endpoints.duel,
                                new Response.Listener<String>() {
                                    @Override
                                    public void onResponse(String response) {
                                        try {
                                            JSONObject jsonObject = new JSONObject(response);
                                            duelID[0] = jsonObject.getString("duel_id");
                                            requestQueue.add(getFCM);
                                        } catch (JSONException e) {
                                            e.printStackTrace();
                                        }
                                    }
                                },
                                new Response.ErrorListener() {
                                    @Override
                                    public void onErrorResponse(VolleyError error) {

                                    }
                                }){
                            @Override
                            protected Map<String, String> getParams() throws AuthFailureError {
                                Map map = new HashMap();
                                map.put("challenger_rt", reference_token);
                                map.put("opponent_rt", userID);
                                map.put("stake",finalStake);
                                return map;
                            }
                        };
                        requestQueue.add(createDuel);
                        Intent i = new Intent(Duel_Alert_Transparent_Activity.this, MiniGame.class);
//                        i.putExtra("duel_id", );
                        i.putExtra("player_type", "opponent");
                        startActivity(i);
                    }
                })
                .setNegativeButton("Reject", new DialogInterface.OnClickListener() {
                    final String fcm[] = new String[1];
                    public void onClick(DialogInterface dialog, int id) {
                        //TODO: "send" Duel Rejected
                        final StringRequest send = new StringRequest(Request.Method.POST,
                                Endpoints.send,
                                new Response.Listener<String>() {
                                    @Override
                                    public void onResponse(String response) {

                                    }
                                },
                                new Response.ErrorListener() {
                                    @Override
                                    public void onErrorResponse(VolleyError error) {

                                    }
                                }) {
                            @Override
                            protected Map<String, String> getParams() throws AuthFailureError {
                                Map map = new HashMap();
                                map.put("registration_id", fcm);
                                map.put("message_title", "");
                                map.put("message_body", "");
                                JSONObject data = new JSONObject();
                                try {
                                    data.put("request_type", "reject_request");
                                    data.put("user", username);
                                    map.put("data_message", data);
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                                return map;
                            }
                        };
                        final StringRequest getFCM = new StringRequest(Request.Method.GET,
                                Endpoints.syncRequest + reference_token,
                                new Response.Listener<String>() {
                                    @Override
                                    public void onResponse(String response) {
                                        try {
                                            JSONObject jsonObject = new JSONObject(response);
                                            fcm[0] = jsonObject.getString("fcm_token");
                                            requestQueue.add(send);
                                        } catch (JSONException e) {
                                            e.printStackTrace();
                                        }
                                    }
                                },
                                new Response.ErrorListener() {
                                    @Override
                                    public void onErrorResponse(VolleyError error) {

                                    }
                                });
                        requestQueue.add(getFCM);
                        finish();
                    }
                });
        AlertDialog dialog = builder.create();
        dialog.show();
    }
    private void showDialogAccept(String user, String stake, final String duel_id)
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setTitle("Accepted")
                .setMessage(user+" has accepted your challenge for stake $"+stake)
                .setPositiveButton("Fight", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        Intent i = new Intent(Duel_Alert_Transparent_Activity.this, MiniGame.class);
                        i.putExtra("duel_id", duel_id);
                        i.putExtra("player_type", "challenger");
                        startActivity(i);
                    }
                });

        AlertDialog dialog = builder.create();

        dialog.show();
    }
    private void showDialogReject(String user)
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setTitle("Rejected")
                .setMessage(user+" has rejected your challenge")
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        finish();
                    }
                });

        AlertDialog dialog = builder.create();
        dialog.show();
    }

}
