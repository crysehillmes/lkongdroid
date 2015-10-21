package org.cryse.lkong.utils;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.BitmapTransformation;

import org.cryse.lkong.R;

public class ImageLoader {
    public static final int IMAGE_LOAD_AVATAR_ALWAYS = 0;
    public static final int IMAGE_LOAD_AVATAR_ONLY_WIFI = 1;
    public static final int IMAGE_LOAD_AVATAR_NEVER = 2;

    public static void loadAvatar(
            Context context,
            ImageView target,
            String avatarUrl,
            int avatarSize,
            BitmapTransformation transformation,
            int avatarLoadPolicy) {
        Glide
                .with(context)
                .load(avatarUrl)
                .error(R.drawable.ic_placeholder_avatar)
                .placeholder(R.drawable.ic_placeholder_avatar)
                .override(avatarSize, avatarSize)
                .transform(transformation)
                .into(target);
    }

    public static void loadAvatar(
            Fragment fragment,
            ImageView target,
            String avatarUrl,
            int avatarSize,
            BitmapTransformation transformation,
            int avatarLoadPolicy) {
        Glide
                .with(fragment)
                .load(avatarUrl)
                .error(R.drawable.ic_placeholder_avatar)
                .placeholder(R.drawable.ic_placeholder_avatar)
                .override(avatarSize, avatarSize)
                .transform(transformation)
                .into(target);
    }
}
