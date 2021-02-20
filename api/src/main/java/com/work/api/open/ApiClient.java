package com.work.api.open;

import android.text.TextUtils;

import com.http.network.RequestParams;
import com.http.network.listener.OnResultDataListener;
import com.http.network.model.RequestWork;
import com.http.network.model.ResponseWork;
import com.http.network.task.ConnectDataTask;
import com.http.network.task.ObjectMapperFactory;
import com.work.api.open.model.DownFileReq;
import com.work.util.SLog;
import com.work.util.SharedUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.net.URLEncoder;
import java.util.Iterator;
import java.util.Map;

/**
 * Created by tangyx on 2017/1/9.
 *
 */

public class ApiClient {

    private static ApiConfig ApiConfig;

    protected void requestPost(String url, RequestWork requestWork, ResponseWork responseWork, OnResultDataListener onResultDataListener, Object... objects){
        url = clientModelUrl(url);
//        StringBuilder urlBuilder = new StringBuilder(url);
//        if(getApiConfig()!=null && getApiConfig().paramObj!=null){//是否配置默认的参数
//            Object paramObj = getApiConfig().paramObj;
//            Field[] fields = paramObj.getClass().getDeclaredFields();
//            for (Field field:fields) {
//                String name = field.getName();
//                Object value = getFieldValueByName(name,paramObj);
//                if(TextUtils.equals(String.valueOf(value),"null")){
//                    continue;
//                }
//                try {
//                    if(!urlBuilder.toString().contains("?")){
//                        urlBuilder.append("?").append(name).append("=").append(URLEncoder.encode(String.valueOf(value),"UTF-8"));
//                    }else{
//                        urlBuilder.append("&").append(name).append("=").append(URLEncoder.encode(String.valueOf(value),"UTF-8"));
//                    }
//                } catch (UnsupportedEncodingException e) {
//                    e.printStackTrace();
//                }
//            }
//        }
//        url = urlBuilder.toString();
        String postData = getPostData(requestWork);
        RequestParams params = getParams(url,postData,requestWork,responseWork,onResultDataListener,objects);
        ConnectDataTask connectDataTask = new ConnectDataTask(params);
        connectDataTask.doPost();
    }

