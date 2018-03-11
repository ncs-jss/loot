package com.example.dell.loot;

/**
 * Created by shobhit on 3/9/2018.
 */
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class CustomRecycleAdapter extends RecyclerView.Adapter<CustomRecycleAdapter.MyViewHolder> {

    private Context mContext;
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


    public CustomRecycleAdapter(Context mContext, ArrayList<User> users) {
        this.mContext = mContext;
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
        User user = users.get(position);
        holder.name.setText(user.getName());
        holder.coins.setText("Coins $"+user.getScore());
        holder.avatar.setImageResource(R.drawable.avatar);
    }

    @Override
    public int getItemCount() {
        return users.size();
    }
}