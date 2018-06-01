package com.lxf.capture;

import android.app.Activity;
import android.app.FragmentManager;
import android.content.Context;
import android.util.DisplayMetrics;
import android.view.WindowManager;

public class Capture {
    private static final String TAG = "Capture";
    /**
     * screen width
     */
    public static int screenW;
    /**
     * screen height
     */
    public static int screenH;
    /**
     * screen density
     */
    public static int screenDensity;
    /**
     * status bar height
     */
    public static int statusH;
    /**
     * native bar height
     */
    public static int nativeH;
    private Activity activity;

    public Capture(Activity activity) {
        this.activity = activity;
        WindowManager windowManager = (WindowManager) activity.getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics outMetrics = new DisplayMetrics();
        if (windowManager != null) {
            windowManager.getDefaultDisplay().getMetrics(outMetrics);
        }
        screenW = outMetrics.widthPixels;
        screenH = outMetrics.heightPixels;
        screenDensity = outMetrics.densityDpi;

        statusH = DimenUtil.getStatusBarHeight(activity);
        nativeH = DimenUtil.getNavigationBarHeight(activity);
    }

    public void startCapture() {
        getPermissionFragment(activity).requestCapture();
    }

    private PermissionFragment getPermissionFragment(Activity activity) {
        PermissionFragment fragment = findPermissionFragment(activity);
        if (fragment == null) {
            fragment = new PermissionFragment();
            FragmentManager fragmentManager = activity.getFragmentManager();
            fragmentManager.beginTransaction()
                    .add(fragment, TAG)
                    .commitAllowingStateLoss();
            fragmentManager.executePendingTransactions();
        }
        return fragment;
    }

    private PermissionFragment findPermissionFragment(Activity activity) {
        return (PermissionFragment) activity.getFragmentManager().findFragmentByTag(TAG);
    }
}
