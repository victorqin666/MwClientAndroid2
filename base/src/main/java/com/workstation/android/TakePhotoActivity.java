package com.workstation.android;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import androidx.annotation.NonNull;
import androidx.annotation.StringRes;
import androidx.core.content.FileProvider;

import com.work.util.FileUtils;
import com.workstation.crop.AspectRatio;
import com.workstation.crop.CropActivity;
import com.workstation.crop.config.CropProperty;
import com.workstation.fragment.BaseHomeFragment;
import com.workstation.listener.TakePhotoListener;
import com.workstation.model.DialogModel;
import com.workstation.permission.PermissionsResultAction;

import java.io.File;

/**
 * Created by tangyx on 16/6/8.
 *
 */
public class TakePhotoActivity extends BaseHomeActivity implements TakePhotoListener{

    private static final int REQUEST_CHOOSE_PHOTO = 1101;
    public static final int RESULT_CROP_PHOTO = 1102;
    private BaseHomeFragment mFragment;
    /**
     * 是否裁剪
     */
    private boolean isCrop;

    /**
     * 拍照输出地址
     */
    private Uri mOutputFileUri;
    /**
     *  启动相机
     */
    @Override
    public void onOpenCamera(){
        onOpenCamera(false);
    }

    public void onOpenCamera(boolean isCropImage){
        this.isCrop = isCropImage;
        String[] permission = {Manifest.permission.CAMERA,Manifest.permission.RECORD_AUDIO};
        if(hasPermission(permission)){
            intentCamera();
        }else{
            onPermissionChecker(permission, new PermissionsResultAction() {
                @Override
                public void onGranted() {
                    intentCamera();
                }

                @Override
                public void onDenied(String permission) {
                    showPermissionDeniedCamera(R.string.permission_camera);
                }
            });
        }
    }

    private void intentCamera(){
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (mOutputFileUri == null) {
            mOutputFileUri = getCaptureImageOutputUri(this);
        }
        intent.putExtra(MediaStore.EXTRA_OUTPUT, getIntentUri(this, mOutputFileUri));
        startActivityForResult(intent,REQUEST_CHOOSE_PHOTO);
    }

    private Uri getCaptureImageOutputUri(@NonNull Context context) {
        Uri outputFileUri = null;
        File getImage = context.getExternalCacheDir();
        if (getImage != null) {
            outputFileUri = Uri.fromFile(new File(getImage.getPath(), "pickImageResult.jpeg"));
        }
        return outputFileUri;
    }

    public static Uri getIntentUri(Context context, Uri uri){
        //support android N+
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.N){
            return getContentUri(context, uri);
        }else{
            return uri;
        }
    }

    public static Uri getContentUri(Context context, Uri fileUri){
        return FileProvider.getUriForFile(context, context.getApplicationContext().getPackageName() + ".fileProvider", new File(fileUri.getPath()));
    }

    /**
     * 开启相册
     */
    @Override
    public void onOpenPhoto(){
        onOpenPhoto(false);
    }

    public void onOpenPhoto(boolean isCropImage){
        this.isCrop = isCropImage;
        String[] permission = {Manifest.permission.WRITE_EXTERNAL_STORAGE};
        if(hasPermission(permission)){
            intentPhoto();
        }else{
            onPermissionChecker(permission, new PermissionsResultAction() {
                @Override
                public void onGranted() {
                    intentPhoto();
                }

                @Override
                public void onDenied(String permission) {
                    showPermissionDeniedCamera(R.string.permission_photo);
                }
            });
        }
    }

    private void intentPhoto(){
        mOutputFileUri = null;
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_GET_CONTENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("image/*");
        startActivityForResult(intent, REQUEST_CHOOSE_PHOTO);
    }

    @Override
    public void onSelectImageCallback(String imagePath,CropProperty property){}

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_CHOOSE_PHOTO && resultCode == Activity.RESULT_OK) {
            if(mOutputFileUri !=null){
                startCropActivity(mOutputFileUri);
            }else{
                startCropActivity(data.getData());
            }
        } else if(resultCode == RESULT_CROP_PHOTO){
            Uri uri = data.getParcelableExtra(CropActivity.EXTRA_URI);
            CropProperty property = data.getParcelableExtra(CropActivity.EXTRA_CROP_IWA_CONFIG);
            onSelectImageCallback(FileUtils.getUriPath(this,uri),property);
        }else{
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    private void startCropActivity(Uri uri){
        if(this.isCrop){
            CropProperty property = new CropProperty();
            property.setAspectRatio(new AspectRatio(1,1));
            if(mFragment==null){
                property = onAttrCropImage(property);
            }else{
                property = mFragment.onAttrCropImage(property);
                mFragment = null;
            }
            startActivityForResult(CropActivity.callingIntent(this, uri,property),0);
        }else{
            onSelectImageCallback(FileUtils.getUriPath(this,uri),null);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    public void showPermissionDeniedCamera(@StringRes int id){
//        DialogModel dm = new DialogModel();
//        dm.setContent(getString(id));
//        dm.setTitle(getString(R.string.label_help));
//        showDialog(dm)
//                .btnNum(2)
//                .btnText(getString(R.string.label_know),getString(R.string.label_setting))
//                .setOnClickDialog(new BaseAlertDialog.OnDialogButton() {
//                    @Override
//                    public void onClickButton() {
//                        dismissDialog();
//                    }
//                }, new BaseAlertDialog.OnDialogButton() {
//                    @Override
//                    public void onClickButton() {
//                        dismissDialog();
//                        onIntentSetting();
//                    }
//                });
    }
    @Override
    public CropProperty onAttrCropImage(CropProperty cropProperty){
        return cropProperty;
    }

    public void setFragment(BaseHomeFragment mFragment) {
        this.mFragment = mFragment;
    }
}
