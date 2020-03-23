package com.zpj.markdown;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.Toast;

import com.zpj.fragmentation.SupportFragment;

import me.shouheng.easymark.EasyMarkViewer;
import me.shouheng.easymark.viewer.listener.LifecycleListener;
import me.shouheng.easymark.viewer.listener.OnImageClickListener;
import me.shouheng.easymark.viewer.listener.OnUrlClickListener;

public class MarkdownViewFragment extends SupportFragment {

    /**
     * The key for argument, used to send the note model to this fragment.
     */
    public static final String ARGS_KEY_NOTE = "__args_key_note";

    /**
     * The key for argument, used to set the behavior of this fragment. If true, the edit FAB
     * won't be displayed.
     */
    public static final String ARGS_KEY_IS_PREVIEW = "__args_key_is_preview";

    /**
     * The request code for editing this note.
     */
    private static final int REQUEST_FOR_EDIT = 0x01;

    private EasyMarkViewer emv;
    private String currentContent = "";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_markdown_view, null, false);

        emv = view.findViewById(R.id.emv);
        /* Config WebView. */
        emv.getFastScrollDelegate().setThumbDrawable(getResources().getDrawable(R.drawable.fast_scroll_bar_dark));
        //  : R.drawable.fast_scroll_bar_light
        emv.getFastScrollDelegate().setThumbSize(16, 40);
        emv.getFastScrollDelegate().setThumbDynamicHeight(false);
        emv.useStyleCss(EasyMarkViewer.LIGHT_STYLE_CSS); // EasyMarkViewer.DARK_STYLE_CSS :
        emv.setOnImageClickListener(new OnImageClickListener() {
            @Override
            public void onImageClick(String url, String[] urls) {

            }
        });
        emv.setOnUrlClickListener(new OnUrlClickListener() {
            @Override
            public void onUrlClick(String url) {

            }
        });
        emv.setLifecycleListener(new LifecycleListener() {
            @Override
            public void onLoadFinished(WebView webView, String str) {
                // noop
            }

            @Override
            public void beforeProcessMarkdown(String content) {
                // noop
            }

            @Override
            public void afterProcessMarkdown(String document) {
//                Toast.makeText(getContext(), "document=" + document, Toast.LENGTH_SHORT).show();
            }
        });
        emv.setUseMathJax(true);
        emv.processMarkdown("");
        return view;
    }

    public final void processMarkdown(String content) {
        if (TextUtils.equals(currentContent, content)) {
            return;
        }
        currentContent = content;
        emv.processMarkdown(content);
    }

//    private void outputHtml(boolean isShare) {
//        try {
//            File exDir = FileManager.getHtmlExportDir();
//            File outFile = new File(exDir, FileManager.getDefaultFileName(Constants.EXPORTED_HTML_EXTENSION));
//            FileUtils.writeStringToFile(outFile, getVM().getHtml(), Constants.NOTE_FILE_ENCODING);
//            if (isShare) {
//                // Share, do share option
//                NoteManager.sendFile(getContext(), outFile, Constants.MIME_TYPE_HTML);
//            } else {
//                // Not share, just show a message
//                ToastUtils.showShort(String.format(getString(R.string.text_file_saved_to), outFile.getPath()));
//            }
//        } catch (IOException e) {
//            ToastUtils.showShort(R.string.text_failed_to_save_file);
//        }
//    }

//    private void outputContent(boolean isShare) {
//        try {
//            File exDir = FileManager.getTextExportDir();
//            File outFile = new File(exDir, FileManager.getDefaultFileName(Constants.EXPORTED_TEXT_EXTENSION));
//            FileUtils.writeStringToFile(outFile, getVM().getNote().getContent(), "utf-8");
//            if (isShare) {
//                // Share, do share option
//                NoteManager.sendFile(getContext(), outFile, Constants.MIME_TYPE_FILES);
//            } else {
//                // Not share, just show a message
//                ToastUtils.showShort(String.format(getString(R.string.text_file_saved_to), outFile.getPath()));
//            }
//        } catch (IOException e) {
//            ToastUtils.showShort(R.string.text_failed_to_save_file);
//        }
//    }

}
