package org.cryse.lkong.event;

public class NewThreadDoneEvent implements AbstractEvent {
    private long fid;
    private long tid;

    public NewThreadDoneEvent(long fid, long tid) {
        this.fid = fid;
        this.tid = tid;
    }

    public long getFid() {
        return fid;
    }

    public void setFid(long fid) {
        this.fid = fid;
    }

    public long getTid() {
        return tid;
    }

    public void setTid(long tid) {
        this.tid = tid;
    }
}
