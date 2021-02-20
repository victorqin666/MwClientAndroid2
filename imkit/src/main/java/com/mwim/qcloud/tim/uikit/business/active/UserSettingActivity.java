package com.mwim.qcloud.tim.uikit.business.active;

import android.content.Intent;
import android.view.View;

import com.mwim.qcloud.tim.uikit.R;
import com.mwim.qcloud.tim.uikit.base.BaseActivity;
import com.mwim.qcloud.tim.uikit.business.dialog.ConfirmDialog;
import com.mwim.qcloud.tim.uikit.business.dialog.UpdateAppDialog;
import com.mwim.qcloud.tim.uikit.component.LineControllerView;
import com.work.util.AppUtils;
import com.work.util.CacheUtil;

/**
 * Created by tangyx
 * Date 2020/9/14
 * email tangyx@live.com
 */

public class UserSettingActivity extends BaseActivity implements View.OnClickListener {

    private LineControllerView mCache;

    @Override
    public void onInitView() throws Exception {
        super.onInitView();
        findViewById(R.id.update_pwd).setOnClickListener(this);
        findViewById(R.id.logout).setOnClickListener(this);
        findViewById(R.id.modify_about).setOnClickListener(this);
        LineControllerView mVersion = findViewById(R.id.modify_version);
        AppUtils.AppInfo appInfo = AppUtils.getAppInfo(this);
        if(appInfo!=null){
            mVersion.setContent(getString(R.string.text_version,appInfo.getVersionName()));
        }
        mVersion.setOnClickListener(this);
        mCache = findViewById(R.id.modify_cache);
        mCache.setContent(CacheUtil.getTotalCacheSize(this));
        mCache.setOnClickListener(this);
    }

    @Override
    public void onInitValue() throws Exception {
        super.onInitValue();
        setTitleName(R.string.modify_setting);
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.update_pwd) {
            startActivity(new Intent(this,UpdatePwdActivity.class));
        }else if(view.getId() == R.id.logout){
            new ConfirmDialog().setContent(R.string.dialog_logout).setOnConfirmListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    logout();
                }
            }).show(getSupportFragmentManager(),"logout");
        }else if(view.getId() == R.id.modify_cache){
            CacheUtil.clearAllCache(this);
            try {
                String c = 0+"K";
                mCache.setContent(c);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }else if(view.getId() == R.id.modify_version){
            UpdateAppDialog.showUpdateDialog(this,true);
        }else if(view.getId() == R.id.modify_about){
            startActivity(new Intent(this,AboutYzActivity.class));
        }
    }
}
