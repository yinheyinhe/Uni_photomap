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
import java.io.FileOutputStream;
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
            //申请权限，REQUEST_TAKE_PHOTO_PERMISSION是自定义常量
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
                    //得到自己的包名
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
                        Bitmap bitmap = SharedData.cameraData;
                        imageView.setImageBitmap(bitmap);

                        Bundle extras = data.getExtras();
                        String filename = extras.getString("camera_data");
                        new Thread(()->{
                            saveImageToGallery(this,SharedData.cameraData);
                        }).start();

//                        File file = new File(getExternalCacheDir(), filename);
//                        try {
//                            MediaStore.Images.Media.insertImage(getApplicationContext().getContentResolver(),file.getAbsolutePath(),filename,null);
//
//                        }catch (FileNotFoundException e){
//                            e.printStackTrace();
//                        }
//                        new Thread(()->{
//                            try{
//                                System.out.println("发送广播");
//                                Uri uri = Uri.fromFile(file);
//                                getApplicationContext().sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE,uri));
//                                byte[] cameraData = new byte[(int)file.length()];
//                                FileInputStream fis = new FileInputStream(file);
//                                fis.read(cameraData);
////                    byte[] cameraData = extras.getByteArray("camera_data");
//                            }catch (Exception e){
//                                e.printStackTrace();
//                            }
//
//                        }).start();

                    }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static boolean saveImageToGallery(Context context, Bitmap bmp) {
        // 首先保存图片
        String storePath = context.getExternalFilesDir(null).getAbsolutePath() + File.separator + "dearxy";
        File appDir = new File(storePath);
        if (!appDir.exists()) {
            appDir.mkdir();
        }
        String fileName = System.currentTimeMillis() + ".jpg";
        File file = new File(appDir, fileName);
        try {
            FileOutputStream fos = new FileOutputStream(file);
            //通过io流的方式来压缩保存图片
            boolean isSuccess = bmp.compress(Bitmap.CompressFormat.JPEG, 100, fos);
            fos.flush();
            fos.close();

            //把文件插入到系统图库
            MediaStore.Images.Media.insertImage(context.getContentResolver(), file.getAbsolutePath(), fileName, null);

            //保存图片后发送广播通知更新数据库
            Uri uri = Uri.fromFile(file);
            context.sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, uri));
            if (isSuccess) {
                return true;
            } else {
                return false;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }
}
