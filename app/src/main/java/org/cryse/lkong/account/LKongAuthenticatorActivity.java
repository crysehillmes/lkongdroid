package org.cryse.lkong.account;

import android.accounts.Account;
import android.accounts.AccountAuthenticatorActivity;
import android.accounts.AccountManager;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TextInputLayout;
import android.support.v7.widget.CardView;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import org.cryse.lkong.R;
import org.cryse.lkong.utils.snackbar.ToastErrorConstant;
import org.cryse.lkong.utils.UIUtils;
import org.cryse.lkong.utils.snackbar.SimpleSnackbarType;
import org.cryse.lkong.utils.snackbar.SnackbarUtils;

import butterknife.ButterKnife;
import butterknife.Bind;
import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import timber.log.Timber;


/**
 * The Authenticator activity.
 * <p>
 * Called by the Authenticator and in charge of identifing the user.
 * <p>
 * It sends back to the Authenticator the result.
 */
public class LKongAuthenticatorActivity extends AccountAuthenticatorActivity {
    private static final String LOG_TAG = LKongAuthenticatorActivity.class.getName();
    public static final String START_MAIN_ACTIVITY = "start_new_activity";

    public final static String ARG_ACCOUNT_TYPE = "ACCOUNT_TYPE";
    public final static String ARG_AUTH_TYPE = "AUTH_TYPE";
    public final static String ARG_ACCOUNT_NAME = "ACCOUNT_NAME";
    public final static String ARG_IS_ADDING_NEW_ACCOUNT = "IS_ADDING_ACCOUNT";

    public static final String KEY_ERROR_MESSAGE = "ERR_MSG";

    public final static String PARAM_USER_PASS = "USER_PASS";

    private final int REQ_SIGNUP = 1;

    ProgressDialog mSignInProgress;

    private AccountManager mAccountManager;
    private String mAuthTokenType;

    @Bind(R.id.activity_sign_in_cardview)
    CardView mSignInCardView;
    @Bind(R.id.edit_email)
    EditText mEmailEditText;
    @Bind(R.id.edit_password)
    EditText mPasswordEditText;
    @Bind(R.id.edit_email_textlayout)
    TextInputLayout mEmailTextLayout;
    @Bind(R.id.edit_password_textlayout)
    TextInputLayout mPasswordTextLayout;
    @Bind(R.id.sign_in_result_textview)
    TextView mResultTextView;

    @Bind(R.id.fab_sign_up)
    FloatingActionButton mSignInButton;
    @Bind(R.id.button_sign_up)
    Button mSignUpButton;

    private View mSnackbarRootView;

    CharSequence mEmailText;
    CharSequence mPasswordText;
    boolean mStartMainActivity = false;

    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE |
                WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        setContentView(R.layout.activity_signin);
        ButterKnife.bind(this);

