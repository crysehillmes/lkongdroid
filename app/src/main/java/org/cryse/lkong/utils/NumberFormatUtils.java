package org.cryse.lkong.utils;

public class NumberFormatUtils {
    public static String humanReadableByteCount(long bytes, boolean si) {
        int unit = si ? 1000 : 1024;
        if (bytes < unit) return bytes + " B";
        int exp = (int) (Math.log(bytes) / Math.log(unit));
        String pre = (si ? "kMGTPE" : "KMGTPE").charAt(exp-1) + (si ? "" : "i");
        return String.format("%.1f %sB", bytes / Math.pow(unit, exp), pre);
    }

    public static String numberToKiloString(long number, String kiloSuffix, boolean keepSpace, boolean keepIfLess) {
        return numberToString(number, 1000, kiloSuffix, keepSpace, keepIfLess);

    }

    public static String numberToTenKiloString(long number, String kiloSuffix, boolean keepSpace, boolean keepIfLess) {
        return numberToString(number, 10000, kiloSuffix, keepSpace, keepIfLess);
    }

    public static String numberToString(long number, long unit, String suffix, boolean keepSpace, boolean keepIfLess) {
        if(keepIfLess && number < unit)
            return Long.toString(number);
        return String.format("%.1f%s%s", (double)number/(double)unit, keepSpace ? " " : "", suffix);
    }
}
