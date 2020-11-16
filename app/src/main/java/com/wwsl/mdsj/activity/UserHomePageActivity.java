package com.wwsl.mdsj.activity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.viewpager.widget.ViewPager;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.flyco.tablayout.SlidingTabLayout;
import com.frame.fire.util.LogUtils;
import com.google.android.material.appbar.AppBarLayout;
import com.lxj.xpopup.XPopup;
import com.permissionx.guolindev.PermissionX;
import com.wwsl.mdsj.AppConfig;
import com.wwsl.mdsj.Constants;
import com.wwsl.mdsj.R;
import com.wwsl.mdsj.activity.message.ChatRoomActivity;
import com.wwsl.mdsj.activity.video.VideoReportActivity;
import com.wwsl.mdsj.adapter.CommPagerAdapter;
import com.wwsl.mdsj.base.BaseActivity;
import com.wwsl.mdsj.bean.UserBean;
import com.wwsl.mdsj.bean.UserDetailBean;
import com.wwsl.mdsj.bean.UserLabelBean;
import com.wwsl.mdsj.custom.DrawableTextView;
import com.wwsl.mdsj.event.VideoFollowEvent;
import com.wwsl.mdsj.fragment.WorkFragment;
import com.wwsl.mdsj.glide.ImgLoader;
import com.wwsl.mdsj.http.HttpCallback;
import com.wwsl.mdsj.http.HttpUtil;
import com.wwsl.mdsj.interfaces.CommonCallback;
import com.wwsl.mdsj.utils.CommonUtil;
import com.wwsl.mdsj.utils.DialogUtil;
import com.wwsl.mdsj.utils.DpUtil;
import com.wwsl.mdsj.utils.ToastUtil;
import com.wwsl.mdsj.utils.tiktok.NumUtils;
import com.wwsl.mdsj.views.CircleImageView;
import com.wwsl.mdsj.views.IconFontTextView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import cn.hutool.core.util.StrUtil;

public class UserHomePageActivity extends BaseActivity implements View.OnClickListener {

