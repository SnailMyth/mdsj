package com.wwsl.mdsj.base;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import androidx.appcompat.app.AppCompatActivity;

import com.gyf.immersionbar.BarHide;
import com.gyf.immersionbar.ImmersionBar;
import com.luck.picture.lib.PictureSelectionModel;
import com.luck.picture.lib.PictureSelector;
import com.luck.picture.lib.animators.AnimationType;
import com.luck.picture.lib.config.PictureConfig;
import com.luck.picture.lib.config.PictureMimeType;
import com.luck.picture.lib.entity.LocalMedia;
import com.luck.picture.lib.style.PictureCropParameterStyle;
import com.luck.picture.lib.style.PictureParameterStyle;
import com.luck.picture.lib.style.PictureWindowAnimationStyle;
import com.luck.picture.lib.tools.SdkVersionUtils;
import com.lxj.xpopup.XPopup;
import com.lxj.xpopup.impl.LoadingPopupView;
import com.umeng.analytics.MobclickAgent;
import com.wwsl.mdsj.R;
import com.wwsl.mdsj.activity.common.ActivityManager;
import com.wwsl.mdsj.utils.CommonUtil;
import com.wwsl.mdsj.utils.GlideEngine;

import java.util.List;

import butterknife.ButterKnife;
import butterknife.Unbinder;
import io.reactivex.disposables.CompositeDisposable;


/**
 * @author :
 * @date : 2020/6/17 15:32
 * @description : activity基类
 */
