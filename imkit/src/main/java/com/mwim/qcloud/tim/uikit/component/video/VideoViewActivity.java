package com.mwim.qcloud.tim.uikit.component.video;

import android.app.AlertDialog;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;

import com.mwim.qcloud.tim.uikit.base.BaseActivity;
import com.mwim.qcloud.tim.uikit.component.video.proxy.IPlayer;
import com.mwim.qcloud.tim.uikit.utils.ImageUtil;
import com.mwim.qcloud.tim.uikit.utils.PopWindowUtil;
import com.mwim.qcloud.tim.uikit.utils.ScreenUtil;
import com.mwim.qcloud.tim.uikit.utils.IMKitConstants;
import com.mwim.qcloud.tim.uikit.R;
import com.work.util.FileUtils;
import com.work.util.SLog;
import com.work.util.ToastUtil;
import com.workstation.view.MaterialMenuDrawable;
import com.workstation.view.MaterialMenuView;

import java.io.File;

public class VideoViewActivity extends BaseActivity {

    private UIKitVideoView mVideoView;
    private int videoWidth = 0;
    private int videoHeight = 0;
    private AlertDialog mDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //去除标题栏
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        //去除状态栏
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onInitView() throws Exception {
        super.onInitView();
        mVideoView = findViewById(R.id.video_play_view);

        String imagePath = getIntent().getStringExtra(IMKitConstants.CAMERA_IMAGE_PATH);
        final Uri videoUri = getIntent().getParcelableExtra(IMKitConstants.CAMERA_VIDEO_PATH);
        Bitmap firstFrame = ImageUtil.getBitmapFormPath(imagePath);
        if (firstFrame != null) {
            videoWidth = firstFrame.getWidth();
            videoHeight = firstFrame.getHeight();
            updateVideoView();
        }

        mVideoView.setVideoURI(videoUri);
        mVideoView.setOnPreparedListener(new IPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(IPlayer mediaPlayer) {
                mVideoView.start();
            }
        });
        mVideoView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mVideoView.isPlaying()) {
                    mVideoView.pause();
                } else {
                    mVideoView.start();
                }
            }
        });
        MaterialMenuView mBack = findViewById(R.id.photo_view_back);
        mBack.setState(MaterialMenuDrawable.IconState.X);
        findViewById(R.id.video_view_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mVideoView.stop();
                finish();
            }
        });
        mVideoView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                SLog.e("长按视频:"+videoUri.getPath());
                if (mDialog == null) {
                    mDialog = PopWindowUtil.buildFullScreenDialog(VideoViewActivity.this);
                    View moreActionView = LayoutInflater.from(VideoViewActivity.this).inflate(R.layout.photo_video_view_pop_menu, null);
                    moreActionView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            mDialog.dismiss();
                        }
                    });
                    Button addBtn = moreActionView.findViewById(R.id.video_call);
                    addBtn.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            try {
                                new Thread(new Runnable() {
                                    @Override
                                    public void run() {
                                        FileUtils fileUtils = new FileUtils(VideoViewActivity.this);
                                        String mOpenFile = fileUtils.getStorageDirectory()+"/"+System.currentTimeMillis()/1000+".mp4";
                                        fileUtils.copyFile(videoUri.getPath(),mOpenFile);
                                        ContentResolver localContentResolver = getContentResolver();
                                        File file = new File(mOpenFile);
                                        ContentValues localContentValues = getVideoContentValues(file, System.currentTimeMillis());
                                        Uri localUri = localContentResolver.insert(MediaStore.Video.Media.EXTERNAL_CONTENT_URI, localContentValues);
                                        sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, localUri));
                                    }
                                }).start();
                            }catch (Exception e){
                                e.printStackTrace();
                                ToastUtil.error(VideoViewActivity.this,R.string.toast_save_fail);
                            }
                            mDialog.dismiss();
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
                } else {
                    mDialog.show();
                }
                return true;
            }
        });
    }

    public ContentValues getVideoContentValues(File paramFile, long paramLong) {
        SLog.e("video file:"+paramFile.getAbsolutePath());
        ContentValues localContentValues = new ContentValues();
        localContentValues.put("title", paramFile.getName());
        localContentValues.put("_display_name", paramFile.getName());
        localContentValues.put("mime_type", "video/mp4");
        localContentValues.put("datetaken", paramLong);
        localContentValues.put("date_modified", paramLong);
        localContentValues.put("date_added", paramLong);
        long duration = 0;
        try {
            MediaMetadataRetriever mediaMetadataRetriever = new MediaMetadataRetriever();
            mediaMetadataRetriever.setDataSource(paramFile.getAbsolutePath());
            duration = Integer.parseInt(mediaMetadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION));
        }catch (Exception ignore){}
        localContentValues.put("duration",duration);
        localContentValues.put("_data", paramFile.getAbsolutePath());
        localContentValues.put("_size", paramFile.length());
        return localContentValues;
    }

    @Override
    public int onCustomContentId() {
        return R.layout.activity_video_view;
    }

    @Override
    public boolean isShowTitleBar() {
        return false;
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        updateVideoView();
    }

    private void updateVideoView() {
        if (videoWidth <= 0 && videoHeight <= 0) {
            return;
        }
        boolean isLandscape = true;
        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
            isLandscape = false;
        }

        int deviceWidth;
        int deviceHeight;
        if (isLandscape) {
            deviceWidth = Math.max(ScreenUtil.getScreenWidth(this), ScreenUtil.getScreenHeight(this));
            deviceHeight = Math.min(ScreenUtil.getScreenWidth(this), ScreenUtil.getScreenHeight(this));
        } else {
            deviceWidth = Math.min(ScreenUtil.getScreenWidth(this), ScreenUtil.getScreenHeight(this));
            deviceHeight = Math.max(ScreenUtil.getScreenWidth(this), ScreenUtil.getScreenHeight(this));
        }
        int[] scaledSize = ScreenUtil.scaledSize(deviceWidth, deviceHeight, videoWidth, videoHeight);
        ViewGroup.LayoutParams params = mVideoView.getLayoutParams();
        params.width = scaledSize[0];
        params.height = scaledSize[1];
        mVideoView.setLayoutParams(params);
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (mVideoView != null) {
            mVideoView.stop();
        }
    }
}
