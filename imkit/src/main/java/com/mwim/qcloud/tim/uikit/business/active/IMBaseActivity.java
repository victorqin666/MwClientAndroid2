package com.mwim.qcloud.tim.uikit.business.active;

import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.Nullable;
import com.mwim.qcloud.tim.uikit.YzIMKitAgent;
import com.mwim.qcloud.tim.uikit.base.BaseActivity;
import com.mwim.qcloud.tim.uikit.base.IMEventListener;
import com.mwim.qcloud.tim.uikit.utils.ClickUtils;
import com.work.util.ToastUtil;

/**
 * 登录状态的Activity都要集成该类，来完成被踢下线等监听处理。
 */
public abstract class IMBaseActivity extends BaseActivity {


    // 监听做成静态可以让每个子类重写时都注册相同的一份。
    private final IMEventListener mIMEventListener = new IMEventListener() {
        @Override
        public void onForceOffline() {
            ToastUtil.warning(IMBaseActivity.this,"您的帐号已在其它终端登录");
            logout();
        }

        @Override
        public void onUserSigExpired() {
            ToastUtil.warning(IMBaseActivity.this,"账号已过期，请重新登录");
            logout();
        }

    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        YzIMKitAgent.instance().addIMEventListener(mIMEventListener);
    }

    @Override
    protected void onStart() {
        super.onStart();
//        boolean login = UserInfo.getInstance().isAutoLogin();
//        if (!login) {
//            BaseActivity.logout(DemoApplication.instance());
//        }
    }

    @Override
    protected void onResume() {
        ClickUtils.clear();
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
    }
}
