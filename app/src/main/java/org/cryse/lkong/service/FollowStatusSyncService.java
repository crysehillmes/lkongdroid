package org.cryse.lkong.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import org.cryse.lkong.sync.FollowStatusSyncAdapter;

public class FollowStatusSyncService extends Service {

    private static final Object sSyncAdapterLock = new Object();
    private static FollowStatusSyncAdapter sSyncAdapter = null;

    @Override
    public void onCreate() {
        synchronized (sSyncAdapterLock) {
            if (sSyncAdapter == null)
                sSyncAdapter = new FollowStatusSyncAdapter(getApplicationContext(), true);
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return sSyncAdapter.getSyncAdapterBinder();
    }
}
