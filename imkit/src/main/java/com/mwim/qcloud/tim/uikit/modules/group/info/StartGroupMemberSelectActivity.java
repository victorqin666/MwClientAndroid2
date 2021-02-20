package com.mwim.qcloud.tim.uikit.modules.group.info;

import android.content.Intent;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.mwim.qcloud.tim.uikit.R;
import com.mwim.qcloud.tim.uikit.base.BaseActivity;
import com.mwim.qcloud.tim.uikit.modules.contact.ContactItemBean;
import com.mwim.qcloud.tim.uikit.modules.contact.ContactListView;
import com.mwim.qcloud.tim.uikit.modules.group.member.GroupMemberInfo;
import com.mwim.qcloud.tim.uikit.utils.IMKitConstants;
import com.tencent.imsdk.v2.V2TIMGroupAtInfo;

import java.util.ArrayList;

/**
 * Created by tangyx
 * Date 2020/9/24
 * email tangyx@live.com
 */

public class StartGroupMemberSelectActivity extends BaseActivity implements TextWatcher {

    private final ArrayList<GroupMemberInfo> mMembers = new ArrayList<>();
    private final ArrayList<ContactItemBean> memberInfoArrayList = new ArrayList<>();
    private ContactListView mContactListView;

    @Override
    public void onInitView() throws Exception {
        super.onInitView();
        EditText mSearch = findViewById(R.id.search);
        mSearch.addTextChangedListener(this);
        mMembers.clear();
        GroupInfo groupInfo = (GroupInfo) getIntent().getExtras().getSerializable(IMKitConstants.Group.GROUP_INFO);
        if(groupInfo ==null){
            finish();
        }
        mContactListView = findViewById(R.id.group_create_member_list);
        mContactListView.setGroupInfo(groupInfo);
        loadGroupInfo();
    }

    private void loadGroupInfo(){
        mContactListView.loadDataSource(ContactListView.DataSource.GROUP_MEMBER_LIST);
        mContactListView.setOnSelectChangeListener(new ContactListView.OnSelectChangedListener() {
            @Override
            public void onSelectChanged(ContactItemBean contact, boolean selected) {
                if (selected) {
                    GroupMemberInfo memberInfo = new GroupMemberInfo();
                    memberInfo.setAccount(contact.getId());
                    memberInfo.setNameCard(TextUtils.isEmpty(contact.getNickname()) ? contact.getId() : contact.getNickname());
                    mMembers.add(memberInfo);
                } else {
                    for (int i = mMembers.size() - 1; i >= 0; i--) {
                        if (mMembers.get(i).getAccount().equals(contact.getId())) {
                            mMembers.remove(i);
                        }
                    }
                }
            }
        });
        mContactListView.setOnItemClickListener(new ContactListView.OnItemClickListener() {
            @Override
            public void onItemClick(int position, ContactItemBean contact) {
                if (position == 0) {
                    mMembers.clear();
                    Intent i = new Intent();
                    i.putExtra(IMKitConstants.Selection.USER_NAMECARD_SELECT, getString(R.string.at_all));
                    i.putExtra(IMKitConstants.Selection.USER_ID_SELECT, V2TIMGroupAtInfo.AT_ALL_TAG);
                    setResult(3, i);
                    finish();
                }
            }
        });

    }


    @Override
    public void onInitValue() throws Exception {
        super.onInitValue();
        setTitleName("选择提醒的人");
    }

    @Override
    public View onCustomTitleRight(TextView view) {
        view.setText(R.string.sure);
        return view;
    }

    @Override
    public void onRightClickListener(View view) {
        super.onRightClickListener(view);
        Intent i = new Intent();
        i.putExtra(IMKitConstants.Selection.USER_NAMECARD_SELECT, getMembersNameCard());
        i.putExtra(IMKitConstants.Selection.USER_ID_SELECT, getMembersUserId());
        setResult(3, i);

        finish();
    }

    @Override
    public int onCustomContentId() {
        return R.layout.popup_start_group_select_activity;
    }

    private String getMembersNameCard(){
        if (mMembers.size() == 0) {
            return "";
        }

        //TUIKitLog.i(TAG, "getMembersNameCard mMembers.size() = " + mMembers.size());
        String nameCardString = "";
        for(int i = 0; i < mMembers.size(); i++){
            nameCardString += mMembers.get(i).getNameCard();
            nameCardString += " ";
        }
        //TUIKitLog.i(TAG, "nameCardString = " + nameCardString);
        return nameCardString;
    }

    private String getMembersUserId(){
        if (mMembers.size() == 0) {
            return "";
        }

        //TUIKitLog.i(TAG, "getMembersUserId mMembers.size() = " + mMembers.size());
        StringBuilder userIdString = new StringBuilder();
        for(int i = 0; i < mMembers.size(); i++){
            userIdString.append(mMembers.get(i).getAccount());
            userIdString.append(" ");
        }
        //TUIKitLog.i(TAG, "userIdString = " + userIdString);
        return userIdString.toString();
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
                if((!TextUtils.isEmpty(groupMemberInfo.getNickname()) && groupMemberInfo.getNickname().contains(text))
                    || (!TextUtils.isEmpty(groupMemberInfo.getRemark()) && groupMemberInfo.getRemark().contains(text))){
                    groupMemberInfos.add(groupMemberInfo);
                }
            }
            mContactListView.setDataSource(groupMemberInfos);
        }
    }
}
