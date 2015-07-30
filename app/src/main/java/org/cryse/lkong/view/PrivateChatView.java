package org.cryse.lkong.view;

import org.cryse.lkong.model.PrivateMessageModel;

import java.util.List;

public interface PrivateChatView extends ContentViewEx {
    void onLoadMessagesComplete(List<PrivateMessageModel> data, boolean isLoadingMore);
    void onSendNewMessageComplete(PrivateMessageModel message);
}
