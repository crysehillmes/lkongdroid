package org.cryse.lkong.ui.dialog;

import android.app.Activity;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.malinskiy.superrecyclerview.SuperRecyclerView;

import org.cryse.lkong.R;
import org.cryse.widget.recyclerview.RecyclerViewBaseAdapter;
import org.cryse.widget.recyclerview.RecyclerViewHolder;
import org.cryse.widget.recyclerview.RecyclerViewOnItemClickListener;
import org.cryse.widget.recyclerview.SimpleRecyclerViewAdapter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.Bind;

public class EmoticonDialog extends DialogFragment {

    private Callback mCallback;
    SuperRecyclerView mEmoticonCollectionView;
    List<String> mEmoticonFileNames;
    EmoticonAdapter mCollectionAdapter;

    public static interface Callback {
        void onEmoticonSelection(String emoticonName);
    }

    public EmoticonDialog() {
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        MaterialDialog dialog = new MaterialDialog.Builder(getActivity())
                .title(R.string.dialog_choose_emoticon_title)
                .autoDismiss(false)
                .customView(R.layout.dialog_emoticon, false)
                .build();

        mEmoticonCollectionView = (SuperRecyclerView) dialog.getCustomView().findViewById(R.id.dialog_emoticon_recyclerview);
        mEmoticonFileNames = listAssetFiles("emoji");
        Collections.sort(mEmoticonFileNames, new EmojiComparator());
        mEmoticonCollectionView.getRecyclerView().setItemAnimator(new DefaultItemAnimator());
        mEmoticonCollectionView.setLayoutManager(new GridLayoutManager(getActivity(), 5));
        mCollectionAdapter = new EmoticonAdapter(getActivity(), mEmoticonFileNames);
        mEmoticonCollectionView.setAdapter(mCollectionAdapter);
        mCollectionAdapter.setOnItemClickListener(new RecyclerViewOnItemClickListener() {
            @Override
            public void onItemClick(View view, int position, long id) {
                if(mCallback != null)
                    mCallback.onEmoticonSelection(mCollectionAdapter.getItem(position));
                EmoticonDialog.this.dismiss();
            }
        });

        return dialog;
    }

    private class EmojiComparator implements Comparator<String> {
        @Override
        public int compare(String lhs, String rhs) {
            int leftDotIndex = lhs.indexOf(".");
            int rightDotIndex = rhs.indexOf(".");
            Integer left = Integer.valueOf(lhs.substring(2,leftDotIndex));
            Integer right = Integer.valueOf(rhs.substring(2,rightDotIndex));
            return left.compareTo(right);
        }
    }

    private List<String> listAssetFiles(String path) {

        String[] fileNameList;
        try {
            fileNameList = getResources().getAssets().list(path);
            if (fileNameList.length > 0) {
                // This is a folder
                List<String> resultList = new ArrayList<String>();
                Collections.addAll(resultList, fileNameList);
                return resultList;
            } else {
                return new ArrayList<String>();
            }
        } catch (IOException e) {
            return new ArrayList<String>();
        }
    }

    public void show(Activity context, Callback callback) {
        mCallback = callback;
        show(context.getFragmentManager(), "EMOTICON_SELECTOR");
    }

    public static class EmoticonAdapter extends SimpleRecyclerViewAdapter<String> {
        public EmoticonAdapter(Context context, List<String> mItemList) {
            super(context, mItemList);
        }

        @Override
        public RecyclerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_emoticon, parent, false);
            return new ViewHolder(v);
        }

        @Override
        public void onBindViewHolder(RecyclerViewHolder holder, int position) {
            super.onBindViewHolder(holder, position);
            ViewHolder viewHolder = (ViewHolder) holder;
            String emoticonPath = getItem(position);
            try {
                Drawable d = Drawable.createFromStream(mContext.getAssets().open("emoji/" + emoticonPath), null);
                viewHolder.mEmoticonImageView.setImageDrawable(d);
            } catch (IOException e) {
                e.printStackTrace();
                viewHolder.mEmoticonImageView.setImageResource(R.drawable.placeholder_error);
            }
        }

        public static class ViewHolder extends RecyclerViewHolder {
            // each data item is just a string in this case

            @Bind(R.id.recyclerview_item_emoticon_imageview)
            public ImageView mEmoticonImageView;

            public ViewHolder(View v) {
                super(v);
                ButterKnife.bind(this, v);
            }
        }
    }

}