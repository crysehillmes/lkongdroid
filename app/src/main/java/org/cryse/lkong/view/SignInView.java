package org.cryse.lkong.view;

import org.cryse.lkong.model.SignInResult;

public interface SignInView extends ContentView {
    public void signInComplete(SignInResult signInResult);
}
