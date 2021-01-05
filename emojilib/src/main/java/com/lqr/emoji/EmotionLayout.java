package com.lqr.emoji;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.StateListDrawable;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.util.Log;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import java.util.List;

/**
 * CSDN_LQR
 * 表情布局
 */
public class EmotionLayout extends LinearLayout implements View.OnClickListener {

    private static final String TAG = "EmotionLayout";

    public static final int EMOJI_COLUMNS = 7;
    public static final int EMOJI_ROWS = 4;
    public static final int EMOJI_PER_PAGE = EMOJI_COLUMNS * EMOJI_ROWS - 1;//最后一个是删除键

    public static final int STICKER_COLUMNS = 4;
    public static final int STICKER_ROWS = 2;
    public static final int STICKER_PER_PAGE = STICKER_COLUMNS * STICKER_ROWS;

    private int mMeasuredWidth;
    private int mMeasuredHeight;

    private int mTabPosi = 0;
    private Context mContext;
    private ViewPager mVpEmotioin;
    private LinearLayout mLlPageNumber;
    private LinearLayout mLlTabContainer;
    private RelativeLayout mRlEmotionAdd;

    private int mTabCount;
    private SparseArray<View> mTabViewArray = new SparseArray<>();
    private EmotionTab mSettingTab;
    private IEmotionSelectedListener mEmotionSelectedListener;
    private IEmotionExtClickListener mEmotionExtClickListener;
    private boolean mEmotionAddVisiable = false;
    private boolean mEmotionSettingVisiable = false;

    public EmotionLayout(Context context) {
        this(context, null);
    }

    public EmotionLayout(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public EmotionLayout(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mContext = context;
    }

    public void setEmotionSelectedListener(IEmotionSelectedListener emotionSelectedListener) {
        if (emotionSelectedListener != null) {
            this.mEmotionSelectedListener = emotionSelectedListener;
        } else {
            Log.i("CSDN_LQR", "IEmotionSelectedListener is null");
        }
    }


    public void setEmotionExtClickListener(IEmotionExtClickListener emotionExtClickListener) {
        if (emotionExtClickListener != null) {
            this.mEmotionExtClickListener = emotionExtClickListener;
        } else {
            Log.i("CSDN_LQR", "IEmotionSettingTabClickListener is null");
        }
    }


    /**
     * 设置表情添加按钮的显隐
     *
     * @param visiable
     */
    public void setEmotionAddVisiable(boolean visiable) {
        mEmotionAddVisiable = visiable;
        if (mRlEmotionAdd != null) {
            mRlEmotionAdd.setVisibility(mEmotionAddVisiable ? View.VISIBLE : View.GONE);
        }
    }

    /**
     * 设置表情设置按钮的显隐
     *
     * @param visiable
     */
    public void setEmotionSettingVisiable(boolean visiable) {
        mEmotionSettingVisiable = visiable;
        if (mSettingTab != null) {
            mSettingTab.setVisibility(mEmotionSettingVisiable ? View.VISIBLE : View.GONE);
        }
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        init();
        initListener();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        mMeasuredWidth = measureWidth(widthMeasureSpec);
        mMeasuredHeight = measureHeight(heightMeasureSpec);
        setMeasuredDimension(mMeasuredWidth, mMeasuredHeight);
    }


    private int measureWidth(int measureSpec) {
        int result = 0;
        int specMode = MeasureSpec.getMode(measureSpec);
        int specSize = MeasureSpec.getSize(measureSpec);

        if (specMode == MeasureSpec.EXACTLY) {
            result = specSize;
        } else {
            result = LQREmotionKit.dip2px(200);
            if (specMode == MeasureSpec.AT_MOST) {
                result = Math.min(result, specSize);
            }
        }
        return result;
    }

    private int measureHeight(int measureSpec) {
        int result = 0;
        int specMode = MeasureSpec.getMode(measureSpec);
        int specSize = MeasureSpec.getSize(measureSpec);

        if (specMode == MeasureSpec.EXACTLY) {
            result = specSize;
        } else {
            result = LQREmotionKit.dip2px(200);
            if (specMode == MeasureSpec.AT_MOST) {
                result = Math.min(result, specSize);
            }
        }
        return result;
    }

    private void init() {
        mTabViewArray.clear();
        removeAllViews();
        LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.emotion_layout, this);
//        View.inflate(mContext, R.layout.emotion_layout, this);

        mVpEmotioin = findViewById(R.id.vpEmotioin);
        mLlPageNumber = findViewById(R.id.llPageNumber);
        mLlTabContainer = findViewById(R.id.llTabContainer);
        mRlEmotionAdd = findViewById(R.id.rlEmotionAdd);
        setEmotionAddVisiable(mEmotionAddVisiable);

        initTabs();

    }

