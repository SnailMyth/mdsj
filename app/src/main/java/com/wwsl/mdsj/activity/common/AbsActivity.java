package com.wwsl.mdsj.activity.common;

import android.app.Activity;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.luck.picture.lib.PictureSelector;
import com.luck.picture.lib.animators.AnimationType;
import com.luck.picture.lib.config.PictureConfig;
import com.luck.picture.lib.config.PictureMimeType;
import com.luck.picture.lib.entity.LocalMedia;
import com.luck.picture.lib.listener.OnResultCallbackListener;
import com.luck.picture.lib.style.PictureCropParameterStyle;
import com.luck.picture.lib.style.PictureParameterStyle;
import com.luck.picture.lib.style.PictureWindowAnimationStyle;
import com.luck.picture.lib.tools.SdkVersionUtils;
import com.lxj.xpopup.XPopup;
import com.lxj.xpopup.impl.LoadingPopupView;
import com.orhanobut.logger.Logger;
import com.umeng.analytics.MobclickAgent;
import com.wwsl.mdsj.R;
import com.wwsl.mdsj.adapter.GridImageAdapter;
import com.wwsl.mdsj.bean.VideoBean;
import com.wwsl.mdsj.interfaces.LifeCycleListener;
import com.wwsl.mdsj.utils.ClickUtil;
import com.wwsl.mdsj.utils.CommonUtil;
import com.wwsl.mdsj.utils.GlideEngine;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by cxf on 2017/8/3.
 */

public abstract class AbsActivity extends AppCompatActivity {

    protected String mTag;
    protected Context mContext;
    protected Activity mActivity;
    protected List<LifeCycleListener> mLifeCycleListeners;
    protected LoadingPopupView loadingPopup;
    protected LoadingPopupView loadingPopupView;
    private PictureParameterStyle mPictureParameterStyle;

