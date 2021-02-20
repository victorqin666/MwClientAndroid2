package com.mwim.qcloud.tim.uikit.business.active;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.mwim.qcloud.tim.uikit.R;
import com.mwim.qcloud.tim.uikit.base.BaseActivity;
import com.mwim.qcloud.tim.uikit.business.barcodescanner.BarcodeResult;
import com.mwim.qcloud.tim.uikit.business.barcodescanner.CaptureManager;
import com.mwim.qcloud.tim.uikit.business.barcodescanner.DecoratedBarcodeView;
import com.mwim.qcloud.tim.uikit.business.barcodescanner.QRCodeConstant;
import com.mwim.qcloud.tim.uikit.business.barcodescanner.QRCodeUtils;
import com.mwim.qcloud.tim.uikit.utils.IMKitConstants;
import com.work.util.NetworkUtils;
import com.work.util.SLog;
import com.work.util.ToastUtil;
import com.workstation.crop.config.CropProperty;

/**
 * Created by tangyx
 * Date 2020/11/23
 * email tangyx@live.com
 */

public class ScanIMQRCodeActivity extends BaseActivity implements View.OnClickListener {

    private CaptureManager capture;
    private DecoratedBarcodeView barcodeScannerView;
    private TextView lightControlTv;

    private boolean isCameraLightOn = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        barcodeScannerView = findViewById(R.id.zxing_barcode_scanner);
        capture = new CaptureManager(this, barcodeScannerView);
        capture.initializeFromIntent(getIntent(), savedInstanceState);
        capture.setOnCaptureResultListener(new CaptureManager.OnCaptureResultListener() {
            @Override
            public void onCaptureResult(BarcodeResult result) {
                handleQrCode(result.toString());
            }
        });
        barcodeScannerView.getViewFinder().networkChange(!NetworkUtils.isConnected(this));
        if (!NetworkUtils.isConnected(this)) {
            capture.stopDecode();
        } else {
            capture.decode();
        }
        barcodeScannerView.setTorchListener(new DecoratedBarcodeView.TorchListener() {
            @Override
            public void onTorchOn() {
                lightControlTv.setText(R.string.zxing_close_light);
                isCameraLightOn = true;
            }

            @Override
            public void onTorchOff() {
                lightControlTv.setText(R.string.zxing_open_light);
                isCameraLightOn = false;
            }
        });
        barcodeScannerView.setTorchListener(new DecoratedBarcodeView.TorchListener() {
            @Override
            public void onTorchOn() {
                lightControlTv.setText(R.string.zxing_close_light);
                isCameraLightOn = true;
            }

            @Override
            public void onTorchOff() {
                lightControlTv.setText(R.string.zxing_open_light);
                isCameraLightOn = false;
            }
        });
        lightControlTv = findViewById(R.id.zxing_open_light);
        lightControlTv.setOnClickListener(this);
        TextView selectPicTv = findViewById(R.id.zxing_select_pic);
        selectPicTv.setVisibility(View.GONE);
    }

    @Override
    public void onInitValue() throws Exception {
        super.onInitValue();
        setTitleName(R.string.scan_qr_code);
    }

    @Override
    public View onCustomTitleRight(TextView view) {
        view.setText(R.string.user_select_phone);
        return view;
    }

    @Override
    public void onRightClickListener(View view) {
        super.onRightClickListener(view);
        onOpenPhoto(false);
    }

    @Override
    public int onCustomContentId() {
        return R.layout.zxing_capture;
    }
    /**
     * 切换摄像头照明
     */
    private void switchCameraLight() {
        if (isCameraLightOn) {
            barcodeScannerView.setTorchOff();
        } else {
            barcodeScannerView.setTorchOn();
        }
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.zxing_open_light) {
            switchCameraLight();
        }
    }
    @Override
    protected void onResume() {
        super.onResume();
        capture.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        capture.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        capture.onDestroy();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        capture.onSaveInstanceState(outState);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String permissions[], @NonNull int[] grantResults) {
        capture.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        return barcodeScannerView.onKeyDown(keyCode, event) || super.onKeyDown(keyCode, event);
    }

    @Override
    public void onSelectImageCallback(String imagePath, CropProperty property) {
        super.onSelectImageCallback(imagePath, property);
        String result = QRCodeUtils.analyzeImage(imagePath);
        handleQrCode(result);
    }
    /**
     * 处理二维码结果，并跳转到相应界面
     */
    private void handleQrCode(String qrCodeText) {
        if(SLog.debug)SLog.e("qrCodeText:"+qrCodeText);
        if (TextUtils.isEmpty(qrCodeText)) {
            ToastUtil.error(this,R.string.zxing_qr_can_not_recognized);
            return;
        }
        // 处理二维码结果
        qRCodeType(qrCodeText);
    }

    /**
     * 获取 QR 二维码结果
     */
    public void qRCodeType(String qrCodeStr){
        Uri uri = Uri.parse(qrCodeStr);
        Uri yzUri = null;

        if(uri.getScheme() != null && QRCodeConstant.QRIMChat.SCHEME.equals(uri.getScheme().toLowerCase())){
            yzUri = uri;
        } else {
            // 若从其他跳转至 判断请求参数中是否包含 的 uri
            String content = uri.getQueryParameter(QRCodeConstant.BASE_URL_QUERY_CONTENT);
            if (!TextUtils.isEmpty(content)) {
                Uri contentUri = Uri.parse(content);
                if(contentUri.getScheme() != null && QRCodeConstant.QRIMChat.SCHEME.equals(contentUri.getScheme().toLowerCase())) {
                    yzUri = contentUri;
                }
            }
        }

        if(yzUri != null){
            String authority = yzUri.getAuthority();
            String path = yzUri.getPath();
            if(path != null && path.startsWith("/")){
                path = path.substring(1);
            }
            if(QRCodeConstant.QRIMChat.AUTHORITY_USER.equals(authority)){
                // 用户信息结果
                if (QRCodeConstant.QRIMChat.USER_PATH_INFO.equals(path)){
                    String userId = yzUri.getQueryParameter(QRCodeConstant.QRIMChat.USER_QUERY_USER_ID);
                    if(!TextUtils.isEmpty(userId)){
                        startActivity(new Intent(ScanIMQRCodeActivity.this,FriendProfileActivity.class).putExtra(IMKitConstants.ProfileType.CONTENT,userId));
                        finish();
                    }
                }
            }else{
                ToastUtil.error(this,R.string.zxing_qr_can_not_recognized);
                finish();
            }
        }else{
            ToastUtil.error(this,R.string.zxing_qr_can_not_recognized);
            finish();
        }
    }
}
