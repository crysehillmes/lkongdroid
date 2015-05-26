package org.cryse.lkong.ui.navigation;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;

import com.mikepenz.materialdrawer.accountswitcher.AccountHeader;
import com.mikepenz.materialdrawer.model.ProfileDrawerItem;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import org.cryse.lkong.R;

import java.lang.ref.WeakReference;

public class PicassoProfileDrawerItem extends ProfileDrawerItem implements Target {
    private long uid;
    private WeakReference<Context> context;
    private WeakReference<AccountHeader> accountHeader;
    public ProfileDrawerItem withContext(Context context, AccountHeader accountHeader, long uid) {
        this.context = new WeakReference<Context>(context);
        this.accountHeader = new WeakReference<AccountHeader>(accountHeader);
        this.uid = uid;
        return this;
    }
    @Override
    public int getLayoutRes() {
        return R.layout.drawer_item_profile;
    }

    @Override
    public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
        if(context != null && context.get() != null)
            setIcon(new BitmapDrawable(context.get().getResources(), bitmap));
        if(accountHeader != null && accountHeader.get() != null)
            accountHeader.get().updateProfileByIdentifier(new ProfileDrawerItem().withIdentifier((int)uid));
    }

    @Override
    public void onBitmapFailed(Drawable errorDrawable) {
        setIcon(errorDrawable);
    }

    @Override
    public void onPrepareLoad(Drawable placeHolderDrawable) {
        setIcon(placeHolderDrawable);
    }
}
