package com.wwsl.mdsj.activity.me;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.widget.Toolbar;
import androidx.viewpager.widget.ViewPager;

import com.flyco.tablayout.SlidingTabLayout;
import com.google.android.material.appbar.AppBarLayout;
import com.lxj.xpopup.XPopup;
import com.wwsl.mdsj.AppConfig;
import com.wwsl.mdsj.R;
import com.wwsl.mdsj.activity.MainActivity;
import com.wwsl.mdsj.activity.common.WebViewActivity;
import com.wwsl.mdsj.activity.live.ShopWindowActivity;
import com.wwsl.mdsj.activity.me.user.UserCenterActivity;
import com.wwsl.mdsj.adapter.ViewPagerAdapter;
import com.wwsl.mdsj.bean.ConfigBean;
import com.wwsl.mdsj.bean.UserBean;
import com.wwsl.mdsj.dialog.SimpleCenterDialog;
import com.wwsl.mdsj.glide.ImgLoader;
import com.wwsl.mdsj.http.HttpCallback;
import com.wwsl.mdsj.http.HttpConst;
import com.wwsl.mdsj.http.HttpUtil;
import com.wwsl.mdsj.interfaces.CommonCallback;
import com.wwsl.mdsj.interfaces.LifeCycleListener;
import com.wwsl.mdsj.utils.CommonUtil;
import com.wwsl.mdsj.utils.DialogUtil;
import com.wwsl.mdsj.utils.IconUtil;
import com.wwsl.mdsj.utils.StringUtil;
import com.wwsl.mdsj.utils.ToastUtil;
import com.wwsl.mdsj.utils.tiktok.NumUtils;
import com.wwsl.mdsj.views.AbsMainViewHolder;
import com.wwsl.mdsj.views.CircleImageView;

import java.util.ArrayList;
import java.util.List;

import cn.hutool.core.util.StrUtil;

public class MainMeViewHolder extends AbsMainViewHolder implements View.OnClickListener {

    private final static String TAG = "MainMeViewHolder";

    private CircleImageView ivHead;
    private TextView tvTitle;
    private ImageView ivSetting;
    private ImageView ivMedal;
    private ImageView ivAixin;
    private ImageView ivVip;
    private ImageView ivVoice;
    private Toolbar toolbar;
    private SlidingTabLayout tabLayout;
    private AppBarLayout appBarLayout;
    private ViewPager viewPager;
    private TextView tvNickname;
    private TextView tvSign;
    private TextView tvSignEdit;
    private TextView tvGetLikeCount;
    private TextView tvFocusCount;
    private TextView tvFansCount;
    private TextView tvFriendCount;
    private TextView tvUid;
    private TextView tvAge;
    private TextView tvCity;
    private TextView btnCopy;
    private TextView tvMyShopWindow;

    private UserBean curUserBean;

    private LinearLayout zanLayout;
    private LinearLayout followLayout;
    private LinearLayout fansLayout;
    private LinearLayout friendLayout;
    private ArrayList<WorkViewHolder> viewList;

    public MainMeViewHolder(Context context, ViewGroup parentView, Activity activity) {
        super(context, parentView, activity);
    }

