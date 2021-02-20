package com.work.api.open.model;

import com.http.network.model.ResponseWork;

/**
 * Created by tangyx on 16/6/15.
 *
 */
public class BaseResp extends ResponseWork {

    private String msg;
    private int code;

    public void setCode(int code) {
        this.code = code;
    }

    public int getCode() {
        return code;
    }

    @Override
    public String getMessage() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    @Override
    public boolean isSuccess() {
        return getCode() == 200;
    }
}
