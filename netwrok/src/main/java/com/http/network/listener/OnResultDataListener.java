package com.http.network.listener;


import com.http.network.model.RequestWork;
import com.http.network.model.ResponseWork;

/**
 * Created by tangyx on 16/7/18.
 *
 */
public interface OnResultDataListener {

    void onResult(RequestWork req,ResponseWork resp) throws Exception;
}
