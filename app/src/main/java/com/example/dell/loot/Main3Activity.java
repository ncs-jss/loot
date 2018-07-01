package com.example.dell.loot;

import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

public class Main3Activity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main3);
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        Splash fragment = new Splash();
        loadFragment(fragment,"splash");
    }


    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    @Override
    public void onBackPressed() {
//
        int size=getSupportFragmentManager().getFragments().size();

        String fragmentTag=getSupportFragmentManager().getFragments().get(size-1).getTag();
        Log.i("Fragment",fragmentTag);


        if(fragmentTag.equals("register")||fragmentTag.equals("login")) {

            android.support.v4.app.Fragment fragment=new Splash();
            loadFragment(fragment,"splash");

        }
        else if (fragmentTag.equals("splash"))
        {
            finishAffinity();
        }
        else {
            super.onBackPressed();
        }

    }

    private void loadFragment(Fragment fragment, String tag) {
        // load fragment
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.login_frame, fragment,tag);
        transaction.addToBackStack(null);
        transaction.commit();
    }
}
