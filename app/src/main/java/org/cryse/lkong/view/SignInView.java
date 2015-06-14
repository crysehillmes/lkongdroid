package org.cryse.lkong.view;

import org.cryse.lkong.model.SignInResult;

public interface SignInView extends ContentView {
    void signInComplete(SignInResult signInResult);
}
