package com.yalantis.ucrop;

import android.net.Uri;

public interface UCropFragmentCallback {

    /**
     * Return loader status
     * @param showLoader
     */
    void loadingProgress(boolean showLoader);

    /**
     * Return cropping result or error
     * @param result
     */
    void onCropFinish(Uri resultUri);

}
