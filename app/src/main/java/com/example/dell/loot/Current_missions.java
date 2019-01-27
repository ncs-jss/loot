package com.example.dell.loot;


import android.*;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.os.Vibrator;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;


/**
 * A simple {@link Fragment} subclass.
 */
public class Current_missions extends Fragment {


    Button submit, drop;
    TextView story;
    EditText answer;
    AlertDialog alertDialog;
    Mission mission;
    int userStage, state, dropCount, score;
    String userID;
    DatabaseReference users, missions;
    LootApplication app;
    RequestQueue requestQueue;
    SharedPreferences sharedPreferences;

    public Current_missions() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_current_mission, container, false);
        story = view.findViewById(R.id.story);
        answer = view.findViewById(R.id.answer);
        submit = view.findViewById(R.id.submit);
        drop = view.findViewById(R.id.drop_mission);

        Bundle bundle =this.getArguments();
        mission=new Mission();
        mission.setMissionID(bundle.getInt("misionID"));
        mission.setStory(bundle.getString("story"));
        mission.setDescription(bundle.getString("description"));
        mission.setMissionName(bundle.getString("missionName"));
        mission.setAnswer(bundle.getString("answer"));

        Log.i("Mission Id",mission.getMissionName());

        sharedPreferences = getActivity().getSharedPreferences("LootPrefs", Context.MODE_PRIVATE);
        userStage = sharedPreferences.getInt("com.hackncs.stage",1);
        state = sharedPreferences.getInt("com.hackncs.state", 0);
        dropCount = sharedPreferences.getInt("com.hackncs.dropCount", 0);
        score = sharedPreferences.getInt("com.hackncs.score", 0);
        userID = sharedPreferences.getString("com.hackncs.userID", null);
        requestQueue = Volley.newRequestQueue(getContext());

        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {

        super.onActivityCreated(savedInstanceState);
        displayMission();
        drop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                builder.setTitle("Drop Mission");
                if (score - (((int) Math.pow(2, dropCount)) * 10) >= 0) {
                    builder.setMessage((((int) Math.pow(2, dropCount)) * 10) + " coins will be deducted!");
                    builder.setPositiveButton("Drop", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            userStage += 1;
                            state = 0;
                            score -= ((int) Math.pow(2, dropCount)) * 10;
                            dropCount += 1;
                            StringRequest updateUser = new StringRequest(Request.Method.POST,
                                    Endpoints.updateUser + userID + "/edit/",
                                    new Response.Listener<String>() {
                                        @Override
                                        public void onResponse(String response) {
                                            Log.d("dropVolley", "response");
                                            SharedPreferences.Editor editor = sharedPreferences.edit();
                                            editor.putInt("com.hackncs.score", score);
                                            editor.putInt("com.hackncs.stage", userStage);
                                            editor.putInt("com.hackncs.state", state);
                                            editor.putInt("com.hackncs.dropCount", dropCount);
                                            editor.apply();

                                            updateActionBarDetails();
                                            //TODO:load fragment
                                            loadFragment(new Missions(),"missions");
                                        }
                                    },
                                    new Response.ErrorListener() {
                                        @Override
                                        public void onErrorResponse(VolleyError error) {
                                            Log.d("dropVolley", "error");
                                        }
                                    }) {
                                @Override
                                protected Map<String, String> getParams() throws AuthFailureError {
                                    Map map = new HashMap();
                                    map.put("score", score+"");
                                    map.put("stage", userStage+"");
                                    map.put("mission_state", "false");
                                    map.put("drop_count", String.valueOf(dropCount));
                                    return map;
                                    //TODO:Confirm
                                }
                                @Override
                                public Map<String, String> getHeaders() throws AuthFailureError {
                                    Map<String, String> params = new HashMap<>();
                                    params.put("x-auth",Endpoints.apikey);
                                    return params;
                                }

                            };
                            requestQueue.add(updateUser);

                        }
                    });
                    builder.setNegativeButton("Stay", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            alertDialog.dismiss();
                        }
                    });
                } else {
                    builder.setMessage("You can't drop this mission! Not enough coins!");
                    builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            alertDialog.dismiss();
                        }
                    });
                    builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            alertDialog.dismiss();
                        }
                    });
                }
                alertDialog = builder.create();
                alertDialog.show();
            }
        });

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Log.i("Clicked","submit");
                Log.i("Ans",answer.getText()+"");
                Log.i("AnsRight",mission.getAnswer());
                if (!answer.getText().toString().trim().equalsIgnoreCase(mission.getAnswer())) {
                    answer.setText("");
                    Toast.makeText(getContext(), "You might wanna try another one!", Toast.LENGTH_SHORT).show();
                } else {
                    answer.setText("");
                    Toast.makeText(getContext(), "Bravo you got it right!", Toast.LENGTH_SHORT).show();
                    userStage += 1;
                    state = 0;
                    score += 100;
                    StringRequest updateUser = new StringRequest(Request.Method.POST,
                            Endpoints.updateUser + userID +"/edit/",
                            new Response.Listener<String>() {
                                @Override
                                public void onResponse(String response) {
                                    Log.d("submitVolley", "response");
                                    SharedPreferences.Editor editor = sharedPreferences.edit();
                                    editor.putInt("com.hackncs.score", score);
                                    editor.putInt("com.hackncs.stage", userStage);
                                    editor.putInt("com.hackncs.state", state);
                                    editor.apply();
                                    //TODO:load fragment
                                    updateActionBarDetails();
                                    loadFragment(new Missions(),"missions");
                                }
                            },
                            new Response.ErrorListener() {
                                @Override
                                public void onErrorResponse(VolleyError error) {
                                    Log.d("submitVolley", error.getMessage());
                                }
                            }) {
                        @Override
                        protected Map<String, String> getParams() throws AuthFailureError {
                            Map map = new HashMap();
                            map.put("score", String.valueOf(score));
                            map.put("stage", String.valueOf(userStage));
                            map.put("mission_state", "false");
                            return map;
                        }
                        @Override
                        public Map<String, String> getHeaders() throws AuthFailureError {
                            Map<String, String> params = new HashMap<>();
                            params.put("x-auth",Endpoints.apikey);
                            return params;
                        }
                    };
                    requestQueue.add(updateUser);
                }
            }
        });



    }

    private void displayMission() {

        story.setText(mission.getDescription());
        answer.setEnabled(true);
        submit.setEnabled(true);
        drop.setEnabled(true);


    }
    @Override
    public void onDestroy() {
        super.onDestroy();

    }

    @Override
    public void onStop() {

        super.onStop();
    }
    private void loadFragment(Fragment fragment,String tag) {
        FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.frame_container, fragment,tag);
        transaction.addToBackStack(null);
        transaction.commit();
    }

    private void updateActionBarDetails()
    {
        TextView user_coins=getActivity().findViewById(R.id.user_coins);
        user_coins.setText(score+"");

    }

}

