package com.workstation.crop.image;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.workstation.crop.config.CropIwaSaveConfig;
import com.workstation.crop.shape.CropIwaShapeMask;
import com.workstation.crop.util.CropIwaUtils;
import com.workstation.crop.util.ImageHeaderParser;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import static com.workstation.crop.util.CropIwaUtils.closeSilently;

public class CropIwaBitmapManager {

    private static final CropIwaBitmapManager INSTANCE = new CropIwaBitmapManager();

    public static final int SIZE_UNSPECIFIED = -1;

    public static CropIwaBitmapManager get() {
        return INSTANCE;
    }

    private final Object loadRequestLock = new Object();

    private Map<Uri, BitmapLoadListener> requestResultListeners;
    private Map<Uri, File> localCache;

    private CropIwaBitmapManager() {
        requestResultListeners = new HashMap<>();
        localCache = new HashMap<>();
    }

    public void load(@NonNull Context context, @NonNull Uri uri, int width, int height, BitmapLoadListener listener) {
        synchronized (loadRequestLock) {
            boolean requestInProgress = requestResultListeners.containsKey(uri);
            requestResultListeners.put(uri, listener);
            if (requestInProgress) {
                return;
            }
        }
        LoadImageTask task = new LoadImageTask(
                context.getApplicationContext(), uri,
                width, height);
        task.execute();
    }

    public void crop(
            Context context, CropArea cropArea, CropIwaShapeMask mask,
            Uri uri, CropIwaSaveConfig saveConfig) {
        CropImageTask cropTask = new CropImageTask(
                context.getApplicationContext(),
                cropArea, mask, uri, saveConfig);
        cropTask.execute();
    }

    public void unregisterLoadListenerFor(Uri uri) {
        synchronized (loadRequestLock) {
            if (requestResultListeners.containsKey(uri)) {
                requestResultListeners.put(uri, null);
            }
        }
    }

    public void removeIfCached(Uri uri) {
        CropIwaUtils.delete(localCache.remove(uri));
    }

    void notifyListener(Uri uri, Bitmap result, Throwable e) {
        CropIwaBitmapManager.BitmapLoadListener listener;
        synchronized (loadRequestLock) {
            listener = requestResultListeners.remove(uri);
        }
        if (listener != null) {
            if (e != null) {
                listener.onLoadFailed(e);
            } else {
                listener.onBitmapLoaded(uri, result);
            }

        } else {
            //There is no listener interested in this request, so nobody will take care of
            //cached image.
            removeIfCached(uri);
        }
    }

    @Nullable
    Bitmap loadToMemory(Context context, Uri uri, int width, int height) throws IOException {
        Uri localResUri = toLocalUri(context, uri);
        BitmapFactory.Options options = getBitmapFactoryOptions(context, localResUri, width, height);
        return tryLoadBitmap(context, localResUri, options);
    }

    private Bitmap tryLoadBitmap(Context context, Uri uri, BitmapFactory.Options options) throws FileNotFoundException {
        Bitmap result;
        while (true) {
            InputStream is = context.getContentResolver().openInputStream(uri);
            try {
                result = BitmapFactory.decodeStream(is, null, options);
            } catch (OutOfMemoryError error) {
                if (options.inSampleSize < 64) {
                    options.inSampleSize *= 2;
                    continue;
                } else {
                    return null;
                }
            }
            return ensureCorrectRotation(context, uri, result);
        }
    }

    private Uri toLocalUri(Context context, Uri uri) throws IOException {
        if (isWebUri(uri)) {
            File cached = localCache.get(uri);
            if (cached == null) {
                cached = cacheLocally(context, uri);
                localCache.put(uri, cached);
            }
            return Uri.fromFile(cached);
        } else {
            return uri;
        }
    }

    private File cacheLocally(Context context, Uri input) throws IOException {
        File local = new File(context.getExternalCacheDir(), generateLocalTempFileName(input));
        URL url = new URL(input.toString());
        BufferedInputStream bis = null;
        BufferedOutputStream bos = null;
        try {
            int read;
            byte[] buffer = new byte[1024];
            bis = new BufferedInputStream(url.openStream());
            bos = new BufferedOutputStream(new FileOutputStream(local));
            while ((read = bis.read(buffer)) != -1) {
                bos.write(buffer, 0, read);
            }
            bos.flush();
        } finally {
            closeSilently(bis);
            closeSilently(bos);
        }
        return local;
    }

