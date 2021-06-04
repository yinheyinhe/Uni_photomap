package com.HBuilder.UniPlugin;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.OrientationEventListener;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class TestActivity extends Activity {

    private ImageView imageView;
    private OrientationEventListener orientationEventListener;
    private static int degree;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);
        requestPermission(getApplicationContext());
        imageView = findViewById(R.id.show_image);
        Button button = findViewById(R.id.take_photo);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openCamera();
            }
        });

        orientationEventListener = new OrientationEventListener(this) {
            @Override
            public void onOrientationChanged(int orientation) {
                if (orientation == OrientationEventListener.ORIENTATION_UNKNOWN) {
                    return;
                }
                if (orientation > 350 || orientation < 10) {
                    orientation = 90;
                } else if (orientation > 80 && orientation < 100) {
                    orientation = 0;
                } else if (orientation > 170 && orientation < 190) {
                    orientation = 270;
                } else if (orientation > 260 && orientation < 280) {
                    orientation = 180;
                } else {
                    orientation = 0;
                }
                degree = orientation;
            }
        };
    }

    private void openCamera() {
        if (ContextCompat.checkSelfPermission(TestActivity.this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);

        } else {
            Intent intent = new Intent(getApplicationContext(), SelfCameraActivity.class);
            startActivityForResult(intent, 1);
        }
    }

    private void requestPermission(final Context context) {
        new Thread() {
            @Override
            public void run() {
                String sharedPkgList[] = null;
                PackageManager pm = context.getPackageManager();
                try {
                    PackageInfo pi = pm.getPackageInfo(context.getPackageName(), 0);
                    String pkgName = pi.packageName;
                    //Log.e(TAG,"requestPermission:  "+ pkgName);
                    PackageInfo pkgInfo = pm.getPackageInfo(pkgName, PackageManager.GET_PERMISSIONS);
                    sharedPkgList = pkgInfo.requestedPermissions;

                } catch (Exception e) {

                }
                List<String> requestedPermissionList = new ArrayList();
                requestedPermissionList.add(Manifest.permission.READ_PHONE_STATE);
                requestedPermissionList.add(Manifest.permission.RECORD_AUDIO);
                requestedPermissionList.add(Manifest.permission.READ_EXTERNAL_STORAGE);
                requestedPermissionList.add(Manifest.permission.ACCESS_NETWORK_STATE);

                if (Build.VERSION.SDK_INT >= 23) {
                    if (sharedPkgList != null) {
                        for (String mPermission : sharedPkgList) {
                            if (requestedPermissionList.contains(mPermission))
                                requestedPermissionList.remove(mPermission);
                        }
                        if (requestedPermissionList.size() >= 1) {
                            String[] notAuthorityPermissionList = new String[requestedPermissionList.size()];
                            for (int i = 0; i < requestedPermissionList.size(); i++) {
                                notAuthorityPermissionList[i] = requestedPermissionList.get(i);
                            }
                            requestPermissions(notAuthorityPermissionList, 100);
                        }
                    }
                }

            }
        }.start();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        try {
            switch (requestCode) {
                case 1:
                    if (resultCode == RESULT_OK) {
                        Bundle extras = data.getExtras();
                        String filename = extras.getString("camera_data");
                        File file = new File(getExternalCacheDir(), filename);
                        try {
                            MediaStore.Images.Media.insertImage(getApplicationContext().getContentResolver(),file.getAbsolutePath(),filename,null);

                        }catch (FileNotFoundException e){
                            e.printStackTrace();
                        }
                        System.out.println("发送广播");
                        Uri uri = Uri.fromFile(file);
                        getApplicationContext().sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE,uri));
                        byte[] cameraData = new byte[(int)file.length()];
                        FileInputStream fis = new FileInputStream(file);
                        fis.read(cameraData);
//                    byte[] cameraData = extras.getByteArray("camera_data");
                        if (cameraData != null) {
                            Bitmap bitmap = BitmapFactory.decodeByteArray(cameraData, 0, cameraData.length);
                            if (SharedData.cameraIndex == 1) {
//                                Matrix m = new Matrix();
//                                int rotate = 0;
//                                if (degree == 90) {
//                                    rotate = 270;
//                                } else if (degree == 0) {
//                                    rotate = 270;
//                                } else if (degree == 180) {
//                                    rotate = 90;
//                                } else if (degree == 270) {
//                                    rotate = 90;
//                                }
//                                m.setRotate(rotate, (float) bitmap.getWidth() / 2, (float) bitmap.getHeight() / 2);
//                                final Bitmap bitmap1 = Bitmap.createBitmap(
//                                        bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), m, true);
                                imageView.setImageBitmap(bitmap);
                            } else {
//                                Matrix m = new Matrix();
//                                int rotate = 0;
//                                if (degree == 90) {
//                                    rotate = 90;
//                                } else if (degree == 0) {
//                                    rotate = 90;
//                                } else if (degree == 180) {
//                                    rotate = -90;
//                                } else if (degree == 270) {
//                                    rotate = -90;
//                                }
//                                m.setRotate(rotate, (float) bitmap.getWidth() / 2, (float) bitmap.getHeight() / 2);
//                                final Bitmap bitmap1 = Bitmap.createBitmap(
//                                        bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), m, true);
                                imageView.setImageBitmap(bitmap);
                            }
                        }
                    }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
