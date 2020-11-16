package com.wwsl.mdsj.im;

import android.animation.ValueAnimator;
import android.content.Context;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.makeramen.roundedimageview.RoundedImageView;
import com.wwsl.mdsj.AppConfig;
import com.wwsl.mdsj.Constants;
import com.wwsl.mdsj.R;
import com.wwsl.mdsj.bean.UserBean;
import com.wwsl.mdsj.custom.BubbleLayout;
import com.wwsl.mdsj.custom.ChatVoiceLayout;
import com.wwsl.mdsj.custom.MyImageView;
import com.wwsl.mdsj.glide.ImgLoader;
import com.wwsl.mdsj.interfaces.CommonCallback;
import com.wwsl.mdsj.utils.ClickUtil;
import com.wwsl.mdsj.utils.DpUtil;
import com.wwsl.mdsj.utils.L;
import com.wwsl.mdsj.utils.TextRender;
import com.wwsl.mdsj.utils.ToastUtil;
import com.wwsl.mdsj.utils.WordUtil;

import java.io.BufferedReader;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import cn.jpush.im.android.api.content.CustomContent;
import cn.jpush.im.android.api.content.LocationContent;
import cn.jpush.im.android.api.content.TextContent;
import cn.jpush.im.android.api.enums.ContentType;
import cn.jpush.im.android.api.model.Message;
import cn.jpush.im.api.BasicCallback;

/**
 * @author :
 * @date : 2020/7/27 11:50
 * @description : ImRoomAdapter
 */
public class ImRoomAdapter extends RecyclerView.Adapter {

    private static final int TYPE_TEXT_LEFT = 1;
    private static final int TYPE_TEXT_RIGHT = 2;
    private static final int TYPE_IMAGE_LEFT = 3;
    private static final int TYPE_IMAGE_RIGHT = 4;
    private static final int TYPE_VOICE_LEFT = 5;
    private static final int TYPE_VOICE_RIGHT = 6;
    private static final int TYPE_LOCATION_LEFT = 7;
    private static final int TYPE_LOCATION_RIGHT = 8;
    private static final int TYPE_VIDEO_LEFT = 9;
    private static final int TYPE_VIDEO_RIGHT = 10;
    private static final int TYPE_RED_PACKET_LEFT = 11;
    private static final int TYPE_RED_PACKET_RIGHT = 12;
    private static final int TYPE_OPEN_RED_PACKET_LEFT = 13;
    private static final int TYPE_OPEN_RED_PACKET_RIGHT = 14;


    private RecyclerView mRecyclerView;
    private LinearLayoutManager mLayoutManager;
    private UserBean mUserBean;
    private UserBean mToUserBean;
    private String mTxLocationKey;
    private List<ImMessageBean> mList;
    private LayoutInflater mInflater;
    private String mUserAvatar;
    private String mToUserAvatar;
    private long mLastMessageTime;
    private ActionListener mActionListener;
    private View.OnClickListener mOnImageClickListener;
    private int[] mLocation;
    private ValueAnimator mAnimator;
    private ChatVoiceLayout mChatVoiceLayout;
    private View.OnClickListener mOnVoiceClickListener;
    private View.OnClickListener mOnVideoClickListener;
    private View.OnClickListener mOnRedPacketClickListener;
    private CommonCallback<File> mVoiceFileCallback;

