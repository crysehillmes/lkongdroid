package org.cryse.lkong.ui.navigation;

public enum NavigationType{
    ACTIVITY_SETTINGS(0),
    FRAGMENT_NOTIFICATION(1),
    FRAGMENT_FORUM_LIST(2),
    FRAGMENT_FAVORITES(3),
    FRAGMENT_TIMELINE(4),
    FRAGMENT_MENTIONS(5);
    int navigationType;

    private NavigationType(int naviType) {
        navigationType = naviType;
    }

    public int getNavigationType() {
        return navigationType;
    }
}
