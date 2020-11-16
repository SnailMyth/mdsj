package com.wwsl.mdsj.adapter;

import android.annotation.SuppressLint;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.makeramen.roundedimageview.RoundedImageView;
import com.wwsl.mdsj.AppConfig;
import com.wwsl.mdsj.R;
import com.wwsl.mdsj.bean.GiftDetailShowBean;
import com.wwsl.mdsj.glide.ImgLoader;
import com.wwsl.mdsj.http.HttpConst;
import com.wwsl.mdsj.utils.DpUtil;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

import cn.hutool.core.util.StrUtil;

public class GiftDetailAdapter extends BaseQuickAdapter<GiftDetailShowBean, BaseViewHolder> {

    private String voteName;
    private String coinName;

    public GiftDetailAdapter(@Nullable List<GiftDetailShowBean> data) {
        super(R.layout.item_account_gift, data);
        voteName = AppConfig.getInstance().getVotesName();
        coinName = AppConfig.getInstance().getCoinName();
        addChildClickViewIds(R.id.videoCover);
    }

    @SuppressLint("SetTextI18n")
    @Override
    protected void convert(@NonNull BaseViewHolder helper, GiftDetailShowBean item) {
        String action = item.getAction();
        ImageView icon = helper.getView(R.id.detail_icon);
        TextView name = helper.getView(R.id.detail_name);
        TextView des = helper.getView(R.id.detail_des);
        TextView tag = helper.getView(R.id.detail_tag);
        TextView videoMoney = helper.getView(R.id.videoMoney);
        RoundedImageView videoCover = helper.getView(R.id.videoCover);

        switch (item.getBelongType()) {
            case HttpConst.DETAIL_ACTION_VIDEO:
                //视频打赏
                tag.setVisibility(View.GONE);
                videoMoney.setVisibility(View.VISIBLE);
                videoCover.setVisibility(View.VISIBLE);

                if (!StrUtil.isEmpty(item.getGifticon())) {
                    ImgLoader.display(item.getGifticon(), icon);
                }
                name.setText(item.getGiftinfo());

                if (HttpConst.DETAIL_TYPE_GIFT_EXPEND.equals(item.getType())) {
                    des.setText(String.format("视频打赏%s送出", item.getTouserinfo()));
                } else {
                    des.setText(String.format("收到%s视频打赏", item.getTouserinfo()));
                }

                if (!StrUtil.isEmpty(item.getCover())) {
                    ImgLoader.display(item.getCover(), videoCover);
                }
                break;
            case HttpConst.DETAIL_ACTION_GIFT:
                //礼物
                tag.setVisibility(View.VISIBLE);
                videoMoney.setVisibility(View.GONE);
                videoCover.setVisibility(View.GONE);
                if (!StrUtil.isEmpty(item.getGifticon())) {
                    ImgLoader.display(item.getGifticon(), icon);
                }
                name.setText(item.getGiftinfo());
                if (HttpConst.DETAIL_TYPE_GIFT_EXPEND.equals(item.getType())) {
                    des.setText(String.format("在%s直播间送出", item.getTouserinfo()));
                } else {
                    des.setText(String.format("收到%s送出的礼物", item.getTouserinfo()));
                }
                tag.setText(String.format("X%s", item.getGiftcount()));
                break;
            case HttpConst.DETAIL_ACTION_CHARGE:
                //充值
                tag.setVisibility(View.VISIBLE);
                videoMoney.setVisibility(View.GONE);
                videoCover.setVisibility(View.GONE);
                name.setText(coinName + "充值");
                ImgLoader.display(R.mipmap.default_coin, icon);
                des.setText("");
                tag.setText(String.format("+%s%s", new BigDecimal(item.getTotalcoin()).setScale(2, RoundingMode.HALF_UP), coinName));
                break;
            case HttpConst.DETAIL_ACTION_ALL:
                //收支记录
                tag.setVisibility(View.VISIBLE);
                videoMoney.setVisibility(View.GONE);
                videoCover.setVisibility(View.GONE);

                String txName;
                String txDes = "";
                String money = "";
                int moneyColor = R.color.red;
                int iconId = R.mipmap.default_coin;
                String iconUrl = null;

                if (HttpConst.DETAIL_ACTION_VIDEO.equals(action)) {
                    tag.setVisibility(View.GONE);
                    videoMoney.setVisibility(View.VISIBLE);
                    videoCover.setVisibility(View.VISIBLE);
                    iconUrl = item.getGifticon();
                    txName = item.getGiftinfo();
                    name.setText(item.getGiftinfo());
                    if (HttpConst.DETAIL_TYPE_GIFT_EXPEND.equals(item.getType())) {
                        txDes = String.format("视频打赏%s送出", item.getTouserinfo());
                        videoMoney.setText(String.format("-%s%s", new BigDecimal(item.getTotalcoin()).toString(), coinName));
                    } else {
                        txDes = String.format("收到%s视频打赏", item.getTouserinfo());
                        videoMoney.setText(String.format("+%s%s", new BigDecimal(item.getTotalcoin()).toString(), voteName));
                    }

                    if (!StrUtil.isEmpty(item.getCover())) {
                        ImgLoader.display(item.getCover(), videoCover);
                    }

                } else if (HttpConst.DETAIL_ACTION_GIFT.equals(action)) {
                    iconUrl = item.getGifticon();
                    txName = item.getGiftinfo();
                    if (HttpConst.DETAIL_TYPE_GIFT_EXPEND.equals(item.getType())) {
                        txDes = String.format("在%s直播间送出", item.getTouserinfo());
                        money = String.format("-%s%s", new BigDecimal(item.getTotalcoin()).toString(), coinName);
                        moneyColor = R.color.red;
                    } else {
                        txDes = String.format("收到%s送出的礼物", item.getTouserinfo());
                        money = String.format("+%s%s", new BigDecimal(item.getTotalcoin()).toString(), voteName);
                        moneyColor = R.color.red;
                    }
                } else if (HttpConst.DETAIL_ACTION_CHARGE.equals(action)) {
                    txName = coinName + "充值";
                    money = String.format("+%s%s", new BigDecimal(item.getTotalcoin()).setScale(2, RoundingMode.HALF_UP), coinName);
                    moneyColor = R.color.pk_blue;
                } else if (HttpConst.DETAIL_ACTION_BUY_VIDEO.equals(action)) {
                    BigDecimal count = new BigDecimal(item.getGiftcount());
                    BigDecimal multiply = count.multiply(new BigDecimal(item.getTotalcoin())).setScale(2, RoundingMode.HALF_UP);
                    if (HttpConst.DETAIL_TYPE_GIFT_EXPEND.equals(item.getType())) {
                        txName = "视频付费";
                        txDes = item.getGiftinfo();
                        money = String.format("-%s%s", multiply, coinName);
                        moneyColor = R.color.red;
                    } else {
                        txName = "视频收益";
                        txDes = item.getGiftinfo();
                        money = String.format("+%s%s", multiply, coinName);
                        moneyColor = R.color.pk_blue;
                    }
                } else {
                    money = String.format("-%s%s", new BigDecimal(item.getTotalcoin()).setScale(2, RoundingMode.HALF_UP), coinName);
                    moneyColor = R.color.red;
                    switch (action) {
                        case HttpConst.DETAIL_ACTION_LOGIN_BONUS:
                            txName = "充值赠送";
                            break;
                        case HttpConst.DETAIL_ACTION_SEND_BARRAGE:
                            txName = "发送弹幕";
                            break;
                        case HttpConst.DETAIL_ACTION_GAME_DEPOSIT_ON:
                            txName = "上庄扣除";
                            break;
                        case HttpConst.DETAIL_ACTION_GAME_WIN:
                            txName = "游戏获胜";
                            money = String.format("+%s%s", new BigDecimal(item.getTotalcoin()).setScale(2, RoundingMode.HALF_UP), coinName);
                            moneyColor = R.color.pk_blue;
                            break;
                        case HttpConst.DETAIL_ACTION_GAME_RETURN:
                            txName = "游戏退还";
                            money = String.format("+%s%s", new BigDecimal(item.getTotalcoin()).setScale(2, RoundingMode.HALF_UP), coinName);
                            moneyColor = R.color.pk_blue;
                            break;
                        case HttpConst.DETAIL_ACTION_GAME_BET:
                            txName = "游戏下注";
                            break;
                        case HttpConst.DETAIL_ACTION_GAME_BANKER:
                            txName = "庄家收益";
                            money = String.format("+%s%s", new BigDecimal(item.getTotalcoin()).setScale(2, RoundingMode.HALF_UP), coinName);
                            moneyColor = R.color.pk_blue;
                            break;
                        case HttpConst.DETAIL_ACTION_GAME_DEPOSIT_DOWN:
                            txName = "下庄退款";
                            money = String.format("+%s%s", new BigDecimal(item.getTotalcoin()).setScale(2, RoundingMode.HALF_UP), coinName);
                            moneyColor = R.color.pk_blue;
                            break;
                        case HttpConst.DETAIL_ACTION_ROOM:
                            txName = "直播房间收费";
                            if (HttpConst.DETAIL_TYPE_GIFT_EXPEND.equals(item.getType())) {
                                money = String.format("-%s%s", new BigDecimal(item.getTotalcoin()).setScale(2, RoundingMode.HALF_UP), coinName);
                            } else {
                                money = String.format("+%s%s", new BigDecimal(item.getTotalcoin()).setScale(2, RoundingMode.HALF_UP), voteName);
                            }
                            break;
                        case HttpConst.DETAIL_ACTION_CHAT_RED_PACKET:
                            txName = "聊天红包";
                            iconId = R.mipmap.icon_red_packet_1;
                            if (HttpConst.DETAIL_TYPE_GIFT_EXPEND.equals(item.getType())) {
                                money = String.format("-%s%s", new BigDecimal(item.getTotalcoin()).setScale(2, RoundingMode.HALF_UP), coinName);
                                txDes = String.format("发给%s聊天红包", item.getTouserinfo());
                            } else {
                                money = String.format("+%s%s", new BigDecimal(item.getTotalcoin()).setScale(2, RoundingMode.HALF_UP), coinName);
                                txDes = String.format("收到%s聊天红包", item.getTouserinfo());
                            }
                            break;
                        case HttpConst.DETAIL_ACTION_TIME:
                            txName = "直播计时收费";
                            if (HttpConst.DETAIL_TYPE_GIFT_EXPEND.equals(item.getType())) {
                                money = String.format("-%s%s", new BigDecimal(item.getTotalcoin()).setScale(2, RoundingMode.HALF_UP), coinName);
                            } else {
                                money = String.format("+%s%s", new BigDecimal(item.getTotalcoin()).setScale(2, RoundingMode.HALF_UP), voteName);
                                moneyColor = R.color.pk_blue;
                            }
                            break;
                        case HttpConst.DETAIL_ACTION_VIP:
                            txName = "购买vip";
                            break;
                        case HttpConst.DETAIL_ACTION_CAR:
                            txName = "购买坐骑";
                            break;
                        case HttpConst.DETAIL_ACTION_SEND_RED_PACKET:
                            txName = "发红包";
                            iconId = R.mipmap.redpack;
                            break;
                        case HttpConst.DETAIL_ACTION_ROB_RED_PACKET:
                            txName = "抢红包";
                            iconId = R.mipmap.redpack;
                            money = String.format("+%s%s", new BigDecimal(item.getTotalcoin()).setScale(2, RoundingMode.HALF_UP), AppConfig.getInstance().getCoinName());
                            moneyColor = R.color.pk_blue;
                            break;
                        case HttpConst.DETAIL_ACTION_GUARD:
                            txName = "购买守护";
                            iconId = R.mipmap.shouhu;
                            break;
                        case HttpConst.DETAIL_ACTION_REG_REWARD:
                            txName = "注册赠送";
                            break;
                        case HttpConst.DETAIL_ACTION_TICKET:
                            txName = "购买门票";
                            break;
                        case HttpConst.DETAIL_ACTION_LIANG:
                        default:
                            txName = "消费支出";
                            break;
                    }
                }

                if (StrUtil.isEmpty(iconUrl)) {
                    ImgLoader.display(iconId, icon);
                } else {
                    ImgLoader.display(iconUrl, icon);
                }
                name.setText(txName);
                des.setText(txDes);
                tag.setText(money);
                tag.setTextColor(getContext().getResources().getColor(moneyColor));
                break;
        }

        helper.setText(R.id.detail_time, item.getAddtime());
    }

    public void insertList(List<GiftDetailShowBean> list) {
        if (list != null && list.size() > 0) {
            int p = getData().size();
            getData().addAll(list);
            notifyItemRangeInserted(p, list.size());
            getRecyclerView().scrollBy(0, DpUtil.dp2px(80));
        }
    }
}
