package com.wwsl.mdsj.dialog;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.text.TextPaint;
import android.text.TextUtils;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.wwsl.mdsj.R;
import com.wwsl.mdsj.adapter.RedPackResultAdapter;
import com.wwsl.mdsj.bean.RedPackBean;
import com.wwsl.mdsj.bean.RedPackResultBean;
import com.wwsl.mdsj.glide.ImgLoader;
import com.wwsl.mdsj.http.HttpCallback;
import com.wwsl.mdsj.http.HttpUtil;
import com.wwsl.mdsj.utils.DpUtil;
import com.wwsl.mdsj.utils.TextViewUtil;
import com.wwsl.mdsj.utils.WordUtil;

import java.util.List;

/**
 * Created by cxf on 2018/11/21.
 * 红包领取详情弹窗
 */

public class LiveRedPackResultDialogFragment extends AbsDialogFragment {

    private ImageView mAvatar;
    private TextView mName;
    private TextView mWinCoin;
    private TextView mNum;
    private RecyclerView mRecyclerView;
    private RedPackBean mRedPackBean;
    private String mStream;
    private String mCoinName;


    @Override
    protected int getLayoutId() {
        return R.layout.dialog_live_red_pack_result;
    }

    @Override
    protected int getDialogStyle() {
        return R.style.dialog2;
    }

    @Override
    protected boolean canCancel() {
        return true;
    }

    @Override
    protected void setWindowAttributes(Window window) {
        WindowManager.LayoutParams params = window.getAttributes();
        params.width = DpUtil.dp2px(260);
        params.height = DpUtil.dp2px(330);
        params.gravity = Gravity.CENTER;
        window.setAttributes(params);
        window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
    }


    public void setRedPackBean(RedPackBean redPackBean) {
        mRedPackBean = redPackBean;
    }

    public void setStream(String stream) {
        mStream = stream;
    }

    public void setCoinName(String coinName) {
        mCoinName = coinName;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (mRedPackBean == null || TextUtils.isEmpty(mStream)) {
            return;
        }
        mAvatar = mRootView.findViewById(R.id.avatar);
        mName = mRootView.findViewById(R.id.name);
        mWinCoin = mRootView.findViewById(R.id.win_coin);
        mNum = mRootView.findViewById(R.id.num);
        mRecyclerView = mRootView.findViewById(R.id.recyclerView);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(mContext, LinearLayoutManager.VERTICAL, false));
//        if(mRedPackBean){
//
//        }
        HttpUtil.getRedPackResult(mStream, mRedPackBean.getId(), new HttpCallback() {
            @Override
            public void onSuccess(int code, String msg, String[] info) {
                if (code == 0 && info.length > 0) {
                    JSONObject obj = JSON.parseObject(info[0]);
                    JSONObject redPackInfo = obj.getJSONObject("redinfo");
                    if (redPackInfo != null) {
                        if (mAvatar != null) {
                            ImgLoader.displayAvatar(redPackInfo.getString("avatar"), mAvatar);
                        }
                        if (mName != null) {
                            mName.setText(String.format(WordUtil.getString(R.string.red_pack_17), redPackInfo.getString("user_nicename")));
                        }
                        if (mNum != null) {
                            mNum.setText(String.format(WordUtil.getString(R.string.red_pack_19),
                                    redPackInfo.getString("nums_rob") + "/" + redPackInfo.getString("nums"),
                                    redPackInfo.getString("coin_rob") + "/" + redPackInfo.getString("coin"),
                                    mCoinName));
                        }
                    }
                    String winCoinVal = obj.getString("win");
                    if (TextUtils.isEmpty(winCoinVal) || "0".equals(winCoinVal)) {//没抢到
                        if (mWinCoin != null) {
                            mWinCoin.setTextSize(TypedValue.COMPLEX_UNIT_SP, 24);
                            TextPaint tp = mWinCoin.getPaint();
                            tp.setFakeBoldText(true);
                            mWinCoin.setText(WordUtil.getString(R.string.red_pack_20));
                            TextViewUtil.setDrawableNull(mWinCoin);
                        }
                    } else {//抢到了
                        if (mWinCoin != null) {
                            mWinCoin.setText(winCoinVal);
                            TextViewUtil.setDrawableRight(mWinCoin, R.mipmap.icon_red_result_money);
                        }
                    }
                    if (mRecyclerView != null) {
                        List<RedPackResultBean> list = JSON.parseArray(obj.getString("list"), RedPackResultBean.class);
                        RedPackResultAdapter adapter = new RedPackResultAdapter(mContext, list);
                        mRecyclerView.setAdapter(adapter);
                    }
                }
            }
        });
    }
}
