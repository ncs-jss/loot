package com.example.dell.loot;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import static com.google.android.gms.internal.zzahn.runOnUiThread;

public class Splash extends Fragment {


    private FirebaseAuth mAuth;
    FirebaseDatabase database;
    DatabaseReference users,missions;
    FirebaseUser fbuser;
    User user;
    ArrayList<Mission> missionsList=new ArrayList<>();

    boolean isConnected,logged_in;
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
        // Inflate the layout for this fragment
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
        database=FirebaseDatabase.getInstance();
        users=database.getReference("Users");
        missions=database.getReference("Missions");



        new BackgroundTasks().execute();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (isConnected) {
                    if (logged_in) {
                        Intent i = new Intent(getContext(), Main2Activity.class);
                        i.putExtra("UID", fbuser.getUid());
                        startActivity(i);
                    } else {
                        changeView();
                    }
                } else {

                    Toast.makeText(getActivity(),"Not Connected",Toast.LENGTH_SHORT).show();
                    getActivity().finish();
                }
            }
        }, 3750);


    }

    @Override
    public void onStart() {
        super.onStart();

    }

    private void attach(String userId) {
        users.child(userId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.

                user = dataSnapshot.getValue(User.class);
                Log.i("User Email", "Value is: " + user.getEmail());
                syncSharedPrefs(user);


            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Log.i("Error", error.toException().getMessage());
            }
        });

        missions.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {

                missionsList.add(dataSnapshot.getValue(Mission.class));
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }
    public void syncSharedPrefs(User user)
    {
        SharedPreferences sharedPreferences=getActivity().getSharedPreferences("LootPrefs", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor=sharedPreferences.edit();
        editor.putString("Uid",user.getUserId());
        editor.putString("Username",user.getUsername());
        editor.putInt("Score",user.getScore());
        editor.putString("mActive",user.getActive());
        editor.apply();
        Loot_Application app=(Loot_Application)getActivity().getApplication();
        app.user=user;
        app.missions=missionsList;



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

    private void backgroundTasks() {

        isConnected = isOnline();
        logged_in = mAuth.getCurrentUser()!=null;
        fbuser=mAuth.getCurrentUser();
        attach(fbuser.getUid());


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
//

        }
    }
    private void changeView()
    {
        ProgressBar loader=(ProgressBar)getView().findViewById(R.id.loader);
        RelativeLayout layout=(RelativeLayout)getView().findViewById(R.id.rel_layout);
        Button login_button = (Button) getView().findViewById(R.id.login_button);
        Button register_button = (Button) getView().findViewById(R.id.register_button);
        loader.setVisibility(View.GONE);
        layout.setVisibility(View.VISIBLE);
        login_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                Login fragment = new Login();
                fragmentTransaction.replace(R.id.login_frame, fragment);
                fragmentTransaction.commit();

            }
        });

        register_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                Register fragment = new Register();
                fragmentTransaction.replace(R.id.login_frame, fragment);
                fragmentTransaction.commit();
            }
        });


    }





}
