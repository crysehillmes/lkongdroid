package org.cryse.lkong.model;

import android.os.Parcel;
import android.os.Parcelable;

public class HotThreadModel implements SimpleCollectionItem {
    public String subject;
    public long tid;

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.subject);
        dest.writeLong(this.tid);
    }

    public HotThreadModel() {
    }

    protected HotThreadModel(Parcel in) {
        this.subject = in.readString();
        this.tid = in.readLong();
    }

    public static final Parcelable.Creator<HotThreadModel> CREATOR = new Parcelable.Creator<HotThreadModel>() {
        public HotThreadModel createFromParcel(Parcel source) {
            return new HotThreadModel(source);
        }

        public HotThreadModel[] newArray(int size) {
            return new HotThreadModel[size];
        }
    };

    @Override
    public long getSortKey() {
        return 0;
    }
}
