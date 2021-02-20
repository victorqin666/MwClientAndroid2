package com.mwim.qcloud.tim.uikit.modules.group.info;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.GridView;
import android.widget.LinearLayout;

import androidx.annotation.Nullable;

import com.mwim.qcloud.tim.uikit.base.BaseActivity;
import com.mwim.qcloud.tim.uikit.business.dialog.ConfirmDialog;
import com.mwim.qcloud.tim.uikit.business.dialog.GroupJoinTypeDialog;
import com.mwim.qcloud.tim.uikit.modules.group.interfaces.IGroupMemberLayout;
import com.mwim.qcloud.tim.uikit.modules.group.member.GroupMemberInfo;
import com.mwim.qcloud.tim.uikit.modules.group.member.GroupMemberRemindActivity;
import com.mwim.qcloud.tim.uikit.modules.group.member.IGroupMemberRouter;
import com.tencent.imsdk.v2.V2TIMCallback;
import com.tencent.imsdk.v2.V2TIMGroupInfo;
import com.tencent.imsdk.v2.V2TIMGroupMemberFullInfo;
import com.tencent.imsdk.v2.V2TIMGroupMemberInfoResult;
import com.tencent.imsdk.v2.V2TIMManager;
import com.mwim.qcloud.tim.uikit.R;
import com.mwim.qcloud.tim.uikit.base.IUIKitCallBack;
import com.mwim.qcloud.tim.uikit.component.LineControllerView;
import com.mwim.qcloud.tim.uikit.component.SelectionActivity;
import com.mwim.qcloud.tim.uikit.component.TitleBarLayout;
import com.mwim.qcloud.tim.uikit.utils.IMKitConstants;
import com.tencent.imsdk.v2.V2TIMValueCallback;
import com.work.util.SLog;
import com.work.util.ToastUtil;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;


public class GroupInfoLayout extends LinearLayout implements IGroupMemberLayout, View.OnClickListener {

    private TitleBarLayout mTitleBar;
    private LineControllerView mMemberView;
    private GroupInfoAdapter mMemberAdapter;
    private GroupInfoAdminAdapter mMemberAdminAdapter;
    private IGroupMemberRouter mMemberPreviewListener;
    private LineControllerView mGroupTypeView;
    private LineControllerView mGroupIDView;
    private LineControllerView mGroupNameView;
    private LineControllerView mGroupNotice;
    private LineControllerView mNickView;
    private LineControllerView mJoinTypeView;
    private LineControllerView mMutedSwitchView;
    private LineControllerView mRevOptView;
    private LineControllerView mTopSwitchView;
    private LineControllerView mTransferGroupView;
    private LineControllerView mMemberAdminView;
    private Button mDissolveBtn;

    private GroupInfo mGroupInfo;
    private GroupInfoPresenter mPresenter;
    private ArrayList<String> mJoinTypes = new ArrayList<>();

    public GroupInfoLayout(Context context) {
        super(context);
        init();
    }

