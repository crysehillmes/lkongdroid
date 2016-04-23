package org.cryse.lkong.account;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.accounts.AccountManagerCallback;
import android.accounts.AccountManagerFuture;
import android.accounts.AuthenticatorException;
import android.accounts.OnAccountsUpdateListener;
import android.accounts.OperationCanceledException;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Handler;
import android.os.HandlerThread;
import android.text.TextUtils;

import org.cryse.lkong.BuildConfig;
import org.cryse.lkong.application.qualifier.ApplicationContext;
import org.cryse.lkong.event.AccountRemovedEvent;
import org.cryse.lkong.event.CurrentAccountChangedEvent;
import org.cryse.lkong.event.NewAccountEvent;
import org.cryse.lkong.event.RxEventBus;
import org.cryse.lkong.logic.restservice.exception.NeedSignInException;
import org.cryse.lkong.ui.MainActivity;
import org.cryse.utils.preference.IntegerPrefs;
import org.cryse.utils.preference.LongPrefs;
import org.cryse.lkong.application.PreferenceConstant;
import org.cryse.utils.preference.Prefs;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;

import javax.inject.Inject;

import timber.log.Timber;

public class UserAccountManager {
    private static final String LOG_TAG = UserAccountManager.class.getName();
    private LinkedHashMap<Long, UserAccount> mUserAccounts = new LinkedHashMap<Long, UserAccount>();
    private UserAccount mCurrentUserAccount;
    private LKAuthObject mAuthObject;
    RxEventBus mEventBus = RxEventBus.getInstance();

    @Inject
    @ApplicationContext
    Context mContext;


    LongPrefs mDefaultAccountUid;
    IntegerPrefs mVersionCodePref;

    @Inject
    AccountManager mAccountManager;

    static HandlerThread sHandlerThread;
    static Handler sHandler;

    private static final String mLKongAccountType = "org.cryse.lkong";

    public UserAccountManager() {
        mDefaultAccountUid = Prefs.getLongPrefs(
                PreferenceConstant.SHARED_PREFERENCE_DEFAULT_ACCOUNT_UID,
                PreferenceConstant.SHARED_PREFERENCE_DEFAULT_ACCOUNT_UID_VALUE
        );
        mVersionCodePref = Prefs.getIntPrefs(
                PreferenceConstant.SHARED_PREFERENCE_VERSION_CODE,
                PreferenceConstant.SHARED_PREFERENCE_VERSION_CODE_VALUE
        );
    }

    public static void startHandlerThread() {
        sHandlerThread = new HandlerThread("UserAccountManager_thread");
        sHandlerThread.start();
        sHandler = new Handler(sHandlerThread.getLooper());
    }

