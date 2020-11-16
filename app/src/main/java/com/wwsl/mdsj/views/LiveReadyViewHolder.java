package com.wwsl.mdsj.views;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.frame.fire.util.LogUtils;
import com.wwsl.mdsj.AppConfig;
import com.wwsl.mdsj.Constants;
import com.wwsl.mdsj.R;
import com.wwsl.mdsj.activity.live.LiveActivity;
import com.wwsl.mdsj.activity.live.LiveAnchorActivity;
import com.wwsl.mdsj.activity.live.LiveChooseClassActivity;
import com.wwsl.mdsj.activity.live.ShopWindowActivity;
import com.wwsl.mdsj.adapter.LiveReadyShareAdapter;
import com.wwsl.mdsj.adapter.ShopWindowAdapter;
import com.wwsl.mdsj.bean.LiveClassBean;
import com.wwsl.mdsj.bean.LiveRoomTypeBean;
import com.wwsl.mdsj.bean.LiveShopWindowBean;
import com.wwsl.mdsj.bean.UserBean;
import com.wwsl.mdsj.dialog.LiveRoomTypeDialogFragment;
import com.wwsl.mdsj.dialog.LiveTimeDialogFragment;
import com.wwsl.mdsj.glide.ImgLoader;
import com.wwsl.mdsj.http.HttpCallback;
import com.wwsl.mdsj.http.HttpConst;
import com.wwsl.mdsj.http.HttpUtil;
import com.wwsl.mdsj.interfaces.ActivityResultCallback;
import com.wwsl.mdsj.interfaces.CommonCallback;
import com.wwsl.mdsj.interfaces.ImageResultCallback;
import com.wwsl.mdsj.interfaces.LifeCycleAdapter;
import com.wwsl.mdsj.utils.DialogUtil;
import com.wwsl.mdsj.utils.L;
import com.wwsl.mdsj.utils.ProcessImageUtil;
import com.wwsl.mdsj.utils.StringUtil;
import com.wwsl.mdsj.utils.ToastUtil;
import com.wwsl.mdsj.utils.WordUtil;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by cxf on 2018/10/7.
 * 开播前准备
 */

public class LiveReadyViewHolder extends AbsViewHolder implements View.OnClickListener {
    private final static String TAG = "LiveReadyViewHolder";
    private ImageView mAvatar;
    private ConstraintLayout addGoodsLayout;
    private TextView mCoverText;
    private EditText mEditTitle;
    private RecyclerView mLiveShareRecyclerView;
    private LiveReadyShareAdapter mLiveShareAdapter;
    private ProcessImageUtil mImageUtil;
    private File mAvatarFile;
    private TextView mLocation;
    private TextView mLiveClass;
    private TextView tvAddGood;
    private TextView tvRoomWish;
    private TextView mLiveTypeTextView;//房间类型TextView
    private int mLiveClassID = 1;//直播频道id
    private int mLiveRoomType;//房间类型
    private int mLiveType;//直播类型
    private int mLiveTypeVal;//房间密码，门票收费金额
    private int mLiveTimeCoin;//计时收费金额
    private ActivityResultCallback mActivityResultCallback;
    private CommonCallback<LiveRoomTypeBean> mLiveRoomTypeCallback;
    private CommonCallback<LiveClassBean> mLiveTypeCallback;
    private List<LiveShopWindowBean> goodsItems;

    public LiveReadyViewHolder(Context context, ViewGroup parentView) {
        super(context, parentView);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.view_live_ready;
    }

