package com.mwim.qcloud.tim.uikit.business.inter;

/**
 * Created by tangyx
 * Date 2020/11/18
 * email tangyx@live.com
 */

public abstract class YzStatusListener {
    public void logout(){}
    public void loginSuccess(Object data){}
    public void loginFail(String module, final int errCode, final String errMsg){}
}
