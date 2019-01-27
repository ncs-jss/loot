package com.example.dell.loot;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Looper;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.location.SettingsClient;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class Missions
        extends
        Fragment implements
        LocationListener,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,ResultCallback<LocationSettingsResult> {

//    static final int LOCATION_REQUEST_CODE = 1;
//    static final int REQUEST_CHECK_SETTINGS = 100;
    static final int STATE_LOCATE = 0, STATE_SOLVE = 1;



    /**
     * Code used in requesting runtime permissions.
     */
    private static final int REQUEST_PERMISSIONS_REQUEST_CODE = 34;

    /**
     * Constant used in the location settings dialog.
     */
    private static final int REQUEST_CHECK_SETTINGS = 0x1;

    /**
     * The desired interval for location updates. Inexact. Updates may be more or less frequent.
     */
    private static final long UPDATE_INTERVAL_IN_MILLISECONDS = 1000;

    /**
     * The fastest rate for active location updates. Exact. Updates will never be more frequent
     * than this value.
     */
    private static final long FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS =
            UPDATE_INTERVAL_IN_MILLISECONDS / 2;
    // Keys for storing activity state in the Bundle.
//    private final static String KEY_REQUESTING_LOCATION_UPDATES = "requesting-location-updates";
//    private final static String KEY_LOCATION = "location";
//    private final static String KEY_LAST_UPDATED_TIME_STRING = "last-updated-time-string";

    /**
     * Provides access to the Fused Location Provider API.
     */
    private FusedLocationProviderClient mFusedLocationClient;

    /**
     * Provides access to the Location Settings API.
     */
    private SettingsClient mSettingsClient;

    /**
     * Stores parameters for requests to the FusedLocationProviderApi.
     */
    private LocationRequest mLocationRequest;

    /**
     * Stores the types of location services the client is interested in using. Used for checking
     * settings to determine if the device has optimal location settings.
     */
    private LocationSettingsRequest mLocationSettingsRequest;

    /**
     * Callback for Location events.
     */
    private LocationCallback mLocationCallback;

    /**
     * Represents a geographical location.
     */
    private Location mCurrentLocation;


    /**
     * Tracks the status of the location updates request. Value changes when the user presses the
     * Start Updates and Stop Updates buttons.
     */
    private Boolean mRequestingLocationUpdates;

    /**
     * Time when the location was updated represented as a String.
     */
    private String mLastUpdateTime;



    TextView dan_msg;
    AlertDialog alertDialog;
    Mission mission;
    int userStage, state, dropCount, score;
    String userID;
    Vibrator vibrator;
    GoogleApiClient googleApiClient;
//    LocationRequest locationRequest;
//    FirebaseDatabase database;
//    DatabaseReference users, missions;
    ProgressDialog progressDialog;
    LootApplication app;
    RequestQueue requestQueue;
    SharedPreferences sharedPreferences;
    int triedAttempts=0;
//    LocationRequest mLocationRequest;

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

        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        vibrator = (Vibrator) getActivity().getSystemService(Context.VIBRATOR_SERVICE);
        progressDialog = new ProgressDialog(getContext());
        progressDialog.setMessage("Loading...");
        progressDialog.show();
        googleApiClient = new GoogleApiClient.Builder(getContext())
                .addApi(LocationServices.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();

        mRequestingLocationUpdates = false;
        mLastUpdateTime = "";

        // Update values using data stored in the Bundle.
//        updateValuesFromBundle(savedInstanceState);

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(getActivity());
        mSettingsClient = LocationServices.getSettingsClient(getActivity());

//        createLocationCallback();
        createLocationRequest();
        buildLocationSettingsRequest();


//        switch (state) {
//            case STATE_LOCATE:
//                checkPermission();
//                googleApiClient = new GoogleApiClient.Builder(getContext())
//                        .addApi(LocationServices.API)
//                        .addConnectionCallbacks(this)
//                        .addOnConnectionFailedListener(this)
//                        .build();
//                if (!googleApiClient.isConnected()) {
//                    googleApiClient.connect();
//                }
//                break;
//            case STATE_SOLVE:
//                if (googleApiClient.isConnected()) {
//                    googleApiClient.disconnect();
//                }
////
//                break;
//        }
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

                        Toast.makeText(getContext(), "Error in fetching data!", Toast.LENGTH_SHORT).show();
                        if(triedAttempts<5) {
                            getFragmentManager().beginTransaction().detach(Missions.this).attach(Missions.this).commit();
                            triedAttempts++;
                        }
                        else
                        {
                            Toast.makeText(getContext(), "Error in fetching data!", Toast.LENGTH_SHORT).show();
                            getActivity().finishAffinity();
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
//                checkPermission();
                if (!googleApiClient.isConnected()) {
                    googleApiClient.connect();
                }
//                startLocationUpdates();
                dan_msg.setText(mission.getStory());
                break;
            case STATE_SOLVE:
                if (googleApiClient.isConnected()) {
                    googleApiClient.disconnect();
                }
//                stopLocationUpdates();
                loadFragment(new Current_missions(), "current_mission");
                break;
        }
    }

    @Override
    public void onPause() {
        if (googleApiClient.isConnected()) {
            googleApiClient.disconnect();
        }
        super.onPause();
    }

    @SuppressLint("MissingPermission")
    @Override
    public void onConnected(@Nullable Bundle bundle) {
//        locationRequest = LocationRequest.create();
//        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
//        locationRequest.setInterval(1000);
//        checkPermission();
//
//        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
//                .addLocationRequest(locationRequest);
//        builder.setAlwaysShow(true);
        PendingResult<LocationSettingsResult> result = LocationServices.SettingsApi.
                checkLocationSettings(googleApiClient,mLocationSettingsRequest);
        result.setResultCallback(this);


        LocationServices.FusedLocationApi.
                requestLocationUpdates(googleApiClient, mLocationRequest, this);
    }

    @Override
    public void onConnectionSuspended(int i) {
        Toast.makeText(getContext(), "Connection Suspended!", Toast.LENGTH_SHORT).show();
        checkPermissions();
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Toast.makeText(getContext(), "Connection Failed!", Toast.LENGTH_SHORT).show();
        checkPermissions();
    }

    @Override
    public void onLocationChanged(Location location) {

        checkDistance(location);

    }

    @Override
    public void onResult(@NonNull LocationSettingsResult locationSettingsResult) {
        Status status = locationSettingsResult.getStatus();
        switch (status.getStatusCode()) {
            case LocationSettingsStatusCodes.SUCCESS:
                // NO need to show the dialog;
//                if (googleApiClient != null || googleApiClient.isConnected() == false) {
//                    connectAPIClient();
//                }
                startLocationUpdates();
                break;
            case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                try {
                    status.startResolutionForResult(getActivity(), REQUEST_CHECK_SETTINGS);
                } catch (IntentSender.SendIntentException e) {
                    e.printStackTrace();
                }
                break;
            case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                // Location settings are unavailable so not possible to show any dialog now
                break;
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (googleApiClient.isConnected()) {
            googleApiClient.disconnect();
        }
    }

    @Override
    public void onStop() {
        if (googleApiClient.isConnected()) {
            googleApiClient.disconnect();
        }
        super.onStop();

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
//        Toast.makeText(getContext(), location.distanceTo(missionLocation)+"", Toast.LENGTH_SHORT).show();
        if (location.distanceTo(missionLocation) < 5) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                if (googleApiClient.isConnected()) {
                    googleApiClient.disconnect();
                }
//                stopLocationUpdates();

            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                vibrator.vibrate(VibrationEffect.createWaveform(new long[]{0, 250, 200, 250, 150, 150, 75, 150, 75, 150}, -1));
            } else {
                vibrator.vibrate(3000);
            }

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


    private void createLocationRequest() {
        mLocationRequest = new LocationRequest();

        mLocationRequest.setInterval(UPDATE_INTERVAL_IN_MILLISECONDS);

        mLocationRequest.setFastestInterval(FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS);

        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }

//    private void createLocationCallback() {
//        mLocationCallback = new LocationCallback() {
//            @Override
//            public void onLocationResult(LocationResult locationResult) {
//                super.onLocationResult(locationResult);
//
//                mCurrentLocation = locationResult.getLastLocation();
//                Log.i("Location","Changed");
//                checkDistance(mCurrentLocation);
//            }
//        };
//    }


    private void buildLocationSettingsRequest() {
        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder();
        builder.addLocationRequest(mLocationRequest);
        mLocationSettingsRequest = builder.build();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            // Check for the integer request code originally supplied to startResolutionForResult().
            case REQUEST_CHECK_SETTINGS:
                switch (resultCode) {
                    case Activity.RESULT_OK:

                        startLocationUpdates();
                        Log.i("Missions", "User agreed to make required location settings changes.");
                        // Nothing to do. startLocationupdates() gets called in onResume again.
                        break;
                    case Activity.RESULT_CANCELED:
                        Log.i("Missions", "User chose not to make required location settings changes.");
                        mRequestingLocationUpdates = false;
                        break;
                }
                break;
        }
    }

    private void startLocationUpdates() {
        // Begin by checking if the device has the necessary location settings.
        mSettingsClient.checkLocationSettings(mLocationSettingsRequest)
                .addOnSuccessListener(getActivity(), new OnSuccessListener<LocationSettingsResponse>() {
                    @SuppressLint("MissingPermission")
                    @Override
                    public void onSuccess(LocationSettingsResponse locationSettingsResponse) {
                        Log.i("Missions", "All location settings are satisfied.");

                        //noinspection MissingPermission
                        Log.i("Location","Started");
//                        mFusedLocationClient.requestLocationUpdates(mLocationRequest,
//                                mLocationCallback, Looper.myLooper());
                        googleApiClient.connect();

                    }
                })
                .addOnFailureListener(getActivity(), new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        int statusCode = ((ApiException) e).getStatusCode();
                        switch (statusCode) {
                            case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                                Log.i("Missions", "Location settings are not satisfied. Attempting to upgrade " +
                                        "location settings ");
                                try {
                                    // Show the dialog by calling startResolutionForResult(), and check the
                                    // result in onActivityResult().
                                    ResolvableApiException rae = (ResolvableApiException) e;
                                    rae.startResolutionForResult(getActivity(), REQUEST_CHECK_SETTINGS);
                                } catch (IntentSender.SendIntentException sie) {
                                    Log.i("Missions", "PendingIntent unable to execute request.");
                                }
                                break;
                            case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                                String errorMessage = "Location settings are inadequate, and cannot be " +
                                        "fixed here. Fix in Settings.";
                                Log.e("Mission", errorMessage);
                                Toast.makeText(getActivity(), errorMessage, Toast.LENGTH_LONG).show();
                                mRequestingLocationUpdates = false;
                        }

                        startLocationUpdates();
                    }
                });
    }

    @Override
    public void onResume() {
        super.onResume();
        if (mRequestingLocationUpdates && checkPermissions()) {
            Log.i("OnResume","check");
            startLocationUpdates();
        } else if (!checkPermissions()) {
            requestPermissions();
        }
        else
        {
//            startLocationUpdates();
            getMissions();
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


    private boolean checkPermissions() {
        int permissionState = ActivityCompat.checkSelfPermission(getActivity(),
                Manifest.permission.ACCESS_FINE_LOCATION);
        return permissionState == PackageManager.PERMISSION_GRANTED;
    }

    private void requestPermissions() {
        boolean shouldProvideRationale =
                ActivityCompat.shouldShowRequestPermissionRationale(getActivity(),
                        Manifest.permission.ACCESS_FINE_LOCATION);
    if (shouldProvideRationale) {
            Log.i("Missions", "Displaying permission rationale to provide additional context.");
            showSnackbar("You need to provide Location Permissions",
                    "OK", new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            // Request permission
                            ActivityCompat.requestPermissions(getActivity(),
                                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                                    REQUEST_PERMISSIONS_REQUEST_CODE);
                        }
                    });
        } else {
            Log.i("Missions:", "Requesting permission");
            ActivityCompat.requestPermissions(getActivity(),
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    REQUEST_PERMISSIONS_REQUEST_CODE);
//            startLocationUpdates();
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        Log.i("Missions:", "onRequestPermissionResult");
        if (requestCode == REQUEST_PERMISSIONS_REQUEST_CODE) {
            if (grantResults.length <= 0) {
                Log.i("Missions:", "User interaction was cancelled.");
            } else if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                if (mRequestingLocationUpdates) {
                    Log.i("Missions:", "Permission granted, updates requested, starting location updates");
                    startLocationUpdates();
                }
            } else {
                showSnackbar("You need to provide location updates",
                        "Settings", new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                // Build intent that displays the App settings screen.
                                Intent intent = new Intent();
                                intent.setAction(
                                        Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                                Uri uri = Uri.fromParts("package",
                                        BuildConfig.APPLICATION_ID, null);
                                intent.setData(uri);
                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                startActivity(intent);
                            }
                        });
            }
        }
    }
}



