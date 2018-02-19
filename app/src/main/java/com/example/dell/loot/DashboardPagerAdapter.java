package com.example.dell.loot;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentStatePagerAdapter;

/**
 * Created by DELL on 1/19/2018.
 */

public class DashboardPagerAdapter extends FragmentStatePagerAdapter {

    private int NUM_ITEMS = 4;
    private String[] titles= new String[]{"Current Mission", "Locator","Stats","LeaderBoard"};

    public DashboardPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    // Returns total number of pages
    @Override
    public int getCount() {
        return  NUM_ITEMS ;
    }

    // Returns the fragment to display for that page
    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                return new Current_Mission();
            case 1:
                //return new SecondFragment();
                return new Locator();
            case 2:
                //return new ThirdFragment();
                return new Stats();
            case 3:
                return new LeaderBoard();
            default:
                return null;
        }
    }

    // Returns the page title for the top indicator
    @Override
    public CharSequence getPageTitle(int position) {
        return  titles[position];
    }

}