    private void initTabs() {
        //默认添加一个表情tab
//        EmotionTab emojiTab = new EmotionTab(mContext, R.drawable.ic_tab_emoji);
//        mLlTabContainer.addView(emojiTab);
//        mTabViewArray.put(0, emojiTab);

        for (int i = 0; i < EmojiManager.getCategoryCount(); i++) {
            EmotionTab emojiTab = new EmotionTab(mContext, EmojiManager.getDrawable(getContext(), EmojiManager.getCategoryList(i).get(0).text));
            mLlTabContainer.addView(emojiTab);
            mTabViewArray.put(i, emojiTab);
        }

        //添加所有的贴图tab
        List<StickerCategory> stickerCategories = StickerManager.getInstance().getStickerCategories();
        Log.d(TAG, "initTabs stickerCategories.size=" + stickerCategories.size());
        for (int i = 0; i < stickerCategories.size(); i++) {
            StickerCategory category = stickerCategories.get(i);
            EmotionTab tab = new EmotionTab(mContext, category.getCoverImgPath());
            mLlTabContainer.addView(tab);
            mTabViewArray.put(i + EmojiManager.getCategoryCount(), tab);
        }

        //最后添加一个表情设置Tab
        mSettingTab = new EmotionTab(mContext, getResources().getDrawable(R.drawable.ic_emotion_setting));
        StateListDrawable drawable = new StateListDrawable();
//        Drawable unSelected = mContext.getResources().getDrawable(R.color.white);
//        drawable.addState(new int[]{-android.R.attr.state_pressed}, unSelected);
//        Drawable selected = mContext.getResources().getDrawable(R.color.gray);
//        drawable.addState(new int[]{android.R.attr.state_pressed}, selected);
        Drawable unSelected = new ColorDrawable(Color.TRANSPARENT);
        drawable.addState(new int[]{-android.R.attr.state_pressed}, unSelected);
        Drawable selected = new ColorDrawable(Color.parseColor("#eeeeee"));
        drawable.addState(new int[]{android.R.attr.state_pressed}, selected);
        mSettingTab.setBackground(drawable);
        mLlTabContainer.addView(mSettingTab);
        mTabViewArray.put(mTabViewArray.size(), mSettingTab);
        setEmotionSettingVisiable(mEmotionSettingVisiable);

        selectTab(0);
    }

    private void initListener() {
        if (mLlTabContainer != null) {
            mTabCount = mLlTabContainer.getChildCount() - 1;//不包含最后的设置按钮
            for (int position = 0; position < mTabCount; position++) {
                View tab = mLlTabContainer.getChildAt(position);
                tab.setTag(position);
                tab.setOnClickListener(this);
            }
        }

        mVpEmotioin.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                setCurPageCommon(position);
            }

