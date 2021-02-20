package com.mwim.qcloud.tim.uikit.business.active;

import android.content.Intent;
import android.view.View;

import com.mwim.qcloud.tim.uikit.utils.IMKitConstants;
import com.mwim.qcloud.tim.uikit.R;

public class AddMoreActivity extends IMBaseActivity {

    private boolean mIsGroup;

    @Override
    public void onInitView() throws Exception {
        super.onInitView();
        startActivity(new Intent(this,SearchAddMoreActivity.class));
        finish();
    }

    @Override
    public void onInitValue() throws Exception {
        super.onInitValue();
        if (getIntent() != null) {
            mIsGroup = getIntent().getExtras().getBoolean(IMKitConstants.GroupType.GROUP);
        }
        setTitleName(mIsGroup ? getResources().getString(R.string.add_group) : getResources().getString(R.string.add_friend));
        findViewById(R.id.submit).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mIsGroup) {
                    addGroup(view);
                } else {
                    addFriend(view);
                }
            }
        });
    }

    @Override
    public int onCustomContentId() {
        return R.layout.activity_im_contact_add;
    }


    public void addFriend(View view) {
//        String id = mUserID.getText().toString();
//        if (TextUtils.isEmpty(id)) {
//            return;
//        }
//        if(id.equals(UserApi.instance().getMobile())){
//            ToastUtil.info(this,R.string.toast_add_more_friends);
//            return;
//        }
//        V2TIMFriendAddApplication v2TIMFriendAddApplication = new V2TIMFriendAddApplication(id);
//        v2TIMFriendAddApplication.setAddWording(mAddWording.getText().toString());
//        v2TIMFriendAddApplication.setAddSource("android");
//        V2TIMManager.getFriendshipManager().addFriend(v2TIMFriendAddApplication, new V2TIMValueCallback<V2TIMFriendOperationResult>() {
//            @Override
//            public void onError(int code, String desc) {
//                SLog.e("addFriend err code = " + code + ", desc = " + desc);
//            }
//
//            @Override
//            public void onSuccess(V2TIMFriendOperationResult v2TIMFriendOperationResult) {
//                SLog.i("addFriend success");
//                switch (v2TIMFriendOperationResult.getResultCode()) {
//                    case BaseConstants.ERR_SUCC:
//                        ToastUtil.success(AddMoreActivity.this,"成功");
//                        break;
//                    case BaseConstants.ERR_SVR_FRIENDSHIP_INVALID_PARAMETERS:
//                        if (TextUtils.equals(v2TIMFriendOperationResult.getResultInfo(), "Err_SNS_FriendAdd_Friend_Exist")) {
//                            ToastUtil.info(AddMoreActivity.this,"对方已是您的好友");
//                            break;
//                        }
//                    case BaseConstants.ERR_SVR_FRIENDSHIP_COUNT_LIMIT:
//                        ToastUtil.info(AddMoreActivity.this,"您的好友数已达系统上限");
//                        break;
//                    case BaseConstants.ERR_SVR_FRIENDSHIP_PEER_FRIEND_LIMIT:
//                        ToastUtil.info(AddMoreActivity.this,"对方的好友数已达系统上限");
//                        break;
//                    case BaseConstants.ERR_SVR_FRIENDSHIP_IN_SELF_BLACKLIST:
//                        ToastUtil.info(AddMoreActivity.this,"被加好友在自己的黑名单中");
//                        break;
//                    case BaseConstants.ERR_SVR_FRIENDSHIP_ALLOW_TYPE_DENY_ANY:
//                        ToastUtil.info(AddMoreActivity.this,"对方已禁止加好友");
//                        break;
//                    case BaseConstants.ERR_SVR_FRIENDSHIP_IN_PEER_BLACKLIST:
//                        ToastUtil.info(AddMoreActivity.this,"您已被被对方设置为黑名单");
//                        break;
//                    case BaseConstants.ERR_SVR_FRIENDSHIP_ALLOW_TYPE_NEED_CONFIRM:
//                        ToastUtil.info(AddMoreActivity.this,"等待好友审核同意");
//                        break;
//                    default:
//                        ToastUtil.info(AddMoreActivity.this,v2TIMFriendOperationResult.getResultCode() + " " + v2TIMFriendOperationResult.getResultInfo());
//                        break;
//                }
//                finish();
//            }
//        });
    }

    public void addGroup(View view) {
//        String id = mUserID.getText().toString();
//        if (TextUtils.isEmpty(id)) {
//            return;
//        }
//
//        V2TIMManager.getInstance().joinGroup(id, mAddWording.getText().toString(), new V2TIMCallback() {
//            @Override
//            public void onError(int code, String desc) {
//                SLog.e("addGroup err code = " + code + ", desc = " + desc);
//                ToastUtil.error(AddMoreActivity.this,"Error code = " + code + ", desc = " + desc);
//            }
//
//            @Override
//            public void onSuccess() {
//                SLog.i("addGroup success");
//                ToastUtil.success(AddMoreActivity.this,"加群请求已发送");
//                finish();
//            }
//        });
    }

    @Override
    public void finish() {
        super.finish();
//        SoftKeyBoardUtil.hideKeyBoard(mUserID.getWindowToken());
    }
}
