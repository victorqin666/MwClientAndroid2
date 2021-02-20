package com.work.mw.wemeet

import android.content.Context
import android.util.Log
import com.mwim.qcloud.tim.uikit.TUIKit
import com.mwim.qcloud.tim.uikit.base.BaseActivity
import com.tencent.wemeet.sdk.WemeetSDK
import com.tencent.wemeet.sdk.data.InitParams
import com.tencent.wemeet.sdk.data.WemeetError
import com.tencent.wemeet.sdk.ipc.RemoteCallback
import com.work.util.ToastUtil

object WemeetSdkHelper {

    private const val TAG = "SLog"

    // TODO SDKKEY替换
    private const val SDKKEY = "2009233371"
    // TODO sdktoken替换
//    private const val SDKTOKEN = "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJpc3MiOiIyMDA5MjMzMzcxIiwiaWF0IjoxNjAxMjgyNTY2LCJleHAiOjE2MDY1NTI5NjYsImF1ZCI6IlRlbmNlbnQgTWVldGluZyIsInN1YiI6Inl1YW56aGlfdGVzdDAxIn0.9DXh4MFF490mVipau7QgotrFvCe-tupj3JtefbTLQ44"
    private lateinit var mContext:Context;
    private var url = "";
    @JvmStatic
    fun init(context: Context,sdkToken:String) {
        mContext = context
        WemeetSDK.getInstance().initialize(context, InitParams(
            SDKKEY,
            sdkToken
        ))
    }

    @JvmStatic
    fun startAuth(url: String) {
        if (SDKKEY.isBlank()) {
            ToastUtil.error(TUIKit.getAppContext(), "Invalid parameter in $TAG:SDKKEY or SDKTOKEN,")
            return
        }
        if(mContext is BaseActivity){
            (mContext as BaseActivity).showProgressLoading(false,false)
        }
        WemeetSdkHelper.url = url;
        WemeetSDK.getInstance().login(LOGIN_CALLBACK)
    }
    @JvmStatic
    fun logout() {
        WemeetSDK.getInstance().logout(LOGOUT_CALLBACK)
    }

    private val LOGIN_CALLBACK: RemoteCallback.LoginCallback = object : RemoteCallback.LoginCallback() {
        override fun onLoginFinished(errorCode: Int, errorMsg: String) {
            if(mContext is BaseActivity){
                (mContext as BaseActivity).dismissProgress();
            }
            when (errorCode) {
                WemeetError.SUCCESS -> {
                    Log.i(TAG, "auth success")
                }
                else -> {
                    Log.i(TAG, "auth fail result code:$errorCode")
                    ToastUtil.error(mContext,"启动会议失败，请稍后尝试")
                }
            }
        }

        override fun onAuthCodeRefresh(callback: RemoteCallback.AuthCodeCallback) {
            Log.i(TAG, "onAuthCodeRefresh")
            AuthCodeHelper.getAuthCode(url) { authCode ->
                if (authCode.isBlank()) {
                    ToastUtil.error(TUIKit.getAppContext(), "获取auth code失败，请重试")
                } else {
                    callback.onAuthCodeResult(authCode)
                }
            }
        }
    }

    private val LOGOUT_CALLBACK: RemoteCallback.LogoutCallback = object : RemoteCallback.LogoutCallback() {
        override fun onLogoutFinished(errorCode: Int) {
            if (WemeetError.SUCCESS == errorCode) {
                Log.i(TAG, "logout success")
            } else {
                Log.i(TAG, "logout failed result code:$errorCode")
            }
        }
    }
}