//    private void checkPermission() {
//        if (ActivityCompat.checkSelfPermission(getContext(), android.Manifest.permission.ACCESS_FINE_LOCATION) !=
//                PackageManager.PERMISSION_GRANTED ||
//                ActivityCompat.checkSelfPermission(getContext(), android.Manifest.permission.ACCESS_COARSE_LOCATION) !=
//                        PackageManager.PERMISSION_GRANTED) {
//            ActivityCompat.requestPermissions(getActivity(),
//                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION, android.Manifest.permission.ACCESS_COARSE_LOCATION},
//                    LOCATION_REQUEST_CODE);
//        } else {
//            if (googleApiClient != null || googleApiClient.isConnected() == false) {
//                connectAPIClient();
//            }
//        }
//    }
//
//    @Override
//    public void onRequestPermissionsResult(int requestCode,
//                                           String permissions[], int[] grantResults) {
//        if (requestCode == LOCATION_REQUEST_CODE) {
//            if (!(grantResults.length > 0
//                    && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
//                Toast.makeText(getContext(), "Permission not granted!", Toast.LENGTH_SHORT).show();
////                if (googleApiClient != null || googleApiClient.isConnected() == false) {
////                    connectAPIClient();
////                }
//                startLocationUpdates();
//            } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
//                if (shouldShowRequestPermissionRationale(Manifest.permission.ACCESS_FINE_LOCATION)) {
//                    new AlertDialog.Builder(getContext())
//                            .setMessage("The app needs to access device's location to function properly!")
//                            .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
//                                @Override
//                                public void onClick(DialogInterface dialogInterface, int i) {
//                                    checkPermission();
//                                }
//                            })
//                            .setNegativeButton("Cancel", null)
//                            .create()
//                            .show();
//                }
//            }
//        }
//    }
////    public void connectAPIClient() {
//        googleApiClient = new GoogleApiClient.Builder(getContext())
//                .addApi(LocationServices.API)
//                .addConnectionCallbacks(this)
//                .addOnConnectionFailedListener(this)
//                .build();
//
//    }

