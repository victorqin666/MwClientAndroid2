package com.mwim.qcloud.tim.uikit.modules.group.info;


import com.mwim.qcloud.tim.uikit.R;
import com.mwim.qcloud.tim.uikit.base.BaseActivity;

public class GroupInfoActivity extends BaseActivity {

    @Override
    public void onInitView() throws Exception {
        super.onInitView();
        GroupInfoFragment fragment = new GroupInfoFragment();
        fragment.setArguments(getIntent().getExtras());
        getSupportFragmentManager().beginTransaction().replace(R.id.group_manager_base, fragment).commitAllowingStateLoss();
    }

    @Override
    public int onCustomContentId() {
        return R.layout.group_info_activity;
    }

    @Override
    public void finish() {
        super.finish();
        setResult(1001);
    }

    @Override
    public boolean isShowTitleBar() {
        return false;
    }
}
