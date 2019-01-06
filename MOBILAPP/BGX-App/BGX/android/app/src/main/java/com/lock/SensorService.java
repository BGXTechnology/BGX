package com.lock;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.os.SystemClock;
import android.support.annotation.Nullable;
import android.util.Log;

import java.util.Timer;
import java.util.TimerTask;


public class SensorService extends Service {
    private Context ctx;
    //    TimerCounter tc;
    private int counter = 0;
    private static final String TAG = SensorService.class.getSimpleName();

    private Timer timer;
    private TimerTask timerTask;
    private SharedPreferences sharedPreferences;

    public SensorService() {

    }

    public SensorService(Context applicationContext) {
        super();
        ctx = applicationContext;
        Log.i(TAG, "SensorService class");

    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.i(TAG, "onCreate()");
        sharedPreferences = getSharedPreferences("LockTaskService", Context.MODE_PRIVATE);
        counter = sharedPreferences.getInt("counter", 0);

        // set a new timer
        timer = new Timer();
        // initialize the timer task's job
        timerTask = new TimerTask() {
            @Override
            public void run() {
                Log.i("in timer", "in timer ++++ " + (counter++));
                if (counter == 600) {
//                    Intent broadcastIntent = new Intent("com.arvi.ActivityRecognition.RestartSensor");
//                    sendBroadcast(broadcastIntent);
                    stopSelf();
                    // stop the timer, if it's not already null
                    if (timer != null) {
                        timer.cancel();
                        timer = null;
                    }
                    counter = 0;
                    sharedPreferences.edit().putInt("counter", 0).apply();
                }
            }
        };

        // schedule the timer, to wake up every 1 second
        timer.schedule(timerTask, 1000, 1000);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);
        Log.i(TAG, "onStartCommand()");
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        Log.i(TAG, "serviceOnDestroy()");
        super.onDestroy();
    }

    @Override
    public void onTaskRemoved(Intent rootIntent) {
        Log.i(TAG, "serviceonTaskRemoved()");

        // workaround for kitkat: set an alarm service to trigger service again
        Intent intent = new Intent(getApplicationContext(), SensorService.class);
        PendingIntent pendingIntent = PendingIntent.getService(this, 1, intent, PendingIntent.FLAG_ONE_SHOT);
        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        alarmManager.set(AlarmManager.RTC_WAKEUP, SystemClock.elapsedRealtime() + 5000, pendingIntent);
//        counter = 0;
        sharedPreferences.edit().putInt("counter", counter).apply();
        super.onTaskRemoved(rootIntent);

    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        Log.i(TAG, "onLowMemory()");
    }
}
