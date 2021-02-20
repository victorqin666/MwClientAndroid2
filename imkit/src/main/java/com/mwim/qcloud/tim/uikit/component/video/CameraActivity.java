package com.mwim.qcloud.tim.uikit.component.video;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import com.mwim.qcloud.tim.uikit.base.BaseActivity;
import com.mwim.qcloud.tim.uikit.base.IUIKitCallBack;
import com.mwim.qcloud.tim.uikit.component.video.listener.ClickListener;
import com.mwim.qcloud.tim.uikit.component.video.listener.ErrorListener;
import com.mwim.qcloud.tim.uikit.component.video.listener.JCameraListener;
import com.mwim.qcloud.tim.uikit.component.video.util.DeviceUtil;
import com.mwim.qcloud.tim.uikit.utils.FileUtil;
import com.mwim.qcloud.tim.uikit.utils.IMKitConstants;
import com.mwim.qcloud.tim.uikit.R;
import com.work.util.SLog;
import com.work.util.ToastUtil;

public class CameraActivity extends BaseActivity {

    public static IUIKitCallBack mCallBack;
    private JCameraView jCameraView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //去除标题栏
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        //去除状态栏
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onInitView() throws Exception {
        super.onInitView();

        jCameraView = findViewById(R.id.jcameraview);
        //设置视频保存路径
        //jCameraView.setSaveVideoPath(Environment.getExternalStorageDirectory().getPath() + File.separator + "JCamera");

        int state = getIntent().getIntExtra(IMKitConstants.CAMERA_TYPE, JCameraView.BUTTON_STATE_BOTH);
        jCameraView.setFeatures(state);
        if (state == JCameraView.BUTTON_STATE_ONLY_CAPTURE) {
            jCameraView.setTip("点击拍照");
        } else if (state == JCameraView.BUTTON_STATE_ONLY_RECORDER) {
            jCameraView.setTip("长按摄像");
        }

        jCameraView.setMediaQuality(JCameraView.MEDIA_QUALITY_MIDDLE);
        jCameraView.setErrorLisenter(new ErrorListener() {
            @Override
            public void onError() {
                //错误监听
                SLog.i( "camera error");
                Intent intent = new Intent();
                setResult(103, intent);
                finish();
            }

            @Override
            public void AudioPermissionError() {
                ToastUtil.info(CameraActivity.this,"给点录音权限可以?");
            }
        });
        //JCameraView监听
        jCameraView.setJCameraLisenter(new JCameraListener() {
            @Override
            public void captureSuccess(Bitmap bitmap) {
                //获取图片bitmap
                String path = FileUtil.saveBitmap("JCamera", bitmap);
               /* Intent intent = new Intent();
                intent.putExtra(ILiveConstants.CAMERA_IMAGE_PATH, path);
                setResult(-1, intent);*/
                if (mCallBack != null) {
                    mCallBack.onSuccess(path);
                }
                finish();
            }

            @Override
            public void recordSuccess(String url, Bitmap firstFrame, long duration) {
                //获取视频路径
                String path = FileUtil.saveBitmap("JCamera", firstFrame);
                Intent intent = new Intent();
                intent.putExtra(IMKitConstants.IMAGE_WIDTH, firstFrame.getWidth());
                intent.putExtra(IMKitConstants.IMAGE_HEIGHT, firstFrame.getHeight());
                intent.putExtra(IMKitConstants.VIDEO_TIME, duration);
                intent.putExtra(IMKitConstants.CAMERA_IMAGE_PATH, path);
                intent.putExtra(IMKitConstants.CAMERA_VIDEO_PATH, url);
                firstFrame.getWidth();
                //setResult(-1, intent);
                if (mCallBack != null) {
                    mCallBack.onSuccess(intent);
                }
                finish();
            }
        });

        jCameraView.setLeftClickListener(new ClickListener() {
            @Override
            public void onClick() {
                CameraActivity.this.finish();
            }
        });
        jCameraView.setRightClickListener(new ClickListener() {
            @Override
            public void onClick() {
            }
        });
        //jCameraView.setVisibility(View.GONE);
        SLog.i( DeviceUtil.getDeviceModel());
    }

    @Override
    public boolean isShowTitleBar() {
        return false;
    }

    @Override
    public void onDetachedFromWindow() {
        super.onDetachedFromWindow();
    }

    @Override
    public int onCustomContentId() {
        return R.layout.activity_im_camera;
    }

    @Override
    protected void onStart() {
        super.onStart();
        //全屏显示
        if (Build.VERSION.SDK_INT >= 19) {
            View decorView = getWindow().getDecorView();
            decorView.setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                            | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
        } else {
            View decorView = getWindow().getDecorView();
            int option = View.SYSTEM_UI_FLAG_FULLSCREEN;
            decorView.setSystemUiVisibility(option);
        }
    }

    @Override
    protected void onResume() {
        SLog.i( "onResume");
        super.onResume();
        jCameraView.onResume();
    }

    @Override
    protected void onPause() {
        SLog.i( "onPause");
        super.onPause();
        jCameraView.onPause();
    }

    @Override
    protected void onDestroy() {
        SLog.i( "onDestroy");
        super.onDestroy();
        mCallBack = null;
    }

}
