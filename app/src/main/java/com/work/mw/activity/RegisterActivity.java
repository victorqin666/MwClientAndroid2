package com.work.mw.activity;

import android.os.Handler;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;

import androidx.core.content.ContextCompat;

import com.dd.processbutton.iml.ActionProcessButton;
import com.http.network.model.RequestWork;
import com.http.network.model.ResponseWork;
import com.mwim.qcloud.tim.uikit.base.BaseActivity;
import com.mwim.qcloud.tim.uikit.business.active.WebActivity;
import com.mwim.qcloud.tim.uikit.business.dialog.ConfirmDialog;
import com.mwim.qcloud.tim.uikit.business.modal.UserApi;
import com.work.api.open.Yz;
import com.work.api.open.model.LoginResp;
import com.work.api.open.model.RegisterReq;
import com.work.api.open.model.SendSmsReq;
import com.work.api.open.model.client.OpenData;
import com.work.mw.R;
import com.work.util.RegularUtils;
import com.work.util.ToastUtil;

/**
 * Created by tangyx
 * Date 2020/9/7
 * email tangyx@live.com
 */

public class RegisterActivity extends BaseActivity implements View.OnClickListener, TextWatcher {

    private ActionProcessButton mSubmit;
    private EditText mPhone;
    private EditText mSmsCode;
    private TextView mSend;
    private EditText mPassword;
    private EditText mConfirmPassword;
    private CheckBox mChecked;
    private Handler mHandler;
    private int mTimer=60;
    private boolean isRegister = true;

    @Override
    public void onInitView() throws Exception {
        super.onInitView();
        mSubmit = findViewById(R.id.submit);
        mSmsCode = findViewById(R.id.sms_code);
        mPhone = findViewById(R.id.phone);
        mSend = findViewById(R.id.send);
        mPassword = findViewById(R.id.password);
        mConfirmPassword = findViewById(R.id.confirm_password);
        mChecked = findViewById(R.id.checked);
    }

    @Override
    public void onInitValue() throws Exception {
        super.onInitValue();
        setSubEnable(false);
        findViewById(R.id.agree).setOnClickListener(this);
        findViewById(R.id.conceal).setOnClickListener(this);
        mSubmit.setOnClickListener(this);
        mSmsCode.addTextChangedListener(this);
        mSend.setOnClickListener(this);
        mPhone.addTextChangedListener(this);
        mPassword.addTextChangedListener(this);
        mConfirmPassword.addTextChangedListener(this);
        mHandler = new Handler(getMainLooper());
        isRegister = getIntent().getBooleanExtra(RegisterActivity.class.getSimpleName(),true);
        if(!isRegister){
            setTitleName(R.string.activity_forgetpassword);
            findViewById(R.id.xy_layout).setVisibility(View.GONE);
            mChecked.setChecked(true);
        }
    }
    private void setSubEnable(boolean enable){
        if(enable){
            mSubmit.setBackgroundCompat(ContextCompat.getDrawable(this,R.drawable.shape_2da0f0_radius_5));
            mSubmit.setEnabled(true);
        }else{
            mSubmit.setBackgroundCompat(ContextCompat.getDrawable(this,R.drawable.shape_dbe7ef_radius_5));
            mSubmit.setEnabled(false);
        }
    }

