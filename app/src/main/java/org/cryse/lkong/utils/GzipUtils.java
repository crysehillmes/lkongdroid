package org.cryse.lkong.utils;

import android.text.TextUtils;

import org.cryse.utils.MiniIOUtils;

import java.io.ByteArrayInputStream;
import java.util.zip.GZIPInputStream;

import okhttp3.Response;

public class GzipUtils {
    public static String responseToString(Response response) throws Exception {
        String contentEncoding = response.header("Content-Encoding");
        if(!TextUtils.isEmpty(contentEncoding) && contentEncoding.contains("gzip")) {
            return decompress(response.body().bytes());
        } else {
            return response.body().string();
        }
    }

    private static String decompress(byte[] bytes) throws Exception {
        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(bytes);
        GZIPInputStream gis = new GZIPInputStream(byteArrayInputStream);
        String resultString = MiniIOUtils.toString(gis);
        gis.close();
        byteArrayInputStream.close();
        return resultString;
    }
}
