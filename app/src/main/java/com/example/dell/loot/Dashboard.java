package com.example.dell.loot;

import android.content.Context;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;



public class Dashboard extends Fragment {


    private ViewPager viewPager;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_dashboard, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
//        viewPager=(ViewPager)getView().findViewById(R.id.viewpager);
        Loot_Application app=(Loot_Application) getActivity().getApplication();

//        DashboardPagerAdapter dashboardPagerAdapter=new DashboardPagerAdapter(getActivity().getSupportFragmentManager());
//        viewPager.setAdapter(dashboardPagerAdapter);
//
//        TabLayout tabLayout = (TabLayout)getView().findViewById(R.id.tabs);
//        tabLayout.setSelected(true);
//        tabLayout.setSelectedTabIndicatorColor(Color.WHITE);
//        tabLayout.setupWithViewPager(viewPager);
//        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);
//        if(app.user.getActive()==null||app.user.getActive().equalsIgnoreCase(""))
//        {
//            switchTab(1);
//        }

        BottomNavigationView navigation = (BottomNavigationView)getView().findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        if(app.user.getActive()==null||app.user.getActive().equalsIgnoreCase(""))
        {
            Fragment fragment=new Locator();
            navigation.getMenu().getItem(1).setChecked(true);
            loadFragment(fragment,"locator");
        }
        else
        {
            navigation.getMenu().getItem(0).setChecked(true);
            Fragment fragment=new Current_Mission();
            loadFragment(fragment,"current_mission");
        }


    }

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            Fragment fragment;
            switch (item.getItemId()) {
                case R.id.navigation_current_mission:
                    fragment=new Current_Mission();
                    loadFragment(fragment,"current_mission");
                    return true;
//                case R.id.navigation_locator:
//                    fragment=new Locator();
//                    loadFragment(fragment,"locator");
//                    return true;
//                case R.id.navigation_stats:
//                    fragment=new Stats();
//                    loadFragment(fragment,"stats");
//                    return true;
                case R.id.navigation_leaderboard:
                    fragment=new LeaderBoard();
                    loadFragment(fragment,"leaderboard");
                    return true;
            }
            return false;
        }
    };


    private void loadFragment(Fragment fragment,String tag) {
        // load fragment
        FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.frame_container, fragment,tag);
        transaction.addToBackStack(null);
        transaction.commit();
    }
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

    }

    @Override
    public void onDetach() {
        super.onDetach();

    }


}
