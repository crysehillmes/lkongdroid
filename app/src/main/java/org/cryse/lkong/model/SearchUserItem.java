package org.cryse.lkong.model;

import android.os.Parcel;

public class SearchUserItem extends AbstractSearchResult implements SimpleCollectionItem {
    private String avatarUrl;
    private CharSequence customStatus; // "customstatus": "",
    private CharSequence signHtml; // "sightml": "",
    private int gender; //        "gender": 1,
    private long userId; //        "uid": "764822",
    private CharSequence userName; //        "username": "<em>tyk<\/em><em>5555<\/em>",
    private String id; //        "id": "user_764822"
    private long sortKey;

    public CharSequence getCustomStatus() {
        return customStatus;
    }

    public void setCustomStatus(CharSequence customStatus) {
        this.customStatus = customStatus;
    }

    public CharSequence getSignHtml() {
        return signHtml;
    }

    public void setSignHtml(CharSequence signHtml) {
        this.signHtml = signHtml;
    }

    public int getGender() {
        return gender;
    }

    public void setGender(int gender) {
        this.gender = gender;
    }

    public long getUserId() {
        return userId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }

    public CharSequence getUserName() {
        return userName;
    }

    public void setUserName(CharSequence userName) {
        this.userName = userName;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getAvatarUrl() {
        return avatarUrl;
    }

    public void setAvatarUrl(String avatarUrl) {
        this.avatarUrl = avatarUrl;
    }

    @Override
    public long getSortKey() {
        return sortKey;
    }

    public void setSortKey(long sortKey) {
        this.sortKey = sortKey;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.avatarUrl);
        dest.writeString(this.customStatus.toString());
        dest.writeString(this.signHtml.toString());
        dest.writeInt(this.gender);
        dest.writeLong(this.userId);
        dest.writeString(this.userName.toString());
        dest.writeString(this.id);
        dest.writeLong(this.sortKey);
    }

    public SearchUserItem() {
    }

    protected SearchUserItem(Parcel in) {
        this.avatarUrl = in.readString();
        this.customStatus = in.readParcelable(CharSequence.class.getClassLoader());
        this.signHtml = in.readParcelable(CharSequence.class.getClassLoader());
        this.gender = in.readInt();
        this.userId = in.readLong();
        this.userName = in.readParcelable(CharSequence.class.getClassLoader());
        this.id = in.readString();
        this.sortKey = in.readLong();
    }

    public static final Creator<SearchUserItem> CREATOR = new Creator<SearchUserItem>() {
        public SearchUserItem createFromParcel(Parcel source) {
            return new SearchUserItem(source);
        }

        public SearchUserItem[] newArray(int size) {
            return new SearchUserItem[size];
        }
    };
}
