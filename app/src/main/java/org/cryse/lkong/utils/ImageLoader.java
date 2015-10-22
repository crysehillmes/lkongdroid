package org.cryse.lkong.utils;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.BitmapTransformation;

import org.cryse.lkong.R;
import org.cryse.lkong.application.NetworkPolicyManager;

public class ImageLoader {
    public static final int IMAGE_LOAD_ALWAYS = 0;
    public static final int IMAGE_LOAD_NEVER = 1;
    public static final int IMAGE_LOAD_ONLY_WIFI = 2;
    public static final int IMAGE_LOAD_AVATAR_ALWAYS = IMAGE_LOAD_ALWAYS;
    public static final int IMAGE_LOAD_AVATAR_ONLY_WIFI = IMAGE_LOAD_ONLY_WIFI;
    public static final int IMAGE_LOAD_AVATAR_NEVER = IMAGE_LOAD_NEVER;

    public static void loadAvatar(
            Context context,
            ImageView target,
            String avatarUrl,
            int avatarSize,
            BitmapTransformation transformation,
            int avatarLoadPolicy) {
        if(shouldDownloadAvatar(avatarLoadPolicy)) {
            Glide
                    .with(context)
                    .load(avatarUrl)
                    .error(R.drawable.ic_placeholder_avatar)
                    .placeholder(R.drawable.ic_placeholder_avatar)
                    .override(avatarSize, avatarSize)
                    .transform(transformation)
                    .into(target);
        } else {
            Glide
                    .with(context)
                    .load(R.drawable.ic_placeholder_avatar)
                    .error(R.drawable.ic_placeholder_avatar)
                    .placeholder(R.drawable.ic_placeholder_avatar)
                    .override(avatarSize, avatarSize)
                    .transform(transformation)
                    .into(target);
        }
    }

    public static void loadAvatar(
            Fragment fragment,
            ImageView target,
            String avatarUrl,
            int avatarSize,
            BitmapTransformation transformation,
            int avatarLoadPolicy) {
        if(shouldDownloadAvatar(avatarLoadPolicy)) {
            Glide
                    .with(fragment)
                    .load(avatarUrl)
                    .error(R.drawable.ic_placeholder_avatar)
                    .placeholder(R.drawable.ic_placeholder_avatar)
                    .override(avatarSize, avatarSize)
                    .transform(transformation)
                    .into(target);
        } else {
            Glide
                    .with(fragment)
                    .load(R.drawable.ic_placeholder_avatar)
                    .error(R.drawable.ic_placeholder_avatar)
                    .placeholder(R.drawable.ic_placeholder_avatar)
                    .override(avatarSize, avatarSize)
                    .transform(transformation)
                    .into(target);
        }
    }

    public static boolean shouldDownloadImage(int policy) {
        boolean wifiAvailability = NetworkPolicyManager.isWifiAvailable();
        switch (policy) {
            case IMAGE_LOAD_ONLY_WIFI:
                return wifiAvailability;
            case IMAGE_LOAD_NEVER:
                return false;
            case IMAGE_LOAD_ALWAYS:
            default:
                return true;
        }
    }

    public static boolean shouldDownloadAvatar(int policy) {
        boolean wifiAvailability = NetworkPolicyManager.isWifiAvailable();
        switch (policy) {
            case IMAGE_LOAD_AVATAR_ONLY_WIFI:
                return wifiAvailability;
            case IMAGE_LOAD_AVATAR_NEVER:
                return false;
            case IMAGE_LOAD_AVATAR_ALWAYS:
            default:
                return true;
        }
    }
}
