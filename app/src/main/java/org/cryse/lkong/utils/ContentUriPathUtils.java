package org.cryse.lkong.utils;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.CursorLoader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.provider.DocumentsContract;
import android.provider.MediaStore;

public class ContentUriPathUtils {
    public static String getRealPathFromUri(Context context, Uri uri) {
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            if(uri.getPath().contains(":"))
                return getRealPathFromUri_API19(context, uri);
            else
                return getRealPathFromUri_API11to18(context, uri);
        } else if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            return getRealPathFromUri_API11to18(context, uri);
        } else {
            return getRealPathFromUri_BelowAPI11(context, uri);
        }
    }


    @TargetApi(Build.VERSION_CODES.KITKAT)
    private static String getRealPathFromUri_API19(Context context, Uri uri){
        Cursor cursor = null;
        try {
            // Will return "image:x*"
            String wholeID = DocumentsContract.getDocumentId(uri);

            // Split at colon, use second item in the array
            String id = wholeID.split(":")[1];

            String[] column = { MediaStore.Images.Media.DATA };

            // where id is equal to
            String sel = MediaStore.Images.Media._ID + "=?";

            cursor = context.getContentResolver().
                    query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                            column, sel, new String[]{ id }, null);

            String filePath = "";

            int columnIndex = cursor.getColumnIndex(column[0]);

            if (cursor.moveToFirst()) {
                filePath = cursor.getString(columnIndex);
            }
            return filePath;
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
    }


    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    private static String getRealPathFromUri_API11to18(Context context, Uri contentUri) {
        Cursor cursor = null;
        try {
            String[] proj = {MediaStore.Images.Media.DATA};
            String result = null;

            CursorLoader cursorLoader = new CursorLoader(
                    context,
                    contentUri, proj, null, null, null);
            cursor = cursorLoader.loadInBackground();

            if (cursor != null) {
                int column_index =
                        cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
                cursor.moveToFirst();
                result = cursor.getString(column_index);
            }
            return result;
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
    }

    private static String getRealPathFromUri_BelowAPI11(Context context, Uri contentUri){
        Cursor cursor = null;
        try {
            String[] proj = {MediaStore.Images.Media.DATA};
            cursor = context.getContentResolver().query(contentUri, proj, null, null, null);
            int column_index
                    = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            cursor.moveToFirst();

            return cursor.getString(column_index);
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
    }
}
