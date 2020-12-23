package com.zpj.shouji.market.ui.fragment.dialog;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;

import com.zpj.toast.ZToast;
import com.zpj.shouji.market.R;
import com.zpj.shouji.market.api.HttpApi;
import com.zpj.shouji.market.manager.UserManager;
import com.zpj.shouji.market.model.CollectionInfo;
import com.zpj.shouji.market.model.DiscoverInfo;
import com.zpj.shouji.market.ui.fragment.ReportFragment;

public class AppCollectionMoreDialogFragment extends BottomListMenuDialogFragment
        implements BottomListMenuDialogFragment.OnItemClickListener {

    private CollectionInfo info;

    public AppCollectionMoreDialogFragment() {
        setMenu(R.menu.menu_app_collection);
        onItemClick(this);
        setTitle("更多操作");
    }

    @Override
    public void onClick(BottomListMenuDialogFragment menu, View view, MenuItem item) {
//        menu.dismiss();
        dismiss();
        switch (item.getItemId()) {
            case R.id.share:
                new ShareDialogFragment()
                        .setShareContent(getString(R.string.text_app_collection_share_content, info.getTitle(), info.getMemberId(), info.getId()))
                        .show(context);
                break;
            case R.id.collect:
                HttpApi.addFavCollectionApi(info.getId(), info.getType());
                break;
            case R.id.delete_collect:
                HttpApi.delFavCollectionApi(info.getId(), info.getType());
                break;
            case R.id.delete:
                HttpApi.deleteThemeApi(info.getId(), info.getContentType());
                break;
            case R.id.private_theme:
                HttpApi.privateThemeApi(info.getId());
                break;
            case R.id.public_theme:
                HttpApi.publicThemeApi(info.getId());
                break;
        }
    }

    public AppCollectionMoreDialogFragment isCollection() {
        hideMenuItemList.add(R.id.collect);
        hideMenuItemList.remove((Integer) R.id.delete_collect);
        return this;
    }

    public AppCollectionMoreDialogFragment isMe() {
        hideMenuItemList.add(R.id.collect);
        hideMenuItemList.add(R.id.delete_collect);
        return this;
    }

    public AppCollectionMoreDialogFragment setCollectionInfo(CollectionInfo info) {
        this.info = info;
        boolean isLogin = UserManager.getInstance().isLogin();
        if (isLogin) {
            if (!TextUtils.equals(info.getMemberId(), UserManager.getInstance().getUserId())) {
                hideMenuItemList.add(R.id.delete);
                hideMenuItemList.add(R.id.private_theme);
                hideMenuItemList.add(R.id.public_theme);
            }
            hideMenuItemList.add(R.id.delete_collect);
        } else {
            hideMenuItemList.add(R.id.collect);
            hideMenuItemList.add(R.id.delete);
            hideMenuItemList.add(R.id.delete_collect);
            hideMenuItemList.add(R.id.private_theme);
            hideMenuItemList.add(R.id.public_theme);
        }
        return this;
    }
}
