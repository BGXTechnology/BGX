package com.setting;

import android.content.Intent;
import android.net.Uri;
import android.provider.Settings;

import com.facebook.react.bridge.Callback;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;

public class CheckCameraAndPhotoHelperModule extends ReactContextBaseJavaModule {

    private ReactContext mReactContext;

    public CheckCameraAndPhotoHelperModule(ReactApplicationContext reactContext) {
        super(reactContext);
        this.mReactContext = reactContext;
    }

    @Override
    public String getName() {
        return "CheckCameraAndPhotoHelper";
    }

    @ReactMethod
    public void goToCameraPhotoSetting(Callback setting) {
        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
        intent.setData(Uri.parse("package:" + mReactContext.getPackageName()));
        if (intent.resolveActivity(mReactContext.getPackageManager()) != null) {
            mReactContext.startActivity(intent);
        }
        setting.invoke(true);
    }
}
