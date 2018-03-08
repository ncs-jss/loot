package com.example.dell.loot;

import android.content.Intent;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.PopupMenu;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.luseen.spacenavigation.SpaceItem;
import com.luseen.spacenavigation.SpaceNavigationView;
import com.luseen.spacenavigation.SpaceOnClickListener;

import java.util.HashMap;
import java.util.Map;

public class DashboardLoot extends AppCompatActivity {

    SpaceNavigationView spaceNavigationView;
    FloatingActionButton fab;

    FirebaseAuth mAuth;
    FirebaseFirestore db;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard_loot);
        Toolbar toolbar=(Toolbar)findViewById(R.id.toolbar2);
        fab = findViewById(R.id.fab);
        db= FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        spaceNavigationView = (SpaceNavigationView) findViewById(R.id.space);
        spaceNavigationView.initWithSaveInstanceState(savedInstanceState);
        spaceNavigationView.addSpaceItem(new SpaceItem("Duel", R.drawable.ic_menu_slideshow));
        spaceNavigationView.addSpaceItem(new SpaceItem("LeaderBoard", R.drawable.ic_menu_camera));
        spaceNavigationView.showIconOnly();
//        spaceNavigationView.changeCenterButtonIcon(R.drawable.avatar1);
        spaceNavigationView.setSpaceOnClickListener(new SpaceOnClickListener() {
            @Override
            public void onCentreButtonClick() {
                Toast.makeText(DashboardLoot.this,"onCentreButtonClick", Toast.LENGTH_SHORT).show();
                Fragment fragment=new Missions();
                loadFragment(fragment,"missions");
            }

            @Override
            public void onItemClick(int itemIndex, String itemName) {
                Toast.makeText(DashboardLoot.this, itemIndex + " " + itemName, Toast.LENGTH_SHORT).show();
                if(itemIndex==0)
                {
                    Fragment fragment=new Stats();
                    loadFragment(fragment,"duel");
                }
                else
                {
                    Fragment fragment=new LeaderBoard();
                    loadFragment(fragment,"leaderboard");
                }
            }

            @Override
            public void onItemReselected(int itemIndex, String itemName) {
                Toast.makeText(DashboardLoot.this, itemIndex + " " + itemName, Toast.LENGTH_SHORT).show();
            }
        });

//        fab.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                PopupMenu popup = new PopupMenu(DashboardLoot.this, view);
//                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
//                    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
//                    @Override
//                    public boolean onMenuItemClick(MenuItem menuItem) {
//                        switch (menuItem.getItemId()) {
//                            case R.id.item_stats:
//                                loadFragment(new Stats(),"stats");
//                                return true;
//                            case R.id.item_howTo:
//                                loadFragment(new HowTo(),"how_to");
//                                return true;
//                            case R.id.item_help:
//                                loadFragment(new Help(),"help");
//                                return true;
//                            case R.id.pop_logout:
//                                mAuth.signOut();
//                                Intent intent=new Intent(getApplicationContext(), Main3Activity.class);
//                                startActivity(intent);
//
//                                return true;
//                            default:
//                                return false;
//                        }
//                    }
//                });
//                popup.inflate(R.menu.popup_menu);
//                popup.show();
//            }
//        });

        Fragment fragment=new Missions();
        loadFragment(fragment,"missions");
        spaceNavigationView.setCentreButtonSelectable(true);
        spaceNavigationView.setCentreButtonSelected();
    }
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        spaceNavigationView.onSaveInstanceState(outState);
    }

    @Override
    protected void onResume() {
        super.onResume();
        updateFirebase(mAuth.getCurrentUser(),true);
    }

    private void loadFragment(Fragment fragment, String tag) {
        // load fragment
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.frame_container, fragment,tag);
        transaction.addToBackStack(null);
        transaction.commit();
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    @Override
    public void onBackPressed() {
//
        int size=getSupportFragmentManager().getFragments().size();

        String fragmentTag=getSupportFragmentManager().getFragments().get(size-1).getTag();
        Log.i("Fragment",fragmentTag);


        if(fragmentTag.equals("duel")||fragmentTag.equals("leaderboard")||fragmentTag.equals("current_mission")) {

            spaceNavigationView.setCentreButtonSelected();
            android.support.v4.app.Fragment fragment=new Missions();
            loadFragment(fragment,"missions");

        }
        else if (fragmentTag.equals("missions"))
        {
            finishAffinity();
        }
        else if(fragmentTag.equals("about")||fragmentTag.equals("how_to")||fragmentTag.equals("help")||fragmentTag.equals("contact_us"))
        {
//                navigationView.getMenu().getItem(0).setChecked(true);
            android.support.v4.app.Fragment fragment=new Missions();
            loadFragment(fragment,"missions");
        }

        else {
            super.onBackPressed();
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CurrentMission.LOCATION_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                Toast.makeText(this, "GPS enabled!", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "GPS disabled!", Toast.LENGTH_SHORT).show();
                finish();
            }
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    @Override
    protected void onStop() {
        super.onStop();

    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    @Override
    protected void onPause() {
        super.onPause();
        if(mAuth.getCurrentUser()!=null)
            updateFirebase(mAuth.getCurrentUser(),false);
        else
            finishAffinity();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.popup_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem) {
        switch (menuItem.getItemId()) {
            case R.id.item_stats:
                loadFragment(new Stats(),"stats");
                break;

            case R.id.item_howTo:
                loadFragment(new HowTo(),"how_to");
                break;
            case R.id.item_help:
                loadFragment(new Help(),"help");
                break;
            case R.id.pop_logout:
                updateFirebase(mAuth.getCurrentUser(),false);
                mAuth.signOut();
                Intent intent=new Intent(getApplicationContext(), Main3Activity.class);
                startActivity(intent);
                break;
            default:
        }
        return super.onOptionsItemSelected(menuItem);
    }
    public void updateFirebase(FirebaseUser firebaseUser,boolean state)
    {
        Map<String, Object> user = new HashMap<>();
        user.put("userID", firebaseUser.getUid());
        user.put("online", state);

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
