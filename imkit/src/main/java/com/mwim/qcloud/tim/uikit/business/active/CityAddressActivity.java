package com.mwim.qcloud.tim.uikit.business.active;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;

import com.http.network.model.RequestWork;
import com.http.network.model.ResponseWork;
import com.mwim.qcloud.tim.uikit.R;
import com.mwim.qcloud.tim.uikit.base.BaseActivity;
import com.mwim.qcloud.tim.uikit.business.dialog.CityAreaDialog;
import com.mwim.qcloud.tim.uikit.component.SelectionActivity;
import com.mwim.qcloud.tim.uikit.modules.contact.ContactItemBean;
import com.mwim.qcloud.tim.uikit.modules.contact.ContactListView;
import com.work.api.open.Yz;
import com.work.api.open.model.GetCityListResp;
import com.work.api.open.model.client.OpenCity;
import com.work.util.ToastUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by tangyx
 * Date 2020/12/28
 * email tangyx@live.com
 */

public class CityAddressActivity extends BaseActivity {

    private static SelectionActivity.OnResultReturnListener sOnResultReturnListener;

    private ContactListView mContactListView;
    private List<OpenCity> mCityAll = new ArrayList<>();
    private CityAreaDialog mCityArea;


    public static void startCity(Context context,SelectionActivity.OnResultReturnListener onResultReturnListener){
        sOnResultReturnListener = onResultReturnListener;
        Intent intent = new Intent(context,CityAddressActivity.class);
        context.startActivity(intent);
    }

    @Override
    public void onInitView() throws Exception {
        super.onInitView();
        mContactListView = findViewById(R.id.black_list);
        mContactListView.setOnItemClickListener(new ContactListView.OnItemClickListener() {
            @Override
            public void onItemClick(int position, ContactItemBean contact) {
                if(mCityArea==null){
                    mCityArea = new CityAreaDialog();
                }
                mCityArea.setActivity(CityAddressActivity.this);
                mCityArea.setOnResultReturnListener(sOnResultReturnListener);
                mCityArea.setData(contact);
                mCityArea.show(getSupportFragmentManager(),"city_area");
            }
        });
    }

    @Override
    public void onInitValue() throws Exception {
        super.onInitValue();
        setTitleName(R.string.user_city_select);
        Yz.getSession().getCityList(this);
    }

    @Override
    public int onCustomContentId() {
        return R.layout.activity_im_contact_blacklist;
    }

    @Override
    public void onResult(RequestWork req, ResponseWork resp) throws Exception {
        super.onResult(req, resp);
        if(resp.isSuccess()){
            if(resp instanceof GetCityListResp){
                mCityAll = ((GetCityListResp) resp).getData();
                new CityTask().execute("");
            }
        }else{
            ToastUtil.warning(this,resp.getMessage());
        }
    }
    private class CityTask extends AsyncTask<String,Void, List<ContactItemBean>>{

        @Override
        protected List<ContactItemBean> doInBackground(String... voids) {
            String keyword = voids[0];
            List<ContactItemBean> contactItemBeans = new ArrayList<>();
            for (OpenCity city:mCityAll) {
                if(city.getCity().contains(keyword)){
                    ContactItemBean contactItemBean = new ContactItemBean();
                    contactItemBean.setNickname(city.getCity());
                    contactItemBean.setArea(city.getArea());
                    contactItemBeans.add(contactItemBean);
                }
            }
            return contactItemBeans;
        }

        @Override
        protected void onPostExecute(List<ContactItemBean> contactItemBeans) {
            super.onPostExecute(contactItemBeans);
            mContactListView.setDataSource(contactItemBeans);
        }
    }
}
