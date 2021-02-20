package com.work.api.open.model;

import com.work.api.open.model.client.OpenData;

import java.util.List;

/**
 * Created by tangyx
 * Date 2020/12/27
 * email tangyx@live.com
 */

public class GetUserListByMobilesReq extends BaseReq{

    private List<OpenData> paramVal;

    public List<OpenData> getParamVal() {
        return paramVal;
    }

    public void setParamVal(List<OpenData> paramVal) {
        this.paramVal = paramVal;
    }
}
