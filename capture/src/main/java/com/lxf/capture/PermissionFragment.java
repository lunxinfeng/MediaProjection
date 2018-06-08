package com.lxf.capture;

import android.app.Activity;
import android.app.Fragment;
import android.content.Intent;
import android.util.Log;

public class PermissionFragment extends Fragment {
    private static final String TAG = "PermissionFragment";
    private OnResultListener onResultListener;

    public void setResultListener(OnResultListener resultListener) {
        this.onResultListener = resultListener;
    }

    public void requestCapture() {
        if (Capture.manager != null) {
            startActivityForResult(Capture.manager.createScreenCaptureIntent(), 1);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 1 && resultCode == Activity.RESULT_OK) {
            Log.d(TAG, "onActivityResult: permission request success..");
            if (onResultListener!=null)
                onResultListener.onActivityResult(requestCode,resultCode,data);
        } else {
            Log.d(TAG, "onActivityResult: permission request defined..");
        }
    }

    interface OnResultListener{
        void onActivityResult(int requestCode, int resultCode, Intent data);
    }
}