    private CommonCallback<UserBean> mCallback;

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_main_me;
    }

    @Override
    public void init() {
        findView();
        toolbar.setContentInsetsAbsolute(0, 0);//解决toolbar左边距问题
        setAppbarLayoutPercent();
        initTabLayout();
        initListener();
    }

    private void initListener() {
        mCallback = new CommonCallback<UserBean>() {
            @Override
            public void callback(UserBean bean) {
                ((MainActivity) mContext).runOnUiThread(() -> {
                    if (bean != null) {
                        curUserBean = bean;
                        showData();
                        updateProduct(false);
                    }
                });
            }
        };

        ivVip.setOnClickListener(this);
        ivVoice.setOnClickListener(this);
        ivHead.setOnClickListener(this);
        zanLayout.setOnClickListener(this);
        followLayout.setOnClickListener(this);
        fansLayout.setOnClickListener(this);
        friendLayout.setOnClickListener(this);
        tvSign.setOnClickListener(this);
        tvSignEdit.setOnClickListener(this);
        ivSetting.setOnClickListener(this);
        ivMedal.setOnClickListener(this);
        ivAixin.setOnClickListener(this);
        tvMyShopWindow.setOnClickListener(this);
        btnCopy.setOnClickListener(this);
        tvUid.setOnClickListener(this);

        ConfigBean config = AppConfig.getInstance().getConfig();
        if (config != null) {
            String nullStr = StringUtil.checkNullStr(config.getUserCenterShow());
            ivSetting.setVisibility("1".equals(nullStr) ? View.VISIBLE : View.GONE);
        } else {
            ivSetting.setVisibility(View.GONE);
        }

    }

    @Override
    public void onResume() {
        loadData();
    }


    @Override
    public void onDestroy() {
        HttpUtil.cancel(HttpConst.GET_BASE_INFO);
    }

    /**
     * 根据滚动比例渐变view
     */
    private void setAppbarLayoutPercent() {
        appBarLayout.addOnOffsetChangedListener((appBarLayout, verticalOffset) -> {
            float percent = (Math.abs(verticalOffset * 1.0f) / appBarLayout.getTotalScrollRange());  //滑动比例

            if (percent > 0.6) {
                tvTitle.setVisibility(View.VISIBLE);
                float alpha = 1 - (1 - percent) * 7;  //渐变变换
                toolbar.setVisibility(View.VISIBLE);
                toolbar.setAlpha(alpha);
                tvTitle.setAlpha(alpha);
            } else {
                tvTitle.setVisibility(View.GONE);
                toolbar.setVisibility(View.GONE);
            }
        });

    }


    private void initTabLayout() {

        String[] titles = new String[2];
        titles[0] = "0 作品";
        titles[1] = "0 喜欢";

        List<View> views = new ArrayList<>(2);
        viewList = new ArrayList<>();
        viewList.add(new WorkViewHolder(mContext, viewPager, "1"));
        viewList.add(new WorkViewHolder(mContext, viewPager, "3"));
        views.add(viewList.get(0).getContentView());
        views.add(viewList.get(1).getContentView());
        ViewPagerAdapter viewPagerAdapter = new ViewPagerAdapter(views);
        viewPager.setOffscreenPageLimit(2);
        viewPager.setAdapter(viewPagerAdapter);
        tabLayout.setViewPager(viewPager, titles);
    }


    private void findView() {
        ivHead = (CircleImageView) findViewById(R.id.iv_head);
        ivMedal = (ImageView) findViewById(R.id.iv_medal);
        ivAixin = (ImageView) findViewById(R.id.iv_aixin);
        ivVip = (ImageView) findViewById(R.id.iv_vip);
        ivVoice = (ImageView) findViewById(R.id.ivVoice);
        tvTitle = (TextView) findViewById(R.id.tv_title);
        btnCopy = (TextView) findViewById(R.id.btnCopy);
        tvMyShopWindow = (TextView) findViewById(R.id.tvMyShopWindow);
        ivSetting = (ImageView) findViewById(R.id.ivSetting);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        tabLayout = (SlidingTabLayout) findViewById(R.id.tablayout);
        appBarLayout = (AppBarLayout) findViewById(R.id.appbarlayout);
        viewPager = (ViewPager) findViewById(R.id.viewpager);
        tvNickname = (TextView) findViewById(R.id.tv_nickname);
        tvSign = (TextView) findViewById(R.id.tv_sign);
        tvSignEdit = (TextView) findViewById(R.id.tvSignEdit);
        tvGetLikeCount = (TextView) findViewById(R.id.txMainLike);
        tvFocusCount = (TextView) findViewById(R.id.txFollow);
        tvFansCount = (TextView) findViewById(R.id.txFans);
        tvFriendCount = (TextView) findViewById(R.id.txFriend);
        tvUid = (TextView) findViewById(R.id.tvUid);
        tvAge = (TextView) findViewById(R.id.txMainAge);
        tvCity = (TextView) findViewById(R.id.txMainCity);
        zanLayout = (LinearLayout) findViewById(R.id.zanLayout);
        followLayout = (LinearLayout) findViewById(R.id.followLayout);
        fansLayout = (LinearLayout) findViewById(R.id.fansLayout);
        friendLayout = (LinearLayout) findViewById(R.id.friendLayout);
    }

    private void updateProduct(boolean isNewData) {
        List<String> titles = new ArrayList<>();
        titles.add(NumUtils.numberFilter2(curUserBean.getVideoNum()) + " 作品");
        titles.add(NumUtils.numberFilter2(curUserBean.getLikeVideoNum()) + "喜欢");

        for (int i = 0; i < 2; i++) {
            tabLayout.getTitleView(i).setText(titles.get(i));
            if (isNewData) {
                viewList.get(i).setNewData(curUserBean.getId());
            } else {
                viewList.get(i).updateDate();
            }
        }

    }

    @Override
    public List<LifeCycleListener> getLifeCycleListenerList() {
        List<LifeCycleListener> list = new ArrayList<>();
        if (mLifeCycleListener != null) {
            list.add(mLifeCycleListener);
        }
        return list;
    }

    @Override
    public void loadData() {
        if (isFirstLoadData()) {
            HttpUtil.getMDBaseInfo();
            AppConfig appConfig = AppConfig.getInstance();
            UserBean u = appConfig.getUserBean();
            if (u != null) {
                curUserBean = u;
                showData();
                updateProduct(true);
            }
        } else {
            HttpUtil.getBaseInfo(mCallback);
        }
    }

    private void showData() {
        ImgLoader.display(curUserBean.getAvatar(), R.mipmap.icon_avatar_placeholder, ivHead);
        tvUid.setText(String.format("ID:%s", curUserBean.getId()));
        tvNickname.setText(curUserBean.getUsername());
        tvTitle.setText(curUserBean.getUsername());
        tvSign.setText(curUserBean.getSignature());
        String age = curUserBean.getAge();
        if (StringUtil.isInteger(age)) {
            tvAge.setText(String.format("%s岁", age));
        } else {
            tvAge.setText(age);
        }
        tvAge.postInvalidate();
        if (StrUtil.isEmpty(curUserBean.getCity())) {
            tvCity.setVisibility(View.GONE);
        } else {
            tvCity.setText(curUserBean.getCity());
            tvCity.setVisibility(View.VISIBLE);
        }

        if (curUserBean.getSex() != 0) {
            Drawable drawable = mContext.getDrawable(IconUtil.getSexIcon(curUserBean.getSex()));
            tvAge.setCompoundDrawablesWithIntrinsicBounds(drawable, null, null, null);
            tvAge.setCompoundDrawablePadding(5);
        } else {
            tvAge.setCompoundDrawablesWithIntrinsicBounds(null, null, null, null);
        }

        String partnerId = curUserBean.getPartnerId();

        ivMedal.setBackgroundResource((StrUtil.isEmpty(partnerId) || "0".equals(partnerId)) ? R.mipmap.icon_medal_grey : R.mipmap.icon_medal);

        int res = curUserBean.getIsVip() > 0 ? R.mipmap.icon_vip_enable : R.mipmap.icon_vip_disable;
        ivVip.setBackgroundResource(res);
        int resVoice = curUserBean.getAuth() == 1 ? R.mipmap.icon_voice_enable : R.mipmap.icon_voice_disable;
        ivVoice.setBackgroundResource(resVoice);

        //由配置设置
        ivAixin.setVisibility(View.GONE);
        ConfigBean config = AppConfig.getInstance().getConfig();
        if (config != null && config.getIsOpenZnImg() == 1) {
            UserBean userBean = AppConfig.getInstance().getUserBean();
            if (userBean != null && userBean.getShowAx() == 1) {
                ivAixin.setVisibility(View.VISIBLE);
            }
        }

        //获赞 关注 粉丝
        tvGetLikeCount.setText(NumUtils.numberFilter2(curUserBean.getDzNum()));
        tvFocusCount.setText(NumUtils.numberFilter2(curUserBean.getFollows()));
        tvFansCount.setText(NumUtils.numberFilter2(curUserBean.getFans()));
        tvFriendCount.setText(NumUtils.numberFilter2(curUserBean.getFriendNum()));
        tvMyShopWindow.setVisibility(curUserBean.getIsHaveShowWindow() > 0 ? View.VISIBLE : View.GONE);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnCopy:
            case R.id.tvUid:
                CommonUtil.copyText(mContext, AppConfig.getInstance().getUid());
                break;
            case R.id.tvMyShopWindow:
                ShopWindowActivity.forward(mContext, 0);
                break;
            case R.id.zanLayout:
                showZanDialog();
                break;
            case R.id.followLayout:
                FollowActivity.forward(mContext, AppConfig.getInstance().getUid(), 0);
                break;
            case R.id.fansLayout:
                FollowActivity.forward(mContext, AppConfig.getInstance().getUid(), 2);
                break;
            case R.id.friendLayout:
                FollowActivity.forward(mContext, AppConfig.getInstance().getUid(), 1);
                break;
            case R.id.iv_head:
                showHeadBig();
                break;
            case R.id.tv_sign:
            case R.id.tvSignEdit:
                openSignDialog();
                break;
            case R.id.ivSetting:
                UserCenterActivity.forward(mContext);
                break;
            case R.id.iv_medal:
                MadelWallActivity.forward(mContext);
                break;
            case R.id.iv_aixin:
                WebViewActivity.forward3(mContext, "http://mdsj.wenzuxz.com/index.php?g=portal&m=page&a=index&id=74", 2);
                break;
            case R.id.iv_vip:
            case R.id.ivVoice:
                UserVipActivity.forward(mContext);
                break;
        }
    }

    private void showHeadBig() {
        new XPopup.Builder(mContext)
                .asImageViewer(ivHead, curUserBean.getAvatar(), new ImgLoader())
                .show();
    }


    private void openSignDialog() {
        new XPopup.Builder(mContext)
                //.dismissOnBackPressed(false)
                .autoOpenSoftInput(true)
                //.autoFocusEditText(false) //是否让弹窗内的EditText自动获取焦点，默认是true
                .isRequestFocus(false)
                //.moveUpToKeyboard(false)   //是否移动到软键盘上面，默认为true
                .asInputConfirm("我的签名", "请输入签名", curUserBean.getSignature(), "帅气的签名,更体现你的魅力哟~",
                        this::modifySlogan)
                .bindLayout(R.layout.dialog_center_input_confirm)
                .show();

    }

    private void modifySlogan(String text) {
        HttpUtil.updateUserData(HttpConst.USER_INFO_SIGNATURE, text, new HttpCallback() {
            @Override
            public void onSuccess(int code, String msg, String[] info) {
                if (code == HttpConst.SUCCESS) {
                    ToastUtil.show("修改成功");
                    tvSign.setText(text);
                }
            }
        });
    }

    private SimpleCenterDialog simpleCenterDialog;//点赞弹窗

    @SuppressLint("DefaultLocale")
    private void showZanDialog() {
        if (simpleCenterDialog == null) {
            simpleCenterDialog = DialogUtil.getSimpleCenterDialog(mContext, R.mipmap.icon_dz_bg, 0, 200, String.format("\"%s\"获得了%d个赞", curUserBean.getUsername(), curUserBean.getDzNum()));
        }
        simpleCenterDialog.show();
    }

}
