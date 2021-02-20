package com.mwim.qcloud.tim.uikit.business.modal;

import android.text.TextUtils;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.work.util.SharedUtils;

/**
 * Created by Administrator on 2019/3/25
 * Description
 */

public class UserApi {
    private final static String USER_SIGN="userSign";
    private final static String USER_PHONE="userPhone";
    private final static String USER_ID="userId";
    private final static String USER_ICON="userIcon";
    private final static String USER_NAME="nickName";
    private final static String USER_CARD="userCard";
    private final static String USER_POSITION="userPosition";
    private final static String USER_EMAIL="userEmailKey";
    private final static String USER_DEPARTMENT="userDepartment";
    private final static String USER_DEPARTMENT_ID="userDepartment_ID";
    private final static String USER_TOKEN="userToken";
    private final static String USER_SIGNATURE="userSignature";
    private final static String USER_CITY="userCity";
    private final static String USER_GENDER="userGender";
    private static UserApi INSTANCE;
    private String userId;
    @JsonIgnore
    private String userSign;
    @JsonIgnore
    private String userIcon;
    @JsonIgnore
    private String nickName;
    @JsonIgnore
    private String card;
    @JsonIgnore
    private String position;
    @JsonIgnore
    private String email;
    @JsonIgnore
    private String departmentId;
    @JsonIgnore
    private String departName;
    @JsonIgnore
    private String userSignature;
    @JsonIgnore
    private String city;
    @JsonIgnore
    private int gender=-1;

    private String mobile;
    private String token;
    private String store;
    private String version = "1.0.1";
    private int versionCode = 1;

    public static UserApi instance(){
        return INSTANCE==null?INSTANCE = new UserApi():INSTANCE;
    }

    private UserApi() {
    }


    public String getUserId() {
        if(TextUtils.isEmpty(userId)){
            userId = SharedUtils.getString(USER_ID);
        }
        return userId;
    }

    public void setUserId(String userId) {
        SharedUtils.putData(USER_ID,userId);
        this.userId = userId;
    }

    public String getUserSign() {
        if(TextUtils.isEmpty(userSign)){
            userSign = SharedUtils.getString(USER_SIGN);
        }
        return userSign;
    }

    public void setUserSign(String userSign) {
        this.userSign = userSign;
        SharedUtils.putData(USER_SIGN,userSign);
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public int getVersionCode() {
        return versionCode;
    }

    public void setVersionCode(int versionCode) {
        this.versionCode = versionCode;
    }

    public String getToken() {
        if(TextUtils.isEmpty(token)){
            token = SharedUtils.getString(USER_TOKEN);
        }
        return token;
    }

    public void setToken(String token) {
        this.token = token;
        SharedUtils.putData(USER_TOKEN,token);
    }

    public String getStore() {
        return store;
    }

    public void setStore(String store) {
        this.store = store;
    }

    public String getMobile() {
        if(TextUtils.isEmpty(mobile)){
            mobile = SharedUtils.getString(USER_PHONE);
        }
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
        SharedUtils.putData(USER_PHONE, mobile);
    }

    public String getUserIcon() {
        if(TextUtils.isEmpty(userIcon)){
            userIcon = SharedUtils.getString(USER_ICON);
        }
        return userIcon;
    }

    public void setUserIcon(String userIcon) {
        this.userIcon = userIcon;
        SharedUtils.putData(USER_ICON,userIcon);
    }

    public String getNickName() {
        if(TextUtils.isEmpty(nickName)){
            nickName = SharedUtils.getString(USER_NAME);
        }
        return nickName;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
        SharedUtils.putData(USER_NAME,nickName);
    }

    public String getCard() {
        if(TextUtils.isEmpty(card)){
            card = SharedUtils.getString(USER_CARD);
        }
        return card;
    }

    public void setCard(String card) {
        this.card = card;
        SharedUtils.putData(USER_CARD,card);
    }

    public String getPosition() {
        if(TextUtils.isEmpty(position)){
            position = SharedUtils.getString(USER_POSITION);
        }
        return position;
    }

    public void setPosition(String position) {
        this.position = position;
        SharedUtils.putData(USER_POSITION,position);
    }

    public String getEmail() {
        if(TextUtils.isEmpty(email)){
            email = SharedUtils.getString(USER_EMAIL);
        }
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
        SharedUtils.putData(USER_EMAIL,email);
    }

    public String getDepartmentId() {
        if(TextUtils.isEmpty(departmentId)){
            departmentId = SharedUtils.getString(USER_DEPARTMENT_ID);
        }
        return departmentId;
    }

    public void setDepartmentId(String departmentId) {
        this.departmentId = departmentId;
        SharedUtils.putData(USER_DEPARTMENT_ID,departmentId);
    }

    public String getDepartName() {
        if(TextUtils.isEmpty(departName)){
            departName = SharedUtils.getString(USER_DEPARTMENT);
        }
        return departName;
    }

    public void setDepartName(String departName) {
        this.departName = departName;
        SharedUtils.putData(USER_DEPARTMENT,departName);
    }

    public String getUserSignature() {
        if(TextUtils.isEmpty(userSignature)){
            userSignature = SharedUtils.getString(USER_SIGNATURE);
        }
        return userSignature;
    }

    public void setUserSignature(String userSignature) {
        this.userSignature = userSignature;
        SharedUtils.putData(USER_SIGNATURE,userSignature);
    }

    public String getCity() {
        if(TextUtils.isEmpty(city)){
            city = SharedUtils.getString(USER_CITY);
        }
        return city;
    }

    public void setCity(String city) {
        this.city = city;
        SharedUtils.putData(USER_CITY,city);
    }

    public int getGender() {
        if(gender==-1){
            gender = SharedUtils.getInt(USER_GENDER,0);
        }
        return gender;
    }

    public void setGender(int gender) {
        this.gender = gender;
        SharedUtils.putData(USER_GENDER,gender);
    }

    public void clear(){
        SharedUtils.removeData(USER_ID);
        SharedUtils.removeData(USER_SIGN);
        SharedUtils.removeData(USER_DEPARTMENT);
        SharedUtils.removeData(USER_EMAIL);
        SharedUtils.removeData(USER_PHONE);
        SharedUtils.removeData(USER_POSITION);
        SharedUtils.removeData(USER_CARD);
        SharedUtils.removeData(USER_ICON);
        SharedUtils.removeData(USER_NAME);
        SharedUtils.removeData(USER_TOKEN);
        SharedUtils.removeData(USER_SIGNATURE);
        SharedUtils.removeData(USER_CITY);
    }
}
