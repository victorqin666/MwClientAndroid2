package com.work.api.open.model;

import com.work.api.open.model.client.OpenCity;

import java.util.List;

/**
 * Created by tangyx
 * Date 2020/12/28
 * email tangyx@live.com
 */

public class GetCityListResp extends BaseResp{

    private List<OpenCity> data;

    public List<OpenCity> getData() {
        return data;
    }
}
