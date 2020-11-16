package com.wwsl.mdsj.dialog;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import androidx.viewpager.widget.ViewPager;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.wwsl.mdsj.AppConfig;
import com.wwsl.mdsj.Constants;
import com.wwsl.mdsj.R;
import com.wwsl.mdsj.activity.live.LiveAnchorActivity;
import com.wwsl.mdsj.adapter.LiveFunctionPagerAdapter;
import com.wwsl.mdsj.bean.LiveFunctionBean;
import com.wwsl.mdsj.interfaces.LiveFunctionClickListener;
import com.wwsl.mdsj.interfaces.OnItemClickListener;
import com.wwsl.mdsj.utils.DpUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by cxf on 2018/10/9.
 */

public class LiveFunctionDialogFragment extends AbsDialogFragment implements OnItemClickListener<Integer> {
    private ViewPager mViewPager;
    private RadioGroup mRadioGroup;
    private LiveFunctionPagerAdapter liveFunctionPagerAdapter;
    private LiveFunctionClickListener mFunctionClickListener;

    @Override
    protected int getLayoutId() {
        return R.layout.dialog_live_function;
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
        window.setWindowAnimations(R.style.bottomToTopAnim);
        WindowManager.LayoutParams params = window.getAttributes();
        params.width = WindowManager.LayoutParams.MATCH_PARENT;
        params.height = WindowManager.LayoutParams.WRAP_CONTENT;
        params.gravity = Gravity.BOTTOM;
        params.y = DpUtil.dp2px(60);
        window.setAttributes(params);
        window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewPager = (ViewPager) mRootView.findViewById(R.id.viewPager);
        mViewPager.setOffscreenPageLimit(4);
        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                if (mRadioGroup != null) {
                    ((RadioButton) mRadioGroup.getChildAt(position)).setChecked(true);
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        mRadioGroup = (RadioGroup) mRootView.findViewById(R.id.radio_group);

        boolean hasGame = false;
        Bundle bundle = getArguments();
        if (bundle != null) {
            hasGame = bundle.getBoolean(Constants.HAS_GAME, false);
        }
        List<LiveFunctionBean> mList;
        mList = new ArrayList<>();
        mList.add(new LiveFunctionBean(Constants.LIVE_FUNC_BEAUTY, R.mipmap.icon_live_func_beauty, R.string.live_beauty));
        mList.add(new LiveFunctionBean(Constants.LIVE_FUNC_CAMERA, R.mipmap.icon_live_func_camera, R.string.live_camera));
        mList.add(new LiveFunctionBean(Constants.LIVE_FUNC_FLASH, R.mipmap.icon_live_func_flash, R.string.live_flash));
        mList.add(new LiveFunctionBean(Constants.LIVE_FUNC_MUSIC, R.mipmap.icon_live_func_music, R.string.live_music));
        if (hasGame) {//含有游戏
            mList.add(new LiveFunctionBean(Constants.LIVE_FUNC_GAME, R.mipmap.icon_live_func_game, R.string.live_game));
        }
        mList.add(new LiveFunctionBean(Constants.LIVE_FUNC_RED_PACK, R.mipmap.icon_live_func_rp, R.string.live_red_pack));
        mList.add(new LiveFunctionBean(Constants.LIVE_FUNC_LINK_MIC, R.mipmap.icon_live_func_lm, R.string.live_link_mic));
        // TODO: 2020/7/23 暂时屏蔽直播消息
//        mList.add(new LiveFunctionBean(Constants.LIVE_FUNC_LINK_MESSAGE, R.mipmap.icon_live_func_share, R.string.live_message));

        if (AppConfig.showWishBill()) {
            mList.add(new LiveFunctionBean(Constants.LIVE_FUNC_LINK_WISH, R.mipmap.icon_live_func_wish, R.string.live_wish));
        }
        liveFunctionPagerAdapter = new LiveFunctionPagerAdapter(mContext, mList);
        liveFunctionPagerAdapter.setOnItemClickListener(this);
        mViewPager.setAdapter(liveFunctionPagerAdapter);

        LayoutInflater inflater = LayoutInflater.from(mContext);
        for (int i = 0, size = liveFunctionPagerAdapter.getCount(); i < size; i++) {
            RadioButton radioButton = (RadioButton) inflater.inflate(R.layout.view_gift_indicator, mRadioGroup, false);
            radioButton.setBackgroundResource(R.drawable.bg_live_function_indicator);
            radioButton.setId(i + 20000);
            if (i == 0) {
                radioButton.setChecked(true);
            }
            mRadioGroup.addView(radioButton);
        }
        if (liveFunctionPagerAdapter.getCount() > 1) {
            mRadioGroup.setVisibility(View.VISIBLE);
        } else {
            mRadioGroup.setVisibility(View.GONE);
        }
    }

    public void setFunctionClickListener(LiveFunctionClickListener functionClickListener) {
        mFunctionClickListener = functionClickListener;
    }

    @Override
    public void onItemClick(Integer bean, int position) {
        dismiss();
        if (mFunctionClickListener != null) {
            mFunctionClickListener.onClick(bean);
        }
    }

    @Override
    public void onDestroy() {
        mFunctionClickListener = null;
        ((LiveAnchorActivity) mContext).setBtnFunctionDark();
        super.onDestroy();
    }
}
