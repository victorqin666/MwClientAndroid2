package com.workstation.android;

import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import androidx.annotation.ColorRes;
import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import androidx.core.content.ContextCompat;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.workstation.listener.TitleWorkListener;
import com.workstation.view.MaterialMenuDrawable;
import com.workstation.view.MaterialMenuView;

/**
 * Created by tangyx on 16/3/28.
 */
public class ToolBarActivity extends AppCompatActivity implements TitleWorkListener {
    public Context mContext;
    private LinearLayout mContentView;
    private Toolbar mToolBar;
    protected LayoutInflater mInflater;
    //title控件
    private ActionBar mActionBar = null;
    protected TextView mBackName;
    protected View mBackView;
    protected TextView mTitleName;
    protected RelativeLayout mTitleLayout;

    private View mActionBarView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = this;
        mInflater = LayoutInflater.from(mContext);
    }


    @Override
    public void setContentView(int layoutResID) {
        setContentView(mInflater.inflate(layoutResID, null));
    }

    @Override
    public void setContentView(View view) {
        if (isShowTitleBar() && !initTitle()) {
            /*初始化整个内容*/
            initContentView();
            initToolBar();
            initUserView(view);
            super.setContentView(mContentView);
            /*把 toolbar 设置到Activity 中*/
            setSupportActionBar(mToolBar);
        } else {
            super.setContentView(view);
        }
    }

    /**
     * 初始化title
     */
    protected boolean initTitle() {
        return initTitle(null);
    }

    public boolean initTitle(View customView) {
        mActionBar = getSupportActionBar();
        if (mActionBar == null) {
            return false;
        }
        ActionBar.LayoutParams params = new ActionBar.LayoutParams(ActionBar.LayoutParams.MATCH_PARENT,
                ActionBar.LayoutParams.MATCH_PARENT);
        if (customView == null) {
            customView = onCustomTitleView(mInflater);
        }
        if (customView != null) {
            mActionBar.setCustomView(customView, params);
        } else {
            mActionBar.setCustomView(R.layout.toolbar);
        }
        initTitleView(mActionBar.getCustomView());
        mActionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        mActionBar.setDisplayShowCustomEnabled(true);
        if (Build.VERSION.SDK_INT >= 21) {
            mActionBar.setElevation(0);
        }
        return true;
    }

    /**
     * 初始化title的控件
     */
    private void initTitleView(View view) {
        this.mActionBarView = view;
        mTitleLayout = view.findViewById(R.id.title_layout);
        mBackName = view.findViewById(R.id.title_back_name);
        mTitleName =view.findViewById(R.id.title_title_name);
        mBackView = view.findViewById(R.id.title_back);
        if (mBackView != null) {
            if(mBackView instanceof MaterialMenuView){
                mBackView.setTag(((MaterialMenuView)mBackView).getColor());
                ((MaterialMenuView)mBackView).setState(MaterialMenuDrawable.IconState.ARROW);
                mBackView.setOnTouchListener(new View.OnTouchListener() {
                    @Override
                    public boolean onTouch(View view, MotionEvent motionEvent) {
                        switch (motionEvent.getAction()) {
                            case MotionEvent.ACTION_DOWN:
                                ((MaterialMenuView)mBackView).setColor(ContextCompat.getColor(ToolBarActivity.this,R.color.color_b2b2b2));
                                break;
                            case MotionEvent.ACTION_UP:
                                ((MaterialMenuView)mBackView).setColor((Integer) mBackView.getTag());
                                break;
                        }
                        return false;
                    }
                });
            }
            mBackView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onBackPressed();
                }
            });
        }
        TextView mRightText = new TextView(this);
        mRightText.setTextColor(ContextCompat.getColor(this,R.color.colorTitle));
        View mRightView = onCustomTitleRight(mRightText);
        if(mRightView!=null){
            RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) mRightView.getLayoutParams();
            if (params == null) {
                params = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                params.setMargins(0,0,getResources().getDimensionPixelSize(R.dimen.title_right_view_mr),0);
            }
            params.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
            params.addRule(RelativeLayout.CENTER_VERTICAL);
            mRightView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onRightClickListener(v);
                }
            });
            mTitleLayout.addView(mRightView, params);
        }
    }

    /**
     * 隐藏title
     */
    public void hideTitleBar() {
        if (mActionBar != null) {
            mActionBar.hide();
        }
        if (mToolBar != null) {
            mToolBar.setVisibility(View.GONE);
        }
    }

    /**
     * 显示title
     */
    public void showTitleBar() {
        if (mActionBar != null) {
            mActionBar.show();
        }
        if (mToolBar != null) {
            mToolBar.setVisibility(View.VISIBLE);
        }
    }

    /**
     * 设置返回按钮的标题
     *
     * @param name
     */
    public void setBackName(String name) {
        if (mBackName != null) {
            mBackName.setText(name);
        }
    }

    public void setBackName(int resName) {
        if (mBackName != null) {
            mBackName.setText(resName);
        }
    }

    public void hideBackName() {
        if (mBackName != null) {
            mBackName.setVisibility(View.GONE);
        }
        if (mBackView != null) {
            mBackView.setVisibility(View.GONE);
        }
    }

    public void showBackName() {
        if (mBackName != null) {
            mBackName.setVisibility(View.VISIBLE);
        }
        if (mBackView != null) {
            mBackView.setVisibility(View.VISIBLE);
        }
    }

    /**
     * 设置主题
     *
     * @param name
     */
    public void setTitleName(String name) {
        if (mTitleName != null) {
            mTitleName.setText(name);
        }
    }

    public void setTitleName(@StringRes int resName) {
        if (mTitleName != null) {
            mTitleName.setText(resName);
        }
    }

    public void hideTitleName() {
        if (mTitleName != null) {
            mTitleName.setVisibility(View.GONE);
        }
    }

    /**
     * 右边自定义控件
     */
    @Override
    public View onCustomTitleRight(TextView view) {
        return null;
    }

    private void initContentView() {
        mContentView = new LinearLayout(mContext);
        mContentView.setOrientation(LinearLayout.VERTICAL);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT);
        mContentView.setLayoutParams(params);
        //必须设置此属性否则toolbar会覆盖status
