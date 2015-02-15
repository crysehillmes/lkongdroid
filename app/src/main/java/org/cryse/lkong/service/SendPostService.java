package org.cryse.lkong.service;

import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Binder;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import org.apache.commons.lang3.StringEscapeUtils;
import org.cryse.lkong.R;
import org.cryse.lkong.application.LKongApplication;
import org.cryse.lkong.event.NewPostDoneEvent;
import org.cryse.lkong.event.NewThreadDoneEvent;
import org.cryse.lkong.event.RxEventBus;
import org.cryse.lkong.logic.restservice.LKongRestService;
import org.cryse.lkong.model.NewPostResult;
import org.cryse.lkong.model.NewThreadResult;
import org.cryse.lkong.service.task.SendPostTask;
import org.cryse.lkong.service.task.SendTask;
import org.cryse.lkong.service.task.SendThreadTask;
import org.cryse.lkong.utils.ContentProcessor;
import org.cryse.lkong.utils.LKAuthObject;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import javax.inject.Inject;

public class SendPostService extends Service {
    private static final String LOG_TAG = SendPostService.class.getName();
    @Inject
    LKongRestService mLKRestService;

    @Inject
    RxEventBus mEventBus;

    BlockingQueue<SendTask> mTaskQueue = new LinkedBlockingQueue<SendTask>();
    SendTask mCurrentTask = null;
    public static final int NOTIFICATION_START_ID = 150;
    public int notification_count = 0;

    NotificationManager mNotifyManager;
    static final int SENDING_NOTIFICATION_ID = 110;

    boolean stopCurrentTask = false;
    Thread mCachingThread;
    boolean mIsStopingService;