        CoordinatorLayout.LayoutParams cardViewLayoutParams;
        if (isTablet()) {
            cardViewLayoutParams = new CoordinatorLayout.LayoutParams(
                    getResources().getDimensionPixelSize(R.dimen.width_signin_cardview),
                    ViewGroup.LayoutParams.WRAP_CONTENT
            );
        } else {
            cardViewLayoutParams = new CoordinatorLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
            );
            int marginValue = UIUtils.dp2px(this, 16f);
            cardViewLayoutParams.setMargins(marginValue, marginValue, marginValue, marginValue);
        }
        cardViewLayoutParams.gravity = Gravity.CENTER;
        mSignInCardView.setLayoutParams(cardViewLayoutParams);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setStatusBarColor(getResources().getColor(R.color.activity_bg_sign_in));
        }

        Intent intent = getIntent();
        if (intent.hasExtra(START_MAIN_ACTIVITY)) {
            mStartMainActivity = intent.getBooleanExtra(START_MAIN_ACTIVITY, false);
        }
        mSignInButton.setOnClickListener(view -> signIn());


        mAccountManager = AccountManager.get(getBaseContext());

        String accountName = getIntent().getStringExtra(ARG_ACCOUNT_NAME);
        mAuthTokenType = getIntent().getStringExtra(ARG_AUTH_TYPE);
        if (mAuthTokenType == null)
            mAuthTokenType = AccountConst.AUTHTOKEN_TYPE_FULL_ACCESS;

        /*if (accountName != null) {
            ((TextView)findViewById(R.id.accountName)).setText(accountName);
        }

        findViewById(R.id.submit).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                submit();
            }
        });
        findViewById(R.id.signUp).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Since there can only be one AuthenticatorActivity, we call the sign up activity, get his results,
                // and return them in setAccountAuthenticatorResult(). See finishLogin().
                Intent signup = new Intent(getBaseContext(), SignUpActivity.class);
                signup.putExtras(getIntent().getExtras());
                startActivityForResult(signup, REQ_SIGNUP);
            }
        });*/
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        // The sign up activity returned that the user has successfully created an account
        if (requestCode == REQ_SIGNUP && resultCode == RESULT_OK) {
            finishLogin(data);
        } else
            super.onActivityResult(requestCode, resultCode, data);
    }

    private void signIn() {
        if (checkEmailAndPasswordEditText()) {
            setLoading(true);
            Observable.create(new Observable.OnSubscribe<Intent>() {
                @Override
                public void call(Subscriber<? super Intent> subscriber) {

                    Log.d(LOG_TAG, "> Started authenticating");
                    String userName = mEmailText.toString();
                    String userPassword = mPasswordText.toString();
                    String authtoken = null;
                    final String accountType = getIntent().getStringExtra(ARG_ACCOUNT_TYPE);
                    Bundle data = new Bundle();
                    try {
                        LKongServerAuthenticate serverAuthenticate = new LKongServerAuthenticate();
                        LKongAuthenticateResult result = serverAuthenticate.userSignIn(userName, userPassword);
                        authtoken = result.combinedCookie;
                        data.putString(AccountManager.KEY_ACCOUNT_NAME, userName);
                        data.putString(AccountManager.KEY_ACCOUNT_TYPE, accountType);
                        data.putString(AccountManager.KEY_AUTHTOKEN, authtoken);
                        data.putString(PARAM_USER_PASS, userPassword);
                        data.putString(AccountConst.KEY_ACCOUNT_USER_ID, Long.toString(result.userId));
                        data.putString(AccountConst.KEY_ACCOUNT_USER_NAME, result.userName);
                        data.putString(AccountConst.KEY_ACCOUNT_USER_AVATAR, result.userAvatar);
                        data.putString(AccountConst.KEY_ACCOUNT_USER_AUTH, result.authCookie);
                        data.putString(AccountConst.KEY_ACCOUNT_USER_DZSBHEY, result.dzsbheyCookie);
                    } catch (Exception e) {
                        Log.e("ERROR", e.getMessage(), e);
                        data.putString(KEY_ERROR_MESSAGE, e.getMessage());
                    }

                    Intent intent = new Intent();
                    intent.putExtras(data);
                    subscriber.onNext(intent);
                    subscriber.onCompleted();
                }
            }).subscribeOn(Schedulers.newThread())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(
                            result -> {
                                if (result.hasExtra(KEY_ERROR_MESSAGE)) {
                                    showSnackbar(
                                            result.getStringExtra(KEY_ERROR_MESSAGE),
                                            SimpleSnackbarType.ERROR,
                                            SimpleSnackbarType.LENGTH_SHORT
                                    );
                                } else {
                                    finishLogin(result);
                                }
                            },
                            error -> {
                                Timber.e(error, "LKongAuthenticatorActivity::SignIn() onError().", LOG_TAG);
                                showSnackbar(
                                        ToastErrorConstant.TOAST_FAILURE_RATE_POST,
                                        SimpleSnackbarType.ERROR,
                                        SimpleSnackbarType.LENGTH_SHORT
                                );
                            },
                            () -> {
                                Timber.d("LKongAuthenticatorActivity::SignIn() onComplete().", LOG_TAG);
                                setLoading(false);
                            });
        }

    }

    private boolean checkEmailAndPasswordEditText() {
        mEmailText = mEmailEditText.getText();
        mPasswordText = mPasswordEditText.getText();
        if (TextUtils.isEmpty(mEmailText)) {
            mEmailTextLayout.setError(getString(R.string.input_error_email));
            return false;
        } else {
            mEmailTextLayout.setError("");
        }
        if (TextUtils.isEmpty(mPasswordText)) {
            mPasswordTextLayout.setError(getString(R.string.input_error_password));
            return false;
        } else {
            mPasswordTextLayout.setError("");
        }
        return true;
    }

    private void finishLogin(Intent intent) {
        Log.d(LOG_TAG, "> finishLogin");

        String accountName = intent.getStringExtra(AccountManager.KEY_ACCOUNT_NAME);
        String accountPassword = intent.getStringExtra(PARAM_USER_PASS);
        final Account account = new Account(accountName, intent.getStringExtra(AccountManager.KEY_ACCOUNT_TYPE));

        if (getIntent().getBooleanExtra(ARG_IS_ADDING_NEW_ACCOUNT, false)) {
            Log.d(LOG_TAG, "> finishLogin > addAccountExplicitly");
            String authtoken = intent.getStringExtra(AccountManager.KEY_AUTHTOKEN);
            String authtokenType = mAuthTokenType;

            Bundle userData = new Bundle();
            userData.putString(AccountConst.KEY_ACCOUNT_USER_ID, intent.getStringExtra(AccountConst.KEY_ACCOUNT_USER_ID));
            userData.putString(AccountConst.KEY_ACCOUNT_USER_NAME, intent.getStringExtra(AccountConst.KEY_ACCOUNT_USER_NAME));
            userData.putString(AccountConst.KEY_ACCOUNT_USER_AVATAR, intent.getStringExtra(AccountConst.KEY_ACCOUNT_USER_AVATAR));
            userData.putString(AccountConst.KEY_ACCOUNT_USER_AUTH, intent.getStringExtra(AccountConst.KEY_ACCOUNT_USER_AUTH));
            userData.putString(AccountConst.KEY_ACCOUNT_USER_DZSBHEY, intent.getStringExtra(AccountConst.KEY_ACCOUNT_USER_DZSBHEY));

            // Creating the account on the device and setting the auth token we got
            // (Not setting the auth token will cause another call to the server to authenticate the user)
            mAccountManager.addAccountExplicitly(account, accountPassword, userData);
            mAccountManager.setAuthToken(account, authtokenType, authtoken);
        } else {
            Log.d(LOG_TAG, "> finishLogin > setPassword");
            mAccountManager.setPassword(account, accountPassword);
        }

        setAccountAuthenticatorResult(intent.getExtras());
        setResult(RESULT_OK, intent);
        finish();
    }

    private void dismissProgressDialog() {
        if (mSignInProgress != null && mSignInProgress.isShowing())
            mSignInProgress.dismiss();
    }

    public void setLoading(Boolean value) {
        if (value) {
            dismissProgressDialog();
            mSignInProgress = ProgressDialog.show(this, "", getString(R.string.dialog_signing_in));
        } else {
            dismissProgressDialog();
        }
    }

    public boolean isTablet() {
        return getResources().getBoolean(R.bool.isTablet);
    }

    public void showSnackbar(int errorCode, SimpleSnackbarType type, Object... args) {
        SnackbarUtils.makeSimple(
                getSnackbarRootView(),
                getString(ToastErrorConstant.errorCodeToStringRes(errorCode)),
                type,
                SimpleSnackbarType.LENGTH_SHORT
        ).show();
    }

    public void showSnackbar(CharSequence content, SimpleSnackbarType type, Object... args) {
        SnackbarUtils.makeSimple(
                getSnackbarRootView(),
                content,
                type,
                SimpleSnackbarType.LENGTH_SHORT
        ).show();
    }

    protected View getSnackbarRootView() {
        if (mSnackbarRootView == null)
            mSnackbarRootView = findViewById(android.R.id.content);
        return mSnackbarRootView;
    }
}