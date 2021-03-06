package com.HBuilder.UniPlugin;

import android.Manifest;
import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.hardware.Camera;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.view.SurfaceView;
import android.view.Surface;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.esri.arcgisruntime.ArcGISRuntimeEnvironment;
import com.esri.arcgisruntime.io.RequestConfiguration;
import com.esri.arcgisruntime.layers.WebTiledLayer;
import com.esri.arcgisruntime.mapping.ArcGISMap;
import com.esri.arcgisruntime.mapping.Basemap;
import com.esri.arcgisruntime.mapping.view.DefaultMapViewOnTouchListener;
import com.esri.arcgisruntime.mapping.view.LocationDisplay;
import com.esri.arcgisruntime.mapping.view.MapView;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;

import static java.lang.Thread.sleep;

public class SelfCameraActivity extends Activity implements View.OnClickListener, SensorEventListener {

    private static final String TAG = "CFActivity";
    public static final String FILE_NAME = "filename";
    private CameraPreView cameraPreView;
    private Camera mCamera;
    private ImageView takePhotoAgain;
    private ImageView takePhoto;
    private ImageView takePhotoConfirm;
    private ImageView takePhotoCancle;
    //?????????????????????
    private CompassView cView;
    private SensorManager sManager;
    private Sensor mSensorOrientation;

    //????????????
    private int count=0;

    //handler??????
    private mHandler mhandler = new mHandler();



    private TextView takePhotoTips;
    private TextView location;
    private ImageView faceSurface;
    private byte[] mData;
    private String fileName;

    private MapView mapView;
    private LocationDisplay locationDisplay;
    private List<String> point;
    private Button button;
    private TextView sensorlocation;

