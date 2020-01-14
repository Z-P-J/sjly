package com.zpj.zdialog.view;//package com.zpj.zdialog.view;
//
//import android.content.Context;
//import android.content.res.TypedArray;
//import android.graphics.Color;
//import android.support.annotation.Nullable;
//import android.util.AttributeSet;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.widget.FrameLayout;
//import android.widget.TextView;
//
//import com.zpj.zdialog.R;
//
///**
// * @author Z-P-J
// * @date 2019/5/17 16:14
// */
//public class CheckLayout extends FrameLayout {
//
//    public interface OnCheckedChangeListener{
//        void onCheckedChanged(CheckLayout checkLayout, boolean isChecked);
//    }
//
//    private TextView textView;
//    private SmoothCheckBox checkBox;
//
//    private boolean checked;
//    private String content;
//    private int textColor;
//    private int textSize;
//
//    private OnCheckedChangeListener onCheckedChangeListener;
//
//    public CheckLayout(Context context) {
//        super(context);
//        init(context, null);
//    }
//
//    public CheckLayout(Context context, @Nullable AttributeSet attrs) {
//        super(context, attrs);
//        init(context, attrs);
//    }
//
//    public CheckLayout(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
//        super(context, attrs, defStyleAttr);
//        init(context, attrs);
//    }
//
//    private void init(Context context, AttributeSet attrs) {
//        View view = LayoutInflater.from(context).inflate(R.layout.layout_check, this, true);
//        textView = view.getView(R.id.text_view);
//        checkBox = view.getView(R.id.check_box);
//        if (attrs != null) {
//            final TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.CheckLayout);
//            checked = typedArray.getBoolean(R.styleable.CheckLayout_box_checked, false);
//            content = typedArray.getString(R.styleable.CheckLayout_check_content);
//            textColor = typedArray.getColor(R.styleable.CheckLayout_content_text_color, Color.parseColor("#222222"));
//            textSize = typedArray.getInt(R.styleable.CheckLayout_content_text_size, 14);
//            typedArray.recycle();
//            checkBox.setChecked(checked, true);
//            textView.setText(content);
//            textView.setTextColor(textColor);
//            textView.setTextSize(textSize);
//        }
//        checkBox.setOnClickListener(new OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                performClick();
//            }
//        });
//        setOnClickListener(new OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                checkBox.setChecked(!checked, true);
//                checked = !checked;
//                if (onCheckedChangeListener != null) {
//                    onCheckedChangeListener.onCheckedChanged(CheckLayout.this, checked);
//                }
//            }
//        });
//    }
//
//    public void setContent(String content) {
//        this.content = content;
//        textView.setText(content);
//    }
//
//    public void setChecked(boolean checked) {
//        this.checked = checked;
//        checkBox.setChecked(checked, true);
//    }
//
//    public void setOnCheckedChangeListener(OnCheckedChangeListener onCheckedChangeListener) {
//        this.onCheckedChangeListener = onCheckedChangeListener;
//    }
//
//    public void setTextSize(int textSize) {
//        this.textSize = textSize;
//        textView.setTextSize(textSize);
//    }
//
//    public void setTextColor(int textColor) {
//        this.textColor = textColor;
//        textView.setTextColor(textColor);
//    }
//
//    public boolean isChecked() {
//        return checked;
//    }
//}
