package com.mwim.qcloud.tim.uikit.business.active;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.mwim.qcloud.tim.uikit.R;
import com.mwim.qcloud.tim.uikit.base.BaseActivity;
import com.mwim.qcloud.tim.uikit.business.barcodescanner.QRCodeConstant;
import com.mwim.qcloud.tim.uikit.business.barcodescanner.QRCodeUtils;
import com.mwim.qcloud.tim.uikit.business.modal.UserApi;
import com.mwim.qcloud.tim.uikit.component.picture.imageEngine.impl.GlideEngine;
import com.work.util.FileUtils;
import com.work.util.ToastUtil;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;

/**
 * Created by tangyx
 * Date 2020/11/23
 * email tangyx@live.com
 */

public class UserQRCodeActivity extends BaseActivity {

    @Override
    public void onInitView() throws Exception {
        super.onInitView();
        ImageView mAvatar = findViewById(R.id.avatar);
        TextView mName = findViewById(R.id.name);
        TextView mMobile = findViewById(R.id.mobile);
        GlideEngine.loadCornerAvatar(mAvatar, UserApi.instance().getUserIcon());
        mName.setText(UserApi.instance().getNickName());
        mMobile.setText(UserApi.instance().getMobile());

        final ImageView mQRCodeImage = findViewById(R.id.qr_code_image);
        String content = generateUserQRCodeContent(UserApi.instance().getUserId());
        final Bitmap bitmap = QRCodeUtils.generateImage(content,mQRCodeImage.getLayoutParams().width,mQRCodeImage.getLayoutParams().height,null);
        mQRCodeImage.setImageBitmap(bitmap);

        final LinearLayout mQRCodeLayout = findViewById(R.id.qr_code_layout);
        final View mSaveQrCode = findViewById(R.id.save_qr_code);
        mSaveQrCode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    mSaveQrCode.setVisibility(View.GONE);
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            String name = UserApi.instance().getUserId();
                            if(name.length()>10){
                                name = name.substring(0,10);
                            }
                            String path = new FileUtils(UserQRCodeActivity.this).saveBitmap(name+".png",createViewBitmap(mQRCodeLayout));
                            MediaScannerConnection.scanFile(UserQRCodeActivity.this,
                                    new String[]{path},
                                    new String[]{"image/jpeg"},
                                    new MediaScannerConnection.OnScanCompletedListener() {
                                        @Override
                                        public void onScanCompleted(final String path, Uri uri) {
                                            mQRCodeImage.post(new Runnable() {
                                                @Override
                                                public void run() {
                                                    mSaveQrCode.setVisibility(View.VISIBLE);
                                                    ToastUtil.success(UserQRCodeActivity.this,getString(R.string.toast_save_success)+":"+path);
                                                }
                                            });
                                        }
                                    });
                        }
                    }).start();
                }catch (Exception e){
                    e.printStackTrace();
                    ToastUtil.error(UserQRCodeActivity.this,R.string.toast_save_fail);
                }
            }
        });
    }
    public Bitmap createViewBitmap(View v) {
        Bitmap bitmap = Bitmap.createBitmap(v.getWidth(), v.getHeight(),
                Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        v.draw(canvas);
        return bitmap;
    }

    /**
     * 生成用户二维码内容
     *
     * @param userId
     * @return
     */
    public String generateUserQRCodeContent(String userId) {
        Uri baseUri = Uri.parse(QRCodeConstant.BASE_URL);
        Uri sealGroupUri = new Uri.Builder()
                .scheme(QRCodeConstant.QRIMChat.SCHEME)
                .authority(QRCodeConstant.QRIMChat.AUTHORITY_USER)
                .appendPath(QRCodeConstant.QRIMChat.USER_PATH_INFO)
                .appendQueryParameter(QRCodeConstant.QRIMChat.USER_QUERY_USER_ID, userId)
                .build();
        Uri fullUri = baseUri.buildUpon().appendQueryParameter(QRCodeConstant.BASE_URL_QUERY_CONTENT, sealGroupUri.toString()).build();
        String url = fullUri.toString();
        try {
            url = URLDecoder.decode(url, "utf-8");
        } catch (UnsupportedEncodingException e) {
        }
        return url;
    }

    @Override
    public void onInitValue() throws Exception {
        super.onInitValue();
        setTitleName(R.string.text_qr_code);
    }
}
