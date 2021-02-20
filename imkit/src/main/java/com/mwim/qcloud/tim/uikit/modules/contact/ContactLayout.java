package com.mwim.qcloud.tim.uikit.modules.contact;

import android.content.Context;

import androidx.annotation.Nullable;

import android.content.Intent;
import android.util.AttributeSet;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;

import com.mwim.qcloud.tim.uikit.business.active.SearchAddMoreActivity;
import com.mwim.qcloud.tim.uikit.modules.contact.interfaces.IContactLayout;
import com.mwim.qcloud.tim.uikit.R;
import com.mwim.qcloud.tim.uikit.component.TitleBarLayout;


public class ContactLayout extends LinearLayout implements IContactLayout {

    private ContactListView mContactListView;

    public ContactLayout(Context context) {
        super(context);
        init();
    }

    public ContactLayout(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public ContactLayout(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    public ContactLayout(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init();
    }

    private void init() {
        inflate(getContext(), R.layout.contact_layout, this);

        final View mAddMore = findViewById(R.id.add_more);
        mAddMore.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                getContext().startActivity(new Intent(getContext(), SearchAddMoreActivity.class));
            }
        });
        EditText mSearch = findViewById(R.id.search);
        mSearch.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                SearchAddMoreActivity.startSearchMore(getContext(),0);
            }
        });
        mContactListView = findViewById(R.id.contact_listview);
    }

    public void initDefault() {
        initDefault(ContactListView.DataSource.CONTACT_LIST);
    }

    public void initDefault(int type){
        mContactListView.loadDataSource(type);
    }

    @Override
    public ContactListView getContactListView() {
        return mContactListView;
    }

    @Override
    public TitleBarLayout getTitleBar() {
        return null;
    }

    @Override
    public void setParentLayout(Object parent) {

    }
}
