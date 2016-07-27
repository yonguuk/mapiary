package com.yonguk.test.activity.mapiary.mindwave;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

public class BluetoothService extends Service {
    public BluetoothService() {
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.i("uks","onCreate()");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.i("uks","onStartCommand()");
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.i("uks","onDestroy()");
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }
}
