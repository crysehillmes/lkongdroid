package org.cryse.lkong.event;

public class ThemeColorChangedEvent extends AbstractEvent {
    private int newPrimaryColor;
    private int newPrimaryDarkColor;

    public ThemeColorChangedEvent(int newPrimaryColor, int newPrimaryDarkColor) {
        this.newPrimaryColor = newPrimaryColor;
        this.newPrimaryDarkColor = newPrimaryDarkColor;
    }

    public int getNewPrimaryColor() {
        return newPrimaryColor;
    }

    public void setNewPrimaryColor(int newPrimaryColor) {
        this.newPrimaryColor = newPrimaryColor;
    }

    public int getNewPrimaryDarkColor() {
        return newPrimaryDarkColor;
    }

    public void setNewPrimaryDarkColor(int newPrimaryDarkColor) {
        this.newPrimaryDarkColor = newPrimaryDarkColor;
    }
}
