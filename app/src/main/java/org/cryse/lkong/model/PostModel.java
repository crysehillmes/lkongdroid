package org.cryse.lkong.model;


import android.os.Parcel;
import android.os.Parcelable;

import java.util.Date;
import java.util.List;

public class PostModel implements Parcelable {
    private long fid;
    private long sortKey;
    private Date sortKeyTime;
    private Date dateline;
    private String message;
    private String authorName;
    private long authorId;
    private boolean favorite;
    private boolean isMe; // Gson add int to boolean converter
    private boolean notGroup; // Gson add int to boolean converter
    private long pid; // GSON add String to long converter
    private boolean first; // Gson add int to boolean converter
    private int status;
    private String id; // GSON add String to long converter
    private boolean tsAdmin;
    private boolean isAdmin;
    private int ordinal;
    private long tid;
    private int rateScore;
    private List<PostRate> rateLog;
    private PostAuthor author;

    public PostModel() {
    }

    public long getFid() {
        return fid;
    }

    public void setFid(long fid) {
        this.fid = fid;
    }

    public long getSortKey() {
        return sortKey;
    }

    public void setSortKey(long sortKey) {
        this.sortKey = sortKey;
    }

    public Date getSortKeyTime() {
        return sortKeyTime;
    }

    public void setSortKeyTime(Date sortKeyTime) {
        this.sortKeyTime = sortKeyTime;
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

    public String getAuthorName() {
        return authorName;
    }

    public void setAuthorName(String authorName) {
        this.authorName = authorName;
    }

    public long getAuthorId() {
        return authorId;
    }

    public void setAuthorId(long authorId) {
        this.authorId = authorId;
    }

    public boolean isFavorite() {
        return favorite;
    }

    public void setFavorite(boolean favorite) {
        this.favorite = favorite;
    }

    public boolean isMe() {
        return isMe;
    }

    public void setMe(boolean isMe) {
        this.isMe = isMe;
    }

    public boolean isNotGroup() {
        return notGroup;
    }

    public void setNotGroup(boolean notGroup) {
        this.notGroup = notGroup;
    }

    public long getPid() {
        return pid;
    }

    public void setPid(long pid) {
        this.pid = pid;
    }

    public boolean isFirst() {
        return first;
    }

    public void setFirst(boolean first) {
        this.first = first;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public boolean isTsAdmin() {
        return tsAdmin;
    }

    public void setTsAdmin(boolean tsAdmin) {
        this.tsAdmin = tsAdmin;
    }

    public boolean isAdmin() {
        return isAdmin;
    }

    public void setAdmin(boolean isAdmin) {
        this.isAdmin = isAdmin;
    }

    public int getOrdinal() {
        return ordinal;
    }

    public void setOrdinal(int ordinal) {
        this.ordinal = ordinal;
    }

    public long getTid() {
        return tid;
    }

    public void setTid(long tid) {
        this.tid = tid;
    }

    public int getRateScore() {
        return rateScore;
    }

    public void setRateScore(int rateScore) {
        this.rateScore = rateScore;
    }

    public List<PostRate> getRateLog() {
        return rateLog;
    }

    public void setRateLog(List<PostRate> rateLog) {
        this.rateLog = rateLog;
    }

    public PostAuthor getAuthor() {
        return author;
    }

    public void setAuthor(PostAuthor author) {
        this.author = author;
    }

    public static class PostAuthor implements Parcelable {
        private String adminId;
        private String customStatus;
        private int gender;
        private Date regDate;
        private long uid;
        private String userName;
        private boolean verify;
        private String verifyMessage;
        private String color;
        private String stars;
        private String rankTitle;

        public PostAuthor() {
        }

        public PostAuthor(String adminId, String customStatus, int gender, Date regDate, long uid, String userName, boolean verify, String verifyMessage, String color, String stars, String rankTitle) {
            this.adminId = adminId;
            this.customStatus = customStatus;
            this.gender = gender;
            this.regDate = regDate;
            this.uid = uid;
            this.userName = userName;
            this.verify = verify;
            this.verifyMessage = verifyMessage;
            this.color = color;
            this.stars = stars;
            this.rankTitle = rankTitle;
        }

        public String getAdminId() {
            return adminId;
        }

        public void setAdminId(String adminId) {
            this.adminId = adminId;
        }

        public String getCustomStatus() {
            return customStatus;
        }

        public void setCustomStatus(String customStatus) {
            this.customStatus = customStatus;
        }

        public int getGender() {
            return gender;
        }

        public void setGender(int gender) {
            this.gender = gender;
        }

        public Date getRegDate() {
            return regDate;
        }

        public void setRegDate(Date regDate) {
            this.regDate = regDate;
        }

        public long getUid() {
            return uid;
        }

        public void setUid(long uid) {
            this.uid = uid;
        }

        public String getUserName() {
            return userName;
        }

        public void setUserName(String userName) {
            this.userName = userName;
        }

        public boolean isVerify() {
            return verify;
        }

        public void setVerify(boolean verify) {
            this.verify = verify;
        }

        public String getVerifyMessage() {
            return verifyMessage;
        }

        public void setVerifyMessage(String verifyMessage) {
            this.verifyMessage = verifyMessage;
        }

        public String getColor() {
            return color;
        }

        public void setColor(String color) {
            this.color = color;
        }

        public String getStars() {
            return stars;
        }

        public void setStars(String stars) {
            this.stars = stars;
        }

        public String getRankTitle() {
            return rankTitle;
        }

        public void setRankTitle(String rankTitle) {
            this.rankTitle = rankTitle;
        }

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeString(this.adminId);
            dest.writeString(this.customStatus);
            dest.writeInt(this.gender);
            dest.writeLong(regDate != null ? regDate.getTime() : -1);
            dest.writeLong(this.uid);
            dest.writeString(this.userName);
            dest.writeByte(verify ? (byte) 1 : (byte) 0);
            dest.writeString(this.verifyMessage);
            dest.writeString(this.color);
            dest.writeString(this.stars);
            dest.writeString(this.rankTitle);
        }

        private PostAuthor(Parcel in) {
            this.adminId = in.readString();
            this.customStatus = in.readString();
            this.gender = in.readInt();
            long tmpRegDate = in.readLong();
            this.regDate = tmpRegDate == -1 ? null : new Date(tmpRegDate);
            this.uid = in.readLong();
            this.userName = in.readString();
            this.verify = in.readByte() != 0;
            this.verifyMessage = in.readString();
            this.color = in.readString();
            this.stars = in.readString();
            this.rankTitle = in.readString();
        }

        public static final Parcelable.Creator<PostAuthor> CREATOR = new Parcelable.Creator<PostAuthor>() {
            public PostAuthor createFromParcel(Parcel source) {
                return new PostAuthor(source);
            }

            public PostAuthor[] newArray(int size) {
                return new PostAuthor[size];
            }
        };
    }

    public static class PostRate implements Parcelable {
        private Date dateline;
        private int extCredits;
        private long pid;
        private String reason;
        private int score;
        private long uid;
        private String userName;

        public PostRate() {
        }

        public PostRate(Date dateline, int extCredits, long pid, String reason, int score, long uid, String userName) {
            this.dateline = dateline;
            this.extCredits = extCredits;
            this.pid = pid;
            this.reason = reason;
            this.score = score;
            this.uid = uid;
            this.userName = userName;
        }

        public Date getDateline() {
            return dateline;
        }

        public void setDateline(Date dateline) {
            this.dateline = dateline;
        }

        public int getExtCredits() {
            return extCredits;
        }

        public void setExtCredits(int extCredits) {
            this.extCredits = extCredits;
        }

        public long getPid() {
            return pid;
        }

        public void setPid(long pid) {
            this.pid = pid;
        }

        public String getReason() {
            return reason;
        }

        public void setReason(String reason) {
            this.reason = reason;
        }

        public int getScore() {
            return score;
        }

        public void setScore(int score) {
            this.score = score;
        }

        public long getUid() {
            return uid;
        }

        public void setUid(long uid) {
            this.uid = uid;
        }

        public String getUserName() {
            return userName;
        }

        public void setUserName(String userName) {
            this.userName = userName;
        }

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeLong(dateline != null ? dateline.getTime() : -1);
            dest.writeInt(this.extCredits);
            dest.writeLong(this.pid);
            dest.writeString(this.reason);
            dest.writeInt(this.score);
            dest.writeLong(this.uid);
            dest.writeString(this.userName);
        }

        private PostRate(Parcel in) {
            long tmpDateline = in.readLong();
            this.dateline = tmpDateline == -1 ? null : new Date(tmpDateline);
            this.extCredits = in.readInt();
            this.pid = in.readLong();
            this.reason = in.readString();
            this.score = in.readInt();
            this.uid = in.readLong();
            this.userName = in.readString();
        }

        public static final Parcelable.Creator<PostRate> CREATOR = new Parcelable.Creator<PostRate>() {
            public PostRate createFromParcel(Parcel source) {
                return new PostRate(source);
            }

            public PostRate[] newArray(int size) {
                return new PostRate[size];
            }
        };
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(this.fid);
        dest.writeLong(this.sortKey);
        dest.writeLong(sortKeyTime != null ? sortKeyTime.getTime() : -1);
        dest.writeLong(dateline != null ? dateline.getTime() : -1);
        dest.writeString(this.message);
        dest.writeString(this.authorName);
        dest.writeLong(this.authorId);
        dest.writeByte(favorite ? (byte) 1 : (byte) 0);
        dest.writeByte(isMe ? (byte) 1 : (byte) 0);
        dest.writeByte(notGroup ? (byte) 1 : (byte) 0);
        dest.writeLong(this.pid);
        dest.writeByte(first ? (byte) 1 : (byte) 0);
        dest.writeInt(this.status);
        dest.writeString(this.id);
        dest.writeByte(tsAdmin ? (byte) 1 : (byte) 0);
        dest.writeByte(isAdmin ? (byte) 1 : (byte) 0);
        dest.writeInt(this.ordinal);
        dest.writeLong(this.tid);
        dest.writeInt(this.rateScore);
        dest.writeTypedList(rateLog);
        dest.writeParcelable(this.author, 0);
    }

