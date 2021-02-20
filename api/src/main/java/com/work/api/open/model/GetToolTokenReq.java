package com.work.api.open.model;

/**
 * Created by tangyx
 * Date 2020/10/14
 * email tangyx@live.com
 */

public class GetToolTokenReq extends BaseReq {

    private String toolCode;
    private String userName;

    public String getToolCode() {
        return toolCode;
    }

    public void setToolCode(String toolCode) {
        this.toolCode = toolCode;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }
}
