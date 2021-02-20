package com.mwim.qcloud.tim.uikit.modules.chat;

import android.text.TextUtils;

import com.google.gson.Gson;
import com.http.network.listener.OnResultDataListener;
import com.http.network.model.RequestWork;
import com.http.network.model.ResponseWork;
import com.mwim.qcloud.tim.uikit.TUIKit;
import com.mwim.qcloud.tim.uikit.business.modal.UserApi;
import com.mwim.qcloud.tim.uikit.modules.chat.base.ChatInfo;
import com.mwim.qcloud.tim.uikit.modules.conversation.ConversationManagerKit;
import com.mwim.qcloud.tim.uikit.modules.group.apply.GroupApplyInfo;
import com.mwim.qcloud.tim.uikit.modules.group.info.GroupInfo;
import com.mwim.qcloud.tim.uikit.modules.group.info.GroupInfoProvider;
import com.mwim.qcloud.tim.uikit.modules.group.member.GroupMemberInfo;
import com.mwim.qcloud.tim.uikit.modules.message.MessageCustom;
import com.mwim.qcloud.tim.uikit.modules.message.MessageInfo;
import com.mwim.qcloud.tim.uikit.modules.message.MessageInfoUtil;
import com.tencent.imsdk.v2.V2TIMGroupChangeInfo;
import com.tencent.imsdk.v2.V2TIMGroupInfo;
import com.tencent.imsdk.v2.V2TIMGroupInfoResult;
import com.tencent.imsdk.v2.V2TIMGroupMemberInfo;
import com.tencent.imsdk.v2.V2TIMGroupTipsElem;
import com.tencent.imsdk.v2.V2TIMManager;
import com.tencent.imsdk.v2.V2TIMMessage;
import com.tencent.imsdk.v2.V2TIMSendCallback;
import com.mwim.qcloud.tim.uikit.base.IUIKitCallBack;
import com.mwim.qcloud.tim.uikit.modules.chat.base.ChatManagerKit;
import com.mwim.qcloud.tim.uikit.utils.IMKitConstants;
import com.work.api.open.Yz;
import com.work.api.open.model.CreateGroupReq;
import com.work.api.open.model.CreateGroupResp;
import com.work.api.open.model.client.OpenData;
import com.work.api.open.model.client.OpenGroupInfo;
import com.work.api.open.model.client.OpenGroupMember;
import com.work.util.SLog;
import com.work.util.ToastUtil;

import java.util.ArrayList;
import java.util.List;

public class GroupChatManagerKit extends ChatManagerKit {

    private static GroupChatManagerKit mKit;
    private GroupInfo mCurrentChatInfo;
    private List<GroupApplyInfo> mCurrentApplies = new ArrayList<>();
    private List<GroupMemberInfo> mCurrentGroupMembers = new ArrayList<>();
    private GroupNotifyHandler mGroupHandler;
    private GroupInfoProvider mGroupInfoProvider;

    private GroupChatManagerKit() {
        init();
    }

    public static GroupChatManagerKit getInstance() {
        if (mKit == null) {
            mKit = new GroupChatManagerKit();
        }
        return mKit;
    }

    private static void sendTipsMessage(String groupID, V2TIMMessage message, final IUIKitCallBack callBack) {
        V2TIMManager.getMessageManager().sendMessage(message, null, groupID, V2TIMMessage.V2TIM_PRIORITY_DEFAULT, false, null, new V2TIMSendCallback<V2TIMMessage>() {
            @Override
            public void onProgress(int progress) {

            }

            @Override
            public void onError(int code, String desc) {
                SLog.e("sendTipsMessage fail:" + code + "=" + desc);
            }

            @Override
            public void onSuccess(V2TIMMessage v2TIMMessage) {
                SLog.i("sendTipsMessage onSuccess");
                if (callBack != null) {
                    callBack.onSuccess(v2TIMMessage);
                }
            }
        });
    }

