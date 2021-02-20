package com.mwim.qcloud.tim.uikit.modules.group.info;

import android.app.Activity;

import com.mwim.qcloud.tim.uikit.TUIKit;
import com.mwim.qcloud.tim.uikit.base.IUIKitCallBack;
import com.mwim.qcloud.tim.uikit.utils.IMKitConstants;
import com.work.util.SLog;
import com.work.util.ToastUtil;


public class GroupInfoPresenter {

    private GroupInfoLayout mInfoLayout;
    private GroupInfoProvider mProvider;

    public GroupInfoPresenter(GroupInfoLayout layout) {
        this.mInfoLayout = layout;
        mProvider = new GroupInfoProvider();
    }

    public void loadGroupInfo(String groupId, final IUIKitCallBack callBack) {
        mProvider.loadGroupInfo(groupId, new IUIKitCallBack() {
            @Override
            public void onSuccess(Object data) {
                callBack.onSuccess(data);
            }

            @Override
            public void onError(String module, int errCode, String errMsg) {
                SLog.e(errCode + ":" + errMsg);
                callBack.onError(module, errCode, errMsg);
                ToastUtil.error(TUIKit.getAppContext(),errMsg);
            }
        });
    }

    public void modifyGroupName(final String name) {
        mProvider.modifyGroupInfo(name, IMKitConstants.Group.MODIFY_GROUP_NAME, new IUIKitCallBack() {
            @Override
            public void onSuccess(Object data) {
                mInfoLayout.onGroupInfoModified(name, IMKitConstants.Group.MODIFY_GROUP_NAME);
            }

            @Override
            public void onError(String module, int errCode, String errMsg) {
                SLog.e(errCode + ":" + errMsg);
                ToastUtil.info(TUIKit.getAppContext(),errMsg);
            }
        });
    }

    public void modifyGroupNotice(final String notice) {
        mProvider.modifyGroupInfo(notice, IMKitConstants.Group.MODIFY_GROUP_NOTICE, new IUIKitCallBack() {
            @Override
            public void onSuccess(Object data) {
                mInfoLayout.onGroupInfoModified(notice, IMKitConstants.Group.MODIFY_GROUP_NOTICE);
            }

            @Override
            public void onError(String module, int errCode, String errMsg) {
                SLog.e(errCode + ":" + errMsg);
                ToastUtil.info(TUIKit.getAppContext(),errMsg);
            }
        });
    }


    public String getNickName() {
        String nickName = "";
        if (mProvider.getSelfGroupInfo() != null) {
            nickName = mProvider.getSelfGroupInfo().getNameCard();
        }
        return nickName == null ? "" : nickName;
    }

    public void modifyMyGroupNickname(final String nickname) {
        mProvider.modifyMyGroupNickname(nickname, new IUIKitCallBack() {
            @Override
            public void onSuccess(Object data) {
                mInfoLayout.onGroupInfoModified(nickname, IMKitConstants.Group.MODIFY_MEMBER_NAME);
            }

            @Override
            public void onError(String module, int errCode, String errMsg) {
                SLog.e(errCode + ":" + errMsg);
                ToastUtil.error(TUIKit.getAppContext(),errMsg);
            }
        });
    }

    public void deleteGroup() {
        mProvider.deleteGroup(new IUIKitCallBack() {
            @Override
            public void onSuccess(Object data) {
                ((Activity) mInfoLayout.getContext()).finish();
            }

            @Override
            public void onError(String module, int errCode, String errMsg) {
                SLog.e( errCode + ":" + errMsg);
                ToastUtil.error(TUIKit.getAppContext(),errMsg);
            }
        });
    }

    public void setTopConversation(boolean flag) {
        mProvider.setTopConversation(flag);
    }

    public void quitGroup() {
        mProvider.quitGroup(new IUIKitCallBack() {
            @Override
            public void onSuccess(Object data) {
                ((Activity) mInfoLayout.getContext()).finish();
            }

            @Override
            public void onError(String module, int errCode, String errMsg) {
                ((Activity) mInfoLayout.getContext()).finish();
                SLog.e( errCode + ":" + errMsg);
            }
        });
    }

    public void modifyGroupInfo(int value, int type) {
        mProvider.modifyGroupInfo(value, type, new IUIKitCallBack() {
            @Override
            public void onSuccess(Object data) {
                mInfoLayout.onGroupInfoModified(data, IMKitConstants.Group.MODIFY_GROUP_JOIN_TYPE);
            }

            @Override
            public void onError(String module, int errCode, String errMsg) {
                ToastUtil.error(TUIKit.getAppContext(),"modifyGroupInfo fail :" + errCode + "=" + errMsg);
            }
        });
    }
}
