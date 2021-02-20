package com.workstation.android;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.PixelFormat;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import androidx.annotation.NonNull;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import com.http.network.HttpConnectWork;
import com.http.network.listener.OnResultDataListener;
import com.http.network.model.RequestWork;
import com.http.network.model.ResponseWork;
import com.work.util.NetworkUtils;
import com.work.util.ToastUtil;
import com.workstation.listener.HomeWorkListener;
import com.workstation.permission.PermissionsManager;
import com.workstation.permission.PermissionsResultAction;
import com.workstation.view.GraduallyTextView;

import java.util.Locale;

/**
 * Created by tangyx on 15/8/28.
 *
 */
public class BaseHomeActivity extends ToolBarActivity
        implements
        HomeWorkListener,
        OnResultDataListener{
    public final static int FinishCode = -100000;
    public final static int TextSizeCode = - 200000;
    public String mThisClassName;
    private WindowManager mWindowManager = null;
    private View mProgressLayout = null;
    private GraduallyTextView mGraduallyTextView;
    /**
     * 提示框
     */
    public View mContentView;
    /**
     * 权限检查
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //强制竖屏
//        setRequestedOrientation(onScreenFlag());
        super.onCreate(savedInstanceState);
        mContentView = onCustomContentView();
        if(mContentView ==null){
            int layoutId = onCustomContentId();
            if(layoutId<=0){
                layoutId = onInitContentView();
            }
            if(layoutId>0){
                mContentView = mInflater.inflate(layoutId, null);
                setContentView(mContentView);
            }
        } else {
            setContentView(mContentView);
        }
        try {
            onInitContentView();
            onInitView();
            onInitValue();
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @Deprecated
    protected int onScreenFlag(){
        return ActivityInfo.SCREEN_ORIENTATION_PORTRAIT;
    }

    /**
     * 初始化view
     */
    protected int onInitContentView(){
        mThisClassName = this.getClass().getSimpleName();
        String resLayoutName = "activity_"+ mThisClassName.replaceFirst("Activity","");
        resLayoutName = resLayoutName.toLowerCase(Locale.getDefault());
        return getResources().getIdentifier(resLayoutName, "layout", getPackageName());
    }


    /**
     * 控件初始化
     */
    @Override
    public void onInitView() throws Exception{
        String resLayoutName = "activity_"+ mThisClassName.replaceFirst("Activity","");
        resLayoutName = resLayoutName.toLowerCase(Locale.getDefault());
        final int layoutId = getResources().getIdentifier(resLayoutName, "string", getPackageName());
        if(layoutId>0){
            setTitleName(layoutId);
        }
    }
    @Override
    public void onInitValue() throws Exception{
        setStatusBar();
    };

    public void setStatusBar(){
//        StatusBarUtil.setColor(this, ContextCompat.getColor(this,R.color.defaultColorPrimary),50);
    }

    @Override
    public View onCustomContentView() {
        return null;
    }

    @Override
    public int onCustomContentId() {
        return -1;
    }


    public void showProgressLoading(){
        showProgressLoading(false);
    }

    public void showProgressLoading(boolean isOperation) {
        boolean isShowTitleBar = isShowTitleBar();
        showProgressLoading(isOperation, null, 1000,isShowTitleBar);
    }

    public void showProgressLoading(boolean isOperation,boolean hiddenContentView){
        showProgressLoading(isOperation, null, 500,hiddenContentView);
    }

    public void showProgressLoading(int resId) {
        showProgressLoading(getResources().getString(resId));
    }

    public void showProgressLoading(String tips) {
        showProgressLoading(false, tips, 0,true);
    }

    @Override
    public void showProgressLoading(boolean isOperation, String tips, int delayMillis,boolean hiddenContentView) {
        if(mProgressLayout !=null || isFinishing()){
            return;
        }
        mProgressLayout = LayoutInflater.from(this).inflate(R.layout.progress, null);
        mProgressLayout.setVisibility(View.GONE);
        mGraduallyTextView = mProgressLayout.findViewById(R.id.loading);
        WindowManager.LayoutParams lp;
        if(!isOperation){//不可点击屏幕其他地方
            lp = new WindowManager.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT,
                    FrameLayout.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.TYPE_APPLICATION,
                    WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL
                            | WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                    PixelFormat.RGBA_8888);
            lp.gravity = Gravity.BOTTOM;
        }else{
            lp = new WindowManager.LayoutParams(FrameLayout.LayoutParams.WRAP_CONTENT,
                    FrameLayout.LayoutParams.WRAP_CONTENT, WindowManager.LayoutParams.TYPE_APPLICATION,
                    WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL
                            | WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                    PixelFormat.RGBA_8888);
            lp.gravity = Gravity.CENTER;
        }
        if(hiddenContentView){
            mContentView.setVisibility(View.INVISIBLE);
        }else{
            mContentView.setVisibility(View.VISIBLE);
            LinearLayout mLayout = mProgressLayout.findViewById(R.id.m_layout);
            mLayout.setBackgroundResource(R.drawable.border_d00_bg_d00_5);

        }
        mWindowManager = this.getWindowManager();
        mWindowManager.addView(mProgressLayout, lp);
        mProgressLayout.postDelayed(new Runnable() {
            @Override
            public void run() {
                if(mProgressLayout!=null && !isFinishing()){
                    mProgressLayout.setVisibility(View.VISIBLE);
                    mGraduallyTextView.startLoading();
                    mGraduallyTextView.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            dismissProgress();
                        }
                    },1000 * 60);
                }
            }
        },delayMillis);
    }

    public boolean isShowProgress(){
        return mWindowManager !=null && mProgressLayout !=null;
    }

    @Override
    public void dismissProgress(){
        dismissProgress(false);
    }

    private void dismissProgress(final boolean isFinishActivity){
        removeProgress(isFinishActivity);
    }

    private void removeProgress(boolean isFinishActivity){
        if(mContentView.getVisibility() == View.INVISIBLE){
            mContentView.setVisibility(View.VISIBLE);
        }
        if(mWindowManager !=null && mProgressLayout !=null){
            mWindowManager.removeView(mProgressLayout);
            mProgressLayout = null;
            mGraduallyTextView.stopLoading();
            mGraduallyTextView = null;
            if(isFinishActivity){
                finish();
            }
        }
    }

    private void checkNetwork(){
        if(!NetworkUtils.isConnected(this)){
        }
    }
    public boolean isAutoCloseProgress(){
        return true;
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == FinishCode){
            finish();
        }else if(resultCode == TextSizeCode){
            setResult(TextSizeCode);
        }
    }

    @Override
    public void onBackPressed() {
        onWindowFinish();
    }

    public void onWindowFinish(){
        finish();
    }
    @Override
    public void onPermissionChecker(String[] permissions,PermissionsResultAction permissionsResultAction){
        PermissionsManager.getInstance().requestPermissionsIfNecessaryForResult(this, permissions, permissionsResultAction);
    }


    @Override
    public boolean hasPermission(String[] permissions){
        return PermissionsManager.getInstance().hasAllPermissions(this,permissions);
    }

    public void onIntentSetting(){
        final String PACKAGE_URL_SCHEME = "package:"; // 方案
        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        intent.setData(Uri.parse(PACKAGE_URL_SCHEME + getPackageName()));
        startActivity(intent);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        PermissionsManager.getInstance().notifyPermissionsChange(permissions,grantResults);
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onResult(RequestWork req, ResponseWork resp) throws Exception {
        onResultActivity(req,resp);
    }
    public void onResultActivity(RequestWork req, ResponseWork resp) throws Exception{
        if(isAutoCloseProgress()){
            dismissProgress();
        }
        if(resp.getHttpCode() == HttpConnectWork.SocketCode){
            ToastUtil.warning(this,R.string.tips_service_error);
            checkNetwork();
        }

    }
}
