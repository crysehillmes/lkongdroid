package org.cryse.lkong.data.model;

import java.util.Date;

public class CacheObjectEntity {
    private String key;
    private String value;
    private Date createTime;
    private Date expireTime;

    public CacheObjectEntity() {
    }

    public CacheObjectEntity(String key, String value, Date createTime, Date expireTime) {
        this.key = key;
        this.value = value;
        this.createTime = createTime;
        this.expireTime = expireTime;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Date getExpireTime() {
        return expireTime;
    }

    public void setExpireTime(Date expireTime) {
        this.expireTime = expireTime;
    }
}
