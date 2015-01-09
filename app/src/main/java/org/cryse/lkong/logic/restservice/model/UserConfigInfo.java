package org.cryse.lkong.logic.restservice.model;

import com.google.gson.annotations.Expose;

public class UserConfigInfo {

    @Expose
    private Integer email;
    @Expose
    private Integer invite;
    @Expose
    private Integer phonenum;
    @Expose
    private String regdate;
    @Expose
    private Object me;
    @Expose
    private Integer fansnum;
    @Expose
    private Integer followuidnum;
    @Expose
    private String id;
    @Expose
    private Boolean isok;

    /**
     *
     * @return
     * The email
     */
    public Integer getEmail() {
        return email;
    }

    /**
     *
     * @param email
     * The email
     */
    public void setEmail(Integer email) {
        this.email = email;
    }

    /**
     *
     * @return
     * The invite
     */
    public Integer getInvite() {
        return invite;
    }

    /**
     *
     * @param invite
     * The invite
     */
    public void setInvite(Integer invite) {
        this.invite = invite;
    }

    /**
     *
     * @return
     * The phonenum
     */
    public Integer getPhonenum() {
        return phonenum;
    }

    /**
     *
     * @param phonenum
     * The phonenum
     */
    public void setPhonenum(Integer phonenum) {
        this.phonenum = phonenum;
    }

    /**
     *
     * @return
     * The regdate
     */
    public String getRegdate() {
        return regdate;
    }

    /**
     *
     * @param regdate
     * The regdate
     */
    public void setRegdate(String regdate) {
        this.regdate = regdate;
    }

    /**
     *
     * @return
     * The me
     */
    public Object getMe() {
        return me;
    }

    /**
     *
     * @param me
     * The me
     */
    public void setMe(Object me) {
        this.me = me;
    }

    /**
     *
     * @return
     * The fansnum
     */
    public Integer getFansnum() {
        return fansnum;
    }

    /**
     *
     * @param fansnum
     * The fansnum
     */
    public void setFansnum(Integer fansnum) {
        this.fansnum = fansnum;
    }

    /**
     *
     * @return
     * The followuidnum
     */
    public Integer getFollowuidnum() {
        return followuidnum;
    }

    /**
     *
     * @param followuidnum
     * The followuidnum
     */
    public void setFollowuidnum(Integer followuidnum) {
        this.followuidnum = followuidnum;
    }

    /**
     *
     * @return
     * The id
     */
    public String getId() {
        return id;
    }

    /**
     *
     * @param id
     * The id
     */
    public void setId(String id) {
        this.id = id;
    }

    /**
     *
     * @return
     * The isok
     */
    public Boolean getIsok() {
        return isok;
    }

    /**
     *
     * @param isok
     * The isok
     */
    public void setIsok(Boolean isok) {
        this.isok = isok;
    }

}
