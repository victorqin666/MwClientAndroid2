package com.mwim.qcloud.tim.uikit.business.active;

import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;

import com.http.network.model.RequestWork;
import com.http.network.model.ResponseWork;
import com.mwim.qcloud.tim.uikit.R;
import com.mwim.qcloud.tim.uikit.base.BaseActivity;
import com.work.api.open.Yz;
import com.work.api.open.model.RegisterReq;
import com.work.util.RegularUtils;
import com.work.util.ToastUtil;

/**
 * Created by tangyx
 * Date 2020/9/14
 * email tangyx@live.com
 */

public class UpdatePwdActivity extends BaseActivity implements View.OnClickListener {

    private EditText mOldPassword;
    private EditText mNewPassword;
    private EditText mConfirmPassword;

    @Override
    public void onInitView() throws Exception {
        super.onInitView();
        mOldPassword = findViewById(R.id.old_password);
        mNewPassword = findViewById(R.id.new_password);
        mConfirmPassword = findViewById(R.id.confirm_password);
    }

    @Override
    public void onInitValue() throws Exception {
        super.onInitValue();
        findViewById(R.id.submit).setOnClickListener(this);
        setTitleName(R.string.user_setting_update_pwd);
    }

    @Override
    public void onClick(View view) {
        String oldPwd = mOldPassword.getText().toString().trim();
        if(TextUtils.isEmpty(oldPwd)){
            ToastUtil.error(this,mOldPassword.getHint().toString());
            return;
        }
        String newPwd = mNewPassword.getText().toString().trim();
        if(TextUtils.isEmpty(newPwd)){
            ToastUtil.error(this,mNewPassword.getHint().toString());
            return;
        }
        if(!RegularUtils.isPassword(newPwd)){
            ToastUtil.error(this,R.string.hint_password);
            return;
        }
        String confirmPwd = mConfirmPassword.getText().toString().trim();
        if(TextUtils.isEmpty(confirmPwd)){
            ToastUtil.error(this,mConfirmPassword.getHint().toString());
            return;
        }
        if(!newPwd.equals(confirmPwd)){
            ToastUtil.error(this, R.string.toast_pwd_error);
            return;
        }
        showProgressLoading(false,false);
        RegisterReq registerReq = new RegisterReq();
        registerReq.setOldPassword(oldPwd);
        registerReq.setNewPassword(newPwd);
        Yz.getSession().updatePwd(registerReq,this);
    }

    @Override
    public void onResult(RequestWork req, ResponseWork resp) throws Exception {
        super.onResult(req, resp);
        if(resp.isSuccess()){
            if(req instanceof RegisterReq){
                ToastUtil.success(this,R.string.toast_pwd_success);
                onBackPressed();
            }
        }else{
            ToastUtil.warning(this,resp.getMessage());
        }
    }
}
