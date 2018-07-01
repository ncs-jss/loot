package com.example.dell.loot;

import android.graphics.Color;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import java.util.HashMap;
import java.util.Map;



public class GridTap extends Fragment implements View.OnClickListener {

    View view;
    GridLayout gridLayout;
    Button b[][];
    Bundle savedInstanceState;
    ClockCountdown clockCountdown;
    TileCountdown tileCountdown;
    DelayCountdown delayCountdown;
    MediaPlayer mediaPlayer;
    TextView counterText, timerText;
    Bundle args;
    int avatarIds[]=new int[5];
    int random;
    boolean finish;

    int buttonsID[][] = {
            {R.id.b00, R.id.b01, R.id.b02, R.id.b03, R.id.b04},
            {R.id.b10, R.id.b11, R.id.b12, R.id.b13, R.id.b14},
            {R.id.b20, R.id.b21, R.id.b22, R.id.b23, R.id.b24},
            {R.id.b30, R.id.b31, R.id.b32, R.id.b33, R.id.b34},
            {R.id.b40, R.id.b41, R.id.b42, R.id.b43, R.id.b44}
    };
    int redLocation, tappedLocation, counter = 0, seconds = 30, tileChangeInterval = 600, x, y;

    public GridTap() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.savedInstanceState = savedInstanceState;
        b = new Button[5][5];

        avatarIds[0]=R.drawable.avatar_1;
        avatarIds[1]=R.drawable.avatar_2;
        avatarIds[2]=R.drawable.avatar_3;
        avatarIds[3]=R.drawable.avatar_4;
        avatarIds[4]=R.drawable.avatar_5;


        clockCountdown = new ClockCountdown(seconds * 1000, 1000);
        tileCountdown = new TileCountdown(seconds * 1000, tileChangeInterval);
        delayCountdown = new DelayCountdown(5000, 1000);
        x = (int)(Math.random() * 5);
        y = (int)(Math.random() * 5);
        redLocation = (x*10) + (y+1);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_grid_tap, container, false);
        initializeViews();
        delayCountdown.start();
        Toast.makeText(getContext(), "Get ready to Loot", Toast.LENGTH_SHORT).show();
        mediaPlayer = MediaPlayer.create(getContext(), R.raw.minigame);
        mediaPlayer.setLooping(false);
        mediaPlayer.start();
        args=getArguments();
        return view;
    }

    private void initializeViews() {
        gridLayout = view.findViewById(R.id.gridLayout);
        timerText = view.findViewById(R.id.timer);
        counterText = view.findViewById(R.id.counter);
        for (int i = 0; i < 5; i++) {
            for (int j = 0; j < 5; j++) {
                b[i][j] = view.findViewById(buttonsID[i][j]);
                b[i][j].setTag(String.valueOf((i*10)+(j+1)));
                b[i][j].setBackgroundColor(Color.TRANSPARENT);
                b[i][j].setOnClickListener(this);
            }
        }
    }

    @Override
    public void onClick(View view) {
        if(!finish) {
            tappedLocation = Integer.valueOf(view.getTag().toString());
            updateCounter();
        }
        else
        {
            Toast.makeText(getActivity(),"Please wait",Toast.LENGTH_SHORT).show();
        }
    }

    private void updateCounter() {
        if (tappedLocation == redLocation) {
            counter++;
        } else {
            counter--;
        }
        counterText.setText(String.valueOf(counter));
    }

    private void updateRedTile() {

        b[x][y].setBackgroundColor(Color.TRANSPARENT);
       x = (int)(Math.random() * 5);
        y = (int)(Math.random() * 5);
        redLocation = (x*10) + (y+1);
          random=random%5+1;

        b[x][y].setBackgroundResource(avatarIds[random-1]);

    }

    class ClockCountdown extends CountDownTimer {

        public ClockCountdown(long millisInFuture, long countDownInterval) {
            super(millisInFuture, countDownInterval);
        }

        @Override
        public void onTick(long l) {
            timerText.setText("00:" + String.valueOf(--seconds));
        }

        @Override
        public void onFinish() {
            tileCountdown.cancel();
            finish=true;
            b[x][y].setBackgroundColor(Color.TRANSPARENT);
            StringRequest updateTapCount = new StringRequest(Request.Method.POST,
                    Endpoints.duel + args.get("duel_id") + "/edit/",
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            Toast.makeText(getContext(), "Stay tuned for the results!", Toast.LENGTH_SHORT).show();
                            getActivity().finish();
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            Toast.makeText(getContext(), "Error"+ error.getMessage() , Toast.LENGTH_SHORT).show();;
                        }
                    }){
                @Override
                protected Map<String, String> getParams() throws AuthFailureError {
                    Map map = new HashMap();
//                    if (args.get("player_type").equals("challenger")) {
                    Log.i("Player_type",getActivity().getIntent().getStringExtra("player_type"));
                    if (getActivity().getIntent().getStringExtra("player_type").equals("challenger")) {
                        map.put("challenger_tap_count", String.valueOf(counter));
                    } else {
                        map.put("opponent_tap_count", String.valueOf(counter));
                    }
                    return map;
                }
            };
            RequestQueue requestQueue = Volley.newRequestQueue(getContext());
            requestQueue.add(updateTapCount);
        }
    }

    class TileCountdown extends CountDownTimer {

        public TileCountdown(long millisInFuture, long countDownInterval) {
            super(millisInFuture, countDownInterval);
        }

        @Override
        public void onTick(long l) {
            updateRedTile();
        }

        @Override
        public void onFinish() {

            mediaPlayer.stop();
        }
    }

    class DelayCountdown extends CountDownTimer {

        public DelayCountdown(long millisInFuture, long countDownInterval) {
            super(millisInFuture, countDownInterval);
        }

        @Override
        public void onTick(long l) {
            //TODO: update text below
//            Toast.makeText(getContext(), "Get ready to Loot", Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onFinish() {

            random=random%5+1;
            b[x][y].setBackgroundResource(avatarIds[random-1]);
            clockCountdown.start();
            tileCountdown.start();
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        if(mediaPlayer.isPlaying())
        {
            mediaPlayer.stop();
        }
    }
}