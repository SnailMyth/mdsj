package com.wwsl.mdsj.adapter;

import android.content.Context;
import android.net.Uri;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.luck.picture.lib.config.PictureMimeType;
import com.luck.picture.lib.entity.LocalMedia;
import com.luck.picture.lib.listener.OnItemClickListener;
import com.luck.picture.lib.tools.DateUtils;
import com.wwsl.mdsj.R;
import com.wwsl.mdsj.views.dialog.OnItemLongClickListener;

import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import timber.log.Timber;


/**
 * @author :
 * @date : 2020/6/9 15:21
 * @description : GridImageAdapter
 */
public class GridImageAdapter extends
        RecyclerView.Adapter<GridImageAdapter.ViewHolder> {
    public static final String TAG = "PictureSelector";
    public static final int TYPE_CAMERA = 1;
    public static final int TYPE_PICTURE = 2;
    private LayoutInflater mInflater;
    private List<LocalMedia> list = new ArrayList<>();
    private int selectMax = 9;

    private boolean editMode = true;

    public void setEditMode(boolean editMode) {
        this.editMode = editMode;
    }

    /**
     * 点击添加图片跳转
     */
    private onAddPicClickListener mOnAddPicClickListener;

    public interface onAddPicClickListener {
        void onAddPicClick();
    }

    /**
     * 删除
     */
    public void delete(int position) {
        try {

            if (position != RecyclerView.NO_POSITION && list.size() > position) {
                list.remove(position);
                notifyItemRemoved(position);
                notifyItemRangeChanged(position, list.size());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public GridImageAdapter(Context context, onAddPicClickListener mOnAddPicClickListener) {
        this.mInflater = LayoutInflater.from(context);
        this.mOnAddPicClickListener = mOnAddPicClickListener;
    }

    public void setSelectMax(int selectMax) {
        this.selectMax = selectMax;
    }

    public void setList(List<LocalMedia> list) {
        this.list = list;
    }

    public List<LocalMedia> getData() {
        return list == null ? new ArrayList<>() : list;
    }

    public void remove(int position) {
        if (list != null && position < list.size()) {
            list.remove(position);
        }
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        ImageView mImg;
        ImageView mIvDel;
        TextView tvDuration;

        public ViewHolder(View view) {
            super(view);
            mImg = view.findViewById(R.id.fiv);
            mIvDel = view.findViewById(R.id.iv_del);
            tvDuration = view.findViewById(R.id.tv_duration);
        }

        void showDelete(boolean show) {
            mIvDel.setVisibility(show ? View.VISIBLE : View.GONE);
        }

    }

    @Override
    public int getItemCount() {
        if (list.size() < selectMax) {
            return list.size() + 1;
        } else {
            return list.size();
        }
    }

    @Override
    public int getItemViewType(int position) {
        if (isShowAddItem(position)) {
            return TYPE_CAMERA;
        } else {
            return TYPE_PICTURE;
        }
    }

    /**
     * 创建ViewHolder
     */
    @NotNull
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View view = mInflater.inflate(R.layout.gv_filter_image,
                viewGroup, false);
        return new ViewHolder(view);
    }

    private boolean isShowAddItem(int position) {
        return position == list.size();
    }

    /**
     * 设置值
     */
    @Override
    public void onBindViewHolder(final ViewHolder viewHolder, final int position) {
        //少于8张，显示继续添加的图标
        if (getItemViewType(position) == TYPE_CAMERA) {
            viewHolder.mImg.setVisibility(editMode ? View.VISIBLE : View.GONE);
            viewHolder.mImg.setImageResource(R.mipmap.icon_publish_pic_add);
            viewHolder.mImg.setOnClickListener(v -> mOnAddPicClickListener.onAddPicClick());
            viewHolder.mIvDel.setVisibility(View.INVISIBLE);
        } else {
            viewHolder.mIvDel.setVisibility(editMode ? View.VISIBLE : View.INVISIBLE);
            viewHolder.mIvDel.setOnClickListener(view -> {
                int index = viewHolder.getAdapterPosition();
                // 这里有时会返回-1造成数据下标越界,具体可参考getAdapterPosition()源码，
                // 通过源码分析应该是bindViewHolder()暂未绘制完成导致，知道原因的也可联系我~感谢
                if (index != RecyclerView.NO_POSITION && list.size() > index) {
                    list.remove(index);
                    notifyItemRemoved(index);
                    notifyItemRangeChanged(index, list.size());
                }
            });
            LocalMedia media = list.get(position);
            if (media == null
                    || TextUtils.isEmpty(media.getPath())) {
                return;
            }
            int chooseModel = media.getChooseModel();
            String path;
            if (media.isCut() && !media.isCompressed()) {
                // 裁剪过
                path = media.getCutPath();
            } else if (media.isCompressed() || (media.isCut() && media.isCompressed())) {
                // 压缩过,或者裁剪同时压缩过,以最终压缩过图片为准
                path = media.getCompressPath();
            } else {
                // 原图
                path = media.getPath();
            }

            Timber.i("原图地址::%s", media.getPath());

            if (media.isCut()) {
                Timber.i("裁剪地址::%s", media.getCutPath());
            }
            if (media.isCompressed()) {
                Timber.i("压缩地址::%s", media.getCompressPath());
                Timber.i("压缩后文件大小::" + new File(media.getCompressPath()).length() / 1024 + "k");
            }
            if (!TextUtils.isEmpty(media.getAndroidQToPath())) {
                Timber.i("Android Q特有地址::%s", media.getAndroidQToPath());
            }
            if (media.isOriginal()) {
                Timber.i("是否开启原图功能::%s", true);
                Timber.i("开启原图功能后地址::%s", media.getOriginalPath());
            }

            long duration = media.getDuration();
            viewHolder.tvDuration.setVisibility(PictureMimeType.isHasVideo(media.getMimeType())
                    ? View.VISIBLE : View.GONE);
            if (chooseModel == PictureMimeType.ofAudio()) {
                viewHolder.tvDuration.setVisibility(View.VISIBLE);
                viewHolder.tvDuration.setCompoundDrawablesRelativeWithIntrinsicBounds
                        (R.drawable.picture_icon_audio, 0, 0, 0);

            } else {
                viewHolder.tvDuration.setCompoundDrawablesRelativeWithIntrinsicBounds
                        (R.drawable.picture_icon_video, 0, 0, 0);
            }
            viewHolder.tvDuration.setText(DateUtils.formatDurationTime(duration));
            if (chooseModel == PictureMimeType.ofAudio()) {
                viewHolder.mImg.setImageResource(R.drawable.picture_audio_placeholder);
            } else {
                Glide.with(viewHolder.itemView.getContext())
                        .load(PictureMimeType.isContent(path) && !media.isCut() && !media.isCompressed() ? Uri.parse(path)
                                : path)
                        .centerCrop()
                        .placeholder(R.color.app_color_f6)
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .into(viewHolder.mImg);
            }
            //itemView 的点击事件
            if (mItemClickListener != null) {
                viewHolder.itemView.setOnClickListener(v -> {
                    int adapterPosition = viewHolder.getAdapterPosition();
                    mItemClickListener.onItemClick(v, adapterPosition);
                });
            }

            if (mItemLongClickListener != null) {
                viewHolder.itemView.setOnLongClickListener(v -> {
                    int adapterPosition = viewHolder.getAdapterPosition();
                    mItemLongClickListener.onItemLongClick(viewHolder, adapterPosition, v);
                    return true;
                });
            }
        }
    }


    private OnItemClickListener mItemClickListener;

    public void setOnItemClickListener(OnItemClickListener l) {
        this.mItemClickListener = l;
    }

    private OnItemLongClickListener mItemLongClickListener;

    public void setItemLongClickListener(OnItemLongClickListener l) {
        this.mItemLongClickListener = l;
    }
}
