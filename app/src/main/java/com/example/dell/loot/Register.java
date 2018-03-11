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
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
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
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Register extends Fragment {

    private FirebaseAuth mAuth;
//    private FirebaseDatabase database;
//    DatabaseReference users;
    View view;
    Spinner spinner;
    EditText name, email, contact, zeal, username, password;
    ImageView avatar;
    ProgressDialog dialog;
    FirebaseFirestore db;
    FirebaseUser firebaseUser;
    int avatarID = 0;
    User user;
    public Register() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_register, container, false);
        spinner.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                avatarID = i;
                //TODO: set avatar
//                avatar.setImageResource();
            }
        });
        ArrayList<String> avatars = new ArrayList<>();
        avatars.add("Avatar 1");
        avatars.add("Avatar 2");
        avatars.add("Avatar 3");
        avatars.add("Avatar 4");
        avatars.add("Avatar 5");
        avatars.add("Avatar 6");
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_spinner_item, avatars);
        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(arrayAdapter);
        spinner.setSelection(avatarID);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initializeViews();
        mAuth = FirebaseAuth.getInstance();
        db=FirebaseFirestore.getInstance();
//        database = FirebaseDatabase.getInstance();
//        users = database.getReference("Users");
        Button register = getView().findViewById(R.id.register);
        TextView login = getView().findViewById(R.id.goto_login);

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                login();
            }
        });

        dialog.setTitle("Please Wait");
        dialog.setCancelable(false);
        dialog.setMessage("Signing in...");

        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.show();
                mAuth.createUserWithEmailAndPassword(email.getText().toString(), password.getText().toString())
                        .addOnCompleteListener(getActivity(), new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {
                                    dialog.dismiss();
                                    firebaseUser = mAuth.getCurrentUser();
                                    updateFirebase(firebaseUser);
                                    StringRequest register = new StringRequest(Request.Method.POST,
                                            Endpoints.register,
                                            new Response.Listener<String>() {
                                                @Override
                                                public void onResponse(String response) {
                                                    syncSharedPrefs(getUser());
                                                    Toast.makeText(getContext(),"You're registered successfully!",Toast.LENGTH_SHORT).show();
                                                    Intent i=new Intent(getContext(),WelcomeSlider.class);
                                                    startActivity(i);
                                                }
                                            },
                                            new Response.ErrorListener() {
                                                @Override
                                                public void onErrorResponse(VolleyError error) {

                                                }
                                            }){
                                        @Override
                                        protected Map<String, String> getParams() throws AuthFailureError {
                                            Map<String, String> map = new HashMap();
                                            map.put("reference_token", firebaseUser.getUid());
                                            map.put("email", email.getText().toString());
                                            map.put("name", name.getText().toString());
                                            map.put("username", username.getText().toString());
                                            map.put("zeal_id", zeal.getText().toString());
                                            map.put("contact_number", contact.getText().toString());
                                            map.put("score","0");
                                            map.put("stage","1");
                                            map.put("mission_state","0");
                                            map.put("drop_count","0");
                                            map.put("duel_won","0");
                                            map.put("duel_lost","0");
                                            map.put("avatar_id", String.valueOf(avatarID));
                                            return map;
                                        }
                                    };
                                    RequestQueue requestQueue = Volley.newRequestQueue(getActivity());
                                    requestQueue.add(register);
                                } else {
                                    dialog.dismiss();
                                    Toast.makeText(getContext(), "Registration failed."+ task.getException().getMessage(),
                                            Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
            }
        });
    }

    private void initializeViews() {
        name = view.findViewById(R.id.name);
        email = view.findViewById(R.id.email);
        contact = view.findViewById(R.id.contactNumber);
        zeal = view.findViewById(R.id.zealId);
        username = view.findViewById(R.id.username);
        password = view.findViewById(R.id.password);
        spinner = view.findViewById(R.id.spinner);
        avatar = view.findViewById(R.id.avatar);
        dialog=new ProgressDialog(getContext());
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    private User getUser() {
        user.setUserID(firebaseUser.getUid());
        user.setUsername(username.getText().toString());
        user.setZealID(zeal.getText().toString());
        user.setName(name.getText().toString());
        user.setEmail(email.getText().toString());
        user.setAvatarID(avatarID);
        user.setScore(0);
        user.setStage(1);
        user.setState(0);
        user.setDropCount(0);
        user.setDuelWon(0);
        user.setDuelLost(0);
        user.setContactNumber(Long.valueOf(contact.getText().toString()));
//        user.setDropped();
        return  user;
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
    }


//
//    private void updateDB(User user) {
//        users.child(user.getUserID()).setValue(user);
//        Intent i=new Intent(getContext(),Main2Activity.class);
//        i.putExtra("UID",user.getUserID());
//        startActivity(i);
//    }

    public void login() {

        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        Login fragment = new Login();
        fragmentTransaction.replace(R.id.login_frame, fragment);
        fragmentTransaction.commit();
    }
    public void updateFirebase(FirebaseUser firebaseUser)
    {
        Map<String, Object> user = new HashMap<>();
        user.put("userID", firebaseUser.getUid());
        user.put("online", false);

        db.collection("users").document(firebaseUser.getUid())
                .set(user).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {

                if(task.isSuccessful())
                {
                    Log.i("Added Succesfully","");
                }
                else
                {
                    Log.i("Error",task.getException().getMessage());
                }
            }
        });

    }
}
