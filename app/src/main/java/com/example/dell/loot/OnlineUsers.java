package com.example.dell.loot;


import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * A simple {@link Fragment} subclass.
 */
public class OnlineUsers extends Fragment {


    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    ArrayList<User> onlineUsers;
    FirebaseFirestore db;

    public OnlineUsers() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_online_users, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mRecyclerView = (RecyclerView) getView().findViewById(R.id.my_recycler_view);

        db = FirebaseFirestore.getInstance();
        onlineUsers = new ArrayList<>();
        // use a linear layout manager
        mLayoutManager = new LinearLayoutManager(getContext());
        mRecyclerView.setLayoutManager(mLayoutManager);


        mAdapter = new CustomRecycleAdapter(getContext(), getActivity(), onlineUsers,"online_users");
        SharedPreferences sharedPreferences =getActivity().getSharedPreferences("LootPrefs", Context.MODE_PRIVATE);
        final String userId = sharedPreferences.getString("com.hackncs.userID","");
        mRecyclerView.setAdapter(mAdapter);
        db.collection("users")
                .whereEqualTo("online", true)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (DocumentSnapshot document : task.getResult()) {
                                Map<String, Object> map = document.getData();
                                if(!map.get("userID").toString().equals(userId))
                                getUser(map.get("userID").toString());
                            }
                        } else {
                            Log.i("Error getting documents", task.getException().getMessage());
                        }
                    }
                });
    }


    private void getUser(String userID) {
        final User user = new User();
        StringRequest syncRequest = new StringRequest(Request.Method.GET,
                Endpoints.syncRequest + userID,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        JSONObject jsonObject;
                        try {
                            jsonObject = new JSONObject(response);
                            Log.i("response",response);
                            user.setUserID(jsonObject.getString("reference_token"));
                            user.setUsername(jsonObject.getString("username"));
                            user.setAdmissionNo(jsonObject.getString("admission_no"));
                            user.setName(jsonObject.getString("name"));
                            user.setEmail(jsonObject.getString("email"));
                            user.setAvatarID(Integer.valueOf(jsonObject.getString("avatar_id")));
                            user.setScore(Integer.valueOf(jsonObject.getString("score")));
                            user.setStage(Integer.valueOf(jsonObject.getString("stage")));
                            user.setState(jsonObject.getString("mission_state").equals("0")?0:1);
                            user.setDropCount(Integer.valueOf(jsonObject.getString("drop_count")));
                            user.setDuelWon(Integer.valueOf(jsonObject.getString("duel_won")));
                            user.setDuelLost(Integer.valueOf(jsonObject.getString("duel_lost")));
                            user.setContactNumber(Long.valueOf(jsonObject.getString("contact_number")));
//                            user.setDropped();

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        onlineUsers.add(user);
                        mAdapter.notifyDataSetChanged();
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.i("Error",error.getMessage());
                    }
                });
        RequestQueue requestQueue = Volley.newRequestQueue(getActivity());
        requestQueue.add(syncRequest);
    }

}
