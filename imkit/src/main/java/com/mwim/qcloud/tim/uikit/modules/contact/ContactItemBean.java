package com.mwim.qcloud.tim.uikit.modules.contact;

import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;

import com.mwim.qcloud.tim.uikit.R;
import com.mwim.qcloud.tim.uikit.TUIKit;
import com.mwim.qcloud.tim.uikit.config.TUIKitConfigs;
import com.tencent.imsdk.v2.V2TIMFriendInfo;
import com.tencent.imsdk.v2.V2TIMGroupInfo;
import com.mwim.qcloud.tim.uikit.component.indexlib.IndexBar.bean.BaseIndexPinyinBean;
import com.tencent.imsdk.v2.V2TIMGroupMemberFullInfo;
import com.tencent.imsdk.v2.V2TIMGroupMemberInfoResult;
import com.tencent.imsdk.v2.V2TIMManager;
import com.tencent.imsdk.v2.V2TIMValueCallback;
import com.work.util.SLog;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import static com.mwim.qcloud.tim.uikit.modules.conversation.ConversationManagerKit.SP_IMAGE;

public class ContactItemBean extends BaseIndexPinyinBean {

    public static final String INDEX_STRING_TOP = "↑";
    private String id;
    private boolean isTop;//是否是最上面的 不需要被转化成拼音的
    private boolean isSelected;
    private boolean isBlackList;
    private String remark;
    private String nickname;
    private String avatarurl;
    private List<Object> iconUrlList;
    private boolean isGroup;
    private boolean isFriend = true;
    private boolean isEnable = true;
    private boolean isSystemContacts;
    private String systemRemark;
    private int userType;
    private String mobile;
    private List<String> area;
    private int newFriendCount;

    public int getNewFriendCount() {
        return newFriendCount;
    }

    public ContactItemBean setNewFriendCount(int newFriendCount) {
        this.newFriendCount = newFriendCount;
        return this;
    }

    public List<String> getArea() {
        return area;
    }

