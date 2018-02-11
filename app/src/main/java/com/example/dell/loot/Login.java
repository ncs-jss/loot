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
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


/**
 * A simple {@link Fragment} subclass.
 */
public class Login extends Fragment {


    private FirebaseAuth mAuth;
    private FirebaseDatabase database;
    DatabaseReference users;
    User user;
    public Login() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_login, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mAuth=FirebaseAuth.getInstance();
        database=FirebaseDatabase.getInstance();
        users=database.getReference("Users");
        final EditText email_field=(EditText)getView().findViewById(R.id.email);
        final EditText password_field=(EditText)getView().findViewById(R.id.password);
        Button login=(Button)getView().findViewById(R.id.login);
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String email=String.valueOf(email_field.getText());
                String password=String.valueOf(password_field.getText());
                mAuth.signInWithEmailAndPassword(email, password)
                        .addOnCompleteListener(getActivity(), new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {
                                    // Sign in success, update UI with the signed-in user's information
                                    // Log.d(TAG, "createUserWithEmail:success");
                                    Toast.makeText(getContext(),"Register Successful",Toast.LENGTH_SHORT).show();
                                    FirebaseUser firebaseUser = mAuth.getCurrentUser();

                                    updateSharedPrefs(firebaseUser.getUid());
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


        final TextView register=(TextView)getView().findViewById(R.id.goto_register);

        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                register();
            }
        });

    }

    public void register()
    {
        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        Register fragment = new Register();
        fragmentTransaction.replace(R.id.login_frame, fragment);
        fragmentTransaction.commit();

    }

    private void updateSharedPrefs(String uid)
    {
        users.child(uid).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.
                user= dataSnapshot.getValue(User.class);
                Log.i("User Email", "Value is: " + user.getEmail());
                SharedPreferences sharedPreferences=getActivity().getSharedPreferences("LootPrefs", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor=sharedPreferences.edit();
                editor.putString("Uid",user.getUserId());
                editor.putString("Uname",user.getUsername());
                editor.putInt("Score",user.getScore());
                editor.putString("mActive",user.getActive());
                editor.putString("mFound",user.getFound().get(user.getFound().size()-1));
                editor.putString("mCompleted",user.getCompleted().get(user.getCompleted().size()-1));
                editor.putString("mDropped",user.getDropped().get(user.getDropped().size()-1));
                editor.apply();
                Intent i=new Intent(getContext(),Main2Activity.class);
                i.putExtra("userID",user.getUserId());
                startActivity(i);
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Log.i("Error",  error.toException().getMessage());
            }
        });
    }

}