    @Override
    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

    }

    @Override
    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        setSubEnable(!TextUtils.isEmpty(mPassword.getText().toString().trim())&&!TextUtils.isEmpty(mConfirmPassword.getText().toString().trim())&&!TextUtils.isEmpty(mPhone.getText().toString().trim())&!TextUtils.isEmpty(mSmsCode.getText().toString().trim()));
    }

    @Override
    public void afterTextChanged(Editable editable) {

    }

    @Override
    public void onClick(View view) {
        String phone = mPhone.getText().toString().trim();
        switch (view.getId()){
            case R.id.send:
                if(TextUtils.isEmpty(phone)){
                    ToastUtil.error(this,mPhone.getHint().toString());
                    break;
                }
                mSend.setEnabled(false);
                mSend.setText(R.string.text_sending);
                SendSmsReq sendSmsReq = new SendSmsReq();
                sendSmsReq.setMobile(phone);
                if(isRegister){
                    sendSmsReq.setCode(1);
                }else{
                    sendSmsReq.setCode(2);
                }
                Yz.getSession().sendSms(sendSmsReq,this);
                break;
            case R.id.agree:
                WebActivity.startWebView(getString(R.string.text_agreement));
                break;
            case R.id.conceal:
                WebActivity.startWebView(getString(R.string.text_conceal));
                break;
            case R.id.submit:
                String password = mPassword.getText().toString().trim();
                if(!RegularUtils.isPassword(password)){
                    ToastUtil.error(this,R.string.hint_password);
                    return;
                }
                String confirmPassword = mConfirmPassword.getText().toString().trim();
                if(!password.equals(confirmPassword)){
                    ToastUtil.error(this,R.string.toast_password_error);
                    break;
                }
                if(!mChecked.isChecked()){
                    new ConfirmDialog().setContent("请先同意<font color='#3a89f0'>《服务协议》</font>和<font color='#3a89f0'>《隐私政策》</font>")
                            .setConfirmTextResId(R.string.text_agree)
                            .setOnConfirmListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    mChecked.setChecked(true);
                                }
                            }).show(getSupportFragmentManager(),"cancel");
                    return;
                }
                String smsCode = mSmsCode.getText().toString().trim();
                RegisterReq registerReq = new RegisterReq();
                registerReq.setPassword(password);
                registerReq.setMobile(phone);
                registerReq.setSmsCode(smsCode);
                mSubmit.setEnabled(false);
                mSubmit.setProgress(50);
                if(isRegister){
                    Yz.getSession().register(registerReq,this);
                }else{
                    Yz.getSession().resetPwd(registerReq,this);
                }
                break;
        }
    }

    /**
     * 启动倒计时
     */
    Runnable runnable = new Runnable() {
        @Override
        public void run() {
            if(mTimer>0){
                mTimer--;
                mSend.setText(getString(R.string.text_sms_timer,mTimer));
                mHandler.postDelayed(this,1000);
            }else{
                mHandler.removeCallbacks(this);
                mSend.setEnabled(true);
                mSend.setText(R.string.text_retry_sms);
                mTimer = 60;
            }
        }
    };

    @Override
    public void onResult(RequestWork req, ResponseWork resp) throws Exception {
        super.onResult(req, resp);
        if(req instanceof SendSmsReq){
            if(resp.isSuccess()){
                mSend.setText(R.string.text_send_success);
                mHandler.postDelayed(runnable,1000);
                mSmsCode.requestFocus();
            }else{
                mSend.setText(R.string.text_send_fail);
                mSend.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        mSend.setEnabled(true);
                        mSend.setText(R.string.text_retry_sms);
                    }
                },1000);
                ToastUtil.warning(this,resp.getMessage());
            }
        }else if(resp instanceof LoginResp){
            mSubmit.setEnabled(true);
            mSubmit.setProgress(0);
            if(resp.isSuccess()){
                OpenData data = ((LoginResp) resp).getData();
                if(data!=null){
                    UserApi userApi = UserApi.instance();
                    userApi.setUserId(data.getUserId());
                    userApi.setUserSign(data.getUserSign());
                    RegisterReq registerReq = (RegisterReq) req;
                    userApi.setMobile(registerReq.getMobile());
                }
                onBackPressed();
            }else{
                ToastUtil.warning(this,resp.getMessage());
            }
        }else if(req instanceof RegisterReq){
            mSubmit.setEnabled(true);
            mSubmit.setProgress(0);
            if(resp.isSuccess()){
                ToastUtil.success(this,R.string.toast_reset_success);
                onBackPressed();
            }else{
                ToastUtil.warning(this,resp.getMessage());
            }
        }
    }
}
