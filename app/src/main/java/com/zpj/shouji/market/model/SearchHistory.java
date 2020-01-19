package com.zpj.shouji.market.model;

import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.PrimaryKey;
import com.raizlabs.android.dbflow.annotation.Table;
import com.raizlabs.android.dbflow.structure.BaseModel;
import com.zpj.shouji.market.database.SearchHistoryManager;

@Table(database = SearchHistoryManager.class)
public class SearchHistory extends BaseModel {

    @PrimaryKey(autoincrement = true)
    private int id;
    @Column
    private String text;
    @Column
    private long time;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }
}
