package org.cryse.lkong.modules.postlist;

import org.cryse.lkong.model.DataItemLocationModel;
import org.cryse.lkong.model.PostModel;
import org.cryse.lkong.model.ThreadInfoModel;
import org.cryse.lkong.modules.base.ContentView;

import java.util.List;

public interface PostListView extends ContentView {
    int SHOW_MODE_REPLACE_SIMPLE = 0;
    int SHOW_MODE_REPLACE = 1;
    int SHOW_MODE_PREV_PAGE = 2;
    int SHOW_MODE_NEXT_PAGE = 3;
    void showPostList(int page, List<PostModel> posts, boolean refreshPosition, int showMode, Throwable throwable);
    void onGetPostLocationComplete(DataItemLocationModel locationModel, boolean loadThreadInfo);
    void onLoadThreadInfoComplete(ThreadInfoModel threadInfoModel, Throwable throwable);
    void onAddOrRemoveFavoriteComplete(boolean isFavorite);
    void onRatePostComplete(PostModel.PostRate postRate);
}
