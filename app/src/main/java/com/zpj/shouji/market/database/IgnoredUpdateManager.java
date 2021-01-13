package com.zpj.shouji.market.database;

import com.raizlabs.android.dbflow.annotation.Database;
import com.raizlabs.android.dbflow.sql.language.SQLite;
import com.zpj.shouji.market.model.IgnoredUpdateInfo;
import com.zpj.shouji.market.model.IgnoredUpdateInfo_Table;
import com.zpj.shouji.market.model.SearchHistory;
import com.zpj.shouji.market.model.SearchHistory_Table;

import java.util.List;

@Database(name = IgnoredUpdateManager.NAME, version = IgnoredUpdateManager.VERSION)
public class IgnoredUpdateManager {

    static final String NAME = "ignored_update_manager";
    public static final int VERSION = 1;

    public static List<IgnoredUpdateInfo> getAllIgnoredUpdateApp() {
        return SQLite.select()
                .from(IgnoredUpdateInfo.class)
                .orderBy(IgnoredUpdateInfo_Table.id, false)
                .queryList();
    }

}
