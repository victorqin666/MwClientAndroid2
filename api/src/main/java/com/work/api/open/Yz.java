package com.work.api.open;


import com.http.network.RequestParams;
import com.http.network.listener.OnResultDataListener;
import com.http.network.task.ConnectDataTask;
import com.work.api.open.contacts.ModeApi;
import com.work.api.open.model.AddApplyStaticsReq;
import com.work.api.open.model.BaseReq;
import com.work.api.open.model.BaseResp;
import com.work.api.open.model.CheckToolTokenReq;
import com.work.api.open.model.CheckToolTokenResp;
import com.work.api.open.model.CreateGroupReq;
import com.work.api.open.model.CreateGroupResp;
import com.work.api.open.model.DestroyGroupReq;
import com.work.api.open.model.GetCarWebViewUrlResp;
import com.work.api.open.model.GetCityListResp;
import com.work.api.open.model.GetFriendByMobileResp;
import com.work.api.open.model.GetGroupMsgReq;
import com.work.api.open.model.GetGroupMsgResp;
import com.work.api.open.model.GetToolListByUserIdResp;
import com.work.api.open.model.GetToolTokenReq;
import com.work.api.open.model.GetToolTokenResp;
import com.work.api.open.model.GetUserByParamReq;
import com.work.api.open.model.GetUserByParamResp;
import com.work.api.open.model.GetUserListByMobilesReq;
import com.work.api.open.model.GetUserListByMobilesResp;
import com.work.api.open.model.GetVersionResp;
import com.work.api.open.model.InviteUserReq;
import com.work.api.open.model.LoginReq;
import com.work.api.open.model.LoginResp;
import com.work.api.open.model.RegisterReq;
import com.work.api.open.model.SendSmsReq;
import com.work.api.open.model.SysUserReq;
import com.work.api.open.model.UpdateMobileReq;
import com.work.api.open.model.UploadResp;
import com.work.util.SLog;
import com.work.util.SharedUtils;

import java.io.File;

/**
 * Created by Administrator on 2019/4/2
 * Description
 */

public class Yz extends ApiClient {

