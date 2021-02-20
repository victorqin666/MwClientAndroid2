package com.mwim.qcloud.tim.uikit.modules.group.member;

import android.content.Context;
import android.content.Intent;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.widget.EditText;
import android.widget.ListView;

import com.mwim.qcloud.tim.uikit.R;
import com.mwim.qcloud.tim.uikit.base.BaseActivity;
import com.mwim.qcloud.tim.uikit.base.IUIKitCallBack;
import com.mwim.qcloud.tim.uikit.component.SelectionActivity;
import com.mwim.qcloud.tim.uikit.modules.group.info.GroupInfo;
import com.mwim.qcloud.tim.uikit.modules.group.info.GroupInfoProvider;
import com.tencent.imsdk.v2.V2TIMGroupMemberFullInfo;
import com.tencent.imsdk.v2.V2TIMGroupMemberInfoResult;
import com.tencent.imsdk.v2.V2TIMManager;
import com.tencent.imsdk.v2.V2TIMValueCallback;
import com.work.util.SLog;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by tangyx
 * Date 2020/9/16
 * email tangyx@live.com
 */

public class GroupMemberRemindActivity extends BaseActivity implements TextWatcher {
    private final static String TITLE="title";
    private final static String ROLE="ROLE";
    private static SelectionActivity.OnResultReturnListener sOnResultReturnListener;
    private GroupMemberRemindAdapter mAdapter;
    private List<GroupMemberInfo> mData = null;
    private EditText mSearch;

    @Override
    public void onInitView() throws Exception {
        super.onInitView();
        mSearch = findViewById(R.id.search);
        ListView mMembers = findViewById(R.id.group_members);
        mAdapter = new GroupMemberRemindAdapter();
        mAdapter.setOnResultReturnListener(sOnResultReturnListener);
        mAdapter.setActivity(this);
        mMembers.setAdapter(mAdapter);
    }

    @Override
    public void onInitValue() throws Exception {
        super.onInitValue();
        mSearch.addTextChangedListener(this);
        String title = getIntent().getStringExtra(TITLE);
        if(TextUtils.isEmpty(title)){
            setTitleName(R.string.group_remind);
        }else{
            setTitleName(title);
        }
        int role = getIntent().getIntExtra(ROLE,-999);
        if(role== V2TIMGroupMemberFullInfo.V2TIM_GROUP_MEMBER_ROLE_MEMBER){
            V2TIMManager.getGroupManager().getGroupMemberList(getIntent().getStringExtra(GroupMemberRemindActivity.class.getSimpleName()), V2TIMGroupMemberFullInfo.V2TIM_GROUP_MEMBER_FILTER_ADMIN, 0, new V2TIMValueCallback<V2TIMGroupMemberInfoResult>() {
                @Override
                public void onError(int code, String desc) {
                    SLog.e("loadGroupMembers failed, code: " + code + "|desc: " + desc);
                }

                @Override
                public void onSuccess(V2TIMGroupMemberInfoResult v2TIMGroupMemberInfoResult) {
                    List<GroupMemberInfo> members = new ArrayList<>();
                    for (int i = 0; i < v2TIMGroupMemberInfoResult.getMemberInfoList().size(); i++) {
                        GroupMemberInfo member = new GroupMemberInfo();
                        members.add(member.covertTIMGroupMemberInfo(v2TIMGroupMemberInfoResult.getMemberInfoList().get(i)));
                    }
                    mData = new ArrayList<>(members);
                    mAdapter.setDataSource(members);
                }
            });
        }else{
            GroupInfoProvider mProvider = new GroupInfoProvider();
            mProvider.loadGroupInfo(getIntent().getStringExtra(GroupMemberRemindActivity.class.getSimpleName()), new IUIKitCallBack() {
                @Override
                public void onSuccess(Object data) {
                    GroupInfo groupInfo = (GroupInfo) data;
                    mData = new ArrayList<>(groupInfo.getMemberDetails());
                    mAdapter.setDataSource(groupInfo.getMemberDetails());
                }

                @Override
                public void onError(String module, int errCode, String errMsg) {

                }
            });
        }
    }
    public static void startGroupMemberRemind(Context context,String groupId,String title, SelectionActivity.OnResultReturnListener listener){
        startGroupMemberRemind(context,groupId,title,-999,listener);
    }

    public static void startGroupMemberRemind(Context context,String groupId,String title,int role, SelectionActivity.OnResultReturnListener listener){
        Intent intent = new Intent(context,GroupMemberRemindActivity.class);
        intent.putExtra(GroupMemberRemindActivity.class.getSimpleName(),groupId);
        intent.putExtra(TITLE,title);
        intent.putExtra(ROLE,role);
        sOnResultReturnListener = listener;
        context.startActivity(intent);
    }

    @Override
    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

    }

    @Override
    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

    }

    @Override
    public void afterTextChanged(Editable editable) {
        String s = editable.toString().trim();
        List<GroupMemberInfo> data = new ArrayList<>();
        if(TextUtils.isEmpty(s)){
            data.addAll(mData);
            mAdapter.setDataSource(data);
        }else{
            if(mData!=null){
                for (GroupMemberInfo info:mData) {
                    if(info.getNickName().contains(s) || info.getNameCard().contains(s)){
                        data.add(info);
                    }
                }
                mAdapter.setDataSource(data);
            }
        }
    }
}
