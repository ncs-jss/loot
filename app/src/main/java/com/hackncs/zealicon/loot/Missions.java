package com.hackncs.zealicon.loot;

import android.Manifest;

import android.app.Activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;

import android.os.Build;
import android.os.Bundle;
import android.os.VibrationEffect;
import android.os.Vibrator;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.SettingsClient;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.snackbar.Snackbar;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class Missions
        extends
        Fragment {

    private FusedLocationProviderClient fusedLocationClient;
    private Location mCurrentLocation;
    private LocationCallback locationCallback;
    private LocationRequest locationRequest;
    private boolean requestingLocationUpdates;

    static final int STATE_LOCATE = 0, STATE_SOLVE = 1;
    TextView dan_msg;
    Mission mission;
    int userStage, state, dropCount, score;
    String userID;
    Vibrator vibrator;

    ProgressDialog progressDialog;
    LootApplication app;
    RequestQueue requestQueue;
    SharedPreferences sharedPreferences;
    int triedAttempts = 0;

    public Missions() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_missions, container, false);
        dan_msg = view.findViewById(R.id.dan_msg);
        BottomNavigationView navigationView = getActivity().findViewById(R.id.bottom_nav);
        navigationView.getMenu().getItem(1).setChecked(true);

        sharedPreferences = getActivity().getSharedPreferences("LootPrefs", Context.MODE_PRIVATE);
        userStage = sharedPreferences.getInt("com.hackncs.stage", 1);
        state = sharedPreferences.getInt("com.hackncs.state", 0);
        dropCount = sharedPreferences.getInt("com.hackncs.dropCount", 0);
        score = sharedPreferences.getInt("com.hackncs.score", 0);
        userID = sharedPreferences.getString("com.hackncs.userID", null);
        requestQueue = Volley.newRequestQueue(getContext());
        Log.i("State",state+"");

        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        vibrator = (Vibrator) getActivity().getSystemService(Context.VIBRATOR_SERVICE);
        progressDialog = new ProgressDialog(getContext());
        progressDialog.setMessage("Loading...");
        progressDialog.show();
        requestingLocationUpdates = false;
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(getActivity());
        getMissions();
        createLocationRequest();
        locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                if (locationResult == null) {
                    return;
                }
                for (Location location : locationResult.getLocations()) {
                    // Update UI with location data
                    // ...
                    if(mission!=null)
                    checkDistance(location);
//                    Toast.makeText(getActivity(), location.getLatitude() + " " + location.getLongitude(), Toast.LENGTH_LONG).show();
                }
            }

            ;
        };
    }
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case 1: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.
                    startLocationUpdates();
                } else {
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                    getActivity().finish();
                }
                return;
            }

            // other 'case' lines to check for other
            // permissions this app might request.
        }
    }



    protected void createLocationRequest() {
        locationRequest = LocationRequest.create();
        locationRequest.setInterval(1000);
        locationRequest.setFastestInterval(500);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                .addLocationRequest(locationRequest);

// ...

        SettingsClient client = LocationServices.getSettingsClient(getActivity());
        Task<LocationSettingsResponse> task = client.checkLocationSettings(builder.build());
        task.addOnSuccessListener(getActivity(), new OnSuccessListener<LocationSettingsResponse>() {
            @Override
            public void onSuccess(LocationSettingsResponse locationSettingsResponse) {
                // All location settings are satisfied. The client can initialize
                // location requests here.
                // ...
                requestingLocationUpdates = true;
                startLocationUpdates();
            }
        });

        task.addOnFailureListener(getActivity(), new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                if (e instanceof ResolvableApiException) {
                    // Location settings are not satisfied, but this can be fixed
                    // by showing the user a dialog.
                    try {
                        // Show the dialog by calling startResolutionForResult(),
                        // and check the result in onActivityResult().
                        ResolvableApiException resolvable = (ResolvableApiException) e;
                        resolvable.startResolutionForResult(getActivity(),
                                2);
                    } catch (IntentSender.SendIntentException sendEx) {
                        // Ignore the error.
                    }
                }
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 2) {
            switch (resultCode) {
                case Activity.RESULT_OK:

                    startLocationUpdates();
                    Log.i("Missions", "User agreed to make required location settings changes.");
                    // Nothing to do. startLocationupdates() gets called in onResume again.
                    break;
                case Activity.RESULT_CANCELED:
                    Log.i("Missions", "User chose not to make required location settings changes.");
                    requestingLocationUpdates = false;
                    break;
            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (requestingLocationUpdates) {
            startLocationUpdates();
        }
    }

    private void startLocationUpdates() {
        if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(),
                    Manifest.permission.ACCESS_FINE_LOCATION)) {
                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.
                Toast.makeText(getActivity(), "Provide Location Permissions to Continue", Toast.LENGTH_SHORT).show();
                ActivityCompat.requestPermissions(getActivity(),
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        1);
            } else {
                // No explanation needed; request the permission
                ActivityCompat.requestPermissions(getActivity(),
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        1);

                // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
                // app-defined int constant. The callback method gets the
                // result of the request.
            }
        } else {
            fusedLocationClient.requestLocationUpdates(locationRequest,
                    locationCallback,
                    null /* Looper */);
        }
    }
    private void getMissions()
    {   Log.i("Stage",String.valueOf(userStage));
        StringRequest fetchMission = new StringRequest(Request.Method.GET,
                Endpoints.fetchMission + userStage,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        JSONObject jsonObject;
                        mission = new Mission();
                        try {
                            jsonObject = new JSONObject(response);
                            mission.setMissionID(jsonObject.getInt("id"));
                            mission.setMissionName(jsonObject.getString("mission_name"));
                            mission.setStory(jsonObject.getString("story"));
                            mission.setDescription(jsonObject.getString("description"));
                            String g = jsonObject.getString("geocode");
                            mission.setLat(Double.valueOf(g.substring(0, g.indexOf(" "))));
                            mission.setLng(Double.valueOf(g.substring(g.indexOf(" "))));
                            mission.setAnswer(jsonObject.getString("answer"));
                            displayMission();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        progressDialog.dismiss();
                    }
                },
                new Response.ErrorListener() {
                    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        progressDialog.dismiss();
//                        Log.i("error",error.getMessage());
                        Toast.makeText(getContext(), "Slow  Network...Error in fetching data!", Toast.LENGTH_SHORT).show();
                        if(triedAttempts<5) {
                            getFragmentManager().beginTransaction().detach(Missions.this).attach(Missions.this).commit();
                            triedAttempts++;
                        }
                        else
                        {
                            Toast.makeText(getContext(), "Slow Network Connectivity.. Restart the App when connected", Toast.LENGTH_SHORT).show();
                            Intent i = new Intent(getActivity(), WelcomeSlider.class);
                            startActivity(i);
                        }
                    }
                }){
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("x-auth",Endpoints.apikey);
                return params;
            }
        };
        requestQueue.add(fetchMission);


    }

    private void displayMission() {
        switch (state) {
            case STATE_LOCATE:
                dan_msg.setText(mission.getStory());
                break;
            case STATE_SOLVE:
                stopLocationUpdates();
               loadFragment(new Current_missions(), "current_mission");
                break;
        }
    }
    private void loadFragment(Fragment fragment, String tag) {
        Bundle bundle = new Bundle();
        bundle.putInt("missionID", mission.getMissionID());
        bundle.putString("story", mission.getStory());
        bundle.putString("description", mission.getDescription());
        bundle.putString("missionName", mission.getMissionName());
        bundle.putString("answer", mission.getAnswer());
        bundle.putDouble("lat", mission.getLat());
        bundle.putDouble("lng", mission.getLng());
        fragment.setArguments(bundle);
        FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.frame_container, fragment, tag);
        transaction.addToBackStack(null);
        transaction.commit();
    }

    private void checkDistance(Location location) {
        Location missionLocation = new Location("");
        missionLocation.setLatitude(mission.getLat());
        missionLocation.setLongitude(mission.getLng());
        Log.i("Lat",String.valueOf(location.getLatitude()));
        Log.i("Lon",String.valueOf(location.getLongitude()));
        Log.i("MLat",String.valueOf(mission.getLat()));
        Log.i("MLon",String.valueOf(mission.getLng()));
//        Toast.makeText(getContext(), location.distanceTo(missionLocation)+"", Toast.LENGTH_SHORT).show();
        if (location.distanceTo(missionLocation) < 5) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

                stopLocationUpdates();

            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                vibrator.vibrate(VibrationEffect.createWaveform(new long[]{0, 250, 200, 250, 150, 150, 75, 150, 75, 150}, -1));
            } else {
                vibrator.vibrate(3000);
            }
            Log.i("Distance",location.distanceTo(missionLocation)+"");

            state = 1;
            StringRequest updateUser = new StringRequest(Request.Method.POST,
                    Endpoints.updateUser + userID + "/edit/",
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            Log.d("updateVolley", "response");
                            SharedPreferences.Editor editor = sharedPreferences.edit();
                            editor.putInt("com.hackncs.state", state);
                            editor.apply();
//                            getFragmentManager().beginTransaction().detach(Missions.this).attach(Missions.this).commit();
                            loadFragment(new Current_missions(), "current_mission");
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            Log.d("updateVolley", error.getMessage());
                        }
                    }) {
                @Override
                protected Map<String, String> getParams() throws AuthFailureError {
                    Map map = new HashMap();
                    map.put("mission_state", "true");
                    return map;
                }
                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    Map<String, String> params = new HashMap<>();
                    params.put("x-auth",Endpoints.apikey);
                    return params;
                }
            };
            requestQueue.add(updateUser);
        }
    }
    private void showSnackbar(String mainTextStringId, String actionStringId,
                              View.OnClickListener listener) {
        Snackbar.make(
                getActivity().findViewById(android.R.id.content),
                mainTextStringId,
                Snackbar.LENGTH_INDEFINITE)
                .setAction(actionStringId, listener).show();
    }
    @Override
    public void onPause() {
        super.onPause();
        stopLocationUpdates();
    }

    private void stopLocationUpdates() {
        fusedLocationClient.removeLocationUpdates(locationCallback);
    }
}