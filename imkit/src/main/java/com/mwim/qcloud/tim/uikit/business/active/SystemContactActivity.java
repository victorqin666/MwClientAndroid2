package com.mwim.qcloud.tim.uikit.business.active;

import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.provider.ContactsContract;
import android.text.TextUtils;
import android.view.View;

import com.http.network.model.RequestWork;
import com.http.network.model.ResponseWork;
import com.mwim.qcloud.tim.uikit.R;
import com.mwim.qcloud.tim.uikit.base.BaseActivity;
import com.mwim.qcloud.tim.uikit.business.dialog.BaseDialog;
import com.mwim.qcloud.tim.uikit.business.dialog.ConfirmDialog;
import com.mwim.qcloud.tim.uikit.modules.contact.ContactItemBean;
import com.mwim.qcloud.tim.uikit.modules.contact.ContactListView;
import com.mwim.qcloud.tim.uikit.utils.IMKitConstants;
import com.tencent.imsdk.v2.V2TIMFriendApplication;
import com.tencent.imsdk.v2.V2TIMFriendApplicationResult;
import com.tencent.imsdk.v2.V2TIMManager;
import com.tencent.imsdk.v2.V2TIMValueCallback;
import com.work.api.open.Yz;
import com.work.api.open.model.GetUserListByMobilesReq;
import com.work.api.open.model.GetUserListByMobilesResp;
import com.work.api.open.model.client.OpenData;
import com.work.util.RegularUtils;
import com.work.util.ToastUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.tencent.imsdk.v2.V2TIMFriendApplication.V2TIM_FRIEND_APPLICATION_SEND_OUT;

/**
 * Created by tangyx
 * Date 2020/12/24
 * email tangyx@live.com
 */

public class SystemContactActivity extends BaseActivity {

    private ContactListView mContactListView;
    private Map<String,String> mContactsMaps;
    private List<OpenData> mSystemData;
    private int mOffset = 0;
    private List<ContactItemBean> mData;
    private List<V2TIMFriendApplication> mFriendApplications;
    private View mProgressLayout;

    @Override
    public void onInitView() throws Exception {
        super.onInitView();
        mProgressLayout = findViewById(R.id.progress_layout);
        mContactListView = findViewById(R.id.black_list);
        mContactListView.hideLoading();
        mContactListView.setOnItemClickListener(new ContactListView.OnItemClickListener() {
            @Override
            public void onItemClick(int position, ContactItemBean contact) {
                if(contact.getUserType()!=3
                        && contact.getUserType()!=4){
                    Intent intent = new Intent(SystemContactActivity.this, FriendProfileActivity.class);
                    intent.putExtra(IMKitConstants.ProfileType.CONTENT, contact.getId());
                    startActivity(intent);
                }
            }
        });
    }

    @Override
    public void onInitValue() throws Exception {
        super.onInitValue();
        setTitleName(R.string.text_new_contacts_phone);
        new SearchModelTask().execute();
    }

    private void getFriendApplicationList(final List<OpenData> data){
        if(isFinishing()){
            return;
        }
        if(mFriendApplications!=null){
            new MobileTask(data,mFriendApplications).execute();
        }else{
            V2TIMManager.getFriendshipManager().getFriendApplicationList(new V2TIMValueCallback<V2TIMFriendApplicationResult>() {
                @Override
                public void onError(int code, String desc) {
                    if(isFinishing()){
                        return;
                    }
                    new MobileTask(data,null).execute();
                }

                @Override
                public void onSuccess(V2TIMFriendApplicationResult v2TIMFriendApplicationResult) {
                    if(isFinishing()){
                        return;
                    }
                    if (v2TIMFriendApplicationResult.getFriendApplicationList() != null) {
                        mFriendApplications = v2TIMFriendApplicationResult.getFriendApplicationList();
                        if (mFriendApplications.size() == 0) {
                            new MobileTask(data,null).execute();
                        }else{
                            new MobileTask(data,mFriendApplications).execute();
                        }
                    }else{
                        new MobileTask(data,null).execute();
                    }

                }
            });
        }
    }


    @Override
    public int onCustomContentId() {
        return R.layout.activity_im_contact_blacklist;
    }

    @Override
    public void onResult(RequestWork req, ResponseWork resp) throws Exception {
        super.onResult(req, resp);
        if(resp.isSuccess()){
            if(resp instanceof GetUserListByMobilesResp){
                List<OpenData> data = ((GetUserListByMobilesResp) resp).getData();
                getFriendApplicationList(data);
            }
        }else{
            ToastUtil.warning(this,resp.getMessage());
        }
    }

    private class SearchModelTask extends AsyncTask<Void,Void,List<OpenData>>{

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mContactsMaps = new HashMap<>();
            mData = new ArrayList<>();
            mProgressLayout.setVisibility(View.VISIBLE);
        }

