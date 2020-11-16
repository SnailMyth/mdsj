package com.wwsl.mdsj.activity.video;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.media.MediaMetadataRetriever;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.Switch;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.frame.fire.util.LogUtils;
import com.permissionx.guolindev.PermissionX;
import com.tencent.map.geolocation.TencentLocation;
import com.tencent.map.geolocation.TencentLocationListener;
import com.tencent.rtmp.ITXLivePlayListener;
import com.tencent.rtmp.TXLiveConstants;
import com.tencent.rtmp.TXLivePlayConfig;
import com.tencent.rtmp.TXLivePlayer;
import com.tencent.rtmp.ui.TXCloudVideoView;
import com.wwsl.mdsj.AppConfig;
import com.wwsl.mdsj.Constants;
import com.wwsl.mdsj.R;
import com.wwsl.mdsj.activity.common.AbsActivity;
import com.wwsl.mdsj.activity.live.ShopWindowActivity;
import com.wwsl.mdsj.adapter.ShopWindowAdapter;
import com.wwsl.mdsj.bean.ConfigBean;
import com.wwsl.mdsj.bean.LiveShopWindowBean;
import com.wwsl.mdsj.bean.MusicBean;
import com.wwsl.mdsj.bean.PictureChooseBean;
import com.wwsl.mdsj.bean.net.NetFriendBean;
import com.wwsl.mdsj.dialog.AtFriendDialog;
import com.wwsl.mdsj.dialog.MainStartDialogFragment;
import com.wwsl.mdsj.http.HttpCallback;
import com.wwsl.mdsj.http.HttpConst;
import com.wwsl.mdsj.http.HttpUtil;
import com.wwsl.mdsj.interfaces.CommonCallback;
import com.wwsl.mdsj.upload.VideoUploadBean;
import com.wwsl.mdsj.upload.VideoUploadCallback;
import com.wwsl.mdsj.upload.VideoUploadQnImpl;
import com.wwsl.mdsj.upload.VideoUploadStrategy;
import com.wwsl.mdsj.upload.VideoUploadTxImpl;
import com.wwsl.mdsj.utils.DialogUtil;
import com.wwsl.mdsj.utils.L;
import com.wwsl.mdsj.utils.LocationUtil;
import com.wwsl.mdsj.utils.ToastUtil;
import com.wwsl.mdsj.utils.WordUtil;
import com.wwsl.mdsj.views.dialog.OnDialogCallBackListener;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;

import cn.hutool.core.util.StrUtil;

/**
 * Created by cxf on 2018/12/10.
 * 视频发布
 */
@SuppressLint("DefaultLocale")
public class VideoPublishActivity extends AbsActivity implements ITXLivePlayListener, View.OnClickListener {
    private static final int TYPE_VIDEO = 0;
    private static final int TYPE_PICTURE = 1;
    private LiveShopWindowBean goodItem;
    private String cityCode = "";

    public static void forward(Context context, ArrayList<PictureChooseBean> listPictures) {
        Intent intent = new Intent(context, VideoPublishActivity.class);
        intent.putParcelableArrayListExtra(Constants.PICTURE_LIST, listPictures);
        context.startActivity(intent);
    }

    public static void forward(Context context, String videoPath, int saveType, long videoTimeLong, MusicBean musicBean) {
        Intent intent = new Intent(context, VideoPublishActivity.class);
        intent.putExtra(Constants.VIDEO_PATH, videoPath);
        intent.putExtra(Constants.VIDEO_SAVE_TYPE, saveType);
        intent.putExtra(Constants.VIDEO_TIME_LONG, videoTimeLong);
        intent.putExtra(Constants.VIDEO_MUSIC_BEAN, musicBean);
        context.startActivity(intent);
    }

    private static final String TAG = "VideoPublishActivity";
    private TextView mNum;
    private TextView mLocation;
    private TextView txAddGood;
    private TXCloudVideoView mTXCloudVideoView;
    private TXLivePlayer mPlayer;
    private String mVideoPath;
    private boolean mPlayStarted;//播放是否开始了
    private boolean mPaused;//生命周期暂停
    private ConfigBean mConfigBean;
    private VideoUploadStrategy mUploadStrategy;
    private EditText mInput;
    private String mVideoTitle;//视频标题
    private Dialog mLoading;
    private int mSaveType;
    private long mVideoTimeLong;
    private FrameLayout layoutVideo;
    private ConstraintLayout addGoodsLayout;
    private ConstraintLayout yxypLayout;
    private ConstraintLayout znLayout;
    private Switch publicSwitch;
    private Switch znSwitch;
    private Switch yxSwitch;

