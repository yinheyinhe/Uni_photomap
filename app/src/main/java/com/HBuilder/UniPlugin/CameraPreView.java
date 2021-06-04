package com.HBuilder.UniPlugin;

import android.content.Context;
import android.hardware.Camera;
import android.util.AttributeSet;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import java.io.IOException;
import java.util.List;

public class CameraPreView extends SurfaceView implements SurfaceHolder.Callback {
    private SurfaceHolder mHolder;
    private Camera mCamera;

    public CameraPreView(Context context){
        super(context);
    }

    public CameraPreView(Context context, AttributeSet attrs){ super(context, attrs);}

    public CameraPreView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public void setCamera(Camera camera){
        mCamera = camera;
        mHolder = getHolder();
        mHolder.addCallback(this);
        mHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
//        mCamera.setDisplayOrientation();
//        Camera.Parameters params = mCamera.getParameters();
//        List<String> focusModes = params.getSupportedFocusModes();
//        if (!focusModes.contains(Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE)){
//            params.setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE);
//        }
//        mCamera.setParameters(params);
//        mCamera.setDisplayOrientation(90);
    }



    public void surfaceCreated(SurfaceHolder holder){
        try {
            mCamera.setPreviewDisplay(holder);
            mCamera.startPreview();
        }catch (IOException e){
            Log.d("this","Error setting camera preview: " + e.getMessage());
        }
    }

    public void surfaceDestroyed(SurfaceHolder holder){

    }

    public void surfaceChanged(SurfaceHolder holder, int format, int w, int h){
        if (mHolder.getSurface() == null){
            return;
        }
        try {
            mCamera.stopPreview();
        }catch (Exception e){

        }
        try {
            mCamera.setPreviewDisplay(mHolder);
            mCamera.startPreview();
        }catch (Exception e){
            Log.d("this","Error starting camera preview: " + e.getMessage());
        }
    }

}
