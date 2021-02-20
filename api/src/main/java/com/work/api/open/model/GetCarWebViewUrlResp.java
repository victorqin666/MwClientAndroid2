package com.work.api.open.model;

import com.work.api.open.model.client.OpenData;

/**
 * Created by tangyx
 * Date 2020/10/19
 * email tangyx@live.com
 */

public class GetCarWebViewUrlResp extends BaseResp {
    public boolean Success;
    public OpenData Result;
    public String Message;

    @Override
    public String getMessage() {
        return Message;
    }

    @Override
    public boolean isSuccess() {
        return Success;
    }
}
