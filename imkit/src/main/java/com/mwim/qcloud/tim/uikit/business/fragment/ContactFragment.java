package com.mwim.qcloud.tim.uikit.business.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;

import com.mwim.qcloud.tim.uikit.TUIKit;
import com.mwim.qcloud.tim.uikit.base.BaseFragment;
import com.mwim.qcloud.tim.uikit.business.active.BlackListActivity;
import com.mwim.qcloud.tim.uikit.business.active.FriendProfileActivity;
import com.mwim.qcloud.tim.uikit.business.active.GroupListActivity;
import com.mwim.qcloud.tim.uikit.business.active.NewFriendActivity;
import com.mwim.qcloud.tim.uikit.modules.contact.ContactItemBean;
import com.mwim.qcloud.tim.uikit.modules.contact.ContactLayout;
import com.mwim.qcloud.tim.uikit.modules.contact.ContactListView;
import com.mwim.qcloud.tim.uikit.utils.IMKitConstants;
import com.mwim.qcloud.tim.uikit.R;


public class ContactFragment extends BaseFragment {

    private ContactLayout mContactLayout;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View baseView = inflater.inflate(R.layout.fragment_im_contact, container, false);
        initViews(baseView);
        return baseView;
    }

    private void initViews(View view) {
        // 从布局文件中获取通讯录面板
        mContactLayout = view.findViewById(R.id.contact_layout);
        mContactLayout.getContactListView().setOnItemClickListener(new ContactListView.OnItemClickListener() {
            @Override
            public void onItemClick(int position, ContactItemBean contact) {
                if (position == 0) {
                    Intent intent = new Intent(TUIKit.getAppContext(), NewFriendActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    TUIKit.getAppContext().startActivity(intent);
                } else if (position == 1) {
                    Intent intent = new Intent(TUIKit.getAppContext(), GroupListActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    TUIKit.getAppContext().startActivity(intent);
                } else if (position == 2) {
                    Intent intent = new Intent(TUIKit.getAppContext(), BlackListActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    TUIKit.getAppContext().startActivity(intent);
                } else {
                    Intent intent = new Intent(TUIKit.getAppContext(), FriendProfileActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    intent.putExtra(IMKitConstants.ProfileType.CONTENT, contact);
                    TUIKit.getAppContext().startActivity(intent);
                }
            }
        });
    }

    public void refreshData() {
        // 通讯录面板的默认UI和交互初始化
        if(mContactLayout!=null){
            mContactLayout.initDefault();
        }
    }

    public void newFriendCount(int count){
        if(mContactLayout!=null){
            mContactLayout.getContactListView().newFriendCount(count);
        }
    }
}
