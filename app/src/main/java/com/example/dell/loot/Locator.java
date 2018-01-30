package com.example.dell.loot;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.PixelFormat;
import android.hardware.Camera;
import android.net.Uri;
import android.opengl.GLSurfaceView;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.Toast;


public class Locator extends Fragment {


    final private int Camera_Request=1;
    Camera camera;

    public Locator() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        return inflater.inflate(R.layout.fragment_locator, container, false);


    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);


    }

    @Override
    public void onDetach() {
        super.onDetach();

    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        
        //getActivity().getActionBar().hide();

    }

    @Override
    public void onResume() {
        super.onResume();
        FrameLayout camera_frame=(FrameLayout)getActivity().findViewById(R.id.camera_frame);
        camera=getCameraInstance();
        CameraPreview preview=new CameraPreview(getContext(),camera);
        camera_frame.addView(preview,0);
    }

    public Camera getCameraInstance(){
        Camera c = null;
        try {

            c = Camera.open();
            if(c==null) {
                checkPermission();
                c = Camera.open();
            }
        }
        catch (Exception e){
            Toast.makeText(getContext(),"Unable to Open Camera",Toast.LENGTH_SHORT).show();
        }

        return c;


    }

    @Override
    public void onPause() {
        super.onPause();
        if(camera!=null)
        {
            camera.stopPreview();
            camera.release();
        }
    }

    private void checkPermission()
    {
        if (ContextCompat.checkSelfPermission(getContext(),
                Manifest.permission.CAMERA)
                == PackageManager.PERMISSION_DENIED) {

            ActivityCompat.requestPermissions(getActivity(),
                    new String[]{Manifest.permission.CAMERA},
                    Camera_Request);
        }

    }
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        if(requestCode== Camera_Request)
        {
            if (grantResults.length > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                Toast.makeText(getContext(),"Permission Granted",Toast.LENGTH_SHORT).show();
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
}
