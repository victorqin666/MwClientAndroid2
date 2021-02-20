package com.work.api.open.model;

import com.work.api.open.model.client.OpenGroupMember;

import java.util.List;

/**
 * Created by tangyx
 * Date 2020/9/29
 * email tangyx@live.com
 */

public class CreateGroupReq extends BaseReq {
    public String GroupId;
    public String Owner_Account;
    public String Type="Private";
    public String Name;
    public String Introduction;
    public String Notification;
    public String FaceUrl;
    public String MaxMemberCount;
    public String ApplyJoinOption;
    public List<OpenGroupMember> MemberList;

}
