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
                // Add action buttons
                .setPositiveButton("Accept", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        // sign in the user ...
                    }
                })
                .setNegativeButton("Reject", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {

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
                // Add action buttons
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        // sign in the user ...
                    }
                });

        AlertDialog dialog = builder.create();

        dialog.show();
    }
    private void showDialogReject(String user)
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setTitle("Accepted")
                .setMessage(user+" has rejected your challenge")
                // Add action buttons
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        // sign in the user ...
                    }
                });

        AlertDialog dialog = builder.create();

        dialog.show();
    }

}
