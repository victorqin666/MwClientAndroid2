package com.work.api.open.contacts;


/**
 * Created by tangyx on 15/9/18.
 *
 */
public final class ModeApi {
    /**
     *短信验证码
     */
    public final static String sendSms = "user/sendSms";
    /**
     * 注册
     */
    public final static String register = "user/register";
    /**
     * 忘记密码
     */
    public final static String resetPwd = "user/resetPwd";
    /**
     * 修改密码
     */
    public final static String updatePwd = "user/updatePwd";
    /**
     * 登录
     */
    public final static String login ="user/login";
    /**
     * 修改信息
     */
    public final static String update="user/update";
    /**
     * 获取用户信息
     */
    public final static String getUserByUserId="user/getUserByUserId";
    /**
     * 获取指定人的信息
     */
    public final static String getUserByMobile = "user/getUserByMobile";
    /**
     * 上传文件
     */
    public final static String upload = "api/upload";
    /**
     * 搜索朋友
     */
    public final static String getUserByParam="user/getUserByParam";
    /**
     * 修改手机号
     */
    public final static String updateMobile="user/updateMobile";
    /**
     * 工具箱接口
     */
    public final static String getToolListByUserId = "tool/getToolListByUserId";
    /**
     * 创建群
     */
    public final static String createGroup="group/createGroup";
    /**
     * 更新群资料
     */
    public final static String updateGroup="group/updateGroup";
    /**
     * 解散群组
     */
    public final static String destroyGroup = "group/destroyGroup";
    /**
     * 获取群信息
     */
    public final static String getGroupMsg = "group/getGroupMsg";
    /**
     * 获取版本信息
     */
    public final static String getVersion = "api/getVersion";
    /**
     * 获取应用的token
     */
    public final static String getToolToken = "tool/getToolToken";
    /**
     * 搜索
     */
    public final static String getFriendByMobile="user/getFriendByMobile";
    /**
     * 同步User
     */
    public final static String sysUser = "api/sysUser";
    /**
     * 统计报表
     */
    public final static String addApplyStatics = "apply/addApplyStatics";
    /**
     * 验证JSKey
     */
    public final static String checkToolToken = "api/checkToolToken";
    /**
     *匹配手机通讯录
     */
    public final static String getUserListByMobiles="user/getUserListByMobiles";
    /**
     * 邀请下载
     */
    public final static String inviteUser="user/inviteUser";
    /**
     * 获取城市列表
     */
    public final static String getCityList="api/getCityList";
    /**
     * 退出登录
     */
    public final static String logout = "user.logout";
}
