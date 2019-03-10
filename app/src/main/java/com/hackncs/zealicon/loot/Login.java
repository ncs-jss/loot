package com.hackncs.zealicon.loot;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
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
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import static androidx.constraintlayout.motion.widget.MotionScene.TAG;

public class Login extends Fragment {

    private FirebaseAuth mAuth;

    User user;
    ProgressDialog dialog;
    EditText email, password;
    Button login;
    TextView sendMail;
    FirebaseUser firebaseUser;

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
                                     firebaseUser = mAuth.getCurrentUser();
                                    if(firebaseUser.isEmailVerified())
                                        syncUser(firebaseUser.getUid());
                                    else {
                                        dialog.dismiss();
                                        sendMail.setVisibility(View.VISIBLE);
                                        mAuth.signOut();
                                        Toast.makeText(getContext(), "Please Verify your email to continue", Toast.LENGTH_LONG).show();
                                    }
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

    }

    private void initializeViews() {
        email = getView().findViewById(R.id.email);
        password = getView().findViewById(R.id.password);
        login = getView().findViewById(R.id.login);
        sendMail=getView().findViewById(R.id.sendmail);

        sendMail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                firebaseUser.sendEmailVerification().addOnCompleteListener(getActivity(), new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful()){
                            Toast.makeText(getActivity(),
                                    "Verification Mail Sent. Please verify to continue",
                                    Toast.LENGTH_SHORT).show();

                        }
                        else{
                            Log.e(TAG, "sendEmailVerification", task.getException());
                            Toast.makeText(getActivity(),
                                    "Failed to send verification email.",
                                    Toast.LENGTH_SHORT).show();

                        }
                    }
                });
            }
        });
    }



    public void syncSharedPrefs(User user) {
        dialog.setMessage("Completing...");
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("LootPrefs", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("com.hackncs.userID", user.getUserID());
        editor.putString("com.hackncs.username", user.getUsername());
        editor.putString("com.hackncs.admissionNo", user.getAdmissionNo());
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
        editor.apply();
        dialog.dismiss();
        Intent i = new Intent(getContext(),DashboardLoot.class);
        i.putExtra("UID", user.getUserID());
        startActivity(i);
    }

    private void syncUser(final String userID) {
        dialog.setMessage("Syncing Data...");
        StringRequest syncRequest = new StringRequest(Request.Method.GET, Endpoints.syncRequest+userID,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        JSONObject jsonObject;
                        try {
                            Log.i("Response",response);
                            jsonObject = new JSONObject(response);
                            user.setUserID(jsonObject.getString("reference_token"));
                            user.setUsername(jsonObject.getString("username"));
                            user.setAdmissionNo(jsonObject.getString("admission_no"));
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
                }){
                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    Map<String, String> params = new HashMap<>();
                    params.put("x-auth",Endpoints.apikey);
                    return params;
                }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(getActivity());
        requestQueue.add(syncRequest);
        getFCMToken();
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
                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    Map<String, String> params = new HashMap<>();
                    params.put("x-auth",Endpoints.apikey);
                    return params;
                }
            };
            requestQueue.add(syncFCMToken);
        }
    }
    public void getFCMToken(){
        FirebaseInstanceId.getInstance().getInstanceId()
                .addOnCompleteListener(new OnCompleteListener<InstanceIdResult>() {
                    @Override
                    public void onComplete(@NonNull Task<InstanceIdResult> task) {
                        if (!task.isSuccessful()) {
                            Log.w(TAG, "getInstanceId failed", task.getException());
                            return;
                        }

                        // Get new Instance ID token
                        String token = task.getResult().getToken();
                        Log.i("FCMTOKEN",token);
                        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("LootPrefs", Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putString("com.hackncs.FCMToken", token);
                        editor.commit();
                    }
                });
    }
}