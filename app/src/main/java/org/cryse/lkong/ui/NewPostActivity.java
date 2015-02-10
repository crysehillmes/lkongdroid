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
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.os.Parcelable;
import android.provider.MediaStore;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.text.style.ImageSpan;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.ImageButton;

import org.cryse.lkong.R;
import org.cryse.lkong.application.LKongApplication;
import org.cryse.lkong.application.UserAccountManager;
import org.cryse.lkong.application.qualifier.PrefsPostTail;
import org.cryse.lkong.event.NewPostDoneEvent;
import org.cryse.lkong.event.RxEventBus;
import org.cryse.lkong.model.NewPostResult;
import org.cryse.lkong.presenter.NewPostPresenter;
import org.cryse.lkong.service.SendPostService;
import org.cryse.lkong.ui.common.AbstractThemeableActivity;
import org.cryse.lkong.ui.dialog.EmoticonDialog;
import org.cryse.lkong.utils.AnalyticsUtils;
import org.cryse.lkong.utils.ContentProcessor;
import org.cryse.lkong.utils.ContentUriPathUtils;
import org.cryse.lkong.utils.DataContract;
import org.cryse.lkong.utils.ToastProxy;
import org.cryse.lkong.utils.ToastSupport;
import org.cryse.lkong.view.NewPostView;
import org.cryse.utils.ColorUtils;
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

public class NewPostActivity extends AbstractThemeableActivity implements NewPostView {
    public static final String LOG_TAG = NewPostActivity.class.getName();
    @Inject
    NewPostPresenter mPresenter;

    @Inject
    UserAccountManager mUserAccountManager;

    @Inject
    RxEventBus mEventBus;

    @Inject
    @PrefsPostTail
    StringPreference mPostTailText;

    @InjectView(R.id.activity_new_post_edittext_content)
    EditText mContentEditText;

    @InjectView(R.id.action_insert_emoji)
    ImageButton mInsertEmoticonButton;
    @InjectView(R.id.action_insert_image)
    ImageButton mInsertImageButton;

    String mTitle;
    long mThreadId;
    Long mPostId;

    ProgressDialog mProgressDialog;
    ServiceConnection mBackgroundServiceConnection;
    private SendPostService.SendPostServiceBinder mSendServiceBinder;

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
        mEventBus.toObservable().subscribe(event -> {
            if(event instanceof NewPostDoneEvent) {
                runOnUiThread(() -> onPostComplete(((NewPostDoneEvent)event).getPostResult()));
            }
        });
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
        getPresenter().destroy();
        if(mProgressDialog != null && mProgressDialog.isShowing())
            mProgressDialog.dismiss();
    }

    @Override
    protected void onStop() {
        super.onStop();
        getPresenter().unbindView();
        this.unbindService(mBackgroundServiceConnection);
    }

    @Override
    protected void onStart() {
        super.onStart();
        getPresenter().bindView(this);
        Intent service = new Intent(this.getApplicationContext(), SendPostService.class);
        this.bindService(service, mBackgroundServiceConnection, Context.BIND_AUTO_CREATE);
    }

    @Override
    protected void injectThis() {
        LKongApplication.get(this).lKongPresenterComponent().inject(this);
    }

    @Override
    protected void analyticsTrackEnter() {
        AnalyticsUtils.trackActivityEnter(this, LOG_TAG);
    }

    @Override
    protected void analyticsTrackExit() {
        AnalyticsUtils.trackActivityExit(this, LOG_TAG);
    }

    private void submitPost() {
        mContentEditText.clearFocus();
        Spannable spannableContent = mContentEditText.getText();
        if(spannableContent != null && spannableContent.length() > 0) {
            if(mSendServiceBinder != null) {
                mProgressDialog = ProgressDialog.show(this, "", getString(R.string.dialog_new_post_sending));
                StringBuilder sendContentBuilder = new StringBuilder();
                sendContentBuilder.append(android.text.Html.toHtml(spannableContent));
                sendContentBuilder.append("<br><br>").append(getString(R.string.format_post_tail_prefix)).append(mPostTailText.get());
                mSendServiceBinder.sendPost(mUserAccountManager.getAuthObject(), mThreadId, mPostId, sendContentBuilder.toString());
                // finishCompat();
            }
        } else {
            ToastProxy.showToast(this, getString(R.string.toast_error_content_empty), ToastSupport.TOAST_ALERT);
        }
    }

    public NewPostPresenter getPresenter() {
        return mPresenter;
    }

    @Override
    public void onPostComplete(NewPostResult result) {
        if(mProgressDialog != null && mProgressDialog.isShowing())
            mProgressDialog.dismiss();
        if(result != null && result.isSuccess()) {
            new Handler().postDelayed(this::finishCompat, 300);
        } else {
            if(result != null) {
                ToastProxy.showToast(this, TextUtils.isEmpty(result.getErrorMessage()) ? getString(R.string.toast_failure_new_post) : result.getErrorMessage(), ToastSupport.TOAST_ALERT);
            } else {
                ToastProxy.showToast(this, getString(R.string.toast_failure_new_post), ToastSupport.TOAST_ALERT);
            }
        }
    }

    private void insertEmoticon() {
        new EmoticonDialog().show(this, new EmoticonDialog.Callback() {
            @Override
            public void onEmoticonSelection(String emoticonName) {
                try {
                    Drawable emoji = Drawable.createFromStream(NewPostActivity.this.getAssets().open("emoji/" + emoticonName), null);
                    addImageBetweenText(emoji, ContentProcessor.IMG_TYPE_EMOJI, emoticonName.substring(0, emoticonName.indexOf(".gif")), 96, 96);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private static final String IMG_TAG_FORMAT = "([([[%d][%s]])])";

    private void addImageBetweenText(Drawable drawable, int type, String src, int width, int height) {
        drawable .setBounds(0, 0, width == 0 ? drawable.getIntrinsicWidth() : width, height == 0 ? drawable.getIntrinsicHeight() : height);
        Timber.d(src, "addImageBetweenText");
        String imageTag = String.format(IMG_TAG_FORMAT, type, src);
        int selectionCursor = mContentEditText.getSelectionStart();
        SpannableStringBuilder builder = new SpannableStringBuilder(imageTag);
        builder.setSpan(new ImageSpan(drawable, imageTag), 0, imageTag.length(), Spannable.SPAN_INCLUSIVE_EXCLUSIVE);
        mContentEditText.getText().insert(selectionCursor, builder);
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
        final Intent captureIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
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
                        isCamera = action.equals(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
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
}
