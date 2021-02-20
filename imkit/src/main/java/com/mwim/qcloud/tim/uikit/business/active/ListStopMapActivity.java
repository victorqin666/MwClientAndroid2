package com.mwim.qcloud.tim.uikit.business.active;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Point;
import android.location.LocationManager;
import android.net.Uri;
import android.provider.Settings;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.amap.api.location.AMapLocation;
import com.amap.api.maps.CameraUpdateFactory;
import com.amap.api.maps.Projection;
import com.amap.api.maps.model.BitmapDescriptorFactory;
import com.amap.api.maps.model.CameraPosition;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.Marker;
import com.amap.api.maps.model.MarkerOptions;
import com.amap.api.services.core.LatLonPoint;
import com.amap.api.services.core.PoiItem;
import com.amap.api.services.help.Inputtips;
import com.amap.api.services.help.InputtipsQuery;
import com.amap.api.services.help.Tip;
import com.amap.api.services.poisearch.PoiResult;
import com.amap.api.services.poisearch.PoiSearch;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.divider.HorizontalDividerItemDecoration;
import com.mwim.qcloud.tim.uikit.R;
import com.mwim.qcloud.tim.uikit.business.adapter.FenceAddPoiAdapter;
import com.mwim.qcloud.tim.uikit.business.adapter.ListStopMapAdapter;
import com.mwim.qcloud.tim.uikit.business.dialog.ConfirmDialog;
import com.mwim.qcloud.tim.uikit.business.helper.AMapManager;
import com.work.util.KeyboardUtils;
import com.work.util.SLog;
import com.work.util.ScreenUtils;
import com.work.util.ToastUtil;
import com.workstation.permission.PermissionsResultAction;

import java.util.List;

/**
 * Created by tangyx
 * Date 2020/9/19
 * email tangyx@live.com
 */

