package com.HBuilder.UniPlugin;
import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.esri.arcgisruntime.ArcGISRuntimeEnvironment;
import com.esri.arcgisruntime.geometry.Point;
import com.esri.arcgisruntime.geometry.SpatialReference;
import com.esri.arcgisruntime.layers.ArcGISTiledLayer;
import com.esri.arcgisruntime.location.LocationDataSource;
import com.esri.arcgisruntime.mapping.ArcGISMap;
import com.esri.arcgisruntime.mapping.Basemap;
import com.esri.arcgisruntime.mapping.view.LocationDisplay;
import com.esri.arcgisruntime.mapping.view.MapView;

import java.util.ArrayList;
import java.util.List;



public class MainActivity extends Activity {

    private static final int REQUEST_CODE = 1;
    private TextView textView;
    private MapView mapView;
    private LocationDisplay locationDisplay;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        textView = findViewById(R.id.text);
        textView.setText("hello");
        mapView = (MapView) findViewById(R.id.mapView);
        textView = findViewById(R.id.text);
//创建一个地图对象
//        ArcGISRuntimeEnvironment.setApiKey("AAPK6b685d3dbd564411a14f7fc06273d81beaL7" +
//                "_xTeuZFPaviQLhHt2MEPoa9iFC3hrOppU2GSGwUsZTrEFLf7epfcVtqAUx6W");
        ArcGISMap map = new ArcGISMap();
//去除水印
        //ArcGISRuntime.setClientId("1eFHW78avlnRUPHm");
//创建并添加地图服务
        String strMapUrl="http://map.geoq.cn/arcgis/rest/services/ChinaOnlineCommunity/MapServer";
        ArcGISTiledLayer arcGISTiledLayer = new ArcGISTiledLayer(strMapUrl);
//创建底图、并设置底图图层
        Basemap basemap = new Basemap(arcGISTiledLayer);
        //basemap.getBaseLayers().add(arcGISTiledLayer);
//设置地图底图
        map.setBasemap(basemap);
//设置map地图对象在MapView控件中显示
        mapView.setMap(map);
//        cusLocationDataSource = new CusLocationDataSource();
        locationDisplay = mapView.getLocationDisplay();
        locationDisplay.setShowLocation(true);
        locationDisplay.setAutoPanMode(LocationDisplay.AutoPanMode.RECENTER);
        locationDisplay.startAsync();
//        LocationDataSource.Location location = new LocationDataSource.Location(new Point(39.577,116.201313,SpatialReference.create(4326)));
//        cusLocationDataSource.UpdateLocation(location);

        //locationDisplay.setLocationDataSource(cusLocationDataSource);
//        locationDisplay.addLocationChangedListener(locationChangedEvent -> {
//
//            Double Latitude =  locationChangedEvent.getLocation().getPosition().getY()-0.375043;
//            Double Longitude = locationChangedEvent.getLocation().getPosition().getX()-0.135801;
////            LocationDataSource.Location location = new LocationDataSource.Location(new Point(Longitude,Latitude,SpatialReference.create(4326)));
////            cusLocationDataSource.UpdateLocation(location);
//
//           textView.setText(Latitude+","+Longitude);
//        });
//
//
//        if(!locationDisplay.isStarted())
//
//
//            locationDisplay.startAsync();
//
//
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

    @Override
    protected void onPause() {
        super.onPause();
        mapView.pause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mapView.resume();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mapView.dispose();
    }





}