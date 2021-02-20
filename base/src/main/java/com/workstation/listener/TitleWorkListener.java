package com.workstation.listener;

import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

/**
 * Created by tangyx on 16/3/28.
 */
public interface TitleWorkListener {
    /**
     * 自定义布局
     * @param inflater
     * @return
     */
    View onCustomTitleView(LayoutInflater inflater);
    /**
     * 自定义右边
     */
    View onCustomTitleRight(TextView view);
    /**
     * 对右边元素的click事件监听
     */
    void onRightClickListener(View view);
}
