package com.mwim.liteav.trtcvideocall.ui.videolayout;

import android.view.View;
import android.widget.FrameLayout;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.GridLayoutManager;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.mwim.liteav.login.UserModel;
import com.mwim.liteav.model.ITRTCAVCall;
import com.mwim.qcloud.tim.uikit.R;
import com.mwim.qcloud.tim.uikit.component.picture.imageEngine.impl.GlideEngine;
import com.mwim.qcloud.tim.uikit.utils.ScreenUtil;

import java.util.List;

/**
 * Created by tangyx
 * Date 2020/11/13
 * email tangyx@live.com
 */

public class TRTCVideoAdapter extends BaseQuickAdapter<UserModel, BaseViewHolder> {

    private ITRTCAVCall mITRTCAVCall;
    private int height = 0;

    public TRTCVideoAdapter(@Nullable List<UserModel> data) {
        super(R.layout.adapter_trtc_video_call,data);
    }

    @Override
    protected void convert(BaseViewHolder helper, UserModel item) {
        if(height==0){
            height = ScreenUtil.getScreenWidth(getContext()) / 2;
        }
        FrameLayout mContainer = helper.getView(R.id.container);
        GridLayoutManager.LayoutParams params = (GridLayoutManager.LayoutParams) mContainer.getLayoutParams();
        params.height = height;

        TRTCVideoLayout mVideoLayout = helper.getView(R.id.video_layout);
        GlideEngine.loadCornerAvatar(mVideoLayout.getHeadImg(),item.userAvatar);
        mVideoLayout.getUserNameTv().setText(item.userName);
        mVideoLayout.setVideoAvailable(item.videoAvailable);
        if(item.videoAvailable){
            mITRTCAVCall.startRemoteView(item.userId, mVideoLayout.getVideoView());
            if(item.videoAvailableRefresh){//重新打开摄像头
                mITRTCAVCall.closeCamera();
                item.videoAvailableRefresh = false;
            }
            mITRTCAVCall.openCamera(true, mVideoLayout.getVideoView());
        }else{
            mITRTCAVCall.stopRemoteView(item.userId);
            if(!item.loading){
                mVideoLayout.getWaiting().setVisibility(View.GONE);
            }
        }
        if(item.isSponsor){
            mVideoLayout.getFlbg().setVisibility(View.GONE);
            mVideoLayout.getWaiting().setVisibility(View.GONE);
        }
    }

    public void setITRTCAVCall(ITRTCAVCall mITRTCAVCall) {
        this.mITRTCAVCall = mITRTCAVCall;
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
}
