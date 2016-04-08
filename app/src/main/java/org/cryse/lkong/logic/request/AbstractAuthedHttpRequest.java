package org.cryse.lkong.logic.request;

import org.cryse.lkong.logic.HttpDelegate;
import org.cryse.lkong.logic.restservice.exception.NeedSignInException;
import org.cryse.lkong.logic.restservice.exception.SignInExpiredException;
import org.cryse.lkong.account.LKAuthObject;

import java.util.ArrayList;
import java.util.List;

import okhttp3.Cookie;
import okhttp3.HttpUrl;

public abstract class AbstractAuthedHttpRequest<ResponseType> extends AbstractHttpRequest<ResponseType> {
    private LKAuthObject mAuthObject;
    public AbstractAuthedHttpRequest(LKAuthObject authObject) {
        this.mAuthObject = authObject;
    }

    public AbstractAuthedHttpRequest(HttpDelegate httpDelegate, LKAuthObject authObject) {
        super(httpDelegate);
        this.mAuthObject = authObject;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        checkSignInStatus(getAuthObject(), isCheckIdentityCookie());
        applyAuthCookies();
    }

    protected void applyAuthCookies() {
        clearCookies();
        List<Cookie> cookies = new ArrayList<>(2);
        cookies.add(getAuthObject().getAuthCookie());
        cookies.add(getAuthObject().getDzsbheyCookie());

        getCookieJar().saveFromResponse(HttpUrl.parse(getAuthObject().getAuthUrl().toString()), cookies);
        // getCookieJar().getCookieStore().add(getAuthObject().getDzsbheyURI(), getAuthObject().getDzsbheyHttpCookie());
        // cookieManager.getCookieStore().add(authObject.getIdentityURI(), authObject.getIdentityHttpCookie());
    }

    protected boolean isCheckIdentityCookie() {
        return false;
    }

    private void checkSignInStatus(LKAuthObject authObject, boolean checkIdentity) {
        if(!authObject.isSignedIn()) {
            if(authObject.hasExpired()) {
                throw new SignInExpiredException();
            } else {
                throw new NeedSignInException();
            }
        }
        /*if(checkIdentity) {
            if(authObject.hasIdentity()) {
                if(authObject.hasIdentityExpired())
                    throw new IdentityExpiredException();
            } else {
                throw new NeedIdentityException();
            }
        }*/
    }

    protected LKAuthObject getAuthObject() {
        return mAuthObject;
    }
}
