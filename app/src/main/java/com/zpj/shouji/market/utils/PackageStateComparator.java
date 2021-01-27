package com.zpj.shouji.market.utils;

import com.zpj.shouji.market.manager.AppUpdateManager;
import com.zpj.shouji.market.model.InstalledAppInfo;

import java.util.Comparator;

public class PackageStateComparator implements Comparator<InstalledAppInfo> {

    private final PinyinComparator pinyinComparator = new PinyinComparator();

    @Override
    public int compare(InstalledAppInfo o1, InstalledAppInfo o2) {
        if (o1.isDamaged() && o2.isDamaged()) {
            return pinyinComparator.compare(o1, o2);
//                            return Long.compare(o1.getAppSize(), o2.getAppSize());
        } else if (o1.isDamaged()) {
            return -1;
        } else if (o2.isDamaged()) {
            return 1;
        } else {

            boolean isUpdate1 = AppUpdateManager.getInstance().hasUpdate(o1.getPackageName());
            boolean isUpdate2 = AppUpdateManager.getInstance().hasUpdate(o2.getPackageName());

            if (isUpdate1 && isUpdate2) {
//                                return Long.compare(o1.getAppSize(), o2.getAppSize());
                return pinyinComparator.compare(o1, o2);
            } else if (isUpdate1){
                return -1;
            } else if (isUpdate2) {
                return 1;
            }

            boolean isHas1 = AppUpdateManager.getInstance().getAppIdAndType(o1.getPackageName()) != null;
            boolean isHas2 = AppUpdateManager.getInstance().getAppIdAndType(o2.getPackageName()) != null;
            if (isHas1 && isHas2) {
//                                return Long.compare(o1.getAppSize(), o2.getAppSize());
                return pinyinComparator.compare(o1, o2);
            } else if (isHas1){
                return -1;
            } else if (isHas2) {
                return 1;
            } else {
                boolean isBackup1 = o1.isBackuped();
                boolean isBackup2 = o2.isBackuped();
                if (isBackup1 && isBackup2) {
                    return Long.compare(o1.getAppSize(), o2.getAppSize());
                } else if (isBackup1){
                    return -1;
                } else if (isBackup2) {
                    return 1;
                } else {
                    return pinyinComparator.compare(o1, o2);
                }
            }
        }
    }
}
