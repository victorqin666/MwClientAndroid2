package com.work.download;

public interface DownloadListener {

    void onCancel();

    void onDone(boolean canceled, int error);

    void onPercentUpdate(int percent);
}
