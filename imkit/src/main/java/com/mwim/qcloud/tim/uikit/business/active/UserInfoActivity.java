package com.mwim.qcloud.tim.uikit.business.active;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import androidx.core.content.ContextCompat;

import com.http.network.model.RequestWork;
import com.http.network.model.ResponseWork;
import com.mwim.qcloud.tim.uikit.R;
import com.mwim.qcloud.tim.uikit.base.BaseActivity;
import com.mwim.qcloud.tim.uikit.business.dialog.UserAvatarDialog;
import com.mwim.qcloud.tim.uikit.business.modal.UserApi;
import com.mwim.qcloud.tim.uikit.component.LineControllerView;
import com.mwim.qcloud.tim.uikit.component.SelectionActivity;
import com.mwim.qcloud.tim.uikit.component.photoview.PhotoViewActivity;
import com.mwim.qcloud.tim.uikit.component.picture.imageEngine.impl.GlideEngine;
import com.mwim.qcloud.tim.uikit.utils.PopWindowUtil;
import com.mwim.qcloud.tim.uikit.utils.IMKitConstants;
import com.tencent.imsdk.v2.V2TIMCallback;
import com.tencent.imsdk.v2.V2TIMManager;
import com.tencent.imsdk.v2.V2TIMUserFullInfo;
import com.work.api.open.Yz;
import com.work.api.open.model.RegisterReq;
import com.work.api.open.model.UploadResp;
import com.work.api.open.model.client.OpenData;
import com.work.util.SLog;
import com.work.util.ToastUtil;
import com.workstation.crop.config.CropProperty;
/**
 * Created by tangyx
 * Date 2020/9/14
 * email tangyx@live.com
 */

public class UserInfoActivity extends BaseActivity implements View.OnClickListener {

    private ImageView mUserIcon;
    private LineControllerView mNickname;
    private LineControllerView mSex;
    private LineControllerView mCity;
    private LineControllerView mSignature;
    private LineControllerView mPhone;
    private LineControllerView mDepartment;
    private LineControllerView mPosition;
    private LineControllerView mCard;
    private LineControllerView mEmail;
    private UserAvatarDialog mUserAvatarDialog;
    private AlertDialog mDialog;

    @Override
    public void onInitView() throws Exception {
        super.onInitView();
        mUserIcon = findViewById(R.id.user_icon);
        mNickname = findViewById(R.id.modify_nick_name);
        mSex = findViewById(R.id.modify_sex);
        mCity = findViewById(R.id.modify_city);
        mSignature = findViewById(R.id.modify_signature);
        mPhone = findViewById(R.id.modify_phone);
        mDepartment = findViewById(R.id.modify_department);
        mPosition = findViewById(R.id.modify_position);
        mCard = findViewById(R.id.modify_card);
        mEmail = findViewById(R.id.modify_email);
        ImageView mRightView = findViewById(R.id.icon_arrow_right);
        mRightView.setColorFilter(ContextCompat.getColor(this,R.color.color_E4E6E9));
    }

