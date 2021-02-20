package com.work.api.open.model;

/**
 * create by tangyx
 * 2018/9/20
 */
public class DownFileReq extends BaseReq{

    private String downloadUrl;
    private String dirFile;
    private String fileName;

    public String getDownloadUrl() {
        return downloadUrl;
    }

    public void setDownloadUrl(String downloadUrl) {
        this.downloadUrl = downloadUrl;
    }

    public void setDirFile(String dirFile) {
        this.dirFile = dirFile;
    }

    public String getDirFile() {
        return dirFile;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }
}
