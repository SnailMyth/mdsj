package com.wwsl.mdsj.adapter;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.wwsl.mdsj.R;
import com.wwsl.mdsj.bean.PictureChooseBean;
import com.wwsl.mdsj.glide.ImgLoader;
import com.wwsl.mdsj.interfaces.OnPictureChooseItemClickListener;

import java.util.List;

public class PictureChooseAdapter extends RecyclerView.Adapter<PictureChooseAdapter.Vh> {
    private List<PictureChooseBean> mList;
    private LayoutInflater mInflater;
    private View.OnClickListener mOnClickListener;
    private OnPictureChooseItemClickListener<PictureChooseBean> mOnItemClickListener;
    private boolean showSelect = true;

    public void setShowSelect(boolean showSelect) {
        this.showSelect = showSelect;
    }

    public PictureChooseAdapter(Context context, List<PictureChooseBean> list) {
        mList = list;
        mInflater = LayoutInflater.from(context);
        mOnClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (v.getId()) {
                    case R.id.tvSelect: {
                        Object tag = v.getTag();
                        if (tag != null && mOnItemClickListener != null) {
                            int position = (int) v.getTag();
                            PictureChooseBean bean = mList.get(position);
                            if (showSelect) {
                                mOnItemClickListener.onItemSelect(bean, position);
                            } else {
                                if (!TextUtils.isEmpty(bean.getPath())) {
                                    mOnItemClickListener.onItemDelete(bean, position);
                                }
                            }
                        }
                    }
                    break;
                    default: {
                        Object tag = v.getTag();
                        if (tag != null && mOnItemClickListener != null) {
                            int position = (int) v.getTag();
                            mOnItemClickListener.onItemClick(mList.get(position), position);
                        }
                    }
                    break;
                }
            }
        };
    }

    public void setOnItemClickListener(OnPictureChooseItemClickListener<PictureChooseBean> listener) {
        mOnItemClickListener = listener;
    }

    @NonNull
    @Override
    public Vh onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new Vh(mInflater.inflate(R.layout.item_picture_choose_local, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull Vh vh, int position) {
        vh.setData(mList.get(position), position);
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    class Vh extends RecyclerView.ViewHolder {
        ImageView mCover;
        TextView tvNum;
        ImageView ivNone;
        TextView tvBg;
        TextView tvSelect;
        ImageView ivDelete;

        public Vh(View itemView) {
            super(itemView);
            mCover = itemView.findViewById(R.id.cover);
            tvNum = itemView.findViewById(R.id.tvNum);
            ivNone = itemView.findViewById(R.id.ivNone);
            tvBg = itemView.findViewById(R.id.tvBg);
            tvSelect = itemView.findViewById(R.id.tvSelect);
            ivDelete = itemView.findViewById(R.id.ivDelete);
            tvSelect.setOnClickListener(mOnClickListener);
            itemView.setOnClickListener(mOnClickListener);
        }

        void setData(PictureChooseBean bean, int position) {
            tvSelect.setTag(position);
            itemView.setTag(position);
            if (!TextUtils.isEmpty(bean.getPath())) {
                tvBg.setVisibility(View.GONE);
                mCover.setScaleType(ImageView.ScaleType.CENTER_CROP);
                ImgLoader.displayVideoThumb(bean.getPath(), mCover);
            } else {
                tvBg.setVisibility(View.VISIBLE);
                mCover.setScaleType(ImageView.ScaleType.CENTER);
                mCover.setImageResource(R.mipmap.icon_publish_pic_add);
            }
            if (showSelect) {
                if (bean.getNum() > 0) {
                    tvNum.setVisibility(View.VISIBLE);
                    tvNum.setText(bean.getNum() + "");
                    ivNone.setVisibility(View.GONE);
                } else {
                    tvNum.setVisibility(View.GONE);
                    ivNone.setVisibility(View.VISIBLE);
                }
                ivDelete.setVisibility(View.GONE);
            } else {
                tvNum.setVisibility(View.GONE);
                ivNone.setVisibility(View.GONE);
                if (!TextUtils.isEmpty(bean.getPath())) {
                    ivDelete.setVisibility(View.VISIBLE);
                } else {
                    ivDelete.setVisibility(View.GONE);
                }
            }
        }
    }
}
