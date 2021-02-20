package com.mwim.qcloud.tim.uikit.business.thirdpush;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;

import com.huawei.hms.push.HmsMessageService;
import com.huawei.hms.push.RemoteMessage;
import com.mwim.qcloud.tim.uikit.utils.BrandUtil;
import com.work.util.SLog;


public class HUAWEIHmsMessageService extends HmsMessageService {


    @Override
    public void onMessageReceived(RemoteMessage message) {
        SLog.i( "onMessageReceived message=" + message);
    }

    @Override
    public void onMessageSent(String msgId) {
        SLog.i( "onMessageSent msgId=" + msgId);
    }

    @Override
    public void onSendError(String msgId, Exception exception) {
        SLog.i("onSendError msgId=" + msgId);
    }

    @Override
    public void onNewToken(String token) {
        SLog.i("onNewToken token=" + token);
        ThirdPushTokenMgr.getInstance().setThirdPushToken(token);
        ThirdPushTokenMgr.getInstance().setPushTokenToTIM();
    }

    @Override
    public void onTokenError(Exception exception) {
        SLog.i( "onTokenError exception=" + exception);
    }

    @Override
    public void onMessageDelivered(String msgId, Exception exception) {
        SLog.i( "onMessageDelivered msgId=" + msgId);
    }


    public static void updateBadge(final Context context, final int number) {
        if (!BrandUtil.isBrandHuawei()) {
            return;
        }
        SLog.i( "huawei badge = " + number);
        try {
            Bundle extra = new Bundle();
            extra.putString("package", "com.work.mw");
            extra.putString("class", "com.work.mw.activity.SplashActivity");
            extra.putInt("badgenumber", number);
            context.getContentResolver().call(Uri.parse("content://com.huawei.android.launcher.settings/badge/"), "change_badge", null, extra);
        } catch (Exception e) {
            SLog.w( "huawei badge exception: " + e.getLocalizedMessage());
        }
    }
}
