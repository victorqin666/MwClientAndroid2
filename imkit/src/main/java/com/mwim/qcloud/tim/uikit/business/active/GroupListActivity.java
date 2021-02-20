package com.mwim.qcloud.tim.uikit.business.active;

import android.content.Intent;
import android.text.TextUtils;
import android.view.View;

import com.mwim.qcloud.tim.uikit.TUIKit;
import com.mwim.qcloud.tim.uikit.base.IUIKitCallBack;
import com.mwim.qcloud.tim.uikit.business.Constants;
import com.mwim.qcloud.tim.uikit.modules.chat.GroupChatManagerKit;
import com.mwim.qcloud.tim.uikit.modules.chat.base.ChatInfo;
import com.mwim.qcloud.tim.uikit.modules.contact.ContactItemBean;
import com.mwim.qcloud.tim.uikit.modules.contact.ContactListView;
import com.mwim.qcloud.tim.uikit.modules.group.info.GroupInfo;
import com.mwim.qcloud.tim.uikit.modules.message.MessageInfo;
import com.mwim.qcloud.tim.uikit.modules.message.MessageInfoUtil;
import com.tencent.imsdk.v2.V2TIMConversation;
import com.mwim.qcloud.tim.uikit.R;
import com.work.util.SLog;
import com.work.util.ToastUtil;

public class GroupListActivity extends IMBaseActivity {

    private ContactListView mListView;

    @Override
    public void onInitView() throws Exception {
        super.onInitView();
        mListView = findViewById(R.id.group_list);
        mListView.setOnItemClickListener(new ContactListView.OnItemClickListener() {
            @Override
            public void onItemClick(int position, ContactItemBean contact) {
                ChatInfo chatInfo = new ChatInfo();
                chatInfo.setType(V2TIMConversation.V2TIM_GROUP);
                String chatName = contact.getId();
                if (!TextUtils.isEmpty(contact.getRemark())) {
                    chatName = contact.getRemark();
                } else if (!TextUtils.isEmpty(contact.getNickname())) {
                    chatName = contact.getNickname();
                }
                chatInfo.setChatName(chatName);
                chatInfo.setId(contact.getId());
                String customData = getIntent().getStringExtra(GroupListActivity.class.getSimpleName());
                if(customData!=null){
                    GroupChatManagerKit groupChatManagerKit = GroupChatManagerKit.getInstance();
                    GroupInfo groupInfo = new GroupInfo();
                    groupInfo.setId(chatInfo.getId());
                    groupInfo.setChatName(chatInfo.getChatName());
                    groupChatManagerKit.setCurrentChatInfo(groupInfo);
                    MessageInfo info = MessageInfoUtil.buildCustomMessage(customData);
                    groupChatManagerKit.sendMessage(info, false, new IUIKitCallBack() {
                        @Override
                        public void onSuccess(Object data) {
                            SLog.e("custom message group send success:"+data);
                            finish();
                        }

                        @Override
                        public void onError(String module, int errCode, String errMsg) {
                            ToastUtil.warning(GroupListActivity.this,errCode+">"+errMsg);
                        }
                    });
                }else{
                    Intent intent = new Intent(GroupListActivity.this, ChatActivity.class);
                    intent.putExtra(Constants.CHAT_INFO, chatInfo);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                }
            }
        });
//        loadDataSource();
    }

    @Override
    public void onInitValue() throws Exception {
        super.onInitValue();
        setTitleName(R.string.group);
    }

    @Override
    public int onCustomContentId() {
        return R.layout.activity_im_group_list;
    }


    @Override
    public void onRightClickListener(View view) {
        super.onRightClickListener(view);
        Intent intent = new Intent(TUIKit.getAppContext(), AddMoreActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra("isGroup", true);
        startActivity(intent);
    }

    @Override
    public void onResume() {
        super.onResume();
        loadDataSource();
    }

    public void loadDataSource() {
        mListView.loadDataSource(ContactListView.DataSource.GROUP_LIST);
    }
}
