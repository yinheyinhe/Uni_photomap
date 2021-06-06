package com.HBuilder.UniPlugin;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.hardware.Camera;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.view.SurfaceView;
import android.view.Surface;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

public class SelfCameraActivity extends Activity implements View.OnClickListener {

    private static final String TAG = "CFActivity";
    public static final String FILE_NAME = "filename";
    private CameraPreView cameraPreView;
    private Camera mCamera;
    private ImageView takePhotoAgain;
    private ImageView takePhoto;
    private ImageView takePhotoConfirm;
    private ImageView takePhotoCancle;

    private TextView takePhotoTips;
    private TextView location;
    private ImageView faceSurface;
    private byte[] mData;
    private String fileName;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_self_camera);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        takePhotoTips = findViewById(R.id.cf_tips_tv);
        location = findViewById(R.id.location);



        takePhotoTips.setText("请调整图像处于正方形内");
        faceSurface = findViewById(R.id.private_cf_frame_iv);
        takePhotoAgain = findViewById(R.id.cf_shoot_again_btn);
        takePhoto = findViewById(R.id.cf_shot_btn);
        takePhotoConfirm = findViewById(R.id.cf_confirm_btn);
        takePhotoCancle = findViewById(R.id.camera_cancle_button);
        OnClickUtil.setOnclick(this,takePhotoAgain,takePhoto,takePhotoConfirm,takePhotoCancle);
        if (getIntent()!=null){
            fileName = getIntent().getStringExtra(FILE_NAME);
        }
        cameraPreView = findViewById(R.id.cf_frame_camera_sv);
        mCamera = Camera.open(SharedData.cameraIndex);
       // Camera.Parameters mParameters = mCamera.getParameters();
//        List<Camera.Size> sizeList = mParameters.getSupportedPreviewSizes();
//        for (Camera.Size size:sizeList){
//            Log.e(TAG,"onClick:w: " +size.width+"h: "+size.height);
//        }
//        mParameters.setPictureSize(1440,1080);
//        mParameters.setPreviewFrameRate(20);
//        mCamera.setParameters(mParameters);
        //mParameters.setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE);
       // mCamera.setParameters(mParameters);
        setCameraDisplayOrientation(0, mCamera);
        cameraPreView.setCamera(mCamera);

//        cameraPreView.surfaceCreated(cameraPreView.getHolder());
    }
    public void setCameraDisplayOrientation(int cameraId, android.hardware.Camera camera) {
        android.hardware.Camera.CameraInfo info =
                new android.hardware.Camera.CameraInfo();
        android.hardware.Camera.getCameraInfo(cameraId, info);
        int rotation = this.getWindowManager().getDefaultDisplay()
                .getRotation();
        int degrees = 0;
        switch (rotation) {
            case Surface.ROTATION_0: degrees = 0; break;
            case Surface.ROTATION_90: degrees = 90; break;
            case Surface.ROTATION_180: degrees = 180; break;
            case Surface.ROTATION_270: degrees = 270; break;
        }

        int result;
        if (info.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
            result = (info.orientation + degrees) % 360;
            result = (360 - result) % 360;  // compensate the mirror
        } else {  // back-facing
            result = (info.orientation - degrees + 360) % 360;
        }
        camera.setDisplayOrientation(result);
    }

    @Override
    public void onClick(View v){

        switch (v.getId()){
            case R.id.cf_shot_btn://拍摄
                takePicture();
                break;
            case R.id.cf_shoot_again_btn://重拍
                takePictureAgain();
                break;

            case R.id.cf_confirm_btn://确定
                confirmTakePicture();
                break;
            case R.id.camera_cancle_button://取消
                takePictureCancle();
                break;
        }
    }

    @Override
    protected void onDestroy(){
        super.onDestroy();
        if (mCamera!=null){
            Intent intent = new Intent();
            intent.putExtra("result",mData);
            setResult(RESULT_OK,intent);
            mCamera.release();
        }

    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults){ }

    private void takePicture(){
        SharedData.uploadResultShow=false;
        mCamera.takePicture(null,null,new Camera.PictureCallback(){
            @Override
            public void onPictureTaken(byte[] data, Camera camera){
                mData = data;
            }
        });
        //GONE不可见且不占空间，INVISIBLE不可见但占用空间
        takePhoto.setVisibility(View.GONE);
        takePhotoCancle.setVisibility(View.GONE);
        takePhotoAgain.setVisibility(View.VISIBLE);
        takePhotoConfirm.setVisibility(View.VISIBLE);
        faceSurface.setVisibility(View.GONE);
        takePhotoTips.setVisibility(View.GONE);
        location.setVisibility(View.GONE);
    }

    private void takePictureAgain(){
        SharedData.selfCameraFlag=false;
        takePhoto.setVisibility(View.VISIBLE);
        takePhotoCancle.setVisibility(View.VISIBLE);

        cameraPreView.surfaceCreated(cameraPreView.getHolder());
        takePhotoAgain.setVisibility(View.GONE);
        takePhotoConfirm.setVisibility(View.GONE);
        faceSurface.setVisibility(View.VISIBLE);
        takePhotoTips.setVisibility(View.VISIBLE);
        location.setVisibility(View.VISIBLE);
    }



    private void confirmTakePicture(){
        if (mData!=null){
            try {
                Log.d(TAG,"点击确定，拍照完成，返回图片data数据");
                Bitmap bitmap = BitmapFactory.decodeByteArray(mData, 0, mData.length);
                Matrix m = new Matrix();
                int rotate = 90;
                m.setRotate(rotate, (float) bitmap.getWidth() / 2, (float) bitmap.getHeight() / 2);
                Bitmap bitmap1 = Bitmap.createBitmap(
                        bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), m, true);
                SharedData.cameraData = bitmap1;
                Intent intent = new Intent();
                //第一个是键名，第二个是键对应的值
                intent.putExtra("camera_data","output_image.jpg");
                setResult(RESULT_OK,intent);
            }catch (Exception e){
                e.printStackTrace();
            }

        }
        else {
            setResult(RESULT_CANCELED);
        }
        finish();
    }

    private void takePictureCancle(){SelfCameraActivity.this.finish();}


}
