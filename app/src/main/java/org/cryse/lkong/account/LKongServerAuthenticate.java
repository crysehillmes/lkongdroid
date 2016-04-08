package org.cryse.lkong.account;

import org.cryse.lkong.logic.request.SignInRequest;

public class LKongServerAuthenticate {
    private static final String LOG_TAG = LKongServerAuthenticate.class.getName();

    public LKongServerAuthenticate() {
    }

    public LKongAuthenticateResult userSignIn(String email, String password) throws Exception {
        SignInRequest signInRequest = new SignInRequest(email, password);
        return signInRequest.execute();
    }
}
