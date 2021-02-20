package com.mwim.qcloud.tim.uikit.business.dialog;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.Window;
import android.view.WindowManager;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentManager;

import com.mwim.qcloud.tim.uikit.R;
import com.work.util.BarUtils;
import com.work.util.ScreenUtils;

import java.util.Locale;

/**
 * Created by Administrator on 2019/4/8
 * Description
 */

public abstract class BaseDialog extends DialogFragment {

    private Context mContext;
    private View mCovertView;
    private OnDismissListener mOnDismissListener;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.mContext = context;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = this.getContext();
        setStyle(DialogFragment.STYLE_NORMAL, R.style.AppDialogTheme);
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        Dialog dialog = super.onCreateDialog(savedInstanceState);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCanceledOnTouchOutside(isCanceledOnTouchOutside());
        Window window = dialog.getWindow();
        if(window!=null){
            if(getBackgroundColor()!=-1){
                window.setBackgroundDrawable(new ColorDrawable(getBackgroundColor()));
            }
            if(onDimAmount()!=-1){
                window.setDimAmount(onDimAmount());
            }
            WindowManager.LayoutParams params = window.getAttributes();
            params.gravity = onGravity();
            window.setAttributes(params);
        }
        return dialog;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mCovertView = super.onCreateView(inflater,container,savedInstanceState);
        if(mCovertView==null){
            mCovertView = onCustomContentView();
            if(mCovertView==null){
                int layoutId = onCustomContentId();
                if(layoutId<=0){
                    layoutId = onInitContentView();
                }
                if(layoutId>0){
                    mCovertView = inflater.inflate(layoutId,container,false);
                }
            }
            if(mCovertView==null){
                mCovertView = super.onCreateView(inflater,container,savedInstanceState);
            }
            mCovertView.getViewTreeObserver().addOnGlobalLayoutListener(mOnGlobalLayoutListener);
            onInitView();
        }
        return mCovertView;
    }

    private ViewTreeObserver.OnGlobalLayoutListener mOnGlobalLayoutListener = new ViewTreeObserver.OnGlobalLayoutListener() {
        @Override
        public void onGlobalLayout() {
            int[] location = new int[2];
            mCovertView.getLocationOnScreen(location);
            int y = location[1];
            if (y == 0) {
                //此处的topMarginView是被状态栏覆盖的View
                ViewGroup.MarginLayoutParams params
                        = (ViewGroup.MarginLayoutParams)mCovertView.getLayoutParams();
                params.topMargin += BarUtils.getStatusBarHeight(getDialogContext());
                mCovertView.setLayoutParams(params);
            }
        }
    };

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        onInitValue();
    }

    @Override
    public void onStart() {
        super.onStart();
        if(isFull()){
            Window window = getDialog().getWindow();
            if(window!=null){
                WindowManager.LayoutParams attributes = window.getAttributes();
                attributes.width = ViewGroup.LayoutParams.MATCH_PARENT;
                attributes.height = ScreenUtils.getScreenHeight(getDialogContext());
                if(attributes.height==0){
                    attributes.height = ViewGroup.LayoutParams.MATCH_PARENT;
                }
                setFlagNotFocusable();
                setBackgroundTransparent();
                window.setAttributes(attributes);

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                    window.getDecorView().setSystemUiVisibility(
                            View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                                    | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                                    | View.SYSTEM_UI_FLAG_FULLSCREEN);
                }
            }
        }else{
            Window window = getDialog().getWindow();
            if(window!=null){
                window.setLayout((int) (ScreenUtils.getScreenWidth(mContext) * widthRatio()),ViewGroup.LayoutParams.WRAP_CONTENT);
            }
        }
    }

    /**
     * 这种方式无法拉起键盘？
     */
    protected void setFlagNotFocusable(){
        Window window = getDialog().getWindow();
        if(window!=null){
            window.addFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE);//不占用主容器焦点
        }
    }

    /**
     * 设置背景阴影为透明
     * 如果是全屏模式必须要调用此方法
     */
    protected void setBackgroundTransparent(){
        Window window = getDialog().getWindow();
        if(window!=null){
            window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));//注意此处
        }
    }

    @Override
    public void show(@NonNull FragmentManager manager, String tag) {
        if(!isAdded() && manager.findFragmentByTag(tag)==null){//判断没有添加过
            super.show(manager, tag);
        }
    }

    @Override
    public void onDismiss(@NonNull DialogInterface dialog) {
        super.onDismiss(dialog);
        if(mOnDismissListener!=null){
            mOnDismissListener.onDismiss(dialog);
        }
    }

    public BaseDialog setOnDismissListener(OnDismissListener mOnDismissListener) {
        this.mOnDismissListener = mOnDismissListener;
        return this;
    }

    public void onInitView(){

    }

    public void onInitValue(){

    }

    public int getBackgroundColor(){
        return -1;
    }

    public float onDimAmount(){
        return -1;
    }

    public int onGravity(){
        return Gravity.CENTER;
    }
    /**
     * 初始化view
     */
    protected int onInitContentView(){
        String mThisClassName = this.getClass().getSimpleName();
        String resLayoutName = "dialog_"+ mThisClassName.replaceFirst("Dialog","");
        resLayoutName = resLayoutName.toLowerCase(Locale.getDefault());
        return getResources().getIdentifier(resLayoutName, "layout", mContext.getPackageName());
    }

    public boolean isShow(){
        Dialog dialog = getDialog();
        if(dialog!=null){
            return dialog.isShowing();
        }
        return false;
    }

    protected<T extends View> T findViewById(int id){
        return mCovertView.findViewById(id);
    }

    public Context getDialogContext() {
        return this.mContext;
    }

    public int onCustomContentId(){
        return -1;
    }
    public View onCustomContentView(){
        return null;
    }
    public boolean isFull(){
        return false;
    }
    public float widthRatio(){
        return 0.9f;
    }
    public boolean isCanceledOnTouchOutside(){
        return true;
    }

    public interface OnDismissListener{
        void onDismiss(DialogInterface dialog);
    }

}
