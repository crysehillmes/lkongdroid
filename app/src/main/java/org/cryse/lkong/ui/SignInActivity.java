package org.cryse.lkong.ui;

import android.os.Bundle;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.Toast;

import org.cryse.lkong.R;
import org.cryse.lkong.application.LKongApplication;
import org.cryse.lkong.model.SignInResult;
import org.cryse.lkong.presenter.SignInPresenter;
import org.cryse.lkong.ui.common.AbstractThemeableActivity;
import org.cryse.lkong.view.SignInView;

import javax.inject.Inject;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class SignInActivity extends AbstractThemeableActivity implements SignInView{
    @Inject
    SignInPresenter mPresenter;

    @InjectView(R.id.edit_email)
    EditText mEmailEditText;
    @InjectView(R.id.edit_password)
    EditText mPasswordEditText;


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
                CharSequence email = mEmailEditText.getText();
                CharSequence password = mPasswordEditText.getText();
                if (email.length() <= 0) {
                    mEmailEditText.setError("Please enter your email here.");
                    return true;
                } else if (password.length() <= 0) {
                    mPasswordEditText.setError("Please enter your password here.");
                    return true;
                } else {
                    getPresenter().SignIn(email.toString(), password.toString());
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
        if(signInResult.isSuccess())
            Toast.makeText(this, "SignIn successfully.", Toast.LENGTH_SHORT).show();
        else
            Toast.makeText(this, "SignIn failed.", Toast.LENGTH_SHORT).show();
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

    }

    private SignInPresenter getPresenter() {
        return mPresenter;
    }
}
