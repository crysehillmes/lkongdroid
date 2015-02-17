package org.cryse.lkong.ui;

import android.content.Intent;
import android.os.Handler;
import android.text.TextUtils;

import org.cryse.lkong.R;
import org.cryse.lkong.application.LKongApplication;
import org.cryse.lkong.event.AbstractEvent;
import org.cryse.lkong.event.NewPostDoneEvent;
import org.cryse.lkong.model.NewPostResult;
import org.cryse.lkong.utils.DataContract;
import org.cryse.lkong.utils.ToastProxy;
import org.cryse.lkong.utils.ToastSupport;

public class NewPostActivity extends AbstractPostActivity {
    public static final String LOG_TAG = NewPostActivity.class.getName();

    String mTitle;
    long mThreadId;
    Long mPostId;

    @Override
    protected void readDataFromIntent(Intent intent) {
        if (intent.hasExtra(DataContract.BUNDLE_THREAD_ID)) {
            mThreadId = intent.getLongExtra(DataContract.BUNDLE_THREAD_ID, 0);
            mTitle = intent.getStringExtra(DataContract.BUNDLE_POST_REPLY_TITLE);
            if (intent.hasExtra(DataContract.BUNDLE_POST_ID))
                mPostId = intent.getLongExtra(DataContract.BUNDLE_POST_ID, 0);
            else
                mPostId = null;
        }
    }

    @Override
    protected void sendData(String title, String content) {
        getSendServiceBinder().sendPost(mUserAccountManager.getAuthObject(), mThreadId, mPostId, content);
    }

    @Override
    protected boolean hasTitleField() {
        return false;
    }

    @Override
    protected String getTitleString() {
        return mTitle;
    }

    @Override
    protected String getLogTag() {
        return LOG_TAG;
    }

    @Override
    protected void onSendDataDone(AbstractEvent event) {
        if (event instanceof NewPostDoneEvent) {
            NewPostResult result = ((NewPostDoneEvent) event).getPostResult();
            if (mProgressDialog != null && mProgressDialog.isShowing())
                mProgressDialog.dismiss();
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
