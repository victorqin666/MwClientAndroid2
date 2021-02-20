package com.mwim.qcloud.tim.uikit.business.dialog;

import android.view.View;
import android.widget.RadioButton;
import android.widget.TextView;

import com.mwim.qcloud.tim.uikit.R;
import com.mwim.qcloud.tim.uikit.component.SelectionActivity;

import java.util.List;

/**
 * Created by tangyx
 * Date 2020/9/16
 * email tangyx@live.com
 */

public class GroupJoinTypeDialog extends BaseDialog implements View.OnClickListener {

    private List<String> mJoinTypes;
    private int mJoinTypeIndex;
    private SelectionActivity.OnResultReturnListener onResultReturnListener;
    private TextView mJoinType1;
    private TextView mJoinType2;
    private TextView mJoinType3;
    private RadioButton mJoinTypeR1;
    private RadioButton mJoinTypeR2;
    private RadioButton mJoinTypeR3;

    @Override
    public void onInitView() {
        super.onInitView();
        mJoinType1 = findViewById(R.id.join_type_1);
        mJoinType2 = findViewById(R.id.join_type_2);
        mJoinType3 = findViewById(R.id.join_type_3);
        mJoinTypeR1 = findViewById(R.id.join_type_r_1);
        mJoinTypeR2 = findViewById(R.id.join_type_r_2);
        mJoinTypeR3 = findViewById(R.id.join_type_r_3);
    }

    @Override
    public void onInitValue() {
        super.onInitValue();
        mJoinType1.setOnClickListener(this);
        mJoinType2.setOnClickListener(this);
        mJoinType3.setOnClickListener(this);
        mJoinType1.setText(mJoinTypes.get(0));
        mJoinType2.setText(mJoinTypes.get(1));
        mJoinType3.setText(mJoinTypes.get(2));
        mJoinTypeR1.setChecked(mJoinTypeIndex==0);
        mJoinTypeR2.setChecked(mJoinTypeIndex==1);
        mJoinTypeR3.setChecked(mJoinTypeIndex==2);
    }

    public void setJoinTypeIndex(int mJoinTypeIndex) {
        this.mJoinTypeIndex = mJoinTypeIndex;
    }

    public void setJoinTypes(List<String> mJoinTypes) {
        this.mJoinTypes = mJoinTypes;
    }

    public void setOnResultReturnListener(SelectionActivity.OnResultReturnListener onResultReturnListener) {
        this.onResultReturnListener = onResultReturnListener;
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.join_type_1) {
            mJoinTypeIndex = 0;
            mJoinTypeR1.setChecked(true);
        }else if(view.getId() == R.id.join_type_2){
            mJoinTypeIndex = 1;
            mJoinTypeR2.setChecked(true);
        }else if(view.getId() == R.id.join_type_3){
            mJoinTypeIndex = 2;
            mJoinTypeR3.setChecked(true);
        }
        onResultReturnListener.onReturn(mJoinTypeIndex);
        dismiss();
    }
}
