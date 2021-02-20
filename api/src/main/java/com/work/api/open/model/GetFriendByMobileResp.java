package com.work.api.open.model;

import com.work.api.open.model.client.OpenByMobile;

import java.util.List;

/**
 * Created by tangyx
 * Date 2020/10/28
 * email tangyx@live.com
 */

public class GetFriendByMobileResp extends BaseResp {

    private List<OpenByMobile> data;

    public List<OpenByMobile> getData() {
        return data;
    }
}
