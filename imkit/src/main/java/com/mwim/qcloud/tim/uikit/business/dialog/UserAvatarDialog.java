package com.mwim.qcloud.tim.uikit.business.dialog;

import android.view.View;

import com.mwim.qcloud.tim.uikit.R;

/**
 * Created by tangyx
 * Date 2020/9/14
 * email tangyx@live.com
 */

public class UserAvatarDialog extends BaseDialog {

    private View.OnClickListener mOnClickListener;

    @Override
    public void onInitView() {
        super.onInitView();
        findViewById(R.id.camera).setOnClickListener(mOnClickListener);
        findViewById(R.id.photo).setOnClickListener(mOnClickListener);
    }

    public UserAvatarDialog setOnClickListener(View.OnClickListener mOnClickListener) {
        this.mOnClickListener = mOnClickListener;
        return this;
    }
}
