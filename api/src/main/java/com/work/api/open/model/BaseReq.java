package com.work.api.open.model;

import com.http.network.model.RequestWork;
/**
 * Created by tangyx on 16/6/15.
 *
 */
public class BaseReq extends RequestWork {

    private String source = "android";

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }
}
