package org.cryse.lkong.ui;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.text.Html;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;

import org.cryse.lkong.R;
import org.cryse.lkong.application.LKongApplication;
import org.cryse.lkong.event.AbstractEvent;
import org.cryse.lkong.event.NewThreadDoneEvent;
import org.cryse.lkong.model.NewThreadResult;
import org.cryse.lkong.utils.DataContract;
import org.cryse.lkong.utils.EmptyImageGetter;
import org.cryse.lkong.utils.ToastProxy;
import org.cryse.lkong.utils.ToastSupport;
import org.cryse.lkong.utils.htmltextview.ClickableImageSpan;
import org.cryse.lkong.utils.htmltextview.EmoticonImageSpan;
import org.cryse.lkong.utils.htmltextview.HtmlTagHandler;
import org.cryse.lkong.utils.htmltextview.HtmlTextUtils;

public class NewThreadActivity extends AbstractPostActivity {
    public static final String LOG_TAG = NewThreadActivity.class.getName();

    private long mForumId;
    private String mForumName;
    boolean mIsEditMode = false;
    String mEditTitle;
    String mEditHtmlContent;
    long mThreadId;
    long mPostId;

    @Override
    protected void readDataFromIntent(Intent intent) {
        if(intent.hasExtra(DataContract.BUNDLE_FORUM_ID)) {
            mForumId = intent.getLongExtra(DataContract.BUNDLE_FORUM_ID, 0);
            mForumName = intent.getStringExtra(DataContract.BUNDLE_FORUM_NAME);
        } else if(intent.hasExtra(DataContract.BUNDLE_IS_EDIT_MODE)) {
            mIsEditMode = true;
            mEditTitle = intent.getStringExtra(DataContract.BUNDLE_EDIT_TITLE);
            mEditHtmlContent = removeLastEditInfo(intent.getStringExtra(DataContract.BUNDLE_EDIT_CONTENT));
            mThreadId = intent.getLongExtra(DataContract.BUNDLE_THREAD_ID, 0l);
            mPostId = intent.getLongExtra(DataContract.BUNDLE_POST_ID, 0l);
        }
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        if (mIsEditMode) {
            mTitleEditText.setText(mEditTitle);
            Html.ImageGetter imageGetter = new EmptyImageGetter();
            Spanned spannedText = HtmlTextUtils.htmlToSpanned(mEditHtmlContent, imageGetter, new HtmlTagHandler());
            Drawable drawable = getResources().getDrawable(R.drawable.image_placeholder);
            SpannableString spannableString = (SpannableString) replaceImageSpan(drawable, mPostId, spannedText);
            mContentEditText.append(spannableString);
            ImageSpanContainerImpl imageSpanContainer = new ImageSpanContainerImpl(mContentEditText);
            Object[] objects = spannableString.getSpans(0, spannableString.length(), Object.class);
            for (Object spanObj : objects) {
                if (spanObj instanceof ClickableImageSpan) {
                    ((ClickableImageSpan) spanObj).loadImage(imageSpanContainer);
                } else if (spanObj instanceof EmoticonImageSpan) {
                    ((EmoticonImageSpan) spanObj).loadImage(imageSpanContainer);
                }
            }
        }
        mContentEditTextHandler = new ImageEditTextHandler(mContentEditText);
    }

    @Override
    protected void sendData(String title, String content) {
        if(mIsEditMode)
            getSendServiceBinder().editThread(mUserAccountManager.getAuthObject(), mThreadId, mPostId, title, content);
        else
            getSendServiceBinder().sendThread(mUserAccountManager.getAuthObject(), title, mForumId, content, false);
    }

    @Override
    protected boolean hasTitleField() {
        return true;
    }

    @Override
    protected String getTitleString() {
        if(isInEditMode()) {
            return getString(R.string.button_edit);
        } else {
            return mForumName;
        }
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
    protected boolean isInEditMode() {
        return mIsEditMode;
    }

    @Override
    protected void injectThis() {
        LKongApplication.get(this).lKongPresenterComponent().inject(this);
    }
}