    protected void requestGet(String url, RequestWork requestWork,ResponseWork responseWork, OnResultDataListener onResultDataListener,Object... objects){
        url = clientModelUrl(url);
        StringBuilder urlBuilder = new StringBuilder(url);
        if(getApiConfig()!=null && getApiConfig().paramObj!=null){//是否配置默认的参数
            Object paramObj = getApiConfig().paramObj;
            Field[] fields = paramObj.getClass().getDeclaredFields();
            for (Field field:fields) {
                String name = field.getName();
                Object value = getFieldValueByName(name,paramObj);
                if(TextUtils.equals(String.valueOf(value),"null")){
                    continue;
                }
                try {
                    if(!urlBuilder.toString().contains("?")){
                        urlBuilder.append("?").append(name).append("=").append(URLEncoder.encode(String.valueOf(value),"UTF-8"));
                    }else{
                        urlBuilder.append("&").append(name).append("=").append(URLEncoder.encode(String.valueOf(value),"UTF-8"));
                    }
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
            }
        }
        if(requestWork!=null){//把模型的值追加到url后面
            Field[] fields = requestWork.getClass().getDeclaredFields();
            for (Field field:fields) {
                String name = field.getName();
                Object value = getFieldValueByName(name,requestWork);
                if(TextUtils.equals(String.valueOf(value),"null")){
                    continue;
                }
                try {
                    if(!urlBuilder.toString().contains("?")){
                        urlBuilder.append("?").append(name).append("=").append(URLEncoder.encode(String.valueOf(value),"UTF-8"));
                    }else{
                        urlBuilder.append("&").append(name).append("=").append(URLEncoder.encode(String.valueOf(value),"UTF-8"));
                    }
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
            }
        }
        url = urlBuilder.toString();
        RequestParams params = getParams(url,null,requestWork,responseWork,onResultDataListener,objects);
        ConnectDataTask connectDataTask = new ConnectDataTask(params);
        connectDataTask.doGet();
    }
    protected void requestPut(String url, RequestWork requestWork, ResponseWork responseWork, OnResultDataListener onResultDataListener, Object... objects){
        url = clientModelUrl(url);
        String postData = getPostData(requestWork);
        RequestParams params = getParams(url,postData,requestWork,responseWork,onResultDataListener,objects);
        ConnectDataTask connectDataTask = new ConnectDataTask(params);
        connectDataTask.doPut();
    }

    protected void requestDownFile(DownFileReq downFileReq, ResponseWork responseWork, OnResultDataListener onResultDataListener, Object... objects){
        RequestParams params = getParams(downFileReq.getDownloadUrl(),null,downFileReq,responseWork,onResultDataListener,objects);
        ConnectDataTask connectDataTask = new ConnectDataTask(params);
        connectDataTask.downFile();
    }

    protected String clientModelUrl(String url){
        if(!url.startsWith("http") && getApiConfig()!=null){
            url = getApiConfig().hostName+url;
        }
        return url;
    }

    private String getPostData(RequestWork requestWork){
        String postData = null;
        if(requestWork!=null){
            postData = ObjectMapperFactory.getObjectMapper().model2JsonStr(requestWork);
        }
        if(getApiConfig()!=null && getApiConfig().paramObj!=null){
            Object paramObj = getApiConfig().paramObj;
            String paramData = ObjectMapperFactory.getObjectMapper().model2JsonStr(paramObj);
            if(!TextUtils.isEmpty(paramData)){
                if(TextUtils.isEmpty(postData)){
                    postData = paramData;
                }else{
                    try {
                        JSONObject paramJson = new JSONObject(paramData);
                        JSONObject postJson = new JSONObject(postData);
                        Iterator<String> iterator = paramJson.keys();
                        while (iterator.hasNext()){
                            String key = iterator.next();
                            if(!postJson.has(key)){//防止覆盖
                                String val = paramJson.optString(key);
                                postJson.put(key,val);
                            }
                        }
                        postJson.put("appid",SharedUtils.getString("YzAppId"));
                        postData = postJson.toString();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
        return postData;
    }

    /**
     * 根据属性名获取属性值
     * */
    private Object getFieldValueByName(String fieldName, Object o) {
        try {
            String firstLetter = fieldName.substring(0, 1).toUpperCase();
            String getter = "get" + firstLetter + fieldName.substring(1);
            Method method = o.getClass().getMethod(getter);
            return method.invoke(o);
        } catch (Exception e) {
//            e.printStackTrace();
        }
        return null;
    }

    private RequestParams getParams(String url, String postData, RequestWork requestWork, ResponseWork responseWork, OnResultDataListener onResultDataListener, Object... objects){
        RequestParams params = new RequestParams(url);
        params.req = requestWork;
        params.resp = responseWork;
        params.addHeader("Accept", "application/json");
        params.addHeader("Content-Type", "application/json;charset=UTF-8");
        params.addHeader("token", SharedUtils.getString("userToken"));
        params.addHeader("appid", SharedUtils.getString("YzAppId"));
        if(requestWork!=null){
            Map<String,String> header = requestWork.getRequestHeader();
            if(header!=null && header.size()>0){
                for (String key : header.keySet()) {
                    String val = header.get(key);
                    params.addHeader(key, val);
                }
            }
        }
        params.onResultDataListener = onResultDataListener;
        params.putParams(objects);
        if(SLog.debug) SLog.d("Request Data:" + postData);
        if(postData!=null){
            params.postData = postData.getBytes();
        }
        return params;
    }

    public static void setApiConfig(ApiClient.ApiConfig apiConfig) {
        ApiConfig = apiConfig;
    }

    public static ApiClient.ApiConfig getApiConfig() {
        return ApiConfig;
    }

    public static class ApiConfig{

        private String hostName;
        private Object paramObj;

        public ApiConfig setHostName(String hostName) {
            this.hostName = hostName;
            return this;
        }

        public ApiConfig setParamObj(Object paramObj) {
            this.paramObj = paramObj;
            return this;
        }

        public Object getParamObj() {
            return paramObj;
        }
    }
}
