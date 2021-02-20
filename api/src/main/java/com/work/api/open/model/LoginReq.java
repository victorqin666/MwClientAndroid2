package com.work.api.open.model;

/**
 * Created by tangyx
 * Date 2020/9/13
 * email tangyx@live.com
 */

public class LoginReq extends BaseReq {

    private String userId;
    private String mobile;
    private String password;

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
