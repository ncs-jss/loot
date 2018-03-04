package com.example.dell.loot;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class CurrentMission extends Fragment {

    Button submit, drop_mission;
    TextView story, question;
    EditText answer;
    FirebaseDatabase database;
    DatabaseReference users, missions;
    Mission mission;
    ProgressDialog progressDialog;
    LootApplication app;

    public CurrentMission() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_current_mission, container, false);
        story = view.findViewById(R.id.story);
        question = view.findViewById(R.id.question);
        answer = view.findViewById(R.id.answer);
        submit = view.findViewById(R.id.submit);
        drop_mission = view.findViewById(R.id.drop_mission);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        database = FirebaseDatabase.getInstance();
        users = database.getReference("Users");
        missions = database.getReference("Missions");
        progressDialog = new ProgressDialog(getContext());
        app = (LootApplication)getActivity().getApplication();
        if(app.user.active != null) {
            progressDialog.setMessage("Loading Mission...");
            progressDialog.setTitle("Please Wait...");
            progressDialog.show();
            missions.child(app.user.active).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    mission = dataSnapshot.getValue(Mission.class);
                    story.setText(mission.getStory());
                    question.setText(mission.getDescription());
                    if(progressDialog.isShowing()) {
                        progressDialog.dismiss();
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(answer.getText().toString().equalsIgnoreCase(mission.getAnswer())) {
                    app.user.active = null;
                    app.user.completed.add(mission.getMissionId());
                    app.user.score += 10;
                    users.child(app.user.getUserId()).setValue(app.user);
                }
            }
        });

        drop_mission.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                    app.user.active = null;
                    app.user.dropped.add(mission.getMissionId());
                    app.user.score -= 2;
                    users.child(app.user.getUserId()).setValue(app.user);
            }
        });
    }

    @Override
    public void onDestroyView() {
        Log.i("Current Mission","Destroy View");
        if(progressDialog.isShowing()) {
            progressDialog.dismiss();
        }
        super.onDestroyView();
    }

    @Override
    public void onStart() {
          if(app.user.getActive()==null) {
            showDialog();
          }
          super.onStart();
    }

    @Override
    public void onStop() {
        Log.i("Current Mission","Stop");
        super.onStop();
    }

    private void showDialog()
    {
        AlertDialog alertDialog = new AlertDialog.Builder(getActivity()).create();
        alertDialog.setTitle("Alert");
        alertDialog.setMessage("No Active Mission");
        alertDialog.setButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                BottomNavigationView navigationView = getActivity().findViewById(R.id.navigation);
                navigationView.getMenu().getItem(0).setChecked(true);
                android.support.v4.app.Fragment fragment = new Locator();
                FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
                transaction.replace(R.id.frame_container, fragment,"locator");
                transaction.addToBackStack(null);
                transaction.commit();
            }
        });
        alertDialog.show();
    }
}