    @Override
    public void onInitValue() throws Exception {
        super.onInitValue();
        setTitleName(R.string.user_info_title);
        findViewById(R.id.user_layout).setOnClickListener(this);
        mNickname.setOnClickListener(this);
        mSex.setOnClickListener(this);
        mCity.setOnClickListener(this);
        mSignature.setOnClickListener(this);
        mPhone.setOnClickListener(this);
        mDepartment.setCanNav(true);
        mDepartment.setOnClickListener(this);
        mPosition.setCanNav(true);
        mPosition.setOnClickListener(this);
        mCard.setCanNav(true);
        mCard.setOnClickListener(this);
        mEmail.setOnClickListener(this);
        mUserIcon.setOnClickListener(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        updateUser();
    }

    private void updateUser(){
        V2TIMUserFullInfo v2TIMUserFullInfo = new V2TIMUserFullInfo();
        UserApi userApi = UserApi.instance();
        String userIcon = userApi.getUserIcon();
        if (TextUtils.isEmpty(userIcon)) {
            GlideEngine.loadImage(mUserIcon, R.drawable.default_head);
        } else {
            v2TIMUserFullInfo.setFaceUrl(userIcon);
            GlideEngine.loadCornerAvatar(mUserIcon, userIcon);
        }
        mNickname.setContent(userApi.getNickName());
        int gender = userApi.getGender();
        switch (gender){
            case 1:
                mSex.setContent(getString(R.string.user_gender_1));
                break;
            case 2:
                mSex.setContent(getString(R.string.user_gender_2));
                break;
            default:
                mSex.setContent(getString(R.string.user_gender_0));
                break;

        }
        v2TIMUserFullInfo.setNickname(userApi.getNickName());
        mSignature.setContent(TextUtils.isEmpty(userApi.getUserSignature())?getString(R.string.hint_user_info_input):userApi.getUserSignature());
        v2TIMUserFullInfo.setSelfSignature(userApi.getUserSignature());
        mCity.setContent(TextUtils.isEmpty(userApi.getCity())?getString(R.string.hint_user_info_select):userApi.getCity());
        mPhone.setContent(userApi.getMobile());
        mDepartment.setContent(userApi.getDepartName());
        mPosition.setContent(userApi.getPosition());
        mCard.setContent(userApi.getCard());
        mEmail.setContent(userApi.getEmail());
//        HashMap<String,byte[]> customMap = new HashMap<>();
//        customMap.put("mobile",userApi.getMobile().getBytes());
//        v2TIMUserFullInfo.setCustomInfo(customMap);
        V2TIMManager.getInstance().setSelfInfo(v2TIMUserFullInfo, new V2TIMCallback() {
            @Override
            public void onError(int code, String desc) {
                SLog.e("个人信息同步失败："+code+">"+desc);
            }

            @Override
            public void onSuccess() {
                if(SLog.debug)SLog.e("个人信息同步成功");
            }
        });
    }

    private void selectAvatar(){
        if(mUserAvatarDialog==null){
            mUserAvatarDialog = new UserAvatarDialog().setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (view.getId() == R.id.camera) {
                        onOpenCamera(true);
                    }else{
                        onOpenPhoto(true);
                    }
                    mUserAvatarDialog.dismiss();
                }
            });
        }
        mUserAvatarDialog.show(getSupportFragmentManager(),"user_avatar");
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        if(id == R.id.user_layout){
            selectAvatar();
        }else if(id == R.id.user_icon){
            if(TextUtils.isEmpty(UserApi.instance().getUserIcon())){
                selectAvatar();
            }else{
                startActivity(new Intent(this, PhotoViewActivity.class).putExtra(IMKitConstants.IMAGE_DATA,UserApi.instance().getUserIcon()));
            }
        }else if (id == R.id.modify_nick_name) {
            Bundle bundle = new Bundle();
            bundle.putString(IMKitConstants.Selection.TITLE, getResources().getString(R.string.modify_nick_name));
            bundle.putString(IMKitConstants.Selection.INIT_CONTENT, UserApi.instance().getNickName());
            bundle.putInt(IMKitConstants.Selection.LIMIT, 10);
            SelectionActivity.startTextSelection(this, bundle, new SelectionActivity.OnResultReturnListener() {
                @Override
                public void onReturn(Object res) {
                    String text = (String) res;
                    showProgressLoading(false,false);
                    RegisterReq registerReq = new RegisterReq();
                    registerReq.setNickName(text);
                    Yz.getSession().update(registerReq,UserInfoActivity.this,R.id.modify_nick_name);
                }
            });
        } else if (id == R.id.modify_department) {
            Bundle bundle = new Bundle();
            bundle.putString(IMKitConstants.Selection.TITLE, getResources().getString(R.string.modify_department));
            bundle.putString(IMKitConstants.Selection.INIT_CONTENT, UserApi.instance().getDepartName());
            bundle.putInt(IMKitConstants.Selection.LIMIT, 14);
            SelectionActivity.startTextSelection(this, bundle, new SelectionActivity.OnResultReturnListener() {
                @Override
                public void onReturn(Object res) {
                    String text = (String) res;
                    showProgressLoading(false,false);
                    RegisterReq registerReq = new RegisterReq();
                    registerReq.setDepartName(text);
                    Yz.getSession().update(registerReq,UserInfoActivity.this,R.id.modify_department);
                }
            });
        }else if (id == R.id.modify_position) {
            Bundle bundle = new Bundle();
            bundle.putString(IMKitConstants.Selection.TITLE, getResources().getString(R.string.modify_im_position));
            bundle.putString(IMKitConstants.Selection.INIT_CONTENT, UserApi.instance().getPosition());
            bundle.putInt(IMKitConstants.Selection.LIMIT, 14);
            SelectionActivity.startTextSelection(this, bundle, new SelectionActivity.OnResultReturnListener() {
                @Override
                public void onReturn(Object res) {
                    String text = (String) res;
                    showProgressLoading(false,false);
                    RegisterReq registerReq = new RegisterReq();
                    registerReq.setPosition(text);
                    Yz.getSession().update(registerReq,UserInfoActivity.this,R.id.modify_position);
                }
            });
        }else if (id == R.id.modify_card) {
            Bundle bundle = new Bundle();
            bundle.putString(IMKitConstants.Selection.TITLE, getResources().getString(R.string.modify_im_card));
            bundle.putString(IMKitConstants.Selection.INIT_CONTENT, UserApi.instance().getCard());
            bundle.putInt(IMKitConstants.Selection.LIMIT, 20);
            SelectionActivity.startTextSelection(this, bundle, new SelectionActivity.OnResultReturnListener() {
                @Override
                public void onReturn(Object res) {
                    String text = (String) res;
                    showProgressLoading(false,false);
                    RegisterReq registerReq = new RegisterReq();
                    registerReq.setCard(text);
                    Yz.getSession().update(registerReq,UserInfoActivity.this,R.id.modify_card);
                }
            });
        }else if (id == R.id.modify_email) {
            Bundle bundle = new Bundle();
            bundle.putString(IMKitConstants.Selection.TITLE, getResources().getString(R.string.modify_im_email));
            bundle.putString(IMKitConstants.Selection.INIT_CONTENT, UserApi.instance().getEmail());
            bundle.putString(IMKitConstants.Selection.TYPE_INPUT, "email");
            bundle.putInt(IMKitConstants.Selection.LIMIT, 30);
            SelectionActivity.startTextSelection(this, bundle, new SelectionActivity.OnResultReturnListener() {
                @Override
                public void onReturn(Object res) {
                    String text = (String) res;
                    showProgressLoading(false,false);
                    RegisterReq registerReq = new RegisterReq();
                    registerReq.setEmail(text);
                    Yz.getSession().update(registerReq,UserInfoActivity.this,R.id.modify_email);
                }
            });
        }else if(id == R.id.modify_phone){
            startActivity(new Intent(this,UpdatePhoneActivity.class));
        }else if(id == R.id.modify_signature){
            Bundle bundle = new Bundle();
            bundle.putString(IMKitConstants.Selection.TITLE, getResources().getString(R.string.modify_signature));
            bundle.putString(IMKitConstants.Selection.INIT_CONTENT, UserApi.instance().getUserSignature());
            bundle.putInt(IMKitConstants.Selection.LIMIT, 30);
            SelectionActivity.startTextSelection(this, bundle, new SelectionActivity.OnResultReturnListener() {
                @Override
                public void onReturn(Object res) {
                    String text = (String) res;
                    showProgressLoading(false,false);
                    RegisterReq registerReq = new RegisterReq();
                    registerReq.setUserSignature(text);
                    Yz.getSession().update(registerReq,UserInfoActivity.this,R.id.modify_signature);
                }
            });
        }else if(id == R.id.modify_city){
            CityAddressActivity.startCity(UserInfoActivity.this, new SelectionActivity.OnResultReturnListener() {
                @Override
                public void onReturn(Object res) {
                    if(res instanceof String){
                        showProgressLoading(false,false);
                        RegisterReq registerReq = new RegisterReq();
                        registerReq.setCity((String) res);
                        Yz.getSession().update(registerReq,UserInfoActivity.this,R.id.modify_city);
                    }
                }
            });
        }else if(id == R.id.modify_sex){
            if(mDialog == null){
                mDialog = PopWindowUtil.buildFullScreenDialog(this);
                View moreActionView = LayoutInflater.from(this).inflate( R.layout.gender_pop_menu, null);
                moreActionView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        mDialog.dismiss();
                    }
                });
                Button btn0 = moreActionView.findViewById(R.id.gender_0);
                btn0.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        mDialog.dismiss();
                        RegisterReq registerReq = new RegisterReq();
                        registerReq.setGender(0);
                        Yz.getSession().update(registerReq, UserInfoActivity.this,R.id.modify_sex,0);
                    }
                });

                Button btn1 = moreActionView.findViewById(R.id.gender_1);
                btn1.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        mDialog.dismiss();
                        RegisterReq registerReq = new RegisterReq();
                        registerReq.setGender(1);
                        Yz.getSession().update(registerReq, UserInfoActivity.this,R.id.modify_sex,1);
                    }
                });

                Button btn2 = moreActionView.findViewById(R.id.gender_2);
                btn2.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        mDialog.dismiss();
                        RegisterReq registerReq = new RegisterReq();
                        registerReq.setGender(2);
                        Yz.getSession().update(registerReq, UserInfoActivity.this,R.id.modify_sex,2);
                    }
                });


                Button cancelBtn = moreActionView.findViewById(R.id.cancel);
                cancelBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        mDialog.dismiss();
                    }
                });
                mDialog.setContentView(moreActionView);
            }else {
                mDialog.show();
            }
        }
    }

    @Override
    public void onResult(RequestWork req, ResponseWork resp) throws Exception {
        super.onResult(req, resp);
        if(resp.isSuccess()){
            UserApi userApi = UserApi.instance();
            if(req instanceof RegisterReq){
                int viewId = resp.getPositionParams(0);
                if (viewId == R.id.modify_nick_name) {
                    userApi.setNickName(((RegisterReq) req).getNickName());
                }else if(viewId == R.id.modify_department){
                    userApi.setDepartName(((RegisterReq) req).getDepartName());
                }else if(viewId == R.id.modify_position){
                    userApi.setPosition(((RegisterReq) req).getPosition());
                }else if(viewId == R.id.modify_card){
                    userApi.setCard(((RegisterReq) req).getCard());
                }else if(viewId == R.id.modify_email){
                    userApi.setEmail(((RegisterReq) req).getEmail());
                }else if(viewId == R.id.modify_signature){
                    userApi.setUserSignature(((RegisterReq) req).getUserSignature());
                }else if(viewId == R.id.modify_sex){
                    int gender = resp.getPositionParams(1);
                    userApi.setGender(gender);
                }else if(viewId == R.id.modify_city){
                    userApi.setCity(((RegisterReq) req).getCity());
                }
                updateUser();
            }else if(resp instanceof UploadResp){
                OpenData data = ((UploadResp) resp).getData();
                if(data!=null){
                    userApi.setUserIcon(data.getUserIcon());
                    updateUser();
                    RegisterReq registerReq = new RegisterReq();
                    registerReq.setUserIcon(data.getUserIcon());
                    Yz.getSession().update(registerReq,null);
                }
            }
        }else{
            ToastUtil.warning(this,resp.getMessage());
        }
    }

    @Override
    public void onSelectImageCallback(String imagePath, CropProperty property) {
        super.onSelectImageCallback(imagePath, property);
        showProgressLoading(false,false);
        Yz.getSession().upload(imagePath,this);
    }

    @Override
    public CropProperty onAttrCropImage(CropProperty cropProperty) {
//        cropProperty.setCropShape(CropProperty.OVAL);
        return super.onAttrCropImage(cropProperty);
    }
}