    @Override
    public void init() {
        mAvatar = (ImageView) findViewById(R.id.avatar);
        addGoodsLayout = (ConstraintLayout) findViewById(R.id.addGoodsLayout);
        mCoverText = (TextView) findViewById(R.id.cover_text);
        mEditTitle = (EditText) findViewById(R.id.edit_title);
        mLocation = (TextView) findViewById(R.id.location);
        mLocation.setOnClickListener(this);
        mLiveClass = (TextView) findViewById(R.id.live_class);
        tvAddGood = (TextView) findViewById(R.id.tvAddGood);
        tvRoomWish = (TextView) findViewById(R.id.btn_room_wish);
        mLiveTypeTextView = (TextView) findViewById(R.id.btn_room_type);
        mLiveShareRecyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        goodsItems = new ArrayList<>();
        if (AppConfig.showWishBill()) {
            tvRoomWish.setVisibility(View.VISIBLE);
            mLiveTypeTextView.setText(WordUtil.getString(R.string.wish_live_room_ready));
        }

        addGoodsLayout.setVisibility(AppConfig.getInstance().canAddGood() ? View.VISIBLE : View.GONE);

        mLiveShareRecyclerView.setHasFixedSize(true);
        mLiveShareRecyclerView.setLayoutManager(new LinearLayoutManager(mContext, LinearLayoutManager.HORIZONTAL, false));
        mLiveShareAdapter = new LiveReadyShareAdapter(mContext);
        mLiveShareRecyclerView.setAdapter(mLiveShareAdapter);
        mImageUtil = ((LiveActivity) mContext).getProcessImageUtil();
        UserBean bean = AppConfig.getInstance().getUserBean();
        if (bean != null) {
            mLocation.setText(bean.getCity());
            if (!TextUtils.isEmpty(bean.getLiveThumb())) {
                ImgLoader.display(bean.getLiveThumb(), mAvatar);
                mCoverText.setText(WordUtil.getString(R.string.live_cover_2));
            }
        }
        mImageUtil.setImageResultCallback(new ImageResultCallback() {

            @Override
            public void beforeCamera() {
                ((LiveAnchorActivity) mContext).beforeCamera();
            }

            @Override
            public void onSuccess(File file) {
                if (file != null) {
                    ImgLoader.display(file, mAvatar);
                    if (mAvatarFile == null) {
                        mCoverText.setText(WordUtil.getString(R.string.live_cover_2));
                    }
                    mAvatarFile = file;
                }
            }

            @Override
            public void onFailure() {
            }
        });
        mLiveClass.setOnClickListener(this);
        tvAddGood.setOnClickListener(this);
        findViewById(R.id.avatar_group).setOnClickListener(this);
        findViewById(R.id.btn_camera).setOnClickListener(this);
        findViewById(R.id.btn_close).setOnClickListener(this);
        findViewById(R.id.btn_beauty).setOnClickListener(this);
        findViewById(R.id.btn_start_live).setOnClickListener(this);
        tvRoomWish.setOnClickListener(this);
        mLiveTypeTextView.setOnClickListener(this);
        mActivityResultCallback = new ActivityResultCallback() {
            @Override
            public void onSuccess(Intent intent) {
                mLiveClassID = intent.getIntExtra(Constants.CLASS_ID, 0);
                mLiveClass.setText(intent.getStringExtra(Constants.CLASS_NAME));
            }
        };
        mLiveRoomTypeCallback = new CommonCallback<LiveRoomTypeBean>() {
            @Override
            public void callback(LiveRoomTypeBean bean) {
                switch (bean.getId()) {
                    case Constants.LIVE_TYPE_NORMAL:
                        onLiveTypeNormal(bean);
                        break;
                    case Constants.LIVE_TYPE_PWD:
                        onLiveTypePwd(bean);
                        break;
                    case Constants.LIVE_TYPE_PAY:
                        onLiveTypePay(bean);
                        break;
                    case Constants.LIVE_TYPE_TIME:
                        onLiveTypeTime(bean);
                        break;
                }
            }
        };

        mLiveTypeCallback = new CommonCallback<LiveClassBean>() {
            @Override
            public void callback(LiveClassBean bean) {
                mLiveType = bean.getId();
            }
        };

        mLifeCycleListener = new LifeCycleAdapter() {
            @Override
            public void onDestroy() {
                HttpUtil.cancel(HttpConst.CREATE_ROOM);
            }
        };
    }

    @Override
    public void onClick(View v) {
        if (!canClick()) {
            return;
        }
        switch (v.getId()) {
            case R.id.avatar_group:
                setAvatar();
                break;
            case R.id.btn_camera:
                toggleCamera();
                break;
            case R.id.btn_close:
                close();
                break;
            case R.id.live_class:
                chooseLiveClass();
                break;
            case R.id.tvAddGood:
                openLiveAddGoods();
                break;
            case R.id.btn_beauty:
                beauty();
                break;
            case R.id.btn_room_type:
                chooseRoomType();
                break;
            case R.id.btn_start_live:
                startLive();
                break;
            case R.id.btn_room_wish:
                ((LiveAnchorActivity) mContext).openWishBillAddWindow();
                break;
            case R.id.location:
                setCity();
                break;
        }
    }

