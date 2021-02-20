package com.work.api.open.model;

import com.work.api.open.model.client.OpenData;

/**
 * Created by tangyx
 * Date 2020/9/13
 * email tangyx@live.com
 */

public class LoginResp extends BaseResp {

    private OpenData data;
    private String token;

    public String getToken() {
        return token;
    }

    public OpenData getData() {
        return data;
    }
}