    public static void createGroupChat(final GroupInfo chatInfo, final IUIKitCallBack callBack) {
        V2TIMGroupInfo v2TIMGroupInfo = new V2TIMGroupInfo();
        v2TIMGroupInfo.setGroupType(chatInfo.getGroupType());
        v2TIMGroupInfo.setGroupName(chatInfo.getGroupName());
        v2TIMGroupInfo.setGroupAddOpt(chatInfo.getJoinType());

//        List<V2TIMCreateGroupMemberInfo> v2TIMCreateGroupMemberInfoList = new ArrayList<>();
        List<OpenGroupMember> groupMembers = new ArrayList<>();
        for (int i = 0; i < chatInfo.getMemberDetails().size(); i++) {
            GroupMemberInfo groupMemberInfo = chatInfo.getMemberDetails().get(i);
//            V2TIMCreateGroupMemberInfo v2TIMCreateGroupMemberInfo = new V2TIMCreateGroupMemberInfo();
//            v2TIMCreateGroupMemberInfo.setUserID(groupMemberInfo.getAccount());
//            v2TIMCreateGroupMemberInfoList.add(v2TIMCreateGroupMemberInfo);
            OpenGroupMember openGroupMember = new OpenGroupMember();
            openGroupMember.Member_Account = groupMemberInfo.getAccount();
            groupMembers.add(openGroupMember);
        }
        CreateGroupReq createGroupReq = new CreateGroupReq();
        createGroupReq.Name = chatInfo.getGroupName();
        createGroupReq.Owner_Account = UserApi.instance().getUserId();
        createGroupReq.MemberList = groupMembers;
        Yz.getSession().createGroup(createGroupReq, new OnResultDataListener() {
            @Override
            public void onResult(RequestWork req, ResponseWork resp) {

                if(resp.isSuccess() && resp instanceof CreateGroupResp){
                    OpenData openData = ((CreateGroupResp) resp).getData();
                    final String groupId = openData.GroupId;
                    chatInfo.setId(groupId);
                    Gson gson = new Gson();
                    MessageCustom messageCustom = new MessageCustom();
                    messageCustom.version = IMKitConstants.version;
                    messageCustom.businessID = MessageCustom.BUSINESS_ID_GROUP_CREATE;
                    messageCustom.opUser = UserApi.instance().getNickName();
                    messageCustom.content = "发起了群聊";
                    String data = gson.toJson(messageCustom);
                    V2TIMMessage createTips = MessageInfoUtil.buildGroupCustomMessage(data);
                    try {
                        Thread.sleep(200);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    sendTipsMessage(groupId, createTips, new IUIKitCallBack() {
                        @Override
                        public void onSuccess(Object data) {
                            if (callBack != null) {
                                callBack.onSuccess(groupId);
                            }
                        }

                        @Override
                        public void onError(String module, int errCode, String errMsg) {
                            SLog.e("sendTipsMessage failed, code: " + errCode + "|desc: " + errMsg);
                        }
                    });
                }else{
                    ToastUtil.warning(TUIKit.getAppContext(),resp.getMessage());
                }
            }
        });
        //原生创建的模式
//        V2TIMManager.getGroupManager().createGroup(v2TIMGroupInfo, v2TIMCreateGroupMemberInfoList, new V2TIMValueCallback<String>() {
//            @Override
//            public void onError(int code, String desc) {
//                SLog.e("createGroup failed, code: " + code + "|desc: " + desc);
//                if (callBack != null) {
//                    callBack.onError("SLog",code, desc);
//                }
//            }
//
//            @Override
//            public void onSuccess(final String groupId) {
//                chatInfo.setId(groupId);
//                Gson gson = new Gson();
//                MessageCustom messageCustom = new MessageCustom();
//                messageCustom.version = TUIKitConstants.version;
//                messageCustom.businessID = MessageCustom.BUSINESS_ID_GROUP_CREATE;
//                messageCustom.opUser = UserApi.instance().getNickName();
//                messageCustom.content = "发起了群聊";
//                String data = gson.toJson(messageCustom);
//
//                V2TIMMessage createTips = MessageInfoUtil.buildGroupCustomMessage(data);
//
//                try {
//                    Thread.sleep(200);
//                } catch (InterruptedException e) {
//                    e.printStackTrace();
//                }
//                sendTipsMessage(groupId, createTips, new IUIKitCallBack() {
//                    @Override
//                    public void onSuccess(Object data) {
//                        if (callBack != null) {
//                            callBack.onSuccess(groupId);
//                        }
//                    }
//
//                    @Override
//                    public void onError(String module, int errCode, String errMsg) {
//                        SLog.e("sendTipsMessage failed, code: " + errCode + "|desc: " + errMsg);
//                    }
//                });
//            }
//        });
    }

    @Override
    protected void init() {
        super.init();
        mGroupInfoProvider = new GroupInfoProvider();
    }

    public GroupInfoProvider getProvider() {
        return mGroupInfoProvider;
    }

    @Override
    public ChatInfo getCurrentChatInfo() {
        return mCurrentChatInfo;
    }

    @Override
    public void setCurrentChatInfo(ChatInfo info) {
        super.setCurrentChatInfo(info);

        mCurrentChatInfo = (GroupInfo) info;
        mCurrentApplies.clear();
        mCurrentGroupMembers.clear();
        mGroupInfoProvider.loadGroupInfo(mCurrentChatInfo);
    }

    @Override
    protected void addGroupMessage(MessageInfo msgInfo) {
        V2TIMGroupTipsElem groupTips;
        if (msgInfo.getMsgType() == MessageInfo.MSG_TYPE_GROUP_JOIN
                || msgInfo.getMsgType() == MessageInfo.MSG_TYPE_GROUP_QUITE
                || msgInfo.getMsgType() == MessageInfo.MSG_TYPE_GROUP_KICK
                || msgInfo.getMsgType() == MessageInfo.MSG_TYPE_GROUP_MODIFY_NAME
                || msgInfo.getMsgType() == MessageInfo.MSG_TYPE_GROUP_MODIFY_NOTICE) {
            V2TIMMessage v2TIMMessage = msgInfo.getTimMessage();
            if (v2TIMMessage.getElemType() != V2TIMMessage.V2TIM_ELEM_TYPE_GROUP_TIPS) {
                return;
            }
            groupTips = v2TIMMessage.getGroupTipsElem();
        } else {
            return;
        }
        if (msgInfo.getMsgType() == MessageInfo.MSG_TYPE_GROUP_JOIN) {
            List<V2TIMGroupMemberInfo> memberInfos = groupTips.getMemberList();
            if (memberInfos.size() > 0) {
                for (V2TIMGroupMemberInfo v2TIMGroupMemberInfo : memberInfos) {
                    GroupMemberInfo member = new GroupMemberInfo();
                    member.covertTIMGroupMemberInfo(v2TIMGroupMemberInfo);
                    mCurrentGroupMembers.add(member);
                }
            } else {
                GroupMemberInfo member = new GroupMemberInfo();
                member.covertTIMGroupMemberInfo(groupTips.getOpMember());
                mCurrentGroupMembers.add(member);
            }
            mCurrentChatInfo.setMemberDetails(mCurrentGroupMembers);
        } else if (msgInfo.getMsgType() == MessageInfo.MSG_TYPE_GROUP_QUITE || msgInfo.getMsgType() == MessageInfo.MSG_TYPE_GROUP_KICK) {
            List<V2TIMGroupMemberInfo> memberInfos = groupTips.getMemberList();
            if (memberInfos.size() > 0) {
                for (V2TIMGroupMemberInfo v2TIMGroupMemberInfo : memberInfos) {
                    String memberUserID = v2TIMGroupMemberInfo.getUserID();
                    for (int i = 0; i < mCurrentGroupMembers.size(); i++) {
                        if (mCurrentGroupMembers.get(i).getAccount().equals(memberUserID)) {
                            mCurrentGroupMembers.remove(i);
                            break;
                        }
                    }
                }
            } else {
                V2TIMGroupMemberInfo memberInfo = groupTips.getOpMember();
                for (int i = 0; i < mCurrentGroupMembers.size(); i++) {
                    if (mCurrentGroupMembers.get(i).getAccount().equals(memberInfo.getUserID())) {
                        mCurrentGroupMembers.remove(i);
                        break;
                    }
                }
            }
            mCurrentChatInfo.setMemberDetails(mCurrentGroupMembers);
        } else if (msgInfo.getMsgType() == MessageInfo.MSG_TYPE_GROUP_MODIFY_NAME || msgInfo.getMsgType() == MessageInfo.MSG_TYPE_GROUP_MODIFY_NOTICE) {
            List<V2TIMGroupChangeInfo> modifyList = groupTips.getGroupChangeInfoList();
            if (modifyList.size() > 0) {
                V2TIMGroupChangeInfo modifyInfo = modifyList.get(0);
                int modifyType = modifyInfo.getType();
                if (modifyType == V2TIMGroupChangeInfo.V2TIM_GROUP_INFO_CHANGE_TYPE_NAME) {
                    mCurrentChatInfo.setGroupName(modifyInfo.getValue());
                    if (mGroupHandler != null) {
                        mGroupHandler.onGroupNameChanged(modifyInfo.getValue());
                    }
                } else if (modifyType == V2TIMGroupChangeInfo.V2TIM_GROUP_INFO_CHANGE_TYPE_NOTIFICATION) {
                    mCurrentChatInfo.setNotice(modifyInfo.getValue());
                }
            }
        }
    }

    public void notifyJoinGroup(String groupID, final boolean isInvited) {
        if(mCurrentChatInfo!=null && !TextUtils.isEmpty(mCurrentChatInfo.getGroupName())){
            if (isInvited) {
                ToastUtil.info(TUIKit.getAppContext(),"您已被邀请进群：" + mCurrentChatInfo.getGroupName());
            } else {
                ToastUtil.info(TUIKit.getAppContext(),"您已加入群：" + mCurrentChatInfo.getGroupName());
            }
        }else{
            mGroupInfoProvider.loadGroupPublicInfo(groupID, new IUIKitCallBack() {
                @Override
                public void onSuccess(Object data) {
                    mCurrentChatInfo = new GroupInfo();
                    mCurrentChatInfo.covertTIMGroupDetailInfo((V2TIMGroupInfoResult) data);
                    if (isInvited) {
                        ToastUtil.info(TUIKit.getAppContext(),"您已被邀请进群：" + mCurrentChatInfo.getGroupName());
                    } else {
                        ToastUtil.info(TUIKit.getAppContext(),"您已加入群：" + mCurrentChatInfo.getGroupName());
                    }
                }

                @Override
                public void onError(String module, int errCode, String errMsg) {

                }
            });
        }
    }

    public void notifyJoinGroupRefused(String groupID) {
        if(mCurrentChatInfo!=null){
            ToastUtil.info(TUIKit.getAppContext(),"您被拒绝加入群：" + mCurrentChatInfo.getGroupName());
        }else{
            mGroupInfoProvider.loadGroupPublicInfo(groupID, new IUIKitCallBack() {
                @Override
                public void onSuccess(Object data) {
                    mCurrentChatInfo = new GroupInfo();
                    mCurrentChatInfo.covertTIMGroupDetailInfo((V2TIMGroupInfoResult) data);
                    ToastUtil.info(TUIKit.getAppContext(),"您被拒绝加入群：" + mCurrentChatInfo.getGroupName());
                }

                @Override
                public void onError(String module, int errCode, String errMsg) {

                }
            });
        }
    }

    public void notifyKickedFromGroup(String groupID) {
        if(mCurrentChatInfo!=null && !TextUtils.isEmpty(mCurrentChatInfo.getGroupName())){
            ToastUtil.info(TUIKit.getAppContext(),"您已被踢出群：" + mCurrentChatInfo.getGroupName());
        }else{
            mGroupInfoProvider.loadGroupPublicInfo(groupID,true, new IUIKitCallBack() {
                @Override
                public void onSuccess(Object data) {
                    if(data instanceof OpenGroupInfo && !TextUtils.isEmpty(((OpenGroupInfo) data).Name)){
                        ToastUtil.info(TUIKit.getAppContext(),"您已被踢出群：" + ((OpenGroupInfo) data).Name);
                    }
                }

                @Override
                public void onError(String module, int errCode, String errMsg) {

                }
            });
        }
        ConversationManagerKit.getInstance().deleteConversation(groupID, true);
        if (mCurrentChatInfo != null && groupID.equals(mCurrentChatInfo.getId())) {
            onGroupForceExit();
        }
    }

    public void notifyGroupDismissed(String groupID) {
        if(mCurrentChatInfo!=null && !TextUtils.isEmpty(mCurrentChatInfo.getGroupName())){
            ToastUtil.info(TUIKit.getAppContext(),"您所在的群" + mCurrentChatInfo.getGroupName() + "已解散");
        }else{
            mGroupInfoProvider.loadGroupPublicInfo(groupID,true, new IUIKitCallBack() {
                @Override
                public void onSuccess(Object data) {
                    if(data instanceof String){
                        ToastUtil.info(TUIKit.getAppContext(),"您所在的群" + data + "已解散");
                    }
                }

                @Override
                public void onError(String module, int errCode, String errMsg) {

                }
            });
        }
        if (mCurrentChatInfo != null && groupID.equals(mCurrentChatInfo.getId())) {
            onGroupForceExit();
        }
        ConversationManagerKit.getInstance().deleteConversation(groupID, true);
    }

    public void notifyGroupRESTCustomSystemData(String groupID, byte[] customData) {
        if (mCurrentChatInfo != null && groupID.equals(mCurrentChatInfo.getId())) {
            ToastUtil.info(TUIKit.getAppContext(),"收到自定义系统通知：" + new String(customData));
        }

    }

    public void onGroupForceExit() {
        if (mGroupHandler != null) {
            mGroupHandler.onGroupForceExit();
        }
    }

    @Override
    public void destroyChat() {
        super.destroyChat();
        mCurrentChatInfo = null;
        mGroupHandler = null;
        mCurrentApplies.clear();
        mCurrentGroupMembers.clear();
    }

    public void setGroupHandler(GroupNotifyHandler mGroupHandler) {
        this.mGroupHandler = mGroupHandler;
    }

    public void onApplied(int unHandledSize) {
        if (mGroupHandler != null) {
            mGroupHandler.onApplied(unHandledSize);
        }
    }

    @Override
    protected boolean isGroup() {
        return true;
    }

    @Override
    protected void assembleGroupMessage(MessageInfo message) {
        message.setGroup(true);
        message.setFromUser(V2TIMManager.getInstance().getLoginUser());
    }

    public interface GroupNotifyHandler {

        void onGroupForceExit();

        void onGroupNameChanged(String newName);

        void onApplied(int size);
    }
}
