package com.http.network.model;

import com.fasterxml.jackson.annotation.JsonIgnore;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by tangyx on 2016/12/26.
 */

public class RequestWork extends ClientModel {

    @JsonIgnore
    private Map<String,String> requestHeader;

    @JsonIgnore
    private int timeReadOut;

    public void addHeader(String key,String value){
        if(requestHeader == null){
            requestHeader = new HashMap<>();
        }
        requestHeader.put(key,value);
    }

    public Map<String, String> getRequestHeader() {
        return requestHeader;
    }

    public int getTimeReadOut() {
        return timeReadOut;
    }

    public void setTimeReadOut(int timeReadOut) {
        this.timeReadOut = timeReadOut;
    }
}
