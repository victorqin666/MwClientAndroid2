package com.mwim.qcloud.tim.uikit.business.active;


import android.content.Context;
import android.content.Intent;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.divider.HorizontalDividerItemDecoration;
import com.http.network.model.RequestWork;
import com.http.network.model.ResponseWork;
import com.mwim.qcloud.tim.uikit.R;
import com.mwim.qcloud.tim.uikit.TUIKit;
import com.mwim.qcloud.tim.uikit.base.IUIKitCallBack;
import com.mwim.qcloud.tim.uikit.business.Constants;
import com.mwim.qcloud.tim.uikit.business.adapter.SearchAddMoreAdapter;
import com.mwim.qcloud.tim.uikit.modules.chat.base.ChatInfo;
import com.mwim.qcloud.tim.uikit.modules.contact.ContactItemBean;
import com.mwim.qcloud.tim.uikit.modules.conversation.ConversationListAdapter;
import com.mwim.qcloud.tim.uikit.modules.conversation.ConversationListLayout;
import com.mwim.qcloud.tim.uikit.modules.conversation.ConversationManagerKit;
import com.mwim.qcloud.tim.uikit.modules.conversation.ConversationProvider;
import com.mwim.qcloud.tim.uikit.modules.conversation.base.ConversationInfo;
import com.mwim.qcloud.tim.uikit.utils.IMKitConstants;
import com.mwim.qcloud.tim.uikit.utils.ThreadHelper;
import com.tencent.imsdk.v2.V2TIMConversation;
import com.tencent.imsdk.v2.V2TIMFriendInfo;
import com.tencent.imsdk.v2.V2TIMManager;
import com.tencent.imsdk.v2.V2TIMValueCallback;
import com.work.api.open.Yz;
import com.work.api.open.model.GetFriendByMobileResp;
import com.work.api.open.model.GetUserByParamReq;
import com.work.api.open.model.GetUserByParamResp;
import com.work.api.open.model.client.OpenByMobile;
import com.work.api.open.model.client.OpenData;
import com.work.util.SLog;
import com.work.util.ToastUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by tangyx
 * Date 2020/9/15
 * email tangyx@live.com
 */

public class SearchAddMoreActivity extends IMBaseActivity implements View.OnClickListener, TextWatcher, BaseQuickAdapter.OnItemClickListener {

    private EditText mSearch;
    private GetUserByParamReq mUserByParamReq;
    private SearchAddMoreAdapter mAdapter;
    private ConversationListAdapter mConversationAdapter;
    private int flag=-1;
    private List<V2TIMFriendInfo> mV2TIMFriendData;

    @Override
    public void onInitView() throws Exception {
        super.onInitView();
        flag = getIntent().getIntExtra(SearchAddMoreActivity.class.getSimpleName(),-1);
        mSearch = findViewById(R.id.search);
        RecyclerView mRecyclerView = findViewById(R.id.recycler_view);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        if(flag==1){
            mConversationAdapter = new ConversationListAdapter();
            mConversationAdapter.setOnItemClickListener(new ConversationListLayout.OnItemClickListener() {
                @Override
                public void onItemClick(View view, int position, ConversationInfo conversationInfo) {
                    ChatInfo chatInfo = new ChatInfo();
                    chatInfo.setType(conversationInfo.isGroup() ? V2TIMConversation.V2TIM_GROUP : V2TIMConversation.V2TIM_C2C);
                    chatInfo.setId(conversationInfo.getId());
                    chatInfo.setChatName(conversationInfo.getTitle());
                    Intent intent = new Intent(TUIKit.getAppContext(), ChatActivity.class);
                    intent.putExtra(Constants.CHAT_INFO, chatInfo);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    TUIKit.getAppContext().startActivity(intent);
                }
            });
            mRecyclerView.setAdapter(mConversationAdapter);
        }else{
            mRecyclerView.addItemDecoration(new HorizontalDividerItemDecoration.Builder(this).colorResId(R.color.background_color).build());
            mAdapter = new SearchAddMoreAdapter(null);
            mAdapter.setOnItemClickListener(this);
            mRecyclerView.setAdapter(mAdapter);
        }
    }

    @Override
    public void onInitValue() throws Exception {
        super.onInitValue();
        if(flag==0){
            mSearch.setHint(R.string.conversation_search_friends);
        }else if(flag == 1){
            mSearch.setHint(R.string.conversation_search_hf);
        }
        mSearch.requestFocus();
        mSearch.addTextChangedListener(this);
        findViewById(R.id.cancel).setOnClickListener(this);
    }

    @Override
    public boolean isShowTitleBar() {
        return false;
    }

    @Override
    public void onClick(View view) {
        onBackPressed();
    }

