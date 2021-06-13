package com.HBuilder.UniPlugin;

import android.graphics.Bitmap;

public class SharedData {
    public static int cameraIndex=0;//相机索引，0代表后置摄像头
    public static boolean uploadResultShow=false;//是否是上传之后的图片显示
    public static boolean selfCameraFlag=false;//是否是自定义相机拍摄的照片
    public static Bitmap cameraData;//静态变量存储照片
    public static String filepath;
}
