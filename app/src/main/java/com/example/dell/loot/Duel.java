package com.example.dell.loot;


import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;


/**
 * A simple {@link Fragment} subclass.
 */
public class Duel extends Fragment {


    public Duel() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_duel, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        View view=getView();
        TextView won=view.findViewById(R.id.won);
        TextView lost=view.findViewById(R.id.lost);
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("LootPrefs", Context.MODE_PRIVATE);
        int won_duels = sharedPreferences.getInt("com.hackncs.duelWon", 0);
        int lost_duels = sharedPreferences.getInt("com.hackncs.duelLost",0);
        won.setText(won_duels+"");
        lost.setText(lost_duels+"");
        ImageButton loot=view.findViewById(R.id.loot);
        BottomNavigationView navigationView=getActivity().findViewById(R.id.bottom_nav);
        navigationView.getMenu().getItem(0).setChecked(true);
        loot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

               loadFragment(new OnlineUsers(),"online_users");

            }
        });
    }

    private void loadFragment(Fragment fragment, String tag) {
        // load fragment
        FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.frame_container, fragment,tag);
        transaction.addToBackStack(null);
        transaction.commit();
    }
}
