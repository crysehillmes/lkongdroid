package org.cryse.lkong.ui;

import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import org.cryse.lkong.R;
import org.cryse.lkong.application.LKongApplication;
import org.cryse.lkong.application.qualifier.PrefsDefaultAccountUid;
import org.cryse.lkong.data.model.UserAccountEntity;
import org.cryse.lkong.model.SignInResult;
import org.cryse.lkong.presenter.SignInPresenter;
import org.cryse.lkong.ui.common.AbstractThemeableActivity;
import org.cryse.lkong.utils.ToastErrorConstant;
import org.cryse.lkong.utils.ToastProxy;
import org.cryse.lkong.view.SignInView;
import org.cryse.utils.preference.LongPreference;

import javax.inject.Inject;

import butterknife.ButterKnife;
import butterknife.InjectView;
import timber.log.Timber;

public class SignInActivity extends AbstractThemeableActivity implements SignInView{
    private static final String LOG_TAG = SignInActivity.class.getName();
    @Inject
    SignInPresenter mPresenter;

    @Inject
    @PrefsDefaultAccountUid
    LongPreference mDefaultAccountUid;

    @InjectView(R.id.edit_email)
    EditText mEmailEditText;
    @InjectView(R.id.edit_password)
    EditText mPasswordEditText;
    @InjectView(R.id.progressbar_signin)
    ProgressBar mSignInProgressBar;

    CharSequence mEmailText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        injectThis();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signin);
        ButterKnife.inject(this);
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        mEmailEditText.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_NEXT) {
                if(mEmailEditText.getText().length() <= 0)
                    mEmailEditText.setError("Please enter your email here.");
                else
                    mPasswordEditText.requestFocus();
                return true;
        }
            return false;
        });
        mPasswordEditText.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                mEmailText = mEmailEditText.getText();
                CharSequence password = mPasswordEditText.getText();
                if (mEmailText.length() <= 0) {
                    mEmailEditText.setError("Please enter your email here.");
                    return true;
                } else if (password.length() <= 0) {
                    mPasswordEditText.setError("Please enter your password here.");
                    return true;
                } else {
                    setViewStatus(true);
                    getPresenter().SignIn(mEmailText.toString(), password.toString());
                    return true;
                }
            }
            return false;
        });
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
    public void signInComplete(SignInResult signInResult) {
        if(signInResult.isSuccess()) {
            Timber.d("SignInActivity::signInComplete() success.", LOG_TAG);
            UserAccountEntity userAccountEntity = new UserAccountEntity(
                    signInResult.getMe().getUid(),
                    mEmailText.toString(),
                    signInResult.getMe().getUserName(),
                    signInResult.getMe().getUserIcon(),
                    signInResult.getAuthCookie(),
                    signInResult.getDzsbheyCookie(),
                    signInResult.getIdentityCookie()
            );
            getPresenter().persistUserAccount(userAccountEntity);
        } else {
            Timber.d("SignInActivity::signInComplete() failed().", LOG_TAG);
            setViewStatus(false);
            Toast.makeText(this, "SignIn failed.", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onPersistUserAccountComplete(UserAccountEntity userAccount) {
        Timber.d("SignInActivity::onPersistUserAccountComplete().", LOG_TAG);
        if(userAccount != null) {
            Toast.makeText(this, "SignIn successfully.", Toast.LENGTH_SHORT).show();
            mDefaultAccountUid.set(userAccount.getUserId());
        }
        setViewStatus(false);
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
            this.finishAfterTransition();
        else
            this.finish();
    }

    @Override
    public void setLoading(Boolean value) {

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

    private void setViewStatus(boolean isSigningIn) {
        if(isSigningIn) {
            mEmailEditText.setEnabled(false);
            mPasswordEditText.setEnabled(false);
            mSignInProgressBar.setVisibility(View.VISIBLE);
        } else {
            mEmailEditText.setEnabled(true);
            mPasswordEditText.setEnabled(true);
            mSignInProgressBar.setVisibility(View.INVISIBLE);
        }
    }
}
