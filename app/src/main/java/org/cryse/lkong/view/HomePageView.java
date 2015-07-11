package org.cryse.lkong.view;

import org.cryse.lkong.model.PunchResult;

public interface HomePageView extends CheckNoticeCountView {
    void onPunchUserComplete(PunchResult punchResult);
}
