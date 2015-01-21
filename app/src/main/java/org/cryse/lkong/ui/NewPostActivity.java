package org.cryse.lkong.ui;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.text.Spannable;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;

import org.cryse.lkong.R;
import org.cryse.lkong.application.LKongApplication;
import org.cryse.lkong.application.UserAccountManager;
import org.cryse.lkong.model.NewPostResult;
import org.cryse.lkong.presenter.NewPostPresenter;
import org.cryse.lkong.ui.common.AbstractThemeableActivity;
import org.cryse.lkong.utils.DataContract;
import org.cryse.lkong.utils.ToastProxy;
import org.cryse.lkong.utils.ToastSupport;
import org.cryse.lkong.view.NewPostView;
import org.cryse.utils.ColorUtils;

import javax.inject.Inject;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class NewPostActivity extends AbstractThemeableActivity implements NewPostView {
    @Inject
    NewPostPresenter mPresenter;

    @Inject
    UserAccountManager mUserAccountManager;

    @InjectView(R.id.activity_new_post_edittext_content)
    EditText mContentEditText;

    String mTitle;
    long mThreadId;
    Long mPostId;


    ProgressDialog mProgressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        injectThis();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_post);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDefaultDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
            getWindow().setStatusBarColor(ColorUtils.getColorFromAttr(this, R.attr.colorPrimaryDark));
        ButterKnife.inject(this);
        Intent intent = getIntent();
        if(intent.hasExtra(DataContract.BUNDLE_THREAD_ID)) {
            mThreadId = intent.getLongExtra(DataContract.BUNDLE_THREAD_ID, 0);
            mTitle = intent.getStringExtra(DataContract.BUNDLE_POST_REPLY_TITLE);
            if(intent.hasExtra(DataContract.BUNDLE_POST_ID))
                mPostId = intent.getLongExtra(DataContract.BUNDLE_POST_ID, 0);
            else
                mPostId = null;
        }
        setTitle(mTitle);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_new_post, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_send_post:
                submitPost();
                return true;
            case R.id.action_change_theme:
                setNightMode(!isNightMode());
                return true;
            case android.R.id.home:
                if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
                    finishAfterTransition();
                else
                    finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        getPresenter().destroy();
    }

    @Override
    protected void onStop() {
        super.onStop();
        getPresenter().unbindView();
    }

    @Override
    protected void onStart() {
        super.onStart();
        getPresenter().bindView(this);
    }

    @Override
    protected void injectThis() {
        LKongApplication.get(this).lKongPresenterComponent().inject(this);
    }

    private void submitPost() {
        mContentEditText.clearFocus();
        Spannable spannableContent = mContentEditText.getText();
        if(spannableContent != null && spannableContent.length() > 0) {
            mProgressDialog = ProgressDialog.show(this, getString(R.string.dialog_new_post_sending), "");
            getPresenter().newPost(mUserAccountManager.getAuthObject(), mThreadId, mPostId, android.text.Html.toHtml(spannableContent));
        } else {
            ToastProxy.showToast(this, "Empty content.", ToastSupport.TOAST_ALERT);
        }
    }

    public NewPostPresenter getPresenter() {
        return mPresenter;
    }

    @Override
    public void onPostComplete(NewPostResult result) {
        if(mProgressDialog != null)
            mProgressDialog.dismiss();
        if(result != null && result.isSuccess()) {
            Log.d("NewPostActivity::onPostComplete()", "success");
            Intent intent = new Intent();
            intent.putExtra(DataContract.BUNDLE_THREAD_PAGE_COUNT, result.getPageCount());
            intent.putExtra(DataContract.BUNDLE_THREAD_REPLY_COUNT, result.getReplyCount());
            NewPostActivity.this.setResult(RESULT_OK, intent);
            NewPostActivity.this.finish();

        } else {
            ToastProxy.showToast(this, TextUtils.isEmpty(result.getErrorMessage()) ? "Error" : result.getErrorMessage(), ToastSupport.TOAST_ALERT);
        }
    }
}