    @Override
    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

    }

    @Override
    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        if(TextUtils.isEmpty(charSequence)){
            if(mAdapter!=null){
                mAdapter.clear();
            }
            if(mConversationAdapter!=null){
                mConversationAdapter.clearData();
            }
        }else{
            if(flag==0){
                loadFriendListDataAsync();
            }else if(flag==1){
                loadConversationAsync();
            }else{
                if(mUserByParamReq == null){
                    mUserByParamReq = new GetUserByParamReq();
                }
                mUserByParamReq.setParam(charSequence.toString().trim());
                Yz.getSession().getUserByParam(mUserByParamReq,this);
            }
        }
    }

    private void loadConversationAsync(){
        ConversationManagerKit.getInstance().loadConversation(mSearch.getText().toString().trim(),new IUIKitCallBack() {
            @Override
            public void onSuccess(Object data) {
                mConversationAdapter.setDataProvider((ConversationProvider) data);
            }

            @Override
            public void onError(String module, int errCode, String errMsg) {
                ToastUtil.error(TUIKit.getAppContext(),"加载消息失败");
            }
        });
    }

    private void loadFriendListDataAsync() {
        SLog.i("loadFriendListDataAsync");
        ThreadHelper.INST.execute(new Runnable() {
            @Override
            public void run() {
                // 压测时数据量比较大，query耗时比较久，所以这里使用新线程来处理
                V2TIMManager.getFriendshipManager().getFriendList(new V2TIMValueCallback<List<V2TIMFriendInfo>>() {
                    @Override
                    public void onError(int code, String desc) {
                        SLog.e("loadFriendListDataAsync err code:" + code + ", desc:" + desc);
                    }

                    @Override
                    public void onSuccess(List<V2TIMFriendInfo> v2TIMFriendInfos) {
                        if (v2TIMFriendInfos == null) {
                            v2TIMFriendInfos = new ArrayList<>();
                        }
                        SLog.i("loadFriendListDataAsync->getFriendList:" + v2TIMFriendInfos.size());
                        mV2TIMFriendData = v2TIMFriendInfos;
                        assembleFriendListData();
                    }
                });
            }
        });
    }

    private void assembleFriendListData() {
        List<OpenData> data = new ArrayList<>();
        String keyword = mSearch.getText().toString().trim();
        for (V2TIMFriendInfo friendInfo : mV2TIMFriendData) {
            String remark = friendInfo.getFriendRemark();
            String name  = friendInfo.getUserProfile().getNickName();
            if(!TextUtils.isEmpty(keyword)
                    && (remark.contains(keyword) || name.contains(keyword))){
                OpenData openData = new OpenData();
                openData.setUserId(friendInfo.getUserID());
                openData.setRemark(remark);
                openData.setNickName(name);
                openData.setUserIcon(friendInfo.getUserProfile().getFaceUrl());
                data.add(openData);
            }
        }
        if(data.size()==0){
            if(mV2TIMFriendData!=null && mV2TIMFriendData.size()>0){
                GetUserByParamReq getUserByParamReq = new GetUserByParamReq();
                getUserByParamReq.setParamVal(keyword);
                Yz.getSession().getFriendByMobile(getUserByParamReq,this);
            }
        }else{
            mAdapter.setNewData(data);
        }
    }

    @Override
    public void afterTextChanged(Editable editable) {

    }

    @Override
    public void onResult(RequestWork req, ResponseWork resp) throws Exception {
        super.onResult(req, resp);
        if(resp.isSuccess()){
            if(resp instanceof GetUserByParamResp){
                if(TextUtils.isEmpty(mSearch.getText())){
                    mAdapter.clear();
                }else{
                    mAdapter.setNewData(((GetUserByParamResp) resp).getData());
                }
            }else if(resp instanceof GetFriendByMobileResp){
                List<OpenByMobile> openByMobiles = ((GetFriendByMobileResp) resp).getData();
                if(openByMobiles != null && openByMobiles.size()>0){
                    List<OpenData> data = new ArrayList<>();
                    for (V2TIMFriendInfo friendInfo : mV2TIMFriendData) {
                        String userId = friendInfo.getUserID();
                        for (OpenByMobile byMobile : openByMobiles){
                            if(byMobile.To_Account.equals(userId)){
                                OpenData openData = new OpenData();
                                openData.setUserId(friendInfo.getUserID());
                                String remark = friendInfo.getFriendRemark();
                                String name  = friendInfo.getUserProfile().getNickName();
                                openData.setRemark(remark);
                                openData.setNickName(name);
                                openData.setUserIcon(friendInfo.getUserProfile().getFaceUrl());
                                data.add(openData);
                            }
                        }
                    }
                    mAdapter.setNewData(data);
                }else{
                    mAdapter.clear();
                }
            }
        }
    }

    @Override
    public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
        OpenData item = mAdapter.getItem(position);
        if(item!=null){
            if(flag==0){
                ContactItemBean contact = new ContactItemBean();
                contact.setAvatarurl(item.getUserIcon());
                contact.setNickname(item.getNickName());
                contact.setRemark(item.getRemark());
                contact.setId(item.getUserId());
                Intent intent = new Intent(TUIKit.getAppContext(), FriendProfileActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.putExtra(IMKitConstants.ProfileType.CONTENT, contact);
                TUIKit.getAppContext().startActivity(intent);
            }else{
                Intent intent = new Intent(this, FriendProfileActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.putExtra(IMKitConstants.ProfileType.CONTENT, item);
                startActivity(intent);
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ConversationManagerKit.getInstance().destroyConversationSearch();
    }

    public static void startSearchMore(Context context, int flag){
        Intent intent = new Intent(context,SearchAddMoreActivity.class);
        intent.putExtra(SearchAddMoreActivity.class.getSimpleName(),flag);
        context.startActivity(intent);
    }
}
