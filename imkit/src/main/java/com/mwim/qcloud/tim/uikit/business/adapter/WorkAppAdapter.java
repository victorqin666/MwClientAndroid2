package com.mwim.qcloud.tim.uikit.business.adapter;

import android.widget.ImageView;

import androidx.annotation.Nullable;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.mwim.qcloud.tim.uikit.R;
import com.mwim.qcloud.tim.uikit.component.picture.imageEngine.impl.GlideEngine;
import com.work.api.open.model.client.OpenData;

import java.util.List;

/**
 * Created by tangyx
 * Date 2020/9/27
 * email tangyx@live.com
 */

class WorkAppAdapter extends BaseQuickAdapter<OpenData, BaseViewHolder> {

    public WorkAppAdapter(@Nullable List<OpenData> data) {
        super(R.layout.adapter_work_app_item,data);
    }

    @Override
    protected void convert(BaseViewHolder helper, OpenData item) {
        ImageView mIcon = helper.getView(R.id.icon);
        helper.setText(R.id.name,item.getToolName());
        GlideEngine.loadImage(mIcon,item.getIconUrl());
    }
}
