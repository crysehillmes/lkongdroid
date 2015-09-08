package org.cryse.lkong.ui.search;

import android.content.Context;

import org.cryse.lkong.R;
import org.cryse.widget.persistentsearch.SearchItem;
import org.cryse.widget.persistentsearch.SearchSuggestionsBuilder;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class SuggestionsBuilder implements SearchSuggestionsBuilder {
    private Context mContext;
    private List<SearchItem> mHistorySuggestions = new ArrayList<SearchItem>();

    public SuggestionsBuilder(Context context) {
        this.mContext = context;
        createHistorys();
    }

    private void createHistorys() {
    }

    @Override
    public Collection<SearchItem> buildEmptySearchSuggestion(int maxCount) {
        List<SearchItem> items = new ArrayList<SearchItem>();
        items.addAll(mHistorySuggestions);
        return items;
    }

    @Override
    public Collection<SearchItem> buildSearchSuggestion(int maxCount, String query) {
        List<SearchItem> items = new ArrayList<SearchItem>();
        if(query.startsWith("@")) {
            SearchItem peopleSuggestion = new SearchItem(
                    mContext.getString(R.string.text_search_people) + query.substring(1),
                    query,
                    SearchItem.TYPE_SEARCH_ITEM_SUGGESTION
            );
            items.add(peopleSuggestion);
        } else if(query.startsWith("#")) {
            SearchItem toppicSuggestion = new SearchItem(
                    mContext.getString(R.string.text_search_forum) + query.substring(1),
                    query,
                    SearchItem.TYPE_SEARCH_ITEM_SUGGESTION
            );
            items.add(toppicSuggestion);
        } else {
            SearchItem peopleSuggestion = new SearchItem(
                    mContext.getString(R.string.text_search_people) + query,
                    "@" + query,
                    SearchItem.TYPE_SEARCH_ITEM_SUGGESTION
            );
            items.add(peopleSuggestion);
            SearchItem toppicSuggestion = new SearchItem(
                    mContext.getString(R.string.text_search_forum) + query,
                    "#" + query,
                    SearchItem.TYPE_SEARCH_ITEM_SUGGESTION
            );
            items.add(toppicSuggestion);
        }
        for(SearchItem item : mHistorySuggestions) {
            if(item.getValue().startsWith(query)) {
                items.add(item);
            }
        }
        return items;
    }
}
