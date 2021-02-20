package com.mwim.qcloud.tim.uikit.modules.group.member;

import android.app.Activity;
import android.content.Context;

import androidx.annotation.Nullable;

import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;

import com.mwim.qcloud.tim.uikit.modules.contact.ContactItemBean;
import com.mwim.qcloud.tim.uikit.modules.contact.ContactListView;
import com.mwim.qcloud.tim.uikit.modules.group.interfaces.IGroupMemberLayout;
import com.mwim.qcloud.tim.uikit.R;
import com.mwim.qcloud.tim.uikit.base.BaseFragment;
import com.mwim.qcloud.tim.uikit.base.IUIKitCallBack;
import com.mwim.qcloud.tim.uikit.component.TitleBarLayout;
import com.mwim.qcloud.tim.uikit.modules.group.info.GroupInfo;
import com.mwim.qcloud.tim.uikit.modules.group.info.GroupInfoProvider;
import com.work.util.ToastUtil;

import java.util.ArrayList;
import java.util.List;

public class GroupMemberInviteLayout extends LinearLayout implements IGroupMemberLayout, TextWatcher {

    private TitleBarLayout mTitleBar;
    private ContactListView mContactListView;
    private final List<String> mInviteMembers = new ArrayList<>();
    private final ArrayList<ContactItemBean> memberInfoArrayList = new ArrayList<>();
    private Object mParentLayout;
    private GroupInfo mGroupInfo;

    public GroupMemberInviteLayout(Context context) {
        super(context);
        init();
    }

    public GroupMemberInviteLayout(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public GroupMemberInviteLayout(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        inflate(getContext(), R.layout.group_member_invite_layout, this);
        mTitleBar = findViewById(R.id.group_invite_title_bar);
        mTitleBar.setTitle("确定", TitleBarLayout.POSITION.RIGHT);
        mTitleBar.setTitle("添加成员", TitleBarLayout.POSITION.MIDDLE);
        mTitleBar.getRightIcon().setVisibility(View.GONE);
        mTitleBar.setOnRightClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                GroupInfoProvider provider = new GroupInfoProvider();
                provider.loadGroupInfo(mGroupInfo);
                provider.inviteGroupMembers(mInviteMembers, new IUIKitCallBack() {
                    @Override
                    public void onSuccess(Object data) {
                        if (data instanceof String) {
                            ToastUtil.info(getContext(),data.toString());
                        } else {
                            ToastUtil.info(getContext(),"邀请成员成功");
                        }
                        mInviteMembers.clear();
                        finish();
                    }

                    @Override
                    public void onError(String module, int errCode, String errMsg) {
                        ToastUtil.error(getContext(),"邀请成员失败:" + errCode + "=" + errMsg);
                    }
                });
            }
        });
        EditText mSearch = findViewById(R.id.search);
        mSearch.addTextChangedListener(this);
        mContactListView = findViewById(R.id.group_invite_member_list);
        mContactListView.loadDataSource(ContactListView.DataSource.FRIEND_LIST);
        mContactListView.setOnSelectChangeListener(new ContactListView.OnSelectChangedListener() {
            @Override
            public void onSelectChanged(ContactItemBean contact, boolean selected) {
                if (selected) {
                    mInviteMembers.add(contact.getId());
                } else {
                    mInviteMembers.remove(contact.getId());
                }
                if (mInviteMembers.size() > 0) {
                    mTitleBar.setTitle("确定（" + mInviteMembers.size() + "）", TitleBarLayout.POSITION.RIGHT);
                } else {
                    mTitleBar.setTitle("确定", TitleBarLayout.POSITION.RIGHT);
                }
            }
        });
    }

    public void setDataSource(GroupInfo groupInfo) {
        mGroupInfo = groupInfo;
        if (mContactListView != null) {
            mContactListView.setGroupInfo(mGroupInfo);
        }
    }

    public TitleBarLayout getTitleBar() {
        return mTitleBar;
    }

    @Override
    public void setParentLayout(Object parent) {
        mParentLayout = parent;
    }

    private void finish() {
        if (mParentLayout instanceof Activity) {
            ((Activity) mParentLayout).finish();
        } else if (mParentLayout instanceof BaseFragment) {
            ((BaseFragment) mParentLayout).backward();
        }
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
