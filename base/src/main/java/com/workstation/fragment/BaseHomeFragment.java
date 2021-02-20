package com.workstation.fragment;

import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.http.network.listener.OnResultDataListener;
import com.http.network.model.RequestWork;
import com.http.network.model.ResponseWork;
import com.work.util.SLog;
import com.workstation.android.BaseHomeActivity;
import com.workstation.android.TakePhotoActivity;
import com.workstation.crop.config.CropProperty;
import com.workstation.listener.HomeWorkListener;
import com.workstation.listener.TakePhotoListener;
import com.workstation.listener.TitleWorkListener;
import com.workstation.model.DialogModel;
import com.workstation.permission.PermissionsManager;
import com.workstation.permission.PermissionsResultAction;

import java.util.Locale;

/**
 * Created by tangyx on 15/9/1.
 *
 */
public class BaseHomeFragment extends Fragment implements
        HomeWorkListener
        ,OnResultDataListener
        ,TitleWorkListener
        ,View.OnClickListener
        ,TakePhotoListener{
    protected BaseHomeActivity activity;
    protected View mContentView;
    public String thisClassName;

    public BaseHomeFragment(){

    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activity = (BaseHomeActivity)getActivity();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (mContentView == null) {
            mContentView = onCustomContentView();
            if(mContentView ==null){
                int layoutId = onCustomContentId();
                if(layoutId<=0){
                    layoutId = onInitContentView();
                }
                if(layoutId>0){
                    mContentView = inflater.inflate(layoutId,container,false);
                }
            }
            try {
                onInitView();
                onInitValue();
            }catch (Exception e){
                e.printStackTrace();
            }
        }
        return mContentView;
    }
    /**
     * 控件初始化
     */
    @Override
    public void onInitView() throws Exception{}
    @Override
    public void onInitValue() throws Exception{
    }

    @Override
    public View onCustomTitleView(LayoutInflater inflater) {
        return null;
    }

    @Override
    public View onCustomTitleRight(TextView view) {
        return activity.onCustomTitleRight(view);
    }

    @Override
    public void onRightClickListener(View view) {

    }

    ;
    /**
     * 初始化view
     */
    protected int onInitContentView(){
        thisClassName = this.getClass().getSimpleName();
        String resLayoutName = "fragment_"+thisClassName.replaceFirst("Fragment","");
        resLayoutName = resLayoutName.toLowerCase(Locale.getDefault());
        return getResources().getIdentifier(resLayoutName,"layout",activity.getPackageName());
    }

    @Override
    public View onCustomContentView(){
        return null;
    }

    @Override
    public int onCustomContentId() {
        return -1;
    }
    /**
     *
     * 获取控件
     */
    protected <T extends View> T findViewById(int id){
        View v = mContentView.findViewById(id);
        //按下特效处理
        return (T)v;
    }
    /**
     * 显示加载效果
     */
    public void showProgressLoading(){
        showProgressLoading(false, null, 0,true);
    }

    public void showProgressLoading(boolean isOperation) {
        activity.showProgressLoading(isOperation);
    }

    public void showProgressLoading(boolean isOperation,boolean hiddenContentView) {
        activity.showProgressLoading(isOperation,hiddenContentView);
    }

    @Override
    public void showProgressLoading(boolean isOperation, String tips, int delayMillis,boolean hiddenContentView) {
        activity.showProgressLoading(isOperation, tips, delayMillis,hiddenContentView);
    }

    public void showProgressLoading(int resId) {
        activity.showProgressLoading(resId);
    }

    public void showProgressLoading(String tips) {
        activity.showProgressLoading(tips);
    }

    @Override
    public void dismissProgress(){
        activity.dismissProgress();
    }

    @Override
    public void onResult(RequestWork req, ResponseWork resp) throws Exception {
        if(activity!=null){
            activity.onResultActivity(req,resp);
        }
    }

    @Override
    public void onClick(View v) {

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        PermissionsManager.getInstance().notifyPermissionsChange(permissions,grantResults);
    }

    @Override
    public void onPermissionChecker(String[] permissions,PermissionsResultAction permissionsResultAction){
        PermissionsManager.getInstance().requestPermissionsIfNecessaryForResult(this, permissions, permissionsResultAction);
    }

    @Override
    public boolean hasPermission(String[] permissions){
        return activity.hasPermission(permissions);
    }

    public boolean isShow(){
        return mContentView.isShown();
    }

    @Override
    public void onOpenCamera() {
        onOpenCamera(false);
    }

    public void onOpenCamera(boolean isCropImage){
        TakePhotoActivity takePhotoActivity = (TakePhotoActivity) getActivity();
        if(takePhotoActivity==null){
            SLog.e("TakePhotoActivity is null");
            return;
        }
        takePhotoActivity.setFragment(this);
        takePhotoActivity.onOpenCamera(isCropImage);
    }

    @Override
    public void onOpenPhoto() {
        onOpenPhoto(false);
    }

    public void onOpenPhoto(boolean isCropImage){
        TakePhotoActivity takePhotoActivity = (TakePhotoActivity) getActivity();
        if(takePhotoActivity==null){
            SLog.e("TakePhotoActivity is null");
            return;
        }
        takePhotoActivity.setFragment(this);
        takePhotoActivity.onOpenPhoto(isCropImage);
    }

    @Override
    public void onSelectImageCallback(String imagePath,CropProperty property) {

    }

    @Override
    public CropProperty onAttrCropImage(CropProperty cropProperty) {
        return cropProperty;
    }
}