        @Override
        protected List<OpenData> doInBackground(Void... voids) {
            List<OpenData> contacts = new ArrayList<>();
            //获取联系人信息的Uri
            Uri uri = ContactsContract.CommonDataKinds.Phone.CONTENT_URI;
            //获取ContentResolver
            ContentResolver contentResolver = SystemContactActivity.this.getContentResolver();
            //查询数据，返回Cursor
            Cursor cursor = contentResolver.query(uri, null, null, null, null);
            if(cursor!=null){
                while(cursor.moveToNext()){
                    String name = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
                    String mobile = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                    if(TextUtils.isEmpty(mobile)){
                        continue;
                    }
                    mobile = mobile.replaceAll(" ","").replaceAll("-","");
                    if(mContactsMaps.containsKey(mobile) || !RegularUtils.isMobileSimple(mobile)){
                        continue;
                    }
//                    String contactId = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts._ID));
                    OpenData contactPhone = new OpenData();
                    contactPhone.setMobile(mobile);
                    mContactsMaps.put(mobile,name);
                    contacts.add(contactPhone);
                }
                cursor.close();
            }
            return contacts;
        }

        @Override
        protected void onPostExecute(List<OpenData> contactItemBeans) {
            super.onPostExecute(contactItemBeans);
            if(isFinishing()){
                return;
            }
            if(contactItemBeans.size()==0){
                ConfirmDialog cd = new ConfirmDialog();
                cd.setContent(R.string.toast_sys_contacts_error);
                cd.setOnDismissListener(new BaseDialog.OnDismissListener() {
                    @Override
                    public void onDismiss(DialogInterface dialog) {
                        finish();
                    }
                });
                cd.show(getSupportFragmentManager(),"sys_contacts");
            }else{
                mSystemData = contactItemBeans;
                limitData();
            }
        }
    }

    private void limitData(){
        if(mOffset >= mSystemData.size()){
            mProgressLayout.setVisibility(View.GONE);
            return;
        }
        int start = mOffset;
        int mLength = 40;
        int end = mOffset + mLength;
        end = Math.min(end, mSystemData.size());
        List<OpenData> data = mSystemData.subList(start,end);
        mOffset = end;
        GetUserListByMobilesReq getUserListByMobilesReq = new GetUserListByMobilesReq();
        getUserListByMobilesReq.setParamVal(data);
        Yz.getSession().getUserListByMobiles(getUserListByMobilesReq,SystemContactActivity.this);
    }

    private class MobileTask extends AsyncTask<Void,Void,List<ContactItemBean>>{

        private List<OpenData> data;
        private List<V2TIMFriendApplication> friendsApplications;

        public MobileTask(List<OpenData> data,List<V2TIMFriendApplication> friendsApplications) {
            this.data = data;
            this.friendsApplications = friendsApplications;
        }

        @Override
        protected List<ContactItemBean> doInBackground(Void... voids) {
            List<ContactItemBean> contactItemBeans = new ArrayList<>();
            for (OpenData openData:data) {
                ContactItemBean contactItemBean = new ContactItemBean();
                contactItemBean.setId(openData.getUserId());
                contactItemBean.setSystemContacts(true);
                contactItemBean.setMobile(openData.getMobile());
                contactItemBean.setUserType(openData.getUserType());
                if(friendsApplications!=null && friendsApplications.size()>0){
                    for (V2TIMFriendApplication application : friendsApplications) {
                        if(application.getType() == V2TIM_FRIEND_APPLICATION_SEND_OUT //我申请加别人好友
                                && application.getUserID().equals(openData.getUserId())){
                            contactItemBean.setUserType(4);
                        }
                    }
                }
                String icon = openData.getUserIcon();
                if(!TextUtils.isEmpty(icon)){
                    List<Object> iconUrl = new ArrayList<>();
                    iconUrl.add(icon);
                    contactItemBean.setIconUrlList(iconUrl);
                }
                contactItemBean.setNickname(mContactsMaps.get(openData.getMobile()));
                if(contactItemBean.getUserType()==1
                        || contactItemBean.getUserType()==2
                        || contactItemBean.getUserType() == 4){
                    contactItemBean.setSystemRemark(getString(R.string.text_contacts_phone_nickname,openData.getNickName()));
                }
                contactItemBeans.add(contactItemBean);
            }
            return contactItemBeans;
        }

        @Override
        protected void onPostExecute(List<ContactItemBean> contactItemBeans) {
            super.onPostExecute(contactItemBeans);
            if(isFinishing()){
                return;
            }
            mData.addAll(contactItemBeans);
            mContactListView.setDataSource(mData);
            limitData();
        }
    }
}
