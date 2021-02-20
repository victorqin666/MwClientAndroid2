package com.mwim.qcloud.tim.uikit.base;

import android.os.Build;
import android.view.View;
import android.view.WindowManager;

import androidx.core.content.ContextCompat;

import com.http.network.model.RequestWork;
import com.http.network.model.ResponseWork;
import com.mwim.qcloud.tim.uikit.YzIMKitAgent;
import com.mwim.qcloud.tim.uikit.R;
import com.work.api.open.model.BaseResp;
import com.work.util.ToastUtil;
import com.workstation.android.TakePhotoActivity;

public class BaseActivity extends TakePhotoActivity {
    @Override
    public void setStatusBar() {
        super.setStatusBar();
        setStatusBar(ContextCompat.getColor(this, R.color.white));
    }

    public void setStatusBar(int color) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
            getWindow().setStatusBarColor(color);
            getWindow().setNavigationBarColor(ContextCompat.getColor(this, R.color.navigation_bar_color));
            int vis = getWindow().getDecorView().getSystemUiVisibility();
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                vis |= View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR;
            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                vis |= View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR;
            }
            getWindow().getDecorView().setSystemUiVisibility(vis);
        }
    }

    public void logout() {
        YzIMKitAgent.instance().logout();
    }

    @Override
    public void onResult(RequestWork req, ResponseWork resp) throws Exception {
        super.onResult(req, resp);
        if (!resp.isSuccess()
                && resp instanceof BaseResp
                && ((BaseResp) resp).getCode() == 501) {//登录失效
            ToastUtil.info(this, "登录已失效，请重新登录。");
            logout();
        }
    }
}
