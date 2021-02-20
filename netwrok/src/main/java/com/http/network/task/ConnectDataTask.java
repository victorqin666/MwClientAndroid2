package com.http.network.task;

import android.os.AsyncTask;

import com.http.network.HttpConnectWork;
import com.http.network.HttpType;
import com.http.network.RequestParams;
import com.work.util.SLog;

public class ConnectDataTask extends AsyncTask<Object, Integer, Object>{

	private HttpConnectWork hc;
	private RequestParams params;

	public ConnectDataTask(RequestParams params){
		hc = new HttpConnectWork();
		this.params = params;
	}
	@Override
	protected void onPreExecute() {
		super.onPreExecute();
		params.start = System.currentTimeMillis();
	}
	@Override
	protected Object doInBackground(Object... param){
		HttpType method = params.method;
		hc.setSSLSocket(params.getSslSocketFactory());
		hc.setHcTimeOut(params.getTimeReadout());
		Object result=null;
		if(method == HttpType.GET){
			result = hc.getRequestManager(params.url, params.headMap);
		}else if(method == HttpType.POST){
			result = hc.postRequestManager(params.url, params.headMap, params.postData);
		}else if(method == HttpType.PUT){
			result = hc.putRequestManager(params.url,params.headMap,params.postData);
		}else if(method == HttpType.DOWNLOAD){
			result = hc.downFile(params.url,params.headMap);
		}else if(method == HttpType.UPLOAD){
			result = hc.uploadFileManager(params.url,params.getFileParams(),params.getTextParams(),params.headMap);
		}
		if(SLog.debug)SLog.v("Result data:"+result);
		params.resp.setHttpCode(hc.getCode());
		params.resp.onResultData(params,result);
		params.resp.setPositionParams(params.getParamsList());
		//对象重新变化后赋值网络状态
		params.resp.setHttpCode(hc.getCode());
		return result;
	};
	
	@Override
	protected void onPostExecute(Object result) {
		super.onPostExecute(result);
		params.end = System.currentTimeMillis();
		try {
            if (params.onResultDataListener != null) {
                params.onResultDataListener.onResult(params.req, params.resp);
            }
        }catch (Exception e){
			e.printStackTrace();
		}
	}

	@Override
	protected void onProgressUpdate(Integer... values) {
		super.onProgressUpdate(values);
	}

	/**
	 * Get请求
	 */
	public void doGet(){
		if(params==null){
			throw new NullPointerException("params can not null!");
		}
		if(SLog.debug) SLog.d("Get：" + params.url);
		params.method = HttpType.GET;
		this.execute();
	}

	/**
	 * Post请求
	 */
	public void doPost(){
		if(params==null){
			throw new NullPointerException("params can not null!");
		}
		if(SLog.debug) SLog.d("Post：" + params.url);
		params.method = HttpType.POST;
		this.execute();
	}
	/**
	 * Put请求
	 */
	public void doPut(){
		if(params==null){
			throw new NullPointerException("params can not null!");
		}
		if(SLog.debug) SLog.d("Put：" + params.url);
		params.method = HttpType.PUT;
		this.execute();
	}
	/**
	 * 下载
	 */
	public void downFile(){
		if(params==null){
			throw new NullPointerException("params can not null!");
		}
		if(SLog.debug) SLog.d("Down File：" + params.url);
		params.method = HttpType.DOWNLOAD;
		this.execute();
	}
	/**
	 * 上传
	 */
	public void uploadFile(){
		if(params==null){
			throw new NullPointerException("params can not null!");
		}
		if(SLog.debug) SLog.d("Upload File：" + params.url);
		params.method = HttpType.UPLOAD;
		this.execute();
	}
	/**
	 * 关闭网络链接
	 */
	public void discount(){
		if(hc!=null){
			hc.disconnect();
		}
	}
}
