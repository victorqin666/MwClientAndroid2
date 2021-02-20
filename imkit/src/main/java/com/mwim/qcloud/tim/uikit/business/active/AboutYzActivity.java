package com.mwim.qcloud.tim.uikit.business.active;

import android.view.View;
import android.widget.TextView;

import com.mwim.qcloud.tim.uikit.R;
import com.mwim.qcloud.tim.uikit.base.BaseActivity;
import com.work.util.AppUtils;

/**
 * Created by tangyx
 * Date 2020/10/21
 * email tangyx@live.com
 */

public class AboutYzActivity extends BaseActivity implements View.OnClickListener {
    @Override
    public void onInitView() throws Exception {
        super.onInitView();
        TextView mVersion = findViewById(R.id.version);
        AppUtils.AppInfo appInfo = AppUtils.getAppInfo(this);
        if(appInfo!=null){
            mVersion.setText(getString(R.string.text_version,appInfo.getVersionName()));
        }
        findViewById(R.id.agree).setOnClickListener(this);
        findViewById(R.id.conceal).setOnClickListener(this);
    }

    @Override
    public void onInitValue() throws Exception {
        super.onInitValue();
        setTitleName(R.string.user_setting_about);
    }

    @Override
    public void onClick(View view) {
        if(view.getId() == R.id.agree){
            WebActivity.startWebView(getString(R.string.text_agreement));
        }else if(view.getId() == R.id.conceal){
            WebActivity.startWebView(getString(R.string.text_conceal));
        }
    }
}
