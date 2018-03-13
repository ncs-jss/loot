package com.example.dell.loot;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;

public class Duel_Alert_Transparent_Activity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main22);
        Intent intent=getIntent();
        String request_type=intent.getStringExtra("request_type");
        if(request_type.equals("duel_request")) {
            String from_user = intent.getStringExtra("user");
            String stake = intent.getStringExtra("stake");
            showDialogRequest(from_user,stake);

        }
        else if(request_type.equals("accept_request")) {
            String from_user = intent.getStringExtra("user");
            String stake = intent.getStringExtra("stake");
            showDialogAccept(from_user,stake);

        }
        else if(request_type.equals("reject_request"))
        {
            String from_user = intent.getStringExtra("user");
            showDialogReject(from_user);
        }
        else if(request_type.equals("won_message"))
        {
            String from_user = intent.getStringExtra("user");
            showDialogWon(from_user);
        }
        else if(request_type.equals("lost_message"))
        {
            String from_user = intent.getStringExtra("user");
            showDialogLost(from_user);
        }

    }

    private void showDialogLost(String from_user) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setTitle("You Lost")
                .setMessage(from_user+" won!")
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        finish();
                    }
                });

        AlertDialog dialog = builder.create();

        dialog.show();
    }

    private void showDialogWon(String from_user) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setTitle("You Won")
                .setMessage(from_user+" lost!")
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        finish();
                    }
                });

        AlertDialog dialog = builder.create();

        dialog.show();
    }

    private void showDialogRequest(String user,String stake)
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater layoutInflater = getLayoutInflater();
        View view=layoutInflater.inflate(R.layout.duel_alert,null);
        TextView from_user=view.findViewById(R.id.from_user);
        TextView user_stake=view.findViewById(R.id.user_stake);
        from_user.setText(user);
        user_stake.setText(stake);
        builder.setView(view)
                .setPositiveButton("Accept", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        //TODO: create Duel
                        //TODO: "send" Duel Accepted and duel_id
                        Intent i = new Intent(Duel_Alert_Transparent_Activity.this, MiniGame.class);
//                        i.putExtra("duel_id", );
                        i.putExtra("player_type", "opponent");
                        startActivity(i);
                    }
                })
                .setNegativeButton("Reject", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        //TODO: "send" Duel Rejected
                        finish();
                    }
                });

        AlertDialog dialog = builder.create();
        dialog.show();
    }
    private void showDialogAccept(String user,String stake)
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setTitle("Accepted")
                .setMessage(user+" has accepted your challenge for stake $"+stake)
                .setPositiveButton("Fight", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        Intent i = new Intent(Duel_Alert_Transparent_Activity.this, MiniGame.class);
//                        i.putExtra("duel_id", );
                        i.putExtra("player_type", "challenger");
                        startActivity(i);
                    }
                });

        AlertDialog dialog = builder.create();

        dialog.show();
    }
    private void showDialogReject(String user)
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setTitle("Rejected")
                .setMessage(user+" has rejected your challenge")
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        finish();
                    }
                });

        AlertDialog dialog = builder.create();
        dialog.show();
    }

}
