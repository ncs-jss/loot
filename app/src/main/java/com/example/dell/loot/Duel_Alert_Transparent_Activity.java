package com.example.dell.loot;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

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

import retrofit2.Call;
import retrofit2.Callback;

public class Duel_Alert_Transparent_Activity extends AppCompatActivity {

        APIService fcmService;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main22);
        Intent intent=getIntent();
        fcmService=APIUtils.getAPIService();
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
//            String stake=intent.getStringExtra("stake");
            showDialogWon(from_user);

        }
        else if(request_type.equals("lost_message"))
        {
            String from_user = intent.getStringExtra("user");
//            String stake=intent.getStringExtra("stake");
            showDialogLost(from_user);
        }
        else if(request_type.equals("tie_message"))
        {
            String from_user = intent.getStringExtra("user");
//            String stake=intent.getStringExtra("stake");
            showDialogTie(from_user);
        }

    }

    private void showDialogTie(String from_user) {
        LayoutInflater layoutInflater = getLayoutInflater();
        View view=layoutInflater.inflate(R.layout.duel_alert,null);

        TextView title=view.findViewById(R.id.title);
        final TextView message=view.findViewById(R.id.message);
        Button ok=view.findViewById(R.id.positive);
        title.setText("Result");
        message.setText("It was a tie!");
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setView(view);

        final AlertDialog dialog = builder.create();

        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
                finish();
            }
        });

        dialog.show();
    }

    private void showDialogLost(String from_user) {

        LayoutInflater layoutInflater = getLayoutInflater();
        View view=layoutInflater.inflate(R.layout.duel_alert,null);

        TextView title=view.findViewById(R.id.title);
        final TextView message=view.findViewById(R.id.message);
        Button ok=view.findViewById(R.id.positive);
        title.setText("Result");
        message.setText("You Lost!\n"+from_user+" Won!");
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setView(view);
        SharedPreferences sharedPreferences =getSharedPreferences("LootPrefs", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        int wonCount=sharedPreferences.getInt("com.hackncs.duelLost",0)+1;
        editor.putInt("com.hackncs.duelLost", wonCount);
        editor.apply();


        final AlertDialog dialog = builder.create();

        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
                finish();
            }
        });

        dialog.show();
    }

    private void showDialogWon(String from_user) {
        LayoutInflater layoutInflater = getLayoutInflater();
        View view=layoutInflater.inflate(R.layout.duel_alert,null);

        TextView title=view.findViewById(R.id.title);
        final TextView message=view.findViewById(R.id.message);
        Button ok=view.findViewById(R.id.positive);
        title.setText("Result");
        message.setText("You Won!\n"+from_user+" Lost!");
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setView(view);

        final AlertDialog dialog = builder.create();

        SharedPreferences sharedPreferences =getSharedPreferences("LootPrefs", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        int wonCount=sharedPreferences.getInt("com.hackncs.duelWon",0)+1;
        editor.putInt("com.hackncs.duelWon", wonCount);
        editor.apply();

        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
                finish();
            }
        });

        dialog.show();
    }

    private void showDialogRequest(String user, final String stake, final String reference_token)
    {
        final RequestQueue requestQueue = Volley.newRequestQueue(this);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater layoutInflater = getLayoutInflater();
        View view=layoutInflater.inflate(R.layout.challenge_dialog,null);

        TextView message=view.findViewById(R.id.message);
        final EditText my_stake=view.findViewById(R.id.stake);
        String chlng_msg=user+" has challenged you for a duel of stake "+ stake+"\nDo you want to accept it?";
        message.setText(chlng_msg);
        Button accept=view.findViewById(R.id.positive);
        Button reject=view.findViewById(R.id.negative);
        accept.setText("Accept");
        reject.setText("Reject");
        SharedPreferences sharedPreferences = getSharedPreferences("LootPrefs", Context.MODE_PRIVATE);
        final String userID = sharedPreferences.getString("com.hackncs.userID", "");
        final String username = sharedPreferences.getString("com.hackncs.username", "");
        builder.setView(view);
        final AlertDialog dialog = builder.create();
        accept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                final String finalStake;
                final String[] duelID = new String[1];
                final String[] fcm = new String[1];
                int stakeEntered = 0;
                dialog.dismiss();
                try {

                    stakeEntered = Integer.valueOf(my_stake.getText().toString());
                    if (stakeEntered <= 0) {
                        Toast.makeText(Duel_Alert_Transparent_Activity.this, "You entered an invalid value!", Toast.LENGTH_SHORT).show();

                    } else {
                        if (Integer.valueOf(stake) < Integer.valueOf(my_stake.getText().toString())) {
                            finalStake = stake;
                        } else {
                            finalStake = my_stake.getText().toString();
                        }
                        final StringRequest getFCM = new StringRequest(Request.Method.GET,
                                Endpoints.syncRequest + reference_token,
                                new Response.Listener<String>() {
                                    @Override
                                    public void onResponse(String response) {
                                        try {
                                            JSONObject jsonObject = new JSONObject(response);
                                            fcm[0] = jsonObject.getString("fcm_token");
                                            Log.i("senders fcm", fcm[0]);
                                            Data_Message message = new Data_Message();
                                            message.setId(duelID[0]);
                                            message.setRequest_type("accept_request");
                                            message.setUser(username);
                                            message.setStake(finalStake);
                                            FCMData fcmData = new FCMData();
                                            fcmData.setData_message(message);
                                            fcmData.setMessage_body("");
                                            fcmData.setMessage_title("");
                                            fcmData.setRegistration_id(fcm[0]);

                                            sendFCM(fcmData);

                                        } catch (JSONException e) {
                                            e.printStackTrace();
                                        }
                                    }
                                },
                                new Response.ErrorListener() {
                                    @Override
                                    public void onErrorResponse(VolleyError error) {

                                        Log.i("Error", error.getMessage());
                                    }
                                });


                        StringRequest createDuel = new StringRequest(Request.Method.POST,
                                Endpoints.duel,
                                new Response.Listener<String>() {
                                    @Override
                                    public void onResponse(String response) {
                                        try {
                                            JSONObject jsonObject = new JSONObject(response);
                                            duelID[0] = jsonObject.getString("id");
                                            String duelID1 = jsonObject.getString("id");
                                            Log.i("duelId", duelID1);
                                            requestQueue.add(getFCM);
                                        } catch (JSONException e) {
                                            e.printStackTrace();
                                        }
                                    }
                                },
                                new Response.ErrorListener() {
                                    @Override
                                    public void onErrorResponse(VolleyError error) {

                                        Log.i("Error", error.getMessage());
                                    }
                                }) {
                            @Override
                            protected Map<String, String> getParams() throws AuthFailureError {
                                HashMap<String, String> map = new HashMap<>();
                                map.put("challenger_rt", reference_token);
                                map.put("opponent_rt", userID);
                                map.put("stake", String.valueOf(finalStake));
                                Log.i("stake", finalStake);
                                return map;
                            }
                        };
                        requestQueue.add(createDuel);
                    }
//
                } catch (Exception e) {
                    Log.i("Exception", e.getMessage());
                }
            }});


        reject.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
                final String fcm[] = new String[1];
                final StringRequest getFCM = new StringRequest(Request.Method.GET,
                        Endpoints.syncRequest + reference_token,
                        new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {
                                try {
                                    JSONObject jsonObject = new JSONObject(response);
                                    fcm[0] = jsonObject.getString("fcm_token");
                                    Data_Message message=new Data_Message();
                                    message.setRequest_type("reject_request");
                                    message.setUser(username);
                                    FCMData fcmData=new FCMData();
                                    fcmData.setData_message(message);
                                    fcmData.setMessage_body("");
                                    fcmData.setMessage_title("");
                                    fcmData.setRegistration_id(fcm[0]);

                                    sendFCM(fcmData);
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


            }
        });



        dialog.show();

    }
    private void showDialogAccept(String user, String stake, final String duel_id)
    {
        LayoutInflater layoutInflater = getLayoutInflater();
        View view=layoutInflater.inflate(R.layout.duel_alert,null);

        TextView title=view.findViewById(R.id.title);
        final TextView message=view.findViewById(R.id.message);
        Button ok=view.findViewById(R.id.positive);
        title.setText("Accepted");
        message.setText(user+" has accepted your duel for "+stake+" coins");
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setView(view);

        final AlertDialog dialog = builder.create();

        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
                Intent i = new Intent(Duel_Alert_Transparent_Activity.this, MiniGame.class);
                i.putExtra("duel_id",duel_id );
                i.putExtra("player_type", "challenger");
                startActivity(i);
            }
        });

        dialog.show();
    }
    private void showDialogReject(String user)
    {
        LayoutInflater layoutInflater = getLayoutInflater();
        View view=layoutInflater.inflate(R.layout.duel_alert,null);

        TextView title=view.findViewById(R.id.title);
        final TextView message=view.findViewById(R.id.message);
        Button ok=view.findViewById(R.id.positive);
        title.setText("Rejected");
        message.setText(user+" has rejected your duel");
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setView(view);

        final AlertDialog dialog = builder.create();

        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
                finish();
            }
        });

        dialog.show();
    }


    public void sendFCM(final FCMData fcmData) {
        fcmService.sendFCM(fcmData).enqueue(new Callback<JSONObject>() {

            @Override
            public void onResponse(Call<JSONObject> call, retrofit2.Response<JSONObject> response) {

                Log.i("Response",call.toString() );

                if(response.isSuccessful())
                {
                    JSONObject object=response.body();

//                       Toast.makeText(Duel_Alert_Transparent_Activity.this,"Success",Toast.LENGTH_SHORT).show();
                        if(fcmData.getData_message().getRequest_type().equals("accept_request")) {
                            Intent i = new Intent(Duel_Alert_Transparent_Activity.this, MiniGame.class);
                            i.putExtra("duel_id",fcmData.getData_message().getId() );
                            i.putExtra("player_type", "opponent");
                            startActivity(i);
                        }
                        else
                        {
                            finish();
                        }

                }
                else
                {
                    Toast.makeText(Duel_Alert_Transparent_Activity.this,"Response Failure "+response.message(),Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<JSONObject> call, Throwable t) {
                Log.e("Error", "Unable to submit post to API."+ t.getMessage());
            }
        });
    }

    @Override
    protected void onPause() {
        super.onPause();
        finish();
    }
}
