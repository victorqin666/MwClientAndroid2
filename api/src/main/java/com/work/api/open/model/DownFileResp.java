package com.work.api.open.model;

import android.text.TextUtils;

import com.http.network.RequestParams;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * create by tangyx
 * 2018/9/20
 */
public class DownFileResp extends BaseResp {

    private InputStream inputStream;
    private String saveFilePath;

    public void setSaveFilePath(String saveFilePath) {
        this.saveFilePath = saveFilePath;
    }

    public String getSaveFilePath() {
        return saveFilePath;
    }

    public void setInputStream(InputStream inputStream) {
        this.inputStream = inputStream;
    }

    public InputStream getInputStream() {
        return inputStream;
    }

    @Override
    public void onResultData(RequestParams params, Object result) {
        if(result!=null){
            setCode(100);
            if(params.req instanceof DownFileReq){
                String dir = ((DownFileReq) params.req).getDirFile();
                String fileName = ((DownFileReq) params.req).getFileName();
                if(!TextUtils.isEmpty(dir) && !TextUtils.isEmpty(fileName)){
                    setSaveFilePath(saveImage(dir,fileName, (InputStream) result));
                }else{
                    setInputStream((InputStream) result);
                }
            }
        }
    }
    public String saveImage(String dir,String imageName,InputStream inputStream) {
        File file = new File(dir);
        if(!file.exists()){
            file.mkdir();
        }
        dir += "/"+imageName;
        FileOutputStream fileOutputStream = null;
        try {
            fileOutputStream = new FileOutputStream(dir);
            byte[] buffer = new byte[1024 * 8];
            int tem;
            while((tem = inputStream.read(buffer))!=-1){
                fileOutputStream.write(buffer,0,tem);
            }
            fileOutputStream.flush();
            return dir;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            if(fileOutputStream!=null){
                try {
                    fileOutputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if(inputStream!=null){
                try {
                    inputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return null;
    };
}
