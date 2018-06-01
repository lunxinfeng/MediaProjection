package com.lxf.capture;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.hardware.display.DisplayManager;
import android.hardware.display.VirtualDisplay;
import android.media.ImageReader;
import android.media.projection.MediaProjection;
import android.media.projection.MediaProjectionManager;
import android.os.Bundle;
import android.util.Log;

public class PermissionFragment extends Fragment {
    private static final String TAG = "PermissionFragment";
    private MediaProjectionManager manager;
    private MediaProjection mediaProjection;
    private VirtualDisplay virtualDisplay;
    private ImageReader imageReader;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        imageReader = ImageReader.newInstance(Capture.screenW, Capture.screenH, PixelFormat.RGBA_8888, 1);
        imageReader.setOnImageAvailableListener(new ImageReader.OnImageAvailableListener() {
            @Override
            public void onImageAvailable(ImageReader reader) {

            }
        },null);
    }

    public void requestCapture() {
        manager = (MediaProjectionManager) getActivity().getSystemService(Context.MEDIA_PROJECTION_SERVICE);
        if (manager != null) {
            startActivityForResult(manager.createScreenCaptureIntent(), 1);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == Activity.RESULT_OK) {
            Log.d(TAG, "onActivityResult: 权限申请成功");
            getMediaProjection(resultCode,data);
            getVirtualDisplay();
        } else {
            Log.d(TAG, "onActivityResult: 权限申请失败");
        }
    }

    private void getMediaProjection(int resultCode, Intent data){
        mediaProjection = manager.getMediaProjection(resultCode, data);
    }

    private void getVirtualDisplay(){
        virtualDisplay = mediaProjection.createVirtualDisplay("demo", Capture.screenW,
                Capture.screenH + Capture.nativeH, Capture.screenDensity,
                DisplayManager.VIRTUAL_DISPLAY_FLAG_AUTO_MIRROR, null,
                null, null);
    }
}
