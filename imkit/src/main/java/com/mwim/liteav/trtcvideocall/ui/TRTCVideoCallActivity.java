package com.mwim.liteav.trtcvideocall.ui;

import android.Manifest;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Vibrator;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.mwim.liteav.trtcvideocall.ui.videolayout.TRTCVideoAdapter;
import com.mwim.qcloud.tim.uikit.base.BaseActivity;
import com.mwim.qcloud.tim.uikit.business.modal.UserApi;
import com.mwim.liteav.login.ProfileManager;
import com.mwim.liteav.login.UserModel;
import com.mwim.liteav.model.ITRTCAVCall;
import com.mwim.liteav.model.IntentParams;
import com.mwim.liteav.model.TRTCAVCallImpl;
import com.mwim.liteav.model.TRTCAVCallListener;
import com.mwim.qcloud.tim.uikit.R;
import com.work.api.open.Yz;
import com.work.api.open.model.AddApplyStaticsReq;
import com.work.util.SLog;
import com.work.util.ToastUtil;
import com.workstation.permission.PermissionsResultAction;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 用于展示视频通话的主界面，通话的接听和拒绝就是在这个界面中完成的。
 *
 * @author guanyifeng
 */
public class TRTCVideoCallActivity extends BaseActivity {

    public static final int TYPE_BEING_CALLED = 1;
    public static final int TYPE_CALL = 2;

    public static final String PARAM_GROUP_ID = "group_id";
    public static final String PARAM_TYPE = "type";
    public static final String PARAM_USER = "user_model";
    public static final String PARAM_BEINGCALL_USER = "beingcall_user_model";
    public static final String PARAM_OTHER_INVITING_USER = "other_inviting_user_model";
    private static final int MAX_SHOW_INVITING_USER = 4;

    /**
     * 界面元素相关
     */
    private ImageView mMuteImg;
    private LinearLayout mMuteLl;
    private LinearLayout mHangupLl;
    private ImageView mHandsfreeImg;
    private LinearLayout mHandsfreeLl;
    private ImageView mMuteVideoImg;
    private LinearLayout mVideoLl;
    private LinearLayout mDialingLl;
    private TRTCVideoAdapter mTrtcAdapter;
    private View mShadeSponsor;
    private TextView mTimeTv;
    private Runnable mTimeRunnable;
    private int mTimeCount;
    private Handler mTimeHandler;
    private HandlerThread mTimeHandlerThread;

    /**
     * 拨号相关成员变量
     */
    private UserModel mSelfModel;
    private List<UserModel> mCallUserModelList = new ArrayList<>(); // 呼叫方
    private Map<String, UserModel> mCallUserModelMap = new HashMap<>();
    private UserModel mSponsorUserModel; // 被叫方
    private List<UserModel> mOtherInvitingUserModelList;
    private int mCallType;
    private ITRTCAVCall mITRTCAVCall;
    private boolean isHandsFree = true;
    private boolean isMuteMic = false;
    private boolean isCamera = true;
    private boolean isMuteVideo = false;
    private String mGroupId;

    private Vibrator mVibrator;
    private Ringtone mRingtone;

    /**
     * 拨号的回调
     */
    private final TRTCAVCallListener mTRTCAVCallListener = new TRTCAVCallListener() {
        @Override
        public void onError(int code, String msg) {
            //发生了错误，报错并退出该页面
            ToastUtil.error(TRTCVideoCallActivity.this, "发送错误[" + code + "]:" + msg);
            finishActivity();
        }

        @Override
        public void onInvited(String sponsor, List<String> userIdList, boolean isFromGroup, int callType) {
        }

        @Override
        public void onGroupCallInviteeListUpdate(List<String> userIdList) {
        }

        @Override
        public void onUserEnter(final String userId) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    mVibrator.cancel();
                    showCallingView();
                    UserModel layout = mTrtcAdapter.findAudioCallLayout(userId);
                    if (layout != null) {
                        layout.loading = false;
                        layout.videoAvailable = true;
                        UserModel model = mTrtcAdapter.getItem(0);
                        if(model!=null){//自己
                            model.videoAvailableRefresh = true;
                        }
                        mTrtcAdapter.notifyDataSetChanged();
                    }
//                    mITRTCAVCall.switchCamera(true);
//                    TRTCVideoLayout videoLayout = addUserToManager(model);
//                    if (videoLayout == null) {
//                        return;
//                    }
//                    videoLayout.setVideoAvailable(false);
                }
            });
        }

        @Override
        public void onUserLeave(final String userId) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    //1. 回收界面元素
