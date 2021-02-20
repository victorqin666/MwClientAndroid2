package com.mwim.qcloud.tim.uikit.business.fragment;

import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.http.network.listener.OnResultDataListener;
import com.http.network.model.RequestWork;
import com.http.network.model.ResponseWork;
import com.mwim.qcloud.tim.uikit.R;
import com.mwim.qcloud.tim.uikit.YzIMKitAgent;
import com.mwim.qcloud.tim.uikit.base.BaseActivity;
import com.mwim.qcloud.tim.uikit.business.active.ScanIMQRCodeActivity;
import com.mwim.qcloud.tim.uikit.business.active.UserInfoActivity;
import com.mwim.qcloud.tim.uikit.business.active.UserQRCodeActivity;
import com.mwim.qcloud.tim.uikit.business.active.UserSettingActivity;
import com.mwim.qcloud.tim.uikit.business.active.WebActivity;
import com.mwim.qcloud.tim.uikit.business.message.CustomMessage;
import com.mwim.qcloud.tim.uikit.business.modal.UserApi;
import com.mwim.qcloud.tim.uikit.component.LineControllerView;
import com.mwim.qcloud.tim.uikit.component.picture.imageEngine.impl.GlideEngine;
import com.mwim.qcloud.tim.uikit.utils.IMKitConstants;
import com.work.api.open.Yz;
import com.work.api.open.model.LoginReq;
import com.work.api.open.model.LoginResp;
import com.work.api.open.model.client.OpenData;

public class ProfileLayout extends LinearLayout implements View.OnClickListener {

    private ImageView mUserIcon;
    private TextView mNickname;
    private ImageView mGender;
    private TextView mLabel;
    private TextView mSubMessage;

    private LineControllerView mDepartment;
    private LineControllerView mPosition;
    private LineControllerView mCard;
    private LineControllerView mMobile;
    private LineControllerView mEmail;

    public ProfileLayout(Context context) {
        super(context);
        init();
    }

