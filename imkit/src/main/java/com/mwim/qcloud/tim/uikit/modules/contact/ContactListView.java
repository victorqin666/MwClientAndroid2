package com.mwim.qcloud.tim.uikit.modules.contact;

import android.content.Context;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import android.text.TextUtils;
import android.util.AttributeSet;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.mwim.qcloud.tim.uikit.TUIKit;
import com.tencent.imsdk.v2.V2TIMFriendInfo;
import com.tencent.imsdk.v2.V2TIMGroupInfo;
import com.tencent.imsdk.v2.V2TIMGroupMemberFullInfo;
import com.tencent.imsdk.v2.V2TIMGroupMemberInfoResult;
import com.tencent.imsdk.v2.V2TIMManager;
import com.tencent.imsdk.v2.V2TIMValueCallback;
import com.mwim.qcloud.tim.uikit.R;
import com.mwim.qcloud.tim.uikit.component.CustomLinearLayoutManager;
import com.mwim.qcloud.tim.uikit.component.indexlib.IndexBar.widget.IndexBar;
import com.mwim.qcloud.tim.uikit.component.indexlib.suspension.SuspensionDecoration;
import com.mwim.qcloud.tim.uikit.modules.group.info.GroupInfo;
import com.mwim.qcloud.tim.uikit.modules.group.member.GroupMemberInfo;
import com.mwim.qcloud.tim.uikit.utils.BackgroundTasks;
import com.mwim.qcloud.tim.uikit.utils.ThreadHelper;
import com.work.util.SLog;

import java.util.ArrayList;
import java.util.List;


public class ContactListView extends LinearLayout {

    private ContactAdapter mAdapter;
    private List<ContactItemBean> mData = new ArrayList<>();
    private SuspensionDecoration mDecoration;
    private ProgressBar mContactLoadingBar;
    private GroupInfo mGroupInfo;
    /**
     * 右侧边栏导航区域
     */
    private IndexBar mIndexBar;
    private int newFriendCount;

    public ContactListView(Context context) {
        super(context);
        init();
    }

