package org.cryse.lkong.broadcast;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.NotificationCompat;

import org.cryse.lkong.R;
import org.cryse.lkong.model.NoticeCountModel;
import org.cryse.lkong.ui.NotificationActivity;
import org.cryse.lkong.utils.DataContract;

public class CheckNewBroadcastReceiver extends BroadcastReceiver {
    public static final int NOTIFICATION_START_ID = 150;
    @Override
    public void onReceive(Context context, Intent intent) {
        long userId = intent.getLongExtra(DataContract.BUNDLE_USER_ID, 0);
        NoticeCountModel noticeCountModel = intent.getParcelableExtra(DataContract.BUNDLE_NOTICE_COUNT_MODEL);
        if(noticeCountModel.hasNotification())
            showNewNoticeNotification(context, userId, noticeCountModel);
    }

    public void showNewNoticeNotification(Context context, long userId, NoticeCountModel noticeCount) {
        StringBuilder stringBuilder = new StringBuilder(60);
        if(noticeCount.getMentionNotice() > 0) {
            stringBuilder.append(
                    context.getString(
                            R.string.format_notice_mentions,
                            noticeCount.getMentionNotice())
            );
        }
        if(noticeCount.getNotice() > 0) {
            stringBuilder
                    .append(stringBuilder.length() > 0 ? "," : "")
                    .append(
                            context.getString(
                                    R.string.format_notice_notice,
                                    noticeCount.getNotice())
                    );
        }
        if(noticeCount.getPrivateMessageNotice() > 0) {
            stringBuilder
                    .append(stringBuilder.length() > 0 ? "," : "")
                    .append(
                            context.getString(
                                    R.string.format_notice_private_message,
                                    noticeCount.getPrivateMessageNotice())
                    );
        }
        if(noticeCount.getRateNotice() > 0) {
            stringBuilder
                    .append(stringBuilder.length() > 0 ? "," : "")
                    .append(
                            context.getString(
                                    R.string.format_notice_rate,
                                    noticeCount.getRateNotice())
                    );
        }

        NotificationManager notificationManager =
                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        NotificationCompat.Builder mResultBuilder = new NotificationCompat.Builder(context);
        Intent openNotificationActivityIntent = new Intent(context, NotificationActivity.class);
        PendingIntent chaptersListIntent =
                PendingIntent.getActivity(context, 0, openNotificationActivityIntent, PendingIntent.FLAG_ONE_SHOT);

        Bundle extras = Bundle.EMPTY;
        mResultBuilder.setContentTitle(context.getString(R.string.format_notice_all_count, noticeCount.getAllNoticeCount()))
                .setContentText(stringBuilder.toString())
                .setSmallIcon(R.drawable.ic_notification_lkong_logo)
                .setExtras(extras)
                .setContentIntent(chaptersListIntent)
                .setAutoCancel(true);
        notificationManager.notify(NOTIFICATION_START_ID + (int)userId, mResultBuilder.build());
    }
}
