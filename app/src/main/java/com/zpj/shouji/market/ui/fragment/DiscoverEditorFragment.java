package com.zpj.shouji.market.ui.fragment;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;

import com.felix.atoast.library.AToast;
import com.zpj.dialog.ZAlertDialog;
import com.zpj.popupmenuview.popup.EverywherePopup;
import com.zpj.shouji.market.R;
import com.zpj.shouji.market.ui.fragment.base.BaseFragment;
import com.zpj.utils.KeyboardRepairer;

import me.jingbin.richeditor.bottomlayout.LuBottomMenu;
import me.jingbin.richeditor.editrichview.SimpleRichEditor;
import me.jingbin.richeditor.editrichview.base.RichEditor;
import me.yokeyword.fragmentation.anim.DefaultNoAnimator;
import me.yokeyword.fragmentation.anim.FragmentAnimator;

public class DiscoverEditorFragment extends BaseFragment {

    private final String headerImageSrc = "https://upload.jianshu.io/admin_banners/web_images/4611/5645ed8603a55d79042f2f7d8e7cc1d533cc30ac.jpeg?imageMogr2/auto-orient/strip|imageView2/1/w/1250/h/540";
    private final String contentImageSrc = "https://upload-images.jianshu.io/upload_images/15152899-e1a43b1cca2a4d58.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1000/format/webp";
    private SimpleRichEditor richEditor;
    private String mTitle = "";
    private String mContent = "";
    private long mContentLength = 0;
    private boolean isShowDialog = false;

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_discover_editor;
    }

    @Override
    protected boolean supportSwipeBack() {
        return true;
    }

    @Override
    public FragmentAnimator onCreateFragmentAnimator() {
        return new DefaultNoAnimator();
    }

    @Override
    protected void initView(View view, @Nullable Bundle savedInstanceState) {
        KeyboardRepairer.with(view)
                .setFitsSystemWindows(false)
                .init();

        richEditor = view.findViewById(R.id.rich_edit);
        LuBottomMenu luBottomMenu = view.findViewById(R.id.lu_bottom_menu);


        setToolbarTitle("0字");
        richEditor.setLuBottomMenu(luBottomMenu);
        richEditor.setOnTextLengthChangeListener(length -> post(() -> {
            setToolbarTitle(length + "字");
            mContentLength = length;
        }));
        richEditor.setOnOutHandleListener(new RichEditor.OnOutHandleListener() {
            @Override
            public void onClickHeaderImageListener() {
                // 封面图 这在子线程
                postDelay(() -> {
                    // 主线程
                    AToast.normal("点击了封面");
                    richEditor.edUpcover(headerImageSrc);
                }, 70);
            }

            @Override
            public void onGetTitleContent(final String title, final String content) {
                Log.e("RichEdit", "---获取标题：" + title);
                Log.e("RichEdit", "---获取内容：" + content);
                postDelay(() -> {
                    if (isShowDialog) {
                        ZAlertDialog.with(context)
                                .setTitle(title)
                                .setContent(content)
                                .setPositiveButton("保存", dialog -> {
                                    mTitle = title;
                                    mContent = content;
                                    AToast.success("保存成功");
                                })
                                .show();
                    } else {
                        mTitle = title;
                        mContent = content;
                        AToast.success("保存成功");
                    }
                }, 10);
            }
        });
        richEditor.setOnButtonClickListener(new SimpleRichEditor.OnButtonClickListener() {
            @Override
            public void addImage() {
                // 添加图片
                hideSoftInput();
                postDelay(() -> richEditor.edAddimgsrc(contentImageSrc), 70);
            }

            @Override
            public void addProduct() {
                // 添加产品
                hideSoftInput();
                AToast.normal("添加产品");
//                GoodsBean goodsBean = new GoodsBean();
//                goodsBean.setTitle("title");
//                goodsBean.setAlias("alias");
//                goodsBean.setImageSrc("https://ss0.baidu.com/6ONWsjip0QIZ8tyhnq/it/u=3509424840,3355088205&fm=179&app=42&f=JPEG?w=56&h=56");
//                // 添加产品(主线程)
//                richEditor.edAddProduct(123, new Gson().toJson(goodsBean));
            }
        });

    }

    @Override
    public void toolbarRightImageButton(@NonNull ImageButton imageButton) {
        imageButton.setOnClickListener(v -> EverywherePopup.create(context)
                .addItems("显示源码", "清空内容", "保存", "回显")
                .setOnItemClickListener((title, position) -> {
                    switch (position) {
                        case 0:
                            isShowDialog = true;
                            richEditor.edThishtml();
                            break;
                        case 1:
                            richEditor.edOutdata("", "");
                            setToolbarTitle("0字");
                            break;
                        case 2:
                            isShowDialog = false;
                            richEditor.edThishtml();
                            break;
                        case 3:
                            if (!TextUtils.isEmpty(mTitle) || TextUtils.isEmpty(mContent)) {
                                // 回显 标题和内容
                                richEditor.edOutdata(mTitle, mContent);
                                if (!TextUtils.isEmpty(mContent)) {
                                    setToolbarTitle(mContentLength + "字");
                                }
                            }
                            break;
                    }
                })
                .show(imageButton));
    }

    public class GoodsBean {

        private String image;
        private String mid;
        private String title;
        private String alias;
        private Integer entityId;
        private String imageSrc;

        public String getImage() {
            return image;
        }

        public void setImage(String image) {
            this.image = image;
        }

        public String getMid() {
            return mid;
        }

        public void setMid(String mid) {
            this.mid = mid;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public String getAlias() {
            return alias;
        }

        public void setAlias(String alias) {
            this.alias = alias;
        }

        public Integer getEntityId() {
            return entityId;
        }

        public void setEntityId(Integer entityId) {
            this.entityId = entityId;
        }

        public String getImageSrc() {
            return imageSrc;
        }

        public void setImageSrc(String imageSrc) {
            this.imageSrc = imageSrc;
        }
    }

}
