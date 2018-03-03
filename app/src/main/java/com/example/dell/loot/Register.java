package com.example.dell.loot;


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

import org.w3c.dom.Text;


/**
 * A simple {@link Fragment} subclass.
 */
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
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_register, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mAuth=FirebaseAuth.getInstance();
        database=FirebaseDatabase.getInstance();
        users=database.getReference("Users");
        Button register=(Button)getView().findViewById(R.id.register);
        TextView login=(TextView)getView().findViewById(R.id.goto_login);

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                login();
            }
        });

        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                user=getUser();
                mAuth.createUserWithEmailAndPassword(user.getEmail(), user.getPassword())
                        .addOnCompleteListener(getActivity(), new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {
                                    // Sign in success, update UI with the signed-in user's information
                                   // Log.d(TAG, "createUserWithEmail:success");
                                    Toast.makeText(getContext(),"Register Successful",Toast.LENGTH_SHORT).show();
                                    FirebaseUser firebaseUser = mAuth.getCurrentUser();
                                    user.setUserId(firebaseUser.getUid());
                                    updateDB(user);
                                } else {
                                    // If sign in fails, display a message to the user.
                                    //Log.w(TAG, "createUserWithEmail:failure", task.getException());
                                    Toast.makeText(getContext(), "Authentication failed."+ task.getException().getMessage(),
                                            Toast.LENGTH_SHORT).show();

                                }

                                // ...
                            }
                        });
            }
        });
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    private User getUser()
    {
        View view=getView();
        EditText username=(EditText)view.findViewById(R.id.username);
        EditText zealId=(EditText)view.findViewById(R.id.zealId);
        EditText name=(EditText)view.findViewById(R.id.name);
        EditText contact=(EditText)view.findViewById(R.id.contact);
        EditText email=(EditText)view.findViewById(R.id.email);
        EditText password=(EditText)view.findViewById(R.id.password);
        ImageView avtar=(ImageView)view.findViewById(R.id.avtar);
        User user=new User();
        //user.setAvtarId((int)avtar.getTag());
        user.setContact(Long.parseLong(contact.getText()+""));
        user.setEmail(email.getText()+"");
        user.setName(name.getText()+"");
        user.setScore(0);
        user.setPassword(password.getText()+"");
        user.setZealID(zealId.getText()+"");
        user.setUsername(username.getText()+"");
        return  user;

    }
    private void updateDB(User user)
    {

        users.child(user.getUserId()).setValue(user);

        Intent i=new Intent(getContext(),WelcomeSlider.class);
        startActivity(i);
    }

    public void login()
    {
        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        Login fragment = new Login();
        fragmentTransaction.replace(R.id.login_frame, fragment);
        fragmentTransaction.commit();

    }

}
