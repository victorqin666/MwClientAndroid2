package com.mwim.qcloud.tim.uikit.modules.contact;

import android.content.Intent;
import android.graphics.Color;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.http.network.listener.OnResultDataListener;
import com.http.network.model.RequestWork;
import com.http.network.model.ResponseWork;
import com.mwim.qcloud.tim.uikit.TUIKit;
import com.mwim.qcloud.tim.uikit.business.active.FriendProfileActivity;
import com.mwim.qcloud.tim.uikit.modules.conversation.base.ConversationIconView;
import com.mwim.qcloud.tim.uikit.utils.IMKitConstants;
import com.mwim.qcloud.tim.uikit.R;
import com.work.api.open.Yz;
import com.work.api.open.model.InviteUserReq;
import com.work.api.open.model.client.OpenData;
import com.work.util.SizeUtils;
import com.work.util.ToastUtil;

import java.util.List;

import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;


public class ContactAdapter extends RecyclerView.Adapter<ContactAdapter.ViewHolder> {

    protected List<ContactItemBean> mData;
    protected LayoutInflater mInflater;
    private ContactListView.OnSelectChangedListener mOnSelectChangedListener;
    private ContactListView.OnItemClickListener mOnClickListener;

    private int mPreSelectedPosition;
    private boolean isSingleSelectMode;

    public ContactAdapter(List<ContactItemBean> data) {
        this.mData = data;
        mInflater = LayoutInflater.from(TUIKit.getAppContext());
    }

