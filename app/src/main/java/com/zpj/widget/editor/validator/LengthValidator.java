package com.zpj.widget.editor.validator;

import android.text.TextUtils;
import android.widget.EditText;

/**
 * A simple validator that validates the field only if the field is not empty.
 *
 * @author Andrea Baccega <me@andreabaccega.com>
 */
public class LengthValidator extends Validator {

    private final int min;
    private final int max;

    public LengthValidator(String message, int min, int max) {
        super(message);
        if (min < 0) {
            min = 0;
        }
        if (max < min) {
            max = min;
        }
        this.min = min;
        this.max = max;
    }

    public boolean isValid(EditText et) {
        String text = et.getText().toString();
        if (TextUtils.isEmpty(text)) {
            return min == 0;
        }
        int length = TextUtils.getTrimmedLength(text);
        return length >= min && length <= max;
    }
}
