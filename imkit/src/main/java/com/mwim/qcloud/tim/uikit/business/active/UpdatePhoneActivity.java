package com.mwim.qcloud.tim.uikit.business.active;

import android.os.Handler;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import androidx.core.content.ContextCompat;

import com.dd.processbutton.iml.ActionProcessButton;
import com.http.network.model.RequestWork;
import com.http.network.model.ResponseWork;
import com.mwim.qcloud.tim.uikit.R;
import com.mwim.qcloud.tim.uikit.base.BaseActivity;
import com.mwim.qcloud.tim.uikit.business.modal.UserApi;
import com.work.api.open.Yz;
import com.work.api.open.model.SendSmsReq;
import com.work.api.open.model.UpdateMobileReq;
import com.work.util.ToastUtil;

/**
 * Created by tangyx
 * Date 2020/9/16
 * email tangyx@live.com
 */

public class UpdatePhoneActivity extends BaseActivity implements View.OnClickListener, TextWatcher {

    private ActionProcessButton mSubmit;
    private EditText mPhone;
    private EditText mSmsCode;
    private TextView mSend;
    private Handler mHandler;
    private int mTimer=60;

    @Override
    public void onInitView() throws Exception {
        super.onInitView();
        mSubmit = findViewById(R.id.submit);
        mSmsCode = findViewById(R.id.sms_code);
        mPhone = findViewById(R.id.mobile);
        mSend = findViewById(R.id.send);
    }

    @Override
    public void onInitValue() throws Exception {
        super.onInitValue();
        mHandler = new Handler(getMainLooper());
        setTitleName(R.string.text_change_phone);
        mSubmit.setOnClickListener(this);
        mSmsCode.addTextChangedListener(this);
        mSend.setOnClickListener(this);
        mPhone.addTextChangedListener(this);
        setSubEnable(false);
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
        setSubEnable(!TextUtils.isEmpty(mPhone.getText().toString().trim())&!TextUtils.isEmpty(mSmsCode.getText().toString().trim()));
    }

    @Override
    public void afterTextChanged(Editable editable) {

    }

    @Override
    public void onClick(View view) {
        String phone = mPhone.getText().toString().trim();
        if (view.getId() == R.id.send) {
            if(TextUtils.isEmpty(phone)){
                ToastUtil.error(this,mPhone.getHint().toString());
                return;
            }
            mSend.setEnabled(false);
            mSend.setText(R.string.text_sending);
            SendSmsReq sendSmsReq = new SendSmsReq();
            sendSmsReq.setMobile(phone);
            sendSmsReq.setCode(3);
            Yz.getSession().sendSms(sendSmsReq,this);
        }else if(view.getId() == R.id.submit){
            UpdateMobileReq registerReq = new UpdateMobileReq();
            String smsCode = mSmsCode.getText().toString().trim();
            registerReq.setSmsCode(smsCode);
            registerReq.setMobile(phone);
            registerReq.setOldMobile(UserApi.instance().getMobile());
            showProgressLoading(false,false);
            Yz.getSession().updateMobile(registerReq,this);
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
        }else if(req instanceof UpdateMobileReq){
            if(resp.isSuccess()){
                ToastUtil.success(this,R.string.toast_update_pwd_success);
                UserApi.instance().setMobile(((UpdateMobileReq) req).getMobile());
                onBackPressed();
            }else{
                ToastUtil.warning(this,resp.getMessage());
            }
        }
    }
}
