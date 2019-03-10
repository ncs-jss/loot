package com.hackncs.zealicon.loot;

import android.os.Build;
import android.os.Bundle;

import android.util.Log;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

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

            Fragment fragment=new Splash();
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
