package com.mwim.qcloud.tim.uikit.component;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.TextView;

import androidx.annotation.DrawableRes;
import androidx.appcompat.widget.SwitchCompat;
import androidx.core.content.ContextCompat;

import com.mwim.qcloud.tim.uikit.utils.ScreenUtil;
import com.mwim.qcloud.tim.uikit.R;
import com.work.util.SizeUtils;

/**
 * 设置等页面条状控制或显示信息的控件
 */
public class LineControllerView extends LinearLayout {

    private String mName;
    private boolean mIsBottom;
    private boolean mIsTop;
    private String mContent;
    private boolean mIsJump;
    private boolean mIsSwitch;

    private TextView mContentText;
    private TextView mNameText;
    private ImageView mNavArrowView;
    private View bottomLine;
    private View topLine;
    private Switch mSwitchView;

    public LineControllerView(Context context, AttributeSet attrs) {
        super(context, attrs);
        LayoutInflater.from(context).inflate(R.layout.line_controller_view, this);
        TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.LineControllerView, 0, 0);
        try {
            mName = ta.getString(R.styleable.LineControllerView_name);
            mContent = ta.getString(R.styleable.LineControllerView_subject);
            mIsBottom = ta.getBoolean(R.styleable.LineControllerView_isBottom, false);
            mIsTop = ta.getBoolean(R.styleable.LineControllerView_isTop, false);
            mIsJump = ta.getBoolean(R.styleable.LineControllerView_canNav, false);
            mIsSwitch = ta.getBoolean(R.styleable.LineControllerView_isSwitch, false);
            setUpView();
        } finally {
            ta.recycle();
        }
    }

    private void setUpView() {
        mNameText = findViewById(R.id.name);
        mNameText.setText(mName);
        mContentText = findViewById(R.id.content);
        mContentText.setText(mContent);
        bottomLine = findViewById(R.id.bottomLine);
        bottomLine.setVisibility(mIsBottom ? VISIBLE : GONE);
        topLine = findViewById(R.id.topLine);
        topLine.setVisibility(mIsTop?GONE:VISIBLE);
        mNavArrowView = findViewById(R.id.rightArrow);
        mNavArrowView.setColorFilter(ContextCompat.getColor(getContext(),R.color.color_E4E6E9), PorterDuff.Mode.SRC_ATOP);
        mNavArrowView.setVisibility(mIsJump ? VISIBLE : GONE);
        RelativeLayout contentLayout = findViewById(R.id.contentText);
        contentLayout.setVisibility(mIsSwitch ? GONE : VISIBLE);
        mSwitchView = findViewById(R.id.btnSwitch);
        mSwitchView.setVisibility(mIsSwitch ? VISIBLE : GONE);
    }
    public void setLeftDrawable(@DrawableRes int resId){
        Drawable drawable = ContextCompat.getDrawable(getContext(),resId);
        if(drawable!=null){
            drawable.setBounds(0,0,drawable.getMinimumWidth(),drawable.getMinimumHeight());
//            drawable.setColorFilter(ContextCompat.getColor(getContext(),R.color.color_d4d4d4), PorterDuff.Mode.SRC_ATOP);
        }
        mNameText.setCompoundDrawables(drawable,null,null,null);
        LinearLayout.LayoutParams params = (LayoutParams) bottomLine.getLayoutParams();
        params.setMargins(SizeUtils.dp2px(getContext(),55),0,0,0);
        params = (LayoutParams) topLine.getLayoutParams();
        params.setMargins(SizeUtils.dp2px(getContext(),55),0,0,0);
    }
    /**
     * 获取内容
     */
    public String getContent() {
        return mContentText.getText().toString();
    }

    /**
     * 设置文字内容
     *
     * @param content 内容
     */
    public void setContent(String content) {
        this.mContent = content;
        mContentText.setText(content);
    }

    public void setSingleLine(boolean singleLine) {
        mContentText.setSingleLine(singleLine);
    }

    /**
     * 设置是否可以跳转
     *
     * @param canNav 是否可以跳转
     */
    public void setCanNav(boolean canNav) {
        this.mIsJump = canNav;
        mNavArrowView.setVisibility(canNav ? VISIBLE : GONE);
        if (canNav) {
            ViewGroup.LayoutParams params = mContentText.getLayoutParams();
            params.width = ScreenUtil.getPxByDp(120);
            params.height = ViewGroup.LayoutParams.WRAP_CONTENT;
            mContentText.setLayoutParams(params);
//            mContentText.setTextIsSelectable(false);
        } else {
            ViewGroup.LayoutParams params = mContentText.getLayoutParams();
            params.width = ViewGroup.LayoutParams.WRAP_CONTENT;
            params.height = ViewGroup.LayoutParams.WRAP_CONTENT;
            mContentText.setLayoutParams(params);
//            mContentText.setTextIsSelectable(true);
        }
    }

    public boolean isChecked() {
        return mSwitchView.isChecked();
    }

    /**
     * 设置开关状态
     *
     * @param on 开关
     */
    public void setChecked(boolean on) {
        mSwitchView.setChecked(on);
    }

    /**
     * 设置开关监听
     *
     * @param listener 监听
     */
    public void setCheckListener(CompoundButton.OnCheckedChangeListener listener) {
        mSwitchView.setOnCheckedChangeListener(listener);
    }


}
