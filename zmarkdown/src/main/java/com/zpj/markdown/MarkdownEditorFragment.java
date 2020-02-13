package com.zpj.markdown;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import java.util.Objects;

import me.shouheng.easymark.EasyMarkEditor;
import me.shouheng.easymark.editor.Format;
import me.shouheng.easymark.tools.Utils;
import me.yokeyword.fragmentation.SupportFragment;

public class MarkdownEditorFragment extends SupportFragment {

    /**
     * The key for action, used to send a command to this fragment.
     * The MainActivity will directly put the action argument to this fragment if received itself.
     */
    public static final String ARGS_KEY_ACTION = "__args_key_action";

    /**
     * The intent the MainActivity received. This fragment will get the extras from this value,
     * and handle the intent later.
     */
    public static final String ARGS_KEY_INTENT = "__args_key_intent";

    /**
     * The most important argument, the note model, used to get the information of note.
     */
    public static final String ARGS_KEY_NOTE = "__args_key_note";

    private static final String TAB_REPLACEMENT = "    ";

    private MarkdownEditLayout mel;
    private EditText etTitle;
    private EasyMarkEditor eme;
    private KeyboardHeightProvider keyboardHeightProvider;

    public static int parseInteger(String intString, int defaultValue) {
        int number;
        try {
            number = TextUtils.isEmpty(intString) ? defaultValue : Integer.parseInt(intString);
        } catch (NumberFormatException e) {
            number = defaultValue;
        }
        return number;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_markdown_editor, null, false);

        /* Config the edit layout */
        mel = view.findViewById(R.id.mel);
//        mel.setOverHeight(Utils.dp2px(getContext(), 48));
        mel.setOnFormatClickListener(format -> {
            if (format == Format.TABLE) {
                TableInputDialog2.with(getContext())
                        .setOnConfirmClickListener((rowsStr, colsStr) -> {
                            int rows = parseInteger(rowsStr, 3);
                            int cols = parseInteger(colsStr, 3);
                            eme.useFormat(format, rows, cols);
                        })
                        .show();
            } else {
                eme.useFormat(format);
            }
        });
        eme = mel.getEditText();
        eme.setFormatPasteEnable(true);
        etTitle = mel.getTitleEditor();
        TextWatcher inputWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                // noop
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                // noop
            }

            @Override
            public void afterTextChanged(Editable editable) {
                String title = etTitle.getText().toString();
                String content = Objects.requireNonNull(eme.getText()).toString();
                String count = getResources().getString(R.string.text_chars) + ":" + (title.length() + content.length());
                ((TextView) (view.findViewById(R.id.tv_count))).setText(count);
            }
        };
        etTitle.addTextChangedListener(inputWatcher);
        eme.addTextChangedListener(inputWatcher);
        mel.getFastScrollView().getFastScrollDelegate().setThumbSize(16, 40);
        mel.getFastScrollView().getFastScrollDelegate().setThumbDynamicHeight(false);
        mel.getFastScrollView().getFastScrollDelegate().setThumbDrawable(getResources().getDrawable(R.drawable.fast_scroll_bar_dark));
        mel.setOnCustomFormatClickListener(formatId -> {
            if (formatId == 100) {
                eme.undo();
                return;
            } else if (formatId == 101) {
                eme.redo();
                return;
            }
            eme.useFormat(formatId);
        });
        keyboardHeightProvider = new KeyboardHeightProvider(_mActivity);
        keyboardHeightProvider.setKeyboardHeightObserver(mel);
        post(() -> keyboardHeightProvider.start());
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        keyboardHeightProvider.setKeyboardHeightObserver(mel);
    }

    @Override
    public void onPause() {
        super.onPause();
        keyboardHeightProvider.setKeyboardHeightObserver(null);
    }

    @Override
    public void onStop() {
        super.onStop();
        hideSoftInput();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        keyboardHeightProvider.close();
    }

    public String getMarkdownContent() {
        Editable editable = eme.getText();
        if (editable == null) {
            return "";
        }
        String str = editable.toString();
        if (TextUtils.isEmpty(str)) {
            return "";
        }
        return str;
    }

    public void showSoftInput() {
        mel.showSoftInput();
    }

    public void hideSoftInput() {
        mel.hideSoftInput();
    }

