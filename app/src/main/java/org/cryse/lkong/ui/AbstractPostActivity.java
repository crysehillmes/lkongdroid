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
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.IBinder;
import android.os.Parcelable;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.style.ImageSpan;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RelativeLayout;

import org.cryse.lkong.R;
import org.cryse.lkong.application.UserAccountManager;
import org.cryse.lkong.application.qualifier.PrefsPostTail;
import org.cryse.lkong.event.AbstractEvent;
import org.cryse.lkong.event.NewPostDoneEvent;
import org.cryse.lkong.event.NewThreadDoneEvent;
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
import org.cryse.utils.preference.StringPreference;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import javax.inject.Inject;

import butterknife.ButterKnife;
import butterknife.InjectView;
import timber.log.Timber;

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

    protected abstract void readDataFromIntent(Intent intent);
    protected abstract void sendData(String title, String content);
    protected abstract boolean hasTitleField();
    protected abstract String getTitleString();
    protected abstract String getLogTag();
    protected abstract void onSendDataDone(AbstractEvent event);


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        injectThis();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_thread);
        setUpToolbar(R.id.my_awesome_toolbar, R.id.toolbar_shadow);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDefaultDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        ButterKnife.inject(this);
        mTitleEditText.setVisibility(hasTitleField() ? View.VISIBLE : View.GONE);
        if(!hasTitleField()) {
            RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams)mContentEditText.getLayoutParams();
            int actionBarSize = UIUtils.calculateActionBarSize(this);
            layoutParams.setMargins(0, actionBarSize, 0, actionBarSize);
        }
        mContentEditTextHandler = new ImageEditTextHandler(mContentEditText);
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
                sendContentBuilder.append(android.text.Html.toHtml(spannableContent));
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

    private static class ImageEditTextHandler implements TextWatcher {

        private final EditText mEditor;
        private final ArrayList<ImageSpan> mEmoticonsToRemove = new ArrayList<ImageSpan>();

        public ImageEditTextHandler(EditText editor) {
            // Attach the handler to listen for text changes.
            mEditor = editor;
            mEditor.addTextChangedListener(this);
        }

        public void insert(String emoticon, Drawable drawable) {
            // Create the ImageSpan
            ImageSpan span = new ImageSpan(drawable, ImageSpan.ALIGN_BASELINE);

            // Get the selected text.
            int start = mEditor.getSelectionStart();
            int end = mEditor.getSelectionEnd();
            Editable message = mEditor.getEditableText();

            // Insert the emoticon.
            message.replace(start, end, emoticon);
            message.setSpan(span, start, start + emoticon.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        }

        @Override
        public void beforeTextChanged(CharSequence text, int start, int count, int after) {
            // Check if some text will be removed.
            if (count > 0) {
                int end = start + count;
                Editable message = mEditor.getEditableText();
                ImageSpan[] list = message.getSpans(start, end, ImageSpan.class);

                for (ImageSpan span : list) {
                    // Get only the emoticons that are inside of the changed
                    // region.
                    int spanStart = message.getSpanStart(span);
                    int spanEnd = message.getSpanEnd(span);
                    if ((spanStart < end) && (spanEnd > start)) {
                        // Add to remove list
                        mEmoticonsToRemove.add(span);
                    }
                }
            }
        }

        @Override
        public void afterTextChanged(Editable text) {
            Editable message = mEditor.getEditableText();

            // Commit the emoticons to be removed.
            for (ImageSpan span : mEmoticonsToRemove) {
                int start = message.getSpanStart(span);
                int end = message.getSpanEnd(span);

                // Remove the span
                message.removeSpan(span);

                // Remove the remaining emoticon text.
                if (start != end) {
                    message.delete(start, end);
                }
            }
            mEmoticonsToRemove.clear();
        }

        @Override
        public void onTextChanged(CharSequence text, int start, int before, int count) {
        }

    }
}