            @Override
            public void onPageSelected(int position) {

            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        mRlEmotionAdd.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mEmotionExtClickListener != null) {
                    mEmotionExtClickListener.onEmotionAddClick(v);
                }
            }
        });

        mSettingTab.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mEmotionExtClickListener != null) {
                    mEmotionExtClickListener.onEmotionSettingClick(v);
                }
            }
        });
    }

    private void setCurPageCommon(int position) {
        if (mTabPosi < EmojiManager.getCategoryCount())
            setCurPage(position, (int) Math.ceil(EmojiManager.getCategoryList(mTabPosi).size() / (float) EmotionLayout.EMOJI_PER_PAGE));
        else {
            StickerCategory category = StickerManager.getInstance().getStickerCategories().get(mTabPosi - EmojiManager.getCategoryCount());
            setCurPage(position, (int) Math.ceil(category.getStickers().size() / (float) EmotionLayout.STICKER_PER_PAGE));
        }
    }


    @Override
    public void onClick(View v) {
        mTabPosi = (int) v.getTag();
        selectTab(mTabPosi);
    }

    private void selectTab(int tabPosi) {
        if (tabPosi == mTabViewArray.size() - 1)
            return;

        Log.d(TAG, "selectTab tabPosi=" + tabPosi + " mTabCount=" + mTabCount + " mTabViewArray.size=" + mTabViewArray.size());
        for (int i = 0; i < mTabCount; i++) {
            View tab = mTabViewArray.get(i);
            tab.setBackgroundColor(Color.TRANSPARENT);
//            tab.setBackgroundResource(R.drawable.shape_tab_normal);
        }
        GradientDrawable drawable = new GradientDrawable();
        drawable.setColor(Color.parseColor("#40cccccc"));
        drawable.setCornerRadius(LQREmotionKit.dip2px(8));
        mTabViewArray.get(tabPosi).setBackground(drawable);
//        mTabViewArray.get(tabPosi).setBackgroundColor(Color.parseColor("#40cccccc"));
//        mTabViewArray.get(tabPosi).setBackgroundResource(R.drawable.shape_tab_press);

        //显示表情内容
        fillVpEmotioin(tabPosi);
    }

    private void fillVpEmotioin(int tabPosi) {
        EmotionViewPagerAdapter adapter = new EmotionViewPagerAdapter(getMeasuredWidth(), getMeasuredHeight(), tabPosi, mEmotionSelectedListener);
        mVpEmotioin.setAdapter(adapter);
        mLlPageNumber.removeAllViews();
        setCurPageCommon(tabPosi);
        if (tabPosi < EmojiManager.getCategoryCount()) {
            adapter.attachEditText(mMessageEditText);
        }
    }

    private void setCurPage(int page, int pageCount) {
        int hasCount = mLlPageNumber.getChildCount();
        int forMax = Math.max(hasCount, pageCount);

        ImageView ivCur = null;
        for (int i = 0; i < forMax; i++) {
            if (pageCount <= hasCount) {
                if (i >= pageCount) {
                    mLlPageNumber.getChildAt(i).setVisibility(View.GONE);
                    continue;
                } else {
                    ivCur = (ImageView) mLlPageNumber.getChildAt(i);
                }
            } else {
                if (i < hasCount) {
                    ivCur = (ImageView) mLlPageNumber.getChildAt(i);
                } else {
                    ivCur = new ImageView(mContext);
                    ivCur.setBackgroundResource(R.drawable.selector_view_pager_indicator);
                    LayoutParams params = new LayoutParams(LQREmotionKit.dip2px(8), LQREmotionKit.dip2px(8));
                    ivCur.setLayoutParams(params);
                    params.leftMargin = LQREmotionKit.dip2px(3);
                    params.rightMargin = LQREmotionKit.dip2px(3);
                    mLlPageNumber.addView(ivCur);
                }
            }

            ivCur.setId(i);
            ivCur.setSelected(i == page);
            ivCur.setVisibility(View.VISIBLE);
        }
    }

    EditText mMessageEditText;

    public void attachEditText(EditText messageEditText) {
        mMessageEditText = messageEditText;
        if (mVpEmotioin != null && mVpEmotioin.getAdapter() instanceof EmotionViewPagerAdapter) {
            ((EmotionViewPagerAdapter) mVpEmotioin.getAdapter()).attachEditText(mMessageEditText);
        }
    }

}