    private BitmapFactory.Options getBitmapFactoryOptions(Context c, Uri uri, int width, int height) throws FileNotFoundException {
        if (width != SIZE_UNSPECIFIED && height != SIZE_UNSPECIFIED) {
            return getOptimalSizeOptions(c, uri, width, height);
        } else {
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inSampleSize = 1;
            return options;
        }
    }

    private static BitmapFactory.Options getOptimalSizeOptions(
            Context context, Uri bitmapUri,
            int reqWidth, int reqHeight) throws FileNotFoundException {
        InputStream is = context.getContentResolver().openInputStream(bitmapUri);
        BitmapFactory.Options result = new BitmapFactory.Options();
        result.inJustDecodeBounds = true;
        BitmapFactory.decodeStream(is, null, result);
        result.inJustDecodeBounds = false;
        result.inSampleSize = calculateInSampleSize(result, reqWidth, reqHeight);
        return result;
    }

    private static int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;
        if (height > reqHeight || width > reqWidth) {
            final int halfHeight = height / 2;
            final int halfWidth = width / 2;
            while ((halfHeight / inSampleSize) >= reqHeight && (halfWidth / inSampleSize) >= reqWidth) {
                inSampleSize *= 2;
            }
        }
        return inSampleSize;
    }

    private static Bitmap ensureCorrectRotation(Context context, Uri uri, Bitmap bitmap) {
        int degrees = exifToDegrees(extractExifOrientation(context, uri));
        if (degrees != 0) {
            Matrix matrix = new Matrix();
            matrix.preRotate(degrees);
            return transformBitmap(bitmap, matrix);
        }
        return bitmap;
    }

    private static Bitmap transformBitmap(@NonNull Bitmap bitmap, @NonNull Matrix transformMatrix) {
        Bitmap result = bitmap;
        try {
            Bitmap converted = Bitmap.createBitmap(
                    bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(),
                    transformMatrix,
                    true);
            if (!bitmap.sameAs(converted)) {
                result = converted;
//                bitmap.recycle();
            }
        } catch (OutOfMemoryError error) {
            error.printStackTrace();
        }
        return result;
    }

    private static int extractExifOrientation(@NonNull Context context, @NonNull Uri imageUri) {
        InputStream is = null;
        try {
            is = context.getContentResolver().openInputStream(imageUri);
            if (is == null) {
                return ExifInterface.ORIENTATION_UNDEFINED;
            }
            return new ImageHeaderParser(is).getOrientation();
        } catch (IOException e) {
            return ExifInterface.ORIENTATION_UNDEFINED;
        } finally {
            closeSilently(is);
        }
    }

    private static int exifToDegrees(int exifOrientation) {
        int rotation;
        switch (exifOrientation) {
            case ExifInterface.ORIENTATION_ROTATE_90:
            case ExifInterface.ORIENTATION_TRANSPOSE:
                rotation = 90;
                break;
            case ExifInterface.ORIENTATION_ROTATE_180:
            case ExifInterface.ORIENTATION_FLIP_VERTICAL:
                rotation = 180;
                break;
            case ExifInterface.ORIENTATION_ROTATE_270:
            case ExifInterface.ORIENTATION_TRANSVERSE:
                rotation = 270;
                break;
            default:
                rotation = 0;
        }
        return rotation;
    }

    private boolean isWebUri(Uri uri) {
        String scheme = uri.getScheme();
        return "http".equals(scheme) || "https".equals(scheme);
    }

    private String generateLocalTempFileName(Uri uri) {
        return "temp_" + uri.getLastPathSegment() + "_" + System.currentTimeMillis();
    }

    public interface BitmapLoadListener {
        void onBitmapLoaded(Uri uri, Bitmap bitmap);

        void onLoadFailed(Throwable e);
    }
}
