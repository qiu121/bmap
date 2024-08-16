package com.miluo.service;

import android.content.Context;
import android.content.Intent;
import android.provider.MediaStore;

public class CameraService {
    public static void openCamera(Context context) {
        // 创建启动系统相机的意图
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // 检查是否有相机应用可用来处理意图
        if (takePictureIntent.resolveActivity(context.getPackageManager()) != null) {
            // 启动相机应用
            context.startActivity(takePictureIntent);
        }
    }
}
