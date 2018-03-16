package com.example.dell.loot;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import static com.google.android.gms.internal.zzahn.runOnUiThread;

public class Splash extends Fragment {

    private FirebaseAuth mAuth;
    FirebaseDatabase database;
    DatabaseReference users,missions;
    FirebaseUser fbuser;
    User user;
    ProgressBar loader;
    ArrayList<Mission> missionsList = new ArrayList<>();
    boolean isConnected, logged_in, synced_user, synced_missions;

    public Splash() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        user = new User();
        return inflater.inflate(R.layout.fragment_splash, container, false);
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mAuth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();
        users = database.getReference("Users");
        missions = database.getReference("Current_missions");
        loader=(ProgressBar)getView().findViewById(R.id.loader);

        loader.setMax(5000);
        new CountDownTimer(5100, 1) {
            @Override
            public void onTick(long l) {
                int progress=(int)((5000-l)/50);
                loader.setProgress(5000-(int)l);
            }

            @Override
            public void onFinish() {

            }
        }.start();

        new BackgroundTasks().execute();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (isConnected) {
                    if (logged_in) {
                        syncSharedPrefs(user);
                    }
                    else {
                        changeView();
                    }
                }
                else {
                    Toast.makeText(getActivity(),"You're not connected!",Toast.LENGTH_SHORT).show();
                    getActivity().finish();
                }
            }
        }, 5000);
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    private void syncUser(String userID) {

        StringRequest syncRequest = new StringRequest(Request.Method.GET,
                Endpoints.syncRequest+userID,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        JSONObject jsonObject;
                        try {
                            jsonObject = new JSONObject(response);
                            user.setUserID(jsonObject.getString("reference_token"));
                            user.setUsername(jsonObject.getString("username"));
                            user.setZealID(jsonObject.getString("zeal_id"));
                            user.setName(jsonObject.getString("name"));
                            user.setEmail(jsonObject.getString("email"));
                            user.setAvatarID(Integer.valueOf(jsonObject.getString("avatar_id")));
                            user.setScore(Integer.valueOf(jsonObject.getString("score")));
                            user.setStage(Integer.valueOf(jsonObject.getString("stage")));
                            user.setState(jsonObject.getString("mission_state").equals("false")?0:1);
                            user.setDropCount(Integer.valueOf(jsonObject.getString("drop_count")));
                            user.setDuelWon(Integer.valueOf(jsonObject.getString("duel_won")));
                            user.setDuelLost(Integer.valueOf(jsonObject.getString("duel_lost")));
                            user.setContactNumber(Long.valueOf(jsonObject.getString("contact_number")));
//                            user.setDropped();
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
        RequestQueue requestQueue = Volley.newRequestQueue(getActivity());
        requestQueue.add(syncRequest);
    }

    public void syncSharedPrefs(User user) {
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("LootPrefs", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("com.hackncs.userID", user.getUserID());
        editor.putString("com.hackncs.username", user.getUsername());
        editor.putString("com.hackncs.zealID", user.getZealID());
        editor.putString("com.hackncs.name", user.getName());
        editor.putString("com.hackncs.email", user.getEmail());
        editor.putInt("com.hackncs.avatarID", user.getAvatarID());
        editor.putInt("com.hackncs.score", user.getScore());
        editor.putInt("com.hackncs.stage", user.getStage());
        editor.putInt("com.hackncs.state", user.getState());
        editor.putInt("com.hackncs.dropCount", user.getDropCount());
        editor.putInt("com.hackncs.duelWon", user.getDuelWon());
        editor.putInt("com.hackncs.duelLost", user.getDuelLost());
        editor.putLong("com.hackncs.contactNumber", user.getContactNumber());
//        editor.putStringSet("com.hackncs.dropped", new HashSet<>(user.getDropped()));
        editor.apply();
        Intent i = new Intent(getContext(), DashboardLoot.class);
        i.putExtra("UID", fbuser.getUid());
        startActivity(i);
    }

    public boolean isOnline() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        assert connectivityManager != null;
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();

        if (networkInfo != null && networkInfo.isConnected()) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(getActivity(), "Connected!", Toast.LENGTH_SHORT).show();
                }
            });
            return true;
        }
        else {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(getActivity(), "Disconnected!", Toast.LENGTH_SHORT).show();
                }
            });
            return false;
        }
    }

    private  void backgroundTasks() {
        isConnected = isOnline();
        logged_in = mAuth.getCurrentUser() != null;
        fbuser = mAuth.getCurrentUser();
        if(logged_in) {
            syncUser(fbuser.getUid());
        }
    }

    public class BackgroundTasks extends AsyncTask<String, Integer, String> {

        @Override
        protected String doInBackground(String... params) {
            backgroundTasks();
            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
        }
    }

    private void changeView() {
        ProgressBar loader = getView().findViewById(R.id.loader);
        RelativeLayout layout = getView().findViewById(R.id.rel_layout);
        loader.setVisibility(View.GONE);
        layout.setVisibility(View.VISIBLE);
        Button login_button =  getView().findViewById(R.id.login_button);
        Button register_button =  getView().findViewById(R.id.register_button);
        login_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                Login fragment = new Login();
                fragmentTransaction.replace(R.id.login_frame, fragment,"login");
                fragmentTransaction.commit();
            }
        });
        register_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                Register fragment = new Register();
                fragmentTransaction.replace(R.id.login_frame, fragment,"register");
                fragmentTransaction.commit();
            }
        });
    }
}