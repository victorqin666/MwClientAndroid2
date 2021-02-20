package com.mwim.qcloud.tim.uikit.modules.chat;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;

import androidx.annotation.Nullable;

import android.util.AttributeSet;
import android.view.View;

import com.mwim.qcloud.tim.uikit.modules.chat.base.AbsChatLayout;
import com.mwim.qcloud.tim.uikit.modules.chat.base.ChatInfo;
import com.mwim.qcloud.tim.uikit.modules.group.apply.GroupApplyInfo;
import com.mwim.qcloud.tim.uikit.modules.group.apply.GroupApplyManagerActivity;
import com.mwim.qcloud.tim.uikit.modules.group.info.GroupInfo;
import com.mwim.qcloud.tim.uikit.modules.group.info.GroupInfoActivity;
import com.tencent.imsdk.v2.V2TIMConversation;
import com.mwim.qcloud.tim.uikit.R;
import com.mwim.qcloud.tim.uikit.base.IUIKitCallBack;
import com.mwim.qcloud.tim.uikit.component.TitleBarLayout;
import com.mwim.qcloud.tim.uikit.modules.chat.base.ChatManagerKit;
import com.mwim.qcloud.tim.uikit.utils.IMKitConstants;
import com.work.util.SharedUtils;
import com.work.util.ToastUtil;

import java.util.List;


public class ChatLayout extends AbsChatLayout implements GroupChatManagerKit.GroupNotifyHandler, C2CChatManagerKit.Chat2C2Handler {

    private GroupInfo mGroupInfo;
    private GroupChatManagerKit mGroupChatManager;
    private C2CChatManagerKit mC2CChatManager;
    private boolean isGroup = false;

    public ChatLayout(Context context) {
        super(context);
    }

    public ChatLayout(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public ChatLayout(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public void setChatInfo(ChatInfo chatInfo) {
        super.setChatInfo(chatInfo);
        if (chatInfo == null) {
            return;
        }

        isGroup = chatInfo.getType() != V2TIMConversation.V2TIM_C2C;

        if (isGroup) {
            mGroupChatManager = GroupChatManagerKit.getInstance();
            mGroupChatManager.setGroupHandler(this);
            GroupInfo groupInfo = new GroupInfo();
            groupInfo.setId(chatInfo.getId());
            groupInfo.setChatName(chatInfo.getChatName());
            mGroupChatManager.setCurrentChatInfo(groupInfo);
            mGroupInfo = groupInfo;
            loadChatMessages(null);
            loadApplyList();
            getTitleBar().getRightIcon().setImageResource(R.drawable.icon_more);
            getTitleBar().setOnRightClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (mGroupInfo != null) {
                        Intent intent = new Intent(getContext(), GroupInfoActivity.class);
                        intent.putExtra(IMKitConstants.Group.GROUP_ID, mGroupInfo.getId());
                        getContext().startActivity(intent);
                    } else {
                        ToastUtil.info(getContext(),"请稍后再试试~");
                    }
                }
            });
            mGroupApplyLayout.setOnNoticeClickListener(new OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(getContext(), GroupApplyManagerActivity.class);
                    intent.putExtra(IMKitConstants.Group.GROUP_INFO, mGroupInfo);
                    getContext().startActivity(intent);
                }
            });
            SharedUtils.removeData(groupInfo.getId());
        } else {
            getTitleBar().getRightIcon().setImageResource(R.drawable.icon_more);
            mC2CChatManager = C2CChatManagerKit.getInstance();
            mC2CChatManager.setCurrentChatInfo(chatInfo);
            mC2CChatManager.setChat2C2Handler(this);
            loadChatMessages(null);
        }
    }

    @Override
    public ChatManagerKit getChatManager() {
        if (isGroup) {
            return mGroupChatManager;
        } else {
            return mC2CChatManager;
        }
    }

    private void loadApplyList() {
        mGroupChatManager.getProvider().loadGroupApplies(new IUIKitCallBack() {
            @Override
            public void onSuccess(Object data) {
                List<GroupApplyInfo> applies = (List<GroupApplyInfo>) data;
                if (applies != null && applies.size() > 0) {
                    mGroupApplyLayout.getContent().setText(getContext().getString(R.string.group_apply_tips, applies.size()));
                    mGroupApplyLayout.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onError(String module, int errCode, String errMsg) {
                ToastUtil.error(getContext(),"loadApplyList onError: " +errCode + ">" + errMsg);
            }
        });
    }

    @Override
    public void onGroupForceExit() {
        if (getContext() instanceof Activity) {
            ((Activity) getContext()).finish();
        }
    }

    @Override
    public void onGroupNameChanged(final String newName) {
        getChatInfo().setChatName(newName);
        getTitleBar().setTitle(newName, TitleBarLayout.POSITION.MIDDLE);

    }

    @Override
    public void onApplied(int size) {
        if (size == 0) {
            mGroupApplyLayout.setVisibility(View.GONE);
        } else {
            mGroupApplyLayout.getContent().setText(getContext().getString(R.string.group_apply_tips, size));
            mGroupApplyLayout.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onChatRemarkChange(String name) {
        getChatInfo().setChatName(name);
        getTitleBar().setTitle(getChatInfo().getChatName(), TitleBarLayout.POSITION.MIDDLE);
    }
}
