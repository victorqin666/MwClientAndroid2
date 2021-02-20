package com.http.network.model;

import android.text.TextUtils;

import com.http.network.RequestParams;
import com.http.network.task.ObjectMapperFactory;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.List;

/**
 * Created by tangyx on 2016/12/26.
 *
 */

public class ResponseWork extends ClientModel {
    /**
     * 网络请求状态
     */
    private int httpCode;
    private List<Object> positionParams;

    public int getHttpCode() {
        return httpCode;
    }

    public void setHttpCode(int httpCode) {
        this.httpCode = httpCode;
    }

    public void setPositionParams(List<Object> positionParams) {
        this.positionParams = positionParams;
    }

    public <T extends Object> T getPositionParams(int position){
        return (T) positionParams.get(position);
    }
    /**
     * 自己处理参数赋值
     */
    public void onResultData(RequestParams params,Object result){
        if(params.resp!=null && result instanceof String){
            String value = (String) result;
            if(!TextUtils.isEmpty(value)){
                //过滤字段不存在的情况
                try {
                    if(value.startsWith("[") && value.endsWith("]")){
                        JSONObject tempJson = new JSONObject();
                        tempJson.put("list",new JSONArray(value));
                        value = tempJson.toString();
                    }
                    params.resp = ObjectMapperFactory.getObjectMapper().json2Model(value,params.resp.getClass());
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        }
    }
    /**
     * 判断结果
     */
    public boolean isSuccess(){
        return false;
    }
    /**
     * 内容
     */
    public String getMessage(){
        return null;
    }
}
