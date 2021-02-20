package com.workstation.crop.image;

import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;

import com.workstation.crop.config.CropIwaSaveConfig;
import com.workstation.crop.shape.CropIwaShapeMask;
import com.workstation.crop.util.CropIwaUtils;

import java.io.IOException;
import java.io.OutputStream;

class CropImageTask extends AsyncTask<Void, Void, Throwable> {

    private Context context;
    private CropArea cropArea;
    private CropIwaShapeMask mask;
    private Uri srcUri;
    private CropIwaSaveConfig saveConfig;

    public CropImageTask(
            Context context, CropArea cropArea, CropIwaShapeMask mask,
            Uri srcUri, CropIwaSaveConfig saveConfig) {
        this.context = context;
        this.cropArea = cropArea;
        this.mask = mask;
        this.srcUri = srcUri;
        this.saveConfig = saveConfig;
    }

    @Override
    protected Throwable doInBackground(Void... params) {
        try {
            Bitmap bitmap = CropIwaBitmapManager.get().loadToMemory(
                    context, srcUri, saveConfig.getWidth(),
                    saveConfig.getHeight());

            if (bitmap == null) {
                return new NullPointerException("Failed to initialize bitmap");
            }

            Bitmap cropped = cropArea.applyCropTo(bitmap);

            cropped = mask.applyMaskTo(cropped);

            Uri dst = saveConfig.getDstUri();
            OutputStream os = context.getContentResolver().openOutputStream(dst);
            cropped.compress(saveConfig.getCompressFormat(), saveConfig.getQuality(), os);
            CropIwaUtils.closeSilently(os);

//            bitmap.recycle();
//            cropped.recycle();
        } catch (IOException e) {
            return e;
        }
        return null;
    }

    @Override
    protected void onPostExecute(Throwable throwable) {
        if (throwable == null) {
            CropIwaResultReceiver.onCropCompleted(context, saveConfig.getDstUri());
        } else {
            CropIwaResultReceiver.onCropFailed(context, throwable);
        }
    }
}