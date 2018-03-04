package com.example.dell.loot;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.hardware.Camera;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.os.Vibrator;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

public class Locator extends Fragment implements
        LocationListener,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener {

    final private int CAMERA_REQUEST_CODE = 1;
    final private int LOCATION_REQUEST_CODE = 2;
    Camera camera;
    private static final String TAG = "LocationActivity";
    private static final long INTERVAL = 1000;
//    private static final long FASTEST_INTERVAL = 1000 * 5;
    private LootApplication app;
    private GoogleApiClient googleApiClient;
    private LocationRequest mLocationRequest;
    int minDistI;
    double maxDist;
    FirebaseDatabase database;
    DatabaseReference users;
    private FusedLocationProviderClient mFusedLocationClient;
    Vibrator vibrator;
    ProgressBar hot_cold;
    ArrayList<Location> geocodes;
    ArrayList<Mission> missions_left;

    public Locator() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.i("Locator","createView");
        View view = inflater.inflate(R.layout.fragment_locator, container, false);
        checkPermission();
        vibrator = (Vibrator) getActivity().getSystemService(Context.VIBRATOR_SERVICE);
        hot_cold = view.findViewById(R.id.hot_cold);
        return view;
    }

    @Override
    public void onAttach(Context context) {
        Log.i("Locator","attach");
        super.onAttach(context);
    }

    @Override
    public void onDetach() {
        Log.i("Locator","detach");
        super.onDetach();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        checkPermission();
        app = (LootApplication) getActivity().getApplication();
        googleApiClient = new GoogleApiClient.Builder(getContext())
                .addApi(LocationServices.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();
        database = FirebaseDatabase.getInstance();
        users = database.getReference("Users");
    }

    @Override
    public void onResume() {
        Log.i("Locator","Resume");
        super.onResume();
        updateGeocode();
        startCameraUpdates();
    }

    protected void startCameraUpdates() {
        FrameLayout camera_frame = getActivity().findViewById(R.id.camera_frame);
        camera = getCameraInstance();
        CameraPreview preview = new CameraPreview(getContext(), camera);
        camera_frame.addView(preview, 0);
    }

    public Camera getCameraInstance() {
        Camera c = null;
        try {
            c = Camera.open();
            if (c == null) {
                checkPermission();
                c = Camera.open();
            }
        } catch (Exception e) {
            Toast.makeText(getContext(), "Unable to Open Camera", Toast.LENGTH_SHORT).show();
        }
        return c;
    }

    @Override
    public void onPause() {
        Log.i("Locator","pause");
        super.onPause();
        stopCameraUpdates();
//        stopLocationUpdates();
    }

    private void stopCameraUpdates() {
        if (camera != null) {
            camera.stopPreview();
            camera.release();
        }
    }

    private void checkPermission() {
        if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.CAMERA) ==
                PackageManager.PERMISSION_DENIED) {
            ActivityCompat.requestPermissions(getActivity(),
                    new String[]{Manifest.permission.CAMERA},
                    CAMERA_REQUEST_CODE);
        }

        if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) !=
                PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) !=
                        PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(getActivity(),
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION},
                    LOCATION_REQUEST_CODE);
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        if (requestCode == CAMERA_REQUEST_CODE) {
            if (grantResults.length > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(getContext(), "Permission Granted", Toast.LENGTH_SHORT).show();
            }
            else {
                Toast.makeText(getContext(), "Please provide the required Permissions", Toast.LENGTH_SHORT).show();
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    if (shouldShowRequestPermissionRationale(Manifest.permission.CAMERA)) {
                        showMessageOKCancel("You need to allow access camera permissions",
                                new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                            checkPermission();
                                        }
                                    }
                                });
                    }
                }
            }
        }
        if (requestCode == LOCATION_REQUEST_CODE) {
            if (grantResults.length > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(getContext(), "Permission Granted", Toast.LENGTH_SHORT).show();
            }
            else
                Toast.makeText(getContext(), "Please provide the required Permissions", Toast.LENGTH_SHORT).show();
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    if (shouldShowRequestPermissionRationale(Manifest.permission.CAMERA)) {
                        showMessageOKCancel("You need to allow Location  permissions",
                                new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                            checkPermission();
                                        }
                                    }
                                });
                    }
                }
            }
    }

    private void showMessageOKCancel(String message, DialogInterface.OnClickListener okListener) {
        new AlertDialog.Builder(getContext())
                .setMessage(message)
                .setPositiveButton("OK", okListener)
                .setNegativeButton("Cancel", null)
                .create()
                .show();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

    }


