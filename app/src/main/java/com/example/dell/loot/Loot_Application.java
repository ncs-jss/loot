package com.example.dell.loot;

import android.app.Application;
import android.content.Context;
import android.support.multidex.MultiDex;

import java.util.ArrayList;

/**
 * Created by DELL on 2/12/2018.
 */

public class Loot_Application extends Application {


    User user;
    ArrayList<Mission> missions;
    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }

}
