package org.cryse.lkong.logic;

public class ThreadListType {
    public static final int TYPE_SORT_BY_REPLY = 0;
    public static final int TYPE_DIGEST = 1;
    public static final int TYPE_SORT_BY_POST = 2;

    public static String typeToRequestParam(int typeCode) {
        switch (typeCode) {
            case TYPE_SORT_BY_REPLY:
                return "";
            case TYPE_SORT_BY_POST:
                return "/thread_dateline";
            case TYPE_DIGEST:
                return "/digest";
            default:
                throw new IllegalArgumentException("Unknown type.");
        }
    }
}
