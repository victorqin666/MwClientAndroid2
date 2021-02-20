package com.work.api.open.model;

/**
 * Created by tangyx
 * Date 2020/12/6
 * email tangyx@live.com
 */

public class CheckToolTokenReq extends BaseReq {

    private String toolDomain;
    private String toolKey;

    public String getToolDomain() {
        return toolDomain;
    }

    public void setToolDomain(String toolDomain) {
        this.toolDomain = toolDomain;
    }

    public String getToolKey() {
        return toolKey;
    }

    public void setToolKey(String toolKey) {
        this.toolKey = toolKey;
    }
}