    public ContactListView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public ContactListView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    public ContactListView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init();
    }

    private void init() {
        inflate(getContext(), R.layout.contact_list, this);
        RecyclerView mRv = findViewById(R.id.contact_member_list);
        CustomLinearLayoutManager mManager = new CustomLinearLayoutManager(getContext());
        mRv.setLayoutManager(mManager);

        mAdapter = new ContactAdapter(mData);
        mRv.setAdapter(mAdapter);
        mRv.addItemDecoration(mDecoration = new SuspensionDecoration(getContext(), mData));
        TextView mTvSideBarHint = findViewById(R.id.contact_tvSideBarHint);
        mIndexBar = findViewById(R.id.contact_indexBar);
        mIndexBar.setPressedShowTextView(mTvSideBarHint)
                .setNeedRealIndex(false)
                .setLayoutManager(mManager);
        mContactLoadingBar = findViewById(R.id.contact_loading_bar);
    }

    public ContactAdapter getAdapter() {
        return mAdapter;
    }

    public void setDataSource(List<ContactItemBean> data) {
        hideLoading();
        this.mData = data;
        mAdapter.setDataSource(mData);
        mIndexBar.setSourceDatas(mData).invalidate();
        mDecoration.setDatas(mData);
    }

    public void hideLoading(){
        mContactLoadingBar.setVisibility(GONE);
    }

    public void setSingleSelectMode(boolean mode) {
        mAdapter.setSingleSelectMode(mode);
    }

    public void setOnSelectChangeListener(OnSelectChangedListener selectChangeListener) {
        mAdapter.setOnSelectChangedListener(selectChangeListener);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        mAdapter.setOnItemClickListener(listener);
    }

    public void loadDataSource(int dataSource) {
        mContactLoadingBar.setVisibility(VISIBLE);
        mData.clear();
        switch (dataSource) {
            case DataSource.FRIEND_LIST:
                loadFriendListDataAsync();
                break;
            case DataSource.BLACK_LIST:
                loadBlackListData();
                break;
            case DataSource.GROUP_LIST:
                loadGroupListData();
                break;
            case DataSource.CONTACT_LIST:
                mData.add((ContactItemBean) new ContactItemBean(getResources().getString(R.string.new_friend))
                        .setNewFriendCount(this.newFriendCount)
                        .setTop(true).setBaseIndexTag(ContactItemBean.INDEX_STRING_TOP));
                mData.add((ContactItemBean) new ContactItemBean(getResources().getString(R.string.group)).
                        setTop(true).setBaseIndexTag(ContactItemBean.INDEX_STRING_TOP));
                mData.add((ContactItemBean) new ContactItemBean(getResources().getString(R.string.blacklist)).
                        setTop(true).setBaseIndexTag(ContactItemBean.INDEX_STRING_TOP));
                loadFriendListDataAsync();
                break;
            case DataSource.CONTACT_GROUP_SELECT:
                mData.add((ContactItemBean) new ContactItemBean(getResources().getString(R.string.group)).
                        setTop(true).setBaseIndexTag(ContactItemBean.INDEX_STRING_TOP));
                loadFriendListDataAsync();
                break;
            case DataSource.GROUP_MEMBER_LIST:
                ContactItemBean contactItemBean = new ContactItemBean(getResources().getString(R.string.at_all));
                contactItemBean.setTop(true).setBaseIndexTag(ContactItemBean.INDEX_STRING_TOP);
                mData.add(contactItemBean);
                loadGroupMembers();
                break;
            default:
                break;
        }
        mAdapter.notifyDataSetChanged();
    }

    public void newFriendCount(int count){
        this.newFriendCount = count;
        if(mData!=null && mData.size()>0 && mAdapter!=null){
            ContactItemBean contactBean = mData.get(0);
            if(TextUtils.equals(TUIKit.getAppContext().getResources().getString(R.string.new_friend), contactBean.getId())){
                contactBean.setNewFriendCount(count);
                mAdapter.notifyItemChanged(0);
            }
        }
    }

    public void setGroupInfo(GroupInfo groupInfo) {
        mGroupInfo = groupInfo;
    }

    private void updateStatus(List<ContactItemBean> beanList) {
        if (mGroupInfo == null) {
            return;
        }
        List<GroupMemberInfo> list = mGroupInfo.getMemberDetails();
        boolean needFresh = false;
        if (list.size() > 0) {
            for (GroupMemberInfo info : list) {
                for (ContactItemBean bean : beanList) {
                    if (info.getAccount().equals(bean.getId())) {
                        bean.setSelected(true);
                        bean.setEnable(false);
                        needFresh = true;
                    }
                }
            }
        }
        if (needFresh) {
            BackgroundTasks.getInstance().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    mAdapter.notifyDataSetChanged();
                }
            });
        }
    }

    private void loadFriendListDataAsync() {
        SLog.i("loadFriendListDataAsync");
        ThreadHelper.INST.execute(new Runnable() {
            @Override
            public void run() {
                // 压测时数据量比较大，query耗时比较久，所以这里使用新线程来处理
                V2TIMManager.getFriendshipManager().getFriendList(new V2TIMValueCallback<List<V2TIMFriendInfo>>() {
                    @Override
                    public void onError(int code, String desc) {
                        SLog.e("loadFriendListDataAsync err code:" + code + ", desc:" + desc);
                    }

                    @Override
                    public void onSuccess(List<V2TIMFriendInfo> v2TIMFriendInfos) {
                        if (v2TIMFriendInfos == null) {
                            v2TIMFriendInfos = new ArrayList<>();
                        }
                        SLog.i("loadFriendListDataAsync->getFriendList:" + v2TIMFriendInfos.size());
                        assembleFriendListData(v2TIMFriendInfos);
                    }
                });
            }
        });
    }

    private void assembleFriendListData(final List<V2TIMFriendInfo> timFriendInfoList) {
        for (V2TIMFriendInfo timFriendInfo : timFriendInfoList) {
            ContactItemBean info = new ContactItemBean();
            info.covertTIMFriend(timFriendInfo);
            mData.add(info);
        }
        updateStatus(mData);
        setDataSource(mData);
    }

    private void loadBlackListData() {
        SLog.i("loadBlackListData");

        V2TIMManager.getFriendshipManager().getBlackList(new V2TIMValueCallback<List<V2TIMFriendInfo>>() {
            @Override
            public void onError(int code, String desc) {
                SLog.e("getBlackList err code = " + code + ", desc = " + desc);
                mContactLoadingBar.setVisibility(GONE);
            }

            @Override
            public void onSuccess(List<V2TIMFriendInfo> v2TIMFriendInfos) {
                SLog.i("getBlackList success: " + v2TIMFriendInfos.size());
                if (v2TIMFriendInfos.size() == 0) {
                    SLog.i("getBlackList success but no data");
                }
                mData.clear();
                for (V2TIMFriendInfo timFriendInfo : v2TIMFriendInfos) {
                    ContactItemBean info = new ContactItemBean();
                    info.covertTIMFriend(timFriendInfo).setBlackList(true);
                    mData.add(info);
                }
                setDataSource(mData);
            }
        });
    }

    private void loadGroupListData() {
        SLog.i("loadGroupListData");

        V2TIMManager.getGroupManager().getJoinedGroupList(new V2TIMValueCallback<List<V2TIMGroupInfo>>() {
            @Override
            public void onError(int code, String desc) {
                SLog.e("getGroupList err code = " + code + ", desc = " + desc);
                mContactLoadingBar.setVisibility(GONE);
            }

            @Override
            public void onSuccess(List<V2TIMGroupInfo> v2TIMGroupInfos) {
                SLog.i("getGroupList success: " + v2TIMGroupInfos.size());
                if (v2TIMGroupInfos.size() == 0) {
                    SLog.i("getGroupList success but no data");
                }
                mData.clear();
                for (V2TIMGroupInfo info : v2TIMGroupInfos) {
                    ContactItemBean bean = new ContactItemBean();
                    mData.add(bean.covertTIMGroupBaseInfo(info,mAdapter));
                }
                setDataSource(mData);
            }
        });
    }

    private void loadGroupMembers() {
        V2TIMManager.getGroupManager().getGroupMemberList(mGroupInfo.getId(), V2TIMGroupMemberFullInfo.V2TIM_GROUP_MEMBER_FILTER_ALL, 0, new V2TIMValueCallback<V2TIMGroupMemberInfoResult>() {
            @Override
            public void onError(int code, String desc) {
                SLog.e("loadGroupMembers failed, code: " + code + "|desc: " + desc);
            }

            @Override
            public void onSuccess(V2TIMGroupMemberInfoResult v2TIMGroupMemberInfoResult) {
                List<V2TIMGroupMemberFullInfo> members = new ArrayList<>();
                for (int i = 0; i < v2TIMGroupMemberInfoResult.getMemberInfoList().size(); i++) {
                    //remove self
                    if (v2TIMGroupMemberInfoResult.getMemberInfoList().get(i).getUserID().equals(V2TIMManager.getInstance().getLoginUser())){
                        continue;
                    }
                    members.add(v2TIMGroupMemberInfoResult.getMemberInfoList().get(i));
                }
                for (V2TIMGroupMemberFullInfo info : members) {
                    ContactItemBean bean = new ContactItemBean();
                    mData.add(bean.covertTIMGroupMemberFullInfo(info));
                }
                setDataSource(mData);
            }
        });
    }

    public List<ContactItemBean> getGroupData() {
        return mData;
    }

    public interface OnSelectChangedListener {
        void onSelectChanged(ContactItemBean contact, boolean selected);
    }

    public interface OnItemClickListener {
        void onItemClick(int position, ContactItemBean contact);
    }

    public static class DataSource {
        public static final int UNKNOWN = -1;
        public static final int FRIEND_LIST = 1;
        public static final int BLACK_LIST = 2;
        public static final int GROUP_LIST = 3;
        public static final int CONTACT_LIST = 4;
        public static final int GROUP_MEMBER_LIST = 5;
        public static final int CONTACT_GROUP_SELECT = 6;
    }
}
