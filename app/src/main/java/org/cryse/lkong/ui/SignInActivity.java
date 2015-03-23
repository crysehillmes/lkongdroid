package org.cryse.lkong.ui;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import org.cryse.lkong.R;
import org.cryse.lkong.application.LKongApplication;
import org.cryse.lkong.application.UserAccountManager;
import org.cryse.lkong.application.qualifier.PrefsDefaultAccountUid;
import org.cryse.lkong.event.NewAccountEvent;
import org.cryse.lkong.model.SignInResult;
import org.cryse.lkong.presenter.SignInPresenter;
import org.cryse.lkong.ui.common.AbstractThemeableActivity;
import org.cryse.lkong.utils.AnalyticsUtils;
import org.cryse.lkong.utils.ToastErrorConstant;
import org.cryse.lkong.utils.ToastProxy;
import org.cryse.lkong.view.SignInView;
import org.cryse.utils.preference.LongPreference;

import javax.inject.Inject;

import butterknife.ButterKnife;
import butterknife.InjectView;
import timber.log.Timber;

public class SignInActivity extends AbstractThemeableActivity implements SignInView {
    private static final String LOG_TAG = SignInActivity.class.getName();
    public static final String START_MAIN_ACTIVITY = "start_new_activity";
    @Inject
    SignInPresenter mPresenter;

    @Inject
    @PrefsDefaultAccountUid
    LongPreference mDefaultAccountUid;
    @Inject
    UserAccountManager mUserAccountManager;


    @InjectView(R.id.edit_email)
    EditText mEmailEditText;
    @InjectView(R.id.edit_password)
    EditText mPasswordEditText;
    @InjectView(R.id.sign_in_result_textview)
    TextView mResultTextView;

    @InjectView(R.id.button_sign_in)
    Button mSignInButton;
    @InjectView(R.id.button_sign_up)
    Button mSignUpButton;

    ProgressDialog mSignInProgress;

    CharSequence mEmailText;
    CharSequence mPasswordText;
    boolean mStartMainActivity = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        injectThis();
        super.onCreate(savedInstanceState);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE |
                WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        setContentView(R.layout.activity_signin);
        ButterKnife.inject(this);
        getSwipeBackLayout().setEnableGesture(false);
        Intent intent = getIntent();
        if(intent.hasExtra(START_MAIN_ACTIVITY)) {
            mStartMainActivity = intent.getBooleanExtra(START_MAIN_ACTIVITY, false);
        }
        mSignInButton.setOnClickListener(view -> signIn());
        mSignUpButton.setOnClickListener(view -> {
            String url = "http://lkong.cn/";
            Intent i = new Intent(Intent.ACTION_VIEW);
            i.setData(Uri.parse(url));
            startActivity(i);
        });
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        mEmailEditText.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_NEXT) {
                if (mEmailEditText.getText().length() <= 0)
                    mEmailEditText.setError(getString(R.string.input_error_email));
                else
                    mPasswordEditText.requestFocus();
                return true;
            }
            return false;
        });
        mPasswordEditText.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                if (checkEmailAndPasswordEditText())
                    signIn();
                return true;
            }
            return false;
        });
    }

    private boolean checkEmailAndPasswordEditText() {
        mEmailText = mEmailEditText.getText();
        mPasswordText = mPasswordEditText.getText();
        if (TextUtils.isEmpty(mEmailText)) {
            mEmailEditText.setError(getString(R.string.input_error_email));
            return false;
        }
        if (TextUtils.isEmpty(mPasswordText)) {
            mPasswordEditText.setError(getString(R.string.input_error_password));
            return false;
        }
        return true;
    }

    private void signIn() {
        if(checkEmailAndPasswordEditText()) {
            setLoading(true);
            getPresenter().SignIn(mEmailText.toString(), mPasswordText.toString());
        }

    }

    @Override
    protected void onStart() {
        super.onStart();
        getPresenter().bindView(this);
    }

    @Override
    protected void onStop() {
        super.onStop();
        getPresenter().unbindView();
        dismissProgressDialog();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        getPresenter().destroy();
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

    @Override
    public void signInComplete(SignInResult signInResult) {
        setLoading(false);
        if(signInResult != null && signInResult.isSuccess()) {
            Timber.d("SignInActivity::signInComplete() success.", LOG_TAG);
            mResultTextView.setText("");
            mDefaultAccountUid.set(signInResult.getMe().getUid());
            mUserAccountManager.refresh();
            mUserAccountManager.setCurrentUserAccount(signInResult.getMe().getUid());
            getEventBus().sendEvent(new NewAccountEvent());
            if(mStartMainActivity) {
                Intent intent = new Intent(this, MainActivity.class);
                startActivity(intent);
            }
            finishCompat();
        } else {
            Timber.d("SignInActivity::signInComplete() failed().", LOG_TAG);
            String errorMessage = signInResult == null ? "" : signInResult.getErrorMessage();
            mResultTextView.setText(TextUtils.isEmpty(errorMessage) ? getString(R.string.toast_sign_in_failed) : errorMessage);
        }
    }

    @Override
    public void setLoading(Boolean value) {
        if(value) {
            dismissProgressDialog();
            mSignInProgress = ProgressDialog.show(this, "", getString(R.string.dialog_signing_in));
        } else {
            dismissProgressDialog();
        }
    }

    @Override
    public Boolean isLoading() {
        return null;
    }

    @Override
    public void showToast(int text, int toastType) {
        ToastProxy.showToast(this, getString(ToastErrorConstant.errorCodeToStringRes(text)), toastType);
    }

    private SignInPresenter getPresenter() {
        return mPresenter;
    }

    private void dismissProgressDialog() {
        if(mSignInProgress != null && mSignInProgress.isShowing())
            mSignInProgress.dismiss();
    }

    @Override
    protected int getAppTheme() {
        if(isNightMode())
            return R.style.LKongDroidTheme_Dark_NoTranslucent;
        else
            return R.style.LKongDroidTheme_Light_NoTranslucent;
    }
}
