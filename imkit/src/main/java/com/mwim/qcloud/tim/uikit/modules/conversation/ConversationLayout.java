package com.mwim.qcloud.tim.uikit.modules.conversation;

import android.content.Context;
import android.content.Intent;
import android.util.AttributeSet;
import android.view.View;
import android.widget.EditText;
import android.widget.RelativeLayout;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.mwim.qcloud.tim.uikit.TUIKit;
import com.mwim.qcloud.tim.uikit.YzIMKitAgent;
import com.mwim.qcloud.tim.uikit.business.MorePopWindow;
import com.mwim.qcloud.tim.uikit.business.active.ScanIMQRCodeActivity;
import com.mwim.qcloud.tim.uikit.business.active.SearchAddMoreActivity;
import com.mwim.qcloud.tim.uikit.business.active.StartGroupChatActivity;
import com.mwim.qcloud.tim.uikit.modules.conversation.base.ConversationInfo;
import com.mwim.qcloud.tim.uikit.modules.conversation.interfaces.IConversationAdapter;
import com.mwim.qcloud.tim.uikit.modules.conversation.interfaces.IConversationLayout;
import com.mwim.qcloud.tim.uikit.R;
import com.mwim.qcloud.tim.uikit.base.IUIKitCallBack;
import com.mwim.qcloud.tim.uikit.component.TitleBarLayout;
import com.mwim.qcloud.tim.uikit.utils.IMKitConstants;
import com.work.util.ToastUtil;

import java.util.ArrayList;
import java.util.List;

public class ConversationLayout extends RelativeLayout implements IConversationLayout {

    private ConversationListLayout mConversationList;
    private MorePopWindow mMenu;

    public ConversationLayout(Context context) {
        super(context);
        init();
    }

    public ConversationLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public ConversationLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    /**
     * 初始化相关UI元素
     */
    private void init() {
        inflate(getContext(), R.layout.conversation_layout, this);
        EditText mSearch = findViewById(R.id.search);
        mSearch.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                SearchAddMoreActivity.startSearchMore(getContext(),1);
            }
        });
        mConversationList = findViewById(R.id.conversation_list);
    }
    private IConversationAdapter adapter;
    public void initDefault() {
        final View mAddMore = findViewById(R.id.add_more);
        int functionPrem = YzIMKitAgent.instance().getFunctionPrem();
        if((functionPrem & 2)>0){
            mAddMore.setVisibility(VISIBLE);
            mAddMore.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(mMenu==null){
                        List<String> item = new ArrayList<>();
                        item.add(getContext().getResources().getString(R.string.add_friend));
                        item.add(getContext().getResources().getString(R.string.add_group));
                        item.add(getContext().getResources().getString(R.string.scan_qr_code));
                        mMenu = new MorePopWindow(getContext(),item , new BaseQuickAdapter.OnItemClickListener() {
                            @Override
                            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                                mMenu.dismiss();
                                switch (position){
                                    case 0:
                                        getContext().startActivity(new Intent(getContext(), SearchAddMoreActivity.class));
                                        break;
                                    case 1:
                                        Intent intent = new Intent(getContext(), StartGroupChatActivity.class);
                                        intent.putExtra(IMKitConstants.GroupType.TYPE, IMKitConstants.GroupType.PUBLIC);
                                        getContext().startActivity(intent);
                                        break;
                                    case 2:
                                        getContext().startActivity(new Intent(getContext(), ScanIMQRCodeActivity.class));
                                        break;
                                }
                            }
                        });
                    }
                    mMenu.showPopupWindow(mAddMore);

                }
            });
        }else{
            mAddMore.setVisibility(GONE);
        }
        if(adapter==null){
            adapter = new ConversationListAdapter();
            mConversationList.setAdapter(adapter);
        }
        ConversationManagerKit.getInstance().loadConversation(new IUIKitCallBack() {
            @Override
            public void onSuccess(Object data) {
                adapter.setDataProvider((ConversationProvider) data);
            }

            @Override
            public void onError(String module, int errCode, String errMsg) {
                ToastUtil.error(TUIKit.getAppContext(),"加载消息失败");
            }
        });
    }

    public TitleBarLayout getTitleBar() {
        return null;
    }

    @Override
    public void setParentLayout(Object parent) {

    }

    @Override
    public ConversationListLayout getConversationList() {
        return mConversationList;
    }

    public void addConversationInfo(int position, ConversationInfo info) {
        mConversationList.getAdapter().addItem(position, info);
    }

    public void removeConversationInfo(int position) {
        mConversationList.getAdapter().removeItem(position);
    }

    @Override
    public void setConversationTop(int position, ConversationInfo conversation) {
        ConversationManagerKit.getInstance().setConversationTop(position, conversation);
    }

    @Override
    public void deleteConversation(int position, ConversationInfo conversation) {
        ConversationManagerKit.getInstance().deleteConversation(position, conversation);
    }
}
