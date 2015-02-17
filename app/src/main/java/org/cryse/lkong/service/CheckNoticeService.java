package org.cryse.lkong.service;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

import org.cryse.lkong.application.LKongApplication;
import org.cryse.lkong.event.NoticeCountEvent;
import org.cryse.lkong.event.RxEventBus;
import org.cryse.lkong.logic.restservice.LKongRestService;
import org.cryse.lkong.model.NoticeCountModel;
import org.cryse.lkong.utils.LKAuthObject;

import javax.inject.Inject;

public class CheckNoticeService extends Service {
    private static final String LOG_TAG = CheckNoticeService.class.getName();
    @Inject
    LKongRestService mLKRestService;

    @Inject
    RxEventBus mEventBus;
    boolean mIsStopingService;

    @Override
    public void onCreate() {
        super.onCreate();
        LKongApplication.get(this).sendServiceComponet().inject(this);
    }

    @Override
    public void onDestroy() {
        mIsStopingService = true;
        super.onDestroy();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return new CheckNoticeCountServiceBinder();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return super.onStartCommand(intent, flags, startId);
    }
    public void checkNew(LKAuthObject authObject) {
        new Thread(() -> {
            try {
                NoticeCountModel result = mLKRestService.checkNoticeCount(authObject);
                if (result != null && result.isSuccess()) {
                    mEventBus.sendEvent(new NoticeCountEvent(result));
                }
            } catch (Exception e) {
                Log.e(LOG_TAG, "sendThread error", e);
            }
        }).start();
    }

    public class CheckNoticeCountServiceBinder extends Binder {

        public void checkNoticeCount(LKAuthObject authObject) {
            checkNew(authObject);
        }
    }


}
