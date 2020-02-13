package com.zpj.shouji.market.ui.fragment;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentManager;
import android.text.InputType;
import android.text.TextUtils;
import android.util.Base64;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.felix.atoast.library.AToast;
import com.joker.richeditor.GlideImageLoader;
import com.joker.richeditor.fragment.EditorMenuFragment;
import com.joker.richeditor.interfaces.OnActionPerformListener;
import com.joker.richeditor.keyboard.KeyboardHeightObserver;
import com.joker.richeditor.keyboard.KeyboardHeightProvider;
import com.joker.richeditor.keyboard.KeyboardUtils;
import com.joker.richeditor.util.FileIOUtil;
import com.joker.richeditor.widget.ActionImageView;
import com.joker.richeditor.widget.ActionType;
import com.joker.richeditor.widget.RichEditorAction;
import com.joker.richeditor.widget.RichEditorCallback;
import com.lzy.imagepicker.ImagePicker;
import com.lzy.imagepicker.bean.ImageItem;
import com.lzy.imagepicker.ui.ImageGridActivity;
import com.lzy.imagepicker.view.CropImageView;
import com.zpj.dialog.ZAlertDialog;
import com.zpj.shouji.market.R;
import com.zpj.shouji.market.ui.fragment.base.BaseFragment;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@SuppressLint("SetJavaScriptEnabled")
public class RichEditorFragment extends BaseFragment
        implements KeyboardHeightObserver, View.OnClickListener {
    private FrameLayout flAction;
    private LinearLayout llActionBarContainer;

    /**
     * The keyboard height provider
     */
    private KeyboardHeightProvider keyboardHeightProvider;
    private boolean isKeyboardShowing;
    private String htmlContent = "<p>Hello World</p>";

    private RichEditorAction mRichEditorAction;
    private RichEditorCallback mRichEditorCallback;

    private EditorMenuFragment mEditorMenuFragment;

    private List<ActionType> mActionTypeList =
            Arrays.asList(ActionType.BOLD, ActionType.ITALIC, ActionType.UNDERLINE,
                    ActionType.STRIKETHROUGH, ActionType.SUBSCRIPT, ActionType.SUPERSCRIPT,
                    ActionType.NORMAL, ActionType.H1, ActionType.H2, ActionType.H3, ActionType.H4,
                    ActionType.H5, ActionType.H6, ActionType.INDENT, ActionType.OUTDENT,
                    ActionType.JUSTIFY_LEFT, ActionType.JUSTIFY_CENTER, ActionType.JUSTIFY_RIGHT,
                    ActionType.JUSTIFY_FULL, ActionType.ORDERED, ActionType.UNORDERED, ActionType.LINE,
                    ActionType.BLOCK_CODE, ActionType.BLOCK_QUOTE, ActionType.CODE_VIEW);

    private List<Integer> mActionTypeIconList =
            Arrays.asList(com.joker.richeditor.R.drawable.ic_format_bold, com.joker.richeditor.R.drawable.ic_format_italic,
                    com.joker.richeditor.R.drawable.ic_format_underlined, com.joker.richeditor.R.drawable.ic_format_strikethrough,
                    com.joker.richeditor.R.drawable.ic_format_subscript, com.joker.richeditor.R.drawable.ic_format_superscript,
                    com.joker.richeditor.R.drawable.ic_format_para, com.joker.richeditor.R.drawable.ic_format_h1, com.joker.richeditor.R.drawable.ic_format_h2,
                    com.joker.richeditor.R.drawable.ic_format_h3, com.joker.richeditor.R.drawable.ic_format_h4, com.joker.richeditor.R.drawable.ic_format_h5,
                    com.joker.richeditor.R.drawable.ic_format_h6, com.joker.richeditor.R.drawable.ic_format_indent_decrease,
                    com.joker.richeditor.R.drawable.ic_format_indent_increase, com.joker.richeditor.R.drawable.ic_format_align_left,
                    com.joker.richeditor.R.drawable.ic_format_align_center, com.joker.richeditor.R.drawable.ic_format_align_right,
                    com.joker.richeditor.R.drawable.ic_format_align_justify, com.joker.richeditor.R.drawable.ic_format_list_numbered,
                    com.joker.richeditor.R.drawable.ic_format_list_bulleted, com.joker.richeditor.R.drawable.ic_line, com.joker.richeditor.R.drawable.ic_code_block,
                    com.joker.richeditor.R.drawable.ic_format_quote, com.joker.richeditor.R.drawable.ic_code_review);

    private static final int REQUEST_CODE_CHOOSE = 0;

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_rich_editor;
    }

    @Override
    protected boolean supportSwipeBack() {
        return true;
    }

    @Override
    protected void initView(View view, @Nullable Bundle savedInstanceState) {
        initImageLoader();
        initView(view);

        int width = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 40,
                getResources().getDisplayMetrics());
        int padding = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 9,
                getResources().getDisplayMetrics());
        for (int i = 0, size = mActionTypeList.size(); i < size; i++) {
            final ActionImageView actionImageView = new ActionImageView(getContext());
            actionImageView.setLayoutParams(new LinearLayout.LayoutParams(width, width));
            actionImageView.setPadding(padding, padding, padding, padding);
            actionImageView.setActionType(mActionTypeList.get(i));
            actionImageView.setTag(mActionTypeList.get(i));
            actionImageView.setActivatedColor(R.color.colorAccent);
            actionImageView.setDeactivatedColor(R.color.tintColor);
            actionImageView.setRichEditorAction(mRichEditorAction);
            actionImageView.setBackgroundResource(R.drawable.btn_colored_material);
            actionImageView.setImageResource(mActionTypeIconList.get(i));
            actionImageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    actionImageView.command();
                }
            });
            llActionBarContainer.addView(actionImageView);
        }

        mEditorMenuFragment = new EditorMenuFragment();
        mEditorMenuFragment.setActionClickListener(new MOnActionPerformListener(mRichEditorAction));
        FragmentManager fm = getChildFragmentManager();
        fm.beginTransaction()
                .add(R.id.fl_action, mEditorMenuFragment, EditorMenuFragment.class.getName())
                .commit();
    }

    /**
     * ImageLoader for insert Image
     */
    private void initImageLoader() {
        ImagePicker imagePicker = ImagePicker.getInstance();
        imagePicker.setImageLoader(new GlideImageLoader());
        imagePicker.setShowCamera(true);
        imagePicker.setCrop(false);
        imagePicker.setMultiMode(false);
        imagePicker.setSaveRectangle(true);
        imagePicker.setStyle(CropImageView.Style.RECTANGLE);
        imagePicker.setFocusWidth(800);
        imagePicker.setFocusHeight(800);
        imagePicker.setOutPutX(256);
        imagePicker.setOutPutY(256);
    }

    private void initView(View view) {
        WebView mWebView = view.findViewById(com.joker.richeditor.R.id.wv_container);
        flAction = view.findViewById(com.joker.richeditor.R.id.fl_action);
        llActionBarContainer = view.findViewById(com.joker.richeditor.R.id.ll_action_bar_container);

        mWebView.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
            }

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.loadUrl(url);
                return true;
            }
        });

        mWebView.setWebChromeClient(new CustomWebChromeClient());
        mWebView.getSettings().setJavaScriptEnabled(true);
        mWebView.getSettings().setDomStorageEnabled(true);
        mRichEditorCallback = new MRichEditorCallback();
        mWebView.addJavascriptInterface(mRichEditorCallback, "MRichEditor");
        mWebView.loadUrl("file:///android_asset/richEditor.html");
        mRichEditorAction = new RichEditorAction(mWebView);

        keyboardHeightProvider = new KeyboardHeightProvider(_mActivity);
        post(() -> keyboardHeightProvider.start());
        int[] viewIds = new int[]{com.joker.richeditor.R.id.iv_action, com.joker.richeditor.R.id.iv_get_html, com.joker.richeditor.R.id.iv_action_undo, com.joker.richeditor.R.id.iv_action_redo,
                com.joker.richeditor.R.id.iv_action_txt_color, com.joker.richeditor.R.id.iv_action_txt_bg_color, com.joker.richeditor.R.id.iv_action_line_height, com.joker.richeditor.R.id.iv_action_insert_image,
                com.joker.richeditor.R.id.iv_action_insert_link, com.joker.richeditor.R.id.iv_action_table,};
        for (int viewId : viewIds) {
            view.findViewById(viewId).setOnClickListener(this);
        }
        mWebView.setClickable(true);
        mWebView.setFocusable(true);
        mWebView.setFocusableInTouchMode(true);
        postDelay(mWebView::performClick,500);
    }

    private class CustomWebChromeClient extends WebChromeClient {
        @Override
        public void onProgressChanged(WebView view, int newProgress) {
            super.onProgressChanged(view, newProgress);
            if (newProgress == 100) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                    view.evaluateJavascript("javascript:setPlaceholder('" + "占位符号" + "')", null);
                } else {
                    view.loadUrl("javascript:setPlaceholder('" + "占位符号" + "')");
                }
                if (!TextUtils.isEmpty(htmlContent)) {
                    mRichEditorAction.insertHtml(htmlContent);
                }
                showSoftInput(_mActivity.getCurrentFocus());
            }
        }

        @Override
        public void onReceivedTitle(WebView view, String title) {
            super.onReceivedTitle(view, title);
        }
    }

    @Override
    public void onClick(View v) {
        int i = v.getId();
        if (i == com.joker.richeditor.R.id.iv_action) {
            onClickAction();
        } else if (i == com.joker.richeditor.R.id.iv_get_html) {
            mRichEditorAction.refreshHtml(mRichEditorCallback, onGetHtmlListener);
        } else if (i == com.joker.richeditor.R.id.iv_action_undo) {
            mRichEditorAction.undo();
        } else if (i == com.joker.richeditor.R.id.iv_action_redo) {
            mRichEditorAction.redo();
        } else if (i == com.joker.richeditor.R.id.iv_action_txt_color) {
            mRichEditorAction.foreColor("blue");
        } else if (i == com.joker.richeditor.R.id.iv_action_txt_bg_color) {
            mRichEditorAction.backColor("red");
        } else if (i == com.joker.richeditor.R.id.iv_action_line_height) {
            mRichEditorAction.lineHeight(20);
        } else if (i == com.joker.richeditor.R.id.iv_action_insert_image) {
            onClickInsertImage();
        } else if (i == com.joker.richeditor.R.id.iv_action_insert_link) {
            onClickInsertLink();
        } else if (i == com.joker.richeditor.R.id.iv_action_table) {
            onClickInsertTable();
        }
    }

    private void onClickAction() {
        if (flAction.getVisibility() == View.VISIBLE) {
            flAction.setVisibility(View.GONE);
            if (!isKeyboardShowing) {
                showSoftInput(_mActivity.getCurrentFocus());
            }
        } else {
            if (isKeyboardShowing) {
                hideSoftInput();
            }
            flAction.setVisibility(View.VISIBLE);
        }
    }

    private void onClickInsertImage() {
        Intent intent = new Intent(getContext(), ImageGridActivity.class);
        startActivityForResult(intent, REQUEST_CODE_CHOOSE);
    }

    private void onClickInsertLink() {
        hideSoftInput();

        ZAlertDialog.with(context)
                .setTitle("添加链接")
                .setContentView(R.layout.layout_edit)
                .setOnViewCreateListener((dialog, view) -> {
                    TextView tvTitle1 = dialog.getView(R.id.tv_title_1);
                    TextView tvTitle2 = dialog.getView(R.id.tv_title_2);
                    tvTitle1.setText("网址");
                    tvTitle2.setText("显示文字");
                })
                .setPositiveButton(dialog -> {
                    EditText etAddress = dialog.getView(R.id.et_1);
                    EditText etDisplayText = dialog.getView(R.id.et_2);
                    mRichEditorAction.createLink(etAddress.getText().toString(), etDisplayText.getText().toString());
                    dialog.dismiss();
                })
                .show();

//        EditHyperlinkFragment fragment = new EditHyperlinkFragment();
//        fragment.setOnHyperlinkListener(new EditHyperlinkFragment.OnHyperlinkListener() {
//            @Override
//            public void onHyperlinkOK(String address, String text) {
//                mRichEditorAction.createLink(text, address);
//            }
//        });
//        getChildFragmentManager().beginTransaction()
//                .add(com.joker.richeditor.R.id.fl_container, fragment, EditHyperlinkFragment.class.getName())
//                .commit();
    }

    private void onClickInsertTable() {
        KeyboardUtils.hideSoftInput(_mActivity);

        ZAlertDialog.with(context)
                .setTitle("添加表格")
                .setContentView(R.layout.layout_edit)
                .setOnViewCreateListener((dialog, view) -> {
                    TextView tvTitle1 = dialog.getView(R.id.tv_title_1);
                    TextView tvTitle2 = dialog.getView(R.id.tv_title_2);
                    tvTitle1.setText("行");
                    tvTitle2.setText("列");
                    EditText et1 = dialog.getView(R.id.et_1);
                    EditText et2 = dialog.getView(R.id.et_2);
                    et1.setInputType(InputType.TYPE_NUMBER_FLAG_DECIMAL);
                    et2.setInputType(InputType.TYPE_NUMBER_FLAG_DECIMAL);
                })
                .setPositiveButton(dialog -> {
                    String rows = ((EditText)(dialog.getView(R.id.et_1))).getText().toString();
                    String cols = ((EditText)(dialog.getView(R.id.et_2))).getText().toString();
                    if (!TextUtils.isDigitsOnly(rows) && !TextUtils.isDigitsOnly(cols)) {
                        AToast.warning("输入有误！");
                        return;
                    }
                    mRichEditorAction.insertTable(Integer.valueOf(rows), Integer.valueOf(cols));
                })
                .show();

//        EditTableFragment fragment = new EditTableFragment();
//        fragment.setOnTableListener(new EditTableFragment.OnTableListener() {
//            @Override
//            public void onTableOK(int rows, int cols) {
//                mRichEditorAction.insertTable(rows, cols);
//            }
//        });
//        getChildFragmentManager().beginTransaction()
//                .add(com.joker.richeditor.R.id.fl_container, fragment, EditTableFragment.class.getName())
//                .commit();
    }

    private RichEditorCallback.OnGetHtmlListener onGetHtmlListener =
            new RichEditorCallback.OnGetHtmlListener() {
                @Override
                public void getHtml(String html) {
                    if (TextUtils.isEmpty(html)) {
                        Toast.makeText(getContext(), "Empty Html String", Toast.LENGTH_SHORT)
                                .show();
                        return;
                    }
                    Toast.makeText(getContext(), html, Toast.LENGTH_SHORT).show();
                }
            };

    @Override
    public void onResume() {
        super.onResume();
        keyboardHeightProvider.setKeyboardHeightObserver(this);
    }

    @Override
    public void onPause() {
        super.onPause();
        keyboardHeightProvider.setKeyboardHeightObserver(null);
        if (flAction.getVisibility() == View.INVISIBLE) {
            flAction.setVisibility(View.GONE);
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        hideSoftInput();
    }

    @Override
    public void onDestroy() {
        keyboardHeightProvider.close();
        super.onDestroy();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == ImagePicker.RESULT_CODE_ITEMS && data != null && requestCode == REQUEST_CODE_CHOOSE) {
            ArrayList<ImageItem> images = (ArrayList<ImageItem>) data.getSerializableExtra(ImagePicker.EXTRA_RESULT_ITEMS);
            if (images != null && !images.isEmpty()) {

                //1.Insert the Base64 String (Base64.NO_WRAP)
                ImageItem imageItem = images.get(0);
                mRichEditorAction.insertImageData(imageItem.name, encodeFileToBase64Binary(imageItem.path));

                //2.Insert the ImageUrl
                //mRichEditorAction.insertImageUrl(
                //"https://avatars0.githubusercontent.com/u/5581118?v=4&u=b7ea903e397678b3675e2a15b0b6d0944f6f129e&s=400");
            }
        }
    }

    private static String encodeFileToBase64Binary(String filePath) {
        byte[] bytes = FileIOUtil.readFile2BytesByStream(filePath);
        byte[] encoded = Base64.encode(bytes, Base64.NO_WRAP);
        return new String(encoded);
    }

    @Override
    public void onKeyboardHeightChanged(int height, int orientation) {
        isKeyboardShowing = height > 0;
        if (height != 0) {
            flAction.setVisibility(View.INVISIBLE);
            ViewGroup.LayoutParams params = flAction.getLayoutParams();
            params.height = height;
            flAction.setLayoutParams(params);
        } else if (flAction.getVisibility() != View.VISIBLE) {
            flAction.setVisibility(View.GONE);
        }
    }

    class MRichEditorCallback extends RichEditorCallback {

        @Override
        public void notifyFontStyleChange(ActionType type, final String value) {
            ActionImageView actionImageView = llActionBarContainer.findViewWithTag(type);
            if (actionImageView != null) {
                actionImageView.notifyFontStyleChange(type, value);
            }

            if (mEditorMenuFragment != null) {
                mEditorMenuFragment.updateActionStates(type, value);
            }
        }
    }

    public class MOnActionPerformListener implements OnActionPerformListener {
        private RichEditorAction mRichEditorAction;

        public MOnActionPerformListener(RichEditorAction mRichEditorAction) {
            this.mRichEditorAction = mRichEditorAction;
        }

        @Override
        public void onActionPerform(ActionType type, Object... values) {
            if (mRichEditorAction == null) {
                return;
            }

            String value = null;
            if (values != null && values.length > 0) {
                value = (String) values[0];
            }

            switch (type) {
                case SIZE:
                    mRichEditorAction.fontSize(Double.valueOf(value));
                    break;
                case LINE_HEIGHT:
                    mRichEditorAction.lineHeight(Double.valueOf(value));
                    break;
                case FORE_COLOR:
                    mRichEditorAction.foreColor(value);
                    break;
                case BACK_COLOR:
                    mRichEditorAction.backColor(value);
                    break;
                case FAMILY:
                    mRichEditorAction.fontName(value);
                    break;
                case IMAGE:
                    onClickInsertImage();
                    break;
                case LINK:
                    onClickInsertLink();
                    break;
                case TABLE:
                    onClickInsertTable();
                    break;
                case BOLD:
                case ITALIC:
                case UNDERLINE:
                case SUBSCRIPT:
                case SUPERSCRIPT:
                case STRIKETHROUGH:
                case JUSTIFY_LEFT:
                case JUSTIFY_CENTER:
                case JUSTIFY_RIGHT:
                case JUSTIFY_FULL:
                case CODE_VIEW:
                case ORDERED:
                case UNORDERED:
                case INDENT:
                case OUTDENT:
                case BLOCK_QUOTE:
                case BLOCK_CODE:
                case NORMAL:
                case H1:
                case H2:
                case H3:
                case H4:
                case H5:
                case H6:
                case LINE:
                    ActionImageView actionImageView = llActionBarContainer.findViewWithTag(type);
                    if (actionImageView != null) {
                        actionImageView.performClick();
                    }
                    break;
                default:
                    break;
            }
        }
    }
}