    private TencentLocationListener mLocationListener;
    private double lat;
    private double lng;
    private String address;
    private MusicBean mMusicBean;//背景音乐

    private AtFriendDialog atFriendDialog;

    private String atUid = "";
    private String atUserName;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_video_publish;
    }

    @Override
    protected boolean isStatusBarWhite() {
        return false;
    }

    @Override
    protected void main() {

//        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        initView();

        initData();

        initVideoPlayer();

        initListener();

        initLocation();

    }

    private void initListener() {
        findViewById(R.id.btn_pub).setOnClickListener(this);

        mInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }


            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (mNum != null) {
                    mNum.setText(String.format("%d/50", s.length()));
                }

                LogUtils.e(TAG, "onTextChanged: start:" + start + "--before:" + before);

                String temp = s.toString();

                if (before == 0) {
                    //添加字符
                    if (temp.endsWith("@") && StrUtil.isEmpty(atUid)) {
                        LogUtils.e(TAG, "onTextChanged: 输入了@");
                        hideSoftInput();
                        atFriendDialog.show();
                    }
                } else if (before > 0) {
                    //删除字符
                    if (!StrUtil.isEmpty(atUid) && !temp.contains(String.format("@%s", atUserName))) {
                        //去掉@用户
                        atUid = "";
                        atUserName = "";
                    }
                }

            }


            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        addGoodsLayout.setOnClickListener(this);
    }

    private void initVideoPlayer() {
        mPlayer = new TXLivePlayer(mContext);
        mPlayer.setConfig(new TXLivePlayConfig());
        mPlayer.setPlayerView(mTXCloudVideoView);
        mPlayer.enableHardwareDecode(false);
        mPlayer.setRenderRotation(TXLiveConstants.RENDER_ROTATION_PORTRAIT);
        mPlayer.setRenderMode(TXLiveConstants.RENDER_MODE_FULL_FILL_SCREEN);
        mPlayer.setPlayListener(this);
        int result = mPlayer.startPlay(mVideoPath, TXLivePlayer.PLAY_TYPE_LOCAL_VIDEO);
        if (result == 0) {
            mPlayStarted = true;
        }
    }

    private void initData() {
        mVideoPath = getIntent().getStringExtra(Constants.VIDEO_PATH);
        mSaveType = getIntent().getIntExtra(Constants.VIDEO_SAVE_TYPE, Constants.VIDEO_SAVE_SAVE_AND_PUB);
        mVideoTimeLong = getIntent().getLongExtra(Constants.VIDEO_TIME_LONG, 0);
        mMusicBean = getIntent().getParcelableExtra(Constants.VIDEO_MUSIC_BEAN);

        if (TextUtils.isEmpty(mVideoPath)) {
            ToastUtil.show("视频路径获取失败");
            finish();
        }

        AppConfig.getInstance().getConfig(new CommonCallback<ConfigBean>() {
            @Override
            public void callback(ConfigBean bean) {
                mConfigBean = bean;
            }
        });


    }

    private void initView() {
        setTitle(WordUtil.getString(R.string.video_pub));

        mLocation = findViewById(R.id.location);
        txAddGood = findViewById(R.id.txAddGood);

        mTXCloudVideoView = findViewById(R.id.video_view);

        mNum = findViewById(R.id.num);
        publicSwitch = findViewById(R.id.publicSwitch);
        addGoodsLayout = findViewById(R.id.add_goods_panel);
        znLayout = findViewById(R.id.znLayout);
        yxypLayout = findViewById(R.id.yxypLayout);
        znSwitch = findViewById(R.id.znSwitch);
        yxSwitch = findViewById(R.id.yxSwitch);


        if (AppConfig.getInstance().isCanPubYxYp()) {
            znLayout.setVisibility(View.GONE);
            yxypLayout.setVisibility(View.VISIBLE);
        } else {
            yxypLayout.setVisibility(View.GONE);
            znLayout.setVisibility(View.VISIBLE);
        }

        mInput = findViewById(R.id.input);

        atFriendDialog = DialogUtil.getAtFriendDialog(this, AppConfig.getInstance().getUid(), new OnDialogCallBackListener() {
            @Override
            public void onDialogViewClick(View view, Object object) {
                if (null != object) {
                    NetFriendBean bean = (NetFriendBean) object;
                    atUid = bean.getTouid();
                    atUserName = bean.getUsername();
                    String trim = mInput.getText().toString().trim();
                    trim = trim + atUserName;
                    mInput.setText(trim);
                    mInput.setSelection(trim.length());
                }
            }
        });

        addGoodsLayout.setVisibility(AppConfig.getInstance().canAddGood() ? View.VISIBLE : View.GONE);

    }

    private void initLocation() {
        mLocationListener = new TencentLocationListener() {
            @Override
            public void onLocationChanged(TencentLocation location, int code, String s) {
                if (code == TencentLocation.ERROR_OK) {
                    LogUtils.e(TAG, "获取定位成功------>" + location.toString());
                    lng = location.getLongitude();//经度
                    lat = location.getLatitude();//纬度
                    address = String.format("%s-%s-%s", location.getProvince(), location.getCity(), location.getDistrict());
                    mLocation.setText(address);
                    cityCode = location.getCityCode();
                }
            }

            @Override
            public void onStatusUpdate(String s, int i, String s1) {

            }
        };

        PermissionX.init(this)
                .permissions(Manifest.permission.ACCESS_FINE_LOCATION)
                .request((allGranted, grantedList, deniedList) -> {
                    if (allGranted) {
                        LocationUtil.getInstance().getSingleLocation(mLocationListener);
                    } else {
                        ToastUtil.show("定位权限未授予,无法定位当前位置");
                    }
                });
    }

    @Override
    public void onPlayEvent(int e, Bundle bundle) {
        switch (e) {
            case TXLiveConstants.PLAY_EVT_PLAY_END://播放结束
                onReplay();
                break;
            case TXLiveConstants.PLAY_EVT_CHANGE_RESOLUTION:
                onVideoSizeChanged(bundle.getInt("EVT_PARAM1", 0), bundle.getInt("EVT_PARAM2", 0));
                break;
        }
    }

    @Override
    public void onNetStatus(Bundle bundle) {

    }

    /**
     * 获取到视频宽高回调
     */
    public void onVideoSizeChanged(float videoWidth, float videoHeight) {
        if (mTXCloudVideoView != null && videoWidth > 0 && videoHeight > 0) {
            FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) mTXCloudVideoView.getLayoutParams();
            if (videoWidth / videoHeight > 0.5625f) {//横屏 9:16=0.5625
                params.height = (int) (mTXCloudVideoView.getWidth() / videoWidth * videoHeight);
                params.gravity = Gravity.CENTER;
                mTXCloudVideoView.requestLayout();
            }
        }
    }

    /**
     * 循环播放
     */
    private void onReplay() {
        if (mPlayStarted && mPlayer != null) {
            mPlayer.seek(0);
            mPlayer.resume();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        mPaused = true;
        if (mPlayStarted && mPlayer != null) {
            mPlayer.pause();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mPaused && mPlayStarted && mPlayer != null) {
            mPlayer.resume();
        }
        mPaused = false;
    }

    public void release() {
        HttpUtil.cancel(HttpConst.GET_CONFIG);
        HttpUtil.cancel(HttpConst.SAVE_UPLOAD_VIDEO_INFO);
        mPlayStarted = false;
        if (mPlayer != null) {
            mPlayer.stopPlay(false);
            mPlayer.setPlayListener(null);
        }
        if (mUploadStrategy != null) {
            mUploadStrategy.cancel();
        }
        mPlayer = null;
        mUploadStrategy = null;
    }

    @Override
    public void onBackPressed() {
        DialogUtil.showSimpleDialog(mContext, WordUtil.getString(R.string.video_give_up_pub), new DialogUtil.SimpleCallback() {
            @Override
            public void onConfirmClick(Dialog dialog, String content) {
                if (mSaveType == Constants.VIDEO_SAVE_PUB) {
                    if (!TextUtils.isEmpty(mVideoPath)) {
                        File file = new File(mVideoPath);
                        if (file.exists()) {
                            file.delete();
                        }
                    }
                }
                release();
                VideoPublishActivity.super.onBackPressed();
            }
        });
    }

    @Override
    protected void onDestroy() {
        release();
        super.onDestroy();
        L.e(TAG, "-------->onDestroy");
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_pub:
                publishVideo();
                break;
            case R.id.add_goods_panel:
                ShopWindowActivity.forward(this, ShopWindowAdapter.TYPE_VIDEO, ShopWindowActivity.INTENT_VIDEO);
                break;
        }
    }

    /**
     * 发布视频
     */
    private void publishVideo() {
        if (mConfigBean == null) {
            return;
        }
        if (mPlayStarted) {
            mPlayer.stopPlay(true);
        }

        String title = mInput.getText().toString().trim();

        mVideoTitle = title;

        if (TextUtils.isEmpty(mVideoPath)) {
            return;
        }


        mLoading = DialogUtil.loadingDialog(mContext, WordUtil.getString(R.string.video_pub_ing), false);
        mLoading.show();
        Bitmap bitmap = null;
        //生成视频封面图
        MediaMetadataRetriever mmr = null;
        try {
            mmr = new MediaMetadataRetriever();
            mmr.setDataSource(mVideoPath);
            bitmap = mmr.getFrameAtTime(0, MediaMetadataRetriever.OPTION_CLOSEST);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (mmr != null) {
                mmr.release();
            }
        }
        if (bitmap == null) {
            if (mLoading != null) {
                mLoading.dismiss();
            }
            ToastUtil.show(R.string.video_cover_img_failed);
            return;
        }
        String coverImagePath = mVideoPath.replace(".mp4", ".jpg");
        File imageFile = new File(coverImagePath);
        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(imageFile);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);
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
        bitmap.recycle();
        if (imageFile == null) {
            if (mLoading != null) {
                mLoading.dismiss();
            }
            ToastUtil.show(R.string.video_cover_img_failed);
            return;
        }

        if (mConfigBean.getVideoCloudType() == 1) {
            mUploadStrategy = new VideoUploadQnImpl(mConfigBean);
        } else {
            mUploadStrategy = new VideoUploadTxImpl(mConfigBean);
        }

        mUploadStrategy.upload(new VideoUploadBean(new File(mVideoPath), imageFile), new VideoUploadCallback() {
            @Override
            public void onSuccess(VideoUploadBean bean) {
                if (mSaveType == Constants.VIDEO_SAVE_PUB) {
                    bean.deleteFile();
                }
                saveUploadVideoInfo(bean);
            }

            @Override
            public void onFailure() {
                ToastUtil.show(R.string.video_pub_failed);
                if (mLoading != null) {
                    mLoading.dismiss();
                }
            }
        });
    }

    /**
     * 把视频上传后的信息保存在服务器
     */
    private void saveUploadVideoInfo(VideoUploadBean bean) {


        int musicId = 0;
        if (mMusicBean != null) {
            musicId = mMusicBean.getId();
        }

        String goodId = "0";

        if (goodItem != null) {
            goodId = goodItem.getId();
        }

        HttpUtil.saveUploadVideoInfo(mVideoTitle, bean.getResultImageUrl(), bean.getResultVideoUrl(), musicId, mVideoTimeLong,
                lat, lng, cityCode, atUid, goodId, publicSwitch.isChecked(), znSwitch.isChecked(), yxSwitch.isChecked(), "",
                new HttpCallback() {
                    @Override
                    public void onSuccess(int code, String msg, String[] info) {
                        if (code == 0 && info.length > 0) {
                            if (mConfigBean != null && mConfigBean.getVideoAuditSwitch() == 1) {
                                ToastUtil.show(R.string.video_pub_success_2);
                            } else {
                                ToastUtil.show(R.string.video_pub_success);
                            }

                            finish();
                        } else {
                            ToastUtil.show(msg);
                        }
                    }

                    @Override
                    public void onFinish() {
                        if (mLoading != null) {
                            mLoading.dismiss();
                        }
                    }
                });
    }

    /**
     * 软键盘隐藏
     */
    private void hideSoftInput() {
        InputMethodManager imm = (InputMethodManager) mContext.getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm != null) {
            imm.hideSoftInputFromWindow(mInput.getWindowToken(), 0);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == ShopWindowActivity.INTENT_VIDEO && data != null) {
            goodItem = data.getParcelableExtra("goods");
            if (null != goodItem) {
                txAddGood.setText(goodItem.getTitle());
            }
        }
    }
}
