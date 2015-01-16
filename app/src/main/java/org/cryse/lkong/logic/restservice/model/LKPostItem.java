package org.cryse.lkong.logic.restservice.model;

import java.util.Date;
import java.util.List;

public class LKPostItem {
    private long fid;
    private long sortkey;
    private Date dateline;
    private String message;
    private String author;
    private long authorid;
    private int isme; // Gson add int to boolean converter
    private int notgroup; // Gson add int to boolean converter
    private String pid; // GSON add String to long converter
    private int first; // Gson add int to boolean converter
    private int status;
    private String id; // GSON add String to long converter
    private boolean tsadmin;
    private int isadmin;
    private int lou;
    private long tid;
    private List<LKPostRateItem> ratelog;
    private LKPostUser alluser;

    public long getFid() {
        return fid;
    }

    public void setFid(long fid) {
        this.fid = fid;
    }

    public long getSortkey() {
        return sortkey;
    }

    public void setSortkey(long sortkey) {
        this.sortkey = sortkey;
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

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public long getAuthorid() {
        return authorid;
    }

    public void setAuthorid(long authorid) {
        this.authorid = authorid;
    }

    public int getIsme() {
        return isme;
    }

    public void setIsme(int isme) {
        this.isme = isme;
    }

    public int getNotgroup() {
        return notgroup;
    }

    public void setNotgroup(int notgroup) {
        this.notgroup = notgroup;
    }

    public String getPid() {
        return pid;
    }

    public void setPid(String pid) {
        this.pid = pid;
    }

    public int getFirst() {
        return first;
    }

    public void setFirst(int first) {
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

    public boolean isTsadmin() {
        return tsadmin;
    }

    public void setTsadmin(boolean tsadmin) {
        this.tsadmin = tsadmin;
    }

    public int getIsadmin() {
        return isadmin;
    }

    public void setIsadmin(int isadmin) {
        this.isadmin = isadmin;
    }

    public int getLou() {
        return lou;
    }

    public void setLou(int lou) {
        this.lou = lou;
    }

    public long getTid() {
        return tid;
    }

    public void setTid(long tid) {
        this.tid = tid;
    }

    public List<LKPostRateItem> getRatelog() {
        return ratelog;
    }

    public void setRatelog(List<LKPostRateItem> ratelog) {
        this.ratelog = ratelog;
    }

    public LKPostUser getAlluser() {
        return alluser;
    }

    public void setAlluser(LKPostUser alluser) {
        this.alluser = alluser;
    }
}
