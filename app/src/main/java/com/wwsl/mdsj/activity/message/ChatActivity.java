package com.wwsl.mdsj.activity.message;

import android.content.Context;
import android.content.Intent;
import android.view.ViewGroup;

import com.wwsl.mdsj.R;
import com.wwsl.mdsj.activity.common.AbsActivity;
import com.wwsl.mdsj.im.ImUserBean;

/**
 * @author :
 * @date : 2020/7/25 17:14
 * @description : xxxxx
 */
public class ChatActivity extends AbsActivity {

    private ChatListViewHolder mChatListViewHolder;

    public static void forward(Context context) {
        context.startActivity(new Intent(context, ChatActivity.class));
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_chat_list;
    }

    @Override
    protected void main() {
        mChatListViewHolder = new ChatListViewHolder(mContext, (ViewGroup) findViewById(R.id.root),ChatListViewHolder.TYPE_ACTIVITY);
        mChatListViewHolder.setActionListener(new ChatListViewHolder.ActionListener() {
            @Override
            public void onCloseClick() {
                onBackPressed();
            }

            @Override
            public void onItemClick(ImUserBean bean) {
                ChatRoomActivity.forward(mContext, bean, bean.getAttent() == 1);
            }
        });
        mChatListViewHolder.addToParent();
        mChatListViewHolder.loadData();
    }

    @Override
    protected void onDestroy() {
        if (mChatListViewHolder != null) {
            mChatListViewHolder.release();
        }
        super.onDestroy();
    }
}