    private static Yz INSTANCE;
    public static Yz getSession(){
        return INSTANCE==null?INSTANCE = new Yz():INSTANCE;
    }
    /**
     * 发送验证码
     */
    public void sendSms(SendSmsReq sendSmsReq, OnResultDataListener onResultDataListener){
        requestPost(ModeApi.sendSms,sendSmsReq,new BaseResp(),onResultDataListener);
    }
    /**
     * 注册
     */
    public void register(RegisterReq registerReq,OnResultDataListener onResultDataListener){
        requestPost(ModeApi.register,registerReq,new LoginResp(),onResultDataListener);
    }
    /**
     * 忘记密码
     */
    public void resetPwd(RegisterReq registerReq,OnResultDataListener onResultDataListener){
        requestPost(ModeApi.resetPwd,registerReq,new BaseResp(),onResultDataListener);
    }
    /**
     * 登录
     */
    public void login(LoginReq loginReq,OnResultDataListener onResultDataListener){
        requestPost(ModeApi.login,loginReq,new LoginResp(),onResultDataListener);
    }
    /**
     * 修改信息
     */
    public void update(RegisterReq registerReq,OnResultDataListener onResultDataListener,Object... objects){
        requestPost(ModeApi.update,registerReq,new BaseResp(),onResultDataListener,objects);
    }
    /**
     * 修改密码
     */
    public void updatePwd(RegisterReq registerReq,OnResultDataListener onResultDataListener){
        requestPost(ModeApi.updatePwd,registerReq,new BaseResp(),onResultDataListener);
    }
    /**
     * 获取用户基本信息
     */
    public void getUserByUserId(LoginReq loginReq,OnResultDataListener onResultDataListener){
        requestPost(ModeApi.getUserByUserId,loginReq,new LoginResp(),onResultDataListener);
    }
    /**
     * 获取指定手机号的信息
     */
    public void getUserByMobile(LoginReq loginReq,OnResultDataListener onResultDataListener){
        requestPost(ModeApi.getUserByMobile,loginReq,new LoginResp(),onResultDataListener);
    }
    /**
     * 搜索朋友
     */
    public void getUserByParam(GetUserByParamReq getUserByParamReq,OnResultDataListener onResultDataListener){
        requestPost(ModeApi.getUserByParam,getUserByParamReq,new GetUserByParamResp(),onResultDataListener);
    }
    /**
     * 修改手机号
     */
    public void updateMobile(UpdateMobileReq registerReq, OnResultDataListener onResultDataListener){
        requestPost(ModeApi.updateMobile,registerReq,new BaseResp(),onResultDataListener);
    }
    /**
     * 工具箱
     */
    public void getToolListByUserId(OnResultDataListener onResultDataListener){
        requestPost(ModeApi.getToolListByUserId,new BaseReq(),new GetToolListByUserIdResp(),onResultDataListener);
    }
    /**
     * 创建群
     */
    public void createGroup(CreateGroupReq createGroupReq,OnResultDataListener onResultDataListener){
        requestPost(ModeApi.createGroup,createGroupReq,new CreateGroupResp(),onResultDataListener);
    }
    /**
     * 解散群组
     */
    public void destroyGroup(DestroyGroupReq destroyGroupReq,OnResultDataListener onResultDataListener){
        requestPost(ModeApi.destroyGroup,destroyGroupReq,new BaseResp(),onResultDataListener);
    }
    /**
     * 获取群信息
     */
    public void getGroupMsg(GetGroupMsgReq getGroupMsgReq, OnResultDataListener onResultDataListener){
        requestPost(ModeApi.getGroupMsg,getGroupMsgReq,new GetGroupMsgResp(),onResultDataListener);
    }
    /**
     * 修改群信息
     */
    public void updateGroup(CreateGroupReq createGroupReq,OnResultDataListener onResultDataListener){
        requestPost(ModeApi.updateGroup,createGroupReq,new BaseResp(),onResultDataListener);
    }
    /**
     * 获取版本信息
     */
    public void getVersion(OnResultDataListener onResultDataListener){
        requestPost(ModeApi.getVersion,new BaseReq(),new GetVersionResp(),onResultDataListener);
    }
    /**
     * 获取token
     */
    public void getToolToken(GetToolTokenReq getToolTokenReq,OnResultDataListener onResultDataListener,Object... objects){
        requestPost(ModeApi.getToolToken,getToolTokenReq,new GetToolTokenResp(),onResultDataListener,objects);
    }
    /**
     * 搜索我的朋友
     */
    public void getFriendByMobile(GetUserByParamReq getUserByParamReq,OnResultDataListener onResultDataListener){
        requestPost(ModeApi.getFriendByMobile,getUserByParamReq,new GetFriendByMobileResp(),onResultDataListener);
    }
    /**
     * 同步数据
     */
    public void sysUser(SysUserReq sysUserReq,OnResultDataListener onResultDataListener){
        requestPost(ModeApi.sysUser,sysUserReq,new LoginResp(),onResultDataListener);
    }
    /**
     * 统计报表
     */
    public void addApplyStatics(AddApplyStaticsReq addApplyStaticsReq,OnResultDataListener onResultDataListener){
        requestPost(ModeApi.addApplyStatics,addApplyStaticsReq,new BaseResp(),onResultDataListener);
    }
    /**
     * 验证JSKey
     */
    public void checkToolToken(CheckToolTokenReq checkToolTokenReq,OnResultDataListener onResultDataListener){
        requestPost(ModeApi.checkToolToken,checkToolTokenReq,new CheckToolTokenResp(),onResultDataListener);
    }
    /**
     * 匹配手机通讯录
     */
    public void getUserListByMobiles(GetUserListByMobilesReq getUserListByMobilesReq,OnResultDataListener onResultDataListener){
        requestPost(ModeApi.getUserListByMobiles,getUserListByMobilesReq,new GetUserListByMobilesResp(),onResultDataListener);
    }
    /**
     * 邀请下载
     */
    public void inviteUser(InviteUserReq inviteUserReq,OnResultDataListener onResultDataListener){
        requestPost(ModeApi.inviteUser,inviteUserReq,new BaseResp(),onResultDataListener);
    }
    /**
     * 获取城市
     */
    public void getCityList(OnResultDataListener onResultDataListener){
        requestPost(ModeApi.getCityList,new BaseReq(),new GetCityListResp(),onResultDataListener);
    }
    /**
     * 获取打车链接
     */
    public void getCarWebViewUrl(String url,OnResultDataListener onResultDataListener){
        RequestParams params = new RequestParams();
        params.url = url;
        params.onResultDataListener = onResultDataListener;
        params.req = new BaseReq();
        params.resp = new GetCarWebViewUrlResp();
        ConnectDataTask dataTask = new ConnectDataTask(params);
        dataTask.doGet();
    }
    /**
     * 上传文件
     */
    public void upload(String path,OnResultDataListener onResultDataListener){
        RequestParams params = new RequestParams();
        params.url = clientModelUrl(ModeApi.upload);
        params.onResultDataListener = onResultDataListener;
        params.req = new BaseReq();
        params.resp = new UploadResp();
        if(SLog.debug)SLog.e("Upload File:"+path);
        params.addFileParam("file",new File(path));
        String token = SharedUtils.getString("userToken");
        params.addTextParam("token",token);
        params.addHeader("token",token);
        ConnectDataTask dataTask = new ConnectDataTask(params);
        dataTask.uploadFile();
    }
}
