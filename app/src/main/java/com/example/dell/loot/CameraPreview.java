package com.example.dell.loot;


import android.annotation.SuppressLint;

import android.content.Context;

import android.content.res.Configuration;
import android.hardware.Camera;

import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;


import java.io.IOException;

import static android.content.ContentValues.TAG;

/**
 * Created by DELL on 1/21/2018.
 */

@SuppressLint("ViewConstructor")
public class CameraPreview extends SurfaceView implements SurfaceHolder.Callback {
    private SurfaceHolder mHolder;
    private Camera mCamera;


    public CameraPreview(Context context, Camera camera) {
        super(context);
        mCamera = camera;
        mHolder = getHolder();
        Log.i("holder",mHolder.getSurface().toString());
        mHolder.addCallback(this);

    }

    public void surfaceCreated(SurfaceHolder holder) {
        try {
            if(mCamera!=null) {
                mCamera.setPreviewDisplay(holder);
                mCamera.startPreview();
            }
            else
            {

            }
        } catch (IOException e) {
            Log.d(TAG, "Error setting camera preview: " + e.getMessage());
        }
    }

    public void surfaceDestroyed(SurfaceHolder holder) {


    }

    public void surfaceChanged(SurfaceHolder holder, int format, int w, int h) {

        if (mHolder.getSurface() == null){
            return;
        }

        try {
            mCamera.stopPreview();
        } catch (Exception e){

        }

        try {

            mCamera.setPreviewDisplay(mHolder);
            Camera.Parameters parameters = mCamera.getParameters();
            if (this.getResources().getConfiguration().orientation != Configuration.ORIENTATION_LANDSCAPE) {
                parameters.set("orientation", "portrait");
                parameters.set("rotation",90);
                mCamera.setDisplayOrientation(90);
            }
            mCamera.setParameters(parameters);
            mCamera.startPreview();

        } catch (Exception e){
            Log.d(TAG, "Error starting camera preview: " + e.getMessage());
        }
    }



}