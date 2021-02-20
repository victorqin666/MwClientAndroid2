package com.workstation.crop;

import androidx.annotation.Nullable;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.workstation.android.R;

import java.util.List;

/**
 * create by tangyx
 * 2018/7/6
 */
public class AspectRatioPreviewAdapter extends BaseQuickAdapter<AspectRatio,BaseViewHolder> {

    private AspectRatio mSelectAspectRation;

    public AspectRatioPreviewAdapter(@Nullable List<AspectRatio> data) {
        super(R.layout.adapter_aspect_ratio,data);
    }

    @Override
    protected void convert(BaseViewHolder helper, AspectRatio item) {
        AspectRatioPreviewView view = helper.getView(R.id.aspect_ratio_preview);
        view.setAspectRatio(item);
        view.setSelected(mSelectAspectRation!=null && item == mSelectAspectRation);
    }

    public void setSelectAspectRation(AspectRatio mSelectAspectRation) {
        this.mSelectAspectRation = mSelectAspectRation;
    }

    public AspectRatio getSelectAspectRation() {
        return mSelectAspectRation;
    }
}
