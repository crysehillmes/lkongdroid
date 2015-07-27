package org.cryse.lkong.logic.request;

import org.cryse.lkong.logic.HttpDelegate;
import org.cryse.lkong.logic.restservice.exception.NeedSignInException;
import org.cryse.lkong.logic.restservice.exception.SignInExpiredException;
import org.cryse.lkong.utils.LKAuthObject;

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
        getCookieManager().getCookieStore().add(getAuthObject().getAuthURI(), getAuthObject().getAuthHttpCookie());
        getCookieManager().getCookieStore().add(getAuthObject().getDzsbheyURI(), getAuthObject().getDzsbheyHttpCookie());
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
