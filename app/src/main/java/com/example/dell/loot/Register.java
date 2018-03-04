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

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class Register extends Fragment {

    private FirebaseAuth mAuth;
    private FirebaseDatabase database;
    DatabaseReference users;
    User user;
    public Register() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_register, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mAuth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();
        users = database.getReference("Users");
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
                user = getUser();
                mAuth.createUserWithEmailAndPassword(user.getEmail(), user.getPassword())
                        .addOnCompleteListener(getActivity(), new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {
                                    Toast.makeText(getContext(),"Register Successful",Toast.LENGTH_SHORT).show();
                                    FirebaseUser firebaseUser = mAuth.getCurrentUser();
                                    user.setUserId(firebaseUser.getUid());
                                    updateDB(user);
                                } else {
                                    Toast.makeText(getContext(), "Authentication failed."+ task.getException().getMessage(),
                                            Toast.LENGTH_SHORT).show();

                                }
                            }
                        });
            }
        });
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    private User getUser() {
        View view = getView();
        EditText username = view.findViewById(R.id.username);
        EditText zealId = view.findViewById(R.id.zealId);
        EditText name = view.findViewById(R.id.name);
        EditText contact = view.findViewById(R.id.contact);
        EditText email = view.findViewById(R.id.email);
        EditText password = view.findViewById(R.id.password);
        ImageView avtar = view.findViewById(R.id.avtar);
        User user = new User();
        //user.setAvatarId((int)avtar.getTag());
        user.setContact(Long.parseLong(contact.getText()+""));
        user.setEmail(email.getText()+"");
        user.setName(name.getText()+"");
        user.setScore(0);
        user.setPassword(password.getText()+"");
        user.setZealID(zealId.getText()+"");
        user.setUsername(username.getText()+"");
        return  user;
    }

    private void updateDB(User user) {
        users.child(user.getUserId()).setValue(user);
        Intent i=new Intent(getContext(),Main2Activity.class);
        i.putExtra("UID",user.getUserId());
        startActivity(i);
    }

    public void login() {
        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        Login fragment = new Login();
        fragmentTransaction.replace(R.id.login_frame, fragment);
        fragmentTransaction.commit();
    }
}