    @Override
    public ContactAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ContactAdapter.ViewHolder(mInflater.inflate(R.layout.contact_selecable_adapter_item, parent, false));
    }

    @Override
    public void onBindViewHolder(final ContactAdapter.ViewHolder holder, final int position) {
        final ContactItemBean contactBean = mData.get(position);
        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams)holder.line.getLayoutParams();
        if (position < mData.size() - 1) {
            String tag1 = contactBean.getSuspensionTag();
            String tag2 = mData.get(position + 1).getSuspensionTag();
            // tag不同时对item的分割线进行重新处理
            if (TextUtils.equals(tag1, tag2)) {
                params.leftMargin = holder.tvName.getLeft();
            } else {
                params.leftMargin = 0;
            }
        } else {
            params.leftMargin = 0;
        }
        holder.line.setLayoutParams(params);
        if (!TextUtils.isEmpty(contactBean.getRemark())) {
            holder.tvName.setText(contactBean.getRemark());
        } else if (!TextUtils.isEmpty(contactBean.getNickname())) {
            holder.tvName.setText(contactBean.getNickname());
        } else {
            holder.tvName.setText(contactBean.getId());
        }
        if (mOnSelectChangedListener != null) {
            holder.ccSelect.setVisibility(View.VISIBLE);
            holder.ccSelect.setChecked(contactBean.isSelected());
        }

        holder.content.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!contactBean.isEnable()) {
                    return;
                }
                holder.ccSelect.setChecked(!holder.ccSelect.isChecked());
                if (mOnSelectChangedListener != null) {
                    mOnSelectChangedListener.onSelectChanged(getItem(position), holder.ccSelect.isChecked());
                }
                contactBean.setSelected(holder.ccSelect.isChecked());
                if (mOnClickListener != null) {
                    mOnClickListener.onItemClick(position, contactBean);
                }
                if (isSingleSelectMode && position != mPreSelectedPosition && contactBean.isSelected()) {
                    //单选模式的prePos处理
                    mData.get(mPreSelectedPosition).setSelected(false);
                    notifyItemChanged(mPreSelectedPosition);
                }
                mPreSelectedPosition = position;
            }
        });
        holder.unreadText.setVisibility(View.GONE);
        if (TextUtils.equals(TUIKit.getAppContext().getResources().getString(R.string.new_friend), contactBean.getId())) {
            holder.avatar.setDefaultImageResId(R.drawable.icon_add_contact_stroke,ContextCompat.getColor(holder.avatar.getContext(),R.color.defaultColorAccent));
            holder.avatar.getLayoutParams().width = SizeUtils.dp2px(holder.avatar.getContext(),24);
            holder.avatar.getLayoutParams().height = SizeUtils.dp2px(holder.avatar.getContext(),24);
            holder.avatar.setRadius(0);
            if(contactBean.getNewFriendCount()==0){
                holder.unreadText.setVisibility(View.GONE);
            }else{
                holder.unreadText.setVisibility(View.VISIBLE);
                holder.unreadText.setText(String.valueOf(contactBean.getNewFriendCount()));
            }
//            V2TIMManager.getFriendshipManager().getFriendApplicationList(new V2TIMValueCallback<V2TIMFriendApplicationResult>() {
//                @Override
//                public void onError(int code, String desc) {
//                    ToastUtil.error(holder.avatar.getContext(),"Error code = " + code + ", desc = " + desc);
//                }
//
//                @Override
//                public void onSuccess(V2TIMFriendApplicationResult v2TIMFriendApplicationResult) {
//                    if (v2TIMFriendApplicationResult.getFriendApplicationList() != null) {
//                        int pendingRequest = v2TIMFriendApplicationResult.getUnreadCount();
//                        if (pendingRequest == 0) {
//                            holder.unreadText.setVisibility(View.GONE);
//                        } else {
//                            holder.unreadText.setVisibility(View.VISIBLE);
//                            holder.unreadText.setText(String.valueOf(pendingRequest));
//                        }
//                    }
//                }
//            });
        }
        else if (TextUtils.equals(TUIKit.getAppContext().getResources().getString(R.string.group), contactBean.getId())) {
            holder.avatar.setDefaultImageResId(R.drawable.icon_chat_group_stroke,ContextCompat.getColor(holder.avatar.getContext(),R.color.color_fdac3b));
            holder.avatar.getLayoutParams().width = SizeUtils.dp2px(holder.avatar.getContext(),24);
            holder.avatar.getLayoutParams().height = SizeUtils.dp2px(holder.avatar.getContext(),24);
            holder.avatar.setRadius(0);
        }
        else if (TextUtils.equals(TUIKit.getAppContext().getResources().getString(R.string.blacklist), contactBean.getId())) {
            holder.avatar.setDefaultImageResId(R.drawable.icon_block_fill,ContextCompat.getColor(holder.avatar.getContext(),R.color.color_999999));
            holder.avatar.getLayoutParams().width = SizeUtils.dp2px(holder.avatar.getContext(),24);
            holder.avatar.getLayoutParams().height = SizeUtils.dp2px(holder.avatar.getContext(),24);
            holder.avatar.setRadius(0);
        }
        else {
            if (contactBean.getIconUrlList()==null) {
                holder.avatar.setDefaultImageResId(R.drawable.default_head);
            } else {
                holder.avatar.setRadius(SizeUtils.dp2px(holder.avatar.getContext(),30));
                holder.avatar.setIconUrls(contactBean.getIconUrlList());
            }
            holder.avatar.getLayoutParams().width = SizeUtils.dp2px(holder.avatar.getContext(),30);
            holder.avatar.getLayoutParams().height = SizeUtils.dp2px(holder.avatar.getContext(),30);
            holder.mAgree.setVisibility(View.GONE);
            holder.mRemark.setVisibility(View.GONE);
            if(contactBean.isSystemContacts()){
                holder.mAgree.setVisibility(View.VISIBLE);
                String remark = contactBean.getSystemRemark();
                if(TextUtils.isEmpty(remark)){
                    holder.mRemark.setVisibility(View.GONE);
                }else{
                    holder.mRemark.setVisibility(View.VISIBLE);
                    holder.mRemark.setText(remark);
                }
                if(contactBean.getUserType()==1){
                    holder.mAgree.setBackgroundColor(Color.TRANSPARENT);
                    holder.mAgree.setTextColor(ContextCompat.getColor(holder.itemView.getContext(),R.color.color_999999));
                    holder.mAgree.setText(R.string.text_contacts_phone_friends);
                }else if(contactBean.getUserType()==2){
                    holder.mAgree.setBackgroundResource(R.drawable.friend_border_2);
                    holder.mAgree.setTextColor(ContextCompat.getColor(holder.itemView.getContext(),R.color.white));
                    holder.mAgree.setText(R.string.add_friend);
                    holder.mAgree.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            OpenData openData = new OpenData();
                            openData.setUserId(contactBean.getId());
                            Intent intent = new Intent(holder.itemView.getContext(),FriendProfileActivity.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            holder.itemView.getContext().startActivity(intent.putExtra(IMKitConstants.ProfileType.CONTENT,openData));
                        }
                    });
                } else if(contactBean.getUserType()==4){
                    holder.mAgree.setBackgroundColor(Color.TRANSPARENT);
                    holder.mAgree.setTextColor(ContextCompat.getColor(holder.itemView.getContext(),R.color.color_999999));
                    holder.mAgree.setText(R.string.text_contacts_phone_wait);
                }else{
                    holder.mAgree.setBackgroundResource(R.drawable.friend_border_3);
                    holder.mAgree.setTextColor(ContextCompat.getColor(holder.itemView.getContext(),R.color.defaultColorAccent));
                    holder.mAgree.setText(R.string.text_contacts_phone_download);
                    holder.mAgree.setEnabled(contactBean.isEnable());
                    holder.mAgree.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            holder.mAgree.setText(R.string.text_contacts_phone_download_sending);
                            holder.mAgree.setEnabled(false);
                            InviteUserReq inviteUserReq = new InviteUserReq();
                            inviteUserReq.setMobile(contactBean.getMobile());
                            Yz.getSession().inviteUser(inviteUserReq, new OnResultDataListener() {
                                @Override
                                public void onResult(RequestWork req, ResponseWork resp) {
                                    if(resp.isSuccess()){
                                        contactBean.setEnable(false);
                                        holder.mAgree.setText(R.string.text_contacts_phone_download_ok);
                                        ToastUtil.success(TUIKit.getAppContext(),R.string.text_contacts_add_download_success);
                                    }else{
                                        holder.mAgree.setText(R.string.text_contacts_phone_download);
                                        holder.mAgree.setEnabled(true);
                                        ToastUtil.warning(TUIKit.getAppContext(),resp.getMessage());
                                    }
                                }
                            });
                        }
                    });
                }
            }else if(contactBean.getArea()!=null){
                holder.avatar.setVisibility(View.GONE);
                holder.mContainer.setPadding(0,0,0,0);
            }
        }

    }

    @Override
    public void onViewRecycled(ContactAdapter.ViewHolder holder) {
        if (holder != null) {
//            GlideEngine.clear(holder.avatar);
//            holder.avatar.setImageResource(0);

        }
        super.onViewRecycled(holder);
    }

    private ContactItemBean getItem(int position) {
        if (position < mData.size()) {
            return mData.get(position);
        }
        return null;
    }

    @Override
    public int getItemCount() {
        return mData != null ? mData.size() : 0;
    }

    public void setDataSource(List<ContactItemBean> datas) {
        this.mData = datas;
        notifyDataSetChanged();
    }

    public void setSingleSelectMode(boolean mode) {
        isSingleSelectMode = mode;
    }

    public void setOnSelectChangedListener(ContactListView.OnSelectChangedListener selectListener) {
        mOnSelectChangedListener = selectListener;
    }

    public void setOnItemClickListener(ContactListView.OnItemClickListener listener) {
        mOnClickListener = listener;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        LinearLayout mContainer;
        TextView tvName;
        TextView unreadText;
        ConversationIconView avatar;
        CheckBox ccSelect;
        View content;
        View line;
        Button mAgree;
        TextView mRemark;

        public ViewHolder(View itemView) {
            super(itemView);
            mContainer = itemView.findViewById(R.id.container);
            tvName = itemView.findViewById(R.id.tvCity);
            unreadText = itemView.findViewById(R.id.conversation_unread);
            unreadText.setVisibility(View.GONE);
            avatar = itemView.findViewById(R.id.ivAvatar);
            ccSelect = itemView.findViewById(R.id.contact_check_box);
            content = itemView.findViewById(R.id.selectable_contact_item);
            line = itemView.findViewById(R.id.view_line);
            mAgree = itemView.findViewById(R.id.agree);
            mRemark = itemView.findViewById(R.id.remark);

        }
    }
}
