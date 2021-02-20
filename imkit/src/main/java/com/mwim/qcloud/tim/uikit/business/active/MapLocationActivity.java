package com.mwim.qcloud.tim.uikit.business.active;

import android.content.Context;
import android.content.Intent;
import android.widget.TextView;

import com.amap.api.maps.CameraUpdateFactory;
import com.amap.api.maps.model.BitmapDescriptorFactory;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.Marker;
import com.amap.api.maps.model.MarkerOptions;
import com.mwim.qcloud.tim.uikit.R;

/**
 * Created by tangyx
 * Date 2020/11/11
 * email tangyx@live.com
 */

public class MapLocationActivity extends MapViewActivity {

    private final static String TITLE="title";
    private final static String ADDRESS="address";
    private final static String LAT="lat";
    private final static String LNG="lng";

    @Override
    public void onInitView() throws Exception {
        super.onInitView();
        TextView mTitle = findViewById(R.id.title);
        mTitle.setText(getIntent().getStringExtra(TITLE));
        TextView mAddress = findViewById(R.id.address);
        mAddress.setText(getIntent().getStringExtra(ADDRESS));
    }

    @Override
    public void onInitValue() throws Exception {
        super.onInitValue();
        setTitleName("位置信息");
        MarkerOptions options = new MarkerOptions();
        double lat = getIntent().getDoubleExtra(LAT,0);
        double lng = getIntent().getDoubleExtra(LNG,0);
        options.position(new LatLng(lat,lng));
        options.icon(BitmapDescriptorFactory.fromResource(R.drawable.icon_marker_location));
        Marker mMark = mAMap.addMarker(options);
        mAMap.animateCamera(CameraUpdateFactory.newLatLngZoom(mMark.getPosition(), 12), 400, this);

    }

    @Override
    public int onCustomContentId() {
        return R.layout.activity_map_location;
    }

    public static void launcherLocation(Context context, String title, String address, double lat, double lng){
        Intent intent = new Intent(context,MapLocationActivity.class);
        intent.putExtra(TITLE,title);
        intent.putExtra(ADDRESS,address);
        intent.putExtra(LAT,lat);
        intent.putExtra(LNG,lng);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }
}
