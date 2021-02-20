package com.mwim.qcloud.tim.uikit.modules.message;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaMetadataRetriever;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.webkit.MimeTypeMap;

import com.google.gson.Gson;
import com.tencent.imsdk.v2.V2TIMCustomElem;
import com.tencent.imsdk.v2.V2TIMDownloadCallback;
import com.tencent.imsdk.v2.V2TIMElem;
import com.tencent.imsdk.v2.V2TIMFaceElem;
import com.tencent.imsdk.v2.V2TIMFileElem;
import com.tencent.imsdk.v2.V2TIMGroupChangeInfo;
import com.tencent.imsdk.v2.V2TIMGroupMemberChangeInfo;
import com.tencent.imsdk.v2.V2TIMGroupMemberInfo;
import com.tencent.imsdk.v2.V2TIMGroupTipsElem;
import com.tencent.imsdk.v2.V2TIMImageElem;
import com.tencent.imsdk.v2.V2TIMManager;
import com.tencent.imsdk.v2.V2TIMMessage;
import com.tencent.imsdk.v2.V2TIMSoundElem;
import com.tencent.imsdk.v2.V2TIMTextElem;
import com.tencent.imsdk.v2.V2TIMVideoElem;
import com.mwim.liteav.model.CallModel;
import com.mwim.qcloud.tim.uikit.utils.DateTimeUtil;
import com.mwim.qcloud.tim.uikit.utils.FileUtil;
import com.mwim.qcloud.tim.uikit.utils.ImageUtil;
import com.mwim.qcloud.tim.uikit.utils.IMKitConstants;
import com.mwim.qcloud.tim.uikit.utils.TUIKitLog;
import com.work.util.SLog;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import static com.mwim.qcloud.tim.uikit.utils.IMKitConstants.BUSINESS_ID_CUSTOM_CARD;
import static com.mwim.qcloud.tim.uikit.utils.IMKitConstants.BUSINESS_ID_CUSTOM_LOCATION;

public class MessageInfoUtil {

    /**
     * 创建一条文本消息
     *
     * @param message 消息内容
     * @return
     */
    public static MessageInfo buildTextMessage(String message) {
        MessageInfo info = new MessageInfo();
        V2TIMMessage v2TIMMessage = V2TIMManager.getMessageManager().createTextMessage(message);
        info.setExtra(message);
        info.setMsgTime(System.currentTimeMillis() / 1000);
        info.setSelf(true);
        info.setTimMessage(v2TIMMessage);
        info.setFromUser(V2TIMManager.getInstance().getLoginUser());
        info.setMsgType(MessageInfo.MSG_TYPE_TEXT);
        return info;
    }

    public static MessageInfo buildTextAtMessage(List<String> atUserList, String message) {
        MessageInfo info = new MessageInfo();
        V2TIMMessage v2TIMMessage = V2TIMManager.getMessageManager().createTextAtMessage(message, atUserList);

        info.setExtra(message);
        info.setMsgTime(System.currentTimeMillis() / 1000);
        info.setSelf(true);
        info.setTimMessage(v2TIMMessage);
        info.setFromUser(V2TIMManager.getInstance().getLoginUser());
        info.setMsgType(MessageInfo.MSG_TYPE_TEXT);
        return info;
    }

    /**
     * 创建一条自定义表情的消息
     *
     * @param groupId  自定义表情所在的表情组id
     * @param faceName 表情的名称
     * @return
     */
    public static MessageInfo buildCustomFaceMessage(int groupId, String faceName) {
        MessageInfo info = new MessageInfo();
        V2TIMMessage v2TIMMessage = V2TIMManager.getMessageManager().createFaceMessage(groupId, faceName.getBytes());

        info.setExtra("[自定义表情]");
        info.setMsgTime(System.currentTimeMillis() / 1000);
        info.setSelf(true);
        info.setTimMessage(v2TIMMessage);
        info.setFromUser(V2TIMManager.getInstance().getLoginUser());
        info.setMsgType(MessageInfo.MSG_TYPE_CUSTOM_FACE);
        return info;
    }

    /**
     * 创建一条图片消息
     *
     * @param uri        图片URI
     * @param compressed 是否压缩
     * @return
     */
    public static MessageInfo buildImageMessage(final Uri uri, boolean compressed) {
        final MessageInfo info = new MessageInfo();
        String path = FileUtil.getPathFromUri(uri);
        V2TIMMessage v2TIMMessage = V2TIMManager.getMessageManager().createImageMessage(path);

        info.setDataUri(uri);
        int[] size = ImageUtil.getImageSize(uri);
        info.setDataPath(path);
        info.setImgWidth(size[0]);
        info.setImgHeight(size[1]);
        info.setSelf(true);
        info.setTimMessage(v2TIMMessage);
        info.setExtra("[图片]");
        info.setMsgTime(System.currentTimeMillis() / 1000);
        info.setFromUser(V2TIMManager.getInstance().getLoginUser());
        info.setMsgType(MessageInfo.MSG_TYPE_IMAGE);
        return info;
    }

