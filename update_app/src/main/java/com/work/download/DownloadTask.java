package com.work.download;

import android.webkit.URLUtil;

import com.work.util.FileUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;


public class DownloadTask extends UpdateTask {

    public static final int RESULT_OK = 1;
    public static final int RESULT_URL_ERROR = 2;
    public static final int RESULT_DOWNLOAD_ERROR = 3;
    public static final int RESULT_NO_ENOUGH_SPACE = 4;

    private int mResult = RESULT_OK;
    private String mUrl;
    private String mFileName;
    private DownloadListener mDownLoadListener;

    public DownloadTask(DownloadListener listener, String url, String fileName) {
        mDownLoadListener = listener;
        mUrl = url;
        mFileName = fileName;
    }

    private void setResult(int result) {
        mResult = result;
    }

    @Override
    public void doInBackground() {

        if (!URLUtil.isNetworkUrl(mUrl)) {
            setResult(RESULT_URL_ERROR);
            return;
        }

        int updatePercent;
        String updateUrl = mUrl;

        int totalRead = 0;
        int totalSize = 0;

        try {
            URL myURL = new URL(updateUrl);
            HttpURLConnection conn = (HttpURLConnection) myURL.openConnection();
            conn.setConnectTimeout(30 * 1000);
            conn.connect();
            totalSize = conn.getContentLength();
            File dstFile = new File(mFileName);

            if (dstFile.exists() && totalSize == dstFile.length()) {//判断文件是否存在
                conn.disconnect();
                setResult(RESULT_OK);
                return;
            }

            if (dstFile.exists()) {
                dstFile.delete();
            }

            File dir = dstFile.getParentFile();
            if (!dir.exists() && !dir.mkdirs()) {
                setResult(RESULT_DOWNLOAD_ERROR);
                return;
            }

            long free = FileUtils.getUsableSpace(dir);
            if (free < totalSize) {
                setResult(RESULT_NO_ENOUGH_SPACE);
                return;
            }
            InputStream is = conn.getInputStream();
            if (is == null) {
                setResult(RESULT_DOWNLOAD_ERROR);
                return;
            }
            FileOutputStream fos = new FileOutputStream(dstFile);
            byte buf[] = new byte[1024 * 8];
            int mm=0;
            while (!isCancelled()) {
                int read = is.read(buf);
                if (read <= 0) {
                    break;
                }
                fos.write(buf, 0, read);
                totalRead += read;
                updatePercent = (int) (100f * totalRead / totalSize);
                mm++;
                if (!isCancelled() && mm==50) {
                    mDownLoadListener.onPercentUpdate(updatePercent);
                    mm=0;
                }
            }

            try {
                is.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
            try {
                fos.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
            conn.disconnect();
        } catch (Exception e) {
            e.printStackTrace();
            setResult(RESULT_DOWNLOAD_ERROR);
            return;
        }

        if (isCancelled()) {
            return;
        }

        if (totalRead != totalSize) {
            setResult(RESULT_DOWNLOAD_ERROR);
        } else {
            FileUtils.chmod("666", mFileName);
            setResult(RESULT_OK);
        }
    }

    public static long getUsableSpace(File path) {
        if (path == null) {
            return -1;
        }
        return path.getUsableSpace();
    }

    @Override
    protected void onCancel() {
        mDownLoadListener.onCancel();
    }

    @Override
    public void onFinish(boolean canceled) {
        mDownLoadListener.onDone(canceled, mResult);
    }
}
