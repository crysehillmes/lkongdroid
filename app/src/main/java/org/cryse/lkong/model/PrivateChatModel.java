package org.cryse.lkong.model;

import android.os.Parcel;

import java.io.Serializable;
import java.util.Date;

public class PrivateChatModel implements Serializable, SimpleCollectionItem {
    long myUserId;
    long targetUserId;
    long typeId;
    long sortKey;
    Date dateline;
    String userName;
    String message;
    String id;
    String targetUserAvatar;

    public long getMyUserId() {
        return myUserId;
    }

    public void setMyUserId(long myUserId) {
        this.myUserId = myUserId;
    }

    public long getTargetUserId() {
        return targetUserId;
    }

    public void setTargetUserId(long targetUserId) {
        this.targetUserId = targetUserId;
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

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
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
        dest.writeLong(this.myUserId);
        dest.writeLong(this.targetUserId);
        dest.writeLong(this.typeId);
        dest.writeLong(this.sortKey);
        dest.writeLong(dateline != null ? dateline.getTime() : -1);
        dest.writeString(this.userName);
        dest.writeString(this.message);
        dest.writeString(this.id);
        dest.writeString(this.targetUserAvatar);
    }

    protected PrivateChatModel(Parcel in) {
        this.myUserId = in.readLong();
        this.targetUserId = in.readLong();
        this.typeId = in.readLong();
        this.sortKey = in.readLong();
        long tmpDateline = in.readLong();
        this.dateline = tmpDateline == -1 ? null : new Date(tmpDateline);
        this.userName = in.readString();
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
