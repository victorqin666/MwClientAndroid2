package com.work.api.open.model;

/**
 * Created by tangyx
 * Date 2020/9/13
 * email tangyx@live.com
 */

public class SendSmsReq extends BaseReq {

    private String mobile;
    private int code;

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }
}
