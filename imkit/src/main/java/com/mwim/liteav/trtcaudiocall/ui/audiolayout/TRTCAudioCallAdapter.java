package com.mwim.liteav.trtcaudiocall.ui.audiolayout;

import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.mwim.liteav.login.UserModel;
import com.mwim.qcloud.tim.uikit.R;
import com.mwim.qcloud.tim.uikit.business.modal.UserApi;
import com.mwim.qcloud.tim.uikit.component.picture.imageEngine.impl.GlideEngine;
import com.work.util.SizeUtils;

import java.util.List;

/**
 * Created by tangyx
 * Date 2020/11/5
 * email tangyx@live.com
 */

public class TRTCAudioCallAdapter extends BaseQuickAdapter<UserModel, BaseViewHolder> {

    private boolean isC2C;
    private int avatarSize;
    private String sponsorUserId;

    public TRTCAudioCallAdapter(@Nullable List<UserModel> data) {
        super(R.layout.adapter_trtc_audio_call,data);
    }

    @Override
    protected void convert(BaseViewHolder helper, UserModel item) {
        FrameLayout mAvatarLayout = helper.getView(R.id.avatar_layout);
        View mLoadingView = helper.getView(R.id.loading_view);
        TextView mLoadingText = helper.getView(R.id.loading_text);
        TextView mName = helper.getView(R.id.name);
        mName.setText(item.userName);
        if(isC2C){//是单聊
            if(avatarSize==0){
                avatarSize = SizeUtils.dp2px(getContext(),160);
                LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) mAvatarLayout.getLayoutParams();
                if(params!=null){
                    params.width = avatarSize;
                    params.height = avatarSize;
                }
                mLoadingText.setText("正在呼叫...");
                FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,ViewGroup.LayoutParams.WRAP_CONTENT);
                layoutParams.gravity = Gravity.CENTER;
                mLoadingView.setLayoutParams(layoutParams);
                mName.setTextSize(TypedValue.COMPLEX_UNIT_SP,24);
            }
        }

        ImageView mAvatar = helper.getView(R.id.avatar);
        GlideEngine.loadCornerAvatar(mAvatar, item.userAvatar);
        View mLoadingFg = helper.getView(R.id.fl_bg);
        if(item.loading
                && !item.userId.equals(UserApi.instance().getUserId())
                && !item.userId.equals(sponsorUserId)){
            mLoadingText.setVisibility(View.VISIBLE);
            mLoadingView.setVisibility(View.VISIBLE);
            mLoadingFg.setVisibility(View.VISIBLE);
        }else{
            mLoadingText.setVisibility(View.GONE);
            mLoadingView.setVisibility(View.GONE);
            mLoadingFg.setVisibility(View.GONE);
        }
    }
    public UserModel findAudioCallLayout(String userId){
        if (userId == null) return null;
        for (UserModel model : getData()) {
            if (model.userId.equals(userId)) {
                return model;
            }
        }
        return null;
    }
    public int findAudioCallIndex(String userId){
        if (userId == null) return -1;
        for (int i = 0;i<getData().size();i++) {
            UserModel model = getData().get(i);
            if (model.userId.equals(userId)) {
                return i;
            }
        }
        return -1;
    }

    public void setC2C(boolean c2C) {
        isC2C = c2C;
    }

    public void setSponsorUserId(String sponsorUserId) {
        this.sponsorUserId = sponsorUserId;
    }
}
