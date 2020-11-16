package com.wwsl.video.custom;

import android.content.Context;
import android.graphics.Bitmap;
import androidx.annotation.AttrRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.wwsl.video.R;

import java.util.List;

/**
 * Created by vinsonswang on 2017/11/6.
 */

public class VideoProgressView extends FrameLayout {

    private Context mContext;
    private View mRootView;
    private RecyclerView mRecyclerView;
    private ThumbnailAdapter mThumbnailAdapter;

    public VideoProgressView(@NonNull Context context) {
        this(context, null);
    }

    public VideoProgressView(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public VideoProgressView(@NonNull Context context, @Nullable AttributeSet attrs, @AttrRes int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mContext = context;
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        mRootView = LayoutInflater.from(mContext).inflate(R.layout.layout_video_progress, this, false);
        mRecyclerView = (RecyclerView) mRootView.findViewById(R.id.rv_video_thumbnail);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(mContext, LinearLayoutManager.HORIZONTAL, false));
        mThumbnailAdapter = new ThumbnailAdapter(mContext);
        mRecyclerView.setAdapter(mThumbnailAdapter);
        addView(mRootView);
    }

    public void addBitmapList(List<Bitmap> list){
        if(mThumbnailAdapter!=null){
            mThumbnailAdapter.addBitmapList(list);
        }
    }

    public void reverse() {
        if (mThumbnailAdapter != null) {
            mThumbnailAdapter.reverse();
        }
    }



    public RecyclerView getRecyclerView() {
        return mRecyclerView;
    }

    public int getThumbnailCount() {
        if (mThumbnailAdapter != null) {
            return mThumbnailAdapter.getItemCount() - 2;
        }
        return 0;
    }


    public ViewGroup getParentView() {
        return (ViewGroup) mRootView;
    }

}