    private PictureCropParameterStyle mCropParameterStyle;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mTag = this.getClass().getSimpleName();
        setStatusBar();
        setContentView(getLayoutId());
        mContext = this;
        mActivity = this;
        ActivityManager.getInstance().addActivity(this);
        mLifeCycleListeners = new ArrayList<>();
        mPictureParameterStyle = CommonUtil.getDefaultStyle(this);
        mCropParameterStyle = CommonUtil.getDefaultCropStyle(this);
        main(savedInstanceState);
        if (mLifeCycleListeners != null) {
            for (LifeCycleListener listener : mLifeCycleListeners) {
                listener.onCreate();
            }
        }
    }

    protected abstract int getLayoutId();

    protected void main(Bundle savedInstanceState) {
        main();
    }

    protected void main() {

    }

    protected boolean isStatusBarWhite() {
        return false;
    }

    protected void setTitle(String title) {
        TextView titleView = (TextView) findViewById(R.id.titleView);
        if (titleView != null) {
            titleView.setText(title);
        }
    }


    public void backClick(View v) {
        if (v.getId() == R.id.btn_back) {
            onBackPressed();
        }
    }

    protected boolean canClick() {
        return ClickUtil.canClick();
    }


    /**
     * 设置透明状态栏
     */
    private void setStatusBar() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(Color.TRANSPARENT);
            if (isStatusBarWhite()) {
                window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
            } else {
                window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
            }
            window.setNavigationBarColor(Color.parseColor("#000000"));
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            Window window = getWindow();
            window.setFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS,
                    WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        }
    }

    protected void process() {
        // 华为,OPPO机型在StatusBarUtil.setLightStatusBar后布局被顶到状态栏上去了
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            View content = ((ViewGroup) findViewById(android.R.id.content)).getChildAt(0);
            if (content != null) {
                content.setFitsSystemWindows(true);
            }
        }
    }


    @Override
    protected void onDestroy() {
        if (mLifeCycleListeners != null) {
            for (LifeCycleListener listener : mLifeCycleListeners) {
                listener.onDestroy();
            }
            mLifeCycleListeners.clear();
            mLifeCycleListeners = null;
        }
        ActivityManager.getInstance().removeActivity(this);
        super.onDestroy();
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (mLifeCycleListeners != null) {
            for (LifeCycleListener listener : mLifeCycleListeners) {
                listener.onStart();
            }
        }
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        if (mLifeCycleListeners != null) {
            for (LifeCycleListener listener : mLifeCycleListeners) {
                listener.onReStart();
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        MobclickAgent.onResume(this);
//        if (mLifeCycleListeners != null) {
//            for (LifeCycleListener listener : mLifeCycleListeners) {
//                listener.onResume();
//            }
//        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        MobclickAgent.onPause(this);
//        if (mLifeCycleListeners != null) {
//            for (LifeCycleListener listener : mLifeCycleListeners) {
//                listener.onPause();
//            }
//        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (mLifeCycleListeners != null) {
            for (LifeCycleListener listener : mLifeCycleListeners) {
                listener.onStop();
            }
        }
    }

    public void addLifeCycleListener(LifeCycleListener listener) {
        if (mLifeCycleListeners != null && listener != null) {
            mLifeCycleListeners.add(listener);
        }
    }

    public void addAllLifeCycleListener(List<LifeCycleListener> listeners) {
        if (mLifeCycleListeners != null && listeners != null) {
            mLifeCycleListeners.addAll(listeners);
        }
    }

    public void removeLifeCycleListener(LifeCycleListener listener) {
        if (mLifeCycleListeners != null) {
            mLifeCycleListeners.remove(listener);
        }
    }

    /**
     * 显示“动态”中的 更多评论
     */
    public void showTrendCommentMore(VideoBean videoBean, int position) {

    }

    public void openAlbum(GridImageAdapter adapter) {

        PictureWindowAnimationStyle mWindowAnimationStyle = new PictureWindowAnimationStyle();
        mWindowAnimationStyle.ofAllAnimation(R.anim.picture_anim_up_in, R.anim.picture_anim_down_out);
        int animationMode = AnimationType.ALPHA_IN_ANIMATION;

        PictureSelector.create(this)
                .openGallery(PictureMimeType.ofAll())// 全部.PictureMimeType.ofAll()、图片.ofImage()、视频.ofVideo()、音频.ofAudio()
                .imageEngine(GlideEngine.createGlideEngine())// 外部传入图片加载引擎，必传项
                .theme(R.style.picture_WeChat_style)// 主题样式设置 具体参考 values/styles   用法：R.style.picture.white.style v2.3.3后 建议使用setPictureStyle()动态方式
                .isWeChatStyle(true)// 是否开启微信图片选择风格
                .isUseCustomCamera(false)// 是否使用自定义相机
                .isPageStrategy(true)// 是否开启分页策略 & 每页多少条；默认开启
                .setPictureStyle(mPictureParameterStyle)// 动态自定义相册主题
                .setPictureCropStyle(mCropParameterStyle)// 动态自定义裁剪主题
                .setPictureWindowAnimationStyle(mWindowAnimationStyle)// 自定义相册启动退出动画
                .setRecyclerAnimationMode(animationMode)// 列表动画效果
                .isWithVideoImage(true)// 图片和视频是否可以同选,只在ofAll模式下有效
                .isMaxSelectEnabledMask(true)// 选择数到了最大阀值列表是否启用蒙层效果
                .maxSelectNum(9)// 最大图片选择数量
                .minSelectNum(1)// 最小选择数量
                .maxVideoSelectNum(1) // 视频最大选择数量
                .imageSpanCount(4)// 每行显示个数
                .isReturnEmpty(false)// 未选择数据时点击按钮是否可以返回
                .closeAndroidQChangeWH(true)//如果图片有旋转角度则对换宽高,默认为true
                .closeAndroidQChangeVideoWH(!SdkVersionUtils.checkedAndroid_Q())// 如果视频有旋转角度则对换宽高,默认为false
                //.isAndroidQTransform(false)// 是否需要处理Android Q 拷贝至应用沙盒的操作，只针对compress(false); && .isEnableCrop(false);有效,默认处理
                .setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED)// 设置相册Activity方向，不设置默认使用系统
                .isOriginalImageControl(false)// 是否显示原图控制按钮，如果设置为true则用户可以自由选择是否使用原图，压缩、裁剪功能将会失效
                //.bindCustomPlayVideoCallback(new MyVideoSelectedPlayCallback(getContext()))// 自定义视频播放回调控制，用户可以使用自己的视频播放界面
                //.bindCustomPreviewCallback(new MyCustomPreviewInterfaceListener())// 自定义图片预览回调接口
                //.bindCustomCameraInterfaceListener(new MyCustomCameraInterfaceListener())// 提供给用户的一些额外的自定义操作回调
                //.cameraFileName(System.currentTimeMillis() +".jpg")    // 重命名拍照文件名、如果是相册拍照则内部会自动拼上当前时间戳防止重复，注意这个只在使用相机时可以使用，如果使用相机又开启了压缩或裁剪 需要配合压缩和裁剪文件名api
                //.renameCompressFile(System.currentTimeMillis() +".jpg")// 重命名压缩文件名、 如果是多张压缩则内部会自动拼上当前时间戳防止重复
                //.renameCropFileName(System.currentTimeMillis() + ".jpg")// 重命名裁剪文件名、 如果是多张裁剪则内部会自动拼上当前时间戳防止重复
                .selectionMode(PictureConfig.MULTIPLE)// 多选 or 单选
                .isPreviewImage(true)// 是否可预览图片
                .isPreviewVideo(true)// 是否可预览视频
                .isCamera(true)// 是否显示拍照按钮
                //.isMultipleSkipCrop(false)// 多图裁剪时是否支持跳过，默认支持
                //.isMultipleRecyclerAnimation(false)// 多图裁剪底部列表显示动画效果
                .isZoomAnim(true)// 图片列表点击 缩放效果 默认true
                //.imageFormat(PictureMimeType.PNG)// 拍照保存图片格式后缀,默认jpeg,Android Q使用PictureMimeType.PNG_Q
                .isEnableCrop(false)// 是否裁剪
                //.basicUCropConfig()//对外提供所有UCropOptions参数配制，但如果PictureSelector原本支持设置的还是会使用原有的设置
                .isCompress(true)// 是否压缩
                .compressQuality(80)// 图片压缩后输出质量 0~ 100
                .synOrAsy(true)//同步true或异步false 压缩 默认同步
                //.queryMaxFileSize(10)// 只查多少M以内的图片、视频、音频  单位M
                //.compressSavePath(getPath())//压缩图片保存地址
                //.sizeMultiplier(0.5f)// glide 加载图片大小 0~1之间 如设置 .glideOverride()无效 注：已废弃
                //.glideOverride(160, 160)// glide 加载宽高，越小图片列表越流畅，但会影响列表图片浏览的清晰度 注：已废弃
//                .withAspectRatio(aspect_ratio_x, aspect_ratio_y)// 裁剪比例 如16:9 3:2 3:4 1:1 可自定义
                .hideBottomControls(false)// 是否显示uCrop工具栏，默认不显示
                .isGif(true)// 是否显示gif图片
//                .freeStyleCropEnabled(cb_styleCrop.isChecked())// 裁剪框是否可拖拽
//                .circleDimmedLayer(cb_crop_circular.isChecked())// 是否圆形裁剪
                //.setCropDimmedColor(ContextCompat.getColor(getContext(), R.color.app_color_white))// 设置裁剪背景色值
                //.setCircleDimmedBorderColor(ContextCompat.getColor(getApplicationContext(), R.color.app_color_white))// 设置圆形裁剪边框色值
                //.setCircleStrokeWidth(3)// 设置圆形裁剪边框粗细
//                .showCropFrame(cb_showCropFrame.isChecked())// 是否显示裁剪矩形边框 圆形裁剪时建议设为false
//                .showCropGrid(cb_showCropGrid.isChecked())// 是否显示裁剪矩形网格 圆形裁剪时建议设为false
                .isOpenClickSound(true)// 是否开启点击声音
                .selectionData(adapter.getData())// 是否传入已选图片
                //.isDragFrame(false)// 是否可拖动裁剪框(固定)
                //.videoMinSecond(10)// 查询多少秒以内的视频
                //.videoMaxSecond(15)// 查询多少秒以内的视频
                //.recordVideoSecond(10)//录制视频秒数 默认60s
                //.isPreviewEggs(true)// 预览图片时 是否增强左右滑动图片体验(图片滑动一半即可看到上一张是否选中)
                //.cropCompressQuality(90)// 注：已废弃 改用cutOutQuality()
                .cutOutQuality(90)// 裁剪输出质量 默认100
                .minimumCompressSize(100)// 小于多少kb的图片不压缩
                //.cropWH()// 裁剪宽高比，设置如果大于图片本身宽高则无效
                //.cropImageWideHigh()// 裁剪宽高比，设置如果大于图片本身宽高则无效
                //.rotateEnabled(false) // 裁剪是否可旋转图片
                //.scaleEnabled(false)// 裁剪是否可放大缩小图片
                //.videoQuality()// 视频录制质量 0 or 1
                //.forResult(PictureConfig.CHOOSE_REQUEST);//结果回调onActivityResult code
                .forResult(new MyResultCallback(adapter));
    }

    /**
     * @param maxNum    最大选择数量
     * @param minDur    单位s
     * @param maxDur    单位s
     * @param selection 已经选择的视频
     */
    public void openVideo(int maxNum, int minDur, int maxDur, List<LocalMedia> selection, OnResultCallbackListener listener) {
        PictureWindowAnimationStyle mWindowAnimationStyle = new PictureWindowAnimationStyle();
        mWindowAnimationStyle.ofAllAnimation(R.anim.picture_anim_up_in, R.anim.picture_anim_down_out);
        int animationMode = AnimationType.ALPHA_IN_ANIMATION;

        PictureSelector.create(this)
                .openGallery(PictureMimeType.ofVideo())// 全部.PictureMimeType.ofAll()、图片.ofImage()、视频.ofVideo()、音频.ofAudio()
                .imageEngine(GlideEngine.createGlideEngine())// 外部传入图片加载引擎，必传项
                .theme(R.style.picture_WeChat_style)// 主题样式设置 具体参考 values/styles   用法：R.style.picture.white.style v2.3.3后 建议使用setPictureStyle()动态方式
                .isWeChatStyle(true)// 是否开启微信图片选择风格
                .isUseCustomCamera(false)// 是否使用自定义相机
                .isPageStrategy(true)// 是否开启分页策略 & 每页多少条；默认开启
                .setPictureStyle(mPictureParameterStyle)// 动态自定义相册主题
                .setPictureWindowAnimationStyle(mWindowAnimationStyle)// 自定义相册启动退出动画
                .setRecyclerAnimationMode(animationMode)// 列表动画效果
                .isWithVideoImage(true)// 图片和视频是否可以同选,只在ofAll模式下有效
                .isMaxSelectEnabledMask(true)// 选择数到了最大阀值列表是否启用蒙层效果
                .maxSelectNum(maxNum)// 最大图片选择数量
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
                .selectionData(selection)// 是否传入已选图片
                .videoMinSecond(minDur)
                .videoMaxSecond(maxDur)
                .forResult(listener);
    }

    public void openPicture(GridImageAdapter adapter) {

        PictureWindowAnimationStyle mWindowAnimationStyle = new PictureWindowAnimationStyle();
        mWindowAnimationStyle.ofAllAnimation(R.anim.picture_anim_up_in, R.anim.picture_anim_down_out);
        int animationMode = AnimationType.ALPHA_IN_ANIMATION;

        PictureSelector.create(this)
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
                .maxSelectNum(9)// 最大图片选择数量
                .minSelectNum(1)// 最小选择数量
                .maxVideoSelectNum(1) // 视频最大选择数量
                .imageSpanCount(4)// 每行显示个数
                .isReturnEmpty(false)// 未选择数据时点击按钮是否可以返回
                .closeAndroidQChangeWH(true)//如果图片有旋转角度则对换宽高,默认为true
                .closeAndroidQChangeVideoWH(!SdkVersionUtils.checkedAndroid_Q())// 如果视频有旋转角度则对换宽高,默认为false
                //.isAndroidQTransform(false)// 是否需要处理Android Q 拷贝至应用沙盒的操作，只针对compress(false); && .isEnableCrop(false);有效,默认处理
                .setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED)// 设置相册Activity方向，不设置默认使用系统
                .isOriginalImageControl(false)// 是否显示原图控制按钮，如果设置为true则用户可以自由选择是否使用原图，压缩、裁剪功能将会失效
                //.bindCustomPlayVideoCallback(new MyVideoSelectedPlayCallback(getContext()))// 自定义视频播放回调控制，用户可以使用自己的视频播放界面
                //.bindCustomPreviewCallback(new MyCustomPreviewInterfaceListener())// 自定义图片预览回调接口
                //.bindCustomCameraInterfaceListener(new MyCustomCameraInterfaceListener())// 提供给用户的一些额外的自定义操作回调
                //.cameraFileName(System.currentTimeMillis() +".jpg")    // 重命名拍照文件名、如果是相册拍照则内部会自动拼上当前时间戳防止重复，注意这个只在使用相机时可以使用，如果使用相机又开启了压缩或裁剪 需要配合压缩和裁剪文件名api
                //.renameCompressFile(System.currentTimeMillis() +".jpg")// 重命名压缩文件名、 如果是多张压缩则内部会自动拼上当前时间戳防止重复
                //.renameCropFileName(System.currentTimeMillis() + ".jpg")// 重命名裁剪文件名、 如果是多张裁剪则内部会自动拼上当前时间戳防止重复
                .selectionMode(PictureConfig.MULTIPLE)// 多选 or 单选
                .isPreviewImage(true)// 是否可预览图片
                .isPreviewVideo(true)// 是否可预览视频
                .isCamera(true)// 是否显示拍照按钮
                //.isMultipleSkipCrop(false)// 多图裁剪时是否支持跳过，默认支持
                //.isMultipleRecyclerAnimation(false)// 多图裁剪底部列表显示动画效果
                .isZoomAnim(true)// 图片列表点击 缩放效果 默认true
                //.imageFormat(PictureMimeType.PNG)// 拍照保存图片格式后缀,默认jpeg,Android Q使用PictureMimeType.PNG_Q
                .isEnableCrop(false)// 是否裁剪
                //.basicUCropConfig()//对外提供所有UCropOptions参数配制，但如果PictureSelector原本支持设置的还是会使用原有的设置
                .isCompress(true)// 是否压缩
                .compressQuality(80)// 图片压缩后输出质量 0~ 100
                .synOrAsy(true)//同步true或异步false 压缩 默认同步
                //.queryMaxFileSize(10)// 只查多少M以内的图片、视频、音频  单位M
                //.compressSavePath(getPath())//压缩图片保存地址
                //.sizeMultiplier(0.5f)// glide 加载图片大小 0~1之间 如设置 .glideOverride()无效 注：已废弃
                //.glideOverride(160, 160)// glide 加载宽高，越小图片列表越流畅，但会影响列表图片浏览的清晰度 注：已废弃
