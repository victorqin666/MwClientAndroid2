package com.mwim.qcloud.tim.uikit.business.active;

import android.Manifest;
import android.content.Intent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.divider.HorizontalDividerItemDecoration;
import com.mwim.qcloud.tim.uikit.TUIKit;
import com.mwim.qcloud.tim.uikit.business.adapter.NewFriendListAdapter;
import com.mwim.qcloud.tim.uikit.business.dialog.ConfirmDialog;
import com.mwim.qcloud.tim.uikit.utils.IMKitConstants;
import com.tencent.imsdk.v2.V2TIMFriendApplication;
import com.tencent.imsdk.v2.V2TIMFriendApplicationResult;
import com.tencent.imsdk.v2.V2TIMManager;
import com.tencent.imsdk.v2.V2TIMValueCallback;
import com.mwim.qcloud.tim.uikit.R;
import com.work.util.SLog;
import com.workstation.permission.PermissionsResultAction;

import java.util.ArrayList;
import java.util.List;

public class NewFriendActivity extends IMBaseActivity implements BaseQuickAdapter.OnItemClickListener, View.OnClickListener {

    private RecyclerView mNewFriendLv;
    private NewFriendListAdapter mAdapter;
    private TextView mEmptyView;
    private final List<V2TIMFriendApplication> mList = new ArrayList<>();

    @Override
    public void onInitView() throws Exception {
        super.onInitView();
        mNewFriendLv = findViewById(R.id.new_friend_list);
        mEmptyView = findViewById(R.id.empty_text);
        findViewById(R.id.add_new_contacts_phone).setOnClickListener(this);
    }

    @Override
    public void onInitValue() throws Exception {
        super.onInitValue();
        mNewFriendLv.setLayoutManager(new LinearLayoutManager(this));
        mNewFriendLv.addItemDecoration(new HorizontalDividerItemDecoration.Builder(this).colorResId(R.color.background_color).build());
        setTitleName(R.string.new_friend);
    }

    @Override
    protected void onResume() {
        super.onResume();
        initPendency();
    }

    @Override
    public int onCustomContentId() {
        return R.layout.activity_im_contact_new_friend;
    }

    @Override
    public View onCustomTitleRight(TextView view) {
        ImageView imageView = new ImageView(this);
        imageView.setImageResource(R.drawable.icon_add_contact_stroke);
        return imageView;
    }

    @Override
    public void onRightClickListener(View view) {
        super.onRightClickListener(view);
        Intent intent = new Intent(TUIKit.getAppContext(), AddMoreActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra("isGroup", false);
        startActivity(intent);
    }

    private void initPendency() {
        V2TIMManager.getFriendshipManager().getFriendApplicationList(new V2TIMValueCallback<V2TIMFriendApplicationResult>() {
            @Override
            public void onError(int code, String desc) {
                SLog.e("getPendencyList err code = " + code + ", desc = " + desc);
            }

            @Override
            public void onSuccess(V2TIMFriendApplicationResult v2TIMFriendApplicationResult) {
                SLog.e("getFriendApplicationList success");
                if (v2TIMFriendApplicationResult.getFriendApplicationList() != null) {
                    if (v2TIMFriendApplicationResult.getFriendApplicationList().size() == 0) {
                        mEmptyView.setText(getResources().getString(R.string.no_friend_apply));
                        mNewFriendLv.setVisibility(View.GONE);
                        mEmptyView.setVisibility(View.VISIBLE);
                        return;
                    }
                }
                mNewFriendLv.setVisibility(View.VISIBLE);
                mList.clear();
                mList.addAll(v2TIMFriendApplicationResult.getFriendApplicationList());
                mAdapter = new NewFriendListAdapter(mList);
                mAdapter.setOnItemClickListener(NewFriendActivity.this);
                mNewFriendLv.setAdapter(mAdapter);
                mAdapter.notifyDataSetChanged();
            }
        });
    }


    @Override
    public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
        V2TIMFriendApplication item = mAdapter.getItem(position);
        if(item!=null && item.getType() == V2TIMFriendApplication.V2TIM_FRIEND_APPLICATION_COME_IN){
            Intent intent = new Intent(this, FriendProfileActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.putExtra(IMKitConstants.ProfileType.CONTENT, item);
            startActivity(intent);
        }
    }

    @Override
    public void onClick(View view) {
        intentAddressBook();
    }
    /**
     * 跳转到系统通讯录
     */
    private void intentAddressBook(){
        String[] Permission = { Manifest.permission.READ_CONTACTS};
        final Intent intent = new Intent(this, SystemContactActivity.class);
        if(hasPermission(Permission)){
            startActivityForResult(intent,0);
        }else{
            onPermissionChecker(Permission, new PermissionsResultAction() {
                @Override
                public void onGranted() {
                    startActivityForResult(intent,0);
                }

                @Override
                public void onDenied(String permission) {
                    showMissingPermissionDialog();
                }
            });
        }
    }
    // 显示缺失权限提示
    public void showMissingPermissionDialog() {
        new ConfirmDialog()
                .setContent(R.string.tips_permissions)
                .setHiddenCancel(true)
                .show(getSupportFragmentManager(),"permission");
    }
}
