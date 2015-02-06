package org.cryse.lkong.ui.navigation;

public enum NavigationType{
    ACTIVITY_SETTINGS(0),
    FRAGMENT_FORUM_LIST(1),
    FRAGMENT_FAVORITES(2),
    FRAGMENT_TIMELINE(3),
    FRAGMENT_AT_ME_MESSAGES(4);
    int navigationType;

    private NavigationType(int naviType) {
        navigationType = naviType;
    }

    public int getNavigationType() {
        return navigationType;
    }
}
