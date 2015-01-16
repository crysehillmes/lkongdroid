package org.cryse.lkong.view;

import org.cryse.lkong.model.PostModel;
import org.cryse.lkong.model.ThreadInfoModel;

import java.util.List;

public interface PostListView extends ContentView{
    public void showPostList(int page, List<PostModel> posts);
    public void onLoadThreadInfoComplete(ThreadInfoModel threadInfoModel);
}
