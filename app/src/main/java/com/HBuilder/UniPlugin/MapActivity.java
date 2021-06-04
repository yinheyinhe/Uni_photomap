package com.HBuilder.UniPlugin;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;

import com.esri.arcgisruntime.geometry.Point;
import com.esri.arcgisruntime.layers.ArcGISTiledLayer;
import com.esri.arcgisruntime.location.LocationDataSource;
import com.esri.arcgisruntime.mapping.ArcGISMap;
import com.esri.arcgisruntime.mapping.Basemap;
import com.esri.arcgisruntime.mapping.view.LocationDisplay;
import com.esri.arcgisruntime.mapping.view.MapView;


public class MapActivity extends Activity {

    private MapView mapView;
    private LocationDisplay locationDisplay;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.map);
        mapView = (MapView) findViewById(R.id.mapView);
        String theURLString = "http://map.geoq.cn/arcgis/rest/services/ChinaOnlineCommunity/MapServer";
        ArcGISTiledLayer mainArcGISTiledLayer = new ArcGISTiledLayer(theURLString);
        Basemap mainBasemap = new Basemap(mainArcGISTiledLayer);
        ArcGISMap arcGISMap = new ArcGISMap(mainBasemap);
        mapView.setMap(arcGISMap);
        startLocation();

    }
    private void startLocation() {
        locationDisplay = mapView.getLocationDisplay();
        locationDisplay.setAutoPanMode(LocationDisplay.AutoPanMode.RECENTER);
        //当我们执行LocationDisplay.startAsync()方法时候，会在地图上显示出我们当前位置
        locationDisplay.startAsync();

        //获取的点是基于当前地图坐标系的点
//        com.esri.arcgisruntime.geometry.Point point = locationDisplay.getMapLocation();
//        Log.e("xyh", "point: " + point.toString());
//
//        //获取基于GPS的位置信息
//        LocationDataSource.Location location = locationDisplay.getLocation();
//        //基于WGS84的经纬度坐标。
//        Point point1 = location.getPosition();
//        if (point1 != null) {
//            Log.e("xyh", "point1: " + point1.toString());
//        }
//
//        //如果要在LocationDisplay里进行位置信息的自动监听，方法也很简单，只需要LocationDisplay.addLocationChangedListener即可
//        locationDisplay.addLocationChangedListener(new LocationDisplay.LocationChangedListener() {
//            @Override
//            public void onLocationChanged(LocationDisplay.LocationChangedEvent locationChangedEvent) {
//                LocationDataSource.Location location = locationChangedEvent.getLocation();
//                Log.e("xyh", "onLocationChanged: " + location.getPosition().toString());
//            }
//        });
    }
}