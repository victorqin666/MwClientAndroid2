package com.work.api.open.model;

import com.work.api.open.model.client.OpenVersion;

/**
 * Created by tangyx
 * Date 2020/10/6
 * email tangyx@live.com
 */

public class GetVersionResp extends BaseResp {

    private OpenVersion data;

    public OpenVersion getData() {
        return data;
    }
}
