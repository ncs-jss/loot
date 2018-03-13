package com.example.dell.loot;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
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
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

public class Login extends Fragment {

    private FirebaseAuth mAuth;
    //    private FirebaseDatabase database;
//    DatabaseReference users, missions;
//    ArrayList<Mission> missionsList = new ArrayList<>();
    User user;
    ProgressDialog dialog;
    EditText email, password;
//    TextView register;
    Button login;

    public Login() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_login, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initializeViews();
        mAuth = FirebaseAuth.getInstance();
//        database = FirebaseDatabase.getInstance();
//        users = database.getReference("Users");
//        missions = database.getReference("Missions");
        user = new User();
        dialog = new ProgressDialog(getContext());
        dialog.setTitle("Please Wait");
        dialog.setCancelable(false);
        dialog.setMessage("Signing in...");
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.show();
                mAuth.signInWithEmailAndPassword(email.getText().toString(), password.getText().toString())
                        .addOnCompleteListener(getActivity(), new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {
                                    Toast.makeText(getContext(),"Login Successful",Toast.LENGTH_SHORT).show();
                                    FirebaseUser firebaseUser = mAuth.getCurrentUser();
                                    syncUser(firebaseUser.getUid());
                                }
                                else {
                                    dialog.dismiss();
                                    Toast.makeText(getContext(), "Authentication failed."+ task.getException().getMessage(),
                                            Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
            }
        });
//        register.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                register();
//            }
//        });

    }

    private void initializeViews() {
        email = getView().findViewById(R.id.email);
        password = getView().findViewById(R.id.password);
        login = getView().findViewById(R.id.login);
//        register = getView().findViewById(R.id.goto_register);
    }

//    public void register() {
//        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
//        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
//        Register fragment = new Register();
//        fragmentTransaction.replace(R.id.login_frame, fragment);
//        fragmentTransaction.commit();
//    }

    public void syncSharedPrefs(User user) {
        dialog.setMessage("Completing...");
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

//        LootApplication app = (LootApplication)getActivity().getApplication();
//        app.user = user;
//        app.missions = missionsList;
        dialog.dismiss();
        Intent i = new Intent(getContext(),DashboardLoot.class);
        i.putExtra("UID", user.getUserID());
        startActivity(i);
    }

    private void syncUser(final String userID) {
        dialog.setMessage("Syncing Data...");
//        users.child(userId).addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(DataSnapshot dataSnapshot) {
//                user = dataSnapshot.getValue(User.class);
//                Log.i("User Email", "Value is: " + user.getEmail());
//                syncSharedPrefs(user);
//            }
//
//            @Override
//            public void onCancelled(DatabaseError error) {
//                Log.i("Error",  error.toException().getMessage());
//            }
//        });
//
//        missions.addChildEventListener(new ChildEventListener() {
//            @Override
//            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
//                Log.i("Missions",dataSnapshot.getKey());
//                missionsList.add(dataSnapshot.getValue(Mission.class));
//                LootApplication app = (LootApplication)getActivity().getApplication();
//                app.missions=missionsList;
//            }
//
//            @Override
//            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
//
//            }
//
//            @Override
//            public void onChildRemoved(DataSnapshot dataSnapshot) {
//
//            }
//
//            @Override
//            public void onChildMoved(DataSnapshot dataSnapshot, String s) {
//
//            }
//
//            @Override
//            public void onCancelled(DatabaseError databaseError) {
//
//            }
//        });
        StringRequest syncRequest = new StringRequest(Request.Method.GET, Endpoints.syncRequest+userID,
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
                        syncSharedPrefs(user);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        dialog.dismiss();
                        Toast.makeText(getContext(), "Error while syncing data!", Toast.LENGTH_SHORT).show();
                    }
                });
        RequestQueue requestQueue = Volley.newRequestQueue(getActivity());
        requestQueue.add(syncRequest);
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("LootPrefs", Context.MODE_PRIVATE);
        final String fcmToken = sharedPreferences.getString("com.hackncs.FCMToken", "");
        if (!fcmToken.equals("")) {
            Log.d("FCMToken", fcmToken);
            StringRequest syncFCMToken = new StringRequest(Request.Method.POST,
                    Endpoints.updateUser + userID + "/edit/",
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            Log.d("FCMVolley", "response");
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            Log.d("FCMVolley", error.getMessage());
                        }
                    }){
                @Override
                protected Map<String, String> getParams() throws AuthFailureError {
                    Map map = new HashMap();
                    map.put("fcm_token", fcmToken);
                    return map;
                }
            };
            requestQueue.add(syncFCMToken);
        }
    }
}