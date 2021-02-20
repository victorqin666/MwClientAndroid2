package com.mwim.qcloud.tim.uikit.business.fragment;

import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.chad.library.adapter.base.divider.HorizontalDividerItemDecoration;
import com.chad.library.adapter.base.divider.VerticalDividerItemDecoration;
import com.http.network.listener.OnResultDataListener;
import com.http.network.model.RequestWork;
import com.http.network.model.ResponseWork;
import com.mwim.qcloud.tim.uikit.R;
import com.mwim.qcloud.tim.uikit.base.BaseActivity;
import com.mwim.qcloud.tim.uikit.base.BaseFragment;
import com.mwim.qcloud.tim.uikit.business.adapter.WorkAdapter;
import com.work.api.open.Yz;
import com.work.api.open.model.GetToolListByUserIdResp;
import com.work.util.SizeUtils;
import com.work.util.ToastUtil;

/**
 * Created by tangyx
 * Date 2020/9/21
 * email tangyx@live.com
 */

public class WorkFragment extends BaseFragment implements OnResultDataListener {

    private WorkAdapter mAdapter;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View baseView = inflater.inflate(R.layout.fragment_work, container, false);
        RecyclerView mRecyclerView = baseView.findViewById(R.id.recycler_view);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mAdapter = new WorkAdapter(null);
        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.addItemDecoration(new HorizontalDividerItemDecoration.Builder(getActivity()).colorResId(R.color.transparent).sizeResId(R.dimen.dp_10).build());
        View view = new View(getContext());
        view.setLayoutParams(new LinearLayout.LayoutParams(1, SizeUtils.dp2px(getContext(),30)));
        mAdapter.addFooterView(view);

        view = new View(getContext());
        view.setLayoutParams(new LinearLayout.LayoutParams(1, SizeUtils.dp2px(getContext(),35)));
        mAdapter.addHeaderView(view);
        return baseView;
    }

    @Override
    public void onResume() {
        super.onResume();
        if(mAdapter!=null){
            refreshTool();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if(getActivity()!=null && getActivity() instanceof BaseActivity){
            ((BaseActivity) getActivity()).dismissProgress();
        }
    }

    public void refreshTool(){
        Yz.getSession().getToolListByUserId(this);
    }

    @Override
    public void onResult(RequestWork req, ResponseWork resp) {
        if(resp.isSuccess()){
            if(resp instanceof GetToolListByUserIdResp){
                mAdapter.setNewData(((GetToolListByUserIdResp) resp).getData());
            }
        }else{
            ToastUtil.warning(getActivity(),resp.getMessage());
        }
    }
}