//                    mLayoutManagerTrtc.recyclerCloudViewView(userId);
//                    //2. 删除用户model
//                    UserModel userModel = mCallUserModelMap.remove(userId);
//                    if (userModel != null) {
//                        mCallUserModelList.remove(userModel);
//                    }

                    int index = mTrtcAdapter.findAudioCallIndex(userId);
                    if(index!=-1){
                        mTrtcAdapter.remove(index);
                    }
                    //2. 删除用户model
                    UserModel userModel = mCallUserModelMap.remove(userId);
                    if (userModel != null) {
                        mCallUserModelList.remove(userModel);
                    }
                }
            });
        }

        @Override
        public void onReject(final String userId) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (mCallUserModelMap.containsKey(userId)) {
                        // 进入拒绝环节
                        //1. 回收界面元素
//                        mLayoutManagerTrtc.recyclerAudioCallLayout(userId);
                        int index = mTrtcAdapter.findAudioCallIndex(userId);
                        if(index!=-1){
                            mTrtcAdapter.remove(index);
                        }
                        //2. 删除用户model
                        UserModel userModel = mCallUserModelMap.remove(userId);
                        if (userModel != null) {
                            mCallUserModelList.remove(userModel);
                            ToastUtil.info(TRTCVideoCallActivity.this, userModel.userName + "拒绝通话");
                        }
                    }
                }
            });
        }

        @Override
        public void onNoResp(final String userId) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (mCallUserModelMap.containsKey(userId)) {
                        // 进入无响应环节
                        //1. 回收界面元素
//                        mLayoutManagerTrtc.recyclerCloudViewView(userId);
                        int index = mTrtcAdapter.findAudioCallIndex(userId);
                        if(index!=-1){
                            mTrtcAdapter.remove(index);
                        }
                        //2. 删除用户model
                        UserModel userModel = mCallUserModelMap.remove(userId);
                        if (userModel != null) {
                            mCallUserModelList.remove(userModel);
                            ToastUtil.info(TRTCVideoCallActivity.this, userModel.userName + "无响应");
                        }
                    }
                }
            });
        }

        @Override
        public void onLineBusy(String userId) {
            if (mCallUserModelMap.containsKey(userId)) {
                // 进入无响应环节
                //1. 回收界面元素
//                mLayoutManagerTrtc.recyclerCloudViewView(userId);
                int index = mTrtcAdapter.findAudioCallIndex(userId);
                if(index!=-1){
                    mTrtcAdapter.remove(index);
                }
                //2. 删除用户model
                UserModel userModel = mCallUserModelMap.remove(userId);
                if (userModel != null) {
                    mCallUserModelList.remove(userModel);
                    ToastUtil.info(TRTCVideoCallActivity.this, userModel.userName + "忙线");
                }
            }
        }

        @Override
        public void onCallingCancel() {
            if (mSponsorUserModel != null) {
                ToastUtil.info(TRTCVideoCallActivity.this, mSponsorUserModel.userName + " 取消了通话");
            }
            finishActivity();
        }

        @Override
        public void onCallingTimeout() {
            if (mSponsorUserModel != null) {
                ToastUtil.info(TRTCVideoCallActivity.this, mSponsorUserModel.userName + " 通话超时");
            }
            finishActivity();
        }

        @Override
        public void onCallEnd() {
            finishActivity();
        }

        @Override
        public void onUserVideoAvailable(final String userId, final boolean isVideoAvailable) {
            //有用户的视频开启了
//            TRTCVideoLayout layout = mLayoutManagerTrtc.findCloudViewView(userId);
//            if (layout != null) {
//                layout.setVideoAvailable(isVideoAvailable);
//                if (isVideoAvailable) {
//                    mITRTCAVCall.startRemoteView(userId, layout.getVideoView());
//                } else {
//                    mITRTCAVCall.stopRemoteView(userId);
//                }
//            }
            UserModel model = mTrtcAdapter.findAudioCallLayout(userId);
            if(model!=null){
                model.videoAvailable = isVideoAvailable;
                model.loading = false;
                int index = mTrtcAdapter.findAudioCallIndex(userId);
                mTrtcAdapter.notifyItemChanged(index);
            }
        }

        @Override
        public void onUserAudioAvailable(String userId, boolean isVideoAvailable) {

        }

        @Override
        public void onUserVoiceVolume(Map<String, Integer> volumeMap) {
//            for (Map.Entry<String, Integer> entry : volumeMap.entrySet()) {
//                String userId = entry.getKey();
//                TRTCVideoLayout layout = mLayoutManagerTrtc.findCloudViewView(userId);
//                if (layout != null) {
//                    layout.setAudioVolumeProgress(entry.getValue());
//                }
//            }
        }
    };

    /**
     * 主动拨打给某些用户
     *
     * @param context
     * @param models
     */
    public static void startCallSomePeople(Context context, List<UserModel> models, String groupId) {
        SLog.i("startCallSomePeople");
        ((TRTCAVCallImpl) TRTCAVCallImpl.sharedInstance(context)).setWaitingLastActivityFinished(false);
        Intent starter = new Intent(context, TRTCVideoCallActivity.class);
        starter.putExtra(PARAM_GROUP_ID, groupId);
        starter.putExtra(PARAM_TYPE, TYPE_CALL);
        starter.putExtra(PARAM_USER, new IntentParams(models));
        starter.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(starter);
    }

    /**
     * 作为用户被叫
     *
     * @param context
     * @param beingCallUserModel
     */
    public static void startBeingCall(Context context, UserModel beingCallUserModel, List<UserModel> otherInvitingUserModel) {
        SLog.i("startBeingCall");
        ((TRTCAVCallImpl) TRTCAVCallImpl.sharedInstance(context)).setWaitingLastActivityFinished(false);
        Intent starter = new Intent(context, TRTCVideoCallActivity.class);
        starter.putExtra(PARAM_TYPE, TYPE_BEING_CALLED);
        starter.putExtra(PARAM_BEINGCALL_USER, beingCallUserModel);
        starter.putExtra(PARAM_OTHER_INVITING_USER, new IntentParams(otherInvitingUserModel));
        starter.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(starter);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        // 应用运行时，保持不锁屏、全屏化
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        super.onCreate(savedInstanceState);
        SLog.i("onCreate");
    }

    @Override
    public void onInitView() throws Exception {
        super.onInitView();
        String[] permission = {Manifest.permission.RECORD_AUDIO,Manifest.permission.CAMERA};
        if(!hasPermission(permission)){
            onPermissionChecker(permission, new PermissionsResultAction() {
                @Override
                public void onGranted() {
                    loadData();
                }

                @Override
                public void onDenied(String permission) {
                    finish();
                }
            });
        }else{
            loadData();
        }
    }

    private void loadData(){
        mCallType = getIntent().getIntExtra(PARAM_TYPE, TYPE_BEING_CALLED);
        SLog.i("mCallType: " + mCallType);
        if (mCallType == TYPE_BEING_CALLED && ((TRTCAVCallImpl) TRTCAVCallImpl.sharedInstance(this)).isWaitingLastActivityFinished()) {
            // 有些手机禁止后台启动Activity，但是有bug，比如一种场景：对方反复拨打并取消，三次以上极容易从后台启动成功通话界面，
            // 此时对方再挂断时，此通话Activity调用finish后，上一个从后台启动的Activity就会弹出。此时这个Activity就不能再启动。
            SLog.w("ignore activity launch");
            finishActivity();
            return;
        }

        NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        if (manager != null) {
            manager.cancelAll();
        }

        mVibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);
        mRingtone = RingtoneManager.getRingtone(this,
                RingtoneManager.getDefaultUri(RingtoneManager.TYPE_RINGTONE));

        initView();
        initData();
        initListener();
    }

    @Override
    public int onCustomContentId() {
        return R.layout.videocall_activity_online_call;
    }

    private void finishActivity() {
        ((TRTCAVCallImpl) TRTCAVCallImpl.sharedInstance(this)).setWaitingLastActivityFinished(true);
        finish();
    }

    @Override
    public void onBackPressed() {
        if(mITRTCAVCall!=null){
            mITRTCAVCall.hangup();
        }
        super.onBackPressed();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mVibrator != null) {
            mVibrator.cancel();
        }
        if (mRingtone != null) {
            mRingtone.stop();
        }
        if (mITRTCAVCall != null) {
            mITRTCAVCall.closeCamera();
            mITRTCAVCall.removeListener(mTRTCAVCallListener);
        }
        stopTimeCount();
        if (mTimeHandlerThread != null) {
            mTimeHandlerThread.quit();
        }
    }

    private void initListener() {
        mMuteLl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isMuteMic = !isMuteMic;
                mITRTCAVCall.setMicMute(isMuteMic);
                mMuteImg.setActivated(isMuteMic);
//                ToastUtil.info(TRTCVideoCallActivity.this,isMuteMic ? "开启静音" : "关闭静音");
//                isMuteVideo = !isMuteVideo;
//                mITRTCAVCall.setMuteLocalVideo(isMuteVideo);
            }
        });
        mHandsfreeLl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                isCamera = !isCamera;
