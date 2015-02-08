package org.cryse.lkong.event;

public class NewPostDoneEvent implements AbstractEvent {
    private int replyCount;
    private long tid;

    public NewPostDoneEvent(long tid, int replyCount) {
        this.tid = tid;
        this.replyCount = replyCount;
    }

    public int getReplyCount() {
        return replyCount;
    }

    public void setReplyCount(int replyCount) {
        this.replyCount = replyCount;
    }

    public long getTid() {
        return tid;
    }

    public void setTid(long tid) {
        this.tid = tid;
    }
}
