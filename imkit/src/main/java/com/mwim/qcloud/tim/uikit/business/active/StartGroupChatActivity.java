package com.mwim.qcloud.tim.uikit.business.active;

import android.content.Intent;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.mwim.qcloud.tim.uikit.TUIKit;
import com.mwim.qcloud.tim.uikit.base.IUIKitCallBack;
import com.mwim.qcloud.tim.uikit.business.Constants;
import com.mwim.qcloud.tim.uikit.business.dialog.GroupJoinTypeDialog;
import com.mwim.qcloud.tim.uikit.business.modal.UserApi;
import com.mwim.qcloud.tim.uikit.component.LineControllerView;
import com.mwim.qcloud.tim.uikit.component.SelectionActivity;
import com.mwim.qcloud.tim.uikit.modules.chat.GroupChatManagerKit;
import com.mwim.qcloud.tim.uikit.modules.chat.base.ChatInfo;
import com.mwim.qcloud.tim.uikit.modules.contact.ContactItemBean;
import com.mwim.qcloud.tim.uikit.modules.contact.ContactListView;
import com.mwim.qcloud.tim.uikit.modules.group.info.GroupInfo;
import com.mwim.qcloud.tim.uikit.modules.group.member.GroupMemberInfo;
import com.mwim.qcloud.tim.uikit.utils.IMKitConstants;
import com.tencent.imsdk.v2.V2TIMConversation;
import com.tencent.imsdk.v2.V2TIMManager;
import com.mwim.qcloud.tim.uikit.R;
import com.work.util.ToastUtil;

import java.util.ArrayList;
import java.util.Arrays;

public class StartGroupChatActivity extends IMBaseActivity implements TextWatcher {

    private LineControllerView mJoinType;
    private final ArrayList<GroupMemberInfo> mMembers = new ArrayList<>();
    private final ArrayList<ContactItemBean> memberInfoArrayList = new ArrayList<>();
    private int mGroupType = -1;
    private int mJoinTypeIndex = 2;
    private final ArrayList<String> mJoinTypes = new ArrayList<>();
    private final ArrayList<String> mGroupTypeValue = new ArrayList<>();
    private boolean mCreating;
    private ContactListView mContactListView;

