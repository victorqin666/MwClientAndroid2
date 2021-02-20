package com.mwim.qcloud.tim.uikit.business.fragment;

import android.Manifest;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.mwim.qcloud.tim.uikit.R;
import com.mwim.qcloud.tim.uikit.base.BaseActivity;
import com.mwim.qcloud.tim.uikit.base.BaseFragment;
import com.mwim.qcloud.tim.uikit.business.active.WebActivity;
import com.work.util.SLog;
import com.workstation.permission.PermissionsResultAction;
import com.workstation.view.MaterialMenuDrawable;
import com.workstation.view.MaterialMenuView;

import static com.mwim.qcloud.tim.uikit.business.active.WebActivity.CheckLocation;
import static com.mwim.qcloud.tim.uikit.business.active.WebActivity.UA;

/**
 * Created by tangyx
 * Date 2020/4/27
 * email tangyx@live.com
 */
public class WebViewFragment extends BaseFragment implements WebViewProgress.OnWebLoadListener, View.OnClickListener {
    public static String[] PERMISSIONS = new String[]{
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION,};
    private View mErrorLayout;
    private TextView mErrorText;
    private View mBack;
    private TextView mBackTitle;
    private TextView mWebTitle;
    private MaterialMenuView mClose;
    private WebViewProgress mWeb;
    private String mUa;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View mBaseView = inflater.inflate(R.layout.fragment_web_view, container, false);
        mErrorLayout = mBaseView.findViewById(R.id.error_layout);
        mErrorText = mBaseView.findViewById(R.id.error_text);
        mBack = mBaseView.findViewById(R.id.web_back);
        mBackTitle = mBaseView.findViewById(R.id.web_back_title);
        mWebTitle = mBaseView.findViewById(R.id.web_title);
        mClose = mBaseView.findViewById(R.id.web_close);
        mWeb = mBaseView.findViewById(R.id.webview);
        mErrorLayout.setOnClickListener(this);
        mWeb.setOverScrollMode(View.OVER_SCROLL_NEVER);
        mWeb.setOnWebLoadListener(this);
        mBack.setOnClickListener(this);
        mBackTitle.setOnClickListener(this);
        mClose.setOnClickListener(this);
        mClose.setState(MaterialMenuDrawable.IconState.X);
        loadWebUrl();
        return mBaseView;
    }

    private void loadWebUrl(){
        Bundle bundle = getArguments();
        if(bundle==null){
            getActivity().finish();
            return;
        }
        if(bundle.getBoolean(CheckLocation) && getActivity()!=null && getActivity() instanceof BaseActivity){
            if(!((BaseActivity) getActivity()).hasPermission(PERMISSIONS)){
                ((BaseActivity) getActivity()).onPermissionChecker(PERMISSIONS, new PermissionsResultAction() {
                    @Override
                    public void onGranted() {
                        SLog.e("定位权限成功...");
                    }

                    @Override
                    public void onDenied(String permission) {
                        SLog.e("定位权限失败："+permission);
                    }
                });
            }
        }
        String url = bundle.getString(WebActivity.class.getSimpleName());
        mUa = bundle.getString(UA);
        if(!TextUtils.isEmpty(mUa)){
            mWeb.setUserAgentString(mUa);
        }
        mWeb.loadUrl(url);
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.web_back || id == R.id.web_back_title) {
            onBack();
        } else if (id == R.id.web_close) {
            getActivity().finish();
        } else if (id == R.id.error_layout) {
            reload();
        }
    }

    public boolean onBack(){
        if(!mWeb.isWebError() && mWeb!=null && mWeb.canGoBack()){
            mWeb.goBack();
            return true;
        }
        getActivity().finish();
        return false;
    }

    @Override
    public void onLoadFinal() {
        if(mWeb.isWebError()){
            mErrorLayout.setVisibility(View.VISIBLE);
            mErrorText.setText(R.string.text_discover_error);
        }else{
            mErrorLayout.setVisibility(View.GONE);
        }
    }

    @Override
    public void onWebTitleChange(String title) {
        if(!TextUtils.isEmpty(title)){
            mWebTitle.setText(title);
        }else{
            mWebTitle.setText(R.string.app_name);
        }
    }

    @Override
    public void onLoadResource(String url) {
        if(mWeb.canGoBack()){
            if(mBack.getVisibility() == View.GONE ){
                mBack.setVisibility(View.VISIBLE);
                mBackTitle.setVisibility(View.VISIBLE);
                mClose.setVisibility(View.VISIBLE);
            }
        }else if(mBack.getVisibility() == View.VISIBLE){
//            mBack.setVisibility(View.GONE);
//            mBackTitle.setVisibility(View.GONE);
//            mClose.setVisibility(View.GONE);
        }
    }

    @Override
    public void onReceivedError() {//网页加载失败了
        mErrorText.setText(R.string.text_discover_error);
    }

    @Override
    public void onShowFileChooser() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("*/*");
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        startActivityForResult(intent,0);
    }

    public void reload(){
        if(mWeb!=null){
            mErrorLayout.setVisibility(View.VISIBLE);
            mErrorText.setText(R.string.text_loading);
            mWeb.reload();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if("hsh_android".equals(mUa)){//打车
            reload();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        mWeb.fileValueCallbackResult(requestCode,resultCode,data);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if(mWeb!=null){
            mWeb.destroy();
        }
    }
}
