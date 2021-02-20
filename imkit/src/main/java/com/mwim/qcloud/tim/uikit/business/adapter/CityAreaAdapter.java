package com.mwim.qcloud.tim.uikit.business.adapter;

import androidx.annotation.Nullable;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.mwim.qcloud.tim.uikit.R;

import java.util.List;

/**
 * Created by tangyx
 * Date 2020/12/28
 * email tangyx@live.com
 */

public class CityAreaAdapter extends BaseQuickAdapter<String, BaseViewHolder> {

    public CityAreaAdapter(@Nullable List<String> data) {
        super(R.layout.adapter_city_area,data);
    }

    @Override
    protected void convert(BaseViewHolder helper, String item) {
        helper.setText(R.id.name,item);
    }
}
