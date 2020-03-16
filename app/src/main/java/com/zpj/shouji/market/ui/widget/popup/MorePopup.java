package com.zpj.shouji.market.ui.widget.popup;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.animation.TypeEvaluator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.support.design.widget.FloatingActionButton;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.felix.atoast.library.AToast;
import com.lxj.xpopup.core.PopupInfo;
import com.lxj.xpopup.enums.PopupAnimation;
import com.lxj.xpopup.impl.FullScreenPopupView;
import com.zpj.shouji.market.R;

import io.reactivex.Observable;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import per.goweii.burred.Blurred;

public class MorePopup extends FullScreenPopupView implements View.OnClickListener {

    private final int[] menuIconItems = {R.drawable.pic1, R.drawable.pic2, R.drawable.pic3, R.drawable.pic4};
    private final String[] menuTextItems = {"动态", "应用集", "乐图", "私聊"};

    private final Context context;
    private FloatingActionButton floatingActionButton;
    private ViewGroup anchorView;

    private OnItemClickListener listener;

    public interface OnItemClickListener {
        void onDiscoverItemClick();

        void onCollectionItemClick();

        void onWallpaperItemClick();

        void onChatWithFriendItemClick();
    }

    public static MorePopup with(ViewGroup anchorView) {
        return new MorePopup(anchorView);
    }

    private MorePopup(Context context) {
        super(context);
        this.context = context;
    }

    private MorePopup(ViewGroup anchorView) {
        this(anchorView.getContext());
        this.anchorView = anchorView;
        popupInfo = new PopupInfo();
        popupInfo.popupAnimation = PopupAnimation.NoAnimation;
    }

    @Override
    protected int getImplLayoutId() {
        return R.layout.layout_add_view;
    }

    @Override
    protected void onCreate() {
        super.onCreate();
        LinearLayout menuLayout = findViewById(R.id.icon_group);
        floatingActionButton = findViewById(R.id.fab);
        floatingActionButton.setOnClickListener(this);

        for (int i = 0; i < 4; i++) {
            View itemView = createView(context, i);
            itemView.setTag(i);
            itemView.setOnClickListener(this);
            menuLayout.addView(itemView);
        }
        ImageView ivBg = findViewById(R.id.iv_bg);

        Observable.create((ObservableOnSubscribe<Bitmap>) emitter -> {
            Bitmap bitmap = Blurred.with(anchorView)
                    .backgroundColor(Color.WHITE)
                    .scale(1f / 8f)
                    .radius(20)
                    .blur();
            emitter.onNext(bitmap);
            emitter.onComplete();
        })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnNext(ivBg::setImageBitmap)
                .doOnError(throwable -> AToast.error(throwable.getMessage()))
                .subscribe();

        floatingActionButton.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                floatingActionButton.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                //菜单项弹出动画
                for (int i = 0; i < menuLayout.getChildCount(); i++) {
                    final View child = menuLayout.getChildAt(i);
                    child.setVisibility(View.INVISIBLE);
                    postDelayed(() -> {
                        child.setVisibility(View.VISIBLE);
                        ValueAnimator fadeAnim = ObjectAnimator.ofFloat(child, "translationY", 600, 0);
                        fadeAnim.setDuration(500);
                        KickBackAnimator kickAnimator = new KickBackAnimator();
                        kickAnimator.setDuration(500);
                        fadeAnim.setEvaluator(kickAnimator);
                        fadeAnim.start();
                    }, i * 50 + 100);
                }

                floatingActionButton.animate().rotation(135).setDuration(300);
                startAnimation();
            }
        });
    }

    public MorePopup setListener(OnItemClickListener listener) {
        this.listener = listener;
        return this;
    }

//    @Override
//    public void dismiss() {
//        if (popupStatus == PopupStatus.Dismissing) return;
//        popupStatus = PopupStatus.Dismissing;
//        if (popupInfo.autoOpenSoftInput) KeyboardUtils.hideSoftInput(this);
//        clearFocus();
//        doDismissAnimation();
//        doAfterDismiss();
//        popupContentAnimator = null;
//
//    }

    @Override
    public int getAnimationDuration() {
        return 300;
    }

    @Override
    protected void doDismissAnimation() {
        post(() -> floatingActionButton.animate().rotation(0).setDuration(300));
        closeAnimation();
    }

    private View createView(Context context, int index) {
        View itemView = View.inflate(context, R.layout.item_icon, null);
        ImageView menuImage = itemView.findViewById(R.id.menu_icon_iv);
        TextView menuText = itemView.findViewById(R.id.menu_text_tv);

        menuImage.setImageResource(menuIconItems[index]);
        menuText.setText(menuTextItems[index]);

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        params.weight = 1;
        itemView.setLayoutParams(params);
        itemView.setVisibility(View.GONE);
        return itemView;
    }

//    @Override
//    public BasePopupView show() {
//        return super.show();
//    }

    private void startAnimation() {
        post(() -> {
            try {
                //圆形扩展的动画
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
                    int x = floatingActionButton.getLeft() + floatingActionButton.getWidth() / 2;
                    int y = floatingActionButton.getTop() + floatingActionButton.getHeight() / 2;
                    Animator animator = ViewAnimationUtils.createCircularReveal(MorePopup.this, x,
                            y, 0, getHeight());
                    animator.addListener(new AnimatorListenerAdapter() {
                        @Override
                        public void onAnimationStart(Animator animation) {
//                            setVisibility(View.VISIBLE);
                        }

                        @Override
                        public void onAnimationEnd(Animator animation) {
                            //							layout.setVisibility(View.VISIBLE);
                        }
                    });
                    animator.setDuration(300);
                    animator.start();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        });

    }

    private void closeAnimation() {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            int x = floatingActionButton.getLeft() + floatingActionButton.getWidth() / 2;
            int y = floatingActionButton.getTop() + floatingActionButton.getHeight() / 2;
            Animator animator = ViewAnimationUtils.createCircularReveal(MorePopup.this, x,
                    y, getHeight(), 0);
            animator.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationStart(Animator animation) {

                }

                @Override
                public void onAnimationEnd(Animator animation) {
                    setVisibility(View.GONE);
                }
            });
            animator.setDuration(300);
            animator.start();
        } else {
            super.doDismissAnimation();
        }
    }

    @Override
    public void onClick(View v) {
        dismiss();
        if (v == floatingActionButton) {
            return;
        }
        if (listener != null) {
            switch ((int) v.getTag()) {
                case 0:
                    listener.onDiscoverItemClick();
                    break;
                case 1:
                    listener.onCollectionItemClick();
                    break;
                case 2:
                    listener.onWallpaperItemClick();
                    break;
                case 3:
                    listener.onChatWithFriendItemClick();
                    break;
            }
        }
    }

    private class KickBackAnimator implements TypeEvaluator<Float> {

        private static final float s = 1.70158f;
        private float mDuration = 0f;

        public void setDuration(float duration) {
            mDuration = duration;
        }

        public Float evaluate(float fraction, Float startValue, Float endValue) {
            float t = mDuration * fraction;
            float b = startValue;
            float c = endValue - startValue;
            float d = mDuration;
            return calculate(t, b, c, d);
        }

        private Float calculate(float t, float b, float c, float d) {
            return c * ((t = t / d - 1) * t * ((s + 1) * t + s) + 1) + b;
        }
    }
}