//    private void showAttachmentPicker() {
//        new Builder(Objects.requireNonNull(getContext()))
//                .setTitle(R.string.text_pick)
//                .setStyle(isDarkTheme() ? R.style.BottomSheet_Dark : R.style.BottomSheet)
//                .setMenu(ColorUtils.getThemedBottomSheetMenu(getContext(), R.menu.attachment_picker))
//                .setListener(new BottomSheetListener() {
//                    @Override
//                    public void onSheetShown(@NonNull BottomSheet bottomSheet, @Nullable Object o) {
//                        // noop
//                    }
//
//                    @Override
//                    public void onSheetItemSelected(@NonNull BottomSheet bottomSheet, MenuItem menuItem, @Nullable Object o) {
//                        switch (menuItem.getItemId()) {
//                            case R.id.item_pick_from_album:
//                                Activity activity = getActivity();
//                                if (activity != null) {
//                                    PermissionUtils.checkStoragePermission((CommonActivity) activity,
//                                            () -> AttachmentHelper.pickFromCustomAlbum(MarkdownEditorFragment.this));
//                                }
//                                break;
//                            case R.id.item_pick_take_a_photo:
//                                activity = getActivity();
//                                if (activity != null) {
//                                    PermissionUtils.checkPermissions((CommonActivity) activity,
//                                            () -> AttachmentHelper.takeAPhoto(MarkdownEditorFragment.this),
//                                            Permission.STORAGE, Permission.CAMERA);
//                                }
//                                break;
//                            case R.id.item_pick_create_sketch:
//                                activity = getActivity();
//                                if (activity != null) {
//                                    PermissionUtils.checkStoragePermission((CommonActivity) activity,
//                                            () -> AttachmentHelper.createSketch(MarkdownEditorFragment.this));
//                                }
//                                break;
//                            default:
//                                // noop
//                        }
//                    }
//
//                    @Override
//                    public void onSheetDismissed(@NonNull BottomSheet bottomSheet, @Nullable Object o, int i) {
//                        // noop
//                    }
//                })
//                .show();
//    }

//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        switch (item.getItemId()) {
//            case R.id.action_preview:
//                String title = etTitle.getText().toString();
//                String content = Objects.requireNonNull(eme.getText()).toString() + " ";
//                getVM().getNote().setTitle(title);
//                getVM().getNote().setContent(content);
//                ContainerActivity.open(MarkdownViewFragment.class)
//                        .put(MarkdownViewFragment.ARGS_KEY_NOTE, (Serializable) getVM().getNote())
//                        .put(MarkdownViewFragment.ARGS_KEY_IS_PREVIEW, true)
//                        .launch(getActivity());
//                break;
//            case R.id.action_undo:
//                eme.undo();
//                break;
//            case R.id.action_redo:
//                eme.redo();
//                break;
//            case R.id.action_attachment:
//                showAttachmentPicker();
//                break;
//            case R.id.action_notebook:
//                NotebookPickerDialog.newInstance().setOnItemSelectedListener((dialog, value, position) -> {
//                    getVM().getNote().setParentCode(value.getCode());
//                    getVM().getNote().setTreePath(value.getTreePath() + "|" + value.getCode());
//                    dialog.dismiss();
//                }).show(Objects.requireNonNull(getFragmentManager()), "NOTEBOOK_PICKER");
//                break;
//            case R.id.action_category:
//                showCategoriesPicker();
//                break;
//            case R.id.action_send:
//                title = etTitle.getText().toString();
//                content = Objects.requireNonNull(eme.getText()).toString() + " ";
//                NoteManager.send(getContext(), title, content, new ArrayList<>());
//                break;
//            case R.id.action_copy_title:
//                title = etTitle.getText().toString();
//                NoteManager.copy(Objects.requireNonNull(getActivity()), title);
//                ToastUtils.showShort(R.string.note_copied_success);
//                break;
//            case R.id.action_copy_content:
//                content = Objects.requireNonNull(eme.getText()).toString() + " ";
//                NoteManager.copy(Objects.requireNonNull(getActivity()), content);
//                ToastUtils.showShort(R.string.note_copied_success);
//                break;
//            case R.id.action_setting_note:
//                SettingsActivity.open(SettingsNote.class).launch(getContext());
//                break;
//            default:
//                // noop
//        }
//        return super.onOptionsItemSelected(item);
//    }

}
