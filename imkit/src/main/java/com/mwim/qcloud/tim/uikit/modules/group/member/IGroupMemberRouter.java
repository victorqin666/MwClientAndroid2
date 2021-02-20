package com.mwim.qcloud.tim.uikit.modules.group.member;

import com.mwim.qcloud.tim.uikit.modules.group.info.GroupInfo;

public interface IGroupMemberRouter {

    void forwardListMember(GroupInfo info);

    void forwardItemMember(GroupMemberInfo info);

    void forwardAddMember(GroupInfo info);

    void forwardDeleteMember(GroupInfo info);
}