    private void openLiveAddGoods() {
        ShopWindowActivity.forward((Activity) mContext, ShopWindowAdapter.TYPE_LIVE, ShopWindowActivity.INTENT_LIVE);
    }


    /**
     * 设置城市
     */
    private void setCity() {
        DialogUtil.showSimpleInputDialog(mContext, WordUtil.getString(R.string.edit_profile_city), mLocation.getText().toString(), "", DialogUtil.INPUT_TYPE_TEXT, 8, mContext.getResources().getColor(R.color.orange_lite), new DialogUtil.SimpleCallback() {
            @Override
            public void onConfirmClick(Dialog dialog, String content) {
                if (TextUtils.isEmpty(content)) {
                    mLocation.setText(WordUtil.getString(R.string.city_none));
                } else {
                    mLocation.setText(content);
                }
                dialog.dismiss();
            }
        });
    }

    /**
     * 设置头像
     */
    private void setAvatar() {
        DialogUtil.showStringArrayDialog(mContext, new Integer[]{
                R.string.camera, R.string.alumb}, new DialogUtil.StringArrayDialogCallback() {
            @Override
            public void onItemClick(String text, int tag) {
                if (tag == R.string.camera) {
                    mImageUtil.getImageByCamera();
                } else {
                    mImageUtil.getImageByAlumb();
                }
            }
        });
    }

    /**
     * 切换镜头
     */
    private void toggleCamera() {
        ((LiveAnchorActivity) mContext).toggleCamera();
    }

    /**
     * 关闭
     */
    private void close() {
        ((LiveAnchorActivity) mContext).onBackPressed();
    }

    /**
     * 选择直播频道
     */
    private void chooseLiveClass() {
        Intent intent = new Intent(mContext, LiveChooseClassActivity.class);
        intent.putExtra(Constants.CLASS_ID, mLiveClassID);
        mImageUtil.startActivityForResult(intent, mActivityResultCallback);
    }

    /**
     * 设置美颜
     */
    private void beauty() {
        ((LiveAnchorActivity) mContext).beauty();
    }

    /**
     * 选择直播类型
     */
    private void chooseRoomType() {
        Bundle bundle = new Bundle();
        bundle.putInt(Constants.CHECKED_ID, mLiveRoomType);
        LiveRoomTypeDialogFragment fragment = new LiveRoomTypeDialogFragment();
        fragment.setArguments(bundle);
        fragment.setCallback(mLiveRoomTypeCallback);
        fragment.show(((LiveAnchorActivity) mContext).getSupportFragmentManager(), "LiveRoomTypeDialogFragment");
    }

    /**
     * 普通房间
     */
    private void onLiveTypeNormal(LiveRoomTypeBean bean) {
        mLiveRoomType = bean.getId();
        mLiveTypeTextView.setText(bean.getName());
        mLiveTypeVal = 0;
        mLiveTimeCoin = 0;
    }

    /**
     * 密码房间
     */
    private void onLiveTypePwd(final LiveRoomTypeBean bean) {
        DialogUtil.showSimpleInputDialog(mContext, WordUtil.getString(R.string.live_set_pwd), DialogUtil.INPUT_TYPE_NUMBER_PASSWORD, 8, mContext.getResources().getColor(R.color.color_dialog_cancel), new DialogUtil.SimpleCallback() {
            @Override
            public void onConfirmClick(Dialog dialog, String content) {
                if (TextUtils.isEmpty(content)) {
                    ToastUtil.show(R.string.live_set_pwd_empty);
                } else {
                    mLiveRoomType = bean.getId();
                    mLiveTypeTextView.setText(bean.getName());
                    if (StringUtil.isInt(content)) {
                        mLiveTypeVal = Integer.parseInt(content);
                    }
                    mLiveTimeCoin = 0;
                    dialog.dismiss();
                }
            }
        });
    }

