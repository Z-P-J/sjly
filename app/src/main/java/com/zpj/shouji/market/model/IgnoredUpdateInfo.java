package com.zpj.shouji.market.model;

import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.PrimaryKey;
import com.raizlabs.android.dbflow.annotation.Table;
import com.raizlabs.android.dbflow.structure.BaseModel;
import com.zpj.shouji.market.database.IgnoredUpdateManager;
import com.zpj.shouji.market.utils.PinyinComparator;

@Table(database = IgnoredUpdateManager.class)
public class IgnoredUpdateInfo extends BaseModel implements PinyinComparator.PinyinComparable {

    @PrimaryKey(autoincrement = true)
    private int id;
    @Column
    private String appName;
    @Column
    private String packageName;

    private AppUpdateInfo updateInfo;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getAppName() {
        return appName;
    }

    public void setAppName(String appName) {
        this.appName = appName;
    }

    public String getPackageName() {
        return packageName;
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }

    public void setUpdateInfo(AppUpdateInfo updateInfo) {
        this.updateInfo = updateInfo;
    }

    public AppUpdateInfo getUpdateInfo() {
        return updateInfo;
    }

    @Override
    public String getName() {
        return getAppName();
    }
}
