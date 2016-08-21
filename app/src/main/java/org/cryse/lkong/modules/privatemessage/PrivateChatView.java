package org.cryse.lkong.modules.privatemessage;

import org.cryse.lkong.model.PrivateMessageModel;
import org.cryse.lkong.model.SendNewPrivateMessageResult;
import org.cryse.lkong.modules.base.ContentViewEx;

import java.util.List;

public interface PrivateChatView extends ContentViewEx {
    void onLoadMessagesComplete(List<PrivateMessageModel> data, int pointerType, boolean isLoadingMore);
    void onSendNewMessageComplete(SendNewPrivateMessageResult result);
}
