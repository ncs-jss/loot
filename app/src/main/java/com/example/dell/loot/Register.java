package com.example.dell.loot;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
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
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;

public class Register extends Fragment {

    private FirebaseAuth mAuth;
//    private FirebaseDatabase database;
//    DatabaseReference users;
    View view;
    EditText name, email, contact, zeal, username, password;

    public Register() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_register, container, false);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initializeViews();
        mAuth = FirebaseAuth.getInstance();
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

        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mAuth.createUserWithEmailAndPassword(email.getText().toString(), password.getText().toString())
                        .addOnCompleteListener(getActivity(), new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {
                                    final FirebaseUser firebaseUser = mAuth.getCurrentUser();
                                    StringRequest register = new StringRequest(Request.Method.POST, Endpoints.register,
                                            new Response.Listener<String>() {
                                                @Override
                                                public void onResponse(String response) {
                                                    Toast.makeText(getContext(),"You're registered successfully!",Toast.LENGTH_SHORT).show();
                                                    Intent i = new Intent(getContext(),MainActivity.class);
                                                    i.putExtra("UID", firebaseUser.getUid());
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
                                            map.put("", firebaseUser.getUid());
                                            map.put("", email.getText().toString());
                                            map.put("", name.getText().toString());
                                            map.put("", username.getText().toString());
                                            map.put("", zeal.getText().toString());
                                            map.put("", contact.getText().toString());
                                            return map;
                                        }
                                    };
                                    RequestQueue requestQueue = Volley.newRequestQueue(getActivity());
                                    requestQueue.add(register);
                                } else {
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
    }

    @Override
    public void onStart() {
        super.onStart();
    }

//    private User getUser() {
//        View view = getView();
//        EditText username = view.findViewById(R.id.username);
//        EditText zealId = view.findViewById(R.id.zealId);
//        EditText name = view.findViewById(R.id.name);
//        EditText contact = view.findViewById(R.id.contact);
//        EditText email = view.findViewById(R.id.email);
//        EditText password = view.findViewById(R.id.password);
//        ImageView avtar = view.findViewById(R.id.avatar);
//        User user = new User();
////        user.setAvatarID((int)avtar.getTag());
//        user.setContactNumber(Long.parseLong(contact.getText()+""));
//        user.setEmail(email.getText()+"");
//        user.setName(name.getText()+"");
//        user.setScore(0);
//        user.setPassword(password.getText()+"");
//        user.setZealID(zealId.getText()+"");
//        user.setUsername(username.getText()+"");
//        return  user;
//    }
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
}
