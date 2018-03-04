package com.example.dell.loot;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.NavigationView;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class Main2Activity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    FirebaseDatabase database;
    DatabaseReference users, missions;
    FirebaseAuth mAuth;
    User user;
    String userId;
    ArrayList<Mission> missionsList = new ArrayList<>();
    private NavigationView navigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
        Intent intent = getIntent();
        userId = intent.getStringExtra("UID");
        Log.i("UID",userId);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        mAuth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();
        users = database.getReference("Users");
        missions = database.getReference("Missions");
//        users.child(userId).child("online").setValue(true);

        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        Dashboard fragment = new Dashboard();
        fragmentTransaction.replace(R.id.frame, fragment,"dashboard");
        fragmentTransaction.commit();

        navigationView.getMenu().getItem(0).setChecked(true);
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        }
        else {
            int size = getSupportFragmentManager().getFragments().size();
            String fragmentTag = getSupportFragmentManager().getFragments().get(size-1).getTag();
            Log.i("Fragment", fragmentTag);
            if(fragmentTag.equals("locator") ||
                    fragmentTag.equals("stats") ||
                    fragmentTag.equals("leaderboard")) {
                BottomNavigationView navigationView = findViewById(R.id.navigation);
                navigationView.getMenu().getItem(0).setChecked(true);
                android.support.v4.app.Fragment fragment = new CurrentMission();
                FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                transaction.replace(R.id.frame_container, fragment,"current_mission");
                transaction.addToBackStack(null);
                transaction.commit();
            }
            else if (fragmentTag.equals("current_mission")) {
                finishAffinity();
            }
            else if(fragmentTag.equals("about") ||
                    fragmentTag.equals("how_to") ||
                    fragmentTag.equals("help") ||
                    fragmentTag.equals("contact_us")) {
                navigationView.getMenu().getItem(0).setChecked(true);
                FragmentManager fragmentManager = getSupportFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                Dashboard fragment = new Dashboard();
                fragmentTransaction.replace(R.id.frame, fragment,"dashboard");
                fragmentTransaction.commit();
            }
            else {
                super.onBackPressed();
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main2, menu);
        return false;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

        if (id == R.id.nav_dashboard) {
            Dashboard fragment = new Dashboard();
            fragmentTransaction.replace(R.id.frame, fragment,"dashboard");
            fragmentTransaction.commit();
        }
        else if (id == R.id.nav_logout) {
            mAuth.signOut();
            Intent intent=new Intent(this,Main3Activity.class);
            startActivity(intent);
        }
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onResume() {
        super.onResume();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        LootApplication app = (LootApplication)getApplication();
        Log.i("Name",app.user.getEmail());
        Log.i("Mission",app.missions.get(0).missionId);

//        attach(currentUser.getUid());
    }

    public void syncSharedPrefs(User user) {
        SharedPreferences sharedPreferences=getSharedPreferences("LootPrefs", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor=sharedPreferences.edit();
        editor.putString("Uid",user.getUserId());
        editor.putString("Username",user.getUsername());
        editor.putInt("Score",user.getScore());
        editor.putString("mActive",user.getActive());
        editor.apply();

        LootApplication app = (LootApplication)getApplication();
        app.user = user;
        app.missions = missionsList;
    }

    private void attach(String userId)
    {
        users.child(userId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.
                user = dataSnapshot.getValue(User.class);
                Log.i("User Email", "Value is: " + user.getEmail());
                LootApplication app = (LootApplication)getApplication();
                app.user = user;
                syncSharedPrefs(user);
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Log.i("Error",  error.toException().getMessage());
            }
        });

        missions.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                Log.i("Missions",dataSnapshot.getKey());
                missionsList.add(dataSnapshot.getValue(Mission.class));
                LootApplication app = (LootApplication)getApplication();
                app.missions = missionsList;
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

}
