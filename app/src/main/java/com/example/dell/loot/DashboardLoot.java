package com.example.dell.loot;

import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.widget.Toast;

import com.luseen.spacenavigation.SpaceItem;
import com.luseen.spacenavigation.SpaceNavigationView;
import com.luseen.spacenavigation.SpaceOnClickListener;

public class DashboardLoot extends AppCompatActivity {

    SpaceNavigationView spaceNavigationView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard_loot);
        Toolbar toolbar=(Toolbar)findViewById(R.id.toolbar2);
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
            FragmentManager fragmentManager = getSupportFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            Dashboard fragment=new Dashboard();
            fragmentTransaction.replace(R.id.frame, fragment,"dashboard");
            fragmentTransaction.commit();
        }

        else {
            super.onBackPressed();
        }

    }
}
