package com.mwim.qcloud.tim.uikit.modules.group.member;

import android.app.Activity;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.mwim.qcloud.tim.uikit.R;
import com.mwim.qcloud.tim.uikit.TUIKit;
import com.mwim.qcloud.tim.uikit.base.BaseActivity;
import com.mwim.qcloud.tim.uikit.component.SelectionActivity;
import com.mwim.qcloud.tim.uikit.component.picture.imageEngine.impl.GlideEngine;
import com.mwim.qcloud.tim.uikit.utils.BackgroundTasks;

import java.util.ArrayList;
import java.util.List;


public class GroupMemberRemindAdapter extends BaseAdapter {

    private List<GroupMemberInfo> mGroupMembers = new ArrayList<>();
    private SelectionActivity.OnResultReturnListener onResultReturnListener;
    private BaseActivity mActivity;

    public GroupMemberRemindAdapter() {
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
            view = LayoutInflater.from(TUIKit.getAppContext()).inflate(R.layout.group_member_remind_adpater, viewGroup, false);
            holder = new MyViewHolder();
            holder.memberIcon = view.findViewById(R.id.group_member_icon);
            holder.memberName = view.findViewById(R.id.group_member_name);
            view.setTag(holder);
        } else {
            holder = (MyViewHolder) view.getTag();
        }
        final GroupMemberInfo info = getItem(i);
        if (!TextUtils.isEmpty(info.getIconUrl())){
            GlideEngine.loadCornerAvatar(holder.memberIcon, info.getIconUrl());
        }else{
            GlideEngine.loadImage(holder.memberIcon, R.drawable.default_head);
        }
        if(!TextUtils.isEmpty(info.getNameCard())){
            holder.memberName.setText(info.getNameCard());
        }else if(!TextUtils.isEmpty(info.getNickName())){
            holder.memberName.setText(info.getNickName());
        }else{
            holder.memberName.setText(info.getAccount());
        }
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onResultReturnListener.onReturn(info);
                mActivity.finish();
            }
        });
        return view;
    }

    public void setDataSource(List<GroupMemberInfo> members) {
        if (members != null) {
            this.mGroupMembers = members;
            BackgroundTasks.getInstance().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    notifyDataSetChanged();
                }
            });
        }
    }

    public void setOnResultReturnListener(SelectionActivity.OnResultReturnListener onResultReturnListener) {
        this.onResultReturnListener = onResultReturnListener;
    }

    public void setActivity(BaseActivity mActivity) {
        this.mActivity = mActivity;
    }

    private static class MyViewHolder {
        private ImageView memberIcon;
        private TextView memberName;
    }
}