    @Override
    public void onCreate() {
        super.onCreate();
        LKongApplication.get(this).sendServiceComponet().inject(this);
        mNotifyManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        mCachingThread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    while (!mIsStopingService) {
                        mCurrentTask = mTaskQueue.take();
                        if (mCurrentTask instanceof SendPostTask) {
                            sendPost((SendPostTask) mCurrentTask);
                        } else if (mCurrentTask instanceof SendThreadTask) {
                            sendThread((SendThreadTask) mCurrentTask);
                        }
                        mCurrentTask = null;
                    }
                } catch (InterruptedException ex) {
                    Log.e(LOG_TAG, "Caching thread exception.", ex);
                }
            }
        });
        mCachingThread.start();
    }

    @Override
    public void onDestroy() {
        mIsStopingService = true;
        super.onDestroy();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return new SendPostServiceBinder();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(LOG_TAG, "onStartCommand");
        if (intent != null && intent.hasExtra("type")) {

            Log.d(LOG_TAG, String.format("onStartCommand: %s", intent.getStringExtra("type")));
            if ("cancel_current".compareTo(intent.getStringExtra("type")) == 0) {
                Log.d(LOG_TAG, "onStartCommand, type: cancel_current");
                stopCurrentTask = true;
            } else if ("cancel_all".compareTo(intent.getStringExtra("type")) == 0) {
                Log.d(LOG_TAG, "onStartCommand, type: cancel_all");
                mTaskQueue.clear();
                stopCurrentTask = true;
            }
        }
        return super.onStartCommand(intent, flags, startId);
    }

    public void sendPost(SendPostTask task) {
        Log.d(LOG_TAG, "sendPost");
        NotificationCompat.Builder progressNotificationBuilder;

        progressNotificationBuilder = new NotificationCompat.Builder(SendPostService.this);
        progressNotificationBuilder.setContentTitle(getResources().getString(R.string.notification_title_sending_post))
                .setContentText("")
                .setSmallIcon(R.drawable.ic_action_send)
                .setOngoing(true);

        startForeground(SENDING_NOTIFICATION_ID, progressNotificationBuilder.build());
        mNotifyManager.notify(SENDING_NOTIFICATION_ID, progressNotificationBuilder.build());
        NewPostResult postResult = null;
        String replaceResult = preprocessContent(task.getAuthObject(), task.getContent());
        try {
            postResult = mLKRestService.newPostReply(task.getAuthObject(), task.getTid(), task.getPid(), replaceResult);
        } catch (Exception e) {
            Log.e(LOG_TAG, "sendPost error", e);
        } finally {
            mNotifyManager.cancel(SENDING_NOTIFICATION_ID);
            stopForeground(true);
            // showSendPostTaskResultNotification(postResult);
            if(postResult != null && postResult.isSuccess()) {
                mEventBus.sendEvent(new NewPostDoneEvent(postResult));
            }
        }
    }

    public void sendThread(SendThreadTask task) {
        Log.d(LOG_TAG, "sendPost");
        NotificationCompat.Builder progressNotificationBuilder;

        progressNotificationBuilder = new NotificationCompat.Builder(SendPostService.this);
        progressNotificationBuilder.setContentTitle(getResources().getString(R.string.notification_title_sending_post))
                .setContentText("")
                .setSmallIcon(R.drawable.ic_action_send)
                .setOngoing(true);

        startForeground(SENDING_NOTIFICATION_ID, progressNotificationBuilder.build());
        mNotifyManager.notify(SENDING_NOTIFICATION_ID, progressNotificationBuilder.build());
        NewThreadResult threadResult = null;
        String replaceResult = preprocessContent(task.getAuthObject(), task.getContent());
        try {
            threadResult = mLKRestService.newPostThread(task.getAuthObject(), task.getTitle(), task.getFid(), replaceResult, task.isFollow());
        } catch (Exception e) {
            Log.e(LOG_TAG, "sendThread error", e);
        } finally {
            mNotifyManager.cancel(SENDING_NOTIFICATION_ID);
            stopForeground(true);
            // showSendThreadTaskResultNotification(threadResult);
            if(threadResult != null && threadResult.isSuccess()) {
                mEventBus.sendEvent(new NewThreadDoneEvent(threadResult));
            }
        }
    }

    private String preprocessContent(LKAuthObject authObject, String content) {
        String unescapedContent = StringEscapeUtils.unescapeHtml4(content);
        ContentProcessor contentProcessor = new ContentProcessor(unescapedContent);
        contentProcessor.setUploadImageCallback(path -> {
            String uploadUrl = "";
            try {
                Log.d(LOG_TAG, "setUploadImageCallback start");
                uploadUrl = mLKRestService.uploadImageToLKong(authObject, path);
                Log.d(LOG_TAG, String.format("uploadImageToLKong result %s", uploadUrl));
            } catch (Exception ex) {
                Log.e(LOG_TAG, "uploadImageToLKong failed", ex);
            } finally {
                return uploadUrl;
            }
        });
        contentProcessor.run();
        String replaceResult = contentProcessor.getResultContent();

        Log.d(LOG_TAG, replaceResult);
        return replaceResult;
    }

    private void showSendPostTaskResultNotification(NewPostResult newPostResult) {
        notification_count = notification_count + 1;
        NotificationCompat.Builder mResultBuilder = new NotificationCompat.Builder(this);
        Bundle extras = new Bundle();
        if (newPostResult != null && newPostResult.isSuccess()) {
            extras.putLong("tid", newPostResult.getTid());
            extras.putLong("reply_count", newPostResult.getReplyCount());
            mResultBuilder.setContentTitle(getString(R.string.notification_title_sending_post_successfully))
                    .setContentText("")
                    .setSmallIcon(R.drawable.ic_notification_done)
                    .setExtras(extras)
                    .setAutoCancel(true);
        } else {
            mResultBuilder.setContentTitle(getString(R.string.notification_title_sending_post_failed))
                    .setContentText(newPostResult != null ? newPostResult.getErrorMessage() : getString(R.string.notification_content_network_error))
                    .setSmallIcon(R.drawable.ic_notification_error)
                    .setExtras(extras)
                    .setAutoCancel(true);
        }

        mNotifyManager.notify(NOTIFICATION_START_ID + notification_count, mResultBuilder.build());
    }

    private void showSendThreadTaskResultNotification(NewThreadResult newThreadResult) {
        notification_count = notification_count + 1;
        NotificationCompat.Builder mResultBuilder = new NotificationCompat.Builder(this);
        Bundle extras = new Bundle();
        if (newThreadResult != null && newThreadResult.isSuccess()) {
            mResultBuilder.setContentTitle(getString(R.string.notification_title_sending_thread_successfully))
                    .setContentText("")
                    .setSmallIcon(R.drawable.ic_notification_done)
                    .setExtras(extras)
                    .setAutoCancel(true);
        } else {
            mResultBuilder.setContentTitle(getString(R.string.notification_title_sending_thread_failed))
                    .setContentText(newThreadResult != null ? newThreadResult.getErrorMessage() : getString(R.string.notification_content_network_error))
                    .setSmallIcon(R.drawable.ic_notification_error)
                    .setExtras(extras)
                    .setAutoCancel(true);
        }

        mNotifyManager.notify(NOTIFICATION_START_ID + notification_count, mResultBuilder.build());
    }

    public class SendPostServiceBinder extends Binder {
        public boolean hasSendingTask() {
            return mCurrentTask != null;
        }

        public void sendPost(LKAuthObject authObject, long tid, Long pid, String content) {
            SendPostTask task = new SendPostTask();
            task.setAuthObject(authObject);
            task.setTid(tid);
            task.setPid(pid);
            task.setContent(content);
            mTaskQueue.add(task);
        }

        public void sendThread(LKAuthObject authObject, String title, long fid, String content, boolean follow) {
            SendThreadTask task = new SendThreadTask();
            task.setAuthObject(authObject);
            task.setTitle(title);
            task.setFid(fid);
            task.setContent(content);
            task.setFollow(follow);
            mTaskQueue.add(task);
        }
    }


}
