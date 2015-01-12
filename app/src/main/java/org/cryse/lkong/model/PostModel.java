package org.cryse.lkong.model;


import java.util.Date;
import java.util.List;

public class PostModel {
    private long fid;
    private long sortKey;
    private Date dateline;
    private String message;
    private String authorName;
    private long authorId;
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
    private List<PostRate> rateLog;
    private PostAuthor author;

    public PostModel() {
    }

    public PostModel(long fid, long sortKey, Date dateline, String message, String authorName, long authorId, boolean isMe, boolean notGroup, long pid, boolean first, int status, String id, boolean tsAdmin, boolean isAdmin, int ordinal, long tid, List<PostRate> rateLog, PostAuthor author) {
        this.fid = fid;
        this.sortKey = sortKey;
        this.dateline = dateline;
        this.message = message;
        this.authorName = authorName;
        this.authorId = authorId;
        this.isMe = isMe;
        this.notGroup = notGroup;
        this.pid = pid;
        this.first = first;
        this.status = status;
        this.id = id;
        this.tsAdmin = tsAdmin;
        this.isAdmin = isAdmin;
        this.ordinal = ordinal;
        this.tid = tid;
        this.rateLog = rateLog;
        this.author = author;
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

    public static class PostAuthor {
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
    }

    public static class PostRate {
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
    }
}
