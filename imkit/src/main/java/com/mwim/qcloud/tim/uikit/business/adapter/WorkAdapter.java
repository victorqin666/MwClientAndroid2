package com.mwim.qcloud.tim.uikit.business.adapter;

import android.graphics.Color;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.chad.library.adapter.base.divider.HorizontalDividerItemDecoration;
import com.http.network.listener.OnResultDataListener;
import com.http.network.model.RequestWork;
import com.http.network.model.ResponseWork;
import com.mwim.qcloud.tim.uikit.YzIMKitAgent;
import com.mwim.qcloud.tim.uikit.R;
import com.mwim.qcloud.tim.uikit.base.BaseActivity;
import com.mwim.qcloud.tim.uikit.business.active.WebActivity;
import com.mwim.qcloud.tim.uikit.business.inter.YzWorkAppItemClickListener;
import com.mwim.qcloud.tim.uikit.business.modal.UserApi;
import com.mwim.qcloud.tim.uikit.business.modal.WorkApp;
import com.work.api.open.Yz;
import com.work.api.open.model.GetCarWebViewUrlResp;
import com.work.api.open.model.GetToolTokenReq;
import com.work.api.open.model.GetToolTokenResp;
import com.work.api.open.model.client.OpenData;
import com.work.api.open.model.client.OpenWork;
import com.work.util.ToastUtil;

import java.util.HashMap;
import java.util.List;

/**
 * Created by tangyx
 * Date 2020/9/27
 * email tangyx@live.com
 */

public class WorkAdapter extends BaseQuickAdapter<OpenWork, BaseViewHolder> implements OnResultDataListener{

    public final static HashMap<String,String> SdkTokenMaps= new HashMap<>();
    public final static HashMap<String,String> IdTokenMaps= new HashMap<>();

    public WorkAdapter(@Nullable List<OpenWork> data) {
        super(R.layout.adapter_work_item,data);
        SdkTokenMaps.clear();
        IdTokenMaps.clear();
        SdkTokenMaps.put("17774942284","eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJpc3MiOiIyMDEyMjI1NzMxIiwiaWF0IjoxNjA4NjA3NDQxLCJleHAiOjE2MTM5NjQyNDEsImF1ZCI6IlRlbmNlbnQgTWVldGluZyIsInN1YiI6Inl1YW56aGlfdGVzdDA0In0.OcCqySIBdu7ON-e-wdog7Qd5vmgeGiCvvn_IUkB91ek");
        SdkTokenMaps.put("15010027322","eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJpc3MiOiIyMDA5MjMzMzcxIiwiaWF0IjoxNjAxMjgyNTY2LCJleHAiOjE2MDY1NTI5NjYsImF1ZCI6IlRlbmNlbnQgTWVldGluZyIsInN1YiI6Inl1YW56aGlfdGVzdDAyIn0.--2Qu9L0snC6MRjGyS8_NxxGAM2ZekRa4aZMovrdqhg");
        SdkTokenMaps.put("13910660633","eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJpc3MiOiIyMDA5MjMzMzcxIiwiaWF0IjoxNjAxMjgyNTY2LCJleHAiOjE2MDY1NTI5NjYsImF1ZCI6IlRlbmNlbnQgTWVldGluZyIsInN1YiI6Inl1YW56aGlfdGVzdDAzIn0.bQ1u8oO219nAjU5_v7Xx1mQpzn2pOJkv5jlI28Dqa7Q");

        IdTokenMaps.put("17774942284","eyJraWQiOiI3IiwidHlwIjoiSldUIiwiYWxnIjoiUlMyNTYifQ.eyJzdWIiOiJ5dWFuemhpX3Rlc3QwNCIsImlzcyI6InRlbmNlbnQgbWVldGluZyIsIm5hbWUiOiJ5dWFuemhpX3Rlc3QwNCIsImV4cCI6MTYxMzk2NDI0MSwiaWF0IjoxNjA4NjA3NDQxfQ.A1GYXU_tzKKlyO5pfBGb_otyePcKLB3798hGYJHGQHa6VjFcOOCEc-md2Q_F50bmLxF_QBzkwA0BLu9FiJTdi2GozX-W0De_EZBlr7F8ctXXOqUMIdDPz4PMl0Nu5hl81sD0DN7NrTM-7PU6pW-UECEP-cNiRYxnOo1c-9-HvjYIh_FLx4ycersneZjEfr9hDUPESM7MzbT3krB_UO2d2FY9tKvup4Q3uFTzAtmPx19yMJJlBQM88TMvEn4EIlfYq3oSdaDjxTgJSuldwiq2y-G4u7qVKYbWxHarKBwz_c6f9g3n6tGAOKdyJCPC5dmz6HRigJkeIHQ9uD-C4Kr90A");
        IdTokenMaps.put("15010027322","eyJraWQiOiI3IiwidHlwIjoiSldUIiwiYWxnIjoiUlMyNTYifQ.eyJzdWIiOiJ5dWFuemhpX3Rlc3QwMiIsImlzcyI6InRlbmNlbnQgbWVldGluZyIsIm5hbWUiOiJ5dWFuemhpX3Rlc3QwMiIsImV4cCI6MTYwNjU1Mjk2NiwiaWF0IjoxNjAxMzg3MTY2fQ.oWfpQAWYoE_sctY4YhHgzk5dkxCqNjQlrM1FGKSdhIqYWQ-mlHNodkTepjiQOY16bPU5YoNIEDoGukK6xXM470sEoo4R9t-h5F6yDDIcYZZHRi6pAuOjg-qQ9-1-lkwpmnWz7zheezqNnpuH1ueFBrMQhxBICUunMU1TwjJIuXniyRaJYGryT6EKoklm4eua-OYNBnTdmEAIf8_Ua_ZXLpmviAgDLLjEmC3ureRf0pgnueAD5QsDgKnNDNJ3ge5rOYl_qQau8jvl4gZs7ITs2h-Z9pQ5mIdf_W-Y1S0h-MJXlVvgnVAgiwCtX6IXFYbsX6itk7T5FxBaZkNYcwOaCw");
        IdTokenMaps.put("13910660633","eyJraWQiOiI3IiwidHlwIjoiSldUIiwiYWxnIjoiUlMyNTYifQ.eyJzdWIiOiJ5dWFuemhpX3Rlc3QwMyIsImlzcyI6InRlbmNlbnQgbWVldGluZyIsIm5hbWUiOiJ5dWFuemhpX3Rlc3QwMyIsImV4cCI6MTYwNjU1Mjk2NiwiaWF0IjoxNjAxMzg3MTY2fQ.NmklV5-YdyZ8aoZn-pTcozMWl8Q1ZpedGb4LnC6NsqZaNw3JJXRdckB6YPocy3EYOtNSxh8h_iLLAikk_JhqaXj3hKBzZ7hlhQHBot-zxIp79zYARz5vdetmoYdkCjnuiqhKPeUtW1Ac9pkZGtNSnZHOfRAcP1GAiklco3Zhve6DYiWYuYGzUWKU9-BQ3gSp57dP13-a-Ttdt6V7liMsRuF_SKlUBwkBV82HH2v1r2Wofm7KXmHcDkPvYQQSZEAsuvnZPdtEUj8-AbF1fHXOdKCJNmyrwHQHoW6DxElEiIg1KjfP6zNKSlEwEZERpcdsIapQQh_4qBAQaBImfNNGNw");
    }

