package com.mwim.qcloud.tim.uikit.business.active;

import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;

import com.amap.api.maps.AMap;
import com.amap.api.maps.TextureMapView;
import com.amap.api.maps.model.CameraPosition;
import com.amap.api.maps.model.Marker;
import com.mwim.qcloud.tim.uikit.R;
import com.mwim.qcloud.tim.uikit.base.BaseActivity;
import com.mwim.qcloud.tim.uikit.base.IUIKitCallBack;

/**
 * Created by tangyx
 * Date 2020/11/10
 * email tangyx@live.com
 */

public abstract class MapViewActivity extends BaseActivity implements AMap.OnMarkerClickListener,
        AMap.OnMapLoadedListener,
        AMap.OnCameraChangeListener,
        AMap.CancelableCallback,
        AMap.OnInfoWindowClickListener,
        AMap.OnMapTouchListener {

    public static IUIKitCallBack mCallBack;
    private TextureMapView mMapView;
    protected AMap mAMap;
    protected FrameLayout mContainer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(mMapView!=null){
            mMapView.onCreate(savedInstanceState);
        }
    }

    @Override
    public void onInitView() throws Exception {
        super.onInitView();
        mContainer = findViewById(R.id.container);
        mMapView = new TextureMapView(this);
        FrameLayout.LayoutParams params = getMapParams();
        mContainer.addView(mMapView,0,params);
    }

    public FrameLayout.LayoutParams getMapParams(){
        return new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
    }

    @Override
    public void onInitValue() throws Exception {
        super.onInitValue();
        if(mMapView!=null){
            mAMap = mMapView.getMap();
            mAMap.setTrafficEnabled(false);
            mAMap.getUiSettings().setZoomControlsEnabled(false);
            mAMap.getUiSettings().setTiltGesturesEnabled(false);
            mAMap.getUiSettings().setRotateGesturesEnabled(false);

            mAMap.setOnMapLoadedListener(this);
            mAMap.setOnMarkerClickListener(this);
            mAMap.setOnCameraChangeListener(this);
            mAMap.setOnInfoWindowClickListener(this);
            mAMap.setOnMapTouchListener(this);
        }
    }

    @Override
    public void onTouch(MotionEvent motionEvent) {

    }

    @Override
    public int onCustomContentId() {
        return R.layout.activity_mapview;
    }

    @Override
    public void onCameraChange(CameraPosition cameraPosition) {

    }

    @Override
    public void onCameraChangeFinish(CameraPosition cameraPosition) {

    }

    @Override
    public void onMapLoaded() {

    }
    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        if(mMapView!=null){
            mMapView.onSaveInstanceState(outState);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(mMapView!=null){
            mMapView.onResume();
        }
    }
    @Override
    protected void onPause() {
        super.onPause();
        if(mMapView!=null){
            mMapView.onPause();
        }
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        if(mMapView!=null){
            mMapView.onLowMemory();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(mMapView!=null){
            mMapView.onDestroy();
        }
    }

    @Override
    public void onFinish() {

    }

    @Override
    public void onCancel() {

    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        return false;
    }

    @Override
    public void onInfoWindowClick(Marker marker) {

    }
    /**
     * 在地图添加view
     */
    protected void addView(View view){
        if(view!=null){
            mContainer.addView(view);
        }
    }
}