    /**
     * 创建一条视频消息
     *
     * @param imgPath   视频缩略图路径
     * @param videoPath 视频路径
     * @param width     视频的宽
     * @param height    视频的高
     * @param duration  视频的时长
     * @return
     */
    public static MessageInfo buildVideoMessage(String imgPath, String videoPath, int width, int height, long duration) {
        MessageInfo info = new MessageInfo();
        V2TIMMessage v2TIMMessage = V2TIMManager.getMessageManager().createVideoMessage(videoPath, "mp4", (int) duration / 1000, imgPath);

        Uri videoUri = Uri.fromFile(new File(videoPath));
        info.setSelf(true);
        info.setImgWidth(width);
        info.setImgHeight(height);
        info.setDataPath(imgPath);
        info.setDataUri(videoUri);
        info.setTimMessage(v2TIMMessage);
        info.setExtra("[视频]");
        info.setMsgTime(System.currentTimeMillis() / 1000);
        info.setFromUser(V2TIMManager.getInstance().getLoginUser());
        info.setMsgType(MessageInfo.MSG_TYPE_VIDEO);
        return info;
    }

    /**
     * 创建一条音频消息
     *
     * @param recordPath 音频路径
     * @param duration   音频的时长
     * @return
     */
    public static MessageInfo buildAudioMessage(String recordPath, int duration) {
        MessageInfo info = new MessageInfo();
        V2TIMMessage v2TIMMessage = V2TIMManager.getMessageManager().createSoundMessage(recordPath, duration / 1000);

        info.setDataPath(recordPath);
        info.setSelf(true);
        info.setTimMessage(v2TIMMessage);
        info.setExtra("[语音]");
        info.setMsgTime(System.currentTimeMillis() / 1000);
        info.setFromUser(V2TIMManager.getInstance().getLoginUser());
        info.setMsgType(MessageInfo.MSG_TYPE_AUDIO);
        return info;
    }