    @BindView(R.id.iv_bg)
    ImageView ivBg;
    @BindView(R.id.iv_head)
    CircleImageView ivHead;
    @BindView(R.id.rl_dropdown)
    RelativeLayout rlDropdown;
    @BindView(R.id.ll_focus)
    LinearLayout llFocus;
    @BindView(R.id.ll_fans)
    LinearLayout llFans;
    @BindView(R.id.iv_return)
    ImageView ivReturn;
    @BindView(R.id.tv_title)
    TextView tvTitle;
    @BindView(R.id.tv_focus)
    TextView tvFocus;
    @BindView(R.id.iv_more)
    IconFontTextView ivMore;
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.tablayout)
    SlidingTabLayout tabLayout;
    @BindView(R.id.appbarlayout)
    AppBarLayout appBarLayout;
    @BindView(R.id.viewpager)
    ViewPager viewPager;
    @BindView(R.id.tv_nickname)
    TextView tvNickname;
    @BindView(R.id.tv_sign)
    TextView tvSign;
    @BindView(R.id.tv_getlike_count)
    TextView tvGetLikeCount;
    @BindView(R.id.tv_focus_count)
    TextView tvFocusCount;
    @BindView(R.id.tv_fans_count)
    TextView tvFansCount;
    @BindView(R.id.tv_addfocus)
    TextView tvAddFocus;
    @BindView(R.id.tvUid)
    TextView tvUid;
    @BindView(R.id.txMessage)
    TextView txMessage;
    @BindView(R.id.userTagLinear)
    LinearLayout tagLinear;
    @BindView(R.id.goodsLayout)
    ConstraintLayout goodsLayout;
    @BindView(R.id.tvCopy)
    TextView tvCopy;
    @BindView(R.id.tvPhone)
    DrawableTextView tvPhone;
    private ArrayList<WorkFragment> fragments = new ArrayList<>();
    private CommPagerAdapter pagerAdapter;
    private UserDetailBean curUserBean;

    private volatile String userId = "";

    @Override
    protected int setLayoutId() {
        return R.layout.activity_user_home_page;
    }

    @Override
    protected void init() {
        ButterKnife.bind(this);
        EventBus.getDefault().register(this);
        userId = getIntent().getStringExtra("uid");
        //解决toolbar左边距问题
        toolbar.setContentInsetsAbsolute(0, 0);

        setAppbarLayoutPercent();

        ivReturn.setOnClickListener(this);
        ivHead.setOnClickListener(this);
        ivBg.setOnClickListener(this);
        llFocus.setOnClickListener(this);
        llFans.setOnClickListener(this);
        tvAddFocus.setOnClickListener(this);
        txMessage.setOnClickListener(this);
        ivMore.setOnClickListener(this);
        tvCopy.setOnClickListener(this);
        tvPhone.setOnClickListener(this);

        initTabLayout();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tvCopy:
            case R.id.tvUid:
                CommonUtil.copyText(this, curUserBean.getId());
                break;
            case R.id.iv_return:
                finish();
                break;
            case R.id.iv_head:
                transitionAnim(ivHead, curUserBean.getAvatar());
                break;
            case R.id.iv_bg:
                break;
            case R.id.tvPhone:
                callUser();
                break;
            case R.id.ll_focus:
            case R.id.ll_fans:
//                startActivity(new Intent(getActivity(), FollowActivity.class));
                break;
            case R.id.tv_addfocus:
                followUser();
                break;
            case R.id.iv_more:
                openReport();
                break;
            case R.id.txMessage:
                ChatRoomActivity.forward(this, UserBean.builder().id(curUserBean.getId()).username(curUserBean.getUsername()).avatar(curUserBean.getAvatar()).build(), curUserBean.getAttention() == 1);
                break;
        }
    }

    private void callUser() {
        String phone = curUserBean.getMobile();
        if (!StrUtil.isEmpty(phone)) {
            PermissionX.init(this)
                    .permissions(Manifest.permission.CALL_PHONE)
                    .request((allGranted, grantedList, deniedList) -> {
                        if (allGranted) {
                            Intent intent = new Intent(Intent.ACTION_DIAL);
                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            intent.setData(Uri.parse("tel:" + phone));
                            if (intent.resolveActivity(getPackageManager()) != null) {
                                startActivity(intent);
                            }
                        } else {
                            ToastUtil.show("未授权无法拨打电话");
                        }
                    });
        }
    }

    /**
     * 根据滚动比例渐变view
     */
    private void setAppbarLayoutPercent() {
        appBarLayout.addOnOffsetChangedListener((appBarLayout, verticalOffset) -> {
            float percent = (Math.abs(verticalOffset * 1.0f) / appBarLayout.getTotalScrollRange());  //滑动比例

            if (percent > 0.8) {
                tvTitle.setVisibility(View.VISIBLE);
                tvFocus.setVisibility(View.VISIBLE);

                float alpha = 1 - (1 - percent) * 5;  //渐变变换
                tvTitle.setAlpha(alpha);
                tvFocus.setAlpha(alpha);
            } else {
                tvTitle.setVisibility(View.GONE);
                tvFocus.setVisibility(View.GONE);
            }
        });
    }

    /**
     * 自动回顶部
     */
    private void coordinatorLayoutBackTop() {
        CoordinatorLayout.Behavior behavior =
                ((CoordinatorLayout.LayoutParams) appBarLayout.getLayoutParams()).getBehavior();
        if (behavior instanceof AppBarLayout.Behavior) {
            AppBarLayout.Behavior appBarLayoutBehavior = (AppBarLayout.Behavior) behavior;
            int topAndBottomOffset = appBarLayoutBehavior.getTopAndBottomOffset();
            if (topAndBottomOffset != 0) {
                appBarLayoutBehavior.setTopAndBottomOffset(0);
            }
        }
    }


    private void initTabLayout() {
        String[] titles = new String[3];
        titles[0] = "作品0";
        titles[1] = "动态0";
        titles[2] = "喜欢0";

        int i = 1;
        fragments.clear();
        List<String> tList = new ArrayList<>();
        for (String title : titles) {
            fragments.add(WorkFragment.newInstance("" + (i++), ""));
            tList.add(title);
        }

        pagerAdapter = new CommPagerAdapter(getSupportFragmentManager(), fragments, tList);
        viewPager.setOffscreenPageLimit(3);
        viewPager.setAdapter(pagerAdapter);
        tabLayout.setViewPager(viewPager, titles);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    private void loadUserInfo() {
        HttpUtil.getUserHome(userId, new HttpCallback() {
            @Override
            public void onSuccess(int code, String msg, String[] info) {
                if (code == 0 && info.length > 0) {
                    JSONObject obj = JSON.parseObject(info[0]);
                    curUserBean = JSON.toJavaObject(obj, UserDetailBean.class);
                    if ("0".equals(curUserBean.getId())) {
                        ToastUtil.show("用户不存在");
                        showErrorPage();
                    } else {
                        try {
                            tvAddFocus.setClickable(true);
                            txMessage.setClickable(true);
                            ivMore.setClickable(true);
                            tvFocus.setClickable(true);

                            ImgLoader.display(curUserBean.getAvatar(), R.mipmap.icon_avatar_placeholder, ivBg);
                            ImgLoader.display(curUserBean.getAvatar(), R.mipmap.icon_avatar_placeholder, ivHead);
                            tvUid.setText(String.format("ID:%s", curUserBean.getId()));
                            tvNickname.setText(curUserBean.getUsername());
                            tvSign.setText(curUserBean.getSignature());
                            tvTitle.setText(curUserBean.getUsername());
                            String subCount = NumUtils.numberFilter2(curUserBean.getDzNames());
                            String focusCount = NumUtils.numberFilter2(curUserBean.getFollows());
                            String fansCount = NumUtils.numberFilter2(curUserBean.getFans());

                            //获赞 关注 粉丝
                            tvGetLikeCount.setText(subCount);
                            tvFocusCount.setText(focusCount);
                            tvFansCount.setText(fansCount);
                            tvPhone.setText(curUserBean.getMobile());
                            tvPhone.setVisibility(1 == curUserBean.getIsPhonePublic() ? View.VISIBLE : View.GONE);

                            //关注状态
                            onAttention(curUserBean.getAttention());
                            updateProduct();
                            updateTags();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                } else {
                    showErrorPage();
                    ToastUtil.show(msg);
                }
            }

            @Override
            public void onError() {
                showErrorPage();
            }
        });
    }

    private void showErrorPage() {
        tvAddFocus.setClickable(false);
        txMessage.setClickable(false);
        ivMore.setClickable(false);
        tvFocus.setClickable(false);
    }

    @SuppressLint("NewApi")
    private void updateTags() {
        tagLinear.removeAllViews();
        List<UserLabelBean> labels = curUserBean.getLabel();
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        for (UserLabelBean label : labels) {
            TextView textview = new TextView(this);
            textview.setText(label.getLabel());
            textview.setPadding(5, 2, 5, 2);
            textview.setTextColor(getResources().getColor(R.color.color_tag));
            textview.setTextSize(12);
            textview.setMaxWidth(DpUtil.dp2px(150));
            textview.setBackgroundResource(R.drawable.shape_round_halfwhite);
            textview.setLines(1);
            textview.setEllipsize(TextUtils.TruncateAt.END);
            layoutParams.setMarginStart(5);
            textview.setLayoutParams(layoutParams);
            tagLinear.addView(textview);
        }
    }

    private void onAttention(int attention) {
        if (attention == 1) {
            tvAddFocus.setText("已关注");
            tvAddFocus.setBackgroundResource(R.drawable.shape_round_halfwhite);
            tvFocus.setText("已关注");
            tvFocus.setBackgroundResource(R.drawable.shape_round_halfwhite);
        } else {
            tvAddFocus.setText("关注");
            tvAddFocus.setBackgroundResource(R.drawable.shape_round_red);
            tvFocus.setText("关注");
            tvFocus.setBackgroundResource(R.drawable.shape_round_red);
        }
    }

    public void updateUserInfo() {
        LogUtils.e(TAG, "updateUserInfo: userId-->" + userId);
        clearUI();
        coordinatorLayoutBackTop();
        loadUserInfo();
    }

    public void updateUserInfo(String uid) {
        userId = uid;
    }

    private final static String TAG = "UserHomePageActivity";

    private void updateProduct() {
        List<String> titles = new ArrayList<>();
        titles.add("作品" + NumUtils.numberFilter2(curUserBean.getVideoNum()));
        titles.add("动态" + NumUtils.numberFilter2(curUserBean.getTrends()));
        titles.add("喜欢" + NumUtils.numberFilter2(curUserBean.getLikeVideoNum()));
        pagerAdapter.updateTitle(titles);

        for (int i = 0; i < 3; i++) {
            fragments.get(i).setNewData(curUserBean.getId());
            tabLayout.getTitleView(i).setText(titles.get(i));
        }

        fragments.get(0).loadData();

    }

    /**
     * 过渡动画跳转页面
     *
     * @param view
     */
    public void transitionAnim(ImageView view, String res) {
        new XPopup.Builder(this)
                .asImageViewer(view, res, new ImgLoader())
                .show();
    }

    private void followUser() {
        if (StrUtil.isEmpty(userId)) return;
        HttpUtil.setAttention(Constants.FOLLOW_FROM_HOME, userId, new CommonCallback<Integer>() {
            @Override
            public void callback(Integer isAttention) {
                onAttention(isAttention);
            }
        });
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onFollowEvent(VideoFollowEvent e) {
        if (e.getMToUid().equals(userId)) {
            onAttention(e.getMIsAttention());
        }
    }

    public static void forward(Context context, String uid) {
        Intent intent = new Intent(context, UserHomePageActivity.class);
        intent.putExtra("uid", uid);
        context.startActivity(intent);
    }

    @Override
    protected void onResume() {
        super.onResume();
        updateUserInfo();
    }

    private void openReport() {
        if (AppConfig.getInstance().getUid().equals(userId)) {
            //不举报自己
            return;
        }

        DialogUtil.showStringArrayDialog(mContext, new Integer[]{R.string.live_user_jubao}, (text, tag) -> {
            if (tag == R.string.live_user_jubao) {
                VideoReportActivity.forward(mContext, userId);
            }
        });
    }

    private void clearUI() {

        ImgLoader.display(R.mipmap.icon_avatar_placeholder, ivBg);
        ImgLoader.display(R.mipmap.img_default_bg, ivHead);

        tvUid.setText(String.format("ID:%s", "0"));

        tvNickname.setText("");
        tvSign.setText("");
        tvTitle.setText("");

        String subCount = NumUtils.numberFilter2(0);
        String focusCount = NumUtils.numberFilter2(0);
        String fansCount = NumUtils.numberFilter2(0);

        //获赞 关注 粉丝
        tvGetLikeCount.setText(subCount);
        tvFocusCount.setText(focusCount);
        tvFansCount.setText(fansCount);

        //关注状态
        tvAddFocus.setText("已关注");
        tvAddFocus.setBackgroundResource(R.drawable.shape_round_halfwhite);
        tvFocus.setText("已关注");
        tvFocus.setBackgroundResource(R.drawable.shape_round_halfwhite);

        tvPhone.setText("");
        tvPhone.setVisibility(View.GONE);
        clearProduct();
        tagLinear.setVisibility(View.INVISIBLE);
    }

    private void clearProduct() {
        List<String> titles = new ArrayList<>();
        titles.add("作品" + NumUtils.numberFilter2(0));
        titles.add("动态" + NumUtils.numberFilter2(0));
        titles.add("喜欢" + NumUtils.numberFilter2(0));

        for (int i = 0; i < 3; i++) {
            tabLayout.getTitleView(i).setText(titles.get(i));
            fragments.get(i).clearData();
        }

        tabLayout.setCurrentTab(0);
    }
}
