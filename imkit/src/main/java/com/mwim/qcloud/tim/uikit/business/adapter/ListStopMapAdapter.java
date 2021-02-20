package com.mwim.qcloud.tim.uikit.business.adapter;

import androidx.annotation.Nullable;

import com.amap.api.services.core.PoiItem;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.mwim.qcloud.tim.uikit.R;
import com.workstation.view.MaterialMenuDrawable;
import com.workstation.view.MaterialMenuView;

import java.util.List;

/**
 * Created by tangyx
 * Date 2020/9/19
 * email tangyx@live.com
 */

public class ListStopMapAdapter extends BaseQuickAdapter<PoiItem, BaseViewHolder> {

    private PoiItem mSelectData;

    public ListStopMapAdapter(@Nullable List<PoiItem> data) {
        super(R.layout.adapter_list_stop_map_item,data);
    }

    @Override
    protected void convert(BaseViewHolder helper, PoiItem item) {
        helper.setText(R.id.name,item.getTitle());
        String address = item.getProvinceName()+item.getCityName()+item.getAdName()+item.getSnippet();
        helper.setText(R.id.address,address);
        MaterialMenuView mChecked = helper.getView(R.id.checked);
        if(item==mSelectData){
            mChecked.setVisible(true);
            mChecked.setState(MaterialMenuDrawable.IconState.CHECK);
        }else{
            mChecked.setVisible(false);
        }
    }

    @Override
    public void setNewData(@Nullable List<PoiItem> data) {
        super.setNewData(data);
        if(data!=null && data.size()>0){
            mSelectData = data.get(0);
        }
    }

    public PoiItem getSelectData() {
        return mSelectData;
    }

    public void setSelectData(PoiItem mSelectData) {
        this.mSelectData = mSelectData;
        notifyDataSetChanged();
    }
}
