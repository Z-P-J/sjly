package cn.bingoogolapple.baseadapter.databinding;
import cn.bingoogolapple.baseadapter.R;
import cn.bingoogolapple.baseadapter.BR;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;
@SuppressWarnings("unchecked")
public class BgaBaseadapterItemDatabindingDummyBindingImpl extends BgaBaseadapterItemDatabindingDummyBinding  {

    @Nullable
    private static final android.databinding.ViewDataBinding.IncludedLayouts sIncludes;
    @Nullable
    private static final android.util.SparseIntArray sViewsWithIds;
    static {
        sIncludes = null;
        sViewsWithIds = null;
    }
    // views
    @NonNull
    private final android.view.View mboundView0;
    // variables
    // values
    // listeners
    // Inverse Binding Event Handlers

    public BgaBaseadapterItemDatabindingDummyBindingImpl(@Nullable android.databinding.DataBindingComponent bindingComponent, @NonNull View root) {
        this(bindingComponent, root, mapBindings(bindingComponent, root, 1, sIncludes, sViewsWithIds));
    }
    private BgaBaseadapterItemDatabindingDummyBindingImpl(android.databinding.DataBindingComponent bindingComponent, View root, Object[] bindings) {
        super(bindingComponent, root, 0
            );
        this.mboundView0 = (android.view.View) bindings[0];
        this.mboundView0.setTag(null);
        setRootTag(root);
        // listeners
        invalidateAll();
    }

    @Override
    public void invalidateAll() {
        synchronized(this) {
                mDirtyFlags = 0x8L;
        }
        requestRebind();
    }

    @Override
    public boolean hasPendingBindings() {
        synchronized(this) {
            if (mDirtyFlags != 0) {
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean setVariable(int variableId, @Nullable Object variable)  {
        boolean variableSet = true;
        if (BR.model == variableId) {
            setModel((java.lang.Object) variable);
        }
        else if (BR.itemEventHandler == variableId) {
            setItemEventHandler((java.lang.Object) variable);
        }
        else if (BR.viewHolder == variableId) {
            setViewHolder((cn.bingoogolapple.baseadapter.BGABindingViewHolder) variable);
        }
        else {
            variableSet = false;
        }
            return variableSet;
    }

    public void setModel(@Nullable java.lang.Object Model) {
        this.mModel = Model;
    }
    public void setItemEventHandler(@Nullable java.lang.Object ItemEventHandler) {
        this.mItemEventHandler = ItemEventHandler;
    }
    public void setViewHolder(@Nullable cn.bingoogolapple.baseadapter.BGABindingViewHolder ViewHolder) {
        this.mViewHolder = ViewHolder;
    }

    @Override
    protected boolean onFieldChange(int localFieldId, Object object, int fieldId) {
        switch (localFieldId) {
        }
        return false;
    }

    @Override
    protected void executeBindings() {
        long dirtyFlags = 0;
        synchronized(this) {
            dirtyFlags = mDirtyFlags;
            mDirtyFlags = 0;
        }
        // batch finished
    }
    // Listener Stub Implementations
    // callback impls
    // dirty flag
    private  long mDirtyFlags = 0xffffffffffffffffL;
    /* flag mapping
        flag 0 (0x1L): model
        flag 1 (0x2L): itemEventHandler
        flag 2 (0x3L): viewHolder
        flag 3 (0x4L): null
    flag mapping end*/
    //end
}