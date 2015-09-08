package org.cryse.lkong.utils.file;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;

public class FileCopier {
    public static void copyTo(File from, File to, CopyCallback copyCallback) {
        FileChannel inChannel;
        FileChannel outChannel;
        try {
            inChannel = new FileInputStream(from).getChannel();
            outChannel = new FileOutputStream(to).getChannel();
        } catch(FileNotFoundException fileNotFound) {
            if(copyCallback != null)
                copyCallback.onError(fileNotFound);
            return;
        }
        try {
            inChannel.transferTo(0, inChannel.size(), outChannel);
            if(copyCallback != null)
                copyCallback.onComplete();
        } catch (IOException exception) {
            if(copyCallback != null)
                copyCallback.onError(exception);
        } finally {
            try {
                if (inChannel != null)
                    inChannel.close();
                if (outChannel != null)
                    outChannel.close();
            } catch (IOException exception) {
                if(copyCallback != null)
                    copyCallback.onError(exception);
            }
        }
    }

    public interface CopyCallback {
        void onError(Exception exception);
        void onComplete();
    }
}
