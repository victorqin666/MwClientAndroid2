package com.workstation.crop;

import android.os.Parcel;
import android.os.Parcelable;
import androidx.annotation.IntRange;

public class AspectRatio implements Parcelable{

    @SuppressWarnings("Range")
    public static final AspectRatio IMG_SRC = new AspectRatio(-1, -1);

    private final int width;
    private final int height;

    public AspectRatio(@IntRange(from = 1) int w, @IntRange(from = 1) int h) {
        this.width = w;
        this.height = h;
    }

    protected AspectRatio(Parcel in) {
        width = in.readInt();
        height = in.readInt();
    }

    public static final Creator<AspectRatio> CREATOR = new Creator<AspectRatio>() {
        @Override
        public AspectRatio createFromParcel(Parcel in) {
            return new AspectRatio(in);
        }

        @Override
        public AspectRatio[] newArray(int size) {
            return new AspectRatio[size];
        }
    };

    public int getWidth() {
        return width;
    }

    public boolean isSquare() {
        return width == height;
    }

    public int getHeight() {
        return height;
    }

    public float getRatio() {
        return ((float) width) / height;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(width);
        dest.writeInt(height);
    }
}
