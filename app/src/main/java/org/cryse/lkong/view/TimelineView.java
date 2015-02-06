package org.cryse.lkong.view;

import org.cryse.lkong.model.TimelineModel;

import java.util.List;

public interface TimelineView extends ContentViewEx {
    public void showTimeline(List<TimelineModel> timelineItems, boolean loadMore);
}
