package com.mwim.qcloud.tim.uikit.business.modal;

/**
 * Created by tangyx
 * Date 2020/11/19
 * email tangyx@live.com
 */

public class WorkApp {

    private boolean isWemeet;
    private String url;
    private String wemeetToken;

    public WorkApp() {

    }

    public WorkApp(String wemeetToken) {
        this.wemeetToken = wemeetToken;
        this.isWemeet = true;
    }

    public boolean isWemeet() {
        return isWemeet;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getWemeetToken() {
        return wemeetToken;
    }
}
