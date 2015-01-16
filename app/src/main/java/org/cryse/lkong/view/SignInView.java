package org.cryse.lkong.view;

import org.cryse.lkong.data.model.UserAccountEntity;
import org.cryse.lkong.model.SignInResult;

public interface SignInView extends ContentView {
    public void signInComplete(SignInResult signInResult);
    public void onPersistUserAccountComplete(UserAccountEntity userAccount);
}