    /**
     * 创建一条文件消息
     *
     * @param fileUri 文件路径
     * @return
     */
    public static MessageInfo buildFileMessage(Uri fileUri) {
        String filePath = FileUtil.getPathFromUri(fileUri);
        File file = new File(filePath);
        if (file.exists()) {
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = true;
            BitmapFactory.decodeFile(filePath,options);
            if(options.outWidth != -1){//是图片
                return buildImageMessage(fileUri,true);
            }

            String fileExtension = MimeTypeMap.getFileExtensionFromUrl(filePath);
            String mimeType = MimeTypeMap.getSingleton().getMimeTypeFromExtension(fileExtension);
            if(mimeType!=null && mimeType.contains("video")){//是视频
                Bitmap firstFrame = ThumbnailUtils.createVideoThumbnail(filePath, MediaStore.Video.Thumbnails.MINI_KIND);
                String imagePath = FileUtil.saveBitmap("JCamera", firstFrame);
                long duration = 0;
                try {
                    MediaMetadataRetriever mediaMetadataRetriever = new MediaMetadataRetriever();
                    mediaMetadataRetriever.setDataSource(filePath);
                    duration = Integer.parseInt(mediaMetadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION));
                }catch (Exception ignore){}
                return buildVideoMessage(imagePath,filePath,firstFrame.getWidth(),firstFrame.getHeight(),duration);
            }
            MessageInfo info = new MessageInfo();
            V2TIMMessage v2TIMMessage = V2TIMManager.getMessageManager().createFileMessage(filePath, file.getName());

            info.setDataPath(filePath);
            info.setSelf(true);
            info.setTimMessage(v2TIMMessage);
            info.setExtra("[文件]");
            info.setMsgTime(System.currentTimeMillis() / 1000);
            info.setFromUser(V2TIMManager.getInstance().getLoginUser());
            info.setMsgType(MessageInfo.MSG_TYPE_FILE);
            return info;
        }
        return null;
    }
    /**
     * 创建定位消息
     */
    public static MessageInfo buildLocationMessage(String data,double longitude,double latitude){
        MessageInfo info = new MessageInfo();
        V2TIMMessage v2TIMMessage = V2TIMManager.getMessageManager().createLocationMessage(data,longitude,latitude);
        info.setSelf(true);
        info.setTimMessage(v2TIMMessage);
        info.setExtra("[位置]");
        info.setMsgTime(System.currentTimeMillis() / 1000);
        info.setFromUser(V2TIMManager.getInstance().getLoginUser());
        info.setMsgType(MessageInfo.MSG_TYPE_LOCATION);
        return info;
    }
    /**
     * 创建一条自定义消息
     *
     * @param data 自定义消息内容，可以是任何内容
     * @return
     */
    public static MessageInfo buildCustomMessage(String data) {
        MessageInfo info = new MessageInfo();
        V2TIMMessage v2TIMMessage = V2TIMManager.getMessageManager().createCustomMessage(data.getBytes());

        info.setSelf(true);
        info.setTimMessage(v2TIMMessage);
        info.setMsgTime(System.currentTimeMillis() / 1000);
        info.setMsgType(MessageInfo.MSG_TYPE_CUSTOM);
        info.setFromUser(V2TIMManager.getInstance().getLoginUser());
        info.setExtra("[自定义消息]");
        return info;
    }

    /**
     * 创建一条群消息自定义内容
     *
     * @param customMessage 消息内容
     * @return
     */
    public static V2TIMMessage buildGroupCustomMessage(String customMessage) {
        return V2TIMManager.getMessageManager().createCustomMessage(customMessage.getBytes());
    }

    /**
     * 把SDK的消息bean列表转化为TUIKit的消息bean列表
     *
     * @param timMessages SDK的群消息bean列表
     * @param isGroup     是否是群消息
     * @return
     */
    public static List<MessageInfo> TIMMessages2MessageInfos(List<V2TIMMessage> timMessages, boolean isGroup) {
        if (timMessages == null) {
            return null;
        }
        List<MessageInfo> messageInfos = new ArrayList<>();
        for (int i = 0; i < timMessages.size(); i++) {
            V2TIMMessage timMessage = timMessages.get(i);
            List<MessageInfo> info = TIMMessage2MessageInfo(timMessage);
            if (info != null) {
                messageInfos.addAll(info);
            }
        }
        return messageInfos;
    }

    /**
     * 把SDK的消息bean转化为TUIKit的消息bean
     *
     * @param timMessage SDK的群消息bean
     * @return
     */
    public static List<MessageInfo> TIMMessage2MessageInfo(V2TIMMessage timMessage) {
        if (timMessage == null || timMessage.getStatus() == V2TIMMessage.V2TIM_MSG_STATUS_HAS_DELETED || timMessage.getElemType() == V2TIMMessage.V2TIM_ELEM_TYPE_NONE) {
            return null;
        }
        List<MessageInfo> list = new ArrayList<>();
        final MessageInfo msgInfo = createMessageInfo(timMessage);
        if (msgInfo != null) {
            list.add(msgInfo);
        }
        return list;
    }

    public static boolean isOnlineIgnoredDialing(byte[] data) {
        try {
            String str = new String(data, "UTF-8");
            MessageCustom custom = new Gson().fromJson(str, MessageCustom.class);
            if (custom != null
                    && TextUtils.equals(custom.businessID, MessageCustom.BUSINESS_ID_AV_CALL)
                    && custom.version <= IMKitConstants.version) {
                return true;
            }
            return false;
        } catch (Exception e) {
            SLog.e("parse json error");
        }
        return false;
    }

    public static boolean isTyping(byte[] data) {
        try {
            String str = new String(data, StandardCharsets.UTF_8);
            MessageTyping typing = new Gson().fromJson(str, MessageTyping.class);
            if (typing != null
                    && typing.userAction == MessageTyping.TYPE_TYPING
                    && TextUtils.equals(typing.actionParam, MessageTyping.EDIT_START)) {
                return true;
            }
            return false;
        } catch (Exception e) {
            SLog.e( "parse json error");
        }
        return false;
    }

    public static MessageInfo createMessageInfo(V2TIMMessage timMessage) {
        if (timMessage == null
                || timMessage.getStatus() == V2TIMMessage.V2TIM_MSG_STATUS_HAS_DELETED
                || timMessage.getElemType() == V2TIMMessage.V2TIM_ELEM_TYPE_NONE) {
            SLog.e("ele2MessageInfo parameters error");
            return null;
        }
        final MessageInfo msgInfo = new MessageInfo();
        boolean isGroup = !TextUtils.isEmpty(timMessage.getGroupID());
        String sender = timMessage.getSender();
        msgInfo.setTimMessage(timMessage);
        msgInfo.setGroup(isGroup);
        msgInfo.setId(timMessage.getMsgID());
        msgInfo.setPeerRead(timMessage.isPeerRead());
        msgInfo.setFromUser(sender);
        if (isGroup) {
            if (!TextUtils.isEmpty(timMessage.getNameCard())) {
                msgInfo.setGroupNameCard(timMessage.getNameCard());
            }
        }
        msgInfo.setMsgTime(timMessage.getTimestamp());
        msgInfo.setSelf(sender.equals(V2TIMManager.getInstance().getLoginUser()));

        int type = timMessage.getElemType();
        if (type == V2TIMMessage.V2TIM_ELEM_TYPE_CUSTOM) {
            V2TIMCustomElem customElem = timMessage.getCustomElem();
            String data = new String(customElem.getData());
            if (data.equals(MessageCustom.BUSINESS_ID_GROUP_CREATE)) {
                // 兼容4.7版本以前的 tuikith
                msgInfo.setMsgType(MessageInfo.MSG_TYPE_GROUP_CREATE);
                String message =  (TextUtils.isEmpty(msgInfo.getTimMessage().getNameCard())?msgInfo.getTimMessage().getNickName():msgInfo.getTimMessage().getNameCard())+ " 发起了群聊";
                msgInfo.setExtra(message);
            } else {
                if (isTyping(customElem.getData())) {
                    // 忽略正在输入，它不能作为真正的消息展示
                    return null;
                }
                SLog.i( "message info util custom data:" + data);
                String content = "[自定义消息]";
                msgInfo.setMsgType(MessageInfo.MSG_TYPE_CUSTOM);
                msgInfo.setExtra(content);
                Gson gson = new Gson();
                MessageCustom messageCustom;
                try {
                    messageCustom = gson.fromJson(data, MessageCustom.class);
                    String businessId = messageCustom.businessID;
                    if (!TextUtils.isEmpty(businessId) && messageCustom.businessID.equals(MessageCustom.BUSINESS_ID_GROUP_CREATE)) {
                        msgInfo.setMsgType(MessageInfo.MSG_TYPE_GROUP_CREATE);
                        String message = IMKitConstants.covert2HTMLString(messageCustom.opUser) + messageCustom.content;
                        msgInfo.setExtra(message);
                    } else if(!TextUtils.isEmpty(businessId) && messageCustom.businessID.equals(BUSINESS_ID_CUSTOM_CARD)){
                        msgInfo.setExtra("[链接]");
                    }if(!TextUtils.isEmpty(businessId) && messageCustom.businessID.equals(BUSINESS_ID_CUSTOM_LOCATION)){//来自小程序自己发送的位置消息
                        msgInfo.setExtra("[位置]");
                        msgInfo.setMsgType(MessageInfo.MSG_TYPE_LOCATION);
                    } else {
                        CallModel callModel = CallModel.convert2VideoCallData(timMessage);
                        if (callModel != null) {
                            String senderShowName = timMessage.getSender();
                            if (!TextUtils.isEmpty(timMessage.getNameCard())) {
                                senderShowName = timMessage.getNameCard();
                            } else if (!TextUtils.isEmpty(timMessage.getFriendRemark())) {
                                senderShowName = timMessage.getFriendRemark();
                            } else if (!TextUtils.isEmpty(timMessage.getNickName())) {
                                senderShowName = timMessage.getNickName();
                            }
                            switch (callModel.action) {
                                case CallModel.VIDEO_CALL_ACTION_DIALING:
                                    content = isGroup ? ("\"" + senderShowName + "\"" + "发起群通话") : ("发起通话");
                                    if(!isGroup){
                                        msgInfo.setCallType(callModel.callType);
                                    }
                                    break;
                                case CallModel.VIDEO_CALL_ACTION_SPONSOR_CANCEL:
                                    content = isGroup ? "取消群通话" : "取消通话";
                                    if(!isGroup){
                                        msgInfo.setCallType(callModel.callType);
                                    }
                                    break;
                                case CallModel.VIDEO_CALL_ACTION_LINE_BUSY:
                                    content = isGroup ? ("\"" + senderShowName + "\"" + "忙线") : "对方忙线";
                                    if(!isGroup){
                                        msgInfo.setCallType(callModel.callType);
                                    }
                                    break;
                                case CallModel.VIDEO_CALL_ACTION_REJECT:
                                    content = isGroup ? ("\"" + senderShowName + "\"" + "拒绝群通话") : "拒绝通话";
                                    if(!isGroup){
                                        msgInfo.setCallType(callModel.callType);
                                    }
                                    break;
                                case CallModel.VIDEO_CALL_ACTION_SPONSOR_TIMEOUT:
                                    if (isGroup && callModel.invitedList != null && callModel.invitedList.size() == 1
                                            && callModel.invitedList.get(0).equals(timMessage.getSender())) {
                                        content = "\"" + senderShowName + "\"" + "无应答";
                                    } else {
//                                        StringBuilder inviteeShowStringBuilder = new StringBuilder();
                                        if (callModel.invitedList != null && callModel.invitedList.size() > 0) {
//                                            for (String invitee : callModel.invitedList) {
//                                                inviteeShowStringBuilder.append(invitee).append("、");
//                                            }
//                                            if (inviteeShowStringBuilder.length() > 0) {
//                                                inviteeShowStringBuilder.delete(inviteeShowStringBuilder.length() - 1, inviteeShowStringBuilder.length());
//                                            }
                                            content = isGroup ? ("音视频\"" + callModel.invitedList.size() + "\"" + "人无应答") : "无应答";
                                        }
//                                        content = isGroup ? ("\"" + inviteeShowStringBuilder.toString() + "\"" + "无应答") : "无应答";
                                    }
                                    break;
                                case CallModel.VIDEO_CALL_ACTION_ACCEPT:
                                    content = isGroup ? ("\"" + senderShowName + "\"" + "已接听") : "已接听";
                                    if(!isGroup){
                                        msgInfo.setCallType(callModel.callType);
                                    }
                                    break;
                                case CallModel.VIDEO_CALL_ACTION_HANGUP:
                                    content = isGroup ? "结束群通话" : "结束通话，通话时长：" + DateTimeUtil.formatSecondsTo00(callModel.duration);
                                    if(!isGroup){
                                        msgInfo.setCallType(callModel.callType);
                                    }
                                    break;
                                default:
                                    content = "不能识别的通话指令";
                                    break;
                            }
                            if (isGroup) {
                                msgInfo.setMsgType(MessageInfo.MSG_TYPE_GROUP_AV_CALL_NOTICE);
                            }else{
                                msgInfo.setMsgType(MessageInfo.MSG_TYPE_TEXT);
                            }
                            msgInfo.setExtra(content);
                        }
                    }
                } catch (Exception e) {
                    SLog.e( "invalid json: " + data + ", exception:" + e);
                }
            }
        } else if (type == V2TIMMessage.V2TIM_ELEM_TYPE_GROUP_TIPS) {
            V2TIMGroupTipsElem groupTipElem = timMessage.getGroupTipsElem();
            int tipsType = groupTipElem.getType();
            StringBuilder user = new StringBuilder();
            if (groupTipElem.getMemberList().size() > 0) {
                List<V2TIMGroupMemberInfo> v2TIMGroupMemberInfoList = groupTipElem.getMemberList();
                for (int i = 0; i < v2TIMGroupMemberInfoList.size(); i++) {
                    V2TIMGroupMemberInfo v2TIMGroupMemberInfo = v2TIMGroupMemberInfoList.get(i);
                    if (i == 0) {
                        if(!TextUtils.isEmpty(v2TIMGroupMemberInfo.getNameCard())){
                            user.append(v2TIMGroupMemberInfo.getNameCard());
                        }else if(!TextUtils.isEmpty(v2TIMGroupMemberInfo.getNickName())){
                            user.append(v2TIMGroupMemberInfo.getNickName());
                        }else{
                            user.append(v2TIMGroupMemberInfo.getUserID());
                        }
                    } else {
                        if (i == 2 && v2TIMGroupMemberInfoList.size() > 3) {
                            user.append("等");
                            break;
                        } else {
                            user.append("，");
                            if(!TextUtils.isEmpty(v2TIMGroupMemberInfo.getNameCard())){
                                user.append(v2TIMGroupMemberInfo.getNameCard());
                            }else if(!TextUtils.isEmpty(v2TIMGroupMemberInfo.getNickName())){
                                user.append(v2TIMGroupMemberInfo.getNickName());
                            }else{
                                user.append(v2TIMGroupMemberInfo.getUserID());
                            }
                        }
                    }
                }

            } else {
                if(!TextUtils.isEmpty(groupTipElem.getOpMember().getNameCard())){
                    user.append(groupTipElem.getOpMember().getNameCard());
                }else if(!TextUtils.isEmpty(groupTipElem.getOpMember().getNickName())){
                    user.append(groupTipElem.getOpMember().getNickName());
                }else{
                    user.append(groupTipElem.getOpMember().getUserID());
                }
            }
            StringBuilder message = new StringBuilder(IMKitConstants.covert2HTMLString(user.toString()));
            if (tipsType == V2TIMGroupTipsElem.V2TIM_GROUP_TIPS_TYPE_JOIN) {
                msgInfo.setMsgType(MessageInfo.MSG_TYPE_GROUP_JOIN);
                message.append("加入群组");
            }
            if (tipsType == V2TIMGroupTipsElem.V2TIM_GROUP_TIPS_TYPE_INVITE) {
                msgInfo.setMsgType(MessageInfo.MSG_TYPE_GROUP_JOIN);
                message.append("被邀请进群");
            }
            if (tipsType == V2TIMGroupTipsElem.V2TIM_GROUP_TIPS_TYPE_QUIT) {
                msgInfo.setMsgType(MessageInfo.MSG_TYPE_GROUP_QUITE);
                message.append("退出群组");
            }
            if (tipsType == V2TIMGroupTipsElem.V2TIM_GROUP_TIPS_TYPE_KICKED) {
                msgInfo.setMsgType(MessageInfo.MSG_TYPE_GROUP_KICK);
                message.append("被踢出群组");
            }
            if (tipsType == V2TIMGroupTipsElem.V2TIM_GROUP_TIPS_TYPE_SET_ADMIN) {
                msgInfo.setMsgType(MessageInfo.MSG_TYPE_GROUP_MODIFY_NOTICE);
                message.append("被设置管理员");
            }
            if (tipsType == V2TIMGroupTipsElem.V2TIM_GROUP_TIPS_TYPE_CANCEL_ADMIN) {
                msgInfo.setMsgType(MessageInfo.MSG_TYPE_GROUP_MODIFY_NOTICE);
                message.append("被取消管理员");
            }
            if (tipsType == V2TIMGroupTipsElem.V2TIM_GROUP_TIPS_TYPE_GROUP_INFO_CHANGE) {
                List<V2TIMGroupChangeInfo> modifyList = groupTipElem.getGroupChangeInfoList();
                for (int i = 0; i < modifyList.size(); i++) {
                    V2TIMGroupChangeInfo modifyInfo = modifyList.get(i);
                    int modifyType = modifyInfo.getType();
                    if (modifyType == V2TIMGroupChangeInfo.V2TIM_GROUP_INFO_CHANGE_TYPE_NAME) {
                        msgInfo.setMsgType(MessageInfo.MSG_TYPE_GROUP_MODIFY_NAME);
                        message.append("修改群名称为\"").append(modifyInfo.getValue()).append("\"");
                    } else if (modifyType == V2TIMGroupChangeInfo.V2TIM_GROUP_INFO_CHANGE_TYPE_NOTIFICATION) {
                        msgInfo.setMsgType(MessageInfo.MSG_TYPE_GROUP_MODIFY_NOTICE);
                        message.append("修改群公告为\"").append(modifyInfo.getValue()).append("\"");
                    } else if (modifyType == V2TIMGroupChangeInfo.V2TIM_GROUP_INFO_CHANGE_TYPE_OWNER) {
                        msgInfo.setMsgType(MessageInfo.MSG_TYPE_GROUP_MODIFY_NOTICE);
//                        message.append("转让群主给\"").append(modifyInfo.getValue()).append("\"");
                        message.append("已转让群主");
                    } else if (modifyType == V2TIMGroupChangeInfo.V2TIM_GROUP_INFO_CHANGE_TYPE_FACE_URL) {
                        msgInfo.setMsgType(MessageInfo.MSG_TYPE_GROUP_MODIFY_NOTICE);
                        message.append("修改了群头像");
                    } else if (modifyType == V2TIMGroupChangeInfo.V2TIM_GROUP_INFO_CHANGE_TYPE_INTRODUCTION) {
                        msgInfo.setMsgType(MessageInfo.MSG_TYPE_GROUP_MODIFY_NOTICE);
                        message.append("修改群介绍为\"").append(modifyInfo.getValue()).append("\"");
                    }
                    if (i < modifyList.size() - 1) {
                        message.append("、");
                    }
                }
            }
            if (tipsType == V2TIMGroupTipsElem.V2TIM_GROUP_TIPS_TYPE_MEMBER_INFO_CHANGE) {
                List<V2TIMGroupMemberChangeInfo> modifyList = groupTipElem.getMemberChangeInfoList();
                if (modifyList.size() > 0) {
                    long shutupTime = modifyList.get(0).getMuteTime();
                    if (shutupTime > 0) {
                        msgInfo.setMsgType(MessageInfo.MSG_TYPE_GROUP_MODIFY_NOTICE);
                        message.append("被禁言\"").append(DateTimeUtil.formatSeconds(shutupTime)).append("\"");
                    } else {
                        msgInfo.setMsgType(MessageInfo.MSG_TYPE_GROUP_MODIFY_NOTICE);
                        message.append("被取消禁言");
                    }
                }
            }
            if (TextUtils.isEmpty(message.toString())) {
                return null;
            }
            msgInfo.setExtra(message.toString());
        } else {
            if (type == V2TIMMessage.V2TIM_ELEM_TYPE_TEXT) {
                V2TIMTextElem txtEle = timMessage.getTextElem();
                msgInfo.setExtra(txtEle.getText());
            } else if (type == V2TIMMessage.V2TIM_ELEM_TYPE_FACE) {
                V2TIMFaceElem faceElem = timMessage.getFaceElem();
                if (faceElem.getIndex() < 1 || faceElem.getData() == null) {
                    TUIKitLog.e("MessageInfoUtil", "faceElem data is null or index<1");
                    return null;
                }
                msgInfo.setExtra("[自定义表情]");


            } else if (type == V2TIMMessage.V2TIM_ELEM_TYPE_SOUND) {
                V2TIMSoundElem soundElemEle = timMessage.getSoundElem();
                if (msgInfo.isSelf()) {
                    msgInfo.setDataPath(soundElemEle.getPath());
                } else {
                    final String path = IMKitConstants.RECORD_DOWNLOAD_DIR + soundElemEle.getUUID();
                    File file = new File(path);
                    if (!file.exists()) {
                        soundElemEle.downloadSound(path, new V2TIMDownloadCallback() {
                            @Override
                            public void onProgress(V2TIMElem.V2ProgressInfo progressInfo) {
                                long currentSize = progressInfo.getCurrentSize();
                                long totalSize = progressInfo.getTotalSize();
                                int progress = 0;
                                if (totalSize > 0) {
                                    progress = (int) (100 * currentSize / totalSize);
                                }
                                if (progress > 100) {
                                    progress = 100;
                                }
                                TUIKitLog.i("MessageInfoUtil getSoundToFile", "progress:" + progress);
                            }

                            @Override
                            public void onError(int code, String desc) {
                                TUIKitLog.e("MessageInfoUtil getSoundToFile", code + ":" + desc);
                            }

                            @Override
                            public void onSuccess() {
                                msgInfo.setDataPath(path);
                            }
                        });
                    } else {
                        msgInfo.setDataPath(path);
                    }
                }
                msgInfo.setExtra("[语音]");
            } else if (type == V2TIMMessage.V2TIM_ELEM_TYPE_IMAGE) {
                V2TIMImageElem imageEle = timMessage.getImageElem();
                String localPath = imageEle.getPath();
                if (msgInfo.isSelf() && !TextUtils.isEmpty(localPath)) {
                    msgInfo.setDataPath(localPath);
                    int[] size = ImageUtil.getImageSize(localPath);
                    msgInfo.setImgWidth(size[0]);
                    msgInfo.setImgHeight(size[1]);
                }
                //本地路径为空，可能为更换手机或者是接收的消息
                else {
                    List<V2TIMImageElem.V2TIMImage> imgs = imageEle.getImageList();
                    for (int i = 0; i < imgs.size(); i++) {
                        V2TIMImageElem.V2TIMImage img = imgs.get(i);
                        if (img.getType() == V2TIMImageElem.V2TIM_IMAGE_TYPE_THUMB) {
                            final String path = IMKitConstants.IMAGE_DOWNLOAD_DIR + img.getUUID();
                            msgInfo.setImgWidth(img.getWidth());
                            msgInfo.setImgHeight(img.getHeight());
                            File file = new File(path);
                            if (file.exists()) {
                                msgInfo.setDataPath(path);
                            }
                        }
                    }
                }
                msgInfo.setExtra("[图片]");
            } else if (type == V2TIMMessage.V2TIM_ELEM_TYPE_VIDEO) {
                V2TIMVideoElem videoEle = timMessage.getVideoElem();
                if (msgInfo.isSelf() && !TextUtils.isEmpty(videoEle.getSnapshotPath())) {
                    int[] size = ImageUtil.getImageSize(videoEle.getSnapshotPath());
                    msgInfo.setImgWidth(size[0]);
                    msgInfo.setImgHeight(size[1]);
                    msgInfo.setDataPath(videoEle.getSnapshotPath());
                    msgInfo.setDataUri(FileUtil.getUriFromPath(videoEle.getVideoPath()));
                } else {
                    final String videoPath = IMKitConstants.VIDEO_DOWNLOAD_DIR + videoEle.getVideoUUID();
                    Uri uri = Uri.parse(videoPath);
                    msgInfo.setDataUri(uri);
                    msgInfo.setImgWidth((int) videoEle.getSnapshotWidth());
                    msgInfo.setImgHeight((int) videoEle.getSnapshotHeight());
                    final String snapPath = IMKitConstants.IMAGE_DOWNLOAD_DIR + videoEle.getSnapshotUUID();
                    //判断快照是否存在,不存在自动下载
                    if (new File(snapPath).exists()) {
                        msgInfo.setDataPath(snapPath);
                    }
                }

                msgInfo.setExtra("[视频]");
            } else if (type == V2TIMMessage.V2TIM_ELEM_TYPE_FILE) {
                V2TIMFileElem fileElem = timMessage.getFileElem();
                String filename = fileElem.getUUID();
                if (TextUtils.isEmpty(filename)) {
                    filename = System.currentTimeMillis() + fileElem.getFileName();
                }
                final String path = IMKitConstants.FILE_DOWNLOAD_DIR + filename;
                File file = new File(path);
                if (file.exists()) {
                    if (msgInfo.isSelf()) {
                        msgInfo.setStatus(MessageInfo.MSG_STATUS_SEND_SUCCESS);
                    } else {
                        msgInfo.setStatus(MessageInfo.MSG_STATUS_DOWNLOADED);
                    }
                    msgInfo.setDataPath(path);
                } else {
                    if (msgInfo.isSelf()) {
                        if (TextUtils.isEmpty(fileElem.getPath())) {
                            msgInfo.setStatus(MessageInfo.MSG_STATUS_UN_DOWNLOAD);
                            msgInfo.setDataPath(path);
                        } else {
                            file = new File(fileElem.getPath());
                            if (file.exists()) {
                                msgInfo.setStatus(MessageInfo.MSG_STATUS_SEND_SUCCESS);
                                msgInfo.setDataPath(fileElem.getPath());
                            } else {
                                msgInfo.setStatus(MessageInfo.MSG_STATUS_UN_DOWNLOAD);
                                msgInfo.setDataPath(path);
                            }
                        }
                    } else {
                        msgInfo.setStatus(MessageInfo.MSG_STATUS_UN_DOWNLOAD);
                        msgInfo.setDataPath(path);
                    }
                }
                msgInfo.setExtra("[文件]");
            }else if(type == V2TIMMessage.V2TIM_ELEM_TYPE_LOCATION){
                msgInfo.setExtra("[位置]");
            }
            msgInfo.setMsgType(TIMElemType2MessageInfoType(type));
        }

        if (timMessage.getStatus() == V2TIMMessage.V2TIM_MSG_STATUS_LOCAL_REVOKED) {
            msgInfo.setStatus(MessageInfo.MSG_STATUS_REVOKE);
            msgInfo.setMsgType(MessageInfo.MSG_STATUS_REVOKE);
            if (msgInfo.isSelf()) {
                msgInfo.setExtra("您撤回了一条消息");
            } else if (msgInfo.isGroup()) {
                String name = msgInfo.getGroupNameCard();
                if(TextUtils.isEmpty(name)){
                    name = msgInfo.getTimMessage().getNameCard();
                }
                if(TextUtils.isEmpty(name)){
                    name = msgInfo.getTimMessage().getNickName();
                }
                String message = IMKitConstants.covert2HTMLString(name);
                msgInfo.setExtra(message + "撤回了一条消息");
            } else {
                msgInfo.setExtra("对方撤回了一条消息");
            }
        } else {
            if (msgInfo.isSelf()) {
                if (timMessage.getStatus() == V2TIMMessage.V2TIM_MSG_STATUS_SEND_FAIL) {
                    msgInfo.setStatus(MessageInfo.MSG_STATUS_SEND_FAIL);
                } else if (timMessage.getStatus() == V2TIMMessage.V2TIM_MSG_STATUS_SEND_SUCC) {
                    msgInfo.setStatus(MessageInfo.MSG_STATUS_SEND_SUCCESS);
                } else if (timMessage.getStatus() == V2TIMMessage.V2TIM_MSG_STATUS_SENDING) {
                    msgInfo.setStatus(MessageInfo.MSG_STATUS_SENDING);
                }
            }
        }
        return msgInfo;
    }

    private static int TIMElemType2MessageInfoType(int type) {
        switch (type) {
            case V2TIMMessage.V2TIM_ELEM_TYPE_TEXT:
                return MessageInfo.MSG_TYPE_TEXT;
            case V2TIMMessage.V2TIM_ELEM_TYPE_IMAGE:
                return MessageInfo.MSG_TYPE_IMAGE;
            case V2TIMMessage.V2TIM_ELEM_TYPE_SOUND:
                return MessageInfo.MSG_TYPE_AUDIO;
            case V2TIMMessage.V2TIM_ELEM_TYPE_VIDEO:
                return MessageInfo.MSG_TYPE_VIDEO;
            case V2TIMMessage.V2TIM_ELEM_TYPE_FILE:
                return MessageInfo.MSG_TYPE_FILE;
            case V2TIMMessage.V2TIM_ELEM_TYPE_LOCATION:
                return MessageInfo.MSG_TYPE_LOCATION;
            case V2TIMMessage.V2TIM_ELEM_TYPE_FACE:
                return MessageInfo.MSG_TYPE_CUSTOM_FACE;
            case V2TIMMessage.V2TIM_ELEM_TYPE_GROUP_TIPS:
                return MessageInfo.MSG_TYPE_TIPS;
        }

        return -1;
    }
}
