package com.mwim.qcloud.tim.uikit.business.dialog;

import android.text.Html;
import android.text.Spanned;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.StringRes;

import com.mwim.qcloud.tim.uikit.R;

/**
 * Created by Administrator on 2019/4/22
 * Description
 */

public class ConfirmDialog extends BaseDialog implements View.OnClickListener {

    private int mContent;
    private String mContentStr;
    private Spanned mContentSpanned;
    private View.OnClickListener onConfirmListener;
    private View.OnClickListener onCancelListener;
    private boolean isConfirmDismiss = true;
    private TextView mCancel;
    private TextView mConfirm;
    private int mCancelTextResId;
    private int mConfirmTextResId;
    private boolean hiddenCancel;

    @Override
    public void onInitView() {
        super.onInitView();
        TextView mContentText = findViewById(R.id.content);
        if(mContent>0){
            mContentText.setText(mContent);
        }else if(!TextUtils.isEmpty(mContentStr)){
            mContentText.setText(Html.fromHtml(mContentStr));
        }else if(mContentSpanned!=null){
            mContentText.setText(mContentSpanned);
        }
        mCancel = findViewById(R.id.cancel);
        mCancel.setOnClickListener(this);
        mConfirm = findViewById(R.id.confirm);
        mConfirm.setOnClickListener(this);
    }

    @Override
    public void onInitValue() {
        super.onInitValue();
        if(hiddenCancel){
            mCancel.setVisibility(View.GONE);
        }
        if(mCancelTextResId>0){
            mCancel.setText(mCancelTextResId);
        }
        if(mConfirmTextResId>0){
            mConfirm.setText(mConfirmTextResId);
        }
    }

    public ConfirmDialog setContent(int content) {
        this.mContent = content;
        return this;
    }

    public ConfirmDialog setContent(String content) {
        this.mContentStr = content;
        return this;
    }

    public ConfirmDialog setContentSpanned(Spanned mContentSpanned) {
        this.mContentSpanned = mContentSpanned;
        return this;
    }

    public ConfirmDialog setOnConfirmListener(View.OnClickListener onConfirmListener) {
        this.onConfirmListener = onConfirmListener;
        return this;
    }

    public ConfirmDialog setOnCancelListener(View.OnClickListener onCancelListener) {
        this.onCancelListener = onCancelListener;
        return this;
    }

    public ConfirmDialog setCancelTextResId(@StringRes int mCancelTextResId) {
        this.mCancelTextResId = mCancelTextResId;
        return this;
    }

    public ConfirmDialog setConfirmTextResId(@StringRes int mConfirmTextResId) {
        this.mConfirmTextResId = mConfirmTextResId;
        return this;
    }

    public ConfirmDialog setConfirmDismiss(boolean confirmDismiss) {
        isConfirmDismiss = confirmDismiss;
        return this;
    }

    public ConfirmDialog setHiddenCancel(boolean hiddenCancel) {
        this.hiddenCancel = hiddenCancel;
        return this;
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.cancel) {
            dismiss();
            if (onCancelListener != null) {
                onCancelListener.onClick(v);
            }
        } else if (id == R.id.confirm) {
            if (isConfirmDismiss) {
                dismiss();
            }
            if (onConfirmListener != null) {
                onConfirmListener.onClick(v);
            }
        }
    }
}
