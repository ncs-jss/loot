package com.example.dell.loot;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Build;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;


public class MiniGame extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mini_game);
        Toolbar toolbar=findViewById(R.id.toolbar2);
        setSupportActionBar(toolbar);
        Intent intent =getIntent();
        String player_type=intent.getStringExtra("player_type");
        String duel_id=intent.getStringExtra("duel_id");
        Bundle args=new Bundle();
        args.putString("player_type",player_type);
        args.putString("duel_id",duel_id);

        TextView action_bar_username=findViewById(R.id.user_name);
        TextView action_bar_usercoins=findViewById(R.id.user_coins);
        ImageView action_bar_useravatar=findViewById(R.id.avatar);

        SharedPreferences sharedPreferences = getSharedPreferences("LootPrefs", Context.MODE_PRIVATE);
        int score = sharedPreferences.getInt("com.hackncs.score", 0);
        String username= sharedPreferences.getString("com.hackncs.username", null);
        int avatarID = sharedPreferences.getInt("com.hackncs.avatarID", R.drawable.avatar_1);
        action_bar_useravatar.setImageResource(avatarID);
        action_bar_username.setText(username);
        action_bar_usercoins.setText(score+"");
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        GridTap fragment = new GridTap();
        fragment.setArguments(args);
        fragmentTransaction.add(R.id.game_frame, fragment);
        fragmentTransaction.commit();
    }

    @Override
    public void onBackPressed() {

    }
}
