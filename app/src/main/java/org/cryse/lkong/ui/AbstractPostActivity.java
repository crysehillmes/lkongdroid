package org.cryse.lkong.ui;

import android.app.ProgressDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.os.Parcelable;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.SpanWatcher;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.style.DynamicDrawableSpan;
import android.text.style.ImageSpan;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RelativeLayout;

import com.squareup.picasso.Picasso;

import org.cryse.lkong.R;
import org.cryse.lkong.application.UserAccountManager;
import org.cryse.lkong.application.qualifier.PrefsPostTail;
import org.cryse.lkong.event.AbstractEvent;
import org.cryse.lkong.event.EditPostDoneEvent;
import org.cryse.lkong.event.NewPostDoneEvent;
import org.cryse.lkong.event.NewThreadDoneEvent;
import org.cryse.lkong.model.EditPostResult;
import org.cryse.lkong.service.SendPostService;
import org.cryse.lkong.ui.common.AbstractThemeableActivity;
import org.cryse.lkong.ui.dialog.EmoticonDialog;
import org.cryse.lkong.utils.AnalyticsUtils;
import org.cryse.lkong.utils.ContentProcessor;
import org.cryse.lkong.utils.ContentUriPathUtils;
import org.cryse.lkong.utils.PostTailUtils;
import org.cryse.lkong.utils.ToastProxy;
import org.cryse.lkong.utils.ToastSupport;
import org.cryse.lkong.utils.UIUtils;
import org.cryse.lkong.utils.htmltextview.ClickableImageSpan;
import org.cryse.lkong.utils.htmltextview.EmoticonImageSpan;
import org.cryse.lkong.utils.htmltextview.ImageSpanContainer;
import org.cryse.utils.preference.StringPreference;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.Executors;

import javax.inject.Inject;

import butterknife.ButterKnife;
import butterknife.InjectView;

public abstract class AbstractPostActivity extends AbstractThemeableActivity {

    @Inject
    UserAccountManager mUserAccountManager;

    @Inject
    @PrefsPostTail
    StringPreference mPostTailText;

    @InjectView(R.id.activity_new_thread_edittext_title)
    EditText mTitleEditText;
    @InjectView(R.id.activity_new_thread_edittext_content)
    EditText mContentEditText;

    @InjectView(R.id.action_insert_emoji)
    ImageButton mInsertEmoticonButton;
    @InjectView(R.id.action_insert_image)
    ImageButton mInsertImageButton;

