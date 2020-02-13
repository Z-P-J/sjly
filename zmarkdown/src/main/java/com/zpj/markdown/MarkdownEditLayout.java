package com.zpj.markdown;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;

import java.util.LinkedList;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import me.shouheng.easymark.EasyMarkEditor;
import me.shouheng.easymark.editor.Format;
import me.shouheng.easymark.editor.format.DayOneFormatHandler;
import me.shouheng.easymark.scroller.FastScrollScrollView;
import me.shouheng.easymark.tools.Utils;

/**
 * @author WngShhng (shouheng2015@gmail.com)
 * @version $Id: MarkdownEditLayout, v 0.1 2018/11/26 22:59 shouh Exp$
 */
public class MarkdownEditLayout extends FrameLayout implements KeyboardHeightObserver {

    public static final int FORMAT_ID_LEFT = 0;
    public static final int FORMAT_ID_RIGHT = 1;
    public static final int FORMAT_ID_UP = 2;
    public static final int FORMAT_ID_DOWN = 3;

    private View container;
    private RecyclerView rv;
    private EditText titleEditor;
    private EasyMarkEditor easyMarkEditor;
    private FastScrollScrollView fssv;
    private OnFormatClickListener onFormatClickListener;
    private OnCustomFormatClickListener onCustomFormatClickListener;
    private Adapter adapter;

    private boolean isKeyboardShowing;

    public MarkdownEditLayout(Context context) {
        super(context);
        init(context, null, 0, 0);
    }

    public MarkdownEditLayout(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs, 0, 0);
    }

    public MarkdownEditLayout(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs, defStyleAttr, 0);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public MarkdownEditLayout(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(context, attrs, defStyleAttr, defStyleRes);
    }

    private void init(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        LayoutInflater.from(context).inflate(R.layout.layout_markdown_editor, this, true);

        container = findViewById(R.id.container);
        titleEditor = findViewById(R.id.et_title);
        easyMarkEditor = findViewById(R.id.eme);
        fssv = findViewById(R.id.fssv);

        /* Filter formats */
        List<Format> formats = new LinkedList<>();
        Disposable disposable = Observable.fromArray(Format.values())
                .filter(format -> format != Format.H2
                        && format != Format.H3
                        && format != Format.H4
                        && format != Format.H5
                        && format != Format.H6
                        && format != Format.NORMAL_LIST
                        && format != Format.INDENT
                        && format != Format.DEDENT
                        && format != Format.CHECKBOX)
                .toList()
                .subscribe((Consumer<List<Format>>) formats::addAll);

        rv = findViewById(R.id.rv);
        adapter = new Adapter(context, formats, onFormatClickListener);
        rv.setLayoutManager(new GridLayoutManager(context, 8));
        rv.setAdapter(adapter);
        easyMarkEditor.setFormatHandler(new CustomFormatHandler());

        /* Add the bottom buttons click event. */
        ImageView ivSoft = findViewById(R.id.iv_soft);
        ivSoft.setOnClickListener(v -> {
            if (isKeyboardShowing) {
                hideSoftInput();
                rv.setVisibility(View.VISIBLE);
                ivSoft.animate().rotation(180).setDuration(500).start();
            } else {
                showSoftInput();
                ivSoft.animate().rotation(0).setDuration(500).start();

            }
        });
        findViewById(R.id.iv_left).setOnClickListener(v -> performCustomButtonClick(FORMAT_ID_LEFT));
        findViewById(R.id.iv_right).setOnClickListener(v -> performCustomButtonClick(FORMAT_ID_RIGHT));
        findViewById(R.id.iv_dedent).setOnClickListener(v -> performCustomButtonClick(Format.DEDENT.id));
        findViewById(R.id.iv_indent).setOnClickListener(v -> performCustomButtonClick(Format.INDENT.id));
    }

    /**
     * Show the soft input layout.
     */
    public void showSoftInput() {
        if(easyMarkEditor == null) return;
        rv.setVisibility(View.INVISIBLE);
        easyMarkEditor.requestFocus();
        InputMethodManager imm = (InputMethodManager) getContext().getSystemService(Activity.INPUT_METHOD_SERVICE);
        assert imm != null;
        imm.showSoftInput(easyMarkEditor, InputMethodManager.RESULT_UNCHANGED_SHOWN);
    }

    /**
     * Hide the soft input layout.
     */
    public void hideSoftInput() {
        if(easyMarkEditor == null) return;
        InputMethodManager imm = (InputMethodManager) getContext().getSystemService(Activity.INPUT_METHOD_SERVICE);
        assert imm != null;
        imm.hideSoftInputFromWindow(easyMarkEditor.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
    }

    private void performCustomButtonClick(int formatId) {
        if (onCustomFormatClickListener != null) {
            onCustomFormatClickListener.onCustomFormatClick(formatId);
        }
    }

    @Override
    public void onKeyboardHeightChanged(int height, int orientation) {
        isKeyboardShowing = height > 0;
        if (height != 0) {
            rv.setVisibility(View.INVISIBLE);
            rv.getLayoutParams().height = height;
            rv.requestLayout();
        } else if (rv.getVisibility() != View.VISIBLE) {
            rv.setVisibility(View.GONE);
        }
    }

    public static final class Adapter extends RecyclerView.Adapter<Adapter.Holder> {

        private Context context;

        private OnFormatClickListener onFormatClickListener;

        private List<Format> formats;

        Adapter(Context context, List<Format> formats, OnFormatClickListener onFormatClickListener) {
            this.context = context;
            this.formats = formats;
            this.onFormatClickListener = onFormatClickListener;
        }

        @NonNull
        @Override
        public Holder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
            View root = LayoutInflater.from(context).inflate(R.layout.item_format, null, false);
            return new Holder(root);
        }

        @Override
        public void onBindViewHolder(@NonNull Holder holder, int i) {
            Format format = formats.get(i);
            holder.iv.setImageDrawable(Utils.tintDrawable(context, format.drawableResId, Color.WHITE));
        }

        @Override
        public int getItemCount() {
            return formats.size();
        }

        class Holder extends RecyclerView.ViewHolder {

            ImageView iv;

            Holder(@NonNull View itemView) {
                super(itemView);

                iv = itemView.findViewById(R.id.iv);
                itemView.setOnClickListener(v -> {
                    if (onFormatClickListener != null) {
                        onFormatClickListener.onFormatClick(formats.get(getAdapterPosition()));
                    }
                });
            }
        }

        void setOnFormatClickListener(OnFormatClickListener onFormatClickListener) {
            this.onFormatClickListener = onFormatClickListener;
        }
    }

    public RecyclerView getRv() {
        return rv;
    }

    /**
     * Get the markdown editor.
     *
     * @return the editor
     */
