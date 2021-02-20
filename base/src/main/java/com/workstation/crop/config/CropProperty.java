package com.workstation.crop.config;

import android.os.Parcel;
import android.os.Parcelable;

import com.workstation.crop.AspectRatio;

/**
 * create by tangyx
 * 2018/7/2
 */
public class CropProperty implements Parcelable{

    public final static String RECT = "rect";
    public final static String OVAL = "oval";

    private AspectRatio aspectRatio;
    private String cropShape = RECT;
    private boolean dynamicAspectRation;
    private boolean dynamicOverlay;

    public CropProperty(){}

    protected CropProperty(Parcel in) {
        aspectRatio = in.readParcelable(AspectRatio.class.getClassLoader());
        cropShape = in.readString();
        dynamicAspectRation = in.readByte() != 0;
        dynamicOverlay = in.readByte() != 0;
    }

    public static final Creator<CropProperty> CREATOR = new Creator<CropProperty>() {
        @Override
        public CropProperty createFromParcel(Parcel in) {
            return new CropProperty(in);
        }

        @Override
        public CropProperty[] newArray(int size) {
            return new CropProperty[size];
        }
    };

    public void setAspectRatio(AspectRatio aspectRatio) {
        this.aspectRatio = aspectRatio;
    }

    public AspectRatio getAspectRatio() {
        return aspectRatio;
    }

    public String getCropShape() {
        return cropShape;
    }

    public void setCropShape(String cropShape) {
        this.cropShape = cropShape;
    }

    public void setDynamicAspectRation(boolean dynamicAspectRation) {
        this.dynamicAspectRation = dynamicAspectRation;
    }

    public boolean isDynamicAspectRation() {
        return dynamicAspectRation;
    }

    public void setDynamicOverlay(boolean dynamicOverlay) {
        this.dynamicOverlay = dynamicOverlay;
    }

    public boolean isDynamicOverlay() {
        return dynamicOverlay;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeParcelable(aspectRatio, flags);
        dest.writeString(cropShape);
        dest.writeByte((byte) (dynamicAspectRation ? 1 : 0));
        dest.writeByte((byte) (dynamicOverlay ? 1 : 0));
    }
}
