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

import com.wwsl.mdsj.AppConfig;
import com.wwsl.mdsj.Constants;
import com.wwsl.mdsj.R;
import com.wwsl.mdsj.bean.LevelBean;
import com.wwsl.mdsj.bean.UserBean;
import com.wwsl.mdsj.glide.ImgLoader;
import com.wwsl.mdsj.interfaces.OnItemClickListener;
import com.wwsl.mdsj.utils.IconUtil;

import java.util.List;

public class LiveGapListAdapter extends RecyclerView.Adapter<LiveGapListAdapter.Vh> {

    private List<UserBean> mList;
    private LayoutInflater mInflater;
    private View.OnClickListener mOnClickListener;
    private OnItemClickListener<UserBean> mOnItemClickListener;

    public LiveGapListAdapter(Context context, List<UserBean> list) {
        mInflater = LayoutInflater.from(context);
        mList = list;
        mOnClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Object tag = v.getTag();
                if (tag != null) {
                    int position = (int) tag;
                    if (mOnItemClickListener != null) {
                        mOnItemClickListener.onItemClick(mList.get(position), position);
                    }
                }
            }
        };
    }

    public void setOnItemClickListener(OnItemClickListener<UserBean> onItemClickListener) {
        mOnItemClickListener = onItemClickListener;
    }

    public void setList(List<UserBean> list) {
        mList = list;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public LiveGapListAdapter.Vh onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new LiveGapListAdapter.Vh(mInflater.inflate(R.layout.item_live_admin_list, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull LiveGapListAdapter.Vh holder, int position) {

    }

    @Override
    public void onBindViewHolder(@NonNull LiveGapListAdapter.Vh vh, int position, @NonNull List<Object> payloads) {
        Object payload = payloads.size() > 0 ? payloads.get(0) : null;
        vh.setData(mList.get(position), position, payload);
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    class Vh extends RecyclerView.ViewHolder {
        ImageView mLevelAnchor;
        TextView tvSign;
        ImageView mAvatar;
        TextView mName;
        ImageView mSex;
        ImageView mLevel;
        View mBtnDel;

        public Vh(View itemView) {
            super(itemView);
            mLevelAnchor = itemView.findViewById(R.id.level_anchor);
            tvSign = itemView.findViewById(R.id.tvSign);
            mAvatar = (ImageView) itemView.findViewById(R.id.avatar);
            mName = (TextView) itemView.findViewById(R.id.name);
            mSex = (ImageView) itemView.findViewById(R.id.sex);
            mLevel = (ImageView) itemView.findViewById(R.id.level);
            mBtnDel = itemView.findViewById(R.id.btn_delete);
            itemView.setOnClickListener(mOnClickListener);
        }

        void setData(UserBean bean, int position, Object payload) {
            itemView.setTag(position);
            mBtnDel.setVisibility(View.GONE);
            if (payload == null) {
                ImgLoader.displayAvatar(bean.getAvatar(), mAvatar);
                mName.setText(bean.getUsername());
                tvSign.setText(bean.getSignature());
                mSex.setImageResource(IconUtil.getSexIcon(bean.getSex()));
                LevelBean levelBean = AppConfig.getInstance().getLevel(bean.getLevel());
                if (levelBean != null) {
                    ImgLoader.display(levelBean.getThumb(), mLevel);
                }
                LevelBean anchorLevelBean = AppConfig.getInstance().getAnchorLevel(bean.getLevelAnchor());
                if (anchorLevelBean != null) {
                    ImgLoader.display(anchorLevelBean.getThumb(), mLevelAnchor);
                }
            }
        }
    }

    public void removeItem(String uid) {
        if (TextUtils.isEmpty(uid)) {
            return;
        }
        int position = -1;
        for (int i = 0, size = mList.size(); i < size; i++) {
            if (uid.equals(mList.get(i).getId())) {
                position = i;
                break;
            }
        }
        if (position >= 0) {
            mList.remove(position);
            notifyItemRemoved(position);
            notifyItemRangeChanged(position, mList.size(), Constants.PAYLOAD);
        }
    }

    public void clear() {
        mList.clear();
        notifyDataSetChanged();
    }

    public void release() {
        if (mList != null) {
            mList.clear();
        }
        mOnClickListener = null;
        mOnItemClickListener = null;
    }
}
