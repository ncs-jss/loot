package com.example.dell.loot;
import android.app.Application;
import android.content.Context;
import android.support.multidex.MultiDex;
import java.util.ArrayList;

public class LootApplication extends Application {
    User user;
    ArrayList<Mission> missions;
    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }

}
