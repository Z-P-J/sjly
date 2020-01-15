package it.liuting.imagetrans;

import android.content.DialogInterface;
import android.support.v4.view.ViewPager;
import android.view.View;

/**
 * Created by liuting on 18/3/15.
 */

public abstract class ImageTransAdapter {

    /**
     * 创建自定义的页面覆盖在viewpager上面
     *
     * @param parent
     * @param viewPager
     * @param dialogInterface
     * @return
     */
    protected abstract View onCreateView(View parent, ViewPager viewPager, final DialogInterface dialogInterface);

    /**
     * 拖动图片关闭手势 拖动进度
     *
     * @param range
     */
    protected void onPullRange(float range) {

    }

    /**
     * 拖动图片关闭手势 取消拖动
     */
    protected void onPullCancel() {

    }

    /**
     * 打开图片动画开始
     */
    protected void onOpenTransStart() {

    }

    /**
     * 打开图片动画结束
     */
    protected void onOpenTransEnd() {

    }

    /**
     * 关闭图片动画开始
     */
    protected void onCloseTransStart() {

    }

    /**
     * 关闭图片动画结束
     */
    protected void onCloseTransEnd() {

    }

    protected void onPageSelected(int pos) {

    }

    /**
     * 单击图片的回调
     *
     * @param v
     * @param pos 单击的图片索引
     * @return 如果拦截默认的单击关闭图片事件就返回true，反之false
     */
    protected boolean onClick(View v, int pos) {
        return false;
    }

    /**
     * 长按图片的监听事件
     *
     * @param v
     * @param pos 长按的图片索引
     */
    protected void onLongClick(View v, int pos) {

    }
}
