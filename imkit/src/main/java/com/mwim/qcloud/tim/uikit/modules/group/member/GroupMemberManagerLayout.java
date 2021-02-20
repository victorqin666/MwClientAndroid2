package com.mwim.qcloud.tim.uikit.modules.group.member;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;

import androidx.annotation.Nullable;

import android.content.Intent;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;

import com.mwim.qcloud.tim.uikit.business.active.FriendProfileActivity;
import com.mwim.qcloud.tim.uikit.modules.chat.base.ChatInfo;
import com.mwim.qcloud.tim.uikit.modules.contact.ContactItemBean;
import com.mwim.qcloud.tim.uikit.modules.contact.ContactListView;
import com.mwim.qcloud.tim.uikit.modules.group.interfaces.IGroupMemberLayout;
import com.mwim.qcloud.tim.uikit.R;
import com.mwim.qcloud.tim.uikit.component.TitleBarLayout;
import com.mwim.qcloud.tim.uikit.modules.group.info.GroupInfo;
import com.mwim.qcloud.tim.uikit.utils.PopWindowUtil;
import com.mwim.qcloud.tim.uikit.utils.IMKitConstants;

import java.util.ArrayList;
import java.util.List;


public class GroupMemberManagerLayout extends LinearLayout implements IGroupMemberLayout, TextWatcher {

    private TitleBarLayout mTitleBar;
    private AlertDialog mDialog;
//    private GroupMemberManagerAdapter mAdapter;
    private IGroupMemberRouter mGroupMemberManager;
    private GroupInfo mGroupInfo;
    private ContactListView mContactListView;
    private List<ContactItemBean> mAllMemberManager;

    public GroupMemberManagerLayout(Context context) {
        super(context);
        init();
    }

    public GroupMemberManagerLayout(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public GroupMemberManagerLayout(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        inflate(getContext(), R.layout.group_member_layout, this);
        mTitleBar = findViewById(R.id.group_member_title_bar);
        mTitleBar.setTitle("管理", TitleBarLayout.POSITION.RIGHT);
        mTitleBar.getRightIcon().setVisibility(GONE);
        mTitleBar.setOnRightClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                buildPopMenu();
            }
        });
        EditText mSearch = findViewById(R.id.search);
        mSearch.addTextChangedListener(this);
//        mAdapter = new GroupMemberManagerAdapter();
//        mAdapter.setMemberChangedCallback(new IGroupMemberChangedCallback() {
//
//            @Override
//            public void onMemberRemoved(GroupMemberInfo memberInfo) {
//                mTitleBar.setTitle("群成员(" + mGroupInfo.getMemberDetails().size() + ")", TitleBarLayout.POSITION.MIDDLE);
//            }
//        });
//        GridView gridView = findViewById(R.id.group_all_members);
//        gridView.setAdapter(mAdapter);
        mContactListView = findViewById(R.id.group_create_member_list);
        mContactListView.setOnItemClickListener(new ContactListView.OnItemClickListener() {
            @Override
            public void onItemClick(int position, ContactItemBean contact) {
                ChatInfo chatInfo = new ChatInfo();
                chatInfo.setId(contact.getId());
                chatInfo.setChatName(contact.getNickname());
                getContext().startActivity(new Intent(getContext(), FriendProfileActivity.class).putExtra(IMKitConstants.ProfileType.CONTENT,chatInfo));
            }
        });
    }

    public TitleBarLayout getTitleBar() {
        return mTitleBar;
    }

    @Override
    public void setParentLayout(Object parent) {

    }

    public void setDataSource(GroupInfo groupInfo) {
        mGroupInfo = groupInfo;
        mContactListView.setGroupInfo(groupInfo);
        List<GroupMemberInfo> memberDetails = groupInfo.getMemberDetails();
        List<ContactItemBean> contactItemBeans = new ArrayList<>();
        for (GroupMemberInfo info:memberDetails) {
            ContactItemBean contactItemBean = new ContactItemBean();
            List<Object> icons = new ArrayList<>();
            icons.add(info.getIconUrl());
            contactItemBean.setId(info.getAccount());
            contactItemBean.setIconUrlList(icons);
            contactItemBean.setNickname(info.getNickName());
            contactItemBean.setRemark(info.getNameCard());
            contactItemBeans.add(contactItemBean);
        }
        mAllMemberManager = new ArrayList<>(contactItemBeans);
        mContactListView.setDataSource(contactItemBeans);
//        mAdapter.setDataSource(groupInfo);
        if (groupInfo != null) {
            mTitleBar.setTitle("群成员(" + groupInfo.getMemberDetails().size() + ")", TitleBarLayout.POSITION.MIDDLE);
        }
    }

    private void buildPopMenu() {
        if (mGroupInfo == null) {
            return;
        }
        if (mDialog == null) {
            mDialog = PopWindowUtil.buildFullScreenDialog((Activity) getContext());
            View moreActionView = inflate(getContext(), R.layout.group_member_pop_menu, null);
            moreActionView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mDialog.dismiss();
                }
            });
            Button addBtn = moreActionView.findViewById(R.id.add_group_member);
            addBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (mGroupMemberManager != null) {
                        mGroupMemberManager.forwardAddMember(mGroupInfo);
                    }
                    mDialog.dismiss();

                }
            });
            Button deleteBtn = moreActionView.findViewById(R.id.remove_group_member);
            if (!mGroupInfo.isOwner()) {
                deleteBtn.setVisibility(View.GONE);
            }
            deleteBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (mGroupMemberManager != null) {
                        mGroupMemberManager.forwardDeleteMember(mGroupInfo);
                    }
                    mDialog.dismiss();
                }
            });
            Button cancelBtn = moreActionView.findViewById(R.id.cancel);
            cancelBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mDialog.dismiss();
                }
            });
            mDialog.setContentView(moreActionView);
        } else {
            mDialog.show();
        }

    }

    public void setRouter(IGroupMemberRouter callBack) {
        this.mGroupMemberManager = callBack;
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
        if(TextUtils.isEmpty(text)){
            mContactListView.setDataSource(new ArrayList<>(mAllMemberManager));
        }else{
            ArrayList<ContactItemBean> groupMemberInfos = new ArrayList<>();
            for (ContactItemBean groupMemberInfo:mAllMemberManager) {
                if((!TextUtils.isEmpty(groupMemberInfo.getNickname()) && groupMemberInfo.getNickname().contains(text))
                        || (!TextUtils.isEmpty(groupMemberInfo.getRemark()) && groupMemberInfo.getRemark().contains(text))){
                    groupMemberInfos.add(groupMemberInfo);
                }
            }
            mContactListView.setDataSource(groupMemberInfos);
        }
    }
}
