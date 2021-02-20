package com.mwim.qcloud.tim.uikit.component.photoview;

import android.app.AlertDialog;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.mwim.qcloud.tim.uikit.base.BaseActivity;
import com.mwim.qcloud.tim.uikit.component.picture.imageEngine.impl.GlideEngine;
import com.mwim.qcloud.tim.uikit.utils.FileUtil;
import com.mwim.qcloud.tim.uikit.utils.PopWindowUtil;
import com.mwim.qcloud.tim.uikit.utils.IMKitConstants;
import com.tencent.imsdk.v2.V2TIMDownloadCallback;
import com.tencent.imsdk.v2.V2TIMElem;
import com.tencent.imsdk.v2.V2TIMImageElem;
import com.mwim.qcloud.tim.uikit.R;
import com.work.util.FileUtils;
import com.work.util.SLog;
import com.work.util.ToastUtil;
import com.workstation.view.MaterialMenuDrawable;
import com.workstation.view.MaterialMenuView;

import java.io.File;


public class PhotoViewActivity extends BaseActivity {

    public static V2TIMImageElem.V2TIMImage mCurrentOriginalImage;
    private PhotoView mPhotoView;
    private TextView mViewOriginalBtn;
    private AlertDialog mDialog;
    private FileUtils mFileUtils;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        //去除标题栏
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        //去除状态栏
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onInitView() throws Exception {
        super.onInitView();
        MaterialMenuView mBack = findViewById(R.id.photo_view_back);
        mBack.setState(MaterialMenuDrawable.IconState.X);
        mBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        mPhotoView = findViewById(R.id.photo_view);
        Matrix mCurrentDisplayMatrix = new Matrix();
        mPhotoView.setDisplayMatrix(mCurrentDisplayMatrix);
        mPhotoView.setOnMatrixChangeListener(new MatrixChangeListener());
        mPhotoView.setOnPhotoTapListener(new OnPhotoTapListener() {
            @Override
            public void onPhotoTap(ImageView view, float x, float y) {
                finish();
            }
        });
        mPhotoView.setOnSingleFlingListener(new SingleFlingListener());
        mFileUtils = new FileUtils(this);
        String imageData = getIntent().getStringExtra(IMKitConstants.IMAGE_DATA);
        if(imageData.startsWith("http")){//加载网络图片
            GlideEngine.loadImage(mPhotoView,imageData);
            return;
        }
        Uri uri = FileUtil.getUriFromPath(imageData);
        boolean isSelf = getIntent().getBooleanExtra(IMKitConstants.SELF_MESSAGE, false);
        mViewOriginalBtn = findViewById(R.id.view_original_btn);
        if (isSelf || mCurrentOriginalImage == null) {
            mPhotoView.setImageURI(uri);
        } else {
            final String path = IMKitConstants.IMAGE_DOWNLOAD_DIR + mCurrentOriginalImage.getUUID();
            File file = new File(path);
            if (file.exists()) {
                mPhotoView.setImageURI(FileUtil.getUriFromPath(file.getPath()));
            }else {
                mPhotoView.setImageURI(uri);
                mViewOriginalBtn.setVisibility(View.VISIBLE);
                mViewOriginalBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (mCurrentOriginalImage != null) {
                            final File file = new File(path);
                            if (!file.exists()) {
                                mCurrentOriginalImage.downloadImage(path, new V2TIMDownloadCallback() {
                                    @Override
                                    public void onProgress(V2TIMElem.V2ProgressInfo progressInfo) {

                                    }

                                    @Override
                                    public void onError(int code, String desc) {

                                    }

                                    @Override
                                    public void onSuccess() {
                                        mPhotoView.setImageURI(FileUtil.getUriFromPath(file.getPath()));
                                        mViewOriginalBtn.setText("已完成");
                                        mViewOriginalBtn.setOnClickListener(null);
                                    }
                                });
                            } else {
                                mPhotoView.setImageURI(FileUtil.getUriFromPath(file.getPath()));
                            }
                        }
                    }
                });
            }

        }

        mPhotoView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                if (mDialog == null) {
                    mDialog = PopWindowUtil.buildFullScreenDialog(PhotoViewActivity.this);
                    View moreActionView = LayoutInflater.from(PhotoViewActivity.this).inflate(R.layout.photo_video_view_pop_menu, null);
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
                                       Bitmap bitmap = ((BitmapDrawable)mPhotoView.getDrawable()).getBitmap();
                                       String path = mFileUtils.saveBitmap("yzim"+System.currentTimeMillis()/1000+".png",bitmap);
                                       MediaScannerConnection.scanFile(PhotoViewActivity.this,
                                               new String[]{path},
                                               new String[]{"image/jpeg"},
                                               new MediaScannerConnection.OnScanCompletedListener() {
                                                   @Override
                                                   public void onScanCompleted(String path, Uri uri) {
                                                       SLog.i("onScanCompleted"+path);
                                                   }
                                               });
                                   }
                               }).start();
                            }catch (Exception e){
                                e.printStackTrace();
                                ToastUtil.error(PhotoViewActivity.this,R.string.toast_save_fail);
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

    @Override
    public int onCustomContentId() {
        return R.layout.activity_photo_view;
    }

    @Override
    public boolean isShowTitleBar() {
        return false;
    }


    private static class MatrixChangeListener implements OnMatrixChangedListener {

        @Override
        public void onMatrixChanged(RectF rect) {

        }
    }

    private static class SingleFlingListener implements OnSingleFlingListener {

        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
            return true;
        }
    }
}