    public void init() {
        update();
        mAccountManager.addOnAccountsUpdatedListener(new OnAccountsUpdateListener() {
            @Override
            public void onAccountsUpdated(Account[] accounts) {
                synchronized (this) {
                    if(accounts !=null ) {
                        List<Account> lkongAccount = new ArrayList<Account>();
                        for (Account account : accounts) {
                            if(account.type.equals(mLKongAccountType)) {
                                lkongAccount.add(account);
                            }
                        }
                        sHandler.post(() -> {
                            // Do nothing here
                            int i = 518 * 1992;
                        });
                        //Toast.makeText(context, String.format("onAccountsUpdated: account count = %d, mUserAccount count = %d", lkongAccount.size(), mUserAccounts.size()), Toast.LENGTH_SHORT).show();
                        if(mUserAccounts.size() != lkongAccount.size()) {
                            Timber.d("ACCOUNT COUNT CHANGED!", LOG_TAG);
                        }
                        if(mUserAccounts.size() == 0 && lkongAccount.size() != 0) {
                            // New Account, and there is no exist user
                            update();
                            Intent intent = new Intent(mContext, MainActivity.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            mContext.startActivity(intent);
                            mEventBus.sendEvent(new CurrentAccountChangedEvent());
                        } else if(lkongAccount.size() > mUserAccounts.size()) {
                            // New Account, but exists some other users
                            update();
                            mEventBus.sendEvent(new NewAccountEvent());
                        } else if(lkongAccount.size() < mUserAccounts.size() && mUserAccounts.size() > 0) {
                            // Account removed
                            update();
                            mEventBus.sendEvent(new AccountRemovedEvent());
                        } else {
                            update();
                        }
                    }
                }
            }
        }, sHandler, true);
        Timber.d("UserAccountManager init() done.", LOG_TAG);
    }

    public static boolean removeAllAccounts(Context context) {
        AccountManager accountManager = AccountManager.get(context);
        Account[] accounts = accountManager.getAccountsByType(AccountConst.ACCOUNT_TYPE);
        int count = accounts.length;
        int[] successCount = new int[2];
        for(Account account : accounts) {
            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                boolean result = accountManager.removeAccountExplicitly(account);
                successCount[0] += result ? 1 : 0;
            } else {
                accountManager.removeAccount(account, new AccountManagerCallback<Boolean>() {
                    @Override
                    public void run(AccountManagerFuture<Boolean> future) {
                        try {
                            boolean result = future.getResult();
                            successCount[0] += result ? 1 : 0;
                        } catch (OperationCanceledException | IOException | AuthenticatorException ignored) {
                        } finally {
                        }
                    }
                }, sHandler);
            }
        }
        return count == successCount[0];
    }

    public synchronized void update() {
        long uid = mDefaultAccountUid.get();
        try {
            mUserAccounts.clear();
            Account[] accounts = mAccountManager.getAccountsByType(AccountConst.ACCOUNT_TYPE);
            for(Account account : accounts) {
                UserAccount userAccount = getUserAccountFromAccountManager(account, mAccountManager);
                mUserAccounts.put(userAccount.getUserId(), userAccount);
            }

            UserAccount currentAccount = mUserAccounts.get(uid);
            if(currentAccount == null && getFirst() != null) {
                setCurrentUserAccount(getFirst().getUserId());
            } else if(currentAccount != null) {
                setCurrentUserAccount(currentAccount);
            } else {
                mDefaultAccountUid.set(0l);
            }
            /*if(uid >= 0) {
                int index = -1;
                for (int i = 0; i < mUserAccounts.size(); i++) {
                    if(mUserAccounts.get(i).getUserId() == uid) {
                        mCurrentUserAccount = mUserAccounts.get(i);
                        index = i;
                    }
                }
                if(index != -1) {
                    Collections.swap(mUserAccounts, 0, index);
                }
            }*/
        } catch (Exception ex) {
            Timber.e(ex, "getAllUserAccounts() failed.", LOG_TAG);
        }
    }

    public UserAccount getCurrentUserAccount() {
        if(mCurrentUserAccount == null && mUserAccounts.size() > 0) {
            mCurrentUserAccount = mUserAccounts.get(mDefaultAccountUid.get());
            if(mCurrentUserAccount != null) {
                    mDefaultAccountUid.set(mCurrentUserAccount.getUserId());
                    mAuthObject = getAuthObject(mCurrentUserAccount);
            } else {
                mCurrentUserAccount = getFirst();
                if(mCurrentUserAccount != null) {
                    mDefaultAccountUid.set(mCurrentUserAccount.getUserId());
                    mAuthObject = getAuthObject(mCurrentUserAccount);
                }
            }
            return mCurrentUserAccount;
        }
        if(mCurrentUserAccount != null) return mCurrentUserAccount;
        throw new NeedSignInException("You should sign in before you do this.");
    }

    public LKAuthObject getAuthObject() {
        if((mAuthObject == null && mCurrentUserAccount != null) || mAuthObject.getAuthCookie() == null) {
            mAuthObject = getAuthObject(mCurrentUserAccount);
        } else if(mAuthObject == null) {
            setCurrentUserAccount(getFirst().getUserId());
            mAuthObject = getAuthObject(mCurrentUserAccount);
        }
        return mAuthObject;
    }

