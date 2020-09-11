//package com.zpj.shouji.market.ui.widget.popup;
//
//import android.content.ClipData;
//import android.content.ClipboardManager;
//import android.content.Context;
//import android.support.annotation.NonNull;
//import android.text.TextUtils;
//import android.view.MenuItem;
//import android.view.View;
//
//import com.felix.atoast.library.AToast;
//import com.zpj.shouji.market.R;
//import com.zpj.shouji.market.api.HttpApi;
//import com.zpj.shouji.market.manager.UserManager;
//import com.zpj.shouji.market.model.DiscoverInfo;
//import com.zpj.shouji.market.ui.fragment.ReportFragment;
//
//public class ThemeMorePopupMenu extends BottomListPopupMenu
//        implements BottomListPopupMenu.OnItemClickListener {
//
//    private DiscoverInfo info;
//
//    public static ThemeMorePopupMenu with(Context context) {
//        return new ThemeMorePopupMenu(context);
//    }
//
//    private ThemeMorePopupMenu(@NonNull Context context) {
//        super(context);
//        setMenu(R.menu.menu_tools);
//        onItemClick(this);
//        setTitle("更多操作");
//    }
//
//    @Override
//    public BottomListPopupMenu show() {
//        return super.show();
//    }
//
//    @Override
//    public void onClick(BottomListPopupMenu menu, View view, MenuItem item) {
//        switch (item.getItemId()) {
//            case R.id.copy:
//                ClipboardManager cm = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
//                cm.setPrimaryClip(ClipData.newPlainText(null, info.getContent()));
//                AToast.success("已复制到粘贴板");
//                break;
//            case R.id.share:
//                dismiss();
//                SharePopup.with(getContext())
//                        .setShareContent(getContext().getString(R.string.text_theme_share_content, info.getContent(), info.getId()))
//                        .show();
//                break;
//            case R.id.collect:
//                HttpApi.addCollectionApi(info.getId(), this::dismiss);
//                break;
//            case R.id.delete_collect:
//                HttpApi.deleteCollectionApi(info.getId());
//                break;
//            case R.id.delete:
//                HttpApi.deleteThemeApi(info.getId(), info.getContentType());
//                break;
//            case R.id.report:
//                AToast.normal("TODO 举报");
//                ReportFragment.start(info);
//                break;
//            case R.id.black_list:
//                HttpApi.addBlacklistApi(info.getMemberId());
//                break;
//            case R.id.private_theme:
//                HttpApi.privateThemeApi(info.getId());
//                break;
//            case R.id.public_theme:
//                HttpApi.publicThemeApi(info.getId());
//                break;
//        }
//        menu.dismiss();
//    }
//
//    public ThemeMorePopupMenu isCollection() {
//        hideMenuItemList.add(R.id.collect);
//        hideMenuItemList.remove((Integer) R.id.delete_collect);
//        return this;
//    }
//
//    public ThemeMorePopupMenu setDiscoverInfo(DiscoverInfo info) {
//        this.info = info;
////        List<Integer> hideList = new ArrayList<>();
//        boolean isLogin = UserManager.getInstance().isLogin();
//        if (isLogin) {
//            if (TextUtils.equals(info.getMemberId(), UserManager.getInstance().getUserId())) {
//                hideMenuItemList.add(R.id.black_list);
//            } else {
//                hideMenuItemList.add(R.id.delete);
//                hideMenuItemList.add(R.id.private_theme);
//                hideMenuItemList.add(R.id.public_theme);
//            }
////            hideMenuItemList.add(info.isCollection() ? R.id.collect : R.id.delete_collect);
//            hideMenuItemList.add(R.id.delete_collect);
//        } else {
//            hideMenuItemList.add(R.id.collect);
//            hideMenuItemList.add(R.id.delete);
//            hideMenuItemList.add(R.id.delete_collect);
//            hideMenuItemList.add(R.id.report);
//            hideMenuItemList.add(R.id.black_list);
//            hideMenuItemList.add(R.id.private_theme);
//            hideMenuItemList.add(R.id.public_theme);
//        }
//        return this;
//    }
//}
