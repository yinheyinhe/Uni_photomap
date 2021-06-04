package com.HBuilder.UniPlugin;
import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.laocaixw.layout.SuspendButtonLayout;

import java.util.ArrayList;
import java.util.List;



public class MainActivity extends Activity {

    private static final int REQUEST_CODE = 1;
    private String[] suspendChildButtonInfo = {"相机", "音乐", "地图", "亮度", "联系人", "短信"};


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        requestAuthorities();
        init();
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