    //timer?????????
    TimerTask timetask=new TimerTask() {
        @Override
        public void run() {

            Message msg = Message.obtain();
            msg.what = 1;

            //handler????????????

            mhandler.sendMessage(msg);


        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_self_camera);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        //??????????????
        takePhotoTips = findViewById(R.id.cf_tips_tv);
        location = findViewById(R.id.location);
        takePhotoTips.setText("?????????????????????????????????");
        faceSurface = findViewById(R.id.private_cf_frame_iv);
        takePhotoAgain = findViewById(R.id.cf_shoot_again_btn);
        takePhoto = findViewById(R.id.cf_shot_btn);
        takePhotoConfirm = findViewById(R.id.cf_confirm_btn);
        takePhotoCancle = findViewById(R.id.camera_cancle_button);
        button = findViewById(R.id.button);

        //??????????????????
        OnClickUtil.setOnclick(this,takePhotoAgain,takePhoto,takePhotoConfirm,takePhotoCancle,button);
        if (getIntent()!=null){
            fileName = getIntent().getStringExtra(FILE_NAME);
        }

        //????????????
        cameraPreView = findViewById(R.id.cf_frame_camera_sv);
        mCamera = Camera.open(SharedData.cameraIndex);

        setCameraDisplayOrientation(0, mCamera);
        cameraPreView.setCamera(mCamera);
        ArcGISRuntimeEnvironment.setLicense("runtimelite,1000,rud4449636536,none,NKMFA0PL4S0DRJE15166");
        mapView = findViewById(R.id.mapView);
        mapView.setAttributionTextVisible(false);
        startLocation();


        //???????????????
        init();

    }
    //???????????????
    public void init(){
        sensorlocation=(TextView)findViewById(R.id.location);
        cView = new CompassView();
        sManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        mSensorOrientation = sManager.getDefaultSensor(Sensor.TYPE_ORIENTATION);
        sManager.registerListener(this, mSensorOrientation, SensorManager.SENSOR_DELAY_UI);
        //??????handler
        Timer timer=new Timer();
        timer.schedule(timetask,1000,1000);
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
            case R.id.cf_shot_btn://??????
                try {
                    takePicture();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                break;
            case R.id.cf_shoot_again_btn://??????
                takePictureAgain();
                break;

            case R.id.cf_confirm_btn://??????
                confirmTakePicture();
                break;
            case R.id.camera_cancle_button://??????
                takePictureCancle();
                break;
            case R.id.button:
                mapback();
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

    private void takePicture() throws IOException {
        SharedData.uploadResultShow=false;
        mCamera.takePicture(null,null,new Camera.PictureCallback(){
            @Override
            public void onPictureTaken(byte[] data, Camera camera){
                mData = data;
            }
        });
        //GONE???????????????????????????INVISIBLE????????????????????????
        takePhoto.setVisibility(View.GONE);
        takePhotoCancle.setVisibility(View.GONE);
        takePhotoAgain.setVisibility(View.VISIBLE);
        takePhotoConfirm.setVisibility(View.VISIBLE);
        faceSurface.setVisibility(View.GONE);
        takePhotoTips.setVisibility(View.GONE);
        location.setVisibility(View.GONE);
        point = getLocation();
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
                Log.d(TAG,"??????????????????????????????????????????data??????");
                Bitmap bitmap = BitmapFactory.decodeByteArray(mData, 0, mData.length);
                Matrix m = new Matrix();
                int rotate = 90;
                m.setRotate(rotate, (float) bitmap.getWidth() / 2, (float) bitmap.getHeight() / 2);
                Bitmap bitmap1 = Bitmap.createBitmap(
                        bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), m, true);
                SharedData.cameraData = bitmap1;
                Intent intent = new Intent();
                //????????????????????????????????????????????????
                intent.putExtra("camera_data","output_image.jpg");
                setResult(RESULT_OK,intent);
                DatabaseHelper dbHelper = new DatabaseHelper(SelfCameraActivity.this, "test_db",null,1);
                SQLiteDatabase db = dbHelper.getWritableDatabase();
//                ContentValues values = new ContentValues();
                String insertSQL = "INSERT INTO user VALUES(?,?,?,?,?)";

                db.execSQL(insertSQL,new Object[] { point.get(3),point.get(0),point.get(2),point.get(1),point.get(4) });
                db.close();
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

    private void mapback() {
        Intent intent = new Intent(getApplicationContext(),MainActivity.class);
        startActivity(intent);
    }

    //???????????????????????????????????????
    private List<String> getLocation() throws IOException {
        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        StringBuilder place = new StringBuilder();
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.

        }
        Location location = locationManager.getLastKnownLocation(locationManager.NETWORK_PROVIDER);
        List<Address> result = null;
        Geocoder gc = new Geocoder(this, Locale.getDefault());
        result = gc.getFromLocation(location.getLatitude(),
                location.getLongitude(), 1);


        if (result.size() > 0) {
            Address address = result.get(0);
            place.append(address.getLocality())
                    .append(address.getSubLocality())
                    .append(address.getFeatureName())
                    .append("??????");
        }

        List<String> point = new ArrayList<>();
        point.add(place.toString()); //??????
        point.add(String.valueOf(location.getLatitude())); //??????
        point.add(String.valueOf(location.getLongitude())); //??????
        point.add(SharedData.filepath);  //??????????????????
        point.add(cView.getMsg());
        return point;
    }

    //??????????????????
    private void startLocation() {
        //????????????100.2.0???????????????RequestConfiguration
        RequestConfiguration requestConfiguration = new RequestConfiguration();
        requestConfiguration.getHeaders().put("referer", "http://www.arcgis.com");

// ??????????????????-????????????
        WebTiledLayer vecordBaseTiledLayer = TianDiTuMethodsClass.CreateTianDiTuTiledLayer(TianDiTuMethodsClass.LayerType.TIANDITU_VECTOR_MERCATOR);
// ????????????????????????-????????????
        WebTiledLayer vecordWordTiledLayer = TianDiTuMethodsClass.CreateTianDiTuTiledLayer(TianDiTuMethodsClass.LayerType.TIANDITU_VECTOR_ANNOTATION_CHINESE_MERCATOR);

        vecordBaseTiledLayer.setRequestConfiguration(requestConfiguration);
        vecordWordTiledLayer.setRequestConfiguration(requestConfiguration);

        vecordBaseTiledLayer.loadAsync();
        vecordWordTiledLayer.loadAsync();

        Basemap basemap = new Basemap();

        basemap.getBaseLayers().add(vecordBaseTiledLayer);
        basemap.getBaseLayers().add(vecordWordTiledLayer);

        ArcGISMap arcGISMap = new ArcGISMap(basemap);
        mapView.setMap(arcGISMap);
        mapView.setOnTouchListener(new DefaultMapViewOnTouchListener(this,mapView) {
            @Override
            public boolean onRotate(MotionEvent motionEvent, double v) {
                return false;
            }
        });
        locationDisplay = mapView.getLocationDisplay();
        locationDisplay.setAutoPanMode(LocationDisplay.AutoPanMode.COMPASS_NAVIGATION);
        locationDisplay.startAsync();

    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        cView.setDegree(event.values[0]);
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }
    //handler?????????
    class mHandler extends Handler{
        @Override
        public void handleMessage(Message msg){
            super.handleMessage(msg);

            sensorlocation.setText(cView.getMsg());

        }
    }
}
