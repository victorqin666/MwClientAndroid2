package com.mwim.qcloud.tim.uikit.business.helper;

import android.content.Context;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.work.util.SLog;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * Created by tangyx on 16/6/12.
 */
public class AMapManager {
    private static AMapManager Instance=null;
    private AMapLocationClient mLocationClient;
    private double mLat;
    private double mLng;
    private String cityCode="010";
    private String city;
    private List<OnAmapLocationChangeListener> listeners;

    public static AMapManager getInstance(Context context){
        if(Instance==null){
            Instance = new AMapManager(context);
        }
        return Instance;
    }

    private AMapManager(Context context){
        mLocationClient = new AMapLocationClient(context.getApplicationContext());
        //设置定位回调监听
        //声明定位回调监听器
        //定位成功回调信息，设置相关消息
        //获取当前定位结果来源，如网络定位结果，详见定位类型表
        //获取纬度
        //获取经度
        //获取精度信息
        //定位时间
        //地址，如果option中设置isNeedAddress为false，则没有此结果，网络定位结果中会有地址信息，GPS定位不返回地址信息。
        //国家信息
        //省信息
        //城市信息
        //城区信息
        //街道信息
        //街道门牌号信息
        //城市编码
        //地区编码
        //显示错误信息ErrCode是错误码，errInfo是错误信息，详见错误码表。
        AMapLocationListener mLocationListener = new AMapLocationListener() {
            @Override
            public void onLocationChanged(AMapLocation amapLocation) {
                if (amapLocation != null) {
                    if (amapLocation.getErrorCode() == 0) {
                        //定位成功回调信息，设置相关消息
                        amapLocation.getLocationType();//获取当前定位结果来源，如网络定位结果，详见定位类型表
                        mLat = amapLocation.getLatitude();//获取纬度
                        mLng = amapLocation.getLongitude();//获取经度
                        float accuracy = amapLocation.getAccuracy();//获取精度信息
                        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
                        Date date = new Date(amapLocation.getTime());
                        df.format(date);//定位时间
                        String address = amapLocation.getAddress();//地址，如果option中设置isNeedAddress为false，则没有此结果，网络定位结果中会有地址信息，GPS定位不返回地址信息。
                        String country = amapLocation.getCountry();//国家信息
                        String province = amapLocation.getProvince();//省信息
                        city = amapLocation.getCity();//城市信息
                        String district = amapLocation.getDistrict();//城区信息
                        String street = amapLocation.getStreet();//街道信息
                        String streetNum = amapLocation.getStreetNum();//街道门牌号信息
                        cityCode = amapLocation.getCityCode();//城市编码
                        String adCode = amapLocation.getAdCode();//地区编码
                        SLog.i("经度Lng:" + mLng + "-纬度Lat:" + mLat + "(精度:" + accuracy + ")" + "\n地址:" + address + "\n国家:" + country + "\n省份:" + province + "\n城市:" + city + "\n区县:" + district + "\n街道:" + street + "\n街道门牌号码:" + streetNum + "\n城市编码:" + cityCode + "\n地区编码:" + adCode);
                        if(listeners!=null && listeners.size()>0){
                            for (OnAmapLocationChangeListener locationChangeListener:listeners) {
                                locationChangeListener.onLocationChange(amapLocation);
                            }
                        }
                    } else {
                        //显示错误信息ErrCode是错误码，errInfo是错误信息，详见错误码表。
                        SLog.e("location Error, ErrCode:"
                                + amapLocation.getErrorCode() + ", errInfo:"
                                + amapLocation.getErrorInfo());
                        if(listeners!=null && listeners.size()>0){
                            for (OnAmapLocationChangeListener locationChangeListener:listeners) {
                                locationChangeListener.onLocationError(amapLocation);
                            }
                        }
                    }
                }
            }
        };
        mLocationClient.setLocationListener(mLocationListener);
        init();
    }

    private void init(){
        //初始化定位参数
        //声明mLocationOption对象
        AMapLocationClientOption mLocationOption = new AMapLocationClientOption();
        //设置定位模式为高精度模式，Battery_Saving为低功耗模式，Device_Sensors是仅设备模式
        mLocationOption.setLocationMode(AMapLocationClientOption.AMapLocationMode.Hight_Accuracy);
        //设置是否返回地址信息（默认返回地址信息）
        mLocationOption.setNeedAddress(true);
        //设置是否只定位一次,默认为false
        mLocationOption.setOnceLocation(false);
        //设置是否强制刷新WIFI，默认为强制刷新
        mLocationOption.setWifiScan(true);
        //设置是否允许模拟位置,默认为false，不允许模拟位置
        mLocationOption.setMockEnable(false);
        //设置定位间隔,单位毫秒,默认为2000ms
        mLocationOption.setInterval(1000 * 60 *30);
        //给定位客户端对象设置定位参数
        mLocationClient.setLocationOption(mLocationOption);
    }

    public void onStart(){
        if(mLocationClient.isStarted()){
            mLocationClient.stopLocation();
        }
        mLocationClient.startLocation();
        SLog.i("start location...");
    }


    public void onStop(){
        mLocationClient.stopLocation();
        mLocationClient.onDestroy();
        Instance=null;
        SLog.i("stop location...");
    }

    public double getLat() {
        return mLat;
    }

    public double getLng() {
        return mLng;
    }

    public void addOnAmapLocationChangeListener(OnAmapLocationChangeListener locationChangeListener){
        if(listeners==null){
            listeners = new ArrayList<>();
        }
        listeners.add(locationChangeListener);
    }

    public void removeOnAmapLocationChangeListener(OnAmapLocationChangeListener locationChangeListener){
        if(listeners!=null){
            listeners.remove(locationChangeListener);
        }
    }

    public interface OnAmapLocationChangeListener{
        void onLocationChange(AMapLocation location);
        void onLocationError(AMapLocation location);
    }
}