    public GroupInfoLayout(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public GroupInfoLayout(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        inflate(getContext(), R.layout.group_info_layout, this);
        // 标题
        mTitleBar = findViewById(R.id.group_info_title_bar);
        mTitleBar.getRightGroup().setVisibility(GONE);
        mTitleBar.setTitle(getResources().getString(R.string.group_detail), TitleBarLayout.POSITION.MIDDLE);
        mTitleBar.setOnLeftClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((Activity) getContext()).finish();
            }
        });
        // 成员标题
        mMemberView = findViewById(R.id.group_member_bar);
        mMemberView.setOnClickListener(this);
        mMemberView.setCanNav(true);
        // 成员列表
        GridView memberList = findViewById(R.id.group_members);
        mMemberAdapter = new GroupInfoAdapter();
        memberList.setAdapter(mMemberAdapter);
        // 群类型，只读
        mGroupTypeView = findViewById(R.id.group_type_bar);
        // 群ID，只读
        mGroupIDView = findViewById(R.id.group_account);
        // 群聊名称
        mGroupNameView = findViewById(R.id.group_name);
        mGroupNameView.setOnClickListener(this);
        mGroupNameView.setCanNav(true);
        // 群头像
        LineControllerView mGroupIcon = findViewById(R.id.group_icon);
        mGroupIcon.setOnClickListener(this);
        mGroupIcon.setCanNav(false);
        // 群公告
        mGroupNotice = findViewById(R.id.group_notice);
        mGroupNotice.setOnClickListener(this);
        mGroupNotice.setCanNav(true);
        // 加群方式
        mJoinTypeView = findViewById(R.id.join_type_bar);
        mJoinTypeView.setOnClickListener(this);
        mJoinTypeView.setCanNav(true);
        mJoinTypes.addAll(Arrays.asList(getResources().getStringArray(R.array.group_join_type)));
        // 群昵称
        mNickView = findViewById(R.id.self_nickname_bar);
        mNickView.setOnClickListener(this);
        mNickView.setCanNav(true);
        //转让群主
        mTransferGroupView = findViewById(R.id.transfer_group_owner);
        mTransferGroupView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                GroupMemberRemindActivity.startGroupMemberRemind(getContext(), mGroupInfo.getId(),getContext().getString(R.string.transfer_group_owner), new SelectionActivity.OnResultReturnListener() {
                    @Override
                    public void onReturn(Object res) {
                        GroupMemberInfo memberInfo = (GroupMemberInfo) res;
                        V2TIMManager.getGroupManager().transferGroupOwner(mGroupInfo.getId(), memberInfo.getAccount(), new V2TIMCallback() {
                            @Override
                            public void onError(int i, String s) {
                                ToastUtil.error(getContext(),s);
                            }

                            @Override
                            public void onSuccess() {
                                ToastUtil.success(getContext(),"转让成功");
                                ((Activity)getContext()).finish();
                            }
                        });
                    }
                });
            }
        });
        // 管理员列表
        mMemberAdminView = findViewById(R.id.group_member_admin);
        GridView memberAdminList = findViewById(R.id.group_members_admin);
        mMemberAdminAdapter = new GroupInfoAdminAdapter();
        memberAdminList.setAdapter(mMemberAdminAdapter);
        memberAdminList.setVisibility(GONE);
        //消息免打扰
        mRevOptView = findViewById(R.id.rev_opt);
        mRevOptView.setCheckListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, final boolean b) {
                if(mGroupInfo==null){
                    return;
                }
                V2TIMManager.getGroupManager().setReceiveMessageOpt(mGroupInfo.getId(), b ? V2TIMGroupInfo.V2TIM_GROUP_NOT_RECEIVE_MESSAGE : V2TIMGroupInfo.V2TIM_GROUP_RECEIVE_MESSAGE, new V2TIMCallback() {
                    @Override
                    public void onError(int i, String s) {
                        SLog.e("消息免打扰:" + i + "|desc:" + s);
                    }

                    @Override
                    public void onSuccess() {
                        SLog.e("消息免打扰设置:"+b);
                        if(b){
                            mGroupInfo.setRevOpt(V2TIMGroupInfo.V2TIM_GROUP_NOT_RECEIVE_MESSAGE);
                        }else{
                            mGroupInfo.setRevOpt(V2TIMGroupInfo.V2TIM_GROUP_RECEIVE_MESSAGE);
                        }
                    }
                });
            }
        });
        //全群禁言
        mMutedSwitchView = findViewById(R.id.chat_muted);
        mMutedSwitchView.setCheckListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(mGroupInfo==null){
                    return;
                }
                V2TIMGroupInfo v2TIMGroupInfo = new V2TIMGroupInfo();
                v2TIMGroupInfo.setGroupID(mGroupInfo.getId());
                v2TIMGroupInfo.setAllMuted(b);
                V2TIMManager.getGroupManager().setGroupInfo(v2TIMGroupInfo, new V2TIMCallback() {
                    @Override
                    public void onError(int code, String desc) {
                        SLog.e("全员禁言:" + code + "|desc:" + desc);
                    }

                    @Override
                    public void onSuccess() {
//                    ToastUtil.toastLongMessage("修改群头像成功");
                        SLog.e("全员禁言成功");
                    }
                });
            }
        });
        // 是否置顶
        mTopSwitchView = findViewById(R.id.chat_to_top_switch);
        mTopSwitchView.setCheckListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                mPresenter.setTopConversation(isChecked);
            }
        });
        // 退群
        mDissolveBtn = findViewById(R.id.group_dissolve_button);
        mDissolveBtn.setOnClickListener(this);

        mPresenter = new GroupInfoPresenter(this);
    }

    @Override
    public void onClick(View v) {
        if (mGroupInfo == null) {
            SLog.e("mGroupInfo is NULL");
            return;
        }
        BaseActivity activity = (BaseActivity) getContext();
        if (v.getId() == R.id.group_member_bar) {
            if (mMemberPreviewListener != null) {
                mMemberPreviewListener.forwardListMember(mGroupInfo);
            }
        } else if (v.getId() == R.id.group_name) {
            Bundle bundle = new Bundle();
            bundle.putString(IMKitConstants.Selection.TITLE, getResources().getString(R.string.modify_group_name));
            bundle.putString(IMKitConstants.Selection.INIT_CONTENT, mGroupNameView.getContent());
            bundle.putInt(IMKitConstants.Selection.LIMIT, 10);
            SelectionActivity.startTextSelection(getContext(), bundle, new SelectionActivity.OnResultReturnListener() {
                @Override
                public void onReturn(final Object text) {
                    mPresenter.modifyGroupName(text.toString());
                    mGroupNameView.setContent(text.toString());
                }
            });
        } else if (v.getId() == R.id.group_icon) {
            String groupUrl = String.format("https://picsum.photos/id/%d/200/200", new Random().nextInt(1000));

            V2TIMGroupInfo v2TIMGroupInfo = new V2TIMGroupInfo();
            v2TIMGroupInfo.setGroupID(mGroupInfo.getId());
            v2TIMGroupInfo.setFaceUrl(groupUrl);
            V2TIMManager.getGroupManager().setGroupInfo(v2TIMGroupInfo, new V2TIMCallback() {
                @Override
                public void onError(int code, String desc) {
                    SLog.e("modify group icon failed, code:" + code + "|desc:" + desc);
                }

                @Override
                public void onSuccess() {
//                    ToastUtil.toastLongMessage("修改群头像成功");
                }
            });
        } else if (v.getId() == R.id.group_notice) {
            if(!mGroupInfo.isOwner()){
                if(!TextUtils.isEmpty(mGroupNotice.getContent())){
                    new ConfirmDialog().setContent(mGroupNotice.getContent())
                            .setHiddenCancel(true)
                            .setConfirmTextResId(R.string.label_know)
                            .show(((BaseActivity) getContext()).getSupportFragmentManager(),"notice");
                }
                ToastUtil.info(getContext(),"您没有权限修改");
                return;
            }
            Bundle bundle = new Bundle();
            bundle.putString(IMKitConstants.Selection.TITLE, getResources().getString(R.string.modify_group_notice));
            bundle.putString(IMKitConstants.Selection.INIT_CONTENT, mGroupNotice.getContent());
            bundle.putInt(IMKitConstants.Selection.LIMIT, 200);
            SelectionActivity.startTextSelection(getContext(), bundle, new SelectionActivity.OnResultReturnListener() {
                @Override
                public void onReturn(final Object text) {
                    mPresenter.modifyGroupNotice(text.toString());
                    mGroupNotice.setContent(text.toString());
                }
            });
        } else if (v.getId() == R.id.self_nickname_bar) {
            Bundle bundle = new Bundle();
            bundle.putString(IMKitConstants.Selection.TITLE, getResources().getString(R.string.modify_nick_name_in_goup));
            bundle.putString(IMKitConstants.Selection.INIT_CONTENT, mNickView.getContent());
            bundle.putInt(IMKitConstants.Selection.LIMIT, 6);
            SelectionActivity.startTextSelection(getContext(), bundle, new SelectionActivity.OnResultReturnListener() {
                @Override
                public void onReturn(final Object text) {
                    mPresenter.modifyMyGroupNickname(text.toString());
                    mNickView.setContent(text.toString());
                }
            });
        } else if (v.getId() == R.id.join_type_bar) {
            if (mGroupTypeView.getContent().equals("聊天室")) {
                ToastUtil.info(getContext(),"加入聊天室为自动审批，暂不支持修改");
                return;
            }
            Bundle bundle = new Bundle();
            bundle.putString(IMKitConstants.Selection.TITLE, getResources().getString(R.string.group_join_type));
            bundle.putStringArrayList(IMKitConstants.Selection.LIST, mJoinTypes);
            bundle.putInt(IMKitConstants.Selection.DEFAULT_SELECT_ITEM_INDEX, mGroupInfo.getJoinType());
//            SelectionActivity.startListSelection((Activity) getContext(), bundle, new SelectionActivity.OnResultReturnListener() {
//                @Override
//                public void onReturn(final Object text) {
//                    mPresenter.modifyGroupInfo((Integer) text, TUIKitConstants.Group.MODIFY_GROUP_JOIN_TYPE);
//                    mJoinTypeView.setContent(mJoinTypes.get((Integer) text));
//
//                }
//            });
            GroupJoinTypeDialog groupJoinTypeDialog = new GroupJoinTypeDialog();
            groupJoinTypeDialog.setJoinTypeIndex(mGroupInfo.getJoinType());
            groupJoinTypeDialog.setJoinTypes(mJoinTypes);
            groupJoinTypeDialog.setOnResultReturnListener(new SelectionActivity.OnResultReturnListener() {
                @Override
                public void onReturn(Object text) {
                    mPresenter.modifyGroupInfo((Integer) text, IMKitConstants.Group.MODIFY_GROUP_JOIN_TYPE);
                    mJoinTypeView.setContent(mJoinTypes.get((Integer) text));
                }
            });
            groupJoinTypeDialog.show(activity.getSupportFragmentManager(),"join_type");
        } else if (v.getId() == R.id.group_dissolve_button) {
            if (mGroupInfo.isOwner() &&
                    (!mGroupInfo.getGroupType().equals(IMKitConstants.GroupType.TYPE_WORK)
                            || !mGroupInfo.getGroupType().equals(IMKitConstants.GroupType.TYPE_PRIVATE))) {
                new ConfirmDialog().setContent("您确认解散该群?").setOnConfirmListener(new OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        mPresenter.deleteGroup();
                    }
                }).show(activity.getSupportFragmentManager(),"del_group");
            } else {
                new ConfirmDialog().setContent("您确认退出该群?").setOnConfirmListener(new OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        mPresenter.quitGroup();
                    }
                }).show(activity.getSupportFragmentManager(),"quit_group");
            }
        }
    }

    public void setGroupId(String groupId) {
        mPresenter.loadGroupInfo(groupId, new IUIKitCallBack() {
            @Override
            public void onSuccess(Object data) {
                setGroupInfo((GroupInfo) data);
            }

            @Override
            public void onError(String module, int errCode, String errMsg) {

            }
        });
    }

    private void setGroupInfo(GroupInfo info) {
        if (info == null) {
            return;
        }
        this.mGroupInfo = info;
        mGroupNameView.setContent(info.getGroupName());
        mGroupIDView.setContent(info.getId());
        mGroupNotice.setContent(info.getNotice());
        mMemberView.setContent(info.getMemberCount() + "人");
        mMemberAdapter.setDataSource(info);
        mGroupTypeView.setContent(convertGroupText(info.getGroupType()));
        mJoinTypeView.setContent(mJoinTypes.get(info.getJoinType()));
        mNickView.setContent(mPresenter.getNickName());
        mMutedSwitchView.setChecked(mGroupInfo.isMuted());
        mTopSwitchView.setChecked(mGroupInfo.isTopChat());
        mRevOptView.setChecked(mGroupInfo.getRevOpt()==V2TIMGroupInfo.V2TIM_GROUP_NOT_RECEIVE_MESSAGE);
        mDissolveBtn.setText(R.string.dissolve);
        if (mGroupInfo.isOwner()) {
//            mJoinTypeView.setVisibility(VISIBLE);
            if (mGroupInfo.getGroupType().equals(IMKitConstants.GroupType.TYPE_WORK)
                    || mGroupInfo.getGroupType().equals(IMKitConstants.GroupType.TYPE_PRIVATE)) {
                mDissolveBtn.setText(R.string.dissolve);
            }
        } else {
            mTransferGroupView.setVisibility(GONE);
//            mJoinTypeView.setVisibility(GONE);
            mDissolveBtn.setText(R.string.exit_group);
        }
        mMutedSwitchView.setVisibility(GONE);
//        loadAdmin();
    }

    public void loadAdmin(){
        V2TIMManager.getGroupManager().getGroupMemberList(mGroupInfo.getId(), V2TIMGroupMemberFullInfo.V2TIM_GROUP_MEMBER_FILTER_ADMIN, 0, new V2TIMValueCallback<V2TIMGroupMemberInfoResult>() {
            @Override
            public void onError(int code, String desc) {
                SLog.e("loadGroupMembers failed, code: " + code + "|desc: " + desc);
            }

            @Override
            public void onSuccess(V2TIMGroupMemberInfoResult v2TIMGroupMemberInfoResult) {
                List<GroupMemberInfo> members = new ArrayList<>();
                for (int i = 0; i < v2TIMGroupMemberInfoResult.getMemberInfoList().size(); i++) {
                    GroupMemberInfo member = new GroupMemberInfo();
                    members.add(member.covertTIMGroupMemberInfo(v2TIMGroupMemberInfoResult.getMemberInfoList().get(i)));
                }
                mGroupInfo.setMemberAdminDetails(members);
                mMemberAdminView.setContent(members.size()+"人");
                mMemberAdminAdapter.setDataSource(mGroupInfo);

            }
        });
    }

    private String convertGroupText(String groupType) {
        String groupText = "";
        if (TextUtils.isEmpty(groupType)) {
            return groupText;
        }
        if (TextUtils.equals(groupType, IMKitConstants.GroupType.TYPE_PRIVATE)
                || TextUtils.equals(groupType, IMKitConstants.GroupType.TYPE_WORK)) {
            groupText = "讨论组";
        } else if (TextUtils.equals(groupType, IMKitConstants.GroupType.TYPE_PUBLIC)) {
            groupText = "公开群";
        } else if (TextUtils.equals(groupType, IMKitConstants.GroupType.TYPE_CHAT_ROOM)
                || TextUtils.equals(groupType, IMKitConstants.GroupType.TYPE_MEETING)) {
            groupText = "聊天室";
        }
        return groupText;
    }

    public void onGroupInfoModified(Object value, int type) {
        switch (type) {
            case IMKitConstants.Group.MODIFY_GROUP_NAME:
                ToastUtil.success(getContext(),getResources().getString(R.string.modify_group_name_success));
                mGroupNameView.setContent(value.toString());
                break;
            case IMKitConstants.Group.MODIFY_GROUP_NOTICE:
                mGroupNotice.setContent(value.toString());
                ToastUtil.success(getContext(),getResources().getString(R.string.modify_group_notice_success));
                break;
            case IMKitConstants.Group.MODIFY_GROUP_JOIN_TYPE:
                mJoinTypeView.setContent(mJoinTypes.get((Integer) value));
                break;
            case IMKitConstants.Group.MODIFY_MEMBER_NAME:
                ToastUtil.success(getContext(),getResources().getString(R.string.modify_nickname_success));
                mNickView.setContent(value.toString());
                break;
        }
    }

    public void setRouter(IGroupMemberRouter listener) {
        mMemberPreviewListener = listener;
        mMemberAdapter.setManagerCallBack(listener);
        mMemberAdminAdapter.setManagerCallBack(new IGroupMemberRouter() {
            @Override
            public void forwardListMember(GroupInfo info) {

            }

            @Override
            public void forwardItemMember(GroupMemberInfo info) {

            }

            @Override
            public void forwardAddMember(GroupInfo info) {
                GroupMemberRemindActivity.startGroupMemberRemind(getContext(), mGroupInfo.getId(), "添加管理员", new SelectionActivity.OnResultReturnListener() {
                    @Override
                    public void onReturn(Object res) {
                        GroupMemberInfo memberInfo = (GroupMemberInfo) res;
                        V2TIMManager.getGroupManager().setGroupMemberRole(mGroupInfo.getId(), memberInfo.getAccount(), V2TIMGroupMemberFullInfo.V2TIM_GROUP_MEMBER_ROLE_ADMIN, new V2TIMCallback() {
                            @Override
                            public void onError(int i, String s) {
                                SLog.e(i+">>"+s);
                            }

                            @Override
                            public void onSuccess() {
                                ToastUtil.success(getContext(),"管理员设置成功");
                                loadAdmin();
                            }
                        });
                    }
                });
            }

            @Override
            public void forwardDeleteMember(GroupInfo info) {
                GroupMemberRemindActivity.startGroupMemberRemind(getContext(), mGroupInfo.getId(), "移除管理员",V2TIMGroupMemberFullInfo.V2TIM_GROUP_MEMBER_ROLE_MEMBER, new SelectionActivity.OnResultReturnListener() {
                    @Override
                    public void onReturn(Object res) {
                        GroupMemberInfo memberInfo = (GroupMemberInfo) res;
                        V2TIMManager.getGroupManager().setGroupMemberRole(mGroupInfo.getId(), memberInfo.getAccount(), V2TIMGroupMemberFullInfo.V2TIM_GROUP_MEMBER_ROLE_MEMBER, new V2TIMCallback() {
                            @Override
                            public void onError(int i, String s) {
                                SLog.e(i+">>"+s);
                            }

                            @Override
                            public void onSuccess() {
                                ToastUtil.success(getContext(),"移除管理员成功");
                                loadAdmin();
                            }
                        });
                    }
                });
            }
        });
    }

    @Override
    public void setDataSource(GroupInfo dataSource) {

    }

    @Override
    public TitleBarLayout getTitleBar() {
        return mTitleBar;
    }

    @Override
    public void setParentLayout(Object parent) {

    }

}
