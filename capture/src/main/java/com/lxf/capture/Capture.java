package com.lxf.capture;

import android.app.Activity;
import android.app.FragmentManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.PixelFormat;
import android.hardware.display.DisplayManager;
import android.hardware.display.VirtualDisplay;
import android.media.Image;
import android.media.ImageReader;
import android.media.projection.MediaProjection;
import android.media.projection.MediaProjectionManager;
import android.os.SystemClock;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import java.io.Serializable;
import java.nio.ByteBuffer;

public class Capture implements Serializable {
    private static final String TAG = "Capture";
    /**
     * screen width
     */
    private static int screenW;
    /**
     * screen height
     */
    private static int screenH;
    /**
     * screen density
     */
    private static int screenDensity;
    /**
     * status bar height
     */
    private static int statusH;
    /**
     * native bar height
     */
    private static int nativeH;
    static MediaProjectionManager manager;
    private static ImageReader imageReader;
    private static MediaProjection mediaProjection;
    private static VirtualDisplay virtualDisplay;
    private static OnCaptureListener listener;
    private static int failNum;

    public static void init(@NonNull Context application) {
        manager = (MediaProjectionManager) application.getSystemService(Context.MEDIA_PROJECTION_SERVICE);
        int[] dimen = ScreenUtil.getScreenDimen(application);
        screenW = dimen[0];
        screenH = dimen[1];
        screenDensity = dimen[2];
        statusH = ScreenUtil.getStatusBarHeight(application);
        nativeH = ScreenUtil.getNavigationBarHeight(application);
        Log.d(TAG, "init: screenW:" + screenW + ";screenH:" + screenH + ";screenDensity:" + screenDensity + ";statusH:" + statusH + ";nativeH:" + nativeH);
        imageReader = ImageReader.newInstance(screenW, screenH, PixelFormat.RGBA_8888, 1);
    }

    public static void startCapture(Activity activity, @Nullable OnCaptureListener onCaptureListener) {
//        imageReader.setOnImageAvailableListener(new ImageReader.OnImageAvailableListener() {
//            @Override
//            public void onImageAvailable(ImageReader reader) {
//                Log.d(TAG, "onImageAvailable");
//                Bitmap bitmap = screenShot();
//                imageReader.setOnImageAvailableListener(null, null);
//                stopVirtual();
//                if (listener != null)
//                    listener.onScreenShot(bitmap);
//            }
//        }, null);
        listener = onCaptureListener;
        getPermissionFragment(activity).requestCapture();
    }

    public static void stopVirtual() {
        if (virtualDisplay != null) {
            virtualDisplay.release();
        }
    }

    public static void screenShot() {
        Image image = imageReader.acquireNextImage();
        if (image != null) {

            int width = image.getWidth();
            int height = image.getHeight();

            final Image.Plane[] planes = image.getPlanes();
            final ByteBuffer buffer = planes[0].getBuffer();

            int pixelStride = planes[0].getPixelStride();
            int rowStride = planes[0].getRowStride();
            int rowPadding = rowStride - pixelStride * width;

            Bitmap bitmap = Bitmap.createBitmap(width + rowPadding / pixelStride, height, Bitmap.Config.ARGB_8888);
            bitmap.copyPixelsFromBuffer(buffer);
            bitmap = Bitmap.createBitmap(bitmap, 0, 0, width, height);
            image.close();
            if (listener!=null)
                listener.onScreenShot(bitmap);
            Log.d(TAG, "screenShot: get a screenshot");
            failNum = 0;
        } else {
            failNum++;
            Log.d(TAG, "screenShot: is not valid, auto try after 500ms");
            SystemClock.sleep(500);
            if (failNum <= 5)
                screenShot();
            else {
                failNum = 0;
                Log.d(TAG, "screenShot: is not valid, please check your device..");
            }
        }
    }

    private static PermissionFragment getPermissionFragment(final Activity activity) {
        PermissionFragment fragment = findPermissionFragment(activity);
        if (fragment == null) {
            fragment = new PermissionFragment();
            fragment.setResultListener(new PermissionFragment.OnResultListener() {
                @Override
                public void onActivityResult(int requestCode, int resultCode, Intent data) {
                    if (virtualDisplay != null && virtualDisplay.getDisplay().isValid()) {
                        screenShot();
                    } else {
                        getMediaProjection(resultCode, data);
                        getVirtualDisplay(activity);
                        Log.d(TAG, "onActivityResult: " + virtualDisplay);
                        screenShot();
                    }
                }
            });
            FragmentManager fragmentManager = activity.getFragmentManager();
            fragmentManager.beginTransaction()
                    .add(fragment, TAG)
                    .commitAllowingStateLoss();
            fragmentManager.executePendingTransactions();
        }
        return fragment;
    }

    private static PermissionFragment findPermissionFragment(Activity activity) {
        return (PermissionFragment) activity.getFragmentManager().findFragmentByTag(TAG);
    }

    private static void getMediaProjection(int resultCode, Intent data) {
        mediaProjection = manager.getMediaProjection(resultCode, data);
    }

    private static void getVirtualDisplay(Activity activity) {
        int width;
        int height;
        if (ScreenUtil.isLandspace(activity)) {
            width = Capture.screenW + Capture.nativeH;
            height = Capture.screenH;
        } else {
            width = Capture.screenW;
            height = Capture.screenH + Capture.nativeH;
        }
        virtualDisplay = mediaProjection.createVirtualDisplay("demo", width,
                height, Capture.screenDensity,
                DisplayManager.VIRTUAL_DISPLAY_FLAG_AUTO_MIRROR, imageReader.getSurface(),
                null, null);
    }
}
