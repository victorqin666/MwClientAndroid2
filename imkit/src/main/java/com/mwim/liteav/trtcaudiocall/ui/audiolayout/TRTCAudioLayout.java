package com.mwim.liteav.trtcaudiocall.ui.audiolayout;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.AttributeSet;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.mwim.qcloud.tim.uikit.R;
import com.wang.avi.AVLoadingIndicatorView;


/**
 *
 */
public class TRTCAudioLayout extends RelativeLayout {
    private ImageView mHeadImg;
    private TextView mNameTv;
    private ProgressBar mAudioPb;
    private AVLoadingIndicatorView mViewLoading;
    private FrameLayout mShadeFl;

    public TRTCAudioLayout(Context context) {
        this(context, null);
    }

    public TRTCAudioLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        inflate(context, R.layout.audiocall_item_user_layout, this);
        initView();
    }

    private void initView() {
        mHeadImg = findViewById(R.id.img_head);
        mNameTv = findViewById(R.id.tv_name);
        mAudioPb = findViewById(R.id.pb_audio);
        mViewLoading = findViewById(R.id.loading_view);
        mShadeFl = findViewById(R.id.fl_shade);
    }

    public void setAudioVolume(int vol) {
        mAudioPb.setProgress(vol);
    }

    public void setUserId(String userId) {
        mNameTv.setText(userId);
    }

    public void setBitmap(Bitmap bitmap) {
        mHeadImg.setImageBitmap(bitmap);
    }

    public ImageView getImageView() {
        return mHeadImg;
    }

    public void startLoading() {
        mShadeFl.setVisibility(VISIBLE);
        mViewLoading.show();
    }


    public void stopLoading() {
        mShadeFl.setVisibility(GONE);
        mViewLoading.hide();
    }
}