//                .withAspectRatio(aspect_ratio_x, aspect_ratio_y)// 裁剪比例 如16:9 3:2 3:4 1:1 可自定义
                .hideBottomControls(false)// 是否显示uCrop工具栏，默认不显示
                .isGif(true)// 是否显示gif图片
//                .freeStyleCropEnabled(cb_styleCrop.isChecked())// 裁剪框是否可拖拽
//                .circleDimmedLayer(cb_crop_circular.isChecked())// 是否圆形裁剪
                //.setCropDimmedColor(ContextCompat.getColor(getContext(), R.color.app_color_white))// 设置裁剪背景色值
                //.setCircleDimmedBorderColor(ContextCompat.getColor(getApplicationContext(), R.color.app_color_white))// 设置圆形裁剪边框色值
                //.setCircleStrokeWidth(3)// 设置圆形裁剪边框粗细
//                .showCropFrame(cb_showCropFrame.isChecked())// 是否显示裁剪矩形边框 圆形裁剪时建议设为false
//                .showCropGrid(cb_showCropGrid.isChecked())// 是否显示裁剪矩形网格 圆形裁剪时建议设为false
                .isOpenClickSound(true)// 是否开启点击声音
                .selectionData(adapter.getData())// 是否传入已选图片
                //.isDragFrame(false)// 是否可拖动裁剪框(固定)
                //.videoMinSecond(10)// 查询多少秒以内的视频
                //.videoMaxSecond(15)// 查询多少秒以内的视频
                //.recordVideoSecond(10)//录制视频秒数 默认60s
                //.isPreviewEggs(true)// 预览图片时 是否增强左右滑动图片体验(图片滑动一半即可看到上一张是否选中)
                //.cropCompressQuality(90)// 注：已废弃 改用cutOutQuality()
                .cutOutQuality(90)// 裁剪输出质量 默认100
                .minimumCompressSize(100)// 小于多少kb的图片不压缩
                //.cropWH()// 裁剪宽高比，设置如果大于图片本身宽高则无效
                //.cropImageWideHigh()// 裁剪宽高比，设置如果大于图片本身宽高则无效
                //.rotateEnabled(false) // 裁剪是否可旋转图片
                //.scaleEnabled(false)// 裁剪是否可放大缩小图片
                //.videoQuality()// 视频录制质量 0 or 1
                //.forResult(PictureConfig.CHOOSE_REQUEST);//结果回调onActivityResult code
                .forResult(new MyResultCallback(adapter));
    }

    private static class MyResultCallback implements OnResultCallbackListener<LocalMedia> {
        private WeakReference<GridImageAdapter> mAdapterWeakReference;

        public MyResultCallback(GridImageAdapter adapter) {
            super();
            this.mAdapterWeakReference = new WeakReference<>(adapter);
        }

        @Override
        public void onResult(List<LocalMedia> result) {
            for (LocalMedia media : result) {
                Logger.e("是否压缩:" + media.isCompressed()
                        + "压缩:" + media.getCompressPath()
                        + "原图:" + media.getPath()
                        + "是否裁剪:" + media.isCut()
                        + "裁剪:" + media.getCutPath()
                        + "是否开启原图:" + media.isOriginal()
                        + "原图路径:" + media.getOriginalPath()
                        + "Android Q 特有Path:" + media.getAndroidQToPath()
                        + "宽高: " + media.getWidth() + "x" + media.getHeight()
                        + "Size: " + media.getSize());
                // 可以通过PictureSelectorExternalUtils.getExifInterface();方法获取一些额外的资源信息，如旋转角度、经纬度等信息
            }

            if (mAdapterWeakReference.get() != null) {
                mAdapterWeakReference.get().setList(result);
                mAdapterWeakReference.get().notifyDataSetChanged();
            }

        }

        @Override
        public void onCancel() {
            Logger.e("PictureSelector Cancel");
        }
    }


    public void showLoad() {
        if (loadingPopup == null) {
            loadingPopup = new XPopup.Builder(this).asLoading("正在加载中...");
        }
        loadingPopup.show();
    }

    public void showLoadCancelable(boolean cancelable, String context) {
        loadingPopupView = new XPopup.Builder(this)
                .dismissOnTouchOutside(cancelable)
                .asLoading(context);
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


}
