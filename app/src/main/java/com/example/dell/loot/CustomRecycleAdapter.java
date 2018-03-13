package com.example.dell.loot;

/**
 * Created by shobhit on 3/9/2018.
 */
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.toolbox.StringRequest;

import java.util.ArrayList;
import java.util.List;

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
        final User user = users.get(position);
        holder.name.setText(user.getUsername());
        holder.coins.setText("Coins $"+user.getScore());
        //TODO: set avatar
//        holder.avatar.setImageResource();
        holder.name.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
                LayoutInflater layoutInflater = mActivity.getLayoutInflater();
                View dialogView = layoutInflater.inflate(R.layout.create_challenge,null);
                TextView toUser = dialogView.findViewById(R.id.to_user);
                toUser.setText(user.getUsername());
                builder.setView(view)
                        .setPositiveButton("Send", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int id) {
                                //TODO: "send" Duel Challenge
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