public class ListStopMapActivity extends MapViewActivity implements AMapManager.OnAmapLocationChangeListener,
        PoiSearch.OnPoiSearchListener,
        View.OnFocusChangeListener,
        TextWatcher,
        BaseQuickAdapter.OnItemClickListener,
        Inputtips.InputtipsListener {
    private static final String[] PERMISSIONS = new String[]{
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION,};
    private final String GPS_ACTION = "android.location.PROVIDERS_CHANGED";
    private ListStopMapAdapter mAdapter;
    private View mLoadingLayout;
    private AMapManager mapManager;
    private Marker mMark;
    private RecyclerView mRecyclerView;

    private FenceAddPoiAdapter mFenceAdapter = null;
    private EditText mSearch;
    private View mQuery;
    private LinearLayout mDataLayout;

    private GPSReceiver mGpsReceiver;
    private boolean isFirstLocation = true;

    @Override
    public void onInitView() throws Exception {
        super.onInitView();
        mDataLayout = findViewById(R.id.data_layout);
        mSearch = findViewById(R.id.search);
        mQuery = findViewById(R.id.query);
        mLoadingLayout = findViewById(R.id.loading_layout);
        mRecyclerView = findViewById(R.id.recycler_view);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerView.addItemDecoration(new HorizontalDividerItemDecoration.Builder(this).colorResId(R.color.background_color).build());
    }

    @Override
    public void onInitValue() throws Exception {
        super.onInitValue();
        mAdapter = new ListStopMapAdapter(null);
        mAdapter.setOnItemClickListener(this);
        mFenceAdapter = new FenceAddPoiAdapter(null);
        mFenceAdapter.setOnItemClickListener(this);
        mQuery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                KeyboardUtils.hideSoftInput(mSearch);
            }
        });
        mSearch.setOnFocusChangeListener(this);

        mapManager = AMapManager.getInstance(this);
        mapManager.addOnAmapLocationChangeListener(this);
        if(!hasPermission(PERMISSIONS)){
            onPermissionChecker(PERMISSIONS, new PermissionsResultAction() {
                @Override
                public void onGranted() {
                    onStart();
                }

                @Override
                public void onDenied(String permission) {
                    onStart();
                }
            });
        }
        autoRecyclerView(false);
        setTitleName(R.string.self_location);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(hasPermission(PERMISSIONS)){
            mapManager.onStart();
        }
    }

    private void autoRecyclerView(final boolean focus){
        mRecyclerView.setAdapter(focus?mFenceAdapter:mAdapter);
        int h = focus?(int) (ScreenUtils.getScreenHeight(this) * 0.8f):(int) (ScreenUtils.getScreenHeight(this) * 0.45f);
        mQuery.setVisibility(focus?View.VISIBLE:View.GONE);
        FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) mDataLayout.getLayoutParams();
        if(params==null){
            params = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,h);
        }else{
            params.height = h;
        }
        if(focus){
            mSearch.addTextChangedListener(this);
        }else{
            mSearch.removeTextChangedListener(this);
            mSearch.setText(null);
        }
        mDataLayout.setLayoutParams(params);
    }

    @Override
    public int onCustomContentId() {
        return R.layout.dialog_liststopmap;
    }

    @Override
    public View onCustomTitleRight(TextView view) {
        view.setText(R.string.label_confirm);
        return view;
    }

    @Override
    public void onRightClickListener(View view) {
        super.onRightClickListener(view);
        PoiItem poiItem = mAdapter.getSelectData();
        if(poiItem!=null){
            if(mCallBack!=null){
                mCallBack.onSuccess(poiItem);
            }
            finish();
        }
    }

    @Override
    public void onLocationChange(AMapLocation location) {
        searchBound();
    }

    @Override
    public void onLocationError(AMapLocation location) {

        double lat = mapManager.getLat();
        double lng = mapManager.getLng();
        if(lat>0 && lng>0){
            searchBound();
        }else if(location.getErrorCode() == 12){
            new ConfirmDialog().setContent(R.string.text_gps_permission_error).setConfirmTextResId(R.string.label_go_setting).setOnConfirmListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(!isOpenGps(ListStopMapActivity.this)){
                        Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                        startActivityForResult(intent, 0);
                    }else{
                        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                        intent.setData(Uri.parse("package:" + ListStopMapActivity.this.getPackageName()));
                        if (!isIntentAvailable(intent)) return;
                        startActivity(intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
                    }
                }
            }).show(getSupportFragmentManager(), "location");
            mGpsReceiver = new GPSReceiver();
            IntentFilter intentFilter = new IntentFilter();
            intentFilter.addAction(GPS_ACTION);
            registerReceiver(mGpsReceiver, intentFilter);
        }
    }

    private boolean isIntentAvailable(final Intent intent) {
        return getPackageManager()
                .queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY)
                .size() > 0;
    }

    private void searchBound(){
        double lat = mapManager.getLat();
        double lng = mapManager.getLng();
        if(lat>0 && lng>0){
            mapManager.removeOnAmapLocationChangeListener(this);
            mapManager.onStop();
            LatLng latLng = new LatLng(lat,lng);
            MarkerOptions options = new MarkerOptions();
            options.draggable(true);
            options.icon(BitmapDescriptorFactory.fromResource(R.drawable.icon_marker_location));
//            options.position(new LatLng(lat,lng));
            mAMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng,14));
            mMark = mAMap.addMarker(options);
        }
    }

    private void loadPoiData(LatLng latLng){
        if(mMark==null){
            return;
        }
        positionByPixels(latLng);
        PoiSearch.Query query = new PoiSearch.Query(null,null,null);
        query.setPageNum(1);
        query.setPageSize(40);
        PoiSearch poiSearch = new PoiSearch(this,query);
        poiSearch.setBound(new PoiSearch.SearchBound(new LatLonPoint(latLng.latitude,latLng.longitude),1000 * 5));
        poiSearch.setOnPoiSearchListener(this);
        poiSearch.searchPOIAsyn();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mGpsReceiver != null) {
            try {
                unregisterReceiver(mGpsReceiver);
            } catch (Exception ignore) {
            }
        }
        mapManager.removeOnAmapLocationChangeListener(this);
        mapManager.onStop();
    }

    @Override
    public void onPoiSearched(PoiResult poiResult, int i) {
        mAdapter.setNewData(poiResult.getPois());
        mLoadingLayout.setVisibility(View.GONE);
        PoiItem poiItem = mAdapter.getSelectData();
        if(mMark!=null){
//            mMark.setPosition(new LatLng(poiItem.getLatLonPoint().getLatitude(),poiItem.getLatLonPoint().getLongitude()));
            LatLng latLng = new LatLng(poiItem.getLatLonPoint().getLatitude(),poiItem.getLatLonPoint().getLongitude());
            if(isFirstLocation){
                isFirstLocation = false;
                mAMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng,14));
            }else{
                mAMap.animateCamera(CameraUpdateFactory.newLatLng(latLng));
            }
        }
    }

    @Override
    public void onPoiItemSearched(PoiItem poiItem, int i) {
    }

    private boolean isCameraFinishUpdate = true;

    @Override
    public void onTouch(MotionEvent motionEvent) {
        super.onTouch(motionEvent);
        isCameraFinishUpdate = true;
    }

    @Override
    public void onFinish() {
        super.onFinish();
        PoiItem poiItem = mAdapter.getSelectData();
        if(poiItem!=null){
            positionByPixels(new LatLng(poiItem.getLatLonPoint().getLatitude(),poiItem.getLatLonPoint().getLongitude()));
        }
    }

    @Override
    public void onCameraChangeFinish(CameraPosition cameraPosition) {
        super.onCameraChangeFinish(cameraPosition);
        if(isCameraFinishUpdate){
            SLog.e("onCameraChangeFinish");
            isCameraFinishUpdate = false;
            loadPoiData(cameraPosition.target);
        }
    }

    private void positionByPixels(LatLng latLng){
        Projection projection = mAMap.getProjection();
        Point point = projection.toScreenLocation(latLng);
        if (point.x > 0 && point.y > 0) {
            mMark.setPositionByPixels(point.x, point.y);
        }
    }

    public FrameLayout.LayoutParams getMapParams(){
        return new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, (int) (ScreenUtils.getScreenHeight(this) * 0.6));
    }

    @Override
    public void onFocusChange(View view, boolean b) {
        autoRecyclerView(b);
    }

    @Override
    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

    }

    @Override
    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

    }

    @Override
    public void afterTextChanged(Editable editable) {
        String keyword = editable.toString().trim();
        if(TextUtils.isEmpty(keyword)){
            mFenceAdapter.clear();
        }else{
            InputtipsQuery inputtipsQuery = new InputtipsQuery(keyword,"");
            Inputtips inputtips = new Inputtips(this,inputtipsQuery);
            inputtips.setInputtipsListener(this);
            inputtips.requestInputtipsAsyn();
        }
    }

    @Override
    public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
        if(adapter instanceof FenceAddPoiAdapter){
            Tip tip = mFenceAdapter.getItem(position);
            if(tip!=null){
                LatLonPoint point = tip.getPoint();
                if(point!=null){
                    mAdapter.clear();
                    LatLng latLng = new LatLng(point.getLatitude(), point.getLongitude());
//                    mMark.setPosition(new LatLng(point.getLatitude(), point.getLongitude()));
                    mAdapter.setSelectData(null);
                    KeyboardUtils.hideSoftInput(mSearch);
                    isCameraFinishUpdate = true;
                    loadPoiData(latLng);
                }else{
                    ToastUtil.error(this, R.string.toast_fence_add_location_error);
                }
            }else{
                ToastUtil.error(this,R.string.toast_fence_add_location_error);
            }
        }else{
            PoiItem poiItem = mAdapter.getItem(position);
            if(poiItem!=null){
                mAdapter.setSelectData(poiItem);
                if(mMark!=null){
//                    mMark.setPosition(new LatLng(poiItem.getLatLonPoint().getLatitude(),poiItem.getLatLonPoint().getLongitude()));
                    final LatLng latLng = new LatLng(poiItem.getLatLonPoint().getLatitude(),poiItem.getLatLonPoint().getLongitude());
                    isCameraFinishUpdate = false;
                    mAMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 14),this);
                }
            }
        }
    }

    @Override
    public void onGetInputtips(List<Tip> list, int i) {
        if(TextUtils.isEmpty(mSearch.getText().toString().trim())){
            mFenceAdapter.clear();
        }else{
            mFenceAdapter.setNewData(list);
        }
    }

    class GPSReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            if(GPS_ACTION.equals(intent.getAction())){
                boolean gps = isOpenGps(context);
                if(gps){//打开了GPS
                    mapManager.onStart();
                }
            }
        }
    }

    public static boolean isOpenGps(Context context) {
        LocationManager locationManager
                = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        // 通过GPS卫星定位，定位级别可以精确到街
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
    }
}
