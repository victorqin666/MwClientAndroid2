package com.work.api.open.model;

/**
 * Created by tangyx
 * Date 2020/9/16
 * email tangyx@live.com
 */

public class UpdateMobileReq extends BaseReq {

    private String oldMobile;
    private String mobile;
    private String smsCode;

    public String getOldMobile() {
        return oldMobile;
    }

    public void setOldMobile(String oldMobile) {
        this.oldMobile = oldMobile;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getSmsCode() {
        return smsCode;
    }

    public void setSmsCode(String smsCode) {
        this.smsCode = smsCode;
    }
}