    @Override
    protected void convert(BaseViewHolder helper, OpenWork item) {
        helper.setText(R.id.title,item.getToolCategory());
        RecyclerView recyclerView = helper.getView(R.id.recycler_view);
        WorkAppAdapter mAdapter = (WorkAppAdapter) recyclerView.getAdapter();
        if(mAdapter==null){
            recyclerView.setLayoutManager(new GridLayoutManager(getContext(),4));
            recyclerView.addItemDecoration(new HorizontalDividerItemDecoration.Builder(getContext()).color(Color.TRANSPARENT).sizeResId(R.dimen.dp_30).build());
            mAdapter = new WorkAppAdapter(item.getToolDataList());
            recyclerView.setAdapter(mAdapter);
            mAdapter.setOnItemClickListener(new OnItemClickListener() {
                @Override
                public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                    OpenData data = (OpenData) adapter.getItem(position);
                    if(data!=null){
                        if("code001".equals(data.getToolCode()) //腾讯会议
                                || "code002".equals(data.getToolCode())//网盘
                                || "code003".equals(data.getToolCode())){//打车
                            String sdkToken = "";
                            if("code001".equals(data.getToolCode())){
                                sdkToken = SdkTokenMaps.get(UserApi.instance().getMobile());
                                if(sdkToken==null){
                                    ToastUtil.error(getContext(),"该账号未开通会议。");
                                    return;
                                }
                            }
                            if(getContext() instanceof BaseActivity){
                                ((BaseActivity) getContext()).showProgressLoading(false,false);
                            }
                            GetToolTokenReq getToolTokenReq = new GetToolTokenReq();
                            getToolTokenReq.setToolCode(data.getToolCode());
                            getToolTokenReq.setUserName(UserApi.instance().getNickName());
                            Yz.getSession().getToolToken(getToolTokenReq, WorkAdapter.this,data.getToolUrl(),sdkToken);
                        }else{
                            WebActivity.startWebView(data.getToolUrl());
                        }
                    }
                }
            });
        }else{
            mAdapter.setNewData(item.getToolDataList());
        }
    }

    @Override
    public void onResult(RequestWork req, ResponseWork resp) throws Exception{
        if(getContext() instanceof BaseActivity){
            ((BaseActivity) getContext()).onResult(req,resp);
        }
        if(resp.isSuccess()){
            if(req instanceof GetToolTokenReq && resp instanceof GetToolTokenResp){
                String token = ((GetToolTokenResp) resp).getData();
                String url = resp.getPositionParams(0);
                if("code001".equals(((GetToolTokenReq) req).getToolCode())){//腾讯会议
                    YzWorkAppItemClickListener listener = YzIMKitAgent.instance().getWorkAppItemClickListener();
                    if(listener!=null){
                        String sdkToken = resp.getPositionParams(1);
                        WorkApp workApp = new WorkApp(sdkToken);
                        workApp.setUrl(url+IdTokenMaps.get(UserApi.instance().getMobile()));
                        listener.onWorkAppClick(workApp);
                    }
                }else if("code002".equals(((GetToolTokenReq) req).getToolCode())){//网盘
                    WebActivity.startWebView(url+"?token="+token);
                }else if("code003".equals(((GetToolTokenReq) req).getToolCode())){//打车
                    Yz.getSession().getCarWebViewUrl(url+token,WorkAdapter.this);
                }
            }else if(resp instanceof GetCarWebViewUrlResp){
                OpenData result = ((GetCarWebViewUrlResp) resp).Result;
                if(result!=null){
                    WebActivity.startWebView(result.getUrl(),"hsh_android",true);
                }
            }
        }else{
            ToastUtil.warning(getContext(),resp.getMessage());
        }
    }
}
