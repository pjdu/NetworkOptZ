package com.hbmcc.shilinlin.networkoptz.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import com.hbmcc.shilinlin.networkoptz.telephony.NetworkStatus;

public class NetworkClient extends Service {
    NetworkStatus networkStatus;
    public NetworkClient() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        networkStatus = new NetworkStatus();
    }
}
