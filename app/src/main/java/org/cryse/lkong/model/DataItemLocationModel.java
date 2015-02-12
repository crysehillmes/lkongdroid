package org.cryse.lkong.model;

public class DataItemLocationModel {
    private boolean isLoad;
    private String location;
    private int ordinal;

    public boolean isLoad() {
        return isLoad;
    }

    public void setLoad(boolean isLoad) {
        this.isLoad = isLoad;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public int getOrdinal() {
        return ordinal;
    }

    public void setOrdinal(int ordinal) {
        this.ordinal = ordinal;
    }
}