    ImageEditTextHandler mContentEditTextHandler;
    ProgressDialog mProgressDialog;
    ServiceConnection mBackgroundServiceConnection;
    private SendPostService.SendPostServiceBinder mSendServiceBinder;
    protected Picasso mPicasso;
    protected abstract void readDataFromIntent(Intent intent);
    protected abstract void sendData(String title, String content);
    protected abstract boolean hasTitleField();
    protected abstract String getTitleString();
    protected abstract String getLogTag();
    protected abstract void onSendDataDone(AbstractEvent event);
    protected abstract boolean isInEditMode();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        injectThis();
        super.onCreate(savedInstanceState);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE |
                WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        setContentView(R.layout.activity_new_thread);
        setUpToolbar(R.id.my_awesome_toolbar, R.id.toolbar_shadow);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDefaultDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        ButterKnife.inject(this);
        mPicasso = new Picasso.Builder(this).executor(Executors.newSingleThreadExecutor()).build();
        mTitleEditText.setVisibility(hasTitleField() ? View.VISIBLE : View.GONE);
        if(!hasTitleField()) {
            RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams)mContentEditText.getLayoutParams();
            int actionBarSize = UIUtils.calculateActionBarSize(this);
            layoutParams.setMargins(0, actionBarSize, 0, actionBarSize);
        }
        readDataFromIntent(getIntent());
        setTitle(getTitleString());
        mBackgroundServiceConnection = new ServiceConnection() {
            @Override
            public void onServiceConnected(ComponentName name, IBinder service) {
                mSendServiceBinder = (SendPostService.SendPostServiceBinder) service;
            }

            @Override
            public void onServiceDisconnected(ComponentName name) {
                mSendServiceBinder = null;
            }
        };
        mInsertEmoticonButton.setOnClickListener(view -> insertEmoticon());
        mInsertImageButton.setOnClickListener(view -> openImageIntent());
    }

    @Override
    protected void onEvent(AbstractEvent event) {
        super.onEvent(event);
        if (event instanceof NewThreadDoneEvent || event instanceof NewPostDoneEvent) {
            onSendDataDone(event);
        } else if(event instanceof EditPostDoneEvent) {
            onEditDone((EditPostDoneEvent) event);
        }
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
                finishCompat();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(mProgressDialog != null && mProgressDialog.isShowing())
            mProgressDialog.dismiss();
        if(mPicasso != null)
            mPicasso.shutdown();
    }

    @Override
    protected void onStop() {
        super.onStop();
        this.unbindService(mBackgroundServiceConnection);
    }

    @Override
    protected void onStart() {
        super.onStart();
        Intent service = new Intent(this.getApplicationContext(), SendPostService.class);
        this.bindService(service, mBackgroundServiceConnection, Context.BIND_AUTO_CREATE);
    }

    @Override
    protected void analyticsTrackEnter() {
        AnalyticsUtils.trackActivityEnter(this, getLogTag());
    }

    @Override
    protected void analyticsTrackExit() {
        AnalyticsUtils.trackActivityExit(this, getLogTag());
    }

    private void submitPost() {
        mTitleEditText.clearFocus();
        mContentEditText.clearFocus();
        String title = mTitleEditText.getText().toString();
        Editable spannableContent = mContentEditText.getText();
        if(!TextUtils.isEmpty(spannableContent)) {
            if(hasTitleField() && TextUtils.isEmpty(title)) {
                ToastProxy.showToast(this, getString(R.string.toast_error_title_empty), ToastSupport.TOAST_ALERT);
                return;
            }
            if (mSendServiceBinder != null) {
                mProgressDialog = ProgressDialog.show(this, "", getString(R.string.dialog_new_post_sending));
                mProgressDialog.setCancelable(true);
                mProgressDialog.setCanceledOnTouchOutside(false);
                mProgressDialog.setOnDismissListener(dialog -> finishCompat());
                StringBuilder sendContentBuilder = new StringBuilder();
                sendContentBuilder.append(android.text.Html.toHtml(replaceBackToImageSpan(spannableContent)));
                if(!isInEditMode())
                    sendContentBuilder.append(PostTailUtils.getPostTail(this, mPostTailText.get()));
                sendData(hasTitleField() ? title : null, sendContentBuilder.toString());
            }
        } else if(hasTitleField() && TextUtils.isEmpty(title)){
            ToastProxy.showToast(this, getString(R.string.toast_error_title_empty), ToastSupport.TOAST_ALERT);
        } else {
            ToastProxy.showToast(this, getString(R.string.toast_error_content_empty), ToastSupport.TOAST_ALERT);
        }
    }

    private void insertEmoticon() {
        new EmoticonDialog().show(this, emoticonName -> {
            try {
                Drawable emoji = Drawable.createFromStream(AbstractPostActivity.this.getAssets().open("emoji/" + emoticonName), null);
                addImageBetweenText(emoji, ContentProcessor.IMG_TYPE_EMOJI, emoticonName.substring(0, emoticonName.indexOf(".gif")), 96, 96);
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }

    private static final String IMG_TAG_FORMAT = "([([[%d][%s]])])";

    private void addImageBetweenText(Drawable drawable, int type, String src, int width, int height) {
        drawable .setBounds(0, 0, width == 0 ? drawable.getIntrinsicWidth() : width, height == 0 ? drawable.getIntrinsicHeight() : height);
        String imageTag = String.format(IMG_TAG_FORMAT, type, src);

        mContentEditTextHandler.insert(imageTag, drawable);
    }

    private static final int SELECT_PICTURE = 1;
    private Uri outputFileUri;

    private void openImageIntent() {
        // Determine Uri of camera image to save.
        final File root = new File(Environment.getExternalStorageDirectory() + File.separator + "MyDir" + File.separator);
        root.mkdirs();
        final String fname = UUID.randomUUID().toString();
        final File sdImageMainDirectory = new File(root, fname);
        outputFileUri = Uri.fromFile(sdImageMainDirectory);

        // Camera.
        final List<Intent> cameraIntents = new ArrayList<Intent>();
        final Intent captureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        final PackageManager packageManager = getPackageManager();
        final List<ResolveInfo> listCam = packageManager.queryIntentActivities(captureIntent, 0);
        for(ResolveInfo res : listCam) {
            final String packageName = res.activityInfo.packageName;
            final Intent intent = new Intent(captureIntent);
            intent.setComponent(new ComponentName(res.activityInfo.packageName, res.activityInfo.name));
            intent.setPackage(packageName);
            intent.putExtra(MediaStore.EXTRA_OUTPUT, outputFileUri);
            cameraIntents.add(intent);
        }

        // Filesystem.
        final Intent galleryIntent = new Intent();
        galleryIntent.setType("image/*");
        galleryIntent.setAction(Intent.ACTION_GET_CONTENT);

        // Chooser of filesystem options.
        final Intent chooserIntent = Intent.createChooser(galleryIntent, "Select Source");

        // Add the camera options.
        chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, cameraIntents.toArray(new Parcelable[]{}));

        startActivityForResult(chooserIntent, SELECT_PICTURE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        if(resultCode == RESULT_OK)
        {
            if(requestCode == SELECT_PICTURE)
            {
                final boolean isCamera;
                if(data == null)
                {
                    isCamera = true;
                }
                else
                {
                    final String action = data.getAction();
                    if(action == null)
                    {
                        isCamera = false;
                    }
                    else
                    {
                        isCamera = action.equals(MediaStore.ACTION_IMAGE_CAPTURE);
                    }
                }

                Uri selectedImageUri;
                if(isCamera)
                {
                    selectedImageUri = outputFileUri;
                }
                else
                {
                    selectedImageUri = data == null ? null : data.getData();
                }
                try {
                    InputStream imageStream = getContentResolver().openInputStream(selectedImageUri);
                    Bitmap yourSelectedImage = BitmapFactory.decodeStream(imageStream);
                    addImageBetweenText(new BitmapDrawable(getResources(), yourSelectedImage), ContentProcessor.IMG_TYPE_LOCAL, ContentUriPathUtils.getRealPathFromUri(this, selectedImageUri), 256, 256);
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public SendPostService.SendPostServiceBinder getSendServiceBinder() {
        return mSendServiceBinder;
    }

    protected static class ImageEditTextHandler implements TextWatcher {
        private final EditText mEditor;
        final List<Object> mEmoticonsToRemove = new ArrayList<Object>();
        public ImageEditTextHandler(EditText editor) {
            // Attach the handler to listen for text changes.
            mEditor = editor;
            mEditor.addTextChangedListener(this);
        }

        public void insert(CharSequence charSequence) {
            // Get the selected text.
            int start = mEditor.getSelectionStart();
            int end = mEditor.getSelectionEnd();
            Editable message = mEditor.getEditableText();
            Object[] list = ((Spanned)charSequence).getSpans(start, end, Object.class);
            for (Object span : list) {
                Log.d("span", span.getClass().getName());
            }
            // Insert the emoticon.
            message.replace(start, end, charSequence);
        }

        public void insert(String emoticon, Drawable drawable) {
            // Create the ImageSpan
            ImageSpan span = new ImageSpan(drawable, emoticon, ImageSpan.ALIGN_BASELINE);

            // Get the selected text.
            int start = mEditor.getSelectionStart();
            int end = mEditor.getSelectionEnd();
            Editable message = mEditor.getEditableText();

            // Insert the emoticon.
            message.replace(start, end, emoticon);
            message.setSpan(span, start, start + emoticon.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        }

        int mCurrentChangeStart;
        int mCurrentChangeCount;
        int mCurrentChangeEnd;
        @Override
        public void beforeTextChanged(CharSequence text, int start, int count, int after) {
            // Check if some text will be removed.
            mCurrentChangeStart = start;
            mCurrentChangeCount = count;
            mCurrentChangeEnd = mCurrentChangeStart + mCurrentChangeCount;
            int end = start + count;
            int editorStart = mEditor.getSelectionStart();
            int editorEnd = mEditor.getSelectionEnd();
            if(editorStart == start + count) {
                if (count > 0) {
                    Editable message = mEditor.getEditableText();
                    Object[] list = message.getSpans(start, end, Object.class);
                    synchronized (mEmoticonsToRemove) {
                        for (Object span : list) {
                            // Get only the emoticons that are inside of the changed
                            // region.
                            int spanStart = message.getSpanStart(span);
                            int spanEnd = message.getSpanEnd(span);
                            if ((spanStart < end) && (spanEnd > start) && !(span instanceof SpanWatcher)) {
                                // Add to remove list
                                mEmoticonsToRemove.add(span);
                            }
                        }
                    }
                }
            } else {
                mEmoticonsToRemove.clear();
            }
        }

        @Override
        public void afterTextChanged(Editable text) {
            Editable message = mEditor.getEditableText();

            int editorStart = mEditor.getSelectionStart();
            int editorEnd = mEditor.getSelectionEnd();

            if(editorStart == mCurrentChangeStart) {
                synchronized (mEmoticonsToRemove) {
                for (Object span : mEmoticonsToRemove) {
                    int start = message.getSpanStart(span);
                    int end = message.getSpanEnd(span);

                    // Remove the span
                    message.removeSpan(span);

                    // Remove the remaining emoticon text.
                    if (start != end) {
                        message.delete(start, end);
                    }
                    Log.d("Edit", "Remove Span");
                }
                mEmoticonsToRemove.clear();
            }
            }
        }

        @Override
        public void onTextChanged(CharSequence text, int start, int before, int count) {
        }

    }

    protected CharSequence replaceImageSpan(Drawable initPlaceHolder, long pid, CharSequence sequence) {
        Spannable spannable = new SpannableString(sequence);
        ImageSpan[] imageSpans = spannable.getSpans(0, sequence.length(), ImageSpan.class );

        for(ImageSpan imageSpan : imageSpans) {
            int spanStart = spannable.getSpanStart(imageSpan);
            int spanEnd = spannable.getSpanEnd(imageSpan);
            int spanFlags = spannable.getSpanFlags(imageSpan);
            if (!TextUtils.isEmpty(imageSpan.getSource()) && !imageSpan.getSource().contains("http://img.lkong.cn/bq/")) {
                spannable.removeSpan(imageSpan);
                ClickableImageSpan clickableImageSpan = new ClickableImageSpan(
                        this,
                        mPicasso,
                        null,
                        Long.toString(pid),
                        PICASSO_TAG,
                        imageSpan.getSource(),
                        R.drawable.image_placeholder,
                        R.drawable.image_placeholder,
                        256,
                        256,
                        DynamicDrawableSpan.ALIGN_BOTTOM,
                        initPlaceHolder);
                spannable.setSpan(clickableImageSpan,
                        spanStart,
                        spanEnd,
                        spanFlags);
            } else if(!TextUtils.isEmpty(imageSpan.getSource()) && imageSpan.getSource().contains("http://img.lkong.cn/bq/")){
                spannable.removeSpan(imageSpan);
                EmoticonImageSpan emoticonImageSpan = new EmoticonImageSpan(
                        this,
                        mPicasso,
                        null,
                        Long.toString(pid),
                        PICASSO_TAG,
                        imageSpan.getSource(),
                        R.drawable.image_placeholder,
                        R.drawable.image_placeholder,
                        (int)getResources().getDimension(R.dimen.text_size_subhead)* 2
                );
                spannable.setSpan(emoticonImageSpan,
                        spanStart,
                        spanEnd,
                        spanFlags);
            }
        }
        return spannable;
    }

    static final String PICASSO_TAG = "abstract_post_activity";

    protected void onEditDone(EditPostDoneEvent event) {
        if(isInEditMode()) {
            EditPostResult result = event.getPostResult();
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

    protected static class ImageSpanContainerImpl implements ImageSpanContainer {
        private WeakReference<EditText> mEditText;
        ImageSpanContainerImpl(EditText editText) {
            mEditText = new WeakReference<EditText>(editText);
        }

        @Override
        public void notifyImageSpanLoaded(Object tag) {
            if(mEditText != null && mEditText.get() != null) {
                mEditText.get().invalidate();
            }
        }
    }

    private static Spanned replaceBackToImageSpan(CharSequence content) {
        Spannable spannable = new SpannableString(content);
        Drawable tempDrawable = new ColorDrawable(Color.TRANSPARENT);
        ClickableImageSpan[] clickableImageSpans = spannable.getSpans(0, spannable.length(), ClickableImageSpan.class);
        EmoticonImageSpan[] emoticonImageSpans = spannable.getSpans(0, spannable.length(), EmoticonImageSpan.class);
        for (ClickableImageSpan span : clickableImageSpans) {
            int start = spannable.getSpanStart(span);
            int end = spannable.getSpanEnd(span);
            ImageSpan imageSpan = new ImageSpan(null, span.getSource());
            spannable.removeSpan(span);
            spannable.setSpan(imageSpan, start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        }
        for (EmoticonImageSpan span : emoticonImageSpans) {
            int start = spannable.getSpanStart(span);
            int end = spannable.getSpanEnd(span);
            ImageSpan imageSpan = new ImageSpan(tempDrawable, span.getSource());
            spannable.removeSpan(span);
            spannable.setSpan(imageSpan, start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        }
        return spannable;
    }

    protected static String removeLastEditInfo(String content) {
        Document document = Jsoup.parseBodyFragment(content);
        Elements elements = document.select("i");
        for(Element element : elements) {
            if(element.html().contains("\u672c\u5e16\u6700\u540e\u7531") && element.html().contains("\u7f16\u8f91")) {
                if(element.nextElementSibling() != null && element.nextElementSibling().tagName().equals("br")) {
                    element.nextElementSibling().remove();
                }
                element.remove();
            }
        }
        return document.html();
    }
}