    /**
     * 付费房间
     */
    private void onLiveTypePay(final LiveRoomTypeBean bean) {
        DialogUtil.showSimpleInputDialog(mContext, WordUtil.getString(R.string.live_set_fee), DialogUtil.INPUT_TYPE_NUMBER, 8, mContext.getResources().getColor(R.color.color_dialog_cancel), new DialogUtil.SimpleCallback() {
            @Override
            public void onConfirmClick(Dialog dialog, String content) {
                if (TextUtils.isEmpty(content)) {
                    ToastUtil.show(R.string.live_set_fee_empty);
                } else {
                    mLiveRoomType = bean.getId();
                    mLiveTypeTextView.setText(bean.getName());
                    if (StringUtil.isInt(content)) {
                        mLiveTypeVal = Integer.parseInt(content);
                    }
                    mLiveTimeCoin = 0;
                    dialog.dismiss();
                }
            }
        });
    }

    /**
     * 计时房间
     */
    private void onLiveTypeTime(final LiveRoomTypeBean bean) {
        LiveTimeDialogFragment fragment = new LiveTimeDialogFragment();
        Bundle bundle = new Bundle();
        bundle.putInt(Constants.CHECKED_COIN, mLiveTimeCoin);
        fragment.setArguments(bundle);
        fragment.setCommonCallback(new CommonCallback<Integer>() {
            @Override
            public void callback(Integer coin) {
                mLiveRoomType = bean.getId();
                mLiveTypeTextView.setText(bean.getName());
                mLiveTypeVal = coin;
                mLiveTimeCoin = coin;
            }
        });
        fragment.show(((LiveAnchorActivity) mContext).getSupportFragmentManager(), "LiveTimeDialogFragment");
    }

    public void hide() {
        if (mContentView != null && mContentView.getVisibility() == View.VISIBLE) {
            mContentView.setVisibility(View.INVISIBLE);
        }
    }


    public void show() {
        if (mContentView != null && mContentView.getVisibility() != View.VISIBLE) {
            mContentView.setVisibility(View.VISIBLE);
        }
    }

    /**
     * 点击开始直播按钮
     */
    private void startLive() {
        boolean startPreview = ((LiveAnchorActivity) mContext).isStartPreview();
        if (!startPreview) {
            ToastUtil.show(R.string.please_wait);
            return;
        }

        if (mLiveShareAdapter != null) {
            String type = mLiveShareAdapter.getShareType();


            if (!TextUtils.isEmpty(type)) {
                ((LiveActivity) mContext).shareLive(type);
                createRoom();
            } else {
                createRoom();
            }
        } else {
            createRoom();
        }
    }

    /**
     * 请求创建直播间接口，开始直播
     */
    private void createRoom() {
        if (mLiveClassID == 0) {
            ToastUtil.show(R.string.live_choose_live_class);
            return;
        }


        String title = mEditTitle.getText().toString().trim();
        String goodIds = "0";
        if (goodsItems != null) {
            StringBuffer sb = new StringBuffer();
            for (int i = 0; i < goodsItems.size(); i++) {
                sb.append(goodsItems.get(i).getId()).append(",");
            }
            if (sb.length() != 0) {
                goodIds = sb.substring(0, sb.length() - 1);
            }
            LogUtils.e(TAG, "createRoom: goodIds-->" + goodIds);
        }

        HttpUtil.createRoom(title, mLiveClassID, mLiveRoomType, mLiveTypeVal, mAvatarFile, mLocation.getText().toString(), goodIds, new HttpCallback() {
            @Override
            public void onSuccess(int code, String msg, String[] info) {
                if (code == 0 && info.length > 0) {
                    L.e("开播", "createRoom------->" + info[0]);
                    ((LiveAnchorActivity) mContext).startLiveSuccess(info[0], mLiveRoomType, mLiveTypeVal);
                } else {
                    ToastUtil.show(msg);
                }
            }
        });
    }

    public void release() {
        mImageUtil = null;
        mActivityResultCallback = null;
        mLiveRoomTypeCallback = null;
    }

    /**
     * 门票房间类型
     */
    public void setLiveTypeAct() {
        mLiveTypeTextView.setVisibility(View.GONE);
        mLiveRoomType = 4;
        mLiveTypeVal = 0;
        mLiveTimeCoin = 0;
    }

    public void setGoodsItems(List<LiveShopWindowBean> goods) {
        this.goodsItems.clear();
        this.goodsItems.addAll(goods);
    }
}
