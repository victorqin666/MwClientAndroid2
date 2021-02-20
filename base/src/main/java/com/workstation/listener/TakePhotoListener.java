package com.workstation.listener;


import com.workstation.crop.config.CropProperty;

/**
 * Created by tangyx on 16/6/8.
 */
public interface TakePhotoListener {
    /**
     * 拍照 相册
     */
    void onOpenCamera();
    void onOpenPhoto();
    void onSelectImageCallback(String imagePath,CropProperty property);
    /**
     * 剪切照片参数
     */
    CropProperty onAttrCropImage(CropProperty cropProperty);
}
