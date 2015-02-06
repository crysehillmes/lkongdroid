package org.cryse.lkong.logic;

public class TimelineListType {
    public static final int TYPE_TIMELINE = 34;
    public static final int TYPE_AT_ME = 35;
    public static String typeToRequestParam(int typeCode) {
        switch (typeCode) {
            case TYPE_TIMELINE:
                return "?mod=data&sars=index/";
            case TYPE_AT_ME:
                return "?mod=data&sars=my/atme";
            default:
                throw new IllegalArgumentException("Unknown type.");
        }
    }
}