//        mContentView.setFitsSystemWindows(true);
    }

    private void initToolBar() {
        View toolbar = mInflater.inflate(R.layout.toolbar, mContentView);
        mToolBar = toolbar.findViewById(R.id.toolbar);
        View convertView = onCustomTitleView(mInflater);
        if (convertView != null) {
            mToolBar.removeAllViews();
            ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
            mToolBar.addView(convertView, params);
        }
        initTitleView(mToolBar);
    }

    private void initUserView(View view) {
        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        mContentView.addView(view, params);
    }

    public View getActionView() {
        return mActionBarView;
    }

    public void setActionBarView(View mActionBarView) {
        this.mActionBarView = mActionBarView;
    }

    public boolean isShowTitleBar() {
        return true;
    }

    @Override
    public View onCustomTitleView(LayoutInflater inflater) {
        return null;
    }

    @Override
    public void onRightClickListener(View view) {

    }

    /**
     * 设置背景
     * @param colorResId
     */
    public void setBackgroundColorResId(@ColorRes int colorResId){
        if(mContentView != null){
            mContentView.setBackgroundColor(ContextCompat.getColor(this,colorResId));
        }
    }

    public void setBackgroundColor(int color){
        if(mContentView!=null){
            mContentView.setBackgroundColor(color);
        }
    }

    public void setBackgroundResource(int resource){
        if(mContentView!=null){
            mContentView.setBackgroundResource(resource);
        }
    }

    public TextView getTitleName() {
        return mTitleName;
    }
}