    public ProfileLayout(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public ProfileLayout(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        inflate(getContext(), R.layout.profile_layout, this);
        mUserIcon = findViewById(R.id.self_icon);
        mNickname = findViewById(R.id.self_account);
        mGender = findViewById(R.id.gender);
        mLabel = findViewById(R.id.label);
        mSubMessage = findViewById(R.id.sub_message);
        findViewById(R.id.user_bg_layout).setOnClickListener(this);
        mDepartment = findViewById(R.id.modify_department);
        mDepartment.setLeftDrawable(R.drawable.icon_department_fill);
        mPosition = findViewById(R.id.modify_position);
        mPosition.setLeftDrawable(R.drawable.icon_position_fill);
        mCard = findViewById(R.id.modify_card);
        mCard.setLeftDrawable(R.drawable.icon_employee_id);

        mMobile = findViewById(R.id.modify_mobile);
        mMobile.setLeftDrawable(R.drawable.icon_mobile_fill);

        mEmail = findViewById(R.id.modify_email);
        mEmail.setLeftDrawable(R.drawable.icon_mail_fill);

        LineControllerView mQRCode = findViewById(R.id.modify_qrcode);
        mQRCode.setLeftDrawable(R.drawable.icon_qrcode_fill);
        findViewById(R.id.qr_code).setOnClickListener(this);
        mQRCode.setOnClickListener(this);

        LineControllerView mScan = findViewById(R.id.modify_scan);
        mScan.setLeftDrawable(R.drawable.icon_scan_fill);
        mScan.setOnClickListener(this);

        LineControllerView mAgreement = findViewById(R.id.modify_agreement);
        mAgreement.setLeftDrawable(R.drawable.icon_agreement_fill);
        mAgreement.setOnClickListener(this);

        LineControllerView mConceal = findViewById(R.id.modify_conceal);
        mConceal.setLeftDrawable(R.drawable.icon_conceal_fill);
        mConceal.setOnClickListener(this);

        LineControllerView mModifySettingView = findViewById(R.id.modify_setting);
        mModifySettingView.setCanNav(true);
        mModifySettingView.setOnClickListener(this);
        mModifySettingView.setLeftDrawable(R.drawable.icon_setting_fill);

        updateProfile();
        LoginReq loginReq = new LoginReq();
        loginReq.setUserId(UserApi.instance().getUserId());
        Yz.getSession().getUserByUserId(loginReq,new OnResultDataListener() {
            @Override
            public void onResult(RequestWork req, ResponseWork resp) throws Exception{
                if(getContext() instanceof BaseActivity){
                    ((BaseActivity) getContext()).onResult(req,resp);
                }
                if(resp.isSuccess() && resp instanceof LoginResp){
                    OpenData data = ((LoginResp) resp).getData();
                    UserApi userApi = UserApi.instance();
                    userApi.setNickName(data.getNickName());
                    userApi.setUserIcon(data.getUserIcon());
                    userApi.setMobile(data.getMobile());
                    userApi.setDepartName(data.getDepartName());
                    userApi.setPosition(data.getPosition());
                    userApi.setCard(data.getCard());
                    userApi.setEmail(data.getEmail());
                    userApi.setCity(data.getCity());
                    userApi.setGender(data.getGender());
                    userApi.setUserSignature(data.getUserSignature());
                    updateProfile();
                }
            }
        });

        findViewById(R.id.custom_im_message).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if(v.getId() == R.id.user_bg_layout){
            getContext().startActivity(new Intent(getContext(), UserInfoActivity.class));
        }else if(v.getId() == R.id.modify_setting){
            getContext().startActivity(new Intent(getContext(), UserSettingActivity.class));
        }else if(v.getId() == R.id.custom_im_message){
            CustomMessage message = new CustomMessage();
            message.setBusinessID(IMKitConstants.BUSINESS_ID_CUSTOM_CARD);
            message.setLogo("https://yzkj-im.oss-cn-beijing.aliyuncs.com/user/16037885020911603788500745.png");
            message.setDesc("欢迎加入元讯大家庭！欢迎加入元讯大家庭！欢迎加入元讯大家庭！欢迎加入元讯大家庭！");
            message.setTitle("元讯IM生态工具元讯IM生态工具元讯IM生态工具元讯IM生态工具元讯IM生态工具");
            message.setLink("http://yzmsri.com/");
            message.setBusinessID(IMKitConstants.BUSINESS_ID_CUSTOM_CARD);
            YzIMKitAgent.instance().startCustomMessage(message);
        }else if(v.getId() == R.id.qr_code || v.getId()==R.id.modify_qrcode){
            getContext().startActivity(new Intent(getContext(), UserQRCodeActivity.class));
        }else if(v.getId() == R.id.modify_scan){
            getContext().startActivity(new Intent(getContext(), ScanIMQRCodeActivity.class));
        }else if(v.getId() == R.id.modify_agreement){
            WebActivity.startWebView(getResources().getString(R.string.text_agreement));
        }else if(v.getId() == R.id.modify_conceal){
            WebActivity.startWebView(getResources().getString(R.string.text_conceal));
        }
    }

    public void updateProfile() {
        UserApi userApi = UserApi.instance();
        // 头像
        String mIconUrl = userApi.getUserIcon();
        if (!TextUtils.isEmpty(mIconUrl)) {
            GlideEngine.loadCornerAvatar(mUserIcon, mIconUrl);
        }else{
            GlideEngine.loadImage(mUserIcon, R.drawable.default_user_me);
        }
        // 昵称
        String nickName = userApi.getNickName();
        //手机号
        String phone = userApi.getMobile();
        mNickname.setText(TextUtils.isEmpty(nickName)?phone:nickName);
        mSubMessage.setText(TextUtils.isEmpty(userApi.getUserSignature())?getResources().getString(R.string.hint_user_info_input):userApi.getUserSignature());
        int gender = userApi.getGender();
        mGender.setVisibility(GONE);
        if(gender==1){
            mGender.setVisibility(VISIBLE);
            mGender.setImageResource(R.drawable.icon_male_fill);
        }else if(gender==2){
            mGender.setVisibility(VISIBLE);
            mGender.setImageResource(R.drawable.icon_female_fill);
        }
        String city = userApi.getCity();
        mLabel.setText(TextUtils.isEmpty(city)?getResources().getString(R.string.hint_user_info_setting):city);
        String contentDefault = getResources().getString(R.string.user_content_default);
        String email = userApi.getEmail();
        String department = userApi.getDepartName();
        String position = userApi.getPosition();
        String card = userApi.getCard();
        mDepartment.setContent(TextUtils.isEmpty(department)?contentDefault:department);
        mPosition.setContent(TextUtils.isEmpty(position)?contentDefault:position);
        mCard.setContent(TextUtils.isEmpty(card)?contentDefault:card);
        mEmail.setContent(TextUtils.isEmpty(email)?contentDefault:email);
        mMobile.setContent(TextUtils.isEmpty(phone)?contentDefault:phone);
    }

}
