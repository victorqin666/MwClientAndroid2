package com.mwim.qcloud.tim.uikit.business.dialog;

import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import com.http.network.listener.OnResultDataListener;
import com.http.network.model.RequestWork;
import com.http.network.model.ResponseWork;
import com.mwim.qcloud.tim.uikit.R;
import com.mwim.qcloud.tim.uikit.base.BaseActivity;
import com.work.api.open.Yz;
import com.work.api.open.model.GetVersionResp;
import com.work.api.open.model.client.OpenVersion;
import com.work.download.DownloadService;
import com.work.util.AppUtils;
import com.work.util.ToastUtil;


public class UpdateAppDialog extends BaseDialog implements View.OnClickListener {

    private TextView mContent;
    private OpenVersion mLatestApp;

    @Override
    public void onInitView() {
        super.onInitView();
        mContent = findViewById(R.id.content);
        findViewById(R.id.cancel).setOnClickListener(this);
        findViewById(R.id.confirm).setOnClickListener(this);
    }

    @Override
    public void onInitValue() {
        super.onInitValue();
        if(this.mLatestApp!=null){
            String content = mLatestApp.getContent();
            if(!TextUtils.isEmpty(content)){
                content = content.replaceAll("######","\n");
            }
            mContent.setText(content);
        }
    }

    public UpdateAppDialog setLatestApp(OpenVersion mLatestApp) {
        this.mLatestApp = mLatestApp;
        return this;
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.cancel) {
            dismiss();
        } else if (id == R.id.confirm) {
            dismiss();
            DownloadService.intentDownloadService(getDialogContext(), mLatestApp.getUrl());
        }
    }

    @Override
    public boolean isCanceledOnTouchOutside() {
        return false;
    }

    public static void showUpdateDialog(final BaseActivity activity, final boolean showToast){
        activity.showProgressLoading(false,false);
        Yz.getSession().getVersion(new OnResultDataListener() {
            @Override
            public void onResult(RequestWork req, ResponseWork resp) throws Exception {
                activity.onResult(req,resp);
                if(resp.isSuccess()){
                    if(resp instanceof GetVersionResp){
                        AppUtils.AppInfo appInfo = AppUtils.getAppInfo(activity);
                        OpenVersion version = ((GetVersionResp) resp).getData();
                        if(version!=null && appInfo!=null){
                            if(version.getId()>appInfo.getVersionCode()){
                                new UpdateAppDialog().setLatestApp(version).show(activity.getSupportFragmentManager(),"update_app");
                            }else if(showToast){
                                ToastUtil.success(activity,R.string.toast_app_version_up);
                            }
                        }
                    }
                }else if(showToast){
                    ToastUtil.warning(activity,resp.getMessage());
                }
            }
        });
    }
}
