package com.wwsl.mdsj.activity.video;

import android.animation.ValueAnimator;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

import androidx.core.content.ContextCompat;

import android.text.TextUtils;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.tencent.rtmp.TXLiveConstants;
import com.tencent.rtmp.ui.TXCloudVideoView;
import com.tencent.ugc.TXRecordCommon;
import com.tencent.ugc.TXUGCPartsManager;
import com.tencent.ugc.TXUGCRecord;
import com.tencent.ugc.TXVideoEditConstants;
import com.tencent.ugc.TXVideoEditer;
import com.tencent.ugc.TXVideoInfoReader;
import com.wwsl.beauty.bean.FilterBean;
import com.wwsl.mdsj.AppConfig;
import com.wwsl.mdsj.AppContext;
import com.wwsl.mdsj.BuildConfig;
import com.wwsl.mdsj.Constants;
import com.wwsl.mdsj.R;
import com.wwsl.mdsj.activity.common.AbsActivity;
import com.wwsl.mdsj.activity.common.PictureChooseActivity;
import com.wwsl.mdsj.bean.ConfigBean;
import com.wwsl.mdsj.bean.MusicBean;
import com.wwsl.mdsj.bean.PictureChooseBean;
import com.wwsl.mdsj.bean.net.VideoMusicBean;
import com.wwsl.mdsj.beauty.BeautyViewHolder;
import com.wwsl.mdsj.beauty.DefaultBeautyViewHolder;
import com.wwsl.mdsj.beauty.DefaultEffectListener;
import com.wwsl.mdsj.beauty.LiveBeautyViewHolder;
import com.wwsl.mdsj.beauty.TiBeautyEffectListener;
import com.wwsl.mdsj.custom.VideoRecordBtnView;
import com.wwsl.mdsj.utils.BitmapUtil;
import com.wwsl.mdsj.utils.DialogUtil;
import com.wwsl.mdsj.utils.L;
import com.wwsl.mdsj.utils.PictureEditUtil;
import com.wwsl.mdsj.utils.ToastUtil;
import com.wwsl.mdsj.utils.VideoEditUtil;
import com.wwsl.mdsj.utils.WordUtil;
import com.wwsl.mdsj.views.Music;
import com.wwsl.mdsj.views.VideoMusicViewHolder;
import com.wwsl.mdsj.views.VideoProcessViewHolder;
import com.wwsl.video.custom.RecordProgressView;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

import cn.tillusory.sdk.TiSDKManager;
import cn.tillusory.sdk.TiSDKManagerBuilder;
import cn.tillusory.sdk.bean.TiDistortionEnum;
import cn.tillusory.sdk.bean.TiFilterEnum;
import cn.tillusory.sdk.bean.TiRockEnum;
import cn.tillusory.sdk.bean.TiRotation;

/**
 * Created by cxf on 2018/12/5.
 * 视频录制
 */

