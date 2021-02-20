package com.work.api.open.model.client;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.http.network.model.ClientModel;

import java.util.List;

/**
 * Created by tangyx
 * Date 2020/9/13
 * email tangyx@live.com
 */

@JsonIgnoreProperties(ignoreUnknown = true)
public class OpenData extends ClientModel {

    /**
     * userId : 93c97cd2f1ababc7b6b0c91277a12a13
     * userSign : eJwtjNsKgkAURf9lnsNmDmecRugpulBBF5PwMZpRD5WOZmFE-56oj2sv1v6y0zb03rZiAQOPs1HHZGxeU0LdLJRSqBFgIgf9NLeLc2RYIJBzBClR98Y2jirLAp-3XNOjJSG11koDwtBT2v665J7mTbYIj591dC4R5sVLRO5a7jcVLFezcZqZ*lDAzo*n7PcHtNoxAg__
     */

    private String userId;
    private String userSign;
    /**
     * id : 1305013785716416514
     * userIcon : null
     * companyId : jiangpeng00001
     * departmentId : jiangpeng00002
     * mobile : 17774942284
     * nickName : 汤
     * card : 1145882254
     * position : 工程师
     * email : null
     * password : 03179606eb45fd5bec307b2d6587c37e
     * longitude : null
     * dimension :
     * createTime : null
     * createId : null
     * lastModifyId : null
     * lastModifyTime : null
     * departName : 平台研发中心
     * companyName : 元知智能研究院
     */

    private long id;
    private String userIcon;
    private String companyId;
    private String departmentId;
    private String mobile;
    private String nickName;
    private String card;
    private String position;
    private String email;
    private String password;
    private String longitude;
    private String dimension;
    private String createTime;
    private String createId;
    private String lastModifyId;
    private String lastModifyTime;
    private String departName;
    private String companyName;
    private String remark;
    /**
     * orderNum : 0
     * chargeMobile :
     * toolDesc : 抖音短视频,一个旨在帮助大众用户表达自我,记录美好生活的短视频分享平台。为用户创造丰富多样的玩法,让用户在生活中轻松快速产出优质短视频。
     * toolCode :
     * deleteStatus : 0
     * iconUrl : http://118.31.108.13:8008/toolicon/douyin.jpg
     * id : 0
     * chargeName :
     * toolUrl : https://www.douyin.com/
     * status : 0
     * toolName : 抖音
     */

    private int orderNum;
    private String chargeMobile;
    private String toolDesc;
    private String toolCode;
    private int deleteStatus;
    private String iconUrl;
    private String chargeName;
    private String toolUrl;
    private int status;
    private String toolName;
    public String GroupId;
    public String Name;
    public List<OpenGroupInfo> GroupInfo;
    private String url;
    private int functionPerm;
    private int userType;
    private String city;
    private String userSignature;
    private int gender;

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getUserSignature() {
        return userSignature;
    }

    public void setUserSignature(String userSignature) {
        this.userSignature = userSignature;
    }

    public int getGender() {
        return gender;
    }

    public void setGender(int gender) {
        this.gender = gender;
    }

    public void setFunctionPerm(int functionPerm) {
        this.functionPerm = functionPerm;
    }

    public int getUserType() {
        return userType;
    }

    public void setUserType(int userType) {
        this.userType = userType;
    }

    public int getFunctionPerm() {
        return functionPerm;
    }

    public String getGroupId() {
        return GroupId;
    }

    public void setGroupId(String groupId) {
        GroupId = groupId;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public List<OpenGroupInfo> getGroupInfo() {
        return GroupInfo;
    }

    public void setGroupInfo(List<OpenGroupInfo> groupInfo) {
        GroupInfo = groupInfo;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getUrl() {
        return url;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUserSign() {
        return userSign;
    }

    public void setUserSign(String userSign) {
        this.userSign = userSign;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getUserIcon() {
        return userIcon;
    }

    public void setUserIcon(String userIcon) {
        this.userIcon = userIcon;
    }

    public String getCompanyId() {
        return companyId;
    }

    public void setCompanyId(String companyId) {
        this.companyId = companyId;
    }

    public String getDepartmentId() {
        return departmentId;
    }

    public void setDepartmentId(String departmentId) {
        this.departmentId = departmentId;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getNickName() {
        return nickName;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
    }

    public String getCard() {
        return card;
    }

    public void setCard(String card) {
        this.card = card;
    }

    public String getPosition() {
        return position;
    }

    public void setPosition(String position) {
        this.position = position;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public String getDimension() {
        return dimension;
    }

    public void setDimension(String dimension) {
        this.dimension = dimension;
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    public String getCreateId() {
        return createId;
    }

    public void setCreateId(String createId) {
        this.createId = createId;
    }

    public String getLastModifyId() {
        return lastModifyId;
    }

    public void setLastModifyId(String lastModifyId) {
        this.lastModifyId = lastModifyId;
    }

    public String getLastModifyTime() {
        return lastModifyTime;
    }

    public void setLastModifyTime(String lastModifyTime) {
        this.lastModifyTime = lastModifyTime;
    }

    public String getDepartName() {
        return departName;
    }

    public void setDepartName(String departName) {
        this.departName = departName;
    }

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    public int getOrderNum() {
        return orderNum;
    }

    public void setOrderNum(int orderNum) {
        this.orderNum = orderNum;
    }

    public String getChargeMobile() {
        return chargeMobile;
    }

    public void setChargeMobile(String chargeMobile) {
        this.chargeMobile = chargeMobile;
    }

    public String getToolDesc() {
        return toolDesc;
    }

    public void setToolDesc(String toolDesc) {
        this.toolDesc = toolDesc;
    }

    public String getToolCode() {
        return toolCode;
    }

    public void setToolCode(String toolCode) {
        this.toolCode = toolCode;
    }

    public int getDeleteStatus() {
        return deleteStatus;
    }

    public void setDeleteStatus(int deleteStatus) {
        this.deleteStatus = deleteStatus;
    }

    public String getIconUrl() {
        return iconUrl;
    }

    public void setIconUrl(String iconUrl) {
        this.iconUrl = iconUrl;
    }

    public String getChargeName() {
        return chargeName;
    }

    public void setChargeName(String chargeName) {
        this.chargeName = chargeName;
    }

    public String getToolUrl() {
        return toolUrl;
    }

    public void setToolUrl(String toolUrl) {
        this.toolUrl = toolUrl;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getToolName() {
        return toolName;
    }

    public void setToolName(String toolName) {
        this.toolName = toolName;
    }
}