    public void setArea(List<String> area) {
        this.area = area;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public int getUserType() {
        return userType;
    }

    public void setUserType(int userType) {
        this.userType = userType;
    }

    public static String getIndexStringTop() {
        return INDEX_STRING_TOP;
    }

    public boolean isSystemContacts() {
        return isSystemContacts;
    }

    public String getSystemRemark() {
        return systemRemark;
    }

    public void setSystemRemark(String systemRemark) {
        this.systemRemark = systemRemark;
    }

    public ContactItemBean() {
    }

    public void setSystemContacts(boolean systemContacts) {
        isSystemContacts = systemContacts;
    }

    public ContactItemBean(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public ContactItemBean setId(String id) {
        this.id = id;
        return this;
    }

    public boolean isTop() {
        return isTop;
    }

    public ContactItemBean setTop(boolean top) {
        isTop = top;
        return this;
    }

    @Override
    public String getTarget() {
        if (!TextUtils.isEmpty(remark)) {
            return remark;
        }
        if (!TextUtils.isEmpty(nickname)) {
            return nickname;
        }
        return id;
    }

    @Override
    public boolean isNeedToPinyin() {
        return !isTop;
    }

    @Override
    public boolean isShowSuspension() {
        return !isTop;
    }

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }

    public boolean isEnable() {
        return isEnable;
    }

    public void setEnable(boolean enable) {
        isEnable = enable;
    }

    public boolean isBlackList() {
        return isBlackList;
    }

    public void setBlackList(boolean blacklist) {
        isBlackList = blacklist;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public ContactItemBean covertTIMFriend(V2TIMFriendInfo friendInfo) {
        if (friendInfo == null) {
            return this;
        }
        setId(friendInfo.getUserID());
        setRemark(friendInfo.getFriendRemark());
        setNickname(friendInfo.getUserProfile().getNickName());
        setAvatarurl(friendInfo.getUserProfile().getFaceUrl());
        List<Object> list = new ArrayList<>();
        list.add(friendInfo.getUserProfile().getFaceUrl());
        setIconUrlList(list);
        return this;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public boolean isGroup() {
        return isGroup;
    }

    public void setGroup(boolean group) {
        isGroup = group;
    }

    public boolean isFriend() {
        return isFriend;
    }

    public void setFriend(boolean friend) {
        isFriend = friend;
    }

    public String getAvatarurl() {
        return avatarurl;
    }

    public void setAvatarurl(String avatarurl) {
        this.avatarurl = avatarurl;
    }

    public List<Object> getIconUrlList() {
        return iconUrlList;
    }

    public void setIconUrlList(List<Object> iconUrlList) {
        this.iconUrlList = iconUrlList;
    }

    public ContactItemBean covertTIMGroupMemberFullInfo(V2TIMGroupMemberFullInfo member) {
        if (member == null) {
            return this;
        }
        setId(member.getUserID());
        if(TextUtils.isEmpty(member.getNickName())){
            setRemark(member.getNameCard());
            setNickname(member.getNameCard());
        }else{
            setRemark(member.getNickName());
            setNickname(member.getNickName());
        }
        setAvatarurl(member.getFaceUrl());
        List<Object> list = new ArrayList<>();
        list.add(member.getFaceUrl());
        setIconUrlList(list);
        setGroup(false);
        return this;
    }

    public ContactItemBean covertTIMGroupBaseInfo(V2TIMGroupInfo group,ContactAdapter mAdapter) {
        if (group == null) {
            return this;
        }
        setId(group.getGroupID());
        setRemark(group.getGroupName());
//        setAvatarurl(group.getFaceUrl());
        setGroup(true);
        if (TextUtils.isEmpty(group.getFaceUrl())) {
            final String savedIcon = getGroupConversationAvatar(group.getGroupID());
            if (TextUtils.isEmpty(savedIcon)) {
                fillFaceUrlList(group.getGroupID(), this,mAdapter);
            } else {
                List<Object> list = new ArrayList<>();
                list.add(savedIcon);
                setIconUrlList(list);
            }
        } else {
            List<Object> list = new ArrayList<>();
            list.add(group.getFaceUrl());
            setIconUrlList(list);
        }
        return this;
    }
    public String getGroupConversationAvatar(String groupId) {
        SharedPreferences sp = TUIKit.getAppContext().getSharedPreferences(
                TUIKitConfigs.getConfigs().getGeneralConfig().getSDKAppId() + SP_IMAGE, Context.MODE_PRIVATE);
        final String savedIcon = sp.getString(groupId, "");
        if (!TextUtils.isEmpty(savedIcon) && new File(savedIcon).isFile() && new File(savedIcon).exists()) {
            return savedIcon;
        }
        return "";
    }
    private void fillFaceUrlList(final String groupID, final ContactItemBean info, final ContactAdapter mAdapter) {
        V2TIMManager.getGroupManager().getGroupMemberList(groupID, V2TIMGroupMemberFullInfo.V2TIM_GROUP_MEMBER_FILTER_ALL, 0, new V2TIMValueCallback<V2TIMGroupMemberInfoResult>() {
            @Override
            public void onError(int code, String desc) {
                SLog.e( "getGroupMemberList failed! groupID:" + groupID + "|code:" + code + "|desc: " + desc);
            }

            @Override
            public void onSuccess(V2TIMGroupMemberInfoResult v2TIMGroupMemberInfoResult) {
                List<V2TIMGroupMemberFullInfo> v2TIMGroupMemberFullInfoList = v2TIMGroupMemberInfoResult.getMemberInfoList();
                int faceSize = v2TIMGroupMemberFullInfoList.size() > 9 ? 9 : v2TIMGroupMemberFullInfoList.size();
                List<Object> urlList = new ArrayList<>();
                for (int i = 0; i < faceSize; i++) {
                    V2TIMGroupMemberFullInfo v2TIMGroupMemberFullInfo = v2TIMGroupMemberFullInfoList.get(i);
                    if (TextUtils.isEmpty(v2TIMGroupMemberFullInfo.getFaceUrl())) {
                        urlList.add(R.drawable.default_head);
                    } else {
                        urlList.add(v2TIMGroupMemberFullInfo.getFaceUrl());
                    }
                }
                info.setIconUrlList(urlList);
                mAdapter.notifyDataSetChanged();
            }
        });
    }
}
