package org.cryse.lkong.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import org.cryse.lkong.sync.CheckNoticeSyncAdapter;
import org.cryse.lkong.sync.FollowStatusSyncAdapter;

public class CheckNoticeSyncService extends Service {

    private static final Object sSyncAdapterLock = new Object();
    private static CheckNoticeSyncAdapter sSyncAdapter = null;

    @Override
    public void onCreate() {
        synchronized (sSyncAdapterLock) {
            if (sSyncAdapter == null)
                sSyncAdapter = new CheckNoticeSyncAdapter(getApplicationContext(), true);
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return sSyncAdapter.getSyncAdapterBinder();
    }
}
