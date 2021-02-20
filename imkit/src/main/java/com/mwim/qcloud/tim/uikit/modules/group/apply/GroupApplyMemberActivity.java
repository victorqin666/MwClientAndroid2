package com.mwim.qcloud.tim.uikit.modules.group.apply;

import android.app.Activity;
import android.os.Bundle;

import androidx.annotation.Nullable;

import com.mwim.qcloud.tim.uikit.modules.contact.FriendProfileLayout;
import com.mwim.qcloud.tim.uikit.R;
import com.mwim.qcloud.tim.uikit.utils.IMKitConstants;

public class GroupApplyMemberActivity extends Activity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.group_apply_member_activity);
        FriendProfileLayout layout = findViewById(R.id.friend_profile);

        layout.initData(getIntent().getSerializableExtra(IMKitConstants.ProfileType.CONTENT));
    }

}
