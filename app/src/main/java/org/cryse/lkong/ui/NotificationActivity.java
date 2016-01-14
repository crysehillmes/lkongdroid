package org.cryse.lkong.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;

import org.cryse.lkong.application.LKongApplication;

public class NotificationActivity extends SimpleContainerActivity {
    private static final String LOG_TAG = NotificationActivity.class.getName();

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
        Intent intent = getIntent();
        Bundle extras = intent.getExtras();
        return NotificationFragment.newInstance(extras);
    }
}
