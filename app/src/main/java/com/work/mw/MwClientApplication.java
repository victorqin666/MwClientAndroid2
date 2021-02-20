package com.work.mw;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;

import androidx.multidex.MultiDex;
import androidx.multidex.MultiDexApplication;

import com.mwim.qcloud.tim.uikit.YzIMKitAgent;
import com.mwim.qcloud.tim.uikit.business.inter.YzStatusListener;
import com.mwim.qcloud.tim.uikit.business.modal.WorkApp;
import com.work.mw.wemeet.RdmSDK;
import com.work.mw.wemeet.WemeetSdkHelper;
import com.mwim.qcloud.tim.uikit.business.modal.UserApi;
import com.tencent.wemeet.sdk.app.AppGlobals;
import com.work.api.open.ApiClient;
import com.work.mw.activity.LoginActivity;

/**
 * Created by tangyx
 * Date 2020/8/15
 * email tangyx@live.com
 */

public class MwClientApplication extends MultiDexApplication {

    @Override
    public void onCreate() {
        super.onCreate();
        if(!getApplicationContext().getPackageName().equals
                (getCurrentProcessName())){
            return;
        }
        onChannel();
        MultiDex.install(this);

        YzIMKitAgent.init(this,"de241446a50499bb77a8684cf610fd04");
        //账号被踢出，或者失效
        YzIMKitAgent.instance().addStatusListener(new YzStatusListener() {
            @Override
            public void logout() {
                super.logout();
                WemeetSdkHelper.logout();
                Intent intent = new Intent(MwClientApplication.this, LoginActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
            }
        });
        YzIMKitAgent.instance().addWorkAppItemClickListener(item -> {
            if(item instanceof WorkApp){
                WorkApp wp = (WorkApp) item;
                if(wp.isWemeet()){
                    //先初始化会议
                    WemeetSdkHelper.init(MwClientApplication.this,wp.getWemeetToken());
                    //启动会议
                    WemeetSdkHelper.startAuth(wp.getUrl());
                }
            }
        });
        //增加消息前后台逻辑监听器
        registerActivityLifecycleCallbacks(new StatisticActivityLifecycleCallback());
        //腾讯会议
        AppGlobals.INSTANCE.init(this);
        RdmSDK.INSTANCE.init(this);
    }

    private String getCurrentProcessName() {
        int pid = android.os.Process.myPid();
        String processName = "";
        ActivityManager manager = (ActivityManager) getApplicationContext().getSystemService
                (Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningAppProcessInfo process : manager.getRunningAppProcesses()) {
            if (process.pid == pid) {
                processName = process.processName;
            }
        }
        return processName;
    }

    /**
     * 读取渠道号/版本号等信息
     */
    private void onChannel(){
        try {
            ApplicationInfo applicationInfo = this.getPackageManager().getApplicationInfo(getPackageName(), PackageManager.GET_META_DATA);
            String channel = applicationInfo.metaData.getString("UMENG_CHANNEL");
            UserApi userApi = UserApi.instance();
            userApi.setStore(channel);
            //配置网络相关
            ApiClient.setApiConfig(new ApiClient.ApiConfig().setHostName(BuildConfig.HOST_NAME).setParamObj(userApi));
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
    }


    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(base);
    }

    static class StatisticActivityLifecycleCallback implements ActivityLifecycleCallbacks {


        @Override
        public void onActivityCreated(Activity activity, Bundle bundle) {

        }

        @Override
        public void onActivityStarted(Activity activity) {
            YzIMKitAgent.instance().onActivityStarted();
        }

        @Override
        public void onActivityResumed(Activity activity) {

        }

        @Override
        public void onActivityPaused(Activity activity) {

        }

        @Override
        public void onActivityStopped(Activity activity) {
            YzIMKitAgent.instance().onActivityStopped();
        }

        @Override
        public void onActivitySaveInstanceState(Activity activity, Bundle bundle) {

        }

        @Override
        public void onActivityDestroyed(Activity activity) {

        }
    }
}
