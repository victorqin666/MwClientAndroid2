package com.mwim.qcloud.tim.uikit.component;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputFilter;
import android.text.InputType;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.mwim.qcloud.tim.uikit.base.BaseActivity;
import com.mwim.qcloud.tim.uikit.utils.IMKitConstants;
import com.mwim.qcloud.tim.uikit.R;
import com.work.util.KeyboardUtils;
import com.work.util.RegularUtils;
import com.work.util.ToastUtil;

public class SelectionActivity extends BaseActivity {

    private static OnResultReturnListener sOnResultReturnListener;
    private EditText input;
    private String type;
    private int mSelectionType;

    public static void startTextSelection(Context context, Bundle bundle, OnResultReturnListener listener) {
        bundle.putInt(IMKitConstants.Selection.TYPE, IMKitConstants.Selection.TYPE_TEXT);
        startSelection(context, bundle, listener);
    }

    private static void startSelection(Context context, Bundle bundle, OnResultReturnListener listener) {
        Intent intent = new Intent(context, SelectionActivity.class);
        intent.putExtra(IMKitConstants.Selection.CONTENT, bundle);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
        sOnResultReturnListener = listener;
    }

    @Override
    public void onInitValue() throws Exception {
        super.onInitValue();
        input = findViewById(R.id.edit_content_et);
        Bundle bundle = getIntent().getBundleExtra(IMKitConstants.Selection.CONTENT);
        if (bundle.getInt(IMKitConstants.Selection.TYPE) == IMKitConstants.Selection.TYPE_TEXT) {
            String defaultString = bundle.getString(IMKitConstants.Selection.INIT_CONTENT);
            int limit = bundle.getInt(IMKitConstants.Selection.LIMIT);
            if (!TextUtils.isEmpty(defaultString)) {
                input.setText(defaultString);
                input.setSelection(defaultString.length());
            }
            if (limit > 0) {
                input.setFilters(new InputFilter[]{new InputFilter.LengthFilter(limit)});
            }
        } else {
            finish();
            return;
        }
        type = bundle.getString(IMKitConstants.Selection.TYPE_INPUT,"");
        if("email".equals(type)){
            input.setInputType(InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);
        }
        mSelectionType = bundle.getInt(IMKitConstants.Selection.TYPE, IMKitConstants.Selection.TYPE_TEXT);
        final String title = bundle.getString(IMKitConstants.Selection.TITLE);
        setTitleName(title);
        input.post(new Runnable() {
            @Override
            public void run() {
                KeyboardUtils.showSoftInput(input);
            }
        });
    }

    @Override
    public View onCustomTitleRight(TextView view) {
        view.setText(R.string.user_save);
        return view;
    }

    @Override
    public void onRightClickListener(View view) {
        super.onRightClickListener(view);
        if (mSelectionType == IMKitConstants.Selection.TYPE_TEXT) {
//            if (TextUtils.isEmpty(input.getText().toString())) {
//                ToastUtil.error(this,R.string.toast_user_message_empty);
//                return;
//            }
            String text = input.getText().toString().trim();
            if("email".equals(type) && !RegularUtils.isEmail(text)){
                ToastUtil.error(this,"请输入正确的邮箱地址");
                return;
            }
            if (sOnResultReturnListener != null) {
                sOnResultReturnListener.onReturn(text);
            }
        }
        finish();
    }

    @Override
    protected void onStop() {
        super.onStop();
        sOnResultReturnListener = null;
    }

    public interface OnResultReturnListener {
        void onReturn(Object res);
    }
}