    public boolean isSignedIn() {
        update();
        Account[] accounts = mAccountManager.getAccountsByType(AccountConst.ACCOUNT_TYPE);
        // Toast.makeText(activity, String.format("onAccountsUpdated222:  mUserAccount count = %d, accounts count = %d", mUserAccounts.size(), accounts.length), Toast.LENGTH_SHORT).show();
        return mUserAccounts.size() > 0;
    }

    public List<UserAccount> getUserAccounts() {
        ArrayList<UserAccount> userAccounts = new ArrayList<UserAccount>(mUserAccounts.values());
        for(int i = 0; i < userAccounts.size(); i++) {
            if(userAccounts.get(i).getUserId() == mCurrentUserAccount.getUserId()) {
                Collections.swap(userAccounts, 0, i);
                break;
            }
        }
        return userAccounts;
    }

    public long getCurrentUserId() {
        return this.getAuthObject().getUserId();
    }

    public void setCurrentUserAccount(long userId) {
        this.mCurrentUserAccount = mUserAccounts.get(userId);
        this.mAuthObject = getAuthObject(mCurrentUserAccount);
        this.mDefaultAccountUid.set(userId);
    }

    private void setCurrentUserAccount(UserAccount userAccount) {
        this.mCurrentUserAccount = userAccount;
        this.mAuthObject = getAuthObject(mCurrentUserAccount);
        this.mDefaultAccountUid.set(userAccount.getUserId());
    }

    public UserAccount getUserAccount(long uid) {
        return mUserAccounts.get(uid);
    }

    public void signOut(long uid) throws Exception {
        // mLKongDatabase.removeUserAccount(uid);
        try {
            UserAccount userAccount = mUserAccounts.get(uid);
            Account account = userAccount.getAccount();
            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP_MR1) {
                mAccountManager.removeAccountExplicitly(account);
            } else {
                mAccountManager.removeAccount(account,null,null);
            }
        } catch (Exception ex) {
            Timber.d(ex, ex.getMessage(), LOG_TAG);
        }
    }

    public static LKAuthObject getAuthObject(UserAccount userAccount) {
        return new LKAuthObject(
                userAccount.getUserId(),
                userAccount.getUserName(),
                userAccount.getAuthUrl(),
                userAccount.getAuthCookie(),
                userAccount.getDzsbheyUrl(),
                userAccount.getDzsbheyCookie()
        );
    }

    private UserAccount getFirst() {
        Iterator<UserAccount> iter = mUserAccounts.values().iterator();
        if(iter.hasNext()) {
            return iter.next();
        } else {
            return  null;
        }
    }

    public static UserAccount getUserAccountFromAccountManager(Account account, AccountManager accountManager) {
        String idString = accountManager.getUserData(account, AccountConst.KEY_ACCOUNT_USER_ID);
        long userId = Long.valueOf(TextUtils.isEmpty(idString) ? "0" : idString);
        String userEmail = account.name;
        String userName = accountManager.getUserData(account, AccountConst.KEY_ACCOUNT_USER_NAME);
        String userAvatar = accountManager.getUserData(account, AccountConst.KEY_ACCOUNT_USER_AVATAR);
        String userAuth = accountManager.getUserData(account, AccountConst.KEY_ACCOUNT_USER_AUTH);
        String userDzsbhey = accountManager.getUserData(account, AccountConst.KEY_ACCOUNT_USER_DZSBHEY);
        if(TextUtils.isEmpty(userAuth))
            throw new IllegalArgumentException("userAuth is empty!");
        return new UserAccount(
                account,
                userId,
                userName,
                userEmail,
                userAvatar,
                userAuth,
                userDzsbhey
        );
    }
}
