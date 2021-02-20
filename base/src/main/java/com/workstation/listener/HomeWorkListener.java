package com.workstation.listener;

import android.view.View;

import com.workstation.permission.PermissionsResultAction;

/**
 * Created by tangyx on 15/9/10.
 */
public interface HomeWorkListener {
    /**
     * 显示加载过程，
     */
    void showProgressLoading(boolean isOperation, String tips, int delayMillis,boolean hiddenContentView);
    /**
     * 隐藏加载框
     * 如果显示到结束低于1秒时间 则延迟1秒关闭，避免一种闪避效果
     */
    void dismissProgress();
    /**
     * 控件初始化
     */
    void onInitView() throws Exception;
    void onInitValue() throws Exception;
    /**
     * 自定义布局
     */
    View onCustomContentView();
    int onCustomContentId();
    /**
     * 权限管理
     */
    void onPermissionChecker(String[] permissions, PermissionsResultAction permissionsResultAction);
    boolean hasPermission(String[] permissions);
}
