package com.mwim.liteav.trtcvideocall.ui.videolayout;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.mwim.qcloud.tim.uikit.R;
import com.tencent.rtmp.ui.TXCloudVideoView;
/**
 * Module: TRTCVideoLayout
 * <p>
 * Function:
 * <p>
 * 此 TRTCVideoLayout 封装了{@link TXCloudVideoView} 以及业务逻辑 UI 控件
 */
public class TRTCVideoLayout extends RelativeLayout {
    private boolean          mMoveable;
    private TXCloudVideoView mTcCloudViewTrtc;
    private SquareImageView  mHeadImg;
    private TextView         mUserNameTv;
    private View      mFlNoVideo;
    private ProgressBar      mAudioPb;
    private TextView mWaiting;
    private View mFlbg;
    private int layoutId = R.layout.videocall_item_user_user_layout;


    public TRTCVideoLayout(Context context) {
        this(context, null);
    }

    public TRTCVideoLayout(Context context,int layoutId) {
        super(context, null);
        this.layoutId = layoutId;
        initView();
        setClickable(true);
    }

    public TRTCVideoLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView();
        setClickable(true);
    }

    public TRTCVideoLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView();
        setClickable(true);
    }

    public TXCloudVideoView getVideoView() {
        return mTcCloudViewTrtc;
    }

    public SquareImageView getHeadImg() {
        return mHeadImg;
    }

    public TextView getUserNameTv() {
        return mUserNameTv;
    }

    public void setVideoAvailable(boolean available) {
        if (available) {
            mTcCloudViewTrtc.setVisibility(VISIBLE);
            mFlNoVideo.setVisibility(GONE);
            mWaiting.setVisibility(GONE);
            mFlbg.setVisibility(GONE);
        } else {
            mTcCloudViewTrtc.setVisibility(GONE);
            mFlNoVideo.setVisibility(VISIBLE);
            if(layoutId==R.layout.videocall_item_user_user_layout){
                mWaiting.setVisibility(VISIBLE);
                mFlbg.setVisibility(VISIBLE);
            }
        }
    }

    public void setAudioVolumeProgress(int progress) {
        if (mAudioPb != null) {
            mAudioPb.setProgress(progress);
        }
    }

    public void setAudioVolumeProgressBarVisibility(int visibility) {
        if (mAudioPb != null) {
            mAudioPb.setVisibility(visibility);
        }
    }

    private void initView() {
        LayoutInflater.from(getContext()).inflate(layoutId, this, true);
        mTcCloudViewTrtc = findViewById(R.id.trtc_tc_cloud_view);
        mHeadImg = findViewById(R.id.img_avatar);
        mUserNameTv = findViewById(R.id.tv_user_name);
        mFlNoVideo = findViewById(R.id.fl_no_video);
        mAudioPb = findViewById(R.id.pb_audio);
        mWaiting = findViewById(R.id.waiting);
        mFlbg = findViewById(R.id.fl_bg);
    }


    public TextView getWaiting() {
        return mWaiting;
    }

    public View getFlbg() {
        return mFlbg;
    }

    public View getFlNoVideo() {
        return mFlNoVideo;
    }

    public boolean isMoveable() {
        return mMoveable;
    }

    public void setMoveable(boolean enable) {
        mMoveable = enable;
    }
}