//    @SuppressLint("MissingPermission")
//    protected void startLocationUpdates() {
//
//        // Create the location request to start receiving updates
//        mLocationRequest = new LocationRequest();
//        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
//        mLocationRequest.setInterval(INTERVAL);
//        mLocationRequest.setFastestInterval(FASTEST_INTERVAL);
//
//        // Create LocationSettingsRequest object using location request
//        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder();
//        builder.addLocationRequest(mLocationRequest);
//        LocationSettingsRequest locationSettingsRequest = builder.build();
//
//        // Check whether location settings are satisfied
//        // https://developers.google.com/android/reference/com/google/android/gms/location/SettingsClient
//        SettingsClient settingsClient = LocationServices.getSettingsClient(getActivity());
//        settingsClient.checkLocationSettings(locationSettingsRequest);
//
//
//        Task<LocationSettingsResponse> task = settingsClient.checkLocationSettings(builder.build());
//
//        task.addOnSuccessListener(getActivity(), new OnSuccessListener<LocationSettingsResponse>() {
//            @Override
//            public void onSuccess(LocationSettingsResponse locationSettingsResponse) {
//                // All location settings are satisfied. The client can initialize
//                // location requests here.
//                // ...
//
//                LocationServices.getFusedLocationProviderClient(getActivity()).requestLocationUpdates(mLocationRequest, new LocationCallback() {
//                            @Override
//                            public void onLocationResult(LocationResult locationResult) {
//                                // do work here
//                                onLocationChanged(locationResult.getLastLocation());
//                            }
//                        },
//                        Looper.myLooper());
//            }
//        });
//
//        task.addOnFailureListener(getActivity(), new OnFailureListener() {
//            @Override
//            public void onFailure(@NonNull Exception e) {
//                if (e instanceof ResolvableApiException) {
//                    // Location settings are not satisfied, but this can be fixed
//                    // by showing the user a dialog.
////                    try {
////                        // Show the dialog by calling startResolutionForResult(),
////                        // and check the result in onActivityResult().
////                        ResolvableApiException resolvable = (ResolvableApiException) e;
////                        resolvable.startResolutionForResult(getActivity(),
////                                REQUEST_CHECK_SETTINGS);
////                    } catch (IntentSender.SendIntentException sendEx) {
////                        // Ignore the error.
////                    }
//                }
//            }
//        });
//        // new Google API SDK v11 uses getFusedLocationProviderClient(this)
//
//    }



    protected void updateHot_Cold(Location location) {
//        Location location1=new Location("");
//        location1.setLatitude(28.6148327d);
//        location1.setLongitude(77.3588033d);

        Mission mission = findNearest(location);
        Location location1 = new Location("");
        location1.setLatitude(mission.getLat());
        location1.setLongitude(mission.getLng());


        if (location1 != null) {
            double dist = location.distanceTo(location1);
//            int progress = hot_cold.getMax()-(int)((dist/maxDist)*hot_cold.getMax());
            int progress = hot_cold.getMax()-(int)dist;
            hot_cold.setProgress(progress>0?progress:0);
            if (dist <= 10) {
                vibrator.vibrate(7000);
                LootApplication app = (LootApplication) getActivity().getApplication();
                app.user.active = mission.getMissionId();
                if(app.user.found == null) {
                    app.user.found = new ArrayList<>();
                }
                if (!(app.user.found.contains(mission.getMissionId()))) {
                    app.user.found.add(mission.getMissionId());
                    app.user.active = mission.getMissionId();
                    app.user.score += 2;
                    users.child(app.user.getUserId()).setValue(app.user);
                }
                changeTab();
            }
            Toast.makeText(getActivity(), "Distance Left :" + dist,
                    Toast.LENGTH_SHORT).show();
        }
    }

    private void changeTab()
    {
////        Dashboard fragment=(Dashboard)getParentFragment();
////        fragment.switchTab(0);
//            ViewPager viewPager=getView().findViewById(R.id.viewpager);
//            viewPager.setCurrentItem(0,tr

        BottomNavigationView navigationView = getActivity().findViewById(R.id.navigation);
        navigationView.getMenu().getItem(0).setChecked(true);
        Fragment fragment = new CurrentMission();
        loadFragment(fragment);
    }

    private void loadFragment(Fragment fragment) {
        FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.frame_container, fragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }

    protected Mission findNearest(Location location) {
        double minDist = 0;
        int minI = -1;
        for (int i = 0; i < missions_left.size(); i++) {
            Location loc = new Location("");
            loc.setLatitude(missions_left.get(i).getLat());
            loc.setLongitude(missions_left.get(i).getLng());

            double dist = location.distanceTo(loc);

            Log.i("LatM", location.getLatitude() + "");
            Log.i("LngM", location.getLongitude() + "");
            Log.i("Lat", loc.getLatitude() + "");
            Log.i("Lng", loc.getLongitude() + "");
            Log.i("Dist", dist + "");
            if (i == 0 || minDist > dist) {
                minDist = dist;
                minI = i;
            }

        }
        if(minI != minDistI) {
            hot_cold.setMax((int)minDist);
        }
        return missions_left.get(minDistI);
    }

    private void updateGeocode() {
        missions_left = new ArrayList<>();

        ArrayList<String> completed = app.user.getCompleted();
        ArrayList<Mission> missions = app.missions;

        for (Mission mission : missions) {
            if (completed != null) {
                if (!(completed.contains(mission.getMissionId()))) {
                    missions_left.add(mission);
//                    Location loc=new Location("");
//                    loc.setLatitude(mission.getLat());
//                    loc.setLongitude(mission.getLng());
//                    geocodes.add(loc);
                    Log.i("Mission Id", mission.getMissionId());
                    Log.i("Lat", mission.getLat() + "");
                    Log.i("Lng", mission.getLng() + "");
                }
            } else {
                missions_left.add(mission);
//                Location loc=new Location("");
//                loc.setLatitude(mission.getLat());
//                loc.setLongitude(mission.getLng());
//                geocodes.add(loc);
                Log.i("Mission Id", mission.getMissionId());
                Log.i("Lat", mission.getLat() + "");
                Log.i("Lng", mission.getLng() + "");
            }
        }
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        mLocationRequest = LocationRequest.create();
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationRequest.setInterval(INTERVAL);
        checkPermission();
        LocationServices.FusedLocationApi.requestLocationUpdates(googleApiClient, mLocationRequest, this);
    }

    @Override
    public void onConnectionSuspended(int i) {
        checkPermission();
        Log.i("Connection","Suspended");
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.i("Connection","Failed");
        checkPermission();
    }

    @Override
    public void onStart() {
        Log.i("Locator","start");
        if(app.user.getActive()!=null) {
            showDialog();
        }
        else {
            googleApiClient.connect();
        }
        super.onStart();
    }

    @Override
    public void onStop() {
        Log.i("Locator","stop");
        googleApiClient.disconnect();
        super.onStop();
    }

    @Override
    public void onLocationChanged(Location location) {
        String msg = "Updated Location: " +
                Double.toString(location.getLatitude()) + "," +
                Double.toString(location.getLongitude());
        Toast.makeText(getActivity(), msg, Toast.LENGTH_SHORT).show();
        // You can now create a LatLng Object for use with maps
//        LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
        updateHot_Cold(location);
    }

   private void showDialog() {
       AlertDialog.Builder alertDialog = new AlertDialog.Builder(getActivity());
       alertDialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
           @Override
           public void onClick(DialogInterface dialogInterface, int i) {
               BottomNavigationView navigationView = getActivity().findViewById(R.id.navigation);
               navigationView.getMenu().getItem(0).setChecked(true);
               android.support.v4.app.Fragment fragment = new CurrentMission();
               FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
               transaction.replace(R.id.frame_container, fragment,"current_mission");
               transaction.addToBackStack(null);
               transaction.commit();
           }
       });
       alertDialog.setTitle("Alert");
       alertDialog.setMessage("You already have a active Mission");
       alertDialog.create().show();
   }
}
