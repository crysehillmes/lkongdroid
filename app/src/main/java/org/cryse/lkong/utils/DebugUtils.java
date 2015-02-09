package org.cryse.lkong.utils;

import android.os.Environment;

import java.io.File;
import java.io.FileOutputStream;

public class DebugUtils {
    public static void saveToSDCard(String filename, String content)throws Exception {
        File file = new File(Environment.getExternalStorageDirectory(), filename);//指定文件存储目录为SD卡，文件名
        FileOutputStream outStream = new FileOutputStream(file);//输出文件流
        outStream.write(content.getBytes());
        outStream.close();
    }
}
