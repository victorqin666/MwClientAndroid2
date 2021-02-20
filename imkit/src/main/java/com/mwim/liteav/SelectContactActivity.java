package com.mwim.liteav;

import android.content.Context;
import android.content.Intent;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.chad.library.adapter.base.divider.HorizontalDividerItemDecoration;
import com.google.android.flexbox.FlexboxItemDecoration;
import com.google.android.flexbox.FlexboxLayoutManager;
import com.mwim.liteav.login.ProfileManager;
import com.mwim.liteav.trtcaudiocall.ui.TRTCAudioCallActivity;
import com.mwim.liteav.trtcvideocall.ui.TRTCVideoCallActivity;
import com.mwim.qcloud.tim.uikit.base.BaseActivity;
import com.mwim.qcloud.tim.uikit.base.IUIKitCallBack;
import com.mwim.qcloud.tim.uikit.utils.SoftKeyBoardUtil;
import com.tencent.imsdk.v2.V2TIMGroupMemberFullInfo;
import com.tencent.imsdk.v2.V2TIMGroupMemberInfoResult;
import com.tencent.imsdk.v2.V2TIMManager;
import com.tencent.imsdk.v2.V2TIMValueCallback;
import com.mwim.liteav.login.UserModel;
import com.mwim.liteav.model.ITRTCAVCall;
import com.mwim.qcloud.tim.uikit.R;
import com.mwim.qcloud.tim.uikit.component.picture.imageEngine.impl.GlideEngine;
import com.work.util.SLog;
import com.work.util.ToastUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 用于选择联系人
 *
 * @author guanyifeng
 */
public class SelectContactActivity extends BaseActivity {

    private static final String GROUP_ID = "group_id";
    private static final String CALL_TYPE = "call_type";
    public static final int RADIUS = 10;

    private TextView mCompleteBtn;
    private EditText mSearchEt;
    private RelativeLayout mGroupMemberLoadingView;
    private SelectedMemberListAdapter mSelectedMemberListAdapter;
    private List<UserModel> mSelectedModelList = new ArrayList<>();
    private Map<String, UserModel> mUserModelMap = new HashMap<>();
    private RecyclerView mGroupMemberListRv;
    private GroupMemberListAdapter mGroupMemberListAdapter;
    private List<ContactEntity> mUserModelList = new ArrayList<>();
    private Map<String, ContactEntity> mGroupMemberList = new HashMap<>();
    private UserModel mSelfModel;
    private String mGroupId;
    private int mCallType = ITRTCAVCall.TYPE_AUDIO_CALL;

    public static void start(Context context, String groupId, int type) {
        Intent starter = new Intent(context, SelectContactActivity.class);
        starter.putExtra(GROUP_ID, groupId);
        starter.putExtra(CALL_TYPE, type);
        starter.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(starter);
    }

    @Override
    public void onInitView() throws Exception {
        super.onInitView();
        mGroupId = getIntent().getStringExtra(GROUP_ID);
        mCallType = getIntent().getIntExtra(CALL_TYPE, ITRTCAVCall.TYPE_AUDIO_CALL);
        if (TextUtils.isEmpty(mGroupId)) {
            ToastUtil.error(this,"群ID为空");
            finish();
            return;
        }
        initView();
        mSelfModel = ProfileManager.getInstance().getUserModel();
        loadRecentSearch();
    }

    @Override
    public void onInitValue() throws Exception {
        super.onInitValue();
        setTitleName("发起呼叫");
    }

    @Override
    public View onCustomTitleRight(TextView view) {
        view.setText("完成");
        view.setBackgroundResource(R.drawable.bg_button_border);
        view.setTextColor(ContextCompat.getColor(this,R.color.colorWhite));
        mCompleteBtn = view;
        return view;
    }