public abstract class BaseActivity extends AppCompatActivity {
    protected Unbinder unbinder;
    public final CompositeDisposable disposables = new CompositeDisposable();
    protected OnOpenAlbumResultListener openAlbumResultListener;
    protected LoadingPopupView loadingPopup;
    protected LoadingPopupView loadingPopupView;
    protected Activity mActivity;
    protected Context mContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(setLayoutId());
        mActivity = this;
        mContext = this;
        unbinder = ButterKnife.bind(this);
        mPictureParameterStyle = CommonUtil.getDefaultStyle(this);
        mCropParameterStyle = CommonUtil.getDefaultCropStyle(this);
        ActivityManager.getInstance().addActivity(this);
        setStatusBar();
        init();
    }

    protected abstract int setLayoutId();

    protected abstract void init();


    private void setStatusBar() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            if (isStatusBarWhite()) {
                window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
            } else {
                window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
            }
            window.setNavigationBarColor(Color.parseColor("#000000"));
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(0);
        }
    }

    protected boolean isStatusBarWhite() {
        return true;
    }

    /**
     * 设置状态栏颜色
     */
    protected void setSystemBarColor(int color) {
        ImmersionBar.with(this).statusBarColor(color);
    }

    /**
     * 去除状态栏
     */
    protected void hideStatusBar() {
        ImmersionBar.with(this).hideBar(BarHide.FLAG_HIDE_STATUS_BAR).init();
    }

    /**
     * 保持不息屏
     */
    protected void keepScreenOn() {
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
    }

    /**
     * Activity退出动画
     */
    protected void setExitAnimation(int animId) {
        overridePendingTransition(0, animId);
    }

    /**
     * 全屏
     */
    protected void setFullScreen() {
        ImmersionBar.with(this).init();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbinder.unbind();
        ActivityManager.getInstance().removeActivity(this);
        disposables.clear();
    }

    private PictureParameterStyle mPictureParameterStyle;

    private PictureCropParameterStyle mCropParameterStyle;

    public void openAlbum(int maxSize, List<LocalMedia> selectImageList, int requestCode) {

        // 进入相册 以下是例子：用不到的api可以不写
        PictureWindowAnimationStyle mWindowAnimationStyle = new PictureWindowAnimationStyle();
        mWindowAnimationStyle.ofAllAnimation(R.anim.picture_anim_up_in, R.anim.picture_anim_down_out);
        int animationMode = AnimationType.ALPHA_IN_ANIMATION;
        PictureSelectionModel pictureSelectionModel = PictureSelector.create(this)
                .openGallery(PictureMimeType.ofImage())// 全部.PictureMimeType.ofAll()、图片.ofImage()、视频.ofVideo()、音频.ofAudio()
                .imageEngine(GlideEngine.createGlideEngine())// 外部传入图片加载引擎，必传项
                .theme(R.style.picture_WeChat_style)// 主题样式设置 具体参考 values/styles   用法：R.style.picture.white.style v2.3.3后 建议使用setPictureStyle()动态方式
                .isWeChatStyle(true)// 是否开启微信图片选择风格
                .isUseCustomCamera(false)// 是否使用自定义相机
                .isPageStrategy(true)// 是否开启分页策略 & 每页多少条；默认开启
                .setPictureStyle(mPictureParameterStyle)// 动态自定义相册主题
                .setPictureCropStyle(mCropParameterStyle)// 动态自定义裁剪主题
                .setPictureWindowAnimationStyle(mWindowAnimationStyle)// 自定义相册启动退出动画
                .setRecyclerAnimationMode(animationMode)// 列表动画效果
                .isMaxSelectEnabledMask(true)// 选择数到了最大阀值列表是否启用蒙层效果
                .selectionData(selectImageList)
                .minSelectNum(1)// 最小选择数量
                .maxVideoSelectNum(1) // 视频最大选择数量
                .imageSpanCount(4)// 每行显示个数
                .isReturnEmpty(false)// 未选择数据时点击按钮是否可以返回
                .closeAndroidQChangeWH(true)//如果图片有旋转角度则对换宽高,默认为true
                .closeAndroidQChangeVideoWH(!SdkVersionUtils.checkedAndroid_Q())// 如果视频有旋转角度则对换宽高,默认为false
                .setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED)// 设置相册Activity方向，不设置默认使用系统
                .isOriginalImageControl(false)// 是否显示原图控制按钮，如果设置为true则用户可以自由选择是否使用原图，压缩、裁剪功能将会失效
                .selectionMode(PictureConfig.MULTIPLE)// 多选 or 单选
                .isPreviewImage(true)// 是否可预览图片
                .isPreviewVideo(true)// 是否可预览视频
                .isCamera(true)// 是否显示拍照按钮
                .isZoomAnim(true)// 图片列表点击 缩放效果 默认true
                .isEnableCrop(false)// 是否裁剪
                .isCompress(true)// 是否压缩
                .compressQuality(80)// 图片压缩后输出质量 0~ 100
                .synOrAsy(true)//同步true或异步false 压缩 默认同步
                .hideBottomControls(false)// 是否显示uCrop工具栏，默认不显示
                .isGif(true)// 是否显示gif图片
                .isOpenClickSound(true)// 是否开启点击声音
                .cutOutQuality(90)// 裁剪输出质量 默认100
                .minimumCompressSize(100);// 小于多少kb的图片不压缩

        if (maxSize > 0) {
            pictureSelectionModel.maxSelectNum(maxSize);
        }
        pictureSelectionModel.forResult(requestCode);//结果回调onActivityResult code
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (openAlbumResultListener != null) {
                openAlbumResultListener.onResult(requestCode, PictureSelector.obtainMultipleResult(data));
            }
        }
    }

    public void showLoad() {
        if (loadingPopup == null) {
            loadingPopup = new XPopup.Builder(this)
                    .dismissOnTouchOutside(false)
                    .asLoading("正在加载中...");
        }
        loadingPopup.show();
    }


    public void showLoadCancelable(boolean cancelable, String context) {
        if (loadingPopupView == null) {
            loadingPopupView = new XPopup.Builder(this)
                    .dismissOnTouchOutside(cancelable)
                    .asLoading(context);
        } else {
            loadingPopupView.setTitle(context);
        }

        loadingPopupView.show();
    }

    public void dismissLoad() {
        if (loadingPopup != null) {
            loadingPopup.dismiss();
        }
        if (loadingPopupView != null) {
            loadingPopupView.dismiss();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        MobclickAgent.onResume(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        MobclickAgent.onPause(this);
    }
}
