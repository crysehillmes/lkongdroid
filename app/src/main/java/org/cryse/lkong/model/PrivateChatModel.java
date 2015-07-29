package org.cryse.lkong.model;

import android.os.Parcel;

import java.io.Serializable;
import java.util.Date;

public class PrivateChatModel implements Serializable, SimpleCollectionItem {
    long userId;
    String userName;
    long targetUserId;
    String targetUserName;
    long typeId;
    long sortKey;
    Date dateline;
    String message;
    String id;
    String targetUserAvatar;

    public long getUserId() {
        return userId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public long getTargetUserId() {
        return targetUserId;
    }

    public void setTargetUserId(long targetUserId) {
        this.targetUserId = targetUserId;
    }

    public String getTargetUserName() {
        return targetUserName;
    }

    public void setTargetUserName(String targetUserName) {
        this.targetUserName = targetUserName;
    }

    public long getTypeId() {
        return typeId;
    }

    public void setTypeId(long typeId) {
        this.typeId = typeId;
    }

    @Override
    public long getSortKey() {
        return sortKey;
    }

    public void setSortKey(long sortKey) {
        this.sortKey = sortKey;
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

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTargetUserAvatar() {
        return targetUserAvatar;
    }

    public void setTargetUserAvatar(String targetUserAvatar) {
        this.targetUserAvatar = targetUserAvatar;
    }

    public PrivateChatModel() {
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(this.userId);
        dest.writeString(this.userName);
        dest.writeLong(this.targetUserId);
        dest.writeString(this.targetUserName);
        dest.writeLong(this.typeId);
        dest.writeLong(this.sortKey);
        dest.writeLong(dateline != null ? dateline.getTime() : -1);
        dest.writeString(this.message);
        dest.writeString(this.id);
        dest.writeString(this.targetUserAvatar);
    }

    protected PrivateChatModel(Parcel in) {
        this.userId = in.readLong();
        this.userName = in.readString();
        this.targetUserId = in.readLong();
        this.targetUserName = in.readString();
        this.typeId = in.readLong();
        this.sortKey = in.readLong();
        long tmpDateline = in.readLong();
        this.dateline = tmpDateline == -1 ? null : new Date(tmpDateline);
        this.message = in.readString();
        this.id = in.readString();
        this.targetUserAvatar = in.readString();
    }

    public static final Creator<PrivateChatModel> CREATOR = new Creator<PrivateChatModel>() {
        public PrivateChatModel createFromParcel(Parcel source) {
            return new PrivateChatModel(source);
        }

        public PrivateChatModel[] newArray(int size) {
            return new PrivateChatModel[size];
        }
    };
}
