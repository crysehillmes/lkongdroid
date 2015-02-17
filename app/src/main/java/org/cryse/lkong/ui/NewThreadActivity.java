package org.cryse.lkong.ui;
import android.content.Intent;
import android.os.Handler;
import android.text.TextUtils;

import org.cryse.lkong.R;
import org.cryse.lkong.application.LKongApplication;
import org.cryse.lkong.event.AbstractEvent;
import org.cryse.lkong.event.NewThreadDoneEvent;
import org.cryse.lkong.model.NewThreadResult;
import org.cryse.lkong.utils.DataContract;
import org.cryse.lkong.utils.ToastProxy;
import org.cryse.lkong.utils.ToastSupport;

public class NewThreadActivity extends AbstractPostActivity {
    public static final String LOG_TAG = NewThreadActivity.class.getName();

    private long mForumId;
    private String mForumName;

    @Override
    protected void readDataFromIntent(Intent intent) {
        if(intent.hasExtra(DataContract.BUNDLE_FORUM_ID)) {
            mForumId = intent.getLongExtra(DataContract.BUNDLE_FORUM_ID, 0);
            mForumName = intent.getStringExtra(DataContract.BUNDLE_FORUM_NAME);
        }
    }

    @Override
    protected void sendData(String title, String content) {
        getSendServiceBinder().sendThread(mUserAccountManager.getAuthObject(), title, mForumId, content, false);
    }

    @Override
    protected boolean hasTitleField() {
        return true;
    }

    @Override
    protected String getTitleString() {
        return mForumName;
    }

    @Override
    protected String getLogTag() {
        return LOG_TAG;
    }

    @Override
    protected void onSendDataDone(AbstractEvent event) {
        if (event instanceof NewThreadDoneEvent) {
            NewThreadResult result = ((NewThreadDoneEvent) event).getNewThreadResult();
            if (result != null && result.isSuccess()) {
                new Handler().postDelayed(this::finishCompat, 300);

            } else {
                if (result != null) {
                    ToastProxy.showToast(this, TextUtils.isEmpty(result.getErrorMessage()) ? getString(R.string.toast_failure_new_post) : result.getErrorMessage(), ToastSupport.TOAST_ALERT);
                } else {
                    ToastProxy.showToast(this, getString(R.string.toast_failure_new_post), ToastSupport.TOAST_ALERT);
                }
            }
        }
    }

    @Override
    protected void injectThis() {
        LKongApplication.get(this).lKongPresenterComponent().inject(this);
    }
}
