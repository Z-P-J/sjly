package com.zpj.shouji.market.ui.fragment.dialog;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;

import com.zpj.shouji.market.R;
import com.zpj.shouji.market.api.HttpApi;
import com.zpj.shouji.market.manager.UserManager;
import com.zpj.shouji.market.model.DiscoverInfo;
import com.zpj.shouji.market.ui.fragment.ReportFragment;
import com.zpj.toast.ZToast;

public class ThemeMoreDialogFragment extends BottomListMenuDialogFragment
        implements BottomListMenuDialogFragment.OnItemClickListener {

    private DiscoverInfo info;

    public ThemeMoreDialogFragment() {
        setMenu(R.menu.menu_tools);
        onItemClick(this);
        setTitle("更多操作");
    }

    @Override
    public void onClick(BottomListMenuDialogFragment menu, View view, MenuItem item) {
//        menu.dismiss();
        switch (item.getItemId()) {
            case R.id.copy:
                dismiss();
                ClipboardManager cm = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
                cm.setPrimaryClip(ClipData.newPlainText(null, info.getContent()));
                ZToast.success("已复制到粘贴板");
                break;
            case R.id.share:
//                dismiss();
                new ShareDialogFragment()
                        .setShareContent(getString(R.string.text_theme_share_content, info.getContent(), info.getId()))
                        .show(context);
//                dismissWithStart(
//                        new ShareDialogFragment()
//                                .setShareContent(getString(R.string.text_theme_share_content, info.getContent(), info.getId()))
//                );
                dismiss();
                break;
            case R.id.collect:
                HttpApi.addCollectionApi(info.getId(), this::dismiss);
                break;
            case R.id.delete_collect:
                dismiss();
                HttpApi.deleteCollectionApi(info.getId());
                break;
            case R.id.delete:
                dismiss();
                HttpApi.deleteThemeApi(info.getId(), info.getContentType());
                break;
            case R.id.report:
//                dismissWithStart(ReportFragment.newInstance(info));
                ReportFragment.start(info);
                dismiss();
                break;
            case R.id.black_list:
                dismiss();
                HttpApi.addBlacklistApi(info.getMemberId());
                break;
            case R.id.private_theme:
                dismiss();
                HttpApi.privateThemeApi(info.getId());
                break;
            case R.id.public_theme:
                dismiss();
                HttpApi.publicThemeApi(info.getId());
                break;
        }
    }

    public ThemeMoreDialogFragment isCollection() {
        hideMenuItemList.add(R.id.collect);
        hideMenuItemList.remove((Integer) R.id.delete_collect);
        return this;
    }

    public ThemeMoreDialogFragment isMe() {
        hideMenuItemList.add(R.id.collect);
        hideMenuItemList.add(R.id.delete_collect);
        hideMenuItemList.add(R.id.black_list);
        return this;
    }

    public ThemeMoreDialogFragment setDiscoverInfo(DiscoverInfo info) {
        this.info = info;
//        List<Integer> hideList = new ArrayList<>();
        boolean isLogin = UserManager.getInstance().isLogin();
        if (isLogin) {
            if (TextUtils.equals(info.getMemberId(), UserManager.getInstance().getUserId())) {
                hideMenuItemList.add(R.id.black_list);
            } else {
                hideMenuItemList.add(R.id.delete);
                hideMenuItemList.add(R.id.private_theme);
                hideMenuItemList.add(R.id.public_theme);
            }
//            hideMenuItemList.add(info.isCollection() ? R.id.collect : R.id.delete_collect);
            hideMenuItemList.add(R.id.delete_collect);
        } else {
            hideMenuItemList.add(R.id.collect);
            hideMenuItemList.add(R.id.delete);
            hideMenuItemList.add(R.id.delete_collect);
            hideMenuItemList.add(R.id.report);
            hideMenuItemList.add(R.id.black_list);
            hideMenuItemList.add(R.id.private_theme);
            hideMenuItemList.add(R.id.public_theme);
        }
        return this;
    }
}