//    @Override
    public EasyMarkEditor getEditText() {
        return easyMarkEditor;
    }

    public EditText getTitleEditor() {
        return titleEditor;
    }

    /**
     * Get the fast scroll view.
     *
     * @return the fast scroll view.
     */
    public FastScrollScrollView getFastScrollView() {
        return fssv;
    }

    /**
     * Set the format click event callback.
     *
     * @param onFormatClickListener the format click callback
     */
    public void setOnFormatClickListener(OnFormatClickListener onFormatClickListener) {
        this.onFormatClickListener = onFormatClickListener;
        if (adapter != null) {
            adapter.setOnFormatClickListener(onFormatClickListener);
        }
    }

    /**
     * Set the custom format callback, the format id will be send the the listener.
     *
     * @param onCustomFormatClickListener the format listener
     */
    public void setOnCustomFormatClickListener(OnCustomFormatClickListener onCustomFormatClickListener) {
        this.onCustomFormatClickListener = onCustomFormatClickListener;
    }

    /**
     * The custom format handler
     */
    public static class CustomFormatHandler extends DayOneFormatHandler {

        @Override
        public void handle(int formatId,
                           @Nullable String source,
                           int selectionStart,
                           int selectionEnd,
                           @Nullable String selection,
                           @Nullable EditText editor,
                           @NonNull Object... params) {
            switch (formatId) {
                case FORMAT_ID_LEFT: {
                    assert editor != null;
                    int pos = selectionStart - 1;
                    editor.setSelection(pos < 0 ? 0 : pos);
                    break;
                }
                case FORMAT_ID_RIGHT: {
                    assert editor != null;
                    int pos = selectionStart + 1;
                    assert source != null;
                    int length = source.length();
                    editor.setSelection(pos >= length ? length : pos);
                    break;
                }
                case FORMAT_ID_UP:
                    break;
                case FORMAT_ID_DOWN:
                    break;
            }
            Format format = Format.getFormat(formatId);
            if (format != null && editor != null) {
                switch (format) {
                    case IMAGE:
                        if (params.length == 2) {
                            String title = (String) params[0];
                            String url = (String) params[1];
                            String result = "\n![" + title + "](" + url + ")\n";
                            editor.getText().insert(selectionStart, result);
                            editor.setSelection(selectionStart + result.length());
                            return;
                        }
                        break;
                    case LINK:
                        if (params.length == 2) {
                            String title = (String) params[0];
                            String url = (String) params[1];
                            String result = "\n[" + title + "](" + url + ")\n";
                            editor.getText().insert(selectionStart, result);
                            editor.setSelection(selectionStart + result.length());
                            return;
                        }
                        break;
                }
            }
            super.handle(formatId, source, selectionStart, selectionEnd, selection, editor, params);
        }
    }

    public interface OnFormatClickListener {
        void onFormatClick(Format format);
    }

    public interface OnCustomFormatClickListener {
        void onCustomFormatClick(int formatId);
    }
}
