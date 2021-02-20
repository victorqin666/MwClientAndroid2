package com.mwim.qcloud.tim.uikit.business.barcodescanner.camera;

import com.mwim.qcloud.tim.uikit.business.barcodescanner.SourceData;

/**
 * Callback for camera previews.
 */
public interface PreviewCallback {
    void onPreview(SourceData sourceData);
    void onPreviewError(Exception e);
}
