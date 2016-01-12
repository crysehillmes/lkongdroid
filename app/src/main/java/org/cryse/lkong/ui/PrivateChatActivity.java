package org.cryse.lkong.ui;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.WindowManager;

import org.cryse.lkong.application.LKongApplication;
import org.cryse.lkong.utils.DataContract;

public class PrivateChatActivity extends SimpleContainerActivity {
    private static final String LOG_TAG = NotificationActivity.class.getName();
    long mTargetUserId;
    String mTargetUserName;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void injectThis() {
        LKongApplication.get(this).lKongPresenterComponent().inject(this);
    }

    @Override
    protected String getLogTag() {
        return LOG_TAG;
    }

    @Override
    protected Fragment newFragment() {
        mTargetUserId = getIntent().getLongExtra(DataContract.BUNDLE_TARGET_USER_ID, 0);
        mTargetUserName = getIntent().getStringExtra(DataContract.BUNDLE_TARGET_USER_NAME);
        Bundle bundle = new Bundle();
        bundle.putLong(DataContract.BUNDLE_TARGET_USER_ID, mTargetUserId);
        bundle.putString(DataContract.BUNDLE_TARGET_USER_NAME, mTargetUserName);
        return PrivateChatFragment.newInstance(bundle);
    }

    @Override
    protected void requestWindowFeatures() {
        super.requestWindowFeatures();
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE |
                WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
    }
}