//package com.example.dell.loot;
//
//import android.support.v4.app.Fragment;
//import android.support.v4.app.FragmentManager;
//import android.support.v4.app.FragmentPagerAdapter;
//import android.support.v4.app.FragmentStatePagerAdapter;
//
//public class DashboardPagerAdapter extends FragmentStatePagerAdapter {
//
//    private int NUM_ITEMS = 4;
//    private String[] titles= new String[]{"Current Mission", "Locator", "Stats", "LeaderBoard"};
//
//    public DashboardPagerAdapter(FragmentManager fm) {
//        super(fm);
//    }
//
//    @Override
//    public int getCount() {
//        return  NUM_ITEMS ;
//    }
//
//    @Override
//    public Fragment getItem(int position) {
//        switch (position) {
//            case 0:
//                return new Missions();
//            case 1:
//                return new Locator();
//            case 2:
//                return new Stats();
//            case 3:
//                return new LeaderBoard();
//            default:
//                return null;
//        }
//    }
//
//    @Override
//    public CharSequence getPageTitle(int position) {
//        return  titles[position];
//    }
//}