public class VideoRecordActivity extends AbsActivity implements
        TXRecordCommon.ITXVideoRecordListener, //视频录制进度回调
        VideoProcessViewHolder.ActionListener,//预处理控件点击取消回调
        TXVideoEditer.TXVideoProcessListener, //视频编辑前预处理进度回调
        TXVideoEditer.TXThumbnailListener, //视频编辑前预处理中生成每一帧缩略图回调
        TiBeautyEffectListener, //设置美颜数值回调
        TXUGCRecord.VideoCustomProcessListener //视频录制中自定义预处理（添加美颜回调）
{

    public static final String TAKE_PICTURE_ONLY = "TAKE_PICTURE_ONLY";
    private static final int TYPE_PICTURE = 0;
    private static final int TYPE_VIDEO = 1;
    public static final int TYPE_PICTURE_ONLY = 2;
    private boolean mDeleteShow;
    private boolean mNextShow;

    private static final String TAG = "VideoRecordActivity";
    private static final int MIN_DURATION = 5000;//最小录制时间5s
    private static final int MAX_DURATION = 30000;//最大录制时间30s
    //按钮动画相关
    private VideoRecordBtnView mVideoRecordBtnView;
    private View mRecordView;
    private ValueAnimator mRecordBtnAnimator;//录制开始后，录制按钮动画
    private Drawable mRecordDrawable;
    private Drawable mUnRecordDrawable;

    /****************************/
    private boolean mRecordStarted;//是否开始了录制（true 已开始 false 未开始）
    private boolean mRecordStoped;//是否结束了录制
    private boolean mRecording;//是否在录制中（（true 录制中 false 暂停中）
    private ViewGroup mRoot;
    private TXCloudVideoView mVideoView;//预览控件
    private RecordProgressView mRecordProgressView;//录制进度条
    private TextView mTime;//录制时间
    private TextView mBtnFlash;//闪光灯按钮
    private TXUGCRecord mRecorder;//录制器
    private TXRecordCommon.TXUGCCustomConfig mCustomConfig;//录制相关配置
    private TXRecordCommon.TXUGCSimpleConfig simpleParam;
    //录制相关配置
    private boolean mFrontCamera = true;//是否是前置摄像头
    private String mVideoPath;//视频的保存路径
    private boolean mFromRecord;//视频来源是否来自录制
    private int mRecordSpeed;//录制速度
    private View mGroup1;
    private View mGroup2;
    private View mGroup3;
    private View mGroup4;
    private View mBtnNext;//录制完成，下一步
    private Dialog mStopRecordDialog;//停止录制的时候的dialog
    private boolean mIsReachMaxRecordDuration;//是否达到最大录制时间
    private VideoProcessViewHolder mVideoProcessViewHolder;
    private long mDuration;//录制视频的长度
    private TXVideoEditer mVideoEditer;
    private boolean mProcessStarted;//是否开始了预处理
    private int mProcessProgress;//预处理进度
    private MyHandler mHandler;
    private BeautyViewHolder mBeautyViewHolder;
    private TiSDKManager mTiSDKManager;//萌颜的各种工具
    private VideoMusicViewHolder mVideoMusicViewHolder;
    private MusicBean mMusicBean;//背景音乐
    private boolean mBgmPlayStarted;//是否已经开始播放背景音乐了
    private Bitmap mFilterBitmap;//基础美颜的滤镜
    //萌颜美颜效果
    private int mMeibai = 0;//美白
    private int mMoPi = 0;//磨皮
    private int mBaoHe = 0;//饱和
    private int mFengNen = 0;//粉嫩
    private int mBigEye = 0;//大眼
    private int mFace = 0;//瘦脸

    private int mChin = 0;//下巴
    private int mForehead = 0;//额头
    private int mMouth = 0;//嘴型
    private String mTieZhi = "";//贴纸
    private TiFilterEnum mTiFilterEnum = TiFilterEnum.NO_FILTER;//滤镜
    private TiDistortionEnum mTiDistortionEnum = TiDistortionEnum.NO_DISTORTION;//哈哈镜
    private TiRockEnum mTiRockEnum = TiRockEnum.NO_ROCK;//抖动
    private boolean mFristInitBeauty = true;
    private long mRecordTime;
    private RelativeLayout layoutPicturePreview;
    private int type = TYPE_VIDEO;
    private static Bitmap mBitmap;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_video_record;
    }

    @Override
    protected void main() {

        VideoMusicBean tempMusicBean = getIntent().getParcelableExtra("music");
        if (tempMusicBean != null) {
            mMusicBean = MusicBean.parse(tempMusicBean);
        }

        layoutPicturePreview = findViewById(R.id.layoutPicturePreview);
        layoutPicturePreview.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return true;
            }
        });
        //按钮动画相关
        mVideoRecordBtnView = (VideoRecordBtnView) findViewById(R.id.record_btn_view);
        mRecordView = findViewById(R.id.record_view);
        mUnRecordDrawable = ContextCompat.getDrawable(mContext, R.drawable.bg_btn_record_1);
        mRecordDrawable = ContextCompat.getDrawable(mContext, R.drawable.bg_btn_record_2);
        mRecordBtnAnimator = ValueAnimator.ofFloat(100, 0);
        mRecordBtnAnimator.setDuration(500);
        mRecordBtnAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float v = (float) animation.getAnimatedValue();
                if (mVideoRecordBtnView != null) {
                    mVideoRecordBtnView.setRate((int) v);
                }
            }
        });
        mRecordBtnAnimator.setRepeatCount(-1);
        mRecordBtnAnimator.setRepeatMode(ValueAnimator.REVERSE);

        /****************************/
        mRoot = (ViewGroup) findViewById(R.id.root);
        mGroup1 = findViewById(R.id.group_1);
        mGroup2 = findViewById(R.id.group_2);
        mGroup3 = findViewById(R.id.group_3);
        mGroup4 = findViewById(R.id.group_4);
        mVideoView = (TXCloudVideoView) findViewById(R.id.video_view);
        mTime = findViewById(R.id.time);
        mRecordProgressView = (RecordProgressView) findViewById(R.id.record_progress_view);
        mRecordProgressView.setMaxDuration(MAX_DURATION);
        mRecordProgressView.setMinDuration(MIN_DURATION);
        mBtnFlash = findViewById(R.id.btn_flash);
        mBtnNext = findViewById(R.id.btn_next);

        initCameraRecord();
        ((RadioGroup) findViewById(R.id.rgType)).setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int checkedId) {
                switch (checkedId) {
                    case R.id.rbPicture://拍摄
                        type = TYPE_PICTURE;
                        mRecordProgressView.setVisibility(View.GONE);
                        mTime.setVisibility(View.GONE);
                        if (mGroup4.getVisibility() == View.VISIBLE) {
                            mDeleteShow = true;
                            mGroup4.setVisibility(View.INVISIBLE);
                        } else {
                            mDeleteShow = false;
                        }
                        if (mBtnNext.getVisibility() == View.VISIBLE) {
                            mNextShow = true;
                            mBtnNext.setVisibility(View.INVISIBLE);
                        } else {
                            mNextShow = false;
                        }
                        mGroup3.setVisibility(View.VISIBLE);
                        break;
                    case R.id.rbVideo://视频
                        type = TYPE_VIDEO;
                        mRecordProgressView.setVisibility(View.VISIBLE);
                        mTime.setVisibility(View.VISIBLE);
                        if (mDeleteShow) {
                            mGroup4.setVisibility(View.VISIBLE);
                            mGroup3.setVisibility(View.GONE);
                        } else {
                            mGroup3.setVisibility(View.VISIBLE);
                        }
                        if (mNextShow) {
                            mBtnNext.setVisibility(View.VISIBLE);
                        }
                        break;
                    default:
                        break;
                }
            }
        });

        if (AppConfig.hideTakePictureOnVideo()) {
            findViewById(R.id.rgType).setVisibility(View.GONE);
            ((RadioButton) findViewById(R.id.rbVideo)).setChecked(true);
        }
    }

    /**
     * 初始化录制器
     */
    private void initCameraRecord() {
        mRecorder = TXUGCRecord.getInstance(AppContext.sInstance);
        mRecorder.setHomeOrientation(TXLiveConstants.VIDEO_ANGLE_HOME_DOWN);
        mRecorder.setRenderRotation(TXLiveConstants.RENDER_ROTATION_PORTRAIT);
        mRecordSpeed = TXRecordCommon.RECORD_SPEED_NORMAL;
        mRecorder.setRecordSpeed(mRecordSpeed);
        mRecorder.setAspectRatio(TXRecordCommon.VIDEO_ASPECT_RATIO_9_16);
        mCustomConfig = new TXRecordCommon.TXUGCCustomConfig();
        mCustomConfig.videoResolution = TXRecordCommon.VIDEO_RESOLUTION_720_1280;
        mCustomConfig.minDuration = MIN_DURATION;
        mCustomConfig.maxDuration = MAX_DURATION;
        mCustomConfig.videoBitrate = 3600;
        mCustomConfig.videoGop = 3;
        mCustomConfig.videoFps = 20;
        mCustomConfig.isFront = mFrontCamera;

        simpleParam = new TXRecordCommon.TXUGCSimpleConfig();
        simpleParam.videoQuality = TXRecordCommon.VIDEO_QUALITY_HIGH;
        simpleParam.isFront = mFrontCamera;           // 是否使用前置摄像头
        simpleParam.minDuration = MIN_DURATION;    // 视频录制的最小时长 ms
        simpleParam.maxDuration = MAX_DURATION;
        simpleParam.touchFocus = false; // false 为自动聚焦；true 为手动聚焦
        mRecorder.setVideoRecordListener(this);
    }


    /**
     * 初始化萌颜
     */
    private void initBeauty() {
        try {
            mTiSDKManager = new TiSDKManagerBuilder().build();
            mTiSDKManager.setBeautyEnable(true);
            mTiSDKManager.setFaceTrimEnable(true);
            if (mFristInitBeauty) {
                mFristInitBeauty = false;
                ConfigBean configBean = AppConfig.getInstance().getConfig();
                if (configBean != null) {
                    mMeibai = configBean.getBeautyMeiBai();//美白
                    mMoPi = configBean.getBeautyMoPi();//磨皮
                    mBaoHe = configBean.getBeautyBaoHe();//饱和
                    mFengNen = configBean.getBeautyFenNen();//粉嫩
                    mBigEye = configBean.getBeautyBigEye();//大眼
                    mFace = configBean.getBeautyFace();//瘦脸
                }
            }
            mTiSDKManager.setSkinWhitening(mMeibai);//美白
            mTiSDKManager.setSkinBlemishRemoval(mMoPi);//磨皮
            mTiSDKManager.setSkinSaturation(mBaoHe);//饱和
            mTiSDKManager.setSkinTenderness(mFengNen);//粉嫩
            mTiSDKManager.setEyeMagnifying(mBigEye);//大眼
            mTiSDKManager.setChinSlimming(mFace);//瘦脸
            mTiSDKManager.setSticker(mTieZhi);//贴纸
            mTiSDKManager.setFilterEnum(mTiFilterEnum);//滤镜
            mTiSDKManager.setDistortionEnum(mTiDistortionEnum);//哈哈镜
        } catch (Exception e) {
            ToastUtil.show(R.string.beauty_init_error);
        }
    }

    /**
     * 录制回调
     */
    @Override
    public void onRecordEvent(int event, Bundle bundle) {
        if (event == TXRecordCommon.EVT_ID_PAUSE) {
            if (mRecordProgressView != null) {
                mRecordProgressView.clipComplete();
            }
        } else if (event == TXRecordCommon.EVT_CAMERA_CANNOT_USE) {
            ToastUtil.show(R.string.video_record_camera_failed);
        } else if (event == TXRecordCommon.EVT_MIC_CANNOT_USE) {
            ToastUtil.show(R.string.video_record_audio_failed);
        }
    }

    /**
     * 录制回调 录制进度
     */
    @Override
    public void onRecordProgress(long milliSecond) {
        if (mRecordProgressView != null) {
            mRecordProgressView.setProgress((int) milliSecond);
        }
        if (mTime != null) {
            mTime.setText(String.format("%.2f", milliSecond / 1000f) + "s");
        }
        mRecordTime = milliSecond;
        if (milliSecond >= MIN_DURATION) {
            if (mBtnNext != null && mBtnNext.getVisibility() != View.VISIBLE) {
                mBtnNext.setVisibility(View.VISIBLE);
            }
        }
        if (milliSecond >= MAX_DURATION) {
            if (!mIsReachMaxRecordDuration) {
                mIsReachMaxRecordDuration = true;
                if (mRecordBtnAnimator != null) {
                    mRecordBtnAnimator.cancel();
                }
                showProccessDialog();
            }
        }
    }

    /**
     * 录制回调
     */
    @Override
    public void onRecordComplete(TXRecordCommon.TXRecordResult result) {
        hideProccessDialog();
        mRecordStoped = true;
        if (mRecorder != null) {
            mRecorder.stopBGM();
            mDuration = mRecorder.getPartsManager().getDuration();
        }
        if (result.retCode < 0) {
            ToastUtil.show(R.string.video_record_failed);
            return;
        }
        L.e(TAG, "录制完成 开始预处理------->");
        startPreProcess();
    }


    /**
     * 录制中美颜处理回调
     */
    @Override
    public int onTextureCustomProcess(int i, int i1, int i2) {
        if (mTiSDKManager != null) {
            return mTiSDKManager.renderTexture2D(i, i1, i2, TiRotation.CLOCKWISE_ROTATION_0, false);
        }
        return 0;
    }

    /**
     * 录制中美颜处理回调
     */
    @Override
    public void onDetectFacePoints(float[] floats) {

    }

    /**
     * 录制中美颜处理回调
     */
    @Override
    public void onTextureDestroyed() {

    }


    /**
     * 录制结束后，视频预处理进度回调
     */
    @Override
    public void onProcessProgress(float progress) {
        int p = (int) (progress * 100);
        if (p > 0 && p <= 100) {
            mProcessProgress = p;
            if (mVideoProcessViewHolder != null) {
                mVideoProcessViewHolder.setProgress(p);
            }
        }
    }

    /**
     * 录制结束后，视频预处理的回调
     */
    @Override
    public void onProcessComplete(TXVideoEditConstants.TXGenerateResult result) {
        if (result.retCode == TXVideoEditConstants.GENERATE_RESULT_OK) {
            L.e(TAG, "视频预处理----->完成");
            VideoEditActivity.forward(mContext, mDuration, mVideoPath, mFromRecord, mMusicBean);
        } else {
            L.e(TAG, "视频预处理错误------->" + result.descMsg);
            ToastUtil.show(WordUtil.getString(R.string.video_process_failed));
            VideoEditUtil.getInstance().release();
        }
        finish();
    }

    /**
     * 制结束后，获取缩略图的回调
     */
    @Override
    public void onThumbnail(int i, long l, Bitmap bitmap) {
        VideoEditUtil.getInstance().addVideoBitmap(bitmap);
    }

    private static class InnerHandler extends Handler {
        private final WeakReference<VideoRecordActivity> mActivity;

        public InnerHandler(VideoRecordActivity activity) {
            mActivity = new WeakReference<>(activity);
        }

        @Override
        public void handleMessage(Message msg) {
            VideoRecordActivity activity = mActivity.get();
            if (activity != null) {
                activity.showPic(mBitmap);
            }
        }
    }

    private InnerHandler picHandler = new InnerHandler(this);

    private void showPic(Bitmap bitmap) {
        layoutPicturePreview.setVisibility(View.VISIBLE);
        ((ImageView) findViewById(R.id.ivPicturePreview)).setImageBitmap(bitmap);
    }

    public void recordClick(View v) {
        if (mRecordStoped || !canClick()) {
            return;
        }
        switch (v.getId()) {
            case R.id.btn_start_record://录制 暂停录制
                if (type == TYPE_PICTURE || type == TYPE_PICTURE_ONLY) {
                    mRecorder.snapshot(new TXRecordCommon.ITXSnapshotListener() {
                        @Override
                        public void onSnapshot(Bitmap bmp) {
                            // 保存或者显示截图
                            mBitmap = bmp;
                            Message msg = new Message();
//                            msg.obj = bmp;
                            picHandler.sendMessage(msg);
                        }
                    });
                } else {
                    clickRecord();
                }
                break;
            case R.id.btn_camera://翻转
                clickCamera();
                break;
            case R.id.btn_flash://闪光灯
                clickFlash();
                break;
            case R.id.btn_beauty://美颜
                clickBeauty();
                break;
            case R.id.btn_music://音乐
                clickMusic();
                break;
            case R.id.btn_speed_1://极慢
                changeRecordSpeed(TXRecordCommon.RECORD_SPEED_SLOWEST);
                break;
            case R.id.btn_speed_2://慢
                changeRecordSpeed(TXRecordCommon.RECORD_SPEED_SLOW);
                break;
            case R.id.btn_speed_3://正常
                changeRecordSpeed(TXRecordCommon.RECORD_SPEED_NORMAL);
                break;
            case R.id.btn_speed_4://快
                changeRecordSpeed(TXRecordCommon.RECORD_SPEED_FAST);
                break;
            case R.id.btn_speed_5://极快
                changeRecordSpeed(TXRecordCommon.RECORD_SPEED_FASTEST);
                break;
            case R.id.btn_upload://上传
                clickUpload();
                break;
            case R.id.btn_delete://删除
                clickDelete();
                break;
            case R.id.btn_next://录制完成，下一步
                clickNext();
                break;
            case R.id.ivTakePictureCancel://取消拍照
                layoutPicturePreview.setVisibility(View.GONE);
                break;
            case R.id.ivTakePictureConfirm://确认拍照
                pictureConfirm();
                break;
        }
    }

    /**
     * 拍照确认
     */
    private void pictureConfirm() {
        String coverImagePath = PictureEditUtil.getInstance().generatePictureOutputPath();
        File imageFile = new File(coverImagePath);
        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(imageFile);
            mBitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);
            fos.flush();
            fos.close();
        } catch (Exception e) {
            imageFile = null;
            e.printStackTrace();
        } finally {
            if (fos != null) {
                try {
                    fos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        if (mBitmap != null) {
            mBitmap.recycle();
        }
        if (imageFile == null) {
            layoutPicturePreview.setVisibility(View.GONE);
            ToastUtil.show("图片保存失败");
            return;
        }

        ArrayList<PictureChooseBean> list = new ArrayList<>();
        list.add(new PictureChooseBean(coverImagePath, 0));
        if (type == TYPE_PICTURE) {
            VideoPublishActivity.forward(mContext, list);
        } else if (type == TYPE_PICTURE_ONLY) {
            Intent intent = new Intent();
            intent.putParcelableArrayListExtra(Constants.PICTURE_LIST, list);
            setResult(RESULT_OK, intent);
        }

        finish();
    }

    /**
     * 点击摄像头
     */
    private void clickCamera() {
        if (mRecorder != null) {
            if (mBtnFlash != null && mBtnFlash.isSelected()) {
                mBtnFlash.setSelected(false);
                mRecorder.toggleTorch(true);
            }
            mFrontCamera = !mFrontCamera;
            mRecorder.switchCamera(mFrontCamera);
        }
    }

    /**
     * 点击闪光灯
     */
    private void clickFlash() {
        if (mFrontCamera) {
            ToastUtil.show(R.string.live_open_flash);
            return;
        }
        if (mBtnFlash != null) {
            if (mBtnFlash.isSelected()) {
                mBtnFlash.setSelected(false);
            } else {
                mBtnFlash.setSelected(true);
            }
            if (mRecorder != null) {
                mRecorder.toggleTorch(mBtnFlash.isSelected());
            }
        }
    }

    /**
     * 点击美颜
     */
    private void clickBeauty() {
        if (mBeautyViewHolder == null) {
            if (BuildConfig.isOpenTiui) {
                mBeautyViewHolder = new LiveBeautyViewHolder(mContext, mRoot);
            } else {
                mBeautyViewHolder = new DefaultBeautyViewHolder(mContext, mRoot);
            }
            mBeautyViewHolder.setVisibleListener(new DefaultBeautyViewHolder.VisibleListener() {
                @Override
                public void onVisibleChanged(boolean visible) {
                    if (mGroup1 != null) {
                        if (visible) {
                            if (mGroup1.getVisibility() == View.VISIBLE) {
                                mGroup1.setVisibility(View.INVISIBLE);
                            }
                        } else {
                            if (mGroup1.getVisibility() != View.VISIBLE) {
                                mGroup1.setVisibility(View.VISIBLE);
                            }
                        }
                    }
                }
            });
            if (BuildConfig.isOpenTiui) {
                mBeautyViewHolder.setEffectListener(this);//萌颜
            } else {
                mBeautyViewHolder.setEffectListener(mBaseBeautyEffectListener);//基础美颜
            }
        }
        mBeautyViewHolder.show();
    }

    //基础美颜的回调 （非萌颜）
    private DefaultEffectListener mBaseBeautyEffectListener = new DefaultEffectListener() {
        @Override
        public void onFilterChanged(FilterBean filterBean) {
            if (mFilterBitmap != null) {
                mFilterBitmap.recycle();
            }
            if (mRecorder != null) {
                int filterSrc = filterBean.getFilterSrc();
                if (filterSrc != 0) {
                    Bitmap bitmap = BitmapUtil.getInstance().decodeBitmap(filterSrc);
                    if (bitmap != null) {
                        mFilterBitmap = bitmap;
                        mRecorder.setFilter(bitmap);
                    } else {
                        mRecorder.setFilter(null);
                    }
                } else {
                    mRecorder.setFilter(null);
                }
            }
        }

        @Override
        public void onMeiBaiChanged(int progress) {
            if (mRecorder != null) {
                int v = progress / 10;
                if (mMeibai != v) {
                    mMeibai = v;
                    mRecorder.setBeautyDepth(0, mMoPi, mMeibai, mFengNen);
                }
            }

        }

        @Override
        public void onMoPiChanged(int progress) {
            if (mRecorder != null) {
                int v = progress / 10;
                if (mMoPi != v) {
                    mMoPi = v;
                    mRecorder.setBeautyDepth(0, mMoPi, mMeibai, mFengNen);
                }
            }
        }

        @Override
        public void onHongRunChanged(int progress) {
            if (mRecorder != null) {
                int v = progress / 10;
                if (mFengNen != v) {
                    mFengNen = v;
                    mRecorder.setBeautyDepth(0, mMoPi, mMeibai, mFengNen);
                }
            }
        }
    };

    /**
     * 点击音乐
     */
    private void clickMusic() {
        if (mVideoMusicViewHolder == null) {
            mVideoMusicViewHolder = new VideoMusicViewHolder(mContext, mRoot);
            mVideoMusicViewHolder.setActionListener(new VideoMusicViewHolder.ActionListener() {
                @Override
                public void onChooseMusic(MusicBean musicBean) {
                    mMusicBean = musicBean;
                    mBgmPlayStarted = false;
                }
            });
            mVideoMusicViewHolder.addToParent();
            addLifeCycleListener(mVideoMusicViewHolder.getLifeCycleListener());
        }
        mVideoMusicViewHolder.show();
    }

    /**
     * 点击上传，选择本地视频
     */
    private void clickUpload() {
        if (type == TYPE_PICTURE) {
            Intent intent = new Intent(mContext, PictureChooseActivity.class);
            intent.putExtra(PictureChooseActivity.PICTURE_CHOOSE_NUM, 9);
            startActivityForResult(intent, 1);
        } else {
            Intent intent = new Intent(mContext, VideoChooseActivity.class);
            intent.putExtra(Constants.VIDEO_DURATION, Long.valueOf(MAX_DURATION));
            startActivityForResult(intent, 0);
        }
    }

    /**
     * 选择本地视频的回调
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == 0) {
                mMusicBean = null;
                mVideoPath = data.getStringExtra(Constants.VIDEO_PATH);
                mDuration = data.getLongExtra(Constants.VIDEO_DURATION, 0);
                mFromRecord = false;
                startPreProcess();
            } else if (requestCode == 1) {
                VideoPublishActivity.forward(mContext, data.<PictureChooseBean>getParcelableArrayListExtra(Constants.PICTURE_LIST));
                finish();
            }
        }
    }


    /**
     * 开始预览
     */
    private void startCameraPreview() {
        if (mRecorder != null && mCustomConfig != null && mVideoView != null) {
//            mRecorder.startCameraSimplePreview(simpleParam, mVideoView);
            mRecorder.startCameraCustomPreview(mCustomConfig, mVideoView);
            if (!mFrontCamera) {
                mRecorder.switchCamera(false);
            }
            if (BuildConfig.isOpenTiui) {
                initBeauty();
                mRecorder.setVideoProcessListener(this);
            }
        }
    }

    /**
     * 停止预览
     */
    private void stopCameraPreview() {
        if (mRecorder != null) {
            if (mRecording) {
                pauseRecord();
            }
            mRecorder.stopCameraPreview();
            mRecorder.setVideoProcessListener(null);
            if (mTiSDKManager != null) {
                mTiSDKManager.destroy();
            }
            mTiSDKManager = null;
        }
    }

    /**
     * 点击录制
     */
    private void clickRecord() {
        if (mRecordStarted) {
            if (mRecording) {
                pauseRecord();
            } else {
                resumeRecord();
            }
        } else {
            startRecord();
        }
    }

    /**
     * 开始录制
     */
    private void startRecord() {
        if (mRecorder != null) {
            mVideoPath = VideoEditUtil.getInstance().generateVideoOutputPath();//视频保存的路径
            mFromRecord = true;
            int result = mRecorder.startRecord(mVideoPath, null);//为空表示不需要生成视频封面
            if (result != TXRecordCommon.START_RECORD_OK) {
                if (result == TXRecordCommon.START_RECORD_ERR_NOT_INIT) {
                    ToastUtil.show(R.string.video_record_tip_1);
                } else if (result == TXRecordCommon.START_RECORD_ERR_IS_IN_RECORDING) {
                    ToastUtil.show(R.string.video_record_tip_2);
                } else if (result == TXRecordCommon.START_RECORD_ERR_VIDEO_PATH_IS_EMPTY) {
                    ToastUtil.show(R.string.video_record_tip_3);
                } else if (result == TXRecordCommon.START_RECORD_ERR_API_IS_LOWER_THAN_18) {
                    ToastUtil.show(R.string.video_record_tip_4);
                } else if (result == TXRecordCommon.START_RECORD_ERR_LICENCE_VERIFICATION_FAILED) {
                    ToastUtil.show(R.string.video_record_tip_5);
                }
                return;
            }
        }
        mRecordStarted = true;
        mRecording = true;
        resumeBgm();
        startRecordBtnAnim();
        if (mGroup2 != null && mGroup2.getVisibility() == View.VISIBLE) {
            mGroup2.setVisibility(View.INVISIBLE);
        }
    }


    /**
     * 暂停录制
     */
    private void pauseRecord() {
        if (mRecorder == null) {
            return;
        }
        pauseBgm();
        mRecorder.pauseRecord();
        mRecording = false;
        stopRecordBtnAnim();
        if (mGroup2 != null && mGroup2.getVisibility() != View.VISIBLE) {
            mGroup2.setVisibility(View.VISIBLE);
        }
        TXUGCPartsManager partsManager = mRecorder.getPartsManager();
        if (partsManager != null) {
            List<String> partList = partsManager.getPartsPathList();
            if (partList != null && partList.size() > 0) {
                if (mGroup3 != null && mGroup3.getVisibility() == View.VISIBLE) {
                    mGroup3.setVisibility(View.INVISIBLE);
                }
                if (mGroup4 != null && mGroup4.getVisibility() != View.VISIBLE) {
                    mGroup4.setVisibility(View.VISIBLE);
                }
            } else {
                if (mGroup3 != null && mGroup3.getVisibility() != View.VISIBLE) {
                    mGroup3.setVisibility(View.VISIBLE);
                }
                if (mGroup4 != null && mGroup4.getVisibility() == View.VISIBLE) {
                    mGroup4.setVisibility(View.INVISIBLE);
                }
            }
        }
    }

    /**
     * 恢复录制
     */
    private void resumeRecord() {
        if (mRecorder != null) {
            int startResult = mRecorder.resumeRecord();
            if (startResult != TXRecordCommon.START_RECORD_OK) {
                ToastUtil.show(WordUtil.getString(R.string.video_record_failed));
                return;
            }
        }
        mRecording = true;
        resumeBgm();
        startRecordBtnAnim();
        if (mGroup2 != null && mGroup2.getVisibility() == View.VISIBLE) {
            mGroup2.setVisibility(View.INVISIBLE);
        }
    }

    /**
     * 暂停背景音乐
     */
    private void pauseBgm() {
        if (mRecorder != null) {
            mRecorder.pauseBGM();
        }
    }

    /**
     * 恢复背景音乐
     */
    private void resumeBgm() {
        if (mRecorder == null) {
            return;
        }
        if (!mBgmPlayStarted) {
            if (mMusicBean == null) {
                return;
            }
            int bgmDuration = mRecorder.setBGM(mMusicBean.getLocalPath());
            mRecorder.playBGMFromTime(0, bgmDuration);
            mRecorder.setBGMVolume(1);//背景音为1最大
            mRecorder.setMicVolume(0);//原声音为0
            mBgmPlayStarted = true;
        } else {
            mRecorder.resumeBGM();
        }
    }

    /**
     * 按钮录制动画开始
     */
    private void startRecordBtnAnim() {
        if (mRecordView != null) {
            mRecordView.setBackground(mRecordDrawable);
        }
        if (mRecordBtnAnimator != null) {
            mRecordBtnAnimator.start();
        }
    }

    /**
     * 按钮录制动画停止
     */
    private void stopRecordBtnAnim() {
        if (mRecordView != null) {
            mRecordView.setBackground(mUnRecordDrawable);
        }
        if (mRecordBtnAnimator != null) {
            mRecordBtnAnimator.cancel();
        }
        if (mVideoRecordBtnView != null) {
            mVideoRecordBtnView.reset();
        }
    }

    /**
     * 调整录制速度
     */
    private void changeRecordSpeed(int speed) {
        if (mRecordSpeed == speed) {
            return;
        }
        mRecordSpeed = speed;
        if (mRecorder != null) {
            mRecorder.setRecordSpeed(speed);
        }
    }

    /**
     * 删除录制分段
     */
    private void clickDelete() {
        DialogUtil.showSimpleDialog(mContext, WordUtil.getString(R.string.video_record_delete_last), new DialogUtil.SimpleCallback() {
            @Override
            public void onConfirmClick(Dialog dialog, String content) {
                doClickDelete();
            }
        });
    }

    /**
     * 删除录制分段
     */
    private void doClickDelete() {
        if (!mRecordStarted || mRecording || mRecorder == null) {
            return;
        }
        TXUGCPartsManager partsManager = mRecorder.getPartsManager();
        if (partsManager == null) {
            return;
        }
        List<String> partList = partsManager.getPartsPathList();
        if (partList == null || partList.size() == 0) {
            return;
        }
        partsManager.deleteLastPart();
        int time = partsManager.getDuration();
        if (mTime != null) {
            mTime.setText(String.format("%.2f", time / 1000f) + "s");
        }
        mRecordTime = time;
        if (time < MIN_DURATION && mBtnNext != null && mBtnNext.getVisibility() == View.VISIBLE) {
            mBtnNext.setVisibility(View.INVISIBLE);
        }
        if (mRecordProgressView != null) {
            mRecordProgressView.deleteLast();
        }

        mBgmPlayStarted = false;

        partList = partsManager.getPartsPathList();
        if (partList != null && partList.size() == 0) {
            if (mGroup2 != null && mGroup2.getVisibility() != View.VISIBLE) {
                mGroup2.setVisibility(View.VISIBLE);
            }
            if (mGroup3 != null && mGroup3.getVisibility() != View.VISIBLE) {
                mGroup3.setVisibility(View.VISIBLE);
            }
            if (mGroup4 != null && mGroup4.getVisibility() == View.VISIBLE) {
                mGroup4.setVisibility(View.INVISIBLE);
            }
        }
    }

    /**
     * 结束录制，会触发 onRecordComplete
     */
    public void clickNext() {

        stopRecordBtnAnim();
        if (mRecorder != null) {
            mRecorder.stopBGM();
            mRecorder.stopRecord();
            showProccessDialog();
        }

    }


    /**
     * 录制结束时候显示处理中的弹窗
     */
    private void showProccessDialog() {
        if (mStopRecordDialog == null) {
            mStopRecordDialog = DialogUtil.loadingDialog(mContext, WordUtil.getString(R.string.video_processing), false);
            mStopRecordDialog.show();
        }
    }

    /**
     * 隐藏处理中的弹窗
     */
    private void hideProccessDialog() {
        if (mStopRecordDialog != null) {
            mStopRecordDialog.dismiss();
        }
        mStopRecordDialog = null;
    }

    /**
     * 点击取消预处理的按钮
     */
    @Override
    public void onCancelProcessClick() {
        if (mProcessProgress <= 0) {
            return;
        }
        if (mVideoEditer != null) {
            try {
                mVideoEditer.cancel();
            } catch (Exception e) {
                L.e(TAG, "----->" + e.getClass() + "----->" + e.getMessage());
            }
        }
        ToastUtil.show(WordUtil.getString(R.string.video_process_cancel));
        VideoEditUtil.getInstance().release();
        if (mFromRecord && !TextUtils.isEmpty(mVideoPath)) {
            File file = new File(mVideoPath);
            if (file.exists()) {
                file.delete();
            }
        }
        finish();
    }


    /**
     * 开始预处理
     */
    private void startPreProcess() {
        mVideoProcessViewHolder = new VideoProcessViewHolder(mContext, mRoot, WordUtil.getString(R.string.video_process_1));
        mVideoProcessViewHolder.addToParent();
        mVideoProcessViewHolder.setActionListener(this);
        if (mHandler == null) {
            mHandler = new MyHandler(this);
        }
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    TXVideoEditConstants.TXVideoInfo info = TXVideoInfoReader.getInstance(VideoRecordActivity.this).getVideoFileInfo(mVideoPath);
                    if (mHandler != null) {
                        if (info == null) {
                            mHandler.sendEmptyMessage(MyHandler.ERROR);
                        } else {
                            mHandler.sendEmptyMessage(MyHandler.SUCCESS);
                        }
                    }
                } catch (Exception e) {
                    if (mHandler != null) {
                        mHandler.sendEmptyMessage(MyHandler.ERROR);
                    }
                }
            }
        }).start();
    }

    /**
     * 执行预处理
     */
    private void doPreProcess() {
        mProcessStarted = true;
        try {
            mVideoEditer = VideoEditUtil.getInstance().createVideoEditer(mContext, mVideoPath);
            mVideoEditer.setVideoProcessListener(this);
            mVideoEditer.setThumbnailListener(this);
            int thumbnailCount = (int) Math.floor(mDuration / 1000f);
            TXVideoEditConstants.TXThumbnail thumbnail = new TXVideoEditConstants.TXThumbnail();
            thumbnail.count = thumbnailCount;
            thumbnail.width = 100;
            thumbnail.height = 100;
            mVideoEditer.setThumbnail(thumbnail);
            mVideoEditer.processVideo();
        } catch (Exception e) {
            processFailed();
        }
    }

    /**
     * 预处理失败
     */
    private void processFailed() {
        ToastUtil.show(R.string.video_process_failed);
        VideoEditUtil.getInstance().release();
        if (mFromRecord && !TextUtils.isEmpty(mVideoPath)) {
            File file = new File(mVideoPath);
            if (file.exists()) {
                file.delete();
            }
        }
        finish();
    }


    @Override
    public void onBackPressed() {
        if (!canClick()) {
            return;
        }
        if (layoutPicturePreview.getVisibility() == View.VISIBLE) {
            layoutPicturePreview.setVisibility(View.GONE);
        } else if (type == TYPE_PICTURE_ONLY) {
            exit();
        } else if (mProcessStarted) {
            onCancelProcessClick();
        } else {
            if (mBeautyViewHolder != null && mBeautyViewHolder.isShowed()) {
                mBeautyViewHolder.hide();
                return;
            }
            if (mVideoMusicViewHolder != null && mVideoMusicViewHolder.isShowed()) {
                mVideoMusicViewHolder.hide();
                return;
            }
            List<Integer> list = new ArrayList<>();
            if (mRecordTime > 0) {
                list.add(R.string.video_re_record);
            }
            list.add(R.string.video_exit);
            DialogUtil.showStringArrayDialog(mContext, list.toArray(new Integer[list.size()]), new DialogUtil.StringArrayDialogCallback() {
                @Override
                public void onItemClick(String text, int tag) {
                    switch (tag) {
                        case R.string.video_re_record:
                            reRecord();
                            break;
                        case R.string.video_exit:
                            exit();
                            break;
                    }
                }
            });
        }
    }

    /**
     * 重新录制
     */
    private void reRecord() {
        if (mRecorder == null) {
            return;
        }
        mRecorder.pauseBGM();
        mMusicBean = null;
        mBgmPlayStarted = false;
        mRecorder.pauseRecord();
        mRecording = false;
        stopRecordBtnAnim();
        if (mGroup2 != null && mGroup2.getVisibility() != View.VISIBLE) {
            mGroup2.setVisibility(View.VISIBLE);
        }
        TXUGCPartsManager partsManager = mRecorder.getPartsManager();
        if (partsManager != null) {
            partsManager.deleteAllParts();
        }
        mRecorder.onDeleteAllParts();
        if (mTime != null) {
            mTime.setText("0.00s");
        }
        mRecordTime = 0;
        if (mBtnNext != null && mBtnNext.getVisibility() == View.VISIBLE) {
            mBtnNext.setVisibility(View.INVISIBLE);
        }
        if (mRecordProgressView != null) {
            mRecordProgressView.deleteAll();
        }
        if (mGroup3 != null && mGroup3.getVisibility() != View.VISIBLE) {
            mGroup3.setVisibility(View.VISIBLE);
        }
        if (mGroup4 != null && mGroup4.getVisibility() == View.VISIBLE) {
            mGroup4.setVisibility(View.INVISIBLE);
        }

    }

    /**
     * 退出
     */
    private void exit() {
        VideoEditUtil.getInstance().release();
        release();
        super.onBackPressed();
    }


    @Override
    protected void onStart() {
        super.onStart();
        startCameraPreview();
    }

    @Override
    protected void onStop() {
        super.onStop();
        stopCameraPreview();
    }

    @Override
    protected void onDestroy() {
        release();
        super.onDestroy();
        L.e(TAG, "-------->onDestroy");
    }

    @Override
    public void finish() {
        release();
        super.finish();
    }

    private void release() {
        if (mStopRecordDialog != null && mStopRecordDialog.isShowing()) {
            mStopRecordDialog.dismiss();
        }
        if (mHandler != null) {
            mHandler.release();
        }
        if (mBeautyViewHolder != null) {
            mBeautyViewHolder.release();
        }
        if (mVideoMusicViewHolder != null) {
            mVideoMusicViewHolder.release();
        }
        if (mRecordProgressView != null) {
            mRecordProgressView.release();
        }
        if (mRecordBtnAnimator != null) {
            mRecordBtnAnimator.cancel();
        }
        if (mRecorder != null) {
            mRecorder.toggleTorch(false);
            mRecorder.stopBGM();
            if (mRecordStarted) {
                mRecorder.stopRecord();
            }
            mRecorder.stopCameraPreview();
            mRecorder.setVideoProcessListener(null);
            mRecorder.setVideoRecordListener(null);
            TXUGCPartsManager getPartsManager = mRecorder.getPartsManager();
            if (getPartsManager != null) {
                getPartsManager.deleteAllParts();
            }
            mRecorder.release();
        }
        if (mTiSDKManager != null) {
            mTiSDKManager.destroy();
        }
        if (mVideoEditer != null) {
            mVideoEditer.setVideoProcessListener(null);
            mVideoEditer.setThumbnailListener(null);
        }
        if (mVideoProcessViewHolder != null) {
            mVideoProcessViewHolder.setActionListener(null);
        }

        mStopRecordDialog = null;
        mHandler = null;
        mBeautyViewHolder = null;
        mVideoMusicViewHolder = null;
        mRecordProgressView = null;
        mRecordBtnAnimator = null;
        mRecorder = null;
        mTiSDKManager = null;
        mVideoEditer = null;
        mVideoProcessViewHolder = null;

    }


    /****************美颜回调 start********************/
    /**
     * 设置滤镜回调
     */
    @Override
    public void onFilterChanged(TiFilterEnum tiFilterEnum) {
        if (mTiSDKManager != null) {
            mTiFilterEnum = tiFilterEnum;
            mTiSDKManager.setFilterEnum(tiFilterEnum);
        }
    }

    /**
     * 设置美白回调
     */
    @Override
    public void onMeiBaiChanged(int progress) {
        if (mTiSDKManager != null) {
            mMeibai = progress;
            mTiSDKManager.setSkinWhitening(progress);
        }
    }

    /**
     * 设置磨皮回调
     */
    @Override
    public void onMoPiChanged(int progress) {
        if (mTiSDKManager != null) {
            mMoPi = progress;
            mTiSDKManager.setSkinBlemishRemoval(progress);
        }
    }

    /**
     * 设置饱和回调
     */
    @Override
    public void onBaoHeChanged(int progress) {
        if (mTiSDKManager != null) {
            mBaoHe = progress;
            mTiSDKManager.setSkinSaturation(progress);
        }
    }

    /**
     * 设置粉嫩回调
     */
    @Override
    public void onFengNenChanged(int progress) {
        if (mTiSDKManager != null) {
            mFengNen = progress;
            mTiSDKManager.setSkinTenderness(progress);
        }
    }

    /**
     * 设置大眼回调
     */
    @Override
    public void onBigEyeChanged(int progress) {
        if (mTiSDKManager != null) {
            mBigEye = progress;
            mTiSDKManager.setEyeMagnifying(progress);
        }
    }

    /**
     * 设置瘦脸回调
     */
    @Override
    public void onFaceChanged(int progress) {
        if (mTiSDKManager != null) {
            mFace = progress;
            mTiSDKManager.setChinSlimming(progress);
        }
    }

    /**
     * 设置下巴回调
     */
    @Override
    public void onChinChanged(int progress) {
        if (mTiSDKManager != null) {
            mChin = progress;
            mTiSDKManager.setJawTransforming(progress);
        }
    }

    /**
     * 设置额头回调
     */
    @Override
    public void onForeheadChanged(int progress) {
        if (mTiSDKManager != null) {
            mForehead = progress;
            mTiSDKManager.setForeheadTransforming(progress);
        }
    }

    /**
     * 设置嘴型回调
     */
    @Override
    public void onMouthChanged(int progress) {
        if (mTiSDKManager != null) {
            mMouth = progress;
            mTiSDKManager.setMouthTransforming(progress);
        }
    }

    /**
     * 设置贴纸回调
     */
    @Override
    public void onTieZhiChanged(String tieZhiName) {
        if (mTiSDKManager != null) {
            mTieZhi = tieZhiName;
            mTiSDKManager.setSticker(tieZhiName);
        }
    }

    /**
     * 设置哈哈镜回调
     */
    @Override
    public void onHaHaChanged(TiDistortionEnum tiDistortionEnum) {
        if (mTiSDKManager != null) {
            mTiDistortionEnum = tiDistortionEnum;
            mTiSDKManager.setDistortionEnum(tiDistortionEnum);
        }
    }

    /**
     * 设置抖动回调
     */
    @Override
    public void onRockChanged(TiRockEnum tiRockEnum) {
        if (mTiSDKManager != null) {
            mTiRockEnum = tiRockEnum;
            mTiSDKManager.setRockEnum(tiRockEnum);
        }
    }


    /****************美颜回调 end********************/


    private static class MyHandler extends Handler {

        private static final int SUCCESS = 1;
        private static final int ERROR = 0;

        private VideoRecordActivity mRecordActivity;

        public MyHandler(VideoRecordActivity recordActivity) {
            mRecordActivity = new WeakReference<>(recordActivity).get();
        }

        @Override
        public void handleMessage(Message msg) {
            if (mRecordActivity != null) {
                switch (msg.what) {
                    case SUCCESS:
                        mRecordActivity.doPreProcess();
                        break;
                    case ERROR:
                        mRecordActivity.processFailed();
                        break;
                }
            }
        }

        void release() {
            removeCallbacksAndMessages(null);
            mRecordActivity = null;
        }
    }


    public static void forward(Context context, VideoMusicBean musicBean) {
        Intent intent = new Intent(context, VideoRecordActivity.class);
        intent.putExtra("music", musicBean);
        context.startActivity(intent);
    }

    public static void forward(Context context) {
        Intent intent = new Intent(context, VideoRecordActivity.class);
        context.startActivity(intent);
    }
}
