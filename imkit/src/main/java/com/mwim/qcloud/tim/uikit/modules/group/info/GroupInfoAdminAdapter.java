package com.mwim.qcloud.tim.uikit.modules.group.info;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.mwim.qcloud.tim.uikit.R;
import com.mwim.qcloud.tim.uikit.TUIKit;
import com.mwim.qcloud.tim.uikit.component.picture.imageEngine.impl.GlideEngine;
import com.mwim.qcloud.tim.uikit.modules.group.member.GroupMemberInfo;
import com.mwim.qcloud.tim.uikit.modules.group.member.IGroupMemberRouter;
import com.mwim.qcloud.tim.uikit.utils.BackgroundTasks;

import java.util.ArrayList;
import java.util.List;


public class GroupInfoAdminAdapter extends BaseAdapter {

    private static final int ADD_TYPE = -100;
    private static final int DEL_TYPE = -101;
    private static final int OWNER_PRIVATE_MAX_LIMIT = 10;  //讨论组,owner可以添加成员和删除成员，
    private static final int OWNER_PUBLIC_MAX_LIMIT = 11;   //公开群,owner不可以添加成员，但是可以删除成员
    private static final int OWNER_CHATROOM_MAX_LIMIT = 11; //聊天室,owner不可以添加成员，但是可以删除成员
    private static final int NORMAL_PRIVATE_MAX_LIMIT = 11; //讨论组,普通人可以添加成员
    private static final int NORMAL_PUBLIC_MAX_LIMIT = 12;  //公开群,普通人没有权限添加成员和删除成员
    private static final int NORMAL_CHATROOM_MAX_LIMIT = 12; //聊天室,普通人没有权限添加成员和删除成员

    private List<GroupMemberInfo> mGroupMembers = new ArrayList<>();
    private IGroupMemberRouter mTailListener;
    private GroupInfo mGroupInfo;

    public void setManagerCallBack(IGroupMemberRouter listener) {
        mTailListener = listener;
    }

    @Override
    public int getCount() {
        return mGroupMembers.size();
    }

    @Override
    public GroupMemberInfo getItem(int i) {
        return mGroupMembers.get(i);
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(final int i, View view, final ViewGroup viewGroup) {
        MyViewHolder holder;
        if (view == null) {
            view = LayoutInflater.from(TUIKit.getAppContext()).inflate(R.layout.group_member_adpater, viewGroup, false);
            holder = new MyViewHolder();
            holder.memberIcon = view.findViewById(R.id.group_member_icon);
            holder.memberName = view.findViewById(R.id.group_member_name);
            view.setTag(holder);
        } else {
            holder = (MyViewHolder) view.getTag();
        }
        final GroupMemberInfo info = getItem(i);
        if (!TextUtils.isEmpty(info.getIconUrl())) {
            GlideEngine.loadCornerAvatar(holder.memberIcon, info.getIconUrl());
        }else{
            GlideEngine.loadImage(holder.memberIcon,R.drawable.default_head);
        }
        if (!TextUtils.isEmpty(info.getNameCard())) {
            holder.memberName.setText(info.getNameCard());
        } else if(!TextUtils.isEmpty(info.getNickName())){
            holder.memberName.setText(info.getNickName());
        }else {
            holder.memberName.setText(info.getAccount());
        }
        view.setOnClickListener(null);
        holder.memberIcon.setBackground(null);
        if (info.getMemberType() == ADD_TYPE) {
            GlideEngine.loadImage(holder.memberIcon,R.drawable.add_group_member);
            holder.memberIcon.setBackgroundResource(R.drawable.bottom_action_border);
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mTailListener != null) {
                        mTailListener.forwardAddMember(mGroupInfo);
                    }
                }
            });
        } else if (info.getMemberType() == DEL_TYPE) {
            GlideEngine.loadImage(holder.memberIcon,R.drawable.del_group_member);
            holder.memberIcon.setBackgroundResource(R.drawable.bottom_action_border);
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mTailListener != null) {
                        mTailListener.forwardDeleteMember(mGroupInfo);
                    }
                }
            });
        }

        return view;
    }

    public void setDataSource(GroupInfo info) {
        mGroupInfo = info;
        mGroupMembers.clear();
        List<GroupMemberInfo> members = info.getMemberAdminDetails();
        if (members != null) {
            mGroupMembers.addAll(members);
            if(mGroupInfo.isOwner()){
                GroupMemberInfo add = new GroupMemberInfo();
                add.setMemberType(ADD_TYPE);
                mGroupMembers.add(add);

                GroupMemberInfo del = new GroupMemberInfo();
                del.setMemberType(DEL_TYPE);
                mGroupMembers.add(del);
            }
            BackgroundTasks.getInstance().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    notifyDataSetChanged();
                }
            });
        }
    }

    private static class MyViewHolder {
        private ImageView memberIcon;
        private TextView memberName;
    }


}
