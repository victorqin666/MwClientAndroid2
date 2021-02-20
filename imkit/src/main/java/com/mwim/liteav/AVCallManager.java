package com.mwim.liteav;

import android.content.Context;

import com.mwim.liteav.trtcaudiocall.ui.TRTCAudioCallActivity;
import com.mwim.liteav.trtcvideocall.ui.TRTCVideoCallActivity;
import com.mwim.liteav.trtcvideocall.ui.TRTCVideoCallSingleActivity;
import com.mwim.qcloud.tim.uikit.config.TUIKitConfigs;
import com.mwim.liteav.login.ProfileManager;
import com.mwim.liteav.login.UserModel;
import com.mwim.liteav.model.ITRTCAVCall;
import com.mwim.liteav.model.TRTCAVCallImpl;
import com.mwim.liteav.model.TRTCAVCallListener;
import com.work.util.SLog;

import java.util.List;
import java.util.Map;

public class AVCallManager {
    private static class AVCallManagerHolder {
        private static final AVCallManager avCallManager = new AVCallManager();
    }

    public static AVCallManager getInstance() {
        return AVCallManager.AVCallManagerHolder.avCallManager;
    }

    private Context mContext;
    private ITRTCAVCall mITRTCAVCall;
    private TRTCAVCallListener mTRTCAVCallListener = new TRTCAVCallListener() {
        // <editor-fold  desc="视频监听代码">
        @Override
        public void onError(int code, String msg) {
        }

        @Override
        public void onInvited(String sponsor, final List<String> userIdList, boolean isFromGroup, final int callType) {
            processInvite(sponsor, userIdList, callType);
        }

        @Override
        public void onGroupCallInviteeListUpdate(List<String> userIdList) {

        }

        @Override
        public void onUserEnter(String userId) {

        }

        @Override
        public void onUserLeave(String userId) {

        }

        @Override
        public void onReject(String userId) {

        }

        @Override
        public void onNoResp(String userId) {

        }

        @Override
        public void onLineBusy(String userId) {

        }

        @Override
        public void onCallingCancel() {

        }

        @Override
        public void onCallingTimeout() {

        }

        @Override
        public void onCallEnd() {

        }

        @Override
        public void onUserVideoAvailable(String userId, boolean isVideoAvailable) {

        }

        @Override
        public void onUserAudioAvailable(String userId, boolean isVideoAvailable) {

        }

        @Override
        public void onUserVoiceVolume(Map<String, Integer> volumeMap) {

        }
        // </editor-fold  desc="视频监听代码">
    };

    private void processInvite(String sponsor, final List<String> userIdList, final int callType) {
        ProfileManager.getInstance().getUserInfoByUserId(sponsor, new ProfileManager.GetUserInfoCallback() {
            @Override
            public void onSuccess(final UserModel model) {
                if (userIdList == null || userIdList.size() == 0) {
                    if (callType == ITRTCAVCall.TYPE_VIDEO_CALL) {
                        TRTCVideoCallSingleActivity.startBeingCall(mContext, model, null);
                    } else if (callType == ITRTCAVCall.TYPE_AUDIO_CALL) {
                        TRTCAudioCallActivity.startBeingCall(mContext, model, null);
                    }
                } else {
                    ProfileManager.getInstance().getUserInfoBatch(userIdList, new ProfileManager.GetUserInfoBatchCallback() {
                        @Override
                        public void onSuccess(List<UserModel> modelList) {
                            if (callType == ITRTCAVCall.TYPE_VIDEO_CALL) {
                                TRTCVideoCallActivity.startBeingCall(mContext, model, modelList);
                            } else {
                                TRTCAudioCallActivity.startBeingCall(mContext, model, modelList);
                            }
                        }

                        @Override
                        public void onFailed(int code, String msg) {
                            SLog.e( "getUserInfoBatch failed:" + code + ", desc:" + msg);
                        }
                    });
                }
            }

            @Override
            public void onFailed(int code, String msg) {
                SLog.e("getUserInfoByUserId failed:" + code + ", desc:" + msg);
            }
        });
    }

    private AVCallManager() {
    }

    public void init(Context context) {
        mContext = context;
        initVideoCallData();
    }

    public void unInit() {
        if (mITRTCAVCall != null) {
            mITRTCAVCall.removeListener(mTRTCAVCallListener);
        }
        TRTCAVCallImpl.destroySharedInstance();
    }

    private void initVideoCallData() {
        mITRTCAVCall = TRTCAVCallImpl.sharedInstance(mContext);
        mITRTCAVCall.init();
        mITRTCAVCall.addListener(mTRTCAVCallListener);
        int    appid   = TUIKitConfigs.getConfigs().getGeneralConfig().getSDKAppId();
        String userId  = ProfileManager.getInstance().getUserModel().userId;
        String userSig = ProfileManager.getInstance().getUserModel().userSig;
        mITRTCAVCall.login(appid, userId, userSig, null);
    }
}
