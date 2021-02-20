package com.work.api.open.model;

/**
 * Created by tangyx
 * Date 2020/9/15
 * email tangyx@live.com
 */

public class GetUserByParamReq extends BaseReq {
    private String paramVal;
    private String param;

    public String getParamVal() {
        return paramVal;
    }

    public void setParamVal(String paramVal) {
        this.paramVal = paramVal;
    }

    public String getParam() {
        return param;
    }

    public void setParam(String param) {
        this.param = param;
    }
}
