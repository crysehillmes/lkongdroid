package org.cryse.lkong.model;

public class SignInResult {
    private boolean success;
    private String authCookie;
    private String dzsbheyCookie;
    private String identityCookie;
    private UserInfoModel me;

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getAuthCookie() {
        return authCookie;
    }

    public void setAuthCookie(String authCookie) {
        this.authCookie = authCookie;
    }

    public String getDzsbheyCookie() {
        return dzsbheyCookie;
    }

    public void setDzsbheyCookie(String dzsbheyCookie) {
        this.dzsbheyCookie = dzsbheyCookie;
    }

    public String getIdentityCookie() {
        return identityCookie;
    }

    public void setIdentityCookie(String identityCookie) {
        this.identityCookie = identityCookie;
    }

    public UserInfoModel getMe() {
        return me;
    }

    public void setMe(UserInfoModel me) {
        this.me = me;
    }
}
