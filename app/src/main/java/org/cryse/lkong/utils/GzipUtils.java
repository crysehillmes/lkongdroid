package org.cryse.lkong.utils;

import org.cryse.utils.MiniIOUtils;

import java.io.ByteArrayInputStream;
import java.util.zip.GZIPInputStream;

public class GzipUtils {
    public static String decompress(byte[] bytes) throws Exception {
        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(bytes);
        GZIPInputStream gis = new GZIPInputStream(byteArrayInputStream);
        String resultString = MiniIOUtils.toString(gis);
        gis.close();
        byteArrayInputStream.close();
        return resultString;
    }
}
