package com.hackncs.zealicon.loot;
import android.app.Application;
import android.content.Context;
import java.util.ArrayList;

import androidx.multidex.MultiDex;

public class LootApplication extends Application {
    User user;
    ArrayList<Mission> missions;
    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }

}
