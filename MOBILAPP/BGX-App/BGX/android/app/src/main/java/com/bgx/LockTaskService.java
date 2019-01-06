//package com.bgx;
//
//import android.content.Intent;
//import android.os.Bundle;
//import android.util.Log;
//
//import com.facebook.react.HeadlessJsTaskService;
//import com.facebook.react.bridge.Arguments;
//import com.facebook.react.jstasks.HeadlessJsTaskConfig;
//
//import javax.annotation.Nullable;
//
//public class LockTaskService extends HeadlessJsTaskService {
//
//    private final String TASK_ID = "LockTaskService";
//
//    @Nullable
//    @Override
//    protected HeadlessJsTaskConfig getTaskConfig(Intent intent) {
//        Log.e("HeadlessJsTaskConfig", "HeadlessJsTaskConfig");
//        Bundle extras = intent.getExtras();
//        return new HeadlessJsTaskConfig(
//                TASK_ID,
//                extras != null ? Arguments.fromBundle(extras) : null,
//                300000,
//                true);
//    }
//
//    @Override
//    public void onCreate() {
//        super.onCreate();
//        Log.e("checkServiceStop", "onCreate");
//    }
//
//    @Override
//    public void onHeadlessJsTaskFinish(int taskId) {
//        super.onHeadlessJsTaskFinish(taskId);
//        Log.e("checkServiceStop LockTaskService", "stop");
//    }
//
//    @Override
//    public void onDestroy() {
//        super.onDestroy();
//        Log.e("checkServiceStop LockTaskService", "onDestroy");
//    }
//
//    @Override
//    public void onHeadlessJsTaskStart(int taskId) {
//        super.onHeadlessJsTaskFinish(taskId);
//        Log.e("checkServiceStop LockTaskService", "start");
//        Log.e("checkServiceStop taskId", taskId + "");
//    }
//}
