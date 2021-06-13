package com.HBuilder.UniPlugin;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.view.MotionEvent;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.esri.arcgisruntime.ArcGISRuntimeEnvironment;
import com.esri.arcgisruntime.io.RequestConfiguration;
import com.esri.arcgisruntime.layers.WebTiledLayer;
import com.esri.arcgisruntime.mapping.ArcGISMap;
import com.esri.arcgisruntime.mapping.Basemap;
import com.esri.arcgisruntime.mapping.view.DefaultMapViewOnTouchListener;
import com.esri.arcgisruntime.mapping.view.LocationDisplay;
import com.esri.arcgisruntime.mapping.view.MapView;
import com.laocaixw.layout.SuspendButtonLayout;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;


public class MainActivity extends Activity {

    private static final int REQUEST_CODE = 1;
    private String[] suspendChildButtonInfo = {"相机", "音乐", "地图", "亮度", "联系人", "短信"};

    private MapView mapView;
    private LocationDisplay locationDisplay;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ArcGISRuntimeEnvironment.setLicense("runtimelite,1000,rud4449636536,none,NKMFA0PL4S0DRJE15166");
        mapView = findViewById(R.id.mapView);
        mapView.setAttributionTextVisible(false);
        requestAuthorities();
        startLocation();
        init();
    }
    //加载地图定位
    private void startLocation() {
        //注意：在100.2.0之后要设置RequestConfiguration
        RequestConfiguration requestConfiguration = new RequestConfiguration();
        requestConfiguration.getHeaders().put("referer", "http://www.arcgis.com");

// 加载天地图层-电子地图
        WebTiledLayer vecordBaseTiledLayer = TianDiTuMethodsClass.CreateTianDiTuTiledLayer(TianDiTuMethodsClass.LayerType.TIANDITU_VECTOR_MERCATOR);
// 加载中文标注图层-电子地图
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
    //回到当前位置
    private void nowLocation() {
        locationDisplay = mapView.getLocationDisplay();
        locationDisplay.setAutoPanMode(LocationDisplay.AutoPanMode.COMPASS_NAVIGATION);
        locationDisplay.startAsync();
    }
    private String[] getManifextPermissions() {
        // all permissions in AndroidManifext.xml
        // for android don't let me get them dynamically, it is ugly to code like this
        return new String[]{Manifest.permission.INTERNET
                , Manifest.permission.ACCESS_COARSE_LOCATION
                , Manifest.permission.ACCESS_FINE_LOCATION};
    }

    private void requestAuthorities() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            // permissions0 means permissions require requesting
            final List<String> permissions0 = new ArrayList<>();

            for (String permission : getManifextPermissions()) {
                if (ContextCompat.checkSelfPermission(this, permission)
                        != PackageManager.PERMISSION_GRANTED) {
                    permissions0.add(permission);
                }
            }

            if (!permissions0.isEmpty()) {
                String[] permissions1 = new String[permissions0.size()];
                for (int i = 0; i < permissions0.size(); i++) {
                    permissions1[i] = permissions0.get(i);
                }
                // request permission one by one
                ActivityCompat.requestPermissions(this,
                        permissions1,
                        REQUEST_CODE
                );
            } else {
                permissionsPermited();
            }
        } else {
            permissionsPermited();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == REQUEST_CODE) {
            boolean isForbidden = false;
            for (int i = 0; i < grantResults.length; i++) {
                if (grantResults[i] != 0) {
                    isForbidden = true;
                    break;
                }
            }
            if (isForbidden) {
                permissionsDenied();
            } else {
                permissionsPermited();
            }
        }
    }

    private void permissionsPermited() {
        new Thread() {
            @Override
            public void run() {
                System.out.println("afx all permissions are permitted");
            }
        }.start();
    }

    private void permissionsDenied() {
        Toast.makeText(this, "At least one permission is denied", Toast.LENGTH_LONG).show();
        finish();
    }
    private void init(){
        final SuspendButtonLayout suspendButtonLayout = (SuspendButtonLayout) findViewById(R.id.layout);
        suspendButtonLayout.setOnSuspendListener(new SuspendButtonLayout.OnSuspendListener() {
            @Override
            public void onButtonStatusChanged(int status) {

            }

            @Override
            public void onChildButtonClick(int index) {
                switch(index-1){
                    case 0:
                        initlistener();
                        break;
                    case 1:
                        break;
                }
                Toast.makeText(MainActivity.this, "您点击了【"
                        + suspendChildButtonInfo[index - 1] + "】按钮！", Toast.LENGTH_SHORT).show();
            }
        });

    }
    private void initlistener(){
        Intent intent = new Intent(getApplicationContext(),TestActivity.class);
        startActivity(intent);
    }

}