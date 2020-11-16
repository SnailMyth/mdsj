package com.wwsl.mdsj.adapter;

import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.chad.library.adapter.base.BaseMultiItemQuickAdapter;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.makeramen.roundedimageview.RoundedImageView;
import com.wwsl.mdsj.AppContext;
import com.wwsl.mdsj.Constants;
import com.wwsl.mdsj.R;
import com.wwsl.mdsj.bean.MsgShortBean;
import com.wwsl.mdsj.glide.ImgLoader;
import com.wwsl.mdsj.utils.DpUtil;
import com.wwsl.mdsj.utils.IconUtil;

import org.jetbrains.annotations.NotNull;

import java.util.List;

import cn.hutool.core.util.StrUtil;

public class MsgShortAdapter extends BaseMultiItemQuickAdapter<MsgShortBean, BaseViewHolder> {

    private final static String TAG = "MsgShortAdapter";

    public MsgShortAdapter(@Nullable List<MsgShortBean> data) {
        super(data);
        addItemType(Constants.MESSAGE_TYPE_MSG, R.layout.item_short_msg);
        addItemType(Constants.MESSAGE_TYPE_AD, R.layout.item_ad);
        addItemType(Constants.MESSAGE_TYPE_TEXT_LABEL, R.layout.item_recomment_label);
        addItemType(Constants.MESSAGE_TYPE_TEXT_RECOMMEND, R.layout.item_short_msg_recommend);
        addChildClickViewIds(
                R.id.btnFollow,
                R.id.imgRemove
        );
    }

    @Override
    protected void convert(@NonNull BaseViewHolder helper, MsgShortBean item) {

        if (item.getType() == Constants.MESSAGE_TYPE_MSG) {
            ImageView avatar = helper.getView(R.id.img);
            switch (item.getSubType()) {
                case Constants.MESSAGE_SUBTYPE_FRIEND:
                    avatar.setImageResource(R.mipmap.icon_msg_friend);
                    helper.setText(R.id.tvName, "我的好友");
                    break;
                case Constants.MESSAGE_SUBTYPE_SYS_NOTICE:
                case Constants.MESSAGE_SUBTYPE_ASSISTANT_MD:
                case Constants.MESSAGE_SUBTYPE_ASSISTANT_SHOP:
                case Constants.MESSAGE_SUBTYPE_ASSISTANT_LIVE:
                    avatar.setImageResource(IconUtil.getSysMsgIconType(item.getSubType()));
                    helper.setText(R.id.tvName, item.getName());
                    break;
            }

            helper.setText(R.id.content, item.getContent());
            helper.setText(R.id.time, item.getTime());
            TextView num = helper.getView(R.id.unreadNum);
            num.setText(String.valueOf(item.getUnreadNum()));
            if (!StrUtil.isEmpty(item.getUnreadNum()) && !"0".equals(item.getUnreadNum())) {
                num.setVisibility(View.VISIBLE);
            } else {
                num.setVisibility(View.INVISIBLE);
            }
        } else if (item.getType() == Constants.MESSAGE_TYPE_TEXT_RECOMMEND) {
            ImgLoader.displayAvatar(item.getAvatar(), helper.getView(R.id.img));
            helper.setText(R.id.tvName, item.getName());
            LinearLayout linearLayout = helper.getView(R.id.userTagLinear);
            linearLayout.removeAllViews();
            if (item.getSex() > 0) {
                addTag(linearLayout, "男");
            }
            if (!StrUtil.isEmpty(item.getAge())) {
                addTag(linearLayout, item.getAge());
            }
            if (!StrUtil.isEmpty(item.getCity())) {
                addTag(linearLayout, item.getCity());
            }

            if (linearLayout.getChildCount() == 0) {
                linearLayout.setVisibility(View.GONE);
            }


            helper.setText(R.id.originDes, item.getOriginDes());
        } else if (item.getType() == Constants.MESSAGE_TYPE_AD) {
            ImgLoader.display(item.getAdThumb(), helper.getView(R.id.ivAd));
        }
    }

    @Override
    protected void convert(@NotNull BaseViewHolder holder, MsgShortBean item, @NotNull List<?> payloads) {
        for (Object p : payloads) {
            int payload = (int) p;
            if (payload == PAYLOAD_FOLLOW) {
                TextView view = holder.getView(R.id.btnFollow);
                if (item.isFollow()) {
                    view.setText("已关注");
                    view.setBackgroundResource(R.drawable.shape_round_halfwhite);
                }
            } else if (payload == PAYLOAD_UNREAD_UPDATE) {
                holder.setText(R.id.content, item.getContent());
                holder.setText(R.id.time, item.getTime());
                TextView num = holder.getView(R.id.unreadNum);
                num.setText(String.valueOf(item.getUnreadNum()));
                if (!StrUtil.isEmpty(item.getUnreadNum()) && !"0".equals(item.getUnreadNum())) {
                    num.setVisibility(View.VISIBLE);
                } else {
                    num.setVisibility(View.INVISIBLE);
                }
            } else if (payload == PAYLOAD_UNREAD_CLEAR) {
                TextView num = holder.getView(R.id.unreadNum);
                num.setVisibility(View.INVISIBLE);
            }
        }
    }

    public void addTag(LinearLayout tagLinear, String text) {
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        TextView textview = new TextView(getContext());
        textview.setText(text);
        textview.setPadding(5, 2, 5, 2);
        textview.setTextColor(AppContext.sInstance.getResources().getColor(R.color.color_tag));
        textview.setTextSize(12);
        textview.setMaxWidth(DpUtil.dp2px(150));
        textview.setBackgroundResource(R.drawable.shape_round_halfwhite);
        textview.setLines(1);
        textview.setEllipsize(TextUtils.TruncateAt.END);
        layoutParams.setMarginStart(5);
        textview.setLayoutParams(layoutParams);
        tagLinear.addView(textview);
    }

    public static final int PAYLOAD_FOLLOW = 801;
    public static final int PAYLOAD_UNREAD_UPDATE = 802;
    public static final int PAYLOAD_UNREAD_CLEAR = 803;
}