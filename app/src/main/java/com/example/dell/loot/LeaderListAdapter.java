//<<<<<<< HEAD
//package com.example.dell.loot;
//
//import android.content.Context;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.ViewGroup;
//import android.widget.BaseAdapter;
//import android.widget.ImageView;
//import android.widget.TextView;
//
//import java.util.ArrayList;
//
//public class LeaderListAdapter extends BaseAdapter {
//
//    ArrayList<String> usernames, coins;
//    Context context;
//    LayoutInflater layoutInflater;
//    ArrayList<Integer> avatarIDs;
//
//    public LeaderListAdapter(Context context, ArrayList<String> usernames, ArrayList<String> coins, ArrayList<Integer> avatarIDs) {
//        this.context = context;
//        this.usernames = usernames;
//        this.coins = coins;
//        this.avatarIDs = avatarIDs;
//        layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
//    }
//
//    @Override
//    public int getCount() {
//        return usernames.size();
//    }
//
//    @Override
//    public Object getItem(int i) {
//        return null;
//    }
//
//    @Override
//    public long getItemId(int i) {
//        return i;
//    }
//
//    @Override
//    public View getView(int i, View view, ViewGroup viewGroup) {
//        if (view == null) {
//            View customView = layoutInflater.inflate(R.layout.leader_list, viewGroup, false);
//            ImageView icon = customView.findViewById(R.id.icon);
//            TextView username = customView.findViewById(R.id.usernameLine);
//            TextView coin = customView.findViewById(R.id.coinsLine);
//            //TODO: set avatars
////        icon.setImageResource();
//            username.setText(usernames.get(i));
//            coin.setText(coins.get(i));
//            customView.setBackgroundResource(R.drawable.list_shadow);
//            return customView;
//        }
//        return view;
//    }
//}
//=======
////package com.example.dell.loot;
////
////import android.content.Context;
////import android.view.LayoutInflater;
////import android.view.View;
////import android.view.ViewGroup;
////import android.widget.BaseAdapter;
////import android.widget.ImageView;
////import android.widget.TextView;
////
////import java.util.ArrayList;
////
////public class LeaderListAdapter extends BaseAdapter {
////
////    ArrayList<String> usernames, coins;
////    Context context;
////    LayoutInflater layoutInflater;
////
////    public LeaderListAdapter(Context context, ArrayList<String> usernames, ArrayList<String> coins) {
////        this.context = context;
////        this.usernames = usernames;
////        this.coins = coins;
////        layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
////    }
////
////    @Override
////    public int getCount() {
////        return usernames.size();
////    }
////
////    @Override
////    public Object getItem(int i) {
////        return null;
////    }
////
////    @Override
////    public long getItemId(int i) {
////        return i;
////    }
////
////    @Override
////    public View getView(int i, View view, ViewGroup viewGroup) {
////        View customView = layoutInflater.inflate(R.layout.leader_list, viewGroup);
////        ImageView icon = customView.findViewById(R.id.icon);
////        TextView username = customView.findViewById(R.id.usernameLine);
////        TextView coin = customView.findViewById(R.id.coinsLine);
////        icon.setImageResource(R.mipmap.ic_launcher_round);
////        username.setText(usernames.get(i));
////        coin.setText(coins.get(i));
////        customView.setBackgroundResource(R.drawable.list_shadow);
////        return customView;
////    }
////}
//>>>>>>> cd98193f5603aa0e55d628740ec25191ce38576f
