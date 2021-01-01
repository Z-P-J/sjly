package com.zpj.widget.editor;

import android.content.Context;
import android.support.v7.widget.AppCompatEditText;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.View.OnFocusChangeListener;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

public class ZEditText extends AppCompatEditText implements
        OnFocusChangeListener {

    private OnFocusChangeListener onFocusChangeListener;
    private final List<WeakReference<OnFocusChangeListener>> listeners = new ArrayList<>();

//    private List<Validator> validators;

//    private boolean allowEmpty = false;

    public ZEditText(Context context) {
        super(context);
        init();
    }

    public ZEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public ZEditText(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

//    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
//    public ZEditText(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
//        super(context, attrs, defStyleAttr, defStyleRes);
//        init();
//    }

    private void init() {
//        validators = new ArrayList<>();
//        allowEmpty(allowEmpty);
//        addTextChangedListener(new SimpleTextWatcher() {
//            @Override
//            public void afterTextChanged(Editable s) {
//                isValid();
//            }
//        });
        super.setOnFocusChangeListener(this);
    }

    @Override
    public void setOnFocusChangeListener(OnFocusChangeListener onFocusChangeListener) {
        if (onFocusChangeListener != null && onFocusChangeListener != this) {
            removeOnFocusChangeListener(this.onFocusChangeListener);
            this.onFocusChangeListener = onFocusChangeListener;
            addOnFocusChangeListener(onFocusChangeListener);
        }
        super.setOnFocusChangeListener(this);
    }

    public void addOnFocusChangeListener(OnFocusChangeListener onFocusChangeListener) {
        if (onFocusChangeListener == null) {
            return;
        }
        listeners.add(new WeakReference<>(onFocusChangeListener));
    }

    public void removeOnFocusChangeListener(OnFocusChangeListener onFocusChangeListener) {
        if (onFocusChangeListener == null) {
            return;
        }
        for (WeakReference<OnFocusChangeListener> listener : listeners) {
            if (listener != null && listener.get() != null && listener.get() == onFocusChangeListener) {
                listeners.remove(listener);
                return;
            }
        }
    }

//    public void addValidator(Validator validator) {
//        validators.add(validator);
//    }


//    public boolean isEmptyAllowed() {
//        return allowEmpty;
//    }
//
//
//    public void allowEmpty(boolean empty) {
//        if (empty) {
//            allowEmpty = false;
//            for (int i = validators.size() - 1; i >= 0; --i) {
//                Validator validator = validators.get(i);
//                if (validator instanceof EmptyValidator) {
//                    validators.remove(i);
//                }
//            }
//        } else {
//            for (Validator validator : validators) {
//                if (validator instanceof EmptyValidator) {
//                    return;
//                }
//            }
//            allowEmpty = true;
//            validators.add(0, new EmptyValidator("输入内容不能为空"));
//        }
//    }
//
//    public boolean isValid() {
//        if (validators == null || validators.isEmpty()) {
//            return true;
//        }
//        for (Validator validator : validators) {
//            if (!validator.isValid(this)) {
//                setError(validator.getErrorMessage());
//                return false;
//            }
//        }
//        return true;
//    }

//    public String testValid() {
//        if (validators == null || validators.isEmpty()) {
//            return null;
//        }
//        for (Validator validator : validators) {
//            if (!validator.isValid(this)) {
//                return validator.getErrorMessage();
//            }
//        }
//        return null;
//    }

    @Override
    public final void onFocusChange(View v, boolean hasFocus) {
        Log.d("ZEditText", "onFocusChange hasFocus=" + hasFocus);
//        if (!hasFocus) {
//            isValid();
//        }

//        if (onFocusChangeListener != null) {
//            onFocusChangeListener.onFocusChange(v, hasFocus);
//        }

        for (WeakReference<OnFocusChangeListener> listener : listeners) {
            if (listener != null && listener.get() != null) {
                listener.get().onFocusChange(v, hasFocus);
            }
        }
    }


}
