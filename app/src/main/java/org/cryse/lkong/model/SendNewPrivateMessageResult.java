package org.cryse.lkong.model;

public class SendNewPrivateMessageResult {
    private long userId;
    private String userName;
    private long targetUserId;
    private String targetUserName;
    private boolean success;
    private String errorMessage;

    public SendNewPrivateMessageResult() {
    }

    public SendNewPrivateMessageResult(long userId, String userName, long targetUserId, String targetUserName, boolean success) {
        this.userId = userId;
        this.userName = userName;
        this.targetUserId = targetUserId;
        this.targetUserName = targetUserName;
        this.success = success;
    }

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

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }
}
