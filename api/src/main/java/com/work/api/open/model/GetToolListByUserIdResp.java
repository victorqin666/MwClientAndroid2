package com.work.api.open.model;

import com.work.api.open.model.client.OpenWork;

import java.util.List;

/**
 * Created by tangyx
 * Date 2020/9/23
 * email tangyx@live.com
 */

public class GetToolListByUserIdResp extends BaseResp {

    private List<OpenWork> data;

    public List<OpenWork> getData() {
        return data;
    }
}
