package com.mwim.qcloud.tim.uikit.modules.chat;


import com.mwim.qcloud.tim.uikit.modules.chat.base.ChatInfo;
import com.mwim.qcloud.tim.uikit.modules.chat.base.ChatManagerKit;

public class C2CChatManagerKit extends ChatManagerKit {

    private static C2CChatManagerKit mKit;
    private ChatInfo mCurrentChatInfo;
    private Chat2C2Handler mChat2C2Handler;

    private C2CChatManagerKit() {
        super.init();
    }

    public static C2CChatManagerKit getInstance() {
        if (mKit == null) {
            mKit = new C2CChatManagerKit();
        }
        return mKit;
    }

    @Override
    public void destroyChat() {
        super.destroyChat();
        mCurrentChatInfo = null;
        mIsMore = true;
        mChat2C2Handler = null;
    }

    @Override
    public ChatInfo getCurrentChatInfo() {
        return mCurrentChatInfo;
    }

    @Override
    public void setCurrentChatInfo(ChatInfo info) {
        super.setCurrentChatInfo(info);
        mCurrentChatInfo = info;
    }

    public void setChat2C2Handler(Chat2C2Handler handler){
        this.mChat2C2Handler = handler;
    }

    public void onChat2C2RemarkChange(String remark){
        if(mChat2C2Handler!=null){
            mChat2C2Handler.onChatRemarkChange(remark);
        }
    }

    @Override
    protected boolean isGroup() {
        return false;
    }

    public interface Chat2C2Handler{
        void onChatRemarkChange(String name);
    }
}
