package com.work.api.open.model.client;

import com.http.network.model.ClientModel;

/**
 * Created by tangyx
 * Date 2020/9/29
 * email tangyx@live.com
 */

@com.fasterxml.jackson.annotation.JsonIgnoreProperties(ignoreUnknown = true)
public class OpenGroupInfo extends ClientModel {

    /**
     * Appid : 1400425549
     * ApplyJoinOption : DisableApply
     * CreateTime : 1601350519
     * ErrorCode : 0
     * FaceUrl :
     * GroupId : @TGS#1AL54EXGX
     * InfoSeq : 14
     * Introduction :
     * LastInfoTime : 1601351166
     * LastMsgTime : 1601351166
     * MaxMemberNum : 20
     * MemberList : [{"JoinTime":1601350519,"LastSendMsgTime":1601350519,"Member_Account":"a7ab2c8a3d95ff5e953f87ca534bf125","MsgFlag":"AcceptAndNotify","MsgSeq":4,"Role":"Owner","ShutUpUntil":0}]
     * MemberNum : 1
     * Name : 嚣张、卡哦
     * NextMsgSeq : 5
     * Notification :
     * OnlineMemberNum : 0
     * Owner_Account : a7ab2c8a3d95ff5e953f87ca534bf125
     * ShutUpAllMember : Off
     * Type : Private
     */

    public int Appid;
    public String ApplyJoinOption;
    public int CreateTime;
    public int ErrorCode;
    public String FaceUrl;
    public String GroupId;
    public int InfoSeq;
    public String Introduction;
    public int LastInfoTime;
    public int LastMsgTime;
    public int MaxMemberNum;
    public int MemberNum;
    public String Name;
    public int NextMsgSeq;
    public String Notification;
    public int OnlineMemberNum;
    public String Owner_Account;
    public String ShutUpAllMember;
    public String Type;
}
