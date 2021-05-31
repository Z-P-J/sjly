package com.zpj.emoji;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.zpj.recyclerview.EasyRecyclerView;
import com.zpj.recyclerview.EasyViewHolder;
import com.zpj.recyclerview.IEasy;

import net.lucode.hackware.magicindicator.MagicIndicator;
import net.lucode.hackware.magicindicator.ViewPagerHelper;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.CommonNavigator;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.CommonNavigatorAdapter;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.IPagerIndicator;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.IPagerTitleView;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.indicators.WrapPagerIndicator;

import java.util.List;

public class EmotionLayout extends LinearLayout {

    private EditText mAttachEditText;

    private IEmotionSelectedListener mEmotionSelectedListener;
    private IEmotionExtClickListener mEmotionExtClickListener;

    public EmotionLayout(Context context) {
        this(context, null);
    }

    public EmotionLayout(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public EmotionLayout(final Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        LayoutInflater.from(context).inflate(R.layout.layout_emotion, this, true);

        ImageView ivDelete = findViewById(R.id.iv_delete);
        ivDelete.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mAttachEditText != null) {
                    mAttachEditText.dispatchKeyEvent(new KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_DEL));
                }
            }
        });

        final ViewPager viewPager = findViewById(R.id.view_pager);
        MagicIndicator mMagicIndicator = findViewById(R.id.magic_indicator);

        viewPager.setOffscreenPageLimit(EmojiManager.getCategoryCount());
        viewPager.setAdapter(new ViewPagerAdapter(new IEasy.OnItemClickListener<Emoji>() {
            @Override
            public void onClick(EasyViewHolder holder, View view, Emoji data) {
                Toast.makeText(holder.getContext(), "text=" + data.text, Toast.LENGTH_SHORT).show();
                if (mEmotionSelectedListener != null) {
                    mEmotionSelectedListener.onEmojiSelected(data.text);
                }
                if (mAttachEditText != null) {
                    Editable editable = mAttachEditText.getText();
                    int start = mAttachEditText.getSelectionStart();
                    int end = mAttachEditText.getSelectionEnd();
                    if (start < 0) {
                        start = 0;
                    }
                    if (end < 0) {
                        end = start;
                    }
//                    start = (start < 0 ? 0 : start);
//                    end = (start < 0 ? 0 : end);
                    editable.replace(start, end, data.text);

                    int editEnd = mAttachEditText.getSelectionEnd();
                    EmojiUtils.replaceEmoticons(holder.getContext(), editable,  -1,
                            mAttachEditText.getTextSize(), 0, editable.toString().length());
                    mAttachEditText.setSelection(editEnd);
                }
            }
        }));

        final CommonNavigator navigator = new CommonNavigator(context);
        navigator.setAdapter(new CommonNavigatorAdapter() {
            @Override
            public int getCount() {
                return EmojiManager.getCategoryCount();
            }

            @Override
            public IPagerTitleView getTitleView(Context context, final int index) {
                ImagePagerTitle pagerTitle = new ImagePagerTitle(context);
                Drawable drawable = EmojiManager.getDrawable(getContext(), EmojiManager.getCategoryList(index).get(0).text);
                pagerTitle.setImageDrawable(drawable);
                pagerTitle.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        viewPager.setCurrentItem(index, true);
                    }
                });
                return pagerTitle;
            }

            @Override
            public IPagerIndicator getIndicator(Context context) {
                WrapPagerIndicator indicator = new WrapPagerIndicator(context);
                indicator.setFillColor(Color.parseColor("#40cccccc"));
                indicator.setHorizontalPadding(0);
                indicator.setVerticalPadding(0);
//                indicator.setRoundRadius(4);
                return indicator;
            }
        });
        mMagicIndicator.setNavigator(navigator);
        ViewPagerHelper.bind(mMagicIndicator, viewPager);

    }

    public void attachEditText(EditText editText) {
        mAttachEditText = editText;
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

    private static class ViewPagerAdapter extends PagerAdapter {

        private final IEasy.OnItemClickListener<Emoji> mListener;

        private ViewPagerAdapter(IEasy.OnItemClickListener<Emoji> listener) {
            mListener = listener;
        }

        @Override
        public int getCount() {
            return EmojiManager.getCategoryCount();
        }

        @Override
        public boolean isViewFromObject(@NonNull View view, @NonNull Object o) {
            return view == o;
        }

        @NonNull
        @Override
        public Object instantiateItem(@NonNull ViewGroup container, int position) {
            Context context = container.getContext();
            RecyclerView recyclerView = new RecyclerView(context);
            new EasyRecyclerView<Emoji>(recyclerView)
                    .setData(EmojiManager.getCategoryList(position))
                    .setItemRes(R.layout.item_emoji)
                    .setLayoutManager(new GridLayoutManager(context, 7))
                    .onBindViewHolder(new IEasy.OnBindViewHolderListener<Emoji>() {
                        @Override
                        public void onBindViewHolder(EasyViewHolder holder, List<Emoji> list, int position, List<Object> payloads) {
                            Emoji emoji = list.get(position);
                            String text = emoji.text;
                            holder.setText(R.id.tv_emoji, text);
                            holder.setImageDrawable(R.id.iv_emoji, EmojiManager.getDrawable(holder.getContext(), text));
                            holder.setVisible(R.id.tv_emoji, false);
                        }
                    })
                    .onItemClick(mListener)
                    .onItemLongClick(new IEasy.OnItemLongClickListener<Emoji>() {
                        @Override
                        public boolean onLongClick(EasyViewHolder holder, View view, Emoji data) {
                            Toast.makeText(holder.getContext(), data.text, Toast.LENGTH_SHORT).show();
                            return true;
                        }
                    })
                    .build();
            container.addView(recyclerView);
            return recyclerView;
        }

        @Override
        public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
            container.removeView((View) object);
        }
    }


}
