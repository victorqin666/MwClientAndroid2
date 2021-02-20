package com.work.api.open.model.client;

import com.http.network.model.ClientModel;

/**
 * Created by tangyx
 * Date 2020/10/6
 * email tangyx@live.com
 */

public class OpenVersion extends ClientModel {

    /**
     * id : 1
     * version : 1.0.5
     * url : http://118.31.108.13:8008/app/im_1.0.5_5.apk
     * createTime : 2020-10-05 10:46:25
     * deleteStatus : 0
     */

    private int id;
    private String version;
    private String url;
    private String createTime;
    private int deleteStatus;
    private String content;

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    public int getDeleteStatus() {
        return deleteStatus;
    }

    public void setDeleteStatus(int deleteStatus) {
        this.deleteStatus = deleteStatus;
    }
}
