package com.wwsl.mdsj.adapter;

import android.graphics.Color;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.view.View;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.wwsl.mdsj.Constants;
import com.wwsl.mdsj.R;
import com.wwsl.mdsj.bean.MessageShowBean;
import com.wwsl.mdsj.glide.ImgLoader;
import com.wwsl.mdsj.utils.DpUtil;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;


/**
 * @author :
 * @date : 2020/6/30 15:41
 * @description : MessageActionAdapter
 */

public class MessageActionAdapter extends BaseQuickAdapter<MessageShowBean, BaseViewHolder> {

    public MessageActionAdapter(@Nullable List<MessageShowBean> data) {
        super(R.layout.item_msg_user_action, data);
        addChildClickViewIds(R.id.avatar, R.id.thumb, R.id.tvFocus);
    }

    @Override
    protected void convert(@NotNull BaseViewHolder holder, MessageShowBean bean) {
        switch (bean.getType()) {
            case Constants.TYPE_LIKE:
                createLikeItem(holder, bean);
                break;
            case Constants.TYPE_COMMENT:
                createCommentItem(holder, bean);
                break;
            case Constants.TYPE_AT_ME:
                createAtMeItem(holder, bean);
                break;
            case Constants.TYPE_FANS:
                createFansItem(holder, bean);
                break;
        }
    }

    @Override
    protected void convert(@NotNull BaseViewHolder holder, MessageShowBean bean, @NotNull List<?> payloads) {
        for (Object p : payloads) {
            int payload = (int) p;
            if (payload == PAYLOAD_FOLLOW) {
                TextView focus = holder.getView(R.id.tvFocus);
                if (bean.getFollow() == 1) {
                    holder.setVisible(R.id.ivFollowBack, true);
                    focus.setText("回关");
                    focus.setBackgroundResource(R.drawable.shape_follow_btn);
                    focus.setPadding(DpUtil.dp2px(15), 0, 0, 0);
                } else if (bean.getFollow() == 2) {
                    holder.getView(R.id.ivFollowBack).setVisibility(View.GONE);
                    focus.setBackgroundResource(R.drawable.shape_unfollow_btn);
                    focus.setPadding(0, 0, 0, 0);
                    focus.setText("已关注");
                }
            }
        }
    }

    private void createFansItem(BaseViewHolder holder, MessageShowBean bean) {
        ImgLoader.displayAvatar(bean.getAvatar(), holder.getView(R.id.avatar));
        holder.setText(R.id.name, bean.getUsername());
        holder.setText(R.id.time, bean.getTime());
        holder.setText(R.id.content, bean.getContent());
        holder.getView(R.id.actionDes).setVisibility(View.GONE);

        TextView focus = holder.getView(R.id.tvFocus);
        focus.setVisibility(View.VISIBLE);
        if (bean.getFollow() == 1) {
            holder.setVisible(R.id.ivFollowBack, true);
            focus.setText("回关");
            focus.setBackgroundResource(R.drawable.shape_follow_btn);
            focus.setPadding(DpUtil.dp2px(15), 0, 0, 0);
        } else if (bean.getFollow() == 2) {
            holder.getView(R.id.ivFollowBack).setVisibility(View.GONE);
            focus.setBackgroundResource(R.drawable.shape_unfollow_btn);
            focus.setPadding(0, 0, 0, 0);
            focus.setText("已关注");
        }
    }

    private void createAtMeItem(BaseViewHolder holder, MessageShowBean bean) {
        ImgLoader.displayAvatar(bean.getAvatar(), holder.getView(R.id.avatar));
        ImgLoader.display(bean.getThumb(), R.mipmap.img_default_bg, R.mipmap.img_default_bg, holder.getView(R.id.thumb));
        holder.setText(R.id.name, bean.getUsername());
        holder.setText(R.id.time, bean.getTime());

        TextView view = holder.getView(R.id.content);
        SpannableString spannableString = new SpannableString("@ 你" + bean.getContent());
        ForegroundColorSpan colorSpan = new ForegroundColorSpan(Color.parseColor("#FE3919"));
        spannableString.setSpan(colorSpan, 0, 3, Spannable.SPAN_EXCLUSIVE_INCLUSIVE);
        view.setText(spannableString);

        holder.getView(R.id.actionDes).setVisibility(View.GONE);
        holder.getView(R.id.thumb).setVisibility(View.VISIBLE);
    }

    private void createCommentItem(BaseViewHolder holder, MessageShowBean bean) {
        ImgLoader.displayAvatar(bean.getAvatar(), holder.getView(R.id.avatar));
        ImgLoader.display(bean.getThumb(), R.mipmap.img_default_bg, R.mipmap.img_default_bg, holder.getView(R.id.thumb));
        holder.setText(R.id.name, bean.getUsername());
        holder.setText(R.id.time, bean.getTime());
        holder.setText(R.id.content, bean.getContent());
        holder.setText(R.id.actionDes, bean.getActionDes());
        holder.getView(R.id.thumb).setVisibility(View.VISIBLE);

    }

    private void createLikeItem(BaseViewHolder holder, MessageShowBean bean) {
        ImgLoader.displayAvatar(bean.getAvatar(), holder.getView(R.id.avatar));
        ImgLoader.display(bean.getThumb(), R.mipmap.img_default_bg, R.mipmap.img_default_bg, holder.getView(R.id.thumb));
        holder.setText(R.id.name, bean.getUsername());
        holder.setText(R.id.content, bean.getContent());
        holder.setText(R.id.time, bean.getTime());
        holder.getView(R.id.actionDes).setVisibility(View.GONE);
        holder.getView(R.id.thumb).setVisibility(View.VISIBLE);
    }


    public static final int PAYLOAD_FOLLOW = 1001;
}
