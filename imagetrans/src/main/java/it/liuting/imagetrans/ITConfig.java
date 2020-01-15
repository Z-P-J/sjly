package it.liuting.imagetrans;

/**
 * Created by liuting on 18/3/12.
 */
public class ITConfig {
    private static final float DEFAULT_READMODERULE = 2f;
    //阅读模式（长图的默认适宽显示）
    protected boolean isReadMode = true;
    //默认小图显示thumb
    protected boolean thumbLarge = false;
    //判定是否是长图的变量,默认是视图高度的1.5倍数
    protected float readModeRule = DEFAULT_READMODERULE;
    //在有缓存的情况下是否显示缩略图
    protected boolean noThumbWhenCached = false;
    //是否显示缩略图
    protected boolean noThumb = false;


    public ITConfig enableReadMode(boolean enable) {
        isReadMode = enable;
        return this;
    }

    public ITConfig largeThumb() {
        thumbLarge = true;
        return this;
    }

    public ITConfig readModeRule(float rule) {
        this.readModeRule = rule;
        return this;
    }

    public ITConfig noThumbWhenCached() {
        noThumbWhenCached = true;
        return this;
    }

    public ITConfig noThumb() {
        noThumb = true;
        return this;
    }
}
