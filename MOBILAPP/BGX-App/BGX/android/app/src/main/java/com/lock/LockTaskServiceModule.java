package com.lock;

import android.app.ActivityManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AlertDialog;
import android.util.Log;

import com.bgx.R;
import com.facebook.react.bridge.Callback;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;

public class LockTaskServiceModule extends ReactContextBaseJavaModule {
    private static final String TAG = LockTaskServiceModule.class.getSimpleName();

    private SensorService mSensorService;
    Intent mServiceIntent;

    public LockTaskServiceModule(ReactApplicationContext reactContext) {
        super(reactContext);
    }

    @Override
    public String getName() {
        return "LockTaskService";
    }

    @ReactMethod
    public void startServiceLockDevice() {
        SharedPreferences sharedPreferences = getReactApplicationContext().getSharedPreferences(getReactApplicationContext().getString(R.string.SHARED_PREF), Context.MODE_PRIVATE);
        if ("huawei".equalsIgnoreCase(android.os.Build.MANUFACTURER) && !sharedPreferences.getBoolean(getReactApplicationContext().getString(R.string.PREF_IS_PROTECTED_APPS), false)) {
            AlertDialog.Builder builder = new AlertDialog.Builder(getReactApplicationContext());
            builder.setTitle(R.string.huawei_title).setMessage(R.string.huawei_text)
                    .setPositiveButton(R.string.go_to_protected, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            Intent intent = new Intent();
                            intent.setComponent(new ComponentName("com.huawei.systemmanager", "com.huawei.systemmanager.optimize.process.ProtectActivity"));
                            getCurrentActivity().startActivity(intent);
                            SharedPreferences.Editor editor = getReactApplicationContext().getSharedPreferences(getReactApplicationContext().getString(R.string.SHARED_PREF), Context.MODE_PRIVATE).edit();
                            editor.putBoolean(getReactApplicationContext().getString(R.string.PREF_IS_PROTECTED_APPS), true).commit();
                        }
                    }).create().show();
        }

        mSensorService = new SensorService(getReactApplicationContext());
        mServiceIntent = new Intent(getReactApplicationContext(), mSensorService.getClass());
        if (!isMyServiceRunning(mSensorService.getClass())) {
            getReactApplicationContext().startService(mServiceIntent);
        }
    }

    private boolean isMyServiceRunning(Class <?> serviceClass) {
        ActivityManager manager = (ActivityManager) getReactApplicationContext().getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if(serviceClass.getName().equals(service.service.getClassName())) {
                Log.i(TAG, "isMyServiceRunning? " + true + "");
                return true;
            }
        }

        Log.i(TAG, "isMyServiceRunning? " + false + "");
        return false;
    }

    @ReactMethod
    public void checkServiceStop(Callback callback) {
        ActivityManager manager = (ActivityManager) getReactApplicationContext().getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (SensorService.class.getName().equals(service.service.getClassName())) {
                callback.invoke(true);
                return;
            }
        }
        callback.invoke(false);
    }

}