    private PostModel(Parcel in) {
        this.fid = in.readLong();
        this.sortKey = in.readLong();
        long tmpSortKeyTime = in.readLong();
        this.sortKeyTime = tmpSortKeyTime == -1 ? null : new Date(tmpSortKeyTime);
        long tmpDateline = in.readLong();
        this.dateline = tmpDateline == -1 ? null : new Date(tmpDateline);
        this.message = in.readString();
        this.authorName = in.readString();
        this.authorId = in.readLong();
        this.favorite = in.readByte() != 0;
        this.isMe = in.readByte() != 0;
        this.notGroup = in.readByte() != 0;
        this.pid = in.readLong();
        this.first = in.readByte() != 0;
        this.status = in.readInt();
        this.id = in.readString();
        this.tsAdmin = in.readByte() != 0;
        this.isAdmin = in.readByte() != 0;
        this.ordinal = in.readInt();
        this.tid = in.readLong();
        this.rateScore = in.readInt();
        in.readTypedList(rateLog, PostRate.CREATOR);
        this.author = in.readParcelable(PostAuthor.class.getClassLoader());
    }

    public static final Creator<PostModel> CREATOR = new Creator<PostModel>() {
        public PostModel createFromParcel(Parcel source) {
            return new PostModel(source);
        }

        public PostModel[] newArray(int size) {
            return new PostModel[size];
        }
    };
}