//    private void updateValuesFromBundle(Bundle savedInstanceState) {
//        if (savedInstanceState != null) {
//
//            if (savedInstanceState.keySet().contains(KEY_REQUESTING_LOCATION_UPDATES)) {
//                mRequestingLocationUpdates = savedInstanceState.getBoolean(
//                        KEY_REQUESTING_LOCATION_UPDATES);
//            }
//
//            if (savedInstanceState.keySet().contains(KEY_LOCATION)) {
//                mCurrentLocation = savedInstanceState.getParcelable(KEY_LOCATION);
//            }
//
//            if (savedInstanceState.keySet().contains(KEY_LAST_UPDATED_TIME_STRING)) {
//                mLastUpdateTime = savedInstanceState.getString(KEY_LAST_UPDATED_TIME_STRING);
//            }
//        }
//    }

///    private void stopLocationUpdates() {
////        if (!mRequestingLocationUpdates) {
////            Log.d("Missions", "stopLocationUpdates: updates never requested, no-op.");
////            return;
////        }
//
//        mFusedLocationClient.removeLocationUpdates(mLocationCallback)
//                .addOnCompleteListener(getActivity(), new OnCompleteListener<Void>() {
//                    @Override
//                    public void onComplete(@NonNull Task<Void> task) {
//                        if(task.isSuccessful()==false)
//                        Log.i("Result",task.getException().getMessage());
//                        else
//                            Log.i("Result","Stopped");
//
//                        mRequestingLocationUpdates = false;
//                    }
//                });
//    }


//    @Override
//    public void onPause() {
//        super.onPause();
//        Log.i("Paused","Paused");
//        stopLocationUpdates();
//    }


//    public void onSaveInstanceState(Bundle savedInstanceState) {
//        savedInstanceState.putBoolean(KEY_REQUESTING_LOCATION_UPDATES, mRequestingLocationUpdates);
//        savedInstanceState.putParcelable(KEY_LOCATION, mCurrentLocation);
//        savedInstanceState.putString(KEY_LAST_UPDATED_TIME_STRING, mLastUpdateTime);
//        super.onSaveInstanceState(savedInstanceState);
//    }
