////
//// Source code recreated from a .class file by IntelliJ IDEA
//// (powered by Fernflower decompiler)
////
//
//package android.support.v4.app;
//
//import android.animation.Animator;
//import android.animation.AnimatorInflater;
//import android.animation.AnimatorListenerAdapter;
//import android.animation.AnimatorSet;
//import android.animation.PropertyValuesHolder;
//import android.animation.ValueAnimator;
//import android.arch.lifecycle.ViewModelStore;
//import android.content.Context;
//import android.content.res.Configuration;
//import android.content.res.TypedArray;
//import android.content.res.Resources.NotFoundException;
//import android.graphics.Paint;
//import android.os.Bundle;
//import android.os.Looper;
//import android.os.Parcelable;
//import android.os.Build.VERSION;
//import android.support.annotation.CallSuper;
//import android.support.annotation.NonNull;
//import android.support.annotation.Nullable;
//import android.support.v4.app.Fragment.OnStartEnterTransitionListener;
//import android.support.v4.app.Fragment.SavedState;
//import android.support.v4.app.FragmentManager.BackStackEntry;
//import android.support.v4.app.FragmentManager.FragmentLifecycleCallbacks;
//import android.support.v4.app.FragmentManager.OnBackStackChangedListener;
//import android.support.v4.util.ArraySet;
//import android.support.v4.util.DebugUtils;
//import android.support.v4.util.LogWriter;
//import android.support.v4.view.ViewCompat;
//import android.util.AttributeSet;
//import android.util.Log;
//import android.util.SparseArray;
//import android.view.Menu;
//import android.view.MenuInflater;
//import android.view.MenuItem;
//import android.view.View;
//import android.view.ViewGroup;
//import android.view.LayoutInflater.Factory2;
//import android.view.animation.AccelerateInterpolator;
//import android.view.animation.AlphaAnimation;
//import android.view.animation.Animation;
//import android.view.animation.AnimationSet;
//import android.view.animation.AnimationUtils;
//import android.view.animation.DecelerateInterpolator;
//import android.view.animation.Interpolator;
//import android.view.animation.ScaleAnimation;
//import android.view.animation.Transformation;
//import android.view.animation.Animation.AnimationListener;
//import java.io.FileDescriptor;
//import java.io.PrintWriter;
//import java.lang.reflect.Field;
//import java.util.ArrayList;
//import java.util.Arrays;
//import java.util.Collections;
//import java.util.Iterator;
//import java.util.List;
//import java.util.concurrent.CopyOnWriteArrayList;
//
//final class FragmentManagerImpl extends FragmentManager implements Factory2 {
//    static boolean DEBUG = false;
//    static final String TAG = "FragmentManager";
//    static final String TARGET_REQUEST_CODE_STATE_TAG = "android:target_req_state";
//    static final String TARGET_STATE_TAG = "android:target_state";
//    static final String VIEW_STATE_TAG = "android:view_state";
//    static final String USER_VISIBLE_HINT_TAG = "android:user_visible_hint";
//    ArrayList<FragmentManagerImpl.OpGenerator> mPendingActions;
//    boolean mExecutingActions;
//    int mNextFragmentIndex = 0;
//    final ArrayList<Fragment> mAdded = new ArrayList();
//    SparseArray<Fragment> mActive;
//    ArrayList<BackStackRecord> mBackStack;
//    ArrayList<Fragment> mCreatedMenus;
//    ArrayList<BackStackRecord> mBackStackIndices;
//    ArrayList<Integer> mAvailBackStackIndices;
//    ArrayList<OnBackStackChangedListener> mBackStackChangeListeners;
//    private final CopyOnWriteArrayList<FragmentManagerImpl.FragmentLifecycleCallbacksHolder> mLifecycleCallbacks = new CopyOnWriteArrayList();
//    int mCurState = 0;
//    FragmentHostCallback mHost;
//    FragmentContainer mContainer;
//    Fragment mParent;
//    @Nullable
//    Fragment mPrimaryNav;
//    static Field sAnimationListenerField = null;
//    boolean mNeedMenuInvalidate;
//    boolean mStateSaved;
//    boolean mStopped;
//    boolean mDestroyed;
//    String mNoTransactionsBecause;
//    boolean mHavePendingDeferredStart;
//    ArrayList<BackStackRecord> mTmpRecords;
//    ArrayList<Boolean> mTmpIsPop;
//    ArrayList<Fragment> mTmpAddedFragments;
//    Bundle mStateBundle = null;
//    SparseArray<Parcelable> mStateArray = null;
//    ArrayList<FragmentManagerImpl.StartEnterTransitionListener> mPostponedTransactions;
//    FragmentManagerNonConfig mSavedNonConfig;
//    Runnable mExecCommit = new Runnable() {
//        public void run() {
//            FragmentManagerImpl.this.execPendingActions();
//        }
//    };
//    static final Interpolator DECELERATE_QUINT = new DecelerateInterpolator(2.5F);
//    static final Interpolator DECELERATE_CUBIC = new DecelerateInterpolator(1.5F);
//    static final Interpolator ACCELERATE_QUINT = new AccelerateInterpolator(2.5F);
//    static final Interpolator ACCELERATE_CUBIC = new AccelerateInterpolator(1.5F);
//    static final int ANIM_DUR = 220;
//    public static final int ANIM_STYLE_OPEN_ENTER = 1;
//    public static final int ANIM_STYLE_OPEN_EXIT = 2;
//    public static final int ANIM_STYLE_CLOSE_ENTER = 3;
//    public static final int ANIM_STYLE_CLOSE_EXIT = 4;
//    public static final int ANIM_STYLE_FADE_ENTER = 5;
//    public static final int ANIM_STYLE_FADE_EXIT = 6;
//
//    FragmentManagerImpl() {
//    }
//
//    static boolean modifiesAlpha(FragmentManagerImpl.AnimationOrAnimator anim) {
//        if (anim.animation instanceof AlphaAnimation) {
//            return true;
//        } else if (anim.animation instanceof AnimationSet) {
//            List<Animation> anims = ((AnimationSet)anim.animation).getAnimations();
//
//            for(int i = 0; i < anims.size(); ++i) {
//                if (anims.get(i) instanceof AlphaAnimation) {
//                    return true;
//                }
//            }
//
//            return false;
//        } else {
//            return modifiesAlpha(anim.animator);
//        }
//    }
//
//    static boolean modifiesAlpha(Animator anim) {
//        if (anim == null) {
//            return false;
//        } else {
//            if (anim instanceof ValueAnimator) {
//                ValueAnimator valueAnim = (ValueAnimator)anim;
//                PropertyValuesHolder[] values = valueAnim.getValues();
//
//                for(int i = 0; i < values.length; ++i) {
//                    if ("alpha".equals(values[i].getPropertyName())) {
//                        return true;
//                    }
//                }
//            } else if (anim instanceof AnimatorSet) {
//                List<Animator> animList = ((AnimatorSet)anim).getChildAnimations();
//
//                for(int i = 0; i < animList.size(); ++i) {
//                    if (modifiesAlpha((Animator)animList.get(i))) {
//                        return true;
//                    }
//                }
//            }
//
//            return false;
//        }
//    }
//
//    static boolean shouldRunOnHWLayer(View v, FragmentManagerImpl.AnimationOrAnimator anim) {
//        if (v != null && anim != null) {
//            return VERSION.SDK_INT >= 19 && v.getLayerType() == 0 && ViewCompat.hasOverlappingRendering(v) && modifiesAlpha(anim);
//        } else {
//            return false;
//        }
//    }
//
//    private void throwException(RuntimeException ex) {
//        Log.e("FragmentManager", ex.getMessage());
//        Log.e("FragmentManager", "Activity state:");
//        LogWriter logw = new LogWriter("FragmentManager");
//        PrintWriter pw = new PrintWriter(logw);
//        if (this.mHost != null) {
//            try {
//                this.mHost.onDump("  ", (FileDescriptor)null, pw, new String[0]);
//            } catch (Exception var6) {
//                Log.e("FragmentManager", "Failed dumping state", var6);
//            }
//        } else {
//            try {
//                this.dump("  ", (FileDescriptor)null, pw, new String[0]);
//            } catch (Exception var5) {
//                Log.e("FragmentManager", "Failed dumping state", var5);
//            }
//        }
//
//        throw ex;
//    }
//
//    public FragmentTransaction beginTransaction() {
//        return new BackStackRecord(this);
//    }
//
//    public boolean executePendingTransactions() {
//        boolean updates = this.execPendingActions();
//        this.forcePostponedTransactions();
//        return updates;
//    }
//
//    public void popBackStack() {
//        this.enqueueAction(new FragmentManagerImpl.PopBackStackState((String)null, -1, 0), false);
//    }
//
//    public boolean popBackStackImmediate() {
//        this.checkStateLoss();
//        return this.popBackStackImmediate((String)null, -1, 0);
//    }
//
//    public void popBackStack(@Nullable String name, int flags) {
//        this.enqueueAction(new FragmentManagerImpl.PopBackStackState(name, -1, flags), false);
//    }
//
//    public boolean popBackStackImmediate(@Nullable String name, int flags) {
//        this.checkStateLoss();
//        return this.popBackStackImmediate(name, -1, flags);
//    }
//
//    public void popBackStack(int id, int flags) {
//        if (id < 0) {
//            throw new IllegalArgumentException("Bad id: " + id);
//        } else {
//            this.enqueueAction(new FragmentManagerImpl.PopBackStackState((String)null, id, flags), false);
//        }
//    }
//
//    public boolean popBackStackImmediate(int id, int flags) {
//        this.checkStateLoss();
//        this.execPendingActions();
//        if (id < 0) {
//            throw new IllegalArgumentException("Bad id: " + id);
//        } else {
//            return this.popBackStackImmediate((String)null, id, flags);
//        }
//    }
//
//    private boolean popBackStackImmediate(String name, int id, int flags) {
//        this.execPendingActions();
//        this.ensureExecReady(true);
//        if (this.mPrimaryNav != null && id < 0 && name == null) {
//            FragmentManager childManager = this.mPrimaryNav.peekChildFragmentManager();
//            if (childManager != null && childManager.popBackStackImmediate()) {
//                return true;
//            }
//        }
//
//        boolean executePop = this.popBackStackState(this.mTmpRecords, this.mTmpIsPop, name, id, flags);
//        if (executePop) {
//            this.mExecutingActions = true;
//
//            try {
//                this.removeRedundantOperationsAndExecute(this.mTmpRecords, this.mTmpIsPop);
//            } finally {
//                this.cleanupExec();
//            }
//        }
//
//        this.doPendingDeferredStart();
//        this.burpActive();
//        return executePop;
//    }
//
//    public int getBackStackEntryCount() {
//        return this.mBackStack != null ? this.mBackStack.size() : 0;
//    }
//
//    public BackStackEntry getBackStackEntryAt(int index) {
//        return (BackStackEntry)this.mBackStack.get(index);
//    }
//
//    public void addOnBackStackChangedListener(OnBackStackChangedListener listener) {
//        if (this.mBackStackChangeListeners == null) {
//            this.mBackStackChangeListeners = new ArrayList();
//        }
//
//        this.mBackStackChangeListeners.add(listener);
//    }
//
//    public void removeOnBackStackChangedListener(OnBackStackChangedListener listener) {
//        if (this.mBackStackChangeListeners != null) {
//            this.mBackStackChangeListeners.remove(listener);
//        }
//
//    }
//
//    public void putFragment(Bundle bundle, String key, Fragment fragment) {
//        if (fragment.mIndex < 0) {
//            this.throwException(new IllegalStateException("Fragment " + fragment + " is not currently in the FragmentManager"));
//        }
//
//        bundle.putInt(key, fragment.mIndex);
//    }
//
//    @Nullable
//    public Fragment getFragment(Bundle bundle, String key) {
//        int index = bundle.getInt(key, -1);
//        if (index == -1) {
//            return null;
//        } else {
//            Fragment f = (Fragment)this.mActive.get(index);
//            if (f == null) {
//                this.throwException(new IllegalStateException("Fragment no longer exists for key " + key + ": index " + index));
//            }
//
//            return f;
//        }
//    }
//
//    public List<Fragment> getFragments() {
//        if (this.mAdded.isEmpty()) {
//            return Collections.emptyList();
//        } else {
//            synchronized(this.mAdded) {
//                return (List)this.mAdded.clone();
//            }
//        }
//    }
//
//    List<Fragment> getActiveFragments() {
//        if (this.mActive == null) {
//            return null;
//        } else {
//            int count = this.mActive.size();
//            ArrayList<Fragment> fragments = new ArrayList(count);
//
//            for(int i = 0; i < count; ++i) {
//                fragments.add(this.mActive.valueAt(i));
//            }
//
//            return fragments;
//        }
//    }
//
//    int getActiveFragmentCount() {
//        return this.mActive == null ? 0 : this.mActive.size();
//    }
//
//    @Nullable
//    public SavedState saveFragmentInstanceState(Fragment fragment) {
//        if (fragment.mIndex < 0) {
//            this.throwException(new IllegalStateException("Fragment " + fragment + " is not currently in the FragmentManager"));
//        }
//
//        if (fragment.mState > 0) {
//            Bundle result = this.saveFragmentBasicState(fragment);
//            return result != null ? new SavedState(result) : null;
//        } else {
//            return null;
//        }
//    }
//
//    public boolean isDestroyed() {
//        return this.mDestroyed;
//    }
//
//    public String toString() {
//        StringBuilder sb = new StringBuilder(128);
//        sb.append("FragmentManager{");
//        sb.append(Integer.toHexString(System.identityHashCode(this)));
//        sb.append(" in ");
//        if (this.mParent != null) {
//            DebugUtils.buildShortClassTag(this.mParent, sb);
//        } else {
//            DebugUtils.buildShortClassTag(this.mHost, sb);
//        }
//
//        sb.append("}}");
//        return sb.toString();
//    }
//
//    public void dump(String prefix, FileDescriptor fd, PrintWriter writer, String[] args) {
//        String innerPrefix = prefix + "    ";
//        int N;
//        int i;
//        Fragment f;
//        if (this.mActive != null) {
//            N = this.mActive.size();
//            if (N > 0) {
//                writer.print(prefix);
//                writer.print("Active Fragments in ");
//                writer.print(Integer.toHexString(System.identityHashCode(this)));
//                writer.println(":");
//
//                for(i = 0; i < N; ++i) {
//                    f = (Fragment)this.mActive.valueAt(i);
//                    writer.print(prefix);
//                    writer.print("  #");
//                    writer.print(i);
//                    writer.print(": ");
//                    writer.println(f);
//                    if (f != null) {
//                        f.dump(innerPrefix, fd, writer, args);
//                    }
//                }
//            }
//        }
//
//        N = this.mAdded.size();
//        if (N > 0) {
//            writer.print(prefix);
//            writer.println("Added Fragments:");
//
//            for(i = 0; i < N; ++i) {
//                f = (Fragment)this.mAdded.get(i);
//                writer.print(prefix);
//                writer.print("  #");
//                writer.print(i);
//                writer.print(": ");
//                writer.println(f.toString());
//            }
//        }
//
//        if (this.mCreatedMenus != null) {
//            N = this.mCreatedMenus.size();
//            if (N > 0) {
//                writer.print(prefix);
//                writer.println("Fragments Created Menus:");
//
//                for(i = 0; i < N; ++i) {
//                    f = (Fragment)this.mCreatedMenus.get(i);
//                    writer.print(prefix);
//                    writer.print("  #");
//                    writer.print(i);
//                    writer.print(": ");
//                    writer.println(f.toString());
//                }
//            }
//        }
//
//        if (this.mBackStack != null) {
//            N = this.mBackStack.size();
//            if (N > 0) {
//                writer.print(prefix);
//                writer.println("Back Stack:");
//
//                for(i = 0; i < N; ++i) {
//                    BackStackRecord bs = (BackStackRecord)this.mBackStack.get(i);
//                    writer.print(prefix);
//                    writer.print("  #");
//                    writer.print(i);
//                    writer.print(": ");
//                    writer.println(bs.toString());
//                    bs.dump(innerPrefix, fd, writer, args);
//                }
//            }
//        }
//
//        synchronized(this) {
//            if (this.mBackStackIndices != null) {
//                N = this.mBackStackIndices.size();
//                if (N > 0) {
//                    writer.print(prefix);
//                    writer.println("Back Stack Indices:");
//
//                    for(int j = 0; j < N; ++j) {
//                        BackStackRecord bs = (BackStackRecord)this.mBackStackIndices.get(j);
//                        writer.print(prefix);
//                        writer.print("  #");
//                        writer.print(j);
//                        writer.print(": ");
//                        writer.println(bs);
//                    }
//                }
//            }
//
//            if (this.mAvailBackStackIndices != null && this.mAvailBackStackIndices.size() > 0) {
//                writer.print(prefix);
//                writer.print("mAvailBackStackIndices: ");
//                writer.println(Arrays.toString(this.mAvailBackStackIndices.toArray()));
//            }
//        }
//
//        if (this.mPendingActions != null) {
//            N = this.mPendingActions.size();
//            if (N > 0) {
//                writer.print(prefix);
//                writer.println("Pending Actions:");
//
//                for(i = 0; i < N; ++i) {
//                    FragmentManagerImpl.OpGenerator r = (FragmentManagerImpl.OpGenerator)this.mPendingActions.get(i);
//                    writer.print(prefix);
//                    writer.print("  #");
//                    writer.print(i);
//                    writer.print(": ");
//                    writer.println(r);
//                }
//            }
//        }
//
//        writer.print(prefix);
//        writer.println("FragmentManager misc state:");
//        writer.print(prefix);
//        writer.print("  mHost=");
//        writer.println(this.mHost);
//        writer.print(prefix);
//        writer.print("  mContainer=");
//        writer.println(this.mContainer);
//        if (this.mParent != null) {
//            writer.print(prefix);
//            writer.print("  mParent=");
//            writer.println(this.mParent);
//        }
//
//        writer.print(prefix);
//        writer.print("  mCurState=");
//        writer.print(this.mCurState);
//        writer.print(" mStateSaved=");
//        writer.print(this.mStateSaved);
//        writer.print(" mStopped=");
//        writer.print(this.mStopped);
//        writer.print(" mDestroyed=");
//        writer.println(this.mDestroyed);
//        if (this.mNeedMenuInvalidate) {
//            writer.print(prefix);
//            writer.print("  mNeedMenuInvalidate=");
//            writer.println(this.mNeedMenuInvalidate);
//        }
//
//        if (this.mNoTransactionsBecause != null) {
//            writer.print(prefix);
//            writer.print("  mNoTransactionsBecause=");
//            writer.println(this.mNoTransactionsBecause);
//        }
//
//    }
//
//    static FragmentManagerImpl.AnimationOrAnimator makeOpenCloseAnimation(Context context, float startScale, float endScale, float startAlpha, float endAlpha) {
//        AnimationSet set = new AnimationSet(false);
//        ScaleAnimation scale = new ScaleAnimation(startScale, endScale, startScale, endScale, 1, 0.5F, 1, 0.5F);
//        scale.setInterpolator(DECELERATE_QUINT);
//        scale.setDuration(220L);
//        set.addAnimation(scale);
//        AlphaAnimation alpha = new AlphaAnimation(startAlpha, endAlpha);
//        alpha.setInterpolator(DECELERATE_CUBIC);
//        alpha.setDuration(220L);
//        set.addAnimation(alpha);
//        return new FragmentManagerImpl.AnimationOrAnimator(set);
//    }
//
//    static FragmentManagerImpl.AnimationOrAnimator makeFadeAnimation(Context context, float start, float end) {
//        AlphaAnimation anim = new AlphaAnimation(start, end);
//        anim.setInterpolator(DECELERATE_CUBIC);
//        anim.setDuration(220L);
//        return new FragmentManagerImpl.AnimationOrAnimator(anim);
//    }
//
//    FragmentManagerImpl.AnimationOrAnimator loadAnimation(Fragment fragment, int transit, boolean enter, int transitionStyle) {
//        int nextAnim = fragment.getNextAnim();
//        Animation animation = fragment.onCreateAnimation(transit, enter, nextAnim);
//        if (animation != null) {
//            return new FragmentManagerImpl.AnimationOrAnimator(animation);
//        } else {
//            Animator animator = fragment.onCreateAnimator(transit, enter, nextAnim);
//            if (animator != null) {
//                return new FragmentManagerImpl.AnimationOrAnimator(animator);
//            } else {
//                if (nextAnim != 0) {
//                    String dir = this.mHost.getContext().getResources().getResourceTypeName(nextAnim);
//                    boolean isAnim = "anim".equals(dir);
//                    boolean successfulLoad = false;
//                    if (isAnim) {
//                        try {
//                            animation = AnimationUtils.loadAnimation(this.mHost.getContext(), nextAnim);
//                            if (animation != null) {
//                                return new FragmentManagerImpl.AnimationOrAnimator(animation);
//                            }
//
//                            successfulLoad = true;
//                        } catch (NotFoundException var12) {
//                            throw var12;
//                        } catch (RuntimeException var13) {
//                        }
//                    }
//
//                    if (!successfulLoad) {
//                        try {
//                            animator = AnimatorInflater.loadAnimator(this.mHost.getContext(), nextAnim);
//                            if (animator != null) {
//                                return new FragmentManagerImpl.AnimationOrAnimator(animator);
//                            }
//                        } catch (RuntimeException var14) {
//                            if (isAnim) {
//                                throw var14;
//                            }
//
//                            animation = AnimationUtils.loadAnimation(this.mHost.getContext(), nextAnim);
//                            if (animation != null) {
//                                return new FragmentManagerImpl.AnimationOrAnimator(animation);
//                            }
//                        }
//                    }
//                }
//
//                if (transit == 0) {
//                    return null;
//                } else {
//                    int styleIndex = transitToStyleIndex(transit, enter);
//                    if (styleIndex < 0) {
//                        return null;
//                    } else {
//                        switch(styleIndex) {
//                            case 1:
//                                return makeOpenCloseAnimation(this.mHost.getContext(), 1.125F, 1.0F, 0.0F, 1.0F);
//                            case 2:
//                                return makeOpenCloseAnimation(this.mHost.getContext(), 1.0F, 0.975F, 1.0F, 0.0F);
//                            case 3:
//                                return makeOpenCloseAnimation(this.mHost.getContext(), 0.975F, 1.0F, 0.0F, 1.0F);
//                            case 4:
//                                return makeOpenCloseAnimation(this.mHost.getContext(), 1.0F, 1.075F, 1.0F, 0.0F);
//                            case 5:
//                                return makeFadeAnimation(this.mHost.getContext(), 0.0F, 1.0F);
//                            case 6:
//                                return makeFadeAnimation(this.mHost.getContext(), 1.0F, 0.0F);
//                            default:
//                                if (transitionStyle == 0 && this.mHost.onHasWindowAnimations()) {
//                                    transitionStyle = this.mHost.onGetWindowAnimations();
//                                }
//
//                                return transitionStyle == 0 ? null : null;
//                        }
//                    }
//                }
//            }
//        }
//    }
//
//    public void performPendingDeferredStart(Fragment f) {
//        if (f.mDeferStart) {
//            if (this.mExecutingActions) {
//                this.mHavePendingDeferredStart = true;
//                return;
//            }
//
//            f.mDeferStart = false;
//            this.moveToState(f, this.mCurState, 0, 0, false);
//        }
//
//    }
//
//    private static void setHWLayerAnimListenerIfAlpha(View v, FragmentManagerImpl.AnimationOrAnimator anim) {
//        if (v != null && anim != null) {
//            if (shouldRunOnHWLayer(v, anim)) {
//                if (anim.animator != null) {
//                    anim.animator.addListener(new FragmentManagerImpl.AnimatorOnHWLayerIfNeededListener(v));
//                } else {
//                    AnimationListener originalListener = getAnimationListener(anim.animation);
//                    v.setLayerType(2, (Paint)null);
//                    anim.animation.setAnimationListener(new FragmentManagerImpl.AnimateOnHWLayerIfNeededListener(v, originalListener));
//                }
//            }
//
//        }
//    }
//
//    private static AnimationListener getAnimationListener(Animation animation) {
//        AnimationListener originalListener = null;
//
//        try {
//            if (sAnimationListenerField == null) {
//                sAnimationListenerField = Animation.class.getDeclaredField("mListener");
//                sAnimationListenerField.setAccessible(true);
//            }
//
//            originalListener = (AnimationListener)sAnimationListenerField.get(animation);
//        } catch (NoSuchFieldException var3) {
//            Log.e("FragmentManager", "No field with the name mListener is found in Animation class", var3);
//        } catch (IllegalAccessException var4) {
//            Log.e("FragmentManager", "Cannot access Animation's mListener field", var4);
//        }
//
//        return originalListener;
//    }
//
//    boolean isStateAtLeast(int state) {
//        return this.mCurState >= state;
//    }
//
//    void moveToState(Fragment f, int newState, int transit, int transitionStyle, boolean keepActive) {
//        if ((!f.mAdded || f.mDetached) && newState > 1) {
//            newState = 1;
//        }
//
//        if (f.mRemoving && newState > f.mState) {
//            if (f.mState == 0 && f.isInBackStack()) {
//                newState = 1;
//            } else {
//                newState = f.mState;
//            }
//        }
//
//        if (f.mDeferStart && f.mState < 3 && newState > 2) {
//            newState = 2;
//        }
//
//        if (f.mState <= newState) {
//            label297: {
//                if (f.mFromLayout && !f.mInLayout) {
//                    return;
//                }
//
//                if (f.getAnimatingAway() != null || f.getAnimator() != null) {
//                    f.setAnimatingAway((View)null);
//                    f.setAnimator((Animator)null);
//                    this.moveToState(f, f.getStateAfterAnimating(), 0, 0, true);
//                }
//
//                switch(f.mState) {
//                    case 0:
//                        if (newState > 0) {
//                            if (DEBUG) {
//                                Log.v("FragmentManager", "moveto CREATED: " + f);
//                            }
//
//                            if (f.mSavedFragmentState != null) {
//                                f.mSavedFragmentState.setClassLoader(this.mHost.getContext().getClassLoader());
//                                f.mSavedViewState = f.mSavedFragmentState.getSparseParcelableArray("android:view_state");
//                                f.mTarget = this.getFragment(f.mSavedFragmentState, "android:target_state");
//                                if (f.mTarget != null) {
//                                    f.mTargetRequestCode = f.mSavedFragmentState.getInt("android:target_req_state", 0);
//                                }
//
//                                if (f.mSavedUserVisibleHint != null) {
//                                    f.mUserVisibleHint = f.mSavedUserVisibleHint;
//                                    f.mSavedUserVisibleHint = null;
//                                } else {
//                                    f.mUserVisibleHint = f.mSavedFragmentState.getBoolean("android:user_visible_hint", true);
//                                }
//
//                                if (!f.mUserVisibleHint) {
//                                    f.mDeferStart = true;
//                                    if (newState > 2) {
//                                        newState = 2;
//                                    }
//                                }
//                            }
//
//                            f.mHost = this.mHost;
//                            f.mParentFragment = this.mParent;
//                            f.mFragmentManager = this.mParent != null ? this.mParent.mChildFragmentManager : this.mHost.getFragmentManagerImpl();
//                            if (f.mTarget != null) {
//                                if (this.mActive.get(f.mTarget.mIndex) != f.mTarget) {
//                                    throw new IllegalStateException("Fragment " + f + " declared target fragment " + f.mTarget + " that does not belong to this FragmentManager!");
//                                }
//
//                                if (f.mTarget.mState < 1) {
//                                    this.moveToState(f.mTarget, 1, 0, 0, true);
//                                }
//                            }
//
//                            this.dispatchOnFragmentPreAttached(f, this.mHost.getContext(), false);
//                            f.mCalled = false;
//                            f.onAttach(this.mHost.getContext());
//                            if (!f.mCalled) {
//                                throw new SuperNotCalledException("Fragment " + f + " did not call through to super.onAttach()");
//                            }
//
//                            if (f.mParentFragment == null) {
//                                this.mHost.onAttachFragment(f);
//                            } else {
//                                f.mParentFragment.onAttachFragment(f);
//                            }
//
//                            this.dispatchOnFragmentAttached(f, this.mHost.getContext(), false);
//                            if (!f.mIsCreated) {
//                                this.dispatchOnFragmentPreCreated(f, f.mSavedFragmentState, false);
//                                f.performCreate(f.mSavedFragmentState);
//                                this.dispatchOnFragmentCreated(f, f.mSavedFragmentState, false);
//                            } else {
//                                f.restoreChildFragmentState(f.mSavedFragmentState);
//                                f.mState = 1;
//                            }
//
//                            f.mRetaining = false;
//                        }
//                    case 1:
//                        this.ensureInflatedFragmentView(f);
//                        if (newState > 1) {
//                            if (DEBUG) {
//                                Log.v("FragmentManager", "moveto ACTIVITY_CREATED: " + f);
//                            }
//
//                            if (!f.mFromLayout) {
//                                ViewGroup container = null;
//                                if (f.mContainerId != 0) {
//                                    if (f.mContainerId == -1) {
//                                        this.throwException(new IllegalArgumentException("Cannot create fragment " + f + " for a container view with no id"));
//                                    }
//
//                                    container = (ViewGroup)this.mContainer.onFindViewById(f.mContainerId);
//                                    if (container == null && !f.mRestored) {
//                                        String resName;
//                                        try {
//                                            resName = f.getResources().getResourceName(f.mContainerId);
//                                        } catch (NotFoundException var9) {
//                                            resName = "unknown";
//                                        }
//
//                                        this.throwException(new IllegalArgumentException("No view found for id 0x" + Integer.toHexString(f.mContainerId) + " (" + resName + ") for fragment " + f));
//                                    }
//                                }
//
//                                f.mContainer = container;
//                                f.performCreateView(f.performGetLayoutInflater(f.mSavedFragmentState), container, f.mSavedFragmentState);
//                                if (f.mView == null) {
//                                    f.mInnerView = null;
//                                } else {
//                                    f.mInnerView = f.mView;
//                                    f.mView.setSaveFromParentEnabled(false);
//                                    if (container != null) {
//                                        container.addView(f.mView);
//                                    }
//
//                                    if (f.mHidden) {
//                                        f.mView.setVisibility(View.GONE);
//                                    }
//
//                                    f.onViewCreated(f.mView, f.mSavedFragmentState);
//                                    this.dispatchOnFragmentViewCreated(f, f.mView, f.mSavedFragmentState, false);
//                                    f.mIsNewlyAdded = f.mView.getVisibility() == View.VISIBLE && f.mContainer != null;
//                                }
//                            }
//
//                            f.performActivityCreated(f.mSavedFragmentState);
//                            this.dispatchOnFragmentActivityCreated(f, f.mSavedFragmentState, false);
//                            if (f.mView != null) {
//                                f.restoreViewState(f.mSavedFragmentState);
//                            }
//
//                            f.mSavedFragmentState = null;
//                        }
//                    case 2:
//                        if (newState > 2) {
//                            if (DEBUG) {
//                                Log.v("FragmentManager", "moveto STARTED: " + f);
//                            }
//
//                            f.performStart();
//                            this.dispatchOnFragmentStarted(f, false);
//                        }
//                    case 3:
//                        break;
//                    default:
//                        break label297;
//                }
//
//                if (newState > 3) {
//                    if (DEBUG) {
//                        Log.v("FragmentManager", "moveto RESUMED: " + f);
//                    }
//
//                    f.performResume();
//                    this.dispatchOnFragmentResumed(f, false);
//                    f.mSavedFragmentState = null;
//                    f.mSavedViewState = null;
//                }
//            }
//        } else if (f.mState > newState) {
//            switch(f.mState) {
//                case 4:
//                    if (newState < 4) {
//                        if (DEBUG) {
//                            Log.v("FragmentManager", "movefrom RESUMED: " + f);
//                        }
//
//                        f.performPause();
//                        this.dispatchOnFragmentPaused(f, false);
//                    }
//                case 3:
//                    if (newState < 3) {
//                        if (DEBUG) {
//                            Log.v("FragmentManager", "movefrom STARTED: " + f);
//                        }
//
//                        f.performStop();
//                        this.dispatchOnFragmentStopped(f, false);
//                    }
//                case 2:
//                    if (newState < 2) {
//                        if (DEBUG) {
//                            Log.v("FragmentManager", "movefrom ACTIVITY_CREATED: " + f);
//                        }
//
//                        if (f.mView != null && this.mHost.onShouldSaveFragmentState(f) && f.mSavedViewState == null) {
//                            this.saveFragmentViewState(f);
//                        }
//
//                        f.performDestroyView();
//                        this.dispatchOnFragmentViewDestroyed(f, false);
//                        if (f.mView != null && f.mContainer != null) {
//                            f.mContainer.endViewTransition(f.mView);
//                            f.mView.clearAnimation();
//                            FragmentManagerImpl.AnimationOrAnimator anim = null;
//                            if (this.mCurState > 0 && !this.mDestroyed && f.mView.getVisibility() == View.VISIBLE && f.mPostponedAlpha >= 0.0F) {
//                                anim = this.loadAnimation(f, transit, false, transitionStyle);
//                            }
//
//                            f.mPostponedAlpha = 0.0F;
//                            if (anim != null) {
//                                this.animateRemoveFragment(f, anim, newState);
//                            }
//
//                            f.mContainer.removeView(f.mView);
//                        }
//
//                        f.mContainer = null;
//                        f.mView = null;
//                        f.mViewLifecycleOwner = null;
//                        f.mViewLifecycleOwnerLiveData.setValue(null);
//                        f.mInnerView = null;
//                        f.mInLayout = false;
//                    }
//                case 1:
//                    if (newState < 1) {
//                        if (this.mDestroyed) {
//                            if (f.getAnimatingAway() != null) {
//                                View v = f.getAnimatingAway();
//                                f.setAnimatingAway((View)null);
//                                v.clearAnimation();
//                            } else if (f.getAnimator() != null) {
//                                Animator animator = f.getAnimator();
//                                f.setAnimator((Animator)null);
//                                animator.cancel();
//                            }
//                        }
//
//                        if (f.getAnimatingAway() == null && f.getAnimator() == null) {
//                            if (DEBUG) {
//                                Log.v("FragmentManager", "movefrom CREATED: " + f);
//                            }
//
//                            if (!f.mRetaining) {
//                                f.performDestroy();
//                                this.dispatchOnFragmentDestroyed(f, false);
//                            } else {
//                                f.mState = 0;
//                            }
//
//                            f.performDetach();
//                            this.dispatchOnFragmentDetached(f, false);
//                            if (!keepActive) {
//                                if (!f.mRetaining) {
//                                    this.makeInactive(f);
//                                } else {
//                                    f.mHost = null;
//                                    f.mParentFragment = null;
//                                    f.mFragmentManager = null;
//                                }
//                            }
//                        } else {
//                            f.setStateAfterAnimating(newState);
//                            newState = 1;
//                        }
//                    }
//            }
//        }
//
//        if (f.mState != newState) {
//            Log.w("FragmentManager", "moveToState: Fragment state for " + f + " not updated inline; " + "expected state " + newState + " found " + f.mState);
//            f.mState = newState;
//        }
//
//    }
//
//    private void animateRemoveFragment(@NonNull final Fragment fragment, @NonNull FragmentManagerImpl.AnimationOrAnimator anim, int newState) {
//        final View viewToAnimate = fragment.mView;
//        final ViewGroup container = fragment.mContainer;
//        container.startViewTransition(viewToAnimate);
//        fragment.setStateAfterAnimating(newState);
//        if (anim.animation != null) {
//            Animation animation = new FragmentManagerImpl.EndViewTransitionAnimator(anim.animation, container, viewToAnimate);
//            fragment.setAnimatingAway(fragment.mView);
//            AnimationListener listener = getAnimationListener(animation);
//            animation.setAnimationListener(new FragmentManagerImpl.AnimationListenerWrapper(listener) {
//                public void onAnimationEnd(Animation animation) {
//                    super.onAnimationEnd(animation);
//                    container.post(new Runnable() {
//                        public void run() {
//                            if (fragment.getAnimatingAway() != null) {
//                                fragment.setAnimatingAway((View)null);
//                                FragmentManagerImpl.this.moveToState(fragment, fragment.getStateAfterAnimating(), 0, 0, false);
//                            }
//
//                        }
//                    });
//                }
//            });
//            setHWLayerAnimListenerIfAlpha(viewToAnimate, anim);
//            fragment.mView.startAnimation(animation);
//        } else {
//            Animator animator = anim.animator;
//            fragment.setAnimator(anim.animator);
//            animator.addListener(new AnimatorListenerAdapter() {
//                public void onAnimationEnd(Animator anim) {
//                    container.endViewTransition(viewToAnimate);
//                    Animator animator = fragment.getAnimator();
//                    fragment.setAnimator((Animator)null);
//                    if (animator != null && container.indexOfChild(viewToAnimate) < 0) {
//                        FragmentManagerImpl.this.moveToState(fragment, fragment.getStateAfterAnimating(), 0, 0, false);
//                    }
//
//                }
//            });
//            animator.setTarget(fragment.mView);
//            setHWLayerAnimListenerIfAlpha(fragment.mView, anim);
//            animator.start();
//        }
//
//    }
//
//    void moveToState(Fragment f) {
//        this.moveToState(f, this.mCurState, 0, 0, false);
//    }
//
//    void ensureInflatedFragmentView(Fragment f) {
//        if (f.mFromLayout && !f.mPerformedCreateView) {
//            f.performCreateView(f.performGetLayoutInflater(f.mSavedFragmentState), (ViewGroup)null, f.mSavedFragmentState);
//            if (f.mView != null) {
//                f.mInnerView = f.mView;
//                f.mView.setSaveFromParentEnabled(false);
//                if (f.mHidden) {
//                    f.mView.setVisibility(View.GONE);
//                }
//
//                f.onViewCreated(f.mView, f.mSavedFragmentState);
//                this.dispatchOnFragmentViewCreated(f, f.mView, f.mSavedFragmentState, false);
//            } else {
//                f.mInnerView = null;
//            }
//        }
//
//    }
//
//    void completeShowHideFragment(final Fragment fragment) {
//        if (fragment.mView != null) {
//            FragmentManagerImpl.AnimationOrAnimator anim = this.loadAnimation(fragment, fragment.getNextTransition(), !fragment.mHidden, fragment.getNextTransitionStyle());
//            if (anim != null && anim.animator != null) {
//                anim.animator.setTarget(fragment.mView);
//                if (fragment.mHidden) {
//                    if (fragment.isHideReplaced()) {
//                        fragment.setHideReplaced(false);
//                    } else {
//                        final ViewGroup container = fragment.mContainer;
//                        final View animatingView = fragment.mView;
//                        container.startViewTransition(animatingView);
//                        anim.animator.addListener(new AnimatorListenerAdapter() {
//                            public void onAnimationEnd(Animator animation) {
//                                container.endViewTransition(animatingView);
//                                animation.removeListener(this);
//                                if (fragment.mView != null) {
//                                    fragment.mView.setVisibility(View.GONE);
//                                }
//
//                            }
//                        });
//                    }
//                } else {
//                    fragment.mView.setVisibility(View.VISIBLE);
//                }
//
//                setHWLayerAnimListenerIfAlpha(fragment.mView, anim);
//                anim.animator.start();
//            } else {
//                if (anim != null) {
//                    setHWLayerAnimListenerIfAlpha(fragment.mView, anim);
//                    fragment.mView.startAnimation(anim.animation);
//                    anim.animation.start();
//                }
//
//                fragment.mView.setVisibility(fragment.mHidden && !fragment.isHideReplaced() ? View.GONE : View.VISIBLE);
//                if (fragment.isHideReplaced()) {
//                    fragment.setHideReplaced(false);
//                }
//            }
//        }
//
//        if (fragment.mAdded && fragment.mHasMenu && fragment.mMenuVisible) {
//            this.mNeedMenuInvalidate = true;
//        }
//
//        fragment.mHiddenChanged = false;
//        fragment.onHiddenChanged(fragment.mHidden);
//    }
//
//    void moveFragmentToExpectedState(Fragment f) {
//        if (f != null) {
//            int nextState = this.mCurState;
//            if (f.mRemoving) {
//                if (f.isInBackStack()) {
//                    nextState = Math.min(nextState, 1);
//                } else {
//                    nextState = Math.min(nextState, 0);
//                }
//            }
//
//            this.moveToState(f, nextState, f.getNextTransition(), f.getNextTransitionStyle(), false);
//            if (f.mView != null) {
//                Fragment underFragment = this.findFragmentUnder(f);
//                if (underFragment != null) {
//                    View underView = underFragment.mView;
//                    ViewGroup container = f.mContainer;
//                    int underIndex = container.indexOfChild(underView);
//                    int viewIndex = container.indexOfChild(f.mView);
//                    if (viewIndex < underIndex) {
//                        container.removeViewAt(viewIndex);
//                        container.addView(f.mView, underIndex);
//                    }
//                }
//
//                if (f.mIsNewlyAdded && f.mContainer != null) {
//                    if (f.mPostponedAlpha > 0.0F) {
//                        f.mView.setAlpha(f.mPostponedAlpha);
//                    }
//
//                    f.mPostponedAlpha = 0.0F;
//                    f.mIsNewlyAdded = false;
//                    FragmentManagerImpl.AnimationOrAnimator anim = this.loadAnimation(f, f.getNextTransition(), true, f.getNextTransitionStyle());
//                    if (anim != null) {
//                        setHWLayerAnimListenerIfAlpha(f.mView, anim);
//                        if (anim.animation != null) {
//                            f.mView.startAnimation(anim.animation);
//                        } else {
//                            anim.animator.setTarget(f.mView);
//                            anim.animator.start();
//                        }
//                    }
//                }
//            }
//
//            if (f.mHiddenChanged) {
//                this.completeShowHideFragment(f);
//            }
//
//        }
//    }
//
//    void moveToState(int newState, boolean always) {
//        if (this.mHost == null && newState != 0) {
//            throw new IllegalStateException("No activity");
//        } else if (always || newState != this.mCurState) {
//            this.mCurState = newState;
//            if (this.mActive != null) {
//                int numAdded = this.mAdded.size();
//
//                int numActive;
//                for(numActive = 0; numActive < numAdded; ++numActive) {
//                    Fragment f = (Fragment)this.mAdded.get(numActive);
//                    this.moveFragmentToExpectedState(f);
//                }
//
//                numActive = this.mActive.size();
//
//                for(int i = 0; i < numActive; ++i) {
//                    Fragment f = (Fragment)this.mActive.valueAt(i);
//                    if (f != null && (f.mRemoving || f.mDetached) && !f.mIsNewlyAdded) {
//                        this.moveFragmentToExpectedState(f);
//                    }
//                }
//
//                this.startPendingDeferredFragments();
//                if (this.mNeedMenuInvalidate && this.mHost != null && this.mCurState == 4) {
//                    this.mHost.onSupportInvalidateOptionsMenu();
//                    this.mNeedMenuInvalidate = false;
//                }
//            }
//
//        }
//    }
//
//    void startPendingDeferredFragments() {
//        if (this.mActive != null) {
//            for(int i = 0; i < this.mActive.size(); ++i) {
//                Fragment f = (Fragment)this.mActive.valueAt(i);
//                if (f != null) {
//                    this.performPendingDeferredStart(f);
//                }
//            }
//
//        }
//    }
//
//    void makeActive(Fragment f) {
//        if (f.mIndex < 0) {
//            f.setIndex(this.mNextFragmentIndex++, this.mParent);
//            if (this.mActive == null) {
//                this.mActive = new SparseArray();
//            }
//
//            this.mActive.put(f.mIndex, f);
//            if (DEBUG) {
//                Log.v("FragmentManager", "Allocated fragment index " + f);
//            }
//
//        }
//    }
//
//    void makeInactive(Fragment f) {
//        if (f.mIndex >= 0) {
//            if (DEBUG) {
//                Log.v("FragmentManager", "Freeing fragment index " + f);
//            }
//
//            this.mActive.put(f.mIndex, null);
//            f.initState();
//        }
//    }
//
//    public void addFragment(Fragment fragment, boolean moveToStateNow) {
//        if (DEBUG) {
//            Log.v("FragmentManager", "add: " + fragment);
//        }
//
//        this.makeActive(fragment);
//        if (!fragment.mDetached) {
//            if (this.mAdded.contains(fragment)) {
//                throw new IllegalStateException("Fragment already added: " + fragment);
//            }
//
//            synchronized(this.mAdded) {
//                this.mAdded.add(fragment);
//            }
//
//            fragment.mAdded = true;
//            fragment.mRemoving = false;
//            if (fragment.mView == null) {
//                fragment.mHiddenChanged = false;
//            }
//
//            if (fragment.mHasMenu && fragment.mMenuVisible) {
//                this.mNeedMenuInvalidate = true;
//            }
//
//            if (moveToStateNow) {
//                this.moveToState(fragment);
//            }
//        }
//
//    }
//
//    public void removeFragment(Fragment fragment) {
//        if (DEBUG) {
//            Log.v("FragmentManager", "remove: " + fragment + " nesting=" + fragment.mBackStackNesting);
//        }
//
//        boolean inactive = !fragment.isInBackStack();
//        if (!fragment.mDetached || inactive) {
//            synchronized(this.mAdded) {
//                this.mAdded.remove(fragment);
//            }
//
//            if (fragment.mHasMenu && fragment.mMenuVisible) {
//                this.mNeedMenuInvalidate = true;
//            }
//
//            fragment.mAdded = false;
//            fragment.mRemoving = true;
//        }
//
//    }
//
//    public void hideFragment(Fragment fragment) {
//        if (DEBUG) {
//            Log.v("FragmentManager", "hide: " + fragment);
//        }
//
//        if (!fragment.mHidden) {
//            fragment.mHidden = true;
//            fragment.mHiddenChanged = !fragment.mHiddenChanged;
//        }
//
//    }
//
//    public void showFragment(Fragment fragment) {
//        if (DEBUG) {
//            Log.v("FragmentManager", "show: " + fragment);
//        }
//
//        if (fragment.mHidden) {
//            fragment.mHidden = false;
//            fragment.mHiddenChanged = !fragment.mHiddenChanged;
//        }
//
//    }
//
//    public void detachFragment(Fragment fragment) {
//        if (DEBUG) {
//            Log.v("FragmentManager", "detach: " + fragment);
//        }
//
//        if (!fragment.mDetached) {
//            fragment.mDetached = true;
//            if (fragment.mAdded) {
//                if (DEBUG) {
//                    Log.v("FragmentManager", "remove from detach: " + fragment);
//                }
//
//                synchronized(this.mAdded) {
//                    this.mAdded.remove(fragment);
//                }
//
//                if (fragment.mHasMenu && fragment.mMenuVisible) {
//                    this.mNeedMenuInvalidate = true;
//                }
//
//                fragment.mAdded = false;
//            }
//        }
//
//    }
//
//    public void attachFragment(Fragment fragment) {
//        if (DEBUG) {
//            Log.v("FragmentManager", "attach: " + fragment);
//        }
//
//        if (fragment.mDetached) {
//            fragment.mDetached = false;
//            if (!fragment.mAdded) {
//                if (this.mAdded.contains(fragment)) {
//                    throw new IllegalStateException("Fragment already added: " + fragment);
//                }
//
//                if (DEBUG) {
//                    Log.v("FragmentManager", "add from attach: " + fragment);
//                }
//
//                synchronized(this.mAdded) {
//                    this.mAdded.add(fragment);
//                }
//
//                fragment.mAdded = true;
//                if (fragment.mHasMenu && fragment.mMenuVisible) {
//                    this.mNeedMenuInvalidate = true;
//                }
//            }
//        }
//
//    }
//
//    @Nullable
//    public Fragment findFragmentById(int id) {
//        int i;
//        Fragment f;
//        for(i = this.mAdded.size() - 1; i >= 0; --i) {
//            f = (Fragment)this.mAdded.get(i);
//            if (f != null && f.mFragmentId == id) {
//                return f;
//            }
//        }
//
//        if (this.mActive != null) {
//            for(i = this.mActive.size() - 1; i >= 0; --i) {
//                f = (Fragment)this.mActive.valueAt(i);
//                if (f != null && f.mFragmentId == id) {
//                    return f;
//                }
//            }
//        }
//
//        return null;
//    }
//
//    @Nullable
//    public Fragment findFragmentByTag(@Nullable String tag) {
//        int i;
//        Fragment f;
//        if (tag != null) {
//            for(i = this.mAdded.size() - 1; i >= 0; --i) {
//                f = (Fragment)this.mAdded.get(i);
//                if (f != null && tag.equals(f.mTag)) {
//                    return f;
//                }
//            }
//        }
//
//        if (this.mActive != null && tag != null) {
//            for(i = this.mActive.size() - 1; i >= 0; --i) {
//                f = (Fragment)this.mActive.valueAt(i);
//                if (f != null && tag.equals(f.mTag)) {
//                    return f;
//                }
//            }
//        }
//
//        return null;
//    }
//
//    public Fragment findFragmentByWho(String who) {
//        if (this.mActive != null && who != null) {
//            for(int i = this.mActive.size() - 1; i >= 0; --i) {
//                Fragment f = (Fragment)this.mActive.valueAt(i);
//                if (f != null && (f = f.findFragmentByWho(who)) != null) {
//                    return f;
//                }
//            }
//        }
//
//        return null;
//    }
//
//    private void checkStateLoss() {
//        if (this.isStateSaved()) {
//            throw new IllegalStateException("Can not perform this action after onSaveInstanceState");
//        } else if (this.mNoTransactionsBecause != null) {
//            throw new IllegalStateException("Can not perform this action inside of " + this.mNoTransactionsBecause);
//        }
//    }
//
//    public boolean isStateSaved() {
//        return this.mStateSaved || this.mStopped;
//    }
//
//    public void enqueueAction(FragmentManagerImpl.OpGenerator action, boolean allowStateLoss) {
//        if (!allowStateLoss) {
//            this.checkStateLoss();
//        }
//
//        synchronized(this) {
//            if (!this.mDestroyed && this.mHost != null) {
//                if (this.mPendingActions == null) {
//                    this.mPendingActions = new ArrayList();
//                }
//
//                this.mPendingActions.add(action);
//                this.scheduleCommit();
//            } else if (!allowStateLoss) {
//                throw new IllegalStateException("Activity has been destroyed");
//            }
//        }
//    }
//
//    void scheduleCommit() {
//        synchronized(this) {
//            boolean postponeReady = this.mPostponedTransactions != null && !this.mPostponedTransactions.isEmpty();
//            boolean pendingReady = this.mPendingActions != null && this.mPendingActions.size() == 1;
//            if (postponeReady || pendingReady) {
//                this.mHost.getHandler().removeCallbacks(this.mExecCommit);
//                this.mHost.getHandler().post(this.mExecCommit);
//            }
//
//        }
//    }
//
//    public int allocBackStackIndex(BackStackRecord bse) {
//        synchronized(this) {
//            int index;
//            if (this.mAvailBackStackIndices != null && this.mAvailBackStackIndices.size() > 0) {
//                index = (Integer)this.mAvailBackStackIndices.remove(this.mAvailBackStackIndices.size() - 1);
//                if (DEBUG) {
//                    Log.v("FragmentManager", "Adding back stack index " + index + " with " + bse);
//                }
//
//                this.mBackStackIndices.set(index, bse);
//                return index;
//            } else {
//                if (this.mBackStackIndices == null) {
//                    this.mBackStackIndices = new ArrayList();
//                }
//
//                index = this.mBackStackIndices.size();
//                if (DEBUG) {
//                    Log.v("FragmentManager", "Setting back stack index " + index + " to " + bse);
//                }
//
//                this.mBackStackIndices.add(bse);
//                return index;
//            }
//        }
//    }
//
//    public void setBackStackIndex(int index, BackStackRecord bse) {
//        synchronized(this) {
//            if (this.mBackStackIndices == null) {
//                this.mBackStackIndices = new ArrayList();
//            }
//
//            int N = this.mBackStackIndices.size();
//            if (index < N) {
//                if (DEBUG) {
//                    Log.v("FragmentManager", "Setting back stack index " + index + " to " + bse);
//                }
//
//                this.mBackStackIndices.set(index, bse);
//            } else {
//                while(N < index) {
//                    this.mBackStackIndices.add(null);
//                    if (this.mAvailBackStackIndices == null) {
//                        this.mAvailBackStackIndices = new ArrayList();
//                    }
//
//                    if (DEBUG) {
//                        Log.v("FragmentManager", "Adding available back stack index " + N);
//                    }
//
//                    this.mAvailBackStackIndices.add(N);
//                    ++N;
//                }
//
//                if (DEBUG) {
//                    Log.v("FragmentManager", "Adding back stack index " + index + " with " + bse);
//                }
//
//                this.mBackStackIndices.add(bse);
//            }
//
//        }
//    }
//
//    public void freeBackStackIndex(int index) {
//        synchronized(this) {
//            this.mBackStackIndices.set(index, null);
//            if (this.mAvailBackStackIndices == null) {
//                this.mAvailBackStackIndices = new ArrayList();
//            }
//
//            if (DEBUG) {
//                Log.v("FragmentManager", "Freeing back stack index " + index);
//            }
//
//            this.mAvailBackStackIndices.add(index);
//        }
//    }
//
//    private void ensureExecReady(boolean allowStateLoss) {
//        if (this.mExecutingActions) {
//            throw new IllegalStateException("FragmentManager is already executing transactions");
//        } else if (this.mHost == null) {
//            throw new IllegalStateException("Fragment host has been destroyed");
//        } else if (Looper.myLooper() != this.mHost.getHandler().getLooper()) {
//            throw new IllegalStateException("Must be called from main thread of fragment host");
//        } else {
//            if (!allowStateLoss) {
//                this.checkStateLoss();
//            }
//
//            if (this.mTmpRecords == null) {
//                this.mTmpRecords = new ArrayList();
//                this.mTmpIsPop = new ArrayList();
//            }
//
//            this.mExecutingActions = true;
//
//            try {
//                this.executePostponedTransaction((ArrayList)null, (ArrayList)null);
//            } finally {
//                this.mExecutingActions = false;
//            }
//
//        }
//    }
//
//    public void execSingleAction(FragmentManagerImpl.OpGenerator action, boolean allowStateLoss) {
//        if (!allowStateLoss || this.mHost != null && !this.mDestroyed) {
//            this.ensureExecReady(allowStateLoss);
//            if (action.generateOps(this.mTmpRecords, this.mTmpIsPop)) {
//                this.mExecutingActions = true;
//
//                try {
//                    this.removeRedundantOperationsAndExecute(this.mTmpRecords, this.mTmpIsPop);
//                } finally {
//                    this.cleanupExec();
//                }
//            }
//
//            this.doPendingDeferredStart();
//            this.burpActive();
//        }
//    }
//
//    private void cleanupExec() {
//        this.mExecutingActions = false;
//        this.mTmpIsPop.clear();
//        this.mTmpRecords.clear();
//    }
//
//    public boolean execPendingActions() {
//        this.ensureExecReady(true);
//
//        boolean didSomething;
//        for(didSomething = false; this.generateOpsForPendingActions(this.mTmpRecords, this.mTmpIsPop); didSomething = true) {
//            this.mExecutingActions = true;
//
//            try {
//                this.removeRedundantOperationsAndExecute(this.mTmpRecords, this.mTmpIsPop);
//            } finally {
//                this.cleanupExec();
//            }
//        }
//
//        this.doPendingDeferredStart();
//        this.burpActive();
//        return didSomething;
//    }
//
//    private void executePostponedTransaction(ArrayList<BackStackRecord> records, ArrayList<Boolean> isRecordPop) {
//        int numPostponed = this.mPostponedTransactions == null ? 0 : this.mPostponedTransactions.size();
//
//        for(int i = 0; i < numPostponed; ++i) {
//            FragmentManagerImpl.StartEnterTransitionListener listener = (FragmentManagerImpl.StartEnterTransitionListener)this.mPostponedTransactions.get(i);
//            int index;
//            if (records != null && !listener.mIsBack) {
//                index = records.indexOf(listener.mRecord);
//                if (index != -1 && (Boolean)isRecordPop.get(index)) {
//                    listener.cancelTransaction();
//                    continue;
//                }
//            }
//
//            if (listener.isReady() || records != null && listener.mRecord.interactsWith(records, 0, records.size())) {
//                this.mPostponedTransactions.remove(i);
//                --i;
//                --numPostponed;
//                if (records != null && !listener.mIsBack && (index = records.indexOf(listener.mRecord)) != -1 && (Boolean)isRecordPop.get(index)) {
//                    listener.cancelTransaction();
//                } else {
//                    listener.completeTransaction();
//                }
//            }
//        }
//
//    }
//
//    private void removeRedundantOperationsAndExecute(ArrayList<BackStackRecord> records, ArrayList<Boolean> isRecordPop) {
//        if (records != null && !records.isEmpty()) {
//            if (isRecordPop != null && records.size() == isRecordPop.size()) {
//                this.executePostponedTransaction(records, isRecordPop);
//                int numRecords = records.size();
//                int startIndex = 0;
//
//                for(int recordNum = 0; recordNum < numRecords; ++recordNum) {
//                    boolean canReorder = ((BackStackRecord)records.get(recordNum)).mReorderingAllowed;
//                    if (!canReorder) {
//                        if (startIndex != recordNum) {
//                            this.executeOpsTogether(records, isRecordPop, startIndex, recordNum);
//                        }
//
//                        int reorderingEnd = recordNum + 1;
//                        if ((Boolean)isRecordPop.get(recordNum)) {
//                            while(reorderingEnd < numRecords && (Boolean)isRecordPop.get(reorderingEnd) && !((BackStackRecord)records.get(reorderingEnd)).mReorderingAllowed) {
//                                ++reorderingEnd;
//                            }
//                        }
//
//                        this.executeOpsTogether(records, isRecordPop, recordNum, reorderingEnd);
//                        startIndex = reorderingEnd;
//                        recordNum = reorderingEnd - 1;
//                    }
//                }
//
//                if (startIndex != numRecords) {
//                    this.executeOpsTogether(records, isRecordPop, startIndex, numRecords);
//                }
//
//            } else {
//                throw new IllegalStateException("Internal error with the back stack records");
//            }
//        }
//    }
//
//    private void executeOpsTogether(ArrayList<BackStackRecord> records, ArrayList<Boolean> isRecordPop, int startIndex, int endIndex) {
//        boolean allowReordering = ((BackStackRecord)records.get(startIndex)).mReorderingAllowed;
//        boolean addToBackStack = false;
//        if (this.mTmpAddedFragments == null) {
//            this.mTmpAddedFragments = new ArrayList();
//        } else {
//            this.mTmpAddedFragments.clear();
//        }
//
//        this.mTmpAddedFragments.addAll(this.mAdded);
//        Fragment oldPrimaryNav = this.getPrimaryNavigationFragment();
//
//        int postponeIndex;
//        for(postponeIndex = startIndex; postponeIndex < endIndex; ++postponeIndex) {
//            BackStackRecord record = (BackStackRecord)records.get(postponeIndex);
//            boolean isPop = (Boolean)isRecordPop.get(postponeIndex);
//            if (!isPop) {
//                oldPrimaryNav = record.expandOps(this.mTmpAddedFragments, oldPrimaryNav);
//            } else {
//                oldPrimaryNav = record.trackAddedFragmentsInPop(this.mTmpAddedFragments, oldPrimaryNav);
//            }
//
//            addToBackStack = addToBackStack || record.mAddToBackStack;
//        }
//
//        this.mTmpAddedFragments.clear();
//        if (!allowReordering) {
//            FragmentTransition.startTransitions(this, records, isRecordPop, startIndex, endIndex, false);
//        }
//
//        executeOps(records, isRecordPop, startIndex, endIndex);
//        postponeIndex = endIndex;
//        if (allowReordering) {
//            ArraySet<Fragment> addedFragments = new ArraySet();
//            this.addAddedFragments(addedFragments);
//            postponeIndex = this.postponePostponableTransactions(records, isRecordPop, startIndex, endIndex, addedFragments);
//            this.makeRemovedFragmentsInvisible(addedFragments);
//        }
//
//        if (postponeIndex != startIndex && allowReordering) {
//            FragmentTransition.startTransitions(this, records, isRecordPop, startIndex, postponeIndex, true);
//            this.moveToState(this.mCurState, true);
//        }
//
//        for(int recordNum = startIndex; recordNum < endIndex; ++recordNum) {
//            BackStackRecord record = (BackStackRecord)records.get(recordNum);
//            boolean isPop = (Boolean)isRecordPop.get(recordNum);
//            if (isPop && record.mIndex >= 0) {
//                this.freeBackStackIndex(record.mIndex);
//                record.mIndex = -1;
//            }
//
//            record.runOnCommitRunnables();
//        }
//
//        if (addToBackStack) {
//            this.reportBackStackChanged();
//        }
//
//    }
//
//    private void makeRemovedFragmentsInvisible(ArraySet<Fragment> fragments) {
//        int numAdded = fragments.size();
//
//        for(int i = 0; i < numAdded; ++i) {
//            Fragment fragment = (Fragment)fragments.valueAt(i);
//            if (!fragment.mAdded) {
//                View view = fragment.getView();
//                fragment.mPostponedAlpha = view.getAlpha();
//                view.setAlpha(0.0F);
//            }
//        }
//
//    }
//
//    private int postponePostponableTransactions(ArrayList<BackStackRecord> records, ArrayList<Boolean> isRecordPop, int startIndex, int endIndex, ArraySet<Fragment> added) {
//        int postponeIndex = endIndex;
//
//        for(int i = endIndex - 1; i >= startIndex; --i) {
//            BackStackRecord record = (BackStackRecord)records.get(i);
//            boolean isPop = (Boolean)isRecordPop.get(i);
//            boolean isPostponed = record.isPostponed() && !record.interactsWith(records, i + 1, endIndex);
//            if (isPostponed) {
//                if (this.mPostponedTransactions == null) {
//                    this.mPostponedTransactions = new ArrayList();
//                }
//
//                FragmentManagerImpl.StartEnterTransitionListener listener = new FragmentManagerImpl.StartEnterTransitionListener(record, isPop);
//                this.mPostponedTransactions.add(listener);
//                record.setOnStartPostponedListener(listener);
//                if (isPop) {
//                    record.executeOps();
//                } else {
//                    record.executePopOps(false);
//                }
//
//                --postponeIndex;
//                if (i != postponeIndex) {
//                    records.remove(i);
//                    records.add(postponeIndex, record);
//                }
//
//                this.addAddedFragments(added);
//            }
//        }
//
//        return postponeIndex;
//    }
//
//    void completeExecute(BackStackRecord record, boolean isPop, boolean runTransitions, boolean moveToState) {
//        if (isPop) {
//            record.executePopOps(moveToState);
//        } else {
//            record.executeOps();
//        }
//
//        ArrayList<BackStackRecord> records = new ArrayList(1);
//        ArrayList<Boolean> isRecordPop = new ArrayList(1);
//        records.add(record);
//        isRecordPop.add(isPop);
//        if (runTransitions) {
//            FragmentTransition.startTransitions(this, records, isRecordPop, 0, 1, true);
//        }
//
//        if (moveToState) {
//            this.moveToState(this.mCurState, true);
//        }
//
//        if (this.mActive != null) {
//            int numActive = this.mActive.size();
//
//            for(int i = 0; i < numActive; ++i) {
//                Fragment fragment = (Fragment)this.mActive.valueAt(i);
//                if (fragment != null && fragment.mView != null && fragment.mIsNewlyAdded && record.interactsWith(fragment.mContainerId)) {
//                    if (fragment.mPostponedAlpha > 0.0F) {
//                        fragment.mView.setAlpha(fragment.mPostponedAlpha);
//                    }
//
//                    if (moveToState) {
//                        fragment.mPostponedAlpha = 0.0F;
//                    } else {
//                        fragment.mPostponedAlpha = -1.0F;
//                        fragment.mIsNewlyAdded = false;
//                    }
//                }
//            }
//        }
//
//    }
//
//    private Fragment findFragmentUnder(Fragment f) {
//        ViewGroup container = f.mContainer;
//        View view = f.mView;
//        if (container != null && view != null) {
//            int fragmentIndex = this.mAdded.indexOf(f);
//
//            for(int i = fragmentIndex - 1; i >= 0; --i) {
//                Fragment underFragment = (Fragment)this.mAdded.get(i);
//                if (underFragment.mContainer == container && underFragment.mView != null) {
//                    return underFragment;
//                }
//            }
//
//            return null;
//        } else {
//            return null;
//        }
//    }
//
//    private static void executeOps(ArrayList<BackStackRecord> records, ArrayList<Boolean> isRecordPop, int startIndex, int endIndex) {
//        for(int i = startIndex; i < endIndex; ++i) {
//            BackStackRecord record = (BackStackRecord)records.get(i);
//            boolean isPop = (Boolean)isRecordPop.get(i);
//            if (isPop) {
//                record.bumpBackStackNesting(-1);
//                boolean moveToState = i == endIndex - 1;
//                record.executePopOps(moveToState);
//            } else {
//                record.bumpBackStackNesting(1);
//                record.executeOps();
//            }
//        }
//
//    }
//
//    private void addAddedFragments(ArraySet<Fragment> added) {
//        if (this.mCurState >= 1) {
//            int state = Math.min(this.mCurState, 3);
//            int numAdded = this.mAdded.size();
//
//            for(int i = 0; i < numAdded; ++i) {
//                Fragment fragment = (Fragment)this.mAdded.get(i);
//                if (fragment.mState < state) {
//                    this.moveToState(fragment, state, fragment.getNextAnim(), fragment.getNextTransition(), false);
//                    if (fragment.mView != null && !fragment.mHidden && fragment.mIsNewlyAdded) {
//                        added.add(fragment);
//                    }
//                }
//            }
//
//        }
//    }
//
//    private void forcePostponedTransactions() {
//        if (this.mPostponedTransactions != null) {
//            while(!this.mPostponedTransactions.isEmpty()) {
//                ((FragmentManagerImpl.StartEnterTransitionListener)this.mPostponedTransactions.remove(0)).completeTransaction();
//            }
//        }
//
//    }
//
//    private void endAnimatingAwayFragments() {
//        int numFragments = this.mActive == null ? 0 : this.mActive.size();
//
//        for(int i = 0; i < numFragments; ++i) {
//            Fragment fragment = (Fragment)this.mActive.valueAt(i);
//            if (fragment != null) {
//                if (fragment.getAnimatingAway() != null) {
//                    int stateAfterAnimating = fragment.getStateAfterAnimating();
//                    View animatingAway = fragment.getAnimatingAway();
//                    Animation animation = animatingAway.getAnimation();
//                    if (animation != null) {
//                        animation.cancel();
//                        animatingAway.clearAnimation();
//                    }
//
//                    fragment.setAnimatingAway((View)null);
//                    this.moveToState(fragment, stateAfterAnimating, 0, 0, false);
//                } else if (fragment.getAnimator() != null) {
//                    fragment.getAnimator().end();
//                }
//            }
//        }
//
//    }
//
//    private boolean generateOpsForPendingActions(ArrayList<BackStackRecord> records, ArrayList<Boolean> isPop) {
//        boolean didSomething = false;
//        synchronized(this) {
//            if (this.mPendingActions != null && this.mPendingActions.size() != 0) {
//                int numActions = this.mPendingActions.size();
//
//                for(int i = 0; i < numActions; ++i) {
//                    didSomething |= ((FragmentManagerImpl.OpGenerator)this.mPendingActions.get(i)).generateOps(records, isPop);
//                }
//
//                this.mPendingActions.clear();
//                this.mHost.getHandler().removeCallbacks(this.mExecCommit);
//                return didSomething;
//            } else {
//                return false;
//            }
//        }
//    }
//
//    void doPendingDeferredStart() {
//        if (this.mHavePendingDeferredStart) {
//            this.mHavePendingDeferredStart = false;
//            this.startPendingDeferredFragments();
//        }
//
//    }
//
//    void reportBackStackChanged() {
//        if (this.mBackStackChangeListeners != null) {
//            for(int i = 0; i < this.mBackStackChangeListeners.size(); ++i) {
//                ((OnBackStackChangedListener)this.mBackStackChangeListeners.get(i)).onBackStackChanged();
//            }
//        }
//
//    }
//
//    void addBackStackState(BackStackRecord state) {
//        if (this.mBackStack == null) {
//            this.mBackStack = new ArrayList();
//        }
//
//        this.mBackStack.add(state);
//    }
//
//    boolean popBackStackState(ArrayList<BackStackRecord> records, ArrayList<Boolean> isRecordPop, String name, int id, int flags) {
//        if (this.mBackStack == null) {
//            return false;
//        } else {
//            int index;
//            if (name == null && id < 0 && (flags & 1) == 0) {
//                index = this.mBackStack.size() - 1;
//                if (index < 0) {
//                    return false;
//                }
//
//                records.add(this.mBackStack.remove(index));
//                isRecordPop.add(true);
//            } else {
//                index = -1;
//                if (name != null || id >= 0) {
//                    BackStackRecord bss;
//                    for(index = this.mBackStack.size() - 1; index >= 0; --index) {
//                        bss = (BackStackRecord)this.mBackStack.get(index);
//                        if (name != null && name.equals(bss.getName()) || id >= 0 && id == bss.mIndex) {
//                            break;
//                        }
//                    }
//
//                    if (index < 0) {
//                        return false;
//                    }
//
//                    if (flags > 1) {
//                        records.add(this.mBackStack.remove(index));
//                        isRecordPop.add(true);
//                        return true;
//                    }
//
//                    if ((flags & 1) != 0) {
//                        --index;
//
//                        while(index >= 0) {
//                            bss = (BackStackRecord)this.mBackStack.get(index);
//                            if ((name == null || !name.equals(bss.getName())) && (id < 0 || id != bss.mIndex)) {
//                                break;
//                            }
//
//                            --index;
//                        }
//                    }
//                }
//
//                if (index == this.mBackStack.size() - 1) {
//                    return false;
//                }
//
//                for(int i = this.mBackStack.size() - 1; i > index; --i) {
//                    records.add(this.mBackStack.remove(i));
//                    isRecordPop.add(true);
//                }
//            }
//
//            return true;
//        }
//    }
//
//    FragmentManagerNonConfig retainNonConfig() {
//        setRetaining(this.mSavedNonConfig);
//        return this.mSavedNonConfig;
//    }
//
//    private static void setRetaining(FragmentManagerNonConfig nonConfig) {
//        if (nonConfig != null) {
//            List<Fragment> fragments = nonConfig.getFragments();
//            Fragment fragment;
//            if (fragments != null) {
//                for(Iterator var2 = fragments.iterator(); var2.hasNext(); fragment.mRetaining = true) {
//                    fragment = (Fragment)var2.next();
//                }
//            }
//
//            List<FragmentManagerNonConfig> children = nonConfig.getChildNonConfigs();
//            if (children != null) {
//                Iterator var6 = children.iterator();
//
//                while(var6.hasNext()) {
//                    FragmentManagerNonConfig child = (FragmentManagerNonConfig)var6.next();
//                    setRetaining(child);
//                }
//            }
//
//        }
//    }
//
//    void saveNonConfig() {
//        ArrayList<Fragment> fragments = null;
//        ArrayList<FragmentManagerNonConfig> childFragments = null;
//        ArrayList<ViewModelStore> viewModelStores = null;
//        if (this.mActive != null) {
//            for(int i = 0; i < this.mActive.size(); ++i) {
//                Fragment f = (Fragment)this.mActive.valueAt(i);
//                if (f != null) {
//                    if (f.mRetainInstance) {
//                        if (fragments == null) {
//                            fragments = new ArrayList();
//                        }
//
//                        fragments.add(f);
//                        f.mTargetIndex = f.mTarget != null ? f.mTarget.mIndex : -1;
//                        if (DEBUG) {
//                            Log.v("FragmentManager", "retainNonConfig: keeping retained " + f);
//                        }
//                    }
//
//                    FragmentManagerNonConfig child;
//                    if (f.mChildFragmentManager != null) {
//                        f.mChildFragmentManager.saveNonConfig();
//                        child = f.mChildFragmentManager.mSavedNonConfig;
//                    } else {
//                        child = f.mChildNonConfig;
//                    }
//
//                    int j;
//                    if (childFragments == null && child != null) {
//                        childFragments = new ArrayList(this.mActive.size());
//
//                        for(j = 0; j < i; ++j) {
//                            childFragments.add(null);
//                        }
//                    }
//
//                    if (childFragments != null) {
//                        childFragments.add(child);
//                    }
//
//                    if (viewModelStores == null && f.mViewModelStore != null) {
//                        viewModelStores = new ArrayList(this.mActive.size());
//
//                        for(j = 0; j < i; ++j) {
//                            viewModelStores.add(null);
//                        }
//                    }
//
//                    if (viewModelStores != null) {
//                        viewModelStores.add(f.mViewModelStore);
//                    }
//                }
//            }
//        }
//
//        if (fragments == null && childFragments == null && viewModelStores == null) {
//            this.mSavedNonConfig = null;
//        } else {
//            this.mSavedNonConfig = new FragmentManagerNonConfig(fragments, childFragments, viewModelStores);
//        }
//
//    }
//
//    void saveFragmentViewState(Fragment f) {
//        if (f.mInnerView != null) {
//            if (this.mStateArray == null) {
//                this.mStateArray = new SparseArray();
//            } else {
//                this.mStateArray.clear();
//            }
//
//            f.mInnerView.saveHierarchyState(this.mStateArray);
//            if (this.mStateArray.size() > 0) {
//                f.mSavedViewState = this.mStateArray;
//                this.mStateArray = null;
//            }
//
//        }
//    }
//
//    Bundle saveFragmentBasicState(Fragment f) {
//        Bundle result = null;
//        if (this.mStateBundle == null) {
//            this.mStateBundle = new Bundle();
//        }
//
//        f.performSaveInstanceState(this.mStateBundle);
//        this.dispatchOnFragmentSaveInstanceState(f, this.mStateBundle, false);
//        if (!this.mStateBundle.isEmpty()) {
//            result = this.mStateBundle;
//            this.mStateBundle = null;
//        }
//
//        if (f.mView != null) {
//            this.saveFragmentViewState(f);
//        }
//
//        if (f.mSavedViewState != null) {
//            if (result == null) {
//                result = new Bundle();
//            }
//
//            result.putSparseParcelableArray("android:view_state", f.mSavedViewState);
//        }
//
//        if (!f.mUserVisibleHint) {
//            if (result == null) {
//                result = new Bundle();
//            }
//
//            result.putBoolean("android:user_visible_hint", f.mUserVisibleHint);
//        }
//
//        return result;
//    }
//
//    Parcelable saveAllState() {
//        this.forcePostponedTransactions();
//        this.endAnimatingAwayFragments();
//        this.execPendingActions();
//        this.mStateSaved = true;
//        this.mSavedNonConfig = null;
//        if (this.mActive != null && this.mActive.size() > 0) {
//            int N = this.mActive.size();
//            FragmentState[] active = new FragmentState[N];
//            boolean haveFragments = false;
//
//            for(int i = 0; i < N; ++i) {
//                Fragment f = (Fragment)this.mActive.valueAt(i);
//                if (f != null) {
//                    if (f.mIndex < 0) {
//                        this.throwException(new IllegalStateException("Failure saving state: active " + f + " has cleared index: " + f.mIndex));
//                    }
//
//                    haveFragments = true;
//                    FragmentState fs = new FragmentState(f);
//                    active[i] = fs;
//                    if (f.mState > 0 && fs.mSavedFragmentState == null) {
//                        fs.mSavedFragmentState = this.saveFragmentBasicState(f);
//                        if (f.mTarget != null) {
//                            if (f.mTarget.mIndex < 0) {
//                                this.throwException(new IllegalStateException("Failure saving state: " + f + " has target not in fragment manager: " + f.mTarget));
//                            }
//
//                            if (fs.mSavedFragmentState == null) {
//                                fs.mSavedFragmentState = new Bundle();
//                            }
//
//                            this.putFragment(fs.mSavedFragmentState, "android:target_state", f.mTarget);
//                            if (f.mTargetRequestCode != 0) {
//                                fs.mSavedFragmentState.putInt("android:target_req_state", f.mTargetRequestCode);
//                            }
//                        }
//                    } else {
//                        fs.mSavedFragmentState = f.mSavedFragmentState;
//                    }
//
//                    if (DEBUG) {
//                        Log.v("FragmentManager", "Saved state of " + f + ": " + fs.mSavedFragmentState);
//                    }
//                }
//            }
//
//            if (!haveFragments) {
//                if (DEBUG) {
//                    Log.v("FragmentManager", "saveAllState: no fragments!");
//                }
//
//                return null;
//            } else {
//                int[] added = null;
//                BackStackState[] backStack = null;
//                N = this.mAdded.size();
//                int i;
//                if (N > 0) {
//                    added = new int[N];
//
//                    for(i = 0; i < N; ++i) {
//                        added[i] = ((Fragment)this.mAdded.get(i)).mIndex;
//                        if (added[i] < 0) {
//                            this.throwException(new IllegalStateException("Failure saving state: active " + this.mAdded.get(i) + " has cleared index: " + added[i]));
//                        }
//
//                        if (DEBUG) {
//                            Log.v("FragmentManager", "saveAllState: adding fragment #" + i + ": " + this.mAdded.get(i));
//                        }
//                    }
//                }
//
//                if (this.mBackStack != null) {
//                    N = this.mBackStack.size();
//                    if (N > 0) {
//                        backStack = new BackStackState[N];
//
//                        for(i = 0; i < N; ++i) {
//                            backStack[i] = new BackStackState((BackStackRecord)this.mBackStack.get(i));
//                            if (DEBUG) {
//                                Log.v("FragmentManager", "saveAllState: adding back stack #" + i + ": " + this.mBackStack.get(i));
//                            }
//                        }
//                    }
//                }
//
//                FragmentManagerState fms = new FragmentManagerState();
//                fms.mActive = active;
//                fms.mAdded = added;
//                fms.mBackStack = backStack;
//                if (this.mPrimaryNav != null) {
//                    fms.mPrimaryNavActiveIndex = this.mPrimaryNav.mIndex;
//                }
//
//                fms.mNextFragmentIndex = this.mNextFragmentIndex;
//                this.saveNonConfig();
//                return fms;
//            }
//        } else {
//            return null;
//        }
//    }
//
//    void restoreAllState(Parcelable state, FragmentManagerNonConfig nonConfig) {
//        if (state != null) {
//            FragmentManagerState fms = (FragmentManagerState)state;
//            if (fms.mActive != null) {
//                List<FragmentManagerNonConfig> childNonConfigs = null;
//                List<ViewModelStore> viewModelStores = null;
//                List nonConfigFragments;
//                int count;
//                int i;
//                Fragment f;
//                if (nonConfig != null) {
//                    nonConfigFragments = nonConfig.getFragments();
//                    childNonConfigs = nonConfig.getChildNonConfigs();
//                    viewModelStores = nonConfig.getViewModelStores();
//                    count = nonConfigFragments != null ? nonConfigFragments.size() : 0;
//
//                    for(i = 0; i < count; ++i) {
//                        f = (Fragment)nonConfigFragments.get(i);
//                        if (DEBUG) {
//                            Log.v("FragmentManager", "restoreAllState: re-attaching retained " + f);
//                        }
//
//                        int index;
//                        for(index = 0; index < fms.mActive.length && fms.mActive[index].mIndex != f.mIndex; ++index) {
//                        }
//
//                        if (index == fms.mActive.length) {
//                            this.throwException(new IllegalStateException("Could not find active fragment with index " + f.mIndex));
//                        }
//
//                        FragmentState fs = fms.mActive[index];
//                        fs.mInstance = f;
//                        f.mSavedViewState = null;
//                        f.mBackStackNesting = 0;
//                        f.mInLayout = false;
//                        f.mAdded = false;
//                        f.mTarget = null;
//                        if (fs.mSavedFragmentState != null) {
//                            fs.mSavedFragmentState.setClassLoader(this.mHost.getContext().getClassLoader());
//                            f.mSavedViewState = fs.mSavedFragmentState.getSparseParcelableArray("android:view_state");
//                            f.mSavedFragmentState = fs.mSavedFragmentState;
//                        }
//                    }
//                }
//
//                this.mActive = new SparseArray(fms.mActive.length);
//
//
//                for(i = 0; i < fms.mActive.length; ++i) {
//                    FragmentState fs = fms.mActive[i];
//                    if (fs != null) {
//                        FragmentManagerNonConfig childNonConfig = null;
//                        if (childNonConfigs != null && i < childNonConfigs.size()) {
//                            childNonConfig = (FragmentManagerNonConfig)childNonConfigs.get(i);
//                        }
//
//                        ViewModelStore viewModelStore = null;
//                        if (viewModelStores != null && i < viewModelStores.size()) {
//                            viewModelStore = (ViewModelStore)viewModelStores.get(i);
//                        }
//
//                        Fragment fragment = fs.instantiate(this.mHost, this.mContainer, this.mParent, childNonConfig, viewModelStore);
//                        if (DEBUG) {
//                            Log.v("FragmentManager", "restoreAllState: active #" + i + ": " + fragment);
//                        }
//
//                        this.mActive.put(fragment.mIndex, fragment);
//                        fs.mInstance = null;
//                    }
//                }
//
//                if (nonConfig != null) {
//                    nonConfigFragments = nonConfig.getFragments();
//                    count = nonConfigFragments != null ? nonConfigFragments.size() : 0;
//
//                    for(i = 0; i < count; ++i) {
//                        f = (Fragment)nonConfigFragments.get(i);
//                        if (f.mTargetIndex >= 0) {
//                            f.mTarget = (Fragment)this.mActive.get(f.mTargetIndex);
//                            if (f.mTarget == null) {
//                                Log.w("FragmentManager", "Re-attaching retained fragment " + f + " target no longer exists: " + f.mTargetIndex);
//                            }
//                        }
//                    }
//                }
//
//                this.mAdded.clear();
//                if (fms.mAdded != null) {
//                    for(i = 0; i < fms.mAdded.length; ++i) {
//                        Fragment fragment = (Fragment)this.mActive.get(fms.mAdded[i]);
//                        if (fragment == null) {
//                            this.throwException(new IllegalStateException("No instantiated fragment for index #" + fms.mAdded[i]));
//                        }
//
//                        fragment.mAdded = true;
//                        if (DEBUG) {
//                            Log.v("FragmentManager", "restoreAllState: added #" + i + ": " + fragment);
//                        }
//
//                        if (this.mAdded.contains(fragment)) {
//                            throw new IllegalStateException("Already added!");
//                        }
//
//                        synchronized(this.mAdded) {
//                            this.mAdded.add(fragment);
//                        }
//                    }
//                }
//
//                if (fms.mBackStack != null) {
//                    this.mBackStack = new ArrayList(fms.mBackStack.length);
//
//                    for(i = 0; i < fms.mBackStack.length; ++i) {
//                        BackStackRecord bse = fms.mBackStack[i].instantiate(this);
//                        if (DEBUG) {
//                            Log.v("FragmentManager", "restoreAllState: back stack #" + i + " (index " + bse.mIndex + "): " + bse);
//                            LogWriter logw = new LogWriter("FragmentManager");
//                            PrintWriter pw = new PrintWriter(logw);
//                            bse.dump("  ", pw, false);
//                            pw.close();
//                        }
//
//                        this.mBackStack.add(bse);
//                        if (bse.mIndex >= 0) {
//                            this.setBackStackIndex(bse.mIndex, bse);
//                        }
//                    }
//                } else {
//                    this.mBackStack = null;
//                }
//
//                if (fms.mPrimaryNavActiveIndex >= 0) {
//                    this.mPrimaryNav = (Fragment)this.mActive.get(fms.mPrimaryNavActiveIndex);
//                }
//
//                this.mNextFragmentIndex = fms.mNextFragmentIndex;
//            }
//        }
//    }
//
//    private void burpActive() {
//        if (this.mActive != null) {
//            for(int i = this.mActive.size() - 1; i >= 0; --i) {
//                if (this.mActive.valueAt(i) == null) {
//                    this.mActive.delete(this.mActive.keyAt(i));
//                }
//            }
//        }
//
//    }
//
//    public void attachController(FragmentHostCallback host, FragmentContainer container, Fragment parent) {
//        if (this.mHost != null) {
//            throw new IllegalStateException("Already attached");
//        } else {
//            this.mHost = host;
//            this.mContainer = container;
//            this.mParent = parent;
//        }
//    }
//
//    public void noteStateNotSaved() {
//        this.mSavedNonConfig = null;
//        this.mStateSaved = false;
//        this.mStopped = false;
//        int addedCount = this.mAdded.size();
//
//        for(int i = 0; i < addedCount; ++i) {
//            Fragment fragment = (Fragment)this.mAdded.get(i);
//            if (fragment != null) {
//                fragment.noteStateNotSaved();
//            }
//        }
//
//    }
//
//    public void dispatchCreate() {
//        this.mStateSaved = false;
//        this.mStopped = false;
//        this.dispatchStateChange(1);
//    }
//
//    public void dispatchActivityCreated() {
//        this.mStateSaved = false;
//        this.mStopped = false;
//        this.dispatchStateChange(2);
//    }
//
//    public void dispatchStart() {
//        this.mStateSaved = false;
//        this.mStopped = false;
//        this.dispatchStateChange(3);
//    }
//
//    public void dispatchResume() {
//        this.mStateSaved = false;
//        this.mStopped = false;
//        this.dispatchStateChange(4);
//    }
//
//    public void dispatchPause() {
//        this.dispatchStateChange(3);
//    }
//
//    public void dispatchStop() {
//        this.mStopped = true;
//        this.dispatchStateChange(2);
//    }
//
//    public void dispatchDestroyView() {
//        this.dispatchStateChange(1);
//    }
//
//    public void dispatchDestroy() {
//        this.mDestroyed = true;
//        this.execPendingActions();
//        this.dispatchStateChange(0);
//        this.mHost = null;
//        this.mContainer = null;
//        this.mParent = null;
//    }
//
//    private void dispatchStateChange(int nextState) {
//        try {
//            this.mExecutingActions = true;
//            this.moveToState(nextState, false);
//        } finally {
//            this.mExecutingActions = false;
//        }
//
//        this.execPendingActions();
//    }
//
//    public void dispatchMultiWindowModeChanged(boolean isInMultiWindowMode) {
//        for(int i = this.mAdded.size() - 1; i >= 0; --i) {
//            Fragment f = (Fragment)this.mAdded.get(i);
//            if (f != null) {
//                f.performMultiWindowModeChanged(isInMultiWindowMode);
//            }
//        }
//
//    }
//
//    public void dispatchPictureInPictureModeChanged(boolean isInPictureInPictureMode) {
//        for(int i = this.mAdded.size() - 1; i >= 0; --i) {
//            Fragment f = (Fragment)this.mAdded.get(i);
//            if (f != null) {
//                f.performPictureInPictureModeChanged(isInPictureInPictureMode);
//            }
//        }
//
//    }
//
//    public void dispatchConfigurationChanged(Configuration newConfig) {
//        for(int i = 0; i < this.mAdded.size(); ++i) {
//            Fragment f = (Fragment)this.mAdded.get(i);
//            if (f != null) {
//                f.performConfigurationChanged(newConfig);
//            }
//        }
//
//    }
//
//    public void dispatchLowMemory() {
//        for(int i = 0; i < this.mAdded.size(); ++i) {
//            Fragment f = (Fragment)this.mAdded.get(i);
//            if (f != null) {
//                f.performLowMemory();
//            }
//        }
//
//    }
//
//    public boolean dispatchCreateOptionsMenu(Menu menu, MenuInflater inflater) {
//        if (this.mCurState < 1) {
//            return false;
//        } else {
//            boolean show = false;
//            ArrayList<Fragment> newMenus = null;
//
//            int i;
//            Fragment f;
//            for(i = 0; i < this.mAdded.size(); ++i) {
//                f = (Fragment)this.mAdded.get(i);
//                if (f != null && f.performCreateOptionsMenu(menu, inflater)) {
//                    show = true;
//                    if (newMenus == null) {
//                        newMenus = new ArrayList();
//                    }
//
//                    newMenus.add(f);
//                }
//            }
//
//            if (this.mCreatedMenus != null) {
//                for(i = 0; i < this.mCreatedMenus.size(); ++i) {
//                    f = (Fragment)this.mCreatedMenus.get(i);
//                    if (newMenus == null || !newMenus.contains(f)) {
//                        f.onDestroyOptionsMenu();
//                    }
//                }
//            }
//
//            this.mCreatedMenus = newMenus;
//            return show;
//        }
//    }
//
//    public boolean dispatchPrepareOptionsMenu(Menu menu) {
//        if (this.mCurState < 1) {
//            return false;
//        } else {
//            boolean show = false;
//
//            for(int i = 0; i < this.mAdded.size(); ++i) {
//                Fragment f = (Fragment)this.mAdded.get(i);
//                if (f != null && f.performPrepareOptionsMenu(menu)) {
//                    show = true;
//                }
//            }
//
//            return show;
//        }
//    }
//
//    public boolean dispatchOptionsItemSelected(MenuItem item) {
//        if (this.mCurState < 1) {
//            return false;
//        } else {
//            for(int i = 0; i < this.mAdded.size(); ++i) {
//                Fragment f = (Fragment)this.mAdded.get(i);
//                if (f != null && f.performOptionsItemSelected(item)) {
//                    return true;
//                }
//            }
//
//            return false;
//        }
//    }
//
//    public boolean dispatchContextItemSelected(MenuItem item) {
//        if (this.mCurState < 1) {
//            return false;
//        } else {
//            for(int i = 0; i < this.mAdded.size(); ++i) {
//                Fragment f = (Fragment)this.mAdded.get(i);
//                if (f != null && f.performContextItemSelected(item)) {
//                    return true;
//                }
//            }
//
//            return false;
//        }
//    }
//
//    public void dispatchOptionsMenuClosed(Menu menu) {
//        if (this.mCurState >= 1) {
//            for(int i = 0; i < this.mAdded.size(); ++i) {
//                Fragment f = (Fragment)this.mAdded.get(i);
//                if (f != null) {
//                    f.performOptionsMenuClosed(menu);
//                }
//            }
//
//        }
//    }
//
//    public void setPrimaryNavigationFragment(Fragment f) {
//        if (f == null || this.mActive.get(f.mIndex) == f && (f.mHost == null || f.getFragmentManager() == this)) {
//            this.mPrimaryNav = f;
//        } else {
//            throw new IllegalArgumentException("Fragment " + f + " is not an active fragment of FragmentManager " + this);
//        }
//    }
//
//    @Nullable
//    public Fragment getPrimaryNavigationFragment() {
//        return this.mPrimaryNav;
//    }
//
//    public void registerFragmentLifecycleCallbacks(FragmentLifecycleCallbacks cb, boolean recursive) {
//        this.mLifecycleCallbacks.add(new FragmentManagerImpl.FragmentLifecycleCallbacksHolder(cb, recursive));
//    }
//
//    public void unregisterFragmentLifecycleCallbacks(FragmentLifecycleCallbacks cb) {
//        synchronized(this.mLifecycleCallbacks) {
//            int i = 0;
//
//            for(int N = this.mLifecycleCallbacks.size(); i < N; ++i) {
//                if (((FragmentManagerImpl.FragmentLifecycleCallbacksHolder)this.mLifecycleCallbacks.get(i)).mCallback == cb) {
//                    this.mLifecycleCallbacks.remove(i);
//                    break;
//                }
//            }
//
//        }
//    }
//
//    void dispatchOnFragmentPreAttached(@NonNull Fragment f, @NonNull Context context, boolean onlyRecursive) {
//        if (this.mParent != null) {
//            FragmentManager parentManager = this.mParent.getFragmentManager();
//            if (parentManager instanceof FragmentManagerImpl) {
//                ((FragmentManagerImpl)parentManager).dispatchOnFragmentPreAttached(f, context, true);
//            }
//        }
//
//        Iterator var6 = this.mLifecycleCallbacks.iterator();
//
//        while(true) {
//            FragmentManagerImpl.FragmentLifecycleCallbacksHolder holder;
//            do {
//                if (!var6.hasNext()) {
//                    return;
//                }
//
//                holder = (FragmentManagerImpl.FragmentLifecycleCallbacksHolder)var6.next();
//            } while(onlyRecursive && !holder.mRecursive);
//
//            holder.mCallback.onFragmentPreAttached(this, f, context);
//        }
//    }
//
//    void dispatchOnFragmentAttached(@NonNull Fragment f, @NonNull Context context, boolean onlyRecursive) {
//        if (this.mParent != null) {
//            FragmentManager parentManager = this.mParent.getFragmentManager();
//            if (parentManager instanceof FragmentManagerImpl) {
//                ((FragmentManagerImpl)parentManager).dispatchOnFragmentAttached(f, context, true);
//            }
//        }
//
//        Iterator var6 = this.mLifecycleCallbacks.iterator();
//
//        while(true) {
//            FragmentManagerImpl.FragmentLifecycleCallbacksHolder holder;
//            do {
//                if (!var6.hasNext()) {
//                    return;
//                }
//
//                holder = (FragmentManagerImpl.FragmentLifecycleCallbacksHolder)var6.next();
//            } while(onlyRecursive && !holder.mRecursive);
//
//            holder.mCallback.onFragmentAttached(this, f, context);
//        }
//    }
//
//    void dispatchOnFragmentPreCreated(@NonNull Fragment f, @Nullable Bundle savedInstanceState, boolean onlyRecursive) {
//        if (this.mParent != null) {
//            FragmentManager parentManager = this.mParent.getFragmentManager();
//            if (parentManager instanceof FragmentManagerImpl) {
//                ((FragmentManagerImpl)parentManager).dispatchOnFragmentPreCreated(f, savedInstanceState, true);
//            }
//        }
//
//        Iterator var6 = this.mLifecycleCallbacks.iterator();
//
//        while(true) {
//            FragmentManagerImpl.FragmentLifecycleCallbacksHolder holder;
//            do {
//                if (!var6.hasNext()) {
//                    return;
//                }
//
//                holder = (FragmentManagerImpl.FragmentLifecycleCallbacksHolder)var6.next();
//            } while(onlyRecursive && !holder.mRecursive);
//
//            holder.mCallback.onFragmentPreCreated(this, f, savedInstanceState);
//        }
//    }
//
//    void dispatchOnFragmentCreated(@NonNull Fragment f, @Nullable Bundle savedInstanceState, boolean onlyRecursive) {
//        if (this.mParent != null) {
//            FragmentManager parentManager = this.mParent.getFragmentManager();
//            if (parentManager instanceof FragmentManagerImpl) {
//                ((FragmentManagerImpl)parentManager).dispatchOnFragmentCreated(f, savedInstanceState, true);
//            }
//        }
//
//        Iterator var6 = this.mLifecycleCallbacks.iterator();
//
//        while(true) {
//            FragmentManagerImpl.FragmentLifecycleCallbacksHolder holder;
//            do {
//                if (!var6.hasNext()) {
//                    return;
//                }
//
//                holder = (FragmentManagerImpl.FragmentLifecycleCallbacksHolder)var6.next();
//            } while(onlyRecursive && !holder.mRecursive);
//
//            holder.mCallback.onFragmentCreated(this, f, savedInstanceState);
//        }
//    }
//
//    void dispatchOnFragmentActivityCreated(@NonNull Fragment f, @Nullable Bundle savedInstanceState, boolean onlyRecursive) {
//        if (this.mParent != null) {
//            FragmentManager parentManager = this.mParent.getFragmentManager();
//            if (parentManager instanceof FragmentManagerImpl) {
//                ((FragmentManagerImpl)parentManager).dispatchOnFragmentActivityCreated(f, savedInstanceState, true);
//            }
//        }
//
//        Iterator var6 = this.mLifecycleCallbacks.iterator();
//
//        while(true) {
//            FragmentManagerImpl.FragmentLifecycleCallbacksHolder holder;
//            do {
//                if (!var6.hasNext()) {
//                    return;
//                }
//
//                holder = (FragmentManagerImpl.FragmentLifecycleCallbacksHolder)var6.next();
//            } while(onlyRecursive && !holder.mRecursive);
//
//            holder.mCallback.onFragmentActivityCreated(this, f, savedInstanceState);
//        }
//    }
//
//    void dispatchOnFragmentViewCreated(@NonNull Fragment f, @NonNull View v, @Nullable Bundle savedInstanceState, boolean onlyRecursive) {
//        if (this.mParent != null) {
//            FragmentManager parentManager = this.mParent.getFragmentManager();
//            if (parentManager instanceof FragmentManagerImpl) {
//                ((FragmentManagerImpl)parentManager).dispatchOnFragmentViewCreated(f, v, savedInstanceState, true);
//            }
//        }
//
//        Iterator var7 = this.mLifecycleCallbacks.iterator();
//
//        while(true) {
//            FragmentManagerImpl.FragmentLifecycleCallbacksHolder holder;
//            do {
//                if (!var7.hasNext()) {
//                    return;
//                }
//
//                holder = (FragmentManagerImpl.FragmentLifecycleCallbacksHolder)var7.next();
//            } while(onlyRecursive && !holder.mRecursive);
//
//            holder.mCallback.onFragmentViewCreated(this, f, v, savedInstanceState);
//        }
//    }
//
//    void dispatchOnFragmentStarted(@NonNull Fragment f, boolean onlyRecursive) {
//        if (this.mParent != null) {
//            FragmentManager parentManager = this.mParent.getFragmentManager();
//            if (parentManager instanceof FragmentManagerImpl) {
//                ((FragmentManagerImpl)parentManager).dispatchOnFragmentStarted(f, true);
//            }
//        }
//
//        Iterator var5 = this.mLifecycleCallbacks.iterator();
//
//        while(true) {
//            FragmentManagerImpl.FragmentLifecycleCallbacksHolder holder;
//            do {
//                if (!var5.hasNext()) {
//                    return;
//                }
//
//                holder = (FragmentManagerImpl.FragmentLifecycleCallbacksHolder)var5.next();
//            } while(onlyRecursive && !holder.mRecursive);
//
//            holder.mCallback.onFragmentStarted(this, f);
//        }
//    }
//
//    void dispatchOnFragmentResumed(@NonNull Fragment f, boolean onlyRecursive) {
//        if (this.mParent != null) {
//            FragmentManager parentManager = this.mParent.getFragmentManager();
//            if (parentManager instanceof FragmentManagerImpl) {
//                ((FragmentManagerImpl)parentManager).dispatchOnFragmentResumed(f, true);
//            }
//        }
//
//        Iterator var5 = this.mLifecycleCallbacks.iterator();
//
//        while(true) {
//            FragmentManagerImpl.FragmentLifecycleCallbacksHolder holder;
//            do {
//                if (!var5.hasNext()) {
//                    return;
//                }
//
//                holder = (FragmentManagerImpl.FragmentLifecycleCallbacksHolder)var5.next();
//            } while(onlyRecursive && !holder.mRecursive);
//
//            holder.mCallback.onFragmentResumed(this, f);
//        }
//    }
//
//    void dispatchOnFragmentPaused(@NonNull Fragment f, boolean onlyRecursive) {
//        if (this.mParent != null) {
//            FragmentManager parentManager = this.mParent.getFragmentManager();
//            if (parentManager instanceof FragmentManagerImpl) {
//                ((FragmentManagerImpl)parentManager).dispatchOnFragmentPaused(f, true);
//            }
//        }
//
//        Iterator var5 = this.mLifecycleCallbacks.iterator();
//
//        while(true) {
//            FragmentManagerImpl.FragmentLifecycleCallbacksHolder holder;
//            do {
//                if (!var5.hasNext()) {
//                    return;
//                }
//
//                holder = (FragmentManagerImpl.FragmentLifecycleCallbacksHolder)var5.next();
//            } while(onlyRecursive && !holder.mRecursive);
//
//            holder.mCallback.onFragmentPaused(this, f);
//        }
//    }
//
//    void dispatchOnFragmentStopped(@NonNull Fragment f, boolean onlyRecursive) {
//        if (this.mParent != null) {
//            FragmentManager parentManager = this.mParent.getFragmentManager();
//            if (parentManager instanceof FragmentManagerImpl) {
//                ((FragmentManagerImpl)parentManager).dispatchOnFragmentStopped(f, true);
//            }
//        }
//
//        Iterator var5 = this.mLifecycleCallbacks.iterator();
//
//        while(true) {
//            FragmentManagerImpl.FragmentLifecycleCallbacksHolder holder;
//            do {
//                if (!var5.hasNext()) {
//                    return;
//                }
//
//                holder = (FragmentManagerImpl.FragmentLifecycleCallbacksHolder)var5.next();
//            } while(onlyRecursive && !holder.mRecursive);
//
//            holder.mCallback.onFragmentStopped(this, f);
//        }
//    }
//
//    void dispatchOnFragmentSaveInstanceState(@NonNull Fragment f, @NonNull Bundle outState, boolean onlyRecursive) {
//        if (this.mParent != null) {
//            FragmentManager parentManager = this.mParent.getFragmentManager();
//            if (parentManager instanceof FragmentManagerImpl) {
//                ((FragmentManagerImpl)parentManager).dispatchOnFragmentSaveInstanceState(f, outState, true);
//            }
//        }
//
//        Iterator var6 = this.mLifecycleCallbacks.iterator();
//
//        while(true) {
//            FragmentManagerImpl.FragmentLifecycleCallbacksHolder holder;
//            do {
//                if (!var6.hasNext()) {
//                    return;
//                }
//
//                holder = (FragmentManagerImpl.FragmentLifecycleCallbacksHolder)var6.next();
//            } while(onlyRecursive && !holder.mRecursive);
//
//            holder.mCallback.onFragmentSaveInstanceState(this, f, outState);
//        }
//    }
//
//    void dispatchOnFragmentViewDestroyed(@NonNull Fragment f, boolean onlyRecursive) {
//        if (this.mParent != null) {
//            FragmentManager parentManager = this.mParent.getFragmentManager();
//            if (parentManager instanceof FragmentManagerImpl) {
//                ((FragmentManagerImpl)parentManager).dispatchOnFragmentViewDestroyed(f, true);
//            }
//        }
//
//        Iterator var5 = this.mLifecycleCallbacks.iterator();
//
//        while(true) {
//            FragmentManagerImpl.FragmentLifecycleCallbacksHolder holder;
//            do {
//                if (!var5.hasNext()) {
//                    return;
//                }
//
//                holder = (FragmentManagerImpl.FragmentLifecycleCallbacksHolder)var5.next();
//            } while(onlyRecursive && !holder.mRecursive);
//
//            holder.mCallback.onFragmentViewDestroyed(this, f);
//        }
//    }
//
//    void dispatchOnFragmentDestroyed(@NonNull Fragment f, boolean onlyRecursive) {
//        if (this.mParent != null) {
//            FragmentManager parentManager = this.mParent.getFragmentManager();
//            if (parentManager instanceof FragmentManagerImpl) {
//                ((FragmentManagerImpl)parentManager).dispatchOnFragmentDestroyed(f, true);
//            }
//        }
//
//        Iterator var5 = this.mLifecycleCallbacks.iterator();
//
//        while(true) {
//            FragmentManagerImpl.FragmentLifecycleCallbacksHolder holder;
//            do {
//                if (!var5.hasNext()) {
//                    return;
//                }
//
//                holder = (FragmentManagerImpl.FragmentLifecycleCallbacksHolder)var5.next();
//            } while(onlyRecursive && !holder.mRecursive);
//
//            holder.mCallback.onFragmentDestroyed(this, f);
//        }
//    }
//
//    void dispatchOnFragmentDetached(@NonNull Fragment f, boolean onlyRecursive) {
//        if (this.mParent != null) {
//            FragmentManager parentManager = this.mParent.getFragmentManager();
//            if (parentManager instanceof FragmentManagerImpl) {
//                ((FragmentManagerImpl)parentManager).dispatchOnFragmentDetached(f, true);
//            }
//        }
//
//        Iterator var5 = this.mLifecycleCallbacks.iterator();
//
//        while(true) {
//            FragmentManagerImpl.FragmentLifecycleCallbacksHolder holder;
//            do {
//                if (!var5.hasNext()) {
//                    return;
//                }
//
//                holder = (FragmentManagerImpl.FragmentLifecycleCallbacksHolder)var5.next();
//            } while(onlyRecursive && !holder.mRecursive);
//
//            holder.mCallback.onFragmentDetached(this, f);
//        }
//    }
//
//    public static int reverseTransit(int transit) {
//        int rev = 0;
//        switch(transit) {
//            case 4097:
//                rev = 8194;
//                break;
//            case 4099:
//                rev = 4099;
//                break;
//            case 8194:
//                rev = 4097;
//        }
//
//        return rev;
//    }
//
//    public static int transitToStyleIndex(int transit, boolean enter) {
//        int animAttr = -1;
//        switch(transit) {
//            case 4097:
//                animAttr = enter ? 1 : 2;
//                break;
//            case 4099:
//                animAttr = enter ? 5 : 6;
//                break;
//            case 8194:
//                animAttr = enter ? 3 : 4;
//        }
//
//        return animAttr;
//    }
//
//    public View onCreateView(View parent, String name, Context context, AttributeSet attrs) {
//        if (!"fragment".equals(name)) {
//            return null;
//        } else {
//            String fname = attrs.getAttributeValue((String)null, "class");
//            TypedArray a = context.obtainStyledAttributes(attrs, FragmentManagerImpl.FragmentTag.Fragment);
//            if (fname == null) {
//                fname = a.getString(0);
//            }
//
//            int id = a.getResourceId(1, -1);
//            String tag = a.getString(2);
//            a.recycle();
//            if (!Fragment.isSupportFragmentClass(this.mHost.getContext(), fname)) {
//                return null;
//            } else {
//                int containerId = parent != null ? parent.getId() : 0;
//                if (containerId == -1 && id == -1 && tag == null) {
//                    throw new IllegalArgumentException(attrs.getPositionDescription() + ": Must specify unique android:id, android:tag, or have a parent with an id for " + fname);
//                } else {
//                    Fragment fragment = id != -1 ? this.findFragmentById(id) : null;
//                    if (fragment == null && tag != null) {
//                        fragment = this.findFragmentByTag(tag);
//                    }
//
//                    if (fragment == null && containerId != -1) {
//                        fragment = this.findFragmentById(containerId);
//                    }
//
//                    if (DEBUG) {
//                        Log.v("FragmentManager", "onCreateView: id=0x" + Integer.toHexString(id) + " fname=" + fname + " existing=" + fragment);
//                    }
//
//                    if (fragment == null) {
//                        fragment = this.mContainer.instantiate(context, fname, (Bundle)null);
//                        fragment.mFromLayout = true;
//                        fragment.mFragmentId = id != 0 ? id : containerId;
//                        fragment.mContainerId = containerId;
//                        fragment.mTag = tag;
//                        fragment.mInLayout = true;
//                        fragment.mFragmentManager = this;
//                        fragment.mHost = this.mHost;
//                        fragment.onInflate(this.mHost.getContext(), attrs, fragment.mSavedFragmentState);
//                        this.addFragment(fragment, true);
//                    } else {
//                        if (fragment.mInLayout) {
//                            throw new IllegalArgumentException(attrs.getPositionDescription() + ": Duplicate id 0x" + Integer.toHexString(id) + ", tag " + tag + ", or parent id 0x" + Integer.toHexString(containerId) + " with another fragment for " + fname);
//                        }
//
//                        fragment.mInLayout = true;
//                        fragment.mHost = this.mHost;
//                        if (!fragment.mRetaining) {
//                            fragment.onInflate(this.mHost.getContext(), attrs, fragment.mSavedFragmentState);
//                        }
//                    }
//
//                    if (this.mCurState < 1 && fragment.mFromLayout) {
//                        this.moveToState(fragment, 1, 0, 0, false);
//                    } else {
//                        this.moveToState(fragment);
//                    }
//
//                    if (fragment.mView == null) {
//                        throw new IllegalStateException("Fragment " + fname + " did not create a view.");
//                    } else {
//                        if (id != 0) {
//                            fragment.mView.setId(id);
//                        }
//
//                        if (fragment.mView.getTag() == null) {
//                            fragment.mView.setTag(tag);
//                        }
//
//                        return fragment.mView;
//                    }
//                }
//            }
//        }
//    }
//
//    public View onCreateView(String name, Context context, AttributeSet attrs) {
//        return this.onCreateView((View)null, name, context, attrs);
//    }
//
//    Factory2 getLayoutInflaterFactory() {
//        return this;
//    }
//
//    private static class EndViewTransitionAnimator extends AnimationSet implements Runnable {
//        private final ViewGroup mParent;
//        private final View mChild;
//        private boolean mEnded;
//        private boolean mTransitionEnded;
//        private boolean mAnimating = true;
//
//        EndViewTransitionAnimator(@NonNull Animation animation, @NonNull ViewGroup parent, @NonNull View child) {
//            super(false);
//            this.mParent = parent;
//            this.mChild = child;
//            this.addAnimation(animation);
//            this.mParent.post(this);
//        }
//
//        public boolean getTransformation(long currentTime, Transformation t) {
//            this.mAnimating = true;
//            if (this.mEnded) {
//                return !this.mTransitionEnded;
//            } else {
//                boolean more = super.getTransformation(currentTime, t);
//                if (!more) {
//                    this.mEnded = true;
//                    OneShotPreDrawListener.add(this.mParent, this);
//                }
//
//                return true;
//            }
//        }
//
//        public boolean getTransformation(long currentTime, Transformation outTransformation, float scale) {
//            this.mAnimating = true;
//            if (this.mEnded) {
//                return !this.mTransitionEnded;
//            } else {
//                boolean more = super.getTransformation(currentTime, outTransformation, scale);
//                if (!more) {
//                    this.mEnded = true;
//                    OneShotPreDrawListener.add(this.mParent, this);
//                }
//
//                return true;
//            }
//        }
//
//        public void run() {
//            if (!this.mEnded && this.mAnimating) {
//                this.mAnimating = false;
//                this.mParent.post(this);
//            } else {
//                this.mParent.endViewTransition(this.mChild);
//                this.mTransitionEnded = true;
//            }
//
//        }
//    }
//
//    private static class AnimatorOnHWLayerIfNeededListener extends AnimatorListenerAdapter {
//        View mView;
//
//        AnimatorOnHWLayerIfNeededListener(View v) {
//            this.mView = v;
//        }
//
//        public void onAnimationStart(Animator animation) {
//            this.mView.setLayerType(2, (Paint)null);
//        }
//
//        public void onAnimationEnd(Animator animation) {
//            this.mView.setLayerType(0, (Paint)null);
//            animation.removeListener(this);
//        }
//    }
//
//    private static class AnimateOnHWLayerIfNeededListener extends FragmentManagerImpl.AnimationListenerWrapper {
//        View mView;
//
//        AnimateOnHWLayerIfNeededListener(View v, AnimationListener listener) {
//            super(listener);
//            this.mView = v;
//        }
//
//        @CallSuper
//        public void onAnimationEnd(Animation animation) {
//            if (!ViewCompat.isAttachedToWindow(this.mView) && VERSION.SDK_INT < 24) {
//                this.mView.setLayerType(0, (Paint)null);
//            } else {
//                this.mView.post(new Runnable() {
//                    public void run() {
//                        AnimateOnHWLayerIfNeededListener.this.mView.setLayerType(0, (Paint)null);
//                    }
//                });
//            }
//
//            super.onAnimationEnd(animation);
//        }
//    }
//
//    private static class AnimationListenerWrapper implements AnimationListener {
//        private final AnimationListener mWrapped;
//
//        AnimationListenerWrapper(AnimationListener wrapped) {
//            this.mWrapped = wrapped;
//        }
//
//        @CallSuper
//        public void onAnimationStart(Animation animation) {
//            if (this.mWrapped != null) {
//                this.mWrapped.onAnimationStart(animation);
//            }
//
//        }
//
//        @CallSuper
//        public void onAnimationEnd(Animation animation) {
//            if (this.mWrapped != null) {
//                this.mWrapped.onAnimationEnd(animation);
//            }
//
//        }
//
//        @CallSuper
//        public void onAnimationRepeat(Animation animation) {
//            if (this.mWrapped != null) {
//                this.mWrapped.onAnimationRepeat(animation);
//            }
//
//        }
//    }
//
//    private static class AnimationOrAnimator {
//        public final Animation animation;
//        public final Animator animator;
//
//        AnimationOrAnimator(Animation animation) {
//            this.animation = animation;
//            this.animator = null;
//            if (animation == null) {
//                throw new IllegalStateException("Animation cannot be null");
//            }
//        }
//
//        AnimationOrAnimator(Animator animator) {
//            this.animation = null;
//            this.animator = animator;
//            if (animator == null) {
//                throw new IllegalStateException("Animator cannot be null");
//            }
//        }
//    }
//
//    static class StartEnterTransitionListener implements OnStartEnterTransitionListener {
//        final boolean mIsBack;
//        final BackStackRecord mRecord;
//        private int mNumPostponed;
//
//        StartEnterTransitionListener(BackStackRecord record, boolean isBack) {
//            this.mIsBack = isBack;
//            this.mRecord = record;
//        }
//
//        public void onStartEnterTransition() {
//            --this.mNumPostponed;
//            if (this.mNumPostponed == 0) {
//                this.mRecord.mManager.scheduleCommit();
//            }
//        }
//
//        public void startListening() {
//            ++this.mNumPostponed;
//        }
//
//        public boolean isReady() {
//            return this.mNumPostponed == 0;
//        }
//
//        public void completeTransaction() {
//            boolean canceled = this.mNumPostponed > 0;
//            FragmentManagerImpl manager = this.mRecord.mManager;
//            int numAdded = manager.mAdded.size();
//
//            for(int i = 0; i < numAdded; ++i) {
//                Fragment fragment = (Fragment)manager.mAdded.get(i);
//                fragment.setOnStartEnterTransitionListener((OnStartEnterTransitionListener)null);
//                if (canceled && fragment.isPostponed()) {
//                    fragment.startPostponedEnterTransition();
//                }
//            }
//
//            this.mRecord.mManager.completeExecute(this.mRecord, this.mIsBack, !canceled, true);
//        }
//
//        public void cancelTransaction() {
//            this.mRecord.mManager.completeExecute(this.mRecord, this.mIsBack, false, false);
//        }
//    }
//
//    private class PopBackStackState implements FragmentManagerImpl.OpGenerator {
//        final String mName;
//        final int mId;
//        final int mFlags;
//
//        PopBackStackState(String name, int id, int flags) {
//            this.mName = name;
//            this.mId = id;
//            this.mFlags = flags;
//        }
//
//        public boolean generateOps(ArrayList<BackStackRecord> records, ArrayList<Boolean> isRecordPop) {
//            if (FragmentManagerImpl.this.mPrimaryNav != null && this.mId < 0 && this.mName == null) {
//                FragmentManager childManager = FragmentManagerImpl.this.mPrimaryNav.peekChildFragmentManager();
//                if (childManager != null && childManager.popBackStackImmediate()) {
//                    return false;
//                }
//            }
//
//            return FragmentManagerImpl.this.popBackStackState(records, isRecordPop, this.mName, this.mId, this.mFlags);
//        }
//    }
//
//    interface OpGenerator {
//        boolean generateOps(ArrayList<BackStackRecord> var1, ArrayList<Boolean> var2);
//    }
//
//    static class FragmentTag {
//        public static final int[] Fragment = new int[]{16842755, 16842960, 16842961};
//        public static final int Fragment_id = 1;
//        public static final int Fragment_name = 0;
//        public static final int Fragment_tag = 2;
//
//        private FragmentTag() {
//        }
//    }
//
//    private static final class FragmentLifecycleCallbacksHolder {
//        final FragmentLifecycleCallbacks mCallback;
//        final boolean mRecursive;
//
//        FragmentLifecycleCallbacksHolder(FragmentLifecycleCallbacks callback, boolean recursive) {
//            this.mCallback = callback;
//            this.mRecursive = recursive;
//        }
//    }
//}