    @Override
    public int onCustomContentId() {
        return R.layout.audiocall_activity_select_contact;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    private void initView() {
//        Toolbar mToolbar = (Toolbar) findViewById(R.id.toolbar);
        mSearchEt = (EditText) findViewById(R.id.et_search);
        mGroupMemberLoadingView = findViewById(R.id.rl_group_member_loading);
        // 设置已经选中的用户列表
        RecyclerView mSelectedMemberRv = (RecyclerView) findViewById(R.id.rv_selected_member);
        FlexboxLayoutManager manager = new FlexboxLayoutManager(this);
        FlexboxItemDecoration itemDecoration = new FlexboxItemDecoration(this);
        itemDecoration.setDrawable(getResources().getDrawable(R.drawable.bg_divider));
        mSelectedMemberRv.addItemDecoration(itemDecoration);
        mSelectedMemberRv.setLayoutManager(manager);
        mSelectedMemberListAdapter = new SelectedMemberListAdapter(this, mSelectedModelList, new OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                if (position < mSelectedModelList.size() && position >= 0) {
                    UserModel userModel = mSelectedModelList.get(position);
                    removeContact(userModel.userId);
                }
                completeBtnEnable();
            }
        });
        mSelectedMemberRv.setAdapter(mSelectedMemberListAdapter);
        // 设置底部搜索界面列表
        mGroupMemberListRv = (RecyclerView) findViewById(R.id.rv_group_member_list);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        mGroupMemberListRv.addItemDecoration(new HorizontalDividerItemDecoration.Builder(this).colorResId(R.color.background_color).build());
        mGroupMemberListRv.setLayoutManager(layoutManager);
        mGroupMemberListAdapter = new GroupMemberListAdapter( mUserModelList, new OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                if (position < mUserModelList.size() && position >= 0) {
                    ContactEntity entity = mUserModelList.get(position);
                    if (!entity.isSelected) {
                        //之前没有被添加过
                        addContact(entity);
                    } else {
                        removeContact(entity.mUserModel.userId);
                    }
                    completeBtnEnable();
                }
            }
        });
        mGroupMemberListRv.setAdapter(mGroupMemberListAdapter);

        mSearchEt.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    //开始搜索
                    search(v.getText().toString());
                    return true;
                }
                return false;
            }
        });
        mSearchEt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                search(s.toString());
            }
        });

        mCompleteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mSelectedModelList.isEmpty()) {
                    ToastUtil.info(SelectContactActivity.this,"请先选择通话用户");
                    return;
                }
                if (mCallType == ITRTCAVCall.TYPE_AUDIO_CALL) {
                    TRTCAudioCallActivity.startCallSomePeople(SelectContactActivity.this, mSelectedModelList, mGroupId);
                } else {
                    TRTCVideoCallActivity.startCallSomePeople(SelectContactActivity.this, mSelectedModelList, mGroupId);
                }
                finish();
            }
        });
        completeBtnEnable();
    }

    @Override
    protected void onPause() {
        super.onPause();
        SoftKeyBoardUtil.hideKeyBoard(mSearchEt);
    }

    private void loadRecentSearch() {
        mGroupMemberListRv.setVisibility(View.GONE);
        mGroupMemberLoadingView.setVisibility(View.VISIBLE);
        loadGroupMembers(0, new IUIKitCallBack() {
            @Override
            public void onSuccess(Object data) {
                mGroupMemberListRv.setVisibility(View.VISIBLE);
                mGroupMemberLoadingView.setVisibility(View.GONE);
                mUserModelList.clear();
                mUserModelList.addAll(mGroupMemberList.values());
                mGroupMemberListAdapter.notifyDataSetChanged();
            }

            @Override
            public void onError(String module, int errCode, String errMsg) {
                mGroupMemberListRv.setVisibility(View.VISIBLE);
                mGroupMemberLoadingView.setVisibility(View.GONE);
                mUserModelList.clear();
                mGroupMemberListAdapter.notifyDataSetChanged();
                SLog.e("loadGroupMembers failed, module:" + module + "|errCode:" + errCode + "|errMsg:" + errMsg);
            }
        });
    }

    public void loadGroupMembers(long nextSeq, final IUIKitCallBack callBack) {
        V2TIMManager.getGroupManager().getGroupMemberList(mGroupId, V2TIMGroupMemberFullInfo.V2TIM_GROUP_MEMBER_FILTER_ALL, nextSeq, new V2TIMValueCallback<V2TIMGroupMemberInfoResult>() {
            @Override
            public void onError(int code, String desc) {
                SLog.e("loadGroupMembers failed, code: " + code + "|desc: " + desc);
                callBack.onError(mGroupId, code, desc);
            }

            @Override
            public void onSuccess(V2TIMGroupMemberInfoResult v2TIMGroupMemberInfoResult) {
                for (int i = 0; i < v2TIMGroupMemberInfoResult.getMemberInfoList().size(); i++) {
                    V2TIMGroupMemberFullInfo info = v2TIMGroupMemberInfoResult.getMemberInfoList().get(i);
                    if (TextUtils.equals(info.getUserID(), mSelfModel.userId)) {
                        continue;
                    }
                    UserModel userModel = new UserModel();
                    userModel.userId = info.getUserID();
                    if(!TextUtils.isEmpty(info.getNameCard())){
                        userModel.userName = info.getNameCard();
                    }else if(!TextUtils.isEmpty(info.getNickName())){
                        userModel.userName = info.getNickName();
                    }else{
                        userModel.userName = info.getUserID();
                    }
                    userModel.userAvatar = info.getFaceUrl();
                    ContactEntity entity = new ContactEntity();
                    entity.isSelected = false;
                    entity.mUserModel = userModel;
                    mGroupMemberList.put(entity.mUserModel.userId, entity);
                }
                if (v2TIMGroupMemberInfoResult.getNextSeq() != 0) {
                    loadGroupMembers(v2TIMGroupMemberInfoResult.getNextSeq(), callBack);
                } else {
                    callBack.onSuccess(mGroupId);
                }
            }
        });
    }

    private void search(String id) {
        if (TextUtils.isEmpty(id)) {
            mUserModelList.clear();
            mUserModelList.addAll(mGroupMemberList.values());
            mGroupMemberListAdapter.notifyDataSetChanged();
            return;
        }
        id = id.toLowerCase();
        mUserModelList.clear();
        for (ContactEntity entity : mGroupMemberList.values()) {
            if (entity.mUserModel.userName.toLowerCase().contains(id)
                    || entity.mUserModel.userId.toLowerCase().contains(id)) {
                mUserModelList.add(entity);
            }
        }
        mGroupMemberListAdapter.notifyDataSetChanged();
    }

    private void removeContact(String userId) {
        //1. 删除在map中的model
        if (mUserModelMap.containsKey(userId)) {
            UserModel model = mUserModelMap.remove(userId);
            mSelectedModelList.remove(model);
            ContactEntity recentEntity = mGroupMemberList.get(userId);
            if (recentEntity != null) {
                recentEntity.isSelected = false;
            }
            for (ContactEntity entity : mUserModelList) {
                if (entity.mUserModel.userId.equals(userId)) {
                    entity.isSelected = false;
                    break;
                }
            }
        }
        //2. 通知界面刷新
        mGroupMemberListAdapter.notifyDataSetChanged();
        mSelectedMemberListAdapter.notifyDataSetChanged();
    }

    private void completeBtnEnable() {
        mCompleteBtn.setEnabled(!mSelectedModelList.isEmpty());
    }

    private void addContact(ContactEntity entity) {
        //1. 把对应的model增加到map中
        String userId = entity.mUserModel.userId;
        //1.1 判断这个contact是不是自己
        if (userId.equals(mSelfModel.userId)) {
            ToastUtil.info(this,"不能添加自己");
            return;
        }
        if (!mUserModelMap.containsKey(userId)) {
            mUserModelMap.put(userId, entity.mUserModel);
            mSelectedModelList.add(entity.mUserModel);
        }
        entity.isSelected = true;
        //2. 通知界面刷新
        mGroupMemberListAdapter.notifyDataSetChanged();
        mSelectedMemberListAdapter.notifyDataSetChanged();
    }

    public static class SelectedMemberListAdapter extends
            RecyclerView.Adapter<SelectedMemberListAdapter.ViewHolder> {
        private List<UserModel> list;
        private OnItemClickListener onItemClickListener;

        public SelectedMemberListAdapter(Context context, List<UserModel> list,
                                         OnItemClickListener onItemClickListener) {
            this.list = list;
            this.onItemClickListener = onItemClickListener;
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            Context context = parent.getContext();
            LayoutInflater inflater = LayoutInflater.from(context);
            View view = inflater.inflate(R.layout.audiocall_item_selected_contact, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            UserModel item = list.get(position);
            holder.bind(item, onItemClickListener);
        }

        @Override
        public int getItemCount() {
            return list.size();
        }

        public static class ViewHolder extends RecyclerView.ViewHolder {
            private ImageView mAvatarImg;

            public ViewHolder(View itemView) {
                super(itemView);
                initView(itemView);
            }

            public void bind(final UserModel model,
                             final OnItemClickListener listener) {
                GlideEngine.loadCornerAvatar(mAvatarImg, model.userAvatar);
                itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        listener.onItemClick(getLayoutPosition());
                    }
                });
            }

            private void initView(@NonNull final View itemView) {
                mAvatarImg = (ImageView) itemView.findViewById(R.id.img_avatar);
            }
        }
    }


    public static class GroupMemberListAdapter extends
            RecyclerView.Adapter<GroupMemberListAdapter.ViewHolder> {
        private List<SelectContactActivity.ContactEntity> list;
        private OnItemClickListener onItemClickListener;

        public GroupMemberListAdapter(List<SelectContactActivity.ContactEntity> list,
                                      OnItemClickListener onItemClickListener) {
            this.list = list;
            this.onItemClickListener = onItemClickListener;
        }

        public static class ViewHolder extends RecyclerView.ViewHolder {
            private CheckBox mContactCb;
            private ImageView mAvatarImg;
            private TextView mUserNameTv;

            public ViewHolder(View itemView) {
                super(itemView);
                mContactCb = itemView.findViewById(R.id.cb_contact);
                mAvatarImg = itemView.findViewById(R.id.img_avatar);
                mUserNameTv = itemView.findViewById(R.id.tv_user_name);
            }

            public void bind(final SelectContactActivity.ContactEntity model,
                             final OnItemClickListener listener) {
                if(TextUtils.isEmpty(model.mUserModel.userAvatar)){
                    GlideEngine.loadImage(mAvatarImg,R.drawable.default_head);
                }else{
                    GlideEngine.loadCornerAvatar(mAvatarImg, model.mUserModel.userAvatar);
                }
                mUserNameTv.setText(model.mUserModel.userName);
                mContactCb.setChecked(model.isSelected);
                mContactCb.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        listener.onItemClick(getLayoutPosition());
                    }
                });
                itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        listener.onItemClick(getLayoutPosition());
                    }
                });
            }
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            Context context = parent.getContext();
            LayoutInflater inflater = LayoutInflater.from(context);
            View view = inflater.inflate(R.layout.audiocall_item_select_contact, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            SelectContactActivity.ContactEntity item = list.get(position);
            holder.bind(item, onItemClickListener);
        }

        @Override
        public int getItemCount() {
            return list.size();
        }
    }

    public interface OnItemClickListener {
        void onItemClick(int position);
    }

    public static class ContactEntity {
        public UserModel mUserModel;
        public boolean isSelected;
    }
}
