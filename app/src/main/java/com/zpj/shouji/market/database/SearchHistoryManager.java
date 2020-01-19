package com.zpj.shouji.market.database;

import com.raizlabs.android.dbflow.annotation.Database;
import com.raizlabs.android.dbflow.sql.language.SQLite;
import com.zpj.shouji.market.model.SearchHistory;
import com.zpj.shouji.market.model.SearchHistory_Table;

import java.util.List;

@Database(name = SearchHistoryManager.NAME, version = SearchHistoryManager.VERSION)
public class SearchHistoryManager {

    static final String NAME = "search_history_manager";
    public static final int VERSION = 1;

    public static List<SearchHistory> getAllSearchHistory() {
        return SQLite.select()
                .from(SearchHistory.class)
                .orderBy(SearchHistory_Table.time, false)
                .queryList();
    }

    public static SearchHistory getSearchHistoryByText(String text) {
        List<SearchHistory> historyList =  SQLite.select()
                .from(SearchHistory.class)
                .where(SearchHistory_Table.text.is(text))
                .queryList();
        if (historyList.isEmpty()) {
            return null;
        } else {
            for (int i = 1; i < historyList.size(); i++) {
                historyList.get(i).delete();
            }
            return historyList.get(0);
        }
    }

    public static void deleteAllLocalSearchHistory() {
        SQLite.delete()
                .from(SearchHistory.class)
                .execute();
    }

}
