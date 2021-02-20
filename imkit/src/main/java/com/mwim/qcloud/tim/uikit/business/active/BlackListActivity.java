package com.mwim.qcloud.tim.uikit.business.active;

import android.content.Intent;
import com.mwim.qcloud.tim.uikit.modules.contact.ContactItemBean;
import com.mwim.qcloud.tim.uikit.modules.contact.ContactListView;
import com.mwim.qcloud.tim.uikit.utils.IMKitConstants;
import com.mwim.qcloud.tim.uikit.R;

public class BlackListActivity extends IMBaseActivity {

    private ContactListView mListView;

    @Override
    public void onInitView() throws Exception {
        super.onInitView();
        mListView = findViewById(R.id.black_list);
        mListView.setOnItemClickListener(new ContactListView.OnItemClickListener() {
            @Override
            public void onItemClick(int position, ContactItemBean contact) {
                Intent intent = new Intent(BlackListActivity.this, FriendProfileActivity.class);
                intent.putExtra(IMKitConstants.ProfileType.CONTENT, contact);
                startActivity(intent);
            }
        });
    }

    @Override
    public void onInitValue() throws Exception {
        super.onInitValue();
        setTitleName(R.string.blacklist);
    }


    @Override
    public int onCustomContentId() {
        return R.layout.activity_im_contact_blacklist;
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadDataSource();
    }

    public void loadDataSource() {
        mListView.loadDataSource(ContactListView.DataSource.BLACK_LIST);
    }

}
