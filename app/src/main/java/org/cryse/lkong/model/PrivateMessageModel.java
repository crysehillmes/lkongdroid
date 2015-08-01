package org.cryse.lkong.model;

import java.io.Serializable;
import java.util.Date;

public class PrivateMessageModel implements Serializable {
    private String id;
    private long userId;
    private Date dateline;
    private String message;
    private int messageFromId;
    private long sortKey;
    private String avatarUrl;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public long getUserId() {
        return userId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }

    public Date getDateline() {
        return dateline;
    }

    public void setDateline(Date dateline) {
        this.dateline = dateline;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public int getMessageFromId() {
        return messageFromId;
    }

    public void setMessageFromId(int messageFromId) {
        this.messageFromId = messageFromId;
    }

    public long getSortKey() {
        return sortKey;
    }

    public void setSortKey(long sortKey) {
        this.sortKey = sortKey;
    }

    public String getAvatarUrl() {
        return avatarUrl;
    }

    public void setAvatarUrl(String avatarUrl) {
        this.avatarUrl = avatarUrl;
    }
}
