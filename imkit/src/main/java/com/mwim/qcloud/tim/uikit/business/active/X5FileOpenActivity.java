package com.mwim.qcloud.tim.uikit.business.active;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.text.TextUtils;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.mwim.qcloud.tim.uikit.R;
import com.mwim.qcloud.tim.uikit.base.BaseActivity;
import com.mwim.qcloud.tim.uikit.utils.FileUtil;
import com.tencent.smtt.sdk.TbsReaderView;
import com.work.util.FileUtils;
import com.work.util.SLog;


import java.io.File;

/**
 * Created by tangyx
 * Date 2020/9/18
 * email tangyx@live.com
 */

public class X5FileOpenActivity extends BaseActivity implements TbsReaderView.ReaderCallback {

    private TbsReaderView mTbsReaderView;
    private String mOpenFile;

    @Override
    public void onInitView() throws Exception {
        super.onInitView();
        LinearLayout mContainer = findViewById(R.id.container);
        mTbsReaderView = new TbsReaderView(this, this);
        mContainer.addView(mTbsReaderView, new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT));

    }

    @Override
    public void onInitValue() throws Exception {
        super.onInitValue();

        String path = getIntent().getStringExtra(X5FileOpenActivity.class.getSimpleName());
        String fileName = getIntent().getStringExtra("fileName");
        setTitleName(fileName);
        FileUtils fileUtils = new FileUtils(this);
        mOpenFile = fileUtils.getStorageDirectory()+"/"+fileName;
        boolean copy = fileUtils.copyFile(path,mOpenFile);
        Bundle bundle = new Bundle();
        bundle.putString("filePath", mOpenFile);
        bundle.putString("tempPath", Environment.getExternalStorageDirectory() + "/TbsReaderTemp");
        boolean result = mTbsReaderView.preOpen(parseFormat(mOpenFile), false);
        if(SLog.debug)SLog.e("x5 file pre open:" + result + ">>" + mOpenFile +">>"+copy+">");
        if (result) {
            mTbsReaderView.openFile(bundle);
        } else {
            File file = new File(path);
            if (file.exists()) {
                Intent openIntent = new Intent();
                openIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                String type = getMIMEType(file);
                // 设置intent的data和Type属性。
                openIntent.setDataAndType(/* uri */Uri.fromFile(file), type);
                // 跳转
                startActivity(openIntent);
                finish();
            }
        }
    }


    private String getMIMEType(File file) {

        String type = "*/*";
        String fName = file.getName();
        // 获取后缀名前的分隔符"."在fName中的位置。
        int dotIndex = fName.lastIndexOf(".");
        if (dotIndex < 0) {
            return type;
        }
        /* 获取文件的后缀名 */
        String end = fName.substring(dotIndex).toLowerCase();
        if (end.equals(""))
            return type;
        // 在MIME和文件类型的匹配表中找到对应的MIME类型。
        for (String[] strings : MIME_MapTable) {
            if (end.equals(strings[0]))
                type = strings[1];
        }
        return type;
    }

    private final String[][] MIME_MapTable = {
            // {后缀名，MIME类型}
            {".3gp", "video/3gpp"},
            {".apk", "application/vnd.android.package-archive"},
            {".asf", "video/x-ms-asf"},
            {".avi", "video/x-msvideo"},
            {".bin", "application/octet-stream"},
            {".bmp", "image/bmp"},
            {".c", "text/plain"},
            {".class", "application/octet-stream"},
            {".conf", "text/plain"},
            {".cpp", "text/plain"},
            {".doc", "application/msword"},
            {".docx",
                    "application/vnd.openxmlformats-officedocument.wordprocessingml.document"},
            {".xls", "application/vnd.ms-excel"},
            {".xlsx",
                    "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"},
            {".exe", "application/octet-stream"},
            {".gif", "image/gif"},
            {".gtar", "application/x-gtar"},
            {".gz", "application/x-gzip"},
            {".h", "text/plain"},
            {".htm", "text/html"},
            {".html", "text/html"},
            {".jar", "application/java-archive"},
            {".java", "text/plain"},
            {".jpeg", "image/jpeg"},
            {".jpg", "image/jpeg"},
            {".js", "application/x-javascript"},
            {".log", "text/plain"},
            {".m3u", "audio/x-mpegurl"},
            {".m4a", "audio/mp4a-latm"},
            {".m4b", "audio/mp4a-latm"},
            {".m4p", "audio/mp4a-latm"},
            {".m4u", "video/vnd.mpegurl"},
            {".m4v", "video/x-m4v"},
            {".mov", "video/quicktime"},
            {".mp2", "audio/x-mpeg"},
            {".mp3", "audio/x-mpeg"},
            {".mp4", "video/mp4"},
            {".mpc", "application/vnd.mpohun.certificate"},
            {".mpe", "video/mpeg"},
            {".mpeg", "video/mpeg"},
            {".mpg", "video/mpeg"},
            {".mpg4", "video/mp4"},
            {".mpga", "audio/mpeg"},
            {".msg", "application/vnd.ms-outlook"},
            {".ogg", "audio/ogg"},
            {".pdf", "application/pdf"},
            {".png", "image/png"},
            {".pps", "application/vnd.ms-powerpoint"},
            {".ppt", "application/vnd.ms-powerpoint"},
            {".pptx",
                    "application/vnd.openxmlformats-officedocument.presentationml.presentation"},
            {".prop", "text/plain"}, {".rc", "text/plain"},
            {".rmvb", "audio/x-pn-realaudio"}, {".rtf", "application/rtf"},
            {".sh", "text/plain"}, {".tar", "application/x-tar"},
            {".tgz", "application/x-compressed"}, {".txt", "text/plain"},
            {".wav", "audio/x-wav"}, {".wma", "audio/x-ms-wma"},
            {".wmv", "audio/x-ms-wmv"},
            {".wps", "application/vnd.ms-works"}, {".xml", "text/plain"},
            {".z", "application/x-compress"},
            {".zip", "application/x-zip-compressed"}, {"", "*/*"}};

    private String parseFormat(String fileName) {
        return fileName.substring(fileName.lastIndexOf(".") + 1);
    }

    @Override
    public void onCallBackAction(Integer integer, Object o, Object o1) {

    }
    public static void openX5File(Context context,String path,String fileName){
        Intent intent = new Intent(context,X5FileOpenActivity.class);
        intent.putExtra(X5FileOpenActivity.class.getSimpleName(),path);
        intent.putExtra("fileName",fileName);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mTbsReaderView.onStop();
        if(TextUtils.isEmpty(mOpenFile)){
            FileUtil.deleteFile(mOpenFile);
        }
    }
}
