package com.zpj.widget.editor.validator;

import android.util.Patterns;

/**
 * Validates a web url in the format:
 * scheme + authority + path
 *
 * @author Andrea Baccega <me@andreabaccega.com>
 */
public class WebUrlValidator extends PatternValidator {
    public WebUrlValidator(String _customErrorMessage) {
        super(_customErrorMessage, Patterns.WEB_URL);
    }
}