    @Override
    public void onInitView() throws Exception {
        super.onInitView();
        String[] array = getResources().getStringArray(R.array.group_type);
        mGroupTypeValue.addAll(Arrays.asList(array));
        array = getResources().getStringArray(R.array.group_join_type);
        mJoinTypes.addAll(Arrays.asList(array));


        GroupMemberInfo memberInfo = new GroupMemberInfo();
        memberInfo.setAccount(V2TIMManager.getInstance().getLoginUser());
        memberInfo.setNameCard(UserApi.instance().getNickName());
        mMembers.add(memberInfo);

        ChatInfo chatInfo = (ChatInfo) getIntent().getSerializableExtra(StartGroupChatActivity.class.getSimpleName());
        if(chatInfo!=null){
            memberInfo = new GroupMemberInfo();
            memberInfo.setAccount(chatInfo.getId());
            memberInfo.setNameCard(chatInfo.getChatName());
            mMembers.add(memberInfo);
        }

        mJoinType = findViewById(R.id.group_type_join);
        mJoinType.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showJoinTypePickerView();
            }
        });
        mJoinType.setCanNav(true);
        mJoinType.setContent(mJoinTypes.get(mJoinTypeIndex));

        EditText mSearch = findViewById(R.id.search);
        mSearch.addTextChangedListener(this);

        mContactListView = findViewById(R.id.group_create_member_list);
        GroupInfo groupInfo = new GroupInfo();
        groupInfo.setMemberDetails(mMembers);
        mContactListView.setGroupInfo(groupInfo);
        mContactListView.loadDataSource(ContactListView.DataSource.FRIEND_LIST);
        mContactListView.setOnSelectChangeListener(new ContactListView.OnSelectChangedListener() {
            @Override
            public void onSelectChanged(ContactItemBean contact, boolean selected) {
                if (selected) {
                    GroupMemberInfo memberInfo = new GroupMemberInfo();
                    memberInfo.setAccount(contact.getId());
                    memberInfo.setNameCard(contact.getNickname());
                    mMembers.add(memberInfo);
                } else {
                    for (int i = mMembers.size() - 1; i >= 0; i--) {
                        if (mMembers.get(i).getAccount().equals(contact.getId())) {
                            mMembers.remove(i);
                            break;
                        }
                    }
                }
            }
        });

        setGroupType(getIntent().getIntExtra("type", IMKitConstants.GroupType.PRIVATE));
    }

    @Override
    public void onInitValue() throws Exception {
        super.onInitValue();
    }

    @Override
    public int onCustomContentId() {
        return R.layout.activity_popup_start_group_chat;
    }

    @Override
    public View onCustomTitleRight(TextView view) {
        view.setText(R.string.sure);
        return view;
    }

    @Override
    public void onRightClickListener(View view) {
        super.onRightClickListener(view);
        createGroupChat();
    }

    public void setGroupType(int type) {
        mJoinType.setVisibility(View.GONE);
        mGroupType = type;
        switch (type) {
            case IMKitConstants.GroupType.PUBLIC:
                setTitleName(getResources().getString(R.string.create_group_chat));
//                mJoinType.setVisibility(View.VISIBLE);
                break;
            case IMKitConstants.GroupType.CHAT_ROOM:
                setTitleName(getResources().getString(R.string.create_chat_room));
//                mJoinType.setVisibility(View.VISIBLE);
                break;
            case IMKitConstants.GroupType.PRIVATE:
            default:
                setTitleName(getResources().getString(R.string.create_private_group));
                mJoinType.setVisibility(View.GONE);
                break;
        }
    }

    private void showJoinTypePickerView() {
        GroupJoinTypeDialog mGroupJoinDialog = new GroupJoinTypeDialog();
        mGroupJoinDialog.setJoinTypes(mJoinTypes);
        mGroupJoinDialog.setOnResultReturnListener(new SelectionActivity.OnResultReturnListener() {
            @Override
            public void onReturn(Object text) {
                mJoinType.setContent(mJoinTypes.get((Integer) text));
                mJoinTypeIndex = (Integer) text;
            }
        });
        mGroupJoinDialog.setJoinTypeIndex(mJoinTypeIndex);
        mGroupJoinDialog.show(getSupportFragmentManager(),"group_join");
    }

    private void createGroupChat() {
        if (mCreating) {
            return;
        }
        if (mGroupType < 3 && mMembers.size() == 1) {
            ToastUtil.info(this,getResources().getString(R.string.tips_empty_group_member));
            return;
        }
        if (mGroupType > 0 && mJoinTypeIndex == -1) {
            ToastUtil.info(this,getResources().getString(R.string.tips_empty_group_type));
            return;
        }
        if (mGroupType == 0) {
            mJoinTypeIndex = -1;
        }
        final GroupInfo groupInfo = new GroupInfo();
        StringBuilder groupName = new StringBuilder(UserApi.instance().getNickName());
        for (int i = 1; i < mMembers.size(); i++) {
            groupName.append("、").append(mMembers.get(i).getNameCard());
        }
        if (groupName.length() > 9) {
            groupName = new StringBuilder(groupName.substring(0, 9) + "...");
        }
        groupInfo.setChatName(groupName.toString());
        groupInfo.setGroupName(groupName.toString());
        groupInfo.setMemberDetails(mMembers);
//        groupInfo.setGroupType(mGroupTypeValue.get(mGroupType));
        groupInfo.setGroupType("Work");
//        groupInfo.setJoinType(mJoinTypeIndex);

        mCreating = true;
        GroupChatManagerKit.createGroupChat(groupInfo, new IUIKitCallBack() {
            @Override
            public void onSuccess(Object data) {
                ChatInfo chatInfo = new ChatInfo();
                chatInfo.setType(V2TIMConversation.V2TIM_GROUP);
                chatInfo.setId(data.toString());
                chatInfo.setChatName(groupInfo.getGroupName());
                Intent intent = new Intent(TUIKit.getAppContext(), ChatActivity.class);
                intent.putExtra(Constants.CHAT_INFO, chatInfo);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                TUIKit.getAppContext().startActivity(intent);
                finish();
            }

            @Override
            public void onError(String module, int errCode, String errMsg) {
                mCreating = false;
                ToastUtil.error(StartGroupChatActivity.this,"群组创建失败:" + errCode + "=" + errMsg);
            }
        });
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {

    }

    @Override
    public void afterTextChanged(Editable s) {
        String text = s.toString();
        if(memberInfoArrayList.size()==0){
            memberInfoArrayList.addAll(mContactListView.getGroupData());
        }
        if(TextUtils.isEmpty(text)){
            mContactListView.setDataSource(new ArrayList<>(memberInfoArrayList));
        }else{
            ArrayList<ContactItemBean> groupMemberInfos = new ArrayList<>();
            for (ContactItemBean groupMemberInfo:memberInfoArrayList) {
                if(groupMemberInfo.getNickname().contains(text)
                        || groupMemberInfo.getRemark().contains(text)){
                    groupMemberInfos.add(groupMemberInfo);
                }
            }
            mContactListView.setDataSource(groupMemberInfos);
        }
    }
}