//                mITRTCAVCall.switchCamera(isCamera);
                isHandsFree = !isHandsFree;
                mITRTCAVCall.setHandsFree(isHandsFree);
                mHandsfreeImg.setActivated(isHandsFree);
//                ToastUtil.info(TRTCVideoCallActivity.this, isHandsFree ? "扬声器" : "听筒");
            }
        });
        mVideoLl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isMuteVideo = !isMuteVideo;
                mITRTCAVCall.setMuteLocalVideo(isMuteVideo);
                mMuteVideoImg.setActivated(isMuteVideo);
                UserModel userModel = mTrtcAdapter.getItem(0);
                if(userModel!=null){
                    userModel.videoAvailableRefresh = true;
                    userModel.videoAvailable = !isMuteVideo;
                    userModel.loading = false;
                    mTrtcAdapter.notifyItemChanged(0);
                }
            }
        });
        mMuteImg.setActivated(isMuteMic);
        mHandsfreeImg.setActivated(isHandsFree);
        mMuteVideoImg.setActivated(isMuteVideo);
    }

    private void initData() {
        // 初始化成员变量
        mITRTCAVCall = TRTCAVCallImpl.sharedInstance(this);
        mITRTCAVCall.addListener(mTRTCAVCallListener);
        mTimeHandlerThread = new HandlerThread("time-count-thread");
        mTimeHandlerThread.start();
        mTimeHandler = new Handler(mTimeHandlerThread.getLooper());
        // 初始化从外界获取的数据
        Intent intent = getIntent();
        //自己的资料
        mSelfModel = ProfileManager.getInstance().getUserModel();
        mSelfModel.userId = UserApi.instance().getUserId();
        mSelfModel.userName = UserApi.instance().getNickName();
        mSelfModel.userAvatar = UserApi.instance().getUserIcon();
        mCallType = intent.getIntExtra(PARAM_TYPE, TYPE_BEING_CALLED);
        mGroupId = intent.getStringExtra(PARAM_GROUP_ID);
        if (mCallType == TYPE_BEING_CALLED) {
            // 作为被叫
            mSponsorUserModel = (UserModel) intent.getSerializableExtra(PARAM_BEINGCALL_USER);
            IntentParams params = (IntentParams) intent.getSerializableExtra(PARAM_OTHER_INVITING_USER);
            if (params != null) {
                mOtherInvitingUserModelList = params.mUserModels;
            }
            showWaitingResponseView();
            mVibrator.vibrate(new long[]{0, 1000, 1000}, 0);
            mRingtone.play();
        } else {
            // 主叫方
            IntentParams params = (IntentParams) intent.getSerializableExtra(PARAM_USER);
            if (params != null) {
                mCallUserModelList = params.mUserModels;
                for (UserModel userModel : mCallUserModelList) {
                    mCallUserModelMap.put(userModel.userId, userModel);
                }
                startInviting();
                showInvitingView();
                mVibrator.vibrate(new long[]{0, 1000, 1000}, 0);
            }
        }

    }

    private void startInviting() {
        List<String> list = new ArrayList<>();
        for (UserModel userModel : mCallUserModelList) {
            list.add(userModel.userId);
        }
        mITRTCAVCall.groupCall(list, ITRTCAVCall.TYPE_VIDEO_CALL, mGroupId);
    }

    private void initView() {
        mMuteImg = findViewById(R.id.img_mute);
        mMuteLl = findViewById(R.id.ll_mute);
        mHangupLl = findViewById(R.id.ll_hangup);
        mHandsfreeImg = findViewById(R.id.img_handsfree);
        mHandsfreeLl = findViewById(R.id.ll_handsfree);
        mMuteVideoImg = findViewById(R.id.img_video);
        mVideoLl = findViewById(R.id.ll_video);
        mDialingLl = findViewById(R.id.ll_dialing);
        mShadeSponsor = findViewById(R.id.shade_sponsor);
        RecyclerView mLayoutManagerTrtc = findViewById(R.id.trtc_layout_manager);
        mLayoutManagerTrtc.setLayoutManager(new GridLayoutManager(this,2));
        mTrtcAdapter = new TRTCVideoAdapter(null);
        mLayoutManagerTrtc.setAdapter(mTrtcAdapter);
        mTimeTv = findViewById(R.id.tv_time);
    }


    /**
     * 等待接听界面
     */
    public void showWaitingResponseView() {
        //1. 展示自己的画面
        mTrtcAdapter.setITRTCAVCall(mITRTCAVCall);
        mSelfModel.videoAvailable = true;
        addUserToManager(mSelfModel);
        //展示发起人的头像
        mSponsorUserModel.loading = true;
        mSponsorUserModel.isSponsor = true;
        addUserToManager(mSponsorUserModel);
        //3. 展示电话对应界面
        mHangupLl.setVisibility(View.VISIBLE);
        mDialingLl.setVisibility(View.VISIBLE);
        mHandsfreeLl.setVisibility(View.GONE);
        mMuteLl.setVisibility(View.GONE);
        mVideoLl.setVisibility(View.GONE);
        //3. 设置对应的listener
        mHangupLl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mVibrator.cancel();
                mRingtone.stop();
                mITRTCAVCall.reject();
                finishActivity();
            }
        });
        mDialingLl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mVibrator.cancel();
                mRingtone.stop();
                mITRTCAVCall.accept();
                showCallingView();
            }
        });
        //4. 展示其他用户界面
        showOtherInvitingUserView();
    }

    /**
     * 展示邀请列表
     */
    public void showInvitingView() {
        //1. 展示自己的界面
        mTrtcAdapter.setITRTCAVCall(mITRTCAVCall);
        mSelfModel.isSponsor = true;
        mSelfModel.videoAvailable = true;
        addUserToManager(mSelfModel);
        for (UserModel userModel : mCallUserModelList) {
            userModel.loading = true;
            addUserToManager(userModel);
        }
        //2. 设置底部栏
        mHangupLl.setVisibility(View.VISIBLE);
        mHangupLl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mITRTCAVCall.hangup();
                finishActivity();
            }
        });
        mDialingLl.setVisibility(View.GONE);
        mHandsfreeLl.setVisibility(View.GONE);
        mMuteLl.setVisibility(View.GONE);
        mVideoLl.setVisibility(View.GONE);
    }
    /**
     * 展示通话中的界面
     */
    public void showCallingView() {
        //1. 蒙版消失
        mShadeSponsor.setVisibility(View.GONE);
        //2. 底部状态栏
        mHangupLl.setVisibility(View.VISIBLE);
        mDialingLl.setVisibility(View.GONE);
        mHandsfreeLl.setVisibility(View.VISIBLE);
        mMuteLl.setVisibility(View.VISIBLE);
        mVideoLl.setVisibility(View.VISIBLE);
        mHangupLl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mITRTCAVCall.hangup();
                finishActivity();
            }
        });
        mTimeTv.setVisibility(View.VISIBLE);
        showTimeCount();
    }

    private void showTimeCount() {
        if (mTimeRunnable != null) {
            return;
        }
        mTimeCount = 0;
        mTimeTv.setText(getShowTime(mTimeCount));
        if (mTimeRunnable == null) {
            mTimeRunnable = new Runnable() {
                @Override
                public void run() {
                    mTimeCount++;
                    if (mTimeTv != null) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                mTimeTv.setText(getShowTime(mTimeCount));
                            }
                        });
                    }
                    mTimeHandler.postDelayed(mTimeRunnable, 1000);
                }
            };
        }
        mTimeHandler.postDelayed(mTimeRunnable, 1000);
    }

    private void stopTimeCount() {
        if (mTimeHandler != null) {
            mTimeHandler.removeCallbacks(mTimeRunnable);
        }
        mTimeRunnable = null;
        if(mTimeCount>1 && mTimeCount<60){//不足一分钟，按照一分钟计算
            mTimeCount = 60;
        }
        if(mTimeCount>1){
            int s = (int) Math.ceil(mTimeCount / 60f);
            AddApplyStaticsReq addApplyStaticsReq = new AddApplyStaticsReq();
            addApplyStaticsReq.setVideoMinutes(s);
            Yz.getSession().addApplyStatics(addApplyStaticsReq,null);
        }
    }

    private String getShowTime(int count) {
        return String.format("%02d:%02d", count / 60, count % 60);
    }

    private void showOtherInvitingUserView() {
        if (mOtherInvitingUserModelList == null || mOtherInvitingUserModelList.size() == 0) {
            return;
        }
//        mInvitingGroup.setVisibility(View.VISIBLE);
//        int squareWidth = getResources().getDimensionPixelOffset(R.dimen.small_image_size);
//        int leftMargin = getResources().getDimensionPixelOffset(R.dimen.small_image_left_margin);
        for (int index = 0; index < mOtherInvitingUserModelList.size() && index < MAX_SHOW_INVITING_USER; index++) {
            UserModel userModel = mOtherInvitingUserModelList.get(index);
//            ImageView imageView = new ImageView(this);
//            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(squareWidth, squareWidth);
//            if (index != 0) {
//                layoutParams.leftMargin = leftMargin;
//            }
//            imageView.setLayoutParams(layoutParams);
//            GlideEngine.loadCornerAvatar(imageView, userModel.userAvatar);
//
//            mImgContainerLl.addView(imageView);
            userModel.loading = true;
            addUserToManager(userModel);
        }
    }


    private void addUserToManager(UserModel userModel) {
//        TRTCVideoLayout layout = mLayoutManagerTrtc.allocCloudVideoView(userModel.userId);
//        if (layout == null) {
//            return null;
//        }
//        layout.getUserNameTv().setText(userModel.userName);
//        if (!TextUtils.isEmpty(userModel.userAvatar)) {
//            GlideEngine.loadCornerAvatar(layout.getHeadImg(), userModel.userAvatar);
//        }
        mTrtcAdapter.addData(userModel);
    }

    @Override
    public boolean isShowTitleBar() {
        return false;
    }
}
