package com.mwim.qcloud.tim.uikit.business.dialog;

import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.divider.HorizontalDividerItemDecoration;
import com.mwim.qcloud.tim.uikit.R;
import com.mwim.qcloud.tim.uikit.base.BaseActivity;
import com.mwim.qcloud.tim.uikit.business.adapter.CityAreaAdapter;
import com.mwim.qcloud.tim.uikit.component.SelectionActivity;
import com.mwim.qcloud.tim.uikit.modules.contact.ContactItemBean;
import com.mwim.qcloud.tim.uikit.utils.ScreenUtil;
import com.workstation.view.MaterialMenuDrawable;
import com.workstation.view.MaterialMenuView;

/**
 * Created by tangyx
 * Date 2020/12/28
 * email tangyx@live.com
 */

public class CityAreaDialog extends BaseDialog implements BaseQuickAdapter.OnItemClickListener {

    private ContactItemBean mData;
    private CityAreaAdapter mAdapter;
    private SelectionActivity.OnResultReturnListener sOnResultReturnListener;
    private BaseActivity mActivity;

    @Override
    public void onInitView() {
        super.onInitView();
        RecyclerView mAreaView = findViewById(R.id.area_view);
        ViewGroup.LayoutParams params = mAreaView.getLayoutParams();
        int h = (int) (ScreenUtil.getScreenHeight(getDialogContext()) * 0.6);
        if(params==null){
            mAreaView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,h));
        }else{
            mAreaView.getLayoutParams().height = h;
        }
        mAreaView.setLayoutManager(new LinearLayoutManager(getDialogContext()));
        mAreaView.addItemDecoration(new HorizontalDividerItemDecoration.Builder(getDialogContext()).colorResId(R.color.line).showLastDivider().build());
        if(mAdapter==null){
            mAdapter = new CityAreaAdapter(mData.getArea());
            mAdapter.setOnItemClickListener(this);
        }else{
            mAdapter.setNewData(mData.getArea());
        }
        mAreaView.setAdapter(mAdapter);
        TextView mCity = findViewById(R.id.city);
        mCity.setText(mData.getNickname());
        MaterialMenuView mClose = findViewById(R.id.close);
        mClose.setState(MaterialMenuDrawable.IconState.X);
        mClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
            }
        });
    }

    @Override
    public void onInitValue() {
        super.onInitValue();
        setBackgroundTransparent();
    }

    public void setData(ContactItemBean mData) {
        this.mData = mData;
    }

    @Override
    public int onGravity() {
        return Gravity.BOTTOM;
    }

    @Override
    public float widthRatio() {
        return 1;
    }

    public void setOnResultReturnListener(SelectionActivity.OnResultReturnListener sOnResultReturnListener) {
        this.sOnResultReturnListener = sOnResultReturnListener;
    }

    public void setActivity(BaseActivity mActivity) {
        this.mActivity = mActivity;
    }

    @Override
    public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
        String area = mAdapter.getItem(position);
        if(!TextUtils.isEmpty(area)){
            sOnResultReturnListener.onReturn(mData.getNickname()+" "+area);
            mActivity.finish();
        }
    }
}
