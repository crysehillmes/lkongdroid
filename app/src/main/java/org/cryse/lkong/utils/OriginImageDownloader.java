package org.cryse.lkong.utils;

import android.util.Log;

import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import org.apache.tika.Tika;
import org.apache.tika.mime.MimeType;
import org.apache.tika.mime.MimeTypeException;
import org.apache.tika.mime.MimeTypes;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileFilter;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.ref.WeakReference;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import rx.Observable;

public class OriginImageDownloader {
    private OkHttpClient mOkHttpClient;
    private WeakReference<File> mAndroidCacheDir;
    private String mSubCacheDir;
    public OriginImageDownloader(OkHttpClient okHttpClient, File androidCacheDir, String subCacheDir) {
        this.mOkHttpClient = okHttpClient;
        this.mAndroidCacheDir = new WeakReference<File>(androidCacheDir);
        this.mSubCacheDir = subCacheDir;
    }

    public static String md5(final String s) {
        final String MD5 = "MD5";
        try {
            // Create MD5 Hash
            MessageDigest digest = java.security.MessageDigest
                    .getInstance(MD5);
            digest.update(s.getBytes());
            byte messageDigest[] = digest.digest();

            // Create Hex String
            StringBuilder hexString = new StringBuilder();
            for (byte aMessageDigest : messageDigest) {
                String h = Integer.toHexString(0xFF & aMessageDigest);
                while (h.length() < 2)
                    h = "0" + h;
                hexString.append(h);
            }
            return hexString.toString();

        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return "";
    }

    public Observable<String> downloadImage(String url){
        return Observable.create(subscriber -> {
            try {
                File cachePath = new File(mAndroidCacheDir.get(), mSubCacheDir);
                if(!cachePath.exists())
                    if(!cachePath.mkdir())
                        throw new IllegalStateException();
                String fileName = urlToFileName(url);
                File[] fileWithoutExtension = findFilesForId(cachePath, fileName);
                if(fileWithoutExtension.length > 0) {
                    subscriber.onNext(fileWithoutExtension[0].getPath());
                } else {
                    Response response = runNetworkRequest(url);
                    InputStream inputStream = response.body().byteStream();
                    String fullLocalPath = saveToFile(cachePath, fileName, inputStream);
                    subscriber.onNext(fullLocalPath);
                }

                subscriber.onCompleted();
            } catch (Exception e) {
                subscriber.onError(e);
            }
        });
    }

    public static String urlToFileName(String url) {
        return md5(url);
    }

    private Response runNetworkRequest(String url) throws Exception {
        Request request = new Request.Builder()
                .url(url)
                .build();

        Response response = mOkHttpClient.newCall(request).execute();
        if (!response.isSuccessful()) throw new IOException("Unexpected code " + response);
        return response;
    }

    private String saveToFile(File path, String fileName, InputStream rawInputStream) throws IOException {
        BufferedInputStream inputStream = new BufferedInputStream(rawInputStream);
        final File targetFile = new File(path, fileName);
        String fullFileName = targetFile.getPath();
        try {
            Tika tika = new Tika();
            String mimeString = tika.detect(inputStream);
            inputStream.reset();
            MimeTypes allTypes = MimeTypes.getDefaultMimeTypes();
            MimeType jpeg = allTypes.forName(mimeString);
            String extension = jpeg.getExtension(); // .jpg
            final OutputStream output = new FileOutputStream(targetFile);
            try {
                try {

                    final byte[] buffer = new byte[1024];
                    int read;

                    while ((read = inputStream.read(buffer)) != -1)
                        output.write(buffer, 0, read);

                    output.flush();
                } finally {
                    output.close();
                }
            } catch (Exception e) {
                Log.e("OriginImageDownloader", "saveToFile error.", e);
            }
            if(targetFile.exists()) {
                targetFile.renameTo(new File(path, fileName + extension));
                fullFileName = fullFileName + extension;
            }
        } catch (MimeTypeException e) {
            Log.e("OriginImageDownloader", "Unknown image type error.", e);
        } finally {
            inputStream.close();
        }
        return fullFileName;
    }

    public static void removeCachedImage(File androidCachePath, String subCacheDir, String fileName) {
        File cachePath = new File(androidCachePath, subCacheDir);
        File[] fileWithoutExtension = findFilesForId(cachePath, fileName);
        for (File file : fileWithoutExtension) {
            if(file.exists())
                file.delete();
        }
    }


    public static File[] findFilesForId(File dir, final String nameWithoutExtension) {
        return dir.listFiles(new FileFilter() {
            public boolean accept(File pathname) {
                return pathname.getName().startsWith(nameWithoutExtension);
            }
        });
    }
}
