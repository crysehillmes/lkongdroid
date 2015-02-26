package org.cryse.lkong.view;

import org.cryse.lkong.model.DataItemLocationModel;
import org.cryse.lkong.model.PostModel;
import org.cryse.lkong.model.ThreadInfoModel;

import java.util.List;

public interface PostListView extends ContentView{
    public static final int SHOW_MODE_REPLACE = 0;
    public static final int SHOW_MODE_PREV_PAGE = 1;
    public static final int SHOW_MODE_NEXT_PAGE = 2;
    public void showPostList(int page, List<PostModel> posts, boolean refreshPosition, int showMode);
    public void onGetPostLocationComplete(DataItemLocationModel locationModel, boolean loadThreadInfo);
    public void onLoadThreadInfoComplete(ThreadInfoModel threadInfoModel);
    public void onAddOrRemoveFavoriteComplete(boolean isFavorite);
    public void onRatePostComplete(PostModel.PostRate postRate);
}
