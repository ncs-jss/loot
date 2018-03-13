package com.example.dell.loot;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CustomRecycleAdapter extends RecyclerView.Adapter<CustomRecycleAdapter.MyViewHolder> {

    private Context mContext;
    private Activity mActivity;
    private ArrayList<User> users;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView name, coins;
        public ImageView avatar;

        public MyViewHolder(View view) {
            super(view);
            name = (TextView) view.findViewById(R.id.user_name);
            coins = (TextView) view.findViewById(R.id.user_coins);
            avatar = (ImageView) view.findViewById(R.id.imageView);
        }
    }


    public CustomRecycleAdapter(Context mContext, Activity mActivity, ArrayList<User> users) {
        this.mContext = mContext;
        this.mActivity = mActivity;
        this.users = users;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.recycle_cards, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, int position) {
        final RequestQueue requestQueue = Volley.newRequestQueue(new DashboardLoot());
        final User user = users.get(position);
        holder.name.setText(user.getUsername());
        holder.coins.setText("Coins $"+user.getScore());

        //TODO: set avatar
//        holder.avatar.setImageResource();
        holder.avatar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
                LayoutInflater layoutInflater = mActivity.getLayoutInflater();
                View dialogView = layoutInflater.inflate(R.layout.create_challenge,null);
                TextView toUser = dialogView.findViewById(R.id.to_user);
                final EditText stake = dialogView.findViewById(R.id.input_stake);
                toUser.setText(user.getUsername());
                final String[] fcm = new String[1];
                SharedPreferences sharedPreferences = new DashboardLoot().getSharedPreferences("LootPrefs", Context.MODE_PRIVATE);
                final String senderUsername = sharedPreferences.getString("com.hackncs.username", "");
                final String senderUserID = sharedPreferences.getString("com.hackncs.userID", "");
                builder.setView(dialogView)
                        .setPositiveButton("Send", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int id) {
                                if (Integer.valueOf(stake.getText().toString()) > user.getScore()) {
                                    Toast.makeText(mContext, "You cannot put more coins than you have on stake!", Toast.LENGTH_SHORT).show();
                                    dialog.dismiss();
                                }
                                final StringRequest send = new StringRequest(Request.Method.POST,
                                        Endpoints.send,
                                        new Response.Listener<String>() {
                                            @Override
                                            public void onResponse(String response) {

                                            }
                                        },
                                        new Response.ErrorListener() {
                                            @Override
                                            public void onErrorResponse(VolleyError error) {

                                            }
                                        }) {
                                    @Override
                                    protected Map<String, String> getParams() throws AuthFailureError {
                                        Map map = new HashMap();
                                        map.put("registration_id", fcm);
                                        map.put("message_title", "");
                                        map.put("message_body", "");
                                        JSONObject data = new JSONObject();
                                        try {
                                            data.put("request_type", "duel_request");
                                            data.put("user", senderUsername);
                                            data.put("stake", stake.getText().toString());
                                            data.put("reference_token", senderUserID);
                                            map.put("data_message", data);
                                        } catch (JSONException e) {
                                            e.printStackTrace();
                                        }
                                        return map;
                                    }
                                };
                                StringRequest getFCM = new StringRequest(Request.Method.GET,
                                        Endpoints.syncRequest + user.getUserID(),
                                        new Response.Listener<String>() {
                                            @Override
                                            public void onResponse(String response) {
                                                try {
                                                    JSONObject jsonObject = new JSONObject(response);
                                                    fcm[0] = jsonObject.getString("fcm_token");
                                                    requestQueue.add(send);
                                                } catch (JSONException e) {
                                                    e.printStackTrace();
                                                }
                                            }
                                        },
                                        new Response.ErrorListener() {
                                            @Override
                                            public void onErrorResponse(VolleyError error) {

                                            }
                                        });
                                requestQueue.add(getFCM);
                            }
                        })
                        .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.dismiss();
                            }
                        });
                AlertDialog dialog = builder.create();
                dialog.show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return users.size();
    }
}