    public ImRoomAdapter(Context context, String toUid, UserBean toUserBean) {
        mList = new ArrayList<>();
        mInflater = LayoutInflater.from(context);
        mUserBean = AppConfig.getInstance().getUserBean();
        mToUserBean = toUserBean;
        mTxLocationKey = AppConfig.getInstance().getTxLocationKey();
        mUserAvatar = mUserBean.getAvatar();
        mToUserAvatar = mToUserBean.getAvatar();
        mLocation = new int[2];
        mOnImageClickListener = v -> {
            if (!ClickUtil.canClick()) {
                return;
            }
            MyImageView imageView = (MyImageView) v;
            imageView.getLocationOnScreen(mLocation);
            if (mActionListener != null) {
                mActionListener.onImageClick(imageView, mLocation[0], mLocation[1]);
            }
        };

        mOnRedPacketClickListener = v -> {
            if (!ClickUtil.canClick()) {
                return;
            }
            Object tag = v.getTag();
            if (tag == null) {
                return;
            }
            final int position = (int) tag;
            ImMessageBean bean = mList.get(position);
            CustomContent content = (CustomContent) bean.getRawMessage().getContent();
            if (mActionListener != null) {
                mActionListener.onRedPacketClick(
                        content.getStringValue(ImMessageUtil.CUSTOM_RED_PACKET_ID));
            }
        };

        mOnVideoClickListener = v -> {
            if (!ClickUtil.canClick()) {
                return;
            }
            Object tag = v.getTag();
            if (tag == null) {
                return;
            }
            final int position = (int) tag;
            ImMessageBean bean = mList.get(position);
            CustomContent content = (CustomContent) bean.getRawMessage().getContent();
            if (mActionListener != null) {
                mActionListener.onVideoClick(content.getStringValue(ImMessageUtil.CUSTOM_VIDEO_ID));
            }
        };

        mOnVoiceClickListener = v -> {
            if (!ClickUtil.canClick()) {
                return;
            }
            Object tag = v.getTag();
            if (tag == null) {
                return;
            }
            final int position = (int) tag;
            ImMessageBean bean = mList.get(position);
            if (mChatVoiceLayout != null) {
                int msgId1 = mChatVoiceLayout.getMsgId();
                int msgId2 = bean.getMessageId();
                mChatVoiceLayout.cancelAnim();
                if (msgId1 == msgId2) {//同一个消息对象
                    if (mRecyclerView != null) {
                        mRecyclerView.setLayoutFrozen(false);
                    }
                    mChatVoiceLayout = null;
                    if (mActionListener != null) {
                        mActionListener.onVoiceStopPlay();
                    }
                } else {
                    mChatVoiceLayout = (ChatVoiceLayout) v;
                    mAnimator.start();
                    ImMessageUtil.getInstance().getVoiceFile(bean, mVoiceFileCallback);
                    bean.hasRead(new BasicCallback() {
                        @Override
                        public void gotResult(int i, String s) {
                            notifyItemChanged(position, Constants.PAYLOAD);
                        }
                    });

                }
            } else {
                if (mRecyclerView != null) {
                    mRecyclerView.setLayoutFrozen(true);
                }
                mChatVoiceLayout = (ChatVoiceLayout) v;
                mAnimator.start();
                ImMessageUtil.getInstance().getVoiceFile(bean, mVoiceFileCallback);
                bean.hasRead(new BasicCallback() {
                    @Override
                    public void gotResult(int i, String s) {
                        notifyItemChanged(position, Constants.PAYLOAD);
                    }
                });

            }
        };

        mVoiceFileCallback = new CommonCallback<File>() {
            @Override
            public void callback(File file) {
                if (mActionListener != null) {
                    mActionListener.onVoiceStartPlay(file);
                }
            }
        };

        mAnimator = ValueAnimator.ofFloat(0, 900);
        mAnimator.setInterpolator(new LinearInterpolator());
        mAnimator.setDuration(700);
        mAnimator.setRepeatCount(-1);
        mAnimator.setRepeatMode(ValueAnimator.RESTART);
        mAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float v = (float) animation.getAnimatedValue();
                if (mChatVoiceLayout != null) {
                    mChatVoiceLayout.animate((int) (v / 300));
                }
            }
        });
    }

    /**
     * 停止语音动画
     */
    public void stopVoiceAnim() {
        if (mChatVoiceLayout != null) {
            mChatVoiceLayout.cancelAnim();
        }
        mChatVoiceLayout = null;
        if (mRecyclerView != null) {
            mRecyclerView.setLayoutFrozen(false);
        }
    }

    @Override
    public int getItemViewType(int position) {
        ImMessageBean msg = mList.get(position);
        switch (msg.getType()) {
            case ImMessageBean.TYPE_TEXT:
                if (msg.isFromSelf()) {
                    return TYPE_TEXT_RIGHT;
                } else {
                    return TYPE_TEXT_LEFT;
                }
            case ImMessageBean.TYPE_IMAGE:
                if (msg.isFromSelf()) {
                    return TYPE_IMAGE_RIGHT;
                } else {
                    return TYPE_IMAGE_LEFT;
                }
            case ImMessageBean.TYPE_VOICE:
                if (msg.isFromSelf()) {
                    return TYPE_VOICE_RIGHT;
                } else {
                    return TYPE_VOICE_LEFT;
                }
            case ImMessageBean.TYPE_LOCATION:
                if (msg.isFromSelf()) {
                    return TYPE_LOCATION_RIGHT;
                } else {
                    return TYPE_LOCATION_LEFT;
                }
            case ImMessageBean.TYPE_CUSTOM:
                CustomContent content = (CustomContent) msg.getRawMessage().getContent();
                String customType = content.getStringValue(ImMessageUtil.CUSTOM_TYPE);
                if (ImMessageUtil.CUSTOM_TYPE_VIDEO.equals(customType)) {
                    if (msg.isFromSelf()) {
                        return TYPE_VIDEO_RIGHT;
                    } else {
                        return TYPE_VIDEO_LEFT;
                    }
                } else if (ImMessageUtil.CUSTOM_TYPE_RED_PACKET.equals(customType)) {
                    //红包消息
                    if (msg.isFromSelf()) {
                        return TYPE_RED_PACKET_RIGHT;
                    } else {
                        return TYPE_RED_PACKET_LEFT;
                    }
                } else if (ImMessageUtil.CUSTOM_TYPE_RED_PACKET_OPEN.equals(customType)) {
                    //领取红包消息
                    if (msg.isFromSelf()) {
                        return TYPE_OPEN_RED_PACKET_RIGHT;
                    } else {
                        return TYPE_OPEN_RED_PACKET_LEFT;
                    }
                }

        }
        return 0;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        switch (viewType) {
            case TYPE_TEXT_LEFT:
                return new TextVh(mInflater.inflate(R.layout.item_chat_text_left, parent, false));
            case TYPE_TEXT_RIGHT:
                return new SelfTextVh(mInflater.inflate(R.layout.item_chat_text_right, parent, false));
            case TYPE_IMAGE_LEFT:
                return new ImageVh(mInflater.inflate(R.layout.item_chat_image_left, parent, false));
            case TYPE_IMAGE_RIGHT:
                return new SelfImageVh(mInflater.inflate(R.layout.item_chat_image_right, parent, false));
            case TYPE_VOICE_LEFT:
                return new VoiceVh(mInflater.inflate(R.layout.item_chat_voice_left, parent, false));
            case TYPE_VOICE_RIGHT:
                return new SelfVoiceVh(mInflater.inflate(R.layout.item_chat_voice_right, parent, false));
            case TYPE_LOCATION_LEFT:
                return new LocationVh(mInflater.inflate(R.layout.item_chat_location_left, parent, false));
            case TYPE_LOCATION_RIGHT:
                return new SelfLocationVh(mInflater.inflate(R.layout.item_chat_location_right, parent, false));
            case TYPE_VIDEO_LEFT:
                return new VideoVh(mInflater.inflate(R.layout.item_chat_video_left, parent, false));
            case TYPE_VIDEO_RIGHT:
                return new SelfVideoVh(mInflater.inflate(R.layout.item_chat_video_right, parent, false));
            case TYPE_RED_PACKET_LEFT:
                return new RedPacketVH(mInflater.inflate(R.layout.item_chat_packet_left, parent, false));
            case TYPE_RED_PACKET_RIGHT:
                return new SelfRedPacketVH(mInflater.inflate(R.layout.item_chat_packet_right, parent, false));
            case TYPE_OPEN_RED_PACKET_LEFT:
            case TYPE_OPEN_RED_PACKET_RIGHT:
                return new OpenRedPacketVH(mInflater.inflate(R.layout.item_chat_packet_open, parent, false));
        }
        return null;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder vh, int position) {

    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder vh, int position, List payloads) {
        Object payload = payloads.size() > 0 ? payloads.get(0) : null;
        ((Vh) vh).setData(mList.get(position), position, payload);
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    @Override
    public void onAttachedToRecyclerView(@NonNull RecyclerView recyclerView) {
        mRecyclerView = recyclerView;
        mLayoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
    }

    public int insertItem(ImMessageBean bean) {
        if (mList != null && bean != null) {
            int size = mList.size();
            mList.add(bean);
            notifyItemInserted(size);
            int lastItemPosition = mLayoutManager.findLastCompletelyVisibleItemPosition();
            if (lastItemPosition != size - 1) {
                mRecyclerView.smoothScrollToPosition(size);
            } else {
                mRecyclerView.scrollToPosition(size);
            }
            return size;
        }
        return -1;
    }

    public void insertSelfItem(final ImMessageBean bean) {
        bean.setLoading(true);
        final int position = insertItem(bean);
        if (position != -1) {
            final Message msg = bean.getRawMessage();
            if (msg != null) {
                msg.setOnSendCompleteCallback(new BasicCallback() {
                    @Override
                    public void gotResult(int responseCode, String responseDesc) {
                        bean.setLoading(false);
                        if (responseCode != 0) {
                            bean.setSendFail(true);
                            //消息发送失败
                            ToastUtil.show(WordUtil.getString(R.string.im_msg_send_failed));
                            L.e("极光IM---消息发送失败--->  responseDesc:" + responseDesc);
                        }
                        notifyItemChanged(position, Constants.PAYLOAD);
                    }
                });
                ImMessageUtil.getInstance().sendMessage(msg);
            }

        }
    }

    public ImChatImageBean getChatImageBean(int msgId) {
        List<ImMessageBean> list = new ArrayList<>();
        int imagePosition = 0;
        for (int i = 0, size = mList.size(); i < size; i++) {
            ImMessageBean bean = mList.get(i);
            if (bean.getType() == ImMessageBean.TYPE_IMAGE) {
                list.add(bean);
                if (bean.getRawMessage().getId() == msgId) {
                    imagePosition = list.size() - 1;
                }
            }
        }
        return new ImChatImageBean(list, imagePosition);
    }

    public void setList(List<ImMessageBean> list) {
        if (mList != null && list != null && list.size() > 0) {
            mList.clear();
            mList.addAll(list);
            notifyDataSetChanged();
        }
    }

    public void scrollToBottom() {
        if (mList.size() > 0 && mLayoutManager != null) {
            mLayoutManager.scrollToPositionWithOffset(mList.size() - 1, -DpUtil.dp2px(20));
        }
    }

    public ImMessageBean getLastMessage() {
        if (mList == null || mList.size() == 0) {
            return null;
        }
        return mList.get(mList.size() - 1);
    }

    class Vh extends RecyclerView.ViewHolder {
        ImageView mAvatar;
        TextView mTime;
        ImMessageBean mImMessageBean;

        public Vh(View itemView) {
            super(itemView);
            mAvatar = (ImageView) itemView.findViewById(R.id.avatar);
            mTime = (TextView) itemView.findViewById(R.id.time);
        }

        void setData(ImMessageBean bean, int position, Object payload) {
            mImMessageBean = bean;
            if (payload == null) {
                if (bean.isFromSelf()) {
                    ImgLoader.display(mUserAvatar, mAvatar);
                } else {
                    ImgLoader.display(mToUserAvatar, mAvatar);
                }
                if (position == 0) {
                    mLastMessageTime = bean.getTime();
                    if (mTime.getVisibility() != View.VISIBLE) {
                        mTime.setVisibility(View.VISIBLE);
                    }
                    mTime.setText(ImDateUtil.getTimestampString(mLastMessageTime));
                } else {
                    if (ImDateUtil.isCloseEnough(bean.getTime(), mLastMessageTime)) {
                        if (mTime.getVisibility() == View.VISIBLE) {
                            mTime.setVisibility(View.GONE);
                        }
                    } else {
                        mLastMessageTime = bean.getTime();
                        if (mTime.getVisibility() != View.VISIBLE) {
                            mTime.setVisibility(View.VISIBLE);
                        }
                        mTime.setText(ImDateUtil.getTimestampString(mLastMessageTime));
                    }
                }
            }
        }
    }

    class TextVh extends Vh {

        TextView mText;

        public TextVh(View itemView) {
            super(itemView);
            mText = (TextView) itemView.findViewById(R.id.text);
        }

        @Override
        public void setData(ImMessageBean bean, int position, Object payload) {
            super.setData(bean, position, payload);
            if (payload == null) {
                String text = ((TextContent) bean.getRawMessage().getContent()).getText();
                mText.setText(TextRender.renderChatMessage(text));
            }
        }
    }

    class SelfTextVh extends TextVh {

        View mFailIcon;
        View mLoading;

        public SelfTextVh(View itemView) {
            super(itemView);
            mFailIcon = itemView.findViewById(R.id.icon_fail);
            mLoading = itemView.findViewById(R.id.loading);
        }

        @Override
        public void setData(ImMessageBean bean, int position, Object payload) {
            super.setData(bean, position, payload);
            if (bean.isLoading()) {
                if (mLoading.getVisibility() != View.VISIBLE) {
                    mLoading.setVisibility(View.VISIBLE);
                }
            } else {
                if (mLoading.getVisibility() == View.VISIBLE) {
                    mLoading.setVisibility(View.INVISIBLE);
                }
            }
            if (bean.isSendFail()) {
                if (mFailIcon.getVisibility() != View.VISIBLE) {
                    mFailIcon.setVisibility(View.VISIBLE);
                }
            } else {
                if (mFailIcon.getVisibility() == View.VISIBLE) {
                    mFailIcon.setVisibility(View.INVISIBLE);
                }
            }
        }
    }

    class ImageVh extends Vh {

        MyImageView mImg;

        public ImageVh(View itemView) {
            super(itemView);
            mImg = (MyImageView) itemView.findViewById(R.id.img);
            mImg.setOnClickListener(mOnImageClickListener);
        }

        @Override
        public void setData(ImMessageBean bean, int position, Object payload) {
            super.setData(bean, position, payload);
            if (payload == null) {
                mImg.setMsgId(bean.getRawMessage().getId());
                File imageFile = bean.getImageFile();
                if (imageFile != null) {
                    mImg.setFile(imageFile);
                    ImgLoader.display(imageFile, mImg);
                } else {
                    ImMessageUtil.getInstance().displayImageFile(bean, mImg);
                }
            }
        }
    }


    class SelfImageVh extends ImageVh {

        View mFailIcon;
        View mLoading;

        public SelfImageVh(View itemView) {
            super(itemView);
            mFailIcon = itemView.findViewById(R.id.icon_fail);
            mLoading = itemView.findViewById(R.id.loading);
        }

        @Override
        public void setData(ImMessageBean bean, int position, Object payload) {
            super.setData(bean, position, payload);
            if (bean.isLoading()) {
                if (mLoading.getVisibility() != View.VISIBLE) {
                    mLoading.setVisibility(View.VISIBLE);
                }
            } else {
                if (mLoading.getVisibility() == View.VISIBLE) {
                    mLoading.setVisibility(View.INVISIBLE);
                }
            }
            if (bean.isSendFail()) {
                if (mFailIcon.getVisibility() != View.VISIBLE) {
                    mFailIcon.setVisibility(View.VISIBLE);
                }
            } else {
                if (mFailIcon.getVisibility() == View.VISIBLE) {
                    mFailIcon.setVisibility(View.INVISIBLE);
                }
            }
        }
    }

    class VideoVh extends Vh {

        MyImageView mImg;
        RoundedImageView videoAvatar;
        TextView videoTitle;
        TextView videoUsername;

        public VideoVh(View itemView) {
            super(itemView);
            mImg = (MyImageView) itemView.findViewById(R.id.img);
            videoAvatar = (RoundedImageView) itemView.findViewById(R.id.videoAvatar);
            videoTitle = (TextView) itemView.findViewById(R.id.videoTitle);
            videoUsername = (TextView) itemView.findViewById(R.id.videoUsername);
            mImg.setOnClickListener(mOnVideoClickListener);
        }

        @Override
        public void setData(ImMessageBean bean, int position, Object payload) {
            super.setData(bean, position, payload);
            if (payload == null) {
                mImg.setTag(position);
                CustomContent content = (CustomContent) bean.getRawMessage().getContent();
                Map<String, String> map = content.getAllStringValues();
                String cover = map.get(ImMessageUtil.CUSTOM_VIDEO_COVER);
                String title = map.get(ImMessageUtil.CUSTOM_VIDEO_TITLE);
                String username = map.get(ImMessageUtil.CUSTOM_VIDEO_USERNAME);
                String avatar = map.get(ImMessageUtil.CUSTOM_VIDEO_AVATAR);
                ImgLoader.display(cover, mImg);
                ImgLoader.displayAvatar(avatar, videoAvatar);
                videoTitle.setText(title);
                videoUsername.setText(username);
            }
        }
    }

    class SelfVideoVh extends Vh {

        MyImageView mImg;
        RoundedImageView videoAvatar;
        TextView videoTitle;
        TextView videoUsername;

        public SelfVideoVh(View itemView) {
            super(itemView);
            mImg = (MyImageView) itemView.findViewById(R.id.img);
            videoAvatar = (RoundedImageView) itemView.findViewById(R.id.videoAvatar);
            videoTitle = (TextView) itemView.findViewById(R.id.videoTitle);
            videoUsername = (TextView) itemView.findViewById(R.id.videoUsername);
            mImg.setOnClickListener(mOnVideoClickListener);
        }

        @Override
        public void setData(ImMessageBean bean, int position, Object payload) {
            super.setData(bean, position, payload);
            if (payload == null) {
                mImg.setTag(position);
                CustomContent content = (CustomContent) bean.getRawMessage().getContent();
                Map<String, String> map = content.getAllStringValues();
                String cover = map.get(ImMessageUtil.CUSTOM_VIDEO_COVER);
                String title = map.get(ImMessageUtil.CUSTOM_VIDEO_TITLE);
                String username = map.get(ImMessageUtil.CUSTOM_VIDEO_USERNAME);
                String avatar = map.get(ImMessageUtil.CUSTOM_VIDEO_AVATAR);
                ImgLoader.display(cover, mImg);
                ImgLoader.displayAvatar(avatar, videoAvatar);
                videoTitle.setText(title);
                videoUsername.setText(username);
            }
        }
    }

    class RedPacketVH extends Vh {

        private BubbleLayout layout;

        private TextView txRemark;
        private ImageView ivRedPacket;

        public RedPacketVH(View itemView) {
            super(itemView);
            layout = itemView.findViewById(R.id.layout);
            txRemark = itemView.findViewById(R.id.txRemark);
            ivRedPacket = itemView.findViewById(R.id.ivRedPacket);
            layout.setOnClickListener(mOnRedPacketClickListener);
        }

        @Override
        public void setData(ImMessageBean bean, int position, Object payload) {
            super.setData(bean, position, payload);
            if (payload == null) {
                layout.setTag(position);
                CustomContent content = (CustomContent) bean.getRawMessage().getContent();
                Map<String, String> map = content.getAllStringValues();
                txRemark.setText(map.get(ImMessageUtil.CUSTOM_RED_PACKET_REMARK));
                ivRedPacket.setBackgroundResource(bean.isRedOpen() ? R.mipmap.icon_red_packet_open : R.mipmap.icon_red_packet);
            } else {
                int tag = (int) payload;
                if (tag == UPDATE_RED_PACKET) {
                    ivRedPacket.setBackgroundResource(bean.isRedOpen() ? R.mipmap.icon_red_packet_open : R.mipmap.icon_red_packet);
                }
            }
        }
    }

    class SelfRedPacketVH extends Vh {

        private BubbleLayout layout;
        private TextView txRemark;
        private ImageView ivRedPacket;

        public SelfRedPacketVH(View itemView) {
            super(itemView);
            layout = itemView.findViewById(R.id.layout);
            txRemark = itemView.findViewById(R.id.txRemark);
            ivRedPacket = itemView.findViewById(R.id.ivRedPacket);
            layout.setOnClickListener(mOnRedPacketClickListener);
        }

        @Override
        public void setData(ImMessageBean bean, int position, Object payload) {
            super.setData(bean, position, payload);
            if (payload == null) {
                layout.setTag(position);
                CustomContent content = (CustomContent) bean.getRawMessage().getContent();
                Map<String, String> map = content.getAllStringValues();
                txRemark.setText(map.get(ImMessageUtil.CUSTOM_RED_PACKET_REMARK));
                ivRedPacket.setBackgroundResource(bean.isRedOpen() ? R.mipmap.icon_red_packet_open : R.mipmap.icon_red_packet);
            } else {
                if (payload instanceof Integer) {
                    int tag = (int) payload;
                    if (tag == UPDATE_RED_PACKET) {
                        ivRedPacket.setBackgroundResource(bean.isRedOpen() ? R.mipmap.icon_red_packet_open : R.mipmap.icon_red_packet);
                    }
                }
            }
        }
    }

    class OpenRedPacketVH extends Vh {

        private TextView txText;

        public OpenRedPacketVH(View itemView) {
            super(itemView);
            txText = itemView.findViewById(R.id.txText);
        }

        @Override
        public void setData(ImMessageBean bean, int position, Object payload) {
            super.setData(bean, position, payload);
            if (payload == null) {
                CustomContent content = (CustomContent) bean.getRawMessage().getContent();
                Map<String, String> map = content.getAllStringValues();
                String fromId = map.get(ImMessageUtil.CUSTOM_RED_PACKET_FORM);

                if (AppConfig.getInstance().getUid().equals(fromId)) {
                    //别人领,我发的红包
                    txText.setText(String.format("%s领取了我的红包", mToUserBean.getUsername()));
                } else {
                    txText.setText(String.format("我领取了%s的红包", mToUserBean.getUsername()));
                }
            }
        }
    }


    class VoiceVh extends Vh {

        TextView mDuration;
        View mRedPoint;
        ChatVoiceLayout mChatVoiceLayout;

        public VoiceVh(View itemView) {
            super(itemView);
            mRedPoint = itemView.findViewById(R.id.red_point);
            mDuration = (TextView) itemView.findViewById(R.id.duration);
            mChatVoiceLayout = itemView.findViewById(R.id.voice);
            mChatVoiceLayout.setOnClickListener(mOnVoiceClickListener);
        }

        @Override
        public void setData(ImMessageBean bean, int position, Object payload) {
            super.setData(bean, position, payload);
            if (payload == null) {
                mDuration.setText(bean.getVoiceDuration() + "s");
                mChatVoiceLayout.setTag(position);
                mChatVoiceLayout.setMsgId(bean.getMessageId());
                mChatVoiceLayout.setDuration(bean.getVoiceDuration());
            }
            if (bean.isRead()) {
                if (mRedPoint.getVisibility() == View.VISIBLE) {
                    mRedPoint.setVisibility(View.INVISIBLE);
                }
            } else {
                if (mRedPoint.getVisibility() != View.VISIBLE) {
                    mRedPoint.setVisibility(View.VISIBLE);
                }
            }
        }
    }

    class SelfVoiceVh extends Vh {

        TextView mDuration;
        ChatVoiceLayout mChatVoiceLayout;
        View mFailIcon;
        View mLoading;

        public SelfVoiceVh(View itemView) {
            super(itemView);
            mDuration = (TextView) itemView.findViewById(R.id.duration);
            mChatVoiceLayout = itemView.findViewById(R.id.voice);
            mChatVoiceLayout.setOnClickListener(mOnVoiceClickListener);
            mFailIcon = itemView.findViewById(R.id.icon_fail);
            mLoading = itemView.findViewById(R.id.loading);
        }

        @Override
        public void setData(ImMessageBean bean, int position, Object payload) {
            super.setData(bean, position, payload);
            if (payload == null) {
                mDuration.setText(bean.getVoiceDuration() + "s");
                mChatVoiceLayout.setTag(position);
                mChatVoiceLayout.setMsgId(bean.getMessageId());
                mChatVoiceLayout.setDuration(bean.getVoiceDuration());
            }
            if (bean.isLoading()) {
                if (mLoading.getVisibility() != View.VISIBLE) {
                    mLoading.setVisibility(View.VISIBLE);
                }
            } else {
                if (mLoading.getVisibility() == View.VISIBLE) {
                    mLoading.setVisibility(View.INVISIBLE);
                }
            }
            if (bean.isSendFail()) {
                if (mFailIcon.getVisibility() != View.VISIBLE) {
                    mFailIcon.setVisibility(View.VISIBLE);
                }
            } else {
                if (mFailIcon.getVisibility() == View.VISIBLE) {
                    mFailIcon.setVisibility(View.INVISIBLE);
                }
            }
        }
    }


    class LocationVh extends Vh {

        TextView mTitle;
        TextView mAddress;
        ImageView mMap;

        public LocationVh(View itemView) {
            super(itemView);
            mTitle = (TextView) itemView.findViewById(R.id.title);
            mAddress = (TextView) itemView.findViewById(R.id.address);
            mMap = (ImageView) itemView.findViewById(R.id.map);
        }

        @Override
        public void setData(ImMessageBean bean, int position, Object payload) {
            super.setData(bean, position, payload);
            if (payload == null) {
                LocationContent locationContent = (LocationContent) (bean.getRawMessage().getContent());
                try {
                    JSONObject obj = JSON.parseObject(locationContent.getAddress());
                    mTitle.setText(obj.getString("name"));
                    mAddress.setText(obj.getString("info"));
                } catch (Exception e) {
                    mTitle.setText("");
                    mAddress.setText("");
                }
                int zoom = locationContent.getScale().intValue();
                if (zoom > 18 || zoom < 4) {
                    zoom = 18;
                }
                double lat = locationContent.getLatitude().doubleValue();
                double lng = locationContent.getLongitude().doubleValue();
                //腾讯地图生成静态图接口
                String staticMapUrl = "https://apis.map.qq.com/ws/staticmap/v2/?center=" + lat + "," + lng + "&size=200*120&scale=2&zoom=" + zoom + "&key=" + mTxLocationKey;
                ImgLoader.display(staticMapUrl, mMap);
            }

        }
    }

    class SelfLocationVh extends LocationVh {

        View mFailIcon;
        View mLoading;

        public SelfLocationVh(View itemView) {
            super(itemView);
            mFailIcon = itemView.findViewById(R.id.icon_fail);
            mLoading = itemView.findViewById(R.id.loading);
        }

        @Override
        public void setData(ImMessageBean bean, int position, Object payload) {
            super.setData(bean, position, payload);
            if (bean.isLoading()) {
                if (mLoading.getVisibility() != View.VISIBLE) {
                    mLoading.setVisibility(View.VISIBLE);
                }
            } else {
                if (mLoading.getVisibility() == View.VISIBLE) {
                    mLoading.setVisibility(View.INVISIBLE);
                }
            }
            if (bean.isSendFail()) {
                if (mFailIcon.getVisibility() != View.VISIBLE) {
                    mFailIcon.setVisibility(View.VISIBLE);
                }
            } else {
                if (mFailIcon.getVisibility() == View.VISIBLE) {
                    mFailIcon.setVisibility(View.INVISIBLE);
                }
            }

        }
    }

    public void setActionListener(ActionListener actionListener) {
        mActionListener = actionListener;
    }

    public interface ActionListener {
        void onImageClick(MyImageView imageView, int x, int y);

        void onVoiceStartPlay(File voiceFile);

        void onVoiceStopPlay();

        void onVideoClick(String videoId);

        void onRedPacketClick(String redPacketId);
    }

    public void release() {
        if (mAnimator != null) {
            mAnimator.cancel();
        }
        mActionListener = null;
        mOnImageClickListener = null;
        mOnVoiceClickListener = null;
    }

    public void updateOpenRedPacket(String packetId) {
        for (int i = 0; i < mList.size(); i++) {
            Message rawMessage = mList.get(i).getRawMessage();
            if (rawMessage.getContentType() == ContentType.custom) {
                //自定义消息
                CustomContent content = (CustomContent) rawMessage.getContent();
                String customType = content.getStringValue(ImMessageUtil.CUSTOM_TYPE);
                if (customType.equals(ImMessageUtil.CUSTOM_TYPE_RED_PACKET)) {
                    //红包消息
                    String redId = content.getStringValue(ImMessageUtil.CUSTOM_RED_PACKET_ID);
                    if (packetId.equals(redId)) {
                        mList.get(i).setRedOpen(true);
                        notifyItemChanged(i, UPDATE_RED_PACKET);
                        break;
                    }
                }
            }
        }
    }

    public static final int UPDATE_RED_PACKET = 1001;

}
