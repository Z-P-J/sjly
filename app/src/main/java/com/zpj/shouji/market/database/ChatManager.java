package com.zpj.shouji.market.database;

import com.raizlabs.android.dbflow.annotation.Database;
import com.raizlabs.android.dbflow.sql.language.SQLite;
import com.zpj.shouji.market.model.ChatMessageBean;

import java.util.List;

@Database(name = ChatManager.NAME, version = ChatManager.VERSION)
public class ChatManager {

    static final String NAME = "chat_manager";
    public static final int VERSION = 1;

    public static long getPages(int number) {
        long count = SQLite.selectCountOf()
                .from(ChatMessageBean.class)
                .count();
        long page = count / number;
        if (page > 0 && count % number == 0) {
            return page - 1;
        }
        return page;
    }

    public static List<ChatMessageBean> loadPages(int page, int number) {
        return SQLite.select()
                .from(ChatMessageBean.class)
                .offset(page * number)
                .limit(number)
                .queryList();
    }

    public static List<ChatMessageBean> loadPages() {
        return SQLite.select()
                .from(ChatMessageBean.class)
                .queryList();
    }

}
