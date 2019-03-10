package com.hackncs.zealicon.loot;

import android.app.Activity;
import android.content.Context;

import android.content.SharedPreferences;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
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

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import androidx.appcompat.app.AlertDialog;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;
import retrofit2.Call;
import retrofit2.Callback;

public class CustomRecycleAdapter extends RecyclerView.Adapter<CustomRecycleAdapter.MyViewHolder> {
    private Context mContext;
    private Activity mActivity;
    private ArrayList<User> users;
    private String key;
    Activity activity;
    String fcm = "";
    APIService fcmService;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView name, coins;
        public ImageView avatar;
        public CardView cardView;

        public MyViewHolder(View view) {
            super(view);
            name = (TextView) view.findViewById(R.id.user_name);
            coins = (TextView) view.findViewById(R.id.user_coins);
            avatar = (ImageView) view.findViewById(R.id.avatar);
            cardView=view.findViewById(R.id.card_view);
        }
    }


    public CustomRecycleAdapter(Context mContext, Activity mActivity, ArrayList<User> users, String key) {
        this.mContext = mContext;
        this.mActivity = mActivity;
        this.users = users;
        this.key=key;
        fcmService=APIUtils.getAPIService();
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.recycle_cards, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, int position) {
        final RequestQueue requestQueue = Volley.newRequestQueue(mActivity);
        final User user = users.get(position);
//        Log.i("username",users.get(position).getUsername());
        holder.name.setText(user.getUsername());
        holder.coins.setText(""+user.getScore());
        holder.avatar.setImageResource(user.getAvatarID());
        if(key.equalsIgnoreCase("online_users"))
        {
            holder.cardView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(mActivity);
                    LayoutInflater layoutInflater = mActivity.getLayoutInflater();
                    View dialogView=layoutInflater.inflate(R.layout.challenge_dialog,null);

                    TextView message=dialogView.findViewById(R.id.message);
                    final EditText my_stake=dialogView.findViewById(R.id.stake);
                    String chlng_msg="Are you sure you want to challenge "+user.getUsername()+" for a duel?";
                    message.setText(chlng_msg);
                    final Button accept=dialogView.findViewById(R.id.positive);
                    Button reject=dialogView.findViewById(R.id.negative);
                    accept.setText("Send");
                    reject.setText("Cancel");
                    builder.setView(dialogView);
                    final AlertDialog dialog = builder.create();
                    SharedPreferences sharedPreferences = mActivity.getSharedPreferences("LootPrefs", Context.MODE_PRIVATE);
                    final String senderUsername = sharedPreferences.getString("com.hackncs.username", "");
                    final String senderUserID = sharedPreferences.getString("com.hackncs.userID", "");
                    final int myScore = sharedPreferences.getInt("com.hackncs.score", 0);


                    accept.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            if(my_stake.getText()==null||my_stake.getText().length()==0)
                            {
                                Toast.makeText(mActivity,"Enter your stake",Toast.LENGTH_SHORT).show();
                            }
                            else{

                                try
                                {
                                    final int stake=Integer.valueOf(my_stake.getText().toString());

                                    if (stake <= 0) {
                                        Toast.makeText(mContext, "You entered an invalid value!", Toast.LENGTH_SHORT).show();

                                    }
                                    else if (stake > myScore) {
                                        Toast.makeText(mContext, "You cannot put more coins than you have on stake!", Toast.LENGTH_SHORT).show();

                                    }
                                    else {
                                        StringRequest getFCM = new StringRequest(Request.Method.GET,
                                                Endpoints.syncRequest + user.getUserID(),
                                                new Response.Listener<String>() {
                                                    @Override
                                                    public void onResponse(String response) {
                                                        try {
                                                            JSONObject jsonObject = new JSONObject(response);
                                                            fcm = jsonObject.getString("fcm_token");
                                                            //requestQueue.add(send);
//                                                            Log.i("senders fcm", fcm);
                                                            Data_Message message = new Data_Message();
                                                            message.setRequest_type("duel_request");
                                                            message.setUser(senderUsername);
                                                            message.setStake(stake + "");
                                                            message.setReference_token(senderUserID);
                                                            FCMData fcmData = new FCMData();
                                                            fcmData.setData_message(message);
                                                            fcmData.setMessage_body("");
                                                            fcmData.setMessage_title("");
                                                            fcmData.setRegistration_id(fcm);
                                                            dialog.dismiss();
                                                            sendFCM(fcmData);
                                                        } catch (JSONException e) {
                                                            e.printStackTrace();
                                                        }
                                                    }
                                                },
                                                new Response.ErrorListener() {
                                                    @Override
                                                    public void onErrorResponse(VolleyError error) {

                                                    }
                                                }){
                                                @Override
                                                public Map<String, String> getHeaders() throws AuthFailureError {
                                                    Map<String, String> params = new HashMap<>();
                                                    params.put("x-auth",Endpoints.apikey);
                                                    return params;
                                                }
                                        };
                                        requestQueue.add(getFCM);
                                    }


                                }catch (Exception e)
                                {
                                    Toast.makeText(mActivity, "You entered an invalid value!", Toast.LENGTH_SHORT).show();
                                }

                        }
                    }});
                    reject.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            dialog.dismiss();
                        }
                    });
                    dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                    dialog.show();
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return users.size();
    }


    public void sendFCM(final FCMData fcmData) {
        fcmService.sendFCM(fcmData).enqueue(new Callback<JSONObject>() {


            @Override
            public void onResponse(Call<JSONObject> call, retrofit2.Response<JSONObject> response) {

                Log.i("Response Code",response.code()+"");
                if(response.isSuccessful())
                {
                    JSONObject object=response.body();
                    if(fcmData.getData_message().getRequest_type().equals("duel_request")) {
                        Toast.makeText(mActivity, "Challenge Sent", Toast.LENGTH_SHORT).show();
                    }

                }
                else
                {
                    if(response.code()==504){
                        JSONObject jsonObject = null;
                        try {
                            jsonObject = new JSONObject(response.errorBody().string());
                            String userMessage = jsonObject.getString("message");
                            Toast.makeText(mActivity,userMessage,Toast.LENGTH_SHORT).show();
                        } catch (Exception e) {
                            Toast.makeText(mActivity,"User is busy",Toast.LENGTH_SHORT).show();
                            e.printStackTrace();
                        }



                    }
                    else{
                        Toast.makeText(mActivity,"Response Failure "+response.message(),Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onFailure(Call<JSONObject> call, Throwable t) {
                Log.e("Error", "Unable to submit post to API."+ t.getMessage());
            }
        });
    }

}