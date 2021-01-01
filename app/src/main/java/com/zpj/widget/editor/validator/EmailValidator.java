package com.zpj.widget.editor.validator;

import android.util.Patterns;

/**
 * This validates an email using regexps.
 * Note that if an email passes the validation with this validator it doesn't mean it's a valid email - it means it's a valid email <storng>format</strong>
 *
 * @author Andrea Baccega <me@andreabaccega.com>
 */
public class EmailValidator extends PatternValidator {
    public EmailValidator(String _customErrorMessage) {
        super(_customErrorMessage, Patterns.EMAIL_ADDRESS);
    }
}
