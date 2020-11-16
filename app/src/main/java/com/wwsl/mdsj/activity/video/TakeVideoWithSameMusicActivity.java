package com.wwsl.mdsj.activity.video;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.alibaba.fastjson.JSON;
import com.shehuan.niv.NiceImageView;
import com.wwsl.mdsj.AppConfig;
import com.wwsl.mdsj.R;
import com.wwsl.mdsj.adapter.GridVideoAdapter;
import com.wwsl.mdsj.base.BaseActivity;
import com.wwsl.mdsj.bean.VideoBean;
import com.wwsl.mdsj.bean.net.VideoMusicBean;
import com.wwsl.mdsj.glide.ImgLoader;
import com.wwsl.mdsj.http.HttpCallback;
import com.wwsl.mdsj.http.HttpConst;
import com.wwsl.mdsj.http.HttpUtil;
import com.wwsl.mdsj.utils.DownloadUtil;
import com.wwsl.mdsj.utils.ToastUtil;
import com.wwsl.mdsj.utils.VideoStorage;
import com.wwsl.mdsj.utils.cache.PreloadManager;
import com.yanzhenjie.recyclerview.SwipeRecyclerView;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class TakeVideoWithSameMusicActivity extends BaseActivity implements SwipeRecyclerView.LoadMoreListener {

    private NiceImageView ivCover;
    private TextView tvTitle;
    private TextView btnGoTake;
    private TextView tvNum;
    private SwipeRecyclerView recycler;
    private GridVideoAdapter adapter;
    private List<VideoBean> data;
    private VideoMusicBean musicBean;
    private int mPage = 1;
    private int videoIndex = HttpConst.VIDEO_TYPE_SAME_MUSIC << 10;
    private int videoType = HttpConst.VIDEO_TYPE_SAME_MUSIC;

    @Override
    protected int setLayoutId() {
        return R.layout.activity_take_video_with_same_music;
    }

    @Override
    protected void init() {
        musicBean = getIntent().getParcelableExtra("music");
        initView();
        recycler.setLayoutManager(new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL));
        data = new ArrayList<>();
        adapter = new GridVideoAdapter(new ArrayList<>());
        adapter.setEmptyView(LayoutInflater.from(this).inflate(R.layout.view_no_data_video, null, false));
        adapter.setOnItemClickListener((adapter, view, position) -> {
            VideoPlayListActivity.forward(this, AppConfig.getInstance().getUid(), HttpConst.USER_VIDEO_TYPE_SAME_MUSIC, position);
        });
        recycler.setAdapter(adapter);
        recycler.setLoadMoreListener(this);
        recycler.useDefaultLoadMore();


    }

    public void backClick(View view) {
        finish();
    }

    private void initView() {
        ivCover = findViewById(R.id.ivCover);
        tvNum = findViewById(R.id.tvNum);
        btnGoTake = findViewById(R.id.btnGoTake);
        tvTitle = findViewById(R.id.tvTitle);
        recycler = findViewById(R.id.recycler);

        ImgLoader.display(musicBean.getImgUrl(), ivCover);
        tvTitle.setText(musicBean.getMusicName());
        tvNum.setText(String.format("%s人使用", musicBean.getUseNum()));
        btnGoTake.setOnClickListener(v -> {
            DownloadUtil downloadUtil = new DownloadUtil();
            showLoadCancelable(false, "下载音乐到本地...");
            downloadUtil.download(musicBean.getMusicName(), AppConfig.MUSIC_PATH, musicBean.getMusicName(), musicBean.getFileUrl(), new DownloadUtil.Callback() {
                @Override
                public void onSuccess(File file) {
                    dismissLoad();
                    String musicPath = file.getAbsolutePath();
                    musicBean.setLocalPath(musicPath);
                    VideoRecordActivity.forward(TakeVideoWithSameMusicActivity.this, musicBean);
                }

                @Override
                public void onProgress(int progress) {
                }

                @Override
                public void onError(Throwable e) {
                    dismissLoad();
                }
            });
        });
    }

    @Override
    public void onLoadMore() {
        mPage++;
        HttpUtil.getVideoList(musicBean.getId(), new HttpCallback() {
            @Override
            public void onSuccess(int code, String msg, String[] info) {
                if (code == 0 && info != null && info.length > 0) {
                    List<VideoBean> videoBeans = JSON.parseArray(Arrays.toString(info), VideoBean.class);
                    for (int i = 0; i < videoBeans.size(); i++) {
                        PreloadManager.getInstance(TakeVideoWithSameMusicActivity.this).addPreloadTask(videoBeans.get(i).getVideoUrl(), videoIndex + i);
                    }
                    adapter.addData(videoBeans);
                    if (videoBeans.size() < HttpConst.ITEM_COUNT) {
                        recycler.loadMoreFinish(videoBeans.size() == 0, false);
                        mPage--;
                    } else {
                        recycler.loadMoreFinish(false, true);
                    }

                    VideoStorage.getInstance().putVideoList(AppConfig.getInstance().getUid(), HttpConst.USER_VIDEO_TYPE_SAME_MUSIC, videoBeans);

                } else {
                    ToastUtil.show(msg);
                }
            }

            @Override
            public void onError() {
                mPage--;
                recycler.loadMoreFinish(data.size() == 0, true);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (data.size() == 0) {
            loadData();
        }
    }

    private void loadData() {
        mPage = 1;
        HttpUtil.getVideoList(musicBean.getId(), new HttpCallback() {
            @Override
            public void onSuccess(int code, String msg, String[] info) {
                if (code == 0 && info != null && info.length > 0) {
                    List<VideoBean> videoBeans = JSON.parseArray(Arrays.toString(info), VideoBean.class);

                    for (int i = 0; i < videoBeans.size(); i++) {
                        PreloadManager.getInstance(TakeVideoWithSameMusicActivity.this).addPreloadTask(videoBeans.get(i).getVideoUrl(), videoIndex + i);
                    }
                    data.clear();
                    data.addAll(videoBeans);
                    adapter.setNewInstance(data);
                    if (videoBeans.size() < HttpConst.ITEM_COUNT) {
                        recycler.loadMoreFinish(videoBeans.size() == 0, false);
                    } else {
                        recycler.loadMoreFinish(false, true);
                    }
                    VideoStorage.getInstance().putVideoList(AppConfig.getInstance().getUid(), HttpConst.USER_VIDEO_TYPE_SAME_MUSIC, videoBeans);
                } else {
                    data.clear();
                    adapter.setNewInstance(new ArrayList<>());
                    ToastUtil.show(msg);
                }


            }
        });
    }

    public static void forward(Context context, VideoMusicBean musicBean) {
        Intent intent = new Intent(context, TakeVideoWithSameMusicActivity.class);
        intent.putExtra("music", musicBean);
        context.startActivity(intent);
    }
}
