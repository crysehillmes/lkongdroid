package org.cryse.lkong.ui;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.text.Html;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.util.Log;

import org.cryse.lkong.R;
import org.cryse.lkong.application.LKongApplication;
import org.cryse.lkong.event.AbstractEvent;
import org.cryse.lkong.event.NewPostDoneEvent;
import org.cryse.lkong.model.NewPostResult;
import org.cryse.lkong.utils.DataContract;
import org.cryse.lkong.utils.EmptyImageGetter;
import org.cryse.lkong.utils.htmltextview.ClickableImageSpan;
import org.cryse.lkong.utils.htmltextview.EmoticonImageSpan;
import org.cryse.lkong.utils.htmltextview.HtmlTagHandler;
import org.cryse.lkong.utils.htmltextview.HtmlTextUtils;
import org.cryse.lkong.utils.snackbar.SimpleSnackbarType;

public class NewPostActivity extends AbstractPostActivity {
    public static final String LOG_TAG = NewPostActivity.class.getName();

    String mTitle;
    long mThreadId;
    Long mPostId;
    boolean mIsEditMode = false;
    String mEditHtmlContent;

    @Override
    protected void readDataFromIntent(Intent intent) {
        if (intent.hasExtra(DataContract.BUNDLE_THREAD_ID)) {
            mThreadId = intent.getLongExtra(DataContract.BUNDLE_THREAD_ID, 0);
            mTitle = intent.getStringExtra(DataContract.BUNDLE_POST_REPLY_TITLE);
            if (intent.hasExtra(DataContract.BUNDLE_POST_ID))
                mPostId = intent.getLongExtra(DataContract.BUNDLE_POST_ID, 0);
            else
                mPostId = null;
            if (intent.hasExtra(DataContract.BUNDLE_IS_EDIT_MODE)) {
                mIsEditMode = true;
                mEditHtmlContent = removeLastEditInfo(intent.getStringExtra(DataContract.BUNDLE_EDIT_CONTENT));
            }
        }
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        if (mIsEditMode) {
            Html.ImageGetter imageGetter = new EmptyImageGetter();
            Spanned spannedText = HtmlTextUtils.htmlToSpanned(mEditHtmlContent, imageGetter, new HtmlTagHandler());
            Drawable drawable = getResources().getDrawable(R.drawable.image_placeholder);
            SpannableString spannableString = (SpannableString) replaceImageSpan(drawable, mPostId, spannedText);
            mContentEditText.append(spannableString);
            ImageSpanContainerImpl imageSpanContainer = new ImageSpanContainerImpl(mContentEditText);
            Object[] objects = spannableString.getSpans(0, spannableString.length(), Object.class);
            for (Object spanObj : objects) {
                Log.d("Span Class", spanObj.getClass().getName());
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
            getSendServiceBinder().editPost(mUserAccountManager.getAuthObject(), mThreadId, mPostId, content);
        else
            getSendServiceBinder().sendPost(mUserAccountManager.getAuthObject(), mThreadId, mPostId, content);
    }

    @Override
    protected boolean hasTitleField() {
        return false;
    }

    @Override
    protected String getTitleString() {
        if(isInEditMode()) {
            return getString(R.string.button_edit);
        } else {
            return mTitle;
        }
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
                new Handler().postDelayed(this::closeActivityWithTransition, 300);
            } else {
                if (result != null) {
                    showSnackbar(
                            TextUtils.isEmpty(result.getErrorMessage()) ? getString(R.string.toast_failure_new_post) : result.getErrorMessage(),
                            SimpleSnackbarType.ERROR,
                            SimpleSnackbarType.LENGTH_SHORT
                    );
                } else {
                    showSnackbar(
                            getString(R.string.toast_failure_new_post),
                            SimpleSnackbarType.ERROR,
                            SimpleSnackbarType.LENGTH_SHORT
                    );
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
