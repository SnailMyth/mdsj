package com.wwsl.mdsj.base;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

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
import com.scwang.smart.refresh.layout.api.RefreshLayout;
import com.wwsl.mdsj.R;
import com.wwsl.mdsj.utils.CommonUtil;
import com.wwsl.mdsj.utils.GlideEngine;

import java.util.List;

import butterknife.ButterKnife;


/**
 * @author :
 * @date : 2020/6/17 15:31
 * @description : BaseFragment
 */


public abstract class BaseFragment extends Fragment {
    protected LoadingPopupView loadingPopupView;
    protected View rootView;
    protected Context mContext;
    private boolean isOk = false; // 是否完成View初始化
    public boolean isFirst = true; //是否为第一次加载
    protected OnOpenAlbumResultListener openAlbumResultListener;
    protected boolean isNeedInitData = true;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(setLayoutId(), container, false);
        mContext = getContext();
        mPictureParameterStyle = CommonUtil.getDefaultStyle(getContext());
        mCropParameterStyle = CommonUtil.getDefaultCropStyle(getContext());
        ButterKnife.bind(this, rootView);
        init();
        isOk = true; // 完成initView后改变view的初始化状态为完成
        return rootView;
    }

    protected abstract int setLayoutId();

    protected abstract void init();

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    protected View findViewById(int res) {
        return rootView.findViewById(res);
    }


    @Override
    public void onResume() {
        super.onResume();
        initLoadData();
    }

    private void initLoadData() {
        if (isNeedInitData && isOk && isFirst) { // 加载数据时判断是否完成view的初始化，以及是不是第一次加载此数据
            initialData();
            isFirst = false; // 加载第一次数据后改变状态，后续不再重复加载
        }
    }

    protected abstract void initialData();


    @Override
    public void onPause() {
        super.onPause();
    }


    public boolean onBackPressed() {
        return false;
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
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            if (openAlbumResultListener != null) {
                openAlbumResultListener.onResult(requestCode, PictureSelector.obtainMultipleResult(data));
            }
        }
    }

    public void showLoadCancelable(boolean cancelable, String context) {
        loadingPopupView = new XPopup.Builder(getContext())
                .dismissOnTouchOutside(cancelable)
                .asLoading(context);
        loadingPopupView.show();
    }

    public void dismissLoad() {
        if (loadingPopupView != null) {
            loadingPopupView.dismiss();
        }
